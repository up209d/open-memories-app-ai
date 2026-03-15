package com.sony.mexi.orb.servlet;

import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.OrbRequestHeaders;
import com.sony.mexi.orb.server.OrbRequestHeadersImpl;
import com.sony.mexi.orb.service.servlet.OrbServletMethodInvoker;

/* loaded from: classes.dex */
public class OrbMethodInvoker extends OrbServletMethodInvoker {
    private static final long serialVersionUID = 1;

    public boolean authorize(String method, OrbClient client) {
        System.out.println("called authorize, method: " + method);
        return authorize(method, new OrbRequestHeadersImpl(client));
    }

    protected boolean authorize(String method, OrbRequestHeaders reqHeaders) {
        return true;
    }
}
