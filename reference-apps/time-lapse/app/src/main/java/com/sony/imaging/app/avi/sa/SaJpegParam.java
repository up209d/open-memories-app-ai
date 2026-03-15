package com.sony.imaging.app.avi.sa;

import android.util.Log;

/* loaded from: classes.dex */
public class SaJpegParam {
    private static final int GET_ENCODE_SIZE_ERROR_SIZE = 805306368;
    public static final int JPEG_CMD_DEC = 4352;
    public static final int JPEG_CMD_ENC = 5120;
    private static final int OFFSET_ENTRY0_PHYS_AXI_ADDRESS = 292;
    private static final int OFFSET_ENTRY0_SIZE_AND_IO_TYPE = 288;
    private static final int OFFSET_ENTRY1_PHYS_AXI_ADDRESS = 300;
    private static final int OFFSET_ENTRY1_SIZE_AND_IO_TYPE = 296;
    private static final int OFFSET_EXTENT_NUM = 272;
    private static final int OFFSET_EXT_INPUT_ADDRESS = 276;
    private static final int OFFSET_EXT_OUTPUT_ADDRESS = 280;
    public static final int OFFSET_LOG_CODE_LENGTH = 8;
    public static final int PARAM_SIZE_ALLOC = 512;
    private static final int PARAM_SIZE_MIN = 384;
    private static final int P_FIXED_ENTRY_INPUT_SIZE = 81788928;
    private static final int P_FIXED_ENTRY_NUM = 2;
    private static final int P_FIXED_ENTRY_OUTPUT_SIZE = 81788929;
    public static final String SA_PROGRAM_DEC = "SA_PREINSTALLED_PRG:SA_JPEG_DEC";
    public static final String SA_PROGRAM_ENC = "SA_PREINSTALLED_PRG:SA_JPEG_ENC";
    private static final String TAG = "SaJpegMMUParam";

    public static boolean setMMUParam(byte[] param, int inAddr, int outAddr) {
        if (SaUtil.isAVIP()) {
            return true;
        }
        if (param == null) {
            Log.e(TAG, "ERROR!!");
            return false;
        }
        if (param.length < PARAM_SIZE_MIN) {
            Log.e(TAG, "ERROR!!");
            return false;
        }
        SaUtil.writeValueToByteArray(param, OFFSET_EXTENT_NUM, 2, false);
        SaUtil.writeValueToByteArray(param, OFFSET_EXT_INPUT_ADDRESS, inAddr, false);
        SaUtil.writeValueToByteArray(param, OFFSET_ENTRY0_PHYS_AXI_ADDRESS, inAddr, false);
        SaUtil.writeValueToByteArray(param, OFFSET_ENTRY0_SIZE_AND_IO_TYPE, P_FIXED_ENTRY_INPUT_SIZE, false);
        SaUtil.writeValueToByteArray(param, OFFSET_EXT_OUTPUT_ADDRESS, outAddr, false);
        SaUtil.writeValueToByteArray(param, 300, outAddr, false);
        SaUtil.writeValueToByteArray(param, OFFSET_ENTRY1_SIZE_AND_IO_TYPE, P_FIXED_ENTRY_OUTPUT_SIZE, false);
        return true;
    }

    public static final int getEncodeSize(byte[] mboxBuff) {
        int size;
        try {
            if (SaUtil.isAVIP()) {
                size = SaUtil.read_sa_mbox1_a(mboxBuff);
            } else {
                size = SaUtil.readIntFromByteArray(mboxBuff, 8);
            }
            return size;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get mailbox value.");
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return GET_ENCODE_SIZE_ERROR_SIZE;
        }
    }
}
