package com.sony.imaging.app.manuallenscompensation.commonUtil;

import android.app.Application;
import android.content.Context;

/* loaded from: classes.dex */
public class AppContext extends Application {
    private static final String TAG = "AppContext";
    private static Context context;

    @Override // android.app.Application
    public void onCreate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCreate();
        context = getApplicationContext();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static Context getAppContext() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return context;
    }
}
