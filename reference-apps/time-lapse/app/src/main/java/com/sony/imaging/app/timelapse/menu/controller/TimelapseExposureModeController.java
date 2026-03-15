package com.sony.imaging.app.timelapse.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TimelapseExposureModeController extends ExposureModeController {
    private static TimelapseExposureModeController mInstance;
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

    public static TimelapseExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static TimelapseExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new TimelapseExposureModeController();
        }
        return mInstance;
    }

    private TimelapseExposureModeController() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("Aperture");
            availables.add(ExposureModeController.MANUAL_MODE);
            availables.add("ProgramAuto");
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
                supported.add("ProgramAuto");
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
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !"ProgramAuto".equals(expMode) && !"Shutter".equals(expMode) && !"Aperture".equals(expMode)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable;
        boolean isAvailable2 = super.isAvailable(tag);
        AppLog.enter(TAG, "isAvailable()");
        if (isAvailable2 && TLCommonUtil.getThemeUtil().getCurrentState() == 7) {
            isAvailable = true;
        } else {
            isAvailable = false;
        }
        AppLog.info(TAG, "isAvailable() = " + tag + isAvailable);
        AppLog.exit(TAG, "isAvailable()");
        return isAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return TimelapseInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int index = super.getCautionIndex(itemId);
        if (TLCommonUtil.getThemeUtil().getCurrentState() != 7) {
            return 1;
        }
        return index;
    }
}
