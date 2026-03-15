package com.sony.mexi.orb.service.accesscontrol;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.service.http.HttpScalarApiHandler;

/* loaded from: classes.dex */
public class AccessControlInterfaceService extends HttpScalarApiHandler {
    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final void initService() {
        DebugLogger.debug("AcceccControlInterface", "init");
        addVersion("1.0", new com.sony.mexi.orb.service.accesscontrol.v1_0.AccessControlInterfaceService());
    }
}
