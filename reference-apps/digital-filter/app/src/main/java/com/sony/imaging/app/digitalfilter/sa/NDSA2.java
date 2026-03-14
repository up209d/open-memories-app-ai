package com.sony.imaging.app.digitalfilter.sa;

import android.graphics.Point;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFRawDataInfo;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class NDSA2 {
    private static final int ADRC_OFF = 1;
    private static final int ADRC_ON = 0;
    private static final boolean DEBUG = false;
    private static final int GAMMA_8_16_TABLE_SIZE = 2050;
    private static final short GAMMA_OFF = 0;
    private static final short GAMMA_ON_FULL = 2;
    private static final short GAMMA_ON_REDUCED = 1;
    public static final int RAW2 = 2;
    public static final int RAW3 = 3;
    private static final int RAWCOMP_AWC_LIMIT_MODE = 1;
    private static final int RAWCOMP_AWC_OFF = 1;
    private static final int RAWCOMP_AWC_ON_V1 = 1;
    private static final int RAWCOMP_AWC_ON_V2 = 2;
    private static final int RAWCOMP_CMD_COPY_OUT_TO_RAW1 = 2;
    private static final int RAWCOMP_CMD_COPY_OUT_TO_RAW2 = 8;
    private static final int RAWCOMP_CMD_DEBUG = 16777216;
    private static final int RAWCOMP_CMD_RAWCOMPOSITE_2RAW = 4;
    private static final int RAWCOMP_CMD_RAWCOMPOSITE_3RAW = 16;
    private static final int SA_PARAM_SIZE = 512;
    private static int mAxiRawAddr;
    private static int mNumberOfShots;
    private static final String TAG = AppLog.getClassName();
    private static DSP mDSP = null;
    private static DeviceBuffer mRaw1 = null;
    private static DeviceBuffer mRaw2 = null;
    private static DeviceBuffer mRaw3 = null;
    private static CameraSequence.RawData mCompositRaw = null;
    private static GFRawDataInfo m1stRawDataInfo = null;
    private static GFRawDataInfo m2ndRawDataInfo = null;
    private static GFRawDataInfo m3rdRawDataInfo = null;
    private static GFRawDataInfo mCompRawDataInfo = null;
    private static NDSA2 sInstance = null;
    private static ByteBuffer mSAParamByteBuf = null;
    private static final int[] mGammaTbl_8_16 = {0, 1911, 3910, 6044, 8068, 9876, 11634, 13299, 14832, 16296, 17765, 19220, 20638, 21997, 23276, 24453, 25507, 26488, 27457, 28413, 29353, 30274, 31175, 32052, 32903, 33725, 34517, 35275, 35997, 36681, 37324, 37924, 38478, 39005, 39524, 40036, 40541, 41038, 41527, 42009, 42482, 42947, 43403, 43851, 44290, 44720, 45140, 45552, 45954, 46346, 46728, 47101, 47463, 47815, 48156, 48486, 48806, 49115, 49412, 49698, 49973, 50236, 50487, 50725, 50952, 51171, 51387, 51599, 51809, 52016, 52220, 52420, 52618, 52813, 53005, 53194, 53380, 53563, 53744, 53922, 54097, 54269, 54438, 54605, 54769, 54930, 55089, 55245, 55398, 55549, 55698, 55843, 55987, 56127, 56266, 56401, 56535, 56666, 56794, 56920, 57044, 57165, 57285, 57401, 57516, 57628, 57738, 57846, 57952, 58055, 58157, 58256, 58353, 58448, 58541, 58632, 58721, 58808, 58893, 58977, 59058, 59137, 59214, 59290, 59363, 59435, 59505, 59574, 59640, 59705, 59770, 59834, 59897, 59959, 60021, 60082, 60142, 60202, 60261, 60319, 60377, 60433, 60490, 60545, 60600, 60654, 60708, 60761, 60813, 60865, 60916, 60966, 61016, 61065, 61114, 61162, 61209, 61256, 61302, 61347, 61392, 61436, 61480, 61523, 61566, 61608, 61649, 61690, 61730, 61770, 61809, 61848, 61886, 61923, 61960, 61997, 62033, 62068, 62103, 62137, 62171, 62205, 62237, 62270, 62301, 62333, 62364, 62394, 62424, 62453, 62482, 62511, 62539, 62566, 62593, 62620, 62646, 62672, 62697, 62722, 62746, 62770, 62794, 62817, 62840, 62862, 62884, 62905, 62926, 62947, 62967, 62987, 63007, 63026, 63045, 63063, 63081, 63099, 63116, 63133, 63150, 63166, 63182, 63198, 63213, 63228, 63242, 63257, 63270, 63284, 63297, 63310, 63323, 63336, 63348, 63360, 63371, 63382, 63393, 63404, 63414, 63425, 63435, 63444, 63454, 63463, 63472, 63480, 63489, 63497, 63505, 63513, 63520, 63527, 63535, 63541, 63548, 63555, 63561, 63568, 63574, 63581, 63587, 63594, 63600, 63606, 63613, 63619, 63626, 63632, 63638, 63644, 63651, 63657, 63663, 63669, 63676, 63682, 63688, 63694, 63700, 63706, 63712, 63718, 63724, 63730, 63736, 63742, 63748, 63754, 63760, 63766, 63772, 63778, 63783, 63789, 63795, 63801, 63807, 63812, 63818, 63824, 63829, 63835, 63841, 63846, 63852, 63857, 63863, 63869, 63874, 63880, 63885, 63891, 63896, 63901, 63907, 63912, 63918, 63923, 63928, 63934, 63939, 63944, 63949, 63955, 63960, 63965, 63970, 63976, 63981, 63986, 63991, 63996, 64001, 64006, 64011, 64016, 64021, 64026, 64031, 64036, 64041, 64046, 64051, 64056, 64061, 64066, 64071, 64075, 64080, 64085, 64090, 64095, 64099, 64104, 64109, 64113, 64118, 64123, 64127, 64132, 64137, 64141, 64146, 64150, 64155, 64159, 64164, 64168, 64173, 64177, 64182, 64186, 64191, 64195, 64199, 64204, 64208, 64212, 64217, 64221, 64225, 64230, 64234, 64238, 64242, 64247, 64251, 64255, 64259, 64263, 64267, 64272, 64276, 64280, 64284, 64288, 64292, 64296, 64300, 64304, 64308, 64312, 64316, 64320, 64324, 64328, 64332, 64335, 64339, 64343, 64347, 64351, 64355, 64358, 64362, 64366, 64370, 64373, 64377, 64381, 64385, 64388, 64392, 64396, 64399, 64403, 64406, 64410, 64414, 64417, 64421, 64424, 64428, 64431, 64435, 64438, 64442, 64445, 64449, 64452, 64455, 64459, 64462, 64466, 64469, 64472, 64476, 64479, 64482, 64486, 64489, 64492, 64495, 64499, 64502, 64505, 64508, 64512, 64515, 64518, 64521, 64524, 64527, 64531, 64534, 64537, 64540, 64543, 64546, 64549, 64552, 64555, 64558, 64561, 64564, 64567, 64570, 64573, 64576, 64579, 64582, 64585, 64588, 64591, 64593, 64596, 64599, 64602, 64605, 64608, 64610, 64613, 64616, 64619, 64622, 64624, 64627, 64630, 64632, 64635, 64638, 64641, 64643, 64646, 64649, 64651, 64654, 64656, 64659, 64662, 64664, 64667, 64669, 64672, 64675, 64677, 64680, 64682, 64685, 64687, 64690, 64692, 64695, 64697, 64699, 64702, 64704, 64707, 64709, 64712, 64714, 64716, 64719, 64721, 64723, 64726, 64728, 64730, 64733, 64735, 64737, 64740, 64742, 64744, 64746, 64749, 64751, 64753, 64755, 64758, 64760, 64762, 64764, 64766, 64769, 64771, 64773, 64775, 64777, 64779, 64781, 64784, 64786, 64788, 64790, 64792, 64794, 64796, 64798, 64800, 64802, 64804, 64806, 64808, 64810, 64812, 64814, 64816, 64818, 64820, 64822, 64824, 64826, 64828, 64830, 64832, 64834, 64836, 64838, 64839, 64841, 64843, 64845, 64847, 64849, 64851, 64853, 64854, 64856, 64858, 64860, 64862, 64863, 64865, 64867, 64869, 64871, 64872, 64874, 64876, 64878, 64879, 64881, 64883, 64885, 64886, 64888, 64890, 64891, 64893, 64895, 64897, 64898, 64900, 64902, 64903, 64905, 64906, 64908, 64910, 64911, 64913, 64915, 64916, 64918, 64919, 64921, 64923, 64924, 64926, 64927, 64929, 64931, 64932, 64934, 64935, 64937, 64938, 64940, 64941, 64943, 64945, 64946, 64948, 64949, 64951, 64952, 64954, 64955, 64957, 64958, 64960, 64961, 64962, 64964, 64965, 64967, 64968, 64970, 64971, 64973, 64974, 64976, 64977, 64978, 64980, 64981, 64983, 64984, 64985, 64987, 64988, 64990, 64991, 64992, 64994, 64995, 64997, 64998, 64999, 65001, 65002, 65004, 65005, 65006, 65008, 65009, 65010, 65012, 65013, 65014, 65016, 65017, 65018, 65020, 65021, 65022, 65024, 65025, 65026, 65028, 65029, 65030, 65032, 65033, 65034, 65036, 65037, 65038, 65039, 65041, 65042, 65043, 65045, 65046, 65047, 65048, 65050, 65051, 65052, 65054, 65055, 65056, 65057, 65059, 65060, 65061, 65063, 65064, 65065, 65066, 65068, 65069, 65070, 65071, 65073, 65074, 65075, 65077, 65078, 65079, 65080, 65082, 65083, 65084, 65085, 65087, 65088, 65089, 65090, 65092, 65093, 65094, 65095, 65097, 65098, 65099, 65100, 65102, 65103, 65104, 65105, 65107, 65108, 65109, 65110, 65112, 65113, 65114, 65115, 65117, 65118, 65119, 65121, 65122, 65123, 65124, 65126, 65127, 65128, 65129, 65131, 65132, 65133, 65134, 65136, 65137, 65138, 65140, 65141, 65142, 65143, 65145, 65146, 65147, 65148, 65150, 65151, 65152, 65154, 65155, 65156, 65158, 65159, 65160, 65161, 65163, 65164, 65165, 65167, 65168, 65169, 65171, 65172, 65173, 65175, 65176, 65177, 65179, 65180, 65181, 65183, 65184, 65185, 65187, 65188, 65189, 65191, 65192, 65193, 65195, 65196, 65198, 65199, 65200, 65202, 65203, 65204, 65206, 65207, 65209, 65210, 65211, 65213, 65214, 65216, 65217, 65218, 65220, 65221, 65223, 65224, 65226, 65227, 65229, 65230, 65231, 65233, 65234, 65236, 65237, 65239, 65240, 65242, 65243, 65245, 65246, 65248, 65249, 65251, 65252, 65254, 65255, 65257, 65258, 65260, 65261, 65263, 65265, 65266, 65268, 65269, 65271, 65272, 65274, 65276, 65277, 65279, 65280, 65282, 65284, 65285, 65287, 65289, 65290, 65292, 65293, 65295, 65297, 65298, 65300, 65302, 65303, 65305, 65307, 65309, 65310, 65312, 65314, 65315, 65317, 65319, 65321, 65322, 65324, 65326, 65328, 65329, 65331, 65333, 65335, 65337, 65338, 65340, 65342, 65344, 65346, 65348, 65349, 65351, 65353, 65355, 65357, 65359, 65361, 65363, 65365, 65366, 65368, 65370, 65372, 65374, 65376, 65378, 65380, 65382, 65384, 65386, 65388, 65390, 65392, 65394, 65396, 65398, 65400, 65402, 65404, 65406, 65408, 65411, 65413, 65415, 65417, 65419, 65421, 65423, 65425, 65428, 65430, 65432, 65434, 65436, 65438, 65441, 65443, 65445, 65447, 65450, 65452, 65454, 65456, 65459, 65461, 65463, 65466, 65468, 65470, 65473, 
    65475, 65477, 65480, 65482, 65484, 65487, 65489, 65492, 65494, 65496, 65499, 65501, 65504, 65506, 65509, 65511, 65514, 65516, 65519, 65521, 65524, 65526, 65529, 65531, 65534};
    private static final int[] mInvGammaTbl_8_16 = {0, 9, 17, 26, 34, 43, 51, 60, 69, 77, 86, 94, 103, 111, 120, 129, 137, 146, 154, 163, 172, 180, 189, 197, 206, 214, 223, AppRoot.USER_KEYCODE.CENTER, GFIntervalController.DEFAULT_SHOTS, 249, 257, 265, 274, 282, 290, 298, 306, 315, 323, 331, 339, 347, 356, 364, 372, 380, 388, 397, 405, 413, 421, 429, 438, 446, 454, 462, 470, 478, 487, 495, 503, 511, AppRoot.USER_KEYCODE.EYE_SENSER, AppRoot.USER_KEYCODE.DIAL2_STATUS, AppRoot.USER_KEYCODE.MODE_DIAL_INVALID, AppRoot.USER_KEYCODE.MODE_DIAL_PANORAMA, AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM2, 557, 565, 573, AppRoot.USER_KEYCODE.IR_ZOOM_WIDE, AppRoot.USER_KEYCODE.CUSTOM, AppRoot.USER_KEYCODE.SLIDE_AF_MF, AppRoot.USER_KEYCODE.FOCUS_HOLD, AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE, AppRoot.USER_KEYCODE.UM_ZOOM_WIDE, AppRoot.USER_KEYCODE.PROJECTOR, AppRoot.USER_KEYCODE.DIAL3_LEFT, 642, AppRoot.USER_KEYCODE.LENS_APERTURE_RING_STATUS, AppRoot.USER_KEYCODE.WIFI, 665, 673, 680, 688, 696, 703, 711, 719, 726, 734, 742, 749, 757, 765, 773, 781, 789, 797, 805, 813, 821, 829, 837, 845, 854, 862, 870, 878, 886, 894, 902, 910, 918, 926, 935, 943, 951, 959, 967, 975, 983, 991, 999, 1007, 1015, 1024, 1032, 1042, 1051, 1060, 1069, 1078, 1087, 1096, 1105, 1114, 1123, 1132, 1141, 1150, 1159, 1168, 1177, 1187, 1196, 1205, 1214, 1223, 1232, 1241, 1250, 1259, 1268, 1277, 1286, 1296, 1305, 1314, 1324, 1333, 1342, 1352, 1361, 1370, 1380, 1389, 1398, 1408, 1417, 1426, 1436, 1445, 1454, 1463, 1473, 1482, 1491, 1501, 1510, 1519, 1529, 1538, 1548, 1558, 1568, 1578, 1587, 1597, 1607, 1617, 1627, 1637, 1646, 1656, 1666, 1676, 1686, 1696, 1705, 1715, 1725, 1735, 1745, 1755, 1765, 1774, 1784, 1794, 1805, 1816, 1826, 1837, 1848, 1858, 1869, 1880, 1890, 1901, 1912, 1923, 1933, 1944, 1955, 1965, 1976, 1987, 1997, 2008, 2019, 2029, 2040, 2051, 2062, 2073, 2084, 2096, 2107, 2118, 2129, 2140, 2152, 2163, 2174, 2185, 2196, 2208, 2219, 2230, 2241, 2252, 2263, 2275, 2286, 2297, 2308, 2319, 2331, 2342, 2353, 2364, 2375, 2386, 2397, 2409, 2420, 2431, 2442, 2453, 2464, 2476, 2487, 2498, 2509, 2520, 2531, 2542, 2554, 2565, 2576, 2587, 2599, 2610, 2621, 2632, 2644, 2655, 2666, 2677, 2689, 2700, 2711, 2722, 2734, 2745, 2756, 2767, 2779, 2790, 2801, 2813, 2824, 2836, 2847, 2859, 2870, 2882, 2893, 2905, 2916, 2928, 2940, 2951, 2963, 2974, 2986, 2997, 3009, 3020, 3032, 3044, 3055, 3067, 3078, 3090, 3103, 3115, 3127, 3139, 3151, 3163, 3175, 3187, 3199, 3211, 3223, 3235, 3247, 3259, 3271, 3283, 3295, 3307, 3320, 3332, 3345, 3357, 3370, 3383, 3396, 3409, 3421, 3434, 3447, 3460, 3473, 3486, 3498, 3511, 3524, 3537, 3550, 3562, 3575, 3588, 3602, 3616, 3630, 3644, 3658, 3672, 3686, 3700, 3714, 3728, 3742, 3755, 3769, 3783, 3797, 3811, 3825, 3839, 3854, 3870, 3886, 3901, 3917, 3932, 3948, 3963, 3979, 3994, 4010, 4025, 4041, 4057, 4072, 4088, 4104, 4120, 4137, 4154, 4170, 4187, 4204, 4221, 4237, 4254, 4271, 4287, 4304, 4321, 4337, 4354, 4371, 4388, 4405, 4422, 4439, 4456, 4473, 4489, 4506, 4523, 4540, 4557, 4574, 4591, 4608, 4625, 4642, 4659, 4676, 4694, 4711, 4728, 4745, 4762, 4779, 4796, 4813, 4831, 4848, 4865, 4882, 4900, 4917, 4935, 4952, 4969, 4987, 5004, 5022, 5039, 5057, 5074, 5091, 5109, 5126, 5144, 5162, 5180, 5198, 5215, 5233, 5251, 5269, 5287, 5304, 5322, 5340, 5358, 5376, 5394, 5412, 5430, 5448, 5466, 5485, 5503, 5521, 5539, 5557, 5576, 5594, 5612, 5630, 5649, 5667, 5686, 5705, 5723, 5742, 5761, 5779, 5798, 5817, 5836, 5854, 5873, 5892, 5911, 5930, 5949, 5969, 5988, 6007, 6026, 6046, 6065, 6084, 6103, 6123, 6142, 6162, 6182, 6202, 6222, 6242, 6261, 6281, 6301, 6321, 6341, 6361, 6381, 6401, 6422, 6442, 6463, 6484, 6504, 6525, 6546, 6566, 6587, 6608, 6629, 6649, 6671, 6692, 6714, 6735, 6757, 6779, 6800, 6822, 6843, 6865, 6887, 6908, 6931, 6954, 6976, 6999, 7022, 7044, 7067, 7090, 7112, 7135, 7158, 7181, 7205, 7229, 7253, 7277, 7301, 7325, 7349, 7373, 7397, 7421, 7446, 7471, 7497, 7522, 7548, 7573, 7599, 7624, 7650, 7675, 7702, 7730, 7757, 7784, 7812, 7839, 7866, 7893, 7921, 7949, 7979, 8008, 8038, 8067, 8097, 8127, 8156, 8186, 8217, 8248, 8279, 8310, 8341, 8372, 8403, 8434, 8466, 8497, 8529, 8560, 8592, 8623, 8655, 8686, 8718, 8750, 8782, 8814, 8846, 8878, 8910, 8942, 8974, 9007, 9039, 9072, 9104, 9137, 9169, 9202, 9234, 9267, 9300, 9333, 9366, 9399, 9432, 9465, 9498, 9532, 9565, 9599, 9632, 9666, 9699, 9733, 9767, 9801, 9835, 9869, 9903, 9937, 9971, 10005, 10040, 10075, 10109, 10144, 10179, 10213, 10248, 10283, 10318, 10354, 10389, 10424, 10459, 10495, 10530, 10566, 10602, 10638, 10674, 10710, 10746, 10782, 10819, 10856, 10892, 10929, 10965, 11002, 11039, 11076, 11114, 11151, 11188, 11226, 11263, 11301, 11339, 11377, 11415, 11454, 11492, 11530, 11569, 11608, 11647, 11686, 11725, 11764, 11804, 11843, 11883, 11923, 11963, 12002, 12042, 12083, 12124, 12165, 12205, 12246, 12287, 12329, 12370, 12412, 12454, 12496, 12538, 12580, 12623, 12666, 12709, 12752, 12795, 12839, 12883, 12926, 12970, 13014, 13058, 13104, 13149, 13194, 13239, 13285, 13330, 13377, 13423, 13470, 13516, 13563, 13611, 13659, 13707, 13755, 13803, 13852, 13902, 13951, 14001, 14051, 14101, 14152, 14203, 14254, 14306, 14358, 14411, 14464, 14517, 14570, 14624, 14679, 14734, 14789, 14845, 14902, 14959, 15016, 15074, 15132, 15192, 15251, 15311, 15371, 15433, 15495, 15558, 15620, 15685, 15751, 15816, 15882, 15951, 16019, 16088, 16159, 16231, 16303, 16375, 16449, 16524, 16599, 16674, 16750, 16826, 16902, 16979, 17057, 17134, 17212, 17290, 17368, 17446, 17525, 17605, 17684, 17765, 17845, 17925, 18007, 18089, 18171, 18253, 18336, 18419, 18503, 18587, 18671, 18756, 18841, 18927, 19013, 19100, 19187, 19275, 19363, 19451, 19540, 19629, 19719, 19809, 19900, 19991, 20083, 20175, 20268, 20361, 20455, 20550, 20645, 20741, 20837, 20935, 21032, 21130, 21228, 21327, 21427, 21528, 21629, 21731, 21834, 21937, 22041, 22145, 22251, 22357, 22465, 22572, 22681, 22789, 22899, 23009, 23121, 23235, 23347, 23461, 23575, 23693, 23809, 23927, 24045, 24167, 24287, 24409, 24533, 24657, 24781, 24907, 25035, 25165, 25295, 25427, 25559, 25693, 25829, 25965, 26101, 26243, 26383, 26526, 26669, 26816, 26963, 27113, 27264, 27416, 27571, 27727, 27887, 28047, 28209, 28375, 28543, 28713, 28885, 29060, 29237, 29417, 29601, 29787, 29976, 30169, 30363, 30562, 30765, 30973, 31186, 31400, 31622, 31847, 32076, 32312, 32550, 32800, 33052, 33304, 33560, 33820, 34086, 34350, 34620, 34892, 35168, 35447, 35730, 36018, 36307, 36604, 36902, 37205, 37511, 37824, 38139, 38461, 38789, 39121, 39456, 39800, 40149, 40505, 40869, 41239, 41615, 41997, 42389, 42791, 43200, 43619, 44046, 44489, 44935, 45400, 45876, 46360, 46864, 47384, 47923, 48481, 49051, 49655, 50275, 50923, 51605, 52317, 53078, 53875, 54717, 55627, 56608, 57673, 58860, 60161, 61673, 63460, 65527, 65527, 65527, 65528, 65528, 65528, 65528, 
    65529, 65529, 65529, 65529, 65529, 65530, 65530, 65530, 65530, 65531, 65531, 65531, 65531, 65532, 65532, 65532, 65532, 65532, 65533, 65533, 65533, 65533, 65534, 65534, 65534};
    private static int mParamOffset1 = 0;
    private static int mParamOffset2 = 0;

    private NDSA2() {
    }

    public static NDSA2 getInstance() {
        if (sInstance == null) {
            sInstance = new NDSA2();
        }
        return sInstance;
    }

    public void open(DSP dsp, DeviceBuffer raw1, GFRawDataInfo raw1Info, DeviceBuffer raw2, GFRawDataInfo raw2Info, DeviceBuffer raw3, GFRawDataInfo raw3Info, CameraSequence.RawData compRaw, GFRawDataInfo compRawInfo, int axiRawAddr, int numberOfInputImage) {
        mDSP = dsp;
        mRaw1 = raw1;
        mRaw2 = raw2;
        mRaw3 = raw3;
        mCompositRaw = compRaw;
        mAxiRawAddr = axiRawAddr;
        m1stRawDataInfo = raw1Info;
        m2ndRawDataInfo = raw2Info;
        m3rdRawDataInfo = raw3Info;
        mCompRawDataInfo = compRawInfo;
        mNumberOfShots = numberOfInputImage;
        mDSP.setProgram(getFilePath());
        mSAParamByteBuf = getSaParam();
    }

    public void execute() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        DeviceMemory createBuffer = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
        DeviceMemory gammaTable = getGammaTable();
        DeviceMemory invGammaTable = getInvGammaTable();
        GFCommonUtil.getInstance().setGraduationSeed(createBuffer, GFConstants.mGraduationSeed);
        mSAParamByteBuf = setTableAddr(mSAParamByteBuf, createBuffer, gammaTable, invGammaTable);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mCompositRaw, gammaTable, invGammaTable, createBuffer};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createBuffer.release();
        gammaTable.release();
        invGammaTable.release();
    }

    public void cancel() {
        if (mDSP != null) {
            mDSP.cancel();
        }
    }

    public void update() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        DeviceMemory createBuffer = mDSP.createBuffer(GFConstants.mGraduationSeed.length * 2);
        DeviceMemory gammaTable = getGammaTable();
        DeviceMemory invGammaTable = getInvGammaTable();
        GFCommonUtil.getInstance().setGraduationSeed(createBuffer, GFConstants.mGraduationSeed);
        mSAParamByteBuf = setTableAddr(mSAParamByteBuf, createBuffer, gammaTable, invGammaTable);
        mSAParamByteBuf = updateParams(mSAParamByteBuf);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mCompositRaw, gammaTable, invGammaTable, createBuffer};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createBuffer.release();
        gammaTable.release();
        invGammaTable.release();
    }

    public void copy() {
        ByteBuffer bootParam = SaUtil.getMboxParam(mDSP);
        mSAParamByteBuf = copyParams(mSAParamByteBuf);
        DeviceMemory[] dmlist = {mRaw1, mRaw2, mRaw3, mCompositRaw};
        try {
            SaUtil.executeSA(mDSP, bootParam, mSAParamByteBuf, dmlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copy(DSP dsp, DeviceBuffer raw1, GFRawDataInfo raw1Info, CameraSequence.RawData raw) {
        open(dsp, raw1, raw1Info, raw1, raw1Info, raw1, raw1Info, raw, raw1Info, raw1Info.axiRawAddr, 2);
        copy();
        close();
    }

    private ByteBuffer copyParams(ByteBuffer saParam) {
        saParam.position(0);
        saParam.putInt(2);
        return saParam;
    }

    private ByteBuffer updateParams(ByteBuffer saParam) {
        saParam.position(0);
        if (mNumberOfShots == 2) {
            saParam.putInt(4);
        } else {
            saParam.putInt(16);
        }
        return updateSubBoundary(updateMainBoundary(saParam));
    }

    private ByteBuffer updateMainBoundary(ByteBuffer saParam) {
        boolean roteteBoundary = GFShootingOrderController.getInstance().isLandSkyOrder();
        return updateBoundary(saParam, 0, mParamOffset1, roteteBoundary);
    }

    private ByteBuffer updateSubBoundary(ByteBuffer saParam) {
        return updateBoundary(saParam, 1, mParamOffset2, true);
    }

    private ByteBuffer updateBoundary(ByteBuffer saParam, int borderId, int paramOffset, boolean roteteBoundary) {
        boolean isDummySetting = false;
        if (borderId != 0 && borderId != 1) {
            isDummySetting = true;
            borderId = 0;
        }
        int degree = GFCommonUtil.getInstance().getSADegree(borderId);
        if (roteteBoundary && (degree = degree + 180) >= 360) {
            degree -= 360;
        }
        double rad = (degree / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point = GFCommonUtil.getInstance().getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio, borderId);
        int valueX = point.x;
        int valueY = point.y;
        int strength = GFCommonUtil.getInstance().getSAStrength(borderId);
        if (isDummySetting) {
            valueSin = DoubleToFixedPointInt(Math.sin(0.0d), 24);
            valueCos = DoubleToFixedPointInt(Math.cos(0.0d), 24);
            valueX = 0;
            valueY = 0;
            strength = 5120;
        }
        saParam.position(paramOffset);
        saParam.putInt(valueSin);
        saParam.putInt(valueCos);
        saParam.putShort((short) valueX);
        saParam.putShort((short) valueY);
        saParam.putInt(strength);
        return saParam;
    }

    public void close() {
        mDSP.clearProgram();
        mSAParamByteBuf = null;
    }

    private String getFilePath() {
        String filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_half_nd2.so";
        AppLog.info(TAG, "SA file path: " + filePath);
        return filePath;
    }

    private static ByteBuffer setTableAddr(ByteBuffer saParam, DeviceMemory seed, DeviceMemory gammaTable, DeviceMemory invGammaTable) {
        saParam.position(16);
        saParam.putInt(SaUtil.getMemoryAddressAxi(seed));
        saParam.position(20);
        saParam.putInt(SaUtil.getMemoryAddressAxi(gammaTable));
        saParam.position(24);
        saParam.putInt(SaUtil.getMemoryAddressAxi(invGammaTable));
        return saParam;
    }

    private DeviceBuffer getGammaTable() {
        DeviceBuffer devBuffer = mDSP.createBuffer(GAMMA_8_16_TABLE_SIZE);
        ByteBuffer byteBuf = SaUtil.getSaParam(GAMMA_8_16_TABLE_SIZE);
        for (int i = 0; i < 1025; i++) {
            byteBuf.putShort((short) mGammaTbl_8_16[i]);
        }
        devBuffer.write(byteBuf);
        return devBuffer;
    }

    private DeviceBuffer getInvGammaTable() {
        DeviceBuffer devBuffer = mDSP.createBuffer(GAMMA_8_16_TABLE_SIZE);
        ByteBuffer byteBuf = SaUtil.getSaParam(GAMMA_8_16_TABLE_SIZE);
        for (int i = 0; i < 1025; i++) {
            byteBuf.putShort((short) mInvGammaTbl_8_16[i]);
        }
        devBuffer.write(byteBuf);
        return devBuffer;
    }

    private ByteBuffer getSaParam() {
        ByteBuffer saParam = SaUtil.getSaParam(SA_PARAM_SIZE);
        if (mNumberOfShots == 2) {
            saParam.putInt(4);
        } else {
            saParam.putInt(16);
        }
        saParam.putInt(0);
        saParam.putShort(GAMMA_OFF);
        saParam.putShort(GAMMA_OFF);
        saParam.putShort((short) 256);
        saParam.putShort(GAMMA_OFF);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        saParam.putInt(0);
        GFRawDataInfo rawInfo = m1stRawDataInfo;
        saParam.putInt(SaUtil.getMemoryAddressAxi(mRaw1));
        saParam.putShort((short) rawInfo.canvasSizeX);
        saParam.putShort((short) rawInfo.canvasSizeY);
        saParam.putShort((short) rawInfo.marginOffsetX);
        saParam.putShort((short) rawInfo.marginOffsetY);
        saParam.putShort((short) rawInfo.marginSizeX);
        saParam.putShort((short) rawInfo.marginSizeY);
        saParam.putShort((short) rawInfo.validOffsetX);
        saParam.putShort((short) rawInfo.validOffsetY);
        saParam.putShort((short) rawInfo.validSizeX);
        saParam.putShort((short) rawInfo.validSizeY);
        saParam.putShort((short) rawInfo.firstColor);
        saParam.putShort((short) rawInfo.clpR);
        saParam.putShort((short) rawInfo.clpGr);
        saParam.putShort((short) rawInfo.clpGb);
        saParam.putShort((short) rawInfo.clpB);
        saParam.putShort((short) rawInfo.clpOfst);
        saParam.putShort((short) rawInfo.wbR);
        saParam.putShort((short) rawInfo.wbB);
        GFRawDataInfo rawInfo2 = m2ndRawDataInfo;
        saParam.putInt(SaUtil.getMemoryAddressAxi(mRaw2));
        saParam.putShort((short) rawInfo2.canvasSizeX);
        saParam.putShort((short) rawInfo2.canvasSizeY);
        saParam.putShort((short) rawInfo2.marginOffsetX);
        saParam.putShort((short) rawInfo2.marginOffsetY);
        saParam.putShort((short) rawInfo2.marginSizeX);
        saParam.putShort((short) rawInfo2.marginSizeY);
        saParam.putShort((short) rawInfo2.validOffsetX);
        saParam.putShort((short) rawInfo2.validOffsetY);
        saParam.putShort((short) rawInfo2.validSizeX);
        saParam.putShort((short) rawInfo2.validSizeY);
        saParam.putShort((short) rawInfo2.firstColor);
        saParam.putShort((short) rawInfo2.clpR);
        saParam.putShort((short) rawInfo2.clpGr);
        saParam.putShort((short) rawInfo2.clpGb);
        saParam.putShort((short) rawInfo2.clpB);
        saParam.putShort((short) rawInfo2.clpOfst);
        saParam.putShort((short) rawInfo2.wbR);
        saParam.putShort((short) rawInfo2.wbB);
        GFRawDataInfo rawInfo3 = m3rdRawDataInfo;
        saParam.putInt(SaUtil.getMemoryAddressAxi(mRaw3));
        saParam.putShort((short) rawInfo3.canvasSizeX);
        saParam.putShort((short) rawInfo3.canvasSizeY);
        saParam.putShort((short) rawInfo3.marginOffsetX);
        saParam.putShort((short) rawInfo3.marginOffsetY);
        saParam.putShort((short) rawInfo3.marginSizeX);
        saParam.putShort((short) rawInfo3.marginSizeY);
        saParam.putShort((short) rawInfo3.validOffsetX);
        saParam.putShort((short) rawInfo3.validOffsetY);
        saParam.putShort((short) rawInfo3.validSizeX);
        saParam.putShort((short) rawInfo3.validSizeY);
        saParam.putShort((short) rawInfo3.firstColor);
        saParam.putShort((short) rawInfo3.clpR);
        saParam.putShort((short) rawInfo3.clpGr);
        saParam.putShort((short) rawInfo3.clpGb);
        saParam.putShort((short) rawInfo3.clpB);
        saParam.putShort((short) rawInfo3.clpOfst);
        saParam.putShort((short) rawInfo3.wbR);
        saParam.putShort((short) rawInfo3.wbB);
        GFRawDataInfo rawInfo4 = mCompRawDataInfo;
        saParam.putInt(mAxiRawAddr);
        saParam.putShort((short) rawInfo4.canvasSizeX);
        saParam.putShort((short) rawInfo4.canvasSizeY);
        saParam.putShort((short) rawInfo4.marginOffsetX);
        saParam.putShort((short) rawInfo4.marginOffsetY);
        saParam.putShort((short) rawInfo4.marginSizeX);
        saParam.putShort((short) rawInfo4.marginSizeY);
        saParam.putShort((short) rawInfo4.validOffsetX);
        saParam.putShort((short) rawInfo4.validOffsetY);
        saParam.putShort((short) rawInfo4.validSizeX);
        saParam.putShort((short) rawInfo4.validSizeY);
        saParam.putShort((short) rawInfo4.firstColor);
        saParam.putShort((short) rawInfo4.clpR);
        saParam.putShort((short) rawInfo4.clpGr);
        saParam.putShort((short) rawInfo4.clpGb);
        saParam.putShort((short) rawInfo4.clpB);
        saParam.putShort((short) rawInfo4.clpOfst);
        saParam.putShort((short) rawInfo4.wbR);
        saParam.putShort((short) rawInfo4.wbB);
        GFRawDataInfo rawInfo5 = mCompRawDataInfo;
        saParam.putInt(rawInfo5.ddithRstmod);
        saParam.putInt(rawInfo5.ddithOn);
        saParam.putInt(rawInfo5.expBit);
        saParam.putInt(rawInfo5.decompMode);
        saParam.putInt(rawInfo5.dth0);
        saParam.putInt(rawInfo5.dth1);
        saParam.putInt(rawInfo5.dth2);
        saParam.putInt(rawInfo5.dth3);
        saParam.putInt(rawInfo5.dp0);
        saParam.putInt(rawInfo5.dp1);
        saParam.putInt(rawInfo5.dp2);
        saParam.putInt(rawInfo5.dp3);
        saParam.putInt(rawInfo5.dithRstmod);
        saParam.putInt(rawInfo5.dithOn);
        saParam.putInt(rawInfo5.rndBit);
        saParam.putInt(rawInfo5.compMode);
        saParam.putInt(rawInfo5.th0);
        saParam.putInt(rawInfo5.th1);
        saParam.putInt(rawInfo5.th2);
        saParam.putInt(rawInfo5.th3);
        saParam.putInt(rawInfo5.p0);
        saParam.putInt(rawInfo5.p1);
        saParam.putInt(rawInfo5.p2);
        saParam.putInt(rawInfo5.p3);
        saParam.putShort(GAMMA_ON_REDUCED);
        saParam.putShort(GAMMA_ON_FULL);
        saParam.putInt(3);
        int degree = GFCommonUtil.getInstance().getSADegree(0);
        if (GFShootingOrderController.getInstance().isLandSkyOrder() && (degree = degree + 180) >= 360) {
            degree -= 360;
        }
        double rad = (degree / 180.0d) * 3.141592653589793d;
        int valueSin = DoubleToFixedPointInt(Math.sin(rad), 24);
        int valueCos = DoubleToFixedPointInt(Math.cos(rad), 24);
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point = GFCommonUtil.getInstance().getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio, 0);
        int valueX = point.x;
        int valueY = point.y;
        int strength = GFCommonUtil.getInstance().getSAStrength(0);
        mParamOffset1 = saParam.position();
        saParam.putInt(valueSin);
        saParam.putInt(valueCos);
        saParam.putShort((short) valueX);
        saParam.putShort((short) valueY);
        saParam.putInt(strength);
        int degree2 = GFCommonUtil.getInstance().getSADegree(1) + 180;
        if (degree2 >= 360) {
            degree2 -= 360;
        }
        double rad2 = (degree2 / 180.0d) * 3.141592653589793d;
        int valueSin2 = DoubleToFixedPointInt(Math.sin(rad2), 24);
        int valueCos2 = DoubleToFixedPointInt(Math.cos(rad2), 24);
        String aspectRatio2 = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point2 = GFCommonUtil.getInstance().getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio2, 1);
        int valueX2 = point2.x;
        int valueY2 = point2.y;
        mParamOffset2 = saParam.position();
        saParam.putInt(valueSin2);
        saParam.putInt(valueCos2);
        saParam.putShort((short) valueX2);
        saParam.putShort((short) valueY2);
        saParam.putInt(GFCommonUtil.getInstance().getSAStrength(1));
        for (int i = 0; i < 15; i++) {
            saParam.putInt(0);
        }
        saParam.putInt(-1091576147);
        return saParam;
    }

    private static int DoubleToFixedPointInt(double valued, int bitlen) {
        int value = (int) ((1 << (bitlen + 1)) * valued);
        if (value >= 0.0d) {
            int result = (value + 1) >> 1;
            return result;
        }
        int result2 = -(((-value) + 1) >> 1);
        return result2;
    }
}
