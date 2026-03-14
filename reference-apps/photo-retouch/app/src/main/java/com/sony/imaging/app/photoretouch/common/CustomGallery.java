package com.sony.imaging.app.photoretouch.common;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class CustomGallery extends Gallery {
    private int mCoveflowCenter;
    private int mMaxRotationAngle;
    private int mMaxZoom;

    public CustomGallery(Context context) {
        super(context);
        this.mMaxRotationAngle = 40;
        this.mMaxZoom = -80;
    }

    public CustomGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxRotationAngle = 40;
        this.mMaxZoom = -80;
        init();
    }

    public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMaxRotationAngle = 40;
        this.mMaxZoom = -80;
        init();
    }

    protected void init() {
        setAnimationDuration(50);
    }

    public int getMaxRotationAngle() {
        return this.mMaxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
        this.mMaxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
        return this.mMaxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.mMaxZoom = maxZoom;
    }

    private int getCenterOfCoverflow() {
        return (((getWidth() - getPaddingLeft()) - getPaddingRight()) / 2) + getPaddingLeft();
    }

    private int getCenterOfView(View view) {
        return view.getLeft() + (view.getWidth() / 2);
    }

    @Override // android.widget.Gallery, android.view.ViewGroup
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        int childCenter = getCenterOfView(child);
        int childWidth = child.getWidth();
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        if (childCenter == this.mCoveflowCenter) {
            transformImageBitmap((ImageView) child, t, 0);
            return true;
        }
        int rotationAngle = (int) (((this.mCoveflowCenter - childCenter) / childWidth) * this.mMaxRotationAngle);
        if (Math.abs(rotationAngle) > this.mMaxRotationAngle) {
            rotationAngle = rotationAngle < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
        }
        transformImageBitmap((ImageView) child, t, rotationAngle);
        return true;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        Matrix imageMatrix = t.getMatrix();
        int imageHeight = child.getLayoutParams().height;
        int imageWidth = child.getLayoutParams().width;
        float zoom = 1.0f - Math.abs(rotationAngle / 100.0f);
        imageMatrix.postScale(zoom, zoom, 0.5f, 0.5f);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate(imageWidth / 2, imageHeight / 2);
    }

    @Override // android.widget.Gallery, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
