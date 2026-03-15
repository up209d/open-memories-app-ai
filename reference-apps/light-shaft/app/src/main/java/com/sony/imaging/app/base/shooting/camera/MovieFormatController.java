package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.MediaRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class MovieFormatController extends AbstractController {
    protected static final String API_GETTER_ASPECT = "getVideoAspectRatio";
    protected static final String API_GETTER_BITRATE = "getVideoEncodingBitRate";
    protected static final String API_GETTER_FORMAT = "getOutputFormat";
    protected static final String API_GETTER_FRAMERATE = "getVideoFrameRate";
    protected static final String API_GETTER_SIZE = "getVideoSize";
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
    public static final String MOVIE_ASPECT = "movie_aspect";
    public static final String MOVIE_FORMAT = "movie_format";
    public static final String MOVIE_RECORD_SETTING = "record_setting";
    public static final String MP4 = "MP4";
    public static final String MP4_1080 = "MP4_1080";
    public static final String MP4_720 = "MP4_720";
    public static final String MP4_VGA = "MP4_VGA";
    private static final String MSG_FORMAT = "format : ";
    private static final String MSG_GET_INIT_PARAM = "onGetInitRecorderParameters ";
    private static final String MSG_ON_CAMERA_REMOVING = "onCameraRemoving ";
    private static final String MSG_ON_CAMERA_SET = "onCameraSet ";
    private static final String MSG_REC_SETTING = "recordset : ";
    private static final String MSG_SET_AVAILABLE_FORMAT = "Current Format is unsupported, so set available format : ";
    private static final String MSG_SET_AVAILABLE_SETTING = "Current Setting is unsupported, so set available setting : ";
    public static final String NTSC = "NTSC";
    public static final String PAL = "PAL";
    public static final String PS_50P = "PS_50p";
    public static final String PS_60P = "PS_60p";
    private static final String TAG = "MovieFormatController";
    public static final String XAVC_50M_100P = "XAVC_50M_100p";
    public static final String XAVC_50M_120P = "XAVC_50M_120p";
    public static final String XAVC_50M_24P = "XAVC_50M_24p";
    public static final String XAVC_50M_25P = "XAVC_50M_25p";
    public static final String XAVC_50M_30P = "XAVC_50M_30p";
    public static final String XAVC_50M_50P = "XAVC_50M_50p";
    public static final String XAVC_50M_60P = "XAVC_50M_60p";
    public static final String XAVC_S = "XAVC_S";
    private static MovieFormatController mInstance;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sAvchdProfileTable;
    protected static String sBroadcastSystem;
    protected static HashMap<String, String> sConvTableForBroadcastSystem;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sMp4ProfileTable;
    protected static HashMap<String, MediaRecorder.CamcorderProfile> sXAvcProfileTable;
    private CameraSetting mCamSet = CameraSetting.getInstance();
    protected Settings mUserSetting = new Settings();
    private static StringBuilder sBuilder = new StringBuilder();
    private static final String myName = MovieFormatController.class.getSimpleName();

    /* loaded from: classes.dex */
    public static class Settings {
        public static final String EQUAL = "=";
        public static final String KEY_FORMAT = "format";
        public static final String KEY_REC_SETTING_AVCHD = "setting_avchd";
        public static final String KEY_REC_SETTING_MP4 = "setting_mpeg4";
        public static final String KEY_REC_SETTING_XAVC_S = "setting_xavc_s";
        public static final String SEMI_COLON = ";";
        private HashMap<String, String> mMap = new HashMap<>();

        public String flatten() {
            if (this.mMap.size() == 0) {
                return "";
            }
            StringBuilder flattened = new StringBuilder();
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
            if (MovieFormatController.AVCHD.equals(format)) {
                return KEY_REC_SETTING_AVCHD;
            }
            if (MovieFormatController.XAVC_S.equals(format)) {
                return KEY_REC_SETTING_XAVC_S;
            }
            if (!MovieFormatController.MP4.equals(format)) {
                return null;
            }
            return KEY_REC_SETTING_MP4;
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

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        MediaRecorder.CamcorderProfile profile;
        if (Environment.isMovieAPISupported()) {
            if (MOVIE_FORMAT.equals(tag)) {
                MediaRecorder.CamcorderProfile profile2 = null;
                this.mUserSetting.set(Settings.KEY_FORMAT, value);
                String recSetting = this.mUserSetting.getRecSetting();
                if (recSetting != null) {
                    profile2 = appRecordSettingToPfProfile(recSetting);
                }
                MediaRecorder.Parameters params = new MediaRecorder.Parameters();
                params.setOutputFormat(appFormatToPF(value));
                if (profile2 != null) {
                    params.setVideoAspectRatio(profile2.videoAspectRatio);
                    params.setVideoEncodingBitRate(profile2.videoEncodingBitRate);
                    params.setVideoFrameRate(profile2.videoFrameRate);
                    params.setVideoSize(profile2.videoSize);
                }
                this.mCamSet.setRecorderParameters(params);
                if (profile2 == null) {
                    setAvailableSetting();
                    return;
                }
                return;
            }
            if (MOVIE_RECORD_SETTING.equals(tag) && (profile = appRecordSettingToPfProfile(value)) != null) {
                String key = Settings.convToKey(getValue(MOVIE_FORMAT));
                this.mUserSetting.set(key, value);
                MediaRecorder.Parameters params2 = new MediaRecorder.Parameters();
                params2.setVideoAspectRatio(profile.videoAspectRatio);
                params2.setVideoEncodingBitRate(profile.videoEncodingBitRate);
                params2.setVideoFrameRate(profile.videoFrameRate);
                params2.setVideoSize(profile.videoSize);
                this.mCamSet.setRecorderParameters(params2);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        MediaRecorder.Parameters params;
        String value = null;
        if (Environment.isMovieAPISupported() && (params = this.mCamSet.getRecorderParameters()) != null) {
            if (MOVIE_FORMAT.equals(tag)) {
                return pfFormatToApp(params.getOutputFormat());
            }
            if (MOVIE_RECORD_SETTING.equals(tag)) {
                value = pfProfileToApp(params.getOutputFormat(), params.getVideoEncodingBitRate(), params.getVideoFrameRate(), params.getVideoSize(), params.getVideoAspectRatio());
            } else if (MOVIE_ASPECT.equals(tag)) {
                value = params.getVideoAspectRatio();
            }
        }
        return value;
    }

    public static void createSupportedProfileTable(MediaRecorder.Parameters supported) {
        List<MediaRecorder.CamcorderProfile> supportedList;
        boolean bIsXavcSupported = isXavcsSupported();
        if ((sAvchdProfileTable == null || sMp4ProfileTable == null || (bIsXavcSupported && sXAvcProfileTable == null)) && supported != null && (supportedList = supported.getSupportedCamcoderProfiles()) != null) {
            sAvchdProfileTable = new HashMap<>();
            sMp4ProfileTable = new HashMap<>();
            if (bIsXavcSupported) {
                sXAvcProfileTable = new HashMap<>();
            }
            for (MediaRecorder.CamcorderProfile profile : supportedList) {
                if (sBroadcastSystem == null) {
                    sBroadcastSystem = decideBroadcastSystemByFR(profile.videoFrameRate);
                }
                String value = pfProfileToApp(profile.outputFormat, profile.videoEncodingBitRate, profile.videoFrameRate, profile.videoSize, profile.videoAspectRatio);
                if (value != null) {
                    if (AVCHD.equals(profile.outputFormat)) {
                        sAvchdProfileTable.put(value, profile);
                    } else if ("MPEG4".equals(profile.outputFormat)) {
                        sMp4ProfileTable.put(value, profile);
                    } else if (bIsXavcSupported && "XAVCS".equals(profile.outputFormat)) {
                        sXAvcProfileTable.put(value, profile);
                    }
                }
            }
            createConvTable(sBroadcastSystem);
        }
    }

    public static String decideBroadcastSystemByFR(String framerate) {
        if ("60i".equals(framerate) || "60p".equals(framerate) || "30p".equals(framerate) || "24p".equals(framerate) || "120p".equals(framerate)) {
            return NTSC;
        }
        if (!"50i".equals(framerate) && !"50p".equals(framerate) && !"25p".equals(framerate) && !"100p".equals(framerate)) {
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
            clearConvTable();
        }
    }

    protected static void createConvTable(String to) {
        if (sConvTableForBroadcastSystem == null) {
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
            }
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
    public void onCameraSet() {
        if (Environment.isMovieAPISupported()) {
            createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
            String flattened = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_FORMAT_PARAMETERS, null);
            if (flattened != null) {
                sBuilder.replace(0, sBuilder.length(), MSG_ON_CAMERA_SET).append(flattened);
                Log.i(TAG, sBuilder.toString());
                this.mUserSetting.unflatten(flattened);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        if (Environment.isMovieAPISupported()) {
            String flattened = this.mUserSetting.flatten();
            sBuilder.replace(0, sBuilder.length(), MSG_ON_CAMERA_REMOVING).append(flattened);
            Log.i(TAG, sBuilder.toString());
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_FORMAT_PARAMETERS, flattened);
            clearSupportedProfileTable();
        }
        sBuilder.setLength(0);
        sBuilder.trimToSize();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        List<String> availableFormat;
        if (Environment.isMovieAPISupported()) {
            String format = this.mUserSetting.get(Settings.KEY_FORMAT);
            List<String> supported = getSupportedValue(MOVIE_FORMAT);
            if (format != null) {
                sBuilder.replace(0, sBuilder.length(), MSG_GET_INIT_PARAM).append(MSG_FORMAT).append(format);
                Log.i(TAG, sBuilder.toString());
                if (supported == null || !supported.contains(format)) {
                    format = null;
                }
            }
            if (format == null && (format = pfFormatToApp(this.mCamSet.getRecorderParameters().getOutputFormat())) == null && (availableFormat = getAvailableValue(MOVIE_FORMAT)) != null) {
                if (availableFormat.contains(AVCHD)) {
                    format = AVCHD;
                } else {
                    format = availableFormat.get(0);
                }
                sBuilder.replace(0, sBuilder.length(), MSG_SET_AVAILABLE_FORMAT).append(format);
                Log.w(TAG, sBuilder.toString());
            }
            if (format != null) {
                MediaRecorder.Parameters forceSettingParams = new MediaRecorder.Parameters();
                forceSettingParams.setOutputFormat(appFormatToPF(format));
                this.mCamSet.setRecorderParameters(forceSettingParams);
            }
            boolean isProfileSet = false;
            String recordSet = this.mUserSetting.get(Settings.convToKey(format));
            if (recordSet != null) {
                sBuilder.replace(0, sBuilder.length(), MSG_GET_INIT_PARAM).append(MSG_FORMAT).append(format).append(MSG_REC_SETTING).append(recordSet);
                Log.i(TAG, sBuilder.toString());
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
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermRecorderParameters(MediaRecorder.Parameters params) {
    }

    public static String pfFormatToApp(String pf) {
        if (AVCHD.equals(pf)) {
            return AVCHD;
        }
        if ("MPEG4".equals(pf)) {
            return MP4;
        }
        if (!isXavcsSupported() || !"XAVCS".equals(pf)) {
            return null;
        }
        return XAVC_S;
    }

    public static String appFormatToPF(String app) {
        if (AVCHD.equals(app)) {
            return AVCHD;
        }
        if (MP4.equals(app)) {
            return "MPEG4";
        }
        if (!XAVC_S.equals(app) || !isXavcsSupported()) {
            return null;
        }
        return "XAVCS";
    }

    public static String pfProfileToApp(String format, String bitrate, String framerate, String size, String aspect) {
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
                return MP4_720;
            }
            if (!"VGA".equals(size)) {
                return null;
            }
            return MP4_VGA;
        }
        if (!isXavcsSupported() || !"XAVCS".equals(format) || !"50M".equals(bitrate)) {
            return null;
        }
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
        if (!"100p".equals(framerate)) {
            return null;
        }
        return XAVC_50M_100P;
    }

    public static MediaRecorder.CamcorderProfile appRecordSettingToPfProfile(String value) {
        MediaRecorder.CamcorderProfile profile = null;
        String value2 = convertByBroadcastSystem(sBroadcastSystem, value);
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
            return profile4;
        }
        return profile;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> result = null;
        if (Environment.isMovieAPISupported()) {
            if (MOVIE_FORMAT.equals(tag)) {
                createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
                result = new ArrayList<>();
                if (sAvchdProfileTable != null && sAvchdProfileTable.size() > 0) {
                    result.add(AVCHD);
                }
                if (sMp4ProfileTable != null && sMp4ProfileTable.size() > 0) {
                    result.add(MP4);
                }
                if (sXAvcProfileTable != null && sXAvcProfileTable.size() > 0) {
                    result.add(XAVC_S);
                }
            } else if (MOVIE_RECORD_SETTING.equals(tag)) {
                createSupportedProfileTable(this.mCamSet.getSupportedRecorderParameters());
                String fileFormat = getValue(MOVIE_FORMAT);
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
                } else if (2 != ExecutorCreator.getInstance().getRecordingMode()) {
                    if (sAvchdProfileTable != null) {
                        result.addAll(sAvchdProfileTable.keySet());
                    }
                    if (sMp4ProfileTable != null) {
                        result.addAll(sMp4ProfileTable.keySet());
                    }
                    if (sXAvcProfileTable != null) {
                        result.addAll(sXAvcProfileTable.keySet());
                    }
                }
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        HashMap<String, MediaRecorder.CamcorderProfile> table;
        if (!Environment.isMovieAPISupported() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return null;
        }
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            AvailableInfo.update();
            if (MOVIE_FORMAT.equals(tag)) {
                for (String mode : supporteds) {
                    if (AvailableInfo.isAvailable(API_GETTER_FORMAT, mode)) {
                        availables.add(mode);
                    }
                }
            } else if (MOVIE_RECORD_SETTING.equals(tag)) {
                String fileFormat = getValue(MOVIE_FORMAT);
                if (AVCHD.equals(fileFormat)) {
                    table = sAvchdProfileTable;
                } else if (MP4.equals(fileFormat)) {
                    table = sMp4ProfileTable;
                } else {
                    if (XAVC_S.equals(fileFormat) && isXavcsSupported()) {
                        table = sXAvcProfileTable;
                    }
                    return null;
                }
                for (String mode2 : supporteds) {
                    MediaRecorder.CamcorderProfile profile = table.get(mode2);
                    if (AvailableInfo.isAvailable(API_GETTER_BITRATE, profile.videoEncodingBitRate) && AvailableInfo.isAvailable(API_GETTER_FRAMERATE, profile.videoFrameRate) && AvailableInfo.isAvailable(API_GETTER_SIZE, profile.videoSize) && AvailableInfo.isAvailable(API_GETTER_ASPECT, profile.videoAspectRatio)) {
                        availables.add(mode2);
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
        if (!Environment.isMovieAPISupported() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        AvailableInfo.update();
        if (MOVIE_FORMAT.equals(tag)) {
            boolean result = AvailableInfo.isAvailable(API_GETTER_FORMAT, null);
            return result;
        }
        if (MOVIE_RECORD_SETTING.equals(tag)) {
            return AvailableInfo.isAvailable(API_GETTER_BITRATE, null) && AvailableInfo.isAvailable(API_GETTER_FRAMERATE, null) && AvailableInfo.isAvailable(API_GETTER_SIZE, null) && AvailableInfo.isAvailable(API_GETTER_ASPECT, null);
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (!Environment.isMovieAPISupported() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return true;
        }
        if (MOVIE_FORMAT.equals(tag)) {
            AvailableInfo.update();
            boolean isInhibited = isUnavailableAPISceneFactor(API_GETTER_FORMAT, null);
            return isInhibited;
        }
        if (!MOVIE_RECORD_SETTING.equals(tag)) {
            return true;
        }
        AvailableInfo.update();
        boolean isInhibited2 = isUnavailableAPISceneFactor(API_GETTER_BITRATE, null, API_GETTER_FRAMERATE, null, API_GETTER_SIZE, null, API_GETTER_ASPECT, null);
        return isInhibited2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        List<String> supportedList;
        if (2 == ExecutorCreator.getInstance().getRecordingMode() || (supportedList = getSupportedValue(itemId)) == null || supportedList.size() == 0) {
            return 0;
        }
        return -2;
    }

    protected String getDefaultSetting(String format) {
        if (AVCHD.equals(format)) {
            if (NTSC.equals(sBroadcastSystem)) {
                return FH_60I;
            }
            return FH_50I;
        }
        if (MP4.equals(format)) {
            return MP4_1080;
        }
        if (!XAVC_S.equals(format)) {
            return null;
        }
        if (NTSC.equals(sBroadcastSystem)) {
            return XAVC_50M_60P;
        }
        return XAVC_50M_50P;
    }

    protected boolean setAvailableSetting() {
        List<String> availableSetting;
        String movieRecordSetting = getValue(MOVIE_RECORD_SETTING);
        if (movieRecordSetting != null || (availableSetting = getAvailableValue(MOVIE_RECORD_SETTING)) == null) {
            return false;
        }
        String value = getDefaultSetting(getValue(MOVIE_FORMAT));
        if (!availableSetting.contains(value)) {
            value = availableSetting.get(0);
        }
        sBuilder.replace(0, sBuilder.length(), MSG_SET_AVAILABLE_SETTING).append(value);
        Log.w(TAG, sBuilder.toString());
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

    public static String getBroadcastSystem() {
        return sBroadcastSystem;
    }
}
