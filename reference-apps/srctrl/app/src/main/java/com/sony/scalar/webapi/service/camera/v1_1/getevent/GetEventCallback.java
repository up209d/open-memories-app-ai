package com.sony.scalar.webapi.service.camera.v1_1.getevent;

import com.sony.mexi.orb.service.Callbacks;
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

/* loaded from: classes.dex */
public interface GetEventCallback extends Callbacks {
    void returnCb(GetEventAvailableApiListParams getEventAvailableApiListParams, GetEventCameraStatusParams getEventCameraStatusParams, GetEventZoomInformationParams getEventZoomInformationParams, GetEventLiveviewStatusParams getEventLiveviewStatusParams, GetEventLiveviewOrientationParams getEventLiveviewOrientationParams, GetEventTakePictureParams[] getEventTakePictureParamsArr, GetEventContinuousErrorParams[] getEventContinuousErrorParamsArr, GetEventTriggeredErrorParams getEventTriggeredErrorParams, GetEventSceneRecognitionParams getEventSceneRecognitionParams, GetEventFormatStatusParams getEventFormatStatusParams, GetEventStorageInformationParams[] getEventStorageInformationParamsArr, GetEventBeepModeParams getEventBeepModeParams, GetEventCameraFunctionParams getEventCameraFunctionParams, GetEventMovieQualityParams getEventMovieQualityParams, GetEventStillSizeParams getEventStillSizeParams, GetEventCameraFunctionResultParams getEventCameraFunctionResultParams, GetEventSteadyModeParams getEventSteadyModeParams, GetEventViewAngleParams getEventViewAngleParams, GetEventExposureModeParams getEventExposureModeParams, GetEventPostviewImageSizeParams getEventPostviewImageSizeParams, GetEventSelfTimerParams getEventSelfTimerParams, GetEventShootModeParams getEventShootModeParams, GetEventAELockParams getEventAELockParams, GetEventBracketShootModeParams getEventBracketShootModeParams, GetEventCreativeStyleParams getEventCreativeStyleParams, GetEventExposureCompensationParams getEventExposureCompensationParams, GetEventFlashModeParams getEventFlashModeParams, GetEventFNumberParams getEventFNumberParams, GetEventFocusModeParams getEventFocusModeParams, GetEventIsoSpeedRateParams getEventIsoSpeedRateParams, GetEventPictureEffectParams getEventPictureEffectParams, GetEventProgramShiftParams getEventProgramShiftParams, GetEventShutterSpeedParams getEventShutterSpeedParams, GetEventWhiteBalanceParams getEventWhiteBalanceParams, GetEventTouchAFPositionParams getEventTouchAFPositionParams, GetEventFocusStatusParams getEventFocusStatusParams);
}
