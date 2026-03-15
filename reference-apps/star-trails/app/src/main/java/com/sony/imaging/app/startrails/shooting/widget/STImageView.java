package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class STImageView extends ImageView {
    public STImageView(Context context) {
        super(context);
    }

    public STImageView(Context context, AttributeSet attrs) {
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
