package com.sony.imaging.app.srctrl.webapi;

import android.util.Log;
import com.sony.mexi.orb.service.accesscontrol.v1_0.AccessControlInterfaceService;
import com.sony.mexi.orb.service.http.HttpScalarApiHandler;

/* loaded from: classes.dex */
public class AccessControlInterfaceServlet extends HttpScalarApiHandler {
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public void initService() {
        Log.v("AcceccControlInterface", "initService");
        addVersion("1.0", new AccessControlInterfaceService());
    }
}
