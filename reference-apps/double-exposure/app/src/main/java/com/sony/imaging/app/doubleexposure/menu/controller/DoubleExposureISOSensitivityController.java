package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DoubleExposureISOSensitivityController extends ISOSensitivityController {
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposureISOSensitivityController sInstance = null;

    private DoubleExposureISOSensitivityController() {
    }

    public static DoubleExposureISOSensitivityController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposureISOSensitivityController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equals(selectedTheme)) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equals(selectedTheme)) {
            ret = 1;
        } else {
            ret = super.getCautionIndex(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }
}
