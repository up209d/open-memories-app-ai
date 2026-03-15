package com.sony.imaging.app.base.shooting.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class ShootingLayout extends Layout {
    private static final String TAG = "ShootingLayout";
    private SurfaceHolder mHolder = null;
    private int mHolderType = -1;
    private HolderListener mListener = null;

    /* loaded from: classes.dex */
    public interface HolderListener {
        public static final int STATUS_READY = 1;
        public static final int STATUS_UNREADY = -1;

        void onStateChanged(int i, SurfaceHolder surfaceHolder);
    }

    public void setHolderListener(HolderListener listener) {
        this.mListener = listener;
        if (this.mHolder != null) {
            this.mListener.onStateChanged(1, this.mHolder);
        }
    }

    public void setHolderType(int type) {
        this.mHolderType = type;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.shooting_main_ee);
        SurfaceView surface = (SurfaceView) currentView.findViewById(R.id.surface);
        if (surface != null) {
            this.mHolder = surface.getHolder();
            if (-1 != this.mHolderType) {
                this.mHolder.setType(this.mHolderType);
            }
        } else {
            Log.e(TAG, "SurfaceView not found.");
        }
        return currentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mListener != null) {
            this.mListener.onStateChanged(1, this.mHolder);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            this.mListener.onStateChanged(-1, this.mHolder);
        }
        this.mHolder = null;
        super.onPause();
    }

    public SurfaceHolder getHolder() {
        return this.mHolder;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_TRANSPARENT;
    }
}
