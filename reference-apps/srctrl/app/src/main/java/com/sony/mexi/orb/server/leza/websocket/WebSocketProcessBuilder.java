package com.sony.mexi.orb.server.leza.websocket;

import com.sony.mexi.orb.server.OriginalRemoteAddressHandler;
import com.sony.mexi.orb.server.RequestHeaders;
import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandlerGroup;
import com.sony.mexi.orb.server.websocket.WebSocketUpgradeRequestFirewall;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class WebSocketProcessBuilder {
    private WebSocketUpgradeRequestFirewall mWebSocketUpgradeFirewall = WebSocketFailOnOriginHeader.INSTANCE;
    private OrbWebSocketHandlerGroup handlerGroup = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private OriginalRemoteAddressHandler remoteAddressHandler = null;

    /* loaded from: classes.dex */
    private static class WebSocketFailOnOriginHeader implements WebSocketUpgradeRequestFirewall {
        public static final WebSocketFailOnOriginHeader INSTANCE = new WebSocketFailOnOriginHeader();

        @Override // com.sony.mexi.orb.server.websocket.WebSocketUpgradeRequestFirewall
        public boolean allowUpgrade(RequestHeaders headers, String url) {
            return headers.getHeader(HttpDefs.HEADER_ORIGIN) == null;
        }

        protected WebSocketFailOnOriginHeader() {
        }
    }

    public WebSocketProcessBuilder setHandlerGroup(OrbWebSocketHandlerGroup handlerGroup) {
        this.handlerGroup = handlerGroup;
        return this;
    }

    public WebSocketProcessBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public WebSocketProcessBuilder setRemoteAddressHandler(OriginalRemoteAddressHandler remoteAddressHandler) {
        this.remoteAddressHandler = remoteAddressHandler;
        return this;
    }

    public WebSocketProcessBuilder setUpgradeRequestFirewall(WebSocketUpgradeRequestFirewall firewall) {
        if (firewall == null) {
            throw new IllegalArgumentException("Firewall can't be null");
        }
        this.mWebSocketUpgradeFirewall = firewall;
        return this;
    }

    public WebSocketProcess build() {
        return new WebSocketProcess(this.handlerGroup, this.executorService, this.remoteAddressHandler, this.mWebSocketUpgradeFirewall);
    }
}
