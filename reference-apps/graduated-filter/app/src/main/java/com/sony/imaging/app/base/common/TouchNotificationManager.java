package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class TouchNotificationManager extends NotificationManager {
    public static final String TAG_TOUCHABLE_SCREEN = "TouchableScreen";
    public static final String TAG_TOUCH_PANEL_ENABLED = "TouchPanel";
    private static TouchNotificationManager instance = new TouchNotificationManager();

    public static TouchNotificationManager getInstance() {
        if (instance == null) {
            instance = new TouchNotificationManager();
        }
        return instance;
    }

    public void requestNotify(String tag) {
        notify(tag);
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return null;
    }
}
