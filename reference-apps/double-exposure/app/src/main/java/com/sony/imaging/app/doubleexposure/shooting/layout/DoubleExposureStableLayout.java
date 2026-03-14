package com.sony.imaging.app.doubleexposure.shooting.layout;

import android.os.Bundle;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.shooting.wigdet.DoubleExposureModeIconView;

/* loaded from: classes.dex */
public class DoubleExposureStableLayout extends StableLayout {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private final String TAG = AppLog.getClassName();

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.double_exposure_shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.double_exposure_shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
    }

    public DoubleExposureStableLayout() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureUtil doubleExposureUtil = DoubleExposureUtil.getInstance();
        if (doubleExposureUtil.isTurnedEVDial()) {
            doubleExposureUtil.setTurnedEVDial(false);
            doubleExposureUtil.updateExposureCompensation();
        }
        if (doubleExposureUtil.IsSaveSettings()) {
            doubleExposureUtil.setRecommendedValues();
        }
        doubleExposureUtil.setSaveSettings(true);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().saveFocusMode();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int layoutId = -1;
        if (device == 0) {
            layoutId = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
        } else if (device == 1) {
            layoutId = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
        } else if (device == 2) {
            layoutId = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        }
        AppLog.info(this.TAG, "Layout Id: " + layoutId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return layoutId;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0) {
            baseViewId = R.layout.double_exposure_shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.double_exposure_shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.double_exposure_shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        }
        if (-1 != baseViewId && -1 != baseFooterViewId && -1 != mainViewId && (baseViewId != this.mCurrentBaseViewId || baseFooterViewId != this.mCurrentBaseFooterViewId || mainViewId != this.mCurrentMainViewId)) {
            detachView();
            this.mCurrentBaseViewId = baseViewId;
            this.mCurrentMainViewId = mainViewId;
            this.mCurrentBaseFooterViewId = baseFooterViewId;
            this.mCurrentView = obtainViewFromPool(baseViewId);
            this.mCurrentBaseView = (ViewGroup) this.mCurrentView;
            View baseFooterView = obtainViewFromPool(baseFooterViewId);
            this.mCurrentBaseFooterView = baseFooterView;
            this.mCurrentBaseView.addView(baseFooterView);
            if (baseFooterView != null) {
                this.mUserChangableViews[0] = (DigitView) baseFooterView.findViewById(R.id.aperture_view);
                this.mUserChangableViews[1] = (DigitView) baseFooterView.findViewById(R.id.shutterspeed_view);
                this.mUserChangableViews[2] = (DigitView) baseFooterView.findViewById(R.id.exposure_and_meteredmanual_view);
                this.mUserChangableViews[3] = (DigitView) baseFooterView.findViewById(R.id.iso_sensitivity_view);
            }
            Looper.myQueue().addIdleHandler(this.idleHandler);
        }
        DoubleExposureModeIconView doubleExposureModeIcon1 = (DoubleExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView doubleExposureModeIcon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            doubleExposureModeIcon1.setAutoDisappear(false);
            if (1 == displayMode) {
                doubleExposureModeIcon2.setAutoDisappear(false);
            } else {
                doubleExposureModeIcon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            doubleExposureModeIcon1.setAutoDisappear(false);
            doubleExposureModeIcon2.setAutoDisappear(false);
        } else {
            doubleExposureModeIcon1.setAutoDisappear(true);
            doubleExposureModeIcon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateLayout();
    }
}
