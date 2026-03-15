package com.sony.mexi.orb.service;

import com.sony.mexi.webapi.CallbackHandler;

/* loaded from: classes.dex */
public interface Callbacks extends CallbackHandler {
    String getIncomingHeader(String str);

    String getRemoteAddress();

    int getTimeoutTime();

    String getURI();

    void sendRaw(String str);

    void setOnEvent(EventEmitter eventEmitter);

    void setOutgoingHeader(String str, String str2);
}
