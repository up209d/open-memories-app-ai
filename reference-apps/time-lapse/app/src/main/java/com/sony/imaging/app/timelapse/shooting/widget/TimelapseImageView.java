package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class TimelapseImageView extends ImageView {
    public TimelapseImageView(Context context) {
        super(context);
    }

    public TimelapseImageView(Context context, AttributeSet attrs) {
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
