package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.mexi.json.JsValue;
import java.util.Iterator;
import java.util.Vector;

/* loaded from: classes.dex */
public class JsArray extends JsValue {
    private static final long serialVersionUID = 1;
    private Vector<JsValue> vs = new Vector<>();

    public JsArray(Object[] object) {
        Class<?> c = object.getClass();
        if (boolean[].class.isAssignableFrom(c) || int[].class.isAssignableFrom(c) || double[].class.isAssignableFrom(c) || String[].class.isAssignableFrom(c) || JsValue[].class.isAssignableFrom(c)) {
            System.err.println("ERROR only custom classes are allowed");
            return;
        }
        for (Object temp : object) {
            this.vs.add(new JsObject(temp));
        }
    }

    public JsArray(JsValue... vals) {
        for (JsValue v : vals) {
            if (v == null) {
                throw new NullPointerException();
            }
            this.vs.add(v);
        }
    }

    public JsArray(int[] is) {
        for (int i : is) {
            this.vs.add(new JsNumber(Integer.valueOf(i)));
        }
    }

    public JsArray(double[] ds) {
        for (double d : ds) {
            this.vs.add(new JsNumber(Double.valueOf(d)));
        }
    }

    public JsArray(boolean[] bs) {
        for (boolean b : bs) {
            this.vs.add(new JsBoolean(Boolean.valueOf(b)));
        }
    }

    public JsArray(String[] ss) {
        for (String s : ss) {
            this.vs.add(new JsString(s));
        }
    }

    public JsArray(Enum<?>[] enumArr) {
        for (Enum<?> e : enumArr) {
            this.vs.add(new JsNumber(Integer.valueOf(e.ordinal())));
        }
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.ARRAY;
    }

    public int length() {
        return this.vs.size();
    }

    public void add(JsValue val) {
        this.vs.add(val);
    }

    public JsValue get(int i) {
        return this.vs.get(i);
    }

    public void clear() {
        this.vs.clear();
    }

    public String toString() {
        String res = "[";
        int len = this.vs.size();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
            }
            res = String.valueOf(res) + this.vs.get(i);
        }
        return String.valueOf(res) + "]";
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isIntArray() {
        Iterator<JsValue> it = this.vs.iterator();
        while (it.hasNext()) {
            JsValue v = it.next();
            if (!v.isInt()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isDoubleArray() {
        Iterator<JsValue> it = this.vs.iterator();
        while (it.hasNext()) {
            JsValue v = it.next();
            if (!v.isDouble()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isBooleanArray() {
        Iterator<JsValue> it = this.vs.iterator();
        while (it.hasNext()) {
            JsValue v = it.next();
            if (!v.isBoolean()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isStringArray() {
        Iterator<JsValue> it = this.vs.iterator();
        while (it.hasNext()) {
            JsValue v = it.next();
            if (!v.isString()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isObjectArray() {
        Iterator<JsValue> it = this.vs.iterator();
        while (it.hasNext()) {
            JsValue v = it.next();
            if (!v.isObject()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sony.mexi.json.JsValue
    public int[] toJavaIntArray() {
        int len = this.vs.size();
        int[] a = new int[len];
        for (int i = 0; i < len; i++) {
            a[i] = this.vs.get(i).toJavaInt();
        }
        return a;
    }

    @Override // com.sony.mexi.json.JsValue
    public double[] toJavaDoubleArray() {
        int len = this.vs.size();
        double[] a = new double[len];
        for (int i = 0; i < len; i++) {
            a[i] = this.vs.get(i).toJavaDouble();
        }
        return a;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean[] toJavaBooleanArray() {
        int len = this.vs.size();
        boolean[] a = new boolean[len];
        for (int i = 0; i < len; i++) {
            a[i] = this.vs.get(i).toJavaBoolean();
        }
        return a;
    }

    @Override // com.sony.mexi.json.JsValue
    public String[] toJavaStringArray() {
        int len = this.vs.size();
        String[] a = new String[len];
        for (int i = 0; i < len; i++) {
            a[i] = this.vs.get(i).toJavaString();
        }
        return a;
    }
}
