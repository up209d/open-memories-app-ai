package com.sony.imaging.app.pictureeffectplus;

import android.util.Log;

/* loaded from: classes.dex */
public class AppLog {
    public static final boolean ENABLE_LOG_DEBUG = true;
    private static final String ENTER_TAG = " <<<<<<<<";
    private static final String EXIT_TAG = " >>>>>>>>";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum LogLevel {
        LOG_LEVEL_ERROR,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_INFO,
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_VERBOSE
    }

    private static void outLog(LogLevel loglevel, String tag, String log) {
        switch (loglevel) {
            case LOG_LEVEL_ERROR:
                Log.e(tag, log);
                return;
            case LOG_LEVEL_WARNING:
                Log.w(tag, log);
                return;
            case LOG_LEVEL_INFO:
                Log.i(tag, log);
                return;
            case LOG_LEVEL_DEBUG:
                Log.d(tag, log);
                return;
            case LOG_LEVEL_VERBOSE:
                Log.v(tag, log);
                return;
            default:
                return;
        }
    }

    public static final void trace(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_DEBUG, tag, log);
    }

    public static final void enter(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_DEBUG, tag, log + ENTER_TAG);
    }

    public static final void exit(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_DEBUG, tag, log + EXIT_TAG);
    }

    public static final void checkIf(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_DEBUG, tag, log);
    }

    public static final void info(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_INFO, tag, log);
    }

    public static final void warning(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_WARNING, tag, log);
    }

    public static final void error(String tag, String log) {
        outLog(LogLevel.LOG_LEVEL_ERROR, tag, log);
    }

    public static final void error(String tag, String log, Throwable tr) {
        error(tag, log);
        tr.printStackTrace();
    }

    public static String getMethodName() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

    public static String getClassName() {
        return new Exception().getStackTrace()[1].getClassName();
    }
}
