package com.sony.imaging.app.smoothreflection.shooting.layout;

import android.hardware.Camera;
import android.os.Looper;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.menu.layout.ThemeMenuLayout;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.smoothreflection.shooting.widget.SmoothReflectionExposureModeIconView;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothReflectionStableLayout extends StableLayout implements NotificationListener {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static String[] TAGS;
    private final String TAG = AppLog.getClassName();
    public static int sLayoutID = 1;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.smooth_reflection_shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.sr_shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.sr_shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.sr_shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.smooth_reflection_shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.sr_shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.sr_shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
        TAGS = new String[]{"Aperture"};
    }

    public SmoothReflectionStableLayout() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().setNotificationListener(this);
        setAntiBlurOff();
        updateExposureComp();
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (!ThemeController.CUSTOM.equals(selectedTheme)) {
            if (ModeDialDetector.hasModeDial()) {
                SmoothReflectionExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
            } else {
                SmoothReflectionExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
            }
        }
        if (true == ThemeController.MONOTONE.equals(selectedTheme) && true == SmoothReflectionApp.sBootFromAPOPowerOff) {
            SmoothReflectionApp.sBootFromAPOPowerOff = false;
            String monotoneValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, ThemeController.BW);
            if (CameraSetting.getInstance().getCamera() != null) {
                SmoothReflectionUtil.getInstance().setMonotoneSetting(monotoneValue);
                SmoothReflectionUtil.getInstance().applyGammaTable();
            }
        }
        AELController.getInstance().cancelAELock();
        super.onResume();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setAntiHandBlurMode("off");
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
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
        sLayoutID = dispmode;
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
            baseViewId = R.layout.smooth_reflection_shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.smooth_reflection_shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.smooth_reflection_shooting_base_sid_basic_info_panel;
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
        SmoothReflectionExposureModeIconView sFExposureModeIcon1 = (SmoothReflectionExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView doubleExposureModeIcon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            sFExposureModeIcon1.setAutoDisappear(false);
            if (1 == displayMode) {
                doubleExposureModeIcon2.setAutoDisappear(false);
            } else {
                doubleExposureModeIcon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            sFExposureModeIcon1.setAutoDisappear(false);
            doubleExposureModeIcon2.setAutoDisappear(false);
        } else {
            sFExposureModeIcon1.setAutoDisappear(true);
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

    private void updateExposureComp() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionConstants.sISEVDIALTURN) {
            SmoothReflectionConstants.sISEVDIALTURN = false;
            SmoothReflectionConstants.slastStateExposureCompansasion = EVDialDetector.getEVDialPosition();
            ExposureCompensationController.getInstance().setEvDialValue(SmoothReflectionConstants.slastStateExposureCompansasion);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        String currentTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (tag.equals("Aperture") && !ThemeController.CUSTOM.equals(currentTheme)) {
            SmoothReflectionUtil.getInstance().setRecommandedApertureSettings(ThemeMenuLayout.sCurrentSelectedTheme);
        }
    }

    private void setAntiBlurOff() {
        if (!"off".equals(AntiHandBlurController.getInstance().getValue(AntiHandBlurController.STILL))) {
            AntiHandBlurController.getInstance().setValue(AntiHandBlurController.STILL, "off");
        }
    }
}
