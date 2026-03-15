package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import com.sony.imaging.app.timelapse.TimeLapseConstants;

/* loaded from: classes.dex */
public class TimelapseMovieFrameView extends View {
    private Paint mPaint;
    private Rect mRect;

    public TimelapseMovieFrameView(Context context) {
        super(context);
        this.mPaint = null;
        this.mRect = null;
        this.mPaint = new Paint();
        this.mRect = new Rect();
    }

    public void setRect(int left, int top, int right, int bottom) {
        this.mRect.left = left;
        this.mRect.top = top;
        this.mRect.right = right;
        this.mRect.bottom = bottom;
    }

    public void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, TimeLapseConstants.INVALID_APERTURE_VALUE));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setColor(Color.parseColor("#F39800"));
        canvas.drawRect(this.mRect, this.mPaint);
    }
}
