package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;

/* loaded from: classes.dex */
public class FocusMagnificationGuide extends RelativeLayout {
    private static final int FRAME_WIDTH = 2;
    private static final int GUIDE_FRAME_WIDTH = 3;
    private static final int OFFSET_H_11 = 15;
    private static final int OFFSET_H_169 = 0;
    private static final int OFFSET_H_32 = 0;
    private static final int OFFSET_H_43 = 0;
    private static final int OFFSET_V_11 = 0;
    private static final int OFFSET_V_169 = 13;
    private static final int OFFSET_V_32 = 6;
    private static final int OFFSET_V_43 = 1;
    private static final String TAG = "FocusMagnificationGuide";
    protected FrameView mGuideFrame;
    protected int mOffsetH;
    protected int mOffsetV;
    protected ImageView mZoomFrame;
    protected float mZoomRatio;

    /* loaded from: classes.dex */
    public class FrameView extends View {
        private Paint mGuidePaint;
        private Rect mGuideRect;

        public FrameView(Context context) {
            super(context);
            this.mGuidePaint = null;
            this.mGuideRect = null;
        }

        public FrameView(Context context, Paint paint) {
            super(context);
            this.mGuidePaint = null;
            this.mGuideRect = null;
            this.mGuidePaint = paint;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.mGuidePaint != null && this.mGuideRect != null) {
                Log.d(FocusMagnificationGuide.TAG, " onDraw " + this.mGuideRect.toShortString());
                float width = this.mGuidePaint.getStrokeWidth();
                int lt = (int) Math.floor(width / 2.0f);
                int rb = Math.round(width / 2.0f);
                canvas.drawRect(this.mGuideRect.left + lt, this.mGuideRect.top + lt, this.mGuideRect.right - rb, this.mGuideRect.bottom - rb, this.mGuidePaint);
            }
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.RESID_LINESTYLE_MF_ASSIST_COLOR_FRAMELINE));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            int frame_lt = (int) Math.floor(1.0d);
            int frame_rb = Math.round(1.0f);
            canvas.drawRect(frame_lt + 0, frame_lt + 0, getWidth() - frame_rb, getHeight() - frame_rb, paint);
            super.onDraw(canvas);
        }

        public void setDrawingConfig(int color, int line_width) {
            Paint paint = null;
            if (line_width > 0) {
                paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(line_width);
            }
            setGuidePaint(paint);
        }

        public void setGuidePaint(Paint paint) {
            this.mGuidePaint = paint;
            invalidate();
        }

        public void setGuildeRect(Rect rect) {
            this.mGuideRect = rect;
            invalidate();
        }
    }

    public FocusMagnificationGuide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOffsetH = 0;
        this.mOffsetV = 0;
        this.mZoomRatio = 1.0f;
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.RESID_LINESTYLE_MF_ASSIST_COLOR_MOVELINE));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        this.mGuideFrame = new FrameView(context, paint);
        this.mGuideFrame.setBackgroundColor(getResources().getColor(R.color.RESID_LINESTYLE_MF_ASSIST_COLOR_BACKGROUND));
        addView(this.mGuideFrame, new RelativeLayout.LayoutParams(-1, -1));
        this.mZoomFrame = new ImageView(context);
        this.mZoomFrame.setBackgroundResource(17306296);
        addView(this.mZoomFrame, new RelativeLayout.LayoutParams(-1, -1));
    }

    public void onNotify(String tag) {
        if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
            refreshGuideFrame();
            return;
        }
        if (CameraNotificationManager.PICTURE_SIZE.equals(tag)) {
            refresh();
        } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
            float digitalZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification() / 100.0f;
            setDigitalZoomRatio(digitalZoomRatio);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void refresh() {
        if (getWidth() != 0 && getHeight() != 0) {
            refreshOffsetToImageAspect(getWidth(), getHeight());
            refreshGuideFrame();
        }
        float digitalZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification() / 100.0f;
        setDigitalZoomRatio(digitalZoomRatio);
    }

    protected void refreshOffsetToImageAspect(int width, int height) {
        String image_aspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (PictureSizeController.ASPECT_3_2.equals(image_aspect)) {
            this.mOffsetH = 0;
            this.mOffsetV = 6;
        } else if (PictureSizeController.ASPECT_16_9.equals(image_aspect)) {
            this.mOffsetH = 0;
            this.mOffsetV = 13;
        } else if (PictureSizeController.ASPECT_4_3.equals(image_aspect)) {
            this.mOffsetH = 0;
            this.mOffsetV = 1;
        } else if (PictureSizeController.ASPECT_1_1.equals(image_aspect)) {
            this.mOffsetH = 15;
            this.mOffsetV = 0;
        } else {
            this.mOffsetH = 0;
            this.mOffsetV = 0;
        }
        Log.d(TAG, image_aspect + " : " + this.mOffsetH + ", " + this.mOffsetV);
    }

    protected void refreshGuideFrame() {
        if (getWidth() != 0 && getHeight() != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mGuideFrame.getLayoutParams();
            int i = this.mOffsetH;
            params.rightMargin = i;
            params.leftMargin = i;
            int i2 = this.mOffsetV;
            params.bottomMargin = i2;
            params.topMargin = i2;
            params.width = getWidth() - (this.mOffsetH * 2);
            params.height = getHeight() - (this.mOffsetV * 2);
            this.mGuideFrame.setLayoutParams(params);
            Log.d(TAG, "refreshGuideFrame( " + params.leftMargin + "," + params.topMargin + "," + params.rightMargin + "," + params.bottomMargin + LogHelper.MSG_CLOSE_BRACKET);
            Rect rect = FocusMagnificationController.getInstance().calcMagnifyingPositionRect(getWidth() - (this.mOffsetH * 2), getHeight() - (this.mOffsetV * 2));
            if (rect != null) {
                this.mGuideFrame.setGuildeRect(rect);
            }
        }
    }

    public void setDigitalZoomRatio(float ratio) {
        if (ratio < 1.0f) {
            ratio = 1.0f;
        }
        this.mZoomRatio = ratio;
        if (this.mZoomFrame != null && getWidth() != 0 && getHeight() != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mZoomFrame.getLayoutParams();
            int hoffset = this.mOffsetH;
            int voffset = this.mOffsetV;
            if (1.0f != this.mZoomRatio) {
                hoffset = (int) ((getWidth() - ((getWidth() - (hoffset * 2)) / this.mZoomRatio)) / 2.0f);
                voffset = (int) ((getHeight() - ((getHeight() - (voffset * 2)) / this.mZoomRatio)) / 2.0f);
            }
            params.leftMargin = hoffset;
            params.topMargin = voffset;
            params.rightMargin = hoffset;
            params.bottomMargin = voffset;
            params.width = getWidth() - (hoffset * 2);
            params.height = getHeight() - (voffset * 2);
            this.mZoomFrame.setLayoutParams(params);
        }
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        refreshOffsetToImageAspect(r - l, b - t);
        layoutGuide(l, t, r, b);
        layoutZoomGuide(l, t, r, b);
        invalidate();
    }

    public void layoutZoomGuide(int l, int t, int r, int b) {
        this.mZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification() / 100.0f;
        if (this.mZoomFrame != null) {
            int hoffset = this.mOffsetH;
            int voffset = this.mOffsetV;
            if (1.0f != this.mZoomRatio) {
                hoffset = (int) ((getWidth() - ((getWidth() - (hoffset * 2)) / this.mZoomRatio)) / 2.0f);
                voffset = (int) ((getHeight() - ((getHeight() - (voffset * 2)) / this.mZoomRatio)) / 2.0f);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
            params.addRule(13);
            if (-1 == indexOfChild(this.mZoomFrame)) {
                addViewInLayout(this.mZoomFrame, -1, params);
            }
            int widthSpec = ViewGroup.getChildMeasureSpec(getWidth() | 1073741824, hoffset * 2, params.width);
            int heightSpec = ViewGroup.getChildMeasureSpec(getHeight() | 1073741824, voffset * 2, params.height);
            this.mZoomFrame.measure(widthSpec, heightSpec);
            this.mZoomFrame.layout(hoffset, voffset, this.mZoomFrame.getMeasuredWidth() + hoffset, this.mZoomFrame.getMeasuredHeight() + voffset);
        }
    }

    public void layoutGuide(int l, int t, int r, int b) {
        if (this.mGuideFrame != null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
            params.addRule(13);
            if (-1 == indexOfChild(this.mGuideFrame)) {
                addViewInLayout(this.mGuideFrame, -1, params);
            }
            int widthSpec = ViewGroup.getChildMeasureSpec(getWidth() | 1073741824, this.mOffsetH * 2, params.width);
            int heightSpec = ViewGroup.getChildMeasureSpec(getHeight() | 1073741824, this.mOffsetV * 2, params.height);
            this.mGuideFrame.measure(widthSpec, heightSpec);
            this.mGuideFrame.layout(this.mOffsetH, this.mOffsetV, this.mGuideFrame.getMeasuredWidth() + this.mOffsetH, this.mGuideFrame.getMeasuredHeight() + this.mOffsetV);
            Log.d(TAG, "mGuideFrame(" + this.mOffsetH + "," + this.mOffsetV + ")-(" + this.mGuideFrame.getMeasuredWidth() + "," + this.mGuideFrame.getMeasuredHeight() + LogHelper.MSG_CLOSE_BRACKET);
            Rect rect = FocusMagnificationController.getInstance().calcMagnifyingPositionRect(this.mGuideFrame.getMeasuredWidth(), this.mGuideFrame.getMeasuredHeight());
            if (rect != null) {
                Log.d(TAG, "layoutGuide " + rect.toShortString());
                this.mGuideFrame.setGuildeRect(rect);
            }
        }
    }
}
