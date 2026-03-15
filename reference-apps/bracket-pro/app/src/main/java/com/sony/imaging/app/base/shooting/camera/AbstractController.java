package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class AbstractController implements IController {
    protected static final String LOG_GET_AVAILABLE_VALUE = "getAvailableValue:";
    protected static final String LOG_GET_SUPPORTED_VALUE = "getSupportedValue:";
    protected static final String LOG_GET_VALUE = "getValue:";
    protected static final String LOG_IS_AVAILABLE = "isAvailable:";
    protected static final String LOG_SET_VALUE = "setValue:";
    protected static final String LOG_VALUE_TAG = ", value:";
    static final Map<String, String[]> SCENE_MODE_FACTOR = new HashMap();
    protected static HashMap<String, Boolean> sIsUnavailableSceneFactor;

    static {
        SCENE_MODE_FACTOR.put(BracketMasterSubMenu.PROGRAM_AUTO, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_PROGRAM_MODE_STROBE_MANUAL_POPUP_DISABLE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_PROGRAM_MODE_TYPE_P"});
        SCENE_MODE_FACTOR.put(BracketMasterSubMenu.APERTURE_PRIORITY, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_IRIS_MODE_STROBE_MANUAL_POPUP_DISABLE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_IRIS_MODE_TYPE_P"});
        SCENE_MODE_FACTOR.put(BracketMasterSubMenu.SHUTTER_SPEED, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_SHUTTER_MODE_STROBE_MANUAL_POPUP_DISABLE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_SHUTTER_MODE_TYPE_P"});
        SCENE_MODE_FACTOR.put(BracketMasterSubMenu.MANUAL_EXPOSURE, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_MANUAL_MODE_STROBE_MANUAL_POPUP_DISABLE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_MANUAL_MODE_TYPE_P"});
        SCENE_MODE_FACTOR.put("anti-motion-blur", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_ANTI_MOTION_BLUR_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_ANTI_MOTION_BLUR_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("auto", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_FOCUS_DIAL_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_BODY_PHASE_SHIFT_AF_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_BODY_FOCAL_PLANE_PHASE_SHIFT_AF_TYPE_P"});
        SCENE_MODE_FACTOR.put("hand-held-twilight", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_HAND_HELD_TWILIGHT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_HAND_HELD_TWILIGHT_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("landscape", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_LANDSCAPE_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_LANDSCAPE_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.MACRO, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_MACRO_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_MACRO_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("portrait", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_PORTRAIT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_PORTRAIT_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.SPORTS, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_SPORTS_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_SPORTS_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("sunset", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_SUNSET_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_SUNSET_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("night-portrait", new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_TWILIGHT_PORTRAIT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_TWILIGHT_PORTRAIT_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(CreativeStyleController.NIGHT, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_TWILIGHT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_TWILIGHT_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.FIREWORKS, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_FIREWORKS_MODE_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.BEACH, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_BEACH_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_BEACH_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.SNOW, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_SNOW_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_SNOW_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.PET, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_PET_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_PET_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.GOURMET, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_GOURMET_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_GOURMET_BODY_PHASE_SHIFT_AF_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_GOURMET_FOCUS_DIAL_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_GOURMET_FOCUS_DIAL_BODY_PHASE_SHIFT_AF_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.HIGH_SENSITIVITY, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_HIGH_SENSITIVITY_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_HIGH_SENSITIVITY_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.BACK_LIGHT, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_BACKLIGHT_CORRECTION_HDR_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_BACKLIGHT_CORRECTION_HDR_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.SOFT_SKIN, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_SOFT_SKIN_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_SOFT_SKIN_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put(ExposureModeController.ADVANCE_SPORTS, new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_ADVANCE_SPORTS_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_ADVANCE_SPORTS_MODE_FOCUS_DIAL_TYPE_P"});
        SCENE_MODE_FACTOR.put("movieprogram-auto", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_PROGRAM_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_PROGRAM_MODE_AF_AREA_FIXED_TYPE_P"});
        SCENE_MODE_FACTOR.put("movieaperture-priority", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_IRIS_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_IRIS_MODE_AF_AREA_FIXED_TYPE_P"});
        SCENE_MODE_FACTOR.put("movieshutter-speed", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_SHUTTER_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_SHUTTER_MODE_AF_AREA_FIXED_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviemanual-exposure", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_MANUAL_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_MANUAL_MODE_ISO_AUTO_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_MANUAL_MODE_AF_AREA_FIXED_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_MANUAL_MODE_ISO_AUTO_AF_AREA_FIXED_TYPE_P"});
        SCENE_MODE_FACTOR.put("movieauto", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_AUTO2_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_AUTO2_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("movieportrait", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_PORTRAIT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_PORTRAIT_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("movielandscape", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_LANDSCAPE_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_LANDSCAPE_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("movienight", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_TWILIGHT_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_TWILIGHT_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviebeach", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_BEACH_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_BEACH_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviesnow", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_SNOW_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_SNOW_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviefireworks", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_FIREWORKS_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_FIREWORKS_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviehigh-sensitivity", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_HIGH_SENSITIVITY_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_HIGH_SENSITIVITY_MODE_MF_NOT_SUPPORT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviepicture-effect", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_PICTURE_EFFECT_TYPE_P"});
        SCENE_MODE_FACTOR.put("moviemy-photo-style", new String[]{"INH_FACTOR_CAM_SET_EXC_MOVIE_MY_PHOTO_STYLE_TYPE_P"});
        sIsUnavailableSceneFactor = new HashMap<>();
    }

    public boolean isUnavailableSceneFactor(String tag) {
        return false;
    }

    public static void clearCache() {
        sIsUnavailableSceneFactor.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isUnavailableAPISceneFactor(String... apiName) {
        String key = Arrays.deepToString(apiName);
        Boolean value = sIsUnavailableSceneFactor.get(key);
        if (value != null) {
            return value.booleanValue();
        }
        boolean result = isUnavailableAPISceneFactor(null, apiName);
        sIsUnavailableSceneFactor.put(key, Boolean.valueOf(result));
        return result;
    }

    protected boolean isUnavailableAPISceneFactor(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String... apiName) {
        boolean ret = false;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        String mode = ((Camera.Parameters) params.first).getSceneMode();
        int recordingMode = ExecutorCreator.getInstance().getRecordingMode();
        if (recordingMode == 2 || recordingMode == 8) {
            mode = "movie" + mode;
        }
        String[] factor = SCENE_MODE_FACTOR.get(mode);
        if (factor == null) {
            return false;
        }
        int count = factor.length;
        if (p == null) {
            for (int i = 0; i < count && !ret; i++) {
                ret = AvailableInfo.isFactor(factor[i], apiName);
            }
        } else {
            for (int i2 = 0; i2 < count && !ret; i2++) {
                ret = AvailableInfo.isFactor(factor[i2], (Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, apiName);
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        return 0;
    }

    public void onCameraSet() {
    }

    public void onCameraReopened() {
    }

    public void onCameraRemoving() {
    }

    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
    }

    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
    }

    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
    }

    public void onGetTermRecorderParameters(MediaRecorder.Parameters params) {
    }

    public void onMediaRecorderSet() {
    }

    public void onMediaRecorderRemoving() {
    }
}
