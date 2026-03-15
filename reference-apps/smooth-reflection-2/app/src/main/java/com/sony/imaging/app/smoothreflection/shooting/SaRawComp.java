package com.sony.imaging.app.smoothreflection.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: classes.dex */
public class SaRawComp {
    private static final int AB_RAW_OFFSET_LINES = 8;
    private static final String DSCHX400V = "DSC-HX400V";
    private static final String DSCHX60V = "DSC-HX60V";
    private static final String DSCRX100M3 = "DSC-RX100M3";
    private static final double FPNR_ENABLED_SHUTTERSPEED = 1.0d;
    private static final int FQ_RAW_OFFSET_LINES = 6;
    private static final String ILCE5000 = "ILCE-5000";
    private static final int NFBR_ENABLED_SHUTTERSPEED = 8;
    private static final int OPB_REGION = 64;
    private static final String PROP_MODEL_NAME = "model.name";
    private static final int ZNS_ZNGK_RAW_OFFSET_LINES = 6;
    static int avip_canvasSizeX;
    static int avip_canvasSizeY;
    static int avip_clpB;
    static int avip_clpGb;
    static int avip_clpGr;
    static int avip_clpOfst;
    static int avip_clpR;
    static int avip_compMode;
    static int avip_ddithOn;
    static int avip_ddithRstmod;
    static int avip_decompMode;
    static int avip_dithOn;
    static int avip_dithRstmod;
    static int avip_dp0;
    static int avip_dp1;
    static int avip_dp2;
    static int avip_dp3;
    static int avip_dth0;
    static int avip_dth1;
    static int avip_dth2;
    static int avip_dth3;
    static int avip_expBit;
    static int avip_firstColor = 0;
    static SmoothReflectionCompositProcess.ImageDataSet avip_info = new SmoothReflectionCompositProcess.ImageDataSet();
    static int avip_marginOffsetX;
    static int avip_marginOffsetY;
    static int avip_marginSizeX;
    static int avip_marginSizeY;
    static int avip_p0;
    static int avip_p1;
    static int avip_p2;
    static int avip_p3;
    static int avip_raw;
    static int avip_rndBit;
    static int avip_th0;
    static int avip_th1;
    static int avip_th2;
    static int avip_th3;
    static int avip_validOffsetX;
    static int avip_validOffsetY;
    static int avip_validSizeX;
    static int avip_validSizeY;
    static int avip_wbB;
    static int avip_wbR;
    DeviceBuffer mSaWork0 = null;
    DeviceBuffer mSaWork1 = null;
    DSP mDsp = null;
    int mCompositeCount = 0;
    int mRawCanvasSizeX = 0;
    int mRawCanvasSizeY = 0;

    public void open() {
        open(-1, -1);
    }

    public void open(int canvasSizeX, int canvasSizeY) {
        this.mCompositeCount = 0;
        this.mRawCanvasSizeX = canvasSizeX;
        this.mRawCanvasSizeY = canvasSizeY;
        int maxYCSize = getYCImageSize();
        releaseDeviceResources();
        if (this.mDsp == null) {
            this.mDsp = DSP.createProcessor("sony-di-dsp");
            if (SaUtil.isAVIP()) {
                this.mDsp.setProgram("/android/data/data/com.sony.imaging.app.smoothreflection/lib/libsa_smoothphotography_avip.so");
            } else {
                this.mDsp.setProgram("/android/data/data/com.sony.imaging.app.smoothreflection/lib/libsa_smoothphotography.so");
            }
        }
        if (this.mSaWork0 == null) {
            this.mSaWork0 = this.mDsp.createBuffer(maxYCSize / 2);
            SaUtil.dispMemoryAddressArm(this.mSaWork0, "mSaWork0");
        }
        if (this.mSaWork1 == null) {
            this.mSaWork1 = this.mDsp.createBuffer(maxYCSize);
            SaUtil.dispMemoryAddressArm(this.mSaWork1, "mSaWork1");
        }
    }

    public void close() {
        this.mCompositeCount = 0;
        this.mRawCanvasSizeX = 0;
        this.mRawCanvasSizeY = 0;
        releaseDeviceResources();
    }

    public void releaseDeviceResources() {
        if (this.mSaWork0 != null) {
            Log.d("SAE", "mSaWork0 is released");
            this.mSaWork0.release();
            this.mSaWork0 = null;
        }
        if (this.mSaWork1 != null) {
            Log.d("SAE", "mSaWork1 is released");
            this.mSaWork1.release();
            this.mSaWork1 = null;
        }
        if (this.mDsp != null) {
            Log.d("SAE", "mDsp is released");
            this.mDsp.clearProgram();
            this.mDsp.release();
            this.mDsp = null;
        }
    }

    private static ByteBuffer getSaParam(CameraSequence.RawData raw, DeviceMemory saWork0, int saCount, CameraSequence.RawDataInfo info) {
        ByteBuffer saParam = SaUtil.getSaParam(256);
        int cmd = saCount == 0 ? 6 : 4;
        Log.d("SAE", "cmd=" + cmd);
        saParam.putInt(cmd);
        if (SaUtil.isAVIP()) {
            saParam.putInt(SaUtil.convArmAddr2AxiAddr(avip_raw));
        } else {
            int raw_address = SaUtil.getMemoryAddressAxi(raw);
            String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
            Log.d("BFNR Check", "MODEL NAME = " + ModelName);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
            boolean longExposure_flg = ((CameraEx.ParametersModifier) params.second).getLongExposureNR();
            Log.d("BFNR Check", "EXPOSURE = " + longExposure_flg);
            CameraEx.ShutterSpeedInfo sinfo = CameraSetting.getInstance().getShutterSpeedInfo();
            float shutterspeed = sinfo.currentShutterSpeed_n / sinfo.currentShutterSpeed_d;
            Log.d("BFNR Check", "SS = " + shutterspeed);
            if (ModelName.equals(DSCRX100M3) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += (info.canvasSizeX + OPB_REGION) * 6;
            } else if (ModelName.equals(ILCE5000) && longExposure_flg && shutterspeed >= FPNR_ENABLED_SHUTTERSPEED) {
                raw_address += info.canvasSizeX * 8;
            } else if (ModelName.equals(DSCHX400V) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += info.canvasSizeX * 6;
            } else if (ModelName.equals(DSCHX60V) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += info.canvasSizeX * 6;
            }
            saParam.putInt(raw_address);
        }
        saParam.putInt(SaUtil.getMemoryAddressAxi(saWork0));
        saParam.putInt(saCount);
        if (SaUtil.isAVIP()) {
            saParam.putInt(avip_canvasSizeX);
            saParam.putInt(avip_canvasSizeY);
            saParam.putInt(avip_marginOffsetX);
            saParam.putInt(avip_marginOffsetY);
            saParam.putInt(avip_marginSizeX);
            saParam.putInt(avip_marginSizeY);
            saParam.putInt(avip_validOffsetX);
            saParam.putInt(avip_validOffsetY);
            saParam.putInt(avip_validSizeX);
            saParam.putInt(avip_validSizeY);
            saParam.putInt(avip_firstColor);
            saParam.putInt(avip_clpR);
            saParam.putInt(avip_clpGr);
            saParam.putInt(avip_clpGb);
            saParam.putInt(avip_clpB);
            saParam.putInt(avip_clpOfst);
            saParam.putInt(avip_wbR);
            saParam.putInt(avip_wbB);
            saParam.putInt(avip_ddithRstmod);
            saParam.putInt(avip_ddithOn);
            saParam.putInt(avip_expBit);
            saParam.putInt(avip_decompMode);
            saParam.putInt(avip_dth0);
            saParam.putInt(avip_dth1);
            saParam.putInt(avip_dth2);
            saParam.putInt(avip_dth3);
            saParam.putInt(avip_dp0);
            saParam.putInt(avip_dp1);
            saParam.putInt(avip_dp2);
            saParam.putInt(avip_dp3);
            saParam.putInt(avip_dithRstmod);
            saParam.putInt(avip_dithOn);
            saParam.putInt(avip_rndBit);
            saParam.putInt(avip_compMode);
            saParam.putInt(avip_th0);
            saParam.putInt(avip_th1);
            saParam.putInt(avip_th2);
            saParam.putInt(avip_th3);
            saParam.putInt(avip_p0);
            saParam.putInt(avip_p1);
            saParam.putInt(avip_p2);
            saParam.putInt(avip_p3);
        } else {
            saParam.putInt(info.canvasSizeX);
            saParam.putInt(info.canvasSizeY);
            saParam.putInt(info.marginOffsetX);
            saParam.putInt(info.marginOffsetY);
            saParam.putInt(info.marginSizeX);
            saParam.putInt(info.marginSizeY);
            saParam.putInt(info.validOffsetX);
            saParam.putInt(info.validOffsetY);
            saParam.putInt(info.validSizeX);
            saParam.putInt(info.validSizeY);
            saParam.putInt(info.firstColor);
            saParam.putInt(info.clpR);
            saParam.putInt(info.clpGr);
            saParam.putInt(info.clpGb);
            saParam.putInt(info.clpB);
            saParam.putInt(info.clpOfst);
            saParam.putInt(info.wbR);
            saParam.putInt(info.wbB);
            saParam.putInt(info.ddithRstmod);
            saParam.putInt(info.ddithOn);
            saParam.putInt(info.expBit);
            saParam.putInt(info.decompMode);
            saParam.putInt(info.dth0);
            saParam.putInt(info.dth1);
            saParam.putInt(info.dth2);
            saParam.putInt(info.dth3);
            saParam.putInt(info.dp0);
            saParam.putInt(info.dp1);
            saParam.putInt(info.dp2);
            saParam.putInt(info.dp3);
            saParam.putInt(info.dithRstmod);
            saParam.putInt(info.dithOn);
            saParam.putInt(info.rndBit);
            saParam.putInt(info.compMode);
            saParam.putInt(info.th0);
            saParam.putInt(info.th1);
            saParam.putInt(info.th2);
            saParam.putInt(info.th3);
            saParam.putInt(info.p0);
            saParam.putInt(info.p1);
            saParam.putInt(info.p2);
            saParam.putInt(info.p3);
        }
        saParam.putInt(0);
        saParam.putInt(19088743);
        for (int n = 0; n < 12; n++) {
            saParam.putInt(0);
        }
        if (!SaUtil.isAVIP()) {
            Log.i("RAWINFO", "info.canvasSizeX   = " + info.canvasSizeX);
            Log.i("RAWINFO", "info.canvasSizeY   = " + info.canvasSizeY);
            Log.i("RAWINFO", "info.marginOffsetX = " + info.marginOffsetX);
            Log.i("RAWINFO", "info.marginOffsetY = " + info.marginOffsetY);
            Log.i("RAWINFO", "info.marginSizeX   = " + info.marginSizeX);
            Log.i("RAWINFO", "info.marginSizeY   = " + info.marginSizeY);
            Log.i("RAWINFO", "info.validOffsetX  = " + info.validOffsetX);
            Log.i("RAWINFO", "info.validOffsetY  = " + info.validOffsetY);
            Log.i("RAWINFO", "info.validSizeX    = " + info.validSizeX);
            Log.i("RAWINFO", "info.validSizeY    = " + info.validSizeY);
            Log.i("RAWINFO", "info.firstColor    = " + info.firstColor);
            Log.i("RAWINFO", "info.clpR  \t     = " + info.clpR);
            Log.i("RAWINFO", "info.clpGr         = " + info.clpGr);
            Log.i("RAWINFO", "info.clpGb         = " + info.clpGb);
            Log.i("RAWINFO", "info.clpB          = " + info.clpB);
            Log.i("RAWINFO", "info.clpOfst       = " + info.clpOfst);
            Log.i("RAWINFO", "info.wbR           = " + info.wbR);
            Log.i("RAWINFO", "info.ddithRstmod   = " + info.ddithRstmod);
            Log.i("RAWINFO", "info.ddithOn       = " + info.ddithOn);
            Log.i("RAWINFO", "info.expBit        = " + info.expBit);
            Log.i("RAWINFO", "info.decompMode    = " + info.decompMode);
        }
        return saParam;
    }

    private static ByteBuffer getSaParamNew(CameraSequence.RawData raw, DeviceMemory saWork0, int saCount, CameraSequence.RawDataInfo info) {
        ByteBuffer saParam = SaUtil.getSaParam(256);
        if (SaUtil.isAVIP()) {
            displayImageDataSet();
        }
        int cmd = saCount == 0 ? 6 : 4;
        Log.d("SAE", "cmd=" + cmd);
        saParam.putInt(cmd);
        if (SaUtil.isAVIP()) {
            saParam.putInt(SaUtil.convArmAddr2AxiAddr(avip_raw));
            Log.d("Physical Memory", "Physical Memory Address + " + SaUtil.convArmAddr2AxiAddr(avip_raw));
        } else {
            int raw_address = SaUtil.getMemoryAddressAxi(raw);
            String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
            Log.d("BFNR Check", "MODEL NAME = " + ModelName);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
            boolean longExposure_flg = ((CameraEx.ParametersModifier) params.second).getLongExposureNR();
            Log.d("BFNR Check", "EXPOSURE = " + longExposure_flg);
            CameraEx.ShutterSpeedInfo sinfo = CameraSetting.getInstance().getShutterSpeedInfo();
            float shutterspeed = sinfo.currentShutterSpeed_n / sinfo.currentShutterSpeed_d;
            Log.d("BFNR Check", "SS = " + shutterspeed);
            if (ModelName.equals(DSCRX100M3) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += (info.canvasSizeX + OPB_REGION) * 6;
            } else if (ModelName.equals(ILCE5000) && longExposure_flg && shutterspeed >= FPNR_ENABLED_SHUTTERSPEED) {
                raw_address += info.canvasSizeX * 8;
            } else if (ModelName.equals(DSCHX400V) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += info.canvasSizeX * 6;
            } else if (ModelName.equals(DSCHX60V) && longExposure_flg && shutterspeed >= 8.0f) {
                raw_address += info.canvasSizeX * 6;
            }
            saParam.putInt(raw_address);
        }
        saParam.putInt(SaUtil.getMemoryAddressAxi(saWork0));
        saParam.putInt(saCount);
        saParam.putInt(2);
        saParam.putInt(19088743);
        saParam.putInt(19088743);
        saParam.putInt(19088743);
        if (SaUtil.isAVIP()) {
            saParam.putInt(avip_canvasSizeX);
            saParam.putInt(avip_canvasSizeY);
            saParam.putInt(avip_marginOffsetX);
            saParam.putInt(avip_marginOffsetY);
            saParam.putInt(avip_marginSizeX);
            saParam.putInt(avip_marginSizeY);
            saParam.putInt(avip_validOffsetX);
            saParam.putInt(avip_validOffsetY);
            saParam.putInt(avip_validSizeX);
            saParam.putInt(avip_validSizeY);
            saParam.putInt(avip_firstColor);
            saParam.putInt(avip_clpR);
            saParam.putInt(avip_clpGr);
            saParam.putInt(avip_clpGb);
            saParam.putInt(avip_clpB);
            saParam.putInt(avip_clpOfst);
            saParam.putInt(avip_wbR);
            saParam.putInt(avip_wbB);
            saParam.putInt(avip_ddithRstmod);
            saParam.putInt(avip_ddithOn);
            saParam.putInt(avip_expBit);
            saParam.putInt(avip_decompMode);
            saParam.putInt(avip_dth0);
            saParam.putInt(avip_dth1);
            saParam.putInt(avip_dth2);
            saParam.putInt(avip_dth3);
            saParam.putInt(avip_dp0);
            saParam.putInt(avip_dp1);
            saParam.putInt(avip_dp2);
            saParam.putInt(avip_dp3);
            saParam.putInt(avip_dithRstmod);
            saParam.putInt(avip_dithOn);
            saParam.putInt(avip_rndBit);
            saParam.putInt(avip_compMode);
            saParam.putInt(avip_th0);
            saParam.putInt(avip_th1);
            saParam.putInt(avip_th2);
            saParam.putInt(avip_th3);
            saParam.putInt(avip_p0);
            saParam.putInt(avip_p1);
            saParam.putInt(avip_p2);
            saParam.putInt(avip_p3);
        } else {
            saParam.putInt(info.canvasSizeX);
            saParam.putInt(info.canvasSizeY);
            saParam.putInt(info.marginOffsetX);
            saParam.putInt(info.marginOffsetY);
            saParam.putInt(info.marginSizeX);
            saParam.putInt(info.marginSizeY);
            saParam.putInt(info.validOffsetX);
            saParam.putInt(info.validOffsetY);
            saParam.putInt(info.validSizeX);
            saParam.putInt(info.validSizeY);
            saParam.putInt(info.firstColor);
            saParam.putInt(info.clpR);
            saParam.putInt(info.clpGr);
            saParam.putInt(info.clpGb);
            saParam.putInt(info.clpB);
            saParam.putInt(info.clpOfst);
            saParam.putInt(info.wbR);
            saParam.putInt(info.wbB);
            saParam.putInt(info.ddithRstmod);
            saParam.putInt(info.ddithOn);
            saParam.putInt(info.expBit);
            saParam.putInt(info.decompMode);
            saParam.putInt(info.dth0);
            saParam.putInt(info.dth1);
            saParam.putInt(info.dth2);
            saParam.putInt(info.dth3);
            saParam.putInt(info.dp0);
            saParam.putInt(info.dp1);
            saParam.putInt(info.dp2);
            saParam.putInt(info.dp3);
            saParam.putInt(info.dithRstmod);
            saParam.putInt(info.dithOn);
            saParam.putInt(info.rndBit);
            saParam.putInt(info.compMode);
            saParam.putInt(info.th0);
            saParam.putInt(info.th1);
            saParam.putInt(info.th2);
            saParam.putInt(info.th3);
            saParam.putInt(info.p0);
            saParam.putInt(info.p1);
            saParam.putInt(info.p2);
            saParam.putInt(info.p3);
        }
        saParam.putInt(19088743);
        saParam.putInt(19088743);
        for (int n = 0; n < 12; n++) {
            saParam.putInt(0);
        }
        if (!SaUtil.isAVIP()) {
            Log.i("RAWINFO", "info.canvasSizeX   = " + info.canvasSizeX);
            Log.i("RAWINFO", "info.canvasSizeY   = " + info.canvasSizeY);
            Log.i("RAWINFO", "info.marginOffsetX = " + info.marginOffsetX);
            Log.i("RAWINFO", "info.marginOffsetY = " + info.marginOffsetY);
            Log.i("RAWINFO", "info.marginSizeX   = " + info.marginSizeX);
            Log.i("RAWINFO", "info.marginSizeY   = " + info.marginSizeY);
            Log.i("RAWINFO", "info.validOffsetX  = " + info.validOffsetX);
            Log.i("RAWINFO", "info.validOffsetY  = " + info.validOffsetY);
            Log.i("RAWINFO", "info.validSizeX    = " + info.validSizeX);
            Log.i("RAWINFO", "info.validSizeY    = " + info.validSizeY);
            Log.i("RAWINFO", "info.firstColor    = " + info.firstColor);
            Log.i("RAWINFO", "info.clpR  \t     = " + info.clpR);
            Log.i("RAWINFO", "info.clpGr         = " + info.clpGr);
            Log.i("RAWINFO", "info.clpGb         = " + info.clpGb);
            Log.i("RAWINFO", "info.clpB          = " + info.clpB);
            Log.i("RAWINFO", "info.clpOfst       = " + info.clpOfst);
            Log.i("RAWINFO", "info.wbR           = " + info.wbR);
            Log.i("RAWINFO", "info.wbB           = " + info.wbB);
            Log.i("RAWINFO", "info.ddithRstmod   = " + info.ddithRstmod);
            Log.i("RAWINFO", "info.ddithOn       = " + info.ddithOn);
            Log.i("RAWINFO", "info.expBit        = " + info.expBit);
            Log.i("RAWINFO", "info.decompMode    = " + info.decompMode);
        }
        return saParam;
    }

    private static void enableGraphicsOutput() {
        Log.d("CAPTUREPROCESS", "  - enableGraphicsOutput()");
        DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public synchronized void execute(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d("SAE", "SaRawComp#execute(" + this.mCompositeCount + LogHelper.MSG_CLOSE_BRACKET);
        CameraSequence.RawDataInfo info = null;
        if (!SaUtil.isAVIP()) {
            info = raw.getRawDataInfo();
        }
        ByteBuffer saParam = getSaParamNew(raw, this.mSaWork0, this.mCompositeCount, info);
        ByteBuffer mboxParam = SaUtil.getMboxParam(this.mDsp);
        DeviceMemory[] deviceMemoryArr = {raw, this.mSaWork0, this.mSaWork1};
        long satime = System.nanoTime();
        try {
            SaUtil.executeSA(this.mDsp, mboxParam, saParam, deviceMemoryArr);
            Log.d("SAE", "SaRawComp#execute() done");
        } catch (Exception e) {
            Log.e("SaUtil", "SaUtil.executeSA( mDsp, mboxParam, saParam, dmlist ) failed!!");
            e.printStackTrace();
        }
        Log.d("SATIME", "t=" + ((System.nanoTime() - satime) / 1000000));
        deviceMemoryArr[2] = 0;
        deviceMemoryArr[1] = 0;
        deviceMemoryArr[0] = 0;
        this.mCompositeCount++;
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
        Log.d("SaRawComp", "Max Picture Size : " + (maxSize * 2));
        return maxSize * 2;
    }

    public void setImageDataSet(SmoothReflectionCompositProcess.ImageDataSet imData) {
        avip_raw = imData.dmm_info_raw0;
        avip_canvasSizeX = imData.info_canvasSizeX;
        avip_canvasSizeY = imData.info_canvasSizeY;
        avip_marginOffsetX = imData.info_marginOffsetX;
        avip_marginOffsetY = imData.info_marginOffsetY;
        avip_marginSizeX = imData.info_marginSizeX;
        avip_marginSizeY = imData.info_marginSizeY;
        avip_validOffsetX = imData.info_validOffsetX;
        avip_validOffsetY = imData.info_validOffsetY;
        avip_validSizeX = imData.info_validSizeX;
        avip_validSizeY = imData.info_validSizeY;
        avip_clpGr = imData.info_clpGr;
        avip_clpGb = imData.info_clpGb;
        avip_clpR = imData.info_clpR;
        avip_clpB = imData.info_clpB;
        avip_clpOfst = imData.info_clpOfst;
        avip_wbR = imData.info_wbR;
        avip_wbB = imData.info_wbB;
        avip_ddithRstmod = imData.ddithRstmod;
        avip_ddithOn = imData.ddithOn;
        avip_expBit = imData.expBit;
        avip_decompMode = imData.decompMode;
        avip_dth0 = imData.dth0;
        avip_dth1 = imData.dth1;
        avip_dth2 = imData.dth2;
        avip_dth3 = imData.dth3;
        avip_dp0 = imData.dp0;
        avip_dp1 = imData.dp1;
        avip_dp2 = imData.dp2;
        avip_dp3 = imData.dp3;
        avip_dithRstmod = imData.dithRstmod;
        avip_dithOn = imData.dithOn;
        avip_rndBit = imData.rndBit;
        avip_compMode = imData.compMode;
        avip_th0 = imData.th0;
        avip_th1 = imData.th1;
        avip_th2 = imData.th2;
        avip_th3 = imData.th3;
        avip_p0 = imData.p0;
        avip_p1 = imData.p1;
        avip_p2 = imData.p2;
        avip_p3 = imData.p3;
    }

    private static void displayImageDataSet() {
        Log.d("ImageDataSet", "avip_raw = " + String.format("%2x", Integer.valueOf(avip_raw)));
        Log.d("ImageDataSet", "avip_canvasSizeX = " + avip_canvasSizeX);
        Log.d("ImageDataSet", "avip_canvasSizeY = " + avip_canvasSizeY);
        Log.d("ImageDataSet", "avip_marginOffsetX = " + avip_marginOffsetX);
        Log.d("ImageDataSet", "avip_marginOffsetY = " + avip_marginOffsetY);
        Log.d("ImageDataSet", "avip_marginSizeX = " + avip_marginSizeX);
        Log.d("ImageDataSet", "avip_marginSizeY = " + avip_marginSizeY);
        Log.d("ImageDataSet", "avip_validOffsetX = " + avip_validOffsetX);
        Log.d("ImageDataSet", "avip_validOffsetY = " + avip_validOffsetY);
        Log.d("ImageDataSet", "avip_validSizeX = " + avip_validSizeX);
        Log.d("ImageDataSet", "avip_validSizeY = " + avip_validSizeY);
        Log.d("ImageDataSet", "avip_firstColor = " + avip_firstColor);
        Log.d("ImageDataSet", "avip_clpGr = " + avip_clpGr);
        Log.d("ImageDataSet", "avip_clpGb = " + avip_clpGb);
        Log.d("ImageDataSet", "avip_clpR = " + avip_clpR);
        Log.d("ImageDataSet", "avip_clpB = " + avip_clpB);
        Log.d("ImageDataSet", "avip_clpOfst = " + avip_clpOfst);
        Log.d("ImageDataSet", "avip_wbR = " + avip_wbR);
        Log.d("ImageDataSet", "avip_wbB = " + avip_wbB);
        Log.d("ImageDataSet", "avip_ddithRstmod = " + avip_ddithRstmod);
        Log.d("ImageDataSet", "avip_ddithOn = " + avip_ddithOn);
        Log.d("ImageDataSet", "avip_expBit = " + avip_expBit);
        Log.d("ImageDataSet", "avip_decompMode = " + avip_decompMode);
        Log.d("ImageDataSet", "avip_dth0 = " + String.format("%2x", Integer.valueOf(avip_dth0)));
        Log.d("ImageDataSet", "avip_dth1 = " + String.format("%2x", Integer.valueOf(avip_dth1)));
        Log.d("ImageDataSet", "avip_dth2 = " + String.format("%2x", Integer.valueOf(avip_dth2)));
        Log.d("ImageDataSet", "avip_dth3 = " + String.format("%2x", Integer.valueOf(avip_dth3)));
        Log.d("ImageDataSet", "avip_dp0 = " + String.format("%2x", Integer.valueOf(avip_dp0)));
        Log.d("ImageDataSet", "avip_dp1 = " + String.format("%2x", Integer.valueOf(avip_dp1)));
        Log.d("ImageDataSet", "avip_dp2 = " + String.format("%2x", Integer.valueOf(avip_dp2)));
        Log.d("ImageDataSet", "avip_dp3 = " + String.format("%2x", Integer.valueOf(avip_dp3)));
        Log.d("ImageDataSet", "avip_dithRstmod = " + avip_dithRstmod);
        Log.d("ImageDataSet", "avip_dithOn = " + avip_dithOn);
        Log.d("ImageDataSet", "avip_rndBit = " + avip_rndBit);
        Log.d("ImageDataSet", "avip_compMode = " + avip_compMode);
        Log.d("ImageDataSet", "avip_th0 = " + String.format("%2x", Integer.valueOf(avip_th0)));
        Log.d("ImageDataSet", "avip_th1 = " + String.format("%2x", Integer.valueOf(avip_th1)));
        Log.d("ImageDataSet", "avip_th2 = " + String.format("%2x", Integer.valueOf(avip_th2)));
        Log.d("ImageDataSet", "avip_th3 = " + String.format("%2x", Integer.valueOf(avip_th3)));
        Log.d("ImageDataSet", "avip_p0 = " + String.format("%2x", Integer.valueOf(avip_p0)));
        Log.d("ImageDataSet", "avip_p1 = " + String.format("%2x", Integer.valueOf(avip_p1)));
        Log.d("ImageDataSet", "avip_p2 = " + String.format("%2x", Integer.valueOf(avip_p2)));
        Log.d("ImageDataSet", "avip_p3 = " + String.format("%2x", Integer.valueOf(avip_p3)));
    }
}
