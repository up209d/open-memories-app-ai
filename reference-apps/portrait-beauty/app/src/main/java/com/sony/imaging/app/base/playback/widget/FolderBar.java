package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class FolderBar extends ImageView {
    public static final int CENTER = 1;
    private static final int DEFAULT_DURATION = 500;
    public static final int LOWER = 2;
    public static final int NONE = -1;
    private static final int PUSH_PLAY_BEEP = -1;
    private static final int RELEASE_PLAY_BEEP = 1;
    public static final int UPPER = 0;
    private String mCenterBeepId;
    private int mCurrentPushedPart;
    private int mInsensitiveHeight;
    private String mLowerBeepId;
    private Runnable mReleasePressedRunnable;
    protected OnTouchListener mTouchListener;
    private String mUpperBeepId;
    private int m_soundTiming;

    /* loaded from: classes.dex */
    public interface OnTouchListener {
        boolean onTouchDown(int i);

        boolean onTouchUp(int i, int i2);
    }

    public void setTouchListener(OnTouchListener listener) {
        this.mTouchListener = listener;
    }

    public FolderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_soundTiming = 0;
        this.mReleasePressedRunnable = new ReleasePressedRunnable();
        setSelected(false);
        setPressed(false);
        setImageLevel(-1);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FolderBar);
        this.mInsensitiveHeight = attr.getDimensionPixelSize(0, 0);
        this.m_soundTiming = attr.getInteger(1, 0);
        this.mCenterBeepId = attr.getString(2);
        this.mUpperBeepId = attr.getString(3);
        this.mLowerBeepId = attr.getString(4);
    }

    public FolderBar(Context context) {
        super(context);
        this.m_soundTiming = 0;
        this.mReleasePressedRunnable = new ReleasePressedRunnable();
        setSelected(false);
        setPressed(false);
        setImageLevel(-1);
    }

    public void setInsensitiveHeight(int height) {
        this.mInsensitiveHeight = height;
    }

    public void setBeepPattern(int timing, String center, String upper, String lower) {
        this.m_soundTiming = timing;
        this.mCenterBeepId = center;
        this.mUpperBeepId = upper;
        this.mLowerBeepId = lower;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable() && !isLongClickable()) {
            return false;
        }
        float xPos = event.getX();
        float yPos = event.getY();
        int pushedArea = getTouchedArea(xPos, yPos);
        switch (event.getAction() & BatteryIcon.BATTERY_STATUS_CHARGING) {
            case 0:
                this.mCurrentPushedPart = pushedArea;
                if (this.m_soundTiming == -1) {
                    String beepId = null;
                    if (pushedArea == 1) {
                        beepId = this.mCenterBeepId;
                    } else if (pushedArea == 0) {
                        beepId = this.mUpperBeepId;
                    } else if (pushedArea == 2) {
                        beepId = this.mLowerBeepId;
                    }
                    if (beepId != null) {
                        BeepUtility.getInstance().playBeep(beepId);
                    }
                }
                removeCallbacks(this.mReleasePressedRunnable);
                setImageLevel(1);
                setPressed(true);
                setSelected(true);
                if (this.mTouchListener != null) {
                    this.mTouchListener.onTouchDown(this.mCurrentPushedPart);
                }
                return true;
            case 1:
                setImagePressed(pushedArea);
                if (this.m_soundTiming == 1) {
                    String beepId2 = null;
                    if (pushedArea == 1) {
                        beepId2 = this.mCenterBeepId;
                    } else if (pushedArea == 0) {
                        beepId2 = this.mUpperBeepId;
                    } else if (pushedArea == 2) {
                        beepId2 = this.mLowerBeepId;
                    }
                    if (beepId2 != null) {
                        BeepUtility.getInstance().playBeep(beepId2);
                    }
                }
                if (this.mTouchListener != null) {
                    this.mTouchListener.onTouchUp(pushedArea, this.mCurrentPushedPart);
                }
                return true;
            case 2:
                if (pushedArea == this.mCurrentPushedPart) {
                    setPressed(true);
                } else {
                    setPressed(false);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private int getTouchedArea(float xPos, float yPos) {
        float btnWidth = getWidth();
        float btnHeight = getHeight();
        float xEnd = PortraitBeautyConstants.INVALID_APERTURE_VALUE + btnWidth;
        float yUpperEnd = (btnHeight - this.mInsensitiveHeight) / 2.0f;
        float yCenterStart = (btnHeight - this.mInsensitiveHeight) / 2.0f;
        float yCenterEnd = yCenterStart + this.mInsensitiveHeight;
        float yLowerStart = (this.mInsensitiveHeight + btnHeight) / 2.0f;
        if (xPos >= PortraitBeautyConstants.INVALID_APERTURE_VALUE && xPos < xEnd && yPos >= PortraitBeautyConstants.INVALID_APERTURE_VALUE && yPos < yUpperEnd) {
            return 0;
        }
        if (xPos >= PortraitBeautyConstants.INVALID_APERTURE_VALUE && xPos < xEnd && yPos >= yCenterStart && yPos < yCenterEnd) {
            return 1;
        }
        if (xPos < PortraitBeautyConstants.INVALID_APERTURE_VALUE || xPos >= xEnd || yPos < yLowerStart || yPos >= btnHeight) {
            return -1;
        }
        return 2;
    }

    /* loaded from: classes.dex */
    private class ReleasePressedRunnable implements Runnable {
        private ReleasePressedRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            FolderBar.this.setPressed(false);
            FolderBar.this.setImageLevel(1);
        }
    }

    public void setImagePressed(int direction) {
        setImagePressed(direction, 500);
    }

    public void setImagePressed(int direction, int duration) {
        setPressed(true);
        setImageLevel(direction);
        removeCallbacks(this.mReleasePressedRunnable);
        postDelayed(this.mReleasePressedRunnable, duration);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mReleasePressedRunnable);
    }
}
