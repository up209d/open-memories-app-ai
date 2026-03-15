package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class GraphView extends View {
    private static final int HISTOGRAM_DATA_SIZE = 64;
    private static final float HISTOGRAM_DISPLAY_MAX_VALUE = 1966.0199f;
    private static final float HISTOGRAM_DISPLAY_PERCENTAGE = 0.03f;
    private static final int HISTOGRAM_RESIZE_MAX_VALUE = 32767;
    private static final int H_BORDER = 1;
    private static final int OPAQUE_BLACK = -16777216;
    private static final int V_BORDER = 2;
    private int mColor;
    private short[] mData;
    private Paint paint;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        this.mColor = attr.getColor(0, OPAQUE_BLACK);
        this.paint = new Paint();
        this.paint.setColor(this.mColor);
        if (getBackground() == null) {
            setBackgroundDrawable(getResources().getDrawable(android.R.drawable.quickcontact_badge_overlay_normal_light));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        this.mData = null;
        super.onDetachedFromWindow();
    }

    public void setHistogram(short[] y) {
        this.mData = y;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mData != null) {
            int width = getWidth();
            int height = getHeight();
            int graphheight = height - 4;
            int pinWidth = width / HISTOGRAM_DATA_SIZE;
            int l = 1;
            int b = height - 2;
            for (int i = 0; i < HISTOGRAM_DATA_SIZE; i++) {
                int index = i << 1;
                int workData = this.mData[index] + this.mData[index + 1];
                if (workData > HISTOGRAM_DISPLAY_MAX_VALUE) {
                    workData = 1966;
                }
                float h = (graphheight * workData) / HISTOGRAM_DISPLAY_MAX_VALUE;
                if (h > graphheight) {
                    h = graphheight;
                }
                canvas.drawRect(l, b - h, l + pinWidth, b, this.paint);
                l += pinWidth;
            }
        }
    }
}
