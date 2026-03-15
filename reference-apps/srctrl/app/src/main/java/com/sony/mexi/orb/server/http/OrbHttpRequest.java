package com.sony.mexi.orb.server.http;

import java.io.InputStream;

/* loaded from: classes.dex */
public interface OrbHttpRequest {

    /* loaded from: classes.dex */
    public interface DisconnectListener {
        void invoke();
    }

    boolean addDisconnectListener(DisconnectListener disconnectListener);

    String getCharset();

    int getContentLength();

    String getHeader(String str);

    InputStream getInputStream();

    String getLocalAddress();

    int getLocalPort();

    String getMethod();

    String getRemoteAddress();

    String getRequestURI();

    boolean removeDisconnectListener(DisconnectListener disconnectListener);
}
