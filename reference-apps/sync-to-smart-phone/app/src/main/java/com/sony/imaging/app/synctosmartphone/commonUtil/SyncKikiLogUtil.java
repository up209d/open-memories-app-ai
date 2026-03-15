package com.sony.imaging.app.synctosmartphone.commonUtil;

import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class SyncKikiLogUtil {
    public static void logAppLaunch() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_APP_LAUNCH, getAccumulateOptions());
    }

    public static void logAppOffByKey() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_APP_OFF_BY_KEY, getAccumulateOptions());
    }

    public static void logAppOffByUm() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_APP_OFF_BY_UM, getAccumulateOptions());
    }

    public static void logNumberOfTransfers() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_NUMBER_OF_TRANSFERS, getAccumulateOptions());
    }

    public static void logTransferSuccess() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_TRANSFER_SUCCESS, getAccumulateOptions());
    }

    public static void logTransferHalt() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_TRANSFER_HALT, getAccumulateOptions());
    }

    public static void logNotConnect() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_NOT_CONNECT, getAccumulateOptions());
    }

    public static void logSmartphoneMediaFull() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_SMARTPHONE_MEDIA_FULL, getAccumulateOptions());
    }

    public static void logCameraBatExhaused() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_CAMERA_BAT_EXHAUSTED, getAccumulateOptions());
    }

    public static void logNotInsertMemoryCard() {
        Kikilog.setUserLog(ConstantsSync.KIKILOG_ID_NOT_INSERT_MEMORY_CARD, getAccumulateOptions());
    }

    private static Kikilog.Options getAccumulateOptions() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        return options;
    }
}
