package com.sony.imaging.app.snsdirect.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class IntentLauncher {
    private static final String TAG = IntentLauncher.class.getSimpleName();

    public static boolean isInstalled(Context context, String packageName) {
        if (context == null || packageName == null) {
            Log.e(TAG, "isInstalled: Some of input argument is null");
            return false;
        }
        List<ApplicationInfo> aInfos = context.getPackageManager().getInstalledApplications(0);
        if (aInfos.size() != 0) {
            for (ApplicationInfo aInfo : aInfos) {
                if (aInfo.packageName.equals(packageName)) {
                    Log.i(TAG, "Found Target package!: " + packageName);
                    return true;
                }
            }
        }
        Log.i(TAG, "Couldn't find target package: " + packageName);
        return false;
    }

    public static int getIntentSupportedPackageIndex(Context context, Intent intent, String[] packages) {
        if (context == null || intent == null) {
            Log.e(TAG, "isIntentSupported: Some of input argument is null");
            return -1;
        }
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 65536);
        if (apps.size() > 0) {
            Log.d(TAG, apps.size() + " ResolvedInfo found.");
            for (int i = 0; i < packages.length; i++) {
                for (ResolveInfo info : apps) {
                    if (info.activityInfo.packageName.equals(packages[i])) {
                        Log.d(TAG, "Found " + info.activityInfo.packageName + " supports this intent");
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static boolean launch(Activity openFrom, Intent intent) {
        if (openFrom == null || intent == null) {
            Log.e(TAG, "launch: Some of input argument is null");
            return false;
        }
        try {
            openFrom.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Activity Not Found");
            return false;
        }
    }
}
