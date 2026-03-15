package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SmoothReflectionExposureModeController extends ExposureModeController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionExposureModeController sInstance = null;

    private SmoothReflectionExposureModeController() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static SmoothReflectionExposureModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionExposureModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setValue(tag, value);
        if (ThemeController.CUSTOM.equals(getSelectedTheme())) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_SHOOT_MODE, value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.setDetailValue(obj);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Aperture");
            availables.add("Shutter");
            availables.add(ExposureModeController.MANUAL_MODE);
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Aperture");
            availables.add("Shutter");
            availables.add(ExposureModeController.MANUAL_MODE);
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        super.isAvailable(tag);
        if (!ThemeController.CUSTOM.equals(getSelectedTheme())) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = ExposureModeController.scancode2Value(code);
            boolean isValid = isValidValue(expMode);
            AppLog.exit(TAG, AppLog.getMethodName());
            return isValid;
        }
        return false;
    }

    public boolean isValidDialPosition(int mModeDialPosition) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = false;
        if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition) {
            bRetVal = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public void initializeToSetCameraSettings() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    public boolean isValidExpoMode(String expMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isValid = false;
        if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Shutter".equals(expMode) || "Aperture".equals(expMode)) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
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

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return Info.CAUTION_ID_DLAPP_PASM_IAUTO_SCN_CLOSEKEY;
    }
}
