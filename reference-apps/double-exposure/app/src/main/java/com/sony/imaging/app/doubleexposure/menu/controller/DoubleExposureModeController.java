package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DoubleExposureModeController extends ExposureModeController {
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposureModeController sInstance = null;
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();

    static {
        SCN_SELECTION_LIST.add("portrait");
        SCN_SELECTION_LIST.add("landscape");
        SCN_SELECTION_LIST.add("sunset");
        SCN_SELECTION_LIST.add(CreativeStyleController.NIGHT);
        SCN_SELECTION_LIST.add("night-portrait");
        SCN_SELECTION_LIST.add(ExposureModeController.MACRO);
        SCN_SELECTION_LIST.add("hand-held-twilight");
        SCN_SELECTION_LIST.add("anti-motion-blur");
        SCN_SELECTION_LIST.add(ExposureModeController.ANTI_MOTION_BLUR);
        SCN_SELECTION_LIST.add(ExposureModeController.HAND_HELD_TWILIGHT);
        SCN_SELECTION_LIST.add("twilight");
        SCN_SELECTION_LIST.add(ExposureModeController.TWILIGHT_PORTRAIT);
    }

    private DoubleExposureModeController() {
    }

    public static DoubleExposureModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposureModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Aperture");
            availables.add(ExposureModeController.SHUTTER_MODE);
            availables.add("Manual");
            availables.add(ExposureModeController.INTELLIGENT_AUTO_MODE);
            availables.add("SceneSelectionMode");
        } else if ("SceneSelectionMode".equals(tag) || ExposureModeController.SCENE_SELECTION_MODE_MENU.equals(tag)) {
            availables = SCN_SELECTION_LIST;
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
            availables.add(ExposureModeController.SHUTTER_MODE);
            availables.add("Manual");
            availables.add(ExposureModeController.INTELLIGENT_AUTO_MODE);
            availables.add("SceneSelectionMode");
        } else if ("SceneSelectionMode".equals(tag) || ExposureModeController.SCENE_SELECTION_MODE_MENU.equals(tag)) {
            availables = SCN_SELECTION_LIST;
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = super.isAvailable(tag);
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equals(selectedTheme)) {
            bRetVal = false;
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
        if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition || 535 == mModeDialPosition || 545 == mModeDialPosition) {
            bRetVal = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public void initializeToSetCameraSettings() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
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
