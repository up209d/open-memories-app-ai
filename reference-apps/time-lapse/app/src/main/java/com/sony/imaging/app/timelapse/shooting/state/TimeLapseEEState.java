package com.sony.imaging.app.timelapse.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.TimelapseExecutorCreator;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class TimeLapseEEState extends EEState {
    public static final String CAUTION_ID_NOT_EXIST = "Caution ID not exist";
    private static final String TAG = "TimeLapseEEState";
    private static TLCommonUtil mTlCommonUtil = null;
    private static int sCautionId = 0;
    private String mItemId;
    private Bundle mBundle = null;
    protected RemainingMemoryLessDispatcher mRemainingMemoryLessDispatcher = new RemainingMemoryLessDispatcher(this);
    protected NotificationListener mediaStatusChangedListener = new MediaNotificationListener();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        mTlCommonUtil = TLCommonUtil.getInstance();
        mTlCommonUtil.setFlashModeOff();
        TimelapseExecutorCreator creator = (TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance();
        creator.saveTimeLapseBOData();
        mTlCommonUtil.setTestShot(false);
        TLCommonUtil.getInstance().setCaptureStatus(false);
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data = null;
        }
        super.onResume();
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        displayDBErrorCaution();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            this.mItemId = null;
            AppLog.trace(TAG, "getNextChildState  Mode Dial Transition");
            return "Menu";
        }
        if (mTlCommonUtil.getBootFactor() == 0) {
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, TimeLapseConstants.APPTOPLAYOUT);
            this.mItemId = null;
            AppLog.trace(TAG, "getNextChildState  APPTOP Transition");
            return "Menu";
        }
        AppLog.trace(TAG, "getNextChildState  Shooting Screen Transition");
        mTlCommonUtil.setBootFactor(2);
        mTlCommonUtil.updateLastAppliedTheme();
        String ret = super.getNextChildState();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mBundle = null;
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayDBErrorCaution() {
        if (!mTlCommonUtil.checkExposureMode() && DataBaseAdapter.getInstance().isDBStatusCorrupt()) {
            CautionUtilityClass.getInstance().setDispatchKeyEvent(TimelapseInfo.CAUTION_ID_DLAPP_DB_ERROR, null);
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_DB_ERROR);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 0);
        disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
        disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        CameraNotificationManager.OnFocusInfo obj;
        CameraNotificationManager.OnFocusInfo obj2;
        boolean ret = false;
        sCautionId = 0;
        if (518 == msg.what) {
            mTlCommonUtil.pinCalendar();
            TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            sCautionId = mTlCommonUtil.getCautionId();
            if (sCautionId != 0) {
                AppLog.trace(TAG, "handleMessage() sCautionId: " + sCautionId);
                if (sCautionId == 131205) {
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(sCautionId, this.mRemainingMemoryLessDispatcher);
                    HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 1);
                }
                CautionUtilityClass.getInstance().requestTrigger(sCautionId);
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode) && !TLCommonUtil.getInstance().isTestShot() && ((obj2 = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj2.status != 1)) {
                    AppLog.info(TAG, "Cancel S2 trigger.(Focus is not locked)");
                    sCautionId = 1;
                    return false;
                }
                CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                boolean maybePhaseDiff = info != null && "A-mount".equalsIgnoreCase(info.LensType) && FocusAreaController.PHASE_SHIFT_SENSOR_UNKNOWN.equalsIgnoreCase(info.PhaseShiftSensor) && TLCommonUtil.getInstance().isSupportedVersion(2, 14);
                if (maybePhaseDiff && ((obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj.status != 1)) {
                    AppLog.info(TAG, "maybePhaseDiff is true, so I cannot cancel S2 trigger.(Focus is not locked)");
                    TLCommonUtil.getInstance().setMaybePhaseDiffFlag(true);
                }
                if (sCautionId == 0) {
                    TimeLapseStableLayout.isCapturing = true;
                    if (!SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
                        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_MOVIE_SPECIFIC_ICON);
                    }
                }
            }
        }
        if (sCautionId == 0) {
            AppLog.trace(TAG, "handleMessage() sCautionId: " + sCautionId);
            ret = super.handleMessage(msg);
        } else if (TLCommonUtil.getInstance().isTestShot()) {
            TLCommonUtil.getInstance().setTestShot(false);
            TLCommonUtil.getInstance().setActualMediaIds();
        }
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
                            if (TimeLapseEEState.mTlCommonUtil.isOkFocusedOnRemainingMemroryCaution()) {
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
                        case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                            break;
                        case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                        case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                        case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                            return result;
                        case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                            TimeLapseConstants.slastStateExposureCompansasion = ExposureCompensationController.getInstance().getExposureCompensationIndex();
                            break;
                        default:
                            return 1;
                    }
                    CautionUtilityClass.getInstance().executeTerminate();
                    return 0;
                }
                return result;
            }
            return result;
        }

        private void cancelCapturing() {
            TimeLapseEEState.disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        }

        private void startCapturing() {
            TimeLapseStableLayout.isCapturing = true;
            this.mState.setNextState("Capture", null);
            TimeLapseEEState.disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                    TimeLapseEEState.disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                    TimeLapseEEState.this.onResume();
                    break;
            }
            return super.onConvertedKeyUp(event, func);
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
            DataBaseAdapter.getInstance().setDBCorruptStatus(false);
            int caution_ID = 0;
            try {
                caution_ID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            } catch (IndexOutOfBoundsException ex) {
                Log.i("TimelapseEEState", ex.getMessage());
            } catch (NullPointerException e) {
                Log.i("TimelapseEEState", TimeLapseEEState.CAUTION_ID_NOT_EXIST);
            }
            switch (caution_ID) {
                case TimelapseInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 131204 */:
                case TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS /* 131205 */:
                case TimelapseInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL /* 131207 */:
                case TimelapseInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY /* 131208 */:
                case TimelapseInfo.CAUTION_ID_DLAPP_DB_FULL /* 131209 */:
                case TimelapseInfo.CAUTION_ID_DLAPP_DB_ERROR /* 131210 */:
                    int state = MediaNotificationManager.getInstance().getMediaState();
                    if (2 == state) {
                        TimeLapseEEState.disapearDisplayingCaution(TimelapseInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                    }
                    TimeLapseEEState.disapearDisplayingCaution(caution_ID);
                    break;
            }
            DatabaseUtil.DbResult result = DataBaseOperations.getInstance().importDatabase();
            if (result == DatabaseUtil.DbResult.DB_ERROR) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                TimeLapseEEState.displayDBErrorCaution();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disapearDisplayingCaution(int caution_ID) {
        CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
    }
}
