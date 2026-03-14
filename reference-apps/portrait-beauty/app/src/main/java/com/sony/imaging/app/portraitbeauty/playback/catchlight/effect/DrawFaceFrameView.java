package com.sony.imaging.app.portraitbeauty.playback.catchlight.effect;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import com.sony.imaging.app.portraitbeauty.R;

/* loaded from: classes.dex */
public class DrawFaceFrameView extends View {
    Bitmap mBitmap;
    public int mBottom;
    public int mLeft;
    private Paint mPaint;
    public int mRight;
    public boolean mShowFn;
    public int mTop;
    public int mWidth;
    public Resources resources;

    public DrawFaceFrameView(Context context) {
        super(context);
        this.mPaint = null;
        this.mLeft = 0;
        this.mTop = 0;
        this.mRight = 0;
        this.mBottom = 0;
        this.mWidth = 2;
        this.mShowFn = true;
        this.mPaint = new Paint();
        this.resources = getContext().getResources();
        this.mBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.s_16_dd_parts_cl_fn_icon);
    }

    public int getColor() {
        return this.mPaint.getColor();
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
        invalidate();
    }

    public void setWidth(int width) {
        this.mWidth = width;
        invalidate();
    }

    public void setShow(boolean showFn) {
        this.mShowFn = showFn;
        invalidate();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (canvas != null) {
            this.mPaint.setStrokeWidth(this.mWidth);
            this.mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(this.mLeft, this.mTop, this.mRight, this.mBottom, this.mPaint);
        }
    }
}
