package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAF;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAFAssist;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationHalfPressShutter {
    public static final String FOCUS_STATUS_FAILED = "Failed";
    public static final String FOCUS_STATUS_FOCUSED_CONTINUOUS = "Focused With Following Subject Movement";
    public static final String FOCUS_STATUS_FORCUSED = "Focused";
    public static final String FOCUS_STATUS_FORCUSING = "Focusing";
    public static final String FOCUS_STATUS_NOT_FORCUSING = "Not Focusing";
    private static String currentFocusStatus = FOCUS_STATUS_NOT_FORCUSING;
    private static final String tag = CameraOperationHalfPressShutter.class.getName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    private CameraOperationHalfPressShutter() {
    }

    public static Boolean enter() {
        Log.v(tag, "Start shutter half press");
        if (4 != RunStatus.getStatus()) {
            Log.e(tag, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") in " + Thread.currentThread().getStackTrace()[2].toString());
            return Boolean.FALSE;
        }
        State state = StateController.getInstance().getState();
        if (state != null) {
            if ((state instanceof S1OnEEStateForTouchAF) || (state instanceof S1OnEEStateForTouchAFAssist)) {
                return Boolean.FALSE;
            }
            if (StateController.getInstance().changeToS1OnEEState()) {
                Log.v(tag, "Start Autofocus");
                BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
                shootingExecutor.autoFocus(null);
                return Boolean.TRUE;
            }
            Log.e(tag, "Failed. Change to S1OnState.");
            return Boolean.FALSE;
        }
        Log.e(tag, "Cannot acquire current state");
        return Boolean.FALSE;
    }

    public static Boolean leave() {
        if (4 != RunStatus.getStatus()) {
            Log.e(tag, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") in " + Thread.currentThread().getStackTrace()[2].toString());
            return Boolean.FALSE;
        }
        State state = StateController.getInstance().getState();
        if (state != null) {
            if ((state instanceof S1OnEEStateForTouchAF) || (state instanceof S1OnEEStateForTouchAFAssist)) {
                return Boolean.FALSE;
            }
            KeyStatus s1OnStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (1 == s1OnStatus.status) {
                Log.e(tag, "S1 has been pushed on the camera.");
                return Boolean.FALSE;
            }
            if (CameraSetting.getInstance().isFocusHoldSupported() && CameraSetting.getInstance().isFocusHold() && 1 == BaseShootingExecutor.getFocusStatus()) {
                BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
                shootingExecutor.cancelAutoFocus();
                if (StateController.getInstance().getAppCondition() == StateController.AppCondition.SHOOTING_FOCUSING_REMOTE) {
                    StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING);
                }
                return Boolean.TRUE;
            }
            if (StateController.getInstance().changeToS1OffEEState()) {
                BaseShootingExecutor shootingExecutor2 = ExecutorCreator.getInstance().getSequence();
                shootingExecutor2.cancelAutoFocus();
                return Boolean.TRUE;
            }
            Log.e(tag, "Failed. Change to S1OffState.");
            return Boolean.FALSE;
        }
        Log.e(tag, "Cannot acquire current state");
        return Boolean.FALSE;
    }

    public static String getFocusStatus() {
        return currentFocusStatus;
    }

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationHalfPressShutter.1
                private final String[] tags = {CameraNotificationManager.DONE_AUTO_FOCUS, CameraNotificationManager.START_AUTO_FOCUS};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag2) {
                    if (tag2 == CameraNotificationManager.START_AUTO_FOCUS) {
                        if (isVisibleContinuousFocusingStatus()) {
                            String unused = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_FORCUSING;
                        }
                    } else if (tag2 == CameraNotificationManager.DONE_AUTO_FOCUS) {
                        CameraNotificationManager.OnFocusInfo info = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(tag2);
                        if (info != null) {
                            switch (info.status) {
                                case 0:
                                    String unused2 = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_NOT_FORCUSING;
                                    break;
                                case 1:
                                case 4:
                                    if (isVisibleContinuousFocusingStatus()) {
                                        String unused3 = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_FOCUSED_CONTINUOUS;
                                        break;
                                    } else {
                                        String unused4 = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_FORCUSED;
                                        break;
                                    }
                                case 2:
                                    String unused5 = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_FAILED;
                                    break;
                                case 3:
                                default:
                                    String unused6 = CameraOperationHalfPressShutter.currentFocusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_NOT_FORCUSING;
                                    break;
                            }
                        }
                    } else {
                        return;
                    }
                    boolean toBeNotified = ParamsGenerator.updateFocusStatusParams(CameraOperationHalfPressShutter.currentFocusStatus);
                    if (toBeNotified) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.tags;
                }

                private boolean isVisibleContinuousFocusingStatus() {
                    boolean ret = false;
                    KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD);
                    if (key.valid == 1 && key.status == 1) {
                        Log.v(CameraOperationHalfPressShutter.tag, "Lens focus holding.");
                        return false;
                    }
                    int sensorType = FocusAreaController.getInstance().getSensorType();
                    int shootingmode = CameraSetting.getInstance().getCurrentMode();
                    if (2 == shootingmode) {
                        if (1 == sensorType) {
                            ret = true;
                        }
                    } else if (1 == shootingmode && "af-c" == FocusModeController.getInstance().getValue()) {
                        ret = true;
                    }
                    return ret;
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }
}
