package com.sony.imaging.app.startrails.metadatamanager;

import android.util.Log;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/* loaded from: classes.dex */
public class StillSAExec {
    private static final String TAG = "StillSAExec";
    private static StillSAExec sInstance = null;
    private static int SA_WORK_SIZE = 0;
    private final int SA_PARAM_SIZE = 256;
    private final int SA_BOOT_PARAM_SIZE = 60;
    private DSP mDSP = null;
    private DeviceBuffer mBootParamBuf = null;
    private DeviceBuffer mSAParamBuf = null;
    private DeviceMemory mDeviceMemory = null;
    private boolean isInit = false;
    private final int[] STREAK_LEVEL_ARRAY = {12800, 14400, 15360, 16000, 16320, 16380, 16384};
    private int mStreakLevel = 0;

    private StillSAExec() {
        AppLog.enter(TAG, "STEESA instance created");
    }

    public static StillSAExec getInstance() {
        if (sInstance == null) {
            sInstance = new StillSAExec();
        }
        AppLog.info(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public void open() {
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
            this.mDSP.setProgram(STUtility.getInstance().getFilePathForStarTrailsSALib());
        }
        if (this.mBootParamBuf == null) {
            this.mBootParamBuf = this.mDSP.createBuffer(60);
        }
        if (this.mSAParamBuf == null) {
            this.mSAParamBuf = this.mDSP.createBuffer(256);
        }
        if (this.mDeviceMemory == null) {
            SA_WORK_SIZE = getYCImageSize() / 2;
            AppLog.info(TAG, " mDeviceMemory allocation SA_WORK_SIZE " + SA_WORK_SIZE);
            this.mDeviceMemory = this.mDSP.createBuffer(SA_WORK_SIZE);
            AppLog.info(TAG, " mDeviceMemory allocation " + this.mDeviceMemory);
        }
    }

    public void initialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isInit = true;
        this.mStreakLevel = ThemeParameterSettingUtility.getInstance().getStreakLevel() - 1;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public DSP getDSP() {
        AppLog.info(TAG, AppLog.getMethodName());
        return this.mDSP;
    }

    public void updateSAEffect(OptimizedImage inYC) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        buf.order(ByteOrder.nativeOrder());
        int in_addr = getAXIAddr(this.mDSP.getPropertyAsInt(inYC, "memory-address"));
        int offset = this.mDSP.getPropertyAsInt(inYC, "image-data-offset");
        int width = inYC.getWidth();
        int height = inYC.getHeight();
        int cwidth = this.mDSP.getPropertyAsInt(inYC, "image-canvas-width");
        int work_addr = getAXIAddr(this.mDSP.getPropertyAsInt(this.mDeviceMemory, "memory-address"));
        int in_addr2 = in_addr + offset;
        buf.putInt(in_addr2);
        buf.putShort((short) width);
        buf.putShort((short) cwidth);
        buf.putShort((short) height);
        buf.putShort((short) 0);
        buf.putInt(0);
        buf.putInt(in_addr2);
        buf.putShort((short) width);
        buf.putShort((short) cwidth);
        buf.putShort((short) height);
        buf.putShort((short) 0);
        buf.putInt(0);
        buf.putInt(work_addr);
        buf.putShort((short) width);
        buf.putShort((short) width);
        buf.putShort((short) height);
        buf.putShort((short) 0);
        buf.putInt(0);
        AppLog.enter(TAG, AppLog.getMethodName() + " width= " + width);
        AppLog.enter(TAG, AppLog.getMethodName() + " cwidth= " + cwidth);
        AppLog.enter(TAG, AppLog.getMethodName() + " height= " + height);
        if (this.isInit) {
            buf.putInt(1);
            this.isInit = false;
        } else {
            buf.putInt(0);
        }
        buf.putInt(1);
        buf.putInt(1);
        buf.putInt(this.STREAK_LEVEL_ARRAY[this.mStreakLevel]);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        this.mSAParamBuf.write(buf);
        buf.clear();
        this.mDSP.setArg(1, this.mSAParamBuf);
        this.mDSP.setArg(2, inYC);
        this.mDSP.setArg(3, this.mDeviceMemory);
        if (this.mDSP.execute()) {
            Log.i(TAG, "StillSAExec process success");
        } else {
            Log.i(TAG, "StillSAExec process Failed");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDeviceMemory != null) {
            this.mDeviceMemory.release();
            this.mDeviceMemory = null;
        }
        if (this.mBootParamBuf != null) {
            this.mBootParamBuf.release();
            this.mBootParamBuf = null;
        }
        if (this.mSAParamBuf != null) {
            this.mSAParamBuf.release();
            this.mSAParamBuf = null;
        }
        if (this.mDSP != null) {
            this.mDSP.release();
            this.mDSP = null;
        }
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
        buf.put((byte) 0);
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
        this.mBootParamBuf.write(buf);
        buf.clear();
        this.mDSP.setArg(0, this.mBootParamBuf);
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

    public void execute(OptimizedImage optImageInYC) {
        AppLog.enter(TAG, AppLog.getMethodName());
        setBootParam();
        updateSAEffect(optImageInYC);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getYCImageSize() {
        List<ScalarProperties.PictureSize> pictureSizeList = ScalarProperties.getSupportedPictureSizes();
        int maxSize = 0;
        ScalarProperties.PictureSize size = null;
        for (int i = 0; i < pictureSizeList.size(); i++) {
            ScalarProperties.PictureSize size2 = pictureSizeList.get(i);
            size = size2;
            if (maxSize < size.width * size.height) {
                maxSize = size.width * size.height;
            }
        }
        AppLog.info(TAG, "Max Picture Size size.hight : " + size.height);
        AppLog.info(TAG, "Max Picture Size size.width  : " + size.width);
        AppLog.info(TAG, "Max Picture Size : " + (maxSize * 2));
        return maxSize * 2;
    }
}
