package com.sony.imaging.app.smoothreflection.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothReflectionCreativeStyleController extends CreativeStyleController {
    private static final String SEPARATOR_SLASH = "/";
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionCreativeStyleController sInstance = null;

    private SmoothReflectionCreativeStyleController() {
    }

    public static SmoothReflectionCreativeStyleController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionCreativeStyleController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
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

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setValue(tag, value);
        if (ThemeController.CUSTOM.equals(getSelectedTheme())) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE, value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    public void setDetailValue(String mode, Object obj, Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setDetailValue(mode, obj, p);
        if (ThemeController.CUSTOM.equals(getSelectedTheme())) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE_DETAIL, convertCreativeStyleOptionToString((CreativeStyleController.CreativeStyleOptions) obj));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getSelectedTheme() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        AppLog.exit(TAG, AppLog.getMethodName());
        return selectedTheme;
    }

    private String convertCreativeStyleOptionToString(CreativeStyleController.CreativeStyleOptions options) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String optionString = (("" + options.contrast) + SEPARATOR_SLASH + options.saturation) + SEPARATOR_SLASH + options.sharpness;
        AppLog.info(TAG, "Creative style Detail Value  " + optionString);
        AppLog.exit(TAG, AppLog.getMethodName());
        return optionString;
    }
}
