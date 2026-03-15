package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/* loaded from: classes.dex */
public class STImageButton extends ImageButton {
    public STImageButton(Context context) {
        super(context);
    }

    public STImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }
}
