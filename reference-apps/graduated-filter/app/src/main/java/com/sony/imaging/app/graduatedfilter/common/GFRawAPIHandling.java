package com.sony.imaging.app.graduatedfilter.common;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class GFRawAPIHandling {
    private static final int AB_RAW_OFFSET_LINES = 8;
    private static final int ActiveListSize = 22;
    private static final int AddressSize = 4;
    private static final String DSCHX400V = "DSC-HX400V";
    private static final String DSCHX60V = "DSC-HX60V";
    private static final String DSCRX100M3 = "DSC-RX100M3";
    private static final int EmptyListSize = 22;
    private static final double FPNR_ENABLED_SHUTTERSPEED = 1.0d;
    private static final int FQ_RAW_OFFSET_LINES = 6;
    private static final String ILCE5000 = "ILCE-5000";
    private static final int NFBR_ENABLED_SHUTTERSPEED = 8;
    private static final int OPB_REGION = 64;
    private static final String PROP_MODEL_NAME = "model.name";
    private static final int ZNS_ZNGK_RAW_OFFSET_LINES = 6;
    private static final String TAG = AppLog.getClassName();
    private static int mMemAddr = -1011909968;
    private static int[] OriginalList = new int[22];
    private static int ImageParamCaptureShareOffset = 14568;
    private static int dmm_info_raw_unit_type_Offset = 15560;
    private static int dmm_info_raw_count_Offset = 15564;
    private static int dmm_info_raw0_Offset = 15568;
    private static int unitHandle_Offset = 14584;
    private static int unitDataOffset_Offset = 15556;
    private static int info_canvasSizeX_Offset = 504;
    private static int info_canvasSizeY_Offset = 508;
    private static int info_marginOffsetX_Offset = 512;
    private static int info_marginOffsetY_Offset = AppRoot.USER_KEYCODE.S1_ON;
    private static int info_marginSizeX_Offset = AppRoot.USER_KEYCODE.FN;
    private static int info_marginSizeY_Offset = AppRoot.USER_KEYCODE.DIAL1_STATUS;
    private static int info_validOffsetX_Offset = AppRoot.USER_KEYCODE.DIAL2_RIGHT;
    private static int info_validOffsetY_Offset = AppRoot.USER_KEYCODE.AEL;
    private static int info_validSizeX_Offset = AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO;
    private static int info_validSizeY_Offset = AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL;
    private static int info_clpGr_Offset = 8802;
    private static int info_clpGb_Offset = 8804;
    private static int info_clpR_Offset = 8800;
    private static int info_clpB_Offset = 8806;
    private static int info_clpOfst_Offset = 8646;
    private static int info_wbR_Offset = 7556;
    private static int info_wbB_Offset = 7558;
    public static Boolean isLastShooting = false;

    public static void setPhicalAddress() {
        String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
        Log.d("MODEL NAME", "MODEL NAME = " + ModelName);
        if (ModelName.equals("NEX-6")) {
            mMemAddr = -1011909968;
        } else if (ModelName.equals("NEX-5R")) {
            mMemAddr = -1011893712;
        } else if (ModelName.equals("NEX-5T")) {
            mMemAddr = -1011886800;
        }
    }

    public static GFRawDataInfo getGFRawDataInfo(CameraSequence.RawData raw) {
        if (!SaUtil.isAVIP()) {
            String version = ScalarProperties.getString("version.platform");
            int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
            int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
            boolean isSupportedUnitDataOffset = pfMajorVersion >= 3 || (pfMajorVersion == 2 && pfAPIVersion >= 14);
            String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
            boolean islongExposure = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getLongExposureNR();
            CameraEx.ShutterSpeedInfo sinfo = CameraSetting.getInstance().getShutterSpeedInfo();
            float shutterspeed = sinfo.currentShutterSpeed_n / sinfo.currentShutterSpeed_d;
            Log.d(TAG, "BFNR Check: MODEL NAME = " + ModelName + ", EXPOSURE = " + islongExposure + ", SS = " + shutterspeed);
            CameraSequence.RawDataInfo rawInfo = raw.getRawDataInfo();
            AppLog.info(TAG, "getRawDataInfo().wbB : " + rawInfo.wbB);
            AppLog.info(TAG, "getRawDataInfo().wbR : " + rawInfo.wbR);
            int raw_address = SaUtil.getMemoryAddressAxi(raw);
            if (ModelName.equals(DSCRX100M3) && islongExposure && shutterspeed >= 8.0f) {
                raw_address += (rawInfo.canvasSizeX + OPB_REGION) * 6;
                Log.d(TAG, "BFNR(true) : add OPB_REGION and FQ_RAW_OFFSET_LINES");
            } else if (ModelName.equals(ILCE5000) && islongExposure && shutterspeed >= FPNR_ENABLED_SHUTTERSPEED) {
                raw_address += rawInfo.canvasSizeX * 8;
                Log.d(TAG, "BFNR(true) : add AB_RAW_OFFSET_LINES");
            } else if (ModelName.equals(DSCHX400V) && islongExposure && shutterspeed >= 8.0f) {
                raw_address += rawInfo.canvasSizeX * 6;
                Log.d(TAG, "BFNR(true) : add ZNS_ZNGK_RAW_OFFSET_LINES");
            } else if (ModelName.equals(DSCHX60V) && islongExposure && shutterspeed >= 8.0f) {
                raw_address += rawInfo.canvasSizeX * 6;
                Log.d(TAG, "BFNR(true) : add ZNS_ZNGK_RAW_OFFSET_LINES");
            } else if (isSupportedUnitDataOffset) {
                Log.d(TAG, "isSupportedUnitDataOffset: " + isSupportedUnitDataOffset + ", unitDataOffset = " + rawInfo.unitDataOffset);
                raw_address += SaUtil.convArmAddr2AxiAddr(rawInfo.unitDataOffset);
            }
            GFRawDataInfo info = new GFRawDataInfo(rawInfo, isSupportedUnitDataOffset);
            info.axiRawAddr = raw_address;
            return info;
        }
        int next_address = mMemAddr;
        byte[] AddressBuf = new byte[4];
        int[] CurrentList = new int[22];
        int[] CurrentActive = new int[22];
        byte[] Byte4Buf = new byte[4];
        byte[] Byte2Buf = new byte[2];
        int n = 0;
        int ActiveCount = 0;
        int MainAddress = 0;
        boolean MatchingFlg = false;
        MemoryUtil mUtil = new MemoryUtil();
        while (next_address != 0) {
            mUtil.memoryCopyDiademToApplication(next_address, AddressBuf, 4);
            next_address = LittleEndian4Int(AddressBuf);
            CurrentList[n] = next_address;
            n++;
        }
        for (int i = 0; OriginalList[i] != 0; i++) {
            for (int j = 0; CurrentList[j] != 0; j++) {
                if (OriginalList[i] == CurrentList[j]) {
                    MatchingFlg = true;
                }
            }
            if (!MatchingFlg) {
                CurrentActive[ActiveCount] = OriginalList[i];
                ActiveCount++;
            } else {
                MatchingFlg = false;
            }
        }
        for (int m = 0; m < ActiveCount; m++) {
            int temp_address = CurrentActive[m] + ImageParamCaptureShareOffset;
            mUtil.memoryCopyDiademToApplication(temp_address, AddressBuf, 4);
            int temp_address2 = LittleEndian4Int(AddressBuf);
            if (temp_address2 != 0) {
                switch (ActiveCount) {
                    case 1:
                    case 2:
                        MainAddress = CurrentActive[m];
                        break;
                    case 3:
                    case 4:
                        if (CurrentActive[m] != 0) {
                            MainAddress = CurrentActive[m];
                            break;
                        } else {
                            break;
                        }
                    case 5:
                    case 6:
                        if (CurrentActive[m] != 0 && CurrentActive[m] != 0) {
                            MainAddress = CurrentActive[m];
                            break;
                        }
                        break;
                    default:
                        Log.e("ActiveImageInstanceError", "The number of ImageInstance is unexpected.(more than 7. the expected number is 1 to 6.) ActiveCount = " + ActiveCount);
                        break;
                }
            }
        }
        GFRawDataInfo info2 = new GFRawDataInfo();
        mUtil.memoryCopyDiademToApplication(dmm_info_raw_unit_type_Offset + MainAddress, Byte4Buf, 4);
        info2.dmm_info_raw_unit_type = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(dmm_info_raw_count_Offset + MainAddress, Byte4Buf, 4);
        info2.dmm_info_raw_count = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(unitHandle_Offset + MainAddress, Byte4Buf, 4);
        info2.unitHandle = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(unitDataOffset_Offset + MainAddress, Byte4Buf, 4);
        info2.unitDataOffset = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(dmm_info_raw0_Offset + MainAddress, Byte4Buf, 4);
        info2.dmm_info_raw0 = LittleEndian4Int(Byte4Buf) + info2.unitDataOffset;
        info2.axiRawAddr = SaUtil.convArmAddr2AxiAddr(info2.dmm_info_raw0);
        mUtil.memoryCopyDiademToApplication(info_canvasSizeX_Offset + MainAddress, Byte4Buf, 4);
        info2.canvasSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_canvasSizeY_Offset + MainAddress, Byte4Buf, 4);
        info2.canvasSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_marginOffsetX_Offset + MainAddress, Byte4Buf, 4);
        info2.marginOffsetX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_marginOffsetY_Offset + MainAddress, Byte4Buf, 4);
        info2.marginOffsetY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_marginSizeX_Offset + MainAddress, Byte4Buf, 4);
        info2.marginSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_marginSizeY_Offset + MainAddress, Byte4Buf, 4);
        info2.marginSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_validOffsetX_Offset + MainAddress, Byte4Buf, 4);
        info2.validOffsetX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_validOffsetY_Offset + MainAddress, Byte4Buf, 4);
        info2.validOffsetY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_validSizeX_Offset + MainAddress, Byte4Buf, 4);
        info2.validSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_validSizeY_Offset + MainAddress, Byte4Buf, 4);
        info2.validSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(info_clpGr_Offset + MainAddress, Byte2Buf, 2);
        info2.clpGr = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_clpGb_Offset + MainAddress, Byte2Buf, 2);
        info2.clpGb = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_clpR_Offset + MainAddress, Byte2Buf, 2);
        info2.clpR = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_clpB_Offset + MainAddress, Byte2Buf, 2);
        info2.clpB = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_clpOfst_Offset + MainAddress, Byte2Buf, 2);
        info2.clpOfst = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_wbR_Offset + MainAddress, Byte2Buf, 2);
        info2.wbR = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(info_wbB_Offset + MainAddress, Byte2Buf, 2);
        info2.wbB = LittleEndian2Int(Byte2Buf);
        return info2;
    }

    public static void getEmptyImageInstance() {
        int next_address = mMemAddr;
        byte[] AddressBuf = new byte[4];
        MemoryUtil mUtil = new MemoryUtil();
        int i = 0;
        while (next_address != 0) {
            mUtil.memoryCopyDiademToApplication(next_address, AddressBuf, 4);
            next_address = LittleEndian4Int(AddressBuf);
            OriginalList[i] = next_address;
            i++;
        }
    }

    private static int LittleEndian4Int(byte[] buf) {
        return (buf[0] & 255) | (65280 & (buf[1] << 8)) | (16711680 & (buf[2] << 16)) | ((-16777216) & (buf[3] << 24));
    }

    private static int LittleEndian2Int(byte[] buf) {
        return (buf[0] & 255) | (65280 & (buf[1] << 8));
    }
}
