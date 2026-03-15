package com.sony.mexi.orb.server.websocket;

import java.io.IOException;

/* loaded from: classes.dex */
public interface OrbWebSocket {

    /* loaded from: classes.dex */
    public interface DisconnectListener {
        void invoke();
    }

    boolean addDisconnectListener(DisconnectListener disconnectListener);

    void close(int i, String str);

    String getRemoteAddress();

    String getRequestHeader(String str);

    String getUrl();

    boolean removeDisconnectListener(DisconnectListener disconnectListener);

    void send(String str) throws IOException;

    void send(byte[] bArr) throws IOException;
}
