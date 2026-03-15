package com.sony.imaging.app.startrails.base.menu.controller;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class STISOSensitivityController extends ISOSensitivityController {
    private static String TAG = AppLog.getClassName();
    private static STISOSensitivityController mInstance;

    public static STISOSensitivityController getInstance() {
        AppLog.enter(TAG, "getInstance()");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance()");
        return mInstance;
    }

    private static STISOSensitivityController createInstance() {
        AppLog.enter(TAG, "createInstance()");
        if (mInstance == null) {
            mInstance = new STISOSensitivityController();
        }
        AppLog.exit(TAG, "createInstance()");
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable = super.isAvailable(tag);
        AppLog.enter(TAG, "isAvailable()");
        AppLog.info(TAG, "isAvailable() = " + tag + isAvailable);
        AppLog.exit(TAG, "isAvailable()");
        return isAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        AppLog.enter(TAG, "setValue()");
        super.setValue(itemId, value);
        String value2 = getValue();
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                BackUpUtil.getInstance().setPreference(STBackUpKey.DARK_NIGHT_ISO_VALUE_KEY, value2);
                break;
            case 2:
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_ISO_VALUE_KEY, value2);
                break;
            default:
                BackUpUtil.getInstance().setPreference(STBackUpKey.BRIGHT_NIGHT_ISO_VALUE_KEY, value2);
                break;
        }
        AppLog.exit(TAG, "setValue()");
    }

    public void setStartTrailsValue(String itemId, String value) {
        AppLog.enter(TAG, "setValue()");
        super.setValue(itemId, value);
        AppLog.exit(TAG, "setValue()");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int getCautionIndex = super.getCautionIndex(itemId);
        return getCautionIndex;
    }
}
