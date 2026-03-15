package com.sony.imaging.app.srctrl.util;

import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import java.util.Arrays;

/* loaded from: classes.dex */
public class SRCtrlConstants {
    public static final String CAMERA_FUNCTION_REMOTE_OTHER_FUNCTION = "Other Function";
    public static final String CONTENTTRANSFER_DIRECTORY = "http://192.168.122.1:8080/contentstransfer/";
    public static final String CONTENTTRANSFER_LARGE = "scn/";
    public static final String CONTENTTRANSFER_ORG = "org/";
    public static final String CONTENTTRANSFER_ORGJPEG = "orgjpeg/";
    public static final String CONTENTTRANSFER_ORGRAW = "orgraw/";
    public static final String CONTENTTRANSFER_SMALL = "vga/";
    public static final String CONTENTTRANSFER_THUMB = "thumb/";
    public static final String CONTENT_TYPE_APP_OCTET_STREAM = "application/octet-stream";
    public static final String CONTINUOUS_DIRECTORY = "http://192.168.122.1:8080/continuous/";
    public static final int DELAY_WIFI_ENABLE = 5000;
    public static final String DELETE_CONTENT_LIST = "DeleteContentList";
    public static final int DELETE_MODE_TIMEOUT = 30000;
    public static final String DEVICE_DISCOVERY_ACTION_LIST_PATH = "/sony";
    public static final String DEVICE_DISCOVERY_UNIQUE_SERVICE_ID = "000000001000";
    public static final float EXPOSURE_COMPENSATION_STEP_1_2 = 0.5f;
    public static final float EXPOSURE_COMPENSATION_STEP_1_3 = 0.333f;
    public static final String FILE_EXTENTION_HTML = "html";
    public static final int FOCUS_EVENT_TIMEOUT = 15000;
    public static final int HTTP_PORT_INT = 8080;
    public static final String IMAGE_PREFIX = "image";
    public static final String IMAGE_TYPE_LARGE = "Scn";
    public static final String IMAGE_TYPE_ORG = "Original";
    public static final String IMAGE_TYPE_ORGJPEG = "JPEG";
    public static final String IMAGE_TYPE_ORGRAW = "RAW";
    public static final String IMAGE_TYPE_SMALL = "VGA";
    public static final String IMAGE_TYPE_THUMB = "Thumbnail";
    public static final String IMAGE_TYPE_UNKNOWN = "Unknown";
    public static final String JPEG_FILE_SUFFIX = ".JPG";
    public static final int JPEG_QUALITY_FINE = 50;
    public static final int KIKILOG_ID_APP_LAUNCH = 4401;
    public static final int KIKILOG_ID_LOCAL_SHOOTING = 4138;
    public static final int KIKILOG_ID_REMOTE_SHOOTING = 4137;
    public static final String LIVEVIEW_FILENAME = "liveviewstream";
    public static final String LIVEVIEW_URL = "http://192.168.122.1:8080/liveview/liveviewstream";
    public static final int MEXI_SERVER_TRANSFER_MAX_BODY_SIZE = 20480;
    public static final int MOVIE_REC_STRAT_TIMEOUT = 15000;
    public static final String MPO_FILE_SUFFIX = ".MPO";
    public static final String MY_IP_ADDRESS = "192.168.122.1";
    public static final String NUM_OF_BACKLOGS = "backlog";
    public static final int NUM_OF_SERVER_BACKLOGS = 10;
    public static final int NUM_OF_SERVER_THREADS = 7;
    public static final String NUM_OF_THREADS = "num_of_threads";
    public static final String PORT = "port";
    public static final String POSTVIEW_DIRECTORY = "http://192.168.122.1:8080/postview/";
    public static final String POSTVIEW_DIRECTORY_ON_MEMORY = "http://192.168.122.1:8080/postview/memory";
    public static final String POSTVIEW_FILENAME_EXTENTION = ".JPG";
    public static final String POSTVIEW_FILENAME_PREFIX = "pict";
    public static final String RAW_FILE_SUFFIX = ".ARW";
    public static final int RECEIVE_EVENT_TIMEOUT = 15000;
    public static final int RECEIVE_EVENT_WAIT_PREVIOUS_TIMEOUT = 5000;
    public static final int RECEIVE_GET_EVENT_TIMEOUT_v1_3 = 60000;
    public static final String SERVER_KEYWORD_4_0 = "__MNG__";
    public static final String SERVER_KEYWORD_4_1 = "__SAK__";
    public static final String SERVER_NAME = "Smart Remote Control";
    public static final String SERVER_VERSION_4_0 = "2.1.2";
    public static final String SERVER_VERSION_4_1 = "2.1.4";
    public static final String SERVLETS = "servlets";
    public static final String SERVLET_ROOT_PATH_CONTENTSTRANSFER = "/contentstransfer/";
    public static final String SERVLET_ROOT_PATH_CONTINUOUS = "/continuous/";
    public static final String SERVLET_ROOT_PATH_LIVEVIEW = "/liveview/";
    public static final String SERVLET_ROOT_PATH_POSTVIEW = "/postview/";
    public static final String SERVLET_ROOT_PATH_POSTVIEW_ON_MEMORY = "/postview/memory";
    public static final String SERVLET_ROOT_PATH_STREMING = "/streaming/";
    public static final String SERVLET_ROOT_PATH_WEBAPI = "/sony/";
    public static final String SERVLET_ROOT_PATH_WEBAPI_ACCESS_CONTROL = "/sony/accessControl";
    public static final String SERVLET_ROOT_PATH_WEBAPI_AVCONTENT = "/sony/avContent";
    public static final String SERVLET_ROOT_PATH_WEBAPI_CAMERA = "/sony/camera";
    public static final String SERVLET_ROOT_PATH_WEBAPI_GUIDE = "/sony/guide";
    public static final String SERVLET_ROOT_PATH_WEBAPI_SYSTEM = "/sony/system";
    public static final String SHUTTER_SPEED_BULB_STRING = "BULB";
    public static final String SHUTTER_SPEED_BULB_VALUE = "65535\"";
    public static final String SHUTTER_TYPE_BULB = "BULB";
    public static final String SHUTTER_TYPE_CONT = "CONT";
    public static final String SHUTTER_TYPE_NORMAL = "NORMAL";
    public static final int SSID_PREFIX_SIZE = 5;
    public static final String SSID_STRING_DIRECT = "DIRECT-";
    public static final String STREAMIMG_FILENAME = "playbackstream";
    public static final String STREAMIMG_URL = "http://192.168.122.1:8080/streaming/playbackstream";
    public static final boolean SUPPORT_FULL_RCV_EVENT = false;
    public static final int TAKE_PICTURE_TIMEOUT = 15000;
    public static final String TEXT_ENCODING_UTF8 = "UTF-8";
    public static final String URI_CONTENT_ID = ":content?contentId=";
    public static final String URI_CONTENT_ID_SEPARATOR = "_";
    public static final String URL_HTTP_PREFIX = "http://";
    public static final String VIDEO_PREFIX = "video";
    public static final String WEBAPI_BASE_URL = "http://192.168.122.1:8080/sony";
    public static final String WEBAPI_ROOT_FILENAME = "index.html";
    public static final int WPS_PIN_CHARACTERS_LONG = 8;
    public static final int WPS_PIN_CHARACTERS_SHORT = 4;
    public static final int WPS_PIN_TIMEOUT = 120000;
    public static final String CAMERA_FUNCTION_CONTENTS_TRANSFER = "Contents Transfer";
    public static final String CAMERA_FUNCTION_REMOTE_SHOOTING = "Remote Shooting";
    public static final String[] CAMERA_FUNCTION_CANDIDATES = {CAMERA_FUNCTION_CONTENTS_TRANSFER, CAMERA_FUNCTION_REMOTE_SHOOTING};
    private static final float[] s_FNUMBER_TABLE = {1.0f, 1.1f, 1.3f, 1.4f, 1.6f, 1.7f, 2.0f, 2.2f, 2.5f, 2.8f, 3.2f, 3.5f, 4.0f, 4.5f, 5.0f, 5.6f, 6.3f, 7.1f, 8.0f, 9.0f, 10.0f, 11.0f, 13.0f, 14.0f, 16.0f, 18.0f, 20.0f, 22.0f, 25.0f, 29.0f, 32.0f, 36.0f, 40.0f, 45.0f, 51.0f, 57.0f, 64.0f, 72.0f, 81.0f, 90.0f};
    private static final float[] s_FNUMBER_TABLE_F1_8 = createNumberTableF1_8();
    private static final float[] s_FNUMBER_TABLE_F2_4 = createNumberTableF2_4();
    public static final int CHANGE_MODE_TIMEOUT = 20000;
    public static final int BROADCAST_RECEIVER_TIMEOUT = 8000;
    private static final int[][] s_SHUTTER_SPEED_TABLE = {new int[]{1, 32000}, new int[]{1, 25600}, new int[]{1, CHANGE_MODE_TIMEOUT}, new int[]{1, 16000}, new int[]{1, 12800}, new int[]{1, 10000}, new int[]{1, BROADCAST_RECEIVER_TIMEOUT}, new int[]{1, 6400}, new int[]{1, 5000}, new int[]{1, 4000}, new int[]{1, 3200}, new int[]{1, 2500}, new int[]{1, 2000}, new int[]{1, 1600}, new int[]{1, 1250}, new int[]{1, SsdpDevice.RETRY_INTERVAL}, new int[]{1, 800}, new int[]{1, AppRoot.USER_KEYCODE.WATER_HOUSING}, new int[]{1, 500}, new int[]{1, 400}, new int[]{1, 320}, new int[]{1, 250}, new int[]{1, IntervalRecExecutor.INTVL_REC_INITIALIZED}, new int[]{1, 160}, new int[]{1, 125}, new int[]{1, 100}, new int[]{1, 80}, new int[]{1, 60}, new int[]{1, 50}, new int[]{1, 40}, new int[]{1, 30}, new int[]{1, 25}, new int[]{1, 20}, new int[]{1, 15}, new int[]{1, 13}, new int[]{1, 10}, new int[]{1, 8}, new int[]{1, 6}, new int[]{1, 5}, new int[]{1, 4}, new int[]{1, 3}, new int[]{4, 10}, new int[]{5, 10}, new int[]{10, 16}, new int[]{8, 10}, new int[]{1, 1}, new int[]{13, 10}, new int[]{16, 10}, new int[]{2, 1}, new int[]{25, 10}, new int[]{32, 10}, new int[]{4, 1}, new int[]{5, 1}, new int[]{6, 1}, new int[]{8, 1}, new int[]{10, 1}, new int[]{13, 1}, new int[]{15, 1}, new int[]{20, 1}, new int[]{25, 1}, new int[]{30, 1}, new int[]{Info.INVALID_CAUTION_ID, 1}};
    public static final String[] s_EMPTY_STRING_ARRAY = new String[0];
    public static final int[] s_EMPTY_INT_ARRAY = new int[0];
    public static final Integer[] s_EMPTY_INTEGER_ARRAY = new Integer[0];

    private static float[] createNumberTableF1_8() {
        float[] tmp = Arrays.copyOf(s_FNUMBER_TABLE, s_FNUMBER_TABLE.length);
        tmp[Arrays.binarySearch(tmp, 1.7f)] = 1.8f;
        return tmp;
    }

    private static float[] createNumberTableF2_4() {
        float[] tmp = Arrays.copyOf(s_FNUMBER_TABLE, s_FNUMBER_TABLE.length);
        tmp[Arrays.binarySearch(tmp, 2.5f)] = 2.4f;
        return tmp;
    }

    public static final float[] getFNumberTable(int availableMin) {
        switch (availableMin) {
            case 180:
                return s_FNUMBER_TABLE_F1_8;
            case 240:
                return s_FNUMBER_TABLE_F2_4;
            default:
                return s_FNUMBER_TABLE;
        }
    }

    public static final int[][] getShutterSpeedTable() {
        return s_SHUTTER_SPEED_TABLE;
    }
}
