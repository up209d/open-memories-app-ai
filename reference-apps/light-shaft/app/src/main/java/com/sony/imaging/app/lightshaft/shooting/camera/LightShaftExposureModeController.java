package com.sony.imaging.app.lightshaft.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.caution.LightShaftInfo;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LightShaftExposureModeController extends ExposureModeController {
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();
    private static LightShaftExposureModeController mInstance;
    private String TAG = AppLog.getClassName();

    public static LightShaftExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static LightShaftExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new LightShaftExposureModeController();
        }
        return mInstance;
    }

    private LightShaftExposureModeController() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("Aperture");
            availables.add(ExposureModeController.MANUAL_MODE);
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add(ExposureModeController.SHUTTER_MODE);
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
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
            AppLog.info(this.TAG, AppLog.getClassName() + " tag is " + tag);
            supported = super.getSupportedValue(tag);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
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

    public boolean isValidDialPosition(int mModeDialPosition) {
        if (537 != mModeDialPosition && 538 != mModeDialPosition && 539 != mModeDialPosition && 540 != mModeDialPosition) {
            return false;
        }
        return true;
    }

    public boolean isValidExpoMode(String expMode) {
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) && !ExposureModeController.SHUTTER_MODE.equals(expMode) && !"Aperture".equals(expMode)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return LightShaftInfo.CAUTION_ID_DLAPP_PASM_CLOSE_KEY;
    }
}
