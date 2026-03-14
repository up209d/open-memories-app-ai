package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AFFlexibleSpotVariableSizePositionSetting extends AFFlexiblePositionSetting {
    private static final String FLEXIBLE_SPOT_SIZE_L = "large";
    private static final String FLEXIBLE_SPOT_SIZE_M = "midium";
    private static final String FLEXIBLE_SPOT_SIZE_S = "size s";
    private static final String LOG_INITIALIZE = "initialize";
    private static final String TAG = "AFFlexibleSpotVariableSizePositionSetting";
    private static final int X_STEP_VALUE = 30;
    private static final int Y_STEP_VALUE = 20;
    private Rect mAvailableYUVrect;
    private String mCurrentSize;
    private Rect mYUVrect;

    public AFFlexibleSpotVariableSizePositionSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mYUVrect = new Rect();
        this.mAvailableYUVrect = new Rect();
        this.mCurrentSize = FLEXIBLE_SPOT_SIZE_S;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting, com.sony.imaging.app.base.shooting.widget.AFFlexible, com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initialize();
    }

    private void initialize() {
        DisplayManager.VideoRect yuvRect = (DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        updateRect(yuvRect);
        if (isPFverOver2()) {
            String currentSize = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getFocusAreaFlexibleSpotSize();
            if (currentSize != null) {
                this.mCurrentSize = currentSize;
            } else {
                this.mCurrentSize = FLEXIBLE_SPOT_SIZE_S;
            }
        }
        Log.d(TAG, LOG_INITIALIZE);
    }

    public static boolean isPFverOver2() {
        return 2 <= CameraSetting.getPfApiVersion();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void updateRect(DisplayManager.VideoRect yuvRect) {
        this.mYUVrect.set(yuvRect.pxLeft, yuvRect.pxTop, yuvRect.pxRight, yuvRect.pxBottom);
        this.mAvailableYUVrect.set(yuvRect.pxLeft, yuvRect.pxTop, yuvRect.pxRight, yuvRect.pxBottom);
        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(FocusAreaController.FLEX);
        if (areaInfo != null) {
            Rect scalarAreaRect = areaInfo.rectInfos[1].rect;
            double hRate = Math.abs(yuvRect.pxBottom - yuvRect.pxTop) / 2000.0d;
            double wRate = Math.abs(yuvRect.pxRight - yuvRect.pxLeft) / 2000.0d;
            int availableArea_left = ((int) ((scalarAreaRect.left + 1000.0d) * wRate)) + yuvRect.pxLeft;
            int availableArea_top = ((int) ((scalarAreaRect.top + 1000.0d) * hRate)) + yuvRect.pxTop;
            int availableArea_right = ((int) ((scalarAreaRect.right + 1000.0d) * wRate)) + yuvRect.pxLeft;
            int availableArea_bottom = ((int) ((scalarAreaRect.bottom + 1000.0d) * hRate)) + yuvRect.pxTop;
            this.mAvailableYUVrect.set(availableArea_left, availableArea_top, availableArea_right, availableArea_bottom);
        }
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) this.mTouchArea.getLayoutParams();
        p.leftMargin = yuvRect.pxLeft;
        p.topMargin = yuvRect.pxTop;
        p.width = yuvRect.pxRight - yuvRect.pxLeft;
        p.height = yuvRect.pxBottom - yuvRect.pxTop;
        this.mTouchArea.setLayoutParams(p);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void moveToRight(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = this.mFrame.getLeft() + 30;
        int t = this.mFrame.getTop();
        int r = this.mFrame.getRight() + 30;
        int b = this.mFrame.getBottom();
        Rect orgRect = new Rect(l, t, r, b);
        Rect dstRect = adjustCoordinateX(orgRect);
        params.leftMargin = dstRect.left;
        params.topMargin = dstRect.top;
        this.mFrame.setLayoutParams(params);
        updateArrowVisibility(dstRect);
        this.mListener.onPositionChanged(dstRect);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void moveToLeft(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = this.mFrame.getLeft() - 30;
        int t = this.mFrame.getTop();
        int r = this.mFrame.getRight() - 30;
        int b = this.mFrame.getBottom();
        Rect orgRect = new Rect(l, t, r, b);
        Rect dstRect = adjustCoordinateX(orgRect);
        params.leftMargin = dstRect.left;
        params.topMargin = dstRect.top;
        this.mFrame.setLayoutParams(params);
        updateArrowVisibility(dstRect);
        this.mListener.onPositionChanged(dstRect);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void moveToUp(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = this.mFrame.getLeft();
        int t = this.mFrame.getTop() - 20;
        int r = this.mFrame.getRight();
        int b = this.mFrame.getBottom() - 20;
        Rect orgRect = new Rect(l, t, r, b);
        Rect dstRect = adjustCoordinateY(orgRect);
        params.leftMargin = dstRect.left;
        params.topMargin = dstRect.top;
        this.mFrame.setLayoutParams(params);
        updateArrowVisibility(dstRect);
        this.mListener.onPositionChanged(dstRect);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void moveToDown(int step, boolean returnAction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = this.mFrame.getLeft();
        int t = this.mFrame.getTop() + 20;
        int r = this.mFrame.getRight();
        int b = this.mFrame.getBottom() + 20;
        Rect orgRect = new Rect(l, t, r, b);
        Rect dstRect = adjustCoordinateY(orgRect);
        params.leftMargin = dstRect.left;
        params.topMargin = dstRect.top;
        this.mFrame.setLayoutParams(params);
        updateArrowVisibility(dstRect);
        this.mListener.onPositionChanged(dstRect);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void setNeutralPosition() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int left = this.mAvailableYUVrect.centerX() - (this.mFrame.getWidth() / 2);
        int top = this.mAvailableYUVrect.centerY() - (this.mFrame.getHeight() / 2);
        p.leftMargin = left;
        p.topMargin = top;
        this.mFrame.setLayoutParams(p);
        Rect r = new Rect(left, top, this.mFrame.getWidth() + left, this.mFrame.getHeight() + top);
        this.mListener.onPositionChanged(r);
        updateArrowVisibility(r);
    }

    private Rect adjustCoordinateX(Rect r) {
        Rect retR = new Rect(r);
        int xdiff = Math.abs(r.centerX() - this.mYUVrect.centerX()) % 30;
        if (this.mYUVrect.centerX() < retR.centerX()) {
            retR.right -= xdiff;
            retR.left -= xdiff;
        } else if (this.mYUVrect.centerX() > retR.centerX()) {
            retR.right += xdiff;
            retR.left += xdiff;
        }
        if (retR.left < this.mAvailableYUVrect.left) {
            retR.left = this.mAvailableYUVrect.left;
            retR.right = retR.left + this.mFrame.getWidth();
        }
        if (retR.right > this.mAvailableYUVrect.right) {
            retR.right = this.mAvailableYUVrect.right;
            retR.left = retR.right - this.mFrame.getWidth();
        }
        return retR;
    }

    private Rect adjustCoordinateY(Rect r) {
        Rect retR = new Rect(r);
        int ydiff = Math.abs(r.centerY() - this.mYUVrect.centerY()) % 20;
        if (this.mYUVrect.centerY() < retR.centerY()) {
            retR.top -= ydiff;
            retR.bottom -= ydiff;
        } else if (this.mYUVrect.centerY() > retR.centerY()) {
            retR.top += ydiff;
            retR.bottom += ydiff;
        }
        if (retR.top < this.mAvailableYUVrect.top) {
            retR.top = this.mAvailableYUVrect.top;
            retR.bottom = retR.top + this.mFrame.getHeight();
        }
        if (retR.bottom > this.mAvailableYUVrect.bottom) {
            retR.bottom = this.mAvailableYUVrect.bottom;
            retR.top = retR.bottom - this.mFrame.getHeight();
        }
        return retR;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void updateArrowVisibility(Rect focusRect) {
        if (focusRect.top == this.mAvailableYUVrect.top) {
            this.mUpArrow.setVisibility(4);
        } else {
            this.mUpArrow.setVisibility(0);
        }
        if (focusRect.bottom == this.mAvailableYUVrect.bottom) {
            this.mDownArrow.setVisibility(4);
        } else {
            this.mDownArrow.setVisibility(0);
        }
        if (focusRect.left == this.mAvailableYUVrect.left) {
            this.mLeftArrow.setVisibility(4);
        } else {
            this.mLeftArrow.setVisibility(0);
        }
        if (focusRect.right == this.mAvailableYUVrect.right) {
            this.mRightArrow.setVisibility(4);
        } else {
            this.mRightArrow.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting
    protected void touchMotion(MotionEvent e) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mFrame.getLayoutParams();
        int l = ((int) (e.getX() - (this.mFrame.getWidth() / 2))) + this.mYUVrect.left;
        int t = ((int) (e.getY() - (this.mFrame.getHeight() / 2))) + this.mYUVrect.top;
        int r = l + this.mFrame.getWidth();
        int b = t + this.mFrame.getHeight();
        Rect rect = adjustCoordinateY(adjustCoordinateX(new Rect(l, t, r, b)));
        params.leftMargin = rect.left;
        params.topMargin = rect.top;
        this.mFrame.setLayoutParams(params);
        updateArrowVisibility(rect);
        this.mListener.onPositionChanged(rect);
    }
}
