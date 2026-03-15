package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class AFFrameIlluminator extends AFFrame {
    private static final String LOG_CANNOT_FIND_RESOURCES = "Cannot find resources.";
    private static final int MCB_ID = 8;
    private static final int MCL_ID = 5;
    private static final int MCR_ID = 7;
    private static final int MCT_ID = 6;
    private static final String TAG = "AFFrameIlluminator";
    private Drawable mGrayCenterBottom;
    private Drawable mGrayCenterLeft;
    private Drawable mGrayCenterRight;
    private Drawable mGrayCenterTop;
    private Drawable mGreenCenterBottom;
    private Drawable mGreenCenterLeft;
    private Drawable mGreenCenterRight;
    private Drawable mGreenCenterTop;
    private ImageView mcb;
    private ImageView mcl;
    private ImageView mcr;
    private ImageView mct;

    public AFFrameIlluminator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mct = new ImageView(context, attrs);
        this.mcl = new ImageView(context, attrs);
        this.mcr = new ImageView(context, attrs);
        this.mcb = new ImageView(context, attrs);
        this.mct.setId(6);
        this.mcl.setId(5);
        this.mcr.setId(7);
        this.mcb.setId(8);
        initDrawables();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(9);
        params.addRule(15);
        addView(this.mcl, params);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-2, -2);
        params2.addRule(11);
        params2.addRule(15);
        addView(this.mcr, params2);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(-2, -2);
        params3.addRule(10);
        params3.addRule(14);
        addView(this.mct, params3);
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(-2, -2);
        params4.addRule(12);
        params4.addRule(14);
        addView(this.mcb, params4);
    }

    private void initDrawables() {
        this.mGrayCenterTop = this.mTypedArray.getDrawable(12);
        this.mGrayCenterBottom = this.mTypedArray.getDrawable(15);
        this.mGrayCenterRight = this.mTypedArray.getDrawable(14);
        this.mGrayCenterLeft = this.mTypedArray.getDrawable(13);
        this.mGreenCenterTop = this.mTypedArray.getDrawable(16);
        this.mGreenCenterBottom = this.mTypedArray.getDrawable(19);
        this.mGreenCenterRight = this.mTypedArray.getDrawable(18);
        this.mGreenCenterLeft = this.mTypedArray.getDrawable(17);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFrame
    public void changeColor(int color) {
        super.changeColor(color);
        try {
            setVisibility(0);
            switch (color) {
                case 1:
                    this.mct.setImageDrawable(this.mGrayCenterTop);
                    this.mcb.setImageDrawable(this.mGrayCenterBottom);
                    this.mcr.setImageDrawable(this.mGrayCenterRight);
                    this.mcl.setImageDrawable(this.mGrayCenterLeft);
                    break;
                case 3:
                    this.mct.setImageDrawable(this.mGreenCenterTop);
                    this.mcb.setImageDrawable(this.mGreenCenterBottom);
                    this.mcr.setImageDrawable(this.mGreenCenterRight);
                    this.mcl.setImageDrawable(this.mGreenCenterLeft);
                    break;
                case 5:
                    setVisibility(8);
                    break;
            }
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, LOG_CANNOT_FIND_RESOURCES);
        }
        invalidate();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFrame
    @Deprecated
    public void setSelected() {
    }
}
