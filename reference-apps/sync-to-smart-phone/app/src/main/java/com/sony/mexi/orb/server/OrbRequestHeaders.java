package com.sony.mexi.orb.server;

import javax.servlet.http.HttpServletRequest;

/* loaded from: classes.dex */
public class OrbRequestHeaders {
    private HttpServletRequest req;

    public OrbRequestHeaders(HttpServletRequest req) {
        this.req = req;
    }

    public String getHeader(String name) {
        return this.req.getHeader(name);
    }
}
