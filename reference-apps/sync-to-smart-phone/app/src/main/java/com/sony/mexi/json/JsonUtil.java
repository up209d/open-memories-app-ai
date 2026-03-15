package com.sony.mexi.json;

import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.json.JsValue;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Set;

/* loaded from: classes.dex */
public final class JsonUtil {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !JsonUtil.class.desiredAssertionStatus();
    }

    private JsonUtil() {
    }

    private static String escape(String s, boolean control, boolean unicode) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c <= '~') {
                if (control) {
                    switch (c) {
                        case 0:
                            builder.append("\\0");
                            break;
                        case '\b':
                            builder.append("\\b");
                            break;
                        case '\t':
                            builder.append("\\t");
                            break;
                        case '\n':
                            builder.append("\\n");
                            break;
                        case '\f':
                            builder.append("\\f");
                            break;
                        case '\r':
                            builder.append("\\r");
                            break;
                        case '\"':
                            builder.append("\\\"");
                            break;
                        case '\\':
                            builder.append("\\\\");
                            break;
                        default:
                            builder.append(c);
                            break;
                    }
                } else {
                    builder.append(c);
                }
            } else if (unicode) {
                builder.append("\\u");
                String h = Integer.toHexString(c);
                for (int j = h.length(); j < 4; j++) {
                    builder.append('0');
                }
                builder.append(h);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static String escape(String s) {
        return escape(s, true, false);
    }

    public static String unicodeEscape(String s) {
        return escape(s, false, true);
    }

    public static JsValue urlEncode(JsValue val) {
        if (val.type() == JsValue.Type.OBJECT) {
            JsObject v = (JsObject) val;
            JsObject o = new JsObject();
            Set<String> keys = v.map.keySet();
            for (String key : keys) {
                String k = "";
                try {
                    k = URLEncoder.encode(key, AutoSyncConstants.TEXT_ENCODING_UTF8);
                } catch (Exception e) {
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                }
                o.put(k, urlEncode(v.get(key)));
            }
            return o;
        }
        if (val.type() == JsValue.Type.ARRAY) {
            JsArray v2 = (JsArray) val;
            int len = v2.length();
            JsArray a = new JsArray(new JsValue[0]);
            for (int i = 0; i < len; i++) {
                a.add(urlEncode(v2.get(i)));
            }
            return a;
        }
        if (val.type() != JsValue.Type.STRING) {
            return val;
        }
        String s = "";
        try {
            s = URLEncoder.encode(val.toJavaString(), AutoSyncConstants.TEXT_ENCODING_UTF8);
        } catch (Exception e2) {
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        return new JsString(s);
    }

    public static JsValue urlDecode(JsValue val) {
        if (val.type() == JsValue.Type.OBJECT) {
            JsObject v = (JsObject) val;
            JsObject o = new JsObject();
            Set<String> keys = v.map.keySet();
            for (String key : keys) {
                String k = "";
                try {
                    k = URLDecoder.decode(key, AutoSyncConstants.TEXT_ENCODING_UTF8);
                } catch (Exception e) {
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                }
                o.put(k, urlDecode(v.get(key)));
            }
            return o;
        }
        if (val.type() == JsValue.Type.ARRAY) {
            JsArray v2 = (JsArray) val;
            int len = v2.length();
            JsArray a = new JsArray(new JsValue[0]);
            for (int i = 0; i < len; i++) {
                a.add(urlDecode(v2.get(i)));
            }
            return a;
        }
        if (val.type() != JsValue.Type.STRING) {
            return val;
        }
        String s = "";
        try {
            s = URLDecoder.decode(val.toJavaString(), AutoSyncConstants.TEXT_ENCODING_UTF8);
        } catch (Exception e2) {
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        return new JsString(s);
    }
}
