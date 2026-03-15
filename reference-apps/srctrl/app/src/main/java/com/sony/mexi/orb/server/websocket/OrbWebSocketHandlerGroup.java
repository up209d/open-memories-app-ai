package com.sony.mexi.orb.server.websocket;

import com.sony.mexi.orb.server.OrbServiceGroup;
import java.util.Map;

/* loaded from: classes.dex */
public interface OrbWebSocketHandlerGroup extends OrbServiceGroup {
    Map<String, ? extends OrbWebSocketHandler> getHandlerMap();
}
