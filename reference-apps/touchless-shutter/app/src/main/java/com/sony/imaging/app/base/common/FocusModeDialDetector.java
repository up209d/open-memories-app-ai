package com.sony.imaging.app.base.common;

import android.util.Log;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class FocusModeDialDetector {
    public static final int INVALID_SCAN_CODE = -1;
    private static final String TAG = "FocusModeDialDetector";

    public static int getFocusModeDialPosition() {
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED);
        if (1 == status.valid) {
            int position = status.status;
            return position;
        }
        Log.e(TAG, "Illegal state. key is invalid. scancode = " + status.status);
        return -1;
    }

    public static boolean hasFocusModeDial() {
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED);
        if (status.valid != 0) {
            return true;
        }
        return false;
    }
}
