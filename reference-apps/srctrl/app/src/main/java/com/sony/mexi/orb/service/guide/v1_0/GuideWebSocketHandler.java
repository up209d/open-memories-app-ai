package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.orb.service.websocket.WebSocketScalarApiHandler;

/* loaded from: classes.dex */
public final class GuideWebSocketHandler extends WebSocketScalarApiHandler {
    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public void initService() {
        addVersion(new GuideServiceProvider());
    }
}
