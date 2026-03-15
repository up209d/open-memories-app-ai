package com.sony.imaging.app.lightshaft.shooting.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;

/* loaded from: classes.dex */
public class StarShaftEffectView extends EffectView {
    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void drawBasicEffectView(Canvas canvas) {
        super.drawBasicEffectView(canvas);
        this.mRectF = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mSize);
        this.mStartAngle = this.mDirection - (this.mRange / 2.0f);
        int n = this.mParams.getNumberOfShafts();
        float changeAngle = 360.0f / n;
        String value = EffectSelectOptionController.getInstance().getValue(null);
        if (EffectSelectOptionController.STRENGTH.equals(value) && !EffectSelectController.getInstance().isShootingEnable()) {
            changeStrength();
        }
        for (int i = 1; i <= n; i++) {
            if (i % 2 != 0) {
                this.mCanvas.drawArc(this.mRectF, this.mStartAngle, changeAngle, true, this.mDottedBlack);
                this.mCanvas.drawArc(this.mRectF, this.mStartAngle, changeAngle, true, this.mDottedWhite);
            }
            this.mStartAngle += changeAngle;
        }
        this.mCanvas.drawArc(this.mRectF, 0.0f, 360.0f, true, this.mBlackStroke4);
        this.mCanvas.drawArc(this.mRectF, 0.0f, 360.0f, true, this.mWhite);
        drawSingleDottedLine(0.0f, 360.0f, 5);
        if (EffectSelectController.getInstance().isShootingEnable()) {
            displayLightSourcePoint(this.mOffset, this.mCenterX, this.mCenterY, R.drawable.p_16_dd_parts_light_source_point_ss);
        }
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeLength() {
        this.mCanvas.drawArc(this.mRectF, 0.0f, 360.0f, true, this.mBlackStroke6);
        this.mCanvas.drawArc(this.mRectF, 0.0f, 360.0f, true, this.mOrange);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeStrength() {
        this.mStrangthColor.setARGB(getArgbStrength(sARGBalpha, this.mParams.getStrength()), 211, 211, 211);
        this.mCanvas.drawArc(this.mRectF, 0.0f, 360.0f, true, this.mStrangthColor);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeNumberOfShaft() {
        this.mStartAngle = this.mDirection - (this.mRange / 2.0f);
        this.mRectF = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mSize);
        int n = this.mParams.getNumberOfShafts();
        for (int i = 1; i <= n; i++) {
            float changeAngle = (360.0f / n) * i;
            this.mCanvas.drawArc(this.mRectF, this.mStartAngle, 0.0f, true, this.mBlackStroke6);
            this.mCanvas.drawArc(this.mRectF, this.mStartAngle, 0.0f, true, this.mOrange);
            this.mStartAngle = this.mDirection + changeAngle;
        }
        RectF circle = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, 6);
        Paint orange = new Paint();
        orange.setARGB(255, 247, 147, 30);
        orange.setStyle(Paint.Style.FILL);
        this.mCanvas.drawOval(circle, orange);
        Paint black = new Paint();
        black.setARGB(255, 0, 0, 0);
        black.setStrokeWidth(1.0f);
        black.setStyle(Paint.Style.STROKE);
        this.mCanvas.drawOval(circle, black);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeAngle() {
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mBlackStroke6);
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mOrange);
        displayLightSourcePoint(this.mOffset, this.mCenterX, this.mCenterY, R.drawable.p_16_dd_parts_light_source_point);
    }
}
