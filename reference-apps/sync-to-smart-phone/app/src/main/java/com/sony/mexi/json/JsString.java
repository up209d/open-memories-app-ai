package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public class JsString extends JsPrimitive {
    private static final long serialVersionUID = 1;
    private String json;
    private String str;

    public JsString(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.str = s;
        this.json = toJson(s);
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.STRING;
    }

    public String toString() {
        return this.json;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isString() {
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public String toJavaString() {
        return this.str;
    }

    public static String toJson(String s) {
        return "\"" + JsonUtil.escape(s) + "\"";
    }
}
