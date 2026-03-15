package com.sony.mexi.orb.server.leza;

import com.sony.mexi.orb.server.OrbTransport;
import com.sony.mexi.orb.server.leza.Server;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ServerBuilder {
    private static final int DEFAULT_BACKLOG = 511;
    private static final int DEFAULT_BODY_SIZE = 8192;
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_MAX_CONNECTION = -1;
    private static final int DEFAULT_MAX_TIMEOUT = -1;
    private int port;
    private String host = DEFAULT_HOST;
    private int backlog = DEFAULT_BACKLOG;
    private int maxBodySize = DEFAULT_BODY_SIZE;
    private int maxConnections = -1;
    private int timeoutInMilliSec = -1;
    private Server.ServerStatusListener statusListener = DefaultServerStatusListener.INSTANCE;
    Map<OrbTransport, ServerProcess> processMap = new HashMap();

    /* loaded from: classes.dex */
    private static class DefaultServerStatusListener implements Server.ServerStatusListener {
        public static final DefaultServerStatusListener INSTANCE = new DefaultServerStatusListener();

        @Override // com.sony.mexi.orb.server.leza.Server.ServerStatusListener
        public void onServerStatusChange(Server.Status serverStatus, Server.Reason reason) {
        }

        protected DefaultServerStatusListener() {
        }
    }

    public ServerBuilder(int port) {
        this.port = port;
    }

    public ServerBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ServerBuilder setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public ServerBuilder setServerStatusListener(Server.ServerStatusListener serverStatusListener) {
        if (serverStatusListener == null) {
            throw new IllegalArgumentException("statusListener MUST NOT be null");
        }
        this.statusListener = serverStatusListener;
        return this;
    }

    public ServerBuilder setMaxBodySizeInByte(int maxBodySize) {
        this.maxBodySize = maxBodySize;
        return this;
    }

    public ServerBuilder setStreamTimeoutInMilliSec(int timeoutInMilliSec) {
        this.timeoutInMilliSec = timeoutInMilliSec;
        return this;
    }

    public ServerBuilder setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public ServerBuilder addProcess(ServerProcess process) {
        OrbTransport protocol = process.getTransport();
        if (protocol == null) {
            throw new RuntimeException("Illegal Service Protocol");
        }
        if (this.processMap.containsKey(protocol)) {
            throw new RuntimeException("Request handler added already for this protocol");
        }
        this.processMap.put(protocol, process);
        return this;
    }

    public Server build() {
        return new Server(this.port, this.host, this.backlog, this.maxBodySize, this.maxConnections, this.timeoutInMilliSec, this.processMap, this.statusListener);
    }
}
