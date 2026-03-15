package com.sony.imaging.app.avi.sa;

import android.util.Log;
import com.sony.imaging.app.avi.AviExporter;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class SaJpegEncoder {
    private static final int JPEGDBSIZE = 2097152;
    private static final int JPEG_LOG_ADDR_OFFSET = 2095104;
    private static final int JPEG_OUTPUT_SIZE_LIMIT = 2093056;
    private static final int MCUWIDTH = 16;
    private static final int SAMBXBUFSIZE = 256;
    private static final String TAG = "SaJpegEncoder";
    private static final int mJpegHeaderOfstSofHeight = 561;
    private static final int mJpegHeaderOfstSofWidth = 563;
    private static final int mSaJpegParamOfstIMAGEWIDTH = 32;
    private static final int mSaJpegParamOfstInputStartAddress = 52;
    private static final int mSaJpegParamOfstJHWIDTH = 24;
    private static final int mSaJpegParamOfstJVHEIGHT = 28;
    private static final int mSaJpegParamOfstLogOutputAddress = 92;
    private static final int mSaJpegParamOfstOutputStartAddress = 68;
    private static final int mSaJpegParamSize = 1024;
    private String m_salibPath = null;
    private byte[] mSaJpegParam = {0, 0, 0, 0, 0, 0, 0, 0, 32, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 0, 0, 0, 56, 4, 0, 0, AviExporter.COLORI_PARAM_ADOBE_TO_601.col_mode, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, -16, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 2, 16, 9, 7, 4, 3, 3, 2, 2, 9, 16, 16, 4, 4, 3, 2, 3, 9, 18, 9, 7, 5, 3, 3, 3, 16, 20, 21, 9, 9, 6, 3, 3, 19, 24, 25, 18, 17, 9, 6, 4, 21, 32, 24, 20, 21, 20, 17, 9, 23, 33, 33, 24, 23, 22, 21, 18, 23, 24, 23, 25, 8, 4, 3, 2, 23, 23, 23, 23, 17, 4, 3, 3, 23, 23, 23, 23, 23, 9, 4, 4, 23, 23, 23, 23, 23, 23, 17, 8, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 1, 4, 16, 0, 1, 4, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] mJpegHeader = {-1, -40, -1, -37, 0, -124, 0, 2, 2, 2, 3, 4, 7, 9, 16, 2, 2, 3, 3, 4, 16, 16, 9, 3, 2, 3, 4, 7, 9, 18, 9, 3, 3, 3, 5, 9, 21, 20, 16, 3, 3, 6, 9, 18, 25, 24, 19, 4, 6, 9, 17, 20, 24, 32, 21, 9, 17, 20, 21, 24, 33, 33, 23, 18, 21, 22, 23, 25, 23, 24, 23, 1, 2, 3, 4, 8, 23, 23, 23, 23, 3, 3, 4, 17, 23, 23, 23, 23, 4, 4, 9, 23, 23, 23, 23, 23, 8, 17, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, -1, -60, 1, -94, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -64, 0, 17, 8, 4, 56, 7, AviExporter.COLORI_PARAM_ADOBE_TO_601.col_mode, 3, 1, 33, 0, 2, 17, 1, 3, 17, 1, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0};
    DeviceBuffer mDeviceProgramParam = null;

    /* loaded from: classes.dex */
    public static class Options {
        public String libpath;
    }

    public boolean open(Options options) {
        this.m_salibPath = options.libpath;
        if (this.m_salibPath != null) {
            return true;
        }
        Log.e(toString(), "options.libpath is null!!");
        return false;
    }

    public void close() {
        this.m_salibPath = null;
    }

    public int encode(OptimizedImage yc, byte[] jpeg) {
        int size = 0;
        DSP dsp = null;
        try {
            try {
                dsp = DSP.createProcessor("sony-di-dsp");
                size = executeBufSA(dsp, yc, jpeg);
                if (dsp != null) {
                    dsp.release();
                    dsp = null;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                if (dsp != null) {
                    dsp.release();
                    dsp = null;
                }
            }
            return size;
        } catch (Throwable th) {
            if (dsp != null) {
                dsp.release();
            }
            throw th;
        }
    }

    private int executeBufSA(DSP dsp, OptimizedImage yc, byte[] jpeg_ba) {
        int size = 0;
        DeviceBuffer jpeg_db = null;
        try {
            try {
                if (SaUtil.isAVIP()) {
                    dsp.setProgram(this.m_salibPath + "libsajpg.so");
                } else {
                    dsp.setProgram(SaJpegParam.SA_PROGRAM_ENC);
                }
                jpeg_db = dsp.createBuffer(JPEGDBSIZE);
                int width = 0;
                int height = 0;
                try {
                    int inAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(yc, "memory-address"));
                    SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstInputStartAddress, inAddr, false);
                    Log.i(TAG, "MEMORY_ADDRESS(yc) = " + Integer.toHexString(inAddr));
                    int outAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(jpeg_db, "memory-address"));
                    SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstOutputStartAddress, outAddr, false);
                    Log.i(TAG, "MEMORY_ADDRESS(m_jpeg_devicebuffer) = " + Integer.toHexString(outAddr));
                    if (!SaUtil.isAVIP()) {
                        int logOutAddr = outAddr + JPEG_LOG_ADDR_OFFSET;
                        SaUtil.writeValueToByteArray(this.mSaJpegParam, mSaJpegParamOfstLogOutputAddress, logOutAddr, false);
                    }
                    SaJpegParam.setMMUParam(this.mSaJpegParam, inAddr, outAddr);
                    width = dsp.getPropertyAsInt(yc, "image-target-width");
                    int value = width / 16;
                    SaUtil.writeValueToByteArray(this.mSaJpegParam, 24, value, false);
                    Log.i(TAG, "JHWIDTH = " + value);
                    height = dsp.getPropertyAsInt(yc, "image-target-height");
                    SaUtil.writeValueToByteArray(this.mSaJpegParam, 28, height, false);
                    Log.i(TAG, "JVHEIGHT = " + height);
                    int cwidth = dsp.getPropertyAsInt(yc, "image-canvas-width");
                    SaUtil.writeValueToByteArray(this.mSaJpegParam, 32, cwidth, false);
                    Log.i(TAG, "IMAGE_CANVAS_WIDTH = " + cwidth);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                this.mDeviceProgramParam = dsp.createBuffer(SaJpegParam.PARAM_SIZE_ALLOC);
                ByteBuffer bootParam = ByteBuffer.allocateDirect(60);
                bootParam.order(ByteOrder.nativeOrder());
                bootParam.putInt(dsp.getPropertyAsInt("program-descriptor"));
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                if (SaUtil.isAVIP()) {
                    bootParam.put((byte) 0);
                    bootParam.put((byte) 0);
                    bootParam.put((byte) 0);
                    bootParam.put((byte) 0);
                    bootParam.putInt(0);
                    bootParam.putInt(0);
                    bootParam.putInt(0);
                    bootParam.putInt(0);
                } else {
                    bootParam.put((byte) 1);
                    bootParam.put((byte) 1);
                    bootParam.put((byte) 0);
                    bootParam.put((byte) 0);
                    bootParam.putInt(SaJpegParam.JPEG_CMD_ENC);
                    int paramAxiAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(this.mDeviceProgramParam, "memory-address"));
                    bootParam.putInt(paramAxiAddr);
                    bootParam.putInt(0);
                    bootParam.putInt(0);
                }
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.putInt(0);
                bootParam.putInt(0);
                bootParam.putInt(0);
                bootParam.putInt(0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.put((byte) 0);
                bootParam.putInt(0);
                bootParam.putInt(0);
                setSizeToJpegHeader((short) width, (short) height);
                ByteBuffer programParam = ByteBuffer.allocateDirect(mSaJpegParamSize);
                programParam.put(this.mSaJpegParam, 0, this.mSaJpegParam.length);
                programParam.order(ByteOrder.nativeOrder());
                Log.i(TAG, "executeSA");
                size = executeSA(dsp, bootParam, programParam, yc, jpeg_db, jpeg_ba);
                jpeg_db.release();
            } catch (Exception e2) {
                Log.e(TAG, e2.toString());
                e2.printStackTrace();
                if (jpeg_db != null) {
                    jpeg_db.release();
                }
                if (this.mDeviceProgramParam != null) {
                    this.mDeviceProgramParam.release();
                    this.mDeviceProgramParam = null;
                }
            }
            return size;
        } finally {
            if (jpeg_db != null) {
                jpeg_db.release();
            }
            if (this.mDeviceProgramParam != null) {
                this.mDeviceProgramParam.release();
                this.mDeviceProgramParam = null;
            }
        }
    }

    private int executeSA(DSP dsp, ByteBuffer bootParam, ByteBuffer programParam, OptimizedImage yc, DeviceBuffer jpeg_db, byte[] jpeg_ba) {
        int size;
        if (dsp == null || bootParam == null || programParam == null || yc == null || jpeg_db == null || jpeg_ba == null) {
            Log.e(TAG, "executeSA() failed!!");
            Log.e(TAG, "There is null argument!!");
            return -1;
        }
        DeviceBuffer deviceBootParam = null;
        try {
            try {
                programParam.rewind();
                this.mDeviceProgramParam.write(programParam);
                deviceBootParam = dsp.createBuffer(bootParam.capacity());
                bootParam.rewind();
                deviceBootParam.write(bootParam);
                dsp.setArg(0, deviceBootParam);
                if (SaUtil.isAVIP()) {
                    dsp.setArg(1, this.mDeviceProgramParam);
                    dsp.setArg(2, yc);
                    dsp.setArg(3, jpeg_db);
                }
                Log.i(TAG, "dsp.execute()");
                if (!dsp.execute()) {
                    Log.e(TAG, "dsp.execute() = false");
                }
                byte[] mboxbuf = new byte[SAMBXBUFSIZE];
                if (SaUtil.isAVIP()) {
                    deviceBootParam.read(mboxbuf);
                } else {
                    jpeg_db.read(mboxbuf, 0, 16, JPEG_LOG_ADDR_OFFSET);
                }
                int sizeEcs = SaJpegParam.getEncodeSize(mboxbuf);
                Log.d("JPEG", "JPEG THM size=" + sizeEcs);
                size = this.mJpegHeader.length + sizeEcs;
                if (size <= jpeg_ba.length) {
                    System.arraycopy(this.mJpegHeader, 0, jpeg_ba, 0, this.mJpegHeader.length);
                    jpeg_db.read(jpeg_ba, this.mJpegHeader.length, sizeEcs, 0);
                } else {
                    size = -1;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                size = -1;
                if (deviceBootParam != null) {
                    deviceBootParam.release();
                }
            }
            return size;
        } finally {
            if (deviceBootParam != null) {
                deviceBootParam.release();
            }
        }
    }

    void setSizeToJpegHeader(short width, short height) {
        SaUtil.writeValueToByteArray(this.mJpegHeader, mJpegHeaderOfstSofHeight, height, true);
        SaUtil.writeValueToByteArray(this.mJpegHeader, mJpegHeaderOfstSofWidth, width, true);
    }
}
