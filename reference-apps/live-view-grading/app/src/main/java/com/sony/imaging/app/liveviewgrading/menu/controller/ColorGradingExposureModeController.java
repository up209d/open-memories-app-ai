package com.sony.imaging.app.liveviewgrading.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingCaution;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ColorGradingExposureModeController extends ExposureModeController {
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();
    private static ColorGradingExposureModeController mInstance;
    private String TAG = AppLog.getClassName();

    public static ColorGradingExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static ColorGradingExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new ColorGradingExposureModeController();
        }
        return mInstance;
    }

    private ColorGradingExposureModeController() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        if (!ModeDialDetector.hasModeDial()) {
            if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
                availables.add("movie");
            } else if (tag.equals("movie") || tag.equals(ExposureModeController.MOVIE_MODE_MENU)) {
                availables = super.getAvailableValue(tag);
            }
        } else if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add("movie");
        } else if (tag.equals("movie") || tag.equals(ExposureModeController.MOVIE_MODE_MENU)) {
            availables = super.getAvailableValue(tag);
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
                supported.add("movie");
            } else if (tag.equals("movie") || tag.equals(ExposureModeController.MOVIE_MODE_MENU)) {
                supported.add(ExposureModeController.MOVIE_APERATURE_MODE);
                supported.add(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
                supported.add(ExposureModeController.MOVIE_SHUTTER_MODE);
                supported.add(ExposureModeController.MOVIE_MANUAL_MODE);
            }
            if (supported.isEmpty()) {
                return null;
            }
        } else if (tag.equals("movie")) {
            AppLog.info(this.TAG, AppLog.getClassName() + " tag is " + tag);
            supported = super.getSupportedValue(tag);
            Log.e("", "supported = " + supported.toString());
            supported.add(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
            supported.add(ExposureModeController.MOVIE_APERATURE_MODE);
            supported.add(ExposureModeController.MOVIE_SHUTTER_MODE);
            supported.add(ExposureModeController.MOVIE_MANUAL_MODE);
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
        if (544 != mModeDialPosition) {
            return false;
        }
        return true;
    }

    public boolean isValidExpoMode(String expMode) {
        if (!ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode) && !ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(expMode) && !ExposureModeController.MOVIE_SHUTTER_MODE.equals(expMode) && !ExposureModeController.MOVIE_APERATURE_MODE.equals(expMode)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return ColorGradingCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE;
    }

    private void createSupportedValueArray() {
    }
}
