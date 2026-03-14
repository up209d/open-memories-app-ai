package com.sony.imaging.app.digitalfilter.shooting.state;

import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.ApscModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.digitalfilter.DigitalFilterApp;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFISOSensitivityController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFVerticalLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFMfAssistLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFS1OffLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class GFEEState extends EEState {
    private static final String KEY_CODE = "keyCode";
    private static final float ROUND_DIFF = 0.5f;
    private static final String TRANSITION_FROM = "from";
    private static CameraSettingChangeListener mListener;
    private static NotificationListener mediaStatusChangedListener;
    protected RemainingMemoryLessDispatcher mRemainingMemoryLessDispatcher = new RemainingMemoryLessDispatcher(this);
    private static final String TAG = AppLog.getClassName();
    private static String mItemId = null;
    private static Bundle mBundle = null;
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF, CameraNotificationManager.WB_MODE_CHANGE};
    public static NotificationListener mGyroListener = null;

    static {
        mListener = new CameraSettingChangeListener();
        mediaStatusChangedListener = new MediaNotificationListener();
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        CameraNotificationManager.OnFocusInfo obj;
        CameraNotificationManager.OnFocusInfo obj2;
        boolean handled = true;
        int cautionID = 0;
        if (518 == msg.what) {
            GFCommonUtil.getInstance().pinCalendar();
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            cautionID = GFCommonUtil.getInstance().getCautionId();
            if (cautionID != 0) {
                if (cautionID == 131250) {
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionID, this.mRemainingMemoryLessDispatcher);
                    HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 1);
                }
                CautionUtilityClass.getInstance().requestTrigger(cautionID);
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode) && ((obj2 = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj2.status != 1)) {
                    AppLog.info(TAG, "Cancel S2 trigger.(Focus is not locked)");
                    ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                    if (TouchLessShutterController.ExposingByTouchLessShutter) {
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCEL_TOUCHLESS_SHOOTING);
                        AppLog.info(TAG, "Canceled by Focus condition(TouchLessShutter)");
                    }
                    return true;
                }
                CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                boolean maybePhaseDiff = info != null && "A-mount".equalsIgnoreCase(info.LensType) && FocusAreaController.PHASE_SHIFT_SENSOR_UNKNOWN.equalsIgnoreCase(info.PhaseShiftSensor) && GFCommonUtil.getInstance().isSupportedVersion(2, 14);
                if (maybePhaseDiff && ((obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj.status != 1)) {
                    AppLog.info(TAG, "maybePhaseDiff is true, so I cannot cancel S2 trigger.(Focus is not locked)");
                    GFCommonUtil.getInstance().setMaybePhaseDiffFlag(true);
                }
                if (GFCommonUtil.getInstance().isInvalidShutter()) {
                    AppLog.info(TAG, "Cancel S2 trigger.(Invalid Shutter)");
                    return true;
                }
                if (GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
                    CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
                    String focusMode2 = FocusModeController.getInstance().getValue();
                    boolean isS1AFOff = AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_P");
                    if (FocusModeController.MANUAL.equalsIgnoreCase(focusMode2) || FocusModeController.DMF.equalsIgnoreCase(focusMode2) || isS1AFOff || onFocusInfo == null || (onFocusInfo != null && onFocusInfo.status != 0)) {
                        GFCommonUtil.getInstance().setDuringSelfTimer(true);
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.START_SELFTIMER);
                    } else {
                        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                        if (TouchLessShutterController.ExposingByTouchLessShutter) {
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCEL_TOUCHLESS_SHOOTING);
                            AppLog.info(TAG, "Canceled by Focus condition(TouchLessShutter)");
                        } else {
                            AppLog.info(TAG, "Canceled by S2 trigger in SelfTimer.(Focus is not locked)");
                        }
                        return true;
                    }
                }
            }
        }
        if (cautionID == 0) {
            handled = super.handleMessage(msg);
        } else if (TouchLessShutterController.ExposingByTouchLessShutter) {
            TouchLessShutterController.ExposingByTouchLessShutter = false;
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            if (!FocusModeController.MANUAL.equalsIgnoreCase(GFFocusModeController.getInstance().getValue()) || !GFSelfTimerController.SELF_TIMER_OFF.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCEL_TOUCHLESS_SHOOTING);
            }
        }
        return handled;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        boolean isResetApscSetting = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_RESET_APSC_SETTING, false);
        if (!isResetApscSetting) {
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_RESET_APSC_SETTING, true);
            ApscModeController.getInstance().resetToInitial();
        }
        if (GFCommonUtil.getInstance().isEnableFlash() && !GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
            String baseWBmode = param.getWBMode(0);
            String filterWBmode = param.getWBMode(1);
            String layer3WBmode = param.getWBMode(2);
            boolean isBaseAWB = "auto".equalsIgnoreCase(baseWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(baseWBmode);
            boolean isFilterAWB = "auto".equalsIgnoreCase(filterWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(filterWBmode);
            boolean isLayer3AWB = "auto".equalsIgnoreCase(layer3WBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(layer3WBmode);
            if (!GFFilterSetController.getInstance().need3rdShooting()) {
                isLayer3AWB = false;
            }
            if (isBaseAWB || isFilterAWB || isLayer3AWB) {
                CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FLASH_CHANGE);
            }
        }
        try {
            String origQuality = PictureQualityController.getInstance().getValue(null);
            if (origQuality.equals(PictureQualityController.PICTURE_QUALITY_RAW)) {
                PictureQualityController.getInstance().setValue(null, PictureQualityController.PICTURE_QUALITY_RAWJPEG);
            } else {
                PictureQualityController.getInstance().setValue(null, PictureQualityController.PICTURE_QUALITY_RAW);
            }
            PictureQualityController.getInstance().setValue(null, origQuality);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }
        GFS1OffLayout.mPrevS1OffDispMode = -1;
        if (this.data != null) {
            mItemId = this.data.getString(MenuState.ITEM_ID);
            if (this.data.containsKey("keyCode")) {
                String from = this.data.getString("from");
                if (BaseApp.APP_PLAY.equals(from)) {
                    KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                    if (1 == status.status) {
                        GFCommonUtil.getInstance().setS1_ONfromPlayback(true);
                        GFCommonUtil.getInstance().setInvalidShutter(true);
                    }
                }
            }
            this.data = null;
        }
        super.onResume();
        if (GFCommonUtil.getInstance().disableShootingOnPlayback()) {
            Handler mHandler = new Handler();
            Runnable mRun = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.state.GFEEState.1
                @Override // java.lang.Runnable
                public void run() {
                    GFCommonUtil.getInstance().clearHoldKey();
                }
            };
            mHandler.postDelayed(mRun, 2000L);
        }
        if (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS)) {
            GFSettingMenuLayout.disable3rdArea();
        }
        GFCommonUtil.getInstance().setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
        GFCommonUtil.getInstance().setPreivewButtonPostion(0);
        if (GFVerticalLinkController.getInstance().hasDigitalLevel()) {
            if (mGyroListener == null) {
                mGyroListener = createGyroListener();
            }
            GyroscopeObserver.getInstance().setNotificationListener(mGyroListener);
        }
        CameraNotificationManager.getInstance().setNotificationListener(mListener);
        updateAELStatusWithFlash();
        MediaNotificationManager.getInstance().setNotificationListener(mediaStatusChangedListener);
        boolean isInit = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_ISO_AUTO_MAXMIN_INIT, false);
        if (!isInit) {
            GFISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX);
        }
    }

    private void updateAELStatusWithFlash() {
        boolean isLockByApps = GFAELController.getInstance().isAELock();
        boolean isLockByPf = GFAELController.getInstance().getAELockButtonState();
        if (isLockByPf && !isLockByApps) {
            GFAELController.getInstance().unlock();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (GFVerticalLinkController.getInstance().hasDigitalLevel()) {
            GyroscopeObserver.getInstance().removeNotificationListener(mGyroListener);
            mGyroListener = null;
        }
        CameraNotificationManager.getInstance().removeNotificationListener(mListener);
        MediaNotificationManager.getInstance().removeNotificationListener(mediaStatusChangedListener);
        mItemId = null;
        mBundle = null;
        GFHistgramTask.getInstance().stopHistgramUpdating();
        SFRSA.getInstance().terminate();
        GFCommonUtil.getInstance().clearHoldKey();
        if (GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE))) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 0);
            CautionUtilityClass.getInstance().disapperTrigger(GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        }
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String ret;
        if ("SkySettings".equals(mItemId)) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.LAYOUT_ID, GFSettingMenuLayout.MENU_ID);
            ret = "Menu";
            mItemId = null;
        } else if (ExposureModeController.EXPOSURE_MODE.equals(mItemId)) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.ITEM_ID, mItemId);
            mItemId = null;
            ret = "Menu";
        } else if (DigitalFilterApp.mBootFactor == 0) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.ITEM_ID, GFConstants.APPTOP);
            ret = "Menu";
        } else {
            ret = super.getNextChildState();
            if (ret.equalsIgnoreCase(S1OffEEState.STATE_NAME)) {
                ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            }
        }
        DigitalFilterApp.mBootFactor = 2;
        return ret;
    }

    /* loaded from: classes.dex */
    private class RemainingMemoryLessDispatcher extends IkeyDispatchEach {
        private State mState;

        public RemainingMemoryLessDispatcher(State state) {
            this.mState = state;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
            int result = 1;
            if (CustomizableFunction.Unknown.equals(func)) {
                result = 1;
            }
            if (CustomizableFunction.Unchanged.equals(func)) {
                int code = event.getScanCode();
                if (code == 0) {
                    code = event.getKeyCode();
                }
                if (event.getAction() == 0) {
                    switch (code) {
                        case 23:
                        case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                            if (GFCommonUtil.getInstance().isOkFocusedOnRemainingMemroryCaution()) {
                                startCapturing();
                                return 1;
                            }
                            cancelCapturing();
                            return 1;
                        case 82:
                        case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                        case AppRoot.USER_KEYCODE.MENU /* 514 */:
                            cancelCapturing();
                            return 1;
                        case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                            CautionUtilityClass.getInstance().executeTerminate();
                            return 0;
                        case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                        case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                        case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                            return result;
                        default:
                            return 1;
                    }
                }
                return result;
            }
            return result;
        }

        private void cancelCapturing() {
            CautionUtilityClass.getInstance().disapperTrigger(GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        }

        private void startCapturing() {
            this.mState.setNextState("Capture", null);
            CautionUtilityClass.getInstance().disapperTrigger(GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                    CautionUtilityClass.getInstance().disapperTrigger(GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                    GFEEState.this.onResume();
                    break;
            }
            return super.onConvertedKeyUp(event, func);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.StateBase
    public void stopObserveEVDial() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.StateBase
    public void startObserveEVDial() {
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return mBundle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.FLASH_CHANGE) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.WB_MODE_CHANGE)) {
                if (GFCommonUtil.getInstance().isValidFlashCheck() && !GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    String theme = GFThemeController.getInstance().getValue();
                    GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
                    String baseWBmode = param.getWBMode(0);
                    String filterWBmode = param.getWBMode(1);
                    String layer3WBmode = param.getWBMode(2);
                    boolean isBaseAWB = "auto".equalsIgnoreCase(baseWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(baseWBmode);
                    boolean isFilterAWB = "auto".equalsIgnoreCase(filterWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(filterWBmode);
                    boolean isLayer3AWB = "auto".equalsIgnoreCase(layer3WBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(layer3WBmode);
                    if (!GFFilterSetController.getInstance().need3rdShooting()) {
                        isLayer3AWB = false;
                    }
                    boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB);
                    boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB);
                    boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_WB);
                    if (GFCommonUtil.getInstance().isEnableFlash()) {
                        if (isBaseAWB) {
                            param.setWBMode(0, WhiteBalanceController.FLASH);
                            if (isLandLink) {
                                String wbOption = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 0, theme);
                                GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                                GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                                if (isSkyLink) {
                                    GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption, 1, theme);
                                }
                                if (isLayer3Link) {
                                    GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption, 2, theme);
                                }
                            }
                            if (GFEEAreaController.getInstance().isLand() && !GFCommonUtil.getInstance().isLayerSetting()) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                            }
                        }
                        if (isFilterAWB) {
                            param.setWBMode(1, WhiteBalanceController.FLASH);
                            if (isSkyLink && !isLandLink) {
                                String wbOption2 = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 1, theme);
                                GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                                GFBackUpKey.getInstance().setCommonWBOption(wbOption2, theme);
                                if (isLayer3Link) {
                                    GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption2, 2, theme);
                                }
                            }
                            if (GFEEAreaController.getInstance().isSky() && !GFCommonUtil.getInstance().isLayerSetting()) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                            }
                        }
                        if (isLayer3AWB) {
                            param.setWBMode(2, WhiteBalanceController.FLASH);
                            if (isLayer3Link && !isSkyLink && !isLandLink) {
                                String wbOption3 = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 2, theme);
                                GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                                GFBackUpKey.getInstance().setCommonWBOption(wbOption3, theme);
                            }
                            if (GFEEAreaController.getInstance().isLayer3() && !GFCommonUtil.getInstance().isLayerSetting()) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                            }
                        }
                        if (isBaseAWB || isFilterAWB || isLayer3AWB) {
                            GFBackUpKey.getInstance().saveLastParameters(param.flatten());
                            boolean showCaution = true;
                            if (ModeDialDetector.hasModeDial() && !GFExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                                AppLog.info(GFEEState.TAG, "No flash caution by Invalid DialPosition.");
                                showCaution = false;
                            }
                            if (showCaution) {
                                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH);
                                CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_AWB_BY_FLASH);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            if (tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF)) {
                GFCommonUtil.getInstance().setFlashMode();
                String exposureMode = GFExposureModeController.getInstance().getValue(null);
                if (exposureMode.equalsIgnoreCase(ExposureModeController.MANUAL_MODE) || exposureMode.equalsIgnoreCase("Shutter")) {
                    GFCommonUtil.getInstance().setShutterSpeed(GFCommonUtil.getInstance().getSettingLayer());
                    return;
                }
                return;
            }
            if (tag.equalsIgnoreCase(CameraNotificationManager.FOCUS_CHANGE)) {
                checkFocusMode();
            }
        }

        private void checkFocusMode() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GFEEState.TAGS;
        }
    }

    /* loaded from: classes.dex */
    private static class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int caution_ID = 0;
            CautionUtilityClass.cautionData cautionData = CautionUtilityClass.getInstance().getCurrentCautionData();
            if (cautionData != null) {
                caution_ID = cautionData.maxPriorityId;
            }
            AppLog.info(GFEEState.TAG, "cautionID = 0x" + Integer.toHexString(caution_ID));
            switch (caution_ID) {
                case GFInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE /* 131202 */:
                case GFInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 131211 */:
                case GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS /* 131250 */:
                case GFInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL /* 131251 */:
                case GFInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY /* 131252 */:
                    int state = MediaNotificationManager.getInstance().getMediaState();
                    if (2 == state) {
                        CautionUtilityClass.getInstance().disapperTrigger(GFInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                    }
                    CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float roundRadian(float rad) {
        double roundValue = 0.0d;
        if (rad > -0.7853981633974483d && rad <= 0.7853981633974483d) {
            roundValue = 0.0d;
        } else if (rad > 0.7853981633974483d && rad <= 2.356194490192345d) {
            roundValue = 1.5707963267948966d;
        } else if (rad > -2.356194490192345d && rad <= -0.7853981633974483d) {
            roundValue = 4.71238898038469d;
        } else if (rad > 2.356194490192345d || rad <= -2.356194490192345d) {
            roundValue = 3.141592653589793d;
        }
        return (float) roundValue;
    }

    public static int toDegrees(float rad) {
        double rollTmp = Math.toDegrees(rad) * (-1.0d);
        if (rollTmp < 0.0d) {
            int roll = (int) (rollTmp - 0.5d);
            return roll;
        }
        if (rollTmp <= 0.0d) {
            return 0;
        }
        int roll2 = (int) (rollTmp + 0.5d);
        return roll2;
    }

    public static int adjustDegree(int origDegree, int roll, boolean isLayer3) {
        int degree = origDegree - roll;
        if (degree < 0) {
            degree += 360;
        } else if (degree >= 360) {
            degree -= 360;
        }
        if (isLayer3) {
            GFEffectParameters.getInstance().getParameters().setDegree2(degree);
        } else {
            GFEffectParameters.getInstance().getParameters().setDegree(degree);
        }
        return origDegree;
    }

    public static void adjustPoint(DisplayManager.VideoRect videoRect, PointF origPoint, int degree, float rad, boolean isLayer3) {
        int pointX;
        int pointY;
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double ratio = 1.0d;
        if ((DisplayModeObserver.getInstance().getActiveDevice() == 0 || DisplayModeObserver.getInstance().getActiveDevice() == 2) && ScalarProperties.getInt("device.panel.aspect") == 169) {
            ratio = 0.75d;
        }
        int centerX = (videoRect.pxRight - videoRect.pxLeft) / 2;
        int centerY = (videoRect.pxBottom - videoRect.pxTop) / 2;
        double y = ((origPoint.y - centerY) - videoRect.pxTop) * ratio;
        double x = ((origPoint.x - centerX) - videoRect.pxLeft) / ratio;
        double xRotated = (x * cos) - (y * sin);
        double yRotated = (x * sin) + (y * cos);
        if (xRotated > centerX || xRotated < (-centerX) || yRotated > centerY || yRotated < (-centerY)) {
            if (degree == 0 || degree == 180) {
                if (yRotated > centerY) {
                    yRotated = centerY;
                } else if (yRotated < (-centerY)) {
                    yRotated = -centerY;
                }
            } else if (degree != 90 && degree != 270) {
                double compY = yRotated;
                double tan = Math.tan(rad);
                if (yRotated > centerY) {
                    compY = centerY;
                } else if (yRotated < (-centerY)) {
                    compY = -centerY;
                }
                double compX = ((yRotated - compY) / tan) + xRotated;
                if (compX > centerX || compX < (-centerX)) {
                    if (compX > centerX) {
                        compX = centerX;
                    } else if (compX < (-centerX)) {
                        compX = -centerX;
                    }
                    compY = ((compX - xRotated) * tan) + yRotated;
                    if (yRotated > centerY || yRotated < (-centerY)) {
                        compX = xRotated;
                        compY = yRotated;
                    }
                }
                xRotated = compX;
                yRotated = compY;
            } else if (xRotated > centerX) {
                xRotated = centerX;
            } else if (xRotated < (-centerX)) {
                xRotated = -centerX;
            }
        }
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            xRotated *= -1.0d;
            yRotated *= -1.0d;
        }
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            pointX = (int) Math.ceil(centerX + xRotated + videoRect.pxLeft + 0.5d);
            pointY = (int) Math.ceil(centerY + yRotated + videoRect.pxTop + 0.5d);
        } else {
            pointX = (int) Math.ceil(centerX + xRotated + videoRect.pxLeft);
            pointY = (int) Math.ceil(centerY + yRotated + videoRect.pxTop);
        }
        PointF pointf = new PointF(pointX, pointY);
        if (isLayer3) {
            GFEffectParameters.getInstance().getParameters().setOSDPoint2(pointf);
        } else {
            GFEffectParameters.getInstance().getParameters().setOSDPoint(pointf);
        }
    }

    public static void adjustBorder(DisplayManager.VideoRect videoRect, float rad, int roll) {
        int degree = GFEffectParameters.getInstance().getParameters().getDegree();
        int degree2 = GFEffectParameters.getInstance().getParameters().getDegree2();
        adjustDegree(degree, roll, false);
        adjustDegree(degree2, roll, true);
        adjustPoint(videoRect, GFEffectParameters.getInstance().getParameters().getOSDPoint(), degree, rad, false);
        adjustPoint(videoRect, GFEffectParameters.getInstance().getParameters().getOSDPoint2(), degree2, rad, true);
    }

    public NotificationListener createGyroListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.digitalfilter.shooting.state.GFEEState.2
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                if (!GFMfAssistLayout.isOpen) {
                    String theme = GFThemeController.getInstance().getValue();
                    DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                    float[] values = (float[]) GyroscopeObserver.getInstance().getValue(GyroscopeObserver.TAG_GYROSCOPE);
                    if (values[1] >= -0.9f && values[1] <= 0.9f) {
                        GFEEState.toDegrees(values[2]);
                        String mode = GFVerticalLinkController.getInstance().getValue(GFVerticalLinkController.BOUNDARY_MODE);
                        if (GFVerticalLinkController.MANUAL.equals(mode)) {
                            GFBackUpKey.getInstance().saveDeviceDirection(GFEEState.roundRadian(values[2]), theme);
                            return;
                        }
                        if (GFVerticalLinkController.VERTICAL.equals(mode)) {
                            float rad = GFEEState.roundRadian(values[2]);
                            float prevRad = GFBackUpKey.getInstance().getDeviceDirection(theme);
                            if (prevRad != rad) {
                                float rotate = rad - prevRad;
                                GFBackUpKey.getInstance().saveDeviceDirection(rad, theme);
                                int roll = GFEEState.toDegrees(rotate);
                                GFEEState.adjustBorder(videoRect, rotate, roll);
                                GFCommonUtil.getInstance().setVerticalLinkState(true);
                                CameraNotificationManager.getInstance().requestNotify(GFConstants.TAG_GYROSCOPE);
                                if (GFCommonUtil.getInstance().isLayerSetting()) {
                                    CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
                                } else {
                                    CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATED_BORDER);
                                }
                            }
                        }
                    }
                }
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return new String[]{GyroscopeObserver.TAG_GYROSCOPE};
            }
        };
    }
}
