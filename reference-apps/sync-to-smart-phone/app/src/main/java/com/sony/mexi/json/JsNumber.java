package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public class JsNumber extends JsPrimitive {
    private static final long serialVersionUID = 1;
    private Object obj;

    public JsNumber(Integer i) {
        this.obj = null;
        if (i == null) {
            throw new IllegalArgumentException();
        }
        this.obj = i;
    }

    public JsNumber(Long l) {
        this.obj = null;
        if (l == null) {
            throw new IllegalArgumentException();
        }
        this.obj = l;
    }

    public JsNumber(Double d) {
        this.obj = null;
        if (d == null) {
            throw new IllegalArgumentException();
        }
        this.obj = d;
    }

    public JsNumber(Enum<?> e) {
        this.obj = null;
        if (e == null) {
            throw new NullPointerException();
        }
        this.obj = new Integer(e.ordinal());
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.NUMBER;
    }

    public String toString() {
        return this.obj.toString();
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isInt() {
        Number num = (Number) this.obj;
        double d = num.doubleValue();
        return Math.floor(d) == d;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isDouble() {
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public int toJavaInt() {
        Number num = (Number) this.obj;
        double d = num.doubleValue();
        if (Math.floor(d) == d) {
            return num.intValue();
        }
        throw new RuntimeException("Not Integer");
    }

    @Override // com.sony.mexi.json.JsValue
    public double toJavaDouble() {
        Number num = (Number) this.obj;
        return num.doubleValue();
    }
}
