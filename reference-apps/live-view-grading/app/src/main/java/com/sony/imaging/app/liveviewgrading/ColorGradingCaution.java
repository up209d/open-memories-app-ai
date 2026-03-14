package com.sony.imaging.app.liveviewgrading;

import android.util.Log;

/* loaded from: classes.dex */
public class ColorGradingCaution {
    public static final int CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE = 131278;
    public static final int CAUTION_ID_DLAPP_EXCLUSIVE_REASON_AUTO_PANORAMA_BLUR_SCN = 131280;
    public static final int CAUTION_ID_DLAPP_WILL_CHANGE_TO_MOVIE_P = 131279;
    private static final String TAG = "PictureEffectPlusCaution";
    private static ColorGradingCaution mInstance = null;

    private ColorGradingCaution() {
    }

    public static ColorGradingCaution getInstance() {
        if (mInstance == null) {
            mInstance = new ColorGradingCaution();
            Log.i(TAG, "mInstance created");
        }
        return mInstance;
    }

    public boolean isPASMDialPosition(int mModeDialPosition) {
        boolean isPASM = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition) {
            isPASM = true;
        }
        Log.i(TAG, "isPASM:" + isPASM);
        AppLog.exit(TAG, AppLog.getMethodName());
        return isPASM;
    }
}
