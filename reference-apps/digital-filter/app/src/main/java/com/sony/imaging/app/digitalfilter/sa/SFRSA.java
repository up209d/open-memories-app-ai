package com.sony.imaging.app.digitalfilter.sa;

import android.graphics.PointF;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFClippingCorrectionController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.fw.AppRoot;
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
    private static final int ADST = 4;
    private static final int ASRC = 1;
    private static final int A_BIT = 17;
    private static final int BDST = 7;
    private static final int BSRC0 = 4;
    private static final int BSRC1 = 1;
    private static final int B_BIT = 7;
    private static final int CDST = 4;
    private static final int CSRC = 1;
    private static final int C_BIT = 0;
    private static final int EE_IN = 1;
    private static final int EE_OUT = 2;
    private static final int HIST_ACK_ID_POSITION = 68;
    private static final int HIST_LIMIT = 32767;
    private static final int HIST_LIMIT_POSITION = 72;
    private static final int HIST_REQ_ID_POSITION = 64;
    private static final int LAND = 3;
    private static final int LAND_SKY = 4;
    private static final int NONE = 0;
    private static final int ONE = 1;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static final int SA_BORDER_PARAM_OFFSET = 44;
    private static final int SA_HISTDATA_SIZE = 2048;
    private static final int SA_HIST_CMD_OFFSET = 60;
    private static final int SA_MESSAGE_SIZE = 256;
    private static final int SA_PARAM_SIZE = 160;
    private static final int SA_WORK_SIZE = 1572864;
    private static final int SKY = 5;
    private static final String TAG = AppLog.getClassName();
    private static DeviceBuffer mSaWork0 = null;
    private static DeviceBuffer mSaWork1 = null;
    private static DeviceBuffer mSaWork2 = null;
    public static int CMD_LAND_HIST = 2304;
    private static int COPY_WORK_LAND_TO_SKY = 6684672;
    public static int HGF_LAND_TO_SKY = 39168;
    public static int COPY_HGF_LAND_TO_SKY = COPY_WORK_LAND_TO_SKY | HGF_LAND_TO_SKY;
    private static int COPY_WORK_SKY_TO_LAND = 10878976;
    public static int HGF_SKY_TO_LAND = 36096;
    public static int COPY_HGF_SKY_TO_LAND = COPY_WORK_SKY_TO_LAND | HGF_SKY_TO_LAND;
    public static int COPY_WORK_SKY_TO_LAYER3 = HGF_LAND_TO_SKY | 69;
    public static int HGF_SKY_TO_LAYER3 = 41216;
    public static int HGF_LAYER3_TO_SKY = HGF_LAND_TO_SKY;
    public static int mCmd = CMD_LAND_HIST;
    public static int REMOVE_COPY_BITS = 65528;
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
            opt.setOption("MEMORY_MAP_FILE", GFCompositProcess.getFilePathForSpecialSequence("DigitalFilter", "01"));
            mCameraSequence.setShutterSequenceCallback(new CameraSequence.ShutterSequenceCallback() { // from class: com.sony.imaging.app.digitalfilter.sa.SFRSA.1
                public void onShutterSequence(CameraSequence.RawData arg0, CameraSequence arg1) {
                }
            }, opt);
            if (mDSP == null) {
                mDSP = DSP.createProcessor("sony-di-dsp");
            }
            if (mSaWork0 == null) {
                mSaWork0 = mDSP.createBuffer(SA_WORK_SIZE);
            }
            if (mSaWork1 == null) {
                mSaWork1 = mDSP.createBuffer(SA_WORK_SIZE);
            }
            if (mSaWork2 == null) {
                mSaWork2 = mDSP.createBuffer(SA_WORK_SIZE);
            }
            mDSP.setProgram(getFilePath());
            mBootParamBuf = mDSP.createBuffer(60);
            mSAParamBuf = mDSP.createBuffer(SA_PARAM_SIZE);
            mSeed = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
            mReqAckStopPreview = mDSP.createBuffer(SA_MESSAGE_SIZE);
            ByteBuffer zeroBuffer = ByteBuffer.allocateDirect(SA_MESSAGE_SIZE);
            zeroBuffer.order(ByteOrder.nativeOrder());
            for (int i = 0; i < HIST_REQ_ID_POSITION; i++) {
                zeroBuffer.putInt(0);
            }
            zeroBuffer.rewind();
            mReqAckStopPreview.write(zeroBuffer);
            if (GFClippingCorrectionController.getInstance().getValue(null).equals(GFClippingCorrectionController.NORMAL)) {
                GFCommonUtil.getInstance().setGraduationSeed(mSeed, GFConstants.mGraduationSeed0);
            } else if (GFClippingCorrectionController.getInstance().getValue(null).equals(GFClippingCorrectionController.LOW)) {
                GFCommonUtil.getInstance().setGraduationSeed(mSeed, GFConstants.mGraduationSeed1);
            } else {
                GFCommonUtil.getInstance().setGraduationSeed(mSeed, GFConstants.mGraduationSeed2);
            }
            mHistBuf = mDSP.createBuffer(SA_HISTDATA_SIZE);
            ByteBuffer buf = ByteBuffer.allocateDirect(SA_HISTDATA_SIZE);
            buf.order(ByteOrder.nativeOrder());
            mHistBuf.write(buf);
            if (mBuf == null) {
                mBuf = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
                mBuf.order(ByteOrder.nativeOrder());
                for (int i2 = 0; i2 < 40; i2++) {
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
        if (mCmd == COPY_WORK_LAND_TO_SKY || mCmd == COPY_WORK_SKY_TO_LAND || mCmd == COPY_WORK_SKY_TO_LAYER3) {
            while (!getInstance().isCmdExecuted(mCmd)) {
                try {
                    Thread.sleep(32L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        double rad;
        PointF point;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isReversedDisplay = GFCommonUtil.getInstance().isReversedDisplay();
        mParams = GFEffectParameters.getInstance().getParameters();
        mBuf.rewind();
        mBuf.putInt(mCmd);
        mBuf.putInt(0);
        mBuf.putInt(0);
        mBuf.putInt(0);
        int landAddr = SaUtil.getMemoryAddressAxi(mSaWork0);
        int landSkyAddr = SaUtil.getMemoryAddressAxi(mSaWork1);
        int skyAddr = SaUtil.getMemoryAddressAxi(mSaWork2);
        mBuf.putInt(landAddr);
        mBuf.putInt(landSkyAddr);
        mBuf.putInt(skyAddr);
        mBuf.putInt(0);
        mBuf.putInt(0);
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mReqAckStopPreview));
        mBuf.putInt(SaUtil.getMemoryAddressAxi(mSeed));
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            rad = (mParams.getSADegree() / 180.0d) * 3.141592653589793d;
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            rad = (mParams.getSADegree2() / 180.0d) * 3.141592653589793d;
        } else {
            rad = (mParams.getSADegree() / 180.0d) * 3.141592653589793d;
        }
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
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            point = mParams.getOSDSAPoint(previewWidth, previewHeight);
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            point = mParams.getOSDSAPoint2(previewWidth, previewHeight);
        } else {
            point = mParams.getOSDSAPoint(previewWidth, previewHeight);
        }
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
        for (int i = 0; i < 16; i++) {
            mBuf.putInt(0);
        }
        mSAParamBuf.write(mBuf, HIST_REQ_ID_POSITION, 0);
        setHistgramLimit();
        if (mIsFirstupdate) {
            mDSP.setArg(1, mSAParamBuf);
            mDSP.setArg(2, mSaWork0);
            mDSP.setArg(3, mHistBuf);
            mDSP.setArg(4, mSeed);
            mDSP.setArg(5, mReqAckStopPreview);
            mDSP.setArg(6, mSaWork1);
            mDSP.setArg(6, mSaWork2);
        }
        Log.i(TAG, "cmd = 0x" + Integer.toHexString(mCmd));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getBorderId(int cmd) {
        return cmd == HGF_SKY_TO_LAYER3 ? 1 : 0;
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
        buf.putInt(mCmd);
        mSAParamBuf.write(buf, 4, 0);
        Log.i(TAG, "cmd = " + Integer.toHexString(mCmd));
        buf.rewind();
        int borderId = getBorderId(mCmd);
        if (!GFCommonUtil.getInstance().isLayerSetting() && GFEEAreaController.getInstance().isLayer3()) {
            borderId = 1;
        }
        double rad = (GFCommonUtil.getInstance().getSADegree(borderId) / 180.0d) * 3.141592653589793d;
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
        PointF point = GFCommonUtil.getInstance().getOSDSAPoint(previewWidth, previewHeight, borderId);
        int valueX = (int) point.x;
        int valueY = (int) point.y;
        if (isReversedDisplay) {
            valueX = previewWidth - valueX;
        }
        buf.putInt(valueSin);
        buf.putInt(valueCos);
        buf.putShort((short) valueX);
        buf.putShort((short) valueY);
        buf.putInt(GFCommonUtil.getInstance().getSAStrength(borderId));
        buf.rewind();
        mSAParamBuf.write(buf, 16, SA_BORDER_PARAM_OFFSET);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isCmdExecuted(int cmd) {
        ByteBuffer buf = ByteBuffer.allocateDirect(16);
        buf.order(ByteOrder.nativeOrder());
        mSAParamBuf.read(buf, 16, 0);
        boolean isExecuted = cmd != buf.getInt();
        return isExecuted;
    }

    public void setHistgramReqID(int reqID) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(reqID);
        mSAParamBuf.write(buf, 4, HIST_REQ_ID_POSITION);
    }

    public void setHistgramLimit() {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(HIST_LIMIT);
        mSAParamBuf.write(buf, 4, HIST_LIMIT_POSITION);
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
        mSAParamBuf.read(buf, 4, HIST_ACK_ID_POSITION);
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
        if (mSaWork1 != null) {
            mSaWork1.release();
            mSaWork1 = null;
        }
        if (mSaWork2 != null) {
            mSaWork2.release();
            mSaWork2 = null;
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
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
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
        String filePath = "/android/data/lib/" + mPackageName + "/lib/lib_hgf_sfr.so";
        AppLog.info(TAG, "SA file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }
}
