package com.sony.imaging.app.avi.thum;

import android.util.Log;
import com.sony.imaging.app.avi.sa.SaJpegEncoder;
import com.sony.imaging.app.avi.sa.SaUtil;
import com.sony.imaging.app.util.FileHelper;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class ThumExporter {
    private static final String EXT_AVI = ".AVI|.avi";
    private static final String EXT_THUMBNAIL = ".THM";
    public static final int HEIGHT_1080 = 1080;
    public static final int HEIGHT_2160 = 2160;
    public static final int HEIGHT_360 = 360;
    public static final int HEIGHT_720 = 720;
    private static final int RC_HEIGHT = 92;
    private static final int RC_WIDTH = 160;
    private static final String TAG = "ThumExporter";
    private static final int THM_MAX_SIZE = 131072;
    private static final int THUMBNAIL_HEIGHT = 90;
    private static final int THUMBNAIL_PADDING_HEIGHT = 15;
    private static final int THUMBNAIL_WIDTH = 160;
    public static final int WIDTH_1280 = 1280;
    public static final int WIDTH_1920 = 1920;
    public static final int WIDTH_3840 = 3840;
    public static final int WIDTH_640 = 640;
    private String mSaLibPath;
    private int m_width = 3840;
    private int m_height = 2160;
    private String m_avi_filename = null;

    /* loaded from: classes.dex */
    public static class Options {
        public String aviFilename;
        public String libpath;
        public int width = 3840;
        public int height = 2160;
    }

    public boolean initParam(Options options) {
        if (options == null) {
            return false;
        }
        this.m_width = options.width;
        this.m_height = options.height;
        this.m_avi_filename = options.aviFilename;
        this.mSaLibPath = options.libpath;
        return true;
    }

    private static ByteBuffer getBootParam(DSP dsp, int src_addr, int dst_addr, int param1, int param2) {
        ByteBuffer bootParam = ByteBuffer.allocateDirect(60);
        bootParam.order(ByteOrder.nativeOrder());
        bootParam.putInt(dsp.getPropertyAsInt("program-descriptor"));
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.put((byte) 1);
        bootParam.put((byte) 1);
        bootParam.put((byte) 1);
        bootParam.put((byte) 1);
        bootParam.putInt(src_addr);
        bootParam.putInt(dst_addr);
        bootParam.putInt(param1);
        bootParam.putInt(param2);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        return bootParam;
    }

    private void executeSA_padding_CG(DSP dsp, DeviceBuffer yc, OptimizedImage dst, int width, int height, int frame_num) {
        try {
            if (SaUtil.isAVIP()) {
                dsp.setProgram(this.mSaLibPath + "libpadding.so");
            } else {
                dsp.setProgram(this.mSaLibPath + "libpadding_mss.so");
            }
            int src_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(yc, "memory-address")) + (width * height * 2 * frame_num);
            int dst_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(dst, "memory-address"));
            int param1 = (width << 16) + height;
            int param2 = (((this.m_width / 160) * RC_HEIGHT) - this.m_height) << 16;
            ByteBuffer boot_param = getBootParam(dsp, src_addr, dst_addr, param1, param2);
            Log.i(TAG, "executeSA");
            SaUtil.executeSA(dsp, boot_param, (DeviceMemory) yc, (DeviceMemory) dst);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void executeSA_padding_TL(DSP dsp, OptimizedImage yc, OptimizedImage dst) {
        try {
            if (SaUtil.isAVIP()) {
                dsp.setProgram(this.mSaLibPath + "libpadding.so");
            } else {
                dsp.setProgram(this.mSaLibPath + "libpadding_mss.so");
            }
            int src_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(yc, "memory-address"));
            int dst_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(dst, "memory-address"));
            int param1 = (this.m_width << 16) + this.m_height;
            int param2 = (((this.m_width / 160) * RC_HEIGHT) - this.m_height) << 16;
            ByteBuffer boot_param = getBootParam(dsp, src_addr, dst_addr, param1, param2);
            Log.i(TAG, "executeSA");
            SaUtil.executeSA(dsp, boot_param, (DeviceMemory) yc, (DeviceMemory) dst);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void executeSA_thumbnail_padding(DSP dsp, OptimizedImage scaledYc, OptimizedImage thmYc) {
        try {
            if (SaUtil.isAVIP()) {
                dsp.setProgram(this.mSaLibPath + "libthumbnail_padding.so");
            } else {
                dsp.setProgram(this.mSaLibPath + "libthumbnail_padding_mss.so");
            }
            int src_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(scaledYc, "memory-address"));
            int dst_addr = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(thmYc, "memory-address"));
            int param2 = (dsp.getPropertyAsInt(thmYc, "image-canvas-width") << 16) + 15;
            ByteBuffer boot_param = getBootParam(dsp, src_addr, dst_addr, 10485850, param2);
            Log.i(TAG, "executeSA");
            SaUtil.executeSA(dsp, boot_param, (DeviceMemory) scaledYc, (DeviceMemory) thmYc);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void createJpegThm(OptimizedImage thmYc) {
        String thmFileName = Pattern.compile(EXT_AVI).matcher(this.m_avi_filename).replaceAll(EXT_THUMBNAIL);
        File thmFile = new File(thmFileName);
        if (FileHelper.exists(thmFile)) {
            thmFile.setWritable(true);
        }
        byte[] jpeg = new byte[131072];
        SaJpegEncoder enc = new SaJpegEncoder();
        SaJpegEncoder.Options opt = new SaJpegEncoder.Options();
        opt.libpath = this.mSaLibPath;
        enc.open(opt);
        int size = enc.encode(thmYc, jpeg);
        enc.close();
        thmYc.release();
        OutputStream os = null;
        try {
            try {
                OutputStream os2 = new FileOutputStream(Pattern.compile(EXT_AVI).matcher(this.m_avi_filename).replaceAll(EXT_THUMBNAIL));
                try {
                    os2.write(jpeg, 0, size);
                    os2.close();
                    if (os2 != null) {
                        try {
                            os2.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Thumbnail I/O exception!!");
                            e.printStackTrace();
                        }
                        os = null;
                    } else {
                        os = os2;
                    }
                } catch (FileNotFoundException e2) {
                    e = e2;
                    os = os2;
                    Log.e(TAG, "Thumbnail file not found!!");
                    e.printStackTrace();
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e3) {
                            Log.e(TAG, "Thumbnail I/O exception!!");
                            e3.printStackTrace();
                        }
                        os = null;
                    }
                } catch (IOException e4) {
                    e = e4;
                    os = os2;
                    Log.e(TAG, "Thumbnail I/O exception!!");
                    e.printStackTrace();
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e5) {
                            Log.e(TAG, "Thumbnail I/O exception!!");
                            e5.printStackTrace();
                        }
                        os = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    os = os2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e6) {
                            Log.e(TAG, "Thumbnail I/O exception!!");
                            e6.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e7) {
                e = e7;
            } catch (IOException e8) {
                e = e8;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public boolean storeThumbnail(DeviceBuffer yc, int width, int height, int frame_num) {
        ScaleImageFilter rcfilter;
        if (this.m_width != 640) {
            Log.e(TAG, "At storeThumbnail(), m_width is out of range.");
            return false;
        }
        DSP dsp = null;
        OptimizedImage dst = null;
        ScaleImageFilter rcfilter2 = null;
        OptimizedImage thmYc = null;
        OptimizedImage scaledYc = null;
        try {
            try {
                DSP dsp2 = DSP.createProcessor("sony-di-dsp");
                int preRcHeight = (this.m_width / 160) * RC_HEIGHT;
                dst = (OptimizedImage) dsp2.createImage(this.m_width, preRcHeight);
                executeSA_padding_CG(dsp2, yc, dst, width, height, frame_num);
                dsp2.release();
                dsp = null;
                rcfilter = new ScaleImageFilter();
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            try {
                rcfilter.setSource(dst, true);
                rcfilter.setDestSize(160, RC_HEIGHT);
                DSP dsp3 = DSP.createProcessor("sony-di-dsp");
                thmYc = (OptimizedImage) dsp3.createImage(160, 120);
                dsp3.release();
                dsp = null;
                if (rcfilter.execute()) {
                    dst = null;
                    OptimizedImage scaledYc2 = rcfilter.getOutput();
                    DSP dsp4 = DSP.createProcessor("sony-di-dsp");
                    executeSA_thumbnail_padding(dsp4, scaledYc2, thmYc);
                    scaledYc2.release();
                    scaledYc = null;
                    dsp4.release();
                    dsp = null;
                } else {
                    try {
                        dst.release();
                    } catch (Exception e2) {
                        Log.e(TAG, e2.toString());
                        e2.printStackTrace();
                    }
                    dst = null;
                    Log.e(TAG, "OptimizedImage memory is exhausted");
                }
                rcfilter.release();
                rcfilter2 = null;
                createJpegThm(thmYc);
                OptimizedImage thmYc2 = null;
                if (dsp != null) {
                    dsp.release();
                }
                if (dst != null) {
                    dst.release();
                }
                if (0 != 0) {
                    rcfilter2.release();
                }
                if (0 != 0) {
                    thmYc2.release();
                }
                if (scaledYc != null) {
                    scaledYc.release();
                }
                return true;
            } catch (Throwable th2) {
                th = th2;
                rcfilter2 = rcfilter;
                if (dsp != null) {
                    dsp.release();
                }
                if (dst != null) {
                    dst.release();
                }
                if (rcfilter2 != null) {
                    rcfilter2.release();
                }
                if (thmYc != null) {
                    thmYc.release();
                }
                if (scaledYc != null) {
                    scaledYc.release();
                }
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            rcfilter2 = rcfilter;
            Log.e(TAG, e.toString());
            e.printStackTrace();
            if (dsp != null) {
                dsp.release();
            }
            if (dst != null) {
                dst.release();
            }
            if (rcfilter2 != null) {
                rcfilter2.release();
            }
            if (thmYc != null) {
                thmYc.release();
            }
            if (scaledYc == null) {
                return false;
            }
            scaledYc.release();
            return false;
        }
    }

    public boolean storeThumbnail(OptimizedImage yc, boolean releasedResource) {
        ScaleImageFilter rcfilter;
        if (this.m_width != 3840 && this.m_width != 1920 && this.m_width != 1280) {
            Log.e(TAG, "At storeThumbnail(), m_width is out of range.");
            if (releasedResource && yc != null) {
                yc.release();
            }
            return false;
        }
        DSP dsp = null;
        OptimizedImage dst = null;
        ScaleImageFilter rcfilter2 = null;
        OptimizedImage thmYc = null;
        OptimizedImage scaledYc = null;
        try {
            try {
                DSP dsp2 = DSP.createProcessor("sony-di-dsp");
                int preRcHeight = (this.m_width / 160) * RC_HEIGHT;
                dst = (OptimizedImage) dsp2.createImage(this.m_width, preRcHeight);
                executeSA_padding_TL(dsp2, yc, dst);
                dsp2.release();
                dsp = null;
                if (releasedResource) {
                    yc.release();
                    yc = null;
                }
                rcfilter = new ScaleImageFilter();
            } catch (Throwable th) {
                th = th;
            }
            try {
                try {
                    rcfilter.setSource(dst, true);
                    rcfilter.setDestSize(160, RC_HEIGHT);
                    DSP dsp3 = DSP.createProcessor("sony-di-dsp");
                    thmYc = (OptimizedImage) dsp3.createImage(160, 120);
                    dsp3.release();
                    dsp = null;
                    if (rcfilter.execute()) {
                        dst = null;
                        OptimizedImage scaledYc2 = rcfilter.getOutput();
                        DSP dsp4 = DSP.createProcessor("sony-di-dsp");
                        executeSA_thumbnail_padding(dsp4, scaledYc2, thmYc);
                        scaledYc2.release();
                        scaledYc = null;
                        dsp4.release();
                        dsp = null;
                    } else {
                        try {
                            dst.release();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            e.printStackTrace();
                        }
                        dst = null;
                        Log.e(TAG, "OptimizedImage memory is exhausted");
                    }
                    rcfilter.release();
                    rcfilter2 = null;
                    createJpegThm(thmYc);
                    OptimizedImage thmYc2 = null;
                    if (dsp != null) {
                        dsp.release();
                    }
                    if (dst != null) {
                        dst.release();
                    }
                    if (0 != 0) {
                        rcfilter2.release();
                    }
                    if (0 != 0) {
                        thmYc2.release();
                    }
                    if (scaledYc != null) {
                        scaledYc.release();
                    }
                    if (releasedResource && yc != null) {
                        yc.release();
                    }
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    rcfilter2 = rcfilter;
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                    if (dsp != null) {
                        dsp.release();
                    }
                    if (dst != null) {
                        dst.release();
                    }
                    if (rcfilter2 != null) {
                        rcfilter2.release();
                    }
                    if (thmYc != null) {
                        thmYc.release();
                    }
                    if (scaledYc != null) {
                        scaledYc.release();
                    }
                    if (releasedResource && yc != null) {
                        yc.release();
                    }
                    return false;
                }
            } catch (Throwable th2) {
                th = th2;
                rcfilter2 = rcfilter;
                if (dsp != null) {
                    dsp.release();
                }
                if (dst != null) {
                    dst.release();
                }
                if (rcfilter2 != null) {
                    rcfilter2.release();
                }
                if (thmYc != null) {
                    thmYc.release();
                }
                if (scaledYc != null) {
                    scaledYc.release();
                }
                if (releasedResource && yc != null) {
                    yc.release();
                }
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    public boolean storeThumbnailLowMem(OptimizedImage yc) {
        ScaleImageFilter rcfilter;
        if (this.m_width != 3840 && this.m_width != 1920 && this.m_width != 1280) {
            Log.e(TAG, "At storeThumbnail(), m_width is out of range.");
            if (1 != 0 && yc != null) {
                yc.release();
            }
            return false;
        }
        DSP dsp = null;
        OptimizedImage dst = null;
        ScaleImageFilter rcfilter2 = null;
        OptimizedImage thmYc = null;
        OptimizedImage scaledYc = null;
        try {
            try {
                DSP dsp2 = DSP.createProcessor("sony-di-dsp");
                int preRcHeight = (this.m_width / 160) * RC_HEIGHT;
                dst = (OptimizedImage) dsp2.createImage(this.m_width, preRcHeight);
                executeSA_padding_TL(dsp2, yc, dst);
                dsp2.release();
                dsp = null;
                if (1 != 0) {
                    yc.release();
                    yc = null;
                }
                rcfilter = new ScaleImageFilter();
            } catch (Exception e) {
                e = e;
            }
            try {
                try {
                    rcfilter.setSource(dst, true);
                    rcfilter.setDestSize(160, RC_HEIGHT);
                    if (rcfilter.execute()) {
                        dst = null;
                        scaledYc = rcfilter.getOutput();
                    } else {
                        try {
                            dst.release();
                        } catch (Exception e2) {
                            Log.e(TAG, e2.toString());
                            e2.printStackTrace();
                        }
                        dst = null;
                        Log.e(TAG, "OptimizedImage memory is exhausted");
                    }
                    rcfilter.release();
                    rcfilter2 = null;
                    DSP dsp3 = DSP.createProcessor("sony-di-dsp");
                    thmYc = (OptimizedImage) dsp3.createImage(160, 120);
                    dsp3.release();
                    DSP dsp4 = null;
                    executeSA_thumbnail_padding(null, scaledYc, thmYc);
                    scaledYc.release();
                    scaledYc = null;
                    dsp4.release();
                    dsp = null;
                    createJpegThm(thmYc);
                    OptimizedImage thmYc2 = null;
                    if (0 != 0) {
                        dsp.release();
                    }
                    if (dst != null) {
                        dst.release();
                    }
                    if (0 != 0) {
                        rcfilter2.release();
                    }
                    if (0 != 0) {
                        thmYc2.release();
                    }
                    if (0 != 0) {
                        scaledYc.release();
                    }
                    if (1 != 0 && yc != null) {
                        yc.release();
                    }
                    return true;
                } catch (Throwable th) {
                    th = th;
                    rcfilter2 = rcfilter;
                    if (dsp != null) {
                        dsp.release();
                    }
                    if (dst != null) {
                        dst.release();
                    }
                    if (rcfilter2 != null) {
                        rcfilter2.release();
                    }
                    if (0 != 0) {
                        thmYc.release();
                    }
                    if (scaledYc != null) {
                        scaledYc.release();
                    }
                    if (1 != 0 && yc != null) {
                        yc.release();
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                rcfilter2 = rcfilter;
                Log.e(TAG, e.toString());
                e.printStackTrace();
                if (dsp != null) {
                    dsp.release();
                }
                if (dst != null) {
                    dst.release();
                }
                if (rcfilter2 != null) {
                    rcfilter2.release();
                }
                if (thmYc != null) {
                    thmYc.release();
                }
                if (scaledYc != null) {
                    scaledYc.release();
                }
                if (1 != 0 && yc != null) {
                    yc.release();
                }
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static void displayInfo(DSP dsp, OptimizedImage src, OptimizedImage dst) {
        try {
            int value = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(src, "memory-address"));
            Log.i(TAG, "MEMORY_ADDRESS(yc) = " + Integer.toHexString(value));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        try {
            int width = dsp.getPropertyAsInt(src, "image-canvas-width");
            int height = dsp.getPropertyAsInt(src, "image-canvas-height");
            Log.i(TAG, "cwidth    = " + width);
            Log.i(TAG, "cheight   = " + height);
        } catch (Exception e2) {
            Log.e(TAG, e2.toString());
        }
        try {
            int width2 = dsp.getPropertyAsInt(src, "image-target-width");
            int height2 = dsp.getPropertyAsInt(src, "image-target-height");
            Log.i(TAG, "twidth    = " + width2);
            Log.i(TAG, "theight   = " + height2);
        } catch (Exception e3) {
            Log.e(TAG, e3.toString());
        }
        try {
            int dofs = dsp.getPropertyAsInt(src, "image-data-offset");
            int xofs = dsp.getPropertyAsInt(src, "image-target-x-offset");
            int yofs = dsp.getPropertyAsInt(src, "image-target-y-offset");
            Log.i(TAG, "dofs      = " + dofs);
            Log.i(TAG, "xofs      = " + xofs);
            Log.i(TAG, "yofs      = " + yofs);
        } catch (Exception e4) {
            Log.e(TAG, e4.toString());
        }
        try {
            int value2 = SaUtil.convArmAddr2AxiAddr(dsp.getPropertyAsInt(dst, "memory-address"));
            Log.i(TAG, "MEMORY_ADDRESS(yc) = " + Integer.toHexString(value2));
        } catch (Exception e5) {
            Log.e(TAG, e5.toString());
        }
        try {
            int width3 = dsp.getPropertyAsInt(dst, "image-canvas-width");
            int height3 = dsp.getPropertyAsInt(dst, "image-canvas-height");
            Log.i(TAG, "cwidth    = " + width3);
            Log.i(TAG, "cheight   = " + height3);
        } catch (Exception e6) {
            Log.e(TAG, e6.toString());
        }
        try {
            int width4 = dsp.getPropertyAsInt(dst, "image-target-width");
            int height4 = dsp.getPropertyAsInt(dst, "image-target-height");
            Log.i(TAG, "twidth    = " + width4);
            Log.i(TAG, "theight   = " + height4);
        } catch (Exception e7) {
            Log.e(TAG, e7.toString());
        }
        try {
            int dofs2 = dsp.getPropertyAsInt(dst, "image-data-offset");
            int xofs2 = dsp.getPropertyAsInt(dst, "image-target-x-offset");
            int yofs2 = dsp.getPropertyAsInt(dst, "image-target-y-offset");
            Log.i(TAG, "dofs      = " + dofs2);
            Log.i(TAG, "xofs      = " + xofs2);
            Log.i(TAG, "yofs      = " + yofs2);
        } catch (Exception e8) {
            Log.e(TAG, e8.toString());
        }
    }
}
