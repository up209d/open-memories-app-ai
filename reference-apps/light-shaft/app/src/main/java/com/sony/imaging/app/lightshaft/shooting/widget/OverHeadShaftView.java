package com.sony.imaging.app.lightshaft.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class OverHeadShaftView extends View {
    private static final String TAG = "OverHeadShaftView";
    protected int[] mARGBalpha;
    private Bitmap mLightSourceIcon;
    protected NotificationListener mListener;
    protected ShaftsEffect.Parameters mParams;
    private Resources mResources;

    public void setParameters(ShaftsEffect.Parameters p) {
        this.mParams = p;
        refresh();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        AppLog.info("OverHeadShaftViewOUTSIDE", "onDraw OverHeadView");
        if (this.mParams.getEffect() == 1) {
            drawOverHeadRectangles(canvas);
        }
        AppLog.info("INSIDE", "onDetachedFromWindow OverHeadView");
        super.onDraw(canvas);
    }

    protected void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setParameters(ShaftsEffect.getInstance().getParameters());
        ShaftsEffect.getInstance().setNotificationListener(this.mListener);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ShaftsEffect.getInstance().removeNotificationListener(this.mListener);
        if (this.mLightSourceIcon != null) {
            this.mLightSourceIcon.recycle();
            this.mLightSourceIcon = null;
        }
    }

    public OverHeadShaftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mARGBalpha = new int[]{48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208};
        this.mResources = context.getResources();
        this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.lightshaft.shooting.widget.OverHeadShaftView.1
            private final String[] TAG = {ShaftsEffect.TAG_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAG;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                AppLog.info("INSIDE", "onNotify OverHeadView");
                OverHeadShaftView.this.invalidate();
                AppLog.info("OUTSIDE", "onNotify OverHeadView");
            }
        };
        this.mParams = null;
    }

    private void drawOverHeadRectangles(Canvas canvas) {
        Rect innerRect;
        Rect outerRect;
        canvas.save();
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) disp.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int eeScreenWidth = videoRect.pxRight - videoRect.pxLeft;
        int eeScreenHeight = videoRect.pxBottom - videoRect.pxTop;
        Paint innerLinePaint = new Paint();
        innerLinePaint.setARGB(255, 128, 128, 128);
        innerLinePaint.setStyle(Paint.Style.STROKE);
        innerLinePaint.setStrokeWidth(1.0f);
        Paint outerLinePaint = new Paint();
        outerLinePaint.setARGB(255, 211, 211, 211);
        outerLinePaint.setStyle(Paint.Style.STROKE);
        outerLinePaint.setStrokeWidth(1.0f);
        Paint fillAreaPaint = new Paint();
        fillAreaPaint.setARGB(153, 0, 0, 0);
        String currentAspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        ScalarProperties.getInt("device.panel.aspect");
        if (169 == ScalarProperties.getInt("device.panel.aspect")) {
            Log.e("", "Overheadview 16:9 panel");
            if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                innerRect = new Rect(24, 304, 120, 390);
                outerRect = new Rect(12, 294, 132, 400);
            } else if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                innerRect = new Rect(22, 306, 92, 402);
                outerRect = new Rect(12, 294, 102, 414);
            } else if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_4_3)) {
                innerRect = new Rect(24, 306, 120, 402);
                outerRect = new Rect(12, 294, 132, 414);
            } else {
                innerRect = new Rect(24, 303, 120, 375);
                outerRect = new Rect(12, 294, 132, 384);
            }
        } else {
            Log.e("", "Overheadview 4:3 panel");
            if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                innerRect = new Rect(28, 304, 156, 390);
                outerRect = new Rect(12, 294, 172, 400);
            } else if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                innerRect = new Rect(24, 306, 120, 402);
                outerRect = new Rect(12, 294, 132, 414);
            } else if (currentAspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_4_3)) {
                innerRect = new Rect(28, 306, 156, 402);
                outerRect = new Rect(12, 294, 172, 414);
            } else {
                innerRect = new Rect(28, 303, 156, 375);
                outerRect = new Rect(12, 294, 172, 384);
            }
        }
        AppLog.info("Overhead", "RECTANGLE " + innerRect.left + ExposureModeController.SOFT_SNAP + innerRect.top + ExposureModeController.SOFT_SNAP + innerRect.right + ExposureModeController.SOFT_SNAP + innerRect.bottom);
        AppLog.info("Overhead", "RECTANGLE " + (innerRect.right - innerRect.left) + (innerRect.bottom - innerRect.top));
        canvas.drawRect(outerRect, fillAreaPaint);
        canvas.drawRect(innerRect, innerLinePaint);
        canvas.drawRect(outerRect, outerLinePaint);
        AppLog.info("LARGE", "LARGE " + eeScreenWidth + ExposureModeController.SOFT_SNAP + eeScreenHeight);
        AppLog.info("SMALL", "SMALL " + (innerRect.right - innerRect.left) + ExposureModeController.SOFT_SNAP + (innerRect.bottom - innerRect.top));
        float squeezeRatioX = (innerRect.right - innerRect.left) / eeScreenWidth;
        float squeezeRatioY = (innerRect.bottom - innerRect.top) / eeScreenHeight;
        AppLog.info("Squeeze Ratio", "squeeze Ratio" + squeezeRatioX + ExposureModeController.SOFT_SNAP + squeezeRatioY);
        drawOverheadShaftView(canvas, squeezeRatioX, squeezeRatioY, outerRect, innerRect, videoRect);
        canvas.restore();
    }

    public void drawOverheadShaftView(Canvas canvas, float squeezeRatioX, float squeezeRatioY, Rect clip, Rect innerRectangle, DisplayManager.VideoRect eeRect) {
        RectF r;
        AppLog.info("Origin Points", "x = " + clip.left + " y = " + clip.top);
        Paint pBase = new Paint();
        Paint pLine = new Paint();
        pLine.setStyle(Paint.Style.STROKE);
        pLine.setStrokeWidth(1.0f);
        pLine.setAntiAlias(true);
        Paint orangeLine = new Paint();
        orangeLine.setARGB(255, 247, 147, 30);
        orangeLine.setStyle(Paint.Style.STROKE);
        orangeLine.setStrokeWidth(2.0f);
        orangeLine.setAntiAlias(true);
        PointF point = this.mParams.getOSDPoint();
        int centerX = (int) point.x;
        int centerY = (int) point.y;
        int length = this.mParams.getOSDLength();
        AppLog.info("OSD Points", "x = " + centerX + " y = " + centerY + " length = " + length + " offset = 200");
        int newCenterX = innerRectangle.left + ((int) (centerX * squeezeRatioX));
        int newCenterY = innerRectangle.top + ((int) (centerY * squeezeRatioY));
        int newLength = (int) (length * squeezeRatioX);
        AppLog.info("Overhead Points", "x = " + newCenterX + " y = " + newCenterY + " length = " + newLength + " offset = 0");
        int newCenterX2 = newCenterX - ((int) (eeRect.pxLeft * squeezeRatioX));
        int newCenterY2 = newCenterY - ((int) (eeRect.pxTop * squeezeRatioX));
        AppLog.info("Overhead Points corrected", "x = " + newCenterX2 + " y = " + newCenterY2 + " length = " + newLength + " offset = 0");
        int mAspect = ScalarProperties.getInt("device.panel.aspect");
        if (1 == DisplayModeObserver.getInstance().getActiveDevice() || mAspect != 169) {
            r = new RectF((0 + newCenterX2) - newLength, (0 + newCenterY2) - newLength, 0 + newCenterX2 + newLength, 0 + newCenterY2 + newLength);
        } else {
            r = new RectF((0 + newCenterX2) - (newLength * 0.75f), (0 + newCenterY2) - newLength, 0 + newCenterX2 + (newLength * 0.75f), 0 + newCenterY2 + newLength);
        }
        float direction = this.mParams.getDirectionDegree();
        float range = this.mParams.getRangeDegree();
        float startAngle = direction - (range / 2.0f);
        canvas.clipRect(clip);
        boolean flag = EffectSelectController.getInstance().isSetting1Displaying();
        if (flag) {
            AppLog.info(TAG, "Setting screen = 1");
            pLine.setARGB(255, 211, 211, 211);
            canvas.drawArc(r, startAngle, range, true, pLine);
            float olAngleStartAngle = startAngle + (range / 2.0f);
            canvas.drawArc(r, olAngleStartAngle, 0, true, orangeLine);
            if (this.mLightSourceIcon == null) {
                this.mLightSourceIcon = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_light_source_point_overhead);
            }
            canvas.drawBitmap(this.mLightSourceIcon, newCenterX2 - (this.mLightSourceIcon.getWidth() / 2), newCenterY2 - (this.mLightSourceIcon.getHeight() / 2), (Paint) null);
            return;
        }
        AppLog.info(TAG, "Setting screen = 2");
        if (this.mLightSourceIcon != null) {
            this.mLightSourceIcon.recycle();
            this.mLightSourceIcon = null;
        }
        String setting2Item = EffectSelectOptionController.getInstance().getValue(null);
        if (setting2Item.equalsIgnoreCase(EffectSelectOptionController.RANGE)) {
            AppLog.info(TAG, "Setting Item = Range");
            pLine.setARGB(255, 211, 211, 211);
            canvas.drawArc(r, startAngle, range, true, pLine);
            int rangeAreaLength = (int) (this.mParams.getOSDLength(0) * squeezeRatioX);
            AppLog.info(TAG, "rangeAreaLength = " + rangeAreaLength);
            AppLog.info(TAG, "rangeAreaLength = " + this.mParams.getOSDLength(1));
            RectF rangeRect = getRangeAreaRect(0, newCenterX2, newCenterY2, rangeAreaLength);
            Paint rangeAreaPaint = new Paint();
            rangeAreaPaint.setARGB(255, 247, 147, 30);
            canvas.drawArc(rangeRect, startAngle, range, true, rangeAreaPaint);
            return;
        }
        if (setting2Item.equalsIgnoreCase(EffectSelectOptionController.STRENGTH)) {
            AppLog.info(TAG, "Setting Item = Strength");
            pLine.setARGB(255, 211, 211, 211);
            pBase.setARGB(this.mARGBalpha[this.mParams.getStrength()], 211, 211, 211);
            canvas.drawArc(r, startAngle, range, true, pBase);
            canvas.drawArc(r, startAngle, range, true, pLine);
            return;
        }
        if (setting2Item.equalsIgnoreCase(EffectSelectOptionController.LENGTH)) {
            AppLog.info(TAG, "Setting Item = Length");
            pLine.setARGB(255, 247, 147, 30);
            canvas.drawArc(r, startAngle, range, true, pLine);
            return;
        }
        AppLog.info(TAG, "Setting Item = No Item");
    }

    private RectF getRangeAreaRect(int newOffset, int newCenterX, int newCenterY, int newLength) {
        int mAspect = ScalarProperties.getInt("device.panel.aspect");
        if (1 == DisplayModeObserver.getInstance().getActiveDevice() || mAspect != 169) {
            RectF r = new RectF((newOffset + newCenterX) - newLength, (newOffset + newCenterY) - newLength, newOffset + newCenterX + newLength, newOffset + newCenterY + newLength);
            return r;
        }
        RectF r2 = new RectF((newOffset + newCenterX) - (newLength * 0.75f), (newOffset + newCenterY) - newLength, newOffset + newCenterX + (newLength * 0.75f), newOffset + newCenterY + newLength);
        return r2;
    }
}
