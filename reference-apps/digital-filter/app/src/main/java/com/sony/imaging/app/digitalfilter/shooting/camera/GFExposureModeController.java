package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFExposureModeController extends ExposureModeController {
    private static final String TAG = AppLog.getClassName();
    private static GFExposureModeController mInstance;

    public static GFExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static GFExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new GFExposureModeController();
        }
        return mInstance;
    }

    private GFExposureModeController() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("Aperture");
            availables.add(ExposureModeController.MANUAL_MODE);
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Shutter");
        }
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
                supported.add("Shutter");
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
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = ExposureModeController.scancode2Value(code);
            boolean isValid = isValidValue(expMode);
            return isValid;
        }
        return false;
    }

    public boolean isValidDialPosition(int mModeDialPosition) {
        if (537 != mModeDialPosition && 538 != mModeDialPosition && 539 != mModeDialPosition && 540 != mModeDialPosition) {
            return false;
        }
        return true;
    }

    public boolean isValidExpoMode(String expMode) {
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) && !"Shutter".equals(expMode) && !"Aperture".equals(expMode)) {
            return false;
        }
        return true;
    }
}
