package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.mexi.json.JsValue;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class JsObject extends JsValue {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long serialVersionUID = 1;
    HashMap<String, JsValue> map;

    static {
        $assertionsDisabled = !JsObject.class.desiredAssertionStatus();
    }

    public JsObject() {
        this(null);
    }

    public JsObject(Object object) {
        this.map = new HashMap<>();
        if (object != null) {
            Class<?> c = object.getClass();
            if (c.isPrimitive() || c == String.class || boolean[].class.isAssignableFrom(c) || int[].class.isAssignableFrom(c) || double[].class.isAssignableFrom(c) || String[].class.isAssignableFrom(c) || Object[].class.isAssignableFrom(object.getClass())) {
                System.err.println("ERROR only custom classes are allowed");
            } else {
                addObject(object);
            }
        }
    }

    private void addObject(Object object) {
        if (object != null) {
            Field[] fields = object.getClass().getFields();
            for (Field field : fields) {
                String name = field.getName();
                JsValue value = null;
                try {
                    value = toJsValue(field, object);
                } catch (Exception e) {
                    System.err.println("Failed to retrieve value for filed : " + name);
                }
                this.map.put(name, value);
            }
        }
    }

    @Override // com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.OBJECT;
    }

    @Override // com.sony.mexi.json.JsValue
    public boolean isObject() {
        return true;
    }

    public void put(String key, JsValue val) {
        this.map.put(key, val);
    }

    public JsValue get(String key) {
        return this.map.get(key);
    }

    public String toString() {
        String res = "{";
        boolean first = true;
        for (Map.Entry<String, JsValue> e : this.map.entrySet()) {
            if (e.getValue().type() != JsValue.Type.UNDEFINED) {
                if (first) {
                    first = false;
                } else {
                    res = String.valueOf(res) + AbstractSupportedChecker.SEPARATOR;
                }
                res = String.valueOf(res) + "\"" + e.getKey() + "\":" + e.getValue();
            }
        }
        return String.valueOf(res) + "}";
    }

    private JsValue toJsValue(Field field, Object object) {
        Class<?> c = field.getType();
        if (c == Integer.TYPE) {
            return getInt(field, object);
        }
        if (c == Boolean.TYPE) {
            return getBoolean(field, object);
        }
        if (c == Double.TYPE) {
            return getDouble(field, object);
        }
        if (c == String.class) {
            return getString(field, object);
        }
        if (c == int[].class) {
            return getIntArray(field, object);
        }
        if (c == boolean[].class) {
            return getBooleanArray(field, object);
        }
        if (c == double[].class) {
            return getDoubleArray(field, object);
        }
        if (c == String[].class) {
            return getStringArray(field, object);
        }
        if (c.isEnum()) {
            return getEnum(field, object);
        }
        if (c.isArray() && c.getComponentType().isEnum()) {
            return getEnumArray(field, object);
        }
        return null;
    }

    private JsValue getInt(Field field, Object object) {
        try {
            JsValue value = new JsNumber(Integer.valueOf(field.getInt(object)));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getDouble(Field field, Object object) {
        try {
            JsValue value = new JsNumber(Double.valueOf(field.getDouble(object)));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getBoolean(Field field, Object object) {
        try {
            JsValue value = new JsBoolean(Boolean.valueOf(field.getBoolean(object)));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getString(Field field, Object object) {
        try {
            JsValue value = new JsString(field.get(object).toString());
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getIntArray(Field field, Object object) {
        try {
            JsValue value = new JsArray((int[]) field.get(object));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getDoubleArray(Field field, Object object) {
        try {
            JsValue value = new JsArray((double[]) field.get(object));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getBooleanArray(Field field, Object object) {
        try {
            JsValue value = new JsArray((boolean[]) field.get(object));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getStringArray(Field field, Object object) {
        try {
            JsValue value = new JsArray((String[]) field.get(object));
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getEnum(Field field, Object object) {
        try {
            Enum<?> item = (Enum) field.get(object);
            JsValue value = new JsNumber(item);
            return value;
        } catch (Exception e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    private JsValue getEnumArray(Field field, Object object) {
        JsArray array = new JsArray(new JsValue[0]);
        try {
            Enum<?>[] enums = (Enum[]) field.get(object);
            for (Enum<?> item : enums) {
                array.add(new JsNumber(item));
            }
        } catch (Exception e) {
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        return array;
    }
}
