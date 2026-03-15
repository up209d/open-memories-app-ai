package com.sony.mexi.orb.server.websocket;

import com.sony.mexi.orb.server.OrbService;

/* loaded from: classes.dex */
public interface OrbWebSocketHandler extends OrbService {
    void onMessage(OrbWebSocket orbWebSocket, String str);

    void onMessage(OrbWebSocket orbWebSocket, byte[] bArr);
}
