package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.AngleController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextAngle extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextAngle.class.getSimpleName();

    public RotationalSubLcdTextAngle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextAngle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextAngle(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String value = AngleController.getInstance().getValue(AngleController.TAG_ANGLE);
        if (AngleController.ANGLE_120.equals(value)) {
            return getResources().getString(R.string.lockscreen_access_pattern_cell_added_verbose);
        }
        if (AngleController.ANGLE_170.equals(value)) {
            return getResources().getString(R.string.lockscreen_access_pattern_cell_added);
        }
        return "";
    }
}
