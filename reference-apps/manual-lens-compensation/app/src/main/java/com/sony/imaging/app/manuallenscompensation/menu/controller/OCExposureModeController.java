package com.sony.imaging.app.manuallenscompensation.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class OCExposureModeController extends ExposureModeController {
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();
    private static OCExposureModeController mInstance;
    private String TAG = AppLog.getClassName();

    public static OCExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        ExposureModeController.getInstance();
        return mInstance;
    }

    private static OCExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new OCExposureModeController();
        }
        return mInstance;
    }

    protected OCExposureModeController() {
        ExposureModeController.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        int preRecMode = ExecutorCreator.getInstance().getRecordingMode();
        if (ExposureModeController.EXPOSURE_MODE.equals(tag) && Environment.isMovieAPISupported() && "movie".equals(value) && 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            OCUtil.getInstance().resetExifDataOff();
        }
        super.setValue(tag, value);
        if (ExposureModeController.EXPOSURE_MODE.equals(tag) && preRecMode != 1) {
            OCUtil.getInstance().setExifData();
        }
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
        SCN_SELECTION_LIST.add(ExposureModeController.ANTI_MOTION_BLUR);
        SCN_SELECTION_LIST.add(ExposureModeController.HAND_HELD_TWILIGHT);
        SCN_SELECTION_LIST.add("twilight");
        SCN_SELECTION_LIST.add(ExposureModeController.TWILIGHT_PORTRAIT);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        List<String> supporteds = super.getSupportedValue(tag);
        if (supporteds != null && !ModeDialDetector.hasModeDial()) {
            if (ExposureModeController.EXPOSURE_MODE.equals(tag)) {
                if (supporteds != null) {
                    supporteds.clear();
                }
                supporteds.add(ExposureModeController.PROGRAM_AUTO_MODE);
                supporteds.add("Aperture");
                supporteds.add("Shutter");
                supporteds.add(ExposureModeController.MANUAL_MODE);
                supporteds.add("SceneSelectionMode");
                if (Environment.isMovieAPISupported()) {
                    supporteds.add("movie");
                }
            } else {
                AppLog.info(this.TAG, "super getSupportedValue() inherrited ");
            }
        }
        if (supporteds == null || supporteds.isEmpty()) {
            return null;
        }
        return supporteds;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        List<String> supporteds = super.getAvailableValue(tag);
        if (supporteds != null) {
            if (ExposureModeController.EXPOSURE_MODE.equals(tag)) {
                if (supporteds != null) {
                    supporteds.clear();
                }
                supporteds.add(ExposureModeController.PROGRAM_AUTO_MODE);
                supporteds.add("Aperture");
                supporteds.add("Shutter");
                supporteds.add(ExposureModeController.MANUAL_MODE);
                supporteds.add("SceneSelectionMode");
                if (Environment.isMovieAPISupported()) {
                    supporteds.add("movie");
                }
            } else {
                AppLog.info(this.TAG, "super getSupportedValue() inherrited ");
            }
        }
        if (supporteds == null || supporteds.isEmpty()) {
            return null;
        }
        return supporteds;
    }

    public boolean isValidDialPosition(int mModeDialPosition) {
        if (537 != mModeDialPosition && 539 != mModeDialPosition && 538 != mModeDialPosition && 540 != mModeDialPosition && 545 != mModeDialPosition && (544 != mModeDialPosition || !Environment.isMovieAPISupported())) {
            return false;
        }
        return true;
    }

    public boolean isValidExpoMode(String expMode) {
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) && !"Shutter".equals(expMode) && !"Aperture".equals(expMode) && !"SceneSelectionMode".equals(expMode) && (!"movie".equals(expMode) || !Environment.isMovieAPISupported())) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return OCInfo.CAUTION_ID_DLAPP_PASMSCN_CLOSE_KEY;
    }
}
