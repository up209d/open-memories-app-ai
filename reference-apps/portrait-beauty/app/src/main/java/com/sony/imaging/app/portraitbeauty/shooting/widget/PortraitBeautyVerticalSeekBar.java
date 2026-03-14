package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;

/* loaded from: classes.dex */
public class PortraitBeautyVerticalSeekBar extends SeekBar {
    private final String TAG;

    public PortraitBeautyVerticalSeekBar(Context context) {
        super(context);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public PortraitBeautyVerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public PortraitBeautyVerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onSizeChanged(h, w, oldw, oldh);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onDraw(Canvas canvas) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        canvas.rotate(-90.0f);
        canvas.translate(-getHeight(), PortraitBeautyConstants.INVALID_APERTURE_VALUE);
        super.onDraw(canvas);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int progress) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        return false;
    }

    @Override // android.widget.AbsSeekBar, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Boolean bRetVal = false;
        switch (event.getScanCode()) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                bRetVal = false;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                bRetVal = false;
                break;
            default:
                setFocusable(false);
                break;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bRetVal.booleanValue();
    }

    public int moveUp() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setProgress(getProgress() + 1);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return getProgress();
    }

    public int moveDown() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setProgress(getProgress() - 1);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return getProgress();
    }
}
