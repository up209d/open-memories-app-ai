package com.sony.imaging.app.startrails;

import android.app.Application;
import android.content.Context;

/* loaded from: classes.dex */
public class AppContext extends Application {
    private static Context context;

    @Override // android.app.Application
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
