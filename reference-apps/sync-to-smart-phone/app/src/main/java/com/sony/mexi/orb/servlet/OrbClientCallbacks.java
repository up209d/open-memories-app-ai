package com.sony.mexi.orb.servlet;

import com.sony.mexi.json.JsError;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.webapi.Callbacks;

/* loaded from: classes.dex */
public class OrbClientCallbacks implements Callbacks {
    protected JsValue response = null;

    public JsValue getServerResponse() {
        return this.response;
    }

    @Override // com.sony.mexi.webapi.Callbacks
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sony.mexi.webapi.Callbacks
    public int getTimeoutTime() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sony.mexi.webapi.Callbacks
    public void handleStatus(int code, String message) {
        this.response = new JsError(code, message);
    }
}
