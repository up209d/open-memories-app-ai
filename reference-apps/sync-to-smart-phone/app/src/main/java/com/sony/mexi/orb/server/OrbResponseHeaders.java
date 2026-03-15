package com.sony.mexi.orb.server;

import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class OrbResponseHeaders {
    private HttpServletResponse res;

    public OrbResponseHeaders(HttpServletResponse res) {
        this.res = res;
    }

    public void setHeader(String name, String value) {
        this.res.setHeader(name, value);
    }
}
