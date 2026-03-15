package com.sony.mexi.orb.server;

/* loaded from: classes.dex */
public class OrbRequestHeadersImpl implements OrbRequestHeaders {
    private OrbClient client;

    public OrbRequestHeadersImpl(OrbClient client) {
        this.client = client;
    }

    public String getHeader(String name) {
        return this.client.getRequestHeader(name);
    }
}
