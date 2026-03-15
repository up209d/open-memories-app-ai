package com.sony.imaging.app.bracketpro.caution;

import android.util.Log;
import com.sony.imaging.app.bracketpro.AppLog;

/* loaded from: classes.dex */
public class BracketMasterCaution {
    public static final int CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM = 131278;
    public static final int CAUTION_ID_DLAPP_EXCLUSIVE_REASON_AUTO_PANORAMA_BLUR_SCN = 131280;
    public static final int CAUTION_ID_DLAPP_WILL_CHANGE_TO_P = 131279;
    private static final String TAG = "PictureEffectPlusCaution";
    private static BracketMasterCaution mInstance = null;

    private BracketMasterCaution() {
    }

    public static BracketMasterCaution getInstance() {
        if (mInstance == null) {
            mInstance = new BracketMasterCaution();
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
