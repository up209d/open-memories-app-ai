package com.sony.mexi.orb.server;

import com.sony.mexi.orb.servlet.serviceguide.OrbServiceGuideServlet;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;

/* loaded from: classes.dex */
public class OrbServer extends Thread implements SocketActionListener {
    private boolean acceptable;
    private int backlog;
    private ExecutorService pool;
    private int port;
    private HttpService service;
    private ServerSocket socket;
    private Map<String, Socket> socketMap;

    /* loaded from: classes.dex */
    public class OrbRequestHandler implements HttpRequestHandler {
        private HttpServlet servlet;

        public OrbRequestHandler(HttpServlet servlet) {
            this.servlet = servlet;
        }

        @Override // org.apache.http.protocol.HttpRequestHandler
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            try {
                OrbHttpServerConnection connection = (OrbHttpServerConnection) context.getAttribute("connection");
                OrbRequest req = new OrbRequest(request);
                OrbResponse res = new OrbResponse(response, connection);
                this.servlet.service((ServletRequest) req, (ServletResponse) res);
                if (!res.isCommitted()) {
                    res.commit();
                }
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(Map<String, HttpServlet> servlets, int port, ExecutorService pool, boolean autoServiceGuide) {
        this.port = port;
        this.acceptable = false;
        this.pool = pool;
        if (autoServiceGuide) {
            OrbServiceGuideServlet guideServlet = new OrbServiceGuideServlet(servlets);
            servlets.put(guideServlet.name(), guideServlet);
        }
        HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
        if (servlets != null) {
            for (String pattern : servlets.keySet()) {
                HttpServlet servlet = servlets.get(pattern);
                try {
                    servlet.init();
                    registry.register(pattern, new OrbRequestHandler(servlet));
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.service = new HttpService(new BasicHttpProcessor(), new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        this.service.setHandlerResolver(registry);
        this.service.setParams(new BasicHttpParams());
    }

    public OrbServer(Map<String, HttpServlet> servlets, int port, ExecutorService pool) {
        this.backlog = 50;
        this.socketMap = new HashMap();
        init(servlets, port, pool, false);
    }

    public OrbServer(Map<String, HttpServlet> servlets, int port, ExecutorService pool, int backlog, boolean autoServiceGuide) {
        this.backlog = 50;
        this.socketMap = new HashMap();
        init(servlets, port, pool, autoServiceGuide);
        if (backlog >= 0) {
            this.backlog = backlog;
        }
    }

    /* loaded from: classes.dex */
    class OrbConnection implements Runnable {
        private SocketActionListener listener;
        private final Socket socket;

        OrbConnection(Socket socket, SocketActionListener listener) {
            this.socket = socket;
            this.listener = listener;
        }

        @Override // java.lang.Runnable
        public void run() {
            OrbHttpServerConnection connection = new OrbHttpServerConnection();
            HttpContext context = new BasicHttpContext();
            try {
                connection.bind(this.socket, OrbServer.this.service.getParams());
                context.setAttribute("connection", connection);
                OrbServer.this.service.handleRequest(connection, context);
                connection.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            } catch (OutOfMemoryError e3) {
                e3.printStackTrace();
            } catch (HttpException e4) {
                e4.printStackTrace();
            }
            try {
                int remotePort = this.socket.getPort();
                SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
                this.socket.close();
                this.listener.onClose(remoteAddress, remotePort);
            } catch (IOException e5) {
                e5.printStackTrace();
            }
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        super.run();
        while (this.acceptable) {
            try {
                Socket sock = this.socket.accept();
                if (registerSocket(sock)) {
                    this.pool.execute(new OrbConnection(sock, this));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.socket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private synchronized boolean registerSocket(Socket sock) {
        boolean z;
        if (!this.acceptable) {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            z = false;
        } else {
            String mapKey = createMapKey(sock.getRemoteSocketAddress(), sock.getPort());
            if (this.socketMap.containsKey(mapKey)) {
                try {
                    try {
                        Socket remainingSocket = this.socketMap.get(mapKey);
                        if (!remainingSocket.isClosed()) {
                            remainingSocket.close();
                        }
                    } finally {
                        this.socketMap.remove(mapKey);
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                    this.socketMap.remove(mapKey);
                }
            }
            this.socketMap.put(mapKey, sock);
            z = true;
        }
        return z;
    }

    @Override // java.lang.Thread
    public synchronized void start() {
        this.acceptable = true;
        try {
            this.socket = new ServerSocket(this.port, this.backlog);
            this.socket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.start();
    }

    public synchronized void finish() {
        this.acceptable = false;
    }

    public synchronized void shutdown() {
        this.acceptable = false;
        for (Socket sock : this.socketMap.values()) {
            try {
                if (!sock.isClosed()) {
                    sock.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.socketMap.clear();
        try {
            this.socket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.sony.mexi.orb.server.SocketActionListener
    public synchronized void onClose(SocketAddress remoteAddress, int remotePort) {
        this.socketMap.remove(createMapKey(remoteAddress, this.port));
    }

    public String createMapKey(SocketAddress remoteAddress, int remotePort) {
        return remoteAddress + ":" + this.port;
    }
}
