package com.sony.imaging.app.base.common.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BatteryLevelText extends TextView implements NotificationListener {
    static final String[] TAGS = {BatteryObserver.TAG_LEVEL, BatteryObserver.TAG_PLUGGED};
    private boolean mDontDisplay;

    public BatteryLevelText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDontDisplay = false;
        if (2 == ScalarProperties.getInt("model.category")) {
            this.mDontDisplay = true;
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
        int value = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        String batteryLevel = String.format(getResources().getString(R.string.restr_pin_enter_pin), Integer.toString(value));
        setText(batteryLevel);
        if (value == 0) {
            setVisibility(4);
        } else if (plugged == 1 || plugged == 2) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mDontDisplay) {
            BatteryObserver.setNotificationListener(this);
            onNotify(BatteryObserver.TAG_LEVEL);
        } else {
            setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        if (!this.mDontDisplay) {
            BatteryObserver.removeNotificationListener(this);
        }
        super.onDetachedFromWindow();
    }
}
