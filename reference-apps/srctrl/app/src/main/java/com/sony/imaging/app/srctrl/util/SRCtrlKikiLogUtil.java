package com.sony.imaging.app.srctrl.util;

import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class SRCtrlKikiLogUtil {
    public static void logAppLaunch() {
        Kikilog.setUserLog(SRCtrlConstants.KIKILOG_ID_APP_LAUNCH, getAccumulateOptions());
    }

    public static void logRemoteShooting() {
        Kikilog.setUserLog(SRCtrlConstants.KIKILOG_ID_REMOTE_SHOOTING, getAccumulateOptions());
    }

    public static void logLocalShooting() {
        Kikilog.setUserLog(SRCtrlConstants.KIKILOG_ID_LOCAL_SHOOTING, getAccumulateOptions());
    }

    private static Kikilog.Options getAccumulateOptions() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        return options;
    }
}
