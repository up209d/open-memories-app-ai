package com.sony.mexi.webapi;

import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public abstract class DefaultCallbacks implements Callbacks {
    private static AtomicLong callbackId = new AtomicLong(0);
    private String name = null;

    @Override // com.sony.mexi.webapi.Callbacks
    public String getName() {
        if (this.name == null) {
            this.name = "#" + Long.toString(callbackId.get());
            callbackId.incrementAndGet();
        }
        return this.name;
    }

    @Override // com.sony.mexi.webapi.Callbacks
    public int getTimeoutTime() {
        return 300000;
    }

    @Override // com.sony.mexi.webapi.Callbacks
    public void handleStatus(int code, String message) {
    }
}
