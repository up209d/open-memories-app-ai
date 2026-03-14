package com.sony.imaging.app.photoretouch.playback.layout.framing;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class CoverFlow extends Gallery {
    private Camera mCamera;
    private int mCoveflowCenter;
    private int mMaxRotationAngle;
    private int mMaxZoom;

    public CoverFlow(Context context) {
        super(context);
        this.mCamera = new Camera();
        this.mMaxRotationAngle = 60;
        this.mMaxZoom = -80;
        setStaticTransformationsEnabled(true);
    }

    public CoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCamera = new Camera();
        this.mMaxRotationAngle = 60;
        this.mMaxZoom = -80;
        setStaticTransformationsEnabled(true);
    }

    public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCamera = new Camera();
        this.mMaxRotationAngle = 60;
        this.mMaxZoom = -80;
        setStaticTransformationsEnabled(true);
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

    private static int getCenterOfView(View view) {
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
        this.mCamera.save();
        Matrix imageMatrix = t.getMatrix();
        int imageHeight = child.getLayoutParams().height;
        int imageWidth = child.getLayoutParams().width;
        int rotation = Math.abs(rotationAngle);
        this.mCamera.translate(0.0f, 0.0f, 100.0f);
        if (rotation < this.mMaxRotationAngle) {
            float zoomAmount = (float) (this.mMaxZoom + (rotation * 12.0d));
            this.mCamera.translate(0.0f, 0.0f, zoomAmount);
        }
        this.mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate(imageWidth / 2, imageHeight / 2);
        this.mCamera.restore();
    }
}
