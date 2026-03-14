package com.sony.imaging.app.digitalfilter.shooting.state;

import android.os.Handler;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFS1OnEEState extends S1OnEEState implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraEx.LensInfo info;
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(true);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        GFCommonUtil.getInstance().setThemeSelectedFlag(false);
        super.onResume();
        if (SFRSA.getInstance().getCameraSequence() != null) {
            startLiveveiwEffect();
        }
        if (TouchLessShutterController.ExposingByTouchLessShutter) {
            if (myHandler == null) {
                myHandler = new Handler();
            }
            if (myRunnable == null) {
                myRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.state.GFS1OnEEState.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
                    }
                };
            }
            int delayTime = 400;
            if (GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
                CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
                String focusMode = FocusModeController.getInstance().getValue();
                boolean isS1AFOff = AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_P");
                if (!FocusModeController.MANUAL.equalsIgnoreCase(focusMode) && !FocusModeController.DMF.equalsIgnoreCase(focusMode) && !isS1AFOff && onFocusInfo != null && ((onFocusInfo == null || onFocusInfo.status == 0) && (info = CameraSetting.getInstance().getLensInfo()) != null && info.LensType.equals("A-mount") && info.PhaseShiftSensor.equals(FocusAreaController.PHASE_SHIFT_SENSOR_UNKNOWN))) {
                    delayTime = 4000;
                }
            }
            myHandler.postDelayed(myRunnable, delayTime);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler = null;
        }
        myRunnable = null;
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        GFCommonUtil.getInstance().setInvalidShutter(false);
        super.onPause();
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
        return new String[]{GFConstants.UPDATED_BORDER, GFConstants.RESTART_COMPOSIT_PROCESS, GFConstants.CANCEL_TOUCHLESS_SHOOTING};
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
        if (GFConstants.CANCEL_TOUCHLESS_SHOOTING.equalsIgnoreCase(tag)) {
            setNextState(S1OffEEState.STATE_NAME, null);
        }
    }
}
