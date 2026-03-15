package com.sony.mexi.orb.server.leza;

import com.sony.mexi.orb.server.OrbProcess;
import com.sony.mexi.server.jni.HttpServerJni;

/* loaded from: classes.dex */
public interface ServerProcess extends OrbProcess {
    void init(HttpServerJni httpServerJni, int i);

    void shutdown();
}
