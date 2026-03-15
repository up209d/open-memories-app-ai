package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.lightgraffiti.LightGraffitiApp;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class LGSAMixFilter {
    public static final int FILTER_MODE_COMP = 2;
    public static final int FILTER_MODE_COMP_SFR = 4;
    public static final int FILTER_MODE_DIFF = 0;
    public static final int FILTER_MODE_DIFF_LPF = 3;
    public static final int FILTER_MODE_LPF = 1;
    private static final int LSI_TYPE_AVIP = 1;
    private static final int LSI_TYPE_MUSASHI_KOJIRO = 2;
    public static final int MIX_MODE_ADDITIVE = 1;
    public static final int MIX_MODE_SCREAN = 2;
    private DeviceBuffer bootParam = null;
    private ByteBuffer bootParam_buf = null;
    private char[] gGammaTable = {0, 21, '*', '?', 'T', 'i', '~', 147, 168, 189, 210, 231, 252, 273, 294, 315, 336, 357, 378, 399, 420, 441, 462, 483, 504, 526, 547, 568, 589, 610, 631, 652, 673, 694, 715, 736, 757, 778, 799, 820, 841, 862, 883, 904, 925, 946, 967, 988, 1009, 1030, 1051, 1072, 1093, 1114, 1135, 1156, 1177, 1198, 1219, 1240, 1261, 1282, 1303, 1324, 1345, 1366, 1387, 1408, 1429, 1449, 1470, 1491, 1511, 1532, 1552, 1573, 1593, 1613, 1634, 1654, 1674, 1694, 1715, 1735, 1755, 1775, 1795, 1816, 1836, 1856, 1876, 1896, 1917, 1937, 1957, 1978, 1998, 2018, 2039, 2059, 2080, 2100, 2121, 2142, 2163, 2184, 2204, 2225, 2247, 2268, 2289, 2310, 2332, 2353, 2375, 2397, 2419, 2440, 2463, 2485, 2507, 2530, 2552, 2575, 2598, 2621, 2644, 2667, 2691, 2714, 2738, 2761, 2785, 2808, 2832, 2856, 2880, 2903, 2927, 2951, 2975, 2999, 3023, 3047, 3071, 3095, 3119, 3144, 3168, 3193, 3217, 3242, 3266, 3291, 3316, 3341, 3366, 3391, 3416, 3442, 3467, 3493, 3519, 3544, 3570, 3596, 3623, 3649, 3675, 3702, 3729, 3755, 3782, 3809, 3837, 3864, 3892, 3919, 3947, 3975, 4004, 4032, 4061, 4089, 4118, 4147, 4177, 4206, 4236, 4266, 4296, 4326, 4356, 4387, 4417, 4448, 4479, 4510, 4541, 4572, 4603, 4634, 4665, 4697, 4728, 4760, 4791, 4823, 4855, 4887, 4919, 4952, 4984, 5017, 5049, 5082, 5115, 5149, 5182, 5215, 5249, 5283, 5317, 5351, 5385, 5420, 5454, 5489, 5524, 5560, 5595, 5631, 5667, 5703, 5739, 5775, 5812, 5849, 5886, 5924, 5961, 5999, 6037, 6076, 6114, 6153, 6192, 6232, 6271, 6311, 6351, 6392, 6433, 6474, 6515, 6556, 6598, 6640, 6681, 6722, 6763, 6803, 6843, 6883, 6922, 6961, 7000, 7039, 7077, 7116, 7154, 7193, 7231, 7270, 7309, 7347, 7386, 7425, 7464, 7504, 7544, 7584, 7624, 7665, 7706, 7748, 7790, 7832, 7876, 7919, 7964, 8009, 8054, 8100, 8148, 8195, 8244, 8293, 8344, 8395, 8447, 8500, 8554, 8610, 8666, 8723, 8782, 8841, 8902, 8964, 9027, 9092, 9158, 9225, 9294, 9364, 9436, 9509, 9583, 9660, 9737, 9816, 9897, 9978, 10060, 10143, 10227, 10312, 10399, 10486, 10574, 10663, 10753, 10844, 10936, 11029, 11123, 11218, 11314, 11411, 11508, 11607, 11707, 11807, 11909, 12011, 12114, 12218, 12323, 12429, 12536, 12644, 12752, 12862, 12972, 13083, 13195, 13308, 13422, 13536, 13651, 13768, 13885, 14002, 14121, 14240, 14361, 14482, 14604, 14726, 14850, 14974, 15099, 15224, 15351, 15478, 15606, 15735, 15864, 15994, 16125, 16257, 16389, 16522, 16656, 16791, 16928, 17067, 17207, 17350, 17494, 17639, 17787, 17935, 18085, 18237, 18390, 18544, 18700, 18856, 19014, 19173, 19333, 19494, 19656, 19819, 19982, 20147, 20312, 20478, 20644, 20811, 20979, 21147, 21315, 21484, 21653, 21822, 21992, 22161, 22331, 22501, 22670, 22840, 23010, 23179, 23348, 23517, 23686, 23854, 24021, 24189, 24355, 24522, 24687, 24852, 25016, 25179, 25341, 25503, 25663, 25823, 25981, 26138, 26294, 26449, 26603, 26755, 26906, 27056, 27205, 27354, 27503, 27651, 27799, 27946, 28093, 28239, 28385, 28531, 28676, 28821, 28965, 29109, 29253, 29397, 29540, 29682, 29825, 29967, 30109, 30250, 30391, 30532, 30673, 30813, 30953, 31093, 31232, 31371, 31510, 31649, 31788, 31926, 32064, 32202, 32340, 32477, 32614, 32752, 32889, 33025, 33162, 33299, 33435, 33571, 33707, 33843, 33979, 34115, 34251, 34386, 34522, 34657, 34793, 34928, 35063, 35199, 35334, 35469, 35604, 35739, 35874, 36010, 36146, 36282, 36418, 36555, 36693, 36830, 36968, 37106, 37244, 37383, 37521, 37660, 37799, 37938, 38077, 38215, 38354, 38493, 38632, 38770, 38909, 39047, 39185, 39323, 39461, 39598, 39735, 39871, 40008, 40143, 40279, 40414, 40548, 40682, 40815, 40948, 41080, 41211, 41342, 41472, 41602, 41730, 41858, 41985, 42111, 42236, 42361, 42484, 42607, 42728, 42848, 42968, 43086, 43203, 43319, 43434, 43548, 43660, 43771, 43881, 43990, 44097, 44202, 44307, 44410, 44512, 44613, 44713, 44811, 44909, 45005, 45100, 45195, 45288, 45380, 45471, 45561, 45651, 45739, 45826, 45913, 45999, 46084, 46168, 46251, 46333, 46415, 46496, 46577, 46656, 46735, 46814, 46891, 46969, 47045, 47121, 47197, 47272, 47346, 47420, 47494, 47567, 47640, 47713, 47785, 47857, 47928, 47999, 48070, 48141, 48211, 48282, 48352, 48422, 48492, 48561, 48631, 48700, 48770, 48840, 48909, 48979, 49048, 49118, 49188, 49257, 49327, 49397, 49467, 49536, 49604, 49672, 49740, 49807, 49874, 49940, 50006, 50071, 50137, 50201, 50266, 50330, 50393, 50457, 50520, 50582, 50645, 50707, 50768, 50830, 50891, 50952, 51012, 51072, 51132, 51192, 51251, 51310, 51369, 51428, 51487, 51545, 51603, 51661, 51718, 51776, 51833, 51890, 51947, 52004, 52060, 52117, 52173, 52229, 52285, 52341, 52397, 52453, 52508, 52564, 52619, 52675, 52730, 52785, 52841, 52896, 52951, 53006, 53061, 53116, 53171, 53226, 53281, 53336, 53390, 53444, 53499, 53553, 53607, 53660, 53714, 53767, 53820, 53874, 53927, 53979, 54032, 54084, 54137, 54189, 54241, 54293, 54344, 54396, 54447, 54499, 54550, 54601, 54651, 54702, 54752, 54803, 54853, 54903, 54953, 55002, 55052, 55101, 55150, 55199, 55248, 55297, 55345, 55394, 55442, 55490, 55538, 55586, 55633, 55681, 55728, 55775, 55822, 55869, 55916, 55962, 56009, 56055, 56101, 56147, 56193, 56238, 56284, 56329, 56374, 56419, 56464, 56508, 56553, 56597, 56640, 56684, 56727, 56771, 56814, 56856, 56899, 56941, 56983, 57026, 57067, 57109, 57151, 57192, 57233, 57274, 57315, 57356, 57396, 57437, 57477, 57517, 57557, 57597, 57636, 57676, 57715, 57755, 57794, 57833, 57872, 57911, 57950, 57989, 58027, 58066, 58104, 58143, 58181, 58219, 58257, 58296, 58334, 58372, 58409, 58447, 58485, 58523, 58561, 58598, 58636, 58674, 58711, 58749, 58786, 58824, 58862, 58899, 58937, 58974, 59011, 59049, 59086, 59122, 59159, 59196, 59232, 59269, 59305, 59341, 59377, 59413, 59449, 59484, 59520, 59555, 59591, 59626, 59661, 59696, 59731, 59766, 59801, 59836, 59870, 59905, 59940, 59974, 60009, 60043, 60078, 60112, 60146, 60181, 60215, 60249, 60284, 60318, 60352, 60386, 60420, 60455, 60489, 60523, 60557, 60592, 60626, 60660, 60694, 60729, 60763, 60797, 60832, 60866, 60901, 60935, 60970, 61005, 61039, 61074, 61109, 61144, 61179, 61214, 61249, 61284, 61319, 61354, 61389, 61425, 61460, 61495, 61530, 61565, 61601, 61636, 61671, 61706, 61742, 61777, 61812, 61847, 61883, 61918, 61953, 61988, 62024, 62059, 62094, 62130, 62165, 62200, 62235, 62271, 62306, 62341, 62376, 62411, 62447, 62482, 62517, 62552, 62587, 62622, 62657, 62692, 62727, 62762, 62797, 62832, 62867, 62902, 62937, 62972, 63006, 63041, 63076, 63111, 63145, 63180, 63214, 63249, 63283, 63318, 63352, 63387, 63421, 63455, 63489, 63524, 63558, 63592, 63626, 63660, 63694, 63728, 63762, 63796, 63829, 63863, 63897, 63931, 63964, 63998, 64032, 64065, 64099, 64133, 64166, 64200, 64233, 64267, 64300, 64334, 64367, 64400, 64434, 64467, 64501, 64534, 64567, 64601, 64634, 64667, 64701, 64734, 
    64767, 64800, 64834, 64867, 64900, 64934, 64967, 65000, 65034, 65067, 65100, 65134, 65167, 65200, 65234, 65267, 65301, 65334, 65367, 65401, 65434, 65468, 65501, 65535, 65535};
    private DSP mDSP;
    private DeviceBuffer sa_param;
    private ByteBuffer sa_param_buf;
    private static final String TAG = LGSAMixFilter.class.getSimpleName();
    private static boolean DEBUGPRINT = true;

    public LGSAMixFilter() {
        String filePathAndroid;
        this.sa_param = null;
        this.sa_param_buf = null;
        String packagename = LightGraffitiApp.thisApp.getPackageName();
        this.mDSP = DSP.createProcessor("sony-di-dsp");
        if (isAVIP()) {
            filePathAndroid = "/data/lib/" + packagename + "/lib/libsa_lightgraffiti_avip.so";
        } else {
            filePathAndroid = "/data/lib/" + packagename + "/lib/libsa_lightgraffiti_musashi.so";
        }
        Log.i(TAG, "SA file path: /android" + filePathAndroid + " length:" + ("/android" + filePathAndroid).length());
        String filePath = "/android" + filePathAndroid;
        if (filePath.length() > 95) {
            Log.e(TAG, "★ SA file name is too long.  SA file path should be no more than 95.  ");
        }
        if (!new File(filePathAndroid).exists()) {
            Log.e(TAG, "★ SA File not exist.  ");
        }
        this.mDSP.setProgram(filePath);
        Log.d("SA_Mem", "CreateBuffer sa_param 172 byte.");
        this.sa_param = this.mDSP.createBuffer(172);
        this.sa_param_buf = ByteBuffer.allocateDirect(172);
        this.sa_param_buf.order(ByteOrder.nativeOrder());
    }

    public DSP getDSP() {
        return this.mDSP;
    }

    public synchronized void setParamWithDeviceBuffer(DeviceBuffer optImgInput, OptimizedImage optImgOutput2, int targetWidth, int targetHeight, int filterMode) {
        int addressInput;
        int addressOutput2;
        int canvasWidthOutput2;
        int canvasWidthInput;
        Log.d(TAG, "==== setParam with DeviceBuffer");
        if (optImgOutput2 != null) {
            addressInput = this.mDSP.getPropertyAsInt(optImgInput, "memory-address");
            addressOutput2 = this.mDSP.getPropertyAsInt(optImgOutput2, "memory-address");
            canvasWidthOutput2 = this.mDSP.getPropertyAsInt(optImgOutput2, "image-canvas-width");
            canvasWidthInput = targetWidth;
        } else {
            addressInput = this.mDSP.getPropertyAsInt(optImgInput, "memory-address");
            addressOutput2 = addressInput;
            canvasWidthOutput2 = targetWidth;
            canvasWidthInput = targetWidth;
        }
        sa_exec_setParamWithDeviceBuffer(optImgInput, optImgOutput2, addressInput, addressOutput2, targetWidth, targetHeight, canvasWidthInput, canvasWidthOutput2, filterMode);
    }

    protected synchronized void sa_exec_setParamWithDeviceBuffer(DeviceBuffer optImgInput, OptimizedImage optImgOutput2, int addressInput, int addressOutput2, int targetWidth, int targetHeight, int canvasWidthInput, int canvasWidthOutput2, int filterMode) {
        if (isAVIP()) {
            if (addressInput % 8 != 0) {
                Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressInput:" + addressInput + " /8 = " + (addressInput / 8.0d));
            } else if (addressOutput2 % 8 != 0) {
                Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressOutput:" + Integer.toHexString(addressOutput2));
            } else if (targetWidth % 4 != 0) {
                Log.e("sa", "!!!error  Widthは4の倍数  targetWidth:" + targetWidth);
            } else if (canvasWidthInput % 4 != 0) {
                Log.e("sa", "!!!error  入力line offsetが4の倍数であること canvasWidthInput:" + canvasWidthInput);
            } else if (canvasWidthOutput2 % 4 != 0) {
                Log.e("sa", "!!!error  出力line offsetが4の倍数であること canvasWidthOutput:" + canvasWidthOutput2);
            }
        }
        Log.i(TAG, "==== SA params convertedToAxi =  Addrinput:" + convArmAddr2AxiAddr(addressInput) + " targetWidth:" + targetWidth + " targetHeight:" + targetHeight + " canvasWidthInput:" + canvasWidthInput + " canvasWidthOutput2:" + canvasWidthOutput2 + " mode:" + filterMode);
        this.sa_param_buf.clear();
        if (filterMode == 2) {
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput2);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput2);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 1);
        } else if (filterMode == 4) {
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 1);
        }
        this.sa_param_buf.put((byte) 0);
        this.sa_param_buf.put((byte) 2);
        this.sa_param_buf.put((byte) 1);
        this.sa_param_buf.put((byte) 1);
        this.sa_param_buf.put((byte) 2);
        this.sa_param_buf.putInt(16);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.putShort((short) 0);
        this.sa_param_buf.putInt(15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 15);
        if (filterMode == 4) {
            this.sa_param_buf.putShort((short) 1);
        } else {
            this.sa_param_buf.putShort((short) 0);
        }
        this.sa_param_buf.putShort((short) 0);
        Log.d(TAG, "==== sa_param.write");
        this.sa_param.write(this.sa_param_buf, 172, 0);
        Log.d(TAG, "==== sa_bootparam reset");
        prepare_sa_bootparam();
        Log.d(TAG, "==== mDsp.setArg");
        this.mDSP.setArg(0, this.bootParam);
        this.mDSP.setArg(1, this.sa_param);
        if (optImgOutput2 != null) {
            this.mDSP.setArg(2, optImgOutput2);
        }
    }

    public synchronized void setParam(OptimizedImage optImgInput, OptimizedImage optImgOutput, OptimizedImage optImgOutput2, int targetWidth, int targetHeight, int filterMode) {
        Log.d(TAG, "==== setParam");
        if (optImgOutput2 == null) {
            Log.d(TAG, "optImgOutput2 is null. Two arg img process.");
            optImgOutput2 = optImgOutput;
        }
        int addressInput = this.mDSP.getPropertyAsInt(optImgInput, "memory-address");
        int addressOutput = this.mDSP.getPropertyAsInt(optImgOutput, "memory-address");
        int addressOutput2 = this.mDSP.getPropertyAsInt(optImgOutput2, "memory-address");
        int canvasWidthInput = this.mDSP.getPropertyAsInt(optImgInput, "image-canvas-width");
        int canvasWidthOutput = this.mDSP.getPropertyAsInt(optImgOutput, "image-canvas-width");
        int canvasWidthOutput2 = this.mDSP.getPropertyAsInt(optImgOutput2, "image-canvas-width");
        sa_exec_setParam(optImgInput, optImgOutput, optImgOutput2, addressInput, addressOutput, addressOutput2, targetWidth, targetHeight, canvasWidthInput, canvasWidthOutput, canvasWidthOutput2, filterMode);
    }

    protected synchronized void sa_exec_setParam(OptimizedImage optImgInput, OptimizedImage optImgOutput, OptimizedImage optImgOutput2, int addressInput, int addressOutput, int addressOutput2, int targetWidth, int targetHeight, int canvasWidthInput, int canvasWidthOutput, int canvasWidthOutput2, int filterMode) {
        if (isAVIP()) {
            if (addressInput % 8 != 0) {
                Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressInput:" + addressInput + " /8 = " + (addressInput / 8.0d));
            } else if (addressOutput % 8 != 0) {
                Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressOutput:" + Integer.toHexString(addressOutput));
            } else if (addressOutput2 % 8 != 0) {
                Log.e("sa", "!!!error  アドレスが8byte alignであること\u3000addressOutput:" + Integer.toHexString(addressOutput2));
            } else if (targetWidth % 4 != 0) {
                Log.e("sa", "!!!error  Widthは4の倍数  targetWidth:" + targetWidth);
            } else if (canvasWidthInput % 4 != 0) {
                Log.e("sa", "!!!error  入力line offsetが4の倍数であること canvasWidthInput:" + canvasWidthInput);
            } else if (canvasWidthOutput % 4 != 0) {
                Log.e("sa", "!!!error  出力line offsetが4の倍数であること canvasWidthOutput:" + canvasWidthOutput);
            } else if (canvasWidthOutput2 % 4 != 0) {
                Log.e("sa", "!!!error  出力line offsetが4の倍数であること canvasWidthOutput:" + canvasWidthOutput2);
            }
        }
        Log.i(TAG, "==== SA params convertedToAxi =  Addrinput:" + convArmAddr2AxiAddr(addressInput) + " AddrOutput:" + convArmAddr2AxiAddr(addressOutput) + " targetWidth:" + targetWidth + " targetHeight:" + targetHeight + " canvasWidthInput:" + canvasWidthInput + " canvasWidthOutput:" + canvasWidthOutput + " canvasWidthOutput2:" + canvasWidthOutput2 + " mode:" + filterMode);
        this.sa_param_buf.clear();
        if (filterMode == 0) {
            Log.d(TAG, "App side: ycb addr = " + convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            Log.d(TAG, "App side: ycg addr = " + convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            Log.d(TAG, "App side: ycd addr = " + convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput2);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 1);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 0);
        } else if (filterMode == 1) {
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 1);
            this.sa_param_buf.put((byte) 0);
        } else if (filterMode == 2) {
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput2);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput2));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput2);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 1);
        } else if (filterMode == 4) {
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 0);
            this.sa_param_buf.put((byte) 1);
        } else if (filterMode == 3) {
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressOutput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthOutput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(convArmAddr2AxiAddr(addressInput));
            this.sa_param_buf.putShort((short) targetWidth);
            this.sa_param_buf.putShort((short) canvasWidthInput);
            this.sa_param_buf.putShort((short) targetHeight);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putShort((short) 0);
            this.sa_param_buf.putInt(0);
            this.sa_param_buf.put((byte) 1);
            this.sa_param_buf.put((byte) 1);
            this.sa_param_buf.put((byte) 0);
        }
        this.sa_param_buf.put((byte) 0);
        this.sa_param_buf.put((byte) 2);
        this.sa_param_buf.put((byte) 1);
        this.sa_param_buf.put((byte) 1);
        this.sa_param_buf.put((byte) 2);
        this.sa_param_buf.putInt(16);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.putShort((short) 0);
        this.sa_param_buf.putInt(15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 35);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 22);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 7);
        this.sa_param_buf.put((byte) 15);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 38);
        this.sa_param_buf.put((byte) 30);
        this.sa_param_buf.put((byte) 15);
        if (filterMode == 4) {
            this.sa_param_buf.putShort((short) 1);
        } else {
            this.sa_param_buf.putShort((short) 0);
        }
        this.sa_param_buf.putShort((short) 0);
        Log.d(TAG, "==== sa_param.write");
        this.sa_param.write(this.sa_param_buf, 172, 0);
        Log.d(TAG, "==== sa_bootparam reset");
        prepare_sa_bootparam();
        Log.d(TAG, "==== mDsp.setArg");
        this.mDSP.setArg(0, this.bootParam);
        this.mDSP.setArg(1, this.sa_param);
        this.mDSP.setArg(2, optImgInput);
        this.mDSP.setArg(3, optImgOutput);
        if (optImgOutput != optImgOutput2) {
            Log.d(TAG, "setArg(4, optImgOutput2)");
            this.mDSP.setArg(4, optImgOutput2);
        } else {
            Log.d(TAG, "not setArg(4, optImgOutput2)");
        }
    }

    public synchronized int execute() {
        Log.d(TAG, "==== SA start");
        if (this.mDSP.execute()) {
            Log.i("★SA", "success");
            try {
                byte[] byteArray2 = new byte[60];
                this.bootParam.read(byteArray2);
                Log.i(TAG, "sa done  sapd:" + Sa_CommonHelper.read_sapd(byteArray2));
                Log.i(TAG, "sa done  seq_num:" + Sa_CommonHelper.read_seq_num(byteArray2));
                Log.i(TAG, "sa done  errCode:" + Sa_CommonHelper.read_errCode(byteArray2));
                Log.i(TAG, "sa done  errStatus:" + Sa_CommonHelper.read_errStatus(byteArray2));
                Log.i(TAG, "sa done  intfct:" + Sa_CommonHelper.read_intfct(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox0_a:" + Sa_CommonHelper.read_sa_mbox0_a(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox1_a:" + Sa_CommonHelper.read_sa_mbox1_a(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox2_a:" + Sa_CommonHelper.read_sa_mbox2_a(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox3_a:" + Sa_CommonHelper.read_sa_mbox3_a(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox0_b:" + Sa_CommonHelper.read_sa_mbox0_b(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox1_b:" + Sa_CommonHelper.read_sa_mbox1_b(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox2_b:" + Sa_CommonHelper.read_sa_mbox2_b(byteArray2));
                Log.i(TAG, "sa done  read_sa_mbox3_b:" + Sa_CommonHelper.read_sa_mbox3_b(byteArray2));
                String debug_string_bootParam = "";
                for (int i1 = 0; i1 < 57; i1++) {
                    debug_string_bootParam = debug_string_bootParam + Sa_CommonHelper.readIntFromByteArray(byteArray2, i1) + ExposureModeController.SOFT_SNAP;
                }
                Log.d("sa", "sa done  bootParam: " + debug_string_bootParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("sa", "Failed");
        }
        Log.d(TAG, "==== SA done ");
        return 9999;
    }

    public synchronized void releaseResources() {
        Log.d(TAG, "====releaseResources +");
        if (this.mDSP != null) {
            if (this.bootParam != null) {
                Log.d("SA_Mem", "release bootParam 60 byte.");
                this.bootParam.release();
                this.bootParam = null;
            }
            if (this.sa_param != null) {
                Log.d("SA_Mem", "release sa_param 172 byte.");
                this.sa_param.release();
                this.sa_param = null;
            }
            this.mDSP.release();
            this.mDSP = null;
        }
        this.sa_param_buf = null;
        Log.d(TAG, "====releaseResources -");
    }

    private synchronized void prepare_sa_bootparam() {
        if (this.bootParam != null) {
            this.bootParam.write(this.bootParam_buf);
        } else {
            Log.d(TAG, "mDSP.createBuffer(size) run");
            Log.d("SA_Mem", "CreateBuffer bootParam 60 byte.");
            this.bootParam = this.mDSP.createBuffer(60);
            this.bootParam_buf = ByteBuffer.allocateDirect(60);
            this.bootParam_buf.order(ByteOrder.nativeOrder());
            this.bootParam_buf.putInt(this.mDSP.getPropertyAsInt("program-descriptor"));
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

    public static boolean isAVIP() {
        int lsiType = getLsiType();
        return lsiType == 1;
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

    private static int getLsiType() {
        String pfVer = ScalarProperties.getString("version.platform");
        Log.d("SaZm0_RectCopy_FillColor_ImageFilter", "PROP_VERSION_PLATFORM = " + pfVer);
        float lsiType = Float.parseFloat(pfVer);
        Log.d("SaZm0_RectCopy_FillColor_ImageFilter", "lsiType = " + ((int) lsiType));
        return (int) lsiType;
    }
}
