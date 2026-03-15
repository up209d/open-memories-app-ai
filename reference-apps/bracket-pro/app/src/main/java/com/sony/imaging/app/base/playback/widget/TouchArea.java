package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;

/* loaded from: classes.dex */
public class TouchArea extends View {
    private static final String MSG_ACTION_UP = "ACTION_UP";
    private static final String MSG_ON_DOWN = "onDown";
    private static final String MSG_ON_FLING = "onFling";
    private static final String MSG_ON_LONG_PRESS = "onLongPress";
    private static final String MSG_ON_SCROLL = "onScroll";
    private static final String MSG_ON_SHOW_PRESS = "onShowPress";
    private static final String MSG_ON_SINGLE_TAP = "onSingleTapUp";
    private static final String TAG = TouchArea.class.getSimpleName();
    protected static Rect mScratchRect = new Rect();
    private boolean isScrolled;
    private GestureDetector mGestureDetector;
    private boolean mIgnoreUpAfterScroll;
    protected OnTouchAreaListener mTouchListener;

    /* loaded from: classes.dex */
    public interface OnTouchAreaListener {
        boolean onFlick(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        boolean onTouchDown(MotionEvent motionEvent);

        boolean onTouchUp(MotionEvent motionEvent, boolean z);
    }

    public TouchArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIgnoreUpAfterScroll = false;
        this.isScrolled = false;
        setClickable(true);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.TouchArea);
        this.mIgnoreUpAfterScroll = attr.getBoolean(0, false);
        this.mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setTouchAreaListener(OnTouchAreaListener listener) {
        this.mTouchListener = listener;
    }

    public void setIgnoreUpAfterScroll(boolean ignoreUpAfterScroll) {
        this.mIgnoreUpAfterScroll = ignoreUpAfterScroll;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable() && !isLongClickable()) {
            return false;
        }
        if (this.mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction() & BatteryIcon.BATTERY_STATUS_CHARGING) {
            case 1:
                Log.v(TAG, MSG_ACTION_UP);
                if (this.mTouchListener == null || this.isScrolled) {
                    return true;
                }
                Rect r = mScratchRect;
                getHitRect(r);
                r.offset(getLeft(), getTop());
                boolean isReleasedInside = r.contains((int) event.getX(), (int) event.getY());
                this.mTouchListener.onTouchUp(event, isReleasedInside);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /* loaded from: classes.dex */
    class GestureListener implements GestureDetector.OnGestureListener {
        GestureListener() {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_DOWN);
            if (TouchArea.this.mTouchListener != null) {
                TouchArea.this.mTouchListener.onTouchDown(e);
                if (TouchArea.this.mIgnoreUpAfterScroll) {
                    TouchArea.this.isScrolled = false;
                    return true;
                }
                return true;
            }
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_FLING);
            if (TouchArea.this.mTouchListener != null) {
                return TouchArea.this.mTouchListener.onFlick(e1, e2, velocityX, velocityY);
            }
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_SCROLL);
            if (TouchArea.this.mTouchListener == null) {
                return false;
            }
            boolean ret = TouchArea.this.mTouchListener.onScroll(e1, e2, distanceX, distanceY);
            if (ret && TouchArea.this.mIgnoreUpAfterScroll) {
                TouchArea.this.isScrolled = true;
                return ret;
            }
            return ret;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_LONG_PRESS);
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_SHOW_PRESS);
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            Log.v(TouchArea.TAG, TouchArea.MSG_ON_SINGLE_TAP);
            return false;
        }
    }
}
