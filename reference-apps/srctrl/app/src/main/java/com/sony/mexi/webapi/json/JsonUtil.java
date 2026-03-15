package com.sony.mexi.webapi.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class JsonUtil {
    private JsonUtil() {
    }

    public static <T> JSONArray toJsonArray(T[] src, JsonConverter<T> conv) {
        if (src == null) {
            return null;
        }
        JSONArray dst = new JSONArray();
        for (T java : src) {
            put(dst, conv.toJson(java));
        }
        return dst;
    }

    public static <T> List<T> fromJsonArray(JSONArray src, JsonConverter<T> conv) {
        if (src == null) {
            return null;
        }
        List<T> dst = new ArrayList<>();
        for (int i = 0; i < src.length(); i++) {
            T val = conv.fromJson(getObject(src, i));
            dst.add(val);
        }
        return dst;
    }

    public static void putRequired(JSONObject dst, String key, Integer val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val.intValue());
    }

    public static void putOptional(JSONObject dst, String key, Integer val) {
        if (val != null) {
            put(dst, key, val.intValue());
        }
    }

    public static void putRequired(JSONObject dst, String key, Double val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val.doubleValue());
    }

    public static void putOptional(JSONObject dst, String key, Double val) {
        if (val != null) {
            put(dst, key, val.doubleValue());
        }
    }

    public static void putRequired(JSONObject dst, String key, Boolean val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val.booleanValue());
    }

    public static void putOptional(JSONObject dst, String key, Boolean val) {
        if (val != null) {
            put(dst, key, val.booleanValue());
        }
    }

    public static void putRequired(JSONObject dst, String key, String val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, String val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, JSONObject val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, JSONObject val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, int[] val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, int[] val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, double[] val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, double[] val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, String[] val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, String[] val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, boolean[] val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, boolean[] val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static void putRequired(JSONObject dst, String key, JSONArray val) {
        if (val == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        put(dst, key, val);
    }

    public static void putOptional(JSONObject dst, String key, JSONArray val) {
        if (val != null) {
            put(dst, key, val);
        }
    }

    public static int getInt(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                Object value = jsObject.get(key);
                if (value instanceof Integer) {
                    return ((Integer) value).intValue();
                }
                throw new JSONException("Not an integer value");
            } catch (JSONException e) {
                throw new JsonArgumentException("cannot be converted to an integer: " + key);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static int getInt(JSONObject jsObject, String key, int defaultVal) {
        try {
            int defaultVal2 = getInt(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static double getDouble(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                Object value = jsObject.get(key);
                if (value instanceof Double) {
                    return ((Double) value).doubleValue();
                }
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                throw new JSONException("Not a double value");
            } catch (JSONException e) {
                throw new JsonArgumentException("cannot be converted to a double: " + key);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static double getDouble(JSONObject jsObject, String key, double defaultVal) {
        try {
            double defaultVal2 = getDouble(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static boolean getBoolean(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                Object value = jsObject.get(key);
                if (value instanceof Boolean) {
                    return ((Boolean) value).booleanValue();
                }
                throw new JSONException("Not a boolean value");
            } catch (JSONException e) {
                throw new JsonArgumentException("cannot be converted to a boolean: " + key);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static boolean getBoolean(JSONObject jsObject, String key, boolean defaultVal) {
        try {
            boolean defaultVal2 = getBoolean(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static String getString(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                Object value = jsObject.get(key);
                if (value instanceof String) {
                    return (String) value;
                }
                throw new JSONException("Not a String value");
            } catch (JSONException e) {
                throw new JsonArgumentException("cannot be converted to a String: " + key);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static String getString(JSONObject jsObject, String key, String defaultVal) {
        try {
            String defaultVal2 = getString(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static JSONObject getObject(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                return jsObject.getJSONObject(key);
            } catch (JSONException e) {
                throw new JsonArgumentException(e);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static JSONObject getObject(JSONObject jsObject, String key, JSONObject defaultVal) {
        try {
            JSONObject defaultVal2 = getObject(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static JSONArray getArray(JSONObject jsObject, String key) {
        if (jsObject != null && jsObject.has(key)) {
            if (jsObject.isNull(key)) {
                throw new JsonNeedDefaultValueException("key exist but value is null: " + key);
            }
            try {
                return jsObject.getJSONArray(key);
            } catch (JSONException e) {
                throw new JsonArgumentException(e);
            }
        }
        throw new JsonNeedDefaultValueException("Parent is null or key does not exist: " + key);
    }

    public static JSONArray getArray(JSONObject jsObject, String key, JSONArray defaultVal) {
        try {
            JSONArray defaultVal2 = getArray(jsObject, key);
            return defaultVal2;
        } catch (JsonNeedDefaultValueException e) {
            return defaultVal;
        }
    }

    public static int[] getIntArray(JSONObject jsObject, String key) {
        if (jsObject == null) {
            return null;
        }
        return toIntArray(getArray(jsObject, key));
    }

    public static int[] getIntArray(JSONObject jsObject, String key, JSONArray defaultVal) {
        if (jsObject == null) {
            return null;
        }
        return toIntArray(getArray(jsObject, key, defaultVal));
    }

    public static double[] getDoubleArray(JSONObject jsObject, String key) {
        if (jsObject == null) {
            return null;
        }
        return toDoubleArray(getArray(jsObject, key));
    }

    public static double[] getDoubleArray(JSONObject jsObject, String key, JSONArray defaultVal) {
        if (jsObject == null) {
            return null;
        }
        return toDoubleArray(getArray(jsObject, key, defaultVal));
    }

    public static boolean[] getBooleanArray(JSONObject jsObject, String key) {
        if (jsObject == null) {
            return null;
        }
        return toBooleanArray(getArray(jsObject, key));
    }

    public static boolean[] getBooleanArray(JSONObject jsObject, String key, JSONArray defaultVal) {
        if (jsObject == null) {
            return null;
        }
        return toBooleanArray(getArray(jsObject, key, defaultVal));
    }

    public static String[] getStringArray(JSONObject jsObject, String key) {
        if (jsObject == null) {
            return null;
        }
        return toStringArray(getArray(jsObject, key));
    }

    public static String[] getStringArray(JSONObject jsObject, String key, JSONArray defaultVal) {
        if (jsObject == null) {
            return null;
        }
        return toStringArray(getArray(jsObject, key, defaultVal));
    }

    public static int getInt(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            throw new JsonArgumentException("integer in array must not be null.");
        }
        try {
            Object value = jsArray.get(index);
            if (value instanceof Integer) {
                return ((Integer) value).intValue();
            }
            throw new JSONException("Not an integer value");
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static double getDouble(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            throw new JsonArgumentException("double in array must not be null.");
        }
        try {
            Object value = jsArray.get(index);
            if (value instanceof Double) {
                return ((Double) value).doubleValue();
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            throw new JSONException("Not a double value");
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static boolean getBoolean(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            throw new JsonArgumentException("boolean in array must not be null.");
        }
        try {
            Object value = jsArray.get(index);
            if (value instanceof Boolean) {
                return ((Boolean) value).booleanValue();
            }
            throw new JSONException("Not a boolean value");
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static String getString(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        try {
            Object value = jsArray.get(index);
            if (value instanceof String) {
                return (String) value;
            }
            throw new JSONException("Not a String value");
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static JSONObject getObject(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        try {
            return jsArray.getJSONObject(index);
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static JSONArray getArray(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        try {
            return jsArray.getJSONArray(index);
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static int[] getIntArray(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        return toIntArray(getArray(jsArray, index));
    }

    public static double[] getDoubleArray(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        return toDoubleArray(getArray(jsArray, index));
    }

    public static boolean[] getBooleanArray(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        return toBooleanArray(getArray(jsArray, index));
    }

    public static String[] getStringArray(JSONArray jsArray, int index) {
        if (jsArray == null) {
            throw new NullPointerException();
        }
        if (jsArray.isNull(index)) {
            return null;
        }
        return toStringArray(getArray(jsArray, index));
    }

    private static int[] toIntArray(JSONArray jsArray) {
        if (jsArray == null) {
            return null;
        }
        int[] javaArray = new int[jsArray.length()];
        for (int i = 0; i < jsArray.length(); i++) {
            javaArray[i] = getInt(jsArray, i);
        }
        return javaArray;
    }

    private static double[] toDoubleArray(JSONArray jsArray) {
        if (jsArray == null) {
            return null;
        }
        double[] javaArray = new double[jsArray.length()];
        for (int i = 0; i < jsArray.length(); i++) {
            javaArray[i] = getDouble(jsArray, i);
        }
        return javaArray;
    }

    private static boolean[] toBooleanArray(JSONArray jsArray) {
        if (jsArray == null) {
            return null;
        }
        boolean[] javaArray = new boolean[jsArray.length()];
        for (int i = 0; i < jsArray.length(); i++) {
            javaArray[i] = getBoolean(jsArray, i);
        }
        return javaArray;
    }

    private static String[] toStringArray(JSONArray jsArray) {
        if (jsArray == null) {
            return null;
        }
        String[] javaArray = new String[jsArray.length()];
        for (int i = 0; i < jsArray.length(); i++) {
            if (jsArray.isNull(i)) {
                javaArray[i] = null;
            } else {
                javaArray[i] = getString(jsArray, i);
            }
        }
        return javaArray;
    }

    public static void put(JSONObject jsObject, String key, int javaValue) {
        try {
            jsObject.put(key, javaValue);
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, double javaValue) {
        try {
            jsObject.put(key, javaValue);
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, boolean javaValue) {
        try {
            jsObject.put(key, javaValue);
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, String javaValue) {
        try {
            if (javaValue == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, javaValue);
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, JSONObject javaValue) {
        try {
            if (javaValue == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, javaValue);
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, JSONArray javaValue) {
        try {
            if (javaValue == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, javaValue);
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, int[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, Integer[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, double[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, Double[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, boolean[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, Boolean[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONObject jsObject, String key, String[] javaArray) {
        try {
            if (javaArray == null) {
                jsObject.put(key, JSONObject.NULL);
            } else {
                jsObject.put(key, toJsArray(javaArray));
            }
        } catch (JSONException e) {
            throw new JsonArgumentException(e);
        }
    }

    public static void put(JSONArray jsArray, int javaValue) {
        jsArray.put(javaValue);
    }

    public static void put(JSONArray jsArray, double javaValue) {
        try {
            jsArray.put(javaValue);
        } catch (JSONException e) {
            throw new JsonArgumentException("Infinite or NaN is not allowed.");
        }
    }

    public static void put(JSONArray jsArray, boolean javaValue) {
        jsArray.put(javaValue);
    }

    public static void put(JSONArray jsArray, String javaValue) {
        if (javaValue == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(javaValue);
        }
    }

    public static void put(JSONArray jsArray, JSONObject javaValue) {
        if (javaValue == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(javaValue);
        }
    }

    public static void put(JSONArray jsArray, JSONArray javaValue) {
        if (javaValue == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(javaValue);
        }
    }

    public static void put(JSONArray jsArray, int[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, Integer[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, double[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, Double[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, boolean[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, Boolean[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static void put(JSONArray jsArray, String[] javaArray) {
        if (javaArray == null) {
            jsArray.put(JSONObject.NULL);
        } else {
            jsArray.put(toJsArray(javaArray));
        }
    }

    public static JSONArray toJsArray(int[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (int i : javaArray) {
            jsArray.put(i);
        }
        return jsArray;
    }

    public static JSONArray toJsArray(Integer[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (int i = 0; i < javaArray.length; i++) {
            if (javaArray[i] == null) {
                throw new JsonArgumentException("null value in the primitive array");
            }
            jsArray.put(javaArray[i].intValue());
        }
        return jsArray;
    }

    public static JSONArray toJsArray(double[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (double d : javaArray) {
            try {
                jsArray.put(d);
            } catch (JSONException e) {
                throw new JsonArgumentException("Infinite and NaN is not allowed.");
            }
        }
        return jsArray;
    }

    public static JSONArray toJsArray(Double[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (int i = 0; i < javaArray.length; i++) {
            if (javaArray[i] == null) {
                throw new JsonArgumentException("null value in the primitive array");
            }
            try {
                jsArray.put(javaArray[i].doubleValue());
            } catch (JSONException e) {
                throw new JsonArgumentException("Infinite and NaN is not allowed.");
            }
        }
        return jsArray;
    }

    public static JSONArray toJsArray(boolean[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (boolean z : javaArray) {
            jsArray.put(z);
        }
        return jsArray;
    }

    public static JSONArray toJsArray(Boolean[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (int i = 0; i < javaArray.length; i++) {
            if (javaArray[i] == null) {
                throw new JsonArgumentException("null value in the primitive array");
            }
            jsArray.put(javaArray[i].booleanValue());
        }
        return jsArray;
    }

    public static JSONArray toJsArray(String[] javaArray) {
        if (javaArray == null) {
            return null;
        }
        JSONArray jsArray = new JSONArray();
        for (String value : javaArray) {
            jsArray.put(value);
        }
        return jsArray;
    }
}
