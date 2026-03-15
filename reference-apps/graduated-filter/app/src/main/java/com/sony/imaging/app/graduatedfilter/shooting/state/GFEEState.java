package com.sony.imaging.app.graduatedfilter.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.GraduatedFilterApp;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.graduatedfilter.shooting.layout.GFS1OffLayout;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class GFEEState extends EEState {
    private static final String KEY_CODE = "keyCode";
    private static final String TRANSITION_FROM = "from";
    CameraSettingChangeListener mListener = new CameraSettingChangeListener();
    private static final String TAG = AppLog.getClassName();
    private static String mItemId = null;
    private static Bundle mBundle = null;
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF};

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        CameraNotificationManager.OnFocusInfo obj;
        CameraNotificationManager.OnFocusInfo obj2;
        boolean handled = true;
        int cautionID = 0;
        if (518 == msg.what) {
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            cautionID = GFCommonUtil.getInstance().getCautionId();
            if (cautionID != 0) {
                CautionUtilityClass.getInstance().requestTrigger(cautionID);
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode) && ((obj2 = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj2.status != 1)) {
                    AppLog.info(TAG, "Cancel S2 trigger.(Focus is not locked)");
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
                        AppLog.info(TAG, "Cancel S2 trigger in SelfTimer.(Focus is not locked)");
                        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
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
            Runnable mRun = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.state.GFEEState.1
                @Override // java.lang.Runnable
                public void run() {
                    GFCommonUtil.getInstance().clearHoldKey();
                }
            };
            mHandler.postDelayed(mRun, 2000L);
        }
        GFCommonUtil.getInstance().stopFilterSetting();
        GFCommonUtil.getInstance().setPreivewButtonPostion(0);
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        updateAELStatusWithFlash();
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
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        mItemId = null;
        mBundle = null;
        GFHistgramTask.getInstance().stopHistgramUpdating();
        SFRSA.getInstance().terminate();
        GFCommonUtil.getInstance().clearHoldKey();
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String ret;
        if (GFConstants.APPSETTING.equals(mItemId)) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.LAYOUT_ID, GFSettingMenuLayout.MENU_ID);
            ret = "Menu";
            mItemId = null;
        } else if (ExposureModeController.EXPOSURE_MODE.equals(mItemId)) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.ITEM_ID, mItemId);
            mItemId = null;
            ret = "Menu";
        } else if (GraduatedFilterApp.mBootFactor == 0) {
            mBundle = new Bundle();
            mBundle.putString(MenuState.ITEM_ID, GFConstants.APPTOP);
            ret = "Menu";
        } else {
            ret = super.getNextChildState();
        }
        GraduatedFilterApp.mBootFactor = 2;
        return ret;
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

    /* loaded from: classes.dex */
    private class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.FLASH_CHANGE) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
                if (GFCommonUtil.getInstance().isValidFlashCheck() && !GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
                    String baseWBmode = param.getWBMode(true);
                    String filterWBmode = param.getWBMode(false);
                    boolean isBaseAWB = "auto".equalsIgnoreCase(baseWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(baseWBmode);
                    boolean isFilterAWB = "auto".equalsIgnoreCase(filterWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(filterWBmode);
                    boolean isFilterSAME = "OFF".equalsIgnoreCase(param.getColorFilter());
                    if (isFilterSAME) {
                        isFilterAWB = false;
                    }
                    if (GFCommonUtil.getInstance().isEnableFlash()) {
                        if (isBaseAWB) {
                            param.setWBMode(true, WhiteBalanceController.FLASH);
                            if (!GFCommonUtil.getInstance().isFilterSetting()) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                            } else if (isFilterSAME) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, GFWhiteBalanceController.SAME);
                            }
                        }
                        if (isFilterAWB) {
                            param.setWBMode(false, WhiteBalanceController.FLASH);
                            if (GFCommonUtil.getInstance().isFilterSetting()) {
                                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                            }
                        }
                        if (isBaseAWB && isFilterAWB) {
                            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH_BOTH);
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_BASE_AWB_BY_FLASH);
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_FILTER_AWB_BY_FLASH);
                        } else if (isBaseAWB) {
                            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH_BASE);
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_BASE_AWB_BY_FLASH);
                        } else if (isFilterAWB) {
                            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH_FILTER);
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_FILTER_AWB_BY_FLASH);
                        }
                        if (isBaseAWB || isFilterAWB) {
                            GFBackUpKey.getInstance().saveLastParameters(param.flatten());
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
            } else if (tag.equalsIgnoreCase(CameraNotificationManager.FOCUS_CHANGE)) {
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
}
