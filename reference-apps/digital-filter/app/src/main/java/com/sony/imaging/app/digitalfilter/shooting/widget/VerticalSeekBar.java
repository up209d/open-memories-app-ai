package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class VerticalSeekBar extends SeekBar {
    private static final String TAG = "VerticalSeekBar";

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onDraw(Canvas c) {
        c.rotate(-90.0f);
        c.translate(-getHeight(), 0.0f);
        super.onDraw(c);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.widget.AbsSeekBar, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean isEventCall = false;
        switch (event.getScanCode()) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                isEventCall = true;
                moveUp();
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                moveDown();
                isEventCall = true;
                break;
            default:
                setFocusable(false);
                break;
        }
        Log.i("onKeyDown", TAG + getProgress() + "....." + keyCode + "......." + event + ".....");
        return isEventCall.booleanValue();
    }

    public int moveUp() {
        setProgress(getProgress() + 1);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        return getProgress();
    }

    public int moveDown() {
        setProgress(getProgress() - 1);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        return getProgress();
    }
}
