package com.sony.imaging.app.bracketpro.menu.controller;

import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMFocusModeController extends FocusModeController {
    private static BMFocusModeController sInstance = null;
    private static final String TAG = AppLog.getClassName();

    private BMFocusModeController() {
    }

    public static BMFocusModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new BMFocusModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FocusModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        String mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FocusModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        if (FocusModeController.MANUAL_CONTROL_WHEEL.equals(value.toString())) {
            value = FocusModeController.MANUAL;
        } else if (FocusModeController.SMF_CONTROL_WHEEL.equals(value.toString())) {
            value = FocusModeController.SMF;
        }
        super.setValue(tag, value);
    }
}
