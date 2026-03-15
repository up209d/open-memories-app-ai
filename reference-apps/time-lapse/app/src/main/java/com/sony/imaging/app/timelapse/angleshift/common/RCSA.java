package com.sony.imaging.app.timelapse.angleshift.common;

import android.graphics.RectF;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class RCSA {
    public static final boolean FADE_OFF = false;
    public static final boolean FADE_ON = true;
    public static final int HEIGHT_1080 = 1080;
    private static final int LANCZOS3_TABLE_SIZE = 1537;
    private static final int LSI_TYPE_AVIP = 1;
    private static final int SA_COEF_WORK_SIZE = 43200;
    private static final int SA_PARAM_SIZE = 4096;
    public static final int WIDTH_1920 = 1920;
    private boolean DEBUG = true;
    private static final String TAG = RCSA.class.getName();
    private static int mInWidth = 0;
    private static int mInHeight = 0;
    private static int mOutWidth = 0;
    private static int mOutHeight = 0;
    private static byte mFader = 0;
    private static DSP mDSP1 = null;
    private static DSP mDSP2 = null;
    private static String mSaLibPath = null;
    private static int mInOffset = 0;
    private static int mOutOffset = 0;
    private static short mTransParency = 0;
    private static RectF mCropRectF = null;
    private static byte mEnableRC = 1;
    private static final short[] mLancos3 = {0, 2, 4, 7, 9, 12, 14, 16, 19, 21, 24, 27, 29, 32, 34, 37, 40, 43, 45, 48, 51, 54, 57, 60, 63, 66, 69, 72, 75, 78, 81, 84, 87, 90, 93, 97, 100, 103, 106, 109, 112, 116, 119, 122, 125, 129, 132, 135, 138, 141, 145, 148, 151, 154, 157, 161, 164, 167, 170, 173, 176, 180, 183, 186, 189, 192, 195, 198, 201, 204, 207, 210, 213, 215, 218, 221, 224, 226, 229, 232, 234, 237, 239, 242, 244, 247, 249, 251, 254, 256, 258, 260, 262, 264, 266, 268, 269, 271, 273, 274, 276, 278, 279, 280, 282, 283, 284, 285, 286, 287, 288, 288, 289, 290, 290, 291, 291, 291, 291, 291, 291, 291, 291, 291, 291, 290, 290, 289, 288, 287, 286, 285, 284, 283, 281, 280, 278, 277, 275, 273, 271, 269, 267, 264, 262, 259, 257, 254, 251, 248, 245, 242, 238, 235, 231, 227, 223, 219, 215, 211, 207, 202, 198, 193, 188, 183, 178, 173, 167, 162, 156, 151, 145, 139, 133, 126, 120, 113, 107, 100, 93, 86, 79, 72, 64, 57, 49, 41, 33, 25, 17, 8, 0, -9, -17, -26, -35, -44, -54, -63, -72, -82, -92, -102, -112, -122, -132, -142, -153, -163, -174, -185, -196, -207, -218, -230, -241, -253, -264, -276, -288, -300, -312, -324, -337, -349, -362, -374, -387, -400, -413, -426, -439, -452, -466, -479, -493, -506, -520, -534, -548, -562, -576, -590, -604, -618, -632, -647, -661, -676, -691, -705, -720, -735, -750, -765, -780, -795, -810, -825, -840, -855, -871, -886, -901, -917, -932, -948, -963, -979, -994, -1010, -1025, -1041, -1057, -1072, -1088, -1103, -1119, -1135, -1150, -1166, -1182, -1197, -1213, -1228, -1244, -1260, -1275, -1291, -1306, -1321, -1337, -1352, -1367, -1383, -1398, -1413, -1428, -1443, -1458, -1473, -1488, -1503, -1518, -1532, -1547, -1561, -1576, -1590, -1604, -1618, -1632, -1646, -1660, -1674, -1687, -1701, -1714, -1727, -1740, -1753, -1766, -1779, -1791, -1804, -1816, -1828, -1840, -1852, -1863, -1875, -1886, -1897, -1908, -1919, -1930, -1940, -1950, -1960, -1970, -1980, -1989, -1998, -2007, -2016, -2025, -2033, -2041, -2049, -2057, -2064, -2072, -2079, -2085, -2092, -2098, -2104, -2110, -2115, -2120, -2125, -2130, -2134, -2138, -2142, -2146, -2149, -2152, -2154, -2157, -2159, -2161, -2162, -2163, -2164, -2164, -2165, -2164, -2164, -2163, -2162, -2160, -2159, -2156, -2154, -2151, -2148, -2144, -2140, -2136, -2131, -2126, -2121, -2115, -2109, -2102, -2096, -2088, -2081, -2073, -2064, -2055, -2046, -2036, -2026, -2016, -2005, -1994, -1982, -1970, -1957, -1944, -1931, -1917, -1903, -1889, -1873, -1858, -1842, -1826, -1809, -1792, -1774, -1756, -1737, -1718, -1699, -1679, -1659, -1638, -1617, -1595, -1573, -1550, -1527, -1503, -1479, -1455, -1430, -1405, -1379, -1352, -1326, -1298, -1271, -1242, -1214, -1185, -1155, -1125, -1094, -1063, -1032, -1000, -967, -934, -901, -867, -832, -798, -762, -726, -690, -653, -616, -578, -540, -501, -462, -423, -382, -342, -301, -259, -217, -175, -132, -88, -44, 0, 45, 90, 136, 182, 229, 276, 324, 372, 420, 469, 519, 569, 619, 670, 721, 773, 825, 878, 931, 984, 1038, 1093, 1147, 1203, 1258, 1314, 1371, 1428, 1485, 1543, 1601, 1659, 1718, 1778, 1838, 1898, 1958, 2019, 2081, 2143, 2205, 2267, 2330, 2393, 2457, 2521, 2585, 2650, 2715, 2781, 2846, 2913, 2979, 3046, 3113, 3181, 3248, 3316, 3385, 3454, 3523, 3592, 3662, 3732, 3802, 3873, 3943, 4015, 4086, 4158, 4229, 4302, 4374, 4447, 4520, 4593, 4666, 4740, 4814, 4888, 4962, 5037, 5112, 5186, 5262, 5337, 5412, 5488, 5564, 5640, 5716, 5793, 5869, 5946, 6022, 6099, 6176, 6254, 6331, 6408, 6486, 6564, 6641, 6719, 6797, 6875, 6953, 7031, 7109, 7188, 7266, 7344, 7423, 7501, 7579, 7658, 7736, 7815, 7893, 7972, 8050, 8129, 8207, 8286, 8364, 8442, 8520, 8599, 8677, 8755, 8833, 8911, 8989, 9066, 9144, 9222, 9299, 9376, 9454, 9531, 9608, 9684, 9761, 9838, 9914, 9990, 10066, 10142, 10217, 10293, 10368, 10443, 10518, 10593, 10667, 10741, 10815, 10889, 10962, 11035, 11108, 11181, 11253, 11325, 11397, 11468, 11539, 11610, 11681, 11751, 11821, 11891, 11960, 12029, 12097, 12165, 12233, 12301, 12368, 12435, 12501, 12567, 12633, 12698, 12762, 12827, 12891, 12954, 13017, 13080, 13142, 13204, 13265, 13326, 13386, 13446, 13506, 13565, 13623, 13681, 13739, 13796, 13852, 13908, 13964, 14019, 14073, 14127, 14180, 14233, 14285, 14337, 14388, 14439, 14489, 14538, 14587, 14636, 14683, 14731, 14777, 14823, 14869, 14913, 14958, 15001, 15044, 15087, 15128, 15169, 15210, 15250, 15289, 15327, 15365, 15403, 15439, 15475, 15511, 15545, 15579, 15613, 15645, 15677, 15708, 15739, 15769, 15798, 15827, 15855, 15882, 15908, 15934, 15959, 15984, 16007, 16030, 16053, 16074, 16095, 16115, 16135, 16153, 16171, 16189, 16205, 16221, 16236, 16251, 16264, 16277, 16289, 16301, 16311, 16321, 16331, 16339, 16347, 16354, 16360, 16366, 16371, 16375, 16378, 16381, 16383, 16384, 16384, 16384, 16383, 16381, 16378, 16375, 16371, 16366, 16360, 16354, 16347, 16339, 16331, 16321, 16311, 16301, 16289, 16277, 16264, 16251, 16236, 16221, 16205, 16189, 16171, 16153, 16135, 16115, 16095, 16074, 16053, 16030, 16007, 15984, 15959, 15934, 15908, 15882, 15855, 15827, 15798, 15769, 15739, 15708, 15677, 15645, 15613, 15579, 15545, 15511, 15475, 15439, 15403, 15365, 15327, 15289, 15250, 15210, 15169, 15128, 15087, 15044, 15001, 14958, 14913, 14869, 14823, 14777, 14731, 14683, 14636, 14587, 14538, 14489, 14439, 14388, 14337, 14285, 14233, 14180, 14127, 14073, 14019, 13964, 13908, 13852, 13796, 13739, 13681, 13623, 13565, 13506, 13446, 13386, 13326, 13265, 13204, 13142, 13080, 13017, 12954, 12891, 12827, 12762, 12698, 12633, 12567, 12501, 12435, 12368, 12301, 12233, 12165, 12097, 12029, 11960, 11891, 11821, 11751, 11681, 11610, 11539, 11468, 11397, 11325, 11253, 11181, 11108, 11035, 10962, 10889, 10815, 10741, 10667, 10593, 10518, 10443, 10368, 10293, 10217, 10142, 10066, 9990, 9914, 9838, 9761, 9684, 9608, 9531, 9454, 9376, 9299, 9222, 9144, 9066, 8989, 8911, 8833, 8755, 8677, 8599, 8520, 8442, 8364, 8286, 8207, 8129, 8050, 7972, 7893, 7815, 7736, 7658, 7579, 7501, 7423, 7344, 7266, 7188, 7109, 7031, 6953, 6875, 6797, 6719, 6641, 6564, 6486, 6408, 6331, 6254, 6176, 6099, 6022, 5946, 5869, 5793, 5716, 5640, 5564, 5488, 5412, 5337, 5262, 5186, 5112, 5037, 4962, 4888, 4814, 4740, 4666, 4593, 4520, 4447, 4374, 4302, 4229, 4158, 4086, 4015, 3943, 3873, 3802, 3732, 3662, 3592, 3523, 3454, 3385, 3316, 3248, 
    3181, 3113, 3046, 2979, 2913, 2846, 2781, 2715, 2650, 2585, 2521, 2457, 2393, 2330, 2267, 2205, 2143, 2081, 2019, 1958, 1898, 1838, 1778, 1718, 1659, 1601, 1543, 1485, 1428, 1371, 1314, 1258, 1203, 1147, 1093, 1038, 984, 931, 878, 825, 773, 721, 670, 619, 569, 519, 469, 420, 372, 324, 276, 229, 182, 136, 90, 45, 0, -44, -88, -132, -175, -217, -259, -301, -342, -382, -423, -462, -501, -540, -578, -616, -653, -690, -726, -762, -798, -832, -867, -901, -934, -967, -1000, -1032, -1063, -1094, -1125, -1155, -1185, -1214, -1242, -1271, -1298, -1326, -1352, -1379, -1405, -1430, -1455, -1479, -1503, -1527, -1550, -1573, -1595, -1617, -1638, -1659, -1679, -1699, -1718, -1737, -1756, -1774, -1792, -1809, -1826, -1842, -1858, -1873, -1889, -1903, -1917, -1931, -1944, -1957, -1970, -1982, -1994, -2005, -2016, -2026, -2036, -2046, -2055, -2064, -2073, -2081, -2088, -2096, -2102, -2109, -2115, -2121, -2126, -2131, -2136, -2140, -2144, -2148, -2151, -2154, -2156, -2159, -2160, -2162, -2163, -2164, -2164, -2165, -2164, -2164, -2163, -2162, -2161, -2159, -2157, -2154, -2152, -2149, -2146, -2142, -2138, -2134, -2130, -2125, -2120, -2115, -2110, -2104, -2098, -2092, -2085, -2079, -2072, -2064, -2057, -2049, -2041, -2033, -2025, -2016, -2007, -1998, -1989, -1980, -1970, -1960, -1950, -1940, -1930, -1919, -1908, -1897, -1886, -1875, -1863, -1852, -1840, -1828, -1816, -1804, -1791, -1779, -1766, -1753, -1740, -1727, -1714, -1701, -1687, -1674, -1660, -1646, -1632, -1618, -1604, -1590, -1576, -1561, -1547, -1532, -1518, -1503, -1488, -1473, -1458, -1443, -1428, -1413, -1398, -1383, -1367, -1352, -1337, -1321, -1306, -1291, -1275, -1260, -1244, -1228, -1213, -1197, -1182, -1166, -1150, -1135, -1119, -1103, -1088, -1072, -1057, -1041, -1025, -1010, -994, -979, -963, -948, -932, -917, -901, -886, -871, -855, -840, -825, -810, -795, -780, -765, -750, -735, -720, -705, -691, -676, -661, -647, -632, -618, -604, -590, -576, -562, -548, -534, -520, -506, -493, -479, -466, -452, -439, -426, -413, -400, -387, -374, -362, -349, -337, -324, -312, -300, -288, -276, -264, -253, -241, -230, -218, -207, -196, -185, -174, -163, -153, -142, -132, -122, -112, -102, -92, -82, -72, -63, -54, -44, -35, -26, -17, -9, 0, 8, 17, 25, 33, 41, 49, 57, 64, 72, 79, 86, 93, 100, 107, 113, 120, 126, 133, 139, 145, 151, 156, 162, 167, 173, 178, 183, 188, 193, 198, 202, 207, 211, 215, 219, 223, 227, 231, 235, 238, 242, 245, 248, 251, 254, 257, 259, 262, 264, 267, 269, 271, 273, 275, 277, 278, 280, 281, 283, 284, 285, 286, 287, 288, 289, 290, 290, 291, 291, 291, 291, 291, 291, 291, 291, 291, 291, 290, 290, 289, 288, 288, 287, 286, 285, 284, 283, 282, 280, 279, 278, 276, 274, 273, 271, 269, 268, 266, 264, 262, 260, 258, 256, 254, 251, 249, 247, 244, 242, 239, 237, 234, 232, 229, 226, 224, 221, 218, 215, 213, 210, 207, 204, 201, 198, 195, 192, 189, 186, 183, 180, 176, 173, 170, 167, 164, 161, 157, 154, 151, 148, 145, 141, 138, 135, 132, 129, 125, 122, 119, 116, 112, 109, 106, 103, 100, 97, 93, 90, 87, 84, 81, 78, 75, 72, 69, 66, 63, 60, 57, 54, 51, 48, 45, 43, 40, 37, 34, 32, 29, 27, 24, 21, 19, 16, 14, 12, 9, 7, 4, 2, 0};
    private static DeviceBuffer mBootParam1 = null;
    private static DeviceBuffer mBootParam2 = null;
    private static DeviceBuffer mSAParam1 = null;
    private static DeviceBuffer mSAParam2 = null;
    private static ByteBuffer mSAParam_buf = null;
    private static DeviceBuffer mCoefWork = null;

    /* loaded from: classes.dex */
    public static class Options {
        public int inWidth = 1920;
        public int inHeight = 1080;
        public int outWidth = 1920;
        public int outHeight = 1080;
        public boolean fader = false;
        public DSP dsp1 = null;
        public DSP dsp2 = null;
        public String libpath = null;
    }

    /* loaded from: classes.dex */
    public static class VOptions {
        public int transparency = 32768;
        public RectF rectf = null;
    }

    private boolean isMultiUnit() {
        return mDSP2 != null;
    }

    public boolean initParam(Options options) {
        if (options == null) {
            return false;
        }
        mInWidth = options.inWidth;
        mInHeight = options.inHeight;
        mOutWidth = options.outWidth;
        mOutHeight = options.outHeight;
        if (options.fader) {
            mFader = (byte) 1;
        } else {
            mFader = (byte) 0;
        }
        mSaLibPath = options.libpath;
        mDSP1 = options.dsp1;
        mDSP2 = options.dsp2;
        if (isAVIP()) {
            if (isMultiUnit()) {
                mDSP1.setProgram(mSaLibPath + "libsa_resolution_conv_avip_mu_unit0.so");
                mDSP2.setProgram(mSaLibPath + "libsa_resolution_conv_avip_mu_unit1.so");
            } else {
                mDSP1.setProgram(mSaLibPath + "libsa_resolution_conv_avip.so");
            }
        } else if (isMultiUnit()) {
            mDSP1.setProgram(mSaLibPath + "libsa_resolution_conv_musashi_mu_unit0.so");
            mDSP2.setProgram(mSaLibPath + "libsa_resolution_conv_musashi_mu_unit1.so");
        } else {
            mDSP1.setProgram(mSaLibPath + "libsa_resolution_conv_musashi.so");
        }
        mBootParam1 = mDSP1.createBuffer(60);
        mSAParam1 = mDSP1.createBuffer(SA_PARAM_SIZE);
        if (isMultiUnit()) {
            mBootParam2 = mDSP2.createBuffer(60);
            mSAParam2 = mDSP2.createBuffer(SA_PARAM_SIZE);
        }
        mSAParam_buf = null;
        return true;
    }

    public boolean getFrame(OptimizedImage inImage, OptimizedImage outImage, VOptions voptions) {
        mBootParam1 = getBootParam(mBootParam1, 0);
        mCoefWork = mDSP1.createBuffer(SA_COEF_WORK_SIZE);
        mInOffset = mDSP1.getPropertyAsInt(inImage, "image-canvas-width");
        mOutOffset = mDSP1.getPropertyAsInt(outImage, "image-canvas-width");
        mTransParency = (short) voptions.transparency;
        mCropRectF = voptions.rectf;
        mSAParam1 = getSAParam1(mSAParam1, inImage, outImage);
        if (isMultiUnit()) {
            mBootParam2 = getBootParam(mBootParam2, 1);
            mSAParam2 = getSAParam2(mSAParam2, inImage, outImage);
        }
        boolean result = exec_sa(inImage, outImage);
        inImage.release();
        mCoefWork.release();
        mCoefWork = null;
        return result;
    }

    private boolean exec_sa(OptimizedImage inImage, OptimizedImage outImage) {
        boolean result = true;
        mDSP1.setArg(0, mBootParam1);
        mDSP1.setArg(1, mSAParam1);
        mDSP1.setArg(2, inImage);
        mDSP1.setArg(3, outImage);
        mDSP1.setArg(4, mCoefWork);
        if (isMultiUnit()) {
            mDSP2.setArg(0, mBootParam2);
            mDSP2.setArg(1, mSAParam2);
            mDSP2.setArg(2, inImage);
            mDSP2.setArg(3, outImage);
            mDSP2.setArg(4, mCoefWork);
            try {
                Thread thread = new Thread(new Runnable() { // from class: com.sony.imaging.app.timelapse.angleshift.common.RCSA.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AppLog.info(RCSA.TAG, "start1");
                        boolean result1 = RCSA.mDSP1.execute();
                        if (result1) {
                            AppLog.info(RCSA.TAG, "success1");
                        } else {
                            AppLog.info(RCSA.TAG, "failure1");
                        }
                    }
                });
                thread.start();
                AppLog.info(TAG, "start2");
                result = mDSP2.execute();
                if (result) {
                    AppLog.info(TAG, "success2");
                } else {
                    AppLog.info(TAG, "failure2");
                }
                thread.join();
            } catch (InterruptedException e) {
                AppLog.info(TAG, "Exception");
            }
        } else {
            result = mDSP1.execute();
            if (result) {
                AppLog.info(TAG, "success1");
            } else {
                AppLog.info(TAG, "failure1");
            }
        }
        return result;
    }

    private DeviceBuffer getBootParam(DeviceBuffer boot_param, int unit_num) {
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
        buf.order(ByteOrder.nativeOrder());
        if (unit_num == 0) {
            buf.putInt(mDSP1.getPropertyAsInt("program-descriptor"));
        } else {
            buf.putInt(mDSP2.getPropertyAsInt("program-descriptor"));
        }
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

    private DeviceBuffer getSAParam1(DeviceBuffer sa_param, OptimizedImage inImage, OptimizedImage outImage) {
        if (mSAParam_buf == null) {
            mSAParam_buf = ByteBuffer.allocateDirect(SA_PARAM_SIZE);
            mSAParam_buf.order(ByteOrder.nativeOrder());
            setAllSAParams(inImage, outImage);
        } else {
            setChangedSAParams(inImage, outImage);
        }
        sa_param.write(mSAParam_buf);
        return sa_param;
    }

    private DeviceBuffer getSAParam2(DeviceBuffer sa_param, OptimizedImage inImage, OptimizedImage outImage) {
        mSAParam_buf.rewind();
        sa_param.write(mSAParam_buf);
        return sa_param;
    }

    private void setAllSAParams(OptimizedImage inImage, OptimizedImage outImage) {
        int inAddr = mDSP1.getPropertyAsInt(inImage, "memory-address");
        int offset = mDSP1.getPropertyAsInt(inImage, "image-data-offset");
        if (this.DEBUG) {
            AppLog.info(TAG, "inAddr:" + inAddr);
            AppLog.info(TAG, "offset(inAddr):" + offset);
        }
        int inAddr2 = inAddr + offset;
        int outAddr = mDSP1.getPropertyAsInt(outImage, "memory-address");
        int offset2 = mDSP1.getPropertyAsInt(outImage, "image-data-offset");
        if (this.DEBUG) {
            AppLog.info(TAG, "outAddr:" + inAddr2);
            AppLog.info(TAG, "offset(outAddr):" + offset2);
        }
        int outAddr2 = outAddr + offset2;
        int coefworkAddr = mDSP1.getPropertyAsInt(mCoefWork, "memory-address");
        mSAParam_buf.putInt(getAXIAddr(inAddr2));
        mSAParam_buf.putShort((short) mInWidth);
        mSAParam_buf.putShort((short) mInOffset);
        mSAParam_buf.putShort((short) mInHeight);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putInt(0);
        if (this.DEBUG) {
            AppLog.info(TAG, "puc_in_yci:" + getAXIAddr(inAddr2));
            AppLog.info(TAG, "ui_width_yci:" + mInWidth);
            AppLog.info(TAG, "input_yci_offset:" + mInOffset);
            AppLog.info(TAG, "ui_yci_height:" + mInHeight);
        }
        mSAParam_buf.putInt(getAXIAddr(outAddr2));
        mSAParam_buf.putShort((short) mOutWidth);
        mSAParam_buf.putShort((short) mOutOffset);
        mSAParam_buf.putShort((short) mOutHeight);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putInt(0);
        if (this.DEBUG) {
            AppLog.info(TAG, "puc_in_yco:" + getAXIAddr(outAddr2));
            AppLog.info(TAG, "ui_width_yco:" + mOutWidth);
            AppLog.info(TAG, "input_yco_offset:" + mOutOffset);
            AppLog.info(TAG, "ui_yco_height:" + mOutHeight);
        }
        mSAParam_buf.putInt(0);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putInt(0);
        if (this.DEBUG) {
            AppLog.info(TAG, "puc_in_ycv:0");
            AppLog.info(TAG, "ui_width_ycv:0");
            AppLog.info(TAG, "input_ycv_offset:0");
            AppLog.info(TAG, "ui_ycv_height:0");
        }
        mSAParam_buf.put(mEnableRC);
        mSAParam_buf.put(mFader);
        mSAParam_buf.put((byte) 0);
        mSAParam_buf.put((byte) 0);
        if (this.DEBUG) {
            AppLog.info(TAG, "execRC:" + ((int) mEnableRC));
            AppLog.info(TAG, "execAlpha:" + ((int) mFader));
            AppLog.info(TAG, "dummy0:0");
            AppLog.info(TAG, "dummy0:0");
        }
        float fCenterX = (mCropRectF.left + mCropRectF.right) / 2.0f;
        float fCenterY = (mCropRectF.top + mCropRectF.bottom) / 2.0f;
        float fSizeX = mCropRectF.right - mCropRectF.left;
        float fSizeY = mCropRectF.bottom - mCropRectF.top;
        mSAParam_buf.putFloat(fCenterX);
        mSAParam_buf.putFloat(fCenterY);
        mSAParam_buf.putFloat(fSizeX);
        mSAParam_buf.putFloat(fSizeY);
        if (this.DEBUG) {
            AppLog.info(TAG, "fCenterX:" + fCenterX);
            AppLog.info(TAG, "fCenterY:" + fCenterY);
            AppLog.info(TAG, "fSizeX:" + fSizeX);
            AppLog.info(TAG, "fSizeY:" + fSizeY);
        }
        mSAParam_buf.putInt(mOutWidth);
        mSAParam_buf.putInt(mOutHeight);
        if (this.DEBUG) {
            AppLog.info(TAG, "dstSizeX:" + mOutWidth);
            AppLog.info(TAG, "dstSizeY:" + mOutHeight);
        }
        mSAParam_buf.putShort(mTransParency);
        if (this.DEBUG) {
            AppLog.info(TAG, "Alpha:" + ((int) mTransParency));
        }
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putInt(getAXIAddr(coefworkAddr));
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        mSAParam_buf.putInt(0);
        for (int i = 0; i < mLancos3.length; i++) {
            mSAParam_buf.putShort(mLancos3[i]);
        }
    }

    private void setChangedSAParams(OptimizedImage inImage, OptimizedImage outImage) {
        int inAddr = mDSP1.getPropertyAsInt(inImage, "memory-address");
        int offset = mDSP1.getPropertyAsInt(inImage, "image-data-offset");
        if (this.DEBUG) {
            AppLog.info(TAG, "inAddr:" + inAddr);
            AppLog.info(TAG, "offset(inAddr):" + offset);
        }
        int inAddr2 = inAddr + offset;
        int outAddr = mDSP1.getPropertyAsInt(outImage, "memory-address");
        int offset2 = mDSP1.getPropertyAsInt(outImage, "image-data-offset");
        if (this.DEBUG) {
            AppLog.info(TAG, "outAddr:" + inAddr2);
            AppLog.info(TAG, "offset(outAddr):" + offset2);
        }
        int coefworkAddr = mDSP1.getPropertyAsInt(mCoefWork, "memory-address");
        mSAParam_buf.rewind();
        mSAParam_buf.putInt(getAXIAddr(inAddr2));
        mSAParam_buf.position(6);
        mSAParam_buf.putShort((short) mInOffset);
        mSAParam_buf.position(16);
        mSAParam_buf.putInt(getAXIAddr(outAddr + offset2));
        mSAParam_buf.position(22);
        mSAParam_buf.putShort((short) mOutOffset);
        float fCenterX = (mCropRectF.left + mCropRectF.right) / 2.0f;
        float fCenterY = (mCropRectF.top + mCropRectF.bottom) / 2.0f;
        float fSizeX = mCropRectF.right - mCropRectF.left;
        if (fSizeX < mOutWidth) {
            fSizeX = mOutWidth;
        }
        float fSizeY = mCropRectF.bottom - mCropRectF.top;
        if (fSizeY < mOutHeight) {
            fSizeY = mOutHeight;
        }
        mSAParam_buf.position(52);
        mSAParam_buf.putFloat(fCenterX);
        mSAParam_buf.putFloat(fCenterY);
        mSAParam_buf.putFloat(fSizeX);
        mSAParam_buf.putFloat(fSizeY);
        if (this.DEBUG) {
            AppLog.info(TAG, "mCropRectF:" + mCropRectF);
            AppLog.info(TAG, "fCenterX:" + fCenterX);
            AppLog.info(TAG, "fCenterY:" + fCenterY);
            AppLog.info(TAG, "fSizeX:" + fSizeX);
            AppLog.info(TAG, "fSizeY:" + fSizeY);
        }
        mSAParam_buf.position(76);
        mSAParam_buf.putShort(mTransParency);
        if (this.DEBUG) {
            AppLog.info(TAG, "Alpha:" + ((int) mTransParency));
        }
        mSAParam_buf.putShort((short) 0);
        mSAParam_buf.putInt(getAXIAddr(coefworkAddr));
    }

    public void release() {
        if (mBootParam1 != null) {
            mBootParam1.release();
            mBootParam1 = null;
        }
        if (mBootParam2 != null) {
            mBootParam2.release();
            mBootParam2 = null;
        }
        if (mSAParam1 != null) {
            mSAParam1.release();
            mSAParam1 = null;
        }
        if (mSAParam2 != null) {
            mSAParam2.release();
            mSAParam2 = null;
        }
        if (mCoefWork != null) {
            mCoefWork.release();
            mCoefWork = null;
        }
        mCropRectF = null;
    }

    private int getAXIAddr(int addr) {
        if (isAVIP()) {
            int axiAddr = addr & Integer.MAX_VALUE;
            return axiAddr;
        }
        int axiAddr2 = addr & 1073741823;
        return axiAddr2;
    }

    private static int getLsiType() {
        String pfVer = ScalarProperties.getString("version.platform");
        float lsiType = Float.parseFloat(pfVer);
        return (int) lsiType;
    }

    public static boolean isAVIP() {
        return 1 == getLsiType();
    }
}
