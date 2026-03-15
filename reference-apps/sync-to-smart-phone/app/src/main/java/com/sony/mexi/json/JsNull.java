package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public final class JsNull extends JsPrimitive {
    private static JsNull jsNull = null;
    private static final long serialVersionUID = 1;

    private JsNull() {
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.NULL;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isNull() {
        return true;
    }

    public String toString() {
        return "null";
    }

    public static JsNull getInstance() {
        if (jsNull == null) {
            jsNull = new JsNull();
        }
        return jsNull;
    }
}
