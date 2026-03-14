package com.sony.imaging.app.doubleexposure.common;

import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class DESAPre {
    public static final int REVERSE_HORIZONTAL = 0;
    public static final int REVERSE_VERTICAL = 1;
    public static final int ROTATION_180 = 2;
    private static final String TAG = AppLog.getClassName();
    private static DESAPre sInstance = null;
    private final int SA_PARAM_SIZE = 256;
    private final int SA_BOOT_PARAM_SIZE = 60;
    private String mPackageName = null;
    private DSP mDSP = null;
    private DeviceBuffer mSAParamBuf = null;
    private DeviceBuffer mBootParamBuf = null;
    private SAParam mSAParam = null;

    private DESAPre() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static DESAPre getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DESAPre();
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

    public void setRotationMode(int mode) {
        this.mSAParam.setRotationMode(mode);
    }

    public int getRotationMode() {
        return this.mSAParam.getRotationMode();
    }

    public boolean execute(OptimizedImage optImageIn, OptimizedImage optImageOut) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = false;
        setBootParam();
        setSAParam(optImageIn, optImageOut);
        if (this.mDSP.execute()) {
            bRetVal = true;
            AppLog.info(TAG, "SA Success");
        } else {
            AppLog.info(TAG, "SA Failed");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
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

    private String getFilePath() {
        String filePath;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (1 == Environment.getVersionOfHW()) {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_pre_mle.so";
        } else {
            filePath = "/android/data/lib/" + this.mPackageName + "/lib/libsa_pre_mle_MUSASHI.so";
        }
        AppLog.info(TAG, "file path:" + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private void setBootParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
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
        this.mDSP.setArg(0, this.mBootParamBuf);
        buf.clear();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setSAParam(OptimizedImage optImageIn, OptimizedImage optImageOut) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int inAddr = this.mDSP.getPropertyAsInt(optImageIn, "memory-address");
        int offset = this.mDSP.getPropertyAsInt(optImageIn, "image-data-offset");
        int inAddr2 = inAddr + offset;
        int inCwidth = this.mDSP.getPropertyAsInt(optImageIn, "image-canvas-width");
        int outAddr = this.mDSP.getPropertyAsInt(optImageOut, "memory-address");
        int offset2 = this.mDSP.getPropertyAsInt(optImageOut, "image-data-offset");
        int outAddr2 = outAddr + offset2;
        int outWidth = optImageOut.getWidth();
        int outHeight = optImageOut.getHeight();
        int outCwidth = this.mDSP.getPropertyAsInt(optImageOut, "image-canvas-width");
        ByteBuffer buf = ByteBuffer.allocateDirect(256);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(getAXIAddr(inAddr2));
        buf.putInt(getAXIAddr(outAddr2));
        buf.putInt(this.mSAParam.getRotationMode());
        buf.putInt(outWidth);
        buf.putInt(outHeight);
        buf.putInt(inCwidth);
        buf.putInt(outCwidth);
        buf.putInt(0);
        AppLog.info(TAG, "SAPARM Input0 address: " + Integer.toHexString(getAXIAddr(inAddr2)));
        AppLog.info(TAG, "SAPARM Output address: " + Integer.toHexString(getAXIAddr(outAddr2)));
        AppLog.info(TAG, "SAPARM Mode: " + this.mSAParam.getRotationMode());
        AppLog.info(TAG, "SAPARM Width: " + outWidth);
        AppLog.info(TAG, "SAPARM Height: " + outHeight);
        AppLog.info(TAG, "SAPARM Input0 offset: " + inCwidth);
        AppLog.info(TAG, "SAPARM Output offset : " + outCwidth);
        AppLog.info(TAG, "SAPARM Reserved: 0");
        this.mSAParamBuf.write(buf);
        this.mDSP.setArg(1, this.mSAParamBuf);
        buf.clear();
        AppLog.exit(TAG, AppLog.getMethodName());
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SAParam {
        private int mMode;

        private SAParam() {
            this.mMode = 0;
        }

        public void setRotationMode(int mode) {
            this.mMode = mode;
        }

        public int getRotationMode() {
            return this.mMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void close() {
            this.mMode = 0;
        }
    }
}
