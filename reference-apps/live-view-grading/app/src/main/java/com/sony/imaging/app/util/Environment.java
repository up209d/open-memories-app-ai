package com.sony.imaging.app.util;

import android.os.Build;
import android.util.Log;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public final class Environment {
    public static final int API_VERSION_INTVAL_REC_NEW_BIZ = 9;
    public static final int API_VERSION_LOOP_REC_NEW_BIZ = 12;
    public static final int API_VERSION_SUPPORTING_AVINDEX_FILES = 8;
    public static final int API_VERSION_SUPPORTING_INH_FACTOR_FILTER = 8;
    public static final int API_VERSION_SUPPORTING_INH_FACTOR_FILTER_MULTI = 12;
    public static final int API_VERSION_SUPPORTING_MOVIE = 3;
    public static final int API_VERSION_SUPPORTING_MOVIE_PLAYBACK = 8;
    public static final int API_VERSION_SUPPORTING_NEWBIZ = 8;
    public static final int AVAILABLE_BEEP_API_VERSION = 7;
    public static final int DEVICE_TYPE_EMULATOR = 2;
    public static final int DEVICE_TYPE_EX_EMULATOR = 4;
    public static final int DEVICE_TYPE_EX_REAL_MACHINE = 3;
    public static final int DEVICE_TYPE_REAL_MACHINE = 1;
    public static final int ENABLE_AUDIOMANAGER_IN_ANY_MODE = 7;
    public static final int ENABLE_AUDIOMANAGER_WITH_STILL_VERSION = 7;
    public static final int ENABLE_FOCUS_DRIVE_VERSION = 3;
    public static final int ENABLE_GET_SUPPORTED_JPEG_QUALITY_VERSION = 2;
    public static final int ENABLE_PREVIEW_MAGNIFICATION_POINT_MOVABLE_VERSION = 3;
    private static final String LOG_MODEL = "Model : ";
    private static final int PF_VER_GRAPHICS_CONSTRAINT = 10;
    protected static final int PF_VER_SUPPORT_ONE_PUSH_WB = 12;
    protected static final int PF_VER_SUPPORT_SUB_LCD = 5;
    private static final String SCALAR_A = "ScalarA";
    private static final String SCALAR_EX_API = "ScalarA_ExApi";
    private static final String SDK = "sdk";
    private static final String SPLITTER_PERIOD = "\\Q.\\E";
    private static final String TAG = "Environment";
    public static final int DEVICE_TYPE = getDeviceType();
    private static String sPFVersion = null;
    private static int sHWversion = -1;
    private static int sAPIversion = -1;

    private static final int getDeviceType() {
        String model = Build.MODEL;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        Log.i(TAG, builder.replace(0, builder.length(), LOG_MODEL).append(model).toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
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

    public static boolean isAudioManagerAvailableInAnyMode() {
        return 7 <= getVersionPfAPI();
    }

    public static boolean isAvailableAudioManagerWithStill() {
        return 7 <= getVersionPfAPI();
    }

    public static boolean isBeepAPISupported() {
        return 7 <= getVersionPfAPI();
    }

    public static boolean isAvindexFilesSupported() {
        return 8 <= getVersionPfAPI();
    }

    public static boolean isMoviePlaybackSupported() {
        return 8 <= getVersionPfAPI();
    }

    public static boolean isIntervalRecAPISupported() {
        return 9 <= getVersionPfAPI();
    }

    public static boolean isLoopRecAPISupported() {
        return 12 <= getVersionPfAPI();
    }

    public static boolean isNewBizDevice() {
        if (8 > getVersionPfAPI() || 1 != ScalarProperties.getInt("model.group")) {
            return false;
        }
        return true;
    }

    public static boolean isNewBizDeviceActionCam() {
        if (8 > getVersionPfAPI() || 1 != ScalarProperties.getInt("model.group") || 3 != ScalarProperties.getInt("model.category")) {
            return false;
        }
        return true;
    }

    public static boolean isNewBizDeviceLSC() {
        if (8 > getVersionPfAPI()) {
            return false;
        }
        int category = ScalarProperties.getInt("model.category");
        if (1 != ScalarProperties.getInt("model.group")) {
            return false;
        }
        if (2 != category && 1 != category) {
            return false;
        }
        return true;
    }

    public static boolean hasGraphicsConstraint() {
        return 10 > getVersionPfAPI();
    }

    public static boolean isCustomWBOnePush() {
        return getVersionPfAPI() >= 12 && 1 == ScalarProperties.getInt("ui.custom.wb.type");
    }

    public static boolean isSupportingDefaultBootMode() {
        return isNewBizDeviceActionCam() && hasSubLcd();
    }

    public static boolean hasSubLcd() {
        return getVersionPfAPI() >= 5 && 1 == ScalarProperties.getInt("device.sublcd.type");
    }

    public static boolean isAllParameterInheritedByPF() {
        return isNewBizDeviceActionCam() && hasSubLcd();
    }
}
