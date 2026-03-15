package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionPictureEffectController extends PictureEffectController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionPictureEffectController sInstance = null;

    private SmoothReflectionPictureEffectController() {
    }

    public static SmoothReflectionPictureEffectController getInstance() {
        if (sInstance == null) {
            sInstance = new SmoothReflectionPictureEffectController();
        }
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
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

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setValue(tag, value);
        if (ThemeController.CUSTOM.equals(getSelectedTheme())) {
            if (PictureEffectController.PICTUREEFFECT.equals(tag)) {
                BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_PICTURE_EFFECT, value);
            } else {
                BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_PICTURE_EFFECT_OPTION, value);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getSelectedTheme() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        AppLog.exit(TAG, AppLog.getMethodName());
        return selectedTheme;
    }
}
