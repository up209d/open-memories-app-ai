package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;
import com.sony.imaging.app.startrails.util.STConstants;

/* loaded from: classes.dex */
public class STProgressBar extends ProgressBar {
    public STProgressBar(Context context) {
        super(context);
        setVisibility(4);
    }

    public STProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public STProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (STConstants.sCaptureImageCounter < 1 && STSelfTimerMenuController.getInstance().isSelfTimer()) {
            setVisibility(4);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setVisibility(4);
    }
}
