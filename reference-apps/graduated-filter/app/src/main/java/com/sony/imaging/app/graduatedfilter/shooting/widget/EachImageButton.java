package com.sony.imaging.app.graduatedfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/* loaded from: classes.dex */
public class EachImageButton extends ImageButton {
    public EachImageButton(Context context) {
        super(context);
    }

    public EachImageButton(Context context, AttributeSet attrs) {
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
