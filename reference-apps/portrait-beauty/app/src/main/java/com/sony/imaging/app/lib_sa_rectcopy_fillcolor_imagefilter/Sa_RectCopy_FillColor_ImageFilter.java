package com.sony.imaging.app.lib_sa_rectcopy_fillcolor_imagefilter;

import android.util.Log;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class Sa_RectCopy_FillColor_ImageFilter {
    private static final int LSI_TYPE_AVIP = 1;
    private static final int LSI_TYPE_MUSASHI_KOJIRO = 2;
    private DSP mDsp;
    private DeviceBuffer sa_debug_msg_buffer;
    private ByteBuffer sa_debug_msg_buffer_bb;
    private DeviceBuffer sa_param;
    private ByteBuffer sa_param_buf;
    private static final String TAG = Sa_RectCopy_FillColor_ImageFilter.class.getSimpleName();
    private static boolean DEBUGPRINT = true;
    private DeviceBuffer bootParam = null;
    int sa_debug_msg_buffer_size = 64;
    private ByteBuffer bootParam_buf = null;

    public Sa_RectCopy_FillColor_ImageFilter(String packagename) {
        String filePathAndroid;
        this.sa_param = null;
        this.sa_debug_msg_buffer = null;
        this.sa_param_buf = null;
        this.sa_debug_msg_buffer_bb = null;
        DSP mYuvToRGBDsp = DSP.createProcessor("sony-di-dsp");
        if (isAVIP()) {
            filePathAndroid = "/data/lib/" + packagename + "/lib/libsa_RectCopy_FillColor_avip.so";
        } else {
            filePathAndroid = "/data/lib/" + packagename + "/lib/libsa_RectCopy_FillColor_musashi.so";
        }
        Log.i(TAG, "SA file path: /android" + filePathAndroid + " length:" + ("/android" + filePathAndroid).length());
        String filePath = "/android" + filePathAndroid;
        if (filePath.length() > 95) {
            Log.e("TAG", "★ SA file name is too long.  SA file path should be no more than 95.  ");
        }
        if (!new File(filePathAndroid).exists()) {
            Log.e("TAG", "★ SA File not exist.  ");
        }
        mYuvToRGBDsp.setProgram(filePath);
        this.mDsp = mYuvToRGBDsp;
        this.sa_param = this.mDsp.createBuffer(64);
        this.sa_param_buf = ByteBuffer.allocateDirect(64);
        this.sa_param_buf.order(ByteOrder.nativeOrder());
        this.sa_debug_msg_buffer = this.mDsp.createBuffer(this.sa_debug_msg_buffer_size);
        this.sa_debug_msg_buffer_bb = ByteBuffer.allocateDirect(this.sa_debug_msg_buffer_size);
        this.sa_debug_msg_buffer_bb.order(ByteOrder.nativeOrder());
    }

    public synchronized void releaseResources() {
        Log.d(TAG, "====releaseResources +");
        if (this.mDsp != null) {
            if (this.bootParam != null) {
                this.bootParam.release();
                this.bootParam = null;
            }
            if (this.sa_param != null) {
                this.sa_param.release();
                this.sa_param = null;
            }
            if (this.sa_debug_msg_buffer != null) {
                this.sa_debug_msg_buffer.release();
                this.sa_debug_msg_buffer = null;
            }
            this.mDsp.release();
            this.mDsp = null;
        }
        Log.d(TAG, "====releaseResources -");
    }

    public synchronized void rectCopy_setParam(OptimizedImage optImgInput, OptimizedImage optImgOutput, int targetOffsetLeftInput, int targetOffsetTopInput, int targetOffsetLeftOutput, int targetOffsetTopOutput, int targetWidth, int targetHeight) {
        Log.d(TAG, "==== rectCopy");
        int addressInput = this.mDsp.getPropertyAsInt(optImgInput, "memory-address") + (this.mDsp.getPropertyAsInt(optImgInput, "image-canvas-width") * targetOffsetTopInput * 2) + (targetOffsetLeftInput * 2);
        int addressOutput = this.mDsp.getPropertyAsInt(optImgOutput, "memory-address") + (this.mDsp.getPropertyAsInt(optImgOutput, "image-canvas-width") * targetOffsetTopOutput * 2) + (targetOffsetLeftOutput * 2);
        int canvasWidthInput = this.mDsp.getPropertyAsInt(optImgInput, "image-canvas-width");
        int canvasWidthOutput = this.mDsp.getPropertyAsInt(optImgOutput, "image-canvas-width");
        sa_exec_setParam(optImgInput, optImgOutput, addressInput, addressOutput, targetWidth, targetHeight, canvasWidthInput, canvasWidthOutput, 0, 0);
    }

    public synchronized void fillArea_setParam(OptimizedImage optImg, int targetOffsetLeft, int targetOffsetTop, int targetWidth, int targetHeight, int color_YCbYCr) {
        int address = this.mDsp.getPropertyAsInt(optImg, "memory-address");
        int canvasWidth = this.mDsp.getPropertyAsInt(optImg, "image-canvas-width");
        fillArea_setParam(null, optImg, address, targetOffsetLeft, targetOffsetTop, targetWidth, targetHeight, canvasWidth, color_YCbYCr);
        Log.d(TAG, "==== color: " + String.format("int型 intValue1 (16進数表示):%X", Integer.valueOf(color_YCbYCr)));
        sa_exec_setParam(null, optImg, 0, address, targetWidth, targetHeight, 0, canvasWidth, 1, color_YCbYCr);
    }

    public synchronized void fillArea_setParam(OptimizedImage optImgInput, OptimizedImage optImgOutput, int addressInput, int targetOffsetLeft, int targetOffsetTop, int targetWidth, int targetHeight, int canvasWidth, int color_YCbYCr) {
        Log.d(TAG, "==== color: " + String.format("int型 intValue1 (16進数表示):%X", Integer.valueOf(color_YCbYCr)));
        sa_exec_setParam(null, optImgOutput, 0, addressInput, targetWidth, targetHeight, 0, canvasWidth, 1, color_YCbYCr);
    }

    protected synchronized void sa_exec_setParam(OptimizedImage optImgInput, OptimizedImage optImgOutput, int addressInput, int addressOutput, int targetWidth, int targetHeight, int canvasWidthInput, int canvasWidthOutput, int mode, int color_YCbYCr) {
        if (addressInput % 8 != 0) {
            Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressInput:" + Integer.toHexString(addressInput));
        } else if (addressOutput % 8 != 0) {
            Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressOutput:" + Integer.toHexString(addressOutput));
        } else if (targetWidth % 4 != 0) {
            Log.e("sa", "!!!error  Widthは4の倍数  targetWidth:" + targetWidth);
        } else if (canvasWidthInput % 4 != 0) {
            Log.e("sa", "!!!error  入力line offsetが4の倍数であること canvasWidthInput:" + canvasWidthInput);
        } else if (canvasWidthOutput % 4 != 0) {
            Log.e("sa", "!!!error  出力line offsetが4の倍数であること canvasWidthOutput:" + canvasWidthOutput);
        } else {
            Log.i(TAG, "==== SA params =  Addrinput:" + Integer.toHexString(addressInput) + " AddrOutput:" + Integer.toHexString(addressOutput) + " targetWidth:" + targetWidth + " targetHeight:" + targetHeight + " canvasWidthInput:" + canvasWidthInput + " canvasWidthOutput:" + canvasWidthOutput + " mode:" + mode + " color_YCbYCr:" + color_YCbYCr);
            Log.i(TAG, "==== SA params convertedToAxi =  Addrinput:" + Integer.toHexString(convArmAddr2AxiAddr(addressInput)) + " AddrOutput:" + Integer.toHexString(convArmAddr2AxiAddr(addressOutput)) + " targetWidth:" + targetWidth + " targetHeight:" + targetHeight + " canvasWidthInput:" + canvasWidthInput + " canvasWidthOutput:" + canvasWidthOutput + " mode:" + mode + " color_YCbYCr:" + color_YCbYCr);
            this.sa_param_buf.clear();
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putInt(targetWidth);
            this.sa_param_buf.putInt(targetHeight);
            this.sa_param_buf.putInt(mode);
            this.sa_param_buf.putInt(canvasWidthInput);
            this.sa_param_buf.putInt(canvasWidthOutput);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(this.mDsp.getPropertyAsInt(this.sa_debug_msg_buffer, "memory-address")));
            this.sa_param_buf.putInt(color_YCbYCr);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            Log.d(TAG, "==== sa_param.write");
            this.sa_param.write(this.sa_param_buf, 64, 0);
            Log.d(TAG, "==== sa_bootparam reset");
            prepare_sa_bootparam();
            Log.d(TAG, "==== mDsp.setArg");
            this.mDsp.setArg(0, this.bootParam);
            this.mDsp.setArg(1, this.sa_param);
            Log.i(TAG, "==== SA params     sa_param addr:" + this.mDsp.getPropertyAsInt(this.sa_param, "memory-address"));
            Log.i(TAG, "==== SA params     bootParam addr:" + this.mDsp.getPropertyAsInt(this.bootParam, "memory-address"));
            this.mDsp.setArg(2, optImgInput);
            this.mDsp.setArg(3, optImgOutput);
            this.mDsp.setArg(4, this.sa_debug_msg_buffer);
        }
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

    public synchronized int execute(boolean show_debug_sa_log) {
        int ret;
        ret = execute();
        if (show_debug_sa_log) {
            byte[] byteArrayInput = new byte[64];
            this.sa_param.read(byteArrayInput);
            String debug_string_sa_param = "";
            for (byte b : byteArrayInput) {
                debug_string_sa_param = String.valueOf(debug_string_sa_param) + Integer.toHexString(b & 255) + ExposureModeController.SOFT_SNAP;
            }
            Log.d(TAG, "==== sa_param in byte " + debug_string_sa_param);
            byte[] byteArray = new byte[this.sa_debug_msg_buffer_size];
            this.sa_debug_msg_buffer.read(byteArray);
            String debug_string_byte = "";
            for (byte b2 : byteArray) {
                debug_string_byte = String.valueOf(debug_string_byte) + Integer.toHexString(b2 & 255) + ExposureModeController.SOFT_SNAP;
            }
            Log.d(TAG, "==== sa_debug_msg_buffer in byte " + debug_string_byte);
        }
        return ret;
    }

    public synchronized int execute() {
        int ret;
        ret = 9999;
        Log.d(TAG, "==== SA start");
        if (this.mDsp.execute()) {
            Log.i("sa", "success");
            try {
                byte[] byteArray = new byte[60];
                this.bootParam.read(byteArray);
                Log.i("sa", "done  read_sa_mbox0_b:" + read_sa_mbox0_b(byteArray));
                Log.i("sa", "done  read_sa_mbox1_b:" + read_sa_mbox1_b(byteArray));
                Log.i("sa", "done  read_sa_mbox2_b:" + read_sa_mbox2_b(byteArray));
                Log.i("sa", "done  read_sa_mbox3_b:" + read_sa_mbox3_b(byteArray));
                for (int i1 = 0; i1 < 60; i1++) {
                    Log.i("sa", "done  bootParam:" + readIntFromByteArray(byteArray, i1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] dst = new byte[1];
            this.sa_debug_msg_buffer.read(dst, 0, 1, 0);
            Log.i("sa", "done  return code:" + Integer.toHexString(dst[0] & 255));
            if (dst[0] == 0) {
                Log.i("sa", "success.  return code:" + Integer.toHexString(dst[0] & 255));
                ret = 0;
            } else {
                Log.e("sa", "error.   return code:" + Integer.toHexString(dst[0] & 255) + " use execute(true) for detail message codes.");
                ret = dst[0] & 255;
            }
        } else {
            Log.e("sa", "Failed");
        }
        Log.d(TAG, "==== SA done ");
        return ret;
    }

    private synchronized void prepare_sa_bootparam() {
        if (this.bootParam != null) {
            this.bootParam.write(this.bootParam_buf);
        } else {
            Log.d(TAG, "mDsp.createBuffer(size) run");
            this.bootParam = this.mDsp.createBuffer(60);
            this.bootParam_buf = ByteBuffer.allocateDirect(60);
            this.bootParam_buf.order(ByteOrder.nativeOrder());
            this.bootParam_buf.putInt(this.mDsp.getPropertyAsInt("program-descriptor"));
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam.write(this.bootParam_buf);
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
}
