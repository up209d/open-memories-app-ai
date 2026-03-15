package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BatteryObserver;

/* loaded from: classes.dex */
public class STBatteryIcon extends BatteryIcon {
    static String[] tags = {BatteryObserver.TAG_LEVEL, STBackUpKey.PB_DISPLAY_MODE_KEY};

    public STBatteryIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.BatteryIcon, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.BatteryIcon, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.common.widget.BatteryIcon, com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.base.common.widget.BatteryIcon, com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
        int value = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        int status = BatteryObserver.getInt(BatteryObserver.TAG_STATUS);
        int level = checkBatteryLevel(value);
        int displayMode = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.PB_DISPLAY_MODE_KEY, 1);
        if (plugged == 1 || displayMode == 0) {
            setVisibility(4);
            return;
        }
        if (plugged == 2) {
            if (status == 2) {
                setImageLevel(BatteryIcon.BATTERY_STATUS_CHARGING);
                setVisibility(0);
                return;
            } else {
                if (status == 3) {
                    int setLevel = level | 16;
                    setImageLevel(setLevel);
                    setVisibility(0);
                    return;
                }
                return;
            }
        }
        setImageLevel(checkBatteryLevel(value));
        setVisibility(0);
    }
}
