package com.sony.imaging.app.srctrl.webapi.v1_2;

import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.BatteryInfoParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.ContShootingUrlParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventBatteryInfoParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventContShootingSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventFocusStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventNumberOfShotsParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventRecordingTimeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.GetEventZoomSettingParams;

/* loaded from: classes.dex */
public class GetEventAdapter {
    private static final String TAG = GetEventAdapter.class.getSimpleName();
    private static GetEventAdapter mGetEventAdapter = null;
    private static final GetEventStorageInformationParams[] s_EMPTY_STORATE_INFO_ARRAY = new GetEventStorageInformationParams[0];

    private GetEventAdapter() {
    }

    public static GetEventAdapter getInstance() {
        if (mGetEventAdapter == null) {
            mGetEventAdapter = new GetEventAdapter();
        }
        return mGetEventAdapter;
    }

    public GetEventAvailableApiListParams getAvailableApiList(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventAvailableApiListParams params = ParamsGenerator.getAvailableApiList(isPolling);
        if (params == null) {
            return null;
        }
        GetEventAvailableApiListParams ret = new GetEventAvailableApiListParams();
        ret.type = params.type;
        ret.names = params.names;
        return ret;
    }

    public GetEventCameraStatusParams getServerStatus(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraStatusParams params = ParamsGenerator.getServerStatus(isPolling);
        if (params == null) {
            return null;
        }
        GetEventCameraStatusParams ret = new GetEventCameraStatusParams();
        ret.type = params.type;
        ret.cameraStatus = params.cameraStatus;
        return ret;
    }

    public GetEventZoomInformationParams getZoomInformation(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventZoomInformationParams params = ParamsGenerator.getZoomInformation(isPolling);
        if (params == null) {
            return null;
        }
        GetEventZoomInformationParams ret = new GetEventZoomInformationParams();
        ret.type = new String(params.type);
        ret.zoomPosition = params.zoomPosition;
        ret.zoomNumberBox = params.zoomNumberBox;
        ret.zoomIndexCurrentBox = params.zoomIndexCurrentBox;
        ret.zoomPositionCurrentBox = params.zoomPositionCurrentBox;
        return ret;
    }

    public GetEventLiveviewStatusParams getLiveviewStatus(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventLiveviewStatusParams params = ParamsGenerator.getLiveviewStatus(isPolling);
        if (params == null) {
            return null;
        }
        GetEventLiveviewStatusParams ret = new GetEventLiveviewStatusParams();
        ret.type = params.type;
        ret.liveviewStatus = params.liveviewStatus;
        return ret;
    }

    public GetEventTakePictureParams[] getTakePicture(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTakePictureParams[] params = ParamsGenerator.getTakePicture(isPolling);
        if (params == null) {
            return null;
        }
        GetEventTakePictureParams[] ret = new GetEventTakePictureParams[params.length];
        for (int i = 0; i < params.length; i++) {
            ret[i] = new GetEventTakePictureParams();
            ret[i].type = params[i] != null ? params[i].type : null;
            ret[i].takePictureUrl = params[i] != null ? params[i].takePictureUrl : null;
        }
        return ret;
    }

    public GetEventTriggeredErrorParams getTriggeredError(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTriggeredErrorParams params = ParamsGenerator.getTriggeredErrorParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventTriggeredErrorParams ret = new GetEventTriggeredErrorParams();
        ret.type = params.type;
        ret.triggeredError = params.triggeredError;
        return ret;
    }

    public GetEventStorageInformationParams[] getStorageInformationParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventStorageInformationParams[] params = ParamsGenerator.getStorageInformationParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventStorageInformationParams[] ret = new GetEventStorageInformationParams[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                ret[i] = new GetEventStorageInformationParams();
                ret[i].type = params[i].type;
                ret[i].storageID = params[i].storageID;
                ret[i].recordTarget = params[i].recordTarget;
                ret[i].numberOfRecordableImages = params[i].numberOfRecordableImages;
                ret[i].recordableTime = params[i].recordableTime;
                ret[i].recordTarget = params[i].recordTarget;
                ret[i].storageDescription = params[i].storageDescription;
            }
        }
        boolean flag = false;
        for (int i2 = 0; ret.length > i2; i2++) {
            if (ret[i2] != null) {
                flag = true;
            }
        }
        return !flag ? s_EMPTY_STORATE_INFO_ARRAY : ret;
    }

    public GetEventCameraFunctionParams getCameraFunction(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraFunctionParams params = ParamsGenerator.getCameraFunctionParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventCameraFunctionParams ret = new GetEventCameraFunctionParams();
        ret.type = params.type;
        ret.currentCameraFunction = params.currentCameraFunction;
        ret.cameraFunctionCandidates = params.cameraFunctionCandidates;
        return ret;
    }

    public GetEventCameraFunctionResultParams getCameraFunctionResult(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraFunctionResultParams params = ParamsGenerator.getCameraFunctionResultParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventCameraFunctionResultParams ret = new GetEventCameraFunctionResultParams();
        ret.type = params.type;
        ret.cameraFunctionResult = params.cameraFunctionResult;
        return ret;
    }

    public GetEventExposureModeParams getExposureModeParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventExposureModeParams params = ParamsGenerator.getExposureModeParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventExposureModeParams ret = new GetEventExposureModeParams();
        ret.type = params.type;
        ret.currentExposureMode = params.currentExposureMode;
        ret.exposureModeCandidates = params.exposureModeCandidates;
        return ret;
    }

    public GetEventPostviewImageSizeParams getPostviewImageSize(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventPostviewImageSizeParams params = ParamsGenerator.getPostviewImageSize(isPolling);
        if (params == null) {
            return null;
        }
        GetEventPostviewImageSizeParams ret = new GetEventPostviewImageSizeParams();
        ret.type = params.type;
        ret.currentPostviewImageSize = params.currentPostviewImageSize;
        ret.postviewImageSizeCandidates = params.postviewImageSizeCandidates;
        return ret;
    }

    public GetEventSelfTimerParams getSelfTimer(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventSelfTimerParams params = ParamsGenerator.getSelfTimer(isPolling);
        if (params == null) {
            return null;
        }
        GetEventSelfTimerParams ret = new GetEventSelfTimerParams();
        ret.type = params.type;
        ret.currentSelfTimer = params.currentSelfTimer;
        ret.selfTimerCandidates = params.selfTimerCandidates;
        return ret;
    }

    public GetEventShootModeParams getShootModeParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventShootModeParams params = ParamsGenerator.getShootModeParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventShootModeParams ret = new GetEventShootModeParams();
        ret.type = new String(params.type);
        ret.currentShootMode = params.currentShootMode;
        ret.shootModeCandidates = params.shootModeCandidates;
        return ret;
    }

    public GetEventExposureCompensationParams getExposureCompensation(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventExposureCompensationParams params = ParamsGenerator.getExposureCompensation(isPolling);
        if (params == null) {
            return null;
        }
        GetEventExposureCompensationParams ret = new GetEventExposureCompensationParams();
        ret.type = params.type;
        ret.currentExposureCompensation = params.currentExposureCompensation;
        ret.maxExposureCompensation = params.maxExposureCompensation;
        ret.minExposureCompensation = params.minExposureCompensation;
        ret.stepIndexOfExposureCompensation = params.stepIndexOfExposureCompensation;
        return ret;
    }

    public GetEventFlashModeParams getFlashModeParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFlashModeParams params = ParamsGenerator.getFlashModeParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventFlashModeParams ret = new GetEventFlashModeParams();
        ret.type = params.type;
        ret.currentFlashMode = params.currentFlashMode;
        ret.flashModeCandidates = params.flashModeCandidates;
        return ret;
    }

    public GetEventFNumberParams getFNumber(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFNumberParams params = ParamsGenerator.getFNumber(isPolling);
        if (params == null) {
            return null;
        }
        GetEventFNumberParams ret = new GetEventFNumberParams();
        ret.type = params.type;
        ret.currentFNumber = params.currentFNumber;
        ret.fNumberCandidates = params.fNumberCandidates;
        return ret;
    }

    public GetEventFocusModeParams getFocusModeParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFocusModeParams params = ParamsGenerator.getFocusModeParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventFocusModeParams ret = new GetEventFocusModeParams();
        ret.type = params.type;
        ret.currentFocusMode = params.currentFocusMode;
        ret.focusModeCandidates = params.focusModeCandidates;
        return ret;
    }

    public GetEventIsoSpeedRateParams getIsoSpeedRateParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventIsoSpeedRateParams params = ParamsGenerator.getIsoSpeedRateParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventIsoSpeedRateParams ret = new GetEventIsoSpeedRateParams();
        ret.type = params.type;
        ret.currentIsoSpeedRate = params.currentIsoSpeedRate;
        ret.isoSpeedRateCandidates = params.isoSpeedRateCandidates;
        return ret;
    }

    public GetEventProgramShiftParams getProgramShift(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventProgramShiftParams params = ParamsGenerator.getProgramShift(isPolling);
        if (params == null) {
            return null;
        }
        GetEventProgramShiftParams ret = new GetEventProgramShiftParams();
        ret.type = params.type;
        ret.isShifted = params.isShifted;
        return ret;
    }

    public GetEventShutterSpeedParams getShutterSpeed(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventShutterSpeedParams params = ParamsGenerator.getShutterSpeed(isPolling);
        if (params == null) {
            return null;
        }
        GetEventShutterSpeedParams ret = new GetEventShutterSpeedParams();
        ret.type = params.type;
        ret.currentShutterSpeed = params.currentShutterSpeed;
        ret.shutterSpeedCandidates = params.shutterSpeedCandidates;
        return ret;
    }

    public GetEventWhiteBalanceParams getWhiteBalanceParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventWhiteBalanceParams params = ParamsGenerator.getWhiteBalanceParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventWhiteBalanceParams ret = new GetEventWhiteBalanceParams();
        ret.type = params.type;
        ret.checkAvailability = params.checkAvailability;
        ret.currentWhiteBalanceMode = params.currentWhiteBalanceMode;
        ret.currentColorTemperature = params.currentColorTemperature;
        return ret;
    }

    public GetEventTouchAFPositionParams getTouchAFPosition(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTouchAFPositionParams params = ParamsGenerator.getTouchAFPosition(isPolling);
        if (params == null) {
            return null;
        }
        GetEventTouchAFPositionParams ret = new GetEventTouchAFPositionParams();
        ret.type = params.type;
        ret.currentSet = params.currentSet;
        ret.currentTouchCoordinates = params.currentTouchCoordinates;
        return ret;
    }

    public GetEventFocusStatusParams getEventFocusStatusParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFocusStatusParams params = ParamsGenerator.getFocusStatus(isPolling);
        if (params == null) {
            return null;
        }
        GetEventFocusStatusParams ret = new GetEventFocusStatusParams();
        ret.type = params.type;
        ret.focusStatus = params.focusStatus;
        return ret;
    }

    public GetEventZoomSettingParams getEventZoomSettingParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventZoomSettingParams params = ParamsGenerator.getZoomSettingParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventZoomSettingParams ret = new GetEventZoomSettingParams();
        ret.type = params.type;
        ret.zoom = params.zoom;
        ret.candidate = params.candidate;
        return ret;
    }

    public GetEventContShootingModeParams getContShootingMode(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingModeParams params = ParamsGenerator.getContShootingMode(isPolling);
        if (params == null) {
            return null;
        }
        GetEventContShootingModeParams ret = new GetEventContShootingModeParams();
        ret.type = params.type;
        ret.contShootingMode = params.contShootingMode;
        ret.candidate = params.candidate;
        return ret;
    }

    public GetEventContShootingSpeedParams getContShootingSpeed(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingSpeedParams params = ParamsGenerator.getContShootingSpeed(isPolling);
        if (params == null) {
            return null;
        }
        GetEventContShootingSpeedParams ret = new GetEventContShootingSpeedParams();
        ret.type = params.type;
        ret.contShootingSpeed = params.contShootingSpeed;
        ret.candidate = params.candidate;
        return ret;
    }

    public GetEventContShootingParams getContShootingParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingParams params = ParamsGenerator.getContShootingParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventContShootingParams ret = new GetEventContShootingParams();
        ret.type = params.type;
        if (params.contShootingUrl != null) {
            ret.contShootingUrl = new ContShootingUrlParams[params.contShootingUrl.length];
            for (int i = 0; i < params.contShootingUrl.length; i++) {
                ret.contShootingUrl[i] = new ContShootingUrlParams();
                ret.contShootingUrl[i].postviewUrl = params.contShootingUrl[i].postviewUrl;
                ret.contShootingUrl[i].thumbnailUrl = params.contShootingUrl[i].thumbnailUrl;
            }
            return ret;
        }
        return ret;
    }

    public GetEventBatteryInfoParams getEventBatteryInfoParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventBatteryInfoParams params = ParamsGenerator.getGetEventBatteryInfoParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventBatteryInfoParams ret = new GetEventBatteryInfoParams();
        ret.type = params.type;
        if (params.batteryInfo != null) {
            ret.batteryInfo = new BatteryInfoParams[params.batteryInfo.length];
            for (int i = 0; i < params.batteryInfo.length; i++) {
                ret.batteryInfo[i] = new BatteryInfoParams();
                ret.batteryInfo[i].additionalStatus = params.batteryInfo[i].additionalStatus;
                ret.batteryInfo[i].batteryID = params.batteryInfo[i].batteryID;
                ret.batteryInfo[i].description = params.batteryInfo[i].description;
                ret.batteryInfo[i].levelDenom = params.batteryInfo[i].levelDenom;
                ret.batteryInfo[i].levelNumer = params.batteryInfo[i].levelNumer;
                ret.batteryInfo[i].status = params.batteryInfo[i].status;
            }
            return ret;
        }
        return ret;
    }

    public GetEventRecordingTimeParams getEventRecordingTimeParams(boolean isPolling) {
        com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventRecordingTimeParams params = ParamsGenerator.getGetEventRecordingTimeParams(isPolling);
        if (params == null) {
            return null;
        }
        GetEventRecordingTimeParams ret = new GetEventRecordingTimeParams();
        ret.type = params.type;
        ret.recordingTime = params.recordingTime;
        return ret;
    }

    public GetEventNumberOfShotsParams getEventNumberOfShotsParams(boolean isPolling) {
        return null;
    }
}
