package com.sony.mexi.orb.server;

/* loaded from: classes.dex */
public final class DebugLogger {
    private static boolean sIsDebugOn = true;
    private static DebugMode sMode = DebugMode.DEBUG;
    private static DebugLogInterface sLogger = new DefaultServiceLogger();

    private DebugLogger() {
    }

    /* loaded from: classes.dex */
    public enum DebugMode {
        INFO(2),
        DEBUG(1),
        ERROR(0);

        int value;

        DebugMode(int val) {
            this.value = val;
        }
    }

    public static void error(String tag, String msg) {
        if (sIsDebugOn && sMode.value >= DebugMode.ERROR.value) {
            sLogger.error(tag, msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (sIsDebugOn && sMode.value >= DebugMode.DEBUG.value) {
            sLogger.debug(tag, msg);
        }
    }

    public static void info(String tag, String msg) {
        if (sIsDebugOn && sMode.value >= DebugMode.INFO.value) {
            sLogger.info(tag, msg);
        }
    }

    public static void setDebugEnable(boolean isOn, DebugMode mode) {
        sIsDebugOn = isOn;
        if (mode != null) {
            sMode = mode;
        }
    }

    public static void setLogger(DebugLogInterface logger) {
        if (logger != null) {
            sLogger = logger;
        }
    }
}
