package com.sony.imaging.app.photoretouch.playback.layout.softskin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/* loaded from: classes.dex */
public class DrawFaceFrameView extends View {
    public int mBottom;
    public int mLeft;
    private Paint mPaint;
    public int mRight;
    public int mTop;

    public DrawFaceFrameView(Context context) {
        super(context);
        this.mPaint = null;
        this.mLeft = 0;
        this.mTop = 0;
        this.mRight = 0;
        this.mBottom = 0;
        this.mPaint = new Paint();
    }

    public int getColor() {
        return this.mPaint.getColor();
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
        invalidate();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (canvas != null) {
            this.mPaint.setStrokeWidth(2.0f);
            this.mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(this.mLeft, this.mTop, this.mRight, this.mBottom, this.mPaint);
        }
    }
}
