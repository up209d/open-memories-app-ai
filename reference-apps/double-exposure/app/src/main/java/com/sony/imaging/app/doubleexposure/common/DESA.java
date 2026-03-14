package com.sony.imaging.app.doubleexposure.common;

import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class DESA {
    public static final int SFR_MODE_LIVEVIEW = 1;
    public static final int SFR_MODE_PLAYBACK = 0;
    private static final String TAG = AppLog.getClassName();
    private static DESA sInstance = null;
    private final int BLEND_MODE_ADD = 0;
    private final int BLEND_MODE_MEAN = 1;
    private final int BLEND_MODE_LIGHTEN = 2;
    private final int BLEND_MODE_DARKEN = 3;
    private final int BLEND_MODE_SCREEN = 4;
    private final int BLEND_MODE_MULTIPLY = 5;
    private final int BLEND_MODE_WEIGHT_MEAN = 6;
    private final int SA_PARAM_SIZE = 256;
    private final int SA_BOOT_PARAM_SIZE = 60;
    private final int WEIGHT_MEAN_FLAG = 1;
    private String mPackageName = null;
    private SAParam mSAParam = null;
    private DSP mDSP = null;
    private DeviceBuffer mBootParamBuf = null;
    private DeviceBuffer mSAParamBuf = null;
    private CameraSequence mCameraSequence = null;
    private DeviceMemory mDeviceMemory = null;

    private DESA() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static DESA getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DESA();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public void intialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSAParam == null) {
            this.mSAParam = new SAParam();
        }
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
        }
        this.mDSP.setProgram(getFilePath());
        this.mBootParamBuf = this.mDSP.createBuffer(60);
        this.mSAParamBuf = this.mDSP.createBuffer(256);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public DSP getDSP() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mDSP;
    }

    public void setPackageName(String packageName) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mPackageName = packageName;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean setSFRMode(int sfrMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSAParam.setSFRMode(sfrMode);
    }

    public void setBlendMode(int mode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSAParam != null) {
            this.mSAParam.setBlendMode(mode);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setWeightMeanValue(int weightMeanValue) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSAParam.setWeightMeanValue(weightMeanValue);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void startLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCameraSequence == null) {
            AppLog.info(TAG, "open Camera Sequence");
            this.mCameraSequence = CameraSequence.open(ExecutorCreator.getInstance().getSequence().getCameraEx());
        }
        CameraSequence.Options opts = new CameraSequence.Options();
        opts.setOption("PREVIEW_FRAME_RATE", 30000);
        String aspectRatio = DoubleExposureUtil.getInstance().getFirstImageAspectRatio();
        if (aspectRatio == null) {
            aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        } else {
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, aspectRatio);
            DoubleExposureUtil.getInstance().setFirstImageAspectRatio(null);
        }
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                opts.setOption("PREVIEW_FRAME_WIDTH", AppRoot.USER_KEYCODE.WATER_HOUSING);
            } else {
                opts.setOption("PREVIEW_FRAME_WIDTH", 1024);
            }
        }
        opts.setOption("PREVIEW_FRAME_HEIGHT", 0);
        opts.setOption("PREVIEW_PLUGIN_NOTIFY_MASK", 0);
        opts.setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", false);
        opts.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", true);
        setBootParam();
        this.mDeviceMemory = DoubleExposureImageUtil.getInstance().memoryCopyApplicationToDiadem(this.mDSP);
        if (this.mDeviceMemory != null) {
            setBlendMode(DoubleExposureUtil.getInstance().getBlendingMode());
            updateLiveViewEffect();
            this.mCameraSequence.setPreviewPlugin(this.mDSP);
            this.mCameraSequence.startPreviewSequence(opts);
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    public void updateLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDeviceMemory != null) {
            ByteBuffer buf = ByteBuffer.allocateDirect(256);
            buf.order(ByteOrder.nativeOrder());
            int weightMeanValue = 127;
            int blendMode = this.mSAParam.getBlendMode();
            AppLog.info(TAG, "UI Blend Mode: " + blendMode);
            int blendMode2 = getSABlendMode(blendMode);
            AppLog.info(TAG, "SA Blend Mode: " + blendMode2);
            if (6 == blendMode2) {
                weightMeanValue = this.mSAParam.getWeightMeanValue();
            }
            AppLog.info(TAG, "Weight Mean Value: " + weightMeanValue);
            buf.putInt(0);
            buf.putInt(getAXIAddr(this.mDSP.getPropertyAsInt(this.mDeviceMemory, "memory-address")));
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(blendMode2);
            buf.putInt(1);
            buf.putInt(weightMeanValue);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(1);
            buf.putInt(0);
            this.mSAParamBuf.write(buf);
            this.mDSP.setArg(1, this.mSAParamBuf);
            this.mDSP.setArg(2, this.mDeviceMemory);
            buf.clear();
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    public void execute(OptimizedImage optImage0, OptimizedImage optImage1, OptimizedImage optImageOutput) {
        AppLog.enter(TAG, AppLog.getMethodName());
        setBootParam();
        setSAParam(optImage0, optImage1, optImageOutput);
        if (this.mDSP.execute()) {
            AppLog.info(TAG, "SA Success");
        } else {
            AppLog.info(TAG, "SA Failed");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCameraSequence != null) {
            this.mCameraSequence.stopPreviewSequence();
            this.mCameraSequence.setPreviewPlugin((DSP) null);
            this.mCameraSequence.release();
            this.mCameraSequence = null;
        }
        if (this.mDeviceMemory != null) {
            this.mDeviceMemory.release();
            this.mDeviceMemory = null;
        }
        if (this.mDSP != null) {
            this.mDSP.release();
            this.mDSP = null;
        }
        if (this.mBootParamBuf != null) {
            this.mBootParamBuf.release();
            this.mBootParamBuf = null;
        }
        if (this.mSAParamBuf != null) {
            this.mSAParamBuf.release();
            this.mSAParamBuf = null;
        }
        if (this.mSAParam != null) {
            this.mSAParam.close();
            this.mSAParam = null;
        }
        this.mPackageName = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBootParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(this.mDSP.getPropertyAsInt("program-descriptor"));
        buf.put((byte) this.mSAParam.getSFRMode());
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 1);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.position(0);
        this.mBootParamBuf.write(buf);
        this.mDSP.setArg(0, this.mBootParamBuf);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setSAParam(OptimizedImage optImage0, OptimizedImage optImage1, OptimizedImage optImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int in0Addr = this.mDSP.getPropertyAsInt(optImage0, "memory-address");
        int offset = this.mDSP.getPropertyAsInt(optImage0, "image-data-offset");
        int in0Addr2 = in0Addr + offset;
        int in0Cwidth = this.mDSP.getPropertyAsInt(optImage0, "image-canvas-width");
        int in1Addr = this.mDSP.getPropertyAsInt(optImage1, "memory-address");
        int offset2 = this.mDSP.getPropertyAsInt(optImage1, "image-data-offset");
        int in1Addr2 = in1Addr + offset2;
        int in1Cwidth = this.mDSP.getPropertyAsInt(optImage1, "image-canvas-width");
        int outAddr = this.mDSP.getPropertyAsInt(optImage, "memory-address");
        int offset3 = this.mDSP.getPropertyAsInt(optImage, "image-data-offset");
        int outAddr2 = outAddr + offset3;
        int outWidth = optImage.getWidth();
        int outHeight = optImage.getHeight();
        int outCwidth = this.mDSP.getPropertyAsInt(optImage, "image-canvas-width");
        AppLog.info(TAG, "first image ht : " + optImage0.getHeight() + " wt :" + optImage0.getWidth() + " Canvas wt : " + in0Cwidth);
        AppLog.info(TAG, "Second image ht : " + optImage1.getHeight() + " wt :" + optImage1.getWidth() + " Canvas wt : " + in1Cwidth);
        AppLog.info(TAG, "Out image ht : " + optImage.getHeight() + " wt :" + optImage.getWidth() + " Canvas wt : " + outCwidth);
        int sfrMode = this.mSAParam.getSFRMode();
        int weightMeanValue = 127;
        int blendMode = this.mSAParam.getBlendMode();
        AppLog.info(TAG, "UI Blend Mode: " + blendMode);
        int blendMode2 = getSABlendMode(blendMode);
        AppLog.info(TAG, "SA Blend Mode: " + blendMode2);
        if (6 == blendMode2) {
            weightMeanValue = this.mSAParam.getWeightMeanValue();
        }
        AppLog.info(TAG, "Weight Mean Value: " + weightMeanValue);
        ByteBuffer buf = ByteBuffer.allocateDirect(256);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(getAXIAddr(in0Addr2));
        buf.putInt(getAXIAddr(in1Addr2));
        buf.putInt(getAXIAddr(0));
        buf.putInt(getAXIAddr(outAddr2));
        buf.putInt(blendMode2);
        buf.putInt(1);
        buf.putInt(weightMeanValue);
        buf.putInt(outWidth);
        buf.putInt(outHeight);
        buf.putInt(in0Cwidth);
        buf.putInt(in1Cwidth);
        buf.putInt(outCwidth);
        buf.putInt(sfrMode);
        buf.putInt(0);
        AppLog.info(TAG, "SAPARAM Input0 address: " + Integer.toHexString(getAXIAddr(in0Addr2)));
        AppLog.info(TAG, "SAPARAM Input1 address: " + Integer.toHexString(getAXIAddr(in1Addr2)));
        AppLog.info(TAG, "SAPARAM Map address: " + Integer.toHexString(getAXIAddr(0)));
        AppLog.info(TAG, "SAPARAM Output address: " + Integer.toHexString(getAXIAddr(outAddr2)));
        AppLog.info(TAG, "SAPARAM Blend Mode: " + blendMode2);
        AppLog.info(TAG, "SAPARAM Weight Mean Flag: 1");
        AppLog.info(TAG, "SAPARAM Weight Mean value: " + weightMeanValue);
        AppLog.info(TAG, "SAPARAM Width: " + outWidth);
        AppLog.info(TAG, "SAPARAM Height: " + outHeight);
        AppLog.info(TAG, "SAPARAM Input0 offset: " + in0Cwidth);
        AppLog.info(TAG, "SAPARAM Input1 offset: " + in1Cwidth);
        AppLog.info(TAG, "SAPARAM Output offset : " + outCwidth);
        AppLog.info(TAG, "SAPARAM Sfr mode: " + sfrMode);
        AppLog.info(TAG, "SAPARAM Reserved: 0");
        this.mSAParamBuf.write(buf);
        this.mDSP.setArg(1, this.mSAParamBuf);
        buf.clear();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getFilePath() {
        String filePath;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (1 == Environment.getVersionOfHW()) {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_mle.so";
        } else {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_mle_mushashi.so";
        }
        AppLog.info(TAG, "file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private int getAXIAddr(int addr) {
        if (1 == Environment.getVersionOfHW()) {
            int axiAddr = addr & Integer.MAX_VALUE;
            return axiAddr;
        }
        int axiAddr2 = addr & 1073741823;
        return axiAddr2;
    }

    private int getSABlendMode(int blendMode) {
        int[] blendModes = {0, 6, 4, 5, 2, 3};
        return blendModes[blendMode];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SAParam {
        private int mBlendMode;
        private int mSfrMode;
        private int mWeightMeanValue;

        private SAParam() {
            this.mSfrMode = 0;
            this.mBlendMode = 0;
            this.mWeightMeanValue = 127;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setSFRMode(int sfrMode) {
            if (2 <= sfrMode) {
                AppLog.info(DESA.TAG, "Incorrect SFR Value");
                return false;
            }
            this.mSfrMode = sfrMode;
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getSFRMode() {
            return this.mSfrMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setBlendMode(int blendMode) {
            this.mBlendMode = blendMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBlendMode() {
            return this.mBlendMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWeightMeanValue(int weightMeanValue) {
            this.mWeightMeanValue = weightMeanValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getWeightMeanValue() {
            return this.mWeightMeanValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void close() {
            this.mSfrMode = 0;
            this.mBlendMode = 0;
            this.mWeightMeanValue = 127;
        }
    }
}
