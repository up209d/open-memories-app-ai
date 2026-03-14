package com.sony.imaging.app.portraitbeauty.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess;
import com.sony.imaging.app.portraitbeauty.shooting.widget.AdjustModeMessageWidget;
import com.sony.imaging.app.portraitbeauty.shooting.widget.PortraitBeautyAppIcon;
import com.sony.imaging.app.portraitbeauty.shooting.widget.PortraitBeautyModeIconView;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class PortraitBeautyStableLayout extends StableLayout {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private static final String TAG = "PortraitBeautyStableLayout";
    private AdjustModeMessageWidget mAdjustModeMessageWidget = null;
    private ImageView mGuideImageView = null;
    private TextView mID4_Adjust_Mode_screen_title = null;
    private Handler mHandler = null;
    private ViewGroup mEycsView = null;
    private ViewGroup mPjoneView = null;
    private AppIconView mAppIcon = null;
    private Boolean showGuideOnFnPress = true;
    private ExposureModeIconView mExpoModeIcon = null;

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.portrait_beauty_shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.portrait_beauty_shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mAdjustModeMessageWidget = (AdjustModeMessageWidget) this.mCurrentView.findViewById(R.id.adjust_mode_message_view);
        this.mGuideImageView = (ImageView) this.mCurrentView.findViewById(R.id.guide_background);
        this.mID4_Adjust_Mode_screen_title = (TextView) this.mCurrentView.findViewById(R.id.ID4_Adjust_Mode_screen_title);
        this.mEycsView = (ViewGroup) this.mCurrentView.findViewById(R.id.live_view_eycs);
        this.mPjoneView = (ViewGroup) this.mCurrentView.findViewById(R.id.live_view_pjone);
        this.mAppIcon = (PortraitBeautyAppIcon) this.mCurrentView.findViewById(R.id.app_icon);
        if (this.mAppIcon != null && PortraitBeautyUtil.bIsAdjustModeGuide) {
            this.mAppIcon.setVisibility(8);
        }
        this.mExpoModeIcon = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        this.showGuideOnFnPress = false;
        return super.pushedFnKey();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        updateExposureComp();
        super.onResume();
        if (!PortraitBeautyUtil.bIsAdjustModeGuide) {
            showNormalShootingScreen();
        } else {
            showAdjustVisuallyScreen();
        }
        if (!this.showGuideOnFnPress.booleanValue()) {
            if (this.mGuideImageView != null) {
                this.mGuideImageView.setVisibility(4);
            }
            if (this.mAdjustModeMessageWidget != null) {
                this.mAdjustModeMessageWidget.setVisibility(4);
            }
            this.showGuideOnFnPress = true;
        }
    }

    private void showAdjustVisuallyScreen() {
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SELFIE_AL_ADJUST_NAME));
        AppNameView.show(false);
        if (this.mAdjustModeMessageWidget != null) {
            this.mAdjustModeMessageWidget.setVisibility(0);
        }
        if (this.mGuideImageView != null) {
            this.mGuideImageView.setVisibility(0);
        }
        if (this.mAppIcon != null) {
            this.mAppIcon.setVisibility(8);
        }
        if (this.mExpoModeIcon != null) {
            this.mExpoModeIcon.setVisibility(4);
        }
        if (this.mEycsView != null) {
            this.mEycsView.setVisibility(8);
        }
        if (this.mPjoneView != null) {
            this.mPjoneView.setVisibility(8);
        }
        if (this.mID4_Adjust_Mode_screen_title != null) {
            this.mID4_Adjust_Mode_screen_title.setVisibility(0);
        }
    }

    private void showNormalShootingScreen() {
        if (this.mAdjustModeMessageWidget != null) {
            this.mAdjustModeMessageWidget.setVisibility(4);
        }
        if (this.mGuideImageView != null) {
            this.mGuideImageView.setVisibility(4);
        }
        if (this.mAppIcon != null) {
            this.mAppIcon.setVisibility(0);
        }
        if (this.mExpoModeIcon != null) {
            this.mExpoModeIcon.setVisibility(0);
        }
        if (this.mID4_Adjust_Mode_screen_title != null) {
            this.mID4_Adjust_Mode_screen_title.setVisibility(4);
        }
        int displayMode = PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (displayMode == 1) {
            if (isPFverOver2()) {
                if (this.mEycsView != null && this.mPjoneView != null) {
                    this.mPjoneView.setVisibility(0);
                    this.mEycsView.setVisibility(8);
                    return;
                }
                return;
            }
            if (this.mPjoneView != null && this.mEycsView != null) {
                this.mPjoneView.setVisibility(8);
                this.mEycsView.setVisibility(0);
                return;
            }
            return;
        }
        if (this.mEycsView != null && this.mPjoneView != null) {
            this.mEycsView.setVisibility(8);
            this.mPjoneView.setVisibility(8);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int layoutId = -1;
        if (device == 0) {
            layoutId = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
        } else if (device == 1) {
            layoutId = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
        } else if (device == 2) {
            layoutId = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        }
        AppLog.info(TAG, "Layout Id: " + layoutId);
        AppLog.exit(TAG, AppLog.getMethodName());
        return layoutId;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
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
        PortraitBeautyModeIconView icon1 = (PortraitBeautyModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView icon2 = (PortraitBeautyAppIcon) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            icon1.setAutoDisappear(false);
            if (1 == displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            icon1.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            icon1.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean handle = false;
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            handle = handleVisibilityOfAdjustVisuallyGuide(scanCode);
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean handleVisibilityOfAdjustVisuallyGuide(int scanCode) {
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (this.mGuideImageView != null && this.mGuideImageView.isShown()) {
                    this.mGuideImageView.setVisibility(4);
                    this.mAdjustModeMessageWidget.setVisibility(4);
                }
                return false;
            case 232:
                if (this.mGuideImageView == null || !this.mGuideImageView.isShown()) {
                    return false;
                }
                this.mGuideImageView.setVisibility(4);
                this.mAdjustModeMessageWidget.setVisibility(4);
                return true;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                if (this.mGuideImageView == null || !this.mGuideImageView.isShown()) {
                    return false;
                }
                this.mGuideImageView.setVisibility(4);
                this.mAdjustModeMessageWidget.setVisibility(4);
                return false;
            default:
                return false;
        }
    }

    public boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (!PortraitBeautyUtil.bIsAdjustModeGuide && PortraitBeautyEffectProcess.sIsCaptureStarted != PortraitBeautyEffectProcess.CAPTURE_STARTED && PortraitBeautyEffectProcess.sIsCaptureStarted != PortraitBeautyEffectProcess.CAPTURE_DONE) {
            int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (device == 0) {
                int currentDisplayMode = PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0);
                String strCurrentDisplayMode = "" + currentDisplayMode;
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, strCurrentDisplayMode);
            }
            if (device == 1) {
                int currentDisplayMode2 = PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0);
                String strCurrentDisplayMode2 = "" + currentDisplayMode2;
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, strCurrentDisplayMode2);
            }
            if (device == 2) {
                int currentDisplayMode3 = PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0);
                String strCurrentDisplayMode3 = "" + currentDisplayMode3;
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, strCurrentDisplayMode3);
            }
        }
        super.onPause();
    }

    private void updateExposureComp() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PortraitBeautyConstants.sISEVDIALTURN) {
            PortraitBeautyConstants.sISEVDIALTURN = false;
            PortraitBeautyConstants.slastStateExposureCompansasion = EVDialDetector.getEVDialPosition();
            ExposureCompensationController.getInstance().setEvDialValue(PortraitBeautyConstants.slastStateExposureCompansasion);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mAdjustModeMessageWidget = null;
        this.mGuideImageView = null;
        this.mID4_Adjust_Mode_screen_title = null;
        this.mHandler = null;
        this.mEycsView = null;
        this.mPjoneView = null;
        this.mAppIcon = null;
        this.mExpoModeIcon = null;
        this.mCurrentView = null;
        super.onDestroyView();
    }
}
