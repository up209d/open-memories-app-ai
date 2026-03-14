package com.sony.imaging.app.digitalfilter.sa;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.MemoryUtil;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFAdapterImpl;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class PRESA {
    private static final int ADST = 4;
    private static final int ASRC = 1;
    private static final int A_BIT = 17;
    private static final int CMD_COPY_WORK_LAND_TO_DISPLAY = 5111808;
    private static final int EE_OUT = 2;
    private static final int LAND = 3;
    private static final int ONE = 1;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static final int SA_MAX_IMAGE_SIZE = 1572864;
    private static final int SA_MESSAGE_SIZE = 256;
    private static final int SA_PARAM_SIZE = 160;
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
    private static int SA_REQ_STOPPREVIEW_POSITION = 0;
    private static int SA_ACK_STOPPREVIEW_POSITION = 128;
    private static int SA_WAIT_CYCLE_TO_STOP = 19200000;
    private static int SA_REQ_STOPPREVIEW = 1;
    private static int SA_SEND_WAIT_CYCLE = 0;
    private static int SA_ACK_STOPPREVIEW = 1;

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

    private void requestStopPreviewToSA() {
        ByteBuffer buf = ByteBuffer.allocateDirect(8);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(SA_REQ_STOPPREVIEW);
        buf.putInt(SA_WAIT_CYCLE_TO_STOP);
        buf.rewind();
        mReqAckStopPreview.write(buf, 8, SA_REQ_STOPPREVIEW_POSITION);
    }

    private void waitAckStopPreviewFromSA() {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        mReqAckStopPreview.read(buf, 4, SA_ACK_STOPPREVIEW_POSITION);
        int ack = buf.getInt();
        long currentTime = System.currentTimeMillis();
        while (true) {
            if (ack == SA_ACK_STOPPREVIEW) {
                break;
            }
            buf.rewind();
            mReqAckStopPreview.read(buf, 4, SA_ACK_STOPPREVIEW_POSITION);
            ack = buf.getInt();
            long currentTime2 = System.currentTimeMillis();
            if (currentTime2 - currentTime > 1000) {
                AppLog.info(TAG, "waitAckStopPreviewFromSA: TimeOut!");
                break;
            }
        }
    }

    private void ackStopPreviewToSA() {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(SA_SEND_WAIT_CYCLE);
        buf.rewind();
        mReqAckStopPreview.write(buf, 4, SA_REQ_STOPPREVIEW_POSITION);
    }

    public void close() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsFinishedSFRSA = true;
        if (mCameraSequence != null) {
            try {
                requestStopPreviewToSA();
                waitAckStopPreviewFromSA();
                ackStopPreviewToSA();
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
        mByteBuffer.putInt(CMD_COPY_WORK_LAND_TO_DISPLAY);
        mByteBuffer.putInt(0);
        mByteBuffer.putInt(0);
        mByteBuffer.putInt(0);
        int workAddr = SaUtil.getMemoryAddressAxi(isInImage1 ? mInImage1 : mInImage2);
        mByteBuffer.putInt(workAddr);
        mByteBuffer.putInt(0);
        mByteBuffer.putInt(0);
        mByteBuffer.putInt(0);
        mByteBuffer.putInt(0);
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
        AppLog.enter(TAG, AppLog.getMethodName());
        String filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/lib_hgf_sfr.so";
        AppLog.info(TAG, "SA file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private ByteBuffer getCleanBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < 40; i++) {
            buffer.putInt(0);
        }
        buffer.rewind();
        return buffer;
    }
}
