package com.sony.mexi.orb.server.leza.http;

import com.sony.mexi.orb.server.OriginalRemoteAddressHandler;
import com.sony.mexi.orb.server.http.OrbHttpHandlerGroup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class HttpProcessBuilder {
    private OrbHttpHandlerGroup serviceGroup = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private OriginalRemoteAddressHandler remoteAddressHandler = null;
    private boolean supportCORS = false;

    public HttpProcessBuilder setHandlerGroup(OrbHttpHandlerGroup handlerGroup) {
        this.serviceGroup = handlerGroup;
        return this;
    }

    public HttpProcessBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public HttpProcessBuilder setRemoteAddressHandler(OriginalRemoteAddressHandler remoteAddressHandler) {
        this.remoteAddressHandler = remoteAddressHandler;
        return this;
    }

    public HttpProcessBuilder setSupportCORS(boolean supportCORS) {
        this.supportCORS = supportCORS;
        return this;
    }

    public HttpProcess build() {
        return new HttpProcess(this.serviceGroup, this.executorService, this.remoteAddressHandler, this.supportCORS);
    }
}
