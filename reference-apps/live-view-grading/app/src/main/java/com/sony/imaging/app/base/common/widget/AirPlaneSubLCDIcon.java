package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class AirPlaneSubLCDIcon extends SubLcdIconView {
    private static final String TAG = "AirPlaneSubLCDIcon";

    public AirPlaneSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdIconView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean visible = isVisible();
        setOwnVisible(visible);
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected boolean isVisible() {
        return Settings.System.getInt(getContext().getContentResolver(), "airplane_mode_on", 0) == 1;
    }
}
