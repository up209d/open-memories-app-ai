package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class MovieFormatController extends AbstractController {
    protected static final String API_SETTER_ASPECT = "setVideoAspectRatio";
    protected static final String API_SETTER_BITRATE = "setVideoEncodingBitRate";
    protected static final String API_SETTER_FORMAT = "setOutputFormat";
    protected static final String API_SETTER_FRAMERATE = "setVideoFrameRate";
    protected static final String API_SETTER_SIZE = "setVideoSize";
    private static final int API_VER_MOVIE_FORMAT_ICON_SUPPORTED = 13;
    private static final int API_VER_NEW_MP4_FORMAT_SUPPORTED = 13;
    private static final int API_VER_XAVC_S_4K_SUPPORTED = 12;
    private static final int API_VER_XAVC_S_HFR_SUPPORTED = 12;
    private static final int API_VER_XAVC_S_SUPPORTED = 6;
    public static final String AVCHD = "AVCHD";
    public static final String FH_24P = "FH_24p";
    public static final String FH_25P = "FH_25p";
    public static final String FH_50I = "FH_50i";
    public static final String FH_60I = "FH_60i";
    public static final String FX_24P = "FX_24p";
    public static final String FX_25P = "FX_25p";
    public static final String FX_50I = "FX_50i";
    public static final String FX_60I = "FX_60i";
    public static final String HQ_50I = "HQ_50i";
    public static final String HQ_60I = "HQ_60i";
    private static final String LOG_CHANGE_FACE_DETECTION_OFF = "avoidPFIssue: face detection mode to off";
    public static final String MOVIE_ASPECT = "movie_aspect";
    public static final String MOVIE_FORMAT = "movie_format";
    public static final String MOVIE_FORMAT_MENU = "movie_format_menu";
    public static final String MOVIE_RECORD_SETTING = "record_setting";
    public static final String MP4 = "MP4";
    public static final String MP4_1080 = "MP4_1080";
    public static final String MP4_1080_HQ = "MP4_1080_HQ";
    public static final String MP4_1080_HQ_25P = "MP4_1080_HQ_25p";
    public static final String MP4_1080_HQ_30P = "MP4_1080_HQ_30p";
    public static final String MP4_1080_PS = "MP4_1080_PS";
    public static final String MP4_1080_PS_50P = "MP4_1080_PS_50p";
    public static final String MP4_1080_PS_60P = "MP4_1080_PS_60p";
    public static final String MP4_720 = "MP4_720";
    public static final String MP4_720_25P = "MP4_720_25p";
    public static final String MP4_720_30P = "MP4_720_30p";
    public static final String MP4_HS120 = "MP4_HS120";
    public static final String MP4_HS240 = "MP4_HS240";
    public static final String MP4_SSLOW = "MP4_SSLOW";
    public static final String MP4_VGA = "MP4_VGA";
    protected static final String MSG_APP_DEFAULT_VALUE_NOT_FOUND = "getDefaultSetting not found the return : ";
    private static final String MSG_FORMAT = "format : ";
    private static final String MSG_GET_INIT_PARAM = "onGetInitRecorderParameters ";
    private static final String MSG_ON_CAMERA_REMOVING = "onCameraRemoving ";
    private static final String MSG_ON_CAMERA_SET = "onCameraSet ";
    private static final String MSG_REC_SETTING = "recordset : ";
    private static final String MSG_REMEMBER_UNAVAILABLE_SETTING = "Current Setting is unsupported, remember before replaced : ";
    private static final String MSG_SET_AVAILABLE_FORMAT = "Current Format is unsupported, so set available format : ";
    private static final String MSG_SET_AVAILABLE_SETTING = "Current Setting is unsupported, so set available setting : ";
    private static final String MSG_SET_INHIBITED_SETTING_BACK = "Re-set inhibited setting back : ";
    public static final String NTSC = "NTSC";
    public static final String PAL = "PAL";
    public static final String PS_50P = "PS_50p";
    public static final String PS_60P = "PS_60p";
    private static final String TAG = "MovieFormatController";
    public static final String XAVC_100M_100P = "XAVC_100M_100p";
    public static final String XAVC_100M_120P = "XAVC_100M_120p";
    public static final String XAVC_100M_200P = "XAVC_100M_200p";
    public static final String XAVC_100M_240P = "XAVC_100M_240p";
    public static final String XAVC_4K_100M_24P = "XAVC_4K_100M_24p";
    public static final String XAVC_4K_100M_25P = "XAVC_4K_100M_25p";
    public static final String XAVC_4K_100M_30P = "XAVC_4K_100M_30p";
    public static final String XAVC_4K_60M_24P = "XAVC_4K_60M_24p";
    public static final String XAVC_4K_60M_25P = "XAVC_4K_60M_25p";
    public static final String XAVC_4K_60M_30P = "XAVC_4K_60M_30p";
    public static final String XAVC_50M_100P = "XAVC_50M_100p";
    public static final String XAVC_50M_120P = "XAVC_50M_120p";
    public static final String XAVC_50M_24P = "XAVC_50M_24p";
    public static final String XAVC_50M_25P = "XAVC_50M_25p";
    public static final String XAVC_50M_30P = "XAVC_50M_30p";
    public static final String XAVC_50M_50P = "XAVC_50M_50p";
    public static final String XAVC_50M_60P = "XAVC_50M_60p";
    public static final String XAVC_60M_100P = "XAVC_60M_100p";
    public static final String XAVC_60M_120P = "XAVC_60M_120p";
    public static final String XAVC_60M_200P = "XAVC_60M_200p";
    public static final String XAVC_60M_240P = "XAVC_60M_240p";
    public static final String XAVC_S = "XAVC_S";
    public static final String XAVC_S_4K = "XAVC_S_4K";
    public static final String XAVC_S_HD = "XAVC_S_HD";
    private static MovieFormatController mInstance;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sAvchdProfileTable;
    protected static String sBroadcastSystem;
    protected static HashMap<String, String> sConvTableForBroadcastSystem;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sMp4ProfileTable;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sXAvc4KProfileTable;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sXAvcProfileTable;
    private static StringBuilderThreadLocal sBuilders = new StringBuilderThreadLocal();
    private static final String myName = MovieFormatController.class.getSimpleName();
    protected static final String[] TAGS = {CameraNotificationManager.SCENE_MODE};
    protected SceneModeListener mSceneModeListener = new SceneModeListener();
    private CameraSetting mCamSet = CameraSetting.getInstance();
    protected Settings mUserSetting = new Settings();

    /* loaded from: classes.dex */
    public static class Settings {
        public static final String EQUAL = "=";
        public static final String KEY_FORMAT = "format";
        public static final String KEY_REC_SETTING_AVCHD = "setting_avchd";
        public static final String KEY_REC_SETTING_MP4 = "setting_mpeg4";
        public static final String KEY_REC_SETTING_XAVC_S = "setting_xavc_s";
        public static final String KEY_REC_SETTING_XAVC_S_4K = "setting_xavc_s_4k";
        public static final String SEMI_COLON = ";";
        private HashMap<String, String> mMap = new HashMap<>();

        public String flatten() {
            if (this.mMap.size() == 0) {
                return "";
            }
            StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
            StringBuilder flattened = sStringBuilder.get();
            for (String k : this.mMap.keySet()) {
                flattened.append(k);
                flattened.append(EQUAL);
                flattened.append(this.mMap.get(k));
                flattened.append(SEMI_COLON);
            }
            flattened.deleteCharAt(flattened.length() - 1);
            return flattened.toString();
        }

        public void unflatten(String flattened) {
            this.mMap.clear();
            StringTokenizer tokenizer = new StringTokenizer(flattened, SEMI_COLON);
            while (tokenizer.hasMoreElements()) {
                String kv = tokenizer.nextToken();
                int pos = kv.indexOf(EQUAL);
                if (pos != -1) {
                    String k = kv.substring(0, pos);
                    String v = kv.substring(pos + 1);
                    this.mMap.put(k, v);
                }
            }
        }

        public void set(String key, String value) {
            if (key.indexOf(EQUAL) == -1 && key.indexOf(SEMI_COLON) == -1 && value.indexOf(EQUAL) == -1 && value.indexOf(SEMI_COLON) == -1) {
                this.mMap.put(key, value);
            }
        }

        public String get(String key) {
            return this.mMap.get(key);
        }

        public static String convToKey(String format) {
            if (!DatabaseManager.getInstance().isReady()) {
                String key = null;
                if (MovieFormatController.AVCHD.equals(format)) {
                    key = KEY_REC_SETTING_AVCHD;
                } else if (MovieFormatController.XAVC_S.equals(format)) {
                    key = KEY_REC_SETTING_XAVC_S;
                } else if (MovieFormatController.XAVC_S_4K.equals(format)) {
                    key = KEY_REC_SETTING_XAVC_S_4K;
                } else if (MovieFormatController.MP4.equals(format)) {
                    key = KEY_REC_SETTING_MP4;
                }
                return key;
            }
            return format;
        }

        protected String getKey() {
            return convToKey(get(KEY_FORMAT));
        }

        public void setRecSetting(String value) {
            String key = getKey();
            set(key, value);
        }

        public String getRecSetting() {
            String key = getKey();
            return get(key);
        }
    }

    public static final String getName() {
        return myName;
    }

    public static MovieFormatController getInstance() {
        if (mInstance == null) {
            mInstance = new MovieFormatController();
        }
        return mInstance;
    }

    private static void setController(MovieFormatController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected MovieFormatController() {
        setController(this);
    }

    protected boolean useDefaultSetting() {
        return !ExecutorCreator.getInstance().isAssistApp() && DatabaseManager.getInstance().isReady();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        MediaRecorder.CamcorderProfile profile;
        if (Environment.isMovieAPISupported()) {
            boolean avoidPFIssue = false;
            String faceDetectionMode = "off";
            if (isPfIssueAvailable_HfrAndFace()) {
                try {
                    faceDetectionMode = FaceDetectionController.getInstance().getValue();
                } catch (IController.NotSupportedException e) {
                    e.printStackTrace();
                }
                avoidPFIssue = !"off".equals(faceDetectionMode) && (XAVC_50M_100P.equalsIgnoreCase(value) || XAVC_50M_120P.equalsIgnoreCase(value) || XAVC_60M_100P.equalsIgnoreCase(value) || XAVC_60M_120P.equalsIgnoreCase(value) || XAVC_100M_100P.equalsIgnoreCase(value) || XAVC_100M_120P.equalsIgnoreCase(value) || XAVC_S_HD.equals(value) || XAVC_S.equalsIgnoreCase(value));
                if (avoidPFIssue) {
                    FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, "off");
                    Log.i(TAG, LOG_CHANGE_FACE_DETECTION_OFF);
                }
            }
            if ("movie_format".equals(tag) || MOVIE_FORMAT_MENU.equals(tag)) {
                if (XAVC_S_HD.equals(value)) {
                    value = XAVC_S;
                }
                MediaRecorder.CamcorderProfile profile2 = null;
                this.mUserSetting.set(Settings.KEY_FORMAT, value);
                String recSetting = this.mUserSetting.getRecSetting();
                if (recSetting == null && useDefaultSetting()) {
                    recSetting = DatabaseManager.getInstance().getDefaultSetting(value);
                }
                if (recSetting != null) {
                    profile2 = appRecordSettingToPfProfile(recSetting);
                }
                MediaRecorder.Parameters params = appFormatToPF(value);
                if (profile2 != null && params != null) {
                    params.setVideoAspectRatio(profile2.videoAspectRatio);
                    params.setVideoEncodingBitRate(profile2.videoEncodingBitRate);
                    params.setVideoFrameRate(profile2.videoFrameRate);
                    params.setVideoSize(profile2.videoSize);
                }
                this.mCamSet.setRecorderParameters(params);
                if (profile2 == null || !getAvailableValue("record_setting").contains(recSetting)) {
                    setAvailableSetting();
                }
            } else if ("record_setting".equals(tag) && (profile = appRecordSettingToPfProfile(value)) != null) {
                String key = Settings.convToKey(getValue("movie_format"));
                this.mUserSetting.set(key, value);
                MediaRecorder.Parameters params2 = new MediaRecorder.Parameters();
                params2.setVideoAspectRatio(profile.videoAspectRatio);
                params2.setVideoEncodingBitRate(profile.videoEncodingBitRate);
                params2.setVideoFrameRate(profile.videoFrameRate);
                params2.setVideoSize(profile.videoSize);
                this.mCamSet.setRecorderParameters(params2);
            }
            if (isPfIssueAvailable_HfrAndFace() && avoidPFIssue) {
                FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, faceDetectionMode);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        MediaRecorder.Parameters params;
        String value = null;
        if (Environment.isMovieAPISupported() && (params = this.mCamSet.getRecorderParameters()) != null) {
            if ("movie_format".equals(tag)) {
                return pfFormatToApp(params);
            }
            if (MOVIE_FORMAT_MENU.equals(tag)) {
                String value2 = pfFormatToApp(params);
                if (!DatabaseManager.getInstance().isReady() && XAVC_S.equals(value2) && isMovieFormatIconSupported()) {
                    value2 = XAVC_S_HD;
                }
                return value2;
            }
            if ("record_setting".equals(tag)) {
                value = pfProfileToApp(params.getOutputFormat(), params.getVideoEncodingBitRate(), params.getVideoFrameRate(), params.getVideoSize(), params.getVideoAspectRatio());
            } else if (MOVIE_ASPECT.equals(tag)) {
                value = params.getVideoAspectRatio();
            }
        }
        return value;
    }

    public void createSupportedProfileTable(MediaRecorder.Parameters supported) {
        List<MediaRecorder.CamcorderProfile> supportedList;
        if (!DatabaseManager.getInstance().isReady()) {
            boolean bIsXavcSupported = isXavcsSupported();
            boolean bIsXavc4KSupported = isXavcs4KSupported();
            if ((sAvchdProfileTable == null || sMp4ProfileTable == null || ((bIsXavcSupported && sXAvcProfileTable == null) || (bIsXavc4KSupported && sXAvc4KProfileTable == null))) && supported != null && (supportedList = supported.getSupportedCamcoderProfiles()) != null) {
                sAvchdProfileTable = new HashMap<>();
                sMp4ProfileTable = new HashMap<>();
                if (bIsXavcSupported) {
                    sXAvcProfileTable = new HashMap<>();
                }
                if (bIsXavc4KSupported) {
                    sXAvc4KProfileTable = new HashMap<>();
                }
                for (MediaRecorder.CamcorderProfile profile : supportedList) {
                    String value = pfProfileToApp(profile.outputFormat, profile.videoEncodingBitRate, profile.videoFrameRate, profile.videoSize, profile.videoAspectRatio);
                    if (value != null) {
                        if (AVCHD.equals(profile.outputFormat)) {
                            sAvchdProfileTable.put(value, profile);
                        } else if ("MPEG4".equals(profile.outputFormat)) {
                            sMp4ProfileTable.put(value, profile);
                        } else if ("XAVCS".equals(profile.outputFormat)) {
                            if (bIsXavcSupported) {
                                sXAvcProfileTable.put(value, profile);
                            }
                        } else if ("XAVCS_4K".equals(profile.outputFormat) && bIsXavc4KSupported) {
                            sXAvc4KProfileTable.put(value, profile);
                        }
                    }
                }
                createConvTable(sBroadcastSystem);
            }
        }
    }

    public static String decideBroadcastSystemByFR(String framerate) {
        if ("60i".equals(framerate) || "60p".equals(framerate) || "30p".equals(framerate) || "24p".equals(framerate) || "120p".equals(framerate) || "240p".equals(framerate)) {
            return NTSC;
        }
        if (!"50i".equals(framerate) && !"50p".equals(framerate) && !"25p".equals(framerate) && !"100p".equals(framerate) && !"200p".equals(framerate)) {
            return null;
        }
        return PAL;
    }

    public static void clearSupportedProfileTable() {
        if (Environment.isMovieAPISupported()) {
            sBroadcastSystem = null;
            sAvchdProfileTable = null;
            sMp4ProfileTable = null;
            sXAvcProfileTable = null;
            sXAvc4KProfileTable = null;
            clearConvTable();
        }
    }

    protected static void createConvTable(String to) {
        if (sConvTableForBroadcastSystem == null) {
            if (!DatabaseManager.getInstance().isReady()) {
                if (PAL.equals(to)) {
                    sConvTableForBroadcastSystem = new HashMap<>();
                    sConvTableForBroadcastSystem.put(FX_60I, FX_50I);
                    sConvTableForBroadcastSystem.put(FH_60I, FH_50I);
                    sConvTableForBroadcastSystem.put(HQ_60I, HQ_50I);
                    sConvTableForBroadcastSystem.put(PS_60P, PS_50P);
                    sConvTableForBroadcastSystem.put(FX_24P, FX_25P);
                    sConvTableForBroadcastSystem.put(FH_24P, FH_25P);
                    if (isXavcsSupported()) {
                        sConvTableForBroadcastSystem.put(XAVC_50M_60P, XAVC_50M_50P);
                        sConvTableForBroadcastSystem.put(XAVC_50M_30P, XAVC_50M_25P);
                        sConvTableForBroadcastSystem.put(XAVC_50M_24P, XAVC_50M_25P);
                        sConvTableForBroadcastSystem.put(XAVC_50M_120P, XAVC_50M_100P);
                        if (isXavcs4KSupported()) {
                            sConvTableForBroadcastSystem.put(XAVC_4K_60M_30P, XAVC_4K_60M_25P);
                            sConvTableForBroadcastSystem.put(XAVC_4K_60M_24P, XAVC_4K_60M_25P);
                            sConvTableForBroadcastSystem.put(XAVC_4K_100M_30P, XAVC_4K_100M_25P);
                            sConvTableForBroadcastSystem.put(XAVC_4K_100M_24P, XAVC_4K_100M_25P);
                        }
                        if (isXavcsHFRSupported()) {
                            sConvTableForBroadcastSystem.put(XAVC_60M_120P, XAVC_60M_100P);
                            sConvTableForBroadcastSystem.put(XAVC_60M_240P, XAVC_60M_200P);
                            sConvTableForBroadcastSystem.put(XAVC_100M_120P, XAVC_100M_100P);
                            sConvTableForBroadcastSystem.put(XAVC_100M_240P, XAVC_100M_200P);
                        }
                    }
                    if (is152HMP4Format()) {
                        sConvTableForBroadcastSystem.put(MP4_1080_PS_60P, MP4_1080_PS_50P);
                        sConvTableForBroadcastSystem.put(MP4_1080_HQ_30P, MP4_1080_HQ_25P);
                        sConvTableForBroadcastSystem.put(MP4_720_30P, MP4_720_25P);
                        return;
                    }
                    return;
                }
                sConvTableForBroadcastSystem = new HashMap<>();
                sConvTableForBroadcastSystem.put(FX_50I, FX_60I);
                sConvTableForBroadcastSystem.put(FH_50I, FH_60I);
                sConvTableForBroadcastSystem.put(HQ_50I, HQ_60I);
                sConvTableForBroadcastSystem.put(PS_50P, PS_60P);
                sConvTableForBroadcastSystem.put(FX_25P, FX_24P);
                sConvTableForBroadcastSystem.put(FH_25P, FH_24P);
                if (isXavcsSupported()) {
                    sConvTableForBroadcastSystem.put(XAVC_50M_50P, XAVC_50M_60P);
                    sConvTableForBroadcastSystem.put(XAVC_50M_25P, XAVC_50M_30P);
                    sConvTableForBroadcastSystem.put(XAVC_50M_100P, XAVC_50M_120P);
                    if (isXavcs4KSupported()) {
                        sConvTableForBroadcastSystem.put(XAVC_4K_60M_25P, XAVC_4K_60M_30P);
                        sConvTableForBroadcastSystem.put(XAVC_4K_100M_25P, XAVC_4K_100M_30P);
                    }
                    if (isXavcsHFRSupported()) {
                        sConvTableForBroadcastSystem.put(XAVC_60M_100P, XAVC_60M_120P);
                        sConvTableForBroadcastSystem.put(XAVC_60M_200P, XAVC_60M_240P);
                        sConvTableForBroadcastSystem.put(XAVC_100M_100P, XAVC_100M_120P);
                        sConvTableForBroadcastSystem.put(XAVC_100M_200P, XAVC_100M_240P);
                    }
                }
                if (is152HMP4Format()) {
                    sConvTableForBroadcastSystem.put(MP4_1080_PS_50P, MP4_1080_PS_60P);
                    sConvTableForBroadcastSystem.put(MP4_1080_HQ_25P, MP4_1080_HQ_30P);
                    sConvTableForBroadcastSystem.put(MP4_720_25P, MP4_720_30P);
                    return;
                }
                return;
            }
            sConvTableForBroadcastSystem = DatabaseManager.getInstance().getTvSystemConvTable(NTSC.equals(to) ? DatabaseManager.VAL_NTSC : DatabaseManager.VAL_PAL);
        }
    }

    protected static String convertByBroadcastSystem(String system, String value) {
        createConvTable(system);
        if (sConvTableForBroadcastSystem.containsKey(value)) {
            return sConvTableForBroadcastSystem.get(value);
        }
        return value;
    }

    protected static void clearConvTable() {
        sConvTableForBroadcastSystem = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onMediaRecorderSet() {
        if (Environment.isMovieAPISupported()) {
            if (Environment.getVersionPfAPI() >= 2) {
                sBroadcastSystem = 1 == ScalarProperties.getInt("signal.frequency") ? NTSC : PAL;
            }
            if (DatabaseManager.getInstance().isReady()) {
                DatabaseManager.getInstance().updateSupportedProfiles(this.mCamSet.getSupportedRecorderParameters().getSupportedCamcoderProfiles());
            }
            createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
            String flattened = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_FORMAT_PARAMETERS, null);
            if (flattened != null) {
                StringBuilder builder = sBuilders.get();
                builder.replace(0, builder.length(), MSG_ON_CAMERA_SET).append(flattened);
                Log.i(TAG, builder.toString());
                this.mUserSetting.unflatten(flattened);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onMediaRecorderRemoving() {
        if (Environment.isMovieAPISupported()) {
            String flattened = this.mUserSetting.flatten();
            StringBuilder builder = sBuilders.get();
            builder.replace(0, builder.length(), MSG_ON_CAMERA_REMOVING).append(flattened);
            Log.i(TAG, builder.toString());
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_FORMAT_PARAMETERS, flattened);
            clearSupportedProfileTable();
            builder.setLength(0);
            builder.trimToSize();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        MediaRecorder.Parameters forceSettingParams;
        if (Environment.isMovieAPISupported()) {
            String format = this.mUserSetting.get(Settings.KEY_FORMAT);
            if (format == null && useDefaultSetting()) {
                format = DatabaseManager.getInstance().getDefaultFormat();
            }
            List<String> availableFormat = getAvailableValue("movie_format");
            if (availableFormat != null) {
                StringBuilder builder = sBuilders.get();
                builder.replace(0, builder.length(), MSG_GET_INIT_PARAM).append(MSG_FORMAT).append(format);
                Log.i(TAG, builder.toString());
                if (availableFormat == null || !availableFormat.contains(format)) {
                    format = null;
                }
            }
            if (format == null && (format = pfFormatToApp(this.mCamSet.getRecorderParameters())) == null && availableFormat != null) {
                String default_format = AVCHD;
                if (DatabaseManager.getInstance().isReady()) {
                    default_format = DatabaseManager.getInstance().getDefaultFormat();
                }
                if (availableFormat.contains(default_format)) {
                    format = default_format;
                } else {
                    format = availableFormat.get(0);
                }
                StringBuilder builder2 = sBuilders.get();
                builder2.replace(0, builder2.length(), MSG_SET_AVAILABLE_FORMAT).append(format);
                Log.w(TAG, builder2.toString());
            }
            if (format != null && (forceSettingParams = appFormatToPF(format)) != null) {
                String faceDetectionMode = "off";
                boolean avoidPFIssue = false;
                if (isPfIssueAvailable_HfrAndFace()) {
                    try {
                        faceDetectionMode = FaceDetectionController.getInstance().getValue();
                    } catch (IController.NotSupportedException e) {
                        e.printStackTrace();
                    }
                    avoidPFIssue = !"off".equals(faceDetectionMode) && XAVC_S.equals(format);
                    if (avoidPFIssue) {
                        FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, "off");
                        Log.i(TAG, LOG_CHANGE_FACE_DETECTION_OFF);
                    }
                }
                this.mCamSet.setRecorderParameters(forceSettingParams);
                if (isPfIssueAvailable_HfrAndFace() && avoidPFIssue) {
                    FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, faceDetectionMode);
                }
            }
            boolean isProfileSet = false;
            String formatKey = Settings.convToKey(format);
            String recordSet = this.mUserSetting.get(formatKey);
            if (recordSet != null) {
                String convertValue = convertByBroadcastSystem(sBroadcastSystem, recordSet);
                if (!recordSet.equals(convertValue)) {
                    this.mUserSetting.set(formatKey, convertValue);
                    recordSet = convertValue;
                }
            }
            if (recordSet == null && useDefaultSetting()) {
                recordSet = DatabaseManager.getInstance().getDefaultSetting(format);
            }
            if (recordSet != null && getAvailableValue("record_setting").contains(recordSet)) {
                StringBuilder builder3 = sBuilders.get();
                builder3.replace(0, builder3.length(), MSG_GET_INIT_PARAM).append(MSG_FORMAT).append(format).append(MSG_REC_SETTING).append(recordSet);
                Log.i(TAG, builder3.toString());
                MediaRecorder.CamcorderProfile profile = appRecordSettingToPfProfile(recordSet);
                if (profile != null) {
                    params.setVideoAspectRatio(profile.videoAspectRatio);
                    params.setVideoEncodingBitRate(profile.videoEncodingBitRate);
                    params.setVideoFrameRate(profile.videoFrameRate);
                    params.setVideoSize(profile.videoSize);
                    isProfileSet = true;
                }
            }
            if (!isProfileSet) {
                setAvailableSetting();
            }
            CameraNotificationManager.getInstance().setNotificationListener(this.mSceneModeListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermRecorderParameters(MediaRecorder.Parameters params) {
        if (Environment.isMovieAPISupported()) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mSceneModeListener);
        }
    }

    /* loaded from: classes.dex */
    public class SceneModeListener implements NotificationListener {
        public SceneModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MovieFormatController.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag)) {
                String key = Settings.convToKey(MovieFormatController.this.getValue("movie_format"));
                String recSetting = MovieFormatController.this.mUserSetting.get(key);
                if (recSetting == null || !MovieFormatController.this.getAvailableValue("record_setting").contains(recSetting)) {
                    MovieFormatController.this.setAvailableSetting();
                    return;
                }
                String current = MovieFormatController.this.getValue("record_setting");
                if (!recSetting.equals(current)) {
                    StringBuilder builder = MovieFormatController.sBuilders.get();
                    builder.replace(0, builder.length(), MovieFormatController.MSG_SET_INHIBITED_SETTING_BACK).append(recSetting);
                    Log.w(MovieFormatController.TAG, builder.toString());
                    MediaRecorder.CamcorderProfile profile = MovieFormatController.this.appRecordSettingToPfProfile(recSetting);
                    if (profile != null) {
                        MediaRecorder.Parameters params = new MediaRecorder.Parameters();
                        params.setVideoAspectRatio(profile.videoAspectRatio);
                        params.setVideoEncodingBitRate(profile.videoEncodingBitRate);
                        params.setVideoFrameRate(profile.videoFrameRate);
                        params.setVideoSize(profile.videoSize);
                        MovieFormatController.this.mCamSet.setRecorderParameters(params);
                    }
                }
            }
        }
    }

    public static String pfFormatToApp(MediaRecorder.Parameters params) {
        if (DatabaseManager.getInstance().isReady()) {
            return DatabaseManager.getInstance().params2format(params);
        }
        String format = params.getOutputFormat();
        if (AVCHD.equals(format)) {
            return AVCHD;
        }
        if ("MPEG4".equals(format)) {
            return MP4;
        }
        if ("XAVCS".equals(format)) {
            if (!isXavcsSupported()) {
                return null;
            }
            return XAVC_S;
        }
        if (!"XAVCS_4K".equals(format) || !isXavcs4KSupported()) {
            return null;
        }
        return XAVC_S_4K;
    }

    public static MediaRecorder.Parameters appFormatToPF(String app) {
        if (DatabaseManager.getInstance().isReady()) {
            return DatabaseManager.getInstance().format2parameters(app);
        }
        String pf = null;
        if (AVCHD.equals(app)) {
            pf = AVCHD;
        } else if (MP4.equals(app)) {
            pf = "MPEG4";
        } else if ((XAVC_S.equals(app) || XAVC_S_HD.equals(app)) && isXavcsSupported()) {
            pf = "XAVCS";
        } else if (XAVC_S_4K.equals(app) && isXavcs4KSupported()) {
            pf = "XAVCS_4K";
        }
        if (pf == null) {
            return null;
        }
        MediaRecorder.Parameters params = new MediaRecorder.Parameters();
        params.setOutputFormat(pf);
        return params;
    }

    public static String pfProfileToApp(String format, String bitrate, String framerate, String size, String aspect) {
        if (DatabaseManager.getInstance().isReady()) {
            return DatabaseManager.getInstance().profile2value(format, size, aspect, bitrate, framerate);
        }
        String result = null;
        if (AVCHD.equals(format)) {
            if ("FH".equals(bitrate)) {
                if ("60i".equals(framerate)) {
                    return FH_60I;
                }
                if ("50i".equals(framerate)) {
                    return FH_50I;
                }
                if ("24p".equals(framerate)) {
                    return FH_24P;
                }
                if (!"25p".equals(framerate)) {
                    return null;
                }
                return FH_25P;
            }
            if ("FX".equals(bitrate)) {
                if ("60i".equals(framerate)) {
                    return FX_60I;
                }
                if ("50i".equals(framerate)) {
                    return FX_50I;
                }
                if ("24p".equals(framerate)) {
                    return FX_24P;
                }
                if (!"25p".equals(framerate)) {
                    return null;
                }
                return FX_25P;
            }
            if ("HQ".equals(bitrate)) {
                if ("60i".equals(framerate)) {
                    return HQ_60I;
                }
                if (!"50i".equals(framerate)) {
                    return null;
                }
                return HQ_50I;
            }
            if (!"PS".equals(bitrate)) {
                return null;
            }
            if ("60p".equals(framerate)) {
                return PS_60P;
            }
            if (!"50p".equals(framerate)) {
                return null;
            }
            return PS_50P;
        }
        if ("MPEG4".equals(format)) {
            if ("HD_1080".equals(size)) {
                return MP4_1080;
            }
            if ("HD_720".equals(size)) {
                if ("STD".equals(bitrate)) {
                    result = MP4_720;
                } else if ("SP".equals(bitrate)) {
                    if ("30p".equals(framerate)) {
                        result = MP4_720_30P;
                    } else if ("25p".equals(framerate)) {
                        result = MP4_720_25P;
                    }
                }
                if (Environment.isNewBizDevice()) {
                    if ("HS100".equals(bitrate)) {
                        return MP4_HS120;
                    }
                    if ("SSLOW".equals(bitrate)) {
                        return MP4_SSLOW;
                    }
                    return result;
                }
                return result;
            }
            if ("VGA".equals(size)) {
                return MP4_VGA;
            }
            if ("FHD_1080".equals(size)) {
                if ("PS".equals(bitrate)) {
                    if (is152HMP4Format()) {
                        if ("60p".equals(framerate)) {
                            return MP4_1080_PS_60P;
                        }
                        if (!"50p".equals(framerate)) {
                            return null;
                        }
                        return MP4_1080_PS_50P;
                    }
                    return MP4_1080_PS;
                }
                if (!"FH".equals(bitrate)) {
                    return null;
                }
                if (is152HMP4Format()) {
                    if ("30p".equals(framerate)) {
                        return MP4_1080_HQ_30P;
                    }
                    if (!"25p".equals(framerate)) {
                        return null;
                    }
                    return MP4_1080_HQ_25P;
                }
                return MP4_1080_HQ;
            }
            if (!isXavcsHFRSupported() || !"WVGA".equals(size) || !Environment.isNewBizDevice() || !"FZ".equals(bitrate)) {
                return null;
            }
            return MP4_HS240;
        }
        if ("XAVCS".equals(format)) {
            if (!isXavcsSupported()) {
                return null;
            }
            if (isXavcsHFRSupported()) {
                if ("FHD_1080".equals(size)) {
                    if ("60M".equals(bitrate)) {
                        if ("120p".equals(framerate)) {
                            result = XAVC_60M_120P;
                        } else if ("100p".equals(framerate)) {
                            result = XAVC_60M_100P;
                        }
                    } else if ("100M".equals(bitrate)) {
                        if ("120p".equals(framerate)) {
                            result = XAVC_100M_120P;
                        } else if ("100p".equals(framerate)) {
                            result = XAVC_100M_100P;
                        }
                    }
                } else if ("HD_720".equals(size) && Environment.isNewBizDevice()) {
                    if ("60M".equals(bitrate)) {
                        if ("240p".equals(framerate)) {
                            result = XAVC_60M_240P;
                        } else if ("200p".equals(framerate)) {
                            result = XAVC_60M_200P;
                        }
                    } else if ("100M".equals(bitrate)) {
                        if ("240p".equals(framerate)) {
                            result = XAVC_100M_240P;
                        } else if ("200p".equals(framerate)) {
                            result = XAVC_100M_200P;
                        }
                    }
                }
            }
            if (result == null && "50M".equals(bitrate)) {
                if ("60p".equals(framerate)) {
                    return XAVC_50M_60P;
                }
                if ("30p".equals(framerate)) {
                    return XAVC_50M_30P;
                }
                if ("24p".equals(framerate)) {
                    return XAVC_50M_24P;
                }
                if ("120p".equals(framerate)) {
                    return XAVC_50M_120P;
                }
                if ("50p".equals(framerate)) {
                    return XAVC_50M_50P;
                }
                if ("25p".equals(framerate)) {
                    return XAVC_50M_25P;
                }
                if ("100p".equals(framerate)) {
                    return XAVC_50M_100P;
                }
                return result;
            }
            return result;
        }
        if (!"XAVCS_4K".equals(format) || !isXavcs4KSupported() || !"4K_2160".equals(size)) {
            return null;
        }
        if ("60M".equals(bitrate)) {
            if ("30p".equals(framerate)) {
                return XAVC_4K_60M_30P;
            }
            if ("25p".equals(framerate)) {
                return XAVC_4K_60M_25P;
            }
            if (!"24p".equals(framerate)) {
                return null;
            }
            return XAVC_4K_60M_24P;
        }
        if (!"100M".equals(bitrate)) {
            return null;
        }
        if ("30p".equals(framerate)) {
            return XAVC_4K_100M_30P;
        }
        if ("25p".equals(framerate)) {
            return XAVC_4K_100M_25P;
        }
        if (!"24p".equals(framerate)) {
            return null;
        }
        return XAVC_4K_100M_24P;
    }

    public MediaRecorder.CamcorderProfile appRecordSettingToPfProfile(String value) {
        MediaRecorder.CamcorderProfile profile = null;
        String value2 = convertByBroadcastSystem(sBroadcastSystem, value);
        if (DatabaseManager.getInstance().isReady()) {
            return DatabaseManager.getInstance().value2profile(value2);
        }
        if (sAvchdProfileTable != null) {
            MediaRecorder.CamcorderProfile profile2 = sAvchdProfileTable.get(value2);
            profile = profile2;
        }
        if (profile == null && sMp4ProfileTable != null) {
            MediaRecorder.CamcorderProfile profile3 = sMp4ProfileTable.get(value2);
            profile = profile3;
        }
        if (profile == null && sXAvcProfileTable != null) {
            MediaRecorder.CamcorderProfile profile4 = sXAvcProfileTable.get(value2);
            profile = profile4;
        }
        if (profile == null && sXAvc4KProfileTable != null) {
            MediaRecorder.CamcorderProfile profile5 = sXAvc4KProfileTable.get(value2);
            profile = profile5;
        }
        return profile;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> result = null;
        if (Environment.isMovieAPISupported()) {
            if ("movie_format".equals(tag) || MOVIE_FORMAT_MENU.equals(tag)) {
                if (DatabaseManager.getInstance().isReady()) {
                    result = DatabaseManager.getInstance().getSupportedFormat();
                } else {
                    createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
                    result = new ArrayList<>();
                    if (sAvchdProfileTable != null && sAvchdProfileTable.size() > 0) {
                        result.add(AVCHD);
                    }
                    if (sMp4ProfileTable != null && sMp4ProfileTable.size() > 0) {
                        result.add(MP4);
                    }
                    if (sXAvcProfileTable != null && sXAvcProfileTable.size() > 0) {
                        if (MOVIE_FORMAT_MENU.equals(tag) && isMovieFormatIconSupported()) {
                            result.add(XAVC_S_HD);
                        } else {
                            result.add(XAVC_S);
                        }
                    }
                    if (sXAvc4KProfileTable != null && sXAvc4KProfileTable.size() > 0) {
                        result.add(XAVC_S_4K);
                    }
                }
            } else if ("record_setting".equals(tag)) {
                if (DatabaseManager.getInstance().isReady()) {
                    result = DatabaseManager.getInstance().getSupportedSetting(getValue("movie_format"));
                } else {
                    createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
                    String fileFormat = getValue("movie_format");
                    result = new ArrayList<>();
                    if (AVCHD.equals(fileFormat)) {
                        if (sAvchdProfileTable == null) {
                            return null;
                        }
                        result.addAll(sAvchdProfileTable.keySet());
                    } else if (MP4.equals(fileFormat)) {
                        if (sMp4ProfileTable == null) {
                            return null;
                        }
                        result.addAll(sMp4ProfileTable.keySet());
                    } else if (XAVC_S.equals(fileFormat)) {
                        if (sXAvcProfileTable == null) {
                            return null;
                        }
                        result.addAll(sXAvcProfileTable.keySet());
                    } else if (XAVC_S_4K.equals(fileFormat)) {
                        if (sXAvc4KProfileTable == null) {
                            return null;
                        }
                        result.addAll(sXAvc4KProfileTable.keySet());
                    } else if (2 != CameraSetting.getInstance().getCurrentMode()) {
                        if (sAvchdProfileTable != null) {
                            result.addAll(sAvchdProfileTable.keySet());
                        }
                        if (sMp4ProfileTable != null) {
                            result.addAll(sMp4ProfileTable.keySet());
                        }
                        if (sXAvcProfileTable != null) {
                            result.addAll(sXAvcProfileTable.keySet());
                        }
                        if (sXAvc4KProfileTable != null) {
                            result.addAll(sXAvc4KProfileTable.keySet());
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        HashMap<String, MediaRecorder.CamcorderProfile> table;
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return null;
        }
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            AvailableInfo.update();
            if ("movie_format".equals(tag) || MOVIE_FORMAT_MENU.equals(tag)) {
                for (String mode : supporteds) {
                    MediaRecorder.Parameters params = appFormatToPF(mode);
                    if (AvailableInfo.isAvailable(API_SETTER_FORMAT, params.getOutputFormat())) {
                        availables.add(mode);
                    }
                }
            } else if ("record_setting".equals(tag)) {
                if (DatabaseManager.getInstance().isReady()) {
                    boolean isMovieAuto = ExposureModeController.MOVIE_AUTO.equals(ExposureModeController.getInstance().getValue(null));
                    for (String mode2 : supporteds) {
                        if (!isMovieAuto || (!XAVC_50M_100P.equals(mode2) && !XAVC_50M_120P.equals(mode2))) {
                            MediaRecorder.CamcorderProfile profile = DatabaseManager.getInstance().value2profile(mode2);
                            if (AvailableInfo.isAvailable(API_SETTER_FORMAT, profile.outputFormat, API_SETTER_BITRATE, profile.videoEncodingBitRate, API_SETTER_FRAMERATE, profile.videoFrameRate, API_SETTER_SIZE, profile.videoSize, API_SETTER_ASPECT, profile.videoAspectRatio)) {
                                availables.add(mode2);
                            }
                        }
                    }
                } else {
                    String fileFormat = getValue("movie_format");
                    if (AVCHD.equals(fileFormat)) {
                        table = sAvchdProfileTable;
                    } else if (MP4.equals(fileFormat)) {
                        table = sMp4ProfileTable;
                    } else if (XAVC_S.equals(fileFormat)) {
                        if (!isXavcsSupported()) {
                            return null;
                        }
                        table = sXAvcProfileTable;
                    } else {
                        if (!XAVC_S_4K.equals(fileFormat) || !isXavcs4KSupported()) {
                            return null;
                        }
                        table = sXAvc4KProfileTable;
                    }
                    boolean isMovieAuto2 = ExposureModeController.MOVIE_AUTO.equals(ExposureModeController.getInstance().getValue(null));
                    for (String mode3 : supporteds) {
                        if (!isMovieAuto2 || (!XAVC_50M_100P.equals(mode3) && !XAVC_50M_120P.equals(mode3))) {
                            MediaRecorder.CamcorderProfile profile2 = table.get(mode3);
                            if (AvailableInfo.isAvailable(API_SETTER_FORMAT, profile2.outputFormat, API_SETTER_BITRATE, profile2.videoEncodingBitRate, API_SETTER_FRAMERATE, profile2.videoFrameRate, API_SETTER_SIZE, profile2.videoSize, API_SETTER_ASPECT, profile2.videoAspectRatio)) {
                                availables.add(mode3);
                            }
                        }
                    }
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        AvailableInfo.update();
        if ("movie_format".equals(tag) || MOVIE_FORMAT_MENU.equals(tag)) {
            boolean result = AvailableInfo.isAvailable(API_SETTER_FORMAT, null);
            return result;
        }
        if (!"record_setting".equals(tag)) {
            return false;
        }
        boolean result2 = AvailableInfo.isAvailable(API_SETTER_FORMAT, null, API_SETTER_BITRATE, null, API_SETTER_FRAMERATE, null, API_SETTER_SIZE, null, API_SETTER_ASPECT, null);
        return result2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        ExecutorCreator.getInstance().getRecordingMode();
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return true;
        }
        if ("movie_format".equals(tag)) {
            AvailableInfo.update();
            boolean isInhibited = isUnavailableAPISceneFactor(API_SETTER_FORMAT, null);
            return isInhibited;
        }
        if (!"record_setting".equals(tag)) {
            return true;
        }
        AvailableInfo.update();
        boolean isInhibited2 = isUnavailableAPISceneFactor(API_SETTER_FORMAT, null, API_SETTER_BITRATE, null, API_SETTER_FRAMERATE, null, API_SETTER_SIZE, null, API_SETTER_ASPECT, null);
        return isInhibited2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        List<String> supportedList;
        if (2 == ExecutorCreator.getInstance().getRecordingMode() || 8 == ExecutorCreator.getInstance().getRecordingMode() || (supportedList = getSupportedValue(itemId)) == null || supportedList.size() == 0) {
            return 0;
        }
        return -2;
    }

    protected String getDefaultSetting(String format) {
        List<String> supported_list;
        String result = null;
        if (DatabaseManager.getInstance().isReady()) {
            String result2 = DatabaseManager.getInstance().getDefaultSetting(format);
            if (result2 == null && (supported_list = DatabaseManager.getInstance().getSupportedSetting(format)) != null && supported_list.size() > 0) {
                String result3 = supported_list.get(0);
                result2 = result3;
            }
            return result2;
        }
        HashMap<String, MediaRecorder.CamcorderProfile> supported = null;
        if (AVCHD.equals(format)) {
            supported = sAvchdProfileTable;
            if (NTSC.equals(sBroadcastSystem)) {
                result = FH_60I;
            } else {
                result = FH_50I;
            }
        } else if (MP4.equals(format)) {
            supported = sMp4ProfileTable;
            if (is152HMP4Format()) {
                if (NTSC.equals(sBroadcastSystem)) {
                    result = MP4_1080_HQ_30P;
                } else {
                    result = MP4_1080_HQ_25P;
                }
            } else if (supported != null && supported.containsKey(MP4_1080_HQ)) {
                result = MP4_1080_HQ;
            } else {
                result = MP4_1080;
            }
        } else if (XAVC_S.equals(format)) {
            supported = sXAvcProfileTable;
            if (NTSC.equals(sBroadcastSystem)) {
                result = XAVC_50M_60P;
            } else {
                result = XAVC_50M_50P;
            }
        } else if (XAVC_S_4K.equals(format)) {
            supported = sXAvc4KProfileTable;
            if (NTSC.equals(sBroadcastSystem)) {
                result = XAVC_4K_60M_30P;
            } else {
                result = XAVC_4K_60M_25P;
            }
        }
        if (supported != null && supported.size() > 0) {
            if (!supported.containsKey(result)) {
                Iterator<String> it = supported.keySet().iterator();
                String result4 = it.next();
                result = result4;
                StringBuilder builder = sBuilders.get();
                builder.replace(0, builder.length(), MSG_APP_DEFAULT_VALUE_NOT_FOUND).append(result);
                Log.w(TAG, builder.toString());
            }
        } else {
            StringBuilder builder2 = sBuilders.get();
            builder2.replace(0, builder2.length(), MSG_APP_DEFAULT_VALUE_NOT_FOUND).append(result);
            Log.e(TAG, builder2.toString());
        }
        return result;
    }

    protected boolean setAvailableSetting() {
        List<String> availableSetting = getAvailableValue("record_setting");
        if (availableSetting == null) {
            return false;
        }
        String movieRecordSetting = getValue("record_setting");
        if (movieRecordSetting != null && availableSetting.contains(movieRecordSetting)) {
            return false;
        }
        String format = getValue("movie_format");
        if (movieRecordSetting != null) {
            String key = Settings.convToKey(format);
            if (this.mUserSetting.get(key) == null) {
                if (this.mUserSetting.get(Settings.KEY_FORMAT) == null) {
                    this.mUserSetting.set(Settings.KEY_FORMAT, format);
                }
                StringBuilder builder = sBuilders.get();
                builder.replace(0, builder.length(), MSG_REMEMBER_UNAVAILABLE_SETTING).append(movieRecordSetting);
                Log.i(TAG, builder.toString());
                this.mUserSetting.set(key, movieRecordSetting);
            }
        }
        String value = getDefaultSetting(format);
        if (!availableSetting.contains(value)) {
            value = availableSetting.get(0);
        }
        StringBuilder builder2 = sBuilders.get();
        builder2.replace(0, builder2.length(), MSG_SET_AVAILABLE_SETTING).append(value);
        Log.w(TAG, builder2.toString());
        MediaRecorder.CamcorderProfile profile = appRecordSettingToPfProfile(value);
        if (profile == null) {
            return false;
        }
        MediaRecorder.Parameters params = new MediaRecorder.Parameters();
        params.setVideoAspectRatio(profile.videoAspectRatio);
        params.setVideoEncodingBitRate(profile.videoEncodingBitRate);
        params.setVideoFrameRate(profile.videoFrameRate);
        params.setVideoSize(profile.videoSize);
        this.mCamSet.setRecorderParameters(params);
        return true;
    }

    public static final boolean isXavcsSupported() {
        return 6 <= Environment.getVersionPfAPI();
    }

    public static final boolean isXavcs4KSupported() {
        return 12 <= Environment.getVersionPfAPI();
    }

    public static final boolean isXavcsHFRSupported() {
        return 12 <= Environment.getVersionPfAPI();
    }

    public static final boolean is152HMP4Format() {
        return 13 <= Environment.getVersionPfAPI();
    }

    public static final boolean isMovieFormatIconSupported() {
        return 13 <= Environment.getVersionPfAPI();
    }

    public static String getBroadcastSystem() {
        return sBroadcastSystem;
    }

    public static boolean isPfIssueAvailable_HfrAndFace() {
        String ModelName = ScalarProperties.getString("model.name");
        if (!"DSC-RX100M3".equals(ModelName) && !"DSC-RX100M4".equals(ModelName) && !"DSC-RX10M2".equals(ModelName) && !"ILCE-7S".equals(ModelName) && !"ILCE-7RM2".equals(ModelName)) {
            return false;
        }
        return true;
    }
}
