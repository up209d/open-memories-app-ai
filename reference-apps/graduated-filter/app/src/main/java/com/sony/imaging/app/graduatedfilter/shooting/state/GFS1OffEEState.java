package com.sony.imaging.app.graduatedfilter.shooting.state;

import android.os.Bundle;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFGuideLayout;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeSs;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class GFS1OffEEState extends S1OffEEState implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    public static boolean userSettingAperture = false;
    public static boolean userSettingShutterSpeed = false;
    public static boolean enableApertureSetting = false;
    private static DisplayManager mDisplayManager = null;
    private static String mCurrentDisplay = null;
    private static OnDisplayEventListener mDispEventListener = null;

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        TouchLessShutterController.ExposingByTouchLessShutter = false;
        mDispEventListener = new OnDisplayEventListener();
        mDisplayManager = new DisplayManager();
        mDisplayManager.setDisplayStatusListener(mDispEventListener);
        mCurrentDisplay = mDisplayManager.getActiveDevice();
        userSettingAperture = false;
        userSettingShutterSpeed = false;
        enableApertureSetting = GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE);
        enableApertureSetting |= GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Aperture");
        GFCommonUtil.getInstance().updateAppName();
        GFCommonUtil.getInstance().stopFilterSetting();
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
        GFCommonUtil.getInstance().setCommonCameraSettings();
        GFCommonUtil.getInstance().setBaseCameraSettings();
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
        mDisplayManager.releaseDisplayStatusListener();
        mDisplayManager.finish();
        mDispEventListener = null;
        mDisplayManager = null;
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SKYND_LAUNCHER_NAME));
        closeLayout(GFGuideLayout.ID_GFGUIDELAYOUT);
        closeLayout("ID_GFINTRODUCTIONLAYOUT");
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (userSettingAperture) {
            int aperture = ChangeAperture.getApertureFromPF();
            if (aperture > 0) {
                params.setAperture(true, aperture);
            }
            AppLog.info(TAG, "Aperture: " + aperture);
        }
        if (userSettingShutterSpeed) {
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            params.setSSNumerator(true, ((Integer) ss.first).intValue());
            params.setSSDenominator(true, ((Integer) ss.second).intValue());
            AppLog.info(TAG, "SSNumerator: " + ss.first);
            AppLog.info(TAG, "SSDenominator: " + ss.second);
        }
        userSettingAperture = false;
        userSettingShutterSpeed = false;
        super.onPause();
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(16);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{GFConstants.RESTART_COMPOSIT_PROCESS, GFConstants.SHOW_STEP1_GUIDE, CameraNotificationManager.DEVICE_LENS_CHANGED};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.info(TAG, "onNotify: " + tag);
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
        if (CameraNotificationManager.DEVICE_LENS_CHANGED.equalsIgnoreCase(tag) && !GFCommonUtil.getInstance().hasIrisRing()) {
            if (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual()) {
                if (GFCommonUtil.getInstance().isFixedAperture()) {
                    userSettingAperture = false;
                } else if (!userSettingAperture) {
                    GFCommonUtil.getInstance().setAperture(true);
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
                String str = GFS1OffEEState.mDisplayManager.getActiveDevice();
                if (str == null) {
                    AppLog.warning(GFS1OffEEState.TAG, "getActiveDevice() returns null.");
                    return;
                }
                if (str.equals("DEVICE_ID_FINDER") && GFS1OffEEState.mCurrentDisplay.equals("DEVICE_ID_PANEL") && CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                    pushedS1Key();
                    if (FocusModeController.MANUAL.equalsIgnoreCase(GFFocusModeController.getInstance().getValue()) && GFSelfTimerController.SELF_TIMER_OFF.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
                        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
                    } else {
                        GFS1OffEEState.this.setNextState(HALF_SHUTTER_STATE, null);
                    }
                    TouchLessShutterController.ExposingByTouchLessShutter = true;
                }
                String unused = GFS1OffEEState.mCurrentDisplay = GFS1OffEEState.mDisplayManager.getActiveDevice();
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
