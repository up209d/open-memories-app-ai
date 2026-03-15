package com.sony.mexi.orb.server.websocket;

import com.sony.mexi.orb.server.RequestHeaders;

/* loaded from: classes.dex */
public interface WebSocketUpgradeRequestFirewall {
    boolean allowUpgrade(RequestHeaders requestHeaders, String str);
}
