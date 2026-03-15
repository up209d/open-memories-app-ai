package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextSelftimer extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextSelftimer.class.getSimpleName();

    public RotationalSubLcdTextSelftimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextSelftimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextSelftimer(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        DriveModeController drivemodeController = DriveModeController.getInstance();
        String self = drivemodeController.getValue(DriveModeController.SELF);
        return !DriveModeController.SELF_TIMER_OFF.equals(self);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mText;
        DriveModeController drivemodeController = DriveModeController.getInstance();
        String self = drivemodeController.getValue(DriveModeController.SELF);
        if (DriveModeController.SELF_TIMER_2S.equals(self)) {
            String ret2 = getResources().getString(R.string.mediasize_na_gvrnmt_letter);
            return ret2;
        }
        if (DriveModeController.SELF_TIMER_10S.equals(self)) {
            String ret3 = getResources().getString(R.string.mediasize_na_index_4x6);
            return ret3;
        }
        Log.e(TAG, "Incorrect value:" + self);
        return ret;
    }
}
