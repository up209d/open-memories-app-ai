package com.sony.imaging.app.fw;

import android.util.Log;

/* loaded from: classes.dex */
public abstract class LayoutFactory extends BaseFactory {
    @Override // com.sony.imaging.app.fw.BaseFactory
    public abstract void init();

    @Override // com.sony.imaging.app.fw.BaseFactory
    public Layout get(String name) {
        Class<?> c = getClass(name);
        if (c != null) {
            Layout ret = Layout.getInstance(c);
            return ret;
        }
        Log.w(getClass().getSimpleName(), name + " does not exist.");
        return null;
    }
}
