package com.sony.imaging.app.graduatedfilter.common;

import android.util.Log;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class SaUtil {
    private static boolean DEBUGPRINT = true;
    private static final int LSI_TYPE_AVIP = 1;
    private static final int LSI_TYPE_MUSASHI_KOJIRO = 2;
    private static final String TAG = "SaUtil";

    public static void dispMemoryAddressArm(DeviceMemory devMem, String str) {
        int addr = getMemoryAddressArm(devMem);
        Log.d(TAG, str + MovieFormatController.Settings.EQUAL + String.format("0x%08x", Integer.valueOf(addr)));
    }

    public static int getMemoryAddressArm(DeviceMemory devMem) {
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        int address = dsp.getPropertyAsInt(devMem, "memory-address");
        dsp.release();
        return address;
    }

    public static int getOptImgHeight(OptimizedImage optImg) {
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        int value = dsp.getPropertyAsInt(optImg, "image-canvas-height");
        dsp.release();
        return value;
    }

    public static int getOptImgWidth(OptimizedImage optImg) {
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        int value = dsp.getPropertyAsInt(optImg, "image-canvas-width");
        dsp.release();
        return value;
    }

    public static int getMemoryAddressAxi(DeviceMemory devMem) {
        return convArmAddr2AxiAddr(getMemoryAddressArm(devMem));
    }

    private static int getLsiType() {
        String pfVer = ScalarProperties.getString("version.platform");
        Log.d(TAG, "PROP_VERSION_PLATFORM = " + pfVer);
        float lsiType = Float.parseFloat(pfVer);
        Log.d(TAG, "lsiType = " + ((int) lsiType));
        return (int) lsiType;
    }

    public static boolean isAVIP() {
        int lsiType = getLsiType();
        return lsiType == 1;
    }

    public static ByteBuffer getSaParam(int size) {
        ByteBuffer param = ByteBuffer.allocateDirect(size);
        param.order(ByteOrder.nativeOrder());
        return param;
    }

    public static ByteBuffer getMboxParam(DSP dsp) {
        ByteBuffer bootParam = ByteBuffer.allocateDirect(60);
        bootParam.order(ByteOrder.nativeOrder());
        bootParam.putInt(dsp.getPropertyAsInt("program-descriptor"));
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.put((byte) 0);
        bootParam.putInt(0);
        bootParam.putInt(0);
        return bootParam;
    }

    static void dumpByteArray(byte[] ba) {
        Log.d("dumpArray", "--start----------------------------------------------------------------------------");
        String str = "";
        for (int n = 0; n < ba.length; n++) {
            str = str + String.format("%02x ", Byte.valueOf(ba[n]));
            if (n % 16 == 15 || n == ba.length - 1) {
                Log.d("dumpArray", str);
                str = "";
            }
        }
        Log.d("dumpArray", "-- end ----------------------------------------------------------------------------");
    }

    /* JADX WARN: Finally extract failed */
    public static int executeSA(DSP dsp, ByteBuffer mboxParam, ByteBuffer saParam, DeviceMemory[] dm) throws Exception {
        int count;
        if (dsp == null || mboxParam == null) {
            Log.e(TAG, "executeSA() failed!!");
            throw new Exception("There is null argument!!");
        }
        DeviceBuffer db0 = null;
        DeviceBuffer db1 = null;
        try {
            try {
                db0 = dsp.createBuffer(mboxParam.capacity());
                mboxParam.rewind();
                db0.write(mboxParam);
                db1 = dsp.createBuffer(saParam.capacity());
                saParam.rewind();
                db1.write(saParam);
                int count2 = 0 + 1;
                dsp.setArg(0, db0);
                int count3 = count2 + 1;
                dsp.setArg(count2, db1);
                if (dm != null) {
                    int n = 0;
                    int count4 = count3;
                    while (n < dm.length) {
                        if (dm[n] != null) {
                            Log.d("DM", "DeviceMemory = " + dm[n]);
                            count = count4 + 1;
                            dsp.setArg(count4, dm[n]);
                        } else {
                            count = count4;
                        }
                        n++;
                        count4 = count;
                    }
                }
                Log.i(TAG, "dsp.execute()");
                if (!dsp.execute()) {
                    Log.e(TAG, "dsp.execute() = false");
                }
                Log.i(TAG, "dsp.execute() done");
                if (db1 != null) {
                    db1.release();
                }
                if (db0 != null) {
                    db0.release();
                }
                return 0;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                throw new Exception("There is error during dsp.execute() process!!");
            }
        } catch (Throwable th) {
            if (db1 != null) {
                db1.release();
            }
            if (db0 != null) {
                db0.release();
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    public static int executeSA(DSP dsp, ByteBuffer mboxParam, ByteBuffer saParam, DeviceMemory src, DeviceMemory dst) throws Exception {
        int count;
        if (dsp == null || mboxParam == null || src == null) {
            Log.e(TAG, "executeSA() failed!!");
            throw new Exception("There is null argument!!");
        }
        DeviceBuffer db0 = null;
        DeviceBuffer db1 = null;
        try {
            try {
                db0 = dsp.createBuffer(mboxParam.capacity());
                mboxParam.rewind();
                db0.write(mboxParam);
                db1 = dsp.createBuffer(saParam.capacity());
                saParam.rewind();
                db1.write(saParam);
                int count2 = 0 + 1;
                dsp.setArg(0, db0);
                int count3 = count2 + 1;
                dsp.setArg(count2, db1);
                if (src != null) {
                    count = count3 + 1;
                    dsp.setArg(count3, src);
                } else {
                    count = count3;
                }
                if (dst != null) {
                    int i = count + 1;
                    dsp.setArg(count, dst);
                }
                Log.i("CHECK_MMU_DEFECT", "dsp.execute()");
                if (!dsp.execute()) {
                    Log.e("CHECK_MMU_DEFECT", "dsp.execute() = false");
                }
                Log.i("CHECK_MMU_DEFECT", "dsp.execute() done");
                db0.read(mboxParam);
                byte[] tmp = new byte[mboxParam.capacity()];
                mboxParam.get(tmp);
                if (db1 != null) {
                    db1.release();
                }
                if (db0 != null) {
                    db0.release();
                }
                return 0;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                throw new Exception("There is error during dsp.execute() process!!");
            }
        } catch (Throwable th) {
            if (db1 != null) {
                db1.release();
            }
            if (db0 != null) {
                db0.release();
            }
            throw th;
        }
    }

    public static int executeSA(DSP dsp, ByteBuffer bootParam, DeviceMemory src, DeviceMemory dst) throws Exception {
        if (dsp == null || bootParam == null || src == null || dst == null) {
            Log.e(TAG, "executeSA() failed!!");
            throw new Exception("There is null argument!!");
        }
        DeviceBuffer deviceBootParam = null;
        try {
            try {
                deviceBootParam = dsp.createBuffer(bootParam.capacity());
                bootParam.rewind();
                deviceBootParam.write(bootParam);
                dsp.setArg(0, deviceBootParam);
                dsp.setArg(1, src);
                dsp.setArg(2, dst);
                Log.i(TAG, "dsp.execute()");
                if (!dsp.execute()) {
                    Log.e(TAG, "dsp.execute() = false");
                }
                Log.i(TAG, "dsp.execute()\u3000done.");
                deviceBootParam.release();
                return 0;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                throw new Exception("There is error during dsp.execute() process!!");
            }
        } finally {
            if (deviceBootParam != null) {
                deviceBootParam.release();
            }
        }
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
            ba[ofst + 0] = (byte) ((value >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 1] = (byte) ((value >> 0) & BatteryIcon.BATTERY_STATUS_CHARGING);
        } else {
            ba[ofst + 0] = (byte) ((value >> 0) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 1] = (byte) ((value >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
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
            ba[ofst + 0] = (byte) ((value >> 24) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 1] = (byte) ((value >> 16) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 2] = (byte) ((value >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 3] = (byte) ((value >> 0) & BatteryIcon.BATTERY_STATUS_CHARGING);
        } else {
            ba[ofst + 0] = (byte) ((value >> 0) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 1] = (byte) ((value >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 2] = (byte) ((value >> 16) & BatteryIcon.BATTERY_STATUS_CHARGING);
            ba[ofst + 3] = (byte) ((value >> 24) & BatteryIcon.BATTERY_STATUS_CHARGING);
        }
        return true;
    }

    public static int convArmAddr2AxiAddr(int address) {
        int mask;
        if (isAVIP()) {
            mask = Integer.MAX_VALUE;
        } else {
            mask = 1073741823;
        }
        return address & mask;
    }

    public static int convArmAddr2AxiAddr(long address) {
        long mask;
        if (isAVIP()) {
            mask = 2147483647L;
        } else {
            mask = 1073741823;
        }
        return (int) (address & mask);
    }

    public static int INT_little_endian_TO_big_endian(int i) {
        return ((i & BatteryIcon.BATTERY_STATUS_CHARGING) << 24) + ((65280 & i) << 8) + ((16711680 & i) >> 8) + ((i >> 24) & BatteryIcon.BATTERY_STATUS_CHARGING);
    }

    public static int readLittleIntFromByteArray(byte[] ba, int ofst) throws Exception {
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
}
