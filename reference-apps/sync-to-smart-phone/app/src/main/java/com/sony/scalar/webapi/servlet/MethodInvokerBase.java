package com.sony.scalar.webapi.servlet;

import com.sony.mexi.orb.server.OrbRequestHeaders;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.scalar.webapi.lib.authlib.AuthLibManager;
import com.sony.scalar.webapi.lib.authlib.DevLog;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MethodInvokerBase extends OrbMethodInvoker {
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.servlet.OrbMethodInvoker
    protected boolean authorize(String method, OrbRequestHeaders reqHeaders) {
        DevLog.d("authorize");
        AuthLibManager authLibMgr = AuthLibManager.getInstance();
        Iterator<String> it = authLibMgr.getServiceName(getClass().getSimpleName()).iterator();
        while (it.hasNext()) {
            String serviceName = it.next();
            String apiName = String.valueOf(serviceName) + "/" + method;
            if (apiName.equals("accessControl/actEnableMethods")) {
                return true;
            }
            if (authLibMgr.isPrivateMethod(apiName)) {
                return authLibMgr.isAuthenticated(apiName);
            }
        }
        return true;
    }
}
