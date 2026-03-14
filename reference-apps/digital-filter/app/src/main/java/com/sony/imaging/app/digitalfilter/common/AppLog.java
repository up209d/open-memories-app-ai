package com.sony.imaging.app.digitalfilter.common;

import android.util.Log;

/* loaded from: classes.dex */
public class AppLog {
    public static final boolean ENABLE_LOG_DEBUG = true;
    private static final String ENTER_TAG = " <";
    private static final String EXIT_TAG = " >";

    public static final void trace(String tag, String log) {
        Log.d(tag, log);
    }

    public static final void enter(String tag, String log) {
        Log.d(tag, log + ENTER_TAG);
    }

    public static final void exit(String tag, String log) {
        Log.d(tag, log + EXIT_TAG);
    }

    public static final void info(String tag, String log) {
        Log.i(tag, log);
    }

    public static final void warning(String tag, String log) {
        Log.w(tag, log);
    }

    public static final void error(String tag, String log) {
        Log.e(tag, log);
    }

    public static final void error(String tag, String log, Throwable tr) {
        error(tag, log);
        tr.printStackTrace();
    }

    public static String getMethodName() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

    public static String getClassName() {
        String fullPathClassName = new Exception().getStackTrace()[1].getClassName();
        String[] className = fullPathClassName.split("\\.", -1);
        return className[className.length - 1];
    }
}
