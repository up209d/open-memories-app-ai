package com.sony.mexi.webapi;

/* loaded from: classes.dex */
public interface ServiceClient extends Service {
    int close();

    String getClientName();

    String getClientVersion();

    boolean isOpen();

    int onClose(CloseHandler closeHandler);

    int onOpen(OpenHandler openHandler);

    int open();
}
