package com.sony.imaging.app.srctrl.webapi.util;

import android.util.Log;

/* loaded from: classes.dex */
public class SRCtrlServletLogger {
    private static final String tag = SRCtrlServletLogger.class.getName();

    public void log(String msg) {
        Log.v(tag, msg);
    }

    public void debug(String msg) {
        log(msg);
    }
}
