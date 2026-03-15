package com.sony.mexi.orb.server;

/* loaded from: classes.dex */
public interface OrbClient {
    String getRemoteAddress();

    String getRequestHeader(String str);

    String getRequestURI();

    void setResponseHeader(String str, String str2);
}
