package com.sony.imaging.app.soundphoto.util;

import android.os.Environment;

/* loaded from: classes.dex */
public class SPConstants {
    public static final boolean AUTO_PLAY_BACK_STATUS_DEFAULT = false;
    public static final String CAPTURE_DONE_CALLBACK = "CAPTURE_DONE_CALLBACK";
    public static final String DELETE_IMAGE_DATABASE_UPDATE = "DELETE_IMAGE_DATABASE_UPDATE";
    public static final String FILE_SEPARATER = "/";
    public static final int INTEGER_CONSTANT_ZERO = 0;
    public static final int INVALID_INTEGER_CONSTANTS = -1;
    public static final int MAX_DURATION = 10;
    public static final int MIN_DURATION = 2;
    public static final String MS_CARD_PATH = "/MSSONY/CAM_APPS/APP_SDPH";
    public static final String PLAYING_STATUS_PAUSE = "PLAYING_STATUS_PAUSE";
    public static final String PLAY_TIMER_STOP = "PLAY_TIMER_STOP";
    public static final String PLAY_TIMER_UPDATE = "PLAY_TIMER_UPDATE";
    public static final int POST_DURATION_DEFAULT = 2;
    public static final String SD_CARD_PATH = "/PRIVATE/SONY/APP_SDPH";
    public static final String TAG_PROCESSING_LAYOUT = "processing_layout";
    public static final int TOTAL_DURATION_DEFAULT = 10;
    public static final String TYPE_INFO_DB_PATH = "/PRIVATE/";
    public static final String TYPE_INFO_FILENAME = "TYPEINFO.XML";
    public static final String LIB_PATH = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/";
    public static final String ROOT_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
    public static final Integer SOUND_PHOTO_LAUNCH_KIKILOG_ID = 4331;
    public static final Integer SOUND_PHOTO_CAPTURE_KIKILOG_ID = 4332;
    public static final Integer SOUND_PHOTO_DIRECT_UPLOAD_KIKILOG_ID = 4333;
    public static final Integer SOUND_DELETE_KIKILOG_ID = 4334;
    public static int CURRENT_SELECTED_ID_POSITION = -1;
}
