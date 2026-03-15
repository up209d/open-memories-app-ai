package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextDriveMode extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextDriveMode.class.getSimpleName();

    public RotationalSubLcdTextDriveMode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextDriveMode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextDriveMode(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        return super.isValidValue();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String str = this.mText;
        DriveModeController drivemodeController = DriveModeController.getInstance();
        String driveMode = drivemodeController.getValue(DriveModeController.DRIVEMODE);
        if (DriveModeController.BURST.equals(driveMode)) {
            String ret = getResources().getString(R.string.notification_channel_wfc);
            return ret;
        }
        if (DriveModeController.MOTION_SHOT.equals(driveMode)) {
            String ret2 = getResources().getString(R.string.notification_header_divider_symbol);
            return ret2;
        }
        String ret3 = getResources().getString(R.string.notification_channel_vpn);
        return ret3;
    }
}
