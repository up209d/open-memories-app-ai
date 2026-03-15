package com.sony.imaging.app.graduatedfilter.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFGraphView extends View implements NotificationListener {
    private static final String BASE_HIST = "base";
    private static final int BASE_INDEX = 128;
    private static final String BOTH_BASE_HIST = "both_base";
    private static final String BOTH_FILTER_HIST = "both_filter";
    private static final String BOTH_HIST = "both";
    private static final int BOTH_INDEX = 256;
    private static final String FILTER_HIST = "filter";
    private static final int FILTER_INDEX = 0;
    private static final int HISTOGRAM_DATA_SIZE = 64;
    private static final float HISTOGRAM_DISPLAY_MAX_VALUE = 1966.0199f;
    private static final float HISTOGRAM_DISPLAY_PERCENTAGE = 0.03f;
    private static final int HISTOGRAM_RESIZE_MAX_VALUE = 32767;
    private static final int H_BORDER = 1;
    private static final int OPAQUE_BLACK = -16777216;
    private static final int V_BORDER = 2;
    private int mColor;
    private short[] mData;
    private int mIndex;
    private String mMode;
    private Paint paint;

    public GFGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        this.mColor = attr.getColor(0, OPAQUE_BLACK);
        this.mMode = attr.getString(1);
        if (BOTH_FILTER_HIST.equalsIgnoreCase(this.mMode)) {
            this.mIndex = BOTH_INDEX;
        } else if (FILTER_HIST.equalsIgnoreCase(this.mMode)) {
            this.mIndex = 0;
        } else if (BASE_HIST.equalsIgnoreCase(this.mMode)) {
            this.mIndex = BASE_INDEX;
        } else if (BOTH_HIST.equalsIgnoreCase(this.mMode)) {
            this.mIndex = BOTH_INDEX;
        }
        this.paint = new Paint();
        this.paint.setColor(this.mColor);
        if (getBackground() == null) {
            setBackgroundDrawable(getResources().getDrawable(android.R.drawable.quickcontact_badge_overlay_normal_light));
        }
    }

    public void setHistogram(short[] y) {
        this.mData = y;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (BOTH_BASE_HIST.equals(this.mMode)) {
            this.paint.setColor(this.mColor);
            drawHistgram(canvas, BOTH_INDEX);
            this.paint.setColor(-30720);
            drawHistgram(canvas, BASE_INDEX);
            return;
        }
        if (BOTH_FILTER_HIST.equalsIgnoreCase(this.mMode)) {
            this.paint.setColor(this.mColor);
            drawHistgram(canvas, BOTH_INDEX);
            this.paint.setColor(-1);
            drawHistgram(canvas, 0);
            return;
        }
        if (BASE_HIST.equalsIgnoreCase(this.mMode)) {
            this.paint.setColor(this.mColor);
            drawHistgram(canvas, BASE_INDEX);
        } else {
            drawHistgram(canvas, this.mIndex);
        }
    }

    private void drawHistgram(Canvas canvas, int offset) {
        if (this.mData != null) {
            int width = getWidth();
            int height = getHeight();
            int graphheight = height - 4;
            int pinWidth = width / HISTOGRAM_DATA_SIZE;
            int l = 1;
            int b = height - 2;
            for (int i = 0; i < HISTOGRAM_DATA_SIZE; i++) {
                int index = i << 1;
                int workData = this.mData[offset + index] + this.mData[offset + index + 1];
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

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{GFConstants.HISTOGRAM_UPDATE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (!GFCommonUtil.getInstance().isMenuOpened()) {
            setVisibility(0);
        }
        short[] histData = (short[]) CameraNotificationManager.getInstance().getValue(tag);
        setHistogram(histData);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!GFCommonUtil.getInstance().isMenuOpened()) {
            setVisibility(4);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        setHistogram(null);
    }
}
