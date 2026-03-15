package com.sony.imaging.app.fw;

import android.util.Log;

/* loaded from: classes.dex */
public final class ConstituentRecord {
    private static final String TAG = "ConstituentRecord";
    private boolean initializedKeyHandler;
    private boolean initializedLayout;
    private boolean initializedState;
    private Class<? extends BaseKeyHandler> k;
    private BaseKeyHandler keyHandler;
    private Class<? extends LayoutFactory> l;
    private LayoutFactory layoutFactory;
    private Class<? extends StateFactory> s;
    private StateFactory stateFactory;

    public ConstituentRecord(Class<? extends StateFactory> s, Class<? extends LayoutFactory> l, Class<? extends BaseKeyHandler> k) {
        this.initializedState = false;
        this.initializedLayout = false;
        this.initializedKeyHandler = false;
        this.s = s;
        if (s == null) {
            this.initializedState = true;
            this.stateFactory = null;
        }
        this.l = l;
        if (l == null) {
            this.initializedLayout = true;
            this.layoutFactory = null;
        }
        this.k = k;
        if (k == null) {
            this.initializedKeyHandler = true;
            this.keyHandler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StateFactory getStateFactory() {
        if (!this.initializedState) {
            try {
                this.stateFactory = this.s.newInstance();
                this.stateFactory.init();
            } catch (Exception e) {
                Log.e(TAG, "cannot instantiate: " + this.s.getClass().getSimpleName());
                e.printStackTrace();
            }
            this.initializedState = true;
        }
        return this.stateFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayoutFactory getLayoutFactory() {
        if (!this.initializedLayout) {
            try {
                this.layoutFactory = this.l.newInstance();
                this.layoutFactory.init();
            } catch (Exception e) {
                Log.e(TAG, "cannot instantiate: " + this.l.getClass().getSimpleName());
                e.printStackTrace();
            }
            this.initializedLayout = true;
        }
        return this.layoutFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseKeyHandler getKeyHandler() {
        if (!this.initializedKeyHandler) {
            try {
                this.keyHandler = this.k.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "cannot instantiate: " + this.k.getClass().getSimpleName());
                e.printStackTrace();
            }
            this.initializedKeyHandler = true;
        }
        return this.keyHandler;
    }
}
