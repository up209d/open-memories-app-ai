package com.sony.imaging.app.bracketpro.menu.controller;

import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMFlashController extends FlashController {
    private static BMFlashController sInstance = null;
    private static final String TAG = AppLog.getClassName();

    private BMFlashController() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FlashController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        String mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            bRetVal = true;
        } else {
            bRetVal = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }
}
