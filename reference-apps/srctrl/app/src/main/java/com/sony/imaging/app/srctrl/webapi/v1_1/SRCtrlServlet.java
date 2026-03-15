package com.sony.imaging.app.srctrl.webapi.v1_1;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.util.ApiCallLog;
import com.sony.mexi.orb.service.camera.v1_1.SmartRemoteControlServiceBase;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventAELockParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventBeepModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventBracketShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventContinuousErrorParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventCreativeStyleParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventFocusStatusParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventFormatStatusParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventLiveviewOrientationParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventMovieQualityParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventPictureEffectParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventSceneRecognitionParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventSteadyModeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventStillSizeParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventViewAngleParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_1.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_1.getevent.GetEventCallback;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class SRCtrlServlet extends SmartRemoteControlServiceBase {
    private static final String TAG = SRCtrlServlet.class.getSimpleName();
    private static final GetEventContinuousErrorParams[] s_DUMMY_PARAM_ARRAY_CONTINUOUS_ERROR = new GetEventContinuousErrorParams[0];
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.camera.v1_1.SmartRemoteControlServiceBase
    public void getEvent(boolean isPolling, GetEventCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init(false);
                    GetEventAdapter adapter = GetEventAdapter.getInstance();
                    String methodName = apiCallLog2.getMethodName();
                    Log.v(TAG, "---> " + methodName + " 1.1 (isPolling=" + isPolling + LogHelper.MSG_CLOSE_BRACKET);
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
                        GetEventAvailableApiListParams availableApiList = adapter.getAvailableApiList(isPolling);
                        GetEventCameraStatusParams cameraStatus = adapter.getServerStatus(isPolling);
                        GetEventLiveviewStatusParams liveviewStatus = adapter.getLiveviewStatus(isPolling);
                        GetEventTakePictureParams[] takePicture = adapter.getTakePicture(isPolling);
                        GetEventExposureModeParams exposureMode = adapter.getExposureModeParams(isPolling);
                        GetEventPostviewImageSizeParams postviewImageSize = adapter.getPostviewImageSize(isPolling);
                        GetEventSelfTimerParams selfTimer = adapter.getSelfTimer(isPolling);
                        GetEventShootModeParams shootMode = adapter.getShootModeParams(isPolling);
                        GetEventExposureCompensationParams exposureCompensation = adapter.getExposureCompensation(isPolling);
                        GetEventFNumberParams fNumber = adapter.getFNumber(isPolling);
                        GetEventIsoSpeedRateParams isoSpeedRate = adapter.getIsoSpeedRateParams(isPolling);
                        GetEventProgramShiftParams programShift = adapter.getProgramShift(isPolling);
                        GetEventShutterSpeedParams shutterSpeed = adapter.getShutterSpeed(isPolling);
                        GetEventWhiteBalanceParams whiteBalance = adapter.getWhiteBalanceParams(isPolling);
                        GetEventTouchAFPositionParams touchAFPosition = adapter.getTouchAFPosition(isPolling);
                        GetEventFlashModeParams flashMode = adapter.getFlashModeParams(isPolling);
                        GetEventZoomInformationParams zoomInformation = adapter.getZoomInformation(isPolling);
                        GetEventFocusModeParams focusMode = adapter.getFocusModeParams(isPolling);
                        GetEventContinuousErrorParams[] continuousError = s_DUMMY_PARAM_ARRAY_CONTINUOUS_ERROR;
                        GetEventCameraFunctionParams cameraFunction = adapter.getCameraFunction(isPolling);
                        GetEventCameraFunctionResultParams cameraFunctionResult = adapter.getCameraFunctionResult(isPolling);
                        GetEventFocusStatusParams focusStatus = adapter.getEventFocusStatusParams(isPolling);
                        printGetEventData(availableApiList, cameraStatus, zoomInformation, liveviewStatus, null, takePicture, continuousError, null, null, null, null, null, cameraFunction, null, null, cameraFunctionResult, null, null, exposureMode, postviewImageSize, selfTimer, shootMode, null, null, null, exposureCompensation, flashMode, fNumber, focusMode, isoSpeedRate, null, programShift, shutterSpeed, whiteBalance, touchAFPosition, focusStatus);
                        returnCb.returnCb(availableApiList, cameraStatus, zoomInformation, liveviewStatus, null, takePicture, continuousError, null, null, null, null, null, cameraFunction, null, null, cameraFunctionResult, null, null, exposureMode, postviewImageSize, selfTimer, shootMode, null, null, null, exposureCompensation, flashMode, fNumber, focusMode, isoSpeedRate, null, programShift, shutterSpeed, whiteBalance, touchAFPosition, focusStatus);
                    } else if (ServerEventHandler.Status.WAITING.equals(status)) {
                        Log.d(TAG, "Method call of " + apiCallLog2.getMethodName() + " was timeout.");
                        returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
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

    private void printGetEventData(GetEventAvailableApiListParams availableApiList, GetEventCameraStatusParams cameraStatus, GetEventZoomInformationParams zoomInformation, GetEventLiveviewStatusParams liveviewStatus, GetEventLiveviewOrientationParams liveviewOrientation, GetEventTakePictureParams[] takePicture, GetEventContinuousErrorParams[] continuousError, GetEventTriggeredErrorParams triggeredError, GetEventSceneRecognitionParams sceneRecognition, GetEventFormatStatusParams formatStatus, GetEventStorageInformationParams[] storageInformation, GetEventBeepModeParams beepMode, GetEventCameraFunctionParams cameraFunction, GetEventMovieQualityParams movieQuality, GetEventStillSizeParams stillSize, GetEventCameraFunctionResultParams cameraFunctionResult, GetEventSteadyModeParams steadyMode, GetEventViewAngleParams viewAngle, GetEventExposureModeParams exposureMode, GetEventPostviewImageSizeParams postviewImageSize, GetEventSelfTimerParams selfTimer, GetEventShootModeParams shootMode, GetEventAELockParams aeLock, GetEventBracketShootModeParams bracketShootMode, GetEventCreativeStyleParams creativeStyle, GetEventExposureCompensationParams exposureCompensation, GetEventFlashModeParams flashMode, GetEventFNumberParams fNumber, GetEventFocusModeParams focusMode, GetEventIsoSpeedRateParams isoSpeedRate, GetEventPictureEffectParams pictureEffect, GetEventProgramShiftParams programShift, GetEventShutterSpeedParams shutterSpeed, GetEventWhiteBalanceParams whiteBalance, GetEventTouchAFPositionParams touchAFPosition, GetEventFocusStatusParams focusStatus) {
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
                Log.v(TAG, "  --> \t numberOfRecordableImages = " + storageInformation[i3].numberOfRecordableImages);
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
        if (focusStatus != null) {
            Log.v(TAG, "<GetEventFocusStatusParams>");
            Log.v(TAG, "  --> type = " + focusStatus.type);
            Log.v(TAG, "  --> focusStatus = " + focusStatus.focusStatus);
        }
    }
}
