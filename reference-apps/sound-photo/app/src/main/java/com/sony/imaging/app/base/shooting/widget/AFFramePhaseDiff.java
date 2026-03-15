package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class AFFramePhaseDiff extends ImageView {
    public static final int BLACK = 6;
    public static final int CLEAR = 5;
    public static final int GRAY = 1;
    public static final int GREEN = 3;
    public static final int ORANGE = 2;
    public static final int WHITE = 4;
    private Drawable mBlack;
    private Rect mFocusAreaYUV;
    private Drawable mGray;
    private Drawable mGreen;
    private Drawable mOrg;
    private TypedArray mTypedArray;
    private Drawable mWhite;

    public AFFramePhaseDiff(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AFFramePhaseDiff);
        initDrawables();
        setImageDrawable(this.mGray);
        setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void initDrawables() {
        try {
            this.mGray = this.mTypedArray.getDrawable(0);
            this.mGreen = this.mTypedArray.getDrawable(7);
            this.mOrg = this.mTypedArray.getDrawable(14);
            this.mWhite = this.mTypedArray.getDrawable(11);
            this.mBlack = this.mTypedArray.getDrawable(3);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void changeColor(int color) {
        setVisibility(0);
        switch (color) {
            case 1:
                setImageDrawable(this.mGray);
                break;
            case 2:
                setImageDrawable(this.mOrg);
                break;
            case 3:
                setImageDrawable(this.mGreen);
                break;
            case 4:
                setImageDrawable(this.mWhite);
                break;
            case 5:
                setVisibility(4);
                break;
            case 6:
                setImageDrawable(this.mBlack);
                break;
        }
        invalidate();
    }

    public boolean equals(Rect r) {
        return r.left == this.mFocusAreaYUV.left && r.top == this.mFocusAreaYUV.top && r.right == this.mFocusAreaYUV.right && r.bottom == this.mFocusAreaYUV.bottom;
    }

    public void setFocusRect(Rect r) {
        this.mFocusAreaYUV = r;
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) getLayoutParams();
        p.height = r.bottom - r.top;
        p.width = r.right - r.left;
        p.leftMargin = r.left;
        p.topMargin = r.top;
        setLayoutParams(p);
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

    public void setUnSelected() {
        changeColor(4);
    }

    public void setInvisible() {
        changeColor(5);
    }

    public void setOnFocusReady() {
        changeColor(6);
    }
}
