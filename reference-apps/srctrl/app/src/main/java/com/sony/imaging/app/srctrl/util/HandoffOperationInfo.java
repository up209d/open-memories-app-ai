package com.sony.imaging.app.srctrl.util;

/* loaded from: classes.dex */
public class HandoffOperationInfo {
    public static final int ACTION_HALF_PRESS_SHUTTER = 31;
    public static final int CANCEL_HALF_PRESS_SHUTTER = 32;
    public static final int DELETE_CONTENT = 46;
    public static final int EXCUTE_TERMINATE_CAUTION = 22;
    public static final int GET_AVAILABLE_DRIVE_MODE = 10;
    public static final int GET_AVAILABLE_EXPOSURE_COMPENSATION = 5;
    public static final int GET_AVAILABLE_SELF_TIMER = 15;
    public static final int GET_CAMERA_SETTING = 23;
    public static final int GET_CAMERA_SETTING_AVAILABLE = 24;
    public static final int GET_CAMERA_SETTING_SUPPORTED = 25;
    public static final int GET_CONTENT_COUNT = 44;
    public static final int GET_CONTENT_LIST = 45;
    public static final int GET_DRIVE_MODE = 8;
    public static final int GET_EXPOSURE_COMPENSATION_INDEX = 2;
    public static final int GET_EXPOSURE_COMPENSATION_STEP = 3;
    public static final int GET_EXPOSURE_MODE = 16;
    public static final int GET_SCHEME_LIST = 42;
    public static final int GET_SELECTED_SCENE = 17;
    public static final int GET_SELF_TIMER = 13;
    public static final int GET_SOURCE_LIST = 43;
    public static final int GET_SUPPORTED_DRIVE_MODE = 9;
    public static final int GET_SUPPORTED_EXPOSURE_COMPENSATION = 4;
    public static final int GET_SUPPORTED_SELF_TIMER = 14;
    public static final int IS_AVAILABLE_DRIVE_MODE = 6;
    public static final int IS_AVAILABLE_EXPOSURE_COMPENSATION = 0;
    public static final int IS_AVAILABLE_SPECIFIC_DRIVE_MODE = 11;
    public static final int IS_SELF_TIMER = 18;
    public static final int MOVE_TO_CAPTURE_STATE = 21;
    public static final int MOVE_TO_MOVIE_REC_START_STATE = 28;
    public static final int MOVE_TO_NETWORK_STATE = 20;
    public static final int MOVE_TO_S1ON_STATE_FOR_TOUCH_AF = 27;
    public static final int MOVE_TO_SHOOTING_STATE = 19;
    public static final int PAUSE_STREAMING = 52;
    public static final int SEEK_STREAMING = 54;
    public static final int SET_CAMERA_SETTING = 26;
    public static final int SET_DRIVE_MODE = 7;
    public static final int SET_EXPOSURE_COMPENSATION = 1;
    public static final int SET_PLAYBACK_STATE = 41;
    public static final int SET_SELF_TIMER = 12;
    public static final int SET_SHOOTING_STATE = 57;
    public static final int SET_STREAMING_CONTENT = 50;
    public static final int START_LIVEVIEW = 58;
    public static final int START_STREAMING = 51;
    public static final int STOP_BULB_SHOOTING = 60;
    public static final int STOP_CONT_SHOOTING = 36;
    public static final int STOP_LIVEVIEW = 59;
    public static final int STOP_MOVIE_REC = 29;
    public static final int STOP_STREAMING = 53;

    /* loaded from: classes.dex */
    public enum CameraSettings {
        TOUCH_AF,
        F_NUMBER,
        SHUTTER_SPEED,
        ISO_NUMBER,
        EXPOSURE_MODE,
        WHITE_BALANCE,
        LIVEVIEW_SIZE,
        POSTVIEW_IMAGE_SIZE,
        PROGRAM_SHIFT,
        FLASH_MODE,
        ZOOM,
        SELF_TIMER,
        SHOOT_MODE,
        FOCUS_MODE,
        ZOOM_SETTING,
        STORAGE_INFORMATION,
        CONT_SHOOT_MODE,
        CONT_SHOOT_SPEED,
        LIVEVIEW_FRAME_INFO,
        SILNET_SHOOTING
    }
}
