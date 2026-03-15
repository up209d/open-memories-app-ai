package com.sony.imaging.app.srctrl.webapi.availability;

import android.text.TextUtils;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.player.MediaPlayerManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.srctrl.liveview.FrameInfoLoader;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.streaming.StreamingController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationAttachedLensInfo;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationBatteryInfo;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingSpeed;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureCompensation;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFlashMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFocusArea;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFocusMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationHalfPressShutter;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationIsoNumber;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationProgramShift;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationRecordingTime;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSelfTimer;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShootMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShutterSpeed;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSilentShooting;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationWhiteBalance;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoom;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoomSetting;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyIsoNumber;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyPostviewImageSize;
import com.sony.imaging.app.srctrl.webapi.definition.ReceiveEventStatus;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingStatus;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.BatteryInfoParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.BulbShootingUrlParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.ContShootingUrlParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventBatteryInfoParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventBulbCapturingTimeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventBulbShootingParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingModeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContShootingSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventContinuousErrorParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFocusStatusParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventRecordingTimeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventSilentShootingSettingParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventZoomSettingParams;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class ParamsGenerator {
    public static final int s_INVALID_EVCOMPASATION_VALUE = 0;
    public static final int s_INVALID_SELFTIMER_VALUE = 0;
    private static final int s_INVALID_STORAGEINFO_PARAM = -1;
    private static WeakReference<CameraSettingChangeListener> s_cameraChangeListener;
    private static final String tag = ParamsGenerator.class.getName();
    static GetEventCameraStatusParams s_CameraStatusParams = createGetEventCameraStatusParams(null);
    static final Object s_CameraStatusParamsLock = new Object();
    static GetEventLiveviewStatusParams s_LiveviewStatusParams = null;
    static final Object s_LiveviewStatusParamsLock = new Object();
    private static boolean s_LiveviewStatusUpdated = false;
    private static boolean s_PostviewImageSizeUpdated = false;
    static GetEventPostviewImageSizeParams s_PostviewImageSizeParams = null;
    static final Object s_PostviewImageSizeParamsLock = new Object();
    static GetEventExposureCompensationParams s_ExposureCompensationParams = createGetEventExposureCompensationParams(null);
    static final Object s_ExposureCompensationParamsLock = new Object();
    public static boolean s_EXPOSURE_COMPENSATION_VALUE_INITIALIZED = false;
    private static boolean s_ExposureCompensationUpdated = false;
    static GetEventContShootingModeParams s_ContShootingModeParams = createGetEventContShootingModeParams(null);
    static final Object s_ContShootingModeParamsLock = new Object();
    public static final String s_INVALID_CONTSHOOTMODE_VALUE = null;
    public static boolean s_CONTSHOOTINGMODE_VALUE_INITIALIZED = false;
    private static boolean s_ContShootingModeUpdated = false;
    static GetEventContShootingSpeedParams s_ContShootingSpeedParams = createGetEventContShootingSpeedParams(null);
    static final Object s_ContShootingSpeedParamsLock = new Object();
    public static final String s_INVALID_CONTSHOOTSPEED_VALUE = null;
    public static boolean s_CONTSHOOTINGSPEED_VALUE_INITIALIZED = false;
    private static boolean s_ContShootingSpeedUpdated = false;
    static GetEventContShootingParams s_ContShootingParams = createGetEventContShootingParams(null);
    static final Object s_ContShootingParamsLock = new Object();
    private static boolean s_ContShootingUpdated = false;
    private static GetEventContShootingParams s_ContShootingParams_EMPTY = new GetEventContShootingParams();
    static GetEventSelfTimerParams s_SelfTimerParams = createGetEventSelftimerParams(null);
    static final Object s_SelfTimerParamsLock = new Object();
    public static boolean s_SELFTIMER_VALUE_INITIALIZED = false;
    private static boolean s_SelfTimerUpdated = false;
    private static boolean s_AvailableApiListUpdated = false;
    private static GetEventAvailableApiListParams s_AvailableApiListParams = null;
    static final Object s_AvailableApiListParamsLock = new Object();
    private static GetEventShutterSpeedParams s_ShutterSpeedParams = null;
    static final Object s_ShutterSpeedParamsLock = new Object();
    private static boolean s_ShutterSpeedUpdated = false;
    private static GetEventFNumberParams s_FNumberParams = null;
    static final Object s_FNumberParamsLock = new Object();
    private static boolean s_FNumberUpdated = false;
    private static GetEventFocusModeParams s_FocusModeParams = null;
    static final Object s_FocusModeParamsLock = new Object();
    private static boolean s_FocusModeUpdated = false;
    private static boolean s_isFocusControl = false;
    private static _GetEventFocusAreaParams s_FocusAreaParams = null;
    static final Object s_FocusAreaParamsLock = new Object();
    private static boolean s_FocusAreaUpdated = false;
    private static GetEventTouchAFPositionParams s_TouchAFPositionParams = createGetEventTouchAFPositionParams(null);
    static final Object s_TouchAFPositionParamsLock = new Object();
    private static boolean s_TouchAFPositionUpdated = false;
    private static GetEventWhiteBalanceParams s_WhiteBalanceParams = createGetEventWhiteBalanceParams(null);
    private static WhiteBalanceParamCandidate[] s_WhiteBalanceAvaialble = CameraOperationWhiteBalance.s_EMPTY_CANDIDATE;
    static final Object s_WhiteBalanceParamsLock = new Object();
    private static boolean s_WhiteBalanceUpdated = false;
    private static GetEventShootModeParams s_ShootModeParams = createGetEventShootModeParams(null);
    static final Object s_ShootModeParamsLock = new Object();
    private static GetEventShootModeParams s_initShootModeParams = createGetEventShootModeParams(null);
    private static boolean s_ShootModeUpdated = false;
    private static GetEventExposureModeParams s_ExposureModeParams = createGetEventExposureModeParams(null);
    static final Object s_ExposureModeParamsLock = new Object();
    private static boolean s_ExposureModeUpdated = false;
    private static GetEventIsoSpeedRateParams s_IsoSpeedRateParams = createGetEventIsoSpeedRateParams(null);
    static final Object s_IsoSpeedRateParamsLock = new Object();
    private static boolean s_IsoSpeedRateUpdated = false;
    private static GetEventProgramShiftParams s_ProgramShiftParams = null;
    static final Object s_ProgramShiftParamsLock = new Object();
    private static boolean s_ProgramShiftUpdated = false;
    private static List<GetEventTakePictureParams> s_TakePictureParamsList = new ArrayList();
    static final Object s_TakePictureParamsListLock = new Object();
    private static boolean s_TakePictureUpdated = false;
    private static GetEventTakePictureParams[] s_TakePictureParamsArray_EMPTY = new GetEventTakePictureParams[0];
    private static GetEventBulbShootingParams s_BulbShootingParams = null;
    static final Object s_BulbShootingParamsLock = new Object();
    private static boolean s_BulbShootingUpdated = false;
    private static GetEventBulbCapturingTimeParams s_GetEventBulbCapturingTimeParams = null;
    private static final Object s_GetEventBulbCapturingTimeParamsLock = new Object();
    private static boolean s_GetEventBulbCapturingTimeParamsUpdated = false;
    private static GetEventFlashModeParams s_FlashModeParams = null;
    static final Object s_FlashModeParamsLock = new Object();
    private static boolean s_FlashModeUpdated = false;
    private static GetEventZoomInformationParams s_ZoomInformationParams = null;
    static final Object s_ZoomInformationParamsLock = new Object();
    private static boolean s_ZoomInformationUpdated = false;
    private static GetEventFocusStatusParams s_FocusStatusParams = createGetEventFocusStatusParams(null);
    static final Object s_FocusStatusParamsLock = new Object();
    private static boolean s_FocusStatusUpdated = false;
    private static GetEventCameraFunctionParams s_CameraFunctionParams = null;
    static final Object s_CameraFunctionParamsLock = new Object();
    private static boolean s_CameraFunctionParamsUpdated = false;
    private static GetEventCameraFunctionResultParams s_CameraFunctionResultParams = null;
    static final Object s_CameraFunctionResultParamsLock = new Object();
    private static boolean s_CameraFunctionResultParamsUpdated = false;
    private static HashMap<String, Boolean> s_continuousErrors = new HashMap<>();
    static final Object s_ContinuousErrorParamsLock = new Object();
    private static GetEventContinuousErrorParams[] s_ContinuousErrorParamsArray_EMPTY = new GetEventContinuousErrorParams[0];
    private static ArrayList<String> s_triggeredErrorList = new ArrayList<>();
    static final Object s_TriggeredErrorParamsLock = new Object();
    private static boolean s_TriggeredErrorUpdated = false;
    private static GetEventBatteryInfoParams s_GetEventBatteryInfoParams = null;
    private static final Object s_GetBatteryInfoParamsLock = new Object();
    private static boolean s_GetBatteryInfoParamsUpdated = false;
    private static final BatteryInfoParams[] s_EMPTY_BATTERY_INFO_ARRAY = new BatteryInfoParams[0];
    private static GetEventRecordingTimeParams s_GetEventRecordingTimeParams = null;
    private static final Object s_GetEventRecordingTimeParamsLock = new Object();
    private static boolean s_GetEventRecordingTimeParamsUpdated = false;
    static StreamingStatus s_StreamingStatusParams = null;
    static final Object s_StreamingStatusParamsLock = new Object();
    private static boolean s_StreamingStatusUpdated = false;
    private static GetEventStorageInformationParams s_StorageInformationParams = null;
    private static final Object s_StorageInformationParamsLock = new Object();
    private static boolean s_StorageInformationUpdated = false;
    private static final GetEventStorageInformationParams[] s_EMPTY_GETEVENT_STORAGEINFO_ARRAY = new GetEventStorageInformationParams[0];
    private static GetEventZoomSettingParams s_ZoomSettingParams = null;
    static final Object s_ZoomSettingParamsLock = new Object();
    private static boolean s_ZoomSettingUpdated = false;
    private static GetEventSilentShootingSettingParams s_SilentShootingParams = null;
    static final Object s_SilentShootingParamsLock = new Object();
    private static boolean s_SilentShootingUpdated = false;

    /* loaded from: classes.dex */
    public static class _GetEventFocusAreaParams {
        public String currentFocusArea;
        public String[] focusAreaCandidates;
        public String type;
    }

    static /* synthetic */ String access$000() {
        return getFNumber();
    }

    static /* synthetic */ String[] access$100() {
        return getFNumberAvailableCandidate();
    }

    public static void initialize() {
        Log.v(tag, "initialize");
        reset();
        synchronized (s_GetBatteryInfoParamsLock) {
            s_GetEventBatteryInfoParams = createGetEventBatteryInfoParams(null);
            s_GetBatteryInfoParamsUpdated = false;
        }
        synchronized (s_FocusStatusParamsLock) {
            s_FocusStatusParams = createGetEventFocusStatusParams(null);
            s_FocusStatusUpdated = false;
        }
        synchronized (s_ContinuousErrorParamsLock) {
            s_continuousErrors = new HashMap<>();
        }
    }

    private static void reset() {
        Log.v(tag, "reset");
        synchronized (s_CameraStatusParamsLock) {
            s_CameraStatusParams = createGetEventCameraStatusParams(null);
        }
        synchronized (s_LiveviewStatusParamsLock) {
            s_LiveviewStatusParams = null;
            s_LiveviewStatusUpdated = false;
        }
        synchronized (s_PostviewImageSizeParamsLock) {
            s_PostviewImageSizeUpdated = false;
            s_PostviewImageSizeParams = null;
        }
        synchronized (s_ExposureCompensationParamsLock) {
            s_ExposureCompensationParams = createGetEventExposureCompensationParams(null);
            s_EXPOSURE_COMPENSATION_VALUE_INITIALIZED = false;
        }
        synchronized (s_ContShootingModeParamsLock) {
            s_ContShootingModeParams = createGetEventContShootingModeParams(null);
            s_CONTSHOOTINGMODE_VALUE_INITIALIZED = false;
        }
        synchronized (s_ContShootingParamsLock) {
            s_ContShootingParams = createGetEventContShootingParams(null);
            s_ContShootingUpdated = false;
        }
        synchronized (s_ContShootingSpeedParamsLock) {
            s_ContShootingSpeedParams = createGetEventContShootingSpeedParams(null);
            s_CONTSHOOTINGSPEED_VALUE_INITIALIZED = false;
        }
        synchronized (s_SelfTimerParamsLock) {
            s_SelfTimerParams = createGetEventSelftimerParams(null);
            s_SELFTIMER_VALUE_INITIALIZED = false;
        }
        synchronized (s_AvailableApiListParamsLock) {
            s_AvailableApiListUpdated = false;
            s_AvailableApiListParams = null;
        }
        synchronized (s_ShutterSpeedParamsLock) {
            s_ShutterSpeedParams = null;
            s_ShutterSpeedUpdated = false;
        }
        synchronized (s_FNumberParamsLock) {
            s_FNumberParams = null;
            s_FNumberUpdated = false;
        }
        synchronized (s_FocusModeParamsLock) {
            s_FocusModeParams = createGetEventFocusModeParams(null);
            s_FocusModeUpdated = false;
            s_isFocusControl = false;
        }
        synchronized (s_FocusAreaParamsLock) {
            s_FocusAreaParams = createGetEventFocusAreaParams(null);
            s_FocusAreaUpdated = false;
        }
        synchronized (s_TouchAFPositionParamsLock) {
            s_TouchAFPositionParams = createGetEventTouchAFPositionParams(null);
            s_TouchAFPositionUpdated = false;
        }
        synchronized (s_WhiteBalanceParamsLock) {
            s_WhiteBalanceParams = createGetEventWhiteBalanceParams(null);
            s_WhiteBalanceUpdated = false;
            s_WhiteBalanceAvaialble = CameraOperationWhiteBalance.s_EMPTY_CANDIDATE;
        }
        synchronized (s_ShootModeParamsLock) {
            s_ShootModeParams = createGetEventShootModeParams(null);
            s_ShootModeUpdated = false;
        }
        synchronized (s_ExposureModeParamsLock) {
            s_ExposureModeParams = createGetEventExposureModeParams(null);
            s_ExposureModeUpdated = false;
        }
        synchronized (s_IsoSpeedRateParamsLock) {
            s_IsoSpeedRateParams = createGetEventIsoSpeedRateParams(null);
            s_IsoSpeedRateUpdated = false;
        }
        synchronized (s_ProgramShiftParamsLock) {
            s_ProgramShiftParams = null;
            s_ProgramShiftUpdated = false;
        }
        synchronized (s_TakePictureParamsListLock) {
            s_TakePictureParamsList.clear();
            s_TakePictureUpdated = false;
        }
        synchronized (s_BulbShootingParamsLock) {
            s_BulbShootingParams = null;
            s_BulbShootingUpdated = false;
        }
        synchronized (s_GetEventBulbCapturingTimeParamsLock) {
            s_GetEventBulbCapturingTimeParams = createGetEventBulbCapturingTimeParams(null);
            s_GetEventBulbCapturingTimeParamsUpdated = false;
        }
        synchronized (s_FlashModeParamsLock) {
            s_FlashModeParams = createGetEventFlashModeParams(null);
            s_FlashModeUpdated = false;
        }
        synchronized (s_ZoomInformationParamsLock) {
            s_ZoomInformationParams = createGetEventZoomInformationParams(null);
            s_ZoomInformationUpdated = false;
        }
        synchronized (s_GetEventRecordingTimeParamsLock) {
            s_GetEventRecordingTimeParams = createGetEventRecordingTimeParams(null);
            s_GetEventRecordingTimeParamsUpdated = false;
        }
        synchronized (s_ZoomSettingParamsLock) {
            s_ZoomSettingParams = createGetEventZoomSettingParams(null);
            s_ZoomSettingUpdated = false;
        }
        synchronized (s_StorageInformationParamsLock) {
            s_StorageInformationParams = createGetEventStorageInformationParams(null);
            s_StorageInformationUpdated = false;
        }
        synchronized (s_CameraFunctionParamsLock) {
            s_CameraFunctionParams = createGetEventCameraFunctionParams(null);
            s_CameraFunctionParamsUpdated = false;
        }
        synchronized (s_CameraFunctionResultParamsLock) {
            s_CameraFunctionResultParams = createGetEventCameraFunctionResultParams(null);
            s_CameraFunctionResultParamsUpdated = false;
        }
        synchronized (s_TriggeredErrorParamsLock) {
            s_triggeredErrorList = new ArrayList<>();
            s_TriggeredErrorUpdated = false;
        }
        synchronized (s_StreamingStatusParamsLock) {
            s_StreamingStatusParams = createStreamingStatusParams(null);
            s_StreamingStatusUpdated = false;
        }
        synchronized (s_SilentShootingParamsLock) {
            s_SilentShootingParams = createGetEventSilentShootingParams(null);
            s_SilentShootingUpdated = false;
        }
    }

    private static GetEventCameraStatusParams createGetEventCameraStatusParams(GetEventCameraStatusParams original) {
        GetEventCameraStatusParams cameraStatusParams = new GetEventCameraStatusParams();
        cameraStatusParams.type = "cameraStatus";
        if (original != null) {
            cameraStatusParams.cameraStatus = original.cameraStatus;
        } else {
            cameraStatusParams.cameraStatus = ReceiveEventStatus.DUMMY;
        }
        return cameraStatusParams;
    }

    public static GetEventCameraStatusParams getServerStatus(boolean isPolling) {
        String serverStatus;
        GetEventCameraStatusParams clone;
        switch (StateController.getInstance().getServerStatus()) {
            case IDLE:
                serverStatus = ReceiveEventStatus.IDLE;
                break;
            case STILL_CAPTURING:
                serverStatus = ReceiveEventStatus.STILL_CAPTURING;
                break;
            case STILL_POST_PROCESSING:
                serverStatus = ReceiveEventStatus.STILL_POST_PROCESSING;
                break;
            case STILL_SAVING:
                serverStatus = ReceiveEventStatus.STILL_SAVING;
                break;
            case MOVIE_WAIT_REC_START:
                serverStatus = ReceiveEventStatus.MOVIE_STARTING_REC;
                break;
            case MOVIE_RECORDING:
                serverStatus = ReceiveEventStatus.MOVIE_RECORDING;
                break;
            case MOVIE_WAIT_REC_STOP:
                serverStatus = ReceiveEventStatus.MOVIE_STOPPING_REC;
                break;
            case CONTENTS_TRANSFER:
                serverStatus = ReceiveEventStatus.CONTENTS_TRANSFER;
                break;
            case DELETING:
                serverStatus = ReceiveEventStatus.DELETING;
                break;
            case STREAMING:
                serverStatus = ReceiveEventStatus.STREAMING;
                break;
            case NOT_READY:
                serverStatus = ReceiveEventStatus.NOT_READY;
                break;
            default:
                serverStatus = ReceiveEventStatus.ERROR;
                break;
        }
        if (!isPolling || !s_CameraStatusParams.cameraStatus.equals(serverStatus)) {
            synchronized (s_CameraStatusParamsLock) {
                s_CameraStatusParams.cameraStatus = serverStatus;
                clone = createGetEventCameraStatusParams(s_CameraStatusParams);
            }
            return clone;
        }
        return null;
    }

    private static GetEventLiveviewStatusParams createGetEventLiveviewStatusParams(GetEventLiveviewStatusParams original) {
        GetEventLiveviewStatusParams liveviewStatusParams = new GetEventLiveviewStatusParams();
        liveviewStatusParams.type = "liveviewStatus";
        if (original != null) {
            liveviewStatusParams.liveviewStatus = original.liveviewStatus;
        } else {
            liveviewStatusParams.liveviewStatus = false;
        }
        return liveviewStatusParams;
    }

    public static boolean updateLiveviewStatus() {
        boolean z = true;
        synchronized (s_LiveviewStatusParamsLock) {
            if (s_LiveviewStatusUpdated) {
                z = false;
            } else {
                s_LiveviewStatusUpdated = true;
            }
        }
        return z;
    }

    public static GetEventLiveviewStatusParams getLiveviewStatus(boolean isPolling) {
        GetEventLiveviewStatusParams clone = null;
        boolean liveviewStatus = false;
        if (!StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            liveviewStatus = LiveviewLoader.isLoadingPreview();
        }
        synchronized (s_LiveviewStatusParamsLock) {
            if (isPolling) {
                if (!s_LiveviewStatusUpdated && s_LiveviewStatusParams != null && liveviewStatus == s_LiveviewStatusParams.liveviewStatus.booleanValue()) {
                }
            }
            s_LiveviewStatusUpdated = false;
            if (s_LiveviewStatusParams == null) {
                s_LiveviewStatusParams = createGetEventLiveviewStatusParams(null);
            }
            s_LiveviewStatusParams.liveviewStatus = Boolean.valueOf(liveviewStatus);
            clone = createGetEventLiveviewStatusParams(s_LiveviewStatusParams);
        }
        return clone;
    }

    public static boolean updatePostviewImageSize() {
        boolean z = true;
        synchronized (s_PostviewImageSizeParamsLock) {
            if (s_PostviewImageSizeUpdated) {
                z = false;
            } else {
                s_PostviewImageSizeUpdated = true;
            }
        }
        return z;
    }

    private static GetEventPostviewImageSizeParams createGetEventPostviewImageSizeParams(GetEventPostviewImageSizeParams original) {
        GetEventPostviewImageSizeParams postviewImageSizeParams = new GetEventPostviewImageSizeParams();
        postviewImageSizeParams.type = "postviewImageSize";
        if (original != null) {
            postviewImageSizeParams.currentPostviewImageSize = original.currentPostviewImageSize;
            postviewImageSizeParams.postviewImageSizeCandidates = (String[]) Arrays.copyOf(original.postviewImageSizeCandidates, original.postviewImageSizeCandidates.length);
        } else {
            postviewImageSizeParams.currentPostviewImageSize = "";
            postviewImageSizeParams.postviewImageSizeCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        return postviewImageSizeParams;
    }

    public static GetEventPostviewImageSizeParams getPostviewImageSize(boolean isPolling) {
        GetEventPostviewImageSizeParams clone = null;
        String current = CameraProxyPostviewImageSize.get();
        String[] available = CameraProxyPostviewImageSize.getAvailable();
        synchronized (s_PostviewImageSizeParamsLock) {
            if (isPolling) {
                if (s_PostviewImageSizeParams != null && !s_PostviewImageSizeUpdated) {
                }
            }
            s_PostviewImageSizeUpdated = false;
            if (s_PostviewImageSizeParams == null) {
                s_PostviewImageSizeParams = createGetEventPostviewImageSizeParams(null);
            }
            s_PostviewImageSizeParams.currentPostviewImageSize = current;
            s_PostviewImageSizeParams.postviewImageSizeCandidates = available;
            clone = createGetEventPostviewImageSizeParams(s_PostviewImageSizeParams);
        }
        return clone;
    }

    private static GetEventExposureCompensationParams createGetEventExposureCompensationParams(GetEventExposureCompensationParams original) {
        GetEventExposureCompensationParams exposureCompensationParams = new GetEventExposureCompensationParams();
        exposureCompensationParams.type = "exposureCompensation";
        if (original != null) {
            exposureCompensationParams.currentExposureCompensation = original.currentExposureCompensation;
            exposureCompensationParams.maxExposureCompensation = original.maxExposureCompensation;
            exposureCompensationParams.minExposureCompensation = original.minExposureCompensation;
            exposureCompensationParams.stepIndexOfExposureCompensation = original.stepIndexOfExposureCompensation;
        } else {
            exposureCompensationParams.currentExposureCompensation = 0;
            exposureCompensationParams.maxExposureCompensation = 0;
            exposureCompensationParams.minExposureCompensation = 0;
            exposureCompensationParams.stepIndexOfExposureCompensation = 0;
        }
        return exposureCompensationParams;
    }

    public static GetEventExposureCompensationParams getExposureCompensation(boolean isPolling) {
        GetEventExposureCompensationParams ref = null;
        if (!s_EXPOSURE_COMPENSATION_VALUE_INITIALIZED || StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.e(tag, "Camera is not opened yet to get exposure compensation params.");
        } else {
            synchronized (s_ExposureCompensationParamsLock) {
                if (isPolling) {
                    if (!s_ExposureCompensationUpdated) {
                    }
                }
                s_ExposureCompensationUpdated = false;
                ref = s_ExposureCompensationParams;
                s_ExposureCompensationParams = createGetEventExposureCompensationParams(ref);
            }
        }
        return ref;
    }

    public static boolean updateExposureCompensationParams(int current, int maxAvailable, int minAvailable, int availableStep) {
        boolean z = true;
        synchronized (s_ExposureCompensationParamsLock) {
            if (s_ExposureCompensationUpdated && s_ExposureCompensationParams.currentExposureCompensation.intValue() == current && s_ExposureCompensationParams.maxExposureCompensation.intValue() == maxAvailable && s_ExposureCompensationParams.minExposureCompensation.intValue() == minAvailable && s_ExposureCompensationParams.stepIndexOfExposureCompensation.intValue() == availableStep) {
                z = false;
            } else {
                s_ExposureCompensationParams.currentExposureCompensation = Integer.valueOf(current);
                if (s_ExposureCompensationParams.maxExposureCompensation.intValue() != maxAvailable || s_ExposureCompensationParams.minExposureCompensation.intValue() != minAvailable || s_ExposureCompensationParams.stepIndexOfExposureCompensation.intValue() != availableStep) {
                    s_ExposureCompensationParams.maxExposureCompensation = Integer.valueOf(maxAvailable);
                    s_ExposureCompensationParams.minExposureCompensation = Integer.valueOf(minAvailable);
                    s_ExposureCompensationParams.stepIndexOfExposureCompensation = Integer.valueOf(availableStep);
                }
                if (!s_EXPOSURE_COMPENSATION_VALUE_INITIALIZED) {
                    updateAvailableApiList();
                    s_EXPOSURE_COMPENSATION_VALUE_INITIALIZED = true;
                }
                s_ExposureCompensationUpdated = true;
            }
        }
        return z;
    }

    private static GetEventContShootingModeParams createGetEventContShootingModeParams(GetEventContShootingModeParams original) {
        GetEventContShootingModeParams contShootingModeParams = new GetEventContShootingModeParams();
        contShootingModeParams.type = "contShootingMode";
        if (original != null) {
            contShootingModeParams.contShootingMode = original.contShootingMode;
            contShootingModeParams.candidate = (String[]) Arrays.copyOf(original.candidate, original.candidate.length);
        } else {
            contShootingModeParams.contShootingMode = s_INVALID_CONTSHOOTMODE_VALUE;
            contShootingModeParams.candidate = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        return contShootingModeParams;
    }

    public static GetEventContShootingModeParams peekContShootingModeParams() {
        GetEventContShootingModeParams ref;
        synchronized (s_ContShootingModeParamsLock) {
            ref = s_ContShootingModeParams;
        }
        return ref;
    }

    public static GetEventContShootingModeParams getContShootingMode(boolean isPolling) {
        GetEventContShootingModeParams ref = null;
        if (!s_CONTSHOOTINGMODE_VALUE_INITIALIZED || StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.e(tag, "Camera is not opened yet to get contShootingMode params.");
        } else {
            synchronized (s_ContShootingModeParamsLock) {
                if (isPolling) {
                    if (!s_ContShootingModeUpdated) {
                    }
                }
                if (s_ContShootingModeParams.contShootingMode != null) {
                    s_ContShootingModeUpdated = false;
                    ref = s_ContShootingModeParams;
                    s_ContShootingModeParams = createGetEventContShootingModeParams(ref);
                }
            }
        }
        return ref;
    }

    public static boolean updateContShootingModeParams(String current, String[] candidates) {
        synchronized (s_ContShootingModeParamsLock) {
            if (s_ContShootingModeUpdated && s_ContShootingModeParams.contShootingMode == current && Arrays.equals(s_ContShootingModeParams.candidate, candidates)) {
                return false;
            }
            s_ContShootingModeParams.contShootingMode = current;
            s_ContShootingModeParams.candidate = (String[]) Arrays.copyOf(candidates, candidates.length);
            Log.v("nak", "updateContShootingModeParams :" + current + "  : " + candidates.length + "  :" + s_ContShootingModeParams.candidate);
            updateAvailableApiList();
            s_CONTSHOOTINGMODE_VALUE_INITIALIZED = true;
            s_ContShootingModeUpdated = true;
            return true;
        }
    }

    public static String peekContShootingMode() {
        synchronized (s_ContShootingModeParamsLock) {
            if (s_ContShootingModeParams != null) {
                return s_ContShootingModeParams.contShootingMode;
            }
            return null;
        }
    }

    private static GetEventContShootingSpeedParams createGetEventContShootingSpeedParams(GetEventContShootingSpeedParams original) {
        GetEventContShootingSpeedParams contShootingSpeedParams = new GetEventContShootingSpeedParams();
        contShootingSpeedParams.type = "contShootingSpeed";
        if (original != null) {
            contShootingSpeedParams.contShootingSpeed = original.contShootingSpeed;
            contShootingSpeedParams.candidate = (String[]) Arrays.copyOf(original.candidate, original.candidate.length);
        } else {
            contShootingSpeedParams.contShootingSpeed = s_INVALID_CONTSHOOTSPEED_VALUE;
            contShootingSpeedParams.candidate = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        return contShootingSpeedParams;
    }

    public static GetEventContShootingSpeedParams peekContShootingSpeed() {
        GetEventContShootingSpeedParams ref;
        synchronized (s_ContShootingSpeedParamsLock) {
            ref = s_ContShootingSpeedParams;
        }
        return ref;
    }

    public static GetEventContShootingSpeedParams getContShootingSpeed(boolean isPolling) {
        GetEventContShootingSpeedParams ref = null;
        if (!s_CONTSHOOTINGSPEED_VALUE_INITIALIZED || StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.e(tag, "Camera is not opened yet to get contShootingSpeed params.");
        } else {
            synchronized (s_ContShootingSpeedParamsLock) {
                if (isPolling) {
                    if (!s_ContShootingSpeedUpdated) {
                    }
                }
                if (s_ContShootingSpeedParams.contShootingSpeed != null) {
                    s_ContShootingSpeedUpdated = false;
                    ref = s_ContShootingSpeedParams;
                    s_ContShootingSpeedParams = createGetEventContShootingSpeedParams(ref);
                }
            }
        }
        return ref;
    }

    public static boolean updateContShootingSpeedParams(String current, String[] candidates) {
        synchronized (s_ContShootingSpeedParamsLock) {
            if (s_ContShootingSpeedUpdated && s_ContShootingSpeedParams.contShootingSpeed == current && Arrays.equals(s_ContShootingSpeedParams.candidate, candidates)) {
                return false;
            }
            s_ContShootingSpeedParams.contShootingSpeed = current;
            s_ContShootingSpeedParams.candidate = (String[]) Arrays.copyOf(candidates, candidates.length);
            updateAvailableApiList();
            s_CONTSHOOTINGSPEED_VALUE_INITIALIZED = true;
            s_ContShootingSpeedUpdated = true;
            return true;
        }
    }

    public static GetEventContShootingParams createGetEventContShootingParams(List<String> filePathArray) {
        String postviewSize;
        GetEventContShootingParams param = new GetEventContShootingParams();
        param.type = "contShooting";
        if (filePathArray != null) {
            param.contShootingUrl = new ContShootingUrlParams[filePathArray.size()];
            if (CameraProxyPostviewImageSize.isSizeOriginal()) {
                postviewSize = "?size=Origin";
            } else {
                postviewSize = "?size=Scn";
            }
            String continuousUrl = SRCtrlEnvironment.getInstance().getContinuousShootBaseURL();
            for (int i = 0; i < filePathArray.size(); i++) {
                param.contShootingUrl[i] = new ContShootingUrlParams();
                String filePath = filePathArray.get(i);
                param.contShootingUrl[i].postviewUrl = continuousUrl + filePath + postviewSize;
                param.contShootingUrl[i].thumbnailUrl = continuousUrl + filePath + "?size=Thumbnail";
            }
            Log.v(tag, "createGetEventContShootingParams " + param.contShootingUrl[0].postviewUrl + ExposureModeController.SOFT_SNAP + param.contShootingUrl[0].thumbnailUrl);
        }
        return param;
    }

    public static void updateContShootingParams(List<String> filePathArray) {
        synchronized (s_ContShootingParamsLock) {
            if (filePathArray.size() > 0) {
                s_ContShootingParams = createGetEventContShootingParams(filePathArray);
                s_ContShootingUpdated = true;
            }
        }
    }

    public static GetEventContShootingParams peekContShootingParams() {
        GetEventContShootingParams ref;
        synchronized (s_ContShootingParamsLock) {
            ref = s_ContShootingParams;
        }
        return ref;
    }

    public static GetEventContShootingParams getContShootingParams(boolean isPolling) {
        GetEventContShootingParams getEventContShootingParams = null;
        synchronized (s_ContShootingParamsLock) {
            if (isPolling) {
                if (!s_ContShootingUpdated) {
                }
            }
            if (s_ContShootingParams != null && s_ContShootingParams.contShootingUrl != null && s_ContShootingParams.contShootingUrl.length != 0) {
                getEventContShootingParams = s_ContShootingParams;
                s_ContShootingParams = s_ContShootingParams_EMPTY;
                s_ContShootingUpdated = false;
            }
        }
        return getEventContShootingParams;
    }

    private static GetEventSelfTimerParams createGetEventSelftimerParams(GetEventSelfTimerParams original) {
        GetEventSelfTimerParams selfTimerParams = new GetEventSelfTimerParams();
        selfTimerParams.type = "selfTimer";
        if (original != null) {
            selfTimerParams.currentSelfTimer = original.currentSelfTimer;
            selfTimerParams.selfTimerCandidates = Arrays.copyOf(original.selfTimerCandidates, original.selfTimerCandidates.length);
        } else {
            selfTimerParams.currentSelfTimer = 0;
            selfTimerParams.selfTimerCandidates = SRCtrlConstants.s_EMPTY_INT_ARRAY;
        }
        return selfTimerParams;
    }

    public static GetEventSelfTimerParams getSelfTimer(boolean isPolling) {
        GetEventSelfTimerParams ref = null;
        if (!s_SELFTIMER_VALUE_INITIALIZED || StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.e(tag, "Camera is not opened yet to get Selftimer params.");
        } else {
            synchronized (s_SelfTimerParamsLock) {
                if (isPolling) {
                    if (!s_SelfTimerUpdated) {
                    }
                }
                s_SelfTimerUpdated = false;
                ref = s_SelfTimerParams;
                s_SelfTimerParams = createGetEventSelftimerParams(ref);
            }
        }
        return ref;
    }

    public static boolean updateSelfTimerParams(int current, int[] candidates) {
        boolean z = true;
        synchronized (s_SelfTimerParamsLock) {
            if (s_SelfTimerUpdated && s_SelfTimerParams.currentSelfTimer.intValue() == current && Arrays.equals(s_SelfTimerParams.selfTimerCandidates, candidates)) {
                z = false;
            } else {
                s_SelfTimerParams.currentSelfTimer = Integer.valueOf(current);
                s_SelfTimerParams.selfTimerCandidates = Arrays.copyOf(candidates, candidates.length);
                if (!s_SELFTIMER_VALUE_INITIALIZED) {
                    updateAvailableApiList();
                    s_SELFTIMER_VALUE_INITIALIZED = true;
                }
                s_SelfTimerUpdated = true;
            }
        }
        return z;
    }

    /* loaded from: classes.dex */
    public static class CameraSettingChangeListener implements NotificationListener {
        private String[] TAGS = {"Aperture", CameraNotificationManager.FOCUS_AREA_INFO, CameraNotificationManager.ZOOM_INFO_CHANGED};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals("Aperture")) {
                String fNumber = ParamsGenerator.access$000();
                if (fNumber != null) {
                    String[] fNumberCandidates = ParamsGenerator.access$100();
                    ParamsGenerator.updateFNumberParams(fNumber, fNumberCandidates);
                    ServerEventHandler.getInstance().onServerStatusChanged();
                    return;
                }
                return;
            }
            if (tag.equals(CameraNotificationManager.FOCUS_AREA_INFO) || tag.equals(CameraNotificationManager.ZOOM_INFO_CHANGED)) {
                FocusAreaController fac = FocusAreaController.getInstance();
                try {
                    String focusAreaInBase = fac.getValue();
                    String focusArea = CameraOperationFocusArea.getIdFromBase(focusAreaInBase);
                    List<String> availableFocusAreaInBase = fac.getAvailableValue();
                    String[] availableFocusArea = CameraOperationFocusArea.getIdFromBase(availableFocusAreaInBase);
                    boolean updated = ParamsGenerator.updateFocusAreaParams(focusArea, availableFocusArea);
                    if (updated) {
                        ParamsGenerator.updateAvailableApiList();
                        ServerEventHandler.getInstance().onServerStatusChanged();
                        return;
                    }
                    return;
                } catch (IController.NotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            Log.e(tag, "Unknown camera change: " + tag);
        }
    }

    public static void startCameraSettingListener() {
        CameraSettingChangeListener cameraSettingChangeListener = null;
        if (s_cameraChangeListener != null) {
            CameraSettingChangeListener cameraSettingChangeListener2 = s_cameraChangeListener.get();
            cameraSettingChangeListener = cameraSettingChangeListener2;
        }
        if (cameraSettingChangeListener == null) {
            cameraSettingChangeListener = new CameraSettingChangeListener();
            s_cameraChangeListener = new WeakReference<>(cameraSettingChangeListener);
        }
        reset();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.setNotificationListener(cameraSettingChangeListener);
        notifier.setNotificationListener(CameraOperationShutterSpeed.getNotificationListener());
        notifier.setNotificationListener(CameraOperationShootMode.getNotificationListener());
        notifier.setNotificationListener(CameraOperationExposureMode.getNotificationListener());
        notifier.setNotificationListener(CameraOperationWhiteBalance.getNotificationListener());
        notifier.setNotificationListener(CameraOperationIsoNumber.getNotificationListener());
        notifier.setNotificationListener(CameraOperationProgramShift.getNotificationListener());
        notifier.setNotificationListener(CameraOperationFlashMode.getNotificationListener());
        notifier.setNotificationListener(CameraOperationZoom.getNotificationListener());
        notifier.setNotificationListener(CameraOperationSelfTimer.getNotificationListener());
        notifier.setNotificationListener(CameraOperationExposureCompensation.getNotificationListener());
        notifier.setNotificationListener(CameraOperationFNumber.getNotificationListener());
        notifier.setNotificationListener(CameraOperationFocusMode.getNotificationListener());
        notifier.setNotificationListener(CameraOperationRecordingTime.getNotificationListener());
        notifier.setNotificationListener(CameraOperationZoomSetting.getNotificationListener());
        if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            notifier.setNotificationListener(CameraOperationContShootingMode.getNotificationListener());
            notifier.setNotificationListener(CameraOperationContShootingSpeed.getNotificationListener());
        }
        CameraOperationStorageInformation.setMediaNotificationListener();
        CameraOperationBatteryInfo.setNotificationListener();
        notifier.setNotificationListener(FrameInfoLoader.getCameraNotificationListener());
        DisplayModeObserver.getInstance().setNotificationListener(FrameInfoLoader.getDisplayModeNotificationListener());
        notifier.setNotificationListener(CameraOperationHalfPressShutter.getNotificationListener());
        notifier.setNotificationListener(CameraOperationSilentShooting.getNotificationListener());
        SRCtrlNotificationManager.getInstance().setNotificationListener(CameraOperationSilentShooting.getNotificationListener());
        if (SRCtrlEnvironment.getInstance().isEnableAttachedLensInfo()) {
            notifier.setNotificationListener(CameraOperationAttachedLensInfo.getNotificationListener());
        }
    }

    public static void stopCameraSettingListener() {
        CameraSettingChangeListener cameraSettingChangeListener = null;
        if (s_cameraChangeListener != null) {
            CameraSettingChangeListener cameraSettingChangeListener2 = s_cameraChangeListener.get();
            cameraSettingChangeListener = cameraSettingChangeListener2;
        }
        if (cameraSettingChangeListener != null) {
            CameraNotificationManager notifier = CameraNotificationManager.getInstance();
            notifier.removeNotificationListener(cameraSettingChangeListener);
            notifier.removeNotificationListener(CameraOperationShutterSpeed.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationShootMode.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationExposureMode.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationWhiteBalance.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationIsoNumber.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationProgramShift.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationFlashMode.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationZoom.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationSelfTimer.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationExposureCompensation.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationFNumber.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationFocusMode.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationRecordingTime.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationZoomSetting.getNotificationListener());
            if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
                notifier.removeNotificationListener(CameraOperationContShootingMode.getNotificationListener());
                notifier.removeNotificationListener(CameraOperationContShootingSpeed.getNotificationListener());
            }
            CameraOperationStorageInformation.removeMediaNotificationListener();
            CameraOperationBatteryInfo.removeNotificationListener();
            notifier.removeNotificationListener(FrameInfoLoader.getCameraNotificationListener());
            DisplayModeObserver.getInstance().removeNotificationListener(FrameInfoLoader.getDisplayModeNotificationListener());
            notifier.removeNotificationListener(CameraOperationHalfPressShutter.getNotificationListener());
            notifier.removeNotificationListener(CameraOperationSilentShooting.getNotificationListener());
            SRCtrlNotificationManager.getInstance().removeNotificationListener(CameraOperationSilentShooting.getNotificationListener());
            if (SRCtrlEnvironment.getInstance().isEnableAttachedLensInfo()) {
                notifier.removeNotificationListener(CameraOperationAttachedLensInfo.getNotificationListener());
            }
        }
    }

    public static void startContentsTransferListener() {
        CameraSettingChangeListener cameraSettingChangeListener = null;
        if (s_cameraChangeListener != null) {
            CameraSettingChangeListener cameraSettingChangeListener2 = s_cameraChangeListener.get();
            cameraSettingChangeListener = cameraSettingChangeListener2;
        }
        if (cameraSettingChangeListener == null) {
            CameraSettingChangeListener cameraSettingChangeListener3 = new CameraSettingChangeListener();
            s_cameraChangeListener = new WeakReference<>(cameraSettingChangeListener3);
        }
        reset();
        CameraOperationStorageInformation.setMediaNotificationListener();
        MediaPlayerManager mediaPlayer = MediaPlayerManager.getInstance();
        mediaPlayer.setNotificationListener(StreamingController.getNotificationListener());
    }

    public static void stopContentsTransferListener() {
        CameraSettingChangeListener cameraSettingChangeListener = null;
        if (s_cameraChangeListener != null) {
            CameraSettingChangeListener cameraSettingChangeListener2 = s_cameraChangeListener.get();
            cameraSettingChangeListener = cameraSettingChangeListener2;
        }
        if (cameraSettingChangeListener != null) {
            CameraOperationStorageInformation.removeMediaNotificationListener();
            MediaPlayerManager mediaPlayer = MediaPlayerManager.getInstance();
            mediaPlayer.removeNotificationListener(StreamingController.getNotificationListener());
        }
    }

    public static boolean updateAvailableApiList() {
        boolean z = true;
        synchronized (s_AvailableApiListParamsLock) {
            if (s_AvailableApiListUpdated) {
                z = false;
            } else {
                s_AvailableApiListUpdated = true;
            }
        }
        return z;
    }

    public static GetEventAvailableApiListParams getAvailableApiList(boolean isPolling) {
        GetEventAvailableApiListParams getEventAvailableApiListParams;
        boolean changed = false;
        synchronized (s_AvailableApiListParamsLock) {
            if (s_AvailableApiListUpdated || !isPolling) {
                changed = true;
            }
        }
        if (!changed) {
            return null;
        }
        synchronized (s_AvailableApiListParamsLock) {
            s_AvailableApiListUpdated = false;
        }
        String[] apiList = AvailabilityDetector.getAvailables(true);
        synchronized (s_AvailableApiListParamsLock) {
            if (s_AvailableApiListParams == null) {
                s_AvailableApiListParams = new GetEventAvailableApiListParams();
                s_AvailableApiListParams.type = "availableApiList";
                s_AvailableApiListParams.names = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            s_AvailableApiListParams.names = apiList;
            if (s_AvailableApiListParams.names == null) {
                Log.e(tag, "Available API was null.");
                s_AvailableApiListParams.names = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            getEventAvailableApiListParams = s_AvailableApiListParams;
        }
        return getEventAvailableApiListParams;
    }

    private static GetEventShutterSpeedParams createGetEventShutterSpeedParams(GetEventShutterSpeedParams original) {
        GetEventShutterSpeedParams params = new GetEventShutterSpeedParams();
        params.type = "shutterSpeed";
        if (original == null) {
            params.currentShutterSpeed = null;
            params.shutterSpeedCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            params.currentShutterSpeed = original.currentShutterSpeed;
            params.shutterSpeedCandidates = (String[]) Arrays.copyOf(original.shutterSpeedCandidates, original.shutterSpeedCandidates.length);
        }
        return params;
    }

    public static boolean updateShutterSpeedParams(String shutterSpeed, String[] shutterSpeedCandidates) {
        synchronized (s_ShutterSpeedParamsLock) {
            if (shutterSpeedCandidates == null) {
                Log.e(tag, "Shutter speed candidates value was null.");
                shutterSpeedCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            if (s_ShutterSpeedParams == null) {
                s_ShutterSpeedParams = createGetEventShutterSpeedParams(null);
            } else if (s_ShutterSpeedParams.currentShutterSpeed.equals(shutterSpeed) && Arrays.equals(shutterSpeedCandidates, s_ShutterSpeedParams.shutterSpeedCandidates)) {
                return false;
            }
            s_ShutterSpeedParams.currentShutterSpeed = shutterSpeed;
            s_ShutterSpeedParams.shutterSpeedCandidates = (String[]) Arrays.copyOf(shutterSpeedCandidates, shutterSpeedCandidates.length);
            s_ShutterSpeedUpdated = true;
            return true;
        }
    }

    public static GetEventShutterSpeedParams getShutterSpeed(boolean isPolling) {
        GetEventShutterSpeedParams ref;
        synchronized (s_ShutterSpeedParamsLock) {
            if (s_ShutterSpeedUpdated || !isPolling) {
                s_ShutterSpeedUpdated = false;
                ref = s_ShutterSpeedParams;
                if (ref == null) {
                    Log.e(tag, "<GETEVENT> GetEventShutterSpeedParams: not initialized yet.");
                } else {
                    s_ShutterSpeedParams = createGetEventShutterSpeedParams(ref);
                }
            } else {
                ref = null;
            }
        }
        return ref;
    }

    public static GetEventShutterSpeedParams peekShutterSpeedSnapshot() {
        GetEventShutterSpeedParams getEventShutterSpeedParams;
        synchronized (s_ShutterSpeedParamsLock) {
            getEventShutterSpeedParams = s_ShutterSpeedParams;
        }
        return getEventShutterSpeedParams;
    }

    private static String getFNumber() {
        return CameraOperationFNumber.get();
    }

    private static GetEventFNumberParams createGetEventFNumberParams(GetEventFNumberParams original) {
        GetEventFNumberParams params = new GetEventFNumberParams();
        params.type = "fNumber";
        if (original == null) {
            params.currentFNumber = getFNumber();
            params.fNumberCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            params.currentFNumber = original.currentFNumber;
            params.fNumberCandidates = (String[]) Arrays.copyOf(original.fNumberCandidates, original.fNumberCandidates.length);
        }
        return params;
    }

    private static String[] getFNumberAvailableCandidate() {
        return CameraOperationFNumber.getAvailable();
    }

    public static boolean updateFNumberParams(String fNumber, String[] fNumberCandidates) {
        synchronized (s_FNumberParamsLock) {
            if (s_FNumberParams == null) {
                s_FNumberParams = createGetEventFNumberParams(null);
            }
            if (fNumberCandidates == null) {
                Log.e(tag, "FNumber candidates value was null.");
                fNumberCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            if (s_FNumberUpdated && s_FNumberParams.currentFNumber != null && s_FNumberParams.currentFNumber.equals(fNumber) && Arrays.equals(fNumberCandidates, s_FNumberParams.fNumberCandidates)) {
                return false;
            }
            boolean updateAvailableApi = false;
            if ((fNumberCandidates.length == 0 && s_FNumberParams.fNumberCandidates.length != 0) || (fNumberCandidates.length != 0 && s_FNumberParams.fNumberCandidates.length == 0)) {
                updateAvailableApi = true;
            }
            s_FNumberParams.currentFNumber = fNumber;
            s_FNumberParams.fNumberCandidates = (String[]) Arrays.copyOf(fNumberCandidates, fNumberCandidates.length);
            if (updateAvailableApi) {
                updateAvailableApiList();
            }
            s_FNumberUpdated = true;
            return true;
        }
    }

    public static GetEventFNumberParams getFNumber(boolean isPolling) {
        GetEventFNumberParams ref;
        synchronized (s_FNumberParamsLock) {
            if (s_FNumberUpdated || !isPolling) {
                s_FNumberUpdated = false;
                ref = s_FNumberParams;
                if (ref == null) {
                    Log.e(tag, "<GETEVENT> GetEventFNumberParams: not initialized yet.");
                } else {
                    s_FNumberParams = createGetEventFNumberParams(ref);
                }
            } else {
                ref = null;
            }
        }
        return ref;
    }

    public static GetEventFNumberParams peekFNumberParamsSnapshot() {
        GetEventFNumberParams ref;
        synchronized (s_FNumberParamsLock) {
            ref = s_FNumberParams;
        }
        return ref;
    }

    private static GetEventFocusModeParams createGetEventFocusModeParams(GetEventFocusModeParams original) {
        GetEventFocusModeParams params = new GetEventFocusModeParams();
        params.type = "focusMode";
        if (original == null) {
            params.currentFocusMode = null;
            params.focusModeCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            params.currentFocusMode = original.currentFocusMode;
            params.focusModeCandidates = (String[]) Arrays.copyOf(original.focusModeCandidates, original.focusModeCandidates.length);
        }
        return params;
    }

    public static boolean updateFocusModeParams(String focusMode, String[] focusModeCandidates) {
        synchronized (s_FocusModeParamsLock) {
            if (s_FocusModeParams == null) {
                s_FocusModeParams = createGetEventFocusModeParams(null);
            }
            if (focusModeCandidates == null) {
                Log.e(tag, "FocusMode candidates value was null.");
                focusModeCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            if (TextUtils.equals(s_FocusModeParams.currentFocusMode, focusMode) && Arrays.equals(focusModeCandidates, s_FocusModeParams.focusModeCandidates)) {
                return false;
            }
            s_FocusModeParams.currentFocusMode = focusMode;
            s_FocusModeParams.focusModeCandidates = (String[]) Arrays.copyOf(focusModeCandidates, focusModeCandidates.length);
            s_FocusModeUpdated = true;
            return true;
        }
    }

    public static GetEventFocusModeParams peekFocusModeParamsSnapshot() {
        GetEventFocusModeParams ref;
        synchronized (s_FocusModeParamsLock) {
            ref = s_FocusModeParams;
        }
        return ref;
    }

    public static GetEventFocusModeParams getFocusModeParams(boolean isPolling) {
        GetEventFocusModeParams ref = null;
        if (!StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            synchronized (s_FocusModeParamsLock) {
                if (s_FocusModeParams != null && s_FocusModeParams.currentFocusMode != null) {
                    if (s_FocusModeUpdated || !isPolling) {
                        s_FocusModeUpdated = false;
                        ref = s_FocusModeParams;
                        s_FocusModeParams = createGetEventFocusModeParams(ref);
                    }
                }
            }
        }
        return ref;
    }

    public static boolean isFocusControl() {
        return s_isFocusControl;
    }

    public static void setFocusControl(boolean focusControlOn) {
        s_isFocusControl = focusControlOn;
    }

    private static String getFocusArea() {
        if (s_FocusAreaParams == null) {
            return null;
        }
        return s_FocusAreaParams.currentFocusArea;
    }

    private static _GetEventFocusAreaParams createGetEventFocusAreaParams(_GetEventFocusAreaParams original) {
        _GetEventFocusAreaParams params = new _GetEventFocusAreaParams();
        params.type = "focusArea";
        if (original == null) {
            params.currentFocusArea = null;
            params.focusAreaCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            params.currentFocusArea = original.currentFocusArea;
            params.focusAreaCandidates = (String[]) Arrays.copyOf(original.focusAreaCandidates, original.focusAreaCandidates.length);
        }
        return params;
    }

    private static String[] getFocusAreaAvailableCandidate() {
        return s_FocusAreaParams == null ? SRCtrlConstants.s_EMPTY_STRING_ARRAY : s_FocusAreaParams.focusAreaCandidates;
    }

    public static boolean updateFocusAreaParams(String focusArea, String[] focusAreaCandidates) {
        synchronized (s_FocusAreaParamsLock) {
            if (s_FocusAreaParams == null) {
                s_FocusAreaParams = createGetEventFocusAreaParams(null);
            }
            if (focusAreaCandidates == null) {
                Log.e(tag, "FocusArea candidates value was null.");
                focusAreaCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            if (TextUtils.equals(s_FocusAreaParams.currentFocusArea, focusArea) && Arrays.equals(focusAreaCandidates, s_FocusAreaParams.focusAreaCandidates)) {
                return false;
            }
            s_FocusAreaParams.currentFocusArea = focusArea;
            s_FocusAreaParams.focusAreaCandidates = (String[]) Arrays.copyOf(focusAreaCandidates, focusAreaCandidates.length);
            s_FocusAreaUpdated = true;
            return true;
        }
    }

    public static _GetEventFocusAreaParams peekFocusAreaParamsSnapshot() {
        _GetEventFocusAreaParams ref;
        synchronized (s_FocusAreaParamsLock) {
            ref = s_FocusAreaParams;
        }
        return ref;
    }

    public static _GetEventFocusAreaParams getFocusArea(boolean isPolling) {
        _GetEventFocusAreaParams ref;
        synchronized (s_FocusAreaParamsLock) {
            if (s_FocusAreaUpdated || !isPolling) {
                s_FocusAreaUpdated = false;
                ref = s_FocusAreaParams;
                if (ref == null) {
                    Log.e(tag, "<GETEVENT> GetEventFocusAreaParams: not initialized yet.");
                } else {
                    s_FocusAreaParams = createGetEventFocusAreaParams(ref);
                }
            } else {
                ref = null;
            }
        }
        return ref;
    }

    private static GetEventTouchAFPositionParams createGetEventTouchAFPositionParams(GetEventTouchAFPositionParams original) {
        GetEventTouchAFPositionParams param = new GetEventTouchAFPositionParams();
        param.type = "touchAFPosition";
        if (original == null) {
            param.currentSet = false;
            param.currentTouchCoordinates = new double[0];
        } else {
            param.currentSet = original.currentSet;
            param.currentTouchCoordinates = Arrays.copyOf(original.currentTouchCoordinates, original.currentTouchCoordinates.length);
        }
        return param;
    }

    public static boolean updateTouchAFPostionParams(boolean bSet) {
        boolean z = true;
        synchronized (s_TouchAFPositionParamsLock) {
            TouchAFCurrentPositionParams tmp = CameraOperationTouchAFPosition.get();
            if (s_TouchAFPositionUpdated && s_TouchAFPositionParams.currentSet.booleanValue() == bSet) {
                z = false;
            } else {
                s_TouchAFPositionParams.currentSet = tmp.set;
                s_TouchAFPositionUpdated = true;
            }
        }
        return z;
    }

    public static GetEventTouchAFPositionParams getTouchAFPosition(boolean isPolling) {
        GetEventTouchAFPositionParams ref;
        synchronized (s_TouchAFPositionParamsLock) {
            if (s_TouchAFPositionUpdated || !isPolling) {
                s_TouchAFPositionUpdated = false;
                ref = s_TouchAFPositionParams;
                s_TouchAFPositionParams = createGetEventTouchAFPositionParams(ref);
            } else {
                ref = null;
            }
        }
        return ref;
    }

    private static GetEventWhiteBalanceParams createGetEventWhiteBalanceParams(GetEventWhiteBalanceParams original) {
        GetEventWhiteBalanceParams param = new GetEventWhiteBalanceParams();
        param.type = "whiteBalance";
        if (original == null) {
            param.currentWhiteBalanceMode = "";
            param.checkAvailability = true;
            param.currentColorTemperature = 0;
        } else {
            param.currentWhiteBalanceMode = original.currentWhiteBalanceMode;
            param.checkAvailability = original.checkAvailability;
            param.currentColorTemperature = original.currentColorTemperature;
        }
        return param;
    }

    private static boolean compareWhiteBalanceCandidates(WhiteBalanceParamCandidate l, WhiteBalanceParamCandidate r) {
        return l.whiteBalanceMode == r.whiteBalanceMode && Arrays.equals(l.colorTemperatureRange, r.colorTemperatureRange);
    }

    private static boolean compareWhiteBalanceCandidates(WhiteBalanceParamCandidate[] l, WhiteBalanceParamCandidate[] r) {
        if (l.length != r.length) {
            return false;
        }
        for (int i = 0; i < l.length; i++) {
            if (!compareWhiteBalanceCandidates(l[i], r[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean updateWhiteBalanceParams(String mode, int temperature, WhiteBalanceParamCandidate[] available) {
        synchronized (s_WhiteBalanceParamsLock) {
            boolean checkAvailability = !compareWhiteBalanceCandidates(available, s_WhiteBalanceAvaialble);
            if (!checkAvailability && s_WhiteBalanceParams.currentColorTemperature.intValue() == temperature && s_WhiteBalanceParams.currentWhiteBalanceMode.equals(mode)) {
                return false;
            }
            s_WhiteBalanceParams.currentColorTemperature = Integer.valueOf(temperature);
            if (checkAvailability) {
                s_WhiteBalanceParams.checkAvailability = true;
            }
            s_WhiteBalanceParams.currentWhiteBalanceMode = mode;
            s_WhiteBalanceAvaialble = available;
            s_WhiteBalanceUpdated = true;
            return true;
        }
    }

    public static GetEventWhiteBalanceParams getWhiteBalanceParams(boolean isPolling) {
        GetEventWhiteBalanceParams ref;
        synchronized (s_WhiteBalanceParamsLock) {
            if (s_WhiteBalanceUpdated || !isPolling) {
                s_WhiteBalanceUpdated = false;
                ref = s_WhiteBalanceParams;
                s_WhiteBalanceParams = createGetEventWhiteBalanceParams(ref);
                if (!isPolling) {
                    ref.checkAvailability = true;
                }
            } else {
                ref = null;
            }
        }
        return ref;
    }

    public static void initBootShootModeParams() {
        synchronized (s_ShootModeParamsLock) {
            s_initShootModeParams.currentShootMode = CameraOperationShootMode.get();
            s_initShootModeParams.shootModeCandidates = CameraOperationShootMode.getAvailable();
        }
    }

    private static GetEventShootModeParams createGetEventShootModeParams(GetEventShootModeParams original) {
        GetEventShootModeParams param = new GetEventShootModeParams();
        param.type = "shootMode";
        if (original == null) {
            param.currentShootMode = null;
            param.shootModeCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            param.currentShootMode = original.currentShootMode;
            param.shootModeCandidates = (String[]) Arrays.copyOf(original.shootModeCandidates, original.shootModeCandidates.length);
        }
        return param;
    }

    public static boolean updateShootModeParams(String mode, String[] candidates) {
        synchronized (s_ShootModeParamsLock) {
            if (s_ShootModeUpdated && s_ShootModeParams.currentShootMode == mode && Arrays.equals(s_ShootModeParams.shootModeCandidates, candidates)) {
                return false;
            }
            s_ShootModeParams.currentShootMode = mode;
            s_ShootModeParams.shootModeCandidates = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_ShootModeUpdated = true;
            s_initShootModeParams.currentShootMode = s_ShootModeParams.currentShootMode;
            s_initShootModeParams.shootModeCandidates = s_ShootModeParams.shootModeCandidates;
            updateAvailableApiList();
            return true;
        }
    }

    public static GetEventShootModeParams getShootModeParams(boolean isPolling) {
        GetEventShootModeParams ref = null;
        synchronized (s_ShootModeParamsLock) {
            if (s_ShootModeParams.currentShootMode == null) {
                if (s_initShootModeParams.currentShootMode == null) {
                    Log.e(tag, "s_initShootModeParams.currentShootMode is NULL!!");
                } else {
                    s_ShootModeParams.currentShootMode = s_initShootModeParams.currentShootMode;
                    s_ShootModeParams.shootModeCandidates = s_initShootModeParams.shootModeCandidates;
                    s_ShootModeUpdated = true;
                }
            }
            if (s_ShootModeUpdated || !isPolling) {
                s_ShootModeUpdated = false;
                ref = s_ShootModeParams;
                s_ShootModeParams = createGetEventShootModeParams(ref);
            }
        }
        return ref;
    }

    public static GetEventShootModeParams peekShootModeParamsSnapshot() {
        GetEventShootModeParams ref;
        synchronized (s_ShootModeParamsLock) {
            ref = s_ShootModeParams;
        }
        return ref;
    }

    private static GetEventExposureModeParams createGetEventExposureModeParams(GetEventExposureModeParams original) {
        GetEventExposureModeParams param = new GetEventExposureModeParams();
        param.type = "exposureMode";
        if (original == null) {
            param.currentExposureMode = null;
            param.exposureModeCandidates = null;
        } else {
            param.currentExposureMode = original.currentExposureMode;
            param.exposureModeCandidates = (String[]) Arrays.copyOf(original.exposureModeCandidates, original.exposureModeCandidates.length);
        }
        return param;
    }

    public static boolean updateExposureModeParams(String mode, String[] candidates) {
        synchronized (s_ExposureModeParamsLock) {
            if (s_ExposureModeUpdated && s_ExposureModeParams.currentExposureMode == mode && Arrays.equals(s_ExposureModeParams.exposureModeCandidates, candidates)) {
                return false;
            }
            s_ExposureModeParams.currentExposureMode = mode;
            s_ExposureModeParams.exposureModeCandidates = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_ExposureModeUpdated = true;
            updateAvailableApiList();
            CameraNotificationManager notifier = CameraNotificationManager.getInstance();
            notifier.requestNotify(CameraNotificationManager.WB_MODE_CHANGE);
            notifier.requestNotify(CameraNotificationManager.WB_DETAIL_CHANGE);
            return true;
        }
    }

    public static GetEventExposureModeParams getExposureModeParams(boolean isPolling) {
        GetEventExposureModeParams ref = null;
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION) || s_ExposureModeParams.currentExposureMode == null) {
            Log.e(tag, "<GETEVENT> GetEventExposureModeParams: not initialized yet.");
        } else {
            synchronized (s_ExposureModeParamsLock) {
                if (s_ExposureModeUpdated || !isPolling) {
                    s_ExposureModeUpdated = false;
                    ref = s_ExposureModeParams;
                    s_ExposureModeParams = createGetEventExposureModeParams(ref);
                }
            }
        }
        return ref;
    }

    public static GetEventExposureModeParams peekExposureModeParamsSnapshot() {
        GetEventExposureModeParams ref;
        synchronized (s_ExposureModeParamsLock) {
            ref = s_ExposureModeParams;
        }
        return ref;
    }

    private static GetEventIsoSpeedRateParams createGetEventIsoSpeedRateParams(GetEventIsoSpeedRateParams original) {
        GetEventIsoSpeedRateParams param = new GetEventIsoSpeedRateParams();
        param.type = "isoSpeedRate";
        if (original == null) {
            param.currentIsoSpeedRate = null;
            param.isoSpeedRateCandidates = null;
        } else {
            param.currentIsoSpeedRate = original.currentIsoSpeedRate;
            param.isoSpeedRateCandidates = (String[]) Arrays.copyOf(original.isoSpeedRateCandidates, original.isoSpeedRateCandidates.length);
        }
        return param;
    }

    public static boolean updateIsoSpeedRateParams(String iso, String[] candidates) {
        String exposureMode;
        synchronized (s_IsoSpeedRateParamsLock) {
            if (TextUtils.equals(s_IsoSpeedRateParams.currentIsoSpeedRate, iso) && Arrays.equals(s_IsoSpeedRateParams.isoSpeedRateCandidates, candidates)) {
                return false;
            }
            String previousIso = s_IsoSpeedRateParams.currentIsoSpeedRate;
            s_IsoSpeedRateParams.currentIsoSpeedRate = iso;
            s_IsoSpeedRateParams.isoSpeedRateCandidates = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_IsoSpeedRateUpdated = true;
            if (!iso.equals(previousIso) && (("AUTO".equals(iso) || "AUTO".equals(previousIso)) && (exposureMode = peekExposureModeParamsSnapshot().currentExposureMode) != null && "Manual".equals(exposureMode))) {
                updateAvailableApiList();
            }
            return true;
        }
    }

    public static boolean updateIsoSpeedRateParams(String iso) {
        String exposureMode;
        boolean z = true;
        synchronized (s_IsoSpeedRateParamsLock) {
            if (TextUtils.equals(s_IsoSpeedRateParams.currentIsoSpeedRate, iso)) {
                z = false;
            } else {
                String previousIso = s_IsoSpeedRateParams.currentIsoSpeedRate;
                s_IsoSpeedRateParams.currentIsoSpeedRate = iso;
                s_IsoSpeedRateUpdated = true;
                if (("AUTO".equals(iso) || "AUTO".equals(previousIso)) && (exposureMode = peekExposureModeParamsSnapshot().currentExposureMode) != null && "Manual".equals(exposureMode)) {
                    updateAvailableApiList();
                }
            }
        }
        return z;
    }

    public static GetEventIsoSpeedRateParams getIsoSpeedRateParams(boolean isPolling) {
        GetEventIsoSpeedRateParams ref = null;
        if (s_IsoSpeedRateParams.currentIsoSpeedRate == null || StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION)) {
            Log.e(tag, "<GETEVENT> GetEventIsoSpeedRateParams: not initialized yet.");
        } else {
            boolean firstGet = false;
            if (s_IsoSpeedRateParams.currentIsoSpeedRate == null) {
                s_IsoSpeedRateParams.currentIsoSpeedRate = CameraProxyIsoNumber.get();
                s_IsoSpeedRateParams.isoSpeedRateCandidates = CameraProxyIsoNumber.getAvailable();
                firstGet = true;
            }
            synchronized (s_IsoSpeedRateParamsLock) {
                if (s_IsoSpeedRateUpdated || !isPolling || firstGet) {
                    s_IsoSpeedRateUpdated = false;
                    ref = s_IsoSpeedRateParams;
                    s_IsoSpeedRateParams = createGetEventIsoSpeedRateParams(ref);
                }
            }
        }
        return ref;
    }

    public static GetEventIsoSpeedRateParams peekIsoSpeedRateParamsSnapshot() {
        GetEventIsoSpeedRateParams ref;
        synchronized (s_IsoSpeedRateParamsLock) {
            ref = s_IsoSpeedRateParams;
        }
        return ref;
    }

    private static GetEventProgramShiftParams createGetEventProgramShiftParams(GetEventProgramShiftParams original) {
        GetEventProgramShiftParams param = new GetEventProgramShiftParams();
        param.type = "programShift";
        if (original == null) {
            param.isShifted = false;
        } else {
            param.isShifted = original.isShifted;
        }
        return param;
    }

    public static boolean updateProgramShiftParams(boolean isShifted) {
        boolean z = true;
        synchronized (s_ProgramShiftParamsLock) {
            if (s_ProgramShiftUpdated && s_ProgramShiftParams != null && s_ProgramShiftParams.isShifted.booleanValue() == isShifted) {
                z = false;
            } else {
                if (s_ProgramShiftParams == null) {
                    s_ProgramShiftParams = createGetEventProgramShiftParams(null);
                }
                s_ProgramShiftParams.isShifted = Boolean.valueOf(isShifted);
                s_ProgramShiftUpdated = true;
            }
        }
        return z;
    }

    public static GetEventProgramShiftParams getProgramShift(boolean isPolling) {
        GetEventProgramShiftParams ref = null;
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION)) {
            Log.e(tag, "<GETEVENT> GetEventProgramShiftParams: not initialized yet.");
        } else {
            synchronized (s_ProgramShiftParamsLock) {
                if (s_ProgramShiftParams == null) {
                    s_ProgramShiftParams = createGetEventProgramShiftParams(null);
                    s_ProgramShiftParams.isShifted = false;
                    s_ProgramShiftUpdated = true;
                }
                if (s_ProgramShiftUpdated || !isPolling) {
                    s_ProgramShiftUpdated = false;
                    ref = s_ProgramShiftParams;
                    s_ProgramShiftParams = createGetEventProgramShiftParams(ref);
                }
            }
        }
        return ref;
    }

    private static GetEventTakePictureParams createGetEventTakePictureParams(String[] urlArray) {
        GetEventTakePictureParams param = new GetEventTakePictureParams();
        param.type = "takePicture";
        param.takePictureUrl = urlArray;
        return param;
    }

    public static void updateTakePictureParams(String[] urlArray) {
        if (urlArray.length > 0) {
            synchronized (s_TakePictureParamsListLock) {
                GetEventTakePictureParams param = createGetEventTakePictureParams(urlArray);
                s_TakePictureUpdated = true;
                s_TakePictureParamsList.add(param);
            }
        }
    }

    public static GetEventTakePictureParams[] getTakePicture(boolean isPolling) {
        GetEventTakePictureParams[] getEventTakePictureParamsArr;
        synchronized (s_TakePictureParamsListLock) {
            if (isPolling) {
                if (!s_TakePictureUpdated) {
                    getEventTakePictureParamsArr = s_TakePictureParamsArray_EMPTY;
                }
            }
            getEventTakePictureParamsArr = (GetEventTakePictureParams[]) s_TakePictureParamsList.toArray(s_TakePictureParamsArray_EMPTY);
            s_TakePictureParamsList.clear();
            s_TakePictureUpdated = false;
        }
        return getEventTakePictureParamsArr;
    }

    private static GetEventBulbShootingParams createGetEventBulbShootingParams(String[] urlArray) {
        GetEventBulbShootingParams param = new GetEventBulbShootingParams();
        param.type = "bulbShooting";
        int length = urlArray.length;
        BulbShootingUrlParams[] urlParamsList = new BulbShootingUrlParams[length];
        for (int i = 0; i < length; i++) {
            BulbShootingUrlParams urlParams = new BulbShootingUrlParams();
            urlParams.postviewUrl = urlArray[i];
            urlParamsList[i] = urlParams;
        }
        param.bulbShootingUrl = urlParamsList;
        return param;
    }

    public static void updateBulbShootingParams(String[] urlArray) {
        if (urlArray.length > 0) {
            synchronized (s_BulbShootingParamsLock) {
                GetEventBulbShootingParams param = createGetEventBulbShootingParams(urlArray);
                s_BulbShootingUpdated = true;
                s_BulbShootingParams = param;
            }
        }
    }

    public static GetEventBulbShootingParams getBulbShooting(boolean isPolling) {
        GetEventBulbShootingParams getEventBulbShootingParams = null;
        synchronized (s_BulbShootingParamsLock) {
            if (isPolling) {
                if (!s_BulbShootingUpdated) {
                }
            }
            getEventBulbShootingParams = s_BulbShootingParams;
            s_BulbShootingParams = null;
            s_BulbShootingUpdated = false;
        }
        return getEventBulbShootingParams;
    }

    private static GetEventBulbCapturingTimeParams createGetEventBulbCapturingTimeParams(GetEventBulbCapturingTimeParams original) {
        GetEventBulbCapturingTimeParams params = new GetEventBulbCapturingTimeParams();
        params.type = "bulbCapturingTime";
        if (original == null) {
            params.bulbCapturingTime = 0;
        } else {
            params.bulbCapturingTime = original.bulbCapturingTime;
        }
        return params;
    }

    public static boolean updateBulbCapturingTimeParams(int second) {
        boolean z = true;
        synchronized (s_GetEventBulbCapturingTimeParamsLock) {
            if (s_GetEventBulbCapturingTimeParams == null) {
                s_GetEventBulbCapturingTimeParams = createGetEventBulbCapturingTimeParams(null);
            }
            if (s_GetEventBulbCapturingTimeParams.bulbCapturingTime.intValue() == second) {
                z = false;
            } else {
                s_GetEventBulbCapturingTimeParams.bulbCapturingTime = Integer.valueOf(second);
                s_GetEventBulbCapturingTimeParamsUpdated = true;
            }
        }
        return z;
    }

    public static GetEventBulbCapturingTimeParams getBulbCapturingTimeParams(boolean isPolling) {
        GetEventBulbCapturingTimeParams ref = null;
        if (StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.v(tag, "<GETEVENT> GetEventRecordingTimeParams: not initialized yet.");
        } else {
            synchronized (s_GetEventBulbCapturingTimeParamsLock) {
                if (s_GetEventBulbCapturingTimeParamsUpdated || !isPolling) {
                    s_GetEventBulbCapturingTimeParamsUpdated = false;
                    if (s_GetEventBulbCapturingTimeParams != null) {
                        if (s_GetEventBulbCapturingTimeParams.bulbCapturingTime.intValue() != 0) {
                            ref = s_GetEventBulbCapturingTimeParams;
                            s_GetEventBulbCapturingTimeParams = createGetEventBulbCapturingTimeParams(ref);
                        }
                    }
                }
            }
        }
        return ref;
    }

    private static GetEventFlashModeParams createGetEventFlashModeParams(GetEventFlashModeParams original) {
        GetEventFlashModeParams param = new GetEventFlashModeParams();
        param.type = "flashMode";
        if (original == null) {
            param.currentFlashMode = null;
            param.flashModeCandidates = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            param.currentFlashMode = original.currentFlashMode;
            param.flashModeCandidates = (String[]) Arrays.copyOf(original.flashModeCandidates, original.flashModeCandidates.length);
        }
        return param;
    }

    public static boolean updateFlashModeParams(String mode, String[] candidates) {
        synchronized (s_FlashModeParamsLock) {
            if (s_FlashModeUpdated && s_FlashModeParams.currentFlashMode == mode && Arrays.equals(s_FlashModeParams.flashModeCandidates, candidates)) {
                return false;
            }
            boolean updateAvailableApi = !Arrays.equals(s_FlashModeParams.flashModeCandidates, candidates);
            s_FlashModeParams.currentFlashMode = mode;
            s_FlashModeParams.flashModeCandidates = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_FlashModeUpdated = true;
            if (updateAvailableApi) {
                updateAvailableApiList();
            }
            return true;
        }
    }

    public static GetEventFlashModeParams getFlashModeParams(boolean isPolling) {
        GetEventFlashModeParams ref = null;
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION) || s_FlashModeParams.currentFlashMode == null) {
            Log.e(tag, "<GETEVENT> GetEventFlashModeParams: not initialized yet.");
        } else {
            synchronized (s_FlashModeParamsLock) {
                if (s_FlashModeUpdated || !isPolling) {
                    s_FlashModeUpdated = false;
                    ref = s_FlashModeParams;
                    s_FlashModeParams = createGetEventFlashModeParams(ref);
                }
            }
        }
        return ref;
    }

    public static GetEventFlashModeParams peekFlashModeParamsSnapshot() {
        GetEventFlashModeParams ref;
        synchronized (s_FlashModeParamsLock) {
            ref = s_FlashModeParams;
        }
        return ref;
    }

    private static GetEventZoomInformationParams createGetEventZoomInformationParams(GetEventZoomInformationParams original) {
        GetEventZoomInformationParams param = new GetEventZoomInformationParams();
        param.type = "zoomInformation";
        if (original == null) {
            param.zoomPosition = -1;
            param.zoomNumberBox = -1;
            param.zoomIndexCurrentBox = -1;
            param.zoomPositionCurrentBox = -1;
        } else {
            param.zoomPosition = original.zoomPosition;
            param.zoomNumberBox = original.zoomNumberBox;
            param.zoomIndexCurrentBox = original.zoomIndexCurrentBox;
            param.zoomPositionCurrentBox = original.zoomPositionCurrentBox;
        }
        return param;
    }

    public static boolean updateZoomInformationParams(int position, int numberBox, int indexCurrentBox, int positionCurrentBox) {
        synchronized (s_ZoomInformationParamsLock) {
            if (s_ZoomInformationUpdated && s_ZoomInformationParams.zoomPosition.intValue() == position && s_ZoomInformationParams.zoomNumberBox.intValue() == numberBox && s_ZoomInformationParams.zoomIndexCurrentBox.intValue() == indexCurrentBox && s_ZoomInformationParams.zoomPositionCurrentBox.intValue() == positionCurrentBox) {
                return false;
            }
            boolean updateAvailableApi = s_ZoomInformationParams.zoomNumberBox.intValue() != numberBox;
            s_ZoomInformationParams.zoomPosition = Integer.valueOf(position);
            s_ZoomInformationParams.zoomNumberBox = Integer.valueOf(numberBox);
            s_ZoomInformationParams.zoomIndexCurrentBox = Integer.valueOf(indexCurrentBox);
            s_ZoomInformationParams.zoomPositionCurrentBox = Integer.valueOf(positionCurrentBox);
            s_ZoomInformationUpdated = true;
            if (updateAvailableApi) {
                updateAvailableApiList();
            }
            return true;
        }
    }

    public static GetEventZoomInformationParams getZoomInformation(boolean isPolling) {
        GetEventZoomInformationParams ref = null;
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION)) {
            Log.e(tag, "<GETEVENT> getZoomInformation: not initialized yet.");
        } else {
            synchronized (s_ZoomInformationParamsLock) {
                if (s_ZoomInformationUpdated || !isPolling) {
                    s_ZoomInformationUpdated = false;
                    ref = s_ZoomInformationParams;
                    s_ZoomInformationParams = createGetEventZoomInformationParams(ref);
                }
            }
        }
        return ref;
    }

    public static GetEventZoomInformationParams peekZoomInformationParamsSnapshot() {
        GetEventZoomInformationParams ref;
        synchronized (s_ZoomInformationParamsLock) {
            ref = s_ZoomInformationParams;
        }
        return ref;
    }

    private static GetEventFocusStatusParams createGetEventFocusStatusParams(GetEventFocusStatusParams original) {
        GetEventFocusStatusParams param = new GetEventFocusStatusParams();
        param.type = "focusStatus";
        if (original == null) {
            param.focusStatus = CameraOperationHalfPressShutter.FOCUS_STATUS_NOT_FORCUSING;
        } else {
            param.focusStatus = original.focusStatus;
        }
        return param;
    }

    public static boolean updateFocusStatusParams(String status) {
        boolean z = true;
        synchronized (s_FocusStatusParamsLock) {
            if (TextUtils.equals(s_FocusStatusParams.focusStatus, status)) {
                z = false;
            } else {
                s_FocusStatusParams.focusStatus = status;
                s_FocusStatusUpdated = true;
            }
        }
        return z;
    }

    public static GetEventFocusStatusParams getFocusStatus(boolean isPolling) {
        GetEventFocusStatusParams ref;
        synchronized (s_FocusStatusParamsLock) {
            if (s_FocusStatusUpdated || !isPolling) {
                s_FocusStatusUpdated = false;
                ref = s_FocusStatusParams;
                s_FocusStatusParams = createGetEventFocusStatusParams(ref);
            } else {
                ref = null;
            }
        }
        return ref;
    }

    private static GetEventCameraFunctionParams createGetEventCameraFunctionParams(GetEventCameraFunctionParams original) {
        GetEventCameraFunctionParams params = new GetEventCameraFunctionParams();
        params.type = "cameraFunction";
        if (original == null) {
            synchronized (s_CameraFunctionParamsLock) {
                if (s_CameraFunctionParams != null && s_CameraFunctionParams.currentCameraFunction != null) {
                    params.currentCameraFunction = s_CameraFunctionParams.currentCameraFunction;
                    params.cameraFunctionCandidates = s_CameraFunctionParams.cameraFunctionCandidates;
                } else {
                    params.currentCameraFunction = SRCtrlConstants.CAMERA_FUNCTION_REMOTE_SHOOTING;
                    params.cameraFunctionCandidates = SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES;
                }
            }
            Log.v(tag, "createGetEventCameraFunctionParams(" + (params.currentCameraFunction != null ? params.currentCameraFunction : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
        } else {
            params.currentCameraFunction = original.currentCameraFunction;
            params.cameraFunctionCandidates = original.cameraFunctionCandidates;
        }
        return params;
    }

    public static boolean updateCameraFunctionParams(String currentCameraFunction, String[] cameraFunctionCandidates) {
        boolean z = true;
        synchronized (s_CameraFunctionParamsLock) {
            if (s_CameraFunctionParams == null) {
                s_CameraFunctionParams = createGetEventCameraFunctionParams(null);
            }
            Log.v(tag, "updateCameraFunctionParams(" + (currentCameraFunction != null ? currentCameraFunction : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
            if (s_CameraFunctionParamsUpdated && TextUtils.equals(s_CameraFunctionParams.currentCameraFunction, currentCameraFunction)) {
                z = false;
            } else {
                Log.v(tag, "updateCameraFunctionParams() update");
                s_CameraFunctionParams.currentCameraFunction = currentCameraFunction;
                s_CameraFunctionParams.cameraFunctionCandidates = cameraFunctionCandidates;
                s_CameraFunctionParamsUpdated = true;
            }
        }
        return z;
    }

    public static GetEventCameraFunctionParams getCameraFunctionParams(boolean isPolling) {
        GetEventCameraFunctionParams ref = null;
        if (s_CameraFunctionParams == null || (s_CameraFunctionParams != null && s_CameraFunctionParams.currentCameraFunction == null)) {
            Log.v(tag, "getCameraFunctionParams() disable");
        } else {
            synchronized (s_CameraFunctionParamsLock) {
                if (s_CameraFunctionParamsUpdated || !isPolling) {
                    s_CameraFunctionParamsUpdated = false;
                    ref = s_CameraFunctionParams;
                    s_CameraFunctionParams = createGetEventCameraFunctionParams(ref);
                }
            }
        }
        return ref;
    }

    private static GetEventCameraFunctionResultParams createGetEventCameraFunctionResultParams(GetEventCameraFunctionResultParams original) {
        GetEventCameraFunctionResultParams params = new GetEventCameraFunctionResultParams();
        params.type = "cameraFunctionResult";
        if (original == null) {
            params.cameraFunctionResult = null;
            Log.v(tag, "createGetEventCameraFunctionResultParams(" + (params.cameraFunctionResult != null ? params.cameraFunctionResult : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
        } else {
            params.cameraFunctionResult = original.cameraFunctionResult;
        }
        return params;
    }

    public static boolean updateCameraFunctionResultParams(String cameraFunctionResult) {
        boolean z = true;
        synchronized (s_CameraFunctionResultParamsLock) {
            if (s_CameraFunctionResultParams == null) {
                s_CameraFunctionResultParams = createGetEventCameraFunctionResultParams(null);
            }
            Log.v(tag, "updateCameraFunctionResultParams(" + (cameraFunctionResult != null ? cameraFunctionResult : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
            if (s_CameraFunctionResultParamsUpdated && TextUtils.equals(s_CameraFunctionResultParams.cameraFunctionResult, cameraFunctionResult)) {
                z = false;
            } else {
                Log.v(tag, "updateCameraFunctionResultParams() update");
                s_CameraFunctionResultParams.cameraFunctionResult = cameraFunctionResult;
                s_CameraFunctionResultParamsUpdated = true;
            }
        }
        return z;
    }

    public static GetEventCameraFunctionResultParams getCameraFunctionResultParams(boolean isPolling) {
        GetEventCameraFunctionResultParams ref = null;
        if (s_CameraFunctionResultParams == null || (s_CameraFunctionResultParams != null && s_CameraFunctionResultParams.cameraFunctionResult == null)) {
            Log.v(tag, "getCameraFunctionResultParams() disable");
        } else {
            synchronized (s_CameraFunctionResultParamsLock) {
                if (s_CameraFunctionResultParamsUpdated || !isPolling) {
                    s_CameraFunctionResultParamsUpdated = false;
                    ref = s_CameraFunctionResultParams;
                    s_CameraFunctionResultParams = createGetEventCameraFunctionResultParams(ref);
                    s_CameraFunctionResultParams = createGetEventCameraFunctionResultParams(null);
                }
            }
        }
        return ref;
    }

    private static GetEventContinuousErrorParams[] createGetEventContinuousErrorParams() {
        GetEventContinuousErrorParams[] retParams = new GetEventContinuousErrorParams[s_continuousErrors.size()];
        int num = 0;
        for (String error : s_continuousErrors.keySet()) {
            GetEventContinuousErrorParams param = new GetEventContinuousErrorParams();
            param.type = "continuousError";
            param.continuousError = error;
            Boolean isContinued = s_continuousErrors.get(error);
            if (isContinued != null && isContinued.booleanValue()) {
                param.isContinued = true;
            } else {
                param.isContinued = false;
            }
            retParams[num] = param;
            num++;
        }
        return retParams;
    }

    public static boolean addContinuousErrorParams(String error) {
        synchronized (s_ContinuousErrorParamsLock) {
            s_continuousErrors.put(error, true);
        }
        return true;
    }

    public static boolean removeAllContinuousErrorParams() {
        boolean update = false;
        synchronized (s_ContinuousErrorParamsLock) {
            for (String error : s_continuousErrors.keySet()) {
                Boolean isContinued = s_continuousErrors.get(error);
                if (isContinued.booleanValue()) {
                    s_continuousErrors.put(error, false);
                    update |= true;
                }
            }
        }
        return update;
    }

    public static boolean removeContinuousErrorParams(String error) {
        boolean z = false;
        synchronized (s_ContinuousErrorParamsLock) {
            if (s_continuousErrors.containsKey(error)) {
                s_continuousErrors.put(error, false);
                z = true;
            }
        }
        return z;
    }

    public static GetEventContinuousErrorParams[] getContinuousErrorParams(boolean isPolling) {
        GetEventContinuousErrorParams[] continuousErrorParams;
        synchronized (s_ContinuousErrorParamsLock) {
            continuousErrorParams = createGetEventContinuousErrorParams();
            Object[] keys = s_continuousErrors.keySet().toArray();
            for (Object error : keys) {
                Boolean isContinued = s_continuousErrors.get(error);
                if (isContinued == null || !isContinued.booleanValue()) {
                    s_continuousErrors.remove(error);
                }
            }
        }
        return continuousErrorParams;
    }

    public static GetEventContinuousErrorParams[] peekContinuousErrorParamsSnapshot() {
        GetEventContinuousErrorParams[] ref;
        synchronized (s_ContinuousErrorParamsLock) {
            ref = createGetEventContinuousErrorParams();
        }
        return ref;
    }

    private static GetEventTriggeredErrorParams createGetEventTriggeredErrorParams() {
        GetEventTriggeredErrorParams param = new GetEventTriggeredErrorParams();
        param.type = "triggeredError";
        String[] retTriggeredError = new String[s_triggeredErrorList.size()];
        for (int i = 0; s_triggeredErrorList.size() > i; i++) {
            retTriggeredError[i] = s_triggeredErrorList.get(i);
        }
        param.triggeredError = retTriggeredError;
        return param;
    }

    public static boolean addTriggeredErrorParams(String error) {
        boolean z = true;
        synchronized (s_TriggeredErrorParamsLock) {
            if (s_triggeredErrorList.contains(error)) {
                z = false;
            } else {
                s_triggeredErrorList.add(error);
                s_TriggeredErrorUpdated = true;
            }
        }
        return z;
    }

    public static GetEventTriggeredErrorParams getTriggeredErrorParams(boolean isPolling) {
        synchronized (s_TriggeredErrorParamsLock) {
            if (s_TriggeredErrorUpdated) {
                s_TriggeredErrorUpdated = false;
                GetEventTriggeredErrorParams triggeredErrorParams = createGetEventTriggeredErrorParams();
                s_triggeredErrorList.clear();
                return triggeredErrorParams;
            }
            return null;
        }
    }

    public static GetEventTriggeredErrorParams peekTriggeredErrorParamsSnapshot() {
        GetEventTriggeredErrorParams ref;
        synchronized (s_TriggeredErrorParamsLock) {
            ref = createGetEventTriggeredErrorParams();
        }
        return ref;
    }

    private static GetEventBatteryInfoParams createGetEventBatteryInfoParams(GetEventBatteryInfoParams original) {
        GetEventBatteryInfoParams params = new GetEventBatteryInfoParams();
        params.type = "batteryInfo";
        if (original == null || original.batteryInfo == null || original.batteryInfo.length == 0 || original.batteryInfo[0] == null) {
            params.batteryInfo = s_EMPTY_BATTERY_INFO_ARRAY;
        } else {
            params.batteryInfo = (BatteryInfoParams[]) Arrays.copyOf(original.batteryInfo, original.batteryInfo.length);
        }
        return params;
    }

    public static boolean updateBatteryInfoParams(String batteryID, String status, String additionalStatus, int levelNumer, int levelDenom, String description) {
        synchronized (s_GetBatteryInfoParamsLock) {
            if (s_GetEventBatteryInfoParams == null) {
                s_GetEventBatteryInfoParams = createGetEventBatteryInfoParams(null);
            }
            if (s_GetEventBatteryInfoParams.batteryInfo == null || (s_GetEventBatteryInfoParams.batteryInfo != null && s_GetEventBatteryInfoParams.batteryInfo.length == 0)) {
                s_GetEventBatteryInfoParams.batteryInfo = new BatteryInfoParams[1];
                s_GetEventBatteryInfoParams.batteryInfo[0] = new BatteryInfoParams();
            }
            if (s_GetBatteryInfoParamsUpdated && s_GetEventBatteryInfoParams.batteryInfo[0].levelNumer.intValue() == levelNumer && s_GetEventBatteryInfoParams.batteryInfo[0].levelDenom.intValue() == levelDenom && TextUtils.equals(s_GetEventBatteryInfoParams.batteryInfo[0].status, status) && TextUtils.equals(s_GetEventBatteryInfoParams.batteryInfo[0].additionalStatus, additionalStatus) && TextUtils.equals(s_GetEventBatteryInfoParams.batteryInfo[0].batteryID, batteryID) && TextUtils.equals(s_GetEventBatteryInfoParams.batteryInfo[0].description, description)) {
                return false;
            }
            s_GetEventBatteryInfoParams.batteryInfo[0].batteryID = batteryID;
            s_GetEventBatteryInfoParams.batteryInfo[0].status = status;
            s_GetEventBatteryInfoParams.batteryInfo[0].additionalStatus = additionalStatus;
            s_GetEventBatteryInfoParams.batteryInfo[0].levelNumer = Integer.valueOf(levelNumer);
            s_GetEventBatteryInfoParams.batteryInfo[0].levelDenom = Integer.valueOf(levelDenom);
            s_GetEventBatteryInfoParams.batteryInfo[0].description = description;
            s_GetBatteryInfoParamsUpdated = true;
            return true;
        }
    }

    public static GetEventBatteryInfoParams peekGetEventBatteryInfoParamsSnapshot() {
        return s_GetEventBatteryInfoParams;
    }

    public static GetEventBatteryInfoParams getGetEventBatteryInfoParams(boolean isPolling) {
        GetEventBatteryInfoParams ref = null;
        if (StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.v(tag, "<GETEVENT> getGetEventBatteryInfoParams: not initialized yet.");
        } else {
            synchronized (s_GetBatteryInfoParamsLock) {
                if (s_GetBatteryInfoParamsUpdated || !isPolling) {
                    s_GetBatteryInfoParamsUpdated = false;
                    ref = s_GetEventBatteryInfoParams;
                    s_GetEventBatteryInfoParams = createGetEventBatteryInfoParams(ref);
                }
            }
        }
        return ref;
    }

    private static GetEventRecordingTimeParams createGetEventRecordingTimeParams(GetEventRecordingTimeParams original) {
        GetEventRecordingTimeParams params = new GetEventRecordingTimeParams();
        params.type = "recordingTime";
        if (original == null) {
            params.recordingTime = 0;
        } else {
            params.recordingTime = original.recordingTime;
        }
        return params;
    }

    public static boolean updateRecordingTimeParams(int second) {
        boolean z = true;
        synchronized (s_GetEventRecordingTimeParamsLock) {
            if (s_GetEventRecordingTimeParams == null) {
                s_GetEventRecordingTimeParams = createGetEventRecordingTimeParams(null);
            }
            if (s_GetEventRecordingTimeParamsUpdated && s_GetEventRecordingTimeParams.recordingTime.intValue() == second) {
                z = false;
            } else {
                s_GetEventRecordingTimeParams.recordingTime = Integer.valueOf(second);
                s_GetEventRecordingTimeParamsUpdated = true;
            }
        }
        return z;
    }

    public static GetEventRecordingTimeParams peekGetEventRecordingTimeParamsSnapshot() {
        return s_GetEventRecordingTimeParams;
    }

    public static GetEventRecordingTimeParams getGetEventRecordingTimeParams(boolean isPolling) {
        GetEventRecordingTimeParams ref = null;
        if (StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            Log.v(tag, "<GETEVENT> GetEventRecordingTimeParams: not initialized yet.");
        } else {
            synchronized (s_GetEventRecordingTimeParamsLock) {
                if (s_GetEventRecordingTimeParamsUpdated || !isPolling) {
                    s_GetEventRecordingTimeParamsUpdated = false;
                    if (s_GetEventRecordingTimeParams != null) {
                        if (s_GetEventRecordingTimeParams.recordingTime.intValue() != 0) {
                            ref = s_GetEventRecordingTimeParams;
                            s_GetEventRecordingTimeParams = createGetEventRecordingTimeParams(ref);
                        }
                    }
                }
            }
        }
        return ref;
    }

    private static StreamingStatus createStreamingStatusParams(StreamingStatus original) {
        StreamingStatus streamingStatus = new StreamingStatus();
        String[] status = StreamingController.getStreamingStatus();
        if (original == null) {
            streamingStatus.status = status[0];
            streamingStatus.factor = status[1];
        } else {
            streamingStatus.status = original.status;
            streamingStatus.factor = original.factor;
        }
        return streamingStatus;
    }

    public static boolean updateStreamingStatus(String[] streamingStatus) {
        synchronized (s_StreamingStatusParamsLock) {
            if (s_StreamingStatusUpdated && TextUtils.equals(s_StreamingStatusParams.status, streamingStatus[0]) && TextUtils.equals(s_StreamingStatusParams.factor, streamingStatus[1])) {
                return false;
            }
            s_StreamingStatusParams.status = streamingStatus[0];
            s_StreamingStatusParams.factor = streamingStatus[1];
            s_StreamingStatusUpdated = true;
            return true;
        }
    }

    public static StreamingStatus getStreamingStatus(boolean isPolling) {
        StreamingStatus ref;
        synchronized (s_StreamingStatusParamsLock) {
            if (isPolling) {
                if (!s_StreamingStatusUpdated) {
                    ref = null;
                }
            }
            s_StreamingStatusUpdated = false;
            ref = s_StreamingStatusParams;
            s_StreamingStatusParams = createStreamingStatusParams(ref);
        }
        return ref;
    }

    public static boolean getUpdateStreamingStatus() {
        return s_StreamingStatusUpdated;
    }

    private static GetEventStorageInformationParams createGetEventStorageInformationParams(GetEventStorageInformationParams original) {
        GetEventStorageInformationParams params = new GetEventStorageInformationParams();
        params.type = "storageInformation";
        if (original == null) {
            params.storageID = null;
            params.recordTarget = false;
            params.numberOfRecordableImages = -1;
            params.recordableTime = -1;
            params.storageDescription = null;
        } else {
            params.storageID = original.storageID;
            params.recordTarget = original.recordTarget;
            params.numberOfRecordableImages = original.numberOfRecordableImages;
            params.recordableTime = original.recordableTime;
            params.storageDescription = original.storageDescription;
        }
        return params;
    }

    public static boolean updateStorageInformationParams(String storageID, boolean recordTarget, int numberOfRecordableImages, int recordableTime, String storageDescription) {
        boolean z = true;
        synchronized (s_StorageInformationParamsLock) {
            if (s_StorageInformationParams == null) {
                s_StorageInformationParams = createGetEventStorageInformationParams(null);
            }
            if (s_StorageInformationUpdated && TextUtils.equals(s_StorageInformationParams.storageID, storageID) && s_StorageInformationParams.recordTarget.booleanValue() == recordTarget && s_StorageInformationParams.numberOfRecordableImages.intValue() == numberOfRecordableImages && s_StorageInformationParams.recordableTime.intValue() == recordableTime && TextUtils.equals(s_StorageInformationParams.storageDescription, storageDescription)) {
                z = false;
            } else {
                s_StorageInformationParams.storageID = storageID;
                s_StorageInformationParams.recordTarget = Boolean.valueOf(recordTarget);
                s_StorageInformationParams.numberOfRecordableImages = Integer.valueOf(numberOfRecordableImages);
                s_StorageInformationParams.recordableTime = Integer.valueOf(recordableTime);
                s_StorageInformationParams.storageDescription = storageDescription;
                s_StorageInformationUpdated = true;
            }
        }
        return z;
    }

    public static GetEventStorageInformationParams[] peekStorageInformationParamsSnapshot() {
        GetEventStorageInformationParams[] params = new GetEventStorageInformationParams[1];
        synchronized (s_StorageInformationParamsLock) {
            params[0] = s_StorageInformationParams;
        }
        return params;
    }

    public static GetEventStorageInformationParams[] getStorageInformationParams(boolean isPolling) {
        GetEventStorageInformationParams[] params;
        if (StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition())) {
            return s_EMPTY_GETEVENT_STORAGEINFO_ARRAY;
        }
        synchronized (s_StorageInformationParamsLock) {
            if (s_StorageInformationUpdated || !isPolling) {
                s_StorageInformationUpdated = false;
                if (s_StorageInformationParams.storageID == null) {
                    params = s_EMPTY_GETEVENT_STORAGEINFO_ARRAY;
                } else {
                    GetEventStorageInformationParams ref = s_StorageInformationParams;
                    s_StorageInformationParams = createGetEventStorageInformationParams(ref);
                    params = new GetEventStorageInformationParams[]{ref};
                }
            } else {
                params = s_EMPTY_GETEVENT_STORAGEINFO_ARRAY;
            }
        }
        return params;
    }

    private static GetEventZoomSettingParams createGetEventZoomSettingParams(GetEventZoomSettingParams original) {
        GetEventZoomSettingParams param = new GetEventZoomSettingParams();
        param.type = "zoomSetting";
        if (original == null) {
            param.zoom = null;
            param.candidate = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            param.zoom = original.zoom;
            param.candidate = (String[]) Arrays.copyOf(original.candidate, original.candidate.length);
        }
        return param;
    }

    public static boolean updateZoomSettingParams(String mode, String[] candidates) {
        synchronized (s_ZoomSettingParamsLock) {
            if (s_ZoomSettingUpdated && s_ZoomSettingParams.zoom == mode && Arrays.equals(s_ZoomSettingParams.candidate, candidates)) {
                return false;
            }
            boolean updateAvailableApi = !Arrays.equals(s_ZoomSettingParams.candidate, candidates);
            s_ZoomSettingParams.zoom = mode;
            s_ZoomSettingParams.candidate = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_ZoomSettingUpdated = true;
            if (updateAvailableApi) {
                updateAvailableApiList();
            }
            return true;
        }
    }

    public static GetEventZoomSettingParams getZoomSettingParams(boolean isPolling) {
        GetEventZoomSettingParams ref = null;
        if (StateController.AppCondition.PREPARATION.equals(StateController.getInstance().getAppCondition()) || s_ZoomSettingParams.zoom == null) {
            Log.e(tag, "<GETEVENT> GetEventZoomSettingParams: not initialized yet.");
        } else {
            synchronized (s_ZoomSettingParamsLock) {
                if (s_ZoomSettingUpdated || !isPolling) {
                    s_ZoomSettingUpdated = false;
                    ref = s_ZoomSettingParams;
                    s_ZoomSettingParams = createGetEventZoomSettingParams(ref);
                    Log.e(tag, "<GETEVENT> GetEventZoomSettingParams: return ref.");
                } else {
                    Log.e(tag, "<GETEVENT> GetEventZoomSettingParams: null.");
                }
            }
        }
        return ref;
    }

    public static GetEventZoomSettingParams peekZoomSettingParamsSnapshot() {
        GetEventZoomSettingParams ref;
        synchronized (s_ZoomSettingParamsLock) {
            ref = s_ZoomSettingParams;
        }
        return ref;
    }

    private static GetEventSilentShootingSettingParams createGetEventSilentShootingParams(GetEventSilentShootingSettingParams original) {
        Log.i("silent", "ParamsGenerator createGetEventSilentShootingParams");
        GetEventSilentShootingSettingParams param = new GetEventSilentShootingSettingParams();
        param.type = "silentShooting";
        if (original == null) {
            param.silentShooting = null;
            param.candidate = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        } else {
            param.silentShooting = original.silentShooting;
            param.candidate = (String[]) Arrays.copyOf(original.candidate, original.candidate.length);
        }
        Log.i("silent", "ParamsGenerator createGetEventSilentShootingParams : " + param);
        return param;
    }

    public static boolean updateSilentShootingParams(String mode, String[] candidates) {
        synchronized (s_SilentShootingParamsLock) {
            if (s_SilentShootingUpdated && s_SilentShootingParams.silentShooting == mode && Arrays.equals(s_SilentShootingParams.candidate, candidates)) {
                Log.i("silent", "ParamsGenerator updateSilentShootingParams : false");
                return false;
            }
            boolean updateAvailableApi = !Arrays.equals(s_SilentShootingParams.candidate, candidates);
            s_SilentShootingParams.silentShooting = mode;
            s_SilentShootingParams.candidate = (String[]) Arrays.copyOf(candidates, candidates.length);
            s_SilentShootingUpdated = true;
            if (updateAvailableApi) {
                updateAvailableApiList();
            }
            Log.i("silent", "ParamsGenerator updateSilentShootingParams : true");
            return true;
        }
    }

    public static GetEventSilentShootingSettingParams getSilentShootingParams(boolean isPolling) {
        GetEventSilentShootingSettingParams ref = null;
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.PREPARATION) || s_SilentShootingParams.silentShooting == null) {
            Log.e(tag, "<GETEVENT> GetEventSilentShootingSettingParams: not initialized yet.");
        } else {
            synchronized (s_SilentShootingParamsLock) {
                if (s_SilentShootingUpdated || !isPolling) {
                    s_SilentShootingUpdated = false;
                    ref = s_SilentShootingParams;
                    s_SilentShootingParams = createGetEventSilentShootingParams(ref);
                }
            }
        }
        return ref;
    }

    public static GetEventSilentShootingSettingParams peekFSilentShootingParamsSnapshot() {
        GetEventSilentShootingSettingParams ref;
        synchronized (s_SilentShootingParamsLock) {
            ref = s_SilentShootingParams;
        }
        return ref;
    }
}
