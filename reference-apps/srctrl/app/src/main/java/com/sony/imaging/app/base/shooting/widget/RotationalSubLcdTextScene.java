package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextScene extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextScene.class.getSimpleName();
    private final String UNDER_WATER_STR;

    public RotationalSubLcdTextScene(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.UNDER_WATER_STR = getResources().getString(R.string.lockscreen_access_pattern_cleared);
    }

    public RotationalSubLcdTextScene(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.UNDER_WATER_STR = getResources().getString(R.string.lockscreen_access_pattern_cleared);
    }

    public RotationalSubLcdTextScene(Context context) {
        super(context);
        this.UNDER_WATER_STR = getResources().getString(R.string.lockscreen_access_pattern_cleared);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        String exposureMode = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
        return exposureMode != null && exposureMode.equals(ExposureModeController.COMMON_UNDERWATER);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mText;
        String exposureMode = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
        if (exposureMode != null && exposureMode.equals(ExposureModeController.COMMON_UNDERWATER)) {
            return this.UNDER_WATER_STR;
        }
        return ret;
    }
}
