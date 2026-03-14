package com.sony.imaging.app.util;

import android.content.Context;
import android.util.Log;

/* loaded from: classes.dex */
public class OLEDWrapper {
    protected static final String DEV_TYPE_LUMINANCE_ALPHA = "SCREEN_GAIN_CONTROL_LUMINANCE_ALPHA";
    protected static final String DEV_TYPE_LUMINANCE_ONLY = "SCREEN_GAIN_CONTROL_LUMINANCE_ONLY";
    protected static final String DEV_TYPE_NO_CONTROL = "SCREEN_GAIN_NO_CONTROL";
    private static final String LOG_OLED_TYPE = "setOledType : ";
    private static final String TAG = "OledWrapper";
    private static OLEDBurning mOledBurning = null;
    private static final StringBuilder LOG_STRING = new StringBuilder();

    /* loaded from: classes.dex */
    public enum OLED_TYPE {
        UNKNOWN,
        NO_CONTROL,
        LUMINANCE_ONLY,
        LUMINANCE_ALPHA
    }

    private OLEDWrapper() {
    }

    public static void initialize(Context context) {
        mOledBurning = new OLEDBurning(context);
    }

    public static void terminate() {
        mOledBurning.terminate();
        mOledBurning = null;
    }

    public static boolean setOledType(OLED_TYPE type) {
        Log.d(TAG, LOG_STRING.replace(0, LOG_STRING.length(), LOG_OLED_TYPE).append(type).toString());
        String dev_value = null;
        switch (type) {
            case NO_CONTROL:
                dev_value = DEV_TYPE_NO_CONTROL;
                break;
            case LUMINANCE_ONLY:
                dev_value = DEV_TYPE_LUMINANCE_ONLY;
                break;
            case LUMINANCE_ALPHA:
                dev_value = DEV_TYPE_LUMINANCE_ALPHA;
                break;
        }
        if (dev_value != null) {
            mOledBurning.setOLEDType(dev_value);
        }
        return OLED_TYPE.UNKNOWN != type;
    }
}
