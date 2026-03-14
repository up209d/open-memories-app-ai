package com.sony.imaging.app.pbsa;

import android.graphics.Rect;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/* loaded from: classes.dex */
public class SA_PortraitLighting {
    private static int MAIN_LUT_ADDR = 0;
    private static int MAP_ADDR = 0;
    private static int MAP_INFO_ADDR = 0;
    private static final int MAP_INFO_ADDR_OFFSET = 76;
    private static final int MAX_FACE_NUM = 8;
    private static int OBJ_INFO_ADDR = 0;
    private static final int OBJ_INFO_ADDR_OFFSET = 92;
    private static final int SA_BOOT_PARAM_SIZE = 60;
    private static final int SA_MAIN_LUT_SIZE = 512;
    private static int SA_PARAM_ADDR = 0;
    private static final int SA_PL_PARAM_SIZE = 256;
    private static final int SA_SUB_LUT_SIZE = 256;
    private static int START_ADDR = 0;
    private static int SUB_LUT_ADDR = 0;
    private static final String TAG = "SA_PortraitLighting";
    private static int YMAIN_ADDR;
    private CameraNotificationManager mCamNtfy;
    OptimizedImage mOptImage;
    private static String mPackageName = null;
    private static PLSAParam mPLSAParam = null;
    private static boolean DEBUG = false;
    private static boolean isProgramSet = false;
    private static final String[] TAGS = {CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.FOCUS_CHANGE};
    private DSP mDSP = null;
    private DeviceBuffer mSAParam = null;
    private DeviceBuffer mBuffer1 = null;
    private DeviceBuffer mMainLutTbl = null;
    private DeviceBuffer mSubLutTbl = null;

    public SA_PortraitLighting(String packageName) {
        PLSAParam pLSAParam = null;
        Log.i(TAG, "SA_PortraitLighting: 20140605");
        if (mPLSAParam == null) {
            mPLSAParam = new PLSAParam(pLSAParam);
        }
        mPackageName = packageName;
        this.mCamNtfy = CameraNotificationManager.getInstance();
    }

    public void open() {
        Log.i(TAG, "open:");
        if (this.mDSP == null) {
            this.mDSP = DSP.createProcessor("sony-di-dsp");
        }
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
        if (this.mMainLutTbl != null) {
            this.mMainLutTbl.release();
            this.mMainLutTbl = null;
        }
        if (this.mSubLutTbl != null) {
            this.mSubLutTbl.release();
            this.mSubLutTbl = null;
        }
    }

    public void close() {
        Log.i(TAG, "close:");
        releaseMainBuffer();
        releaseBuffer();
        if (this.mDSP != null) {
            this.mDSP.release();
        }
        mPLSAParam.close();
        if (mPLSAParam != null) {
            mPLSAParam = null;
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

    private void createMainBuffer() {
        Log.i(TAG, "    createMainBuffer: ");
        if (this.mBuffer1 == null) {
            List<ScalarProperties.PictureSize> list = ScalarProperties.getSupportedPictureSizes();
            ScalarProperties.PictureSize large_32 = list.get(0);
            ScalarProperties.PictureSize large_169 = list.get(3);
            int max_size = large_32.height * large_32.width;
            int size_169 = large_169.height * large_169.width;
            ScalarProperties.PictureSize large_43 = null;
            ScalarProperties.PictureSize large_11 = null;
            int size_11 = 0;
            int size_43 = 0;
            if (list.size() > 6) {
                ScalarProperties.PictureSize large_432 = list.get(6);
                large_43 = large_432;
                ScalarProperties.PictureSize large_112 = list.get(9);
                large_11 = large_112;
                size_43 = large_43.height * large_43.width;
                size_11 = large_11.height * large_11.width;
            }
            Log.i(TAG, "        max_image_size " + max_size + ", width32=" + large_32.width + ", height32=" + large_32.height);
            if (max_size < size_169) {
                max_size = size_169;
                Log.i(TAG, "        ->max_image_size " + max_size + ", width169=" + large_169.width + ", height43=" + large_169.height);
            }
            if (max_size < size_43) {
                max_size = size_43;
                Log.i(TAG, "        ->max_image_size " + max_size + ", width43=" + large_43.width + ", height43=" + large_43.height);
            }
            if (max_size < size_11) {
                max_size = size_11;
                Log.i(TAG, "        ->max_image_size " + max_size + ", width11=" + large_11.width + ", height11=" + large_11.height);
            }
            this.mBuffer1 = this.mDSP.createBuffer(max_size);
            START_ADDR = this.mDSP.getPropertyAsInt(this.mBuffer1, "memory-address");
            YMAIN_ADDR = START_ADDR;
        }
    }

    private void createBuffer() {
        Log.i(TAG, "    createBuffer: ");
        if (this.mSAParam == null) {
            this.mSAParam = this.mDSP.createBuffer(256);
            SA_PARAM_ADDR = this.mDSP.getPropertyAsInt(this.mSAParam, "memory-address");
            Log.i(TAG, "        SA PARAM ADDER " + Integer.toHexString(SA_PARAM_ADDR));
            OBJ_INFO_ADDR = SA_PARAM_ADDR + OBJ_INFO_ADDR_OFFSET;
            MAP_INFO_ADDR = SA_PARAM_ADDR + MAP_INFO_ADDR_OFFSET;
        }
        if (this.mMainLutTbl == null) {
            this.mMainLutTbl = this.mDSP.createBuffer(SA_MAIN_LUT_SIZE);
            MAIN_LUT_ADDR = this.mDSP.getPropertyAsInt(this.mMainLutTbl, "memory-address");
        }
        if (this.mSubLutTbl == null) {
            this.mSubLutTbl = this.mDSP.createBuffer(256);
            SUB_LUT_ADDR = this.mDSP.getPropertyAsInt(this.mSubLutTbl, "memory-address");
        }
    }

    private String getFilePath() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            String filePath = "/android/data/lib/" + getPackageName() + "/lib/libcslsa_FF.so";
            return filePath;
        }
        String filePath2 = "/android/data/lib/" + getPackageName() + "/lib/libcslsa_musashi.so";
        return filePath2;
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

    private DeviceBuffer getSAParam(DeviceBuffer sa_param, OptimizedImage optImage) {
        this.mOptImage = optImage;
        short width = (short) optImage.getWidth();
        short height = (short) optImage.getHeight();
        short cwidth = (short) this.mDSP.getPropertyAsInt(optImage, "image-canvas-width");
        int offset = this.mDSP.getPropertyAsInt(optImage, "image-data-offset");
        int inout_addr = this.mDSP.getPropertyAsInt(optImage, "memory-address") + offset;
        ImageAnalyzer.AnalyzedFace[] faces = mPLSAParam.getFace();
        int faceNum = mPLSAParam.getFaceNum();
        int mapOffest = (((width * height) * 2) * 3) / 4;
        MAP_ADDR = YMAIN_ADDR + mapOffest;
        ByteBuffer buf = ByteBuffer.allocateDirect(256);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(getAXIAddr(inout_addr));
        buf.putInt(getAXIAddr(YMAIN_ADDR));
        buf.putInt(getAXIAddr(MAP_ADDR));
        buf.putInt(getAXIAddr(MAIN_LUT_ADDR));
        buf.putInt(getAXIAddr(SUB_LUT_ADDR));
        buf.putInt(getAXIAddr(OBJ_INFO_ADDR));
        buf.putInt(getAXIAddr(MAP_INFO_ADDR));
        if (DEBUG) {
            Log.i("SAPARAM", "inout_addr " + Integer.toHexString(getAXIAddr(inout_addr)));
            Log.i("SAPARAM", "YMAIN_ADDR " + Integer.toHexString(getAXIAddr(YMAIN_ADDR)));
            Log.i("SAPARAM", "MAP_ADDR " + Integer.toHexString(getAXIAddr(MAP_ADDR)));
            Log.i("SAPARAM", "MAIN_LUT_ADDR " + Integer.toHexString(getAXIAddr(MAIN_LUT_ADDR)));
            Log.i("SAPARAM", "SUB_LUT_ADDR " + Integer.toHexString(getAXIAddr(SUB_LUT_ADDR)));
            Log.i("SAPARAM", "OBJ_INFO_ADDR " + Integer.toHexString(getAXIAddr(OBJ_INFO_ADDR)));
            Log.i("SAPARAM", "MAP_INFO_ADDR " + Integer.toHexString(getAXIAddr(MAP_INFO_ADDR)));
        }
        buf.putShort(width);
        buf.putShort(cwidth);
        buf.putShort(height);
        buf.putShort((short) 0);
        if (DEBUG) {
            Log.i("SAPARAM", "width " + ((int) width));
            Log.i("SAPARAM", "cwidth " + ((int) cwidth));
            Log.i("SAPARAM", "height " + ((int) height));
            Log.i("SAPARAM", "dmy_us_padding0 0");
        }
        buf.putShort(mPLSAParam.getLceGain());
        buf.putShort(mPLSAParam.getDrangeCoef());
        buf.putShort((short) 64);
        buf.putShort((short) 256);
        if (DEBUG) {
            Log.i("SAPARAM", "us_lce_gain " + ((int) mPLSAParam.getLceGain()));
            Log.i("SAPARAM", "us_drange_coef " + ((int) mPLSAParam.getDrangeCoef()));
            Log.i("SAPARAM", "us_drange_ofs_limit64");
            Log.i("SAPARAM", "us_sub_gain256");
        }
        buf.putShort((short) 0);
        buf.putShort(mPLSAParam.getCGainMax());
        buf.putShort(mPLSAParam.getTransWidRatio());
        buf.putShort((short) 5);
        if (DEBUG) {
            Log.i("SAPARAM", "0 0");
            Log.i("SAPARAM", "mPLSAParam.getCGainMax() " + ((int) mPLSAParam.getCGainMax()));
            Log.i("SAPARAM", "mPLSAParam.getTransWidRatio() " + ((int) mPLSAParam.getTransWidRatio()));
            Log.i("SAPARAM", "5 5");
        }
        buf.putShort((short) 102);
        buf.putShort((short) 179);
        buf.putShort((short) 51);
        buf.putShort(mPLSAParam.getLcelpfGain());
        if (DEBUG) {
            Log.i("SAPARAM", "us_body_coef 102");
            Log.i("SAPARAM", "us_aux_level 179");
            Log.i("SAPARAM", "us_lcelpf_ratio 51");
            Log.i("SAPARAM", "us_lcelpf_gain " + ((int) mPLSAParam.getLcelpfGain()));
        }
        buf.putShort((short) 128);
        buf.putShort((short) 256);
        buf.putShort((short) 256);
        buf.putShort((short) 245);
        if (DEBUG) {
            Log.i("SAPARAM", "128 128");
            Log.i("SAPARAM", "256 256");
            Log.i("SAPARAM", "256 256");
            Log.i("SAPARAM", "245 245");
        }
        buf.putShort((short) 1);
        buf.putShort((short) 0);
        buf.putInt(0);
        if (DEBUG) {
            Log.i("SAPARAM", "1 1");
            Log.i("SAPARAM", "0 0");
            Log.i("SAPARAM", "0 0");
        }
        buf.putInt(getAXIAddr(MAP_ADDR));
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        if (DEBUG) {
            Log.i("SAPARAM", "MAP_ADDR " + Integer.toHexString(MAP_ADDR));
            Log.i("SAPARAM", "0 0");
            Log.i("SAPARAM", "0 0");
            Log.i("SAPARAM", "0 0");
        }
        buf.putShort((short) faceNum);
        if (DEBUG) {
            Log.i("SAPARAM", "faceNum " + faceNum);
        }
        buf.putShort((short) 0);
        if (mPLSAParam.isDetectedFaces()) {
            Log.i(TAG, "face num " + faceNum);
            int i = 0;
            while (i < faceNum) {
                short x1 = getFaceLFPix(faces[i].face.rect.left, width);
                short y1 = getFaceTBPix(faces[i].face.rect.top, height);
                short x2 = getFaceLFPix(faces[i].face.rect.right, width);
                short y2 = getFaceTBPix(faces[i].face.rect.bottom, height);
                short node = (short) faces[i].face.faceAngle.angle;
                buf.putShort(x1);
                buf.putShort(y1);
                buf.putShort(x2);
                buf.putShort(y2);
                buf.putShort(node);
                buf.putShort((short) 255);
                buf.putShort((short) 128);
                buf.putShort((short) 0);
                if (DEBUG) {
                    Log.i("SAPARAM", "x1 " + ((int) x1));
                    Log.i("SAPARAM", "y1 " + ((int) y1));
                    Log.i("SAPARAM", "x2 " + ((int) x2));
                    Log.i("SAPARAM", "y2 " + ((int) y2));
                    Log.i("SAPARAM", "node " + ((int) node));
                    Log.i("SAPARAM", "0x00FF 255");
                    Log.i("SAPARAM", "0x0080 128");
                    Log.i("SAPARAM", "0 0");
                }
                i++;
            }
            while (i < 8) {
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                buf.putShort((short) 0);
                i++;
            }
        }
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

    private short getFaceLFPix(int pos, short width) {
        short pix = (short) (((pos + 1000) * width) / PortraitBeautyConstants.TWO_SECOND_MILLIS);
        return pix;
    }

    private short getFaceTBPix(int pos, short height) {
        String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
            int convPos = (pos * 27) / 32;
            short pix = (short) (((convPos + 1000) * height) / PortraitBeautyConstants.TWO_SECOND_MILLIS);
            return pix;
        }
        short pix2 = (short) (((pos + 1000) * height) / PortraitBeautyConstants.TWO_SECOND_MILLIS);
        return pix2;
    }

    private int min(int a, int b) {
        return a > b ? b : a;
    }

    private void makeLutTbl() {
        float fMainGamma = mPLSAParam.getMainGamma();
        float fSubGamma = mPLSAParam.getSubGamma();
        ByteBuffer mainLutbuf = ByteBuffer.allocateDirect(SA_MAIN_LUT_SIZE);
        mainLutbuf.order(ByteOrder.nativeOrder());
        ByteBuffer subLutbuf = ByteBuffer.allocateDirect(256);
        subLutbuf.order(ByteOrder.nativeOrder());
        for (int i = 0; i < 256; i++) {
            short mainLut = (short) min(65280, (int) ((Math.pow(i / 255.0f, fMainGamma) * 65280.0d) + 0.5d));
            byte subLut = (byte) min(BatteryIcon.BATTERY_STATUS_CHARGING, (int) ((Math.pow(i / 255.0f, fSubGamma) * 255.0d) + 0.5d));
            mainLutbuf.putShort(mainLut);
            subLutbuf.put(subLut);
        }
        this.mMainLutTbl.write(mainLutbuf);
        this.mSubLutTbl.write(subLutbuf);
        if (DEBUG) {
            ByteBuffer buf1 = ByteBuffer.allocateDirect(SA_MAIN_LUT_SIZE);
            buf1.order(ByteOrder.nativeOrder());
            buf1.rewind();
            this.mMainLutTbl.read(buf1);
            for (int i2 = 0; i2 < SA_MAIN_LUT_SIZE; i2++) {
                buf1.rewind();
                Log.i("MLUTTBL", Integer.toHexString(buf1.get(i2)));
            }
            buf1.clear();
            ByteBuffer buf2 = ByteBuffer.allocateDirect(256);
            buf2.order(ByteOrder.nativeOrder());
            buf2.rewind();
            this.mSubLutTbl.read(buf2);
            for (int i3 = 0; i3 < 256; i3++) {
                buf2.rewind();
                Log.i("SLUTTBL", Integer.toHexString(buf2.get(i3)));
            }
            buf2.clear();
        }
        mainLutbuf.clear();
        subLutbuf.clear();
    }

    public boolean execute(OptimizedImage optImage) {
        Log.i(TAG, "execute:");
        createMainBuffer();
        DeviceBuffer paramBoot = getBootParam(this.mDSP.createBuffer(SA_BOOT_PARAM_SIZE));
        this.mSAParam = getSAParam(this.mSAParam, optImage);
        makeLutTbl();
        boolean result = exec_sa(paramBoot);
        Log.i(TAG, "    done... " + result);
        paramBoot.release();
        releaseMainBuffer();
        return result;
    }

    private boolean exec_sa(DeviceBuffer boot_param) {
        if (mPLSAParam.getObjLevel() < 0 && mPLSAParam.getBgLevel() < 0) {
            Log.i(TAG, "sa error");
            return false;
        }
        this.mDSP.setArg(0, boot_param);
        this.mDSP.setArg(1, this.mSAParam);
        this.mDSP.setArg(2, this.mOptImage);
        this.mDSP.setArg(3, this.mBuffer1);
        this.mDSP.setArg(4, this.mMainLutTbl);
        this.mDSP.setArg(5, this.mSubLutTbl);
        boolean result = this.mDSP.execute();
        if (result) {
            Log.i("sa", "success");
            return result;
        }
        Log.i("sa", "failure");
        return result;
    }

    public boolean setFace(int faceNum, ImageAnalyzer.AnalyzedFace[] faces) {
        return mPLSAParam.setFace(faceNum, faces);
    }

    public boolean setIsoValue(int isoValue) {
        return mPLSAParam.setIsoValue(isoValue);
    }

    public boolean setLevel(int objLevel, int bgLevel) {
        return mPLSAParam.setLevel(objLevel, bgLevel);
    }

    public boolean setLevel(int presetLevel) {
        return mPLSAParam.setLevel(presetLevel);
    }

    public Rect getTargetRect() {
        return mPLSAParam.getTargetRect();
    }

    public ImageAnalyzer.AnalyzedFace[] getFace() {
        return mPLSAParam.getFace();
    }

    public int getIsoValue() {
        return mPLSAParam.getIsoValue();
    }

    public int getObjLevel() {
        return mPLSAParam.getObjLevel();
    }

    public int getBgLevel() {
        return mPLSAParam.getBgLevel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PLSAParam {
        private static final int MAX_LEVEL = 6;
        private static final int RECT_HEIGHT_16_9 = 904;
        private static final int RECT_HEIGHT_3_2 = 764;
        private static final int RECT_WIDTH_16_9 = 509;
        private static final int RECT_WIDTH_3_2 = 509;
        private static ImageAnalyzer.AnalyzedFace[] mFaces = null;
        private static int mFaceNum = 0;
        private static boolean mIsDetectFaces = false;
        private static int mIsoValue = 0;
        private static int mObjLevel = 0;
        private static int mBGLevel = 0;
        private static int mCenterX = 0;
        private static int mCenterY = 0;
        private static Rect mRect = null;
        private static float[] mMainGammaTbl = {0.85f, 0.7f, 0.65f, 0.6f, 0.55f, 0.5f, 0.45f};
        private static float[] mSubGammaTbl = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        private static float[] mLceGainTbl = {0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f};
        private static float[] mLcelpfGainTbl = {1.0f, 1.0f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f};
        private static float[] mDrangeCoefTbl = {0.3f, 0.35f, 0.4f, 0.475f, 0.55f, 0.625f, 0.675f};
        private static float[] mCGainMax = {1.05f, 1.1f, 1.2f, 1.225f, 1.25f, 1.275f, 1.291f};
        private static float[] mTransWidRatio = {0.3f, 0.3f, 0.3f, 0.292f, 0.284f, 0.267f, 0.25f};

        private PLSAParam() {
            initParam();
        }

        /* synthetic */ PLSAParam(PLSAParam pLSAParam) {
            this();
        }

        private void initParam() {
            mFaces = null;
            mIsDetectFaces = false;
            mIsoValue = -1;
            mObjLevel = -1;
            mBGLevel = -1;
            mFaceNum = -1;
            mRect = new Rect();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void close() {
            if (mFaces != null) {
                int len = mFaces.length;
                for (int i = 0; i < len; i++) {
                    mFaces[i] = null;
                }
            }
            mFaces = null;
            mRect = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setFace(int faceNum, ImageAnalyzer.AnalyzedFace[] faces) {
            mFaces = new ImageAnalyzer.AnalyzedFace[8];
            mFaces = faces;
            Log.i(SA_PortraitLighting.TAG, "mFaces = " + mFaces);
            mFaceNum = faceNum;
            if (faceNum == 0) {
                mIsDetectFaces = false;
            } else {
                mIsDetectFaces = true;
            }
            return faces != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setIsoValue(int isoValue) {
            mIsoValue = isoValue;
            Log.i(SA_PortraitLighting.TAG, "mIsoValue = " + mIsoValue);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setLevel(int objLevel, int bgLevel) {
            if (objLevel >= 0 && objLevel <= 6) {
                mObjLevel = objLevel;
                if (bgLevel >= 0 && bgLevel <= 6) {
                    mBGLevel = bgLevel;
                    return true;
                }
                Log.e(SA_PortraitLighting.TAG, "bg Level is not valid(setLevel)" + bgLevel);
                return false;
            }
            Log.e(SA_PortraitLighting.TAG, "obj Level is not valid(setLevel)" + objLevel);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setLevel(int presetLevel) {
            Log.i(SA_PortraitLighting.TAG, "presetLevel = " + presetLevel);
            if (presetLevel >= 0 && presetLevel <= 6) {
                mObjLevel = presetLevel;
                mBGLevel = presetLevel;
                return true;
            }
            Log.e(SA_PortraitLighting.TAG, "preset Level is not valid(setLevel)" + presetLevel);
            return false;
        }

        private CameraEx.FocusAreaInfos getCurrentFocusAreaInfos(String focusAreaMode) {
            DisplayManager.DeviceStatus deviceStatus = DisplayModeObserver.getInstance().getActiveDeviceStatus();
            int viewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
            String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            int imagerAsp = -1;
            if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
                imagerAsp = 1;
            } else if (PictureSizeController.ASPECT_3_2.equals(picAsp)) {
                imagerAsp = 0;
            }
            return getFocusAreaInfos(imagerAsp, viewPattern, focusAreaMode);
        }

        private CameraEx.FocusAreaInfos getFocusAreaInfos(int aspect, int viewPattern, String focusAreaMode) {
            CameraEx.FocusAreaInfos[] areaInfos = CameraSetting.getInstance().getFocusAreaRectInfos(aspect, viewPattern);
            if (areaInfos == null) {
                return null;
            }
            for (CameraEx.FocusAreaInfos areaInfo : areaInfos) {
                if (areaInfo.focusAreaMode.equals(focusAreaMode)) {
                    return areaInfo;
                }
            }
            return null;
        }

        private Rect calcRectangle(int x, int y) {
            Rect rect = new Rect();
            String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
                rect.left = x - 254;
                rect.right = x + 254;
                rect.top = y - 452;
                rect.bottom = y + 452;
            } else {
                rect.left = x - 254;
                rect.right = x + 254;
                rect.top = y - 382;
                rect.bottom = y + 382;
            }
            int shift = 0;
            if (rect.left < -1000) {
                shift = rect.left + 1000;
            } else if (rect.right > 1000) {
                shift = rect.right - 1000;
            }
            rect.left -= shift;
            rect.right -= shift;
            int shift2 = 0;
            if (rect.top < -1000) {
                shift2 = rect.top + 1000;
            } else if (rect.bottom > 1000) {
                shift2 = rect.bottom - 1000;
            }
            rect.top -= shift2;
            rect.bottom -= shift2;
            return rect;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Rect getTargetRect() {
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ImageAnalyzer.AnalyzedFace[] getFace() {
            return mFaces;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getFaceNum() {
            Log.i(SA_PortraitLighting.TAG, "mFaceNum = " + mFaceNum);
            return mFaceNum;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getIsoValue() {
            Log.i(SA_PortraitLighting.TAG, "mIsoValue = " + mIsoValue);
            return mIsoValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getObjLevel() {
            Log.i(SA_PortraitLighting.TAG, "mObjLevel = " + mObjLevel);
            return mObjLevel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBgLevel() {
            Log.i(SA_PortraitLighting.TAG, "mBGLevel = " + mBGLevel);
            return mBGLevel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isDetectedFaces() {
            return mIsDetectFaces;
        }

        private short getFix8(float fValue) {
            short sValue = (short) ((256.0f * fValue) + 0.5f);
            return sValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getMainGamma() {
            Log.i(SA_PortraitLighting.TAG, "MainGamma = " + mMainGammaTbl[mObjLevel]);
            return mMainGammaTbl[mObjLevel];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getSubGamma() {
            Log.i(SA_PortraitLighting.TAG, "SubGamma = " + mSubGammaTbl[mBGLevel]);
            return mSubGammaTbl[mBGLevel];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getLceGain() {
            float fLceGain = mLceGainTbl[mObjLevel];
            short sLceGain = getFix8(fLceGain);
            Log.i(SA_PortraitLighting.TAG, "sLceGain = " + ((int) sLceGain));
            return sLceGain;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getDrangeCoef() {
            float fDrangeCoef = mDrangeCoefTbl[mObjLevel];
            short sDrangeCoef = getFix8(fDrangeCoef);
            Log.i(SA_PortraitLighting.TAG, "sDrangeCoef = " + ((int) sDrangeCoef));
            return sDrangeCoef;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getCGainMax() {
            float fCGain = mCGainMax[mObjLevel];
            short sCGain = getFix8(fCGain);
            Log.i(SA_PortraitLighting.TAG, "sCGain = " + ((int) sCGain));
            return sCGain;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getTransWidRatio() {
            float fTransWidRatio = mTransWidRatio[mObjLevel];
            short sTransWidRatio = getFix8(fTransWidRatio);
            Log.i(SA_PortraitLighting.TAG, "sTransWidRatio = " + ((int) sTransWidRatio));
            return sTransWidRatio;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getLcelpfGain() {
            float fLcelpfGain = mLcelpfGainTbl[mObjLevel];
            short sLcelpfGain = getFix8(fLcelpfGain);
            Log.i(SA_PortraitLighting.TAG, "sLcelpfGain = " + ((int) sLcelpfGain));
            return sLcelpfGain;
        }

        private boolean isViewPatternReversed() {
            DisplayModeObserver dispObserver = DisplayModeObserver.getInstance();
            int device = dispObserver.getActiveDevice();
            if (device != 0) {
                return false;
            }
            DisplayManager.DeviceStatus deviceStatus = dispObserver.getActiveDeviceStatus();
            int viewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
            if (viewPattern == 1) {
                Log.i(SA_PortraitLighting.TAG, "VIEW_PATTERN_REVERSE_OSD180");
                return true;
            }
            Log.i(SA_PortraitLighting.TAG, "VIEW_PATTERN_NORMAL");
            return false;
        }
    }
}
