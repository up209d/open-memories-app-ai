package com.sony.imaging.app.srctrl.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShootMode;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ExposureModeControllerEx extends ExposureModeController {
    private static final String UI_MODEL_NAME_ZV = "DSC-15-02";
    private boolean reopenFlag = false;
    private static final String TAG = ExposureModeControllerEx.class.getSimpleName();
    private static final List<String> supportedMovieExposureMode = Collections.unmodifiableList(new ArrayList<String>() { // from class: com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx.1
        {
            add(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
            add(ExposureModeController.MOVIE_APERATURE_MODE);
            add(ExposureModeController.MOVIE_SHUTTER_MODE);
            add(ExposureModeController.MOVIE_MANUAL_MODE);
            add(ExposureModeController.MOVIE_AUTO);
        }
    });

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        super.setValue(tag, value);
        CameraOperationShootMode.resetLastStillExposureMode();
        if (Environment.getVersionPfAPI() >= 14) {
            String model = ScalarProperties.getString("ui.model.mame");
            if ("DSC-15-02".equals(model)) {
                Log.w(TAG, "Special Setting. (setValue) Model=DSC-15-02");
                ExecutorCreator.getInstance().waitChangingRecMode();
                String afterValue = getValue(ExposureModeController.EXPOSURE_MODE);
                if ("movie".equals(afterValue)) {
                    afterValue = getValue("movie");
                }
                Log.w(TAG, "Special Setting. (setValue) After Value =" + afterValue);
                boolean ssUpdate = false;
                if (this.reopenFlag && ("Aperture".equals(afterValue) || "Manual".equals(afterValue) || ExposureModeController.MOVIE_APERATURE_MODE.equals(afterValue) || ExposureModeController.MOVIE_MANUAL_MODE.equals(afterValue))) {
                    ssUpdate = true;
                }
                Log.w(TAG, "Special Setting. (setValue) ssUpdate =" + ssUpdate);
                if (ssUpdate) {
                    CameraSetting.getInstance().getCamera().adjustAperture(1);
                    CameraSetting.getInstance().getCamera().adjustAperture(-1);
                    this.reopenFlag = false;
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = super.isAvailable(tag);
        if ("SceneSelectionMode".equals(tag)) {
            return false;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            availables.add("Aperture");
            availables.add("Shutter");
            availables.add("Manual");
            availables.add(ExposureModeController.INTELLIGENT_AUTO_MODE);
            if ((SRCtrl.getRecMode() & 2) != 0) {
                availables.add("movie");
            }
        } else if (("movie".equals(tag) || ExposureModeController.MOVIE_MODE_MENU.equals(tag)) && (SRCtrl.getRecMode() & 2) != 0) {
            availables.clear();
            List<String> tmpAvailables = super.getAvailableValue(tag);
            if (tmpAvailables == null) {
                return null;
            }
            for (String mode : tmpAvailables) {
                if (supportedMovieExposureMode.contains(mode)) {
                    availables.add(mode);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = ExposureModeController.scancode2Value(code);
            if (expMode.equals("SceneSelectionMode")) {
                return false;
            }
            boolean isValid = isValidValue(expMode);
            return isValid;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> supported = new ArrayList<>();
        if (("movie".equals(tag) || ExposureModeController.MOVIE_MODE_MENU.equals(tag)) && (SRCtrl.getRecMode() & 2) == 0) {
            return null;
        }
        if (!ModeDialDetector.hasModeDial()) {
            if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
                supported.add(ExposureModeController.PROGRAM_AUTO_MODE);
                supported.add("Aperture");
                supported.add("Shutter");
                supported.add("Manual");
                supported.add(ExposureModeController.INTELLIGENT_AUTO_MODE);
                if ((SRCtrl.getRecMode() & 2) != 0) {
                    supported.add("movie");
                }
            } else {
                supported.clear();
                List<String> tmpSupported = super.getSupportedValue(tag);
                if (tmpSupported == null) {
                    return null;
                }
                for (String mode : tmpSupported) {
                    if (supportedMovieExposureMode.contains(mode)) {
                        supported.add(mode);
                    }
                }
            }
            if (supported.isEmpty()) {
                return null;
            }
        } else {
            supported = super.getSupportedValue(tag);
        }
        return supported;
    }

    public static boolean isExposureModeMovieSelected() {
        int code;
        String expMode;
        if (!ModeDialDetector.hasModeDial() || -1 == (code = ModeDialDetector.getModeDialPosition()) || (expMode = ExposureModeController.scancode2Value(code)) == null || !expMode.equals("movie")) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        return isExposureModeMovieSelected() ? InfoEx.CAUTION_ID_SMART_REMOTE_INVALID_MOVIE_CLOSEKEY : super.getCautionId();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        if (Environment.getVersionPfAPI() >= 14) {
            String model = ScalarProperties.getString("ui.model.mame");
            if ("DSC-15-02".equals(model)) {
                Log.w(TAG, "Special Setting. (onCameraReopened) Model=DSC-15-02");
                CameraSetting.getInstance().getCamera().adjustAperture(1);
                CameraSetting.getInstance().getCamera().adjustAperture(-1);
                this.reopenFlag = true;
            }
        }
        super.onCameraReopened();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        super.onCameraSet();
        if (Environment.getVersionPfAPI() >= 14) {
            String model = ScalarProperties.getString("ui.model.mame");
            if ("DSC-15-02".equals(model)) {
                Log.w(TAG, "Special Setting. (onCameraSet) reopenFlag Clear.");
                this.reopenFlag = false;
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> available;
        super.onGetInitParameters(params);
        if (ExecutorCreator.getInstance().getRecordingMode() == 2) {
            String current = getValue("movie");
            if (!supportedMovieExposureMode.contains(current) && (available = getAvailableValue("movie")) != null && available.size() > 0) {
                setValue("movie", available.get(0));
            }
        }
    }
}
