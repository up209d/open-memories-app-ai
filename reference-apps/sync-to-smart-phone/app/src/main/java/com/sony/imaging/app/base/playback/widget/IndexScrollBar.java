package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class IndexScrollBar extends View implements GestureDetector.OnGestureListener {
    private static final int MIN_THUMB_HEIGHT = 48;
    private static final String MSG_END_ON_TOUCH_EVENT = "onTouchEvent end";
    private static final String MSG_ON_SCROLL = "onScroll ";
    private static final String MSG_ON_TOUCH_EVENT = "onTouchEvent ";
    private static final String TAG = "IndexScrollBar";
    private String mDownBeep;
    private GestureDetector mGestureDetector;
    private int mMinThumbHeight;
    private float mScrollStartPosition;
    private IndexScroller mScroller;
    private final Rect mTempBounds;
    private Drawable mThumb;
    private Drawable mTrack;
    private String mUpInsideBeep;

    /* loaded from: classes.dex */
    public interface IndexScroller {
        int getExtent();

        int getOffset();

        int getRange();

        void resisterScrollBar(IndexScrollBar indexScrollBar);

        void scrollThumb(int i, boolean z, int i2, int i3);
    }

    public IndexScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMinThumbHeight = 1;
        this.mTempBounds = new Rect();
        this.mDownBeep = null;
        this.mUpInsideBeep = null;
        this.mScrollStartPosition = Float.NEGATIVE_INFINITY;
        this.mThumb = getResources().getDrawable(17306078);
        this.mTrack = getResources().getDrawable(17306067);
        this.mMinThumbHeight = MIN_THUMB_HEIGHT;
        this.mDownBeep = BeepUtilityRsrcTable.BEEP_ID_TAP;
        this.mGestureDetector = new GestureDetector(context, this);
        this.mGestureDetector.setIsLongpressEnabled(false);
        setFocusable(false);
        setClickable(false);
    }

    public void setScroller(IndexScroller scroller) {
        this.mScroller = scroller;
        scroller.resisterScrollBar(this);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mScroller != null) {
            int theExtent = this.mScroller.getExtent();
            int theRange = this.mScroller.getRange();
            int theOffset = this.mScroller.getOffset();
            boolean drawTrack = true;
            boolean drawThumb = true;
            if (theExtent <= 0) {
                drawTrack = true;
                drawThumb = false;
            }
            getDrawingRect(this.mTempBounds);
            if (!canvas.quickReject(this.mTempBounds.left, this.mTempBounds.top, this.mTempBounds.right, this.mTempBounds.bottom, Canvas.EdgeType.AA)) {
                if (drawTrack) {
                    int w = (this.mTempBounds.width() - this.mTrack.getIntrinsicWidth()) / 2;
                    this.mTrack.setBounds(this.mTempBounds.left + w, this.mTempBounds.top, this.mTempBounds.right - w, this.mTempBounds.bottom);
                    this.mTrack.draw(canvas);
                }
                if (drawThumb) {
                    int size = getHeight();
                    int length = Math.round((size * theExtent) / theRange);
                    if (length < this.mMinThumbHeight) {
                        length = this.mMinThumbHeight;
                    }
                    int offset = Math.round(((size - length) * theOffset) / (theRange - theExtent));
                    int w2 = (this.mTempBounds.width() - this.mThumb.getIntrinsicWidth()) / 2;
                    if (offset + length > size) {
                        offset = size - length;
                    }
                    this.mThumb.setBounds(this.mTempBounds.left + w2, this.mTempBounds.top + offset, this.mTempBounds.right - w2, this.mTempBounds.top + offset + length);
                    this.mThumb.draw(canvas);
                }
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable() && !isLongClickable()) {
            return false;
        }
        int action = event.getActionMasked();
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_ON_TOUCH_EVENT).append(action).toString());
        if (action == 0) {
            setPressed(true);
        } else if (action == 1 || action == 3) {
            setPressed(false);
        }
        boolean result = this.mGestureDetector.onTouchEvent(event);
        if (action == 1) {
            if (this.mUpInsideBeep != null) {
                getHitRect(this.mTempBounds);
                if (this.mTempBounds.contains((int) event.getX(), (int) event.getY())) {
                    BeepUtility.getInstance().playBeep(this.mUpInsideBeep);
                }
            }
            float startPosition = this.mScrollStartPosition;
            this.mScrollStartPosition = Float.NEGATIVE_INFINITY;
            if (!result && Float.NEGATIVE_INFINITY != startPosition) {
                this.mScroller.scrollThumb((((int) (event.getY() - startPosition)) * this.mScroller.getRange()) / getHeight(), true, 0, 0);
            }
        }
        Log.d(TAG, MSG_END_ON_TOUCH_EVENT);
        return result;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent arg0) {
        if (this.mDownBeep != null) {
            BeepUtility.getInstance().playBeep(this.mDownBeep);
            return true;
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_ON_SCROLL).append(e2.getAction()).append(LogHelper.MSG_COLON).append(distanceY).append(", ").append(this.mScroller.getRange()).append(", ").append(getHeight()).toString());
        if (this.mScroller != null) {
            this.mScrollStartPosition = e1.getY();
            int distance = (((int) (e2.getY() - e1.getY())) * this.mScroller.getRange()) / getHeight();
            Log.i(TAG, MSG_ON_SCROLL + distance);
            this.mScroller.scrollThumb(distance, false, 0, 0);
            return true;
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    public boolean isScrolling() {
        return this.mScrollStartPosition != Float.NEGATIVE_INFINITY;
    }

    public void adjust() {
        invalidate();
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        Drawable d = this.mThumb;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        Drawable d2 = this.mTrack;
        if (d2 != null && d2.isStateful()) {
            d2.setState(getDrawableState());
        }
        invalidate();
    }
}
