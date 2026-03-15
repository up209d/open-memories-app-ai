package com.sony.mexi.orb.service;

/* loaded from: classes.dex */
public abstract class OrbAbstractClientCallbacks implements Callbacks {
    public abstract OrbAbstractClient getClient();

    @Override // com.sony.mexi.orb.service.Callbacks
    public int getTimeoutTime() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sony.mexi.orb.service.Callbacks
    public String getIncomingHeader(String header) {
        return getClient().getRequestHeader(header);
    }

    @Override // com.sony.mexi.orb.service.Callbacks
    public void setOutgoingHeader(String header, String value) {
        getClient().setResponseHeader(header, value);
    }

    @Override // com.sony.mexi.orb.service.Callbacks
    public String getRemoteAddress() {
        return getClient().getRemoteAddress();
    }

    @Override // com.sony.mexi.orb.service.Callbacks
    public String getURI() {
        return getClient().getRequestURI();
    }

    @Override // com.sony.mexi.orb.service.Callbacks
    public void setOnEvent(EventEmitter onClose) {
        getClient().setOnClose(onClose);
    }
}
