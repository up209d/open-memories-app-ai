package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.mexi.json.mouse.SourceString;

/* loaded from: classes.dex */
public final class Json {
    private Json() {
    }

    public static JsValue parse(String json) {
        JsonParser p = new JsonParser();
        if (p.parse(new SourceString(json))) {
            return p.getValue();
        }
        return null;
    }

    public static String stringify(int i) {
        return Integer.toString(i);
    }

    public static String stringify(double d) {
        return Double.toString(d);
    }

    public static String stringify(boolean b) {
        return Boolean.toString(b);
    }

    public static String stringify(String s) {
        return s == null ? "null" : JsString.toJson(s);
    }

    public static String stringify(int[] is) {
        if (is == null) {
            return "null";
        }
        String res = "[";
        int len = is.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
            }
            res = String.valueOf(res) + is[i];
        }
        return String.valueOf(res) + "]";
    }

    public static String stringify(double[] ds) {
        if (ds == null) {
            return "null";
        }
        String res = "[";
        int len = ds.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
            }
            res = String.valueOf(res) + ds[i];
        }
        return String.valueOf(res) + "]";
    }

    public static String stringify(boolean[] bs) {
        if (bs == null) {
            return "null";
        }
        String res = "[";
        int len = bs.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
            }
            res = String.valueOf(res) + bs[i];
        }
        return String.valueOf(res) + "]";
    }

    public static String stringify(String[] ss) {
        if (ss == null) {
            return "null";
        }
        String res = "[";
        int len = ss.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
            }
            res = String.valueOf(res) + JsString.toJson(ss[i]);
        }
        return String.valueOf(res) + "]";
    }

    public static String connect(String... jsons) {
        String res = "[";
        int len = jsons.length;
        for (int i = 0; i < len; i++) {
            if (jsons[i].length() != 0) {
                if (i > 0) {
                    res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
                }
                res = String.valueOf(res) + jsons[i];
            }
        }
        return String.valueOf(res) + "]";
    }
}
