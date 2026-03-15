package com.sony.imaging.app.avi;

import android.util.Log;
import com.sony.imaging.app.avi.AviExporter;
import com.sony.imaging.app.avi.sa.SaJpegParam;
import com.sony.imaging.app.avi.sa.SaUtil;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.util.FileHelper;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class AviParser {
    private static final int DCTNUM = 4;
    public static final int FRAMERATE_10 = 10;
    public static final int FRAMERATE_24 = 24;
    public static final int FRAMERATE_30 = 30;
    public static final int HEIGHT_1080 = 1080;
    public static final int HEIGHT_360 = 360;
    public static final int HEIGHT_720 = 720;
    private static final int MCUHEIGHT = 8;
    private static final int MCUWIDTH = 16;
    private static final String TAG = "AviParser";
    private static final String VERSION = "2014/11/14";
    public static final int WIDTH_1280 = 1280;
    public static final int WIDTH_1920 = 1920;
    public static final int WIDTH_640 = 640;
    private static final int mJpegHeaderOfstSofQtableC = 72;
    private static final int mJpegHeaderOfstSofQtableY = 7;
    private static final int mJpegHeaderSize = 589;
    private static final int mSaJpegParamOfstIMAGEWIDTH = 32;
    private static final int mSaJpegParamOfstInputSize = 56;
    private static final int mSaJpegParamOfstInputStartAddress = 52;
    private static final int mSaJpegParamOfstJHWIDTH = 24;
    private static final int mSaJpegParamOfstJM_SET = 8;
    private static final int mSaJpegParamOfstJVHEIGHT = 28;
    private static final int mSaJpegParamOfstOutputStartAddress = 68;
    private static final int mSaJpegParamOfstQtableC = 192;
    private static final int mSaJpegParamOfstQtableY = 128;
    private static byte[] m_jpeg_bytearray = null;
    private static final byte[] JPEG_HEADER_SOI_CHECK = {-1, -40};
    private Status mStatus = Status.CLOSED;
    private String m_salibPath = null;
    private byte[] mSaJpegDecParam = {0, 0, 0, 0, 0, 0, 0, 0, 32, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 0, 0, 0, 56, 4, 0, 0, AviExporter.COLORI_PARAM_ADOBE_TO_601.col_mode, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    DSP m_dsp = null;
    private int m_stream_size = 0;
    private RandomAccessFile m_raf_avi = null;
    private int m_width = 1920;
    private int m_height = 1080;
    private int m_fps = 24;
    private String m_avi_filename = null;
    private AviIndexParser m_aviIndexParser = null;
    private boolean enMusashiLSIBugWorkAround = true;

    /* loaded from: classes.dex */
    public static class Options {
        public String fullPathFileName;
        public String libpath;
        public int width = 1920;
        public int height = 1080;
        public int fps = 24;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Status {
        OPENED,
        CLOSED,
        ERROR
    }

    public AviParser() {
        Log.i(TAG, "AVI Lib Ver. 2014/11/14");
    }

    public synchronized boolean open(Options options) {
        boolean z = false;
        synchronized (this) {
            Log.i(TAG, "open()");
            if (this.mStatus != Status.CLOSED) {
                Log.e(TAG, "Failed to open!!");
            } else if (options != null) {
                this.m_fps = options.fps;
                this.m_stream_size = 0;
                this.m_width = options.width;
                this.m_height = options.height;
                this.m_avi_filename = options.fullPathFileName;
                this.m_salibPath = options.libpath;
                if (!FileHelper.exists(new File(this.m_avi_filename))) {
                    Log.e(TAG, "Failed to open!!");
                } else {
                    if (this.m_width <= 640) {
                        int maxFrameSize = AviExporter.getMaxFrameSize(this.m_width, this.m_height);
                        if (m_jpeg_bytearray == null || m_jpeg_bytearray.length < maxFrameSize) {
                            m_jpeg_bytearray = new byte[AviExporter.getMaxFrameSize(this.m_width, this.m_height) + 131072];
                        }
                    } else {
                        int maxFrameSize2 = AviExporter.getMaxFrameSize(1920, 1080);
                        if (m_jpeg_bytearray == null || m_jpeg_bytearray.length < maxFrameSize2) {
                            m_jpeg_bytearray = new byte[maxFrameSize2 + 131072];
                        }
                    }
                    try {
                        this.m_raf_avi = new RandomAccessFile(this.m_avi_filename, "r");
                        this.m_aviIndexParser = new AviIndexParser();
                        if (!this.m_aviIndexParser.setIndex(this.m_raf_avi)) {
                            Log.e(TAG, "Failed to open!!");
                            try {
                                close();
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to close!!");
                                e.printStackTrace();
                            }
                            this.mStatus = Status.ERROR;
                        } else {
                            this.m_dsp = DSP.createProcessor("sony-di-dsp");
                            if (!this.enMusashiLSIBugWorkAround) {
                                if (SaUtil.isAVIP()) {
                                    this.m_dsp.setProgram(this.m_salibPath + "libsajpgdec.so");
                                } else {
                                    this.m_dsp.setProgram(SaJpegParam.SA_PROGRAM_DEC);
                                }
                            }
                            int value = ((this.m_width * this.m_height) / mSaJpegParamOfstQtableY) * 4;
                            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, 8, value, false);
                            Log.i(TAG, "JM_SET = " + value);
                            int value2 = this.m_width / 16;
                            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, 24, value2, false);
                            Log.i(TAG, "JHWIDTH = " + value2);
                            int value3 = this.m_height;
                            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, 28, value3, false);
                            Log.i(TAG, "JVHEIGHT = " + value3);
                            this.mStatus = Status.OPENED;
                            z = true;
                        }
                    } catch (FileNotFoundException e2) {
                        Log.e(TAG, "Failed to open!!");
                        Log.e(TAG, e2.toString());
                        e2.printStackTrace();
                        try {
                            close();
                        } catch (Exception e1) {
                            Log.e(TAG, "Failed to close!!");
                            e1.printStackTrace();
                        }
                        this.mStatus = Status.ERROR;
                    }
                }
            }
        }
        return z;
    }

    public synchronized boolean close() throws Exception {
        Log.i(TAG, "close()");
        if (this.m_raf_avi != null) {
            this.m_raf_avi.close();
            this.m_raf_avi = null;
        }
        if (this.m_dsp != null) {
            this.m_dsp.release();
            this.m_dsp = null;
        }
        if (this.m_aviIndexParser != null) {
            this.m_aviIndexParser.release();
            this.m_aviIndexParser = null;
        }
        if (this.m_width <= 640) {
            m_jpeg_bytearray = null;
        }
        this.m_avi_filename = null;
        this.m_stream_size = 0;
        this.m_salibPath = null;
        this.mStatus = Status.CLOSED;
        return true;
    }

    private boolean setQtable(byte[] jpeg) {
        Log.i(TAG, "setQtable()");
        for (int n = 0; n < 64; n += 4) {
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableY + 3] = jpeg[n + 7 + 0];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableY + 2] = jpeg[n + 7 + 1];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableY + 1] = jpeg[n + 7 + 2];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableY + 0] = jpeg[n + 7 + 3];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableC + 3] = jpeg[n + mJpegHeaderOfstSofQtableC + 0];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableC + 2] = jpeg[n + mJpegHeaderOfstSofQtableC + 1];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableC + 1] = jpeg[n + mJpegHeaderOfstSofQtableC + 2];
            this.mSaJpegDecParam[n + mSaJpegParamOfstQtableC + 0] = jpeg[n + mJpegHeaderOfstSofQtableC + 3];
        }
        return true;
    }

    public synchronized boolean getFrame(int frameNum, DeviceBuffer jpeg_db) {
        boolean z = false;
        synchronized (this) {
            if (this.mStatus != Status.OPENED) {
                Log.e(TAG, "getFrame() failed!!");
            } else {
                Log.i(TAG, "getFrame(" + frameNum + LogHelper.MSG_CLOSE_BRACKET);
                Log.i(TAG, "getFrame Step1: File to Bytearray.");
                int totalSize = this.m_aviIndexParser.getFrame(frameNum, this.m_raf_avi, m_jpeg_bytearray);
                if (totalSize <= 0) {
                    this.mStatus = Status.ERROR;
                    this.m_stream_size = -1;
                    Log.e(TAG, "getFrame() failed!!");
                } else if (m_jpeg_bytearray[0] != JPEG_HEADER_SOI_CHECK[0] || m_jpeg_bytearray[1] != JPEG_HEADER_SOI_CHECK[1]) {
                    this.mStatus = Status.ERROR;
                    this.m_stream_size = -1;
                    Log.e(TAG, "getFrame() failed!!");
                } else {
                    int ecsSize = totalSize - 589;
                    this.m_stream_size = ecsSize;
                    setQtable(m_jpeg_bytearray);
                    try {
                        jpeg_db.write(m_jpeg_bytearray, 589, ecsSize, 0);
                        z = true;
                    } catch (Exception e) {
                        this.mStatus = Status.ERROR;
                        this.m_stream_size = -1;
                        Log.e(TAG, "getFrame() failed!!");
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
        return z;
    }

    public synchronized boolean decodeFrame(DeviceBuffer jpeg_db, OptimizedImage yc) {
        boolean z = false;
        synchronized (this) {
            if (this.mStatus != Status.OPENED) {
                Log.e(TAG, "decodeFrame() failed!!");
            } else {
                Log.i(TAG, "decodeFrame()");
                if (this.m_stream_size <= 0) {
                    this.mStatus = Status.ERROR;
                    Log.e(TAG, "decodeFrame(): Last getFrame might be failed!!");
                } else {
                    if (this.enMusashiLSIBugWorkAround) {
                        if (SaUtil.isAVIP()) {
                            this.m_dsp.setProgram(this.m_salibPath + "libsajpgdec.so");
                        } else {
                            this.m_dsp.setProgram(SaJpegParam.SA_PROGRAM_DEC);
                        }
                    }
                    executeBufSA(this.m_dsp, yc, jpeg_db);
                    if (this.enMusashiLSIBugWorkAround) {
                        this.m_dsp.clearProgram();
                        this.m_dsp.release();
                        this.m_dsp = null;
                        this.m_dsp = DSP.createProcessor("sony-di-dsp");
                    }
                    z = true;
                }
            }
        }
        return z;
    }

    private synchronized void executeBufSA(DSP dsp, OptimizedImage yc, DeviceBuffer jpeg_db) {
        try {
            int value = dsp.getPropertyAsInt(yc, "image-canvas-width");
            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, 32, value, false);
            int inAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(jpeg_db, "memory-address"));
            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, mSaJpegParamOfstInputStartAddress, inAddr, false);
            int value2 = this.m_stream_size;
            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, mSaJpegParamOfstInputSize, value2, false);
            int outAddr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(yc, "memory-address"));
            SaUtil.writeValueToByteArray(this.mSaJpegDecParam, mSaJpegParamOfstOutputStartAddress, outAddr, false);
            SaJpegParam.setMMUParam(this.mSaJpegDecParam, inAddr, outAddr);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        DeviceBuffer deviceProgramParam = dsp.createBuffer(SaJpegParam.PARAM_SIZE_ALLOC);
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
            bootParam.putInt(SaJpegParam.JPEG_CMD_DEC);
            int paramAxiAddr = SaUtil.convArmAddr2AxiAddr(this.m_dsp.getPropertyAsInt(deviceProgramParam, "memory-address"));
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
        ByteBuffer programParam = ByteBuffer.allocateDirect(SaJpegParam.PARAM_SIZE_ALLOC);
        programParam.put(this.mSaJpegDecParam, 0, this.mSaJpegDecParam.length);
        programParam.order(ByteOrder.nativeOrder());
        programParam.rewind();
        deviceProgramParam.write(programParam);
        DeviceBuffer deviceBootParam = dsp.createBuffer(bootParam.capacity());
        bootParam.rewind();
        deviceBootParam.write(bootParam);
        dsp.setArg(0, deviceBootParam);
        if (SaUtil.isAVIP()) {
            dsp.setArg(1, deviceProgramParam);
            dsp.setArg(2, jpeg_db);
            dsp.setArg(3, yc);
        }
        Log.i(TAG, "JPEG DECODE: start");
        if (!dsp.execute()) {
            Log.e(TAG, "JPEG DECODE: dsp.execute() failed!!!");
        }
        Log.i(TAG, "JPEG DECODE: done");
        deviceBootParam.release();
        deviceProgramParam.release();
        Log.i(TAG, "decodeFrame(): done");
    }

    public synchronized int getFrameNum() {
        int frameNum;
        if (this.mStatus != Status.OPENED) {
            Log.e(TAG, "getFrameNum() failed!!");
            frameNum = -1;
        } else if (this.m_aviIndexParser == null) {
            Log.e(TAG, "getFrameNum() failed!!");
            frameNum = -2;
        } else {
            frameNum = this.m_aviIndexParser.getFrameNum();
        }
        return frameNum;
    }
}
