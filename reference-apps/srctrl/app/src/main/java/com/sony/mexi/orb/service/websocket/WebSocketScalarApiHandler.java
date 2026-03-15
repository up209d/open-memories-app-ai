package com.sony.mexi.orb.service.websocket;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.websocket.OrbWebSocket;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandler;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.orb.service.MethodCallInfoCollector;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceInformationBase;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.orb.service.ScalarApiInvoker;
import com.sony.mexi.webapi.Protocol;
import com.sony.mexi.webapi.WebSocketCloseCode;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class WebSocketScalarApiHandler implements OrbWebSocketHandler, OrbServiceProvider {
    private static final String TAG = WebSocketScalarApiHandler.class.getSimpleName();
    ScalarApiInvoker invoker = new ScalarApiInvoker(this);
    private Authenticator mAuthenticator;
    private OrbAbstractVersion mProvider;

    @Override // com.sony.mexi.orb.server.OrbService
    public void init(String path) {
        initService();
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.mAuthenticator = authenticator;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public Authenticator getAuthenticator() {
        return this.mAuthenticator;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final Protocol getTransportProtocol() {
        return Protocol.WEBSOCKET;
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocketHandler
    public final void onMessage(OrbWebSocket ws, String str) {
        WebSocketEndPoint client = new WebSocketEndPoint(ws);
        MethodCallInfoCollector collector = new MethodCallInfoCollector();
        collector.setConnectionInfo(client, getTransportProtocol());
        DebugLogger.debug(TAG, "Recv(WebSocket): " + str);
        try {
            this.invoker.onWebApiRequest(client, str, collector);
        } catch (IOException e) {
            DebugLogger.debug(TAG, "WebSocket onMessage " + e.getMessage());
        }
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocketHandler
    public final void onMessage(OrbWebSocket ws, byte[] bytes) {
        ws.close(WebSocketCloseCode.UNSUPPORTED_DATA.toInt(), WebSocketCloseCode.UNSUPPORTED_DATA.toMessage());
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public boolean authorize(String method, OrbClient client) {
        return true;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final void addVersion(OrbAbstractVersion version) {
        this.mProvider = version;
        this.invoker.addVersion(version);
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final void addVersion(String name, OrbAbstractVersion version) {
        this.mProvider = version;
        this.invoker.addVersion(name, version);
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public OrbAbstractVersion getProvider() {
        return this.mProvider;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final OrbServiceInformationBase[] getServiceInformation() {
        return this.invoker.getServiceInformation();
    }
}
