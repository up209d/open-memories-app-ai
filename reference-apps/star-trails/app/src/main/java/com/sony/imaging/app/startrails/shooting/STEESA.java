package com.sony.imaging.app.startrails.shooting;

import com.sony.imaging.app.avi.sa.SaJpegParam;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/* loaded from: classes.dex */
public class STEESA {
    public static final int SFR_MODE_LIVEVIEW = 1;
    public static final int SFR_MODE_PLAYBACK = 0;
    private static final String TAG = "STEESA";
    private static STEESA sInstance = null;
    private static int SA_WORK_SIZE = 0;
    private final int SA_PARAM_SIZE = 256;
    private final int SA_BOOT_PARAM_SIZE = 60;
    private String mPackageName = null;
    private DSP mDSP = null;
    private DeviceBuffer mBootParamBuf = null;
    private DeviceBuffer mSAParamBuf = null;
    private CameraSequence mCameraSequence = null;
    private DeviceMemory mDeviceMemory = null;
    private boolean mFirstSetting = false;
    private int mGain = SaJpegParam.PARAM_SIZE_ALLOC;
    private int mSumNum = 32;
    private int mPrevSumNum = 32;

    private STEESA() {
        AppLog.enter(TAG, "STEESA instance created");
    }

    public static STEESA getInstance() {
        if (sInstance == null) {
            sInstance = new STEESA();
        }
        AppLog.info(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public void intialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
        }
        this.mDSP.setProgram(getFilePath());
        this.mBootParamBuf = this.mDSP.createBuffer(60);
        this.mSAParamBuf = this.mDSP.createBuffer(256);
        this.mPrevSumNum = this.mSumNum;
        this.mFirstSetting = true;
        SA_WORK_SIZE = getYCImageSize();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public DSP getDSP() {
        AppLog.info(TAG, AppLog.getMethodName());
        return this.mDSP;
    }

    public void setPackageName(String packageName) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mPackageName = packageName;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setGainValue(int gain) {
        AppLog.enter(TAG, AppLog.getMethodName() + " Gain = " + this.mGain);
        this.mGain = gain;
        AppLog.exit(TAG, AppLog.getMethodName() + " Gain = " + this.mGain);
    }

    public int getGainValue() {
        AppLog.info(TAG, AppLog.getMethodName() + " Gain = " + this.mGain);
        return this.mGain;
    }

    public void setSumValue(int num) {
        AppLog.enter(TAG, AppLog.getMethodName() + " Num = " + this.mSumNum);
        this.mSumNum = num;
        AppLog.exit(TAG, AppLog.getMethodName() + " Num = " + num);
    }

    public int getSumValue() {
        AppLog.info(TAG, AppLog.getMethodName() + " num = " + this.mSumNum);
        return this.mSumNum;
    }

    public void startLiveViewEffect() {
        AppLog.info(TAG, AppLog.getMethodName());
        if (this.mCameraSequence == null) {
            this.mCameraSequence = CameraSequence.open(ExecutorCreator.getInstance().getSequence().getCameraEx());
        }
        CameraSequence.Options opts = new CameraSequence.Options();
        opts.setOption("PREVIEW_FRAME_RATE", 30000);
        opts.setOption("PREVIEW_FRAME_WIDTH", 1024);
        opts.setOption("PREVIEW_FRAME_HEIGHT", 0);
        opts.setOption("PREVIEW_PLUGIN_NOTIFY_MASK", 0);
        opts.setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", false);
        opts.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", true);
        setBootParam();
        this.mDeviceMemory = this.mDSP.createBuffer(SA_WORK_SIZE);
        updateLiveViewEffect();
        this.mCameraSequence.setPreviewPlugin(this.mDSP);
        this.mCameraSequence.startPreviewSequence(opts);
        try {
            AppLog.info(TAG, "time-1" + System.currentTimeMillis());
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        AppLog.info(TAG, "time-2" + System.currentTimeMillis());
        updateSaparamread();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int saParamSize = 60;
        if (!this.mFirstSetting && this.mPrevSumNum == this.mSumNum) {
            saParamSize = 48;
        }
        if (this.mDeviceMemory != null) {
            ByteBuffer buf = ByteBuffer.allocateDirect(saParamSize);
            buf.order(ByteOrder.nativeOrder());
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            int work_addr = getAXIAddr(this.mDSP.getPropertyAsInt(this.mDeviceMemory, "memory-address"));
            buf.putInt(getAXIAddr(this.mDSP.getPropertyAsInt(this.mDeviceMemory, "memory-address")));
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            AppLog.info(TAG, "work_addr " + work_addr);
            AppLog.info(TAG, "gain " + this.mGain);
            AppLog.info(TAG, "SumNum " + this.mSumNum);
            buf.putInt(this.mGain);
            buf.putInt(0);
            buf.putInt(this.mSumNum);
            buf.putInt(0);
            if (this.mFirstSetting || this.mPrevSumNum != this.mSumNum) {
                this.mFirstSetting = false;
                buf.putInt(0);
                buf.putInt(0);
                buf.putInt(1);
            }
            this.mPrevSumNum = this.mSumNum;
            this.mSAParamBuf.write(buf);
            this.mDSP.setArg(1, this.mSAParamBuf);
            this.mDSP.setArg(2, this.mDeviceMemory);
            buf.clear();
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    public void updateSaparamread() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDeviceMemory != null) {
            ByteBuffer buf = ByteBuffer.allocateDirect(60);
            buf.order(ByteOrder.nativeOrder());
            int bytesread_sa = this.mSAParamBuf.read(buf);
            AppLog.info(TAG, "****start******* before bytesread_sa= " + bytesread_sa);
            AppLog.info(TAG, "****start******* ");
            AppLog.info(TAG, "input YUV: " + Integer.toHexString(buf.getInt(0)));
            AppLog.info(TAG, "output YUV: " + Integer.toHexString(buf.getInt(4)));
            AppLog.info(TAG, "us_size_x: " + ((int) buf.getShort(8)));
            AppLog.info(TAG, "us_size_y: " + ((int) buf.getShort(10)));
            AppLog.info(TAG, "us_size_x_offset: " + ((int) buf.getShort(12)));
            AppLog.info(TAG, "puc_work: " + Integer.toHexString(buf.getInt(16)));
            AppLog.info(TAG, "gain: " + buf.getInt(32));
            AppLog.info(TAG, "th: " + buf.getInt(36));
            AppLog.info(TAG, "sumnum_max: " + buf.getInt(40));
            AppLog.info(TAG, "sumnum: " + buf.getInt(48));
            AppLog.info(TAG, "latestpos: " + buf.getInt(52));
            AppLog.info(TAG, "sfr_mode: " + buf.getInt(56));
            AppLog.info(TAG, "*****end****** ");
            buf.clear();
            AppLog.exit(TAG, AppLog.getMethodName());
        }
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
        this.mPackageName = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBootParam() {
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(this.mDSP.getPropertyAsInt("program-descriptor"));
        buf.put((byte) 0);
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

    private String getFilePath() {
        String filePath;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mPackageName == null) {
            this.mPackageName = AppContext.getAppContext().getPackageName();
        }
        if (1 == Environment.getVersionOfHW()) {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_startrail_eesum_avip.so";
        } else {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_startrail_eesum_mushashi.so";
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private int getAXIAddr(int addr) {
        int axiAddr;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (1 == Environment.getVersionOfHW()) {
            axiAddr = addr & Integer.MAX_VALUE;
        } else {
            axiAddr = addr & 1073741823;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return axiAddr;
    }

    private int getYCImageSize() {
        List<ScalarProperties.PictureSize> pictureSizeList = ScalarProperties.getSupportedPictureSizes();
        int maxSize = 0;
        for (int i = 0; i < pictureSizeList.size(); i++) {
            ScalarProperties.PictureSize size = pictureSizeList.get(i);
            if (maxSize < size.width * size.height) {
                maxSize = size.width * size.height;
            }
        }
        AppLog.info(TAG, "Max Picture Size : " + (maxSize * 2));
        return maxSize * 2;
    }
}
