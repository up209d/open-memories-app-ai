package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class AirPlaneIcon extends ImageView {
    public AirPlaneIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isVisible()) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    protected boolean isVisible() {
        return Settings.System.getInt(getContext().getContentResolver(), "airplane_mode_on", 0) == 1;
    }
}
