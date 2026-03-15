package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionISOSensitivityController extends ISOSensitivityController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionISOSensitivityController sInstance = null;

    private SmoothReflectionISOSensitivityController() {
    }

    public static SmoothReflectionISOSensitivityController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionISOSensitivityController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
        }
        if (!ThemeController.CUSTOM.equals(selectedTheme)) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setValue(tag, value);
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
        }
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_ISO, value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
