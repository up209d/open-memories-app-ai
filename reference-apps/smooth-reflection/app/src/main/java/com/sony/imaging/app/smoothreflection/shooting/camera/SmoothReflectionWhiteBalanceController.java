package com.sony.imaging.app.smoothreflection.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionWhiteBalanceController extends WhiteBalanceController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionWhiteBalanceController sInstance = null;

    private SmoothReflectionWhiteBalanceController() {
    }

    public static SmoothReflectionWhiteBalanceController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionWhiteBalanceController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
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

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.d("WBCheck", "SR setValue itemId = " + tag);
        Log.d("WBCheck", "SR setValue value = " + value);
        super.setValue(tag, value);
        if (ThemeController.CUSTOM.equals(getSelectedTheme()) && !WhiteBalanceController.CUSTOM_SET.equals(value)) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_WHITE_BALANCE, value);
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_WHITE_BALANCE_DETAIL, convertWhiteBalanceParamToString());
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

    private String convertWhiteBalanceParamToString() {
        AppLog.enter(TAG, AppLog.getMethodName());
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String paramValue = (("" + param.getLightBalance()) + "/" + param.getColorComp()) + "/" + param.getColorTemp();
        AppLog.info(TAG, "WhiteBalance Detail Value  " + paramValue);
        AppLog.exit(TAG, AppLog.getMethodName());
        return paramValue;
    }
}
