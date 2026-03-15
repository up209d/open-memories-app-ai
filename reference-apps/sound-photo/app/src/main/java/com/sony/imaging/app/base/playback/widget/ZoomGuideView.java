package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class ZoomGuideView extends RelativeLayout {
    private static final boolean DEBUG = false;
    private static final int RECT_OFFSET = 1;
    private static final String TAG = "ZoomGuideView";
    private static final Rect VIRTUAL_RECT = new Rect(0, 0, 120, 120);
    private final int FIX;
    private final int FOUR_LINE;
    private final int LINEWIDTH;
    private int OrigThumH;
    private int OrigThumW;
    private final int TWO_LINE;
    private int X;
    private int Y;
    private Paint guide;
    private float guideScale;
    private Paint inside;
    private Context mContext;
    private Rect mGuideRect;
    private Bitmap mThumBm;
    private Paint outside;

    public ZoomGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.LINEWIDTH = 2;
        this.TWO_LINE = 4;
        this.FOUR_LINE = 8;
        this.FIX = 2;
        this.mGuideRect = new Rect();
        this.guideScale = 1.0f;
        logcat("ZoomGuideView start");
        this.mContext = context;
        this.outside = new Paint();
        this.outside.setColor(context.getResources().getColor(R.color.RESID_LINESTYLE_PLAYZOOM_COLOR_OUTLINE));
        this.outside.setStyle(Paint.Style.FILL);
        this.outside.setStrokeWidth(2.0f);
        this.inside = new Paint();
        this.inside.setColor(context.getResources().getColor(R.color.RESID_LINESTYLE_PLAYZOOM_COLOR_INLINE));
        this.inside.setStyle(Paint.Style.FILL);
        this.inside.setStrokeWidth(2.0f);
        this.guide = new Paint();
        this.guide.setColor(context.getResources().getColor(R.color.RESID_LINESTYLE_PLAYZOOM_COLOR_MOVELINE));
        this.guide.setStyle(Paint.Style.STROKE);
        this.guide.setStrokeWidth(2.0f);
        logcat("ZoomGuideView end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ThumnailView extends ImageView {
        public ThumnailView(Context context) {
            super(context);
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            if (!ZoomGuideView.this.mGuideRect.isEmpty()) {
                canvas.drawRect(ZoomGuideView.this.mGuideRect, ZoomGuideView.this.guide);
            }
            canvas.restore();
        }
    }

    private void makeThumnailView() {
        logcat("makeRectLine start");
        int thumW = this.mThumBm.getWidth();
        int thumH = this.mThumBm.getHeight();
        this.Y = VIRTUAL_RECT.height() - (thumH + 8);
        this.X = VIRTUAL_RECT.width() - (thumW + 8);
        Bitmap bg = Bitmap.createBitmap(thumW + 8, thumH + 8, Bitmap.Config.ARGB_8888);
        ThumnailView thumnailView = new ThumnailView(this.mContext);
        thumnailView.setImageBitmap(drawLine(bg));
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(-2, -2);
        layout.rightMargin = this.X;
        layout.topMargin = this.Y;
        addView(thumnailView, layout);
        logcat("makeRectLine end");
    }

    private Bitmap drawLine(Bitmap bg) {
        Canvas canvas = new Canvas(bg);
        canvas.drawRect(new Rect(0, 0, bg.getWidth(), bg.getHeight()), this.outside);
        canvas.drawRect(new Rect(2, 2, bg.getWidth() - 2, bg.getHeight() - 2), this.inside);
        canvas.drawBitmap(this.mThumBm, 4.0f, 4.0f, (Paint) null);
        return bg;
    }

    private void fitVirtualRect() {
        logcat("fitVirtualRect start");
        if (VIRTUAL_RECT.width() / this.OrigThumW < VIRTUAL_RECT.height() / this.OrigThumH) {
            logcat("fit to width");
            int FitLen = VIRTUAL_RECT.width() - 8;
            this.mThumBm = Bitmap.createScaledBitmap(this.mThumBm, FitLen, (int) (FitLen * (this.OrigThumH / this.OrigThumW)), false);
        } else {
            logcat("fit to heigth");
            int FitLen2 = VIRTUAL_RECT.height() - 8;
            this.mThumBm = Bitmap.createScaledBitmap(this.mThumBm, (int) (FitLen2 * (this.OrigThumW / this.OrigThumH)), FitLen2, false);
        }
        this.guideScale = this.mThumBm.getWidth() / this.OrigThumW;
        logcat("fitVirtualRect end");
    }

    public void setOptThumnail(Bitmap optThum) {
        logcat("setOptThumnail start");
        removeAllViews();
        if (optThum != null) {
            this.mThumBm = optThum;
        }
        this.OrigThumW = this.mThumBm.getWidth();
        this.OrigThumH = this.mThumBm.getHeight();
        fitVirtualRect();
        makeThumnailView();
        logcat("setOptThumnail end");
    }

    public void drawGuideRect(Rect rect) {
        logcat("drawGuideRect start");
        int mLeft = (int) ((rect.left * this.guideScale) + 6.0f);
        int mTop = (int) ((rect.top * this.guideScale) + 6.0f);
        int mRight = (int) ((rect.right * this.guideScale) + 2.0f);
        int mBottom = (int) ((rect.bottom * this.guideScale) + 2.0f);
        if (mLeft >= mRight) {
            mRight = mLeft + 1;
        }
        if (mTop >= mBottom) {
            mBottom = mTop + 1;
        }
        this.mGuideRect.set(mLeft, mTop, mRight, mBottom);
        invalidate();
        logcat("mGuideRect =" + this.mGuideRect.toString());
        logcat("drawGuideRect end");
    }

    private void logcat(String str) {
    }
}
