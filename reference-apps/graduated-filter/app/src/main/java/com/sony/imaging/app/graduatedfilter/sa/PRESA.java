package com.sony.imaging.app.graduatedfilter.sa;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.MemoryUtil;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFAdapterImpl;
import com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class PRESA {
    public static final int CMD_COPY_WORK_TO_DISPLAY = 4;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static final int SA_MAX_IMAGE_SIZE = 1572864;
    private static final int SA_MESSAGE_SIZE = 256;
    private static final int SA_PARAM_SIZE = 96;
    private static final String TAG = AppLog.getClassName();
    private static DSP mDSP = null;
    private static DeviceBuffer mBootParamBuf = null;
    private static DeviceBuffer mSAParamBuf = null;
    private static DeviceBuffer mInImage1 = null;
    private static DeviceBuffer mInImage2 = null;
    private static boolean isInImage1 = true;
    private static ByteBuffer mByteBuffer = null;
    private static CameraSequence mCameraSequence = null;
    private static PRESA sInstance = null;
    private static boolean mIsFinishedSFRSA = true;
    private static DeviceBuffer mReqAckStopPreview = null;

    private PRESA() {
    }

    public static PRESA getInstance() {
        if (sInstance == null) {
            sInstance = new PRESA();
        }
        return sInstance;
    }

    public void open() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mDSP = DSP.createProcessor("sony-di-dsp");
        mDSP.setProgram(getFilePath());
        mCameraSequence = ((GFAdapterImpl) GFCompositProcess.getAdapter()).getSequence();
        mBootParamBuf = mDSP.createBuffer(SA_BOOT_PARAM_SIZE);
        mSAParamBuf = mDSP.createBuffer(SA_PARAM_SIZE);
        mByteBuffer = getCleanBuffer();
        mSAParamBuf.write(mByteBuffer);
        mInImage1 = mDSP.createBuffer(SA_MAX_IMAGE_SIZE);
        mInImage2 = mDSP.createBuffer(SA_MAX_IMAGE_SIZE);
        mReqAckStopPreview = mDSP.createBuffer(SA_MESSAGE_SIZE);
        ByteBuffer zeroBuffer = ByteBuffer.allocateDirect(SA_MESSAGE_SIZE);
        zeroBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < 64; i++) {
            zeroBuffer.putInt(0);
        }
        zeroBuffer.rewind();
        mReqAckStopPreview.write(zeroBuffer);
        mIsFinishedSFRSA = false;
        isInImage1 = true;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void close() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsFinishedSFRSA = true;
        if (mCameraSequence != null) {
            try {
                mCameraSequence.stopPreviewSequence();
                mCameraSequence.setPreviewPlugin((DSP) null);
                mCameraSequence = null;
            } catch (RuntimeException e) {
                Log.e(TAG, "Method called after mCameraSequence.release()", e);
            }
        }
        if (mBootParamBuf != null) {
            mBootParamBuf.release();
            mBootParamBuf = null;
        }
        if (mSAParamBuf != null) {
            mSAParamBuf.release();
            mSAParamBuf = null;
        }
        if (mInImage1 != null) {
            mInImage1.release();
            mInImage1 = null;
        }
        if (mInImage2 != null) {
            mInImage2.release();
            mInImage2 = null;
        }
        if (mReqAckStopPreview != null) {
            mReqAckStopPreview.release();
            mReqAckStopPreview = null;
        }
        if (mDSP != null) {
            mDSP.release();
            mDSP = null;
        }
        mByteBuffer = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void execute(OptimizedImage inImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mIsFinishedSFRSA) {
            AppLog.info(TAG, "SFRSA is already stopped.");
            return;
        }
        CameraSequence.Options opts = new CameraSequence.Options();
        opts.setOption("PREVIEW_FRAME_RATE", 30000);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
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
        update(inImage);
        setBootParam();
        mDSP.setArg(1, mSAParamBuf);
        mDSP.setArg(2, mInImage1);
        mDSP.setArg(3, mInImage2);
        mDSP.setArg(4, mReqAckStopPreview);
        mCameraSequence.setPreviewPlugin(mDSP);
        mCameraSequence.startPreviewSequence(opts);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void update(OptimizedImage inImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mIsFinishedSFRSA) {
            AppLog.info(TAG, "SFRSA is already stopped.");
            return;
        }
        MemoryUtil memUtil = new MemoryUtil();
        int src = mDSP.getPropertyAsInt(inImage, "memory-address");
        int canvasWidth = SaUtil.getOptImgWidth(inImage);
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        int srcSize = canvasWidth * height * 2;
        int dest = mDSP.getPropertyAsInt(isInImage1 ? mInImage1 : mInImage2, "memory-address");
        int destSize = width * height * 2;
        memUtil.memoryCopyRaw(src, dest, srcSize, destSize, canvasWidth, width, height);
        memUtil.memoryCopyRaw(src, dest, srcSize, destSize, canvasWidth, width, height);
        inImage.release();
        setSaParam();
        isInImage1 = !isInImage1;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setSaParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mByteBuffer.rewind();
        mByteBuffer.putInt(4);
        mByteBuffer.putInt(SaUtil.getMemoryAddressAxi(isInImage1 ? mInImage1 : mInImage2));
        mByteBuffer.putInt(SaUtil.getMemoryAddressAxi(mReqAckStopPreview));
        mSAParamBuf.write(mByteBuffer);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBootParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ByteBuffer buf = ByteBuffer.allocateDirect(SA_BOOT_PARAM_SIZE);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(mDSP.getPropertyAsInt("program-descriptor"));
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
        mBootParamBuf.write(buf);
        mDSP.setArg(0, mBootParamBuf);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getFilePath() {
        String filePath;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (1 == Environment.getVersionOfHW()) {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/lib_hgf_sfr_avip.so";
        } else {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/lib_hgf_sfr.so";
        }
        AppLog.info(TAG, "SA file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private ByteBuffer getCleanBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < 24; i++) {
            buffer.putInt(0);
        }
        buffer.rewind();
        return buffer;
    }
}
