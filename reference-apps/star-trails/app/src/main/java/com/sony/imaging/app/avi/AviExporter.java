package com.sony.imaging.app.avi;

import android.util.Log;
import com.sony.imaging.app.avi.header.AVI00DC;
import com.sony.imaging.app.avi.header.AVIINDEX;
import com.sony.imaging.app.avi.header.AVITOP;
import com.sony.imaging.app.avi.sa.SaJpegParam;
import com.sony.imaging.app.avi.sa.SaUtil;
import com.sony.imaging.app.avi.thum.ThumExporter;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class AviExporter {
    private static final int AVI_REQUIRED_MIN_SIZE_BYTE = 16777216;
    public static final int COMPRESS_RETRY_SIZE = 5;
    public static final int FRAMERATE_10 = 10;
    public static final int FRAMERATE_24 = 24;
    public static final int FRAMERATE_30 = 30;
    public static final int HEIGHT_1080 = 1080;
    public static final int HEIGHT_2160 = 2160;
    public static final int HEIGHT_360 = 360;
    public static final int HEIGHT_720 = 720;
    private static final int JPEGDBSIZE = 2097152;
    private static final int JPEG_LOG_ADDR_OFFSET = 2095104;
    private static final int JPEG_OUTPUT_SIZE_LIMIT = 2093056;
    private static final int MAXFRAMENUM = 4096;
    private static final int MCUWIDTH = 16;
    public static final int PIXEL_SIZE_BYTE_2 = 2;
    private static final int RETRY_MAX_SIZE = 788480;
    private static final int RETRY_TARGET_SIZE = 785408;
    private static final int SAMBXBUFSIZE = 256;
    private static final String TAG = "AviExporter";
    private static final String VERSION = "2014/11/14";
    public static final int WIDTH_1280 = 1280;
    public static final int WIDTH_1920 = 1920;
    public static final int WIDTH_3840 = 3840;
    public static final int WIDTH_640 = 640;
    private static final int mJpegHeaderOfstSofHeight = 561;
    private static final int mJpegHeaderOfstSofQtableC = 72;
    private static final int mJpegHeaderOfstSofQtableY = 7;
    private static final int mJpegHeaderOfstSofWidth = 563;
    static final byte mSaJpegParamMaskColMode = Byte.MAX_VALUE;
    static final int mSaJpegParamOfstColCoeff = 256;
    static final int mSaJpegParamOfstColMode = 1;
    private static final int mSaJpegParamOfstIMAGEWIDTH = 32;
    private static final int mSaJpegParamOfstInputStartAddress = 52;
    private static final int mSaJpegParamOfstJHWIDTH = 24;
    private static final int mSaJpegParamOfstJVHEIGHT = 28;
    private static final int mSaJpegParamOfstLogOutputAddress = 92;
    private static final int mSaJpegParamOfstOutputStartAddress = 68;
    private static final int mSaJpegParamOfstQtableC = 192;
    private static final int mSaJpegParamSize = 1024;
    private static final byte[] mJpegHeaderBaseQtableY = {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 3, 4, 5, 4, 3, 3, 3, 4, 5, 7, 5, 4, 3, 3, 4, 5, 7, 7, 6, 5, 4, 5, 6, 7, 8, 6, 5, 5, 6, 8, 8, 7, 7, 7, 8, 9, 8, 8, 9, 10, 10, 10, 12, 12, 14};
    private static final byte[] mJpegHeaderBaseQtableC = {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 3, 4, 5, 4, 3, 3, 3, 4, 5, 7, 5, 4, 3, 3, 4, 5, 7, 7, 6, 5, 4, 5, 6, 7, 8, 6, 5, 5, 6, 8, 8, 7, 7, 7, 8, 9, 8, 8, 9, 10, 10, 10, 12, 12, 14};
    private static final int[] mRetryTableQscaleStd = {SaJpegParam.PARAM_SIZE_ALLOC, 775, 1163, 1828, 2844, 4266, 6400, 12800, 25600};
    private static final int[] mRetryTableQscaleFine = {256, 387, 581, 853, 1280, 1969, 2844, 4266, 6400, 8533, 12800, 25600};
    private static final int mSaJpegParamOfstQtableY = 128;
    private static final int[] mRetryTableQscaleExFine = {32, 64, 96, mSaJpegParamOfstQtableY, 134, 150, 170, 196, AppRoot.USER_KEYCODE.CENTER, 320, 387, 581, 853, 1280, 1969, 2844, 4266, 6400, 8533, 12800, 25600};
    private Status mStatus = Status.CLOSED;
    private DeviceBuffer m_deviceBootParam = null;
    private DeviceBuffer m_deviceProgramParam = null;
    private boolean disableResAllocOptimization = true;
    private String m_salibPath = null;
    private byte[] mSaJpegParam = {0, 0, 0, 0, 0, 0, 0, 0, 32, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 0, 0, 0, 56, 4, 0, 0, COLORI_PARAM_ADOBE_TO_601.col_mode, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, -16, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 4, 16, 0, 1, 4, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] mJpegHeader = {-1, -40, -1, -37, 0, -124, 0, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -60, 1, -94, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -64, 0, 17, 8, 4, 56, 7, COLORI_PARAM_ADOBE_TO_601.col_mode, 3, 1, 33, 0, 2, 17, 1, 3, 17, 1, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0};
    private int[] mRetryTableQscale = null;
    private DeviceBuffer m_jpeg_devicebuffer = null;
    private byte[] m_jpeg_bytearray = null;
    private int m_jpeg_bytearray_size = 0;
    private byte[] m_mbox_buf = null;
    private FileOutputStreamUtil m_fosu_avi = null;
    private int[] m_sizelist = null;
    private int m_width = 1920;
    private int m_height = 1080;
    private int m_fps = 24;
    private int m_framenum = 0;
    private AVITOP m_avitop = null;
    private AVIINDEX m_aviindex = null;
    private int m_stream_size = 0;
    private String m_avi_filename = null;
    private boolean mIsAdobeRGB = false;
    private int m_lastRetryIndex = 0;
    DSP m_dsp = null;
    ByteBuffer m_bootParam = null;
    long[] jpegTc = {0, 0, 0, 0, 0};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum COLORI_METORY_MODE {
        NONE,
        ADOBE_TO_601
    }

    /* loaded from: classes.dex */
    public static class Options {
        public String fullPathFileName;
        public String libpath;
        public int width = 1920;
        public int height = 1080;
        public int fps = 24;
        public boolean isAdobeRGB = false;
        public boolean disableResAllocOptimization = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Status {
        OPENED,
        CLOSED,
        ERROR
    }

    public static int getMaxFrameSize(int width, int height) {
        int size = (((width * height) * 2) / 5) + 3072;
        return Math.min(size, RETRY_MAX_SIZE);
    }

    public static int getTargetFrameSize(int width, int height) {
        int size = ((width * height) * 2) / 5;
        return Math.min(size, RETRY_TARGET_SIZE);
    }

    private boolean setQscale(int qScale) {
        for (int n = 0; n < 64; n += 4) {
            byte[] bArr = this.mSaJpegParam;
            int i = n + mSaJpegParamOfstQtableY + 3;
            byte q = getQ(mJpegHeaderBaseQtableY[n + 0], qScale);
            this.mJpegHeader[n + 7 + 0] = q;
            bArr[i] = q;
            byte[] bArr2 = this.mSaJpegParam;
            int i2 = n + mSaJpegParamOfstQtableY + 2;
            byte q2 = getQ(mJpegHeaderBaseQtableY[n + 1], qScale);
            this.mJpegHeader[n + 7 + 1] = q2;
            bArr2[i2] = q2;
            byte[] bArr3 = this.mSaJpegParam;
            int i3 = n + mSaJpegParamOfstQtableY + 1;
            byte q3 = getQ(mJpegHeaderBaseQtableY[n + 2], qScale);
            this.mJpegHeader[n + 7 + 2] = q3;
            bArr3[i3] = q3;
            byte[] bArr4 = this.mSaJpegParam;
            int i4 = n + mSaJpegParamOfstQtableY + 0;
            byte q4 = getQ(mJpegHeaderBaseQtableY[n + 3], qScale);
            this.mJpegHeader[n + 7 + 3] = q4;
            bArr4[i4] = q4;
        }
        for (int n2 = 0; n2 < 64; n2 += 4) {
            byte[] bArr5 = this.mSaJpegParam;
            int i5 = n2 + mSaJpegParamOfstQtableC + 3;
            byte[] bArr6 = this.mJpegHeader;
            int i6 = n2 + mJpegHeaderOfstSofQtableC + 0;
            byte q5 = getQ(mJpegHeaderBaseQtableC[n2 + 0], qScale);
            bArr6[i6] = q5;
            bArr5[i5] = q5;
            byte[] bArr7 = this.mSaJpegParam;
            int i7 = n2 + mSaJpegParamOfstQtableC + 2;
            byte[] bArr8 = this.mJpegHeader;
            int i8 = n2 + mJpegHeaderOfstSofQtableC + 1;
            byte q6 = getQ(mJpegHeaderBaseQtableC[n2 + 1], qScale);
            bArr8[i8] = q6;
            bArr7[i7] = q6;
            byte[] bArr9 = this.mSaJpegParam;
            int i9 = n2 + mSaJpegParamOfstQtableC + 1;
            byte[] bArr10 = this.mJpegHeader;
            int i10 = n2 + mJpegHeaderOfstSofQtableC + 2;
            byte q7 = getQ(mJpegHeaderBaseQtableC[n2 + 2], qScale);
            bArr10[i10] = q7;
            bArr9[i9] = q7;
            byte[] bArr11 = this.mSaJpegParam;
            int i11 = n2 + mSaJpegParamOfstQtableC + 0;
            byte[] bArr12 = this.mJpegHeader;
            int i12 = n2 + mJpegHeaderOfstSofQtableC + 3;
            byte q8 = getQ(mJpegHeaderBaseQtableC[n2 + 3], qScale);
            bArr12[i12] = q8;
            bArr11[i11] = q8;
        }
        return true;
    }

    private static byte getQ(byte baseQ, int qScale) {
        int q = ((baseQ & BatteryIcon.BATTERY_STATUS_CHARGING) * qScale) >> 8;
        if (q < 1) {
            q = 1;
        } else if (q > 255) {
            q = 255;
        }
        return (byte) q;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class COLORI_PARAM_NONE {
        public static final byte[] col_coeff = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        public static final byte col_mode = 0;

        private COLORI_PARAM_NONE() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class COLORI_PARAM_ADOBE_TO_601 {
        public static final byte[] col_coeff = {44, 24, 0, 16, -19, 91, 4, 0, 84, 45, 0, 0, 0, 0, 0, 0};
        public static final byte col_mode = Byte.MIN_VALUE;

        private COLORI_PARAM_ADOBE_TO_601() {
        }
    }

    private void setColoriMetoryParam_ColMode(byte col_mode) {
        this.mSaJpegParam[1] = (byte) ((this.mSaJpegParam[1] & mSaJpegParamMaskColMode) | col_mode);
    }

    private void setColoriMetoryParam_ColCoeff(byte[] col_coeff) {
        for (int n = 0; n < col_coeff.length; n++) {
            this.mSaJpegParam[n + 256] = col_coeff[n];
        }
    }

    private boolean setColoriMetoryMode(COLORI_METORY_MODE mode) {
        if (mode == COLORI_METORY_MODE.NONE) {
            setColoriMetoryParam_ColMode((byte) 0);
            setColoriMetoryParam_ColCoeff(COLORI_PARAM_NONE.col_coeff);
            return true;
        }
        if (mode == COLORI_METORY_MODE.ADOBE_TO_601) {
            setColoriMetoryParam_ColMode(COLORI_PARAM_ADOBE_TO_601.col_mode);
            setColoriMetoryParam_ColCoeff(COLORI_PARAM_ADOBE_TO_601.col_coeff);
            return true;
        }
        setColoriMetoryParam_ColMode((byte) 0);
        setColoriMetoryParam_ColCoeff(COLORI_PARAM_NONE.col_coeff);
        return false;
    }

    public static int getRequiredMediaCapacity(int width, int height, int frameNum) {
        int size = (getMaxFrameSize(width, height) * Math.max(frameNum, 1)) + AVI_REQUIRED_MIN_SIZE_BYTE;
        return size;
    }

    public static int getAvailableFrameNum(int width, int height, int availableByteSize) {
        int frameSize = getMaxFrameSize(width, height);
        if (frameSize <= 0) {
            return -1;
        }
        return (availableByteSize - AVI_REQUIRED_MIN_SIZE_BYTE) / frameSize;
    }

    public AviExporter() {
        Log.i(TAG, "AVI Lib Ver. 2014/11/14");
    }

    public synchronized boolean open(Options options) throws Exception {
        boolean z = false;
        synchronized (this) {
            Log.i(TAG, "open()");
            if (this.mStatus != Status.CLOSED) {
                Log.e(TAG, "Failed to open!!");
            } else if (options != null && options.fullPathFileName != null && options.libpath != null) {
                this.m_sizelist = new int[MAXFRAMENUM];
                this.m_mbox_buf = new byte[256];
                this.m_fps = options.fps;
                this.m_stream_size = 0;
                this.m_width = options.width;
                this.m_height = options.height;
                this.m_avi_filename = options.fullPathFileName;
                this.m_framenum = 0;
                this.m_salibPath = options.libpath;
                this.mIsAdobeRGB = false;
                this.m_lastRetryIndex = 0;
                Log.i(TAG, " options.fps = " + options.fps);
                Log.i(TAG, " options.width = " + options.width);
                Log.i(TAG, " options.height = " + options.height);
                Log.i(TAG, " options.fullPathFileName = " + options.fullPathFileName);
                Log.i(TAG, " options.libpath = " + options.libpath);
                Log.i(TAG, " options.isAdobeRGB = " + options.isAdobeRGB);
                Log.i(TAG, " options.disableResAllocOptimization = " + options.disableResAllocOptimization);
                if (this.mIsAdobeRGB) {
                    setColoriMetoryMode(COLORI_METORY_MODE.ADOBE_TO_601);
                    Log.d(TAG, "This is AdobeRGB!!");
                } else {
                    setColoriMetoryMode(COLORI_METORY_MODE.NONE);
                    Log.d(TAG, "This is NOT AdobeRGB!!");
                }
                this.m_fosu_avi = new FileOutputStreamUtil(this.m_avi_filename);
                this.m_avitop = new AVITOP();
                this.m_avitop.setFrameRate(1001, this.m_fps * 1000);
                this.m_avitop.setPictureSize(this.m_width, this.m_height);
                this.m_avitop.setDataToOstream(this.m_fosu_avi);
                this.m_aviindex = new AVIINDEX();
                this.m_aviindex.createAVIINDEXENTRY(MAXFRAMENUM);
                setSizeToJpegHeader((short) this.m_width, (short) this.m_height);
                if (this.m_width == 1920 || this.m_width == 1280 || this.m_width == 3840) {
                    this.disableResAllocOptimization = options.disableResAllocOptimization;
                    this.mRetryTableQscale = mRetryTableQscaleExFine;
                } else {
                    this.disableResAllocOptimization = false;
                    this.mRetryTableQscale = mRetryTableQscaleExFine;
                }
                if (!this.disableResAllocOptimization) {
                    openDspRes();
                }
                int frameSize = getMaxFrameSize(this.m_width, this.m_height);
                this.m_jpeg_bytearray = new byte[frameSize];
                this.mStatus = Status.OPENED;
                z = true;
            }
        }
        return z;
    }

    private synchronized void openDspRes() {
        if (this.m_dsp == null) {
            this.m_dsp = DSP.createProcessor("sony-di-dsp");
            if (SaUtil.isAVIP()) {
                this.m_dsp.setProgram(this.m_salibPath + "libsajpg.so");
            } else {
                this.m_dsp.setProgram(SaJpegParam.SA_PROGRAM_ENC);
            }
        }
        if (this.m_deviceBootParam == null) {
            this.m_deviceBootParam = this.m_dsp.createBuffer(SaJpegParam.PARAM_SIZE_ALLOC);
        }
        if (this.m_deviceProgramParam == null) {
            this.m_deviceProgramParam = this.m_dsp.createBuffer(mSaJpegParamSize);
        }
        if (this.m_bootParam == null) {
            int paramAxiAddr = SaUtil.convArmAddr2AxiAddr(this.m_dsp.getPropertyAsInt(this.m_deviceProgramParam, "memory-address"));
            this.m_bootParam = ByteBuffer.allocateDirect(60);
            this.m_bootParam.order(ByteOrder.nativeOrder());
            this.m_bootParam.putInt(this.m_dsp.getPropertyAsInt("program-descriptor"));
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            if (SaUtil.isAVIP()) {
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.putInt(0);
                this.m_bootParam.putInt(0);
                this.m_bootParam.putInt(0);
                this.m_bootParam.putInt(0);
            } else {
                this.m_bootParam.put((byte) 1);
                this.m_bootParam.put((byte) 1);
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.put((byte) 0);
                this.m_bootParam.putInt(SaJpegParam.JPEG_CMD_ENC);
                this.m_bootParam.putInt(paramAxiAddr);
                this.m_bootParam.putInt(0);
                this.m_bootParam.putInt(0);
            }
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.putInt(0);
            this.m_bootParam.putInt(0);
            this.m_bootParam.putInt(0);
            this.m_bootParam.putInt(0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.put((byte) 0);
            this.m_bootParam.putInt(0);
            this.m_bootParam.putInt(0);
        }
        if (this.m_jpeg_devicebuffer == null) {
            this.m_jpeg_devicebuffer = this.m_dsp.createBuffer(JPEGDBSIZE);
        }
    }

    private synchronized void closeDspRes() {
        if (this.m_dsp != null) {
            this.m_dsp.clearProgram();
            this.m_dsp.release();
            this.m_dsp = null;
        }
        if (this.m_bootParam != null) {
            this.m_bootParam.clear();
            this.m_bootParam = null;
        }
        if (this.m_jpeg_devicebuffer != null) {
            this.m_jpeg_devicebuffer.release();
            this.m_jpeg_devicebuffer = null;
        }
        if (this.m_deviceBootParam != null) {
            this.m_deviceBootParam.release();
            this.m_deviceBootParam = null;
        }
        if (this.m_deviceProgramParam != null) {
            this.m_deviceProgramParam.release();
            this.m_deviceProgramParam = null;
        }
    }

    public synchronized boolean storeFrame(OptimizedImage yc, boolean releaseSource) throws Exception {
        boolean result;
        long[] tc = {0, 0, 0, 0, 0, 0};
        tc[0] = System.nanoTime();
        Log.i(TAG, "storeFrame()");
        if (this.mStatus != Status.OPENED) {
            Log.e(TAG, "Failed to storeFrame!!");
            result = false;
        } else {
            boolean result2 = true;
            if (yc == null) {
                result = false;
            } else {
                tc[1] = System.nanoTime();
                if (this.disableResAllocOptimization) {
                    openDspRes();
                }
                tc[2] = System.nanoTime();
                byte[] jpeg = createJpegFromYcImage(yc);
                if (jpeg == null) {
                    result2 = false;
                }
                tc[3] = System.nanoTime();
                result = result2 & storeJpegFrame(jpeg, this.m_jpeg_bytearray_size);
                tc[4] = System.nanoTime();
                if (releaseSource) {
                    yc.release();
                }
                if (this.disableResAllocOptimization) {
                    closeDspRes();
                }
                tc[5] = System.nanoTime();
                Log.i(TAG, "[wp1]storeFrame: " + (tc[1] - tc[0]) + ExposureModeController.SOFT_SNAP + (tc[2] - tc[1]) + ExposureModeController.SOFT_SNAP + (this.jpegTc[1] - this.jpegTc[0]) + ExposureModeController.SOFT_SNAP + (this.jpegTc[2] - this.jpegTc[1]) + ExposureModeController.SOFT_SNAP + (this.jpegTc[3] - this.jpegTc[2]) + ExposureModeController.SOFT_SNAP + (this.jpegTc[4] - this.jpegTc[3]) + ExposureModeController.SOFT_SNAP + (tc[4] - tc[3]) + ExposureModeController.SOFT_SNAP + (tc[5] - tc[4]));
            }
        }
        return result;
    }

    public synchronized byte[] createJpegFromYcBuffer(DeviceBuffer ycBuffer_db, int width, int height, int frameNum) throws Exception {
        byte[] bArr = null;
        synchronized (this) {
            Log.i(TAG, "createJpegFromYcBuffer()");
            if (this.mStatus != Status.OPENED) {
                Log.e(TAG, "Failed to createJpegFromYcBuffer!!");
            } else if (ycBuffer_db == null) {
                Log.e(TAG, "ycBuffer_db is null!!");
                Log.e(TAG, "createJpegFromYcBuffer() failed!!");
            } else {
                try {
                    int address = SaUtil.convArmAddr2AxiAddr(this.m_dsp.getPropertyAsInt(ycBuffer_db, "memory-address")) + (width * height * 2 * frameNum);
                    int db_size = this.m_dsp.getPropertyAsInt(ycBuffer_db, "memory-size");
                    if (width * height * 2 * (frameNum + 1) > db_size) {
                        Log.e(TAG, "Device buffer size error!!");
                        Log.e(TAG, "createJpegFromYcBuffer() failed!!");
                    } else {
                        bArr = executeCreateJpegSATop(this.m_dsp, ycBuffer_db, address, width, height, width, false);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return bArr;
    }

    public synchronized byte[] createJpegFromYcImage(OptimizedImage yc) throws Exception {
        byte[] bArr = null;
        synchronized (this) {
            this.jpegTc[0] = System.nanoTime();
            Log.i(TAG, "createJpegFromYcImage()");
            if (this.mStatus != Status.OPENED) {
                Log.e(TAG, "Failed to createJpegFromYcImage!!");
            } else if (yc == null) {
                Log.e(TAG, "yc is null!!");
                Log.e(TAG, "createJpegFromYcImage() failed!!");
            } else {
                try {
                    long tc0 = System.nanoTime();
                    int address = SaUtil.convArmAddr2AxiAddr(this.m_dsp.getPropertyAsInt(yc, "memory-address"));
                    int width = this.m_dsp.getPropertyAsInt(yc, "image-target-width");
                    int height = this.m_dsp.getPropertyAsInt(yc, "image-target-height");
                    int cwidth = this.m_dsp.getPropertyAsInt(yc, "image-canvas-width");
                    long tc1 = System.nanoTime();
                    Log.i(TAG, "getPropertyAsInt x 4 = " + (tc1 - tc0));
                    bArr = executeCreateJpegSATop(this.m_dsp, yc, address, width, height, cwidth);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return bArr;
    }

    public synchronized boolean storeJpegFrame(byte[] jpeg) {
        return storeJpegFrame(this.m_jpeg_bytearray, this.m_jpeg_bytearray_size);
    }

    public synchronized boolean storeJpegFrame(byte[] jpeg, int jpegLength) {
        boolean result;
        Log.i(TAG, "storeJpegFrame()");
        if (this.mStatus != Status.OPENED) {
            Log.e(TAG, "Failed to storeJpegFrame!!");
            result = false;
        } else {
            result = true;
            if (jpeg == null) {
                Log.e(TAG, "jpeg is null!!");
                Log.e(TAG, "storeJpegFrame() failed!!");
                result = false;
            } else {
                int size = AVI00DC.setDataToOstream(this.m_fosu_avi, jpeg, jpegLength);
                if (size <= 0) {
                    size = -size;
                    result = false;
                    Log.e(TAG, "storeJpegFrame() failed!!");
                }
                this.m_sizelist[this.m_framenum] = size - 8;
                this.m_stream_size += size;
                this.m_framenum++;
            }
        }
        return result;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x005a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean close() throws java.lang.Exception {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r5 = "AviExporter"
            java.lang.String r6 = "close()"
            android.util.Log.i(r5, r6)     // Catch: java.lang.Throwable -> L8a
            r4 = 1
            r9.closeDspRes()     // Catch: java.lang.Throwable -> L8a
            r1 = 0
            com.sony.imaging.app.avi.FileOutputStreamUtil r5 = r9.m_fosu_avi     // Catch: java.lang.Throwable -> L8a
            if (r5 == 0) goto L8d
            com.sony.imaging.app.avi.header.AVIINDEX r5 = r9.m_aviindex     // Catch: java.lang.Throwable -> L8a
            if (r5 == 0) goto L21
            com.sony.imaging.app.avi.header.AVIINDEX r5 = r9.m_aviindex     // Catch: java.lang.Throwable -> L8a
            com.sony.imaging.app.avi.FileOutputStreamUtil r6 = r9.m_fosu_avi     // Catch: java.lang.Throwable -> L8a
            int[] r7 = r9.m_sizelist     // Catch: java.lang.Throwable -> L8a
            int r8 = r9.m_framenum     // Catch: java.lang.Throwable -> L8a
            int r1 = r5.setDataToOstream(r6, r7, r8)     // Catch: java.lang.Throwable -> L8a
        L21:
            com.sony.imaging.app.avi.FileOutputStreamUtil r5 = r9.m_fosu_avi     // Catch: java.io.IOException -> L7e java.lang.Throwable -> L8a
            r5.close()     // Catch: java.io.IOException -> L7e java.lang.Throwable -> L8a
        L26:
            r5 = 0
            r9.m_fosu_avi = r5     // Catch: java.lang.Throwable -> L8a
            com.sony.imaging.app.avi.header.AVITOP r5 = r9.m_avitop     // Catch: java.lang.Throwable -> L8a
            if (r5 == 0) goto La7
            java.lang.String r5 = r9.m_avi_filename     // Catch: java.lang.Throwable -> L8a
            if (r5 == 0) goto La7
            com.sony.imaging.app.avi.header.AVITOP r5 = r9.m_avitop     // Catch: java.lang.Throwable -> L8a
            int r6 = r9.m_framenum     // Catch: java.lang.Throwable -> L8a
            r5.setFrameNum(r6)     // Catch: java.lang.Throwable -> L8a
            com.sony.imaging.app.avi.header.AVITOP r5 = r9.m_avitop     // Catch: java.lang.Throwable -> L8a
            int r6 = r9.m_stream_size     // Catch: java.lang.Throwable -> L8a
            r5.setStreamSize(r6, r1)     // Catch: java.lang.Throwable -> L8a
            com.sony.imaging.app.avi.header.AVITOP r5 = r9.m_avitop     // Catch: java.lang.Throwable -> L8a
            int r6 = r9.m_width     // Catch: java.lang.Throwable -> L8a
            int r7 = r9.m_height     // Catch: java.lang.Throwable -> L8a
            r5.setPictureSize(r6, r7)     // Catch: java.lang.Throwable -> L8a
            r2 = 0
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8f
            java.lang.String r5 = r9.m_avi_filename     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8f
            java.lang.String r6 = "rw"
            r3.<init>(r5, r6)     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> L8f
            com.sony.imaging.app.avi.header.AVITOP r5 = r9.m_avitop     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> La9
            r5.setDataToRAF(r3)     // Catch: java.lang.Throwable -> L8a java.io.FileNotFoundException -> La9
            r2 = r3
        L58:
            if (r2 == 0) goto L5d
            r2.close()     // Catch: java.lang.Throwable -> L8a java.lang.Exception -> L9b
        L5d:
            r5 = 0
            r9.m_avitop = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_aviindex = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_sizelist = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_mbox_buf = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_jpeg_bytearray = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_avi_filename = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.mRetryTableQscale = r5     // Catch: java.lang.Throwable -> L8a
            r5 = 0
            r9.m_lastRetryIndex = r5     // Catch: java.lang.Throwable -> L8a
            com.sony.imaging.app.avi.AviExporter$Status r5 = com.sony.imaging.app.avi.AviExporter.Status.CLOSED     // Catch: java.lang.Throwable -> L8a
            r9.mStatus = r5     // Catch: java.lang.Throwable -> L8a
            java.lang.System.gc()     // Catch: java.lang.Throwable -> L8a
            monitor-exit(r9)
            return r4
        L7e:
            r0 = move-exception
            r4 = 0
            java.lang.String r5 = "AviExporter"
            java.lang.String r6 = r0.toString()     // Catch: java.lang.Throwable -> L8a
            android.util.Log.e(r5, r6)     // Catch: java.lang.Throwable -> L8a
            goto L26
        L8a:
            r5 = move-exception
            monitor-exit(r9)
            throw r5
        L8d:
            r4 = 0
            goto L26
        L8f:
            r0 = move-exception
        L90:
            r4 = 0
            java.lang.String r5 = "AviExporter"
            java.lang.String r6 = r0.toString()     // Catch: java.lang.Throwable -> L8a
            android.util.Log.e(r5, r6)     // Catch: java.lang.Throwable -> L8a
            goto L58
        L9b:
            r0 = move-exception
            r4 = 0
            java.lang.String r5 = "AviExporter"
            java.lang.String r6 = r0.toString()     // Catch: java.lang.Throwable -> L8a
            android.util.Log.e(r5, r6)     // Catch: java.lang.Throwable -> L8a
            goto L5d
        La7:
            r4 = 0
            goto L5d
        La9:
            r0 = move-exception
            r2 = r3
            goto L90
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.avi.AviExporter.close():boolean");
    }

    public synchronized boolean storeThumbnail(DeviceBuffer yc, int width, int height, int frame_num) {
        boolean storeThumbnail;
        Log.i(TAG, "storeThumbnail() For Cinematic Photo");
        if (this.mStatus != Status.OPENED) {
            Log.e(TAG, "Failed to storeThumbnail!!");
            storeThumbnail = false;
        } else {
            ThumExporter thumExporter = new ThumExporter();
            ThumExporter.Options thumOptions = new ThumExporter.Options();
            thumOptions.width = this.m_width;
            thumOptions.height = this.m_height;
            thumOptions.libpath = this.m_salibPath;
            thumOptions.aviFilename = this.m_avi_filename;
            thumExporter.initParam(thumOptions);
            storeThumbnail = thumExporter.storeThumbnail(yc, width, height, frame_num);
        }
        return storeThumbnail;
    }

    public synchronized boolean storeThumbnail(OptimizedImage yc, boolean releasedResource) {
        boolean storeThumbnail;
        Log.i(TAG, "storeThumbnail() for TimeLapse");
        if (this.mStatus != Status.OPENED) {
            Log.e(TAG, "Failed to storeThumbnail!!");
            storeThumbnail = false;
        } else {
            ThumExporter thumExporter = new ThumExporter();
            ThumExporter.Options thumOptions = new ThumExporter.Options();
            thumOptions.width = this.m_width;
            thumOptions.height = this.m_height;
            thumOptions.libpath = this.m_salibPath;
            thumOptions.aviFilename = this.m_avi_filename;
            thumExporter.initParam(thumOptions);
            storeThumbnail = thumExporter.storeThumbnail(yc, releasedResource);
        }
        return storeThumbnail;
    }

    private synchronized void setSizeToJpegHeader(short width, short height) {
        SaUtil.writeValueToByteArray(this.mJpegHeader, mJpegHeaderOfstSofHeight, height, true);
        SaUtil.writeValueToByteArray(this.mJpegHeader, mJpegHeaderOfstSofWidth, width, true);
    }

    private synchronized byte[] executeCreateJpegSATop(DSP dsp, DeviceMemory yc, int address, int width, int height, int cwidth) {
        return executeCreateJpegSATop(dsp, yc, address, width, height, cwidth, true);
    }

    private synchronized byte[] executeCreateJpegSATop(DSP dsp, DeviceMemory yc, int address, int width, int height, int cwidth, boolean returnByteArray) {
        byte[] jpeg;
        this.jpegTc[1] = System.nanoTime();
        long ts = System.nanoTime();
        int streamSizeLimit = getTargetFrameSize(width, height);
        try {
            int inAddr = SaUtil.convArmAddr2AxiAddr(address);
            SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstInputStartAddress, inAddr, false);
            int outAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(this.m_jpeg_devicebuffer, "memory-address"));
            SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstOutputStartAddress, outAddr, false);
            if (!SaUtil.isAVIP()) {
                int logOutAddr = outAddr + JPEG_LOG_ADDR_OFFSET;
                SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstLogOutputAddress, logOutAddr, false);
            }
            SaJpegParam.setMMUParam(this.mSaJpegParam, inAddr, outAddr);
            int mcuWidth = width / 16;
            SaUtil.writeValueToByteArray(this.mSaJpegParam, 24, mcuWidth, false);
            SaUtil.writeValueToByteArray(this.mSaJpegParam, 28, height, false);
            SaUtil.writeValueToByteArray(this.mSaJpegParam, 32, cwidth, false);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        this.jpegTc[2] = System.nanoTime();
        int sizeEcs = 0;
        int size = 0;
        int retryCount = Math.max(this.m_lastRetryIndex - 1, 0);
        while (retryCount < this.mRetryTableQscale.length) {
            setQscale(this.mRetryTableQscale[retryCount]);
            this.m_lastRetryIndex = retryCount;
            ByteBuffer programParam = ByteBuffer.allocateDirect(mSaJpegParamSize);
            programParam.put(this.mSaJpegParam, 0, this.mSaJpegParam.length);
            programParam.order(ByteOrder.nativeOrder());
            this.m_bootParam.rewind();
            this.m_deviceBootParam.write(this.m_bootParam);
            programParam.rewind();
            this.m_deviceProgramParam.write(programParam);
            dsp.setArg(0, this.m_deviceBootParam);
            if (SaUtil.isAVIP()) {
                dsp.setArg(1, this.m_deviceProgramParam);
                dsp.setArg(2, yc);
                dsp.setArg(3, this.m_jpeg_devicebuffer);
            }
            if (!dsp.execute()) {
                Log.e(TAG, "executeCreateJpegSATop(): dsp.execute() failed!!");
            }
            if (SaUtil.isAVIP()) {
                this.m_deviceBootParam.read(this.m_mbox_buf);
            } else {
                this.m_jpeg_devicebuffer.read(this.m_mbox_buf, 0, 16, JPEG_LOG_ADDR_OFFSET);
            }
            sizeEcs = SaJpegParam.getEncodeSize(this.m_mbox_buf);
            size = this.mJpegHeader.length + sizeEcs;
            if (sizeEcs < streamSizeLimit) {
                break;
            }
            retryCount++;
        }
        if (retryCount != 0) {
            Log.w(TAG, "JPEG Retry Count = " + retryCount);
        }
        this.jpegTc[3] = System.nanoTime();
        jpeg = this.m_jpeg_bytearray;
        this.m_jpeg_bytearray_size = size;
        System.arraycopy(this.mJpegHeader, 0, jpeg, 0, this.mJpegHeader.length);
        Log.i(TAG, "JPEG size = " + (this.mJpegHeader.length + sizeEcs));
        try {
            this.m_jpeg_devicebuffer.read(jpeg, this.mJpegHeader.length, sizeEcs, 0);
        } catch (Exception e2) {
            jpeg = null;
            Log.e(TAG, "Failed to get JPEG stream from DeviceBuffer to byte array.");
            Log.e(TAG, e2.toString());
            e2.printStackTrace();
        }
        this.jpegTc[4] = System.nanoTime();
        long te = System.nanoTime();
        Log.i(TAG, "Encode time=" + (te - ts) + "[ns]");
        return jpeg;
    }
}
