package com.sony.mexi.json;

import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class JsValue implements Serializable {
    private static final long serialVersionUID = 1;

    /* loaded from: classes.dex */
    public enum Type {
        BOOLEAN,
        NUMBER,
        STRING,
        ARRAY,
        OBJECT,
        ERROR,
        NULL,
        UNDEFINED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Type[] valuesCustom() {
            Type[] valuesCustom = values();
            int length = valuesCustom.length;
            Type[] typeArr = new Type[length];
            System.arraycopy(valuesCustom, 0, typeArr, 0, length);
            return typeArr;
        }
    }

    public abstract Type type();

    public boolean isBoolean() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isBooleanArray() {
        return false;
    }

    public boolean isIntArray() {
        return false;
    }

    public boolean isDoubleArray() {
        return false;
    }

    public boolean isStringArray() {
        return false;
    }

    public boolean isObjectArray() {
        return false;
    }

    public boolean toJavaBoolean() {
        throw new RuntimeException("Not Applicable");
    }

    public int toJavaInt() {
        throw new RuntimeException("Not Applicable");
    }

    public double toJavaDouble() {
        throw new RuntimeException("Not Applicable");
    }

    public String toJavaString() {
        throw new RuntimeException("Not Applicable");
    }

    public boolean[] toJavaBooleanArray() {
        throw new RuntimeException("Not Applicable");
    }

    public int[] toJavaIntArray() {
        throw new RuntimeException("Not Applicable");
    }

    public double[] toJavaDoubleArray() {
        throw new RuntimeException("Not Applicable");
    }

    public String[] toJavaStringArray() {
        throw new RuntimeException("Not Applicable");
    }
}
