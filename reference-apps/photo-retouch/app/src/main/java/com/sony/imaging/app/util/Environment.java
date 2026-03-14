package com.sony.imaging.app.util;

import android.os.Build;
import android.util.Log;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public final class Environment {
    public static final int API_VERSION_SUPPORTING_MOVIE = 3;
    public static final int DEVICE_TYPE_EMULATOR = 2;
    public static final int DEVICE_TYPE_EX_EMULATOR = 4;
    public static final int DEVICE_TYPE_EX_REAL_MACHINE = 3;
    public static final int DEVICE_TYPE_REAL_MACHINE = 1;
    public static final int ENABLE_FOCUS_DRIVE_VERSION = 3;
    public static final int ENABLE_GET_SUPPORTED_JPEG_QUALITY_VERSION = 2;
    public static final int ENABLE_PREVIEW_MAGNIFICATION_POINT_MOVABLE_VERSION = 3;
    private static final String LOG_MODEL = "Model : ";
    private static final String SCALAR_A = "ScalarA";
    private static final String SCALAR_EX_API = "ScalarA_ExApi";
    private static final String SDK = "sdk";
    private static final String SPLITTER_PERIOD = "\\Q.\\E";
    private static final String TAG = "Environment";
    private static final StringBuilder LOG_STRING = new StringBuilder();
    public static final int DEVICE_TYPE = getDeviceType();
    private static String sPFVersion = null;
    private static int sHWversion = -1;
    private static int sAPIversion = -1;

    private static final int getDeviceType() {
        String model = Build.MODEL;
        Log.i(TAG, LOG_STRING.replace(0, LOG_STRING.length(), LOG_MODEL).append(model).toString());
        if (model.equals(SDK)) {
            return 2;
        }
        if (model.equals(SCALAR_A)) {
            return 3;
        }
        if (model.equals(SCALAR_EX_API)) {
            return 4;
        }
        return 1;
    }

    public static int getVersionOfHW() {
        String[] subVersions;
        if (-1 == sHWversion) {
            if (sPFVersion == null) {
                sPFVersion = ScalarProperties.getString("version.platform");
            }
            if (sPFVersion != null && (subVersions = sPFVersion.split(SPLITTER_PERIOD)) != null && subVersions.length >= 2) {
                sHWversion = Integer.parseInt(subVersions[0]);
            }
        }
        return sHWversion;
    }

    public static int getVersionPfAPI() {
        String[] subVersions;
        if (-1 == sAPIversion) {
            if (sPFVersion == null) {
                sPFVersion = ScalarProperties.getString("version.platform");
            }
            if (sPFVersion != null && (subVersions = sPFVersion.split(SPLITTER_PERIOD)) != null && subVersions.length >= 2) {
                sAPIversion = Integer.parseInt(subVersions[1]);
            }
        }
        return sAPIversion;
    }

    public static boolean isFixedABGMofAWB() {
        return 3 <= getVersionPfAPI();
    }

    public static boolean isMovieAPISupported() {
        return 3 <= getVersionPfAPI();
    }

    public static boolean isAvailableGetSupportedJPEGQuality() {
        return 2 <= getVersionPfAPI();
    }

    public static boolean isAvailableFocusDrive() {
        return 3 <= getVersionPfAPI();
    }

    public static boolean isAvailablePreviewMagnificationPointMovable() {
        return 3 <= getVersionPfAPI();
    }
}
