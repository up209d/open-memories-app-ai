package com.sony.imaging.app.fw;

import android.util.Log;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import java.lang.Thread;

/* loaded from: classes.dex */
public class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "AppUncaughtExceptionHandler";

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread arg0, Throwable e) {
        Log.e(TAG, "UncaughtException occurred!!");
        Log.e(TAG, "Stack Trace is the following");
        e.printStackTrace();
        Log.e(TAG, "Stack Trace is finished");
        if (e instanceof OutOfMemoryError) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_RECSTOP);
        }
    }
}
