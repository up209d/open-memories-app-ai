package com.sony.imaging.app.pbsa;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class SA_SoftFocus implements NotificationListener {
    private static int FILTER33C_ADDR = 0;
    private static int FILTER33Y_ADDR = 0;
    private static int FILTER55C_ADDR = 0;
    private static int FILTER55Y_ADDR = 0;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static int SA_PARAM_ADDR = 0;
    private static final int SA_PL_PARAM_SIZE = 256;
    private static final int SA_WORK_SIZE_THRESHOLD_VALUE = 16000000;
    private static int START_ADDR = 0;
    private static final String TAG = "SA_SoftFocus";
    private static int YMAIN_ADDR;
    private CameraNotificationManager mCamNtfy;
    OptimizedImage mOptImage;
    OptimizedImage mOutImage;
    private static String mPackageName = null;
    private static SFSAParam mSFSAParam = null;
    private static int mSA_WORK_SIZE = 0;
    private static boolean DEBUG = true;
    private static boolean isProgramSet = false;
    private static final String[] TAGS = new String[0];
    private DSP mDSP = null;
    private DeviceBuffer mSAParam = null;
    private DeviceBuffer mBuffer1 = null;

    public SA_SoftFocus(String packageName) {
        SFSAParam sFSAParam = null;
        Log.i(TAG, "SA_SoftFocus: 20140605");
        if (mSFSAParam == null) {
            mSFSAParam = new SFSAParam(sFSAParam);
        }
        mPackageName = packageName;
        this.mCamNtfy = CameraNotificationManager.getInstance();
    }

    public void open() {
        Log.i(TAG, "open:");
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
        }
        this.mCamNtfy.setNotificationListener(this);
    }

    public void initialize() {
        Log.i(TAG, "initialize:");
        if (!isProgramSet) {
            loadProgram();
            isProgramSet = true;
        }
        createBuffer();
    }

    public void releaseMainBuffer() {
        Log.i(TAG, "    releaseMainBuffer: ");
        if (this.mBuffer1 != null) {
            this.mBuffer1.release();
            this.mBuffer1 = null;
        }
    }

    public void releaseBuffer() {
        Log.i(TAG, "release:");
        if (this.mSAParam != null) {
            this.mSAParam.release();
            this.mSAParam = null;
        }
    }

    public void close() {
        Log.i(TAG, "close:");
        this.mCamNtfy.removeNotificationListener(this);
        releaseMainBuffer();
        releaseBuffer();
        if (this.mDSP != null) {
            this.mDSP.release();
        }
        mSFSAParam.close();
        if (mSFSAParam != null) {
            mSFSAParam = null;
        }
        isProgramSet = false;
    }

    private String getPackageName() {
        return mPackageName;
    }

    private void loadProgram() {
        String filePath = getFilePath();
        Log.i(TAG, "    loadProgram: " + filePath);
        this.mDSP.setProgram(filePath);
    }

    public void setSAWorkSize(int width, int height) {
        mSA_WORK_SIZE = width * height;
        if (SA_WORK_SIZE_THRESHOLD_VALUE < mSA_WORK_SIZE) {
            mSA_WORK_SIZE /= 4;
        } else {
            mSA_WORK_SIZE = (int) (mSA_WORK_SIZE * 0.4d);
        }
    }

    private void createMainBuffer() {
        Log.i(TAG, "    createMainBuffer: ");
        if (this.mBuffer1 == null) {
            this.mBuffer1 = this.mDSP.createBuffer(mSA_WORK_SIZE);
            START_ADDR = this.mDSP.getPropertyAsInt(this.mBuffer1, "memory-address");
            YMAIN_ADDR = START_ADDR;
        }
    }

    private void createBuffer() {
        Log.i(TAG, "    createBuffer: ");
        if (this.mSAParam == null) {
            this.mSAParam = this.mDSP.createBuffer(SA_PL_PARAM_SIZE);
            SA_PARAM_ADDR = this.mDSP.getPropertyAsInt(this.mSAParam, "memory-address");
            Log.i(TAG, "        SA PARAM ADDER " + Integer.toHexString(SA_PARAM_ADDR));
            FILTER33Y_ADDR = SA_PARAM_ADDR + 56;
            FILTER33C_ADDR = SA_PARAM_ADDR + 80;
            FILTER55Y_ADDR = SA_PARAM_ADDR + 104;
            FILTER55C_ADDR = SA_PARAM_ADDR + 160;
        }
    }

    private String getFilePath() {
        String filePath;
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            filePath = "/android/data/lib/" + getPackageName() + "/lib/libsa_sftfcs_avip.so";
        } else {
            filePath = "/android/data/lib/" + getPackageName() + "/lib/libsa_sftfcs_musashi.so";
        }
        Log.w(TAG, "filePath = " + filePath);
        return filePath;
    }

    private DeviceBuffer getBootParam(DeviceBuffer boot_param) {
        ByteBuffer buf = ByteBuffer.allocateDirect(64);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(this.mDSP.getPropertyAsInt("program-descriptor"));
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
        buf.put((byte) 1);
        buf.put((byte) 0);
        buf.put((byte) 0);
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
        boot_param.write(buf);
        buf.clear();
        return boot_param;
    }

    private DeviceBuffer getSAParam(DeviceBuffer sa_param, OptimizedImage inputImg, OptimizedImage outputImg) {
        this.mOptImage = inputImg;
        short width = (short) inputImg.getWidth();
        short height = (short) inputImg.getHeight();
        short cwidth = (short) this.mDSP.getPropertyAsInt(inputImg, "image-canvas-width");
        Log.w(TAG, "width = " + ((int) width) + " height = " + ((int) height) + " cwidth = " + ((int) cwidth));
        int input_offset = this.mDSP.getPropertyAsInt(inputImg, "image-data-offset");
        int input_addr = this.mDSP.getPropertyAsInt(inputImg, "memory-address");
        this.mOutImage = outputImg;
        int output_offset = this.mDSP.getPropertyAsInt(outputImg, "image-data-offset");
        int output_addr = this.mDSP.getPropertyAsInt(outputImg, "memory-address");
        ByteBuffer buf = ByteBuffer.allocateDirect(SA_PL_PARAM_SIZE);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(getAXIAddr(input_addr + input_offset));
        buf.putInt(getAXIAddr(output_addr + output_offset));
        buf.putInt(getAXIAddr(YMAIN_ADDR));
        buf.putInt(getAXIAddr(FILTER33Y_ADDR));
        buf.putInt(getAXIAddr(FILTER33C_ADDR));
        buf.putInt(getAXIAddr(FILTER55Y_ADDR));
        buf.putInt(getAXIAddr(FILTER55C_ADDR));
        buf.putInt(width);
        buf.putInt(height);
        buf.putInt(cwidth);
        buf.putInt(getAlpha());
        buf.putInt(getBeta());
        buf.putInt(0);
        buf.putInt(getDelta());
        if (DEBUG) {
            Log.i("SAPARAM", "width " + ((int) width));
            Log.i("SAPARAM", "cwidth " + ((int) cwidth));
            Log.i("SAPARAM", "height " + ((int) height));
            Log.i("SAPARAM", "dmy_us_padding0 0");
        }
        Log.i("SAPARAM", "coef3C");
        buf.putShort((short) 9);
        buf.putShort((short) 65);
        buf.putShort((short) 9);
        buf.putShort((short) 10);
        buf.putShort((short) 70);
        buf.putShort((short) 10);
        buf.putShort((short) 9);
        buf.putShort((short) 65);
        buf.putShort((short) 9);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        Log.i("SAPARAM", "coef3Y");
        buf.putShort((short) 28);
        buf.putShort((short) 28);
        buf.putShort((short) 28);
        buf.putShort((short) 29);
        buf.putShort((short) 30);
        buf.putShort((short) 29);
        buf.putShort((short) 28);
        buf.putShort((short) 28);
        buf.putShort((short) 28);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        Log.i("SAPARAM", "coef5C");
        buf.putShort((short) 0);
        buf.putShort((short) 16);
        buf.putShort((short) 17);
        buf.putShort((short) 16);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 16);
        buf.putShort((short) 19);
        buf.putShort((short) 16);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 17);
        buf.putShort((short) 22);
        buf.putShort((short) 17);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 16);
        buf.putShort((short) 19);
        buf.putShort((short) 16);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 16);
        buf.putShort((short) 17);
        buf.putShort((short) 16);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        Log.i("SAPARAM", "coef5Y");
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 11);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 11);
        buf.putShort((short) 12);
        buf.putShort((short) 11);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 11);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 10);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        sa_param.write(buf);
        buf.clear();
        return sa_param;
    }

    private int getAXIAddr(int addr) {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            int axiAddr = addr & Integer.MAX_VALUE;
            return axiAddr;
        }
        int axiAddr2 = addr & 1073741823;
        return axiAddr2;
    }

    public boolean execute(OptimizedImage optImage, OptimizedImage outputOptimizedImage) {
        Log.i(TAG, "execute:");
        createMainBuffer();
        DeviceBuffer paramBoot = getBootParam(this.mDSP.createBuffer(SA_BOOT_PARAM_SIZE));
        this.mSAParam = getSAParam(this.mSAParam, optImage, outputOptimizedImage);
        boolean result = exec_sa(paramBoot);
        Log.i(TAG, "    done... " + result);
        paramBoot.release();
        releaseMainBuffer();
        return result;
    }

    private boolean exec_sa(DeviceBuffer boot_param) {
        this.mDSP.setArg(0, boot_param);
        this.mDSP.setArg(1, this.mSAParam);
        this.mDSP.setArg(2, this.mOptImage);
        this.mDSP.setArg(3, this.mOutImage);
        this.mDSP.setArg(4, this.mBuffer1);
        boolean result = this.mDSP.execute();
        if (result) {
            Log.i("sa", "success");
        } else {
            Log.i("sa", "failure");
        }
        return result;
    }

    public boolean setLevel(int presetLevel) {
        return mSFSAParam.setLevel(presetLevel);
    }

    public boolean setSize(int num) {
        return mSFSAParam.setSize(num);
    }

    public int getLevel() {
        return mSFSAParam.getLevel();
    }

    public int getAlpha() {
        return mSFSAParam.getAlpha();
    }

    public int getBeta() {
        return mSFSAParam.getBeta();
    }

    public int getDelta() {
        return mSFSAParam.getDelta();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SFSAParam {
        private static final int MAX_LEVEL = 6;
        private static final int MAX_SIZE_LEVEL = 4;
        private static int[][] mAlpha;
        private static int[][] mBeta;
        private static int[][] mDelta;
        private static int mLevel = 0;
        private static int mSize = 0;

        static {
            int[] iArr = new int[7];
            iArr[6] = 2;
            int[] iArr2 = new int[7];
            iArr2[5] = 2;
            iArr2[6] = 4;
            mAlpha = new int[][]{iArr, iArr2, new int[]{0, 0, 0, 1, 3, 5, 7}, new int[]{3, 4, 6, 8, 10, 12, 14}, new int[]{0, 0, 0, 0, 1, 3, 5}};
            mBeta = new int[][]{new int[]{0, 0, 0, 2, 6, 10, 14}, new int[]{0, 0, 0, 1, 3, 7, 11}, new int[]{0, 0, 0, 0, 2, 6, 10}, new int[]{0, 0, 0, 0, 1, 3, 6}, new int[]{1, 2, 3, 4, 5, 6, 7}};
            mDelta = new int[][]{new int[]{22, 24, 28, 26, 24, 22, 20}, new int[]{19, 21, 25, 26, 24, 22, 20}, new int[]{19, 21, 25, 26, 24, 22, 20}, new int[]{21, 22, 20, 18, 16, 14, 12}, new int[]{1, 2, 4, 6, 8, 8, 8}};
        }

        private SFSAParam() {
            initParam();
        }

        /* synthetic */ SFSAParam(SFSAParam sFSAParam) {
            this();
        }

        private void initParam() {
            mLevel = -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void close() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setLevel(int presetLevel) {
            if (presetLevel >= 0 && presetLevel <= 6) {
                mLevel = presetLevel;
                return true;
            }
            Log.e(SA_SoftFocus.TAG, "preset Level is not valid(setLevel)" + presetLevel);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setSize(int num) {
            if (num >= 0 && num <= 4) {
                mSize = num;
                return true;
            }
            Log.e(SA_SoftFocus.TAG, "size is not valid" + num);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getLevel() {
            return mLevel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getAlpha() {
            Log.i(SA_SoftFocus.TAG, "mAlpha = " + mAlpha[mSize][mLevel]);
            return mAlpha[mSize][mLevel];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBeta() {
            Log.i(SA_SoftFocus.TAG, "mBeta = " + mBeta[mSize][mLevel]);
            return mBeta[mSize][mLevel];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getDelta() {
            Log.i(SA_SoftFocus.TAG, "mDelta = " + mDelta[mSize][mLevel]);
            return mDelta[mSize][mLevel];
        }
    }
}
