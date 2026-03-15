package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class SubMenuArea extends RelativeLayout implements GestureDetector.OnGestureListener {
    private static final int ALPHAVIEW_HEIGHT = 480;
    private static final int ALPHAVIEW_MARGIN_LEFT = 0;
    private static final int ALPHAVIEW_MARGIN_TOP = 0;
    private static final int ALPHAVIEW_WIDTH = 72;
    private static final int FOCUSVIEW_HEIGHT = 132;
    private static final int FOCUSVIEW_MARGIN_LEFT = 0;
    private static final int FOCUSVIEW_MARGIN_TOP = 174;
    private static final int FOCUSVIEW_WIDTH = 100;
    private ImageView mAlphaView;
    private ImageView mFocusView;
    private GestureDetector mGesture;
    private int mTouchSlopY;
    private SubMenuView mView;

    private void initView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, FOCUSVIEW_HEIGHT);
        params.leftMargin = 0;
        params.topMargin = FOCUSVIEW_MARGIN_TOP;
        addView(this.mFocusView, params);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ALPHAVIEW_WIDTH, ALPHAVIEW_HEIGHT);
        params2.leftMargin = 0;
        params2.topMargin = 0;
        addView(this.mAlphaView, params2);
        if (this.mGesture != null) {
            this.mGesture.setIsLongpressEnabled(false);
        }
        this.mTouchSlopY = ViewConfiguration.getTouchSlop();
    }

    public SubMenuArea(Context context) {
        super(context);
        this.mGesture = new GestureDetector(this);
        this.mTouchSlopY = 0;
        this.mFocusView = new ImageView(context);
        this.mAlphaView = new ImageView(context);
        initView();
    }

    public SubMenuArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mGesture = new GestureDetector(this);
        this.mTouchSlopY = 0;
        this.mFocusView = new ImageView(context, attrs);
        this.mAlphaView = new ImageView(context, attrs);
        initView();
    }

    public void setSubMenuAreaTouchThrough(SubMenuView view) {
        this.mView = view;
    }

    private boolean isFocusArea(MotionEvent ev) {
        return ev.getX() > 0.0f && ev.getX() < 100.0f && ev.getY() > 174.0f && ev.getY() < 306.0f;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        this.mGesture.onTouchEvent(ev);
        if (this.mView == null) {
            return false;
        }
        switch (ev.getAction() & 255) {
            case 0:
                if (isFocusArea(ev)) {
                    this.mFocusView.setPressed(true);
                    break;
                }
                break;
            case 1:
                this.mFocusView.setPressed(false);
                break;
        }
        return this.mView.onTouchEvent(ev);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        if (Math.abs(arg1.getY() - arg0.getY()) > this.mTouchSlopY) {
            this.mFocusView.setPressed(false);
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }
}
