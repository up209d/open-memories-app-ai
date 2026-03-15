package com.sony.imaging.app.util;

import android.util.Log;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class PfBugAvailability {
    public static final int API_VERSION_BUG_AVAILABLE_ENCODE_AT_PLAY = 4;
    public static final int API_VERSION_BUG_AVAILABLE_SHARP_IN_DEVICE_NAME = 9;
    public static final String DSCHX80 = "DSC-HX80";
    public static final String DSCHX90V = "DSC-HX90V";
    public static final String DSCRX100M4 = "DSC-RX100M4";
    public static final String DSCRX10M2 = "DSC-RX10M2";
    public static final String DSCWX500 = "DSC-WX500";
    public static final String ILCE5100 = "ILCE-5100";
    public static final String ILCE7M2 = "ILCE-7M2";
    public static final String ILCE7RM2 = "ILCE-7RM2";
    public static final String ILCE7S = "ILCE-7S";
    public static final int PATCH_BIT_HIGH_SPEED_LENS_POWER_OFF = 1;
    public static final String UI_MODEL_NAME_DMH = "DSLR-15-01";
    public static final String UI_MODEL_NAME_DMHH = "DSLR-15-04";
    public static final String UI_MODEL_NAME_FV = "DSC-15-01";
    public static final String UI_MODEL_NAME_GC = "DSLR-15-02";
    public static final String UI_MODEL_NAME_ICV2 = "DSLR-15-03";
    public static final String UI_MODEL_NAME_KV = "DSC-15-03";
    public static final String UI_MODEL_NAME_ZV = "DSC-15-02";
    static String TAG = "PfBugAvailability";
    public static final String PF_VERSION = ScalarProperties.getString("version.platform");
    public static final int PF_MAJOR_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final int PF_API_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final String MODEL_NAME = ScalarProperties.getString("model.name");
    public static final String UI_MODEL_NAME = ScalarProperties.getString("ui.model.mame");
    public static boolean highSpeedLensPowerOff = highSpeedLensPowerOff();
    public static boolean sharpInDeviceName = sharpInDeviceName();
    public static boolean virtualMediaPlay = virtualMediaPlay();
    public static boolean encodeAtPlay = encodeAtPlay();

    private static boolean highSpeedLensPowerOff() {
        Log.i(TAG, "ModelName: " + MODEL_NAME);
        boolean isDeviceHighSpeedLensPowerOff = DSCHX90V.equals(MODEL_NAME) || DSCHX80.equals(MODEL_NAME) || DSCWX500.equals(MODEL_NAME) || DSCRX100M4.equals(MODEL_NAME) || DSCRX10M2.equals(MODEL_NAME);
        boolean isHighSpeedOffFixed = (ScalarProperties.getInt("ui.modification.patch.bits0") & 1) == 1;
        if (!isDeviceHighSpeedLensPowerOff || isHighSpeedOffFixed) {
            return false;
        }
        Log.i(TAG, "isHighSpeedLensPowerOffWorkaroundNeeded:  true");
        return true;
    }

    private static boolean sharpInDeviceName() {
        return 9 >= Environment.getVersionPfAPI();
    }

    private static boolean virtualMediaPlay() {
        return DSCHX90V.equals(MODEL_NAME) || DSCWX500.equals(MODEL_NAME) || DSCRX100M4.equals(MODEL_NAME) || DSCRX10M2.equals(MODEL_NAME) || ILCE7M2.equals(MODEL_NAME) || ILCE7RM2.equals(MODEL_NAME);
    }

    private static boolean encodeAtPlay() {
        return 4 >= Environment.getVersionPfAPI() || ILCE5100.equals(MODEL_NAME);
    }
}
