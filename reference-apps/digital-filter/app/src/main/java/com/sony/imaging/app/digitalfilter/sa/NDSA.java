package com.sony.imaging.app.digitalfilter.sa;

import android.graphics.Point;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFRawDataInfo;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class NDSA {
    private static final boolean DEBUG = false;
    private static final int RAWCOMP_AWC_LIMIT_MODE = 1;
    private static final int RAWCOMP_AWC_OFF = 1;
    private static final int RAWCOMP_AWC_ON = 1;
    private static final int RAWCOMP_CMD_COPY_OUT_TO_RAW1 = 2;
    private static final int RAWCOMP_CMD_DEBUG = 16777216;
    private static final int RAWCOMP_CMD_RAWCOMPOSITE = 4;
    private static final int SA_PARAM_SIZE = 512;
    private static int mAxiRawAddr;
    private static final String TAG = AppLog.getClassName();
    private static DSP mDSP = null;
    private static DeviceBuffer mRaw1 = null;
    private static DeviceBuffer mRaw2 = null;
    private static CameraSequence.RawData mCompositRaw = null;
    private static GFRawDataInfo m1stRawDataInfo = null;
    private static GFRawDataInfo m2ndRawDataInfo = null;
    private static GFEffectParameters.Parameters mParams = null;
    private static NDSA sInstance = null;
    private static boolean isCopiedRawData = false;
    private static ByteBuffer mSAParamByteBuf = null;
    private static int mParamOffset = 0;
    private static int mWbROffset = 0;
    private static int mBorderId = 0;

    private NDSA() {
    }

    public static NDSA getInstance() {
        if (sInstance == null) {
            sInstance = new NDSA();
        }
        return sInstance;
    }

    public void open(DSP dsp, DeviceBuffer raw1, GFRawDataInfo raw1Info, DeviceBuffer raw2, GFRawDataInfo raw2Info, CameraSequence.RawData compRaw, int axiRawAddr, int id) {
        mDSP = dsp;
        mRaw1 = raw1;
        mRaw2 = raw2;
        mBorderId = id;
        mCompositRaw = compRaw;
        m1stRawDataInfo = raw1Info;
        m2ndRawDataInfo = raw2Info;
        mAxiRawAddr = axiRawAddr;
        mDSP.setProgram(getFilePath());
        isCopiedRawData = false;
        mParams = GFEffectParameters.getInstance().getParameters();
        mSAParamByteBuf = getSaParam();
    }

    public void execute() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        DeviceMemory createBuffer = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
        GFCommonUtil.getInstance().setGraduationSeed(createBuffer, GFConstants.mGraduationSeed);
        mSAParamByteBuf = setSeedTableAddr(mSAParamByteBuf, createBuffer);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mCompositRaw, createBuffer};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isCopiedRawData = true;
        createBuffer.release();
    }

    public void cancel() {
        if (mDSP != null) {
            mDSP.cancel();
        }
    }

    public void update() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        DeviceMemory createBuffer = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
        GFCommonUtil.getInstance().setGraduationSeed(createBuffer, GFConstants.mGraduationSeed);
        mSAParamByteBuf = setSeedTableAddr(mSAParamByteBuf, createBuffer);
        mSAParamByteBuf = updateParams(mSAParamByteBuf);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mCompositRaw, createBuffer};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createBuffer.release();
    }

    public void copy() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        mSAParamByteBuf = copyParams(mSAParamByteBuf);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mCompositRaw};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isCopiedRawData = true;
    }

    public void copy(DSP dsp, DeviceBuffer raw1, GFRawDataInfo raw1Info, CameraSequence.RawData raw) {
        open(dsp, raw1, raw1Info, raw1, raw1Info, raw, raw1Info.axiRawAddr, 0);
        copy();
        close();
    }

    private ByteBuffer copyParams(ByteBuffer saParam) {
        saParam.position(0);
        saParam.putInt(2);
        return saParam;
    }

    private ByteBuffer updateParams(ByteBuffer saParam) {
        saParam.position(0);
        if (isCopiedRawData) {
            saParam.putInt(4);
        } else {
            saParam.putInt(4);
        }
        saParam.position(mWbROffset);
        saParam.putShort((short) m1stRawDataInfo.wbR);
        saParam.putShort((short) m1stRawDataInfo.wbB);
        saParam.position(mParamOffset);
        int degree = GFCommonUtil.getInstance().getSADegree(mBorderId);
        if (GFShootingOrderController.getInstance().isLandFirst() && (degree = degree + 180) >= 360) {
            degree -= 360;
        }
        double rad = (degree / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point = GFCommonUtil.getInstance().getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio, mBorderId);
        int valueX = point.x;
        int valueY = point.y;
        saParam.putInt(valueSin);
        saParam.putInt(valueCos);
        saParam.putShort((short) valueX);
        saParam.putShort((short) valueY);
        saParam.putInt(GFCommonUtil.getInstance().getSAStrength(mBorderId));
        return saParam;
    }

    public void close() {
        mDSP.clearProgram();
        isCopiedRawData = false;
        mSAParamByteBuf = null;
    }

    private String getFilePath() {
        String filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_half_nd_musashi.so";
        AppLog.info(TAG, "SA file path: " + filePath);
        return filePath;
    }

    private static ByteBuffer setSeedTableAddr(ByteBuffer saParam, DeviceMemory seed) {
        saParam.position(12);
        saParam.putInt(SaUtil.getMemoryAddressAxi(seed));
        return saParam;
    }

    private ByteBuffer getSaParam() {
        ByteBuffer saParam = SaUtil.getSaParam(SA_PARAM_SIZE);
        saParam.putInt(4);
        saParam.putShort((short) 0);
        saParam.putShort((short) 0);
        saParam.putShort((short) 256);
        saParam.putShort((short) 0);
        saParam.putInt(0);
        GFRawDataInfo rawInfo = m1stRawDataInfo;
        saParam.putInt(SaUtil.getMemoryAddressAxi(mRaw1));
        saParam.putShort((short) rawInfo.canvasSizeX);
        saParam.putShort((short) rawInfo.canvasSizeY);
        saParam.putShort((short) rawInfo.marginOffsetX);
        saParam.putShort((short) rawInfo.marginOffsetY);
        saParam.putShort((short) rawInfo.marginSizeX);
        saParam.putShort((short) rawInfo.marginSizeY);
        saParam.putShort((short) rawInfo.validOffsetX);
        saParam.putShort((short) rawInfo.validOffsetY);
        saParam.putShort((short) rawInfo.validSizeX);
        saParam.putShort((short) rawInfo.validSizeY);
        saParam.putShort((short) rawInfo.firstColor);
        saParam.putShort((short) rawInfo.clpR);
        saParam.putShort((short) rawInfo.clpGr);
        saParam.putShort((short) rawInfo.clpGb);
        saParam.putShort((short) rawInfo.clpB);
        saParam.putShort((short) rawInfo.clpOfst);
        mWbROffset = saParam.position();
        saParam.putShort((short) m1stRawDataInfo.wbR);
        saParam.putShort((short) m1stRawDataInfo.wbB);
        GFRawDataInfo rawInfo2 = m2ndRawDataInfo;
        saParam.putInt(SaUtil.getMemoryAddressAxi(mRaw2));
        saParam.putShort((short) rawInfo2.canvasSizeX);
        saParam.putShort((short) rawInfo2.canvasSizeY);
        saParam.putShort((short) rawInfo2.marginOffsetX);
        saParam.putShort((short) rawInfo2.marginOffsetY);
        saParam.putShort((short) rawInfo2.marginSizeX);
        saParam.putShort((short) rawInfo2.marginSizeY);
        saParam.putShort((short) rawInfo2.validOffsetX);
        saParam.putShort((short) rawInfo2.validOffsetY);
        saParam.putShort((short) rawInfo2.validSizeX);
        saParam.putShort((short) rawInfo2.validSizeY);
        saParam.putShort((short) rawInfo2.firstColor);
        saParam.putShort((short) rawInfo2.clpR);
        saParam.putShort((short) rawInfo2.clpGr);
        saParam.putShort((short) rawInfo2.clpGb);
        saParam.putShort((short) rawInfo2.clpB);
        saParam.putShort((short) rawInfo2.clpOfst);
        saParam.putShort((short) rawInfo2.wbR);
        saParam.putShort((short) rawInfo2.wbB);
        GFRawDataInfo rawInfo3 = m2ndRawDataInfo;
        saParam.putInt(mAxiRawAddr);
        saParam.putShort((short) rawInfo3.canvasSizeX);
        saParam.putShort((short) rawInfo3.canvasSizeY);
        saParam.putShort((short) rawInfo3.marginOffsetX);
        saParam.putShort((short) rawInfo3.marginOffsetY);
        saParam.putShort((short) rawInfo3.marginSizeX);
        saParam.putShort((short) rawInfo3.marginSizeY);
        saParam.putShort((short) rawInfo3.validOffsetX);
        saParam.putShort((short) rawInfo3.validOffsetY);
        saParam.putShort((short) rawInfo3.validSizeX);
        saParam.putShort((short) rawInfo3.validSizeY);
        saParam.putShort((short) rawInfo3.firstColor);
        saParam.putShort((short) rawInfo3.clpR);
        saParam.putShort((short) rawInfo3.clpGr);
        saParam.putShort((short) rawInfo3.clpGb);
        saParam.putShort((short) rawInfo3.clpB);
        saParam.putShort((short) rawInfo3.clpOfst);
        saParam.putShort((short) rawInfo3.wbR);
        saParam.putShort((short) rawInfo3.wbB);
        GFRawDataInfo rawInfo4 = m2ndRawDataInfo;
        saParam.putInt(rawInfo4.ddithRstmod);
        saParam.putInt(rawInfo4.ddithOn);
        saParam.putInt(rawInfo4.expBit);
        saParam.putInt(rawInfo4.decompMode);
        saParam.putInt(rawInfo4.dth0);
        saParam.putInt(rawInfo4.dth1);
        saParam.putInt(rawInfo4.dth2);
        saParam.putInt(rawInfo4.dth3);
        saParam.putInt(rawInfo4.dp0);
        saParam.putInt(rawInfo4.dp1);
        saParam.putInt(rawInfo4.dp2);
        saParam.putInt(rawInfo4.dp3);
        saParam.putInt(rawInfo4.dithRstmod);
        saParam.putInt(rawInfo4.dithOn);
        saParam.putInt(rawInfo4.rndBit);
        saParam.putInt(rawInfo4.compMode);
        saParam.putInt(rawInfo4.th0);
        saParam.putInt(rawInfo4.th1);
        saParam.putInt(rawInfo4.th2);
        saParam.putInt(rawInfo4.th3);
        saParam.putInt(rawInfo4.p0);
        saParam.putInt(rawInfo4.p1);
        saParam.putInt(rawInfo4.p2);
        saParam.putInt(rawInfo4.p3);
        saParam.putShort((short) 1);
        saParam.putShort((short) 1);
        int degree = GFCommonUtil.getInstance().getSADegree(mBorderId);
        if (((mBorderId == 0 && GFShootingOrderController.getInstance().isLandFirst()) || mBorderId == 1) && (degree = degree + 180) >= 360) {
            degree -= 360;
        }
        double rad = (degree / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point = GFCommonUtil.getInstance().getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio, mBorderId);
        int valueX = point.x;
        int valueY = point.y;
        mParamOffset = saParam.position();
        saParam.putInt(valueSin);
        saParam.putInt(valueCos);
        saParam.putShort((short) valueX);
        saParam.putShort((short) valueY);
        saParam.putInt(GFCommonUtil.getInstance().getSAStrength(mBorderId));
        for (int i = 0; i < 4; i++) {
            saParam.putInt(0);
        }
        saParam.putInt(0);
        for (int i2 = 0; i2 < 15; i2++) {
            saParam.putInt(0);
        }
        for (int i3 = 0; i3 < 15; i3++) {
            saParam.putInt(0);
        }
        saParam.putInt(-559038737);
        return saParam;
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
}
