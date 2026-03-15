package com.sony.imaging.app.srctrl.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferList;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;
import com.sony.imaging.app.srctrl.streaming.StreamingController;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingSpeed;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFlashMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFocusMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationHalfPressShutter;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationIsoNumber;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationLiveviewFrameInfo;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationProgramShift;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSelfTimer;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShootMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShutterSpeed;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSilentShooting;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationWhiteBalance;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoom;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoomSetting;
import com.sony.imaging.app.srctrl.webapi.specific.SetCameraFunctionHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;
import java.util.Arrays;

/* loaded from: classes.dex */
public class OperationReceiver {
    private static final String TAG = OperationReceiver.class.getSimpleName();
    protected Handler mHandler = new Handler() { // from class: com.sony.imaging.app.srctrl.util.OperationReceiver.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Object[] data = (Object[]) msg.obj;
            OperationRequester<?> c = (OperationRequester) data[0];
            synchronized (c.mSync) {
                c.mResult = OperationReceiver.this.operate(msg.what, (Object[]) data[1]);
                c.mSync.notify();
            }
        }
    };

    public synchronized void terminate() {
        this.mHandler = null;
    }

    public synchronized boolean isAlive() {
        return this.mHandler != null;
    }

    private boolean isRunning() {
        if (RunStatus.getStatus() == 4 && CameraSetting.getInstance().getCamera() != null) {
            return true;
        }
        Log.i(TAG, "RunStatus is Not Running.");
        if (RunStatus.getStatus() != 4) {
            Log.i(TAG, "RunStatus is Not RunStatus.RUNNING.");
        }
        if (CameraSetting.getInstance().getCamera() == null) {
            Log.i(TAG, "CameraSetting.getInstance().getCamera() is null !!");
        }
        return false;
    }

    protected Object operate(int requestID, Object... params) {
        boolean ret;
        Log.i(TAG, "operate.  requestID:" + requestID);
        if (!isRunning()) {
        }
        switch (requestID) {
            case 0:
                return Boolean.valueOf(ExposureCompensationController.getInstance().isAvailable("ExposureCompensation"));
            case 1:
                if (params.length == 1) {
                    ExposureCompensationController.getInstance().setValue(null, (String) params[0]);
                    return true;
                }
                return false;
            case 2:
                return Integer.valueOf(ExposureCompensationController.getInstance().getExposureCompensationIndex());
            case 3:
                return Float.valueOf(ExposureCompensationController.getInstance().getExposureCompensationStep());
            case 4:
                return ExposureCompensationController.getInstance().getSupportedValue(null);
            case 5:
                if (RunStatus.getStatus() == 4 && CameraSetting.getInstance().getCamera() != null) {
                    return ExposureCompensationController.getInstance().getAvailableValue(null);
                }
                return null;
            case 6:
                return Boolean.valueOf(DriveModeController.getInstance().isAvailable(DriveModeController.DRIVEMODE));
            case 7:
                if (params.length == 1) {
                    DriveModeController.getInstance().setValue(DriveModeController.DRIVEMODE, (String) params[0]);
                    return true;
                }
                return false;
            case 8:
                return DriveModeController.getInstance().getValue(DriveModeController.DRIVEMODE);
            case 9:
                return DriveModeController.getInstance().getSupportedValue(DriveModeController.DRIVEMODE);
            case 10:
                return DriveModeController.getInstance().getAvailableValue(DriveModeController.DRIVEMODE);
            case 11:
                if (params.length == 1) {
                    return Boolean.valueOf(DriveModeController.getInstance().isAvailable((String) params[0]));
                }
                return false;
            case 12:
            case 13:
            case 14:
            case 15:
            case 30:
            case 33:
            case 34:
            case 35:
            case 37:
            case 38:
            case 39:
            case 40:
            case 47:
            case 48:
            case 49:
            case 55:
            case 56:
            case HandoffOperationInfo.START_LIVEVIEW /* 58 */:
            case HandoffOperationInfo.STOP_LIVEVIEW /* 59 */:
            default:
                return null;
            case 16:
                return ExposureModeControllerEx.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
            case 17:
                return ExposureModeControllerEx.getInstance().getValue("SceneSelectionMode");
            case 18:
                return Boolean.valueOf(DriveModeController.getInstance().isSelfTimer());
            case 19:
                return Boolean.valueOf(changeToShootingState());
            case 20:
                return Boolean.valueOf(StateController.getInstance().changeToNetworkState());
            case 21:
                if (!isRunning()) {
                    return false;
                }
                String shutterType = (String) params[1];
                String shutterSpeed = CameraOperationShutterSpeed.get();
                Log.v(TAG, "MOVE_TO_CAPTURE_STATE Check. shutterType:" + shutterType + " shutterspeed:" + shutterSpeed);
                if ("BULB".equals(shutterType) && !"BULB".equals(shutterSpeed)) {
                    Log.e(TAG, "ShutterType does not match with ShutterSpeed");
                    return false;
                }
                Log.v(TAG, "MOVE_TO_CAPTURE_STATE\u3000Start. AppCondition : " + StateController.getInstance().getAppCondition());
                StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
                if (StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(appCondition) || StateController.AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.equals(appCondition)) {
                    StateController.getInstance().setLastAppConditionBeforeCapturing(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF);
                } else {
                    StateController.getInstance().setLastAppConditionBeforeCapturing(StateController.AppCondition.SHOOTING_REMOTE);
                }
                return Boolean.valueOf(StateController.getInstance().changeToCaptureState((ShootingHandler.ShutterListenerEx) params[0]));
            case 22:
                CautionUtilityClass.getInstance().executeTerminate();
                return true;
            case 23:
                HandoffOperationInfo.CameraSettings settingName = (HandoffOperationInfo.CameraSettings) params[0];
                return getCameraSetting(settingName);
            case 24:
                HandoffOperationInfo.CameraSettings settingName2 = (HandoffOperationInfo.CameraSettings) params[0];
                return getCameraSettingAvailable(settingName2);
            case 25:
                HandoffOperationInfo.CameraSettings settingName3 = (HandoffOperationInfo.CameraSettings) params[0];
                return getCameraSettingSupported(settingName3);
            case 26:
                HandoffOperationInfo.CameraSettings settingName4 = (HandoffOperationInfo.CameraSettings) params[0];
                return setCameraSetting(settingName4, Arrays.copyOfRange(params, 1, params.length));
            case 27:
                return Boolean.valueOf(changeToS1OnStateForTouchAF());
            case 28:
                return Boolean.valueOf(StateController.getInstance().changeToMovieRecStartState((ShootingHandler.MovieShutterListenerEx) params[0]));
            case 29:
                ExecutorCreator ec = ExecutorCreator.getInstance();
                if (2 == ec.getRecordingMode() && !ec.isRecordingModeChanging()) {
                    ec.getSequence().stopMovieRec();
                    return true;
                }
                return false;
            case 31:
                return CameraOperationHalfPressShutter.enter();
            case 32:
                return CameraOperationHalfPressShutter.leave();
            case 36:
                ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                return true;
            case 41:
                if (!StateController.getInstance().isContentsTransfer()) {
                    boolean bret = StateController.getInstance().changeToPlaybackState();
                    if (!bret) {
                        return SetCameraFunctionHandler.REQ_FAILURE;
                    }
                    return SetCameraFunctionHandler.REQ_SUCCESS;
                }
                return SetCameraFunctionHandler.REQ_SAME_MODE;
            case 42:
                return PrepareTransferList.getInstance().getSchemeValue();
            case 43:
                return PrepareTransferList.getInstance().getSourceList((String) params[0]);
            case HandoffOperationInfo.GET_CONTENT_COUNT /* 44 */:
                return Integer.valueOf(PrepareTransferList.getInstance().getContentCountProxy((String) params[0], (String[]) params[1], (String) params[2], (String) params[3]));
            case HandoffOperationInfo.GET_CONTENT_LIST /* 45 */:
                return PrepareTransferList.getInstance().getContentListProxy((String) params[0], (Integer) params[1], (Integer) params[2], (String[]) params[3], (String) params[4], (String) params[5], (String) params[6]);
            case HandoffOperationInfo.DELETE_CONTENT /* 46 */:
                if (params.length == 1) {
                    ret = StateController.getInstance().changeToDeletingState((String[]) params[0]);
                } else {
                    ret = false;
                }
                return Boolean.valueOf(ret);
            case 50:
                return Boolean.valueOf(StreamingController.getInstance().setContent((String) params[0]));
            case HandoffOperationInfo.START_STREAMING /* 51 */:
                return Boolean.valueOf(StreamingController.getInstance().start());
            case HandoffOperationInfo.PAUSE_STREAMING /* 52 */:
                return Boolean.valueOf(StreamingController.getInstance().pause());
            case HandoffOperationInfo.STOP_STREAMING /* 53 */:
                return Boolean.valueOf(StreamingController.getInstance().stop());
            case HandoffOperationInfo.SEEK_STREAMING /* 54 */:
                return Boolean.valueOf(StreamingController.getInstance().seekPotision(((Integer) params[0]).intValue()));
            case HandoffOperationInfo.SET_SHOOTING_STATE /* 57 */:
                if (StateController.getInstance().isContentsTransfer()) {
                    boolean bret2 = StateController.getInstance().changeToShootingState();
                    if (!bret2) {
                        return SetCameraFunctionHandler.REQ_FAILURE;
                    }
                    return SetCameraFunctionHandler.REQ_SUCCESS;
                }
                return SetCameraFunctionHandler.REQ_SAME_MODE;
            case HandoffOperationInfo.STOP_BULB_SHOOTING /* 60 */:
                ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                SRCtrlNotificationManager.getInstance().requestNotify(SRCtrlNotificationManager.REMOTE_BULB_SHOOT_END);
                return true;
        }
    }

    private Object getCameraSetting(HandoffOperationInfo.CameraSettings settingName) {
        switch (settingName) {
            case TOUCH_AF:
                return CameraOperationTouchAFPosition.get();
            case F_NUMBER:
                return CameraOperationFNumber.get();
            case SHUTTER_SPEED:
                return CameraOperationShutterSpeed.get();
            case ISO_NUMBER:
                return CameraOperationIsoNumber.get();
            case EXPOSURE_MODE:
                return CameraOperationExposureMode.get();
            case WHITE_BALANCE:
                return CameraOperationWhiteBalance.get();
            case FLASH_MODE:
                return CameraOperationFlashMode.get();
            case SHOOT_MODE:
                return CameraOperationShootMode.get();
            case FOCUS_MODE:
                return CameraOperationFocusMode.get();
            case ZOOM_SETTING:
                return CameraOperationZoomSetting.get();
            case STORAGE_INFORMATION:
                return CameraOperationStorageInformation.get();
            case CONT_SHOOT_MODE:
                return CameraOperationContShootingMode.get();
            case CONT_SHOOT_SPEED:
                return CameraOperationContShootingSpeed.get();
            case LIVEVIEW_FRAME_INFO:
                return CameraOperationLiveviewFrameInfo.get();
            case SILNET_SHOOTING:
                return CameraOperationSilentShooting.get();
            case SELF_TIMER:
                return CameraOperationSelfTimer.get();
            default:
                Log.e(TAG, "Invalid camera setting name: " + settingName.name());
                return null;
        }
    }

    private Object setCameraSetting(HandoffOperationInfo.CameraSettings settingName, Object... params) {
        switch (settingName) {
            case TOUCH_AF:
                return CameraOperationTouchAFPosition.set((Double) params[0], (Double) params[1], (CameraOperationTouchAFPosition.CameraNotificationListener) params[2]);
            case F_NUMBER:
                return Boolean.valueOf(CameraOperationFNumber.set((String) params[0]));
            case SHUTTER_SPEED:
                return Boolean.valueOf(CameraOperationShutterSpeed.set((String) params[0]));
            case ISO_NUMBER:
                return Boolean.valueOf(CameraOperationIsoNumber.set((String) params[0]));
            case EXPOSURE_MODE:
                return Boolean.valueOf(CameraOperationExposureMode.set((String) params[0]));
            case WHITE_BALANCE:
                return Boolean.valueOf(CameraOperationWhiteBalance.set((WhiteBalanceParams) params[0], (Boolean) params[1]));
            case FLASH_MODE:
                return Boolean.valueOf(CameraOperationFlashMode.set((String) params[0]));
            case SHOOT_MODE:
                return Boolean.valueOf(CameraOperationShootMode.set((String) params[0]));
            case FOCUS_MODE:
                return Boolean.valueOf(CameraOperationFocusMode.set((String) params[0]));
            case ZOOM_SETTING:
                return Boolean.valueOf(CameraOperationZoomSetting.set((String) params[0]));
            case STORAGE_INFORMATION:
            default:
                Log.e(TAG, "Invalid camera setting name: " + settingName.name());
                return null;
            case CONT_SHOOT_MODE:
                return Boolean.valueOf(CameraOperationContShootingMode.set((String) params[0]));
            case CONT_SHOOT_SPEED:
                return Boolean.valueOf(CameraOperationContShootingSpeed.set((String) params[0]));
            case LIVEVIEW_FRAME_INFO:
                if (isRunning()) {
                    return Boolean.valueOf(CameraOperationLiveviewFrameInfo.set((Boolean) params[0]));
                }
                return false;
            case SILNET_SHOOTING:
                return Boolean.valueOf(CameraOperationSilentShooting.set((String) params[0]));
            case SELF_TIMER:
                return CameraOperationSelfTimer.set(((Integer) params[0]).intValue());
            case PROGRAM_SHIFT:
                return CameraOperationProgramShift.set(((Integer) params[0]).intValue());
            case ZOOM:
                return Boolean.valueOf(CameraOperationZoom.set((String) params[0], (String) params[1]));
        }
    }

    private Object[] getCameraSettingAvailable(HandoffOperationInfo.CameraSettings settingName) {
        switch (settingName) {
            case TOUCH_AF:
                return null;
            case F_NUMBER:
                return CameraOperationFNumber.getAvailable();
            case SHUTTER_SPEED:
                return CameraOperationShutterSpeed.getAvailable();
            case ISO_NUMBER:
                return CameraOperationIsoNumber.getAvailable();
            case EXPOSURE_MODE:
                return CameraOperationExposureMode.getAvailable();
            case WHITE_BALANCE:
                return CameraOperationWhiteBalance.getAvailable();
            case FLASH_MODE:
                return CameraOperationFlashMode.getAvailable();
            case SHOOT_MODE:
                return CameraOperationShootMode.getAvailable();
            case FOCUS_MODE:
                return CameraOperationFocusMode.getAvailable();
            case ZOOM_SETTING:
                return CameraOperationZoomSetting.getAvailable();
            case STORAGE_INFORMATION:
            case LIVEVIEW_FRAME_INFO:
            default:
                Log.e(TAG, "Invalid camera setting name: " + settingName.name());
                return null;
            case CONT_SHOOT_MODE:
                return CameraOperationContShootingMode.getAvailable();
            case CONT_SHOOT_SPEED:
                return CameraOperationContShootingSpeed.getAvailable();
            case SILNET_SHOOTING:
                return CameraOperationSilentShooting.getAvailable();
            case SELF_TIMER:
                return CameraOperationSelfTimer.getAvailable();
        }
    }

    private Object[] getCameraSettingSupported(HandoffOperationInfo.CameraSettings settingName) {
        switch (settingName) {
            case TOUCH_AF:
                return null;
            case F_NUMBER:
                return CameraOperationFNumber.getSupportd();
            case SHUTTER_SPEED:
                return CameraOperationShutterSpeed.getSupportd();
            case ISO_NUMBER:
                return CameraOperationIsoNumber.getSupportd();
            case EXPOSURE_MODE:
                return CameraOperationExposureMode.getSupportd();
            case WHITE_BALANCE:
                return CameraOperationWhiteBalance.getSupportd();
            case FLASH_MODE:
                return CameraOperationFlashMode.getSupportd();
            case SHOOT_MODE:
                return CameraOperationShootMode.getSupportd();
            case FOCUS_MODE:
                return CameraOperationFocusMode.getSupported();
            case ZOOM_SETTING:
                return CameraOperationZoomSetting.getSupported();
            case STORAGE_INFORMATION:
            case LIVEVIEW_FRAME_INFO:
            default:
                Log.e(TAG, "Invalid camera setting name: " + settingName.name());
                return null;
            case CONT_SHOOT_MODE:
                return CameraOperationContShootingMode.getSupportd();
            case CONT_SHOOT_SPEED:
                return CameraOperationContShootingSpeed.getSupportd();
            case SILNET_SHOOTING:
                return CameraOperationSilentShooting.getSupportd();
            case SELF_TIMER:
                return CameraOperationSelfTimer.getSupported();
        }
    }

    public static boolean changeToShootingState() {
        return StateController.getInstance().changeToShootingState();
    }

    public static boolean changeToS1OffState() {
        return StateController.getInstance().changeToS1OffEEState();
    }

    public static boolean changeToS1OnStateForTouchAF() {
        return StateController.getInstance().changeToS1OnEEStateForTouchAF();
    }
}
