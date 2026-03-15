package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class TemperatureNotificationManager extends NotificationManager {
    public static final String TEMP_COUNTDOWN_INFO_CHANGED = "TemperatureCountDownInfoChanged";
    public static final String TEMP_COUNTDOWN_START = "TempCountDownStart";
    public static final String TEMP_COUNTDOWN_STOP = "TempCountDownStop";
    public static final String TEMP_STATUS_CHANGED = "TempStatusChanged";
    private static TemperatureNotificationManager mInstance;

    public static TemperatureNotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new TemperatureNotificationManager();
        }
        return mInstance;
    }

    public void requestNotify(String tag) {
        notify(tag);
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return null;
    }
}
