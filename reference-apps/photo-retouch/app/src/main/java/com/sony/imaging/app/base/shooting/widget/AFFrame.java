package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class AFFrame extends RelativeLayout {
    public static final int CLEAR = 5;
    public static final int GRAY = 1;
    public static final int GREEN = 3;
    private static final String LOG_CANNOT_FIND_RESOURCES = "Cannot find resources.";
    protected static final int MBL_ID = 3;
    protected static final int MBR_ID = 4;
    protected static final int MTL_ID = 1;
    protected static final int MTR_ID = 2;
    public static final int ORANGE = 2;
    private static final String TAG = "AFFrame";
    private int mFrameHeight;
    private int mFrameWidth;
    private Drawable mGraybl;
    private Drawable mGraybr;
    private Drawable mGraytl;
    private Drawable mGraytr;
    private Drawable mGreenbl;
    private Drawable mGreenbr;
    private Drawable mGreentl;
    private Drawable mGreentr;
    private Drawable mOrgbl;
    private Drawable mOrgbr;
    private Drawable mOrgtl;
    private Drawable mOrgtr;
    protected TypedArray mTypedArray;
    protected ImageView mbl;
    protected ImageView mbr;
    protected ImageView mtl;
    protected ImageView mtr;

    public AFFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewGroup.LayoutParams thisLayout = new RelativeLayout.LayoutParams(-2, -2);
        setLayoutParams(thisLayout);
        this.mtl = new ImageView(context, attrs);
        this.mtr = new ImageView(context, attrs);
        this.mbl = new ImageView(context, attrs);
        this.mbr = new ImageView(context, attrs);
        this.mtl.setId(1);
        this.mtr.setId(2);
        this.mbl.setId(3);
        this.mbr.setId(4);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AFFrame);
        initDrawables();
        try {
            this.mFrameHeight = this.mTypedArray.getDimensionPixelSize(21, -1);
            this.mFrameWidth = this.mTypedArray.getDimensionPixelSize(20, -1);
            if (this.mFrameHeight == -1 || this.mFrameWidth == -1) {
                throw new Resources.NotFoundException();
            }
        } catch (Resources.NotFoundException e1) {
            Log.e(TAG, "stack trace", e1);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(9);
        params.addRule(10);
        addView(this.mtl, params);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-2, -2);
        params2.addRule(11);
        params2.addRule(10);
        addView(this.mtr, params2);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(-2, -2);
        params3.addRule(12);
        params3.addRule(9);
        addView(this.mbl, params3);
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(-2, -2);
        params4.addRule(12);
        params4.addRule(11);
        addView(this.mbr, params4);
    }

    public void setWidth(int width) {
        this.mFrameWidth = width;
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) getLayoutParams();
        p.width = width;
        setLayoutParams(p);
    }

    public void setHeight(int height) {
        this.mFrameHeight = height;
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) getLayoutParams();
        p.height = height;
        setLayoutParams(p);
    }

    public void setFocusRect(Rect r) {
        setWidth(r.right - r.left);
        setHeight(r.bottom - r.top);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
        lp.leftMargin = r.left;
        lp.topMargin = r.top;
        setLayoutParams(lp);
    }

    private void initDrawables() {
        this.mGraytl = this.mTypedArray.getDrawable(0);
        this.mGraytr = this.mTypedArray.getDrawable(1);
        this.mGraybl = this.mTypedArray.getDrawable(2);
        this.mGraybr = this.mTypedArray.getDrawable(3);
        this.mGreentl = this.mTypedArray.getDrawable(8);
        this.mGreentr = this.mTypedArray.getDrawable(9);
        this.mGreenbl = this.mTypedArray.getDrawable(10);
        this.mGreenbr = this.mTypedArray.getDrawable(11);
        this.mOrgtl = this.mTypedArray.getDrawable(4);
        this.mOrgtr = this.mTypedArray.getDrawable(5);
        this.mOrgbl = this.mTypedArray.getDrawable(6);
        this.mOrgbr = this.mTypedArray.getDrawable(7);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void changeColor(int color) {
        try {
            setVisibility(0);
            switch (color) {
                case 1:
                    this.mtl.setImageDrawable(this.mGraytl);
                    this.mtr.setImageDrawable(this.mGraytr);
                    this.mbl.setImageDrawable(this.mGraybl);
                    this.mbr.setImageDrawable(this.mGraybr);
                    break;
                case 2:
                    this.mtl.setImageDrawable(this.mOrgtl);
                    this.mtr.setImageDrawable(this.mOrgtr);
                    this.mbl.setImageDrawable(this.mOrgbl);
                    this.mbr.setImageDrawable(this.mOrgbr);
                    break;
                case 3:
                    this.mtl.setImageDrawable(this.mGreentl);
                    this.mtr.setImageDrawable(this.mGreentr);
                    this.mbl.setImageDrawable(this.mGreenbl);
                    this.mbr.setImageDrawable(this.mGreenbr);
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

    public void setOnFocus() {
        changeColor(3);
    }

    public void setSelected() {
        changeColor(2);
    }

    public void setUnFocus() {
        changeColor(1);
    }

    public void setInvisible() {
        changeColor(5);
    }
}
