package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.LogHelper;

/* loaded from: classes.dex */
public class ScalableLayout extends RelativeLayout {
    private static final String MSG_DRAW_TIME = "draw time : ";
    private static final String TAG = "ScalableLayout";
    private float mScaleHeight;

    public ScalableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ScalableLayout);
        this.mScaleHeight = attr.getFloat(0, 1.0f);
        attr.recycle();
    }

    public void setScaleHeight(float scale) {
        this.mScaleHeight = scale;
        invalidate();
    }

    public boolean getScaledRect(Rect rect) {
        if (this.mScaleHeight == 1.0f) {
            return false;
        }
        int h = getHeight() / 2;
        rect.top = (int) (h - ((h - rect.top) * this.mScaleHeight));
        rect.bottom = (int) (h - ((h - rect.bottom) * this.mScaleHeight));
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        long start = System.currentTimeMillis();
        int saveCount = 0;
        boolean scaleIt = this.mScaleHeight != 1.0f;
        if (scaleIt) {
            saveCount = canvas.save();
            int w = getWidth();
            int h = getHeight();
            canvas.scale(1.0f, this.mScaleHeight, w / 2.0f, h / 2.0f);
        }
        super.dispatchDraw(canvas);
        if (scaleIt) {
            canvas.restoreToCount(saveCount);
        }
        long end = System.currentTimeMillis();
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_DRAW_TIME).append(end - start).toString());
    }
}
