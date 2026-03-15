package com.sony.scalar.webapi.lib.authlib;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.service.http.HttpScalarApiHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class AuthApiHandler extends HttpScalarApiHandler {
    private static final String REGEX_PATTERN = "^/sony/(.*)$";
    private Pattern mPattern = Pattern.compile(REGEX_PATTERN);

    @Override // com.sony.mexi.orb.service.http.HttpScalarApiHandler, com.sony.mexi.orb.service.OrbServiceProvider
    public boolean authorize(String method, OrbClient client) {
        DebugLogger.debug("AuthApiHandler", "authorize: " + method);
        AuthLibManager authLibMgr = AuthLibManager.getInstance();
        Matcher matcher = this.mPattern.matcher(client.getRequestURI());
        if (!matcher.find()) {
            return true;
        }
        String serviceName = matcher.group(1);
        String apiName = String.valueOf(serviceName) + "/" + method;
        return apiName.equals("accessControl/actEnableMethods") || !authLibMgr.isPrivateMethod(apiName) || authLibMgr.isAuthenticated(apiName);
    }

    public void initService() {
    }
}
