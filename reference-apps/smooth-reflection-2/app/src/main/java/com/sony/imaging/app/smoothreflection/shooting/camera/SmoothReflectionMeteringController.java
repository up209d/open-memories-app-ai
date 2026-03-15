package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionMeteringController extends MeteringController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionMeteringController sInstance = null;

    private SmoothReflectionMeteringController() {
    }

    public static SmoothReflectionMeteringController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionMeteringController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.MeteringController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!ThemeController.CUSTOM.equals(getSelectedTheme())) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.MeteringController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setValue(tag, value);
        if (ThemeController.CUSTOM.equals(getSelectedTheme())) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_METERING_MODE, value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getSelectedTheme() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return selectedTheme;
    }
}
