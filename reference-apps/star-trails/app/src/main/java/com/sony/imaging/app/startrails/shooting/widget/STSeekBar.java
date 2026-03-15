package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.sony.imaging.app.startrails.util.AppLog;

/* loaded from: classes.dex */
public class STSeekBar extends SeekBar {
    private static final String TAG = "STSeekBar";

    public STSeekBar(Context context) {
        super(context);
    }

    public STSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        AppLog.info(TAG, "Seek Bar onSizeChanged");
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
