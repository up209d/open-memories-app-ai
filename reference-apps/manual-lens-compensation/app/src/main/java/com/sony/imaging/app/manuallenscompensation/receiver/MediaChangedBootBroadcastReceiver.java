package com.sony.imaging.app.manuallenscompensation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;

/* loaded from: classes.dex */
public class MediaChangedBootBroadcastReceiver extends BroadcastReceiver {
    public static final String APP_BOOT = "com.sony.imaging.app.lenscompensation.AppBoot";
    public static final String MEDIA_CHANGED = "com.sony.imaging.app.lenscompensation.MediaChanged";
    private static String TAG = "MediaChangedBootBroadcastReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (checkServcieAction(intent.getAction())) {
            AppLog.info(TAG, "Starting service" + AppLog.getMethodName() + SystemClock.currentThreadTimeMillis());
        }
    }

    private boolean checkServcieAction(String action) {
        AppLog.info(TAG, AppLog.getMethodName() + action);
        if (!action.equalsIgnoreCase(APP_BOOT) && (!action.equalsIgnoreCase(MEDIA_CHANGED) || MediaNotificationManager.getInstance().isNoCard())) {
            return false;
        }
        return true;
    }
}
