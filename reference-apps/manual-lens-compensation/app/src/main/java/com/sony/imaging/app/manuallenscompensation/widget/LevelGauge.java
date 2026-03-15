package com.sony.imaging.app.manuallenscompensation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/* loaded from: classes.dex */
public class LevelGauge extends SeekBar {
    public LevelGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }
}
