package com.sony.imaging.app.lightshaft.shooting.widget;

import android.graphics.Canvas;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;

/* loaded from: classes.dex */
public class FlareEffectView extends EffectView {
    @Override // com.sony.imaging.app.lightshaft.shooting.widget.EffectView
    public void drawBasicEffectView(Canvas canvas) {
        super.drawBasicEffectView(canvas);
        this.mStartAngle = 0.0f;
        this.mSweepAngle = 360.0f;
        this.mRectF = getArcRectF(this.mOffset, this.mCenterX, this.mCenterY, this.mSize);
        String value = EffectSelectOptionController.getInstance().getValue(null);
        if (EffectSelectOptionController.STRENGTH.equals(value) && !EffectSelectController.getInstance().isShootingEnable()) {
            changeStrength();
        }
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mBlackStroke4);
        this.mCanvas.drawArc(this.mRectF, this.mStartAngle, this.mSweepAngle, true, this.mWhite);
        drawSingleDottedLine(this.mStartAngle, this.mSweepAngle, 5);
        if (EffectSelectController.getInstance().isShootingEnable()) {
            displayLightSourcePoint(this.mOffset, this.mCenterX, this.mCenterY, R.drawable.p_16_dd_parts_light_source_point_ss);
        }
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
    public void changeAngle() {
        displayLightSourcePoint(this.mOffset, this.mCenterX, this.mCenterY, R.drawable.p_16_dd_parts_light_source_point);
    }
}
