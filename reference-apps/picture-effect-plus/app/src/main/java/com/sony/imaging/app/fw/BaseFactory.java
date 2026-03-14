package com.sony.imaging.app.fw;

import android.util.Log;
import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class BaseFactory {
    private static final String TAG = "BaseFactory";
    private HashMap<String, Class<?>> map = new HashMap<>(16);

    public abstract void init();

    public Class<?> getClass(String name) {
        return this.map.get(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void add(String name, Class<?> c) {
        this.map.put(name, c);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object get(String name) {
        Class<?> c = this.map.get(name);
        try {
            Object instance = c.newInstance();
            return instance;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "StackTrace", e);
            return null;
        } catch (InstantiationException e2) {
            Log.e(TAG, "StackTrace", e2);
            return null;
        } catch (NullPointerException e3) {
            return null;
        }
    }
}
