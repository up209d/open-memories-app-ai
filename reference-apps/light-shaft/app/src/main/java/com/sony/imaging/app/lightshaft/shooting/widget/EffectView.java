package com.sony.imaging.app.lightshaft.shooting.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public abstract class EffectView {
    public static int[] sARGBalpha = {48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208};
    protected ShaftsEffect.Parameters mParams;
    protected String mTag = "EffectView";
    protected int mOffset = 200;
    protected int mCenterX = 0;
    protected int mCenterY = 0;
    protected float mStartAngle = 0.0f;
    protected float mSweepAngle = 0.0f;
    protected float mDirection = 0.0f;
    protected float mRange = 0.0f;
    protected int mSize = 0;
    protected final float maxAngel = 360.0f;
    protected final float minAngel = 0.0f;
    protected final int LENGTHFIVE = 5;
    protected PointF mPoint = null;
    protected Canvas mCanvas = null;
    private Bitmap mLightSource = null;
    protected RectF mRectF = null;
    private Resources resource = null;
    protected DashPathEffect mWhiteDashPath = null;
    protected DashPathEffect mBlackDashPath = null;
    protected Paint mWhite = null;
    protected Paint mDottedWhite = null;
    protected Paint mOrange = null;
    protected Paint mBlackStroke4 = null;
    protected Paint mBlackStroke6 = null;
    protected Paint mDottedBlack = null;
    protected Paint mLightOrangeFill = null;
    protected Paint mStrangthColor = null;

    protected abstract void changeAngle();

    protected abstract void changeLength();

    protected abstract void changeStrength();

    /* JADX INFO: Access modifiers changed from: protected */
    public EffectView() {
        initializePaintObject();
    }

    public void setUpdatedParameter(ShaftsEffect.Parameters param) {
        this.mParams = param;
        displayParaMeterValue();
    }

    private void initializePaintObject() {
        if (this.mWhiteDashPath == null) {
            this.mWhiteDashPath = new DashPathEffect(new float[]{4.0f, 8.0f}, 0.0f);
        }
        if (this.mBlackDashPath == null) {
            this.mBlackDashPath = new DashPathEffect(new float[]{6.0f, 6.0f}, 1.0f);
        }
        if (this.mDottedWhite == null) {
            this.mDottedWhite = new Paint();
            this.mDottedWhite.setARGB(255, 150, 150, 150);
            this.mDottedWhite.setPathEffect(this.mWhiteDashPath);
            setPaintProperty(this.mDottedWhite, Paint.Style.STROKE, 2, false);
        }
        if (this.mDottedBlack == null) {
            this.mDottedBlack = new Paint();
            this.mDottedBlack.setARGB(255, 0, 0, 0);
            this.mDottedBlack.setPathEffect(this.mBlackDashPath);
            setPaintProperty(this.mDottedBlack, Paint.Style.STROKE, 4, false);
        }
        if (this.mWhite == null) {
            this.mWhite = new Paint();
            this.mWhite.setARGB(255, 211, 211, 211);
            setPaintProperty(this.mWhite, Paint.Style.STROKE, 2, true);
        }
        if (this.mBlackStroke4 == null) {
            this.mBlackStroke4 = new Paint();
            this.mBlackStroke4.setARGB(255, 0, 0, 0);
            setPaintProperty(this.mBlackStroke4, Paint.Style.STROKE, 4, true);
        }
        if (this.mBlackStroke6 == null) {
            this.mBlackStroke6 = new Paint(this.mBlackStroke4);
            setPaintProperty(this.mBlackStroke6, Paint.Style.STROKE, 6, true);
        }
        if (this.mOrange == null) {
            this.mOrange = new Paint();
            this.mOrange.setARGB(255, 247, 147, 30);
            setPaintProperty(this.mOrange, Paint.Style.STROKE, 4, true);
        }
        if (this.mLightOrangeFill == null) {
            this.mLightOrangeFill = new Paint();
            this.mLightOrangeFill.setARGB(179, 247, 147, 30);
            setPaintProperty(this.mLightOrangeFill, Paint.Style.FILL, 2, true);
        }
        if (this.mStrangthColor == null) {
            this.mStrangthColor = new Paint();
        }
        this.mStrangthColor.setARGB(64, 0, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getArgbStrength(int[] argbArray, int index) {
        return argbArray[index];
    }

    protected void changeRange() {
    }

    protected void changeNumberOfShaft() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawBasicEffectView(Canvas canvas) {
        this.mCanvas = canvas;
        this.mPoint = this.mParams.getOSDPoint();
        this.mCenterX = (int) this.mPoint.x;
        this.mCenterY = (int) this.mPoint.y;
        this.mSize = this.mParams.getOSDLength();
        this.mDirection = this.mParams.getDirectionDegree();
        this.mRange = this.mParams.getRangeDegree();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setEffectProperty() {
        if (EffectSelectController.getInstance().isSetting1Displaying()) {
            changeAngle();
            return;
        }
        String value = EffectSelectOptionController.getInstance().getValue(null);
        if (EffectSelectOptionController.RANGE.equals(value)) {
            changeRange();
            return;
        }
        if (EffectSelectOptionController.LENGTH.equals(value)) {
            changeLength();
        } else if (EffectSelectOptionController.SHAFT.equals(value)) {
            changeNumberOfShaft();
        } else if (EffectSelectOptionController.STRENGTH.equals(value)) {
            changeStrength();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RectF getArcRectF(int offset, int centerX, int centerY, int size) {
        RectF rectF;
        int mAspect = ScalarProperties.getInt("device.panel.aspect");
        if (1 == DisplayModeObserver.getInstance().getActiveDevice() || mAspect != 169) {
            rectF = new RectF((offset + centerX) - size, (offset + centerY) - size, offset + centerX + size, offset + centerY + size);
        } else {
            rectF = new RectF((offset + centerX) - (size * 0.75f), (offset + centerY) - size, offset + centerX + (size * 0.75f), offset + centerY + size);
        }
        AppLog.info("dotted rect", "" + (rectF.right - rectF.left) + ExposureModeController.SOFT_SNAP + (rectF.bottom - rectF.top));
        return rectF;
    }

    protected void setPaintProperty(Paint paint, Paint.Style style, int width, boolean antiAlias) {
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        paint.setAntiAlias(antiAlias);
    }

    private void displayParaMeterValue() {
        AppLog.checkIf(this.mTag, "ShaftView:  Range   " + this.mParams.getRange());
        AppLog.checkIf(this.mTag, "ShaftView:  Strength  " + this.mParams.getStrength());
        AppLog.checkIf(this.mTag, "ShaftView:  Length  " + this.mParams.getLength());
        AppLog.checkIf(this.mTag, "ShaftView: NumberOfShafts  " + this.mParams.getNumberOfShafts());
    }

    public void drawDottedLines(float startAngel, float sweepAngel) {
        this.mParams.getLength();
        if (this.mParams.getLength() > 8) {
            AppLog.info("DOTTED", "DOT3");
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedWhite);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(5)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(5)), startAngel, sweepAngel, false, this.mDottedWhite);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(8)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(8)), startAngel, sweepAngel, false, this.mDottedWhite);
            return;
        }
        if (this.mParams.getLength() > 5) {
            AppLog.info("DOTTED", "DOT2");
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedWhite);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(5)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(5)), startAngel, sweepAngel, false, this.mDottedWhite);
            return;
        }
        if (this.mParams.getLength() > 2) {
            AppLog.info("DOTTED", "DOT1");
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(2)), startAngel, sweepAngel, false, this.mDottedWhite);
            return;
        }
        AppLog.info("DOTTED", "Length is in else of dotted" + this.mParams.getLength());
    }

    public void drawSingleDottedLine(float startAngel, float sweepAngel, int position) {
        this.mParams.getLength();
        if (this.mParams.getLength() > position) {
            AppLog.info("DOTTED", "DOT1");
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(position)), startAngel, sweepAngel, false, this.mDottedBlack);
            this.mCanvas.drawArc(getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mParams.getOSDLength(position)), startAngel, sweepAngel, false, this.mDottedWhite);
            return;
        }
        AppLog.info("DOTTED", "Length is in else of dotted" + this.mParams.getLength());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDetachedWindow() {
        AppLog.checkIf(this.mTag, "onDetachedWindow");
        this.mOffset = 200;
        this.mCenterX = 0;
        this.mCenterY = 0;
        this.mStartAngle = 0.0f;
        this.mSweepAngle = 0.0f;
        this.mDirection = 0.0f;
        this.mRange = 0.0f;
        this.mSize = 0;
        this.mParams = null;
        this.mCanvas = null;
        this.mRectF = null;
        this.mPoint = null;
        this.mWhite = null;
        this.mDottedWhite = null;
        this.mOrange = null;
        this.mBlackStroke4 = null;
        this.mBlackStroke6 = null;
        this.mDottedBlack = null;
        this.mLightOrangeFill = null;
        this.mStrangthColor = null;
        this.resource = null;
        this.mWhiteDashPath = null;
        this.mBlackDashPath = null;
        if (this.mLightSource != null) {
            this.mLightSource.recycle();
            this.mLightSource = null;
        }
        System.gc();
    }

    public Resources getResources() {
        return this.resource;
    }

    public void setResources(Resources resource) {
        this.resource = resource;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void displayLightSourcePoint(int offset, int centerxx, int centeryy, int resID) {
        if (this.mLightSource == null) {
            this.mLightSource = BitmapFactory.decodeResource(this.resource, resID);
        }
        int iconWidth = this.mLightSource.getWidth() / 2;
        int iconHight = this.mLightSource.getHeight() / 2;
        this.mCanvas.drawBitmap(this.mLightSource, (offset + centerxx) - iconWidth, (this.mOffset + centeryy) - iconHight, this.mWhite);
    }
}
