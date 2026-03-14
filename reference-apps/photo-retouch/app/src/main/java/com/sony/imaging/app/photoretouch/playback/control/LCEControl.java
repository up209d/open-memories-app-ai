package com.sony.imaging.app.photoretouch.playback.control;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class LCEControl {
    public static final byte DEFAULT_BRIGHTNESS_VALUE = 6;
    public static final byte DEFAULT_CONTRAST_VALUE = 3;
    public static final byte DEFAULT_SATURATION_VALUE = 3;
    public static final byte MAX_BRIGHTNESS_LEVEL = 13;
    public static final byte MAX_CONTRAST_LEVEL = 7;
    public static final byte MAX_SATURATION_LEVEL = 7;
    public static final float THREE_FOUR = 0.75f;
    private final String TAG;
    private DeviceBuffer bootParam;
    private ByteBuffer bootParam_buf;
    private YuvToRgbConversion conv;
    private DSP dsp_lce;
    private DeviceBuffer lceTable;
    private ByteBuffer lceTable_buf;
    private DeviceBuffer lce_param;
    private ByteBuffer lce_param_buf;
    private Activity mActivity;
    private int mAspectRatio;
    private int mBrightness;
    private int mContrast;
    private int mHeight;
    private int mOrientation;
    private int mSaturation;
    private OptimizedImage mScaledImage;
    private int mWidth;

    private native void MakeLceTable(int[] iArr, int[] iArr2, int[] iArr3);

    public synchronized int getBrightness() {
        return this.mBrightness;
    }

    public synchronized void setBrightness(int mBrightness) {
        this.mBrightness = mBrightness;
    }

    public synchronized int getSaturation() {
        return this.mSaturation;
    }

    public synchronized void setSaturation(int mSaturation) {
        this.mSaturation = mSaturation;
    }

    public synchronized int getContrast() {
        return this.mContrast;
    }

    public synchronized void setContrast(int mContrast) {
        this.mContrast = mContrast;
    }

    public synchronized int getAspectRatio() {
        return this.mAspectRatio;
    }

    public synchronized void setAspectRatio(int mAspectRatio) {
        this.mAspectRatio = mAspectRatio;
        if (mAspectRatio == 1) {
            this.mWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            this.mHeight = 360;
        } else if (mAspectRatio == 0) {
            this.mWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            this.mHeight = 428;
        } else if (mAspectRatio == 2) {
            this.mWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            this.mHeight = 480;
        } else {
            this.mWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            this.mHeight = AppRoot.USER_KEYCODE.WATER_HOUSING;
        }
    }

    public synchronized int getOrientation() {
        return this.mOrientation;
    }

    public synchronized void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public synchronized OptimizedImage getScaledImage() {
        return this.mScaledImage;
    }

    public synchronized void setScaledImage(OptimizedImage orgImage) {
        ScaleImageFilter filter = new ScaleImageFilter();
        filter.setSource(orgImage, false);
        filter.setDestSize(this.mWidth, this.mHeight);
        boolean result = filter.execute();
        if (result) {
            this.mScaledImage = filter.getOutput();
            ImageEditor.optImageCount++;
        }
        filter.clearSources();
        filter.release();
    }

    private synchronized OptimizedImage rotateImageWRTOrientation(OptimizedImage optImg) {
        OptimizedImage out;
        RotateImageFilter.ROTATION_DEGREE degree;
        out = null;
        RotateImageFilter.ROTATION_DEGREE rotation_degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        if (6 == this.mOrientation) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_90;
        } else if (8 == this.mOrientation) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
        } else if (3 == this.mOrientation) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_180;
        } else {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        }
        RotateImageFilter rotate = new RotateImageFilter();
        rotate.setTrimMode(3);
        rotate.setRotation(degree);
        rotate.setSource(optImg, false);
        boolean isRotateExecuted = rotate.execute();
        if (isRotateExecuted) {
            Log.d("LCE Control", "===rotate image filter executed");
            out = rotate.getOutput();
        } else {
            Log.e("LCE Control", "===rotate image filter is not executed");
        }
        rotate.clearSources();
        rotate.release();
        return out;
    }

    public LCEControl(DSP dsp) {
        this.TAG = LCEControl.class.getSimpleName();
        this.mBrightness = 6;
        this.mSaturation = 3;
        this.mContrast = 3;
        this.mScaledImage = null;
        this.conv = null;
        this.lceTable_buf = null;
        this.lce_param_buf = null;
        this.bootParam_buf = null;
        this.dsp_lce = dsp;
        this.lceTable = this.dsp_lce.createBuffer(1024);
        this.lce_param = this.dsp_lce.createBuffer(32);
        this.lceTable_buf = ByteBuffer.allocateDirect(1024);
        this.lceTable_buf.order(ByteOrder.nativeOrder());
        this.lce_param_buf = ByteBuffer.allocateDirect(32);
        this.lce_param_buf.order(ByteOrder.nativeOrder());
    }

    public synchronized void releaseLCEResources() {
        Log.d(this.TAG, "====releaseLCEResources +");
        if (this.conv != null) {
            this.conv.releaseYuvToRgbResources();
        }
        if (this.lceTable != null) {
            this.lceTable.release();
            this.lceTable = null;
        }
        if (this.lce_param != null) {
            this.lce_param.release();
            this.lce_param = null;
        }
        if (this.bootParam != null) {
            this.bootParam.release();
            this.bootParam = null;
        }
        Log.d(this.TAG, "====releaseLCEResources -");
    }

    public synchronized void createYuvToRgbConversionInstance(DSP yuvDsp) {
        this.conv = new YuvToRgbConversion(yuvDsp);
        Log.d("YES", "====YuvToRgbConversion= " + this.conv + "\t lce= " + this.dsp_lce);
    }

    public LCEControl(DSP dsp, DSP yuvDsp) {
        this(dsp);
        this.mBrightness = 6;
        this.mSaturation = 3;
        this.mContrast = 3;
    }

    public synchronized OptimizedImage createYUVImageUsingDSP(OptimizedImage optImage) {
        OptimizedImage dspExecutedImage;
        dspExecutedImage = null;
        if (optImage != null) {
            dspExecutedImage = (OptimizedImage) this.dsp_lce.createImage(optImage.getWidth(), optImage.getHeight());
            createYUVImageUsingDSP(optImage, dspExecutedImage);
        }
        return dspExecutedImage;
    }

    private synchronized void prepare_sa_bootparam() {
        if (this.bootParam != null) {
            this.bootParam.write(this.bootParam_buf);
        } else {
            this.bootParam = this.dsp_lce.createBuffer(60);
            this.bootParam_buf = ByteBuffer.allocateDirect(60);
            this.bootParam_buf.order(ByteOrder.nativeOrder());
            Log.d("YES", "====befor key  lce= " + this.dsp_lce);
            this.bootParam_buf.putInt(this.dsp_lce.getPropertyAsInt("program-descriptor"));
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam.write(this.bootParam_buf);
        }
    }

    static {
        System.loadLibrary("lceTable_Jni");
    }

    public synchronized Bitmap getRGBImage(OptimizedImage optImage) {
        Bitmap bmp;
        OptimizedImage yuvImage = createYUVImageUsingDSP(optImage);
        OptimizedImage optImg = null;
        OptimizedImage optRotatedImg = rotateImageWRTOrientation(yuvImage);
        if (!optRotatedImg.equals(yuvImage)) {
            yuvImage.release();
        }
        int originalWidth = optRotatedImg.getWidth();
        int scaledWidth = (int) (originalWidth * 0.75d);
        int scaledHeight = optRotatedImg.getHeight();
        ScaleImageFilter filter = new ScaleImageFilter();
        filter.setSource(optRotatedImg, true);
        filter.setDestSize(scaledWidth & (-4), scaledHeight & (-2));
        boolean isWidthReduced = filter.execute();
        if (isWidthReduced) {
            optImg = filter.getOutput();
        } else {
            Log.d("ERROR", "scaleFilter3by4 not executed");
        }
        optRotatedImg.release();
        filter.clearSources();
        filter.release();
        bmp = null;
        if (this.conv != null) {
            bmp = this.conv.yuv2rgb_main(optImg);
        }
        if (optImg != null) {
            optImg.release();
        }
        return bmp;
    }

    public synchronized void createYUVImageUsingDSP(OptimizedImage optImage, OptimizedImage optImageOut) {
        if (optImage != null) {
            int[] param2 = new int[4];
            int[] toneTable = new int[1024];
            prepare_sa_bootparam();
            int[] param1 = {this.mBrightness, this.mContrast, this.mSaturation};
            MakeLceTable(param1, param2, toneTable);
            this.lceTable_buf.rewind();
            for (int i = 0; i < 1024; i++) {
                this.lceTable_buf.put((byte) toneTable[i]);
            }
            this.lceTable.write(this.lceTable_buf);
            int in_adr = this.dsp_lce.getPropertyAsInt(optImage, "memory-address");
            int out_adr = this.dsp_lce.getPropertyAsInt(optImageOut, "memory-address");
            int tbl_adr = this.dsp_lce.getPropertyAsInt(this.lceTable, "memory-address");
            int cwidth = this.dsp_lce.getPropertyAsInt(optImage, "image-canvas-width");
            int execImgWidth = this.dsp_lce.getPropertyAsInt(optImageOut, "image-canvas-width");
            int offset = this.dsp_lce.getPropertyAsInt(optImage, "image-data-offset");
            this.lce_param_buf.rewind();
            this.lce_param_buf.putInt(getAXIAddr(in_adr + offset));
            this.lce_param_buf.putInt(getAXIAddr(out_adr));
            this.lce_param_buf.putInt(getAXIAddr(tbl_adr));
            this.lce_param_buf.putShort((short) 128);
            this.lce_param_buf.putShort((short) 128);
            this.lce_param_buf.putShort((short) param2[0]);
            this.lce_param_buf.putShort((short) 0);
            this.lce_param_buf.putShort((short) 9);
            this.lce_param_buf.putShort((short) 0);
            this.lce_param_buf.putShort((short) optImage.getWidth());
            this.lce_param_buf.putShort((short) optImage.getHeight());
            this.lce_param_buf.putShort((short) cwidth);
            this.lce_param_buf.putShort((short) execImgWidth);
            this.lce_param.write(this.lce_param_buf);
            this.dsp_lce.setArg(0, this.bootParam);
            this.dsp_lce.setArg(1, this.lce_param);
            this.dsp_lce.setArg(2, optImage);
            this.dsp_lce.setArg(3, optImageOut);
            this.dsp_lce.setArg(4, this.lceTable);
            if (this.dsp_lce.execute()) {
                Log.i("sa", "success");
            } else {
                Log.i("sa", "Failed");
            }
        }
    }

    private int getAXIAddr(int addr) {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        if (pfMajorVersion == 1) {
            int axiAddr = addr & Integer.MAX_VALUE;
            return axiAddr;
        }
        int axiAddr2 = addr & 1073741823;
        return axiAddr2;
    }
}
