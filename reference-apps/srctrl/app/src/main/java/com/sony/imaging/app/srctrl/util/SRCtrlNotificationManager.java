package com.sony.imaging.app.srctrl.util;

import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class SRCtrlNotificationManager extends NotificationManager {
    public static final String REMOTE_BULB_SHOOT_END = "SRCtrlBulbShootEnd";
    public static final String SHOOT_END = "SRCtrlShootEnd";
    public static final String SILENT_SHUTTER_INH_FACTOR_CHANGED = "SRCtrlSilentShutterInhFactorChenged";
    private static SRCtrlNotificationManager instance = new SRCtrlNotificationManager();

    public static SRCtrlNotificationManager getInstance() {
        if (instance == null) {
            instance = new SRCtrlNotificationManager();
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
