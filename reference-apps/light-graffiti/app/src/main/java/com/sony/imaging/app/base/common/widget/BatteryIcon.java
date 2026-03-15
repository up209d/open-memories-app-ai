package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BatteryIcon extends ImageView implements NotificationListener {
    private static final int BATTERY_LEVEL_0 = 0;
    private static final int BATTERY_LEVEL_1 = 1;
    private static final int BATTERY_LEVEL_2 = 2;
    private static final int BATTERY_LEVEL_3 = 3;
    private static final int BATTERY_LEVEL_4 = 4;
    private static final int ENABLE_GET_BATTERY_THRESHOLD_LIST = 2;
    private static final int PREEND_LEVEL = 1;
    private static int[] batteryList = null;
    static String[] tags = {BatteryObserver.TAG_LEVEL, BatteryObserver.TAG_PLUGGED};

    public BatteryIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(4);
        if (Environment.getVersionPfAPI() >= 2) {
            batteryList = ScalarProperties.getIntArray("ui.battery.threshold.list");
        } else {
            batteryList = new int[3];
            batteryList[0] = 80;
            batteryList[1] = 50;
            batteryList[2] = 20;
        }
        for (int i = 0; i < 3; i++) {
            Log.i("BatteryIcon", "battery List i:" + Integer.toString(i) + " value:" + Integer.toString(batteryList[i]));
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
        int value = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        int level = checkBatteryLevel(value);
        if (plugged == 1) {
            setVisibility(4);
        } else {
            setImageLevel(level);
            setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BatteryObserver.setNotificationListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        BatteryObserver.removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int checkBatteryLevel(int value) {
        int level;
        if (1 > value) {
            level = 0;
        } else if (batteryList[2] >= value) {
            level = 1;
        } else if (batteryList[1] >= value) {
            level = 2;
        } else if (batteryList[0] >= value) {
            level = 3;
        } else {
            level = 4;
        }
        Log.i("BatteryIcon", "value:" + Integer.toString(value) + " Battery Level:" + Integer.toString(level));
        return level;
    }
}
