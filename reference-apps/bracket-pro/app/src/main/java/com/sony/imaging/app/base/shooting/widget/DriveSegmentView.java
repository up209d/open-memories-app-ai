package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;

/* loaded from: classes.dex */
public class DriveSegmentView extends ShootingSubLcdActiveText {
    private static final String TAG = "ShootingModeSegmentView";
    int mTextBurst;
    int mTextMotionShot;
    int mTextSingle;

    public DriveSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DriveSegmentView);
        this.mTextSingle = typedArray.getResourceId(0, 0);
        this.mTextMotionShot = typedArray.getResourceId(1, 0);
        this.mTextBurst = typedArray.getResourceId(2, 0);
        typedArray.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        String appValue = DriveModeController.getInstance().getValue();
        if (DriveModeController.SINGLE.equals(appValue)) {
            setText(this.mTextSingle);
        } else if (DriveModeController.MOTION_SHOT.equals(appValue)) {
            setText(this.mTextMotionShot);
        } else if (DriveModeController.BURST.equals(appValue)) {
            setText(this.mTextBurst);
        }
    }
}
