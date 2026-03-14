package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class BaseButton extends RelativeLayout implements GestureDetector.OnGestureListener {
    private static final int DEFAULT_REPEAT_INTERVAL = 1000;
    private static final int PUSH_AND_RELEASE_PLAY_BEEP = 2;
    private static final int PUSH_PLAY_BEEP = -1;
    private static final int RELEASE_PLAY_BEEP = 1;
    private static final String TAG = "BaseButton";
    private boolean isLongPress;
    private GestureDetector mGestureDetector;
    private ImageView mImageView;
    private MotionEvent mMotionEvent;
    private String m_beepId;
    private int m_longPressIntervalTime;
    private String m_longPressedBeepId;
    private int m_soundTiming;
    private String m_text;
    private String m_textColor;
    private int m_textHeight;
    private int m_textOffsetX;
    private int m_textOffsetY;
    private String m_textStyle;
    private int m_textWidth;
    private Handler onRepeat;

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        RelativeLayout.LayoutParams textParam;
        this.m_soundTiming = 0;
        this.m_beepId = null;
        this.m_longPressedBeepId = null;
        this.m_longPressIntervalTime = 0;
        this.mGestureDetector = null;
        this.m_text = null;
        this.m_textColor = null;
        this.m_textStyle = null;
        this.m_textOffsetX = 0;
        this.m_textOffsetY = 0;
        this.m_textWidth = 0;
        this.m_textHeight = 0;
        this.isLongPress = false;
        this.mImageView = null;
        this.onRepeat = new Handler() { // from class: com.sony.imaging.app.base.common.widget.BaseButton.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (BaseButton.this.isLongPress) {
                    Log.d(BaseButton.TAG, "onRepeat");
                    MotionEvent event = BaseButton.this.getMotionEvent();
                    int posX = (int) event.getX();
                    int posY = (int) event.getY();
                    if (BaseButton.this.isInsideArea(posX, posY)) {
                        BaseButton.this.setPressed(true);
                        BeepUtility.getInstance().playBeep(BaseButton.this.m_longPressedBeepId);
                        BaseButton.this.actionTouchLongPress();
                    } else {
                        BaseButton.this.setPressed(false);
                    }
                    BaseButton.this.onRepeat.sendEmptyMessageDelayed(0, BaseButton.this.m_longPressIntervalTime);
                }
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseButton);
        this.m_soundTiming = typedArray.getInteger(0, 0);
        this.m_beepId = typedArray.getString(1);
        this.m_longPressedBeepId = typedArray.getString(2);
        this.m_longPressIntervalTime = typedArray.getInteger(3, 0);
        this.m_textOffsetX = typedArray.getDimensionPixelSize(4, 0);
        this.m_textOffsetY = typedArray.getDimensionPixelSize(5, 0);
        this.m_textWidth = typedArray.getDimensionPixelSize(6, 0);
        this.m_textHeight = typedArray.getDimensionPixelSize(7, 0);
        if (this.m_longPressIntervalTime == 0) {
            this.m_longPressIntervalTime = 1000;
        }
        this.mGestureDetector = new GestureDetector(context, this);
        this.mImageView = new ImageView(context, attrs);
        RelativeLayout.LayoutParams imageParam = new RelativeLayout.LayoutParams(context, attrs);
        imageParam.leftMargin = 0;
        imageParam.topMargin = 0;
        addView(this.mImageView, imageParam);
        if (this.m_textWidth != 0 && this.m_textHeight != 0) {
            textParam = new RelativeLayout.LayoutParams(this.m_textWidth, this.m_textHeight);
        } else {
            textParam = new RelativeLayout.LayoutParams(context, attrs);
        }
        textParam.leftMargin = this.m_textOffsetX;
        textParam.topMargin = this.m_textOffsetY;
        TextView textView = new TextView(context, attrs);
        textView.setGravity(17);
        addView(textView, textParam);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mImageView.getDrawable() == null) {
            this.mImageView.setImageResource(R.drawable.btnimage_base_focused);
        }
        setVisibility(0);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable() && !isLongClickable()) {
            return false;
        }
        int posX = (int) event.getX();
        int posY = (int) event.getY();
        Log.d(TAG, "onTouchEvent");
        setMotionEvent(event);
        this.mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & 255) {
            case 1:
                Log.d(TAG, "ACTION_UP");
                setPressed(false);
                this.isLongPress = false;
                if (isInsideArea(posX, posY)) {
                    onReleaseInside();
                    break;
                }
                break;
        }
        return true;
    }

    private void setMotionEvent(MotionEvent event) {
        this.mMotionEvent = event;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MotionEvent getMotionEvent() {
        return this.mMotionEvent;
    }

    private void onPressInside() {
        setPressed(true);
        if (this.m_soundTiming == -1 || this.m_soundTiming == 2) {
            BeepUtility.getInstance().playBeep(this.m_beepId);
        }
        actionTouchDown();
    }

    private void onReleaseInside() {
        setPressed(false);
        if (this.m_soundTiming == 1 || this.m_soundTiming == 2) {
            BeepUtility.getInstance().playBeep(this.m_beepId);
        }
        actionTouchUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInsideArea(int posX, int posY) {
        boolean isInside = false;
        int width = getWidth();
        int height = getHeight();
        if (posX >= 0 && posX <= width && posY >= 0 && posY <= height) {
            isInside = true;
        }
        Log.d(TAG, "isInsideArea posx [" + posX + "] posy [" + posY + "]");
        return isInside;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent arg0) {
        Log.d(TAG, "onLongPress");
        if (!this.isLongPress) {
            this.onRepeat.sendEmptyMessage(0);
        }
        this.isLongPress = true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown");
        int posX = (int) e.getX();
        int posY = (int) e.getY();
        if (isInsideArea(posX, posY)) {
            onPressInside();
            return false;
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling");
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll");
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress");
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        return false;
    }

    public void actionTouchDown() {
    }

    public void actionTouchUp() {
    }

    public void actionTouchLongPress() {
    }
}
