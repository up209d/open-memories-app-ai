package com.sony.imaging.app.graduatedfilter.common;

/* loaded from: classes.dex */
public class GFConstants {
    public static final String APPSETTING = "ApplicationSettings";
    public static final String APPTOP = "ApplicationTop";
    public static final String CANCELTAKEPICTURE = "CancelTakepicture";
    public static final String CANCEL_TOUCHLESS_SHOOTING = "cancel_touchless_shooting";
    public static final String CHANGE_BASE_AWB_BY_FLASH = "change_base_awb_by_flash";
    public static final String CHANGE_FILTER_AWB_BY_FLASH = "change_filter_awb_by_flash";
    public static final String CHECKE_UPDATE_STATUS = "check_update_appsetting";
    public static final String END_PROCESSING = "FinishCompositRawData";
    public static final String HISTOGRAM_UPDATE = "filter_histgram_update";
    public static final String MENU_STATE = "Menu";
    public static final String MUTE_SCREEN = "MuteScreen";
    public static final int NO_CATION = 0;
    public static final int POS_EDIT = 1;
    public static final int POS_SAVE = 0;
    public static final String REQUEST_ADJUSTMENT = "RequestAdjustment";
    public static final String REQUEST_AUTOREVIEW = "RequestAutoReview";
    public static final String RESET_COLOR_SETTING = "reset_color_setting";
    public static final String RESTART_COMPOSIT_PROCESS = "restart_composit_process";
    public static final long SELFTIMER_DELAY_TIME = 2500;
    public static final int SHADING_COMP_VALUE = 9;
    public static final int SHOOTING_GUIDE_DISP_TIME = 8000;
    public static final String SHOW_STEP1_GUIDE = "show_step1_guide";
    public static final String START_APPSETTING = "start_appsetting";
    public static final String START_BASESETTING = "start_basesetting";
    public static final String START_PROCESSING = "StartCompositRawData";
    public static final String START_SELFTIMER = "StartSelfTimer.";
    public static final String STOP_APPSETTING = "stop_appsetting";
    public static final String UPDATED_COUNTER = "UpdateCounter.";
    public static final String UPDATE_APPSETTING = "update_appsetting";
    public static short[] mGraduationSeed = {8192, 8192, 8191, 8190, 8190, 8189, 8188, 8187, 8187, 8186, 8185, 8185, 8184, 8183, 8182, 8181, 8181, 8180, 8179, 8178, 8177, 8176, 8175, 8175, 8174, 8173, 8172, 8171, 8170, 8169, 8168, 8167, 8166, 8165, 8164, 8163, 8162, 8160, 8159, 8158, 8157, 8156, 8155, 8153, 8152, 8151, 8150, 8148, 8147, 8146, 8144, 8143, 8141, 8140, 8138, 8137, 8136, 8134, 8132, 8131, 8129, 8128, 8126, 8124, 8123, 8121, 8119, 8117, 8116, 8114, 8112, 8110, 8108, 8106, 8104, 8102, 8100, 8098, 8096, 8094, 8092, 8089, 8087, 8085, 8083, 8080, 8078, 8075, 8073, 8070, 8068, 8065, 8063, 8060, 8057, 8055, 8052, 8049, 8046, 8043, 8040, 8037, 8034, 8031, 8028, 8025, 8022, 8018, 8015, 8012, 8008, 8005, 8001, 7998, 7994, 7990, 7987, 7983, 7979, 7975, 7971, 7967, 7963, 7959, 7954, 7950, 7946, 7941, 7937, 7932, 7927, 7923, 7918, 7913, 7908, 7903, 7898, 7893, 7888, 7882, 7877, 7871, 7866, 7860, 7854, 7849, 7843, 7837, 7831, 7825, 7818, 7812, 7805, 7799, 7792, 7785, 7779, 7772, 7765, 7757, 7750, 7743, 7735, 7728, 7720, 7712, 7704, 7696, 7688, 7680, 7671, 7663, 7654, 7645, 7636, 7627, 7618, 7609, 7599, 7590, 7580, 7570, 7560, 7550, 7540, 7529, 7519, 7508, 7497, 7486, 7475, 7463, 7452, 7440, 7428, 7416, 7404, 7392, 7379, 7366, 7353, 7340, 7327, 7314, 7300, 7286, 7272, 7258, 7243, 7229, 7214, 7199, 7184, 7168, 7153, 7137, 7121, 7104, 7088, 7071, 7054, 7037, 7019, 7002, 6984, 6966, 6947, 6929, 6910, 6890, 6871, 6851, 6832, 6811, 6791, 6770, 6749, 6728, 6707, 6685, 6663, 6640, 6618, 6595, 6572, 6548, 6524, 6500, 6476, 6451, 6426, 6401, 6375, 6350, 6323, 6297, 6270, 6243, 6215, 6187, 6159, 6131, 6102, 6073, 6043, 6014, 5983, 5953, 5922, 5891, 5859, 5827, 5795, 5762, 5729, 5696, 5662, 5628, 5594, 5559, 5524, 5488, 5452, 5416, 5379, 5342, 5305, 5267, 5229, 5190, 5151, 5112, 5072, 5032, 4992, 4951, 4910, 4868, 4826, 4784, 4741, 4698, 4654, 4610, 4566, 4521, 4476, 4430, 4384, 4338, 4292, 4245, 4197, 4149, 4101, 4053, 4004, 3954, 3905, 3855, 3804, 3754, 3702, 3651, 3599, 3547, 3494, 3441, 3388, 3335, 3281, 3226, 3172, 3117, 3061, 3006, 2950, 2894, 2837, 2780, 2723, 2666, 2608, 2550, 2492, 2433, 2374, 2315, 2256, 2196, 2136, 2076, 2016, 1955, 1895, 1834, 1772, 1711, 1649, 1587, 1525, 1463, 1401, 1338, 1276, 1213, 1150, 1087, 1023, 960, 896, 833, 769, 705, 641, 577, 513, 449, 385, 321, 257, 192, 128, 64, 0};
}
