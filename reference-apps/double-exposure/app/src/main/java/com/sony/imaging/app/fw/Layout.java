package com.sony.imaging.app.fw;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/* loaded from: classes.dex */
public class Layout extends KeyReceiver {
    private static final String TAG = "Layout";
    private static SparseArray<SoftReference<View>> mViewPool;
    public Bundle data;
    private LayoutInflater mInflater;
    private static HashMap<Class<?>, Layout> map = new HashMap<>(128);
    public static final Drawable BACKGROUND_BLACK = new BlackDrawable();
    public static final Drawable BACKGROUND_FOLLOW = null;
    public static final Drawable BACKGROUND_TRANSPARENT = new TransparentDrawable();

    public Layout() {
        if (mViewPool == null) {
            mViewPool = new SparseArray<>();
        }
    }

    public static void clear() {
        if (mViewPool != null) {
            mViewPool.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int dispatchKeyDown(int keyCode, KeyEvent event) {
        if (getView().dispatchKeyEvent(event)) {
            ((AppRoot) getActivity()).setCurrentState(this);
            return 1;
        }
        int ret = super.dispatchKeyDown(keyCode, event);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int dispatchKeyUp(int keyCode, KeyEvent event) {
        if (getView().dispatchKeyEvent(event)) {
            return 1;
        }
        int ret = super.dispatchKeyUp(keyCode, event);
        return ret;
    }

    public static final Layout getInstance(Class<?> c) {
        Layout instance = map.get(c);
        if (instance == null) {
            try {
                instance = (Layout) c.newInstance();
                map.put(c, instance);
                return instance;
            } catch (IllegalAccessException e) {
                Log.e(TAG, "StackTrace", e);
                return instance;
            } catch (InstantiationException e2) {
                return instance;
            }
        }
        return instance;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mInflater = inflater;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup view = (ViewGroup) getView();
        if (view != null) {
            view.removeAllViews();
        }
    }

    public void onReopened() {
    }

    public Drawable onGetBackground() {
        return BACKGROUND_FOLLOW;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setBackgroundDrawable() {
        Drawable d = onGetBackground();
        if (BACKGROUND_FOLLOW == d) {
            return false;
        }
        getActivity().getWindow().setBackgroundDrawable(d);
        return true;
    }

    public Object getData(String name) {
        AppRoot root = (AppRoot) getActivity();
        return root.getData(name);
    }

    public void closeLayout() {
        closeLayout(this);
    }

    public void updateLayout() {
        updateLayout(this, 1);
    }

    public void updateLayout(int type) {
        updateLayout(this, type);
    }

    public void updateView() {
        updateView(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View obtainViewFromPool(int resId) {
        SoftReference<View> s = mViewPool.get(resId);
        View v = null;
        if (s != null) {
            View v2 = s.get();
            v = v2;
        }
        if (v == null || v.getParent() != null) {
            View v3 = this.mInflater.inflate(resId, (ViewGroup) null);
            mViewPool.put(resId, new SoftReference<>(v3));
            return v3;
        }
        return v;
    }

    /* loaded from: classes.dex */
    static class TransparentDrawable extends Drawable {
        TransparentDrawable() {
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }

        @Override // android.graphics.drawable.Drawable
        public Region getTransparentRegion() {
            return null;
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    /* loaded from: classes.dex */
    static class BlackDrawable extends Drawable {
        BlackDrawable() {
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawColor(-16777216, PorterDuff.Mode.SRC);
        }

        @Override // android.graphics.drawable.Drawable
        public Region getTransparentRegion() {
            return null;
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -1;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }
}
