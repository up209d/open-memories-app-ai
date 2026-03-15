package com.sony.imaging.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/* loaded from: classes.dex */
public final class BatteryObserver extends BroadcastReceiver {
    private static final int LEVEL_RANGE = 100;
    private static final String LOG_ACTION = "ACTION_BATTERY_CHANGED ";
    private static final String LOG_RECEIVE = "onReceive ";
    private static final String LOG_REGISTER = "registerReceiver";
    private static final String LOG_SET_NOTIFY = "setNotificationListener";
    private static final String LOG_UNREGISTER = "unregisterReceiver";
    public static final int NO_PLUGGED = 0;
    public static final int PLUGGED = 1;
    private static final String TAG = "BatteryObserver";
    public static final String TAG_LEVEL = "LEVEL";
    public static final String TAG_PLUGGED = "PLUGGED";
    public static final String TAG_STATUS = "STATUS";
    private static final String _PLUGGED = "plugged";
    private static final String _STATUS = "status";
    private ValueMapNotificationManager mNotification = new ValueMapNotificationManager(true);
    private static BatteryObserver mObserver = new BatteryObserver();
    private static final StringBuilder LOG_STRING = new StringBuilder();

    private BatteryObserver() {
    }

    public static BatteryObserver getInstance() {
        return mObserver;
    }

    public static void start(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        Log.d(TAG, LOG_REGISTER);
        Intent intent = context.registerReceiver(mObserver, filter);
        if (intent != null) {
            mObserver.onReceive(context, intent);
        }
    }

    public static void stop(Context context) {
        Log.d(TAG, LOG_UNREGISTER);
        context.unregisterReceiver(mObserver);
    }

    public static void setNotificationListener(NotificationListener listener) {
        Log.d(TAG, LOG_SET_NOTIFY);
        mObserver.mNotification.setNotificationListener(listener);
    }

    public static void removeNotificationListener(NotificationListener listener) {
        mObserver.mNotification.removeNotificationListener(listener);
    }

    public static Object getValue(String tag) {
        return mObserver.mNotification.getValue(tag);
    }

    public static int getInt(String tag) {
        Integer value = (Integer) getValue(tag);
        if (value != null) {
            return value.intValue();
        }
        return -1;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, LOG_STRING.replace(0, LOG_STRING.length(), LOG_RECEIVE).append(action).toString());
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            Bundle data = intent.getExtras();
            int max = data.getInt("scale", 0);
            int level = max == 0 ? 0 : (data.getInt("level", 0) * 100) / max;
            Log.d(TAG, LOG_STRING.replace(0, LOG_STRING.length(), LOG_ACTION).append(level).toString());
            int plugged = intent.getIntExtra(_PLUGGED, 0);
            int status = intent.getIntExtra(_STATUS, 0);
            this.mNotification.onDeviceChanged(TAG_LEVEL, Integer.valueOf(level));
            this.mNotification.onDeviceChanged(TAG_PLUGGED, Integer.valueOf(plugged));
            this.mNotification.onDeviceChanged(TAG_STATUS, Integer.valueOf(status));
        }
    }
}
