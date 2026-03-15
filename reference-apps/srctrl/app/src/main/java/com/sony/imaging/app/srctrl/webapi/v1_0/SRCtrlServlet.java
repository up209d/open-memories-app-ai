package com.sony.imaging.app.srctrl.webapi.v1_0;

import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.srctrl.liveview.LiveviewContainer;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyContShootingMode;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyContShootingSpeed;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyExposureMode;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyFNumber;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyFlashMode;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyFocusMode;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyHalfPressShutter;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyIsoNumber;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyLiveviewFrameInfo;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyLiveviewSize;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyPostviewImageSize;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyProgramShift;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxySelftimer;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyShootMode;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyShutterSpeed;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxySilentShooting;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyStorageInformation;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyTouchAFPosition;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyWhiteBalance;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyZoom;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyZoomSetting;
import com.sony.imaging.app.srctrl.webapi.availability.AvailabilityDetector;
import com.sony.imaging.app.srctrl.webapi.availability.AvailableManager;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.specific.RecModeTransitionHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.SetCameraFunctionHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.srctrl.webapi.util.ApiCallLog;
import com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase;
import com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StartBulbShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StopBulbShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetAvailableCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetSupportedCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.SetCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventAELockParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventBeepModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventBracketShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventContinuousErrorParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCreativeStyleParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFormatStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventLiveviewOrientationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventMovieQualityParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventPictureEffectParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSceneRecognitionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSteadyModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventStillSizeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventViewAngleParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.LiveviewFrameInfoParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.StorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.contshooting.StartContShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshooting.StopContShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetAvailableContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetSupportedContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.SetContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetAvailableContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetSupportedContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.SetContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetAvailableExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetSupportedExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.SetExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetAvailableExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetSupportedExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.SetExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetAvailableFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetSupportedFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.SetFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetAvailableFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetSupportedFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.SetFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetAvailableFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetSupportedFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.SetFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.getevent.GetEventCallback;
import com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.ActHalfPressShutterCallback;
import com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.CancelHalfPressShutterCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetAvailableIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetSupportedIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.SetIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetAvailableLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetSupportedLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.StartLiveviewCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.StopLiveviewCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.GetLiveviewFrameInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.SetLiveviewFrameInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.misc.GetApplicationInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.misc.GetAvailableApiListCallback;
import com.sony.scalar.webapi.service.camera.v1_0.movierec.StartMovieRecCallback;
import com.sony.scalar.webapi.service.camera.v1_0.movierec.StopMovieRecCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetAvailablePostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetSupportedPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.SetPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.programshift.GetSupportedProgramShiftCallback;
import com.sony.scalar.webapi.service.camera.v1_0.programshift.SetProgramShiftCallback;
import com.sony.scalar.webapi.service.camera.v1_0.recmode.StartRecModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.recmode.StopRecModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetAvailableSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSupportedSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.SetSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetAvailableShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetSupportedShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.SetShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetAvailableShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetSupportedShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.SetShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetAvailableSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSupportedSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.SetSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.storageinformation.GetStorageInformationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.takepicture.ActTakePictureCallback;
import com.sony.scalar.webapi.service.camera.v1_0.takepicture.AwaitTakePictureCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.CancelTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.GetTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.SetTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetAvailableWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetSupportedWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.SetWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.ActZoomCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetAvailableZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetSupportedZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.SetZoomSettingCallback;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class SRCtrlServlet extends SmartRemoteControlServiceBase {
    private static final String TAG = SRCtrlServlet.class.getSimpleName();
    private static final GetEventContinuousErrorParams[] s_DUMMY_PARAM_ARRAY_CONTINUOUS_ERROR = new GetEventContinuousErrorParams[0];
    private static final GetEventStorageInformationParams[] s_DUMMY_PARAM_ARRAY_STORAGEINFOMATION = new GetEventStorageInformationParams[0];
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void actTakePicture(ActTakePictureCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Log.v(TAG, apiCallLog2.getMethodName() + " AppCondition : " + StateController.getInstance().getAppCondition());
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        ShootingHandler.ShootingStatus currentStatus = sHandler.createStillPicture();
                        if (ShootingHandler.ShootingStatus.FINISHED.equals(currentStatus)) {
                            String[] result = sHandler.getUrlArrayResult();
                            if (result == null) {
                                Log.v(TAG, "FAILURE: No url is available.");
                                returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Shooting Succeeded: No URL");
                            } else {
                                Log.v(TAG, "SUCCESS: Finished capturing.");
                                returnCb.returnCb(result);
                            }
                        } else if (ShootingHandler.ShootingStatus.FAILED.equals(currentStatus)) {
                            Log.v(TAG, "FAILURE: Shooting failed: " + sHandler.getErrorStatus().toString());
                            returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Shooting Failed");
                        } else if (ShootingHandler.ShootingStatus.PROCESSING.equals(currentStatus) || ShootingHandler.ShootingStatus.DEVELOPING.equals(currentStatus)) {
                            Log.v(TAG, "PROCESSING: Long capturing is not finished yet.");
                            returnCb.handleStatus(StatusCode.STILL_CAPTURING_NOT_FINISHED.toInt(), "Long shooting");
                        } else if (ShootingHandler.ShootingStatus.READY.equals(currentStatus)) {
                            Log.e(TAG, "FAILURE: Unexpected READY state after shooting.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unexpected state");
                        }
                    } else {
                        Log.e(TAG, "Failed to start taking picture because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void awaitTakePicture(AwaitTakePictureCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init(false);
                    ShootingHandler sHandler = ShootingHandler.getInstance();
                    ShootingHandler.ShootingStatus currentStatus = sHandler.getShootingStatus();
                    ShootingHandler.WaitingStatus waitingStatus = sHandler.getWaitingStatus();
                    switch (waitingStatus) {
                        case WAITING:
                        case NOT_WAITING:
                            if (!AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                                Log.e(TAG, "Failed to await taking picture because of current application state");
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                                break;
                            } else {
                                switch (currentStatus) {
                                    case FINISHED:
                                        Log.v(TAG, "Capturing is finished after previous operation.");
                                        String[] result = sHandler.getUrlArrayResult();
                                        if (result != null) {
                                            Log.v(TAG, "SUCCESS: Return previous pictures' url.");
                                            returnCb.returnCb(result);
                                            break;
                                        } else {
                                            Log.v(TAG, "FAILURE: No url is available.");
                                            returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "No URL");
                                            break;
                                        }
                                    case FAILED:
                                    case READY:
                                        Log.v(TAG, "FAILURE: Remote shooting is not called.");
                                        returnCb.handleStatus(StatusCode.ANY.toInt(), "StillShootingNotCalled");
                                        break;
                                    case PROCESSING:
                                    case DEVELOPING:
                                        ShootingHandler.ShootingStatus currentStatus2 = sHandler.createStillPicture();
                                        if (!ShootingHandler.WaitingStatus.CANCELED.equals(sHandler.getWaitingStatus())) {
                                            switch (currentStatus2) {
                                                case FINISHED:
                                                    String[] result2 = sHandler.getUrlArrayResult();
                                                    if (result2 != null) {
                                                        Log.v(TAG, "SUCCESS: Finished capturing.");
                                                        returnCb.returnCb(result2);
                                                        break;
                                                    } else {
                                                        Log.v(TAG, "FAILURE: No url is available.");
                                                        returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "No URL");
                                                        break;
                                                    }
                                                case FAILED:
                                                    Log.e(TAG, "FAILURE: Shooting failed: " + sHandler.getErrorStatus().toString());
                                                    returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Shooting Failed");
                                                    break;
                                                case PROCESSING:
                                                case DEVELOPING:
                                                    Log.v(TAG, "PROCESSING: Long capturing is not finished yet.");
                                                    returnCb.handleStatus(StatusCode.STILL_CAPTURING_NOT_FINISHED.toInt(), "Not Finished");
                                                    break;
                                                default:
                                                    Log.e(TAG, "FAILURE: Unexpected state after shooting.");
                                                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Unexpected state");
                                                    break;
                                            }
                                        } else {
                                            Log.v(TAG, "CANCELED: DoublePolling");
                                            returnCb.handleStatus(StatusCode.ALREADY_RUNNING_POLLING.toInt(), "DoublePolling");
                                            break;
                                        }
                                    default:
                                        Log.e(TAG, "FAILURE: Unknown Shooting Status.");
                                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown shooting status");
                                        break;
                                }
                            }
                        default:
                            Log.e(TAG, "FAILURE: Unknown Waiting Status.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown waiting status");
                            break;
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setExposureCompensation(int exposure, SetExposureCompensationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        int ret = AvailableManager.setExposureCompensation(exposure);
                        if (ret == 0) {
                            returnCb.returnCb(0);
                        } else {
                            returnCb.handleStatus(ret, "failed");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getExposureCompensation(GetExposureCompensationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Integer current = AvailableManager.getExposureCompensationCurrent();
                        if (current != null) {
                            returnCb.returnCb(current.intValue());
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Current value is not available");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedExposureCompensation(GetSupportedExposureCompensationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        int[][] supported = AvailableManager.getExopsureCompensationSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported[0], supported[1], supported[2]);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "no value is supported");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableExposureCompensation(GetAvailableExposureCompensationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        int[] available = AvailableManager.getExopsureCompensationAvailable();
                        if (available != null) {
                            Integer current = AvailableManager.getExposureCompensationCurrent();
                            if (current != null) {
                                returnCb.returnCb(current.intValue(), available[0], available[1], available[2]);
                            } else {
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Current value is not available");
                            }
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "no value is available");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startLiveview(StartLiveviewCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean error = false;
                        if (LiveviewLoader.isLoadingPreview()) {
                            Log.v(TAG, "Failed: Liveview already started.");
                            String currentSize = CameraProxyLiveviewSize.get();
                            if (!currentSize.equals(LiveviewContainer.s_DEFAULT_LIVEVIEW_SIZE)) {
                                String message = "Default size(=M) differs from the current(=" + currentSize + ").";
                                Log.v(TAG, message);
                                returnCb.handleStatus(StatusCode.ILLEGAL_ARGUMENT.toInt(), message);
                                error = true;
                            }
                        } else {
                            CameraProxyLiveviewSize.set(LiveviewContainer.s_DEFAULT_LIVEVIEW_SIZE);
                            boolean check = LiveviewLoader.startObtainingImages();
                            if (!check) {
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Liveview cannot start.");
                                error = true;
                            }
                        }
                        if (!error) {
                            returnCb.returnCb(SRCtrlConstants.LIVEVIEW_URL);
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void stopLiveview(StopLiveviewCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        LiveviewLoader.stopObtainingImages();
                        returnCb.returnCb(0);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startRecMode(StartRecModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    StateController stateController = StateController.getInstance();
                    stateController.setGoBackFlag(false);
                    if (!StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
                        returnCb.returnCb(0);
                    } else if (!stateController.isWaitingRecModeChange()) {
                        RecModeTransitionHandler.TransStatus status = RecModeTransitionHandler.getInstance().goToShootingState();
                        switch (status) {
                            case SUCCESS:
                                returnCb.returnCb(0);
                                break;
                            default:
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                                break;
                        }
                    } else {
                        returnCb.returnCb(0);
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void stopRecMode(StopRecModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    StateController stateController = StateController.getInstance();
                    StateController.AppCondition condition = stateController.getAppCondition();
                    if (StateController.AppCondition.SHOOTING_INHIBIT.equals(condition) || StateController.AppCondition.SHOOTING_LOCAL.equals(condition) || StateController.AppCondition.SHOOTING_REMOTE.equals(condition) || StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(condition) || StateController.AppCondition.SHOOTING_MOVIE_INHIBIT.equals(condition)) {
                        StateController.getInstance().setGoBackFlag(true);
                        returnCb.returnCb(0);
                    } else if (StateController.AppCondition.PREPARATION.equals(condition)) {
                        returnCb.returnCb(0);
                    } else if (!stateController.isWaitingRecModeChange()) {
                        RecModeTransitionHandler.TransStatus status = RecModeTransitionHandler.getInstance().goToNetworkState();
                        switch (status) {
                            case SUCCESS:
                                returnCb.returnCb(0);
                                break;
                            default:
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                                break;
                        }
                    } else {
                        returnCb.returnCb(0);
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setSelfTimer(int timer, SetSelfTimerCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean ret = CameraProxySelftimer.setSelfTimer(timer);
                        if (ret) {
                            returnCb.returnCb(0);
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "set Drive mode is not available now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSelfTimer(GetSelfTimerCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Integer current = CameraProxySelftimer.getSelftimerCurrent();
                        if (current != null) {
                            returnCb.returnCb(current.intValue());
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Current value is not available");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedSelfTimer(GetSupportedSelfTimerCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        int[] supported = CameraProxySelftimer.getSelftimerSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableSelfTimer(GetAvailableSelfTimerCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Integer current = CameraProxySelftimer.getSelftimerCurrent();
                        if (current != null) {
                            returnCb.returnCb(current.intValue(), CameraProxySelftimer.getSelftimerAvailable());
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Current value is not available");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getApplicationInfo(GetApplicationInfoCallback returnCb) {
        ApiCallLog apiCallLog;
        ApiCallLog apiCallLog2 = null;
        try {
            try {
                apiCallLog = new ApiCallLog();
            } catch (InterruptedException e) {
                e = e;
            } catch (TimeoutException e2) {
            }
            try {
                apiCallLog.init();
                String sServerName = SRCtrlEnvironment.getInstance().getServerName();
                Log.d(TAG, "[" + apiCallLog.getMethodName() + "] NAME=" + sServerName + ", VERSION=" + SRCtrlEnvironment.getInstance().getServerVersion());
                returnCb.returnCb(sServerName, SRCtrlEnvironment.getInstance().getServerVersion());
                if (apiCallLog != null) {
                    apiCallLog.clear();
                    apiCallLog2 = null;
                } else {
                    apiCallLog2 = apiCallLog;
                }
            } catch (InterruptedException e3) {
                e = e3;
                apiCallLog2 = apiCallLog;
                Log.e(TAG, "Method call of " + apiCallLog2.getMethodName() + " was interrupted.");
                e.printStackTrace();
                returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                if (apiCallLog2 != null) {
                    apiCallLog2.clear();
                    apiCallLog2 = null;
                }
            } catch (TimeoutException e4) {
                apiCallLog2 = apiCallLog;
                Log.e(TAG, "Method call of " + apiCallLog2.getMethodName() + " was timeout.");
                returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                if (apiCallLog2 != null) {
                    apiCallLog2.clear();
                    apiCallLog2 = null;
                }
            } catch (Throwable th) {
                th = th;
                apiCallLog2 = apiCallLog;
                if (apiCallLog2 != null) {
                    apiCallLog2.clear();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableApiList(GetAvailableApiListCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        String[] availables = AvailabilityDetector.getAvailables(true);
                        if (availables != null) {
                            returnCb.returnCb(availables);
                            Log.e(TAG, "[" + apiCallLog2.getMethodName() + "] API List: " + Arrays.toString(availables));
                        } else {
                            Log.e(TAG, "[" + apiCallLog2.getMethodName() + "] API List is null.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown Error");
                        }
                    } else {
                        Log.e(TAG, "[" + apiCallLog2.getMethodName() + "] The API call is forbidden now.");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static void printGetEventData(GetEventAvailableApiListParams availableApiList, GetEventCameraStatusParams cameraStatus, GetEventZoomInformationParams zoomInformation, GetEventLiveviewStatusParams liveviewStatus, GetEventLiveviewOrientationParams liveviewOrientation, GetEventTakePictureParams[] takePicture, GetEventContinuousErrorParams[] continuousError, GetEventTriggeredErrorParams triggeredError, GetEventSceneRecognitionParams sceneRecognition, GetEventFormatStatusParams formatStatus, GetEventStorageInformationParams[] storageInformation, GetEventBeepModeParams beepMode, GetEventCameraFunctionParams cameraFunction, GetEventMovieQualityParams movieQuality, GetEventStillSizeParams stillSize, GetEventCameraFunctionResultParams cameraFunctionResult, GetEventSteadyModeParams steadyMode, GetEventViewAngleParams viewAngle, GetEventExposureModeParams exposureMode, GetEventPostviewImageSizeParams postviewImageSize, GetEventSelfTimerParams selfTimer, GetEventShootModeParams shootMode, GetEventAELockParams aeLock, GetEventBracketShootModeParams bracketShootMode, GetEventCreativeStyleParams creativeStyle, GetEventExposureCompensationParams exposureCompensation, GetEventFlashModeParams flashMode, GetEventFNumberParams fNumber, GetEventFocusModeParams focusMode, GetEventIsoSpeedRateParams isoSpeedRate, GetEventPictureEffectParams pictureEffect, GetEventProgramShiftParams programShift, GetEventShutterSpeedParams shutterSpeed, GetEventWhiteBalanceParams whiteBalance, GetEventTouchAFPositionParams touchAFPosition) {
        if (availableApiList != null) {
            Log.v(TAG, "<GetEventAvailableApiListParams>");
            Log.v(TAG, "  --> type = " + availableApiList.type);
            Log.v(TAG, "  --> name = " + Arrays.toString(availableApiList.names));
        }
        if (cameraStatus != null) {
            Log.v(TAG, "<GetEventCameraStatusParams>");
            Log.v(TAG, "  --> type = " + cameraStatus.type);
            Log.v(TAG, "  --> cameraStatus = " + cameraStatus.cameraStatus);
        }
        if (zoomInformation != null) {
            Log.v(TAG, "<GetEventZoomInformationParams>");
            Log.v(TAG, "  --> type = " + zoomInformation.type);
            Log.v(TAG, "  --> zoomIndexCurrentBox =    " + zoomInformation.zoomIndexCurrentBox);
            Log.v(TAG, "  --> zoomNumberBox =          " + zoomInformation.zoomNumberBox);
            Log.v(TAG, "  --> zoomPoistion =           " + zoomInformation.zoomPosition);
            Log.v(TAG, "  --> zoomPositionCurrentBox = " + zoomInformation.zoomPositionCurrentBox);
        }
        if (liveviewStatus != null) {
            Log.v(TAG, "<GetEventLiveviewStatusParams>");
            Log.v(TAG, "  --> type =           " + liveviewStatus.type);
            Log.v(TAG, "  --> liveviewStatus = " + liveviewStatus.liveviewStatus);
        }
        if (liveviewOrientation != null) {
            Log.v(TAG, "<GetEventLiveviewOrientationParams>");
            Log.v(TAG, "  --> type =                " + liveviewOrientation.type);
            Log.v(TAG, "  --> liveviewOrientation = " + liveviewOrientation.liveviewOrientation);
        }
        if (takePicture != null) {
            Log.v(TAG, "<GetEventTakePictureParams[]>");
            for (int i = 0; i < takePicture.length; i++) {
                Log.v(TAG, "  --> [" + i + "]\t type = " + takePicture[i].type);
                Log.v(TAG, "  --> \t takePictureUrl = " + Arrays.toString(takePicture[i].takePictureUrl));
            }
        }
        if (continuousError != null) {
            Log.v(TAG, "<GetEventContinuousErrorParams[]>");
            for (int i2 = 0; i2 < continuousError.length; i2++) {
                Log.v(TAG, "  --> [" + i2 + "]\t type = " + continuousError[i2].type);
                Log.v(TAG, "  --> \t continuousError = " + continuousError[i2].continuousError);
                Log.v(TAG, "  --> \t isContinued =     " + continuousError[i2].isContinued);
            }
        }
        if (triggeredError != null) {
            Log.v(TAG, "<GetEventTriggeredErrorParams>");
            Log.v(TAG, "  --> type = " + triggeredError.type);
            Log.v(TAG, "  --> triggeredError = " + Arrays.toString(triggeredError.triggeredError));
        }
        if (sceneRecognition != null) {
            Log.v(TAG, "<GetEventSceneRecognitionParams>");
            Log.v(TAG, "  --> type = " + sceneRecognition.type);
            Log.v(TAG, "  --> motionRecognition = " + sceneRecognition.motionRecognition);
            Log.v(TAG, "  --> sceneRecognition  = " + sceneRecognition.sceneRecognition);
            Log.v(TAG, "  --> steadyRecognition = " + sceneRecognition.steadyRecognition);
        }
        if (formatStatus != null) {
            Log.v(TAG, "<GetEventFormatStatusParams>");
            Log.v(TAG, "  --> type = " + formatStatus.type);
            Log.v(TAG, "  --> formatResult = " + formatStatus.formatResult);
        }
        if (storageInformation != null) {
            Log.v(TAG, "<GetEventStorageInformationParams[]>");
            for (int i3 = 0; i3 < storageInformation.length; i3++) {
                Log.v(TAG, "  --> [" + i3 + "]\t type = " + storageInformation[i3].type);
                Log.v(TAG, "  --> \t continuousError =    " + storageInformation[i3].numberOfRecordableImages);
                Log.v(TAG, "  --> \t recordableTime =     " + storageInformation[i3].recordableTime);
                Log.v(TAG, "  --> \t storageDescription = " + storageInformation[i3].storageDescription);
                Log.v(TAG, "  --> \t storageID =          " + storageInformation[i3].storageID);
                Log.v(TAG, "  --> \t recordTarget =       " + storageInformation[i3].recordTarget);
            }
        }
        if (beepMode != null) {
            Log.v(TAG, "<GetEventBeepModeParams>");
            Log.v(TAG, "  --> type = " + beepMode.type);
            Log.v(TAG, "  --> currentBeepMode =    " + beepMode.currentBeepMode);
            Log.v(TAG, "  --> beepModeCandidates = " + Arrays.toString(beepMode.beepModeCandidates));
        }
        if (cameraFunction != null) {
            Log.v(TAG, "<GetEventCameraFunctionParams>");
            Log.v(TAG, "  --> type = " + cameraFunction.type);
            Log.v(TAG, "  --> currentCameraFunction =    " + cameraFunction.currentCameraFunction);
            Log.v(TAG, "  --> cameraFunctionCandidates = " + Arrays.toString(cameraFunction.cameraFunctionCandidates));
        }
        if (movieQuality != null) {
            Log.v(TAG, "<GetEventMovieQualityParams>");
            Log.v(TAG, "  --> type = " + movieQuality.type);
            Log.v(TAG, "  --> currentMovieQuality =    " + movieQuality.currentMovieQuality);
            Log.v(TAG, "  --> movieQualityCandidates = " + Arrays.toString(movieQuality.movieQualityCandidates));
        }
        if (stillSize != null) {
            Log.v(TAG, "<GetEventStillSizeParams>");
            Log.v(TAG, "  --> type = " + stillSize.type);
            Log.v(TAG, "  --> currentAspect = " + stillSize.currentAspect);
            Log.v(TAG, "  --> currentSize = " + stillSize.currentSize);
            Log.v(TAG, "  --> checkAvailability = " + stillSize.checkAvailability);
        }
        if (cameraFunctionResult != null) {
            Log.v(TAG, "<GetEventCameraFunctionResultParams>");
            Log.v(TAG, "  --> type = " + cameraFunctionResult.type);
            Log.v(TAG, "  --> cameraFunctionResult" + cameraFunctionResult.cameraFunctionResult);
        }
        if (steadyMode != null) {
            Log.v(TAG, "<GetEventSteadyModeParams>");
            Log.v(TAG, "  --> type = " + steadyMode.type);
            Log.v(TAG, "  --> currentSteadyMode = " + steadyMode.currentSteadyMode);
        }
        if (viewAngle != null) {
            Log.v(TAG, "<GetEventViewAngleParams>");
            Log.v(TAG, "  --> type = " + viewAngle.type);
            Log.v(TAG, "  --> currentViewAngle =           " + viewAngle.currentViewAngle);
            Log.v(TAG, "  --> currentViewAngleCandidates = " + Arrays.toString(viewAngle.viewAngleCandidates));
        }
        if (exposureMode != null) {
            Log.v(TAG, "<GetEventExposureModeParams>");
            Log.v(TAG, "  --> type = " + exposureMode.type);
            Log.v(TAG, "  --> currentExposureMode =    " + exposureMode.currentExposureMode);
            Log.v(TAG, "  --> exposureModeCandidates = " + Arrays.toString(exposureMode.exposureModeCandidates));
        }
        if (postviewImageSize != null) {
            Log.v(TAG, "<GetEventPostviewImageSizeParams>");
            Log.v(TAG, "  --> type = " + postviewImageSize.type);
            Log.v(TAG, "  --> currentPostviewImageSize =    " + postviewImageSize.currentPostviewImageSize);
            Log.v(TAG, "  --> postviewImageSizeCandidates = " + Arrays.toString(postviewImageSize.postviewImageSizeCandidates));
        }
        if (selfTimer != null) {
            Log.v(TAG, "<GetEventSelfTimerParams>");
            Log.v(TAG, "  --> type = " + selfTimer.type);
            Log.v(TAG, "  --> currentSelfTimer =    " + selfTimer.currentSelfTimer);
            Log.v(TAG, "  --> selfTimerCandidates = " + Arrays.toString(selfTimer.selfTimerCandidates));
        }
        if (shootMode != null) {
            Log.v(TAG, "<GetEventShootModeParams>");
            Log.v(TAG, "  --> type = " + shootMode.type);
            Log.v(TAG, "  --> currentShootMode =    " + shootMode.currentShootMode);
            Log.v(TAG, "  --> shootModeCandidates = " + Arrays.toString(shootMode.shootModeCandidates));
        }
        if (aeLock != null) {
            Log.v(TAG, "<GetEventAELockParams>");
            Log.v(TAG, "  --> type = " + aeLock.type);
            Log.v(TAG, "  --> currentAELock =    " + aeLock.currentAELock);
            Log.v(TAG, "  --> aeLockCandidates = " + Arrays.toString(aeLock.aeLockCandidates));
        }
        if (bracketShootMode != null) {
            Log.v(TAG, "<GetEventBracketShootModeParams>");
            Log.v(TAG, "  --> type = " + bracketShootMode.type);
            Log.v(TAG, "  --> currentBracketShootMode =       " + bracketShootMode.currentBracketShootMode);
            Log.v(TAG, "  --> currentBracketShootModeOption = " + bracketShootMode.currentBracketShootModeOption);
            Log.v(TAG, "  --> checkAvailability =             " + bracketShootMode.checkAvailability);
        }
        if (creativeStyle != null) {
            Log.v(TAG, "<GetEventCreativeStyleParams>");
            Log.v(TAG, "  --> type = " + creativeStyle.type);
            Log.v(TAG, "  --> currentCreativeStyle = " + creativeStyle.currentCreativeStyle);
            Log.v(TAG, "  --> currentCreativeStyleContrast = " + creativeStyle.currentCreativeStyleContrast);
            Log.v(TAG, "  --> currentCreativeStyleSaturation = " + creativeStyle.currentCreativeStyleSaturation);
            Log.v(TAG, "  --> currentCreativeStyleSharpness = " + creativeStyle.currentCreativeStyleSharpness);
            Log.v(TAG, "  --> checkAvailability = " + creativeStyle.checkAvailability);
        }
        if (exposureCompensation != null) {
            Log.v(TAG, "<GetEventExposureCompensationParams>");
            Log.v(TAG, "  --> type = " + exposureCompensation.type);
            Log.v(TAG, "  --> currentExposureCompensation =     " + exposureCompensation.currentExposureCompensation);
            Log.v(TAG, "  --> maxExposureCompensation =         " + exposureCompensation.maxExposureCompensation);
            Log.v(TAG, "  --> minExposureCompensation =         " + exposureCompensation.minExposureCompensation);
            Log.v(TAG, "  --> stepIndexOfExposureCompensation = " + exposureCompensation.stepIndexOfExposureCompensation);
        }
        if (flashMode != null) {
            Log.v(TAG, "<GetEventFlashModeParams>");
            Log.v(TAG, "  --> type = " + flashMode.type);
            Log.v(TAG, "  --> currentFlashMode = " + flashMode.currentFlashMode);
            Log.v(TAG, "  --> flashModeCandidates = " + Arrays.toString(flashMode.flashModeCandidates));
        }
        if (fNumber != null) {
            Log.v(TAG, "<GetEventFNumberParams>");
            Log.v(TAG, "  --> type = " + fNumber.type);
            Log.v(TAG, "  --> currentFNumber = " + fNumber.currentFNumber);
            Log.v(TAG, "  --> fNumberCandidates = " + Arrays.toString(fNumber.fNumberCandidates));
        }
        if (focusMode != null) {
            Log.v(TAG, "<GetEventFocusModeParams>");
            Log.v(TAG, "  --> type = " + focusMode.type);
            Log.v(TAG, "  --> currentFocusMode = " + focusMode.currentFocusMode);
            Log.v(TAG, "  --> focusModeCandidates = " + Arrays.toString(focusMode.focusModeCandidates));
        }
        if (isoSpeedRate != null) {
            Log.v(TAG, "<GetEventIsoSpeedRateParams>");
            Log.v(TAG, "  --> type = " + isoSpeedRate.type);
            Log.v(TAG, "  --> currentIsoSpeedRate =    " + isoSpeedRate.currentIsoSpeedRate);
            Log.v(TAG, "  --> isoSpeedRateCandidates = " + Arrays.toString(isoSpeedRate.isoSpeedRateCandidates));
        }
        if (pictureEffect != null) {
            Log.v(TAG, "<GetEventPictureEffectParams>");
            Log.v(TAG, "  --> type = " + pictureEffect.type);
            Log.v(TAG, "  --> currentPictureEffect =       " + pictureEffect.currentPictureEffect);
            Log.v(TAG, "  --> currentPictureEffectOption = " + pictureEffect.currentPictureEffectOption);
            Log.v(TAG, "  --> checkAvailability =          " + pictureEffect.checkAvailability);
        }
        if (programShift != null) {
            Log.v(TAG, "<GetEventProgramShiftParams>");
            Log.v(TAG, "  --> type = " + programShift.type);
            Log.v(TAG, "  --> isShifted = " + programShift.isShifted);
        }
        if (shutterSpeed != null) {
            Log.v(TAG, "<GetEventShutterSpeedParams>");
            Log.v(TAG, "  --> type = " + shutterSpeed.type);
            Log.v(TAG, "  --> currentShutterSpeed =    " + shutterSpeed.currentShutterSpeed);
            Log.v(TAG, "  --> shutterSpeedCandidates = " + Arrays.toString(shutterSpeed.shutterSpeedCandidates));
        }
        if (whiteBalance != null) {
            Log.v(TAG, "<GetEventWhiteBalanceParams>");
            Log.v(TAG, "  --> type = " + whiteBalance.type);
            Log.v(TAG, "  --> currentColorTemperature = " + whiteBalance.currentColorTemperature);
            Log.v(TAG, "  --> currentWhiteBalanceMode = " + whiteBalance.currentWhiteBalanceMode);
            Log.v(TAG, "  --> checkAvailability =       " + whiteBalance.checkAvailability);
        }
        if (touchAFPosition != null) {
            Log.v(TAG, "<GetEventTouchAFPositionParams>");
            Log.v(TAG, "  --> type = " + touchAFPosition.type);
            Log.v(TAG, "  --> currentSet =              " + touchAFPosition.currentSet);
            Log.v(TAG, "  --> currentTouchCoordinates = " + Arrays.toString(touchAFPosition.currentTouchCoordinates));
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getEvent(boolean isPolling, GetEventCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init(false);
                    GetEventAdapter adapter = GetEventAdapter.getInstance();
                    String methodName = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + methodName + "(isPolling=" + isPolling + LogHelper.MSG_CLOSE_BRACKET);
                    ServerEventHandler.Status status = ServerEventHandler.Status.SUCCESS;
                    if (isPolling) {
                        ServerEventHandler eventHandler = ServerEventHandler.getInstance();
                        if (eventHandler.isStatusChanged()) {
                            eventHandler.setStatusChanged(false);
                        } else {
                            status = eventHandler.startWaiting();
                        }
                    }
                    if (ServerEventHandler.Status.SUCCESS.equals(status)) {
                        GetEventAvailableApiListParams availableApiList = GetEventAdapter.getInstance().getAvailableApiList(isPolling);
                        GetEventCameraStatusParams cameraStatus = GetEventAdapter.getInstance().getServerStatus(isPolling);
                        GetEventLiveviewStatusParams liveviewStatus = GetEventAdapter.getInstance().getLiveviewStatus(isPolling);
                        GetEventTakePictureParams[] takePicture = GetEventAdapter.getInstance().getTakePicture(isPolling);
                        GetEventExposureModeParams exposureMode = GetEventAdapter.getInstance().getExposureModeParams(isPolling);
                        GetEventPostviewImageSizeParams postviewImageSize = GetEventAdapter.getInstance().getPostviewImageSize(isPolling);
                        GetEventSelfTimerParams selfTimer = GetEventAdapter.getInstance().getSelfTimer(isPolling);
                        GetEventShootModeParams shootMode = GetEventAdapter.getInstance().getShootModeParams(isPolling);
                        GetEventExposureCompensationParams exposureCompensation = GetEventAdapter.getInstance().getExposureCompensation(isPolling);
                        GetEventFNumberParams fNumber = GetEventAdapter.getInstance().getFNumber(isPolling);
                        GetEventIsoSpeedRateParams isoSpeedRate = GetEventAdapter.getInstance().getIsoSpeedRateParams(isPolling);
                        GetEventProgramShiftParams programShift = GetEventAdapter.getInstance().getProgramShift(isPolling);
                        GetEventShutterSpeedParams shutterSpeed = GetEventAdapter.getInstance().getShutterSpeed(isPolling);
                        GetEventWhiteBalanceParams whiteBalance = GetEventAdapter.getInstance().getWhiteBalanceParams(isPolling);
                        GetEventTouchAFPositionParams touchAFPosition = GetEventAdapter.getInstance().getTouchAFPosition(isPolling);
                        GetEventFlashModeParams flashMode = GetEventAdapter.getInstance().getFlashModeParams(isPolling);
                        GetEventZoomInformationParams zoomInformation = GetEventAdapter.getInstance().getZoomInformation(isPolling);
                        GetEventStorageInformationParams[] storageInformation = adapter.getStorageInformationParams(isPolling);
                        GetEventContinuousErrorParams[] continuousError = s_DUMMY_PARAM_ARRAY_CONTINUOUS_ERROR;
                        GetEventCameraFunctionParams cameraFunction = adapter.getCameraFunction(isPolling);
                        GetEventCameraFunctionResultParams cameraFunctionResult = adapter.getCameraFunctionResult(isPolling);
                        GetEventFocusModeParams focusMode = GetEventAdapter.getInstance().getFocusModeParams(isPolling);
                        printGetEventData(availableApiList, cameraStatus, zoomInformation, liveviewStatus, null, takePicture, continuousError, null, null, null, storageInformation, null, cameraFunction, null, null, cameraFunctionResult, null, null, exposureMode, postviewImageSize, selfTimer, shootMode, null, null, null, exposureCompensation, flashMode, fNumber, focusMode, isoSpeedRate, null, programShift, shutterSpeed, whiteBalance, touchAFPosition);
                        returnCb.returnCb(availableApiList, cameraStatus, zoomInformation, liveviewStatus, null, takePicture, continuousError, null, null, null, storageInformation, null, cameraFunction, null, null, cameraFunctionResult, null, null, exposureMode, postviewImageSize, selfTimer, shootMode, null, null, null, exposureCompensation, flashMode, fNumber, focusMode, isoSpeedRate, null, programShift, shutterSpeed, whiteBalance, touchAFPosition);
                    } else if (ServerEventHandler.Status.CANCELED.equals(status)) {
                        returnCb.handleStatus(StatusCode.ALREADY_RUNNING_POLLING.toInt(), "DoublePolling");
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setTouchAFPosition(double x, double y, SetTouchAFPositionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setTouchAFPosition(x=" + x + ", y=" + y + ", callback)");
                    StateController stateController = StateController.getInstance();
                    StateController.AppCondition previousAppCondition = stateController.getAppCondition();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        stateController.setAppCondition(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF);
                        TouchAFPositionParams position = CameraProxyTouchAFPosition.set(x, y);
                        if (position != null) {
                            Log.e("CHECK", "position: " + position.AFResult + "," + position.AFType);
                            returnCb.returnCb(0, position);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Touch AF operation failed.");
                            stateController.setAppCondition(previousAppCondition);
                        }
                    } else {
                        Log.v(TAG, "Failed to start touch AF because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                        stateController.setAppCondition(previousAppCondition);
                    }
                    Log.e(TAG, "<=== setTouchAFPosition(x=" + x + ", y=" + y + ", callback)");
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getTouchAFPosition(GetTouchAFPositionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getTouchAFPosition()");
                    StateController stateController = StateController.getInstance();
                    StateController.AppCondition previousAppCondition = stateController.getAppCondition();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        TouchAFCurrentPositionParams pos = CameraProxyTouchAFPosition.get();
                        if (pos != null) {
                            returnCb.returnCb(pos);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot get the touch AF position.");
                        }
                    } else {
                        Log.v(TAG, "Failed to start touch AF because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                        stateController.setAppCondition(previousAppCondition);
                    }
                    Log.e(TAG, "<=== getTouchAFPosition()");
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void cancelTouchAFPosition(CancelTouchAFPositionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> cancelTouchAFPosition()");
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        CameraProxyTouchAFPosition.cancel();
                        returnCb.returnCb();
                    } else {
                        Log.v(TAG, "Failed to cancel touch AF because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== cancelTouchAFPosition()");
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setFNumber(String fnumber, SetFNumberCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setFNumber(fnumber=" + fnumber + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyFNumber.set(fnumber);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setFNumber() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getFNumber(GetFNumberCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getFNumber()");
                    boolean DEBUGSUCCEEDED = false;
                    String fNumber = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        fNumber = CameraProxyFNumber.get();
                        if (fNumber != null) {
                            returnCb.returnCb(fNumber);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getFNumber(fNumber=");
                    if (!DEBUGSUCCEEDED || fNumber == null) {
                        fNumber = "n/a";
                    }
                    Log.e(str, append.append(fNumber).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableFNumber(GetAvailableFNumberCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableFNumber()");
                    boolean DEBUG_SUCCEEDED = false;
                    String fNumber = null;
                    String[] availableFNumber = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean error = true;
                        fNumber = CameraProxyFNumber.get();
                        if (fNumber != null && (availableFNumber = CameraProxyFNumber.getAvailable()) != null) {
                            error = false;
                            returnCb.returnCb(fNumber, availableFNumber);
                        }
                        if (error) {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableFNumber(fNumber=");
                    if (!DEBUG_SUCCEEDED || fNumber == null) {
                        fNumber = "n/a";
                    }
                    Log.e(str, append.append(fNumber).append(", available=").append((!DEBUG_SUCCEEDED || availableFNumber == null) ? "n/a" : availableFNumber.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedFNumber(GetSupportedFNumberCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getSupportedFNumber()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supportedFNumber = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        supportedFNumber = CameraProxyFNumber.getSupported();
                        if (supportedFNumber != null) {
                            returnCb.returnCb(supportedFNumber);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== getSupportedFNumber(supported=" + ((!DEBUG_SUCCEEDED || supportedFNumber == null) ? "n/a" : supportedFNumber.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setShutterSpeed(String shutterSpeed, SetShutterSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setShutterSpeed(shutterSpeed=" + shutterSpeed + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyShutterSpeed.set(shutterSpeed);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setShutterSpeed() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getShutterSpeed(GetShutterSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getShutterSpeed()");
                    boolean DEBUGSUCCEEDED = false;
                    String shutterSpeed = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        shutterSpeed = CameraProxyShutterSpeed.get();
                        if (shutterSpeed != null) {
                            returnCb.returnCb(shutterSpeed);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getShutterSpeed(shutterSpeed=");
                    if (!DEBUGSUCCEEDED || shutterSpeed == null) {
                        shutterSpeed = "n/a";
                    }
                    Log.e(str, append.append(shutterSpeed).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableShutterSpeed(GetAvailableShutterSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableShutterSpeed()");
                    boolean DEBUG_SUCCEEDED = false;
                    String shutterSpeed = null;
                    String[] availableShutterSpeed = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean error = true;
                        shutterSpeed = CameraProxyShutterSpeed.get();
                        if (shutterSpeed != null && (availableShutterSpeed = CameraProxyShutterSpeed.getAvailable()) != null) {
                            error = false;
                            returnCb.returnCb(shutterSpeed, availableShutterSpeed);
                        }
                        if (error) {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableShutterSpeed(shutterSpeed=");
                    if (!DEBUG_SUCCEEDED || shutterSpeed == null) {
                        shutterSpeed = "n/a";
                    }
                    Log.e(str, append.append(shutterSpeed).append(", available=").append((!DEBUG_SUCCEEDED || availableShutterSpeed == null) ? "n/a" : availableShutterSpeed.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedShutterSpeed(GetSupportedShutterSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getSupportedShutterSpeed()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supportedShutterSpeed = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        supportedShutterSpeed = CameraProxyShutterSpeed.getSupported();
                        if (supportedShutterSpeed != null) {
                            returnCb.returnCb(supportedShutterSpeed);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== getSupportedShutterSpeed(supported=" + ((!DEBUG_SUCCEEDED || supportedShutterSpeed == null) ? "n/a" : supportedShutterSpeed.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setIsoSpeedRate(String iso, SetIsoSpeedRateCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.e(TAG, "---> " + thisMethod + "(iso=" + iso + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean check = CameraProxyIsoNumber.set(iso);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getIsoSpeedRate(GetIsoSpeedRateCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    String isoNumber = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        isoNumber = CameraProxyIsoNumber.get();
                        if (isoNumber != null) {
                            returnCb.returnCb(isoNumber);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(iso=");
                    if (isoNumber == null) {
                        isoNumber = "n/a";
                    }
                    Log.v(str, append.append(isoNumber).append(") : DEBUGSUCCEEDED=").append(DEBUGSUCCEEDED).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableIsoSpeedRate(GetAvailableIsoSpeedRateCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String isoNumber = null;
                    String[] availableIsoNumber = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean error = true;
                        isoNumber = CameraProxyIsoNumber.get();
                        if (isoNumber != null && (availableIsoNumber = CameraProxyIsoNumber.getAvailable()) != null) {
                            error = false;
                            returnCb.returnCb(isoNumber, availableIsoNumber);
                        }
                        if (error) {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(iso=");
                    if (!DEBUG_SUCCEEDED || isoNumber == null) {
                        isoNumber = "n/a";
                    }
                    Log.v(str, append.append(isoNumber).append(", available=").append((!DEBUG_SUCCEEDED || availableIsoNumber == null) ? "n/a" : availableIsoNumber.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedIsoSpeedRate(GetSupportedIsoSpeedRateCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supportedIsoNumber = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supportedIsoNumber = CameraProxyIsoNumber.getSupported();
                        if (supportedIsoNumber != null) {
                            returnCb.returnCb(supportedIsoNumber);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + ((!DEBUG_SUCCEEDED || supportedIsoNumber == null) ? "n/a" : supportedIsoNumber.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setExposureMode(String mode, SetExposureModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.e(TAG, "---> " + thisMethod + "(mode=" + (mode != null ? mode : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean check = CameraProxyExposureMode.set(mode);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getExposureMode(GetExposureModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    String mode = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        mode = CameraProxyExposureMode.get();
                        if (mode != null) {
                            returnCb.returnCb(mode);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot get camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(mode=" + mode + ") : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableExposureMode(GetAvailableExposureModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String mode = null;
                    String[] available = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean error = true;
                        mode = CameraProxyExposureMode.get();
                        if (mode != null && (available = CameraProxyExposureMode.getAvailable()) != null) {
                            error = false;
                            returnCb.returnCb(mode, available);
                        }
                        if (error) {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot get camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(mode=");
                    if (!DEBUG_SUCCEEDED || mode == null) {
                        mode = "n/a";
                    }
                    Log.v(str, append.append(mode).append(", available=").append((!DEBUG_SUCCEEDED || available == null) ? "n/a" : available.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedExposureMode(GetSupportedExposureModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supported = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supported = CameraProxyExposureMode.getSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + ((!DEBUG_SUCCEEDED || supported == null) ? "n/a" : supported.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startLiveviewWithSize(String size, StartLiveviewCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.e(TAG, "---> " + thisMethod + "(size=" + size + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUG_SUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean error = false;
                        if (LiveviewLoader.isLoadingPreview()) {
                            Log.d(TAG, "Failed: Liveview already started.");
                            String currentSize = CameraProxyLiveviewSize.get();
                            if (!currentSize.equals(size)) {
                                String message = "New size(=" + size + ") differs from the current(=" + currentSize + ").";
                                Log.d(TAG, message);
                                returnCb.handleStatus(StatusCode.ILLEGAL_ARGUMENT.toInt(), message);
                                error = true;
                            }
                        } else {
                            CameraProxyLiveviewSize.set(size);
                            boolean check = LiveviewLoader.startObtainingImages();
                            if (!check) {
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Liveview cannot start.");
                                error = true;
                            }
                        }
                        if (!error) {
                            returnCb.returnCb(SRCtrlConstants.LIVEVIEW_URL);
                            DEBUG_SUCCEEDED = true;
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUG_SUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getLiveviewSize(GetLiveviewSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    String size = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        size = CameraProxyLiveviewSize.get();
                        returnCb.returnCb(size);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED + ", size=" + size);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableLiveviewSize(GetAvailableLiveviewSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String size = null;
                    String[] available = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        size = CameraProxyLiveviewSize.get();
                        available = CameraProxyLiveviewSize.getAvailable();
                        returnCb.returnCb(size, available);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(size=");
                    if (!DEBUG_SUCCEEDED || size == null) {
                        size = "n/a";
                    }
                    Log.v(str, append.append(size).append(", available=").append((!DEBUG_SUCCEEDED || available == null) ? "n/a" : Arrays.toString(available)).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedLiveviewSize(GetSupportedLiveviewSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supported = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supported = CameraProxyLiveviewSize.getSupported();
                        returnCb.returnCb(supported);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + ((!DEBUG_SUCCEEDED || supported == null) ? "n/a" : supported.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setPostviewImageSize(String size, SetPostviewImageSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean check = CameraProxyPostviewImageSize.set(size);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Failed: couldn't set postview size.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(size=" + size + ") : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getPostviewImageSize(GetPostviewImageSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    String size = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        size = CameraProxyPostviewImageSize.get();
                        returnCb.returnCb(size);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("() : DEBUGSUCCEEDED=").append(DEBUGSUCCEEDED).append(", size=");
                    if (size == null) {
                        size = "n/a";
                    }
                    Log.v(str, append.append(size).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailablePostviewImageSize(GetAvailablePostviewImageSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String size = null;
                    String[] available = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        size = CameraProxyPostviewImageSize.get();
                        available = CameraProxyPostviewImageSize.getAvailable();
                        returnCb.returnCb(size, available);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(size=");
                    if (!DEBUG_SUCCEEDED || size == null) {
                        size = "n/a";
                    }
                    Log.v(str, append.append(size).append(", available=").append((!DEBUG_SUCCEEDED || available == null) ? "n/a" : available.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedPostviewImageSize(GetSupportedPostviewImageSizeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supported = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supported = CameraProxyPostviewImageSize.getSupported();
                        returnCb.returnCb(supported);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUG_SUCCEEDED ? supported.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setProgramShift(int step, SetProgramShiftCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setProgramShift(step=" + step + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean[] result = CameraProxyProgramShift.set(step);
                        if (result == null || true != result[0]) {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        } else {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        }
                    }
                    Log.e(TAG, "<=== setProgramShift() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedProgramShift(GetSupportedProgramShiftCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getSupportedProgramShift()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        int[] stepRange = CameraProxyProgramShift.getSupported();
                        DEBUGSUCCEEDED = true;
                        returnCb.returnCb(stepRange);
                    }
                    Log.e(TAG, "<=== getSupportedProgramShift() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setWhiteBalance(String whiteBalanceMode, boolean colorTemperatureEnabled, int colorTemperature, SetWhiteBalanceCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        WhiteBalanceParams param = new WhiteBalanceParams();
                        param.whiteBalanceMode = whiteBalanceMode;
                        param.colorTemperature = Integer.valueOf(colorTemperature);
                        boolean check = CameraProxyWhiteBalance.set(param, colorTemperatureEnabled);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Failed: couldn't set white balance.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== ").append(thisMethod).append("(mode=");
                    if (whiteBalanceMode == null) {
                        whiteBalanceMode = "n/a";
                    }
                    Log.v(str, append.append(whiteBalanceMode).append(") : DEBUGSUCCEEDED=").append(DEBUGSUCCEEDED).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getWhiteBalance(GetWhiteBalanceCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    WhiteBalanceParams param = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        param = CameraProxyWhiteBalance.get();
                        if (param != null) {
                            returnCb.returnCb(param);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED + ", param=" + param);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableWhiteBalance(GetAvailableWhiteBalanceCallback returnCb) {
        WhiteBalanceParamCandidate[] available;
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    WhiteBalanceParams param = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        boolean error = true;
                        param = CameraProxyWhiteBalance.get();
                        if (param != null && (available = CameraProxyWhiteBalance.getAvailable()) != null) {
                            error = false;
                            returnCb.returnCb(param, available);
                        }
                        if (error) {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + ((!DEBUG_SUCCEEDED || param == null) ? "n/a" : param.whiteBalanceMode) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedWhiteBalance(GetSupportedWhiteBalanceCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    WhiteBalanceParamCandidate[] supported = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supported = CameraProxyWhiteBalance.getSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUG_SUCCEEDED ? supported.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setShootMode(String mode, SetShootModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setShootMode(shootMode=" + mode + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyShootMode.set(mode);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setShootMode() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getShootMode(GetShootModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getShootMode()");
                    boolean DEBUGSUCCEEDED = false;
                    String shootMode = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        shootMode = CameraProxyShootMode.get();
                        returnCb.returnCb(shootMode);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getShootMode(ShootMode=");
                    if (!DEBUGSUCCEEDED || shootMode == null) {
                        shootMode = "n/a";
                    }
                    Log.e(str, append.append(shootMode).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableShootMode(GetAvailableShootModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableShootMode()");
                    boolean DEBUG_SUCCEEDED = false;
                    String shootMode = null;
                    String[] availableShootModes = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        shootMode = CameraProxyShootMode.get();
                        availableShootModes = CameraProxyShootMode.getAvailable();
                        returnCb.returnCb(shootMode, availableShootModes);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableShootMode(shootMode=");
                    if (!DEBUG_SUCCEEDED || shootMode == null) {
                        shootMode = "n/a";
                    }
                    Log.e(str, append.append(shootMode).append(", available=").append((!DEBUG_SUCCEEDED || availableShootModes == null) ? "n/a" : availableShootModes.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedShootMode(GetSupportedShootModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getSupportedShootMode()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supportedShootModes = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        supportedShootModes = CameraProxyShootMode.getSupported();
                        returnCb.returnCb(supportedShootModes);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== getSupportedShootMode(supported=" + (DEBUG_SUCCEEDED ? supportedShootModes.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setFlashMode(String flash, SetFlashModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setFlashMode(flashMode=" + flash + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyFlashMode.set(flash);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setFlashMode() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getFlashMode(GetFlashModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getFlashMode()");
                    boolean DEBUGSUCCEEDED = false;
                    String flashMode = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        flashMode = CameraProxyFlashMode.get();
                        returnCb.returnCb(flashMode);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getFlashMode(FlashMode=");
                    if (!DEBUGSUCCEEDED || flashMode == null) {
                        flashMode = "n/a";
                    }
                    Log.e(str, append.append(flashMode).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableFlashMode(GetAvailableFlashModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableFlashMode()");
                    boolean DEBUG_SUCCEEDED = false;
                    String flashMode = null;
                    String[] availableflashModes = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        flashMode = CameraProxyFlashMode.get();
                        availableflashModes = CameraProxyFlashMode.getAvailable();
                        returnCb.returnCb(flashMode, availableflashModes);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableFlashMode(flashMode=");
                    if (!DEBUG_SUCCEEDED || flashMode == null) {
                        flashMode = "n/a";
                    }
                    Log.e(str, append.append(flashMode).append(", available=").append((!DEBUG_SUCCEEDED || availableflashModes == null) ? "n/a" : availableflashModes.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedFlashMode(GetSupportedFlashModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    String[] supported = null;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        supported = CameraProxyFlashMode.getSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUG_SUCCEEDED ? supported.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void actZoom(String direction, String movement, ActZoomCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> actZoom(direction=" + direction + ", movement=" + movement + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyZoom.actZoom(direction, movement);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== actZoom() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startMovieRec(StartMovieRecCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> startMovieRec()");
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        ShootingHandler.ShootingStatus currentStatus = sHandler.startMovieRec();
                        if (ShootingHandler.ShootingStatus.PROCESSING.equals(currentStatus)) {
                            Log.v(TAG, "SUCCESS: Movie Recording start.");
                            returnCb.returnCb(0);
                        } else {
                            Log.v(TAG, "FAILED: Movie Recording start.");
                            returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Movie Rec Start Error");
                        }
                    } else {
                        Log.e(TAG, "Failed to start Movie Rec because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void stopMovieRec(StopMovieRecCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> stopMovieRec()");
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        boolean result = sHandler.stopMovieRec();
                        if (result) {
                            Log.v(TAG, "SUCCESS: Movie Recording stop.");
                            returnCb.returnCb("");
                        } else {
                            Log.v(TAG, "FAILED: Movie Recording stop.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Movie Rec Stop Error");
                        }
                    } else {
                        Log.e(TAG, "Failed to stop Movie Rec because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setFocusMode(String focusMode, SetFocusModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setFocusMode(focusMode=" + focusMode + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyFocusMode.set(focusMode);
                        if (check) {
                            returnCb.returnCb(0);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setFocusMode() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getFocusMode(GetFocusModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getFocusMode()");
                    boolean DEBUGSUCCEEDED = false;
                    String forcusMode = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        forcusMode = CameraProxyFocusMode.get();
                        returnCb.returnCb(forcusMode);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getFocusMode(ForcusMode=");
                    if (!DEBUGSUCCEEDED || forcusMode == null) {
                        forcusMode = "n/a";
                    }
                    Log.e(str, append.append(forcusMode).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedFocusMode(GetSupportedFocusModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    String[] supported = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        supported = CameraProxyFocusMode.getSupported();
                        if (supported != null) {
                            returnCb.returnCb(supported);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUGSUCCEEDED ? supported.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableFocusMode(GetAvailableFocusModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableFocusMode()");
                    boolean DEBUGSUCCEEDED = false;
                    String focusMode = null;
                    String[] availableFocusModes = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        focusMode = CameraProxyFocusMode.get();
                        availableFocusModes = CameraProxyFocusMode.getAvailable();
                        returnCb.returnCb(focusMode, availableFocusModes);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableFocusMode(focusMode=");
                    if (!DEBUGSUCCEEDED || focusMode == null) {
                        focusMode = "n/a";
                    }
                    Log.e(str, append.append(focusMode).append(", available=").append((!DEBUGSUCCEEDED || availableFocusModes == null) ? "n/a" : availableFocusModes.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setZoomSetting(ZoomSettingParams zoomSetting, SetZoomSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setZoomSetting(zoomSetting=" + zoomSetting + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyZoomSetting.set(zoomSetting.zoom);
                        if (check) {
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setZoomSetting() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getZoomSetting(GetZoomSettingCallback getZoomSettingCallback) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getZoomSetting()");
                    boolean z = false;
                    ZoomSettingParams zoomSettingParams = new ZoomSettingParams();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        zoomSettingParams.zoom = CameraProxyZoomSetting.get();
                        getZoomSettingCallback.returnCb(zoomSettingParams);
                        z = true;
                    } else {
                        getZoomSettingCallback.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getZoomSetting(ZoomSetting=");
                    Object obj = zoomSettingParams;
                    if (!z || zoomSettingParams == null) {
                        obj = "n/a";
                    }
                    Log.e(str, append.append(obj).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    getZoomSettingCallback.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    getZoomSettingCallback.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedZoomSetting(GetSupportedZoomSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    ZoomSettingSupportedParams zoomSettingCandidates = new ZoomSettingSupportedParams();
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        zoomSettingCandidates.candidate = CameraProxyZoomSetting.getSupported();
                        if (zoomSettingCandidates.candidate != null) {
                            returnCb.returnCb(zoomSettingCandidates);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(candidate=" + (DEBUG_SUCCEEDED ? zoomSettingCandidates.candidate.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableZoomSetting(GetAvailableZoomSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableZoomSetting()");
                    boolean DEBUG_SUCCEEDED = false;
                    ZoomSettingAvailableParams availableZoomSetting = new ZoomSettingAvailableParams();
                    String[] availablezoomSettings = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        availableZoomSetting.zoom = CameraProxyZoomSetting.get();
                        availableZoomSetting.candidate = CameraProxyZoomSetting.getAvailable();
                        returnCb.returnCb(availableZoomSetting);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getAvailableZoomSetting(zoomSetting=");
                    String zoomSetting = (!DEBUG_SUCCEEDED || 0 == 0) ? "n/a" : null;
                    Log.e(str, append.append(zoomSetting).append(", available=").append((!DEBUG_SUCCEEDED || 0 == 0) ? "n/a" : availablezoomSettings.toString()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setContShootingMode(ContShootingModeParams contShootingMode, SetContShootingModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyContShootingMode.set(contShootingMode.contShootingMode);
                        if (check) {
                            returnCb.returnCb();
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getContShootingMode(GetContShootingModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingModeParams currentMode = new ContShootingModeParams();
                        currentMode.contShootingMode = CameraProxyContShootingMode.get();
                        returnCb.returnCb(currentMode);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedContShootingMode(GetSupportedContShootingModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingModeSupportedParams supportedMode = new ContShootingModeSupportedParams();
                        supportedMode.candidate = CameraProxyContShootingMode.getSupported();
                        returnCb.returnCb(supportedMode);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableContShootingMode(GetAvailableContShootingModeCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingModeAvailableParams availableMode = new ContShootingModeAvailableParams();
                        availableMode.contShootingMode = CameraProxyContShootingMode.get();
                        availableMode.candidate = CameraProxyContShootingMode.getAvailable();
                        returnCb.returnCb(availableMode);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setContShootingSpeed(ContShootingSpeedParams contShootingSpeed, SetContShootingSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxyContShootingSpeed.set(contShootingSpeed.contShootingSpeed);
                        if (check) {
                            returnCb.returnCb();
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getContShootingSpeed(GetContShootingSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingSpeedParams currentSpeed = new ContShootingSpeedParams();
                        currentSpeed.contShootingSpeed = CameraProxyContShootingSpeed.get();
                        returnCb.returnCb(currentSpeed);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedContShootingSpeed(GetSupportedContShootingSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingSpeedSupportedParams supportedSpeed = new ContShootingSpeedSupportedParams();
                        supportedSpeed.candidate = CameraProxyContShootingSpeed.getSupported();
                        returnCb.returnCb(supportedSpeed);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableContShootingSpeed(GetAvailableContShootingSpeedCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ContShootingSpeedAvailableParams availableSpeed = new ContShootingSpeedAvailableParams();
                        availableSpeed.contShootingSpeed = CameraProxyContShootingSpeed.get();
                        availableSpeed.candidate = CameraProxyContShootingSpeed.getAvailable();
                        returnCb.returnCb(availableSpeed);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setLiveviewFrameInfo(LiveviewFrameInfoParams liveviewFrameInfo, SetLiveviewFrameInfoCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        CameraProxyLiveviewFrameInfo.set(liveviewFrameInfo.frameInfo.booleanValue());
                        returnCb.returnCb();
                        DEBUGSUCCEEDED = true;
                    } else {
                        Log.e(TAG, "Failed to set Liveview FrameInfo because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getLiveviewFrameInfo(GetLiveviewFrameInfoCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        LiveviewFrameInfoParams current = new LiveviewFrameInfoParams();
                        current.frameInfo = Boolean.valueOf(CameraProxyLiveviewFrameInfo.get());
                        returnCb.returnCb(current);
                        DEBUGSUCCEEDED = true;
                    } else {
                        Log.e(TAG, "Failed to get Liveview FrameInfo because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startContShooting(StartContShootingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Log.v(TAG, apiCallLog2.getMethodName() + " AppCondition : " + StateController.getInstance().getAppCondition());
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        ShootingHandler.ShootingStatus currentStatus = sHandler.startContShooting();
                        if (ShootingHandler.ShootingStatus.PROCESSING.equals(currentStatus) || ShootingHandler.ShootingStatus.DEVELOPING.equals(currentStatus)) {
                            Log.v(TAG, "SUCCESS(PROCESSING\u3000or DEVELOPING): contShooting is Start.");
                            returnCb.returnCb();
                        } else if (ShootingHandler.ShootingStatus.FAILED.equals(currentStatus)) {
                            Log.v(TAG, "FAILURE: ContShooting failed: " + sHandler.getErrorStatus().toString());
                            returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Shooting Failed");
                        } else if (ShootingHandler.ShootingStatus.READY.equals(currentStatus)) {
                            Log.e(TAG, "FAILURE: Unexpected READY state after shooting.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unexpected state");
                        } else {
                            Log.e(TAG, "FAILURE: Unknown Error. status = " + currentStatus);
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                        }
                    } else {
                        Log.e(TAG, "Failed to start taking picture because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void stopContShooting(StopContShootingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        sHandler.stopContShooting();
                        returnCb.returnCb();
                    } else {
                        Log.e(TAG, "Method call of " + apiCallLog2.getMethodName() + " was Not Available.");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void actHalfPressShutter(ActHalfPressShutterCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        CameraProxyHalfPressShutter.action();
                        returnCb.returnCb();
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_REQUEST.toInt(), "Unexpected API call");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                    }
                } catch (Exception e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "error : " + e.getMessage());
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void cancelHalfPressShutter(CancelHalfPressShutterCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        CameraProxyHalfPressShutter.cancel();
                        returnCb.returnCb();
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_REQUEST.toInt(), "Unexpected API call");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                    }
                } catch (Exception e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "error : " + e.getMessage());
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getStorageInformation(GetStorageInformationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getStorageInformation()");
                    boolean DEBUGSUCCEEDED = false;
                    StorageInformationParams[] storageInformationParams = null;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        storageInformationParams = CameraProxyStorageInformation.get();
                        returnCb.returnCb(storageInformationParams);
                        DEBUGSUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    StringBuffer sb = new StringBuffer();
                    if (storageInformationParams != null) {
                        StorageInformationParams[] arr$ = storageInformationParams;
                        for (StorageInformationParams param : arr$) {
                            sb.append("[").append(param.storageID).append(" ,").append(param.recordTarget).append(" ,");
                            sb.append(param.numberOfRecordableImages).append(" ,").append(param.recordableTime).append(" ,");
                            sb.append(param.storageDescription).append("] ");
                        }
                    }
                    Log.e(TAG, "<=== getStorageInformation(storageInformationParams=" + ((!DEBUGSUCCEEDED || storageInformationParams == null) ? "n/a" : sb.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setCameraFunction(String cameraFunction, SetCameraFunctionCallback returnCb) {
        Log.v(TAG, Name.SET_CAMERA_FUNCTION);
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.v(TAG, "---> " + apiCallLog2.getMethodName() + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (!AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Log.e(TAG, "Failed to setCameraFunction because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    } else if (cameraFunction.equals(SRCtrlConstants.CAMERA_FUNCTION_REMOTE_SHOOTING)) {
                        SetCameraFunctionHandler.SetCameraFuncStatus status = SetCameraFunctionHandler.getInstance().goToShootingState(false);
                        switch (status) {
                            case SUCCESS:
                                DEBUGSUCCEEDED = true;
                                returnCb.returnCb(0);
                                break;
                            case SAME_MODE:
                                returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Illegal state");
                                break;
                            default:
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                                break;
                        }
                    } else if (!cameraFunction.equals(SRCtrlConstants.CAMERA_FUNCTION_CONTENTS_TRANSFER)) {
                        String message = "cameraFunction = " + cameraFunction + " .";
                        Log.v(TAG, message);
                        returnCb.handleStatus(StatusCode.ILLEGAL_ARGUMENT.toInt(), message);
                    } else if (MediaNotificationManager.getInstance().isMounted()) {
                        SetCameraFunctionHandler.SetCameraFuncStatus status2 = SetCameraFunctionHandler.getInstance().goToPlaybackState();
                        switch (status2) {
                            case SUCCESS:
                                DEBUGSUCCEEDED = true;
                                returnCb.returnCb(0);
                                break;
                            case SAME_MODE:
                                returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Illegal state");
                                break;
                            default:
                                returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                                break;
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "media is not mounted.");
                    }
                    Log.e(TAG, "<=== " + apiCallLog2.getMethodName() + "() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getCameraFunction(GetCameraFunctionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
                        String currentCameraFunction = (appCondition.equals(StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER) || appCondition.equals(StateController.AppCondition.PLAYBACK_STREAMING) || appCondition.equals(StateController.AppCondition.PLAYBACK_DELETING)) ? SRCtrlConstants.CAMERA_FUNCTION_CONTENTS_TRANSFER : StateController.AppCondition.PREPARATION.equals(appCondition) ? SRCtrlConstants.CAMERA_FUNCTION_REMOTE_OTHER_FUNCTION : SRCtrlConstants.CAMERA_FUNCTION_REMOTE_SHOOTING;
                        DEBUGSUCCEEDED = true;
                        returnCb.returnCb(currentCameraFunction);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(mode=" + ((String) null) + ") : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedCameraFunction(GetSupportedCameraFunctionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        returnCb.returnCb(SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUG_SUCCEEDED ? SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableCameraFunction(GetAvailableCameraFunctionCallback returnCb) {
        String currentCameraFunction;
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        String[] listOfAvailableCameraFunction = null;
                        StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
                        if (appCondition.equals(StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER) || appCondition.equals(StateController.AppCondition.PLAYBACK_STREAMING) || appCondition.equals(StateController.AppCondition.PLAYBACK_DELETING)) {
                            currentCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_CONTENTS_TRANSFER;
                            listOfAvailableCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES;
                        } else if (StateController.AppCondition.PREPARATION.equals(appCondition)) {
                            currentCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_REMOTE_OTHER_FUNCTION;
                        } else {
                            currentCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_REMOTE_SHOOTING;
                            listOfAvailableCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES;
                        }
                        returnCb.returnCb(currentCameraFunction, listOfAvailableCameraFunction);
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void setSilentShootingSetting(SilentShootingSettingParams silentShootingSetting, SetSilentShootingSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> setSilentShootingSetting(SilentShootingSetting=" + silentShootingSetting.silentShooting + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        boolean check = CameraProxySilentShooting.set(silentShootingSetting.silentShooting);
                        if (check) {
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.INTERNAL_SERVER_ERROR.toInt(), "Set operation failed.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== setSilentShootingSetting() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSilentShootingSetting(GetSilentShootingSettingCallback getSilentShootingSettingCallback) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getSilentShootingSetting()");
                    boolean z = false;
                    SilentShootingSettingParams silentShootingSettingParams = new SilentShootingSettingParams();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        silentShootingSettingParams.silentShooting = CameraProxySilentShooting.get();
                        getSilentShootingSettingCallback.returnCb(silentShootingSettingParams);
                        z = true;
                    } else {
                        getSilentShootingSettingCallback.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    String str = TAG;
                    StringBuilder append = new StringBuilder().append("<=== getSilentShootingSetting(SilentShootingSetting=");
                    Object obj = silentShootingSettingParams;
                    if (!z || silentShootingSettingParams == null) {
                        obj = "n/a";
                    }
                    Log.e(str, append.append(obj).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    getSilentShootingSettingCallback.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    getSilentShootingSettingCallback.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getSupportedSilentShootingSetting(GetSupportedSilentShootingSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    String thisMethod = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + thisMethod + "()");
                    boolean DEBUG_SUCCEEDED = false;
                    SilentShootingSettingSupportedParams silentShootingSupportedParams = new SilentShootingSettingSupportedParams();
                    if (AvailabilityDetector.isAvailable(thisMethod)) {
                        silentShootingSupportedParams.candidate = CameraProxySilentShooting.getSupported();
                        if (silentShootingSupportedParams != null) {
                            returnCb.returnCb(silentShootingSupportedParams);
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Cannot obtain camera settings.");
                        }
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== " + thisMethod + "(supported=" + (DEBUG_SUCCEEDED ? silentShootingSupportedParams.candidate.toString() : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void getAvailableSilentShootingSetting(GetAvailableSilentShootingSettingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> getAvailableSilentShootingSetting()");
                    boolean DEBUG_SUCCEEDED = false;
                    SilentShootingSettingAvailableParams availableSilentShootingSetting = new SilentShootingSettingAvailableParams();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        availableSilentShootingSetting.silentShooting = CameraProxySilentShooting.get();
                        availableSilentShootingSetting.candidate = CameraProxySilentShooting.getAvailable();
                        returnCb.returnCb(availableSilentShootingSetting);
                        DEBUG_SUCCEEDED = true;
                    } else {
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== getAvailableSilentShootingSetting(silentShooting=" + ((!DEBUG_SUCCEEDED || availableSilentShootingSetting.silentShooting == null) ? "n/a" : availableSilentShootingSetting.silentShooting) + ", available=" + ((!DEBUG_SUCCEEDED || availableSilentShootingSetting == null) ? "n/a" : availableSilentShootingSetting.toString()) + LogHelper.MSG_CLOSE_BRACKET);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void startBulbShooting(StartBulbShootingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        Log.v(TAG, apiCallLog2.getMethodName() + " AppCondition : " + StateController.getInstance().getAppCondition());
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        ShootingHandler.ShootingStatus currentStatus = sHandler.startBulbShooting();
                        if (ShootingHandler.ShootingStatus.PROCESSING.equals(currentStatus) || ShootingHandler.ShootingStatus.DEVELOPING.equals(currentStatus)) {
                            Log.v(TAG, "SUCCESS(PROCESSING\u3000or DEVELOPING): Shooting is Start.");
                            returnCb.returnCb();
                        } else if (ShootingHandler.ShootingStatus.FAILED.equals(currentStatus)) {
                            Log.v(TAG, "FAILURE: Shooting failed: " + sHandler.getErrorStatus().toString());
                            returnCb.handleStatus(StatusCode.FAILED_SHOOTING.toInt(), "Shooting Failed");
                        } else if (ShootingHandler.ShootingStatus.READY.equals(currentStatus)) {
                            Log.e(TAG, "FAILURE: Unexpected READY state after shooting.");
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unexpected state");
                        } else {
                            Log.e(TAG, "FAILURE: Unknown Error. status = " + currentStatus);
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                        }
                    } else {
                        Log.e(TAG, "Failed to start taking picture because of current application state");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.mexi.orb.service.camera.v1_0.SmartRemoteControlServiceBase
    public void stopBulbShooting(StopBulbShootingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    if (AvailabilityDetector.isAvailable(apiCallLog2.getMethodName())) {
                        ShootingHandler sHandler = ShootingHandler.getInstance();
                        sHandler.stopBlubShooting();
                        returnCb.returnCb();
                    } else {
                        Log.e(TAG, "Method call of " + apiCallLog2.getMethodName() + " was Not Available.");
                        returnCb.handleStatus(StatusCode.ANY.toInt(), "Not Available Now");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }
}
