package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DoubleExposureCompensationController extends ExposureCompensationController {
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposureCompensationController sInstance = null;

    private DoubleExposureCompensationController() {
    }

    public static DoubleExposureCompensationController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposureCompensationController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public boolean isModeDialAvailable() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = super.isModeDialAvailable();
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!selectedTheme.equalsIgnoreCase("Manual")) {
            bRetVal = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }
}
