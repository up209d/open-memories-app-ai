package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextFPS extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextFPS.class.getSimpleName();

    public RotationalSubLcdTextFPS(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextFPS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextFPS(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        DriveModeController drivemodeController = DriveModeController.getInstance();
        String driveMode = drivemodeController.getValue(DriveModeController.DRIVEMODE);
        return DriveModeController.BURST.equals(driveMode) || DriveModeController.MOTION_SHOT.equals(driveMode);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mText;
        DriveModeController drivemodeController = DriveModeController.getInstance();
        String fps = drivemodeController.getValue(DriveModeController.FPS);
        if (DriveModeController.BURST_SPEED_HIGH.equals(fps)) {
            String ret2 = getResources().getString(R.string.notification_header_divider_symbol_with_spaces);
            return ret2;
        }
        if (DriveModeController.BURST_SPEED_MID.equals(fps)) {
            String ret3 = getResources().getString(R.string.old_app_action);
            return ret3;
        }
        if ("low".equals(fps)) {
            String ret4 = getResources().getString(R.string.oneMonthDurationPast);
            return ret4;
        }
        Log.e(TAG, "Incorrect value:" + fps);
        return ret;
    }
}
