package com.sony.imaging.app.base.shooting.trigger;

import android.util.Log;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ModeDialDetector {
    public static final int DIAL_NONE = 3;
    public static final int DIAL_POSITION_LEFT = 2;
    public static final int DIAL_POSITION_RIGHT = 1;
    public static final int DIAL_UNKNOWN = 0;
    private static final int ENABLE_GET_MODE_DIAL_TYPE_VERSION = 2;
    public static final int INVALID_SCAN_CODE = -1;
    private static int[] MODE_DIAL_KEYCODES = {AppRoot.USER_KEYCODE.MODE_DIAL_INVALID, AppRoot.USER_KEYCODE.MODE_DIAL_AUTO, AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM, AppRoot.USER_KEYCODE.MODE_DIAL_AES, AppRoot.USER_KEYCODE.MODE_DIAL_AEA, AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, AppRoot.USER_KEYCODE.MODE_DIAL_SCN, AppRoot.USER_KEYCODE.MODE_DIAL_MOVIE};
    private static final String TAG = "ModeDialDetector";
    private static ArrayList<Integer> mSupportedModeDials;

    public static boolean isModeDialKeyEvent(int scanCode) {
        int[] arr$ = MODE_DIAL_KEYCODES;
        for (int code : arr$) {
            if (scanCode == code) {
                return true;
            }
        }
        return false;
    }

    public static int getModeDialPosition() {
        KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED);
        if (key.valid == 1) {
            int position = key.status;
            return position;
        }
        Log.e(TAG, "Illegal state. key is invalid. scancode = " + key.status);
        return -1;
    }

    public static List<Integer> getSupportedModeDials() {
        if (mSupportedModeDials == null) {
            mSupportedModeDials = new ArrayList<>();
            int[] scancodes = ScalarProperties.getIntArray("input.mode.sw.mode.list");
            if (scancodes != null) {
                for (int code : scancodes) {
                    mSupportedModeDials.add(Integer.valueOf(code));
                }
            }
            Log.i(TAG, "supported mode dials = " + mSupportedModeDials);
        }
        if (mSupportedModeDials.isEmpty()) {
            return null;
        }
        return mSupportedModeDials;
    }

    public static boolean hasModeDial() {
        List<Integer> list = getSupportedModeDials();
        return list != null;
    }

    public static int getDialPosition() {
        if (Environment.getVersionPfAPI() >= 2) {
            int pos = ScalarProperties.getInt("input.mode.sw.type");
            switch (pos) {
                case -1:
                    return 0;
                case 0:
                    return 3;
                case 1:
                default:
                    return 1;
                case 2:
                    return 2;
            }
        }
        return 1;
    }
}
