package com.sony.imaging.app.fw;

import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class Factory {
    private static HashMap<Class<?>, ConstituentRecord> table = AppRoot.constituentTable;

    protected abstract Class<? extends StateFactory> getTop();

    public Factory() {
        init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void add(Class<?> c, Class<? extends StateFactory> s, Class<? extends LayoutFactory> l, Class<? extends BaseKeyHandler> k) {
        table.put(c, new ConstituentRecord(s, l, k));
    }

    private void init() {
        add(RootContainer.class, getTop(), null, null);
    }
}
