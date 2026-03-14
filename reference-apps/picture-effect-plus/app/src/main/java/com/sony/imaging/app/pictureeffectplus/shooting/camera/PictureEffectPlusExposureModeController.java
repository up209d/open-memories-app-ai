package com.sony.imaging.app.pictureeffectplus.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.PictureEffectPlusCaution;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectPlusExposureModeController extends ExposureModeController {
    private static PictureEffectPlusExposureModeController mInstance;
    private static final String TAG = AppLog.getClassName();
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();

    static {
        SCN_SELECTION_LIST.add("portrait");
        SCN_SELECTION_LIST.add("landscape");
        SCN_SELECTION_LIST.add(ExposureModeController.SPORTS);
        SCN_SELECTION_LIST.add("sunset");
        SCN_SELECTION_LIST.add(CreativeStyleController.NIGHT);
        SCN_SELECTION_LIST.add("night-portrait");
        SCN_SELECTION_LIST.add(ExposureModeController.MACRO);
        SCN_SELECTION_LIST.add("hand-held-twilight");
        SCN_SELECTION_LIST.add("anti-motion-blur");
    }

    public static PictureEffectPlusExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static PictureEffectPlusExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new PictureEffectPlusExposureModeController();
        }
        return mInstance;
    }

    protected PictureEffectPlusExposureModeController() {
    }

    public void initializeAgain() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("Aperture");
            availables.add(ExposureModeController.MANUAL_MODE);
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add(ExposureModeController.SHUTTER_MODE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> supported = new ArrayList<>();
        if (!ModeDialDetector.hasModeDial()) {
            if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
                supported.add(ExposureModeController.PROGRAM_AUTO_MODE);
                supported.add("Aperture");
                supported.add(ExposureModeController.SHUTTER_MODE);
                supported.add(ExposureModeController.MANUAL_MODE);
            }
            if (supported.isEmpty()) {
                return null;
            }
        } else {
            AppLog.info(TAG, AppLog.getClassName() + " tag is " + tag);
            supported = super.getSupportedValue(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return supported;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = ExposureModeController.scancode2Value(code);
            boolean isValid = isValidValue(expMode);
            AppLog.enter(TAG, AppLog.getMethodName());
            return isValid;
        }
        return false;
    }

    public boolean isValidDialPosition(int mModeDialPosition) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isValid = false;
        if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    public boolean isValidExpoMode(String expMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isValid = false;
        if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || ExposureModeController.SHUTTER_MODE.equals(expMode) || "Aperture".equals(expMode)) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return PictureEffectPlusCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM;
    }
}
