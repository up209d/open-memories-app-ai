package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.sony.imaging.app.base.common.ISubLcdDrawer;
import com.sony.imaging.app.base.common.SubLcdManager;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BatteryIcon extends ImageView implements NotificationListener, ISubLcdDrawer {
    private static final int BATTERY_LEVEL_0 = 0;
    private static final int BATTERY_LEVEL_1 = 1;
    private static final int BATTERY_LEVEL_2 = 2;
    private static final int BATTERY_LEVEL_3 = 3;
    private static final int BATTERY_LEVEL_4 = 4;
    public static final int BATTERY_STATUS_CHARGING = 255;
    public static final int BIT_BATTERY_STATUS_DISCHARGING = 16;
    private static final int ENABLE_GET_BATTERY_THRESHOLD_LIST = 2;
    private static final String MSG_DRAW = "draw subLcd ";
    private static final int PREEND_LEVEL = 1;
    protected String mLid;
    protected String mLkid;
    protected String mPattern;
    private static int[] batteryList = null;
    static String[] tags = {BatteryObserver.TAG_LEVEL, BatteryObserver.TAG_PLUGGED, BatteryObserver.TAG_STATUS};
    protected static String[] LKIDs = {"LKID_SUBLCD_BATTERY_STATE_LV0", "LKID_SUBLCD_BATTERY_STATE_LV1", "LKID_SUBLCD_BATTERY_STATE_LV2", "LKID_SUBLCD_BATTERY_STATE_LV3", "LKID_SUBLCD_BATTERY_STATE_LV4"};

    public BatteryIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLid = "LID_SUBLCD_BATTERY_STATE";
        this.mPattern = "PTN_ON";
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
        if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
            setVisibility(4);
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
        int status = BatteryObserver.getInt(BatteryObserver.TAG_STATUS);
        int level = checkBatteryLevel(value);
        if (Environment.isNewBizDeviceActionCam()) {
            if (plugged == 1) {
                setVisibility(4);
                return;
            } else {
                setImageLevel(level);
                setVisibility(0);
                return;
            }
        }
        if (plugged == 1) {
            setVisibility(4);
            return;
        }
        if (plugged == 2) {
            if (status == 2) {
                setImageLevel(BATTERY_STATUS_CHARGING);
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
        setImageLevel(level);
        setVisibility(0);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (Environment.isNewBizDeviceActionCam()) {
            SubLcdManager.getInstance().requestDraw();
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override // android.widget.ImageView
    public void setImageLevel(int level) {
        if (Environment.isNewBizDeviceActionCam() && level >= 0 && level < LKIDs.length && !LKIDs[level].equals(this.mLkid)) {
            this.mLkid = LKIDs[level];
            if (getVisibility() == 0) {
                SubLcdManager.getInstance().requestDraw();
            }
        }
        super.setImageLevel(level);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        this.mLkid = null;
        this.mPattern = "PTN_ON";
        super.onAttachedToWindow();
        BatteryObserver.setNotificationListener(this);
        onNotify(BatteryObserver.TAG_LEVEL);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        BatteryObserver.removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    protected int checkBatteryLevel(int value) {
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

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public String getLId() {
        return this.mLid;
    }

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public SubLcdManager.Element getSubLcdElement(String blink) {
        String pattern = this.mPattern;
        if (blink != null && (pattern == null || "PTN_ON".equals(pattern))) {
            pattern = blink;
        }
        boolean isVisible = getVisibility() == 0;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_DRAW).append(this.mLid).append(" : ").append(isVisible).append(", ").append(this.mLkid).append(", ").append(pattern);
        PTag.start(builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (!isVisible || this.mLid == null || this.mLkid == null) {
            return null;
        }
        if (pattern != null) {
            SubLcdManager.Element elem = SubLcdManager.Element.makeIcon(this.mLid, this.mLkid, pattern);
            return elem;
        }
        SubLcdManager.Element elem2 = SubLcdManager.Element.makeIcon(this.mLid, this.mLkid);
        return elem2;
    }
}
