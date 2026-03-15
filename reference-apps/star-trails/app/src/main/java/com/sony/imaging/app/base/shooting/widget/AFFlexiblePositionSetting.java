package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.widget.AbstractAFView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IConvertibleKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.InnerViewKeyHandler;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AFFlexiblePositionSetting extends AFFlexible implements IConvertibleKeyHandler {
    private static final double ARROW_DISPLAY_RATE = 0.07d;
    private static final String LOG_AFFLEXIBLEPOSITIONSETTING = "AFFlexiblePositionSetting";
    private static final String LOG_DISPATCHKEYEVENT = "dispatchKeyEvent";
    private static final String LOG_ON_CONVERTED_KEYDOWN = "onConvertedKeyDown";
    private static final int TOUCH_PARAM_X_CENTER = 2;
    private static final int TOUCH_PARAM_X_FLEX_GAP = 6;
    private static final int TOUCH_PARAM_X_MAX = 4;
    private static final int TOUCH_PARAM_X_MIN = 0;
    private static final int TOUCH_PARAM_Y_CENTER = 3;
    private static final int TOUCH_PARAM_Y_FLEX_GAP = 7;
    private static final int TOUCH_PARAM_Y_MAX = 5;
    private static final int TOUCH_PARAM_Y_MIN = 1;
    private static final int X_DIVIDE = 5;
    private static int X_RETURN_POS_MAX = 0;
    private static int X_RETURN_POS_MIN = 0;
    private static final int X_SKIP_STEP = 3;
    private static final int X_STEP_NUM = 17;
    private static final int Y_DIVIDE = 5;
    private static int Y_RETURN_POS_MAX = 0;
    private static int Y_RETURN_POS_MIN = 0;
    private static final int Y_SKIP_STEP = 2;
    private static final int Y_STEP_NUM = 11;
    protected RelativeLayout eeRelativeLayout;
    protected ImageView mDownArrow;
    protected InnerViewKeyHandler mInnerKeyHandler;
    private boolean mIsOnPreDraw_Done;
    protected ImageView mLeftArrow;
    protected PositionListener mListener;
    ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private TouchArea.OnTouchAreaListener mOnTouchAreaListener;
    protected ImageView mRightArrow;
    protected TouchArea mTouchArea;
    protected ImageView mUpArrow;
    private Rect mYUVrect;
    private static int[] PARAM_TABLE = {64, 90, 320, 240, 576, 390, 32, 30, 0, 0, 640, 480};
    private static final int X_STEP_CENTER = (int) Math.round(8.5d);
    private static final int Y_STEP_CENTER = (int) Math.round(5.5d);

    /* loaded from: classes.dex */
    public interface PositionListener {
        void onPositionChanged(Rect rect);
    }

    public AFFlexiblePositionSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mYUVrect = new Rect();
        this.mIsOnPreDraw_Done = false;
        this.mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                if (!AFFlexiblePositionSetting.this.mIsOnPreDraw_Done) {
                    AFFlexiblePositionSetting.this.updateArrowVisibility(new Rect(AFFlexiblePositionSetting.this.mFrame.getLeft(), AFFlexiblePositionSetting.this.mFrame.getTop(), AFFlexiblePositionSetting.this.mFrame.getRight(), AFFlexiblePositionSetting.this.mFrame.getBottom()));
                    AFFlexiblePositionSetting.this.mFrameIlluminator.setInvisible();
                    AFFlexiblePositionSetting.this.getViewTreeObserver().removeOnPreDrawListener(AFFlexiblePositionSetting.this.mOnPreDrawListener);
                    AFFlexiblePositionSetting.this.mIsOnPreDraw_Done = true;
                }
                return true;
            }
        };
        this.mOnTouchAreaListener = new TouchArea.OnTouchAreaListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting.2
            @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
            public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
                int touchOperationVersion = Environment.getTouchOperationVersion();
                if (touchOperationVersion != 1) {
                    return false;
                }
                AFFlexiblePositionSetting.this.touchMotion(e);
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                return true;
            }

            @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
            public boolean onTouchDown(MotionEvent e) {
                int touchOperationVersion = Environment.getTouchOperationVersion();
                if (touchOperationVersion < 2) {
                    return false;
                }
                AFFlexiblePositionSetting.this.touchMotion(e);
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                return true;
            }

            @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                AFFlexiblePositionSetting.this.touchMotion(e2);
                return true;
            }

            @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
            public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        };
        this.mInnerKeyHandler = new InnerViewKeyHandler(this, ICustomKey.CATEGORY_FOCUS_SETTING);
        initReturnPositions();
    }

    protected void initReturnPositions() {
        int edgePos = X_STEP_CENTER;
        do {
            edgePos -= 3;
        } while (edgePos >= 1);
        X_RETURN_POS_MIN = edgePos + 3;
        int edgePos2 = X_STEP_CENTER;
        do {
            edgePos2 += 3;
        } while (edgePos2 <= 17);
        X_RETURN_POS_MAX = edgePos2 - 3;
        int edgePos3 = Y_STEP_CENTER;
        do {
            edgePos3 -= 2;
        } while (edgePos3 >= 1);
        Y_RETURN_POS_MIN = edgePos3 + 2;
        int edgePos4 = Y_STEP_CENTER;
        do {
            edgePos4 += 2;
        } while (edgePos4 <= 11);
        Y_RETURN_POS_MAX = edgePos4 - 2;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mUpArrow = (ImageView) findViewById(R.id.up);
        this.mLeftArrow = (ImageView) findViewById(R.id.left);
        this.mDownArrow = (ImageView) findViewById(R.id.down);
        this.mRightArrow = (ImageView) findViewById(R.id.right);
        this.eeRelativeLayout = (RelativeLayout) findViewById(R.id.eeRelativeLayout);
        this.mTouchArea = (TouchArea) findViewById(R.id.toucharea);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected AbstractAFView.CameraNotificationListener getCameraNotificationListener() {
        return new AbstractAFView.CameraNotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting.3
            @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView.CameraNotificationListener, com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexible, com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayManager.VideoRect yuvRect = (DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        updateRect(yuvRect);
        updateArrows(yuvRect);
        this.mFrame.setSelected();
        this.mTouchArea.setTouchAreaListener(this.mOnTouchAreaListener);
        this.mIsOnPreDraw_Done = false;
        getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }

    protected void updateRect(DisplayManager.VideoRect yuvRect) {
        this.mYUVrect.set(yuvRect.pxLeft, yuvRect.pxTop, yuvRect.pxRight, yuvRect.pxBottom);
        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(FocusAreaController.FLEX);
        Rect rect = new Rect(yuvRect.pxLeft, yuvRect.pxTop, yuvRect.pxRight, yuvRect.pxBottom);
        if (areaInfo != null) {
            Rect scalarAreaRect = areaInfo.rectInfos[1].rect;
            Rect scalarFrameRect = areaInfo.rectInfos[2].rect;
            PARAM_TABLE[2] = rect.centerX();
            PARAM_TABLE[3] = rect.centerY();
            double scalarXGap = Math.abs((scalarAreaRect.right - scalarAreaRect.left) - (scalarFrameRect.right - scalarFrameRect.left)) / 17;
            double scalarYGap = Math.abs((scalarAreaRect.bottom - scalarAreaRect.top) - (scalarFrameRect.right - scalarFrameRect.left)) / 11;
            int yuvWidth = yuvRect.pxRight - yuvRect.pxLeft;
            int yuvHeight = yuvRect.pxBottom - yuvRect.pxTop;
            double hRate = yuvWidth / 2000.0d;
            double vRate = yuvHeight / 2000.0d;
            PARAM_TABLE[6] = (int) Math.round(scalarXGap * hRate);
            PARAM_TABLE[7] = (int) Math.round(scalarYGap * vRate);
            PARAM_TABLE[0] = PARAM_TABLE[2] - (PARAM_TABLE[6] * (17 - X_STEP_CENTER));
            PARAM_TABLE[1] = PARAM_TABLE[3] - (PARAM_TABLE[7] * (11 - Y_STEP_CENTER));
            PARAM_TABLE[4] = PARAM_TABLE[2] + (PARAM_TABLE[6] * (17 - X_STEP_CENTER));
            PARAM_TABLE[5] = PARAM_TABLE[3] + (PARAM_TABLE[7] * (11 - Y_STEP_CENTER));
        }
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) this.mTouchArea.getLayoutParams();
        p.leftMargin = yuvRect.pxLeft;
        p.topMargin = yuvRect.pxTop;
        p.width = yuvRect.pxRight - yuvRect.pxLeft;
        p.height = yuvRect.pxBottom - yuvRect.pxTop;
        this.mTouchArea.setLayoutParams(p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexible, com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onChangeYUV(DisplayManager.VideoRect rect) {
        super.onChangeYUV(rect);
        updateRect(rect);
        updateArrows(rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onZoomInfoChanged(DisplayManager.VideoRect videoRect, boolean illuminator) {
        this.mFrameIlluminator.setInvisible();
    }

    protected void updateArrows(DisplayManager.VideoRect rect) {
        RelativeLayout.LayoutParams pUp = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams pDown = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams pRight = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams pLeft = new RelativeLayout.LayoutParams(-2, -2);
        int verticalMargin = (int) ((rect.pxBottom - rect.pxTop) * ARROW_DISPLAY_RATE);
        int horizonalMargin = (int) ((rect.pxRight - rect.pxLeft) * ARROW_DISPLAY_RATE);
        pDown.addRule(14);
        pDown.addRule(12);
        pDown.bottomMargin = verticalMargin;
        pRight.addRule(15);
        pRight.addRule(11);
        pRight.rightMargin = horizonalMargin;
        pUp.addRule(14);
        pUp.addRule(10);
        pUp.topMargin = verticalMargin;
        pLeft.addRule(15);
        pLeft.addRule(9);
        pLeft.leftMargin = horizonalMargin;
        this.mDownArrow.setLayoutParams(pDown);
        this.mRightArrow.setLayoutParams(pRight);
        this.mUpArrow.setLayoutParams(pUp);
        this.mLeftArrow.setLayoutParams(pLeft);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.eeRelativeLayout.getLayoutParams();
        params.leftMargin = rect.pxLeft;
        params.topMargin = rect.pxTop;
        params.width = rect.pxRight - rect.pxLeft;
        params.height = rect.pxBottom - rect.pxTop;
        this.eeRelativeLayout.setLayoutParams(params);
    }

    protected void updateArrowVisibility(Rect focusRect) {
        if (focusRect.centerX() >= PARAM_TABLE[4]) {
            this.mRightArrow.setVisibility(4);
        } else if (focusRect.intersects(this.mRightArrow.getLeft() + this.eeRelativeLayout.getLeft(), this.mRightArrow.getTop() + this.eeRelativeLayout.getTop(), this.mRightArrow.getRight() + this.eeRelativeLayout.getLeft(), this.mRightArrow.getBottom() + this.eeRelativeLayout.getTop())) {
            this.mRightArrow.setVisibility(4);
        } else {
            this.mRightArrow.setVisibility(0);
        }
        if (focusRect.centerX() <= PARAM_TABLE[0]) {
            this.mLeftArrow.setVisibility(4);
        } else if (focusRect.intersects(this.mLeftArrow.getLeft() + this.eeRelativeLayout.getLeft(), this.mLeftArrow.getTop() + this.eeRelativeLayout.getTop(), this.mLeftArrow.getRight() + this.eeRelativeLayout.getLeft(), this.mLeftArrow.getBottom() + this.eeRelativeLayout.getTop())) {
            this.mLeftArrow.setVisibility(4);
        } else {
            this.mLeftArrow.setVisibility(0);
        }
        if (focusRect.centerY() <= PARAM_TABLE[1]) {
            this.mUpArrow.setVisibility(4);
        } else if (focusRect.intersects(this.mUpArrow.getLeft() + this.eeRelativeLayout.getLeft(), this.mUpArrow.getTop() + this.eeRelativeLayout.getTop(), this.mUpArrow.getRight() + this.eeRelativeLayout.getLeft(), this.mUpArrow.getBottom() + this.eeRelativeLayout.getTop())) {
            this.mUpArrow.setVisibility(4);
        } else {
            this.mUpArrow.setVisibility(0);
        }
        if (focusRect.centerY() >= PARAM_TABLE[5]) {
            this.mDownArrow.setVisibility(4);
        } else if (focusRect.intersects(this.mDownArrow.getLeft() + this.eeRelativeLayout.getLeft(), this.mDownArrow.getTop() + this.eeRelativeLayout.getTop(), this.mDownArrow.getRight() + this.eeRelativeLayout.getLeft(), this.mDownArrow.getBottom() + this.eeRelativeLayout.getTop())) {
            this.mDownArrow.setVisibility(4);
        } else {
            this.mDownArrow.setVisibility(0);
        }
    }

    protected void moveToRight(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int moveAmount = PARAM_TABLE[6] * step;
        int l = this.mFrame.getLeft() + moveAmount;
        int t = this.mFrame.getTop();
        int r = this.mFrame.getRight() + moveAmount;
        int b = this.mFrame.getBottom();
        Rect rect = new Rect(l, t, r, b);
        boolean moveOK = false;
        if (rect.centerX() <= PARAM_TABLE[4]) {
            moveOK = true;
        } else if (returnAction) {
            int moveAmount2 = PARAM_TABLE[7] * 2;
            int l2 = (PARAM_TABLE[2] - (this.mFrame.getWidth() / 2)) - (PARAM_TABLE[6] * (X_STEP_CENTER - X_RETURN_POS_MIN));
            int t2 = t + moveAmount2;
            int r2 = l2 + this.mFrame.getWidth();
            rect.set(l2, t2, r2, t2 + this.mFrame.getHeight());
            if (rect.centerY() > PARAM_TABLE[5]) {
                rect = getRect(X_RETURN_POS_MIN, Y_RETURN_POS_MIN);
            }
            moveOK = true;
        } else if (rect.centerX() > PARAM_TABLE[4]) {
            int l_edge = PARAM_TABLE[4] - (this.mFrame.getWidth() / 2);
            int r_edge = l_edge + this.mFrame.getWidth();
            if (l_edge != this.mFrame.getLeft()) {
                rect.set(l_edge, t, r_edge, b);
                moveOK = true;
            }
        }
        if (moveOK) {
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
            this.mFrame.setLayoutParams(params);
            updateArrowVisibility(rect);
            this.mListener.onPositionChanged(rect);
        }
    }

    protected void moveToLeft(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int moveAmount = PARAM_TABLE[6] * step;
        int l = this.mFrame.getLeft() - moveAmount;
        int t = this.mFrame.getTop();
        int r = this.mFrame.getRight() - moveAmount;
        int b = this.mFrame.getBottom();
        Rect rect = new Rect(l, t, r, b);
        boolean moveOK = false;
        if (rect.centerX() >= PARAM_TABLE[0]) {
            moveOK = true;
        } else if (returnAction) {
            int moveAmount2 = PARAM_TABLE[7] * 2;
            int l2 = (PARAM_TABLE[2] - (this.mFrame.getWidth() / 2)) - (PARAM_TABLE[6] * (X_STEP_CENTER - X_RETURN_POS_MAX));
            int t2 = t - moveAmount2;
            int r2 = l2 + this.mFrame.getWidth();
            rect.set(l2, t2, r2, t2 + this.mFrame.getHeight());
            if (rect.centerY() < PARAM_TABLE[1]) {
                rect = getRect(X_RETURN_POS_MAX, Y_RETURN_POS_MAX);
            }
            moveOK = true;
        } else if (rect.centerX() < PARAM_TABLE[0]) {
            int l_edge = PARAM_TABLE[0] - (this.mFrame.getWidth() / 2);
            int r_edge = l_edge + this.mFrame.getWidth();
            if (l_edge != this.mFrame.getLeft()) {
                rect.set(l_edge, t, r_edge, b);
                moveOK = true;
            }
        }
        if (moveOK) {
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
            this.mFrame.setLayoutParams(params);
            updateArrowVisibility(rect);
            this.mListener.onPositionChanged(rect);
        }
    }

    protected void moveToUp(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int moveAmount = PARAM_TABLE[7] * step;
        int l = this.mFrame.getLeft();
        int t = this.mFrame.getTop() - moveAmount;
        int r = this.mFrame.getRight();
        int b = this.mFrame.getBottom() - moveAmount;
        Rect rect = new Rect(l, t, r, b);
        boolean moveOK = false;
        if (rect.centerY() >= PARAM_TABLE[1]) {
            moveOK = true;
        } else if (returnAction) {
            int l2 = l - (PARAM_TABLE[6] * 3);
            int t2 = (PARAM_TABLE[3] - (this.mFrame.getHeight() / 2)) - (PARAM_TABLE[7] * (Y_STEP_CENTER - Y_RETURN_POS_MAX));
            int r2 = l2 + this.mFrame.getWidth();
            int b2 = t2 + this.mFrame.getHeight();
            rect.set(l2, t2, r2, b2);
            if (rect.centerX() < PARAM_TABLE[0]) {
                rect = getRect(X_RETURN_POS_MAX, Y_RETURN_POS_MAX);
            }
            moveOK = true;
        } else if (rect.centerY() < PARAM_TABLE[1]) {
            int t_edge = PARAM_TABLE[1] - (this.mFrame.getHeight() / 2);
            int b_edge = t_edge + this.mFrame.getHeight();
            if (t_edge != this.mFrame.getTop()) {
                rect.set(l, t_edge, r, b_edge);
                moveOK = true;
            }
        }
        if (moveOK) {
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
            this.mFrame.setLayoutParams(params);
            updateArrowVisibility(rect);
            this.mListener.onPositionChanged(rect);
        }
    }

    protected void moveToDown(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int moveAmount = PARAM_TABLE[7] * step;
        int l = this.mFrame.getLeft();
        int t = this.mFrame.getTop() + moveAmount;
        int r = this.mFrame.getRight();
        int b = this.mFrame.getBottom() + moveAmount;
        Rect rect = new Rect(l, t, r, b);
        boolean moveOK = false;
        if (rect.centerY() <= PARAM_TABLE[5]) {
            moveOK = true;
        } else if (returnAction) {
            int l2 = l + (PARAM_TABLE[6] * 3);
            int t2 = (PARAM_TABLE[3] - (this.mFrame.getHeight() / 2)) - (PARAM_TABLE[7] * (Y_STEP_CENTER - Y_RETURN_POS_MIN));
            int r2 = l2 + this.mFrame.getWidth();
            int b2 = t2 + this.mFrame.getHeight();
            rect.set(l2, t2, r2, b2);
            if (rect.centerX() > PARAM_TABLE[4]) {
                rect = getRect(X_RETURN_POS_MIN, Y_RETURN_POS_MIN);
            }
            moveOK = true;
        } else if (rect.centerY() > PARAM_TABLE[5]) {
            int t_edge = PARAM_TABLE[5] - (this.mFrame.getHeight() / 2);
            int b_edge = t_edge + this.mFrame.getHeight();
            if (t_edge != this.mFrame.getTop()) {
                rect.set(l, t_edge, r, b_edge);
                moveOK = true;
            }
        }
        if (moveOK) {
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
            this.mFrame.setLayoutParams(params);
            updateArrowVisibility(rect);
            this.mListener.onPositionChanged(rect);
        }
    }

    private Rect getRect(int xStepPos, int yStepPos) {
        int l = (PARAM_TABLE[2] - (this.mFrame.getWidth() / 2)) + (PARAM_TABLE[6] * (xStepPos - X_STEP_CENTER));
        int t = (PARAM_TABLE[3] - (this.mFrame.getHeight() / 2)) + (PARAM_TABLE[7] * (yStepPos - Y_STEP_CENTER));
        int r = l + this.mFrame.getWidth();
        int b = t + this.mFrame.getHeight();
        return new Rect(l, t, r, b);
    }

    public void setPositionListener(PositionListener l) {
        this.mListener = l;
    }

    protected void setNeutralPosition() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int left = PARAM_TABLE[2] - (this.mFrame.getWidth() / 2);
        int top = PARAM_TABLE[3] - (this.mFrame.getHeight() / 2);
        p.leftMargin = left;
        p.topMargin = top;
        this.mFrame.setLayoutParams(p);
        Rect r = new Rect(left, top, this.mFrame.getWidth() + left, this.mFrame.getHeight() + top);
        this.mListener.onPositionChanged(r);
        updateArrowVisibility(r);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(LOG_AFFLEXIBLEPOSITIONSETTING, LOG_DISPATCHKEYEVENT);
        if (event.getAction() != 0) {
            return false;
        }
        boolean ret = onKeyDown(event.getKeyCode(), event);
        return ret;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mInnerKeyHandler.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        Log.d(LOG_AFFLEXIBLEPOSITIONSETTING, LOG_ON_CONVERTED_KEYDOWN);
        int scanCode = event.getScanCode();
        int device = this.mDisplayModeNotifier.getActiveDevice();
        DisplayManager.DeviceStatus deviceStatus = this.mDisplayModeNotifier.getActiveDeviceStatus();
        boolean ret = false;
        if (device == -1) {
            device = 0;
        }
        int panelViewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
        CustomizableFunction customFunc = (CustomizableFunction) func;
        switch (customFunc) {
            case MainNext:
            case ThirdNext:
                moveToUp(1, false);
                ret = true;
                break;
            case MainPrev:
            case ThirdPrev:
                moveToDown(1, false);
                ret = true;
                break;
            case SubNext:
                if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                    moveToLeft(1, true);
                } else {
                    moveToRight(1, true);
                }
                ret = true;
                break;
            case SubPrev:
                if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                    moveToRight(1, true);
                } else {
                    moveToLeft(1, true);
                }
                ret = true;
                break;
            case Reset:
                setNeutralPosition();
                ret = true;
                break;
            default:
                switch (scanCode) {
                    case 103:
                        moveToUp(1, false);
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToRight(1, false);
                        } else {
                            moveToLeft(1, false);
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToLeft(1, false);
                        } else {
                            moveToRight(1, false);
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                        moveToDown(1, false);
                        ret = true;
                        break;
                }
        }
        return ret ? 1 : 0;
    }

    protected void touchMotion(MotionEvent e) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = ((int) (e.getX() - (this.mFrame.getWidth() / 2))) + this.mYUVrect.left;
        int t = ((int) (e.getY() - (this.mFrame.getHeight() / 2))) + this.mYUVrect.top;
        int r = l + this.mFrame.getWidth();
        int b = t + this.mFrame.getHeight();
        if (e.getX() < PARAM_TABLE[0]) {
            l = PARAM_TABLE[0] - (this.mFrame.getWidth() / 2);
            r = l + this.mFrame.getWidth();
        }
        if (e.getX() > PARAM_TABLE[4]) {
            l = PARAM_TABLE[4] - (this.mFrame.getWidth() / 2);
            r = l + this.mFrame.getWidth();
        }
        if (e.getY() < PARAM_TABLE[1]) {
            t = PARAM_TABLE[1] - (this.mFrame.getHeight() / 2);
            b = t + this.mFrame.getHeight();
        }
        if (e.getY() > PARAM_TABLE[5]) {
            t = PARAM_TABLE[5] - (this.mFrame.getHeight() / 2);
            b = t + this.mFrame.getHeight();
        }
        params.leftMargin = l;
        params.topMargin = t;
        this.mFrame.setLayoutParams(params);
        Rect rect = new Rect(l, t, r, b);
        updateArrowVisibility(rect);
        this.mListener.onPositionChanged(rect);
    }
}
