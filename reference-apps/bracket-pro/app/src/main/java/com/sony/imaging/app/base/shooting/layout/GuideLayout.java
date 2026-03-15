package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class GuideLayout extends Layout implements IModableLayout {
    private OnLayoutModeChangeListener mListener;
    protected View mMainView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return createView();
    }

    protected int getLayout(int device) {
        if (1 != device) {
            int layoutid = R.layout.shooting_main_guide_panel;
            return layoutid;
        }
        int layoutid2 = R.layout.shooting_main_guide_evf;
        return layoutid2;
    }

    protected ViewGroup createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        ViewGroup view = new RelativeLayout(getActivity());
        int viewId = getLayout(device);
        if (-1 != viewId) {
            this.mMainView = obtainViewFromPool(viewId);
            view.addView(this.mMainView);
        }
        return view;
    }

    protected void detachView() {
        if (this.mMainView != null) {
            if (getView() != null) {
                ((ViewGroup) getView()).removeView(this.mMainView);
            }
            this.mMainView = null;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        detachView();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mListener == null) {
            this.mListener = new OnLayoutModeChangeListener(this, 0);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        this.mListener = null;
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }
}
