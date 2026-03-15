package com.sony.imaging.app.bracketpro.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMFocusSettingHelper {
    private static final String COMMA = ", ";
    private static final String EMPTY_STRING = "";
    private static final StringBuilder LOG_STRING = new StringBuilder();
    private static final String NOT_REMEMBER = "Not remember because already stored";
    private static final String NOT_RESET = "Not reset because not stored";
    private static final String RESET = "reset ";
    private static final String STORE = "store ";
    private static final String TAG = "BMFocusSettingHelper";

    public static void remember() {
        BackUpUtil backUpUtil = BackUpUtil.getInstance();
        if ("".equals(backUpUtil.getPreferenceString(BracketMasterBackUpKey.DIADEM_FOCUS_MODE, ""))) {
            String focusMode = FocusModeController.getInstance().getValue();
            String afMode = AutoFocusModeController.getInstance().getValue();
            LOG_STRING.replace(0, LOG_STRING.length(), STORE).append(focusMode).append(", ").append(afMode);
            backUpUtil.setPreference(BracketMasterBackUpKey.DIADEM_FOCUS_MODE, focusMode);
            backUpUtil.setPreference(BracketMasterBackUpKey.DIADEM_AUTO_FOCUS_MODE, afMode);
            return;
        }
        Log.d(TAG, NOT_REMEMBER);
    }

    public static void setBack() {
        BackUpUtil backUpUtil = BackUpUtil.getInstance();
        String focusMode = backUpUtil.getPreferenceString(BracketMasterBackUpKey.DIADEM_FOCUS_MODE, "");
        if (!"".equals(focusMode)) {
            String afMode = backUpUtil.getPreferenceString(BracketMasterBackUpKey.DIADEM_AUTO_FOCUS_MODE, "");
            LOG_STRING.replace(0, LOG_STRING.length(), RESET).append(focusMode).append(", ").append(afMode);
            Log.i(TAG, LOG_STRING.toString());
            FocusModeController.getInstance().setValue(focusMode);
            AutoFocusModeController.getInstance().setValue(afMode);
        } else {
            Log.d(TAG, NOT_RESET);
        }
        backUpUtil.setPreference(BracketMasterBackUpKey.DIADEM_FOCUS_MODE, "");
        backUpUtil.setPreference(BracketMasterBackUpKey.DIADEM_AUTO_FOCUS_MODE, "");
    }
}
