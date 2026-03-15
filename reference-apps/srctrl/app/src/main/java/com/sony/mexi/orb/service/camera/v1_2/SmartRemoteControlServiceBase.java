package com.sony.mexi.orb.service.camera.v1_2;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseType;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventAELockParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventAutoPowerOffParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventBatteryInfoParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventBeepModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventBracketShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventColorSettingParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContinuousErrorParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCreativeStyleParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFlipSettingParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFocusStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFormatStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventInfraredRemoteControlParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventIntervalTimeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventLiveviewOrientationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventMovieFileFormatParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventMovieQualityParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventNumberOfShotsParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventPictureEffectParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventRecordingTimeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventSceneRecognitionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventSceneSelectionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventSteadyModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventStillQualityParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventStillSizeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTimeCodeFormatParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTimeCodeMakeModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTimeCodePresetParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTimeCodeRunModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTrackingFocusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTrackingFocusStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTvColorSystemParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventUserBitPresetParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventUserBitTimeRecParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventViewAngleParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventZoomSettingParams;
import com.sony.scalar.webapi.service.camera.v1_2.getevent.GetEventCallback;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.2";

    public abstract void getEvent(boolean z, GetEventCallback getEventCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class GetEventCallbackImpl extends OrbAbstractClientCallbacks implements GetEventCallback {
        OrbAbstractClient client;

        GetEventCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_2.getevent.GetEventCallback
        public void returnCb(GetEventAvailableApiListParams availableApiList, GetEventCameraStatusParams cameraStatus, GetEventZoomInformationParams zoomInformation, GetEventLiveviewStatusParams liveviewStatus, GetEventLiveviewOrientationParams liveviewOrientation, GetEventTakePictureParams[] takePicture, GetEventContinuousErrorParams[] continuousError, GetEventTriggeredErrorParams triggeredError, GetEventSceneRecognitionParams sceneRecognition, GetEventFormatStatusParams formatStatus, GetEventStorageInformationParams[] storageInformation, GetEventBeepModeParams beepMode, GetEventCameraFunctionParams cameraFunction, GetEventMovieQualityParams movieQuality, GetEventStillSizeParams stillSize, GetEventCameraFunctionResultParams cameraFunctionResult, GetEventSteadyModeParams steadyMode, GetEventViewAngleParams viewAngle, GetEventExposureModeParams exposureMode, GetEventPostviewImageSizeParams postviewImageSize, GetEventSelfTimerParams selfTimer, GetEventShootModeParams shootMode, GetEventAELockParams aeLock, GetEventBracketShootModeParams bracketShootMode, GetEventCreativeStyleParams creativeStyle, GetEventExposureCompensationParams exposureCompensation, GetEventFlashModeParams flashMode, GetEventFNumberParams fNumber, GetEventFocusModeParams focusMode, GetEventIsoSpeedRateParams isoSpeedRate, GetEventPictureEffectParams pictureEffect, GetEventProgramShiftParams programShift, GetEventShutterSpeedParams shutterSpeed, GetEventWhiteBalanceParams whiteBalance, GetEventTouchAFPositionParams touchAFPosition, GetEventFocusStatusParams focusStatus, GetEventZoomSettingParams zoomSetting, GetEventStillQualityParams stillQuality, GetEventContShootingModeParams contShootingMode, GetEventContShootingSpeedParams contShootingSpeed, GetEventContShootingParams contShooting, GetEventFlipSettingParams flipSetting, GetEventSceneSelectionParams sceneSelection, GetEventIntervalTimeParams intervalTime, GetEventColorSettingParams colorSetting, GetEventMovieFileFormatParams movieFileFormat, GetEventTimeCodePresetParams timeCodePreset, GetEventUserBitPresetParams userBitPreset, GetEventTimeCodeFormatParams timeCodeFormat, GetEventTimeCodeRunModeParams timeCodeRunMode, GetEventTimeCodeMakeModeParams timeCodeMakeMode, GetEventUserBitTimeRecParams userBitTimeRec, GetEventInfraredRemoteControlParams infraredRemoteControl, GetEventTvColorSystemParams tvColorSystem, GetEventTrackingFocusStatusParams trackingFocusStatus, GetEventTrackingFocusParams trackingFocus, GetEventBatteryInfoParams batteryInfo, GetEventRecordingTimeParams recordingTime, GetEventNumberOfShotsParams numberOfShots, GetEventAutoPowerOffParams autoPowerOff) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, GetEventAvailableApiListParams.Converter.REF.toJson(availableApiList));
            JsonUtil.put(orbResponse, GetEventCameraStatusParams.Converter.REF.toJson(cameraStatus));
            JsonUtil.put(orbResponse, GetEventZoomInformationParams.Converter.REF.toJson(zoomInformation));
            JsonUtil.put(orbResponse, GetEventLiveviewStatusParams.Converter.REF.toJson(liveviewStatus));
            JsonUtil.put(orbResponse, GetEventLiveviewOrientationParams.Converter.REF.toJson(liveviewOrientation));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(takePicture, GetEventTakePictureParams.Converter.REF));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(continuousError, GetEventContinuousErrorParams.Converter.REF));
            JsonUtil.put(orbResponse, GetEventTriggeredErrorParams.Converter.REF.toJson(triggeredError));
            JsonUtil.put(orbResponse, GetEventSceneRecognitionParams.Converter.REF.toJson(sceneRecognition));
            JsonUtil.put(orbResponse, GetEventFormatStatusParams.Converter.REF.toJson(formatStatus));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(storageInformation, GetEventStorageInformationParams.Converter.REF));
            JsonUtil.put(orbResponse, GetEventBeepModeParams.Converter.REF.toJson(beepMode));
            JsonUtil.put(orbResponse, GetEventCameraFunctionParams.Converter.REF.toJson(cameraFunction));
            JsonUtil.put(orbResponse, GetEventMovieQualityParams.Converter.REF.toJson(movieQuality));
            JsonUtil.put(orbResponse, GetEventStillSizeParams.Converter.REF.toJson(stillSize));
            JsonUtil.put(orbResponse, GetEventCameraFunctionResultParams.Converter.REF.toJson(cameraFunctionResult));
            JsonUtil.put(orbResponse, GetEventSteadyModeParams.Converter.REF.toJson(steadyMode));
            JsonUtil.put(orbResponse, GetEventViewAngleParams.Converter.REF.toJson(viewAngle));
            JsonUtil.put(orbResponse, GetEventExposureModeParams.Converter.REF.toJson(exposureMode));
            JsonUtil.put(orbResponse, GetEventPostviewImageSizeParams.Converter.REF.toJson(postviewImageSize));
            JsonUtil.put(orbResponse, GetEventSelfTimerParams.Converter.REF.toJson(selfTimer));
            JsonUtil.put(orbResponse, GetEventShootModeParams.Converter.REF.toJson(shootMode));
            JsonUtil.put(orbResponse, GetEventAELockParams.Converter.REF.toJson(aeLock));
            JsonUtil.put(orbResponse, GetEventBracketShootModeParams.Converter.REF.toJson(bracketShootMode));
            JsonUtil.put(orbResponse, GetEventCreativeStyleParams.Converter.REF.toJson(creativeStyle));
            JsonUtil.put(orbResponse, GetEventExposureCompensationParams.Converter.REF.toJson(exposureCompensation));
            JsonUtil.put(orbResponse, GetEventFlashModeParams.Converter.REF.toJson(flashMode));
            JsonUtil.put(orbResponse, GetEventFNumberParams.Converter.REF.toJson(fNumber));
            JsonUtil.put(orbResponse, GetEventFocusModeParams.Converter.REF.toJson(focusMode));
            JsonUtil.put(orbResponse, GetEventIsoSpeedRateParams.Converter.REF.toJson(isoSpeedRate));
            JsonUtil.put(orbResponse, GetEventPictureEffectParams.Converter.REF.toJson(pictureEffect));
            JsonUtil.put(orbResponse, GetEventProgramShiftParams.Converter.REF.toJson(programShift));
            JsonUtil.put(orbResponse, GetEventShutterSpeedParams.Converter.REF.toJson(shutterSpeed));
            JsonUtil.put(orbResponse, GetEventWhiteBalanceParams.Converter.REF.toJson(whiteBalance));
            JsonUtil.put(orbResponse, GetEventTouchAFPositionParams.Converter.REF.toJson(touchAFPosition));
            JsonUtil.put(orbResponse, GetEventFocusStatusParams.Converter.REF.toJson(focusStatus));
            JsonUtil.put(orbResponse, GetEventZoomSettingParams.Converter.REF.toJson(zoomSetting));
            JsonUtil.put(orbResponse, GetEventStillQualityParams.Converter.REF.toJson(stillQuality));
            JsonUtil.put(orbResponse, GetEventContShootingModeParams.Converter.REF.toJson(contShootingMode));
            JsonUtil.put(orbResponse, GetEventContShootingSpeedParams.Converter.REF.toJson(contShootingSpeed));
            JsonUtil.put(orbResponse, GetEventContShootingParams.Converter.REF.toJson(contShooting));
            JsonUtil.put(orbResponse, GetEventFlipSettingParams.Converter.REF.toJson(flipSetting));
            JsonUtil.put(orbResponse, GetEventSceneSelectionParams.Converter.REF.toJson(sceneSelection));
            JsonUtil.put(orbResponse, GetEventIntervalTimeParams.Converter.REF.toJson(intervalTime));
            JsonUtil.put(orbResponse, GetEventColorSettingParams.Converter.REF.toJson(colorSetting));
            JsonUtil.put(orbResponse, GetEventMovieFileFormatParams.Converter.REF.toJson(movieFileFormat));
            JsonUtil.put(orbResponse, GetEventTimeCodePresetParams.Converter.REF.toJson(timeCodePreset));
            JsonUtil.put(orbResponse, GetEventUserBitPresetParams.Converter.REF.toJson(userBitPreset));
            JsonUtil.put(orbResponse, GetEventTimeCodeFormatParams.Converter.REF.toJson(timeCodeFormat));
            JsonUtil.put(orbResponse, GetEventTimeCodeRunModeParams.Converter.REF.toJson(timeCodeRunMode));
            JsonUtil.put(orbResponse, GetEventTimeCodeMakeModeParams.Converter.REF.toJson(timeCodeMakeMode));
            JsonUtil.put(orbResponse, GetEventUserBitTimeRecParams.Converter.REF.toJson(userBitTimeRec));
            JsonUtil.put(orbResponse, GetEventInfraredRemoteControlParams.Converter.REF.toJson(infraredRemoteControl));
            JsonUtil.put(orbResponse, GetEventTvColorSystemParams.Converter.REF.toJson(tvColorSystem));
            JsonUtil.put(orbResponse, GetEventTrackingFocusStatusParams.Converter.REF.toJson(trackingFocusStatus));
            JsonUtil.put(orbResponse, GetEventTrackingFocusParams.Converter.REF.toJson(trackingFocus));
            JsonUtil.put(orbResponse, GetEventBatteryInfoParams.Converter.REF.toJson(batteryInfo));
            JsonUtil.put(orbResponse, GetEventRecordingTimeParams.Converter.REF.toJson(recordingTime));
            JsonUtil.put(orbResponse, GetEventNumberOfShotsParams.Converter.REF.toJson(numberOfShots));
            JsonUtil.put(orbResponse, GetEventAutoPowerOffParams.Converter.REF.toJson(autoPowerOff));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetEvent extends OrbAbstractMethod {
        private MethodGetEvent() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"type\":\"string\", \"names\":\"string*\"}", "{\"type\":\"string\", \"cameraStatus\":\"string\"}", "{\"type\":\"string\", \"zoomPosition\":\"int\", \"zoomNumberBox\":\"int\", \"zoomIndexCurrentBox\":\"int\", \"zoomPositionCurrentBox\":\"int\"}", "{\"type\":\"string\", \"liveviewStatus\":\"bool\"}", "{\"type\":\"string\", \"liveviewOrientation\":\"string\"}", "{\"type\":\"string\", \"takePictureUrl\":\"string*\"}*", "{\"type\":\"string\", \"continuousError\":\"string\", \"isContinued\":\"bool\"}*", "{\"type\":\"string\", \"triggeredError\":\"string*\"}", "{\"type\":\"string\", \"sceneRecognition\":\"string\", \"steadyRecognition\":\"string\", \"motionRecognition\":\"string\"}", "{\"type\":\"string\", \"formatResult\":\"string\"}", "{\"type\":\"string\", \"storageID\":\"string\", \"recordTarget\":\"bool\", \"numberOfRecordableImages\":\"int\", \"recordableTime\":\"int\", \"storageDescription\":\"string\"}*", "{\"type\":\"string\", \"currentBeepMode\":\"string\", \"beepModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentCameraFunction\":\"string\", \"cameraFunctionCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentMovieQuality\":\"string\", \"movieQualityCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentAspect\":\"string\", \"currentSize\":\"string\"}", "{\"type\":\"string\", \"cameraFunctionResult\":\"string\"}", "{\"type\":\"string\", \"currentSteadyMode\":\"string\", \"steadyModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentViewAngle\":\"int\", \"viewAngleCandidates\":\"int*\"}", "{\"type\":\"string\", \"currentExposureMode\":\"string\", \"exposureModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentPostviewImageSize\":\"string\", \"postviewImageSizeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentSelfTimer\":\"int\", \"selfTimerCandidates\":\"int*\"}", "{\"type\":\"string\", \"currentShootMode\":\"string\", \"shootModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentAELock\":\"bool\", \"aeLockCandidates\":\"bool*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentBracketShootMode\":\"string\", \"currentBracketShootModeOption\":\"string\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentCreativeStyle\":\"string\", \"currentCreativeStyleContrast\":\"int\", \"currentCreativeStyleSaturation\":\"int\", \"currentCreativeStyleSharpness\":\"int\"}", "{\"type\":\"string\", \"currentExposureCompensation\":\"int\", \"maxExposureCompensation\":\"int\", \"minExposureCompensation\":\"int\", \"stepIndexOfExposureCompensation\":\"int\"}", "{\"type\":\"string\", \"currentFlashMode\":\"string\", \"flashModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentFNumber\":\"string\", \"fNumberCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentFocusMode\":\"string\", \"focusModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentIsoSpeedRate\":\"string\", \"isoSpeedRateCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentPictureEffect\":\"string\", \"currentPictureEffectOption\":\"string\"}", "{\"type\":\"string\", \"isShifted\":\"bool\"}", "{\"type\":\"string\", \"currentShutterSpeed\":\"string\", \"shutterSpeedCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentWhiteBalanceMode\":\"string\", \"currentColorTemperature\":\"int\"}", "{\"type\":\"string\", \"currentSet\":\"bool\", \"currentTouchCoordinates\":\"double*\"}", "{\"type\":\"string\", \"focusStatus\":\"string\"}", "{\"type\":\"string\", \"zoom\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"stillQuality\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"contShootingMode\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"contShootingSpeed\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"contShootingUrl\":\"ContShootingUrlParams[]\"}", "{\"type\":\"string\", \"flip\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"scene\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"intervalTimeSec\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"colorSetting\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"movieFileFormat\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"timeCodePreset\":\"TimeCodePresetCurrentParams\", \"candidate\":\"TimeCodePresetCandidateParams\"}", "{\"type\":\"string\", \"userBitPreset\":\"string\"}", "{\"type\":\"string\", \"timeCodeFormat\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"timeCodeRunMode\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"timeCodeMakeMode\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"userBitTimeRec\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"infraredRemoteControl\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"tvColorSystem\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"trackingFocusStatus\":\"string\"}", "{\"type\":\"string\", \"trackingFocus\":\"string\", \"candidate\":\"string*\"}", "{\"type\":\"string\", \"batteryInfo\":\"BatteryInfoParams[]\"}", "{\"type\":\"string\", \"recordingTime\":\"int\"}", "{\"type\":\"string\", \"numberOfShots\":\"int\"}", "{\"type\":\"string\", \"autoPowerOff\":\"int\", \"candidate\":\"int*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.BOOL};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                GetEventCallbackImpl callbacks = new GetEventCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getEvent(JsonUtil.getBoolean(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod(Name.GET_EVENT, new MethodGetEvent());
    }
}
