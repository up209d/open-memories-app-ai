package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.widget.LGExposureModeIconView;
import com.sony.imaging.app.lightgraffiti.util.AppLog;

/* loaded from: classes.dex */
public class LGStableLayout extends StableLayout {
    protected static final SparseArray<Integer> LG_LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> LG_LAYOUT_LIST_FOR_HDMI;
    private static final String TAG = LGStableLayout.class.getSimpleName();
    protected static final SparseArray<Integer> LG_LAYOUT_LIST_FOR_PANEL = new SparseArray<>();

    static {
        LG_LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_info_on_panel));
        LG_LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_info_off_panel));
        LG_LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_histogram_panel));
        LG_LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_digitallevel_panel));
        LG_LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LG_LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_basic_info_evf));
        LG_LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_info_off_evf));
        LG_LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_histogram_evf));
        LG_LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.lightgraffiti_shooting_main_sid_digitallevel_evf));
        LG_LAYOUT_LIST_FOR_HDMI = LG_LAYOUT_LIST_FOR_PANEL;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        Log.d(TAG, AppLog.getMethodName());
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0) {
            baseViewId = R.layout.lightgraffiti_shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.lightgraffiti_shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.lightgraffiti_shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.lightgraffiti_shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.lightgraffiti_shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.lightgraffiti_shooting_base_sid_basic_footer_panel;
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
        LGExposureModeIconView exposureModeIcon1 = (LGExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView exposureModeIcon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            exposureModeIcon1.setAutoDisappear(false);
            if (1 == displayMode) {
                exposureModeIcon2.setAutoDisappear(false);
            } else {
                exposureModeIcon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            exposureModeIcon1.setAutoDisappear(false);
            exposureModeIcon2.setAutoDisappear(false);
        } else {
            exposureModeIcon1.setAutoDisappear(true);
            exposureModeIcon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = LG_LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = LG_LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = LG_LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }
}
