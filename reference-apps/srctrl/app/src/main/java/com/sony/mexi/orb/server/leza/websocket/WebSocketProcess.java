package com.sony.mexi.orb.server.leza.websocket;

import com.sony.mexi.orb.server.OrbServiceGroup;
import com.sony.mexi.orb.server.OrbTransport;
import com.sony.mexi.orb.server.OriginalRemoteAddressHandler;
import com.sony.mexi.orb.server.PathValidator;
import com.sony.mexi.orb.server.RequestHeaders;
import com.sony.mexi.orb.server.SupportedServiceGpHolder;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.server.leza.ServerProcess;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandler;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandlerGroup;
import com.sony.mexi.orb.server.websocket.WebSocketUpgradeRequestFirewall;
import com.sony.mexi.server.jni.HttpServerJni;
import com.sony.mexi.server.jni.WebSocketConnectionJni;
import com.sony.mexi.server.jni.WebSocketJni;
import java.net.URISyntaxException;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/* loaded from: classes.dex */
public class WebSocketProcess implements ServerProcess {
    private static final String ACCEPT_WEBSOCKET_PROTOCOL = "v10.webapi.scalar.sony.com";
    private static final String KEY_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    private static final String PROTOCOL = "websocket:jsonizer";
    private static final String TYPE = "websocket";
    private final ExecutorService mExecutorService;
    private final OrbWebSocketHandlerGroup mHandlerGroup;
    private int mMaxBodySize;
    private final OriginalRemoteAddressHandler mRemoteAddressHandler;
    private final WebSocketUpgradeRequestFirewall mUpgradeFirewall;

    /* loaded from: classes.dex */
    private static class OrbWebSocketMessageStringHandler implements Runnable {
        private final OrbWebSocketHandler mHandler;
        private final String mMessage;
        private final WebSocketConnection mWs;

        public OrbWebSocketMessageStringHandler(OrbWebSocketHandler handler, WebSocketConnection ws, String message) {
            this.mHandler = handler;
            this.mWs = ws;
            this.mMessage = message;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHandler.onMessage(this.mWs, this.mMessage);
        }
    }

    /* loaded from: classes.dex */
    private static class OrbWebSocketMessageBytesHandler implements Runnable {
        private final byte[] mBuf;
        private final OrbWebSocketHandler mHandler;
        private final WebSocketConnection mWs;

        public OrbWebSocketMessageBytesHandler(OrbWebSocketHandler handler, WebSocketConnection ws, byte[] buf) {
            this.mHandler = handler;
            this.mWs = ws;
            this.mBuf = buf;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHandler.onMessage(this.mWs, this.mBuf);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpgradeHeaders implements RequestHeaders {
        private final WebSocketConnection mConnection;

        UpgradeHeaders(WebSocketConnection connection) {
            this.mConnection = connection;
        }

        @Override // com.sony.mexi.orb.server.RequestHeaders
        public String getHeader(String key) {
            return this.mConnection.getRequestHeader(key);
        }
    }

    public WebSocketProcess(OrbWebSocketHandlerGroup serviceGroup, ExecutorService executorService, OriginalRemoteAddressHandler remoteAddressHandler, WebSocketUpgradeRequestFirewall upgradeFirewall) {
        this.mHandlerGroup = serviceGroup;
        this.mExecutorService = executorService;
        this.mRemoteAddressHandler = remoteAddressHandler;
        this.mUpgradeFirewall = upgradeFirewall;
    }

    @Override // com.sony.mexi.orb.server.leza.ServerProcess
    public void init(HttpServerJni httpServerJni, int maxBodySize) {
        WebSocketJni.listen(httpServerJni, new WebSocketJni.OnUpgradeRequestListener() { // from class: com.sony.mexi.orb.server.leza.websocket.WebSocketProcess.1
            @Override // com.sony.mexi.server.jni.WebSocketJni.OnUpgradeRequestListener
            public void onUpgradeRequest(WebSocketConnectionJni websocketConnection) {
                WebSocketProcess.this.handleUpgradeRequest(websocketConnection);
            }
        });
        this.mMaxBodySize = maxBodySize;
        this.mHandlerGroup.init();
        this.mHandlerGroup.initServiceInformation(protocol());
        SupportedServiceGpHolder.getInstance().addServiceGp(this.mHandlerGroup);
    }

    @Override // com.sony.mexi.orb.server.leza.ServerProcess
    public void shutdown() {
        SupportedServiceGpHolder.getInstance().removeServiceGp(this.mHandlerGroup);
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public OrbTransport getTransport() {
        return OrbTransport.WEBSOCKET;
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public String protocol() {
        return PROTOCOL;
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public OrbServiceGroup getServiceGroup() {
        return this.mHandlerGroup;
    }

    private static boolean hasWebsocketProtocol(String value) {
        if (value == null) {
            return false;
        }
        String[] subProtocols = value.split(",");
        for (String subProtocol : subProtocols) {
            if (ACCEPT_WEBSOCKET_PROTOCOL.equals(subProtocol.trim())) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpgradeRequest(WebSocketConnectionJni webSocketConnection) {
        final OrbWebSocketHandler handler = lookUpHandler(webSocketConnection.url());
        if (handler == null) {
            webSocketConnection.failUpgradeRequest(StatusCode.NOT_FOUND.toCode(), StatusCode.NOT_FOUND.toReasonPhrase());
            return;
        }
        webSocketConnection.setMaxBodySize(this.mMaxBodySize);
        final WebSocketConnection ws = new WebSocketConnection(webSocketConnection);
        UpgradeHeaders upgradeHeaders = new UpgradeHeaders(ws);
        if (!this.mUpgradeFirewall.allowUpgrade(upgradeHeaders, ws.getUrl())) {
            webSocketConnection.failUpgradeRequest(StatusCode.BAD_REQUEST.toCode(), StatusCode.BAD_REQUEST.toReasonPhrase());
            return;
        }
        webSocketConnection.setOnMessageListener(new WebSocketConnectionJni.OnMessageListener() { // from class: com.sony.mexi.orb.server.leza.websocket.WebSocketProcess.2
            @Override // com.sony.mexi.server.jni.WebSocketConnectionJni.OnMessageListener
            public void onStringMessage(String s) {
                OrbWebSocketMessageStringHandler requestHandler = new OrbWebSocketMessageStringHandler(handler, ws, s);
                try {
                    WebSocketProcess.this.mExecutorService.execute(requestHandler);
                } catch (RejectedExecutionException e) {
                    WebSocketProcess.onRejectedExecution(e, ws);
                }
            }

            @Override // com.sony.mexi.server.jni.WebSocketConnectionJni.OnMessageListener
            public void onBytesMessage(byte[] bytes) {
                OrbWebSocketMessageBytesHandler requestHandler = new OrbWebSocketMessageBytesHandler(handler, ws, bytes);
                try {
                    WebSocketProcess.this.mExecutorService.execute(requestHandler);
                } catch (RejectedExecutionException e) {
                    WebSocketProcess.onRejectedExecution(e, ws);
                }
            }
        });
        TreeMap<String, Object> header = new TreeMap<>();
        String orgSubProtocol = ws.getRequestHeader(KEY_WEBSOCKET_PROTOCOL);
        if (hasWebsocketProtocol(orgSubProtocol)) {
            header.put(KEY_WEBSOCKET_PROTOCOL, ACCEPT_WEBSOCKET_PROTOCOL);
        }
        webSocketConnection.upgrade(header);
    }

    private OrbWebSocketHandler lookUpHandler(String uri) {
        try {
            String path = PathValidator.toNormalizedPath(uri);
            return this.mHandlerGroup.getHandlerMap().get(path);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onRejectedExecution(RejectedExecutionException e, WebSocketConnection ws) {
        ws.close();
    }
}
