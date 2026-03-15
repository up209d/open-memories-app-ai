package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.BatteryObserver;

/* loaded from: classes.dex */
public class LGBatteryIcon extends BatteryIcon {
    static String[] tags = {BatteryObserver.TAG_LEVEL};

    public LGBatteryIcon(Context context, AttributeSet attrs) {
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
        if (plugged == 1) {
            setVisibility(4);
        } else {
            setImageLevel(checkBatteryLevel(value));
            setVisibility(0);
        }
    }
}
