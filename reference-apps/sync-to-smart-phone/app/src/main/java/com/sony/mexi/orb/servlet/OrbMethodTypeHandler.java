package com.sony.mexi.orb.servlet;

import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsString;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.webapi.MethodTypeHandler;

/* loaded from: classes.dex */
public class OrbMethodTypeHandler extends OrbClientCallbacks implements MethodTypeHandler {
    private JsArray result;

    @Override // com.sony.mexi.webapi.MethodTypeHandler
    public void handleMethodType(String methodName, String[] parameterTypes, String[] resultTypes, String version) {
        if (this.response == null) {
            this.response = new JsArray(new JsValue[0]);
        }
        this.result = new JsArray(new JsValue[0]);
        this.result.add(new JsString(methodName));
        this.result.add(new JsArray(parameterTypes));
        this.result.add(new JsArray(resultTypes));
        this.result.add(new JsString(version));
        ((JsArray) this.response).add(this.result);
    }
}
