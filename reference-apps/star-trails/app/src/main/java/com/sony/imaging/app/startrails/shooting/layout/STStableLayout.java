package com.sony.imaging.app.startrails.shooting.layout;

import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.startrails.shooting.STExecutorCreator;
import com.sony.imaging.app.startrails.shooting.widget.STExposureModeIconView;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.startrails.util.ThemeRecomandedMenuSetting;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STStableLayout extends StableLayout implements NotificationListener {
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_HDMI;
    private static final String TAG = "STStableLayout";
    private static String[] TAGS;
    private STExposureModeIconView icon1;
    private ShutterSpeedChangeListener mShutterSpeedListener;
    public static boolean is2SecondTimerFinish = false;
    public static boolean isCapturing = false;
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private TextView numberofShot = null;
    private int displayMode = 0;
    private int device = 0;

    static {
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(11, Integer.valueOf(R.layout.star_trails_shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(12, Integer.valueOf(R.layout.star_trails_mute_shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.shooting_main_sid_basic_info_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = STARTRAILS_LAYOUT_LIST_FOR_PANEL;
        STARTRAILS_LAYOUT_LIST_FOR_HDMI = STARTRAILS_LAYOUT_LIST_FOR_PANEL;
        TAGS = new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, CameraNotificationManager.FOCAL_LENGTH_CHANGED, "Aperture"};
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(0);
        updateExposureCompensationValue();
        updateShutterSpeed();
        super.onResume();
        setAntiBlurOff();
        STDisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this.mShutterSpeedListener);
    }

    private void setAntiBlurOff() {
        if (!"off".equals(AntiHandBlurController.getInstance().getValue(AntiHandBlurController.STILL))) {
            AntiHandBlurController.getInstance().setValue(AntiHandBlurController.STILL, "off");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShutterSpeed() {
        if (STUtility.getInstance().getCurrentTrail() == 0) {
            STUtility.getInstance().setRecommanedSS(STConstants.DEFAULT_VALUE_SHUTTER_SPEED);
        }
    }

    private void updateExposureCompensationValue() {
        if (true == STUtility.getInstance().isEVDialRotated() && EVDialDetector.hasEVDial() && EVDialDetector.getEVDialPosition() != 100000) {
            STExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            STUtility.getInstance().updateBackValue();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setUserChanging(int whatUserChanging) {
        super.setUserChanging(whatUserChanging);
        if (2 == whatUserChanging) {
            int expoIndex = ExposureCompensationController.getInstance().getExposureCompensationIndex();
            STExposureCompensationController.getInstance().setEvDialValue(expoIndex);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        STDisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (this.mShutterSpeedListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mShutterSpeedListener);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        this.device = DisplayModeObserver.getInstance().getActiveDevice();
        this.displayMode = STDisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (this.device == 0) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(this.device, this.displayMode);
            touchAreaId = R.id.touchArea;
        } else if (this.device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(this.device, this.displayMode);
        } else if (this.device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(this.device, this.displayMode);
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
        this.icon1 = (STExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (this.device == 1) {
            this.icon1.setAutoDisappear(false);
            if (1 == this.displayMode || 11 == this.displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == this.displayMode || 11 == this.displayMode) {
            this.icon1.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            this.icon1.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                TouchArea.OnTouchAreaListener touchListener = getOnTouchListener();
                this.mTouchArea.setTouchAreaListener(touchListener);
                if (touchListener instanceof TouchArea.OnTouchAreaDoubleTapListener) {
                    this.mTouchArea.setTouchAreaDoubleTapListener((TouchArea.OnTouchAreaDoubleTapListener) touchListener);
                }
            }
        }
        handleStarTrailMode();
    }

    private void handleStarTrailMode() {
        this.mCurrentMainView = obtainViewFromPool(this.mCurrentMainViewId);
        this.displayMode = STDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (this.mCurrentMainView != null && this.displayMode == 11) {
            TextView themeName = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_theme_name);
            themeName.setText(getResources().getString(STUtility.getInstance().getThemeNameResID(STUtility.getInstance().getCurrentTrail())));
            ImageView shootMode = (ImageView) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_icon);
            updateRecModeIcon(shootMode);
            TextView streakLevel = (TextView) this.mCurrentMainView.findViewById(R.id.streakLevel);
            int streakValue = ThemeParameterSettingUtility.getInstance().getStreakLevel();
            CharSequence streakLevelValue = STUtility.getInstance().getStreakValue(streakValue);
            streakLevel.setText(streakLevelValue);
            this.numberofShot = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num);
            this.numberofShot.setText("" + ThemeParameterSettingUtility.getInstance().getNumberOfShot());
        }
        updateInfoModeStarTrailsIcon();
    }

    private void updateInfoModeStarTrailsIcon() {
        if (this.displayMode == 1) {
            this.numberofShot = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num_info_on);
            this.numberofShot.setVisibility(0);
            if (this.numberofShot != null) {
                this.numberofShot.setText("" + ThemeParameterSettingUtility.getInstance().getNumberOfShot());
            }
        }
    }

    private void updateRecModeIcon(ImageView mRecModeImgView) {
        if (ThemeParameterSettingUtility.getInstance().getRecordingMode() == 0) {
            mRecModeImgView.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else {
            mRecModeImgView.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = STARTRAILS_LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = STARTRAILS_LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = STARTRAILS_LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        AppLog.info(TAG, "onLayoutModeChanged()");
        this.device = device;
        this.displayMode = displayMode;
        updateView();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(DisplayModeObserver.TAG_DISPMODE_CHANGE) || tag.equals(DisplayModeObserver.TAG_DEVICE_CHANGE)) {
            STDisplayModeObserver mDisplayModeObserver = STDisplayModeObserver.getInstance();
            this.device = mDisplayModeObserver.getActiveDevice();
            this.displayMode = mDisplayModeObserver.getActiveDispMode(0);
            onLayoutModeChanged(this.device, this.displayMode);
            return;
        }
        if ((tag.equals(CameraNotificationManager.FOCAL_LENGTH_CHANGED) || tag.equals("Aperture")) && STUtility.getInstance().getCurrentTrail() == 1) {
            ThemeRecomandedMenuSetting.getInstance().updateDarkNightAperture();
        } else if (STUtility.getInstance().getCurrentTrail() == 2 && tag.equals("Aperture")) {
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_APERTURE_KEY, STUtility.getInstance().getApertureValues(CameraSetting.getInstance().getApertureInfo()));
        }
    }

    /* loaded from: classes.dex */
    class ShutterSpeedChangeListener implements NotificationListener {
        private String[] TAGS = {CameraNotificationManager.SHUTTER_SPEED};

        ShutterSpeedChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            STStableLayout.this.updateShutterSpeed();
        }
    }

    public STStableLayout() {
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
    }
}
