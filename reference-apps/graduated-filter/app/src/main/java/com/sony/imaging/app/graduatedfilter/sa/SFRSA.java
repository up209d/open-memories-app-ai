package com.sony.imaging.app.graduatedfilter.sa;

import android.graphics.PointF;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/* loaded from: classes.dex */
public class SFRSA {
    public static final int CMD_COPY_IN_TO_WORK_ONCE = 2;
    public static final int CMD_COPY_WORK_TO_DISPLAY = 4;
    public static final int CMD_HGF = 8;
    public static final int CMD_HIST_BASE = 16;
    private static final int HIST_ACK_ID_POSITION = 40;
    private static final int HIST_LIMIT = 32767;
    private static final int HIST_LIMIT_POSITION = 44;
    private static final int HIST_REQ_ID_POSITION = 36;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static final int SA_DEBUG_OFFSET = 48;
    private static final int SA_DEBUG_POSITION = 48;
    private static final int SA_HISTDATA_SIZE = 2048;
    private static final int SA_HIST_CMD_OFFSET = 32;
    private static final int SA_MESSAGE_SIZE = 256;
    private static final int SA_PARAM_SIZE = 96;
    private static final String TAG = AppLog.getClassName();
    private static DeviceBuffer mSaWork0 = null;
    public static int mCmd = 2;
    private static GFEffectParameters.Parameters mParams = null;
    private static String mPackageName = null;
    private static DSP mDSP = null;
    private static DeviceBuffer mBootParamBuf = null;
    private static DeviceBuffer mSAParamBuf = null;
    private static DeviceBuffer mSeed = null;
    private static DeviceBuffer mReqAckStopPreview = null;
    private static ByteBuffer mBuf = null;
    private static DeviceBuffer mHistBuf = null;
    private static CameraSequence mCameraSequence = null;
    private static SFRSA sInstance = null;
    private static boolean mIsFirstupdate = true;
    private static boolean mIsFinishedSFRSA = true;
    private static boolean mCompletedInitialization = true;
    private static boolean mOutputEnabled = false;
    private static int SA_REQ_STOPPREVIEW_POSITION = 0;
    private static int SA_ACK_STOPPREVIEW_POSITION = 128;
    private static int SA_WAIT_CYCLE_TO_STOP = 19200000;
    private static int SA_REQ_STOPPREVIEW = 1;
    private static int SA_SEND_WAIT_CYCLE = 0;
    private static int SA_ACK_STOPPREVIEW = 1;

    private SFRSA() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static SFRSA getInstance() {
        if (sInstance == null) {
            sInstance = new SFRSA();
        }
        return sInstance;
    }

    public void initialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mCompletedInitialization = false;
        mIsFinishedSFRSA = false;
        try {
            if (mCameraSequence == null) {
                AppLog.info(TAG, "open Camera Sequence");
                mCameraSequence = CameraSequence.open(ExecutorCreator.getInstance().getSequence().getCameraEx());
            }
            CameraSequence.Options opt = new CameraSequence.Options();
            opt.setOption("MEMORY_MAP_FILE", GFCompositProcess.getFilePathForSpecialSequence("GraduatedFilter", "00"));
            mCameraSequence.setShutterSequenceCallback(new CameraSequence.ShutterSequenceCallback() { // from class: com.sony.imaging.app.graduatedfilter.sa.SFRSA.1
                public void onShutterSequence(CameraSequence.RawData arg0, CameraSequence arg1) {
                }
            }, opt);
            if (mDSP == null) {
                mDSP = DSP.createProcessor("sony-di-dsp");
            }
            if (mSaWork0 == null) {
                mSaWork0 = mDSP.createBuffer(1572864);
            }
            mDSP.setProgram(getFilePath());
            mBootParamBuf = mDSP.createBuffer(SA_BOOT_PARAM_SIZE);
            mSAParamBuf = mDSP.createBuffer(SA_PARAM_SIZE);
            mSeed = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
            mReqAckStopPreview = mDSP.createBuffer(SA_MESSAGE_SIZE);
            ByteBuffer zeroBuffer = ByteBuffer.allocateDirect(SA_MESSAGE_SIZE);
            zeroBuffer.order(ByteOrder.nativeOrder());
            for (int i = 0; i < 64; i++) {
                zeroBuffer.putInt(0);
            }
            zeroBuffer.rewind();
            mReqAckStopPreview.write(zeroBuffer);
            GFCommonUtil.getInstance().setGraduationSeed(mSeed, GFConstants.mGraduationSeed);
            mHistBuf = mDSP.createBuffer(SA_HISTDATA_SIZE);
            ByteBuffer buf = ByteBuffer.allocateDirect(SA_HISTDATA_SIZE);
            buf.order(ByteOrder.nativeOrder());
            mHistBuf.write(buf);
            if (mBuf == null) {
                mBuf = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
                mBuf.order(ByteOrder.nativeOrder());
                for (int i2 = 0; i2 < 24; i2++) {
                    mBuf.putInt(0);
                }
                mBuf.rewind();
                mSAParamBuf.write(mBuf);
            }
            mIsFirstupdate = true;
            mCompletedInitialization = true;
        } catch (RuntimeException e) {
            Log.e(TAG, "initialize catch exception mCameraSequence", e);
            mCompletedInitialization = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setCameraSequence(CameraSequence sequence) {
        mCameraSequence = sequence;
    }

    public CameraSequence getCameraSequence() {
        return mCameraSequence;
    }

    public void setPackageName(String packageName) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mPackageName = packageName;
        AppLog.info(TAG, "Application Package Name : " + mPackageName);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setCommand(int cmd) {
        mCmd = cmd;
    }

    public void startLiveViewEffect(boolean isOutputEnabled) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!mCompletedInitialization) {
            AppLog.error(TAG, "Initialization is not yet");
            return;
        }
        if (mIsFirstupdate) {
            if (mCameraSequence == null) {
                AppLog.info(TAG, "open Camera Sequence");
                mCameraSequence = CameraSequence.open(ExecutorCreator.getInstance().getSequence().getCameraEx());
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
            opts.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", isOutputEnabled);
            mOutputEnabled = isOutputEnabled;
            setBootParam();
            setLiveViewEffect();
            mCameraSequence.setPreviewPlugin(mDSP);
            mCameraSequence.startPreviewSequence(opts);
            mIsFirstupdate = false;
            mIsFinishedSFRSA = false;
        } else {
            updateLiveViewEffect();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void stopLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mCameraSequence != null) {
            mCameraSequence.setPreviewPlugin((DSP) null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isReversedDisplay = GFCommonUtil.getInstance().isReversedDisplay();
        mParams = GFEffectParameters.getInstance().getParameters();
        mBuf.rewind();
        mBuf.putInt(mCmd);
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mSaWork0));
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mReqAckStopPreview));
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mSeed));
        double rad = (mParams.getSADegree() / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        int previewWidth = 1024;
        int previewHeight = 768;
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                previewWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
                previewHeight = AppRoot.USER_KEYCODE.WATER_HOUSING;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                previewWidth = 1024;
                previewHeight = 684;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                previewWidth = 1024;
                previewHeight = 576;
            } else {
                previewWidth = 1024;
                previewHeight = 768;
            }
        }
        PointF point = mParams.getOSDSAPoint(previewWidth, previewHeight);
        int valueX = (int) point.x;
        int valueY = (int) point.y;
        if (isReversedDisplay) {
            valueX = previewWidth - valueX;
        }
        mBuf.putInt(valueSin);
        mBuf.putInt(valueCos);
        mBuf.putShort((short) valueX);
        mBuf.putShort((short) valueY);
        mBuf.putInt(mParams.getSAStrength());
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mHistBuf));
        mBuf.position(HIST_LIMIT_POSITION);
        mBuf.putInt(HIST_LIMIT);
        for (int i = 0; i < 12; i++) {
            mBuf.putInt(0);
        }
        mSAParamBuf.write(mBuf, 36, 0);
        setHistgramLimit();
        if (mIsFirstupdate) {
            mDSP.setArg(1, mSAParamBuf);
            mDSP.setArg(2, mSaWork0);
            mDSP.setArg(3, mHistBuf);
            mDSP.setArg(4, mSeed);
            mDSP.setArg(5, mReqAckStopPreview);
        }
        Log.i(TAG, "cmd = " + mCmd);
        Log.i(TAG, "workaddr = " + SaUtil.getMemoryAddressAxi(mSaWork0));
        Log.i(TAG, "sfrWorkDataWidth = 0");
        Log.i(TAG, "sfrWorkDataHeight = 0");
        Log.i(TAG, "modeGrad_SinTheta = " + valueSin);
        Log.i(TAG, "modeGrad_CosTheta = " + valueCos);
        Log.i(TAG, "modeGrad_X0 = " + valueX);
        Log.i(TAG, "modeGrad_Y0 = " + valueY);
        Log.i(TAG, "modeGrad_Coeff = " + mParams.getSAStrength());
        Log.i(TAG, "Hist adder = " + SaUtil.getMemoryAddressAxi(mHistBuf));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateLiveViewEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!mCompletedInitialization) {
            AppLog.error(TAG, "Initialization is not yet");
            return;
        }
        boolean isReversedDisplay = GFCommonUtil.getInstance().isReversedDisplay();
        mParams = GFEffectParameters.getInstance().getParameters();
        ByteBuffer buf = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
        buf.order(ByteOrder.nativeOrder());
        double rad = (mParams.getSADegree() / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        int previewWidth = 1024;
        int previewHeight = 768;
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                previewWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
                previewHeight = AppRoot.USER_KEYCODE.WATER_HOUSING;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                previewWidth = 1024;
                previewHeight = 684;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                previewWidth = 1024;
                previewHeight = 576;
            } else {
                previewWidth = 1024;
                previewHeight = 768;
            }
        }
        PointF point = mParams.getOSDSAPoint(previewWidth, previewHeight);
        int valueX = (int) point.x;
        int valueY = (int) point.y;
        if (isReversedDisplay) {
            valueX = previewWidth - valueX;
        }
        buf.putInt(valueSin);
        buf.putInt(valueCos);
        buf.putShort((short) valueX);
        buf.putShort((short) valueY);
        buf.putInt(mParams.getSAStrength());
        buf.rewind();
        mSAParamBuf.write(buf, 16, 16);
        Log.i(TAG, "modeGrad_SinTheta = " + valueSin);
        Log.i(TAG, "modeGrad_CosTheta = " + valueCos);
        Log.i(TAG, "modeGrad_X0 = " + valueX);
        Log.i(TAG, "modeGrad_Y0 = " + valueY);
        Log.i(TAG, "modeGrad_Coeff = " + mParams.getSAStrength());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void dumpSaWork() {
        ByteBuffer buf = ByteBuffer.allocateDirect(1572864);
        buf.order(ByteOrder.nativeOrder());
        mSaWork0.read(buf);
        byte[] data = new byte[1572864];
        buf.get(data);
    }

    public void setHistgramReqID(int reqID) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(reqID);
        mSAParamBuf.write(buf, 4, 36);
    }

    public void setHistgramLimit() {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(HIST_LIMIT);
        mSAParamBuf.write(buf, 4, HIST_LIMIT_POSITION);
    }

    public void setSADebug() {
        ByteBuffer buf = ByteBuffer.allocateDirect(48);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(HIST_LIMIT);
        for (int i = 0; i < 12; i++) {
            buf.putInt(0);
        }
        mSAParamBuf.write(buf, 4, 48);
    }

    public boolean isAvailableHistgram() {
        return !mIsFinishedSFRSA && mCompletedInitialization;
    }

    public int getHistgramID() {
        if (mSAParamBuf == null) {
            return -1;
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        mSAParamBuf.read(buf, 4, 40);
        return buf.getInt();
    }

    public short[] getHistgramData() {
        ByteBuffer buf = ByteBuffer.allocateDirect(SA_HISTDATA_SIZE);
        buf.order(ByteOrder.nativeOrder());
        mHistBuf.read(buf);
        ShortBuffer sbuf = buf.asShortBuffer();
        short[] data = new short[1024];
        sbuf.get(data);
        return data;
    }

    private static int DoubleToFixedPointInt(double valued, int bitlen) {
        int value = (int) ((1 << (bitlen + 1)) * valued);
        if (value >= 0.0d) {
            int result = (value + 1) >> 1;
            return result;
        }
        int result2 = -(((-value) + 1) >> 1);
        return result2;
    }

    public void terminateAll() {
        terminate();
        if (mCameraSequence != null) {
            mCameraSequence.release();
            AppLog.info(TAG, "released mCameraSequence");
            mCameraSequence = null;
        }
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

    private boolean avoidPFIssue() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        return !mOutputEnabled && pfMajorVersion == 2 && pfAPIVersion == 14;
    }

    public void terminate() {
        mCompletedInitialization = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mIsFinishedSFRSA) {
            AppLog.info(TAG, "SFRSA is already stopped.");
            return;
        }
        mIsFinishedSFRSA = true;
        if (mCameraSequence != null) {
            try {
                if (avoidPFIssue()) {
                    requestStopPreviewToSA();
                    waitAckStopPreviewFromSA();
                    ackStopPreviewToSA();
                }
                mCameraSequence.stopPreviewSequence();
                mCameraSequence.setPreviewPlugin((DSP) null);
                mCameraSequence.setShutterSequenceCallback((CameraSequence.ShutterSequenceCallback) null);
            } catch (RuntimeException e) {
                Log.e(TAG, "Method called after mCameraSequence.release()", e);
            }
        }
        if (mDSP != null) {
            mDSP.release();
            mDSP = null;
        }
        if (mSaWork0 != null) {
            mSaWork0.release();
            mSaWork0 = null;
        }
        if (mBootParamBuf != null) {
            mBootParamBuf.release();
            mBootParamBuf = null;
        }
        if (mSAParamBuf != null) {
            mSAParamBuf.release();
            mSAParamBuf = null;
        }
        if (mHistBuf != null) {
            mHistBuf.release();
            mHistBuf = null;
        }
        if (mSeed != null) {
            mSeed.release();
            mSeed = null;
        }
        if (mReqAckStopPreview != null) {
            mReqAckStopPreview.release();
            mReqAckStopPreview = null;
        }
        mBuf = null;
        mPackageName = null;
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
            filePath = "/android/data/lib/" + mPackageName + "/lib/lib_hgf_sfr_avip.so";
        } else {
            filePath = "/android/data/lib/" + mPackageName + "/lib/lib_hgf_sfr.so";
        }
        AppLog.info(TAG, "SA file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }
}
