package com.sony.imaging.app.startrails.base.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class STExposureModeController extends ExposureModeController {
    private static STExposureModeController mInstance;
    private static String TAG = AppLog.getClassName();
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

    public static STExposureModeController getInstance() {
        AppLog.enter(TAG, "getInstance");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance");
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 0:
                super.setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
                return;
            case 1:
                super.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MANUAL_MODE);
                return;
            default:
                super.setValue(tag, value);
                return;
        }
    }

    private static STExposureModeController createInstance() {
        AppLog.enter(TAG, "createInstance");
        if (mInstance == null) {
            mInstance = new STExposureModeController();
        }
        AppLog.exit(TAG, "createInstance");
        return mInstance;
    }

    private STExposureModeController() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, "getavailableValue");
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("Aperture");
            availables.add(ExposureModeController.MANUAL_MODE);
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Shutter");
        }
        AppLog.exit(TAG, "getavailableValue");
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
        boolean isValid = false;
        if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    public boolean isValidExpoMode(String expMode) {
        boolean isValid = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Shutter".equals(expMode) || "Aperture".equals(expMode)) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int index = super.getCautionIndex(itemId);
        if (STUtility.getInstance().getCurrentTrail() != 2) {
            return 1;
        }
        return index;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable;
        boolean isAvailable2 = super.isAvailable(tag);
        AppLog.enter(TAG, "isAvailable()");
        if (isAvailable2 && STUtility.getInstance().getCurrentTrail() == 2) {
            isAvailable = true;
        } else {
            isAvailable = false;
        }
        AppLog.info(TAG, "isAvailable() = " + tag + isAvailable);
        AppLog.exit(TAG, "isAvailable()");
        return isAvailable;
    }
}
