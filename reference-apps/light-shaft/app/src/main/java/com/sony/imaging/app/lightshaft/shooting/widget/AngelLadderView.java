package com.sony.imaging.app.lightshaft.shooting.widget;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;

/* loaded from: classes.dex */
public class AngelLadderView extends EffectView {
    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void drawBasicEffectView(Canvas canvas) {
        super.drawBasicEffectView(canvas);
        this.mStartAngle = this.mDirection - (this.mRange / 2.0f);
        this.mSweepAngle = this.mRange;
        this.mRectF = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mSize);
        String value = EffectSelectOptionController.getInstance().getValue(null);
        if (EffectSelectOptionController.STRENGTH.equals(value) && !EffectSelectController.getInstance().isShootingEnable()) {
            changeStrength();
        }
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mBlackStroke4);
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mWhite);
        drawDottedLines(this.mStartAngle, this.mSweepAngle);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeRange() {
        RectF rangeRectF = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mSize);
        this.mCanvas.drawArc(rangeRectF, this.mStartAngle, 0.0f, true, this.mOrange);
        this.mCanvas.drawArc(rangeRectF, this.mStartAngle + this.mSweepAngle, 0.0f, true, this.mOrange);
        int sizeRange = this.mParams.getOSDLength(0);
        RectF rangeRectF2 = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, sizeRange);
        this.mCanvas.drawArc(rangeRectF2, this.mStartAngle, this.mSweepAngle, true, this.mLightOrangeFill);
        this.mCanvas.drawArc(rangeRectF2, this.mStartAngle, this.mSweepAngle, true, this.mBlackStroke6);
        this.mCanvas.drawArc(rangeRectF2, this.mStartAngle, this.mSweepAngle, true, this.mOrange);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeLength() {
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mBlackStroke6);
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mOrange);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void changeStrength() {
        this.mStrangthColor.setARGB(getArgbStrength(sARGBalpha, this.mParams.getStrength()), 211, 211, 211);
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mStrangthColor);
    }

    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    protected void changeAngle() {
        this.mCanvas.drawArc(this.mRectF, this.mDirection, 0.0f, true, this.mBlackStroke6);
        this.mCanvas.drawArc(this.mRectF, this.mDirection, 0.0f, true, this.mOrange);
        displayLightSourcePoint(this.mOffset, this.mCenterX, this.mCenterY, R.drawable.p_16_dd_parts_light_source_point);
    }
}
