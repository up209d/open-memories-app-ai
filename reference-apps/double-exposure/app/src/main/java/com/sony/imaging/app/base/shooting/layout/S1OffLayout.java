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
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class S1OffLayout extends Layout implements IModableLayout {
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 0);

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentLayout;
    }

    protected int getLayout(int device, int dispmode) {
        if (1 == device || 1 != Environment.getVersionOfHW()) {
            return -1;
        }
        if (1 == dispmode) {
            int layoutid = R.layout.shooting_main_skguide_dispon_for_eview;
            return layoutid;
        }
        int layoutid2 = R.layout.shooting_main_skguide_dispoff_for_eview;
        return layoutid2;
    }

    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        detachView();
        this.mCurrentLayout = new RelativeLayout(getActivity());
        int viewId = getLayout(device, displayMode);
        if (-1 != viewId) {
            this.mMainView = obtainViewFromPool(viewId);
            this.mCurrentLayout.addView(this.mMainView);
        }
    }

    protected void detachView() {
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        detachView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }
}
