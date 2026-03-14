package com.sony.imaging.app.base.common;

import android.util.Log;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class EVDialDetector {
    public static final int EV_SPECIAL_POSITION = 0;
    public static final int EV_STEP_1_2 = 2;
    public static final int EV_STEP_1_3 = 1;
    public static final int INVALID_EV_CODE = 100000;
    private static final String TAG = "EVDialDetector";
    protected static int[] mEVList = null;

    private static int[] getEVList() {
        if (mEVList == null) {
            mEVList = ScalarProperties.getIntArray("input.ev.dial.position.list");
        }
        return mEVList;
    }

    public static int getEVDialPosition() {
        KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED);
        if (key.valid == 1) {
            int position = key.status;
            if ((position & 512) == 512) {
                return -(position - 512);
            }
            return position;
        }
        Log.e(TAG, "Illegal state. key is invalid. scancode = " + key.status);
        return INVALID_EV_CODE;
    }

    public static int getEVStep() {
        switch (getEVList()[0]) {
            case 2:
                return 2;
            case 3:
                return 1;
            default:
                return 1;
        }
    }

    public static boolean hasEVDial() {
        KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED);
        if (key.valid != 0) {
            return true;
        }
        return false;
    }
}
