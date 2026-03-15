package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/* loaded from: classes.dex */
public class TimelapseTextView extends TextView {
    public TimelapseTextView(Context context) {
        super(context);
    }

    public TimelapseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }
}
