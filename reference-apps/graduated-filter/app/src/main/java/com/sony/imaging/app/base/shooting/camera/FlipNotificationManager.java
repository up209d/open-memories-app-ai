package com.sony.imaging.app.base.shooting.camera;

import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class FlipNotificationManager extends NotificationManager {
    public static final String FLIP_CHANGE = "FlipChange";
    private static FlipNotificationManager instance = new FlipNotificationManager();

    public static FlipNotificationManager getInstance() {
        if (instance == null) {
            instance = new FlipNotificationManager();
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
