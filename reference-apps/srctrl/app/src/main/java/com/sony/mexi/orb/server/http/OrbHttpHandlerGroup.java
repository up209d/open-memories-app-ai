package com.sony.mexi.orb.server.http;

import com.sony.mexi.orb.server.OrbServiceGroup;
import java.util.Map;
import java.util.SortedMap;

/* loaded from: classes.dex */
public interface OrbHttpHandlerGroup extends OrbServiceGroup {
    Map<String, ? extends OrbHttpHandler> getHandlerMap();

    SortedMap<String, ? extends OrbHttpHandler> getWildcardedHandlerMap();
}
