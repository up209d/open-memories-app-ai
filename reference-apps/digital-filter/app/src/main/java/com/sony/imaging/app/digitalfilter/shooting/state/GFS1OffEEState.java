package com.sony.imaging.app.digitalfilter.shooting.state;

import android.os.Bundle;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.menu.layout.GFGuideLayout;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.ChangeAperture;
import com.sony.imaging.app.digitalfilter.shooting.ChangeSs;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class GFS1OffEEState extends S1OffEEState implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    public static boolean userSettingAperture = false;
    public static boolean userSettingShutterSpeed = false;
    public static boolean enableApertureSetting = false;
    private static String mCurrentDisplay = null;
    public static boolean isApertureZero = false;
    private static boolean isEnableFlash = false;
    private DisplayManager mDisplayManager = null;
    private OnDisplayEventListener mDispEventListener = null;

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isEnableFlash = GFCommonUtil.getInstance().isEnableFlash();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        TouchLessShutterController.ExposingByTouchLessShutter = false;
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        mCurrentDisplay = this.mDisplayManager.getActiveDevice();
        userSettingAperture = false;
        userSettingShutterSpeed = false;
        enableApertureSetting = GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE);
        enableApertureSetting |= GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Aperture");
        isApertureZero = false;
        GFCommonUtil.getInstance().updateAppName();
        GFCommonUtil.getInstance().setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
        super.onResume();
        boolean isShownIntroGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
        boolean isShownStep1Guide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_STEP1GUIDE, false);
        if (!isShownIntroGuide) {
            openLayout("ID_GFINTRODUCTIONLAYOUT");
        } else if (!isShownStep1Guide) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "STEP1");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
        if (!GFCommonUtil.getInstance().isThemeSelected()) {
            GFCommonUtil.getInstance().setEECameraSettings();
        }
        GFCommonUtil.getInstance().setThemeSelectedFlag(false);
        GFCommonUtil.getInstance().setInvalidShutter(false);
        if (SFRSA.getInstance().getCameraSequence() != null) {
            startLiveveiwEffect();
            int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
            if (!GFHistgramTask.getInstance().isHistgramUpdatingStarted() && 5 == displayMode) {
                GFHistgramTask.getInstance().startHistgramUpdating();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.finish();
        this.mDispEventListener = null;
        this.mDisplayManager = null;
        mCurrentDisplay = null;
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_DF_LAUNCHER_NAME));
        closeLayout(GFGuideLayout.ID_GFGUIDELAYOUT);
        closeLayout("ID_GFINTRODUCTIONLAYOUT");
        saveParameters();
        super.onPause();
    }

    public static void saveParameters() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        if (userSettingAperture) {
            int aperture = ChangeAperture.getApertureFromPF();
            if (aperture > 0) {
                if (GFEEAreaController.getInstance().isLand()) {
                    params.setAperture(0, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value = params.getAperture(1);
                        params.setAperture(1, value);
                        int value2 = params.getAperture(2);
                        params.setAperture(2, value2);
                    }
                } else if (GFEEAreaController.getInstance().isSky()) {
                    params.setAperture(1, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value3 = params.getAperture(0);
                        params.setAperture(0, value3);
                        int value4 = params.getAperture(2);
                        params.setAperture(2, value4);
                    }
                } else if (GFEEAreaController.getInstance().isLayer3()) {
                    params.setAperture(2, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value5 = params.getAperture(0);
                        params.setAperture(0, value5);
                        int value6 = params.getAperture(1);
                        params.setAperture(1, value6);
                    }
                }
            }
            AppLog.info(TAG, "Aperture: " + aperture);
        }
        if (userSettingShutterSpeed) {
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            if (GFEEAreaController.getInstance().isLand()) {
                params.setSSNumerator(0, ((Integer) ss.first).intValue());
                params.setSSDenominator(0, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator = params.getSSNumerator(1);
                    int denominator = params.getSSDenominator(1);
                    params.setSSNumerator(1, numerator);
                    params.setSSDenominator(1, denominator);
                    int numerator2 = params.getSSNumerator(2);
                    int denominator2 = params.getSSDenominator(2);
                    params.setSSNumerator(2, numerator2);
                    params.setSSDenominator(2, denominator2);
                }
            } else if (GFEEAreaController.getInstance().isSky()) {
                params.setSSNumerator(1, ((Integer) ss.first).intValue());
                params.setSSDenominator(1, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator3 = params.getSSNumerator(0);
                    int denominator3 = params.getSSDenominator(0);
                    params.setSSNumerator(0, numerator3);
                    params.setSSDenominator(0, denominator3);
                    int numerator4 = params.getSSNumerator(2);
                    int denominator4 = params.getSSDenominator(2);
                    params.setSSNumerator(2, numerator4);
                    params.setSSDenominator(2, denominator4);
                }
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                params.setSSNumerator(2, ((Integer) ss.first).intValue());
                params.setSSDenominator(2, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator5 = params.getSSNumerator(0);
                    int denominator5 = params.getSSDenominator(0);
                    params.setSSNumerator(0, numerator5);
                    params.setSSDenominator(0, denominator5);
                    int numerator6 = params.getSSNumerator(1);
                    int denominator6 = params.getSSDenominator(1);
                    params.setSSNumerator(1, numerator6);
                    params.setSSDenominator(1, denominator6);
                }
            }
            AppLog.info(TAG, "SSNumerator: " + ss.first);
            AppLog.info(TAG, "SSDenominator: " + ss.second);
        }
        userSettingAperture = false;
        userSettingShutterSpeed = false;
    }

    public static void saveApertureParameters() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        if (userSettingAperture) {
            int aperture = ChangeAperture.getApertureFromPF();
            if (aperture > 0) {
                if (GFEEAreaController.getInstance().isLand()) {
                    params.setAperture(0, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value = params.getAperture(1);
                        params.setAperture(1, value);
                        int value2 = params.getAperture(2);
                        params.setAperture(2, value2);
                    }
                } else if (GFEEAreaController.getInstance().isSky()) {
                    params.setAperture(1, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value3 = params.getAperture(0);
                        params.setAperture(0, value3);
                        int value4 = params.getAperture(2);
                        params.setAperture(2, value4);
                    }
                } else if (GFEEAreaController.getInstance().isLayer3()) {
                    params.setAperture(2, aperture);
                    if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_APERTURE)) {
                        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
                        int value5 = params.getAperture(0);
                        params.setAperture(0, value5);
                        int value6 = params.getAperture(1);
                        params.setAperture(1, value6);
                    }
                }
            }
            AppLog.info(TAG, "Aperture: " + aperture);
        }
    }

    public static void saveSSParameters() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        if (userSettingShutterSpeed) {
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            if (GFEEAreaController.getInstance().isLand()) {
                params.setSSNumerator(0, ((Integer) ss.first).intValue());
                params.setSSDenominator(0, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator = params.getSSNumerator(1);
                    int denominator = params.getSSDenominator(1);
                    params.setSSNumerator(1, numerator);
                    params.setSSDenominator(1, denominator);
                    int numerator2 = params.getSSNumerator(2);
                    int denominator2 = params.getSSDenominator(2);
                    params.setSSNumerator(2, numerator2);
                    params.setSSDenominator(2, denominator2);
                }
            } else if (GFEEAreaController.getInstance().isSky()) {
                params.setSSNumerator(1, ((Integer) ss.first).intValue());
                params.setSSDenominator(1, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator3 = params.getSSNumerator(0);
                    int denominator3 = params.getSSDenominator(0);
                    params.setSSNumerator(0, numerator3);
                    params.setSSDenominator(0, denominator3);
                    int numerator4 = params.getSSNumerator(2);
                    int denominator4 = params.getSSDenominator(2);
                    params.setSSNumerator(2, numerator4);
                    params.setSSDenominator(2, denominator4);
                }
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                params.setSSNumerator(2, ((Integer) ss.first).intValue());
                params.setSSDenominator(2, ((Integer) ss.second).intValue());
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_SS)) {
                    GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
                    GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
                    int numerator5 = params.getSSNumerator(0);
                    int denominator5 = params.getSSDenominator(0);
                    params.setSSNumerator(0, numerator5);
                    params.setSSDenominator(0, denominator5);
                    int numerator6 = params.getSSNumerator(1);
                    int denominator6 = params.getSSDenominator(1);
                    params.setSSNumerator(1, numerator6);
                    params.setSSDenominator(1, denominator6);
                }
            }
            AppLog.info(TAG, "SSNumerator: " + ss.first);
            AppLog.info(TAG, "SSDenominator: " + ss.second);
        }
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(SFRSA.CMD_LAND_HIST);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{GFConstants.UPDATED_BORDER, GFConstants.RESTART_COMPOSIT_PROCESS, GFConstants.SHOW_STEP1_GUIDE, CameraNotificationManager.DEVICE_LENS_CHANGED, "Aperture", CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.SHUTTER_SPEED, CameraNotificationManager.ZOOM_INFO_CHANGED};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.info(TAG, "onNotify: " + tag);
        if (GFConstants.UPDATED_BORDER.equals(tag)) {
            SFRSA.getInstance().updateLiveViewEffect();
            return;
        }
        if (GFConstants.RESTART_COMPOSIT_PROCESS.equalsIgnoreCase(tag)) {
            GFHistgramTask.getInstance().stopHistgramUpdating();
            SFRSA.getInstance().terminate();
            startLiveveiwEffect();
            GFHistgramTask.getInstance().startHistgramUpdating();
            int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
            if (displayMode != 5) {
                GFHistgramTask.getInstance().stopHistgramUpdating();
                return;
            }
            return;
        }
        if (GFConstants.SHOW_STEP1_GUIDE.equalsIgnoreCase(tag)) {
            boolean isShownStep1Guide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_STEP1GUIDE, false);
            if (!isShownStep1Guide) {
                Bundle bundle = new Bundle();
                bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "STEP1");
                openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
                return;
            }
            return;
        }
        if (CameraNotificationManager.DEVICE_LENS_CHANGED.equalsIgnoreCase(tag)) {
            if (!GFCommonUtil.getInstance().hasIrisRing()) {
                if (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual()) {
                    isApertureZero = false;
                    if (GFCommonUtil.getInstance().isFixedAperture()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (GFCommonUtil.getInstance().isFixedAperture()) {
                        userSettingAperture = false;
                        return;
                    }
                    if (!userSettingAperture) {
                        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                        if (info != null) {
                            if (ChangeAperture.getApertureFromPF() != 0) {
                                if (GFEEAreaController.getInstance().isLand()) {
                                    GFCommonUtil.getInstance().setAperture(0, true);
                                    return;
                                } else if (GFEEAreaController.getInstance().isSky()) {
                                    GFCommonUtil.getInstance().setAperture(1, true);
                                    return;
                                } else {
                                    if (GFEEAreaController.getInstance().isLayer3()) {
                                        GFCommonUtil.getInstance().setAperture(2, true);
                                        return;
                                    }
                                    return;
                                }
                            }
                            isApertureZero = true;
                            return;
                        }
                        isApertureZero = true;
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (tag.equals("Aperture")) {
            saveApertureParameters();
            if (isApertureZero && ChangeAperture.getApertureFromPF() != 0 && !GFCommonUtil.getInstance().isFixedAperture()) {
                isApertureZero = false;
                if (GFEEAreaController.getInstance().isLand()) {
                    GFCommonUtil.getInstance().setAperture(0, true);
                    return;
                } else if (GFEEAreaController.getInstance().isSky()) {
                    GFCommonUtil.getInstance().setAperture(1, true);
                    return;
                } else {
                    if (GFEEAreaController.getInstance().isLayer3()) {
                        GFCommonUtil.getInstance().setAperture(2, true);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (tag.equals(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED) || tag.equals(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF) || tag.equals(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equals(CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF)) {
            if (isEnableFlash != GFCommonUtil.getInstance().isEnableFlash()) {
                userSettingShutterSpeed = false;
            }
            isEnableFlash = GFCommonUtil.getInstance().isEnableFlash();
            return;
        }
        if (tag.equals(CameraNotificationManager.SHUTTER_SPEED)) {
            saveSSParameters();
            return;
        }
        if (tag.equals(CameraNotificationManager.ZOOM_INFO_CHANGED)) {
            userSettingAperture = false;
            if (!GFCommonUtil.getInstance().isFixedAperture()) {
                if (GFEEAreaController.getInstance().isLand()) {
                    GFCommonUtil.getInstance().setAperture(0, true);
                } else if (GFEEAreaController.getInstance().isSky()) {
                    GFCommonUtil.getInstance().setAperture(1, true);
                } else if (GFEEAreaController.getInstance().isLayer3()) {
                    GFCommonUtil.getInstance().setAperture(2, true);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        private static final String HALF_SHUTTER_STATE = "S1OnEE";

        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            if (TouchLessShutterController.getInstance().hasTouchLessAddOn() && !"Off".equals(TouchLessShutterController.getInstance().getValue(null)) && 4096 == eventId) {
                String str = GFS1OffEEState.this.mDisplayManager.getActiveDevice();
                if (str == null) {
                    AppLog.warning(GFS1OffEEState.TAG, "getActiveDevice() returns null.");
                    return;
                }
                if (str.equals("DEVICE_ID_FINDER") && GFS1OffEEState.mCurrentDisplay.equals("DEVICE_ID_PANEL") && CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                    pushedS1Key();
                    GFCommonUtil.getInstance().pinCalendar();
                    if (FocusModeController.MANUAL.equalsIgnoreCase(GFFocusModeController.getInstance().getValue()) && GFSelfTimerController.SELF_TIMER_OFF.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
                        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
                    } else {
                        GFS1OffEEState.this.setNextState(HALF_SHUTTER_STATE, null);
                    }
                    TouchLessShutterController.ExposingByTouchLessShutter = true;
                }
                String unused = GFS1OffEEState.mCurrentDisplay = GFS1OffEEState.this.mDisplayManager.getActiveDevice();
            }
        }

        public void pushedS1Key() {
            ExecutorCreator executorCreator = ExecutorCreator.getInstance();
            if (!executorCreator.isAElockedOnAutoFocus()) {
                String behavior = null;
                String focusMode = GFFocusModeController.getInstance().getValue();
                if ("af-s".equals(focusMode)) {
                    behavior = "af_woaf";
                } else if ("af-c".equals(focusMode)) {
                    behavior = "afc_woaf";
                }
                executorCreator.getSequence().autoFocus(null, behavior);
                return;
            }
            executorCreator.getSequence().autoFocus(null);
        }
    }
}
