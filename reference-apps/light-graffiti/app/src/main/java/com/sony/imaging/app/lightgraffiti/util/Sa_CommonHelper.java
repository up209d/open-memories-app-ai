package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/* loaded from: classes.dex */
public class Sa_CommonHelper {
    private static final int LSI_TYPE_AVIP = 1;
    private static final int LSI_TYPE_MUSASHI_KOJIRO = 2;
    static String TAG = Sa_CommonHelper.class.getSimpleName();
    static boolean DEBUGPRINT = true;

    public static int convArmAddr2AxiAddr(int address) {
        int mask;
        if (isAVIP()) {
            mask = Integer.MAX_VALUE;
        } else {
            mask = 1073741823;
        }
        return address & mask;
    }

    public static int getAXIAddr(int addr) {
        return convArmAddr2AxiAddr(addr);
    }

    public static boolean isAVIP() {
        int lsiType = getLsiType();
        return lsiType == 1;
    }

    private static int getLsiType() {
        String pfVer = ScalarProperties.getString("version.platform");
        Log.d("SaZm0_RectCopy_FillColor_ImageFilter", "PROP_VERSION_PLATFORM = " + pfVer);
        float lsiType = Float.parseFloat(pfVer);
        Log.d("SaZm0_RectCopy_FillColor_ImageFilter", "lsiType = " + ((int) lsiType));
        return (int) lsiType;
    }

    public static int read_sa_mbox0_a(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 12);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox0_a: " + result);
        }
        return result;
    }

    public static int read_sa_mbox1_a(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 16);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox1_a: " + result);
        }
        return result;
    }

    public static int read_sapd(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 0);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sapd: " + result);
        }
        return result;
    }

    public static int read_seq_num(byte[] saResult) throws Exception {
        int result = readByteFromByteArray(saResult, 4);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_seq_num: " + result);
        }
        return result;
    }

    public static int read_errCode(byte[] saResult) throws Exception {
        int result = readByteFromByteArray(saResult, 5);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sapd: " + result);
        }
        return result;
    }

    public static int read_errStatus(byte[] saResult) throws Exception {
        int result = readByteFromByteArray(saResult, 6);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sapd: " + result);
        }
        return result;
    }

    public static int read_intfct(byte[] saResult) throws Exception {
        int result = readByteFromByteArray(saResult, 7);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sapd: " + result);
        }
        return result;
    }

    public static int read_sa_mbox2_a(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 20);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox1_a: " + result);
        }
        return result;
    }

    public static int read_sa_mbox3_a(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 24);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox1_a: " + result);
        }
        return result;
    }

    public static int read_sa_mbox0_b(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 32);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox0_b: " + result);
        }
        return result;
    }

    public static int read_sa_mbox1_b(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 36);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox1_b: " + result);
        }
        return result;
    }

    public static int read_sa_mbox2_b(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 40);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox2_b: " + result);
        }
        return result;
    }

    public static int read_sa_mbox3_b(byte[] saResult) throws Exception {
        int result = readIntFromByteArray(saResult, 44);
        if (DEBUGPRINT) {
            Log.i(TAG, "read_sa_mbox3_b: " + result);
        }
        return result;
    }

    public static int readIntFromByteArray(byte[] ba, int ofst) throws Exception {
        if (ba == null) {
            Log.e(TAG, "byte[] ba is null!!");
            throw new Exception("byte[] ba is null!!");
        }
        if (ba.length < ofst + 3) {
            Log.e(TAG, "too big offset !!");
            throw new Exception("too big offset !!");
        }
        int result = (ba[ofst + 0] & 255) << 0;
        return result + ((ba[ofst + 1] & 255) << 8) + ((ba[ofst + 2] & 255) << 16) + ((ba[ofst + 3] & 255) << 24);
    }

    public static int readByteFromByteArray(byte[] ba, int ofst) throws Exception {
        if (ba == null) {
            Log.e(TAG, "byte[] ba is null!!");
            throw new Exception("byte[] ba is null!!");
        }
        if (ba.length < ofst + 3) {
            Log.e(TAG, "too big offset !!");
            throw new Exception("too big offset !!");
        }
        int result = (ba[ofst] & 255) << 0;
        return result;
    }

    public static boolean writeValueToByteArray(byte[] ba, int ofst, short value, boolean baIsBigEndian) {
        if (ba == null) {
            Log.e(TAG, "byte[] ba is null!!");
            return false;
        }
        if (ba.length < ofst + 1) {
            Log.e(TAG, "too big offset !!");
            return false;
        }
        if (baIsBigEndian) {
            ba[ofst + 0] = (byte) ((value >> 8) & 255);
            ba[ofst + 1] = (byte) ((value >> 0) & 255);
        } else {
            ba[ofst + 0] = (byte) ((value >> 0) & 255);
            ba[ofst + 1] = (byte) ((value >> 8) & 255);
        }
        return true;
    }

    public static boolean writeValueToByteArray(byte[] ba, int ofst, int value, boolean baIsBigEndian) {
        if (ba == null) {
            Log.e(TAG, "byte[] ba is null!!");
            return false;
        }
        if (ba.length < ofst + 3) {
            Log.e(TAG, "too big offset !!");
            return false;
        }
        if (baIsBigEndian) {
            ba[ofst + 0] = (byte) ((value >> 24) & 255);
            ba[ofst + 1] = (byte) ((value >> 16) & 255);
            ba[ofst + 2] = (byte) ((value >> 8) & 255);
            ba[ofst + 3] = (byte) ((value >> 0) & 255);
        } else {
            ba[ofst + 0] = (byte) ((value >> 0) & 255);
            ba[ofst + 1] = (byte) ((value >> 8) & 255);
            ba[ofst + 2] = (byte) ((value >> 16) & 255);
            ba[ofst + 3] = (byte) ((value >> 24) & 255);
        }
        return true;
    }

    public int INT_little_endian_TO_big_endian(int i) {
        return ((i & 255) << 24) + ((65280 & i) << 8) + ((16711680 & i) >> 8) + ((i >> 24) & 255);
    }

    public int readLittleIntFromByteArray(byte[] ba, int ofst) throws Exception {
        if (ba == null) {
            Log.e(TAG, "byte[] ba is null!!");
            throw new Exception("byte[] ba is null!!");
        }
        if (ba.length < ofst + 3) {
            Log.e(TAG, "too big offset !!");
            throw new Exception("too big offset !!");
        }
        int result = (ba[ofst + 0] & 255) << 0;
        return result + ((ba[ofst + 1] & 255) << 8) + ((ba[ofst + 2] & 255) << 16) + ((ba[ofst + 3] & 255) << 24);
    }

    public static int[] getIntArrayFromByteBufferInLitteEndian(byte[] byteArray) {
        IntBuffer intBuf = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }
}
