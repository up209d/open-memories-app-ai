package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.AppContext;
import com.sony.imaging.app.lightgraffiti.shooting.CompositProcess;
import com.sony.imaging.app.lightgraffiti.shooting.LGImagingAdapterImpl;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class LGSAReviewFilter {
    public static final int DEFAULT_WEIGHT_AVERAGE_VALUE = 4;
    public static final int SFR_MODE_LIVEVIEW = 1;
    public static final int SFR_MODE_PLAYBACK = 0;
    public static final float WEIGHT_MEAN_FACTOR = 25.5f;
    private boolean releaseReview;
    private static final String TAG = LGSAReviewFilter.class.getSimpleName();
    private static LGSAReviewFilter sInstance = null;
    private final int BLEND_MODE_ADD = 0;
    private final int BLEND_MODE_MEAN = 1;
    private final int BLEND_MODE_LIGHTEN = 2;
    private final int BLEND_MODE_DARKEN = 3;
    private final int BLEND_MODE_SCREEN = 4;
    private final int BLEND_MODE_MULTIPLY = 5;
    private final int BLEND_MODE_WEIGHT_MEAN = 6;
    private int mSuperImposeImgAddr = 0;
    private final int BLENDING_EFFECT_WEIGHT = 6;
    private final int SA_PARAM_SIZE = 256;
    private final int SA_BOOT_PARAM_SIZE = 60;
    private final int WEIGHT_MEAN_FLAG = 1;
    private String mPackageName = null;
    private SAParam mSAParam = null;
    private DSP mDSP = null;
    private DeviceBuffer mBootParamBuf = null;
    private DeviceBuffer mSAParamBuf = null;
    private CameraSequence mCameraSequence = null;
    boolean isStartLiveViewEffect = false;
    private boolean transperencyOpen = false;

    private LGSAReviewFilter() {
        this.releaseReview = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.releaseReview = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static LGSAReviewFilter getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new LGSAReviewFilter();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public void intialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setPackageName(AppContext.getAppContext().getPackageName());
        intializeDSP();
        setSFRMode(1);
        this.transperencyOpen = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void intializeDSP() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setPackageName(AppContext.getAppContext().getPackageName());
        if (this.mSAParam == null) {
            this.mSAParam = new SAParam();
        }
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
            this.mDSP.setProgram(getFilePath());
            this.mBootParamBuf = this.mDSP.createBuffer(60);
            this.mSAParamBuf = this.mDSP.createBuffer(256);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public DSP getDSP() {
        return this.mDSP;
    }

    public DeviceBuffer getDeviceBuffer() {
        return this.mBootParamBuf;
    }

    private void setPackageName(String packageName) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mPackageName = packageName;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean setSFRMode(int sfrMode) {
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

    public void startLiveViewEffect(DeviceBuffer optImgInput) {
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        Log.d("DSPTEST", "LGSAReviewFilter Input <<<<<<<<<<<");
        Log.d("DSPTEST", "MEMORY_ADDRESS = " + dsp.getPropertyAsInt(optImgInput, "memory-address"));
        Log.d("DSPTEST", "MEMORY_SIZE = " + dsp.getPropertyAsInt(optImgInput, "memory-size"));
        Log.d("DSPTEST", ">>>>>>>>>>> LGSAReviewFilter Input");
        dsp.release();
        if (this.mCameraSequence == null && CompositProcess.getAdapter() != null) {
            this.mCameraSequence = ((LGImagingAdapterImpl) CompositProcess.getAdapter()).getSequence();
        }
        CameraSequence.Options opts = new CameraSequence.Options();
        opts.setOption("PREVIEW_FRAME_RATE", 30000);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (aspectRatio == null) {
            aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
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
        this.mSuperImposeImgAddr = this.mDSP.getPropertyAsInt(optImgInput, "memory-address");
        setBlendMode(6);
        updateLiveViewEffect();
        if (this.mCameraSequence != null) {
            if (this.mCameraSequence.getCameraEx() == null) {
                this.mCameraSequence = CameraSequence.open(ExecutorCreator.getInstance().getSequence().getCameraEx());
            }
            this.mCameraSequence.setPreviewPlugin(this.mDSP);
            this.mCameraSequence.startPreviewSequence(opts);
            this.isStartLiveViewEffect = true;
        }
    }

    public void setCameraSequence(CameraSequence sequence) {
        this.mCameraSequence = sequence;
    }

    public CameraSequence getCameraSequence() {
        return this.mCameraSequence;
    }

    public boolean isStartLiveViewEffect() {
        return this.isStartLiveViewEffect;
    }

    public void setStartLiveViewEffect(boolean isOnLiveViewEffect) {
        this.isStartLiveViewEffect = isOnLiveViewEffect;
    }

    public void updateLiveViewEffect() {
        ByteBuffer buf = ByteBuffer.allocateDirect(256);
        buf.order(ByteOrder.nativeOrder());
        int weightMeanValue = 127;
        if (6 == 6) {
            weightMeanValue = this.mSAParam.getWeightMeanValue();
        }
        buf.putInt(0);
        buf.putInt(getAXIAddr(this.mSuperImposeImgAddr));
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(6);
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
        buf.clear();
    }

    public void execute(OptimizedImage optImage0, OptimizedImage optImage1, OptimizedImage optImageOutput) {
        setBootParam();
        setSAParam(optImage0, optImage1, optImageOutput);
        if (this.mDSP.execute()) {
            Log.i(TAG, "SA Success");
        } else {
            Log.i(TAG, "SA Failed");
        }
    }

    public void terminate() {
        Log.d(TAG, "Power off ");
        if (this.mCameraSequence != null && this.isStartLiveViewEffect) {
            Log.i(TAG, "################### Terminate SMSA");
            try {
                this.mCameraSequence.stopPreviewSequence();
                this.mCameraSequence.setPreviewPlugin((DSP) null);
                this.mCameraSequence = null;
            } catch (RuntimeException e) {
                Log.e(TAG, "Method called after mCameraSequence.release()", e);
            }
            this.isStartLiveViewEffect = false;
        }
        releaseDspMemory();
    }

    public void releaseDspMemory() {
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
        if (this.mDSP != null) {
            this.mDSP.release();
            this.mDSP = null;
        }
        this.mPackageName = null;
    }

    public void terminateReview() {
        this.releaseReview = true;
        if (this.mCameraSequence != null && this.isStartLiveViewEffect) {
            Log.i(TAG, "################### Terminate SMSA");
            this.mCameraSequence.setPreviewPlugin((DSP) null);
            this.isStartLiveViewEffect = false;
        }
    }

    private void setBootParam() {
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
    }

    private void setSAParam(OptimizedImage optImage0, OptimizedImage optImage1, OptimizedImage optImage) {
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
        int outWidth = optImage.getWidth();
        int outHeight = optImage.getHeight();
        int outCwidth = this.mDSP.getPropertyAsInt(optImage, "image-canvas-width");
        int sfrMode = this.mSAParam.getSFRMode();
        int weightMeanValue = this.mSAParam.getWeightMeanValue();
        ByteBuffer buf = ByteBuffer.allocateDirect(256);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(getAXIAddr(in0Addr2));
        buf.putInt(getAXIAddr(in1Addr2));
        buf.putInt(getAXIAddr(0));
        buf.putInt(getAXIAddr(outAddr + offset3));
        buf.putInt(6);
        buf.putInt(1);
        buf.putInt(weightMeanValue);
        buf.putInt(outWidth);
        buf.putInt(outHeight);
        buf.putInt(in0Cwidth);
        buf.putInt(in1Cwidth);
        buf.putInt(outCwidth);
        buf.putInt(sfrMode);
        buf.putInt(0);
        this.mSAParamBuf.write(buf);
        this.mDSP.setArg(1, this.mSAParamBuf);
        buf.clear();
    }

    private String getFilePath() {
        if (1 == Environment.getVersionOfHW()) {
            String filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_mle.so";
            return filePath;
        }
        String filePath2 = "/android/data/lib/" + this.mPackageName + "/lib/libsa_mle_mushashi.so";
        return filePath2;
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
                Log.i(LGSAReviewFilter.TAG, "Incorrect SFR Value");
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

        private int getBlendMode() {
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

    public boolean isCameraReleased() {
        if (this.transperencyOpen) {
            return false;
        }
        if (this.mCameraSequence != null && this.mCameraSequence.getCameraEx() != null) {
            return false;
        }
        return true;
    }

    public void setTransperencyOpen(boolean transperencyOpen) {
        this.transperencyOpen = transperencyOpen;
    }

    public boolean isTransperencyOpen() {
        return this.transperencyOpen;
    }
}
