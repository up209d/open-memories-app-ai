package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.GpsNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class GpsSubLCDIcon extends SubLcdActiveIcon {
    private static final String TAG = "GpsSucLCDIcon";
    private static final String[] TAGS = {GpsNotificationManager.GPS_STATE_CHANGED};
    private final String TAG_GPS_POWER;
    private final String TAG_GPS_STATE;

    public GpsSubLCDIcon(Context context) {
        super(context, null);
        this.TAG_GPS_STATE = GpsNotificationManager.GPS_STATE;
        this.TAG_GPS_POWER = GpsNotificationManager.GPS_POWER;
    }

    public GpsSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG_GPS_STATE = GpsNotificationManager.GPS_STATE;
        this.TAG_GPS_POWER = GpsNotificationManager.GPS_POWER;
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationManager getNotificationManager() {
        return GpsNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationListener getNotificationListener() {
        return new GpsStateChangedListener();
    }

    /* loaded from: classes.dex */
    class GpsStateChangedListener implements NotificationListener {
        GpsStateChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GpsSubLCDIcon.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(GpsNotificationManager.GPS_STATE_CHANGED)) {
                boolean visible = GpsSubLCDIcon.this.isVisible();
                GpsSubLCDIcon.this.setOwnVisible(visible);
                if (visible) {
                    GpsSubLCDIcon.this.refresh();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        if (GpsNotificationManager.Status.PowerON != this.mNotifier.getValue(GpsNotificationManager.GPS_POWER)) {
            return false;
        }
        if (GpsNotificationManager.Status.Ok != this.mNotifier.getValue(GpsNotificationManager.GPS_STATE) && GpsNotificationManager.Status.Inh != this.mNotifier.getValue(GpsNotificationManager.GPS_STATE)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        if (GpsNotificationManager.Status.Ok == this.mNotifier.getValue(GpsNotificationManager.GPS_STATE)) {
            setLkid("LKID_SUBLCD_GPS_STATE", "PTN_ON");
        } else if (GpsNotificationManager.Status.Inh == this.mNotifier.getValue(GpsNotificationManager.GPS_STATE)) {
            setLkid("LKID_SUBLCD_GPS_STATE_INH", "PTN_ON");
        } else {
            setPattern("PTN_OFF");
        }
    }
}
