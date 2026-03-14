package com.sony.imaging.app.doubleexposure.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class ThemeGuideOnShootingLayout extends Layout {
    public static final String ID_THEMEGUIDEONSHOOTINGLAYOUT = "ID_THEMEGUIDEONSHOOTINGLAYOUT";
    private final String TAG = AppLog.getClassName();
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentLayout;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        updateLayout(2);
        super.onReopened();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroyView();
        detachView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().setGuideClosed();
        closeLayout();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    private void createView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        detachView();
        this.mCurrentLayout = new RelativeLayout(getActivity());
        if (-1 != R.layout.theme_guide_layout) {
            this.mMainView = obtainViewFromPool(R.layout.theme_guide_layout);
            this.mCurrentLayout.addView(this.mMainView);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void detachView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
