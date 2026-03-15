package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public class JsBoolean extends JsPrimitive {
    private static final long serialVersionUID = 1;
    private Boolean bool;

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.BOOLEAN;
    }

    public JsBoolean(Boolean b) {
        if (b == null) {
            throw new NullPointerException();
        }
        this.bool = b;
    }

    public String toString() {
        return this.bool.toString();
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isBoolean() {
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean toJavaBoolean() {
        return this.bool.booleanValue();
    }
}
