package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public final class JsUndefined extends JsPrimitive {
    private static JsUndefined jsUndefined = null;
    private static final long serialVersionUID = 1;

    private JsUndefined() {
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.UNDEFINED;
    }

    public String toString() {
        return "undefined";
    }

    public static JsUndefined getInstance() {
        if (jsUndefined == null) {
            jsUndefined = new JsUndefined();
        }
        return jsUndefined;
    }
}
