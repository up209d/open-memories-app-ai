package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.ZoomBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class ExposureModeController extends AbstractController {
    public static final String ADVANCE_SPORTS = "advance-sports";
    public static final String ANTI_MOTION_BLUR = "anti_motion_blur";
    public static final String APERATURE_MODE = "Aperture";
    private static final String API_NAME = "setSceneMode";
    public static final String BACKGROUND_BLURR = "background-defocus";
    public static final String BACK_LIGHT = "back-light";
    public static final String BEACH = "beach";
    public static final String COMMON_NORMAL = "commonAuto-wo-sr";
    public static final String COMMON_UNDERWATER = "commonUnderwater";
    public static final String CONT_PRIORITY_AE = "cont-priority-ae";
    private static final int ENABLE_CHECK_MAIN_FEATURE_VERSION = 2;
    private static SparseArray<String> EXPOSURE_DIAL_DICTIONARY = null;
    public static final String EXPOSURE_MODE = "ExposureMode";
    private static HashMap<String, String> EXPOSURE_MODE_DICTIONARY = null;
    public static final String FIREWORKS = "fireworks";
    public static final String FLASH_OFF = "flash_off";
    public static final String FUNCTION_MENU = "ExposureModeFn";
    public static final String GOURMET = "gourmet";
    public static final String HAND_HELD_TWILIGHT = "hand_held_twilight";
    public static final String HIGH_SENSITIVITY = "high-sensitivity";
    public static final String HIGH_SPEED_SHUTTER = "high-speed-shutter";
    private static final String INH_ID = "INH_FACTOR_MOVREC_RUNNING";
    private static final String INH_MOVIE_A = "INH_FEATURE_CAM_VALUEID_EXP_MODE_IRIS";
    private static final String INH_MOVIE_M = "INH_FEATURE_CAM_VALUEID_EXP_MODE_MANUAL";
    private static final String INH_MOVIE_P = "INH_FEATURE_CAM_VALUEID_EXP_MODE_PROGRAM";
    private static final String INH_MOVIE_S = "INH_FEATURE_CAM_VALUEID_EXP_MODE_SHUTTER";
    public static final String INTELLIGENT_AUTO_MODE = "IntelligentAuto";
    public static final String ITEMID_SCENE_SELECTION_MODE = "ExposureMode_SceneSelectionMode";
    public static final String ITEMID_SCENE_SELECTION_MODE_MENU = "SceneSelectionMode";
    public static final String LANDSCAPE = "landscape";
    private static final String LOG_MSG_GETSCENEMODE = "getSceneMode = ";
    private static final String LOG_MSG_GETSUPPORTEDSCENEMODES = "getSupportedSceneModes = ";
    private static final String LOG_MSG_INVALID_SCANCODE = "Invalid scan code. scan code = ";
    private static final String LOG_MSG_MODE_DIAL_POS = "mode dial pos = ";
    private static final String LOG_MSG_NOTSUPPORTEDDIAL = "Not supported dial. scancode = ";
    private static final String LOG_MSG_SCENEMODE = "scene mode = ";
    private static final String LOG_MSG_SCENEMODE_UPDATED = "Scene mode updated. ";
    private static final String LOG_MSG_SETSCENEMODE = "setSceneMode = ";
    public static final String MACRO = "macro";
    public static final String MANUAL_MODE = "Manual";
    private static final int MOVIE = 1;
    public static final String MOVIE_APERATURE_MODE = "movieAperture";
    public static final String MOVIE_AUTO = "movieAuto";
    public static final String MOVIE_BEACH = "movieBeach";
    public static final String MOVIE_FIREWORKS = "movieFireworks";
    public static final String MOVIE_HIGH_SENSITIVITY = "movieHigh-sensitivity";
    public static final String MOVIE_LANDSCAPE = "movieLandscape";
    public static final String MOVIE_MANUAL_MODE = "movieManual";
    public static final String MOVIE_MODE = "movie";
    public static final String MOVIE_MODE_MENU = "movieModeMenu";
    public static final String MOVIE_MODE_SUB = "movieSub";
    public static final String MOVIE_NORMAL = "movieAuto-wo-sr";
    public static final String MOVIE_PORTRAIT = "moviePortrait";
    public static final String MOVIE_PROGRAM_AUTO_MODE = "movieProgramAuto";
    public static final String MOVIE_SHUTTER_MODE = "movieShutter";
    public static final String MOVIE_SNOW = "movieSnow";
    private static HashMap<String, String> MOVIE_SUB_MODE_DICTIONARY = null;
    public static final String MOVIE_TWILIGHT = "movieTwilight";
    public static final String MOVIE_UNDERWATER = "movieUnderwater";
    public static final String MR1 = "custom1";
    public static final String MR2 = "custom2";
    public static final String MR3 = "custom3";
    private static final String MSG_MOVIE_EXP_MODE_INHIBITION = "MovieExpModeInhibition.exec ";
    private static final String MSG_TO_OTHER = "toOther : ";
    private static final String MSG_TO_P = "toP";
    private static final int NODE_ROOT = -1;
    public static final String NORMAL = "auto-wo-sr";
    protected static HashMap<String, String> NOTSUPPORTED_DIAL_DICTIONARY = null;
    public static final String PET = "pet";
    private static final int PF_VER_SUPPORTS_UI_SCENE_LIST = 3;
    public static final String PICTURE_EFFECT = "picture-effect";
    public static final String PORTRAIT = "portrait";
    public static final String PROGRAM_AUTO_MODE = "ProgramAuto";
    public static final String SCENE_SELECTION_MODE = "SceneSelectionMode";
    private static HashMap<String, String> SCENE_SELECTION_MODE_DICTIONARY = null;
    public static final String SCENE_SELECTION_MODE_MENU = "SceneSelectionModeMenu";
    public static final String SHUTTER_MODE = "Shutter";
    public static final String SNOW = "snow";
    public static final String SOFT_SKIN = "soft-skin";
    public static final String SOFT_SNAP = " ";
    public static final String SPORTS = "sports";
    private static final int STILL = 0;
    private static HashMap<String, String> STILL_MOVIE_MODE_DICTIONARY = null;
    public static final String SUNSET = "sunset";
    public static final String SUPERIOR_AUTO_MODE = "SuperiorAuto";
    public static final String SWEEPSHOOTING_MODE = "SweepShooting";
    private static final String TAG = "ExposureModeController";
    public static final String TELE_CONT_PRIORITY_AE = "cont-priority-ae-crop";
    public static final String THREE_D_MODE = "three-d";
    public static final String TWILIGHT = "twilight";
    public static final String TWILIGHT_PORTRAIT = "twilight_portrait";
    private static SparseArray<String> UI_SCENE_MODE_DICTIONARY = null;
    public static final String UNDERWATER = "underwater";
    public static final String UNKNOWN_MODE_PREFIX = "unknown";
    private static ExposureModeController mInstance;
    private static List<Integer> mRootExposureModes;
    private static SparseArray<List<Integer>> mSubExposureModes;
    private static List<String> sSupportedExposureModes;
    private static List<String> sSupportedMovieExposureModes;
    private static List<String> sSupportedSceneSelectionModes;
    private CameraSetting mCameraSetting;
    protected CameraEx.OpenCallback mReopenCallback;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static final String myName = ExposureModeController.class.getSimpleName();
    private static boolean isInitialModeRemembered = false;
    private static final String[] tags = {CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.MOVIE_REC_STOP};
    private MovieExpModeInhibition mMovieExpModeInh = null;
    private NotificationListener mListener = new MyControllerListener();

    private static void initDictionary() {
        EXPOSURE_MODE_DICTIONARY = new HashMap<>();
        EXPOSURE_MODE_DICTIONARY.put(PROGRAM_AUTO_MODE, "program-auto");
        EXPOSURE_MODE_DICTIONARY.put("Aperture", "aperture-priority");
        EXPOSURE_MODE_DICTIONARY.put("Shutter", "shutter-speed");
        EXPOSURE_MODE_DICTIONARY.put(MANUAL_MODE, "manual-exposure");
        EXPOSURE_MODE_DICTIONARY.put(INTELLIGENT_AUTO_MODE, "auto");
        EXPOSURE_MODE_DICTIONARY.put("SceneSelectionMode", null);
        if (Environment.isMovieAPISupported()) {
            EXPOSURE_MODE_DICTIONARY.put("movie", null);
        }
        if (isUiMovieMainFeature()) {
            EXPOSURE_MODE_DICTIONARY.remove("SceneSelectionMode");
            EXPOSURE_MODE_DICTIONARY.remove("movie");
            EXPOSURE_MODE_DICTIONARY.put(MOVIE_PROGRAM_AUTO_MODE, "program-auto");
            EXPOSURE_MODE_DICTIONARY.put(MOVIE_APERATURE_MODE, "aperture-priority");
            EXPOSURE_MODE_DICTIONARY.put(MOVIE_SHUTTER_MODE, "shutter-speed");
            EXPOSURE_MODE_DICTIONARY.put(MOVIE_MANUAL_MODE, "manual-exposure");
            EXPOSURE_MODE_DICTIONARY.put(MOVIE_AUTO, "auto");
        }
        if (isSuperiorAutoSupported()) {
            EXPOSURE_MODE_DICTIONARY.put(SUPERIOR_AUTO_MODE, "superior-auto");
        }
        if (isCommonUnderwaterSupported()) {
            EXPOSURE_MODE_DICTIONARY.put(COMMON_UNDERWATER, UNDERWATER);
        }
        if (isCommonNormalSupported()) {
            EXPOSURE_MODE_DICTIONARY.put(COMMON_NORMAL, NORMAL);
        }
        SCENE_SELECTION_MODE_DICTIONARY = new HashMap<>();
        SCENE_SELECTION_MODE_DICTIONARY.put("portrait", "portrait");
        SCENE_SELECTION_MODE_DICTIONARY.put("landscape", "landscape");
        SCENE_SELECTION_MODE_DICTIONARY.put(SPORTS, SPORTS);
        SCENE_SELECTION_MODE_DICTIONARY.put("sunset", "sunset");
        SCENE_SELECTION_MODE_DICTIONARY.put("twilight", CreativeStyleController.NIGHT);
        SCENE_SELECTION_MODE_DICTIONARY.put(TWILIGHT_PORTRAIT, "night-portrait");
        SCENE_SELECTION_MODE_DICTIONARY.put(MACRO, MACRO);
        SCENE_SELECTION_MODE_DICTIONARY.put(HAND_HELD_TWILIGHT, "hand-held-twilight");
        SCENE_SELECTION_MODE_DICTIONARY.put(ANTI_MOTION_BLUR, "anti-motion-blur");
        SCENE_SELECTION_MODE_DICTIONARY.put(PET, PET);
        SCENE_SELECTION_MODE_DICTIONARY.put(GOURMET, GOURMET);
        SCENE_SELECTION_MODE_DICTIONARY.put(FIREWORKS, FIREWORKS);
        SCENE_SELECTION_MODE_DICTIONARY.put(HIGH_SENSITIVITY, HIGH_SENSITIVITY);
        SCENE_SELECTION_MODE_DICTIONARY.put(BEACH, BEACH);
        SCENE_SELECTION_MODE_DICTIONARY.put(SNOW, SNOW);
        SCENE_SELECTION_MODE_DICTIONARY.put(ADVANCE_SPORTS, ADVANCE_SPORTS);
        SCENE_SELECTION_MODE_DICTIONARY.put(BACK_LIGHT, BACK_LIGHT);
        SCENE_SELECTION_MODE_DICTIONARY.put(SOFT_SKIN, SOFT_SKIN);
        EXPOSURE_DIAL_DICTIONARY = new SparseArray<>();
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_PANORAMA, SWEEPSHOOTING_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM, PROGRAM_AUTO_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_AEA, "Aperture");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_AES, "Shutter");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, MANUAL_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO, SUPERIOR_AUTO_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_AUTO, INTELLIGENT_AUTO_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_SCN, "SceneSelectionMode");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_3D, THREE_D_MODE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM, "custom1");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM2, "custom2");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM3, "custom3");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_CONT_PRIO_AE, TELE_CONT_PRIORITY_AE);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_FLASH_OFF, FLASH_OFF);
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_MOVIE, "movie");
        EXPOSURE_DIAL_DICTIONARY.append(AppRoot.USER_KEYCODE.MODE_DIAL_PE, "picture-effect");
        MOVIE_SUB_MODE_DICTIONARY = new HashMap<>();
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_PROGRAM_AUTO_MODE, "program-auto");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_APERATURE_MODE, "aperture-priority");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_SHUTTER_MODE, "shutter-speed");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_MANUAL_MODE, "manual-exposure");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_AUTO, "auto");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_PORTRAIT, "portrait");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_LANDSCAPE, "landscape");
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_TWILIGHT, CreativeStyleController.NIGHT);
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_BEACH, BEACH);
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_SNOW, SNOW);
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_FIREWORKS, FIREWORKS);
        MOVIE_SUB_MODE_DICTIONARY.put(MOVIE_HIGH_SENSITIVITY, HIGH_SENSITIVITY);
        NOTSUPPORTED_DIAL_DICTIONARY = new HashMap<>();
        NOTSUPPORTED_DIAL_DICTIONARY.put(SUPERIOR_AUTO_MODE, "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put(SWEEPSHOOTING_MODE, "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put(THREE_D_MODE, "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put("custom1", "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put("custom2", "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put("custom3", "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put(TELE_CONT_PRIORITY_AE, "auto");
        NOTSUPPORTED_DIAL_DICTIONARY.put(FLASH_OFF, "auto");
        if (!Environment.isMovieAPISupported() || !ExecutorCreator.getInstance().isMovieModeSupported()) {
            NOTSUPPORTED_DIAL_DICTIONARY.put("movie", "auto");
        }
        NOTSUPPORTED_DIAL_DICTIONARY.put("picture-effect", "auto");
        STILL_MOVIE_MODE_DICTIONARY = new HashMap<>();
        STILL_MOVIE_MODE_DICTIONARY.put(PROGRAM_AUTO_MODE, MOVIE_PROGRAM_AUTO_MODE);
        STILL_MOVIE_MODE_DICTIONARY.put("Aperture", MOVIE_APERATURE_MODE);
        STILL_MOVIE_MODE_DICTIONARY.put("Shutter", MOVIE_SHUTTER_MODE);
        STILL_MOVIE_MODE_DICTIONARY.put(MANUAL_MODE, MOVIE_MANUAL_MODE);
        STILL_MOVIE_MODE_DICTIONARY.put(INTELLIGENT_AUTO_MODE, MOVIE_AUTO);
        STILL_MOVIE_MODE_DICTIONARY.put("portrait", MOVIE_PORTRAIT);
        STILL_MOVIE_MODE_DICTIONARY.put("landscape", MOVIE_LANDSCAPE);
        STILL_MOVIE_MODE_DICTIONARY.put("twilight", MOVIE_TWILIGHT);
        STILL_MOVIE_MODE_DICTIONARY.put(BEACH, MOVIE_BEACH);
        STILL_MOVIE_MODE_DICTIONARY.put(SNOW, MOVIE_SNOW);
        STILL_MOVIE_MODE_DICTIONARY.put(FIREWORKS, MOVIE_FIREWORKS);
        STILL_MOVIE_MODE_DICTIONARY.put(HIGH_SENSITIVITY, MOVIE_HIGH_SENSITIVITY);
        STILL_MOVIE_MODE_DICTIONARY.put(UNDERWATER, MOVIE_UNDERWATER);
        STILL_MOVIE_MODE_DICTIONARY.put(NORMAL, MOVIE_NORMAL);
        UI_SCENE_MODE_DICTIONARY = new SparseArray<>();
        UI_SCENE_MODE_DICTIONARY.put(2116, SUPERIOR_AUTO_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2049, INTELLIGENT_AUTO_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2050, PROGRAM_AUTO_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2051, "Aperture");
        UI_SCENE_MODE_DICTIONARY.put(2052, "Shutter");
        UI_SCENE_MODE_DICTIONARY.put(2053, MANUAL_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2054, "SceneSelectionMode");
        UI_SCENE_MODE_DICTIONARY.put(2055, "portrait");
        UI_SCENE_MODE_DICTIONARY.put(2056, "landscape");
        UI_SCENE_MODE_DICTIONARY.put(2057, MACRO);
        UI_SCENE_MODE_DICTIONARY.put(2058, SPORTS);
        UI_SCENE_MODE_DICTIONARY.put(2059, "sunset");
        UI_SCENE_MODE_DICTIONARY.put(2060, TWILIGHT_PORTRAIT);
        UI_SCENE_MODE_DICTIONARY.put(2061, "twilight");
        UI_SCENE_MODE_DICTIONARY.put(2062, HAND_HELD_TWILIGHT);
        UI_SCENE_MODE_DICTIONARY.put(2063, ANTI_MOTION_BLUR);
        UI_SCENE_MODE_DICTIONARY.put(2064, PET);
        UI_SCENE_MODE_DICTIONARY.put(2065, GOURMET);
        UI_SCENE_MODE_DICTIONARY.put(2066, BEACH);
        UI_SCENE_MODE_DICTIONARY.put(2067, SNOW);
        UI_SCENE_MODE_DICTIONARY.put(2068, FIREWORKS);
        UI_SCENE_MODE_DICTIONARY.put(2069, SOFT_SKIN);
        UI_SCENE_MODE_DICTIONARY.put(2070, HIGH_SENSITIVITY);
        UI_SCENE_MODE_DICTIONARY.put(2071, ADVANCE_SPORTS);
        UI_SCENE_MODE_DICTIONARY.put(2072, BACKGROUND_BLURR);
        if (isCommonUnderwaterSupported()) {
            UI_SCENE_MODE_DICTIONARY.put(2073, COMMON_UNDERWATER);
        }
        if (isCommonNormalSupported()) {
            UI_SCENE_MODE_DICTIONARY.put(2117, COMMON_NORMAL);
        }
        UI_SCENE_MODE_DICTIONARY.put(2078, HIGH_SPEED_SHUTTER);
        UI_SCENE_MODE_DICTIONARY.put(2082, "picture-effect");
        UI_SCENE_MODE_DICTIONARY.put(2083, CONT_PRIORITY_AE);
        UI_SCENE_MODE_DICTIONARY.put(2085, "movie");
        UI_SCENE_MODE_DICTIONARY.put(2086, MOVIE_PROGRAM_AUTO_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2087, MOVIE_APERATURE_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2088, MOVIE_SHUTTER_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2089, MOVIE_MANUAL_MODE);
        UI_SCENE_MODE_DICTIONARY.put(2090, MOVIE_AUTO);
        UI_SCENE_MODE_DICTIONARY.put(2092, MOVIE_PORTRAIT);
        UI_SCENE_MODE_DICTIONARY.put(2093, MOVIE_LANDSCAPE);
        UI_SCENE_MODE_DICTIONARY.put(2098, MOVIE_TWILIGHT);
        UI_SCENE_MODE_DICTIONARY.put(2103, MOVIE_BEACH);
        UI_SCENE_MODE_DICTIONARY.put(2104, MOVIE_SNOW);
        UI_SCENE_MODE_DICTIONARY.put(2105, MOVIE_FIREWORKS);
        UI_SCENE_MODE_DICTIONARY.put(2107, MOVIE_HIGH_SENSITIVITY);
    }

    public static final String getName() {
        return myName;
    }

    public static ExposureModeController getInstance() {
        if (mInstance == null) {
            new ExposureModeController();
        }
        return mInstance;
    }

    private static void setController(ExposureModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ExposureModeController() {
        this.mCameraSetting = null;
        this.mReopenCallback = null;
        initDictionary();
        this.mCameraSetting = CameraSetting.getInstance();
        setController(this);
        if (Environment.isMovieAPISupported()) {
            this.mReopenCallback = new ReopenCallback();
        }
    }

    protected static void createSupportedList(List<Integer> supportedModeDials) {
        if (sSupportedExposureModes == null || sSupportedSceneSelectionModes == null || (sSupportedMovieExposureModes == null && Environment.isMovieAPISupported())) {
            CameraSetting cameraSetting = CameraSetting.getInstance();
            List<Pair<Camera.Parameters, CameraEx.ParametersModifier>> supporteds = new ArrayList<>();
            supporteds.add(0, cameraSetting.getSupportedParameters(1));
            Pair<Camera.Parameters, CameraEx.ParametersModifier> moviesupported = null;
            if (Environment.isMovieAPISupported()) {
                moviesupported = cameraSetting.getSupportedParameters(2);
            }
            supporteds.add(1, moviesupported);
            if (sSupportedExposureModes == null) {
                if (supportedModeDials == null) {
                    sSupportedExposureModes = createSupportedValueArray(supporteds, EXPOSURE_MODE);
                } else {
                    sSupportedExposureModes = createSupportedSceneModesArrayFromModeDial(supportedModeDials);
                }
            }
            if (sSupportedSceneSelectionModes == null) {
                sSupportedSceneSelectionModes = createSupportedValueArray(supporteds, "SceneSelectionMode");
            }
            if (sSupportedMovieExposureModes == null && Environment.isMovieAPISupported()) {
                sSupportedMovieExposureModes = createSupportedValueArray(supporteds, "movie");
            }
        }
    }

    /* loaded from: classes.dex */
    protected class ReopenCallback implements CameraEx.OpenCallback {
        protected ReopenCallback() {
        }

        public void onReopened(CameraEx cameraEx) {
            Log.i(ExposureModeController.TAG, "nothing to do.");
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        String mode = null;
        boolean isMovieInhibitted = false;
        if (EXPOSURE_MODE.equals(tag)) {
            ExecutorCreator ec = ExecutorCreator.getInstance();
            if (Environment.isMovieAPISupported() && "movie".equals(value)) {
                if (2 != ExecutorCreator.getInstance().getRecordingMode()) {
                    ec.setRecordingMode(2, this.mReopenCallback);
                }
            } else if (!COMMON_UNDERWATER.equals(value) && !COMMON_NORMAL.equals(value) && 1 != ExecutorCreator.getInstance().getRecordingMode()) {
                ec.setRecordingMode(1, this.mReopenCallback);
            }
            if ("SceneSelectionMode".equals(value)) {
                mode = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "");
                if ("".equals(mode)) {
                    String appMode = getDefaulSceneSelectionMode();
                    mode = SCENE_SELECTION_MODE_DICTIONARY.get(appMode);
                }
            } else if ("movie".equals(value)) {
                if (!isUiMovieMainFeature()) {
                    String movieBkup = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_MODE_VALUE, "");
                    if ("".equals(movieBkup)) {
                        movieBkup = getDefaultMovieExposureMode();
                    }
                    if (true == isPhaseDetectAF() && (MOVIE_APERATURE_MODE.equals(movieBkup) || MOVIE_MANUAL_MODE.equals(movieBkup) || MOVIE_SHUTTER_MODE.equals(movieBkup))) {
                        isMovieInhibitted = true;
                    }
                    mode = MOVIE_SUB_MODE_DICTIONARY.get(movieBkup);
                }
            } else {
                mode = EXPOSURE_MODE_DICTIONARY.get(value);
                if (mode == null) {
                    mode = NOTSUPPORTED_DIAL_DICTIONARY.get(value);
                }
            }
        } else if ("SceneSelectionMode".equals(tag) || SCENE_SELECTION_MODE_MENU.equals(tag)) {
            mode = SCENE_SELECTION_MODE_DICTIONARY.get(value);
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, mode);
        } else if ("movie".equals(tag) || MOVIE_MODE_MENU.equals(tag)) {
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_MODE_VALUE, value);
            if (true == isPhaseDetectAF() && (MOVIE_APERATURE_MODE.equals(value) || MOVIE_MANUAL_MODE.equals(value) || MOVIE_SHUTTER_MODE.equals(value))) {
                isMovieInhibitted = true;
            }
            mode = MOVIE_SUB_MODE_DICTIONARY.get(value);
        }
        if (mode != null) {
            BackUpUtil.getInstance().removePreference(BaseBackUpKey.ID_DIAL_AVTV_TOGGLE);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((Camera.Parameters) params.first).setSceneMode(mode);
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_SETSCENEMODE).append(mode);
            Log.i(TAG, builder.toString());
            CameraSetting.getInstance().setParameters(params);
            if (!mode.equals("program-auto") && !ExecutorCreator.getInstance().isRecordingModeChanging()) {
                CameraSetting.getInstance().resetProgramLine();
            }
            if (isMovieInhibitted) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = this.mCameraSetting.getEmptyParameters();
                ((Camera.Parameters) params2.first).setSceneMode("program-auto");
                CameraSetting.getInstance().setParametersDirect(params2);
            }
        }
    }

    public static String getRawSceneMode(String tag, String value) {
        if (EXPOSURE_MODE.equals(tag)) {
            String mode = EXPOSURE_MODE_DICTIONARY.get(value);
            String mode2 = mode;
            if (mode2 == null) {
                String mode3 = NOTSUPPORTED_DIAL_DICTIONARY.get(value);
                return mode3;
            }
            return mode2;
        }
        if ("SceneSelectionMode".equals(tag)) {
            String mode4 = SCENE_SELECTION_MODE_DICTIONARY.get(value);
            return mode4;
        }
        if (!"movie".equals(tag)) {
            return null;
        }
        String mode5 = MOVIE_SUB_MODE_DICTIONARY.get(value);
        return mode5;
    }

    private static String getDefaulSceneSelectionMode() {
        if (3 <= Environment.getVersionPfAPI()) {
            if (sSupportedSceneSelectionModes == null || sSupportedSceneSelectionModes.size() <= 0) {
                return null;
            }
            String mode = sSupportedSceneSelectionModes.get(0);
            return mode;
        }
        if (sSupportedSceneSelectionModes == null || sSupportedSceneSelectionModes.size() <= 0) {
            return null;
        }
        if (sSupportedSceneSelectionModes.contains("portrait")) {
            return "portrait";
        }
        String mode2 = sSupportedSceneSelectionModes.get(0);
        return mode2;
    }

    public static String getDefaultMovieExposureMode() {
        if (!Environment.isMovieAPISupported()) {
            return null;
        }
        if (3 <= Environment.getVersionPfAPI()) {
            if (sSupportedMovieExposureModes == null || sSupportedMovieExposureModes.size() <= 0) {
                return null;
            }
            String mode = sSupportedMovieExposureModes.get(0);
            return mode;
        }
        if (sSupportedMovieExposureModes == null || sSupportedMovieExposureModes.size() <= 0) {
            return null;
        }
        if (sSupportedMovieExposureModes.contains(MOVIE_PROGRAM_AUTO_MODE)) {
            return MOVIE_PROGRAM_AUTO_MODE;
        }
        if (sSupportedMovieExposureModes.contains(MOVIE_AUTO)) {
            return MOVIE_AUTO;
        }
        if (sSupportedMovieExposureModes.contains(MOVIE_PORTRAIT)) {
            return MOVIE_PORTRAIT;
        }
        String mode2 = sSupportedMovieExposureModes.get(0);
        return mode2;
    }

    private String getValueDirect() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String mode = ((Camera.Parameters) params.first).getSceneMode();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETSCENEMODE).append(mode);
        Log.i(TAG, builder.toString());
        String returnValue = retrieveKey(EXPOSURE_MODE_DICTIONARY, mode);
        if (returnValue == null) {
            return retrieveKey(SCENE_SELECTION_MODE_DICTIONARY, mode);
        }
        return returnValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String retrieveKey(HashMap<String, String> dictionary, String value) {
        List<String> keyList = new ArrayList<>();
        String key = null;
        Set<Map.Entry<String, String>> itemSet = dictionary.entrySet();
        for (Map.Entry<String, String> item : itemSet) {
            String mode = item.getValue();
            if (mode != null && mode.equals(value)) {
                keyList.add(item.getKey());
            }
        }
        if (!keyList.isEmpty()) {
            String key2 = keyList.get(0);
            key = key2;
        }
        if (keyList.size() > 1) {
            int recMode = 1;
            if (Environment.isMovieAPISupported()) {
                recMode = ExecutorCreator.getInstance().getRecordingMode();
            }
            for (int i = 0; i < keyList.size(); i++) {
                String key3 = keyList.get(i);
                key = key3;
                if (recMode == 2 || recMode == 8) {
                    if (key.startsWith("movie")) {
                        break;
                    }
                } else {
                    if (!key.startsWith("movie")) {
                        break;
                    }
                }
            }
        }
        return key;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        int recordingMode;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String returnValue = null;
        String mode = ((Camera.Parameters) params.first).getSceneMode();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETSCENEMODE).append(mode);
        Log.i(TAG, builder.toString());
        if (tag == null) {
            returnValue = getValueDirect();
            if (!COMMON_UNDERWATER.equals(returnValue) && !COMMON_NORMAL.equals(returnValue) && (((recordingMode = ExecutorCreator.getInstance().getRecordingMode()) == 2 || recordingMode == 8) && !Environment.isNewBizDeviceActionCam())) {
                returnValue = STILL_MOVIE_MODE_DICTIONARY.get(returnValue);
            }
        } else if (tag.equals("SceneSelectionMode") || SCENE_SELECTION_MODE_MENU.equals(tag)) {
            if (1 == CameraSetting.getInstance().getCurrentMode() && (returnValue = retrieveKey(SCENE_SELECTION_MODE_DICTIONARY, mode)) == null && "SceneSelectionMode".equals(tag)) {
                String mode2 = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "");
                if (!"".equals(mode2)) {
                    returnValue = retrieveKey(SCENE_SELECTION_MODE_DICTIONARY, mode2);
                }
                if (returnValue == null) {
                    returnValue = getDefaulSceneSelectionMode();
                }
            }
        } else if (tag.equals(EXPOSURE_MODE)) {
            if (!isUiMovieMainFeature() && !Environment.isNewBizDeviceActionCam() && 2 == ExecutorCreator.getInstance().getRecordingMode()) {
                return "movie";
            }
            returnValue = retrieveKey(EXPOSURE_MODE_DICTIONARY, mode);
            if (returnValue == null && SCENE_SELECTION_MODE_DICTIONARY.containsValue(mode)) {
                returnValue = "SceneSelectionMode";
            }
        } else if (("movie".equals(tag) || MOVIE_MODE_MENU.equals(tag)) && ((2 == ExecutorCreator.getInstance().getRecordingMode() || 8 == ExecutorCreator.getInstance().getRecordingMode()) && (returnValue = retrieveKey(MOVIE_SUB_MODE_DICTIONARY, mode)) == null)) {
            returnValue = getDefaultMovieExposureMode();
        }
        return returnValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        int index;
        List<String> supporteds = null;
        if (tag.equals(EXPOSURE_MODE)) {
            supporteds = sSupportedExposureModes;
            if (isUiMovieMainFeature() && (index = supporteds.indexOf("movie")) != -1) {
                supporteds.remove(index);
            }
        } else if (tag.equals("SceneSelectionMode")) {
            supporteds = sSupportedSceneSelectionModes;
        } else if ("movie".equals(tag)) {
            supporteds = sSupportedMovieExposureModes;
        } else if (SCENE_SELECTION_MODE_MENU.equals(tag)) {
            supporteds = ModeDialDetector.hasModeDial() ? sSupportedSceneSelectionModes : null;
        } else if (MOVIE_MODE_MENU.equals(tag)) {
            supporteds = ModeDialDetector.hasModeDial() ? sSupportedMovieExposureModes : null;
        }
        if (supporteds == null || supporteds.isEmpty()) {
            return null;
        }
        return supporteds;
    }

    private static List<String> createSupportedValueArray(List<Pair<Camera.Parameters, CameraEx.ParametersModifier>> params, String tag) {
        if (3 > Environment.getVersionPfAPI()) {
            return createSupportedValueArrayObsolete("movie".equals(tag) ? params.get(1) : params.get(0), tag);
        }
        List<String> list = new ArrayList<>();
        List<String> supportedSceneModes = null;
        int node = -1;
        HashMap<String, String> dictionary = null;
        if (EXPOSURE_MODE.equals(tag)) {
            supportedSceneModes = ((Camera.Parameters) params.get(0).first).getSupportedSceneModes();
            if (isUiMovieMainFeature() && params.get(1) != null) {
                supportedSceneModes.addAll(((Camera.Parameters) params.get(1).first).getSupportedSceneModes());
            }
            node = -1;
            dictionary = EXPOSURE_MODE_DICTIONARY;
        } else if ("SceneSelectionMode".equals(tag)) {
            supportedSceneModes = ((Camera.Parameters) params.get(0).first).getSupportedSceneModes();
            node = 2054;
            dictionary = SCENE_SELECTION_MODE_DICTIONARY;
        } else if ("movie".equals(tag)) {
            supportedSceneModes = ((Camera.Parameters) params.get(1).first).getSupportedSceneModes();
            node = 2085;
            dictionary = MOVIE_SUB_MODE_DICTIONARY;
        }
        if (supportedSceneModes != null && dictionary != null) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDSCENEMODES).append(supportedSceneModes);
            Log.i(TAG, builder.toString());
            List<Integer> sceneList = getVirtualDialList(node);
            if (sceneList != null) {
                for (Integer scene : sceneList) {
                    String mode = UI_SCENE_MODE_DICTIONARY.get(scene.intValue());
                    if (mode != null) {
                        if ("SceneSelectionMode".equals(mode)) {
                            list.add(mode);
                        } else if ("movie".equals(mode)) {
                            if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
                                list.add(mode);
                            }
                        } else if (supportedSceneModes.contains(dictionary.get(mode))) {
                            list.add(mode);
                        }
                    }
                }
                return list;
            }
            return list;
        }
        return list;
    }

    private static List<String> createSupportedValueArrayObsolete(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, String tag) {
        List<String> list = new ArrayList<>();
        List<String> supportedSceneModes = ((Camera.Parameters) params.first).getSupportedSceneModes();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDSCENEMODES).append(supportedSceneModes);
        Log.i(TAG, builder.toString());
        if (supportedSceneModes != null) {
            if (tag.equals(EXPOSURE_MODE)) {
                Set<Map.Entry<String, String>> itemSet = EXPOSURE_MODE_DICTIONARY.entrySet();
                for (Map.Entry<String, String> item : itemSet) {
                    if (supportedSceneModes.contains(item.getValue()) && !list.contains(item.getKey())) {
                        list.add(item.getKey());
                    }
                    if (!list.contains("SceneSelectionMode")) {
                        list.add("SceneSelectionMode");
                    }
                }
                if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported() && !list.contains("movie")) {
                    list.add("movie");
                }
            } else if (tag.equals("SceneSelectionMode")) {
                Set<Map.Entry<String, String>> itemSet2 = SCENE_SELECTION_MODE_DICTIONARY.entrySet();
                for (Map.Entry<String, String> item2 : itemSet2) {
                    if (supportedSceneModes.contains(item2.getValue()) && !list.contains(item2.getKey())) {
                        list.add(item2.getKey());
                    }
                }
            } else if (tag.equals("movie")) {
                Set<Map.Entry<String, String>> itemSet3 = MOVIE_SUB_MODE_DICTIONARY.entrySet();
                for (Map.Entry<String, String> item3 : itemSet3) {
                    if (supportedSceneModes.contains(item3.getValue()) && !list.contains(item3.getKey())) {
                        list.add(item3.getKey());
                    }
                }
            }
        }
        return list;
    }

    private static List<String> createSupportedSceneModesArrayFromModeDial(List<Integer> supportedModeDials) {
        List<String> list = new ArrayList<>();
        if (supportedModeDials != null) {
            int length = supportedModeDials.size();
            for (int i = 0; i < length; i++) {
                list.add(scancode2Value(supportedModeDials.get(i).intValue()));
            }
        }
        return list;
    }

    public static List<Integer> getVirtualDialList(int parent) {
        if (mRootExposureModes == null) {
            mRootExposureModes = new ArrayList();
            mSubExposureModes = new SparseArray<>();
            int[] array = ScalarProperties.getIntArray("ui.scene.mode.list");
            if (array != null) {
                int[] position = {0};
                mRootExposureModes = getChildDialList(array, position);
            }
        }
        return -1 == parent ? mRootExposureModes : mSubExposureModes.get(parent);
    }

    private static List<Integer> getChildDialList(int[] array, int[] position) {
        List<Integer> children = new ArrayList<>();
        int[] childPos = new int[1];
        int i = position[0];
        int c = array.length;
        while (true) {
            if (i >= c) {
                break;
            }
            int scene = array[i];
            if (2302 == scene) {
                childPos[0] = i + 1;
                int parent = array[i - 1];
                mSubExposureModes.put(parent, getChildDialList(array, childPos));
                i = childPos[0];
            } else {
                if (2303 == scene) {
                    position[0] = i;
                    break;
                }
                children.add(Integer.valueOf(scene));
            }
            i++;
        }
        return children;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (FUNCTION_MENU.equals(tag) && isMovRecRunning()) {
            return null;
        }
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            for (String v : supporteds) {
                if (isModeAvailable(v)) {
                    availables.add(v);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    public boolean isModeAvailable(String value) {
        if (value == null) {
            boolean ret = AvailableInfo.isAvailable(API_NAME, null);
            return ret;
        }
        if (value.equals("SceneSelectionMode")) {
            return true;
        }
        if ("movie".equals(value)) {
            if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
                return true;
            }
            return false;
        }
        String mode = EXPOSURE_MODE_DICTIONARY.get(value);
        if (mode == null && (mode = SCENE_SELECTION_MODE_DICTIONARY.get(value)) == null) {
            mode = MOVIE_SUB_MODE_DICTIONARY.get(value);
        }
        if (mode == null) {
            return false;
        }
        boolean movieInh = false;
        if (MOVIE_PROGRAM_AUTO_MODE.equals(value)) {
            movieInh = !ExecutorCreator.getInstance().isMovieModeSupported() || AvailableInfo.isFactor(INH_MOVIE_P);
        } else if (MOVIE_APERATURE_MODE.equals(value)) {
            movieInh = !ExecutorCreator.getInstance().isMovieModeSupported() || AvailableInfo.isFactor(INH_MOVIE_A) || isPhaseDetectAF();
        } else if (MOVIE_SHUTTER_MODE.equals(value)) {
            movieInh = !ExecutorCreator.getInstance().isMovieModeSupported() || AvailableInfo.isFactor(INH_MOVIE_S) || isPhaseDetectAF();
        } else if (MOVIE_MANUAL_MODE.equals(value)) {
            movieInh = !ExecutorCreator.getInstance().isMovieModeSupported() || AvailableInfo.isFactor(INH_MOVIE_M) || isPhaseDetectAF();
        }
        if (!movieInh) {
            return true;
        }
        return false;
    }

    protected boolean isPhaseDetectAF() {
        if (FocusAreaController.getInstance().getSensorType() == 1) {
            String focusMode = FocusModeController.getInstance().getValue();
            if ("auto".equals(focusMode) || "af-c".equals(focusMode) || "af-s".equals(focusMode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isScnMode() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        if (params == null) {
            Log.w(TAG, "isScnMode cannot getParameters");
            return false;
        }
        String mode = ((Camera.Parameters) params.first).getSceneMode();
        return isSceneSelection(mode);
    }

    public static boolean isSceneSelection(String raw) {
        if (1 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        String mode = retrieveKey(SCENE_SELECTION_MODE_DICTIONARY, raw);
        return mode != null && sSupportedSceneSelectionModes.contains(mode);
    }

    public static boolean isMovieSubExposureMode(String raw) {
        if (2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        String mode = retrieveKey(MOVIE_SUB_MODE_DICTIONARY, raw);
        return mode != null;
    }

    public static String scancode2Value(int scancode) {
        String value = EXPOSURE_DIAL_DICTIONARY.get(scancode);
        if (value == null) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_NOTSUPPORTEDDIAL).append(scancode);
            Log.e(TAG, builder.toString());
            return "unknown";
        }
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = AvailableInfo.isAvailable(API_NAME, null);
        if (FUNCTION_MENU.equals(tag) && ret) {
            if (FUNCTION_MENU.equals(tag) && isMovRecRunning()) {
                return false;
            }
            if (ModeDialDetector.hasModeDial()) {
                String expMode = getValue(null);
                if ("SceneSelectionMode".equals(expMode) || SCENE_SELECTION_MODE_DICTIONARY.containsKey(expMode)) {
                    ret = true;
                } else if ("movie".equals(expMode) || MOVIE_MODE_MENU.equals(expMode) || MOVIE_SUB_MODE_DICTIONARY.containsKey(expMode)) {
                    ret = Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported();
                } else if (2 == ExecutorCreator.getInstance().getRecordingMode() || 8 == ExecutorCreator.getInstance().getRecordingMode()) {
                    String pfMode = EXPOSURE_MODE_DICTIONARY.get(expMode);
                    if (pfMode == null) {
                        pfMode = SCENE_SELECTION_MODE_DICTIONARY.get(expMode);
                    }
                    if (MOVIE_SUB_MODE_DICTIONARY.containsValue(pfMode)) {
                        ret = true;
                    }
                } else {
                    ret = false;
                }
            }
        } else if ("SceneSelectionMode".equals(tag) || SCENE_SELECTION_MODE_MENU.equals(tag)) {
            if (!isScnMode()) {
                ret = false;
            }
        } else if (MOVIE_MODE_MENU.equals(tag)) {
            AvailableInfo.update();
            ret = !AvailableInfo.isInhibition("INH_FEATURE_VIEW_SET_MENU_MOVIE");
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        boolean b = isUnavailableAPISceneFactor(API_NAME, null);
        return b;
    }

    public boolean isValidDialPosition() {
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = scancode2Value(code);
            boolean isValid = isValidValue(expMode);
            Log.i(TAG, "isValidDialPosition: " + Boolean.toString(isValid));
            return isValid;
        }
        return false;
    }

    public boolean isValidValue(String expMode) {
        List<String> list;
        List<String> list2;
        boolean isValid = false;
        List<String> list3 = getAvailableValue(EXPOSURE_MODE);
        if (list3 != null) {
            isValid = list3.contains(expMode);
        }
        if (!isValid && (list2 = getAvailableValue("SceneSelectionMode")) != null) {
            isValid = list2.contains(expMode);
        }
        if (!isValid && ExecutorCreator.getInstance().isMovieModeSupported() && (list = getAvailableValue("movie")) != null) {
            boolean isValid2 = list.contains(expMode);
            return isValid2;
        }
        return isValid;
    }

    public int getCautionId() {
        return Info.CAUTION_ID_DLAPP_PASM_IAUTO_SCN_CLOSEKEY;
    }

    public static boolean isUiMovieMainFeature() {
        if (Environment.getVersionPfAPI() < 2 || ScalarProperties.getInt(ZoomBar.PROP_UI_MAIN_FEATURE) != 1) {
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean isMovieMainFeature() {
        return isUiMovieMainFeature();
    }

    public static void onBoot() {
        BootFactor factor = BootFactor.get();
        int bootfactor = factor.bootFactor;
        if (bootfactor == 0) {
            isInitialModeRemembered = false;
        }
    }

    public void onGetSceneModeOfModeDialPosition(Camera.Parameters params) {
        Log.i(TAG, "scenemode change start");
        CameraSetting cameraSetting = CameraSetting.getInstance();
        int code = ModeDialDetector.getModeDialPosition();
        StringBuilder builder = sStringBuilder.get();
        if (-1 != code) {
            String pfMode = null;
            String appMode = scancode2Value(code);
            if ("SceneSelectionMode".equals(appMode)) {
                pfMode = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "");
                if ("".equals(pfMode)) {
                    if (!isInitialModeRemembered) {
                        pfMode = ((Camera.Parameters) cameraSetting.getParameters().first).getSceneMode();
                    }
                    Log.i(TAG, "get scenemode " + pfMode);
                    if (!isSceneSelection(pfMode)) {
                        String pfMode2 = SCENE_SELECTION_MODE_DICTIONARY.get(getDefaulSceneSelectionMode());
                        pfMode = pfMode2;
                    } else {
                        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, pfMode);
                    }
                }
            } else if (Environment.isMovieAPISupported() && "movie".equals(appMode)) {
                String appMode2 = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_MODE_VALUE, "");
                if (!"".equals(appMode2)) {
                    pfMode = getRawSceneMode("movie", appMode2);
                } else {
                    if (!isInitialModeRemembered) {
                        pfMode = ((Camera.Parameters) cameraSetting.getParameters().first).getSceneMode();
                    }
                    Log.i(TAG, "get scenemode " + pfMode);
                    String appMode3 = retrieveKey(MOVIE_SUB_MODE_DICTIONARY, pfMode);
                    if (!isMovieSubExposureMode(pfMode) || !sSupportedMovieExposureModes.contains(appMode3)) {
                        pfMode = getRawSceneMode("movie", getDefaultMovieExposureMode());
                    } else if (appMode3 != null) {
                        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_MODE_VALUE, appMode3);
                    }
                }
            } else {
                pfMode = getRawSceneMode(EXPOSURE_MODE, appMode);
                if (pfMode == null) {
                    builder.replace(0, builder.length(), LOG_MSG_INVALID_SCANCODE).append(Integer.toString(code));
                    Log.w(TAG, builder.toString());
                    pfMode = "auto";
                }
            }
            builder.replace(0, builder.length(), LOG_MSG_SCENEMODE_UPDATED).append(LOG_MSG_MODE_DIAL_POS).append(Integer.toString(code)).append(LOG_MSG_SCENEMODE).append(pfMode);
            Log.i(TAG, builder.toString());
            params.setSceneMode(pfMode);
            isInitialModeRemembered = true;
        }
        Log.i(TAG, "scenemode change end");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (1 == CameraSetting.getInstance().getCurrentMode()) {
            String preference = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "");
            if ("".equals(preference)) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String mode = ((Camera.Parameters) p.first).getSceneMode();
                if (SCENE_SELECTION_MODE_DICTIONARY.containsValue(mode)) {
                    BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, mode);
                }
            }
        }
        if (ExecutorCreator.getInstance().isAssistApp() && !ModeDialDetector.hasModeDial() && !isInitialModeRemembered) {
            setValue(EXPOSURE_MODE, getValue(EXPOSURE_MODE));
            isInitialModeRemembered = true;
        }
    }

    protected boolean isMovRecRunning() {
        AvailableInfo.update();
        return AvailableInfo.isFactor(INH_ID);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        if (!Environment.isNewBizDeviceActionCam()) {
            this.mMovieExpModeInh = new MovieExpModeInhibition();
            this.mMovieExpModeInh.initialize();
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermRecorderParameters(MediaRecorder.Parameters params) {
        if (!Environment.isNewBizDeviceActionCam()) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mMovieExpModeInh.terminate();
            this.mMovieExpModeInh = null;
        }
    }

    /* loaded from: classes.dex */
    private class MyControllerListener implements NotificationListener {
        private MyControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ExposureModeController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ExposureModeController.this.mMovieExpModeInh.exec(tag);
        }
    }

    /* loaded from: classes.dex */
    class MovieExpModeInhibition {
        private boolean other2p = false;
        private boolean p2other = false;

        MovieExpModeInhibition() {
        }

        void initialize() {
            exec(null);
        }

        void terminate() {
        }

        void exec(String tag) {
            if (!MovieShootingExecutor.isMovieRecording()) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = ExposureModeController.this.mCameraSetting.getParameters();
                String mode = ((Camera.Parameters) params.first).getSceneMode();
                String value = ExposureModeController.retrieveKey(ExposureModeController.MOVIE_SUB_MODE_DICTIONARY, mode);
                StringBuilder builder = ExposureModeController.sStringBuilder.get();
                builder.replace(0, builder.length(), ExposureModeController.MSG_MOVIE_EXP_MODE_INHIBITION).append(value);
                Log.i(ExposureModeController.TAG, builder.toString());
                if (ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(value) || ExposureModeController.MOVIE_APERATURE_MODE.equals(value) || ExposureModeController.MOVIE_MANUAL_MODE.equals(value) || ExposureModeController.MOVIE_SHUTTER_MODE.equals(value)) {
                    if (true == ExposureModeController.this.isPhaseDetectAF()) {
                        toPMode();
                        this.p2other = false;
                    } else if (ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(value)) {
                        toOtherMode();
                        this.other2p = false;
                    }
                }
            }
        }

        void toPMode() {
            if (!this.other2p) {
                Log.i(ExposureModeController.TAG, ExposureModeController.MSG_TO_P);
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = ExposureModeController.this.mCameraSetting.getEmptyParameters();
                String mode = (String) ExposureModeController.MOVIE_SUB_MODE_DICTIONARY.get(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
                ((Camera.Parameters) p.first).setSceneMode(mode);
                this.other2p = true;
                ExposureModeController.this.mCameraSetting.setParametersDirect(p);
            }
        }

        void toOtherMode() {
            if (!this.p2other && true == this.other2p) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = ExposureModeController.this.mCameraSetting.getEmptyParameters();
                String movieBkup = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_MODE_VALUE, "");
                if ("".equals(movieBkup)) {
                    movieBkup = ExposureModeController.getDefaultMovieExposureMode();
                }
                String mode = (String) ExposureModeController.MOVIE_SUB_MODE_DICTIONARY.get(movieBkup);
                StringBuilder builder = ExposureModeController.sStringBuilder.get();
                builder.replace(0, builder.length(), ExposureModeController.MSG_TO_OTHER).append(mode);
                Log.i(ExposureModeController.TAG, builder.toString());
                ((Camera.Parameters) p.first).setSceneMode(mode);
                this.p2other = true;
                ExposureModeController.this.mCameraSetting.setParametersDirect(p);
            }
        }
    }

    protected static boolean isSuperiorAutoSupported() {
        if (!Environment.isNewBizDeviceLSC()) {
            return false;
        }
        return true;
    }

    protected static boolean isCommonUnderwaterSupported() {
        if (!Environment.isNewBizDeviceActionCam()) {
            return false;
        }
        return true;
    }

    protected static boolean isCommonNormalSupported() {
        if (!Environment.isNewBizDeviceActionCam()) {
            return false;
        }
        return true;
    }
}
