package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.common.AudioNotificationManager;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class CameraNotificationManager extends NotificationManager {
    public static final String AE_LOCK = "AELock";
    public static final String AE_LOCK_BUTTON = "AELockButton";
    public static final String AE_RANGE_CHANGED = "AERangeChanged";
    public static final String ANGLE_CHANGED = "AngleChanged";
    public static final String ANTI_HAND_BLUR = "AntiHandBlur";
    public static final String APERTURE = "Aperture";
    public static final String APSC_MODE_CHANGED = "APS-C_Mode";

    @Deprecated
    public static final String AUDIO_WIND_NR = "AudioWindNoiseReduction";
    public static final String AUTOSCENEMODE_CHANGED = "AutoSceneModeChanged";
    public static final String AUTO_FOCUS_AREA = "AutoFocusArea";
    public static final String AUTO_FOCUS_MODE = "AutoFocusMode";

    @Deprecated
    public static final String BEEP_MODE_CHANGED = "BeepModeChanged";
    public static final String CAMERA_ERROR = "CameraErrorCallback";
    public static final String CINEMA_TONE_CHANGED = "CinematoneChanged";
    public static final String COLOR_MODE_CHANGED = "ColorModeChanged";
    public static final String CREATIVE_STYLE_CHANGE = "CreativeStyleChange";
    public static final String DEVICE_EXTERNAL_FLASH_CHANGED = "DeviceExternalFlashChanged";
    public static final String DEVICE_EXTERNAL_FLASH_ONOFF = "DeviceExternalFlashOnOff";
    public static final String DEVICE_INTERNAL_FLASH_CHANGED = "DeviceInternalFlashChanged";
    public static final String DEVICE_INTERNAL_FLASH_ONOFF = "DeviceInternalFlashOnOff";
    public static final String DEVICE_LENS_AFMF_SW_CHANGED = "AFMFSWChanged";
    public static final String DEVICE_LENS_ATTACH_DETACH = "DeviceLensAttachDetach";
    public static final String DEVICE_LENS_CHANGED = "DeviceLensChanged";
    public static final String DIGITAL_ZOOM_MODE_CHANGED = "DigitalZoomMode";
    public static final String DIGITAL_ZOOM_ONOFF_CHANGED = "DigitalZoomOnOffChanged";
    public static final String DONE_AUTO_FOCUS = "DoneAutoFocus";
    public static final String DRIVE_MODE = "DriveMode";
    public static final String DRO_AUTO_HDR = "DROAutoHDR";
    public static final String EXPOSURE_COMPENSATION = "ExposureCompensation";
    public static final String FACE_DETECTED = "FaceDetected";
    public static final String FACE_DETECTION_MODE = "FaceDetectionMode";
    public static final String FLASH_CHANGE = "FlashChange";
    public static final String FLASH_CHARGE_STATUS = "FlashChargeStatus";
    public static final String FLASH_EMITTION = "FlashEmittion";
    public static final String FOCAL_LENGTH_CHANGED = "FocalLengthChanged";
    public static final String FOCUS_AREA_INFO = "FocusAreaInfo";
    public static final String FOCUS_CHANGE = "FocusChange";
    public static final String FOCUS_LIGHT_STATE = "FocusLightState";
    public static final String FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED = "FocusMagnificationAvailabilityChanged";
    public static final String FOCUS_MAGNIFICATION_CHANGED = "FocusMagnificationChanged";
    public static final String FOCUS_POINT = "FocusPoint";
    public static final String FOCUS_POSITION_CHANGED = "FocusPositionChanged";
    public static final String FOCUS_RING_ROTATION_STARTED = "FocusRingRotated";
    public static final String FOCUS_RING_ROTATION_STOPPED = "FocusRingStopped";
    public static final String HISTOGRAM_UPDATE = "histoupdate";
    public static final String IR_CONTROL_MODE_CHANGED = "IrControlModeChanged";
    public static final String ISO_SENSITIVITY = "ISOSensitivity";
    public static final String ISO_SENSITIVITY_AUTO = "ISOSensitivityAuto";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_NOTCHANGED = "Value is not changed. request ignored. ";
    private static final String LOG_MSG_REQUESTSYNCNOTIFY = "requestSyncNotify ";
    private static final String LOG_MSG_TAG = "tag = ";
    private static final String LOG_MSG_VALUE = "value = ";
    public static final String METERED_MANUAL = "MeteredManual";
    public static final String METERING_MODE = "MeteringMode";
    public static final String METERING_MODE_POINT = "MeteringModePoint";
    public static final String METERING_MODE_SPOT_CIRCLE_CHANGE = "MeteringModeSpotCircleChange";
    public static final String METERING_MODE_SPOT_SIZE = "MeteringModeSpotSize";
    public static final String METERING_RANGE = "MeteringRange";

    @Deprecated
    public static final String MIC_REF_LEVEL_CHANGED = "MicRefLevelChanged";
    public static final String MINIATURE_SHADED_REGION_CHANGE = "MiniatureShadedRegionChange";
    public static final String MOTION_RECOGNITION_CHANGED = "MotionRecognitionChanged";
    public static final String MOTION_SHOT_COMPLETED = "MotionShotCompleted";
    public static final String MOVIE_FORMAT = "MovieFormat";
    public static final String MOVIE_REC_START_EXECUTION_ERROR = "startMovieRecExecutionError";
    public static final String MOVIE_REC_START_FAILED = "startMovieRecFailed";
    public static final String MOVIE_REC_START_SUCCEEDED = "startMovieRecSucceeded";
    public static final String MOVIE_REC_STOP = "stopMovieRec";
    public static final String MOVIE_REC_STOP_EXECUTION_ERROR = "stopMovieRecExecutionError";
    public static final String MOVIE_REC_TIME_CHANGED = "movieRecTimeChanged";
    public static final String NDFILTER_CHANGED = "NDFilterChanged";
    public static final String NDFILTER_STATUS_CHANGED = "NDFilterStatusChanged";
    private static final int PF_VER_THIN_OUT_DRIVEMODE = 8;
    public static final String PICTURE_EFFECT_CHANGE = "PictureEffectChange";
    public static final String PICTURE_QUALITY = "PictureQuality";
    public static final String PICTURE_REVIEW_INFO = "PictureReviewInfo";
    public static final String PICTURE_SIZE = "PictureSize";
    public static final String POWER_ZOOM_CHANGED = "PowerZoomChanged";
    public static final String PROCESSING_PROGRESS = "ProcessingProgress";
    public static final String PROGRAM_LINE_CHANGE = "ProgramLineChange";
    public static final String PRO_COLOR_MODE_CHANGED = "ProColorModeChanged";
    public static final String RAW_REC_MODE_SETTING_CHANGED = "RawRecModeSettingChanged";
    public static final String REC_MODE_CHANGED = "RecModeChanged";
    public static final String REC_MODE_CHANGING = "RecModeChanging";
    public static final String SCENE_MODE = "SceneMode";
    public static final String SCENE_RECOGNITION_CHANGED = "SceneRecognitionChanged";
    public static final String SELFTIMER_COUNTDOWN_STATUS = "CowntDownStatus";
    public static final String SHUTTER_SPEED = "ShutterSpeed";
    public static final String SILENT_SHUTTER_SETTING_CHANGED = "SilentShutteｒSettingChanged";
    public static final String START_AUTO_FOCUS = "StartAutoFocus";
    public static final String STEADY_RECOGNITION_CHANGED = "SteadyRecognitionChanged";
    public static final String STREAM_WRITE_START = "startStreamWrite";
    public static final String STREAM_WRITE_STOP = "stopStreamWrite";
    private static final String TAG = "CameraNotificationManager";
    public static final String TAG_INTERVAL_SETTING_CHANGED = "IntervalSettingChanged";
    public static final String TAG_INTVAL_REC_FAILED = "StartFailedIntervalRec";
    public static final String TAG_INTVAL_REC_NOT_START = "NotStartIntervalRec";
    public static final String TAG_INTVAL_REC_ON_SHUTTER_STATUS_NOT_OK = "intervalRecOnsShutterIsNotOK";
    public static final String TAG_INTVAL_REC_SHOTS_COUNT_UPDATED = "UpdateCountOfIntervalRec";
    public static final String TAG_INTVAL_REC_STARTED = "StartIntervalRec";
    public static final String TAG_INTVAL_REC_STOPPED = "StopIntervalRec";
    public static final String TAG_INTVAL_REC_STOP_BEGIN = "StopIntervalRecBegin";
    public static final String TAG_LOOPREC_MAX_DURATION_CHANGED = "LoopRecMaxDurationChanged";
    public static final String TRACKING_FOCUS_CHANGED = "TrackingFocusChanged";
    public static final String TRACKING_FOCUS_INFO_CHANGED = "TrackingFocusInfoChanged";
    public static final String VIEW_TEKI = "ViewTeki";
    public static final String WB_DETAIL_CHANGE = "WhiteBalanceDetailChange";
    public static final String WB_MODE_CHANGE = "WhiteBalanceModeChange";
    public static final String ZOOM_INFO_CHANGED = "ZoomInfoChanged";
    public static final String ZOOM_MAX_MAG_CHANGED = "zoomMaxMagChanged";
    private static CameraNotificationManager instance;
    public static HashSet<String> mThinnedOut = new HashSet<>();
    private Map<String, Object> mValues;

    static {
        mThinnedOut.add(POWER_ZOOM_CHANGED);
        mThinnedOut.add(ZOOM_INFO_CHANGED);
        mThinnedOut.add(DEVICE_LENS_CHANGED);
        if (8 <= Environment.getVersionPfAPI()) {
            mThinnedOut.add(DRIVE_MODE);
        }
        instance = new CameraNotificationManager(false);
    }

    public void clear() {
        this.mValues.clear();
    }

    private CameraNotificationManager(boolean b) {
        super(b, mThinnedOut);
        this.mValues = new ConcurrentHashMap();
    }

    public static CameraNotificationManager getInstance() {
        return instance;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return ("AudioWindNoiseReduction".equals(tag) || "MicRefLevelChanged".equals(tag) || "BeepModeChanged".equals(tag)) ? AudioNotificationManager.getInstance().getValue(tag) : this.mValues.get(tag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.NotificationManager
    public void onFirstListenerSet(String tag) {
        super.onFirstListenerSet(tag);
    }

    public void requestNotify(String tag, Object value) {
        if ("AudioWindNoiseReduction".equals(tag) || "MicRefLevelChanged".equals(tag) || "BeepModeChanged".equals(tag)) {
            AudioNotificationManager.getInstance().requestNotify(tag, value);
            return;
        }
        boolean isValueChanged = true;
        Object current = this.mValues.put(tag, value);
        if (this.mValues.containsKey(tag)) {
            if (current != null) {
                isValueChanged = !current.equals(value);
            } else {
                isValueChanged = value != null;
            }
        }
        if (isValueChanged) {
            notify(tag);
        }
    }

    public void requestNotify(String tag) {
        if ("AudioWindNoiseReduction".equals(tag) || "MicRefLevelChanged".equals(tag) || "BeepModeChanged".equals(tag)) {
            AudioNotificationManager.getInstance().requestNotify(tag);
        } else {
            notify(tag);
        }
    }

    public void requestSyncNotify(String tag, Object value) {
        if ("AudioWindNoiseReduction".equals(tag) || "MicRefLevelChanged".equals(tag) || "BeepModeChanged".equals(tag)) {
            AudioNotificationManager.getInstance().requestSyncNotify(tag, value);
            return;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_REQUESTSYNCNOTIFY).append("tag = ").append(tag).append(", ").append(LOG_MSG_VALUE).append(value);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        boolean isValueChanged = true;
        Object current = this.mValues.put(tag, value);
        if (this.mValues.containsKey(tag)) {
            if (current != null) {
                isValueChanged = !current.equals(value);
            } else {
                isValueChanged = value != null;
            }
        }
        if (isValueChanged) {
            notifySync(tag);
        } else {
            builder.replace(0, builder.length(), LOG_MSG_NOTCHANGED).append("tag = ").append(tag);
            Log.i(TAG, builder.toString());
        }
    }

    /* loaded from: classes.dex */
    public static class OnFocusInfo {
        public int[] area;
        public int status;

        public OnFocusInfo(int status, int[] area) {
            this.status = status;
            this.area = area;
        }
    }

    /* loaded from: classes.dex */
    public static class FocusLightStateInfo {
        public boolean on;
        public boolean ready;

        public FocusLightStateInfo(boolean ready, boolean on) {
            this.ready = ready;
            this.on = on;
        }
    }
}
