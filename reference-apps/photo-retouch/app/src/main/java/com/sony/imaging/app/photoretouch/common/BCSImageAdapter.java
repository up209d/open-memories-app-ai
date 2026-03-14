package com.sony.imaging.app.photoretouch.common;

import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.playback.control.LCEControl;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class BCSImageAdapter {
    private boolean isFirst;
    protected int mCurrentPosition;
    protected LCEControl mLceControl;
    protected int mMax;
    private ChangeParameter mSetter;
    private boolean mStable;
    protected OptimizedImageView mView;
    private final String TAG = BCSImageAdapter.class.getSimpleName();
    protected OptimizedImage mCurrentImage = null;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.photoretouch.common.BCSImageAdapter.2
        @Override // java.lang.Runnable
        public void run() {
            if (BCSImageAdapter.this.mCurrentImage != null) {
                BCSImageAdapter.this.mCurrentImage.release();
            }
            OptimizedImage img = BCSImageAdapter.this.mLceControl.createYUVImageUsingDSP(BCSImageAdapter.this.mLceControl.getScaledImage());
            BCSImageAdapter.this.mCurrentImage = img;
            if (ImageEditor.runnableCount < 0) {
                ImageEditor.runnableCount = 0;
            }
            ImageEditor.runnableCount++;
            Log.i(BCSImageAdapter.this.TAG, "run:runnableCount " + ImageEditor.runnableCount);
            BCSImageAdapter.this.mView.setOptimizedImage(img);
        }
    };

    /* loaded from: classes.dex */
    public interface ChangeParameter {
        void setParameter(int i, LCEControl lCEControl);
    }

    public BCSImageAdapter(LCEControl lceControl, int max, OptimizedImageView v, ChangeParameter setter) {
        this.mStable = false;
        this.isFirst = true;
        setLayout(v);
        this.mMax = max;
        this.mView = v;
        ImageEditor.stableCount = 2;
        this.mStable = false;
        this.isFirst = true;
        this.mView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.photoretouch.common.BCSImageAdapter.1
            public void onDisplay(int errCd) {
                ImageEditor.runnableCount--;
                ImageEditor.stableCount--;
                Log.i(BCSImageAdapter.this.TAG, "onDisplay:runnableCount " + ImageEditor.runnableCount);
                if (ImageEditor.stableCount <= 0) {
                    BCSImageAdapter.this.mStable = true;
                }
            }
        });
        this.mLceControl = lceControl;
        this.mSetter = setter;
        ImageEditor.runnableCount = 1;
    }

    public void terminate() {
        this.handler.removeCallbacks(this.runnable);
        if (this.mCurrentImage != null) {
            this.mCurrentImage.release();
        }
    }

    public int getCount() {
        return this.mMax;
    }

    public int getSelection() {
        return this.mCurrentPosition;
    }

    protected void update() {
        if (this.mStable && this.isFirst) {
            ImageEditor.runnableCount = 0;
            this.isFirst = false;
        }
        this.handler.removeCallbacks(this.runnable);
        this.handler.postDelayed(this.runnable, 100L);
    }

    public void setSelection(int position) {
        this.mSetter.setParameter(position, this.mLceControl);
        this.mCurrentPosition = position;
        update();
    }

    protected void setLayout(OptimizedImageView view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        if (ImageEditor.getAspectRatio() == 0) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 50;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 220;
                layoutParams.topMargin = 30;
                layoutParams.width = 200;
                layoutParams.height = 300;
            }
        } else if (1 == ImageEditor.getAspectRatio()) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 53;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.AF_MF;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 236;
                layoutParams.topMargin = 30;
                layoutParams.width = 168;
                layoutParams.height = 300;
            }
        } else if (2 == ImageEditor.getAspectRatio()) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 120;
                layoutParams.topMargin = 30;
                layoutParams.width = 400;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = AppRoot.USER_KEYCODE.PLAYBACK;
                layoutParams.topMargin = 30;
                layoutParams.width = 225;
                layoutParams.height = 300;
            }
        } else if (3 == ImageEditor.getAspectRatio()) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 170;
                layoutParams.topMargin = 30;
                layoutParams.width = 300;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 170;
                layoutParams.topMargin = 30;
                layoutParams.width = 300;
                layoutParams.height = 300;
            }
        } else {
            Log.d(this.TAG, "None of the four Aspect Ratios Matched");
        }
        Point p = new Point(layoutParams.width >> 1, layoutParams.height >> 1);
        view.setPivot(p);
        view.setLayoutParams(layoutParams);
        int orientation = ImageEditor.getOrientationInfo();
        switch (orientation) {
            case 3:
                view.setDisplayRotationAngle(180);
                return;
            case 4:
            case 5:
            case 7:
            default:
                view.setDisplayRotationAngle(0);
                return;
            case 6:
                view.setDisplayRotationAngle(90);
                return;
            case 8:
                view.setDisplayRotationAngle(270);
                return;
        }
    }

    public boolean isStable() {
        return this.mStable;
    }
}
