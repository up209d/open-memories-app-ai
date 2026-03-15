package com.sony.mexi.webapi;

/* loaded from: classes.dex */
public enum Type {
    VOID,
    INT,
    DOUBLE,
    BOOL,
    STRING,
    INT_ARRAY,
    DOUBLE_ARRAY,
    BOOL_ARRAY,
    STRING_ARRAY,
    ENUM,
    CALLBACKS,
    OBJECT,
    UNKNOWN;

    private static /* synthetic */ int[] $SWITCH_TABLE$com$sony$mexi$webapi$Type;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static Type[] valuesCustom() {
        Type[] valuesCustom = values();
        int length = valuesCustom.length;
        Type[] typeArr = new Type[length];
        System.arraycopy(valuesCustom, 0, typeArr, 0, length);
        return typeArr;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$sony$mexi$webapi$Type() {
        int[] iArr = $SWITCH_TABLE$com$sony$mexi$webapi$Type;
        if (iArr == null) {
            iArr = new int[valuesCustom().length];
            try {
                iArr[BOOL.ordinal()] = 4;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[BOOL_ARRAY.ordinal()] = 8;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[CALLBACKS.ordinal()] = 11;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[DOUBLE_ARRAY.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[ENUM.ordinal()] = 10;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[INT.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[INT_ARRAY.ordinal()] = 6;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[OBJECT.ordinal()] = 12;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[STRING.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[STRING_ARRAY.ordinal()] = 9;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[UNKNOWN.ordinal()] = 13;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[VOID.ordinal()] = 1;
            } catch (NoSuchFieldError e13) {
            }
            $SWITCH_TABLE$com$sony$mexi$webapi$Type = iArr;
        }
        return iArr;
    }

    public static Type classToType(Class<?> c) {
        if (c == null || c == Void.TYPE) {
            return VOID;
        }
        if (c == Integer.TYPE) {
            return INT;
        }
        if (c == Double.TYPE) {
            return DOUBLE;
        }
        if (c == Boolean.TYPE) {
            return BOOL;
        }
        if (c == String.class) {
            return STRING;
        }
        if (c == int[].class) {
            return INT_ARRAY;
        }
        if (c == double[].class) {
            return DOUBLE_ARRAY;
        }
        if (c == boolean[].class) {
            return BOOL_ARRAY;
        }
        if (c == String[].class) {
            return STRING_ARRAY;
        }
        if (c.isEnum()) {
            return ENUM;
        }
        if (c.isInterface() && Callbacks.class.isAssignableFrom(c)) {
            return CALLBACKS;
        }
        if (Object.class.isAssignableFrom(c)) {
            return OBJECT;
        }
        return UNKNOWN;
    }

    public static String typeToString(Type type) {
        switch ($SWITCH_TABLE$com$sony$mexi$webapi$Type()[type.ordinal()]) {
            case 1:
                return "void";
            case 2:
                return "int";
            case 3:
                return "double";
            case 4:
                return "boolean";
            case 5:
                return "String";
            case 6:
                return "int[]";
            case 7:
                return "double[]";
            case 8:
                return "boolean[]";
            case 9:
                return "String[]";
            default:
                return "";
        }
    }
}
