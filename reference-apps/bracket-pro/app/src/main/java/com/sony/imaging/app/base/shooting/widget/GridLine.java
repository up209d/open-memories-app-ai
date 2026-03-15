package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class GridLine extends View {
    private static final String LOG_MSG_GETGRIDLINE = "getGridLine = ";
    static final int SEPARATE_NUMBER_3 = 3;
    static final int SEPARATE_NUMBER_4 = 4;
    static final int SEPARATE_NUMBER_6 = 6;
    static final String TAG = "GridLine";
    private int height;
    protected int mGridMode;
    private Paint mPaint;
    private int width;

    public GridLine(Context context) {
        this(context, null);
    }

    public GridLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mPaint.setColor(-16777216);
    }

    protected void setVisibleGridLine() {
        if (this.mGridMode == 0) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    protected void refresh() {
        this.mGridMode = Settings.getGridLine();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETGRIDLINE).append(this.mGridMode);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        setVisibleGridLine();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas c) {
        if (this.mGridMode != 0) {
            View parentView = (View) getParent();
            this.width = parentView.getWidth();
            this.height = parentView.getHeight();
            switch (this.mGridMode) {
                case 1:
                    separateline(3, 3, c);
                    return;
                case 2:
                    separateline(6, 4, c);
                    return;
                case 3:
                    xline(c);
                    separateline(4, 4, c);
                    return;
                default:
                    return;
            }
        }
    }

    private void xline(Canvas c) {
        c.drawLine(0.0f, 0.0f, this.width, this.height, this.mPaint);
        c.drawLine(0.0f, this.height, this.width, 0.0f, this.mPaint);
    }

    private void separateline(int separateWidthNum, int separateHeightNum, Canvas c) {
        float widthInterval = this.width / separateWidthNum;
        float heightInterval = this.height / separateHeightNum;
        for (int i = 1; i < separateWidthNum; i++) {
            c.drawLine(widthInterval * i, 0.0f, widthInterval * i, this.height, this.mPaint);
        }
        for (int i2 = 1; i2 < separateHeightNum; i2++) {
            c.drawLine(0.0f, heightInterval * i2, this.width, heightInterval * i2, this.mPaint);
        }
    }
}
