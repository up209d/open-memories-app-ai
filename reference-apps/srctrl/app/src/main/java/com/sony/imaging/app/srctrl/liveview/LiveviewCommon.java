package com.sony.imaging.app.srctrl.liveview;

import com.sony.imaging.app.base.common.widget.BatteryIcon;

/* loaded from: classes.dex */
public abstract class LiveviewCommon {
    public static final byte ADDITIONAL_STATUS_INVALID = 0;
    public static final byte ADDITIONAL_STATUS_LARGE_FRAME = 2;
    public static final byte ADDITIONAL_STATUS_SELECTED = 1;
    public static final byte CATEGORY_CONTRAST = 1;
    public static final byte CATEGORY_FACE = 4;
    public static final byte CATEGORY_INVALID = 0;
    public static final byte CATEGORY_PHASE_DETECTION = 2;
    public static final byte CATEGORY_SURFASE_PHASE_DETECTION = 3;
    public static final byte CATEGORY_TRACKING = 5;
    public static final byte COMMON_HEADER_PAYLOAD_TYPE_FRAMEINFO = 2;
    public static final byte COMMON_HEADER_PAYLOAD_TYPE_IMAGES = 1;
    public static final int COMMON_HEADER_SIZE = 8;
    public static final byte COMMON_HEADER_START_BYTE = -1;
    public static final int FRAME_INFO_DATA_DATA_SIZE = 16;
    public static final int FRAME_INFO_DATA_MAX_NUM = 39;
    public static final int FRAME_INFO_DATA_MAX_SIZE = 624;
    public static final byte FRAME_INFO_DATA_VERSION_MJ = 1;
    public static final byte FRAME_INFO_DATA_VERSION_MN = 0;
    public static final int FRAME_INTERVAL = 30;
    public static final int HEADER_DATA_SIZE_MAX = 136;
    public static final byte PADDING_SIZE = 0;
    public static final int PAYLOAD_HEADER_FLAG = 0;
    public static final int PAYLOAD_HEADER_SIZE = 128;
    public static final byte PAYLOAD_HEADER_START_CODE_BYTE1 = 36;
    public static final byte PAYLOAD_HEADER_START_CODE_BYTE2 = 53;
    public static final byte PAYLOAD_HEADER_START_CODE_BYTE3 = 104;
    public static final byte PAYLOAD_HEADER_START_CODE_BYTE4 = 121;
    public static final byte SINGLE_FRAME_INFO_DATA_SIZE = 16;
    public static final byte STATUS_FOCUSED = 4;
    public static final byte STATUS_INVALID = 0;
    public static final byte STATUS_MAIN = 2;
    public static final byte STATUS_NORMAL = 1;
    public static final byte STATUS_REGISTERED = 6;
    public static final byte STATUS_RENGE = 7;
    public static final byte STATUS_SMILE = 5;
    public static final byte STATUS_SUB = 3;
    public static final int WEB_API_COORDINATE_X = 10000;
    public static final int WEB_API_COORDINATE_Y = 10000;
    private static volatile short commonHeaderSequenceNumber = 0;
    private static long commonHeaderTimeStampBase = System.currentTimeMillis();
    private final String TAG = LiveviewCommon.class.getName();

    public static void reset() {
        commonHeaderSequenceNumber = (short) 0;
        commonHeaderTimeStampBase = System.currentTimeMillis();
    }

    public static short getCurrentSequenceNumber() {
        return commonHeaderSequenceNumber;
    }

    public static void incrementSequenceNumber() {
        commonHeaderSequenceNumber = (short) (commonHeaderSequenceNumber + 1);
    }

    public static int getTimeStamp() {
        return (int) (System.currentTimeMillis() - commonHeaderTimeStampBase);
    }

    public static void setNetworkByte(byte[] dst, int dstIndex, int src, int srcSize) {
        int di = dstIndex + srcSize;
        int s = src;
        for (int i = 0; i < srcSize; i++) {
            di--;
            dst[di] = (byte) (s & BatteryIcon.BATTERY_STATUS_CHARGING);
            s >>>= 8;
        }
    }
}
