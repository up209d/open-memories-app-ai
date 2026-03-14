package com.sony.imaging.app.base.shooting.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.comparator.ParameterComparator;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.parameters.BooleanSupportedChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.ExposureModeChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.IntegerValuesChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.IsoSensitivityChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.Keys;
import com.sony.imaging.app.base.shooting.camera.parameters.RangeValueChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.SaturationChecker;
import com.sony.imaging.app.base.shooting.camera.parameters.StringValuesChecker;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.meta.FaceInfo;
import com.sony.scalar.sysutil.ScalarProperties;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class CameraSetting implements CameraEx.ApertureChangeListener, CameraEx.ShutterSpeedChangeListener, CameraEx.AutoISOSensitivityListener, CameraEx.SettingChangedListener, CameraEx.ProgramLineRangeOverListener, CameraEx.FocusLightStateListener, CameraEx.EquipmentCallback, CameraEx.PreviewAnalizeListener, CameraEx.FlashEmittingListener, CameraEx.FocalLengthChangeListener, CameraEx.FaceDetectionListener, CameraEx.AutoSceneModeListener, CameraEx.ProgramLineListener, CameraEx.ErrorCallback {
    public static final int API_VER_SUPPORTS_ANGLE = 12;
    private static final int API_VER_SUPPORTS_ANTI_HAND_BLUR = 3;
    public static final int API_VER_SUPPORTS_CINEMATONE = 3;
    public static final int API_VER_SUPPORTS_MOTIONSHOT = 9;
    private static final int API_VER_SUPPORTS_MOVIE_ANTI_HAND_BLUR = 3;
    public static final int API_VER_SUPPORTS_NDFILTER = 6;
    public static final int API_VER_SUPPORTS_PRO_COLOR = 9;
    public static final int API_VER_SUPPORTS_SILENTSHOOTING = 14;
    public static final int API_VER_SUPPORTS_TRACKING_FOCUS = 8;
    private static final int COMPARATOR_PARAM_NUM_DRO_AUTO_HDR = 4;
    private static final int COMPARATOR_PARAM_NUM_PICTURE_EFFECT = 7;
    private static final int COMPARATOR_PARAM_NUM_PICTURE_QUALITY = 2;
    private static final int COMPARATOR_PARAM_NUM_WB_DETAIL = 3;
    private static final float DEFAULT_EXPOSURE_COMPENSATION_STEP = 0.33333334f;
    private static SparseArray<String> EQUIPMENT_CALLBACK_FLASH_ONOFF_TAGS = null;
    private static SparseArray<String> EQUIPMENT_CALLBACK_TAGS = null;
    public static final int ERROR_FACTOR_UNKNOWN = -1001;
    public static int FOCUS_DRIVE_DIRECTION_FAR = 0;
    public static int FOCUS_DRIVE_DIRECTION_NEAR = 0;
    private static int FOCUS_DRIVE_SPEED = 0;
    public static final int HOLD_STATE = 2;
    private static final int INITVAL = -1;
    public static final int INVALID_FOCAL_LENGTH_VALUE = -100;
    private static final String IS_EMPTY_PARAMETERS = "isEmptyParameters";
    private static final String LOG_CHANGE_FACE_DETECTION_OFF = "avoidPFIssue: face detection mode to off";
    private static final String LOG_MSG_APSC_MODE_CHANGED = "APS-C changed mode = ";
    private static final String LOG_MSG_AUTOISOSENSITIVITYLISTENER_ONCHANGED = "AutoISOSensitivityListener onChanged = ";
    private static final String LOG_MSG_AUTOSCENEMODELISTENER_ONCHANGED = "AutoSceneModeListener.onChanged ";
    private static final String LOG_MSG_CAMERAEX_IS_NULL = "mCameraEX is null.";
    private static final String LOG_MSG_CAMERA_IS_NULL = "mCamera is null.";
    private static final String LOG_MSG_CANCEL_MFASSIST_TIMER = "cancelMfAssistTimer";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_COMPARATOR_NOT_INITIALIZED = "PARAMETER_COMPARATORS has not been initialized.";
    private static final String LOG_MSG_DECREMENTAPERTURE = "decrementAperture";
    private static final String LOG_MSG_DECREMENTPROGRAMLINE = "decrementProgramLine";
    private static final String LOG_MSG_DECREMENTSHUTTERSPEED = "decrementShutterSpeed";
    private static final String LOG_MSG_DEVICETYPE = "deviceType = ";
    private static final String LOG_MSG_DISABLE_APP_SETTING = "disableAppSpecificTypes : ";
    private static final String LOG_MSG_ENABLE_APP_SETTING = "enableAppSpecificTypes : ";
    private static final String LOG_MSG_EQUIPMENTCALLBACK_ONEQUIPMENTCHANGE = "EquipmentCallback.onEquipmentChange ";
    private static final String LOG_MSG_ERROR = "error = ";
    private static final String LOG_MSG_ERRORCALLBACK_ONERROR = "ErrorCallback.onError ";
    private static final String LOG_MSG_FACEDETECTIONLISTENER_ONFACEDETECTED = "FaceDetectionListener.onFaceDetected ";
    private static final String LOG_MSG_FACEINFO = "faceinfo = ";
    private static final String LOG_MSG_FAILED_TO_SET_SETTINGCHANGED_TYPES = "Failed to set enableSettingChangedTypes";
    private static final String LOG_MSG_FLASH_CHARGING = "FlashChargingListener.onChanged ";
    private static final String LOG_MSG_FOCALLENGTHCHANGELISTENER_ONFOCALLENGTHCHANGED = "FocalLengthChangeListener.onFocalLengthChanged ";
    private static final String LOG_MSG_FOCUSLIGHTSTATELISTENER_ONCHANGED = "FocusLightStateListener.onChanged ";
    private static final String LOG_MSG_FOCUS_AREA_CHANGED = "FocusAreaInfo changed";
    private static final String LOG_MSG_FOCUS_AREA_CHANGED_NOT_COME = "FocusAreaInfo notification has not come : ";
    private static final String LOG_MSG_FOCUS_DRIVE_ON_CHANGED = "FocusDrive changed posi/max = ";
    private static final String LOG_MSG_FOCUS_MAGNIGICATION_CHANGED = "FocusMag changed ratio = ";
    private static final String LOG_MSG_FOCUS_RING_CHANGED = "FocusRing changed rotating = ";
    private static final String LOG_MSG_GETAPERTURE = "getAperture = ";
    private static final String LOG_MSG_GETEXPOSURECOMPENSATIONSTEP = "getExposureCompensationStep = ";
    private static final String LOG_MSG_GETFOCUSAREAINFOS_IS_NULL = "getFocusAreaInfos(asp) is null";
    private static final String LOG_MSG_GETSHUTTERSPEED = "getShutterSpeed = ";
    private static final String LOG_MSG_ID = "id = ";
    private static final String LOG_MSG_INCREMENTAPERTURE = "incrementAperture";
    private static final String LOG_MSG_INCREMENTPROGRAMLINE = "incrementProgramLine";
    private static final String LOG_MSG_INCREMENTSHUTTERSPEED = "incrementShutterSpeed";
    private static final String LOG_MSG_INCREMENTSHUTTERSPEED_BY_BULB = "incrementShutterSpeed by BULB";
    private static final String LOG_MSG_INVALIDATE_LENSINFO = "Invalidate lensinfo cache";
    private static final String LOG_MSG_LENGTH = "length = ";
    private static final String LOG_MSG_MFASSIST_TIMEOUT = "onTimeout of MfAssist Timer";
    private static final String LOG_MSG_MFASSIST_TIMER_REQUESTED_UNMAGNIFYING = "MfAssistTimer cannot schedule when unmagnifying";
    private static final String LOG_MSG_MODE = "mode = ";
    private static final String LOG_MSG_ON = "on = ";
    private static final String LOG_MSG_ONAERANGE = "onAERange ";
    private static final String LOG_MSG_ONAERANGE_AE = "ae = ";
    private static final String LOG_MSG_ONAERANGE_AESHUT = "ae_shut = ";
    private static final String LOG_MSG_ONAERANGE_IRIS = "iris = ";
    private static final String LOG_MSG_ONAPERTURECHANGED = "onApertureChanged = ";
    private static final String LOG_MSG_ONEVRANGE_ER_LEVE = "onEVRange er_leve = ";
    private static final String LOG_MSG_ONMETERINGRANGE_METERING = "onMeteringRange metering = ";
    private static final String LOG_MSG_ONSHUTTERSPEEDCHANGED = "onShutterSpeedChanged = ";
    private static final String LOG_MSG_ON_ERROR_LISTENER = "MediaRecorder onErrorListener";
    private static final String LOG_MSG_PARAMTERSARENOTEMPTY = "paramters are not empty.";
    private static final String LOG_MSG_POWER_ZOOM = "PowerZoomListener ";
    private static final String LOG_MSG_READY = "ready = ";
    private static final String LOG_MSG_RECORDER_IS_NULL = "mMediaRecorder is null.";
    private static final String LOG_MSG_RESETPROGRAMLINE = "resetProgramLine";
    private static final String LOG_MSG_ROUND_BRACKET_L = " ( ";
    private static final String LOG_MSG_ROUND_BRACKET_R = " ) ";
    private static final String LOG_MSG_RUNNABLE_OF_FOCUSMAGNIFICATION_CHANGED = "RunnableOnFocusMagnificactionChanged";
    private static final String LOG_MSG_SCORE = "score = ";
    private static final String LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED = "SettingChangedListener.onChanged ";
    private static final String LOG_MSG_SLASH = " / ";
    private static final String LOG_MSG_START_FOCUS_MAGNIGICATION = "startFocusMagnification ratio = ";
    private static final String LOG_MSG_STATUS = "status = ";
    private static final String LOG_MSG_STOP_FOCUS_MAGNIGICATION = "stopFocusMagnification";
    private static final String LOG_MSG_STOP_FOCUS_MAGNIGICATION_ON_CHANGED = "stopPreviewMagnification because off is notified without stopped";
    private static final String LOG_MSG_STORED_LENSINFO = "getLensinfo returns cache";
    private static final String LOG_MSG_STORE_LENSINFO = "Store lensinfo cache";
    private static final String LOG_MSG_TAG = "tag = ";
    private static final String LOG_MSG_TASK_ALREADY_EXIST = "MfAssistTimerTask already exists";
    private static final String LOG_MSG_TYPE = "type = ";
    private static final String LOG_MSG_UPDATE_FOCUS_MAGNIGICATION_POSITION = "updateFocusMagnification reverse : ";
    private static final String LOG_MSG_ZOOMCHANGELISTENER_ONCHANGED = "ZoomChangeListner.onChanged";
    public static final int MAGNIFICATION_ACTUAL_RATIO_1_0 = 100;
    public static final int MAGNIFICATION_RATIO_1_0 = 1;
    public static final int MOVIE = 2;
    private static final String MSG_DUMP_AREA_INFOS = "*** FocusAreaInfos ***";
    private static final String MSG_DUMP_AREA_INFOS_END = "**********************";
    public static final int NEUTRAL_STATE = 1;
    private static final String PARAMETERS_KEY_FOCUS_POINT_X = "focus-point-x";
    protected static final int PF_VER_AVOID_GET_PARAMETERS_ON_SETTING_CHANGED = 8;
    protected static final int PF_VER_FOCUS_HOLD_SUPPORTED = 7;
    private static final int PF_VER_LENSINFO_CACHE_SUPPORTED = 0;
    public static final int RECTIME_INIT_VALUE = -1000;
    private static SparseArray<String[]> SETTING_CHANGE_NOTIFY_TAGS = null;
    private static final Pair<Integer, Integer> SHUTTER_SPEED_BULB;
    public static final int STILL = 1;
    private static final String TAG = "CameraSetting";
    public static final int TOGGLE_STATE = 3;
    public static final int UNSTABLE = 32768;
    private static AFMFSWNotificationListener mAFMFSWNotifyListener;
    private static int mFlashChargeState;
    private static Set<String> mIgnoreBackupList;
    private static Set<String> mIgnoreFormList;
    private static HashMap<String, SpecialOperate> mSpecialFormList;
    private static HashMap<String, SpecialOperate> mSpecialList;
    protected static SparseArray<String[]> sAppSpecificSettingChangeNotifyTags;
    protected static boolean sAvoidGetParametersOnSettingChanged;
    protected static Object sLockForSettingChangedListener;
    private static HashMap<String, ISupportedParameterChecker> sParamCheckerList;
    private List<ParameterComparator<Pair<Camera.Parameters, CameraEx.ParametersModifier>>> CAMERA_PARAMETER_COMPARATORS;
    private List<ParameterComparator<MediaRecorder.Parameters>> RECORDER_PARAMETER_COMPARATORS;
    private AELUpdateRunnable mAELUpdateRunnable;
    private CameraEx.ApertureInfo mApertureInfo;
    private String mAutoSceneMode;
    private Camera.Parameters mBackupParameters;
    private Camera mCamera;
    private int mCameraError;
    private CameraEx mCameraEx;
    private Pair<Camera.Parameters, CameraEx.ParametersModifier> mCameraParams;
    private String mCurrentZoomType;
    private int mDigitalPosition;
    private int mDigitalZoomMagnification;
    private DispModeNotificationListener mDispModeNotifyListener;
    private int mFocalLength;
    private int mFocalLengthByZoomInfo;
    private int mFocusCurrentPosition;
    private int mFocusMaxPosition;
    private IncrementShutterRunnable mIncrementShutterRunnable;
    private Pair<Camera.Parameters, CameraEx.ParametersModifier> mInitialParams;
    private boolean mIsAvailableMagnificationPointMove;
    private boolean mIsProgramLineAdjusted;
    private boolean mIsZoomModeInitialized;
    private boolean mIsZoomStopped;
    private Handler mMainLooperHandler;
    private MediaRecorder mMediaRecorder;
    private Pair<Camera.Parameters, CameraEx.ParametersModifier> mMovieSupportedParams;
    private int mOpticalPosition;
    private int mOpticalZoomMagnification;
    private int mPowerZoomState;
    private MediaRecorder.Parameters mRecorderBackupParameters;
    private MediaRecorder.Parameters mRecorderParameters;
    private Pair<Integer, Integer> mShutterSpeed;
    private CameraEx.ShutterSpeedInfo mShutterSpeedInfo;
    private Pair<Camera.Parameters, CameraEx.ParametersModifier> mStillSupportedParams;
    private MediaRecorder.Parameters mSupportedRecorderParameters;
    private FaceInfo[] mfaceInfos;
    protected static StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static CameraSetting mSetting = new CameraSetting();
    private static CameraNotificationManager mNotify = CameraNotificationManager.getInstance();
    protected static HashMap<String, Class<? extends AbstractController>> mControllers = new HashMap<>();
    protected static ArrayList<String> mControllerOrder = new ArrayList<>();
    static int mAELButtonCtrlState = 1;
    static boolean mAELState = false;
    static boolean mS1OnAELock = false;
    static int mFocusControlState = 1;
    static boolean mNDFilterStatus = false;
    static CameraEx.TrackingFocusInfo mTrackingFocusInfo = null;
    private static int[] FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43 = {0, 7, 4, 6, 3, 1, 2, 8, 5, 9};
    private static int PF_VER_SUPPPORTS_IMAGER_ASPECT = 3;
    private static int PF_VER_FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43 = 4;
    private static final List<String> DIGITAL_ZOOM_TYPES = new ArrayList();
    private int mCurrentMode = 32768;
    private boolean mIsParameterInitialized = false;
    protected HashMap<String, AbstractController> mControllerInstances = new HashMap<>();
    protected boolean mIsListenerInitialized = false;
    private boolean mAeErr = true;
    private boolean mAeShutErr = true;
    private boolean mIrisErr = true;
    private boolean mIsMeteringInRange = true;
    private boolean mIsFlashEmit = false;
    private boolean mFlashExternalEnable = false;
    private boolean mFlashInternalEnable = false;
    private int mErLevel = 0;
    private int mISOSensitivityAuto = 0;
    protected volatile CameraEx.LensInfo mLensInfo = null;
    protected boolean mIsLensInfoCacheEnabled = false;
    protected boolean mIsLensInfoCacheEnabledByUser = true;
    private UpdateMediaRemainingRunnable mUpdateMediaRemainingRunnable = new UpdateMediaRemainingRunnable();
    private volatile boolean mIsFocusAreaInfosForViewPatternDirty = false;
    private SparseArray<SparseArray<CameraEx.FocusAreaInfos[]>> mFocusAreaInfosForViewPattern = new SparseArray<>();
    private ConcurrentHashMap<String, CameraEx.FocusAreaInfos> mFocusAreaInfosForAPIVer1 = new ConcurrentHashMap<>();
    private boolean mNeedToResetProgramLine = false;
    boolean mIsFocusMagnificationStarting = false;
    int mFocusMagnificationRatio = 0;
    int mFocusMagnificationActualRatio = 0;
    Pair<Integer, Integer> mFocusedPositionOnRingRotated = null;
    Pair<Integer, Integer> mMfLastMagnifyingPosition = null;
    Pair<Integer, Integer> mMagnifyingPosition = null;
    boolean mIsFocusRingRotating = false;
    Timer mMfAssistTimer = null;
    MfAssistTimeoutTask mMfAssistTimeoutTask = null;
    private int mFocusMagnificationNextRatio = 0;
    boolean mIsDigitalZoomAtMagnifyStarting = false;
    private boolean mBlockMagnifyingStopbyDigitalZoom = false;
    boolean mUseMagnifyRatio1_0 = false;
    boolean mChangeZoomRatio = false;
    private int mDigitalZoomRatio = 0;
    String mAutoApscMode = null;
    private int mMovieRecTime = -1000;
    private int mMovieRecRemainTime = -1000;
    private int mMovieRecErrorFactor = -1001;

    /* loaded from: classes.dex */
    public interface ComparatorCommand {
        void execute();
    }

    static {
        DIGITAL_ZOOM_TYPES.add(DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART);
        DIGITAL_ZOOM_TYPES.add(DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION);
        DIGITAL_ZOOM_TYPES.add(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION);
        mIgnoreBackupList = new HashSet();
        mIgnoreBackupList.add("atogachi-mode");
        mIgnoreBackupList.add(IS_EMPTY_PARAMETERS);
        mIgnoreBackupList.add(Keys.KEY_FOCUS_AREA_MODE);
        mSpecialList = new HashMap<>();
        mSpecialList.put(Keys.KEY_WHITE_BALANCE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.1
            private static final String P1 = "light-balance-for-white-balance";
            private static final String P2 = "color-compensation-for-white-balance";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera.Parameters from, Camera.Parameters to) {
                if (from.get("light-balance-for-white-balance") == null) {
                    to.remove("light-balance-for-white-balance");
                }
                if (from.get("color-compensation-for-white-balance") == null) {
                    to.remove("color-compensation-for-white-balance");
                }
                to.set(Keys.KEY_WHITE_BALANCE, from.get(Keys.KEY_WHITE_BALANCE));
            }
        });
        mSpecialList.put(PARAMETERS_KEY_FOCUS_POINT_X, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.2
            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera.Parameters from, Camera.Parameters to) {
                DisplayModeObserver dispObserver = DisplayModeObserver.getInstance();
                int fromX = from.getInt(CameraSetting.PARAMETERS_KEY_FOCUS_POINT_X);
                if (true == dispObserver.isPanelReverse()) {
                    fromX = -fromX;
                }
                to.set(CameraSetting.PARAMETERS_KEY_FOCUS_POINT_X, fromX);
            }
        });
        mIgnoreFormList = new HashSet();
        mSpecialFormList = new HashMap<>();
        if (2 > getPfApiVersion()) {
            mSpecialFormList.put(Keys.KEY_AUTO_FOCUS_MODE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.3
                private static final String AF_MODE = "af-mode";
                private static final String FOCUS_MODE = "focus-mode";
                private static final String FOCUS_MODE_AUTO = "auto";

                @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
                void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                    String value = to.get("af-mode");
                    if (value != null) {
                        Camera.Parameters ret = camex.createEmptyParameters();
                        ret.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                        ret.set("focus-mode", "auto");
                        cam.setParameters(ret);
                        Camera.Parameters ret2 = camex.createEmptyParameters();
                        ret2.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                        ret2.set("af-mode", value);
                        cam.setParameters(ret2);
                        to.remove("af-mode");
                    }
                }
            });
        }
        mSpecialFormList.put(Keys.KEY_FLASH_MODE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.4
            private static final String FLASH_MODE = "flash-mode";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                String value = to.get("flash-mode");
                if (value != null) {
                    Camera.Parameters ret = camex.createEmptyParameters();
                    ret.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                    ret.set("flash-mode", value);
                    cam.setParameters(ret);
                }
            }
        });
        mSpecialFormList.put(Keys.KEY_PICTURE_SIZE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.5
            private static final String PICTURE_SIZE = "picture-size";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                String value = to.get("picture-size");
                if (value != null) {
                    Camera.Parameters ret = camex.createEmptyParameters();
                    ret.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                    ret.set("picture-size", value);
                    cam.setParameters(ret);
                }
            }
        });
        if (getPfApiVersion() <= 3) {
            mSpecialFormList.put(Keys.KEY_DRIVE_MODE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.6
                private static final String BURST = "burst";
                private static final String DRIVE_MODE = "drive-mode";
                private static final String SINGLE = "single";

                @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
                void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                    String value = to.get("drive-mode");
                    if (value != null) {
                        Camera.Parameters tmp = camex.createEmptyParameters();
                        tmp.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                        if ("single".equals(value)) {
                            tmp.set("drive-mode", "burst");
                        } else if ("burst".equals(value)) {
                            tmp.set("drive-mode", "single");
                        }
                        cam.setParameters(tmp);
                    }
                }
            });
        }
        mSpecialFormList.put(Keys.KEY_SELF_TIMER, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.7
            private static final String REMO_CON = "remocon";
            private static final String SELF_TIMER = "self-timer";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                SpecialOperate op;
                String value = to.get("self-timer");
                if (value != null) {
                    String value_ = to.get("remocon");
                    if (value_ != null && (op = (SpecialOperate) CameraSetting.mSpecialFormList.get("remocon")) != null) {
                        op.run(cam, camex, to);
                    }
                    Camera.Parameters ret = camex.createEmptyParameters();
                    ret.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                    ret.set("self-timer", value);
                    cam.setParameters(ret);
                    to.remove("self-timer");
                }
            }
        });
        mSpecialFormList.put(Keys.KEY_REMOTE_CONTROL, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.8
            private static final String REMO_CON = "remocon";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                String value = to.get("remocon");
                if (value != null) {
                    Camera.Parameters ret = camex.createEmptyParameters();
                    ret.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                    ret.set("remocon", value);
                    cam.setParameters(ret);
                    to.remove("remocon");
                }
            }
        });
        if (getPfApiVersion() <= 2) {
            mSpecialFormList.put("custom-white-balance", new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.9
                private static final String COMPENSATION = "color-compensation-for-white-balance";
                private static final String CUSTOM = "custom";
                private static final String CWB = "custom-white-balance";
                private static final String LIGHTBALANCE = "light-balance-for-white-balance";
                private static final String WB = "whitebalance";

                @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
                void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                    WhiteBalanceController.WhiteBalanceParam option;
                    if (!ExecutorCreator.getInstance().isAssistApp()) {
                        String current = to.get("whitebalance");
                        if (current == null) {
                            current = cam.getParameters().getWhiteBalance();
                        }
                        Camera.Parameters p = camex.createEmptyParameters();
                        CameraEx.ParametersModifier m = camex.createParametersModifier(p);
                        p.set(CameraSetting.IS_EMPTY_PARAMETERS, 1);
                        Pair<Camera.Parameters, CameraEx.ParametersModifier> ret = new Pair<>(p, m);
                        if (CameraSetting.getPfApiVersion() < 2) {
                            option = WhiteBalanceController.getInstance().getWBOptionFromBackUp("custom");
                            ((Camera.Parameters) ret.first).setWhiteBalance("custom");
                            to.remove(CWB);
                        } else {
                            String numofcwb = to.get(CWB);
                            String custom = "custom" + numofcwb;
                            int num = Integer.parseInt(numofcwb);
                            option = WhiteBalanceController.getInstance().getWBOptionFromBackUp(custom);
                            ((Camera.Parameters) ret.first).setWhiteBalance("custom");
                            ((CameraEx.ParametersModifier) ret.second).setCustomWhiteBalance(num);
                            to.remove(CWB);
                        }
                        ((CameraEx.ParametersModifier) ret.second).setLightBalanceForWhiteBalance(option.lightBalance);
                        if ("custom".equals(current)) {
                            to.remove("light-balance-for-white-balance");
                        }
                        ((CameraEx.ParametersModifier) ret.second).setColorCompensationForWhiteBalance(option.colorComp);
                        if ("custom".equals(current)) {
                            to.remove("color-compensation-for-white-balance");
                        }
                        ((CameraEx.ParametersModifier) ret.second).tryIgnoreInhibit();
                        cam.setParameters((Camera.Parameters) ret.first);
                    }
                }
            });
        }
        mSpecialFormList.put(Keys.KEY_FOCUS_MODE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.10
            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                CameraSetting.mAFMFSWNotifyListener.update();
            }
        });
        mSpecialFormList.put(Keys.KEY_COLOR_TEMPERTURE_WHITE_BALANCE, new SpecialOperate() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.11
            private static final String TEMPERTURE = "color-temperture-white-balance";
            private static final String WHTIEBALANCE = "whitebalance";

            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.SpecialOperate
            void run(Camera cam, CameraEx camex, Camera.Parameters to) {
                String current = to.get("whitebalance");
                if (current == null) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> pair = CameraSetting.getInstance().getParameters();
                    current = ((Camera.Parameters) pair.first).get("whitebalance");
                }
                if (!WhiteBalanceController.COLOR_TEMP.equals(current)) {
                    to.remove("color-temperture-white-balance");
                }
            }
        });
        sParamCheckerList = new HashMap<>();
        if (Environment.isAvailableGetSupportedJPEGQuality()) {
            sParamCheckerList.put(Keys.KEY_JPEG_QUALITY, new IntegerValuesChecker(Keys.KEY_JPEG_QUALITY, Keys.KEY_SUPPORTED_JPEG_QUALITIES));
        }
        sParamCheckerList.put(Keys.KEY_WHITE_BALANCE, new StringValuesChecker(Keys.KEY_WHITE_BALANCE));
        sParamCheckerList.put(Keys.KEY_SCENE_MODE, new ExposureModeChecker(Keys.KEY_SCENE_MODE));
        sParamCheckerList.put(Keys.KEY_FLASH_MODE, new StringValuesChecker(Keys.KEY_FLASH_MODE));
        sParamCheckerList.put(Keys.KEY_FOCUS_MODE, new StringValuesChecker(Keys.KEY_FOCUS_MODE));
        sParamCheckerList.put(Keys.KEY_EXPOSURE_COMPENSATION, new RangeValueChecker(Keys.KEY_EXPOSURE_COMPENSATION));
        sParamCheckerList.put(Keys.KEY_ISO_SENSITIVITY, new IsoSensitivityChecker(Keys.KEY_ISO_SENSITIVITY));
        sParamCheckerList.put(Keys.KEY_METERING_MODE, new StringValuesChecker(Keys.KEY_METERING_MODE));
        sParamCheckerList.put(Keys.KEY_FOCUS_AREA_MODE, new StringValuesChecker(Keys.KEY_FOCUS_AREA_MODE));
        sParamCheckerList.put(Keys.KEY_DRO_MODE, new StringValuesChecker(Keys.KEY_DRO_MODE));
        sParamCheckerList.put(Keys.KEY_DRO_LEVEL, new RangeValueChecker(Keys.KEY_DRO_LEVEL));
        sParamCheckerList.put(Keys.KEY_HDR_MODE, new StringValuesChecker(Keys.KEY_HDR_MODE));
        sParamCheckerList.put(Keys.KEY_HDR_EXPOSURE_DIFFERENCE_LEVEL, new RangeValueChecker(Keys.KEY_HDR_EXPOSURE_DIFFERENCE_LEVEL));
        sParamCheckerList.put(Keys.KEY_FLASH_TYPE, new StringValuesChecker(Keys.KEY_FLASH_TYPE));
        sParamCheckerList.put(Keys.KEY_RED_EYE_REDUCTION_MODE, new StringValuesChecker(Keys.KEY_RED_EYE_REDUCTION_MODE));
        sParamCheckerList.put(Keys.KEY_FLASH_COMPENSATION, new RangeValueChecker(Keys.KEY_FLASH_COMPENSATION));
        sParamCheckerList.put(Keys.KEY_COLOR_TEMPERTURE_WHITE_BALANCE, new RangeValueChecker(Keys.KEY_COLOR_TEMPERTURE_WHITE_BALANCE));
        sParamCheckerList.put(Keys.KEY_COLOR_COMPENSATION_FOR_WHITE_BALANCE, new RangeValueChecker(Keys.KEY_COLOR_COMPENSATION_FOR_WHITE_BALANCE));
        sParamCheckerList.put(Keys.KEY_LIGHT_BALANCE_FOR_WHITE_BALANCE, new RangeValueChecker(Keys.KEY_LIGHT_BALANCE_FOR_WHITE_BALANCE));
        sParamCheckerList.put(Keys.KEY_COLOR_MODE, new StringValuesChecker(Keys.KEY_COLOR_MODE));
        sParamCheckerList.put(Keys.KEY_CONTRAST, new RangeValueChecker(Keys.KEY_CONTRAST));
        sParamCheckerList.put(Keys.KEY_SATURATION, new SaturationChecker(Keys.KEY_SATURATION));
        sParamCheckerList.put(Keys.KEY_SHARPNESS, new RangeValueChecker(Keys.KEY_SHARPNESS));
        sParamCheckerList.put("picture-effect", new StringValuesChecker("picture-effect"));
        sParamCheckerList.put(Keys.KEY_PE_TOY_CAMERA_EFFECT, new StringValuesChecker(Keys.KEY_PE_TOY_CAMERA_EFFECT));
        sParamCheckerList.put(Keys.KEY_AUTO_REVIEW_TIME, new StringValuesChecker(Keys.KEY_AUTO_REVIEW_TIME));
        sParamCheckerList.put(Keys.KEY_PICTURE_DESTINATION, new StringValuesChecker(Keys.KEY_PICTURE_DESTINATION));
        sParamCheckerList.put(Keys.KEY_PE_HDR_ART_EFFECT_LEVEL, new RangeValueChecker(Keys.KEY_PE_HDR_ART_EFFECT_LEVEL));
        sParamCheckerList.put(Keys.KEY_PE_POSTERIZATION_EFFECT, new StringValuesChecker(Keys.KEY_PE_POSTERIZATION_EFFECT));
        sParamCheckerList.put(Keys.KEY_PE_PART_COLOR_EFFECT, new StringValuesChecker(Keys.KEY_PE_PART_COLOR_EFFECT));
        sParamCheckerList.put(Keys.KEY_PE_ROUGH_MONO_EFFECT_LEVEL, new RangeValueChecker(Keys.KEY_PE_ROUGH_MONO_EFFECT_LEVEL, Keys.KEY_MAX_PE_ROUGH_MONO_EFFECT_LEVEL, Keys.KEY_MIN_PE_ROUGH_MONO_EFFECT_LEVEL));
        sParamCheckerList.put(Keys.KEY_PE_SOFT_FOCUS_EFFECT_LEVEL, new RangeValueChecker(Keys.KEY_PE_SOFT_FOCUS_EFFECT_LEVEL));
        sParamCheckerList.put(Keys.KEY_PE_MINIATURE_FOCUS_AREA, new StringValuesChecker(Keys.KEY_PE_MINIATURE_FOCUS_AREA));
        sParamCheckerList.put(Keys.KEY_PE_WATERCOLOR_EFFECT_LEVEL, new RangeValueChecker(Keys.KEY_PE_WATERCOLOR_EFFECT_LEVEL));
        sParamCheckerList.put(Keys.KEY_PE_ILLUST_EFFECT_LEVEL, new RangeValueChecker(Keys.KEY_PE_ILLUST_EFFECT_LEVEL, Keys.KEY_MAX_PE_ILLUST_EFFECT_LEVEL, Keys.KEY_MIN_PE_ILLUST_EFFECT_LEVEL));
        sParamCheckerList.put(Keys.KEY_PE_SOFT_HIGH_KEY_EFFECT, new StringValuesChecker(Keys.KEY_PE_SOFT_HIGH_KEY_EFFECT));
        sParamCheckerList.put(Keys.KEY_PE_TOY_CAMERA_TUNING, new RangeValueChecker(Keys.KEY_PE_TOY_CAMERA_TUNING));
        sParamCheckerList.put(Keys.KEY_COLOR_SELECT_MODE, new StringValuesChecker(Keys.KEY_COLOR_SELECT_MODE));
        sParamCheckerList.put(Keys.KEY_FACE_DETECTION_MODE, new StringValuesChecker(Keys.KEY_FACE_DETECTION_MODE));
        sParamCheckerList.put(Keys.KEY_PE_TOY_CAMERA_TUNING, new RangeValueChecker(Keys.KEY_PE_TOY_CAMERA_TUNING));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_SHADING_WHOLE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_SHADING_WHOLE));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_SHADING_WHOLE_MIDDLE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_SHADING_WHOLE_MIDDLE));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_RED, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_RED));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_BLUE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_BLUE));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_MIDDLE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_SHADING_COLOR_MIDDLE));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_CHROMA_RED, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_CHROMA_RED));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_CHROMA_BLUE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_CHROMA_BLUE));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_DISTOTION, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_DISTOTION));
        sParamCheckerList.put(Keys.KEY_LENS_CORRECTION_DISTOTION_MIDDLE, new RangeValueChecker(Keys.KEY_LENS_CORRECTION_DISTOTION_MIDDLE));
        sParamCheckerList.put(Keys.KEY_DRIVE_MODE, new StringValuesChecker(Keys.KEY_DRIVE_MODE));
        sParamCheckerList.put(Keys.KEY_NUM_OF_BURST_PICTURE, new RangeValueChecker(Keys.KEY_NUM_OF_BURST_PICTURE));
        sParamCheckerList.put(Keys.KEY_BURST_DRIVE_SPEED, new StringValuesChecker(Keys.KEY_BURST_DRIVE_SPEED));
        sParamCheckerList.put(Keys.KEY_BRACKET_MODE, new StringValuesChecker(Keys.KEY_BRACKET_MODE));
        sParamCheckerList.put(Keys.KEY_EXPOSURE_BRACKET_MODE, new StringValuesChecker(Keys.KEY_EXPOSURE_BRACKET_MODE));
        sParamCheckerList.put(Keys.KEY_NUM_OF_BRACKET_PICTURE, new IntegerValuesChecker(Keys.KEY_NUM_OF_BRACKET_PICTURE));
        sParamCheckerList.put(Keys.KEY_EXPOSURE_BRACKET_PERIOD, new IntegerValuesChecker(Keys.KEY_EXPOSURE_BRACKET_PERIOD));
        sParamCheckerList.put(Keys.KEY_BRACKET_STEP_PERIOD, new StringValuesChecker(Keys.KEY_BRACKET_STEP_PERIOD));
        sParamCheckerList.put(Keys.KEY_BRACKET_ORDER, new StringValuesChecker(Keys.KEY_BRACKET_ORDER));
        sParamCheckerList.put(Keys.KEY_SELF_TIMER, new IntegerValuesChecker(Keys.KEY_SELF_TIMER));
        sParamCheckerList.put(Keys.KEY_AE_LOCK, new StringValuesChecker(Keys.KEY_AE_LOCK));
        sParamCheckerList.put(Keys.KEY_PICTURE_STORAGE_FMT, new StringValuesChecker(Keys.KEY_PICTURE_STORAGE_FMT));
        sParamCheckerList.put(Keys.KEY_ANTI_BLUR, new StringValuesChecker(Keys.KEY_ANTI_BLUR));
        sParamCheckerList.put(Keys.KEY_REMOTE_CONTROL, new BooleanSupportedChecker(Keys.KEY_REMOTE_CONTROL));
        sParamCheckerList.put(Keys.KEY_AUTO_FOCUS_MODE, new StringValuesChecker(Keys.KEY_AUTO_FOCUS_MODE));
        sParamCheckerList.put(Keys.KEY_SHOOTING_PREVIEW_MODE, new StringValuesChecker(Keys.KEY_SHOOTING_PREVIEW_MODE));
        sParamCheckerList.put(Keys.KEY_OVF_PREVIEW_MODE, new BooleanSupportedChecker(Keys.KEY_OVF_PREVIEW_MODE));
        sParamCheckerList.put(Keys.KEY_ZOOM_DRIVE_TYPE, new StringValuesChecker(Keys.KEY_ZOOM_DRIVE_TYPE));
        sParamCheckerList.put(Keys.KEY_SMART_ZOOM, new DigitalZoomTypeChecker(Keys.KEY_SMART_ZOOM));
        sParamCheckerList.put(Keys.KEY_SUPER_RESOLUTION_ZOOM, new DigitalZoomTypeChecker(Keys.KEY_SUPER_RESOLUTION_ZOOM));
        sParamCheckerList.put(Keys.KEY_PRECISION_ZOOM, new DigitalZoomTypeChecker(Keys.KEY_PRECISION_ZOOM));
        sParamCheckerList.put(Keys.KEY_APSC_MODE, new BooleanSupportedChecker(Keys.KEY_APSC_MODE, Keys.KEY_APSC_MODE_SUPPORTED));
        sParamCheckerList.put(Keys.KEY_IMAGE_SIZE, new StringValuesChecker(Keys.KEY_IMAGE_SIZE));
        sParamCheckerList.put(Keys.KEY_IMAGE_ASPECT_RATIO, new StringValuesChecker(Keys.KEY_IMAGE_ASPECT_RATIO));
        SHUTTER_SPEED_BULB = new Pair<>(Integer.valueOf(Info.INVALID_CAUTION_ID), 1);
        mFlashChargeState = 0;
        SETTING_CHANGE_NOTIFY_TAGS = new SparseArray<>();
        SETTING_CHANGE_NOTIFY_TAGS.append(4, new String[]{CameraNotificationManager.AE_LOCK_BUTTON});
        SETTING_CHANGE_NOTIFY_TAGS.append(8, new String[]{"AutoFocusMode"});
        SETTING_CHANGE_NOTIFY_TAGS.append(1, new String[]{CameraNotificationManager.FACE_DETECTION_MODE});
        SETTING_CHANGE_NOTIFY_TAGS.append(3, new String[]{CameraNotificationManager.FLASH_CHANGE});
        SETTING_CHANGE_NOTIFY_TAGS.append(2, new String[]{CameraNotificationManager.FOCUS_CHANGE});
        SETTING_CHANGE_NOTIFY_TAGS.append(6, new String[]{CameraNotificationManager.ISO_SENSITIVITY});
        SETTING_CHANGE_NOTIFY_TAGS.append(5, new String[]{"MeteringMode"});
        SETTING_CHANGE_NOTIFY_TAGS.append(9, new String[]{CameraNotificationManager.SCENE_MODE});
        SETTING_CHANGE_NOTIFY_TAGS.append(7, new String[]{CameraNotificationManager.AUTO_FOCUS_AREA});
        SETTING_CHANGE_NOTIFY_TAGS.append(11, new String[]{CameraNotificationManager.DRIVE_MODE});
        SETTING_CHANGE_NOTIFY_TAGS.append(12, new String[]{CameraNotificationManager.DRIVE_MODE});
        SETTING_CHANGE_NOTIFY_TAGS.append(13, new String[]{CameraNotificationManager.DRIVE_MODE, CameraNotificationManager.IR_CONTROL_MODE_CHANGED});
        if (1 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(15, new String[]{CameraNotificationManager.ZOOM_MAX_MAG_CHANGED});
        }
        if (3 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(19, new String[]{CameraNotificationManager.ANTI_HAND_BLUR});
        }
        if (3 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(22, new String[]{CameraNotificationManager.ANTI_HAND_BLUR});
        }
        if (3 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(20, new String[]{CameraNotificationManager.CINEMA_TONE_CHANGED});
        }
        if (6 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(23, new String[]{CameraNotificationManager.NDFILTER_CHANGED});
        }
        if (9 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(27, new String[]{CameraNotificationManager.DRIVE_MODE});
        }
        if (9 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(24, new String[]{CameraNotificationManager.COLOR_MODE_CHANGED});
            SETTING_CHANGE_NOTIFY_TAGS.append(25, new String[]{CameraNotificationManager.PRO_COLOR_MODE_CHANGED});
        }
        if (8 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(26, new String[]{CameraNotificationManager.TRACKING_FOCUS_CHANGED});
        }
        if (12 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(30, new String[]{CameraNotificationManager.ANGLE_CHANGED});
        }
        if (14 <= getPfApiVersion()) {
            SETTING_CHANGE_NOTIFY_TAGS.append(38, new String[]{CameraNotificationManager.SILENT_SHUTTER_SHETTING_CHANGED});
        }
        sLockForSettingChangedListener = new Object();
        sAppSpecificSettingChangeNotifyTags = null;
        sAvoidGetParametersOnSettingChanged = false;
        sAvoidGetParametersOnSettingChanged = 8 <= Environment.getVersionPfAPI();
        EQUIPMENT_CALLBACK_TAGS = new SparseArray<>();
        EQUIPMENT_CALLBACK_TAGS.append(1, CameraNotificationManager.DEVICE_LENS_CHANGED);
        EQUIPMENT_CALLBACK_TAGS.append(2, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED);
        EQUIPMENT_CALLBACK_TAGS.append(3, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED);
        EQUIPMENT_CALLBACK_FLASH_ONOFF_TAGS = new SparseArray<>();
        EQUIPMENT_CALLBACK_FLASH_ONOFF_TAGS.append(2, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF);
        EQUIPMENT_CALLBACK_FLASH_ONOFF_TAGS.append(3, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF);
        FOCUS_DRIVE_DIRECTION_NEAR = 0;
        FOCUS_DRIVE_DIRECTION_FAR = 1;
        FOCUS_DRIVE_SPEED = 4;
    }

    private CameraSetting() {
        this.mIncrementShutterRunnable = new IncrementShutterRunnable();
        this.mAELUpdateRunnable = new AELUpdateRunnable();
        createParameterComparatorList();
        this.mDispModeNotifyListener = new DispModeNotificationListener();
        mAFMFSWNotifyListener = new AFMFSWNotificationListener();
        this.mMainLooperHandler = new Handler(Looper.getMainLooper());
    }

    public static CameraSetting getInstance() {
        return mSetting;
    }

    public static void registController(String name, Class<? extends AbstractController> controller) {
        mControllers.put(name, controller);
        if (!mControllerOrder.contains(name)) {
            mControllerOrder.add(name);
        }
    }

    public static Class<? extends AbstractController> getController(String name) {
        return mControllers.get(name);
    }

    public static ArrayList<String> getControllerOrger() {
        return mControllerOrder;
    }

    public void setCamera(CameraEx cameraEx) {
        this.mCurrentMode = convertExecutorDef2CamSetDef(ExecutorCreator.getInstance().getRecordingMode());
        this.mCameraEx = cameraEx;
        this.mCamera = cameraEx.getNormalCamera();
        if (Environment.isMovieAPISupported()) {
            Camera.Parameters p = this.mCameraEx.getSupportedParameters(0);
            this.mStillSupportedParams = new Pair<>(p, this.mCameraEx.createParametersModifier(p));
            Camera.Parameters p2 = this.mCameraEx.getSupportedParameters(1);
            this.mMovieSupportedParams = new Pair<>(p2, this.mCameraEx.createParametersModifier(p2));
            this.mCameraEx.withSupportedParameters(false);
            this.mCameraParams = getCameraParameterPair();
        } else {
            this.mStillSupportedParams = getCameraParameterPair();
            this.mCameraParams = this.mStillSupportedParams;
            this.mCameraEx.withSupportedParameters(false);
        }
        this.mInitialParams = getCameraInitialParameterPair();
        AbstractController.clearCache();
        initializeControllers();
        initializeCameraParameters();
    }

    protected void initializeCameraParameters() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> facep = getEmptyParameters();
        ((CameraEx.ParametersModifier) facep.second).setFaceFrameRendering(true);
        this.mCamera.setParameters((Camera.Parameters) facep.first);
        Camera.Parameters forSetCamera = this.mCameraEx.createEmptyParameters();
        boolean needUpdateParameter = false;
        String flatten = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_PARAMETERS, null);
        if (flatten != null && flatten.length() != 0) {
            Camera.Parameters bkup = getBackupParameter();
            needUpdateParameter = true;
            bkup.unflatten(flatten);
            copy(bkup, forSetCamera);
            String xString = forSetCamera.get(PARAMETERS_KEY_FOCUS_POINT_X);
            if (xString != null) {
                int x = Integer.valueOf(xString).intValue();
                DisplayModeObserver dispObserver = DisplayModeObserver.getInstance();
                if (true == dispObserver.isPanelReverse()) {
                    x = -x;
                }
                forSetCamera.set(PARAMETERS_KEY_FOCUS_POINT_X, x);
            }
        }
        if (ModeDialDetector.hasModeDial()) {
            needUpdateParameter = true;
            setSceneModeToModeDialPosition(forSetCamera);
        }
        String mode = null;
        if (needUpdateParameter) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = new Pair<>(forSetCamera, this.mCameraEx.createParametersModifier(forSetCamera));
            setParameters(p, true);
            mode = ((Camera.Parameters) p.first).getSceneMode();
        }
        setupProgramLine(mode);
        resetFocalLength();
        setControllersInitialParameters();
        this.mIsParameterInitialized = true;
    }

    protected void finalizeCameraParameters() {
        if (this.mIsFocusMagnificationStarting) {
            stopFocusMagnification();
        }
        setControllersTerminatingParameters();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_PARAMETERS, getBackupParameter().flatten());
    }

    private AbstractController newInstance(Class<? extends AbstractController> clazz) {
        AbstractController controller;
        try {
            try {
                AbstractController controller2 = clazz.newInstance();
                controller = controller2;
            } catch (IllegalAccessException e) {
                Constructor<? extends AbstractController> constructor = clazz.getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                controller = constructor.newInstance(new Object[0]);
            }
            return controller;
        } catch (Exception e2) {
            throw new RuntimeException("cannot newInstance: " + clazz.getSimpleName());
        }
    }

    protected void initializeControllers() {
        int c = mControllerOrder.size();
        for (int i = 0; i < c; i++) {
            String name = mControllerOrder.get(i);
            Class<? extends AbstractController> clazz = mControllers.get(name);
            if (clazz != null) {
                AbstractController controller = this.mControllerInstances.get(name);
                if (controller == null) {
                    controller = newInstance(clazz);
                    this.mControllerInstances.put(name, controller);
                }
                controller.onCameraSet();
            } else {
                this.mControllerInstances.put(name, null);
            }
        }
    }

    protected void notifyMediaRecorderSet() {
        int c = mControllerOrder.size();
        for (int i = 0; i < c; i++) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onMediaRecorderSet();
            }
        }
    }

    protected void notifyMediaRecorderRemoving() {
        for (int i = mControllerOrder.size() - 1; i >= 0; i--) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onMediaRecorderRemoving();
            }
        }
    }

    protected void reinitializeControllers() {
        int c = mControllerOrder.size();
        for (int i = 0; i < c; i++) {
            String name = mControllerOrder.get(i);
            Class<? extends AbstractController> clazz = mControllers.get(name);
            if (clazz != null) {
                AbstractController controller = this.mControllerInstances.get(name);
                if (controller == null) {
                    controller = newInstance(clazz);
                    this.mControllerInstances.put(name, controller);
                }
                controller.onCameraReopened();
            } else {
                this.mControllerInstances.put(name, null);
            }
        }
    }

    protected void setControllersInitialParameters() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = getEmptyParameters();
        int c = mControllerOrder.size();
        for (int i = 0; i < c; i++) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onGetInitParameters(p);
            } else {
                setParametersDirect(p);
                p = getEmptyParameters();
            }
        }
        setParametersDirect(p);
    }

    protected void setControllersTerminatingParameters() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = getEmptyParameters();
        for (int i = mControllerOrder.size() - 1; i >= 0; i--) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onGetTermParameters(p);
            } else {
                setParametersDirect(p);
                p = getEmptyParameters();
            }
        }
        setParametersDirect(p);
        this.mIsParameterInitialized = false;
    }

    protected void setControllersInitialRecorderParameters() {
        MediaRecorder.Parameters p = new MediaRecorder.Parameters();
        int c = mControllerOrder.size();
        for (int i = 0; i < c; i++) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onGetInitRecorderParameters(p);
            }
        }
        try {
            Log.i(TAG, "setControllersInitialRecorderParameters " + p.flatten());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String faceDetectionMode = "off";
        boolean avoidPFIssue = false;
        if (MovieFormatController.isPfIssueAvailable_HfrAndFace()) {
            try {
                faceDetectionMode = FaceDetectionController.getInstance().getValue();
            } catch (IController.NotSupportedException e2) {
                e2.printStackTrace();
            }
            String flattened = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_MOVIE_FORMAT_PARAMETERS, null);
            if (flattened != null) {
                if (flattened.indexOf("format=XAVC_S") != -1) {
                    avoidPFIssue = ("off".equals(faceDetectionMode) || (flattened.indexOf("XAVC_S=XAVC_50M_100p") == -1 && flattened.indexOf("XAVC_S=XAVC_50M_120p") == -1 && flattened.indexOf("XAVC_S=XAVC_60M_100p") == -1 && flattened.indexOf("XAVC_S=XAVC_60M_120p") == -1 && flattened.indexOf("XAVC_S=XAVC_100M_100p") == -1 && flattened.indexOf("XAVC_S=XAVC_100M_120p") == -1)) ? false : true;
                }
                if (avoidPFIssue) {
                    FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, "off");
                    Log.i(TAG, LOG_CHANGE_FACE_DETECTION_OFF);
                }
            }
        }
        MediaRecorder.Parameters current = this.mRecorderParameters;
        this.mMediaRecorder.setParameters(p);
        this.mRecorderParameters = this.mMediaRecorder.getParameters();
        try {
            Log.i(TAG, "after setParam " + this.mRecorderParameters.flatten());
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        notifyRecorderParameterChanged(current, this.mRecorderParameters);
        if (MovieFormatController.isPfIssueAvailable_HfrAndFace() && avoidPFIssue) {
            FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, faceDetectionMode);
        }
    }

    protected void setControllersTerminatingRecorderParameters() {
        MediaRecorder.Parameters p = new MediaRecorder.Parameters();
        for (int i = mControllerOrder.size() - 1; i >= 0; i--) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onGetTermRecorderParameters(p);
            }
        }
        try {
            Log.i(TAG, "setControllersTerminatingRecorderParameters " + p.flatten());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaRecorder.Parameters current = this.mRecorderParameters;
        this.mMediaRecorder.setParameters(p);
        this.mRecorderParameters = this.mMediaRecorder.getParameters();
        try {
            Log.i(TAG, "after setParam " + this.mRecorderParameters.flatten());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        notifyRecorderParameterChanged(current, this.mRecorderParameters);
    }

    protected void terminateControllers() {
        for (int i = mControllerOrder.size() - 1; i >= 0; i--) {
            String name = mControllerOrder.get(i);
            AbstractController controller = this.mControllerInstances.get(name);
            if (controller != null) {
                controller.onCameraRemoving();
            }
        }
    }

    public void setMediaRecorder(MediaRecorder recorder) {
        if (Environment.isMovieAPISupported()) {
            this.mMediaRecorder = recorder;
            if (2 == this.mCurrentMode) {
                this.mSupportedRecorderParameters = this.mMediaRecorder.getSupportedParameters();
                this.mRecorderParameters = this.mMediaRecorder.getParameters();
                notifyMediaRecorderSet();
                initializeRecorderParameters();
                return;
            }
            this.mSupportedRecorderParameters = this.mMediaRecorder.getSupportedParameters();
            this.mRecorderParameters = null;
            notifyMediaRecorderSet();
        }
    }

    protected void initializeRecorderParameters() {
        MediaRecorder.Parameters params = new MediaRecorder.Parameters();
        boolean needUpdateParameter = false;
        String flatten = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_RECORDER_PARAMETERS, null);
        if (flatten != null && flatten.length() != 0) {
            MediaRecorder.Parameters bkup = getBackupRecorderParameter();
            needUpdateParameter = true;
            bkup.unflatten(flatten);
            copy(bkup, params);
        }
        if (needUpdateParameter) {
            setRecorderParameters(params);
        }
        setControllersInitialRecorderParameters();
    }

    protected void finalizeRecorderParameters() {
        setControllersTerminatingRecorderParameters();
        try {
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_RECORDER_PARAMETERS, getBackupRecorderParameter().flatten());
        } catch (Exception e) {
        }
    }

    public int getCurrentMode() {
        return this.mCurrentMode;
    }

    public void reopeningCamera(int mode) {
        suspendListeners();
        int from = this.mCurrentMode;
        if (from == 2) {
            finalizeRecorderParameters();
        }
        finalizeCameraParameters();
        this.mCurrentMode = 32768;
    }

    public void onCameraReopened(int mode) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> old = this.mCameraParams;
        MediaRecorder.Parameters oldRecParams = this.mRecorderParameters;
        this.mCameraParams = getCameraParameterPair();
        if (mode == 2) {
            this.mSupportedRecorderParameters = this.mMediaRecorder.getSupportedParameters();
            this.mRecorderParameters = this.mMediaRecorder.getParameters();
        } else {
            this.mSupportedRecorderParameters = this.mMediaRecorder.getSupportedParameters();
            this.mRecorderParameters = null;
        }
        this.mCurrentMode = mode;
        AbstractController.clearCache();
        reinitializeControllers();
        notifyParameterChanged(old, this.mCameraParams);
        notifyRecorderParameterChanged(oldRecParams, this.mRecorderParameters);
        initializeCameraParameters();
        if (mode == 2) {
            initializeRecorderParameters();
        }
        resumeListeners();
    }

    public List<String> getDigitalZoomTypes() {
        return DIGITAL_ZOOM_TYPES;
    }

    private void setSceneModeToModeDialPosition(Camera.Parameters p) {
        ExposureModeController.getInstance().onGetSceneModeOfModeDialPosition(p);
    }

    public CameraEx getCamera() {
        return this.mCameraEx;
    }

    protected static int[] getKeysOfSparseArray(SparseArray<String[]> array) {
        int c = array.size();
        int[] types = new int[c];
        for (int i = 0; i < c; i++) {
            types[i] = array.keyAt(i);
        }
        return types;
    }

    protected static int[] getKeysOfSparseArray(SparseArray<String[]> array, SparseArray<String[]> excluded) {
        if (excluded == null) {
            return getKeysOfSparseArray(array);
        }
        int c = array.size();
        int[] types = new int[c];
        int unique = 0;
        for (int i = 0; i < c; i++) {
            int type = array.keyAt(i);
            if (excluded.indexOfKey(type) < 0) {
                types[i] = type;
                unique++;
            }
        }
        if (unique != c) {
            return Arrays.copyOf(types, unique);
        }
        return types;
    }

    public void initListeners() {
        this.mIsListenerInitialized = true;
        if (32768 != this.mCurrentMode) {
            this.mCameraEx.setApertureChangeListener(this);
            this.mCameraEx.setShutterSpeedChangeListener(this);
            this.mCameraEx.setProgramLineRangeOverListener(this);
            this.mCameraEx.setFocusLightStateListener(this);
            this.mCameraEx.setAutoISOSensitivityListener(this);
            int[] types = getKeysOfSparseArray(SETTING_CHANGE_NOTIFY_TAGS);
            try {
                this.mCameraEx.enableSettingChangedTypes(types);
            } catch (Exception e) {
                Log.w(TAG, LOG_MSG_FAILED_TO_SET_SETTINGCHANGED_TYPES);
                SETTING_CHANGE_NOTIFY_TAGS.remove(19);
                SETTING_CHANGE_NOTIFY_TAGS.remove(22);
                types = getKeysOfSparseArray(SETTING_CHANGE_NOTIFY_TAGS);
                this.mCameraEx.enableSettingChangedTypes(types);
            }
            int[] appTypes = null;
            synchronized (sLockForSettingChangedListener) {
                if (sAppSpecificSettingChangeNotifyTags != null) {
                    appTypes = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                    try {
                        if (appTypes.length > 0) {
                            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                            builder.replace(0, builder.length(), LOG_MSG_ENABLE_APP_SETTING).append(Arrays.toString(appTypes));
                            Log.d(TAG, builder.toString());
                            StringBuilderThreadLocal.releaseScratchBuilder(builder);
                            this.mCameraEx.enableSettingChangedTypes(appTypes);
                        }
                    } catch (Exception e2) {
                        Log.w(TAG, LOG_MSG_FAILED_TO_SET_SETTINGCHANGED_TYPES);
                        sAppSpecificSettingChangeNotifyTags.remove(19);
                        sAppSpecificSettingChangeNotifyTags.remove(22);
                        appTypes = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                        if (appTypes.length > 0) {
                            this.mCameraEx.enableSettingChangedTypes(appTypes);
                        }
                    }
                }
            }
            this.mCameraEx.setSettingChangedListener(this);
            invalidateLensInfoCache();
            if (getPfApiVersion() >= 0) {
                this.mIsLensInfoCacheEnabled = true;
            }
            this.mCameraEx.setEquipmentCallback(this);
            this.mCameraEx.setFlashChargingStateListener(new FlashChargingListener());
            this.mCameraEx.setFlashEmittingListener(this);
            this.mCameraEx.setProgramLineListener(this);
            this.mCameraEx.setErrorCallback(this);
            this.mCameraEx.setAutoSceneModeListener(this);
            this.mCameraEx.setFaceDetectionListener(this);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = getSupportedParameters();
            if (supported == null) {
                supported = this.mStillSupportedParams;
            }
            boolean isSupportedFocalLengthNotify = ((CameraEx.ParametersModifier) supported.second).isSupportedFocalLengthNotify();
            if (isSupportedFocalLengthNotify) {
                this.mCameraEx.setFocalLengthChangeListener(this);
            }
            this.mCameraEx.setPowerZoomListener(new PowerZoomListener());
            registZoomChangeListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mDispModeNotifyListener);
            this.mDispModeNotifyListener.onNotify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
            if (8 > Environment.getVersionPfAPI()) {
                mAFMFSWNotifyListener.update();
                CameraNotificationManager.getInstance().setNotificationListener(mAFMFSWNotifyListener);
            }
            registerFocusAreaChangedListener();
            registerFocusMagnificationListener();
            registerApscModeListener();
            registerFocusDriveListener();
            initMovieListeners();
            registerNDFilterListener();
            registerTrackingFocusListener();
            SceneRecognitionObserver.getInstance().startListener(this.mCameraEx);
            SteadyRecognitionObserver.getInstance().startListener(this.mCameraEx);
            MotionRecognitionObserver.getInstance().startListener(this.mCameraEx);
            onChanged(types, (Camera.Parameters) getParameters().first, this.mCameraEx);
            if (appTypes != null && appTypes.length > 0) {
                onChanged(appTypes, (Camera.Parameters) getParameters().first, this.mCameraEx);
            }
        }
    }

    public void resumeListeners() {
        if (this.mIsListenerInitialized) {
            initListeners();
        }
    }

    public void suspendListeners() {
        if (this.mIsListenerInitialized) {
            this.mCameraEx.setApertureChangeListener((CameraEx.ApertureChangeListener) null);
            this.mCameraEx.setShutterSpeedChangeListener((CameraEx.ShutterSpeedChangeListener) null);
            this.mCameraEx.setProgramLineRangeOverListener((CameraEx.ProgramLineRangeOverListener) null);
            this.mCameraEx.setFocusLightStateListener((CameraEx.FocusLightStateListener) null);
            this.mCameraEx.setAutoISOSensitivityListener((CameraEx.AutoISOSensitivityListener) null);
            this.mCameraEx.setSettingChangedListener((CameraEx.SettingChangedListener) null);
            this.mCameraEx.setEquipmentCallback((CameraEx.EquipmentCallback) null);
            invalidateLensInfoCache();
            if (getPfApiVersion() >= 0) {
                this.mIsLensInfoCacheEnabled = false;
            }
            this.mCameraEx.setFlashChargingStateListener((CameraEx.FlashChargingStateListener) null);
            this.mCameraEx.setFlashEmittingListener((CameraEx.FlashEmittingListener) null);
            this.mCameraEx.setProgramLineListener((CameraEx.ProgramLineListener) null);
            this.mCameraEx.setErrorCallback((CameraEx.ErrorCallback) null);
            this.mCameraEx.setAutoSceneModeListener((CameraEx.AutoSceneModeListener) null);
            this.mCameraEx.setFaceDetectionListener((CameraEx.FaceDetectionListener) null);
            this.mCameraEx.setFocalLengthChangeListener((CameraEx.FocalLengthChangeListener) null);
            this.mCameraEx.setPowerZoomListener((CameraEx.PowerZoomListener) null);
            unregistZoomChangeListener();
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDispModeNotifyListener);
            if (8 > Environment.getVersionPfAPI()) {
                CameraNotificationManager.getInstance().removeNotificationListener(mAFMFSWNotifyListener);
            }
            unregisterFocusAreaChangedListener();
            unregisterFocusMagnificationListener();
            unregisterApscModeListener();
            unregisterFocusDriveListener();
            suspendMovieListeners();
            unregisterNDFIlterListener();
            unregisterTrackingFocusListener();
            SceneRecognitionObserver.getInstance().stopListener(this.mCameraEx);
            SteadyRecognitionObserver.getInstance().stopListener(this.mCameraEx);
            MotionRecognitionObserver.getInstance().stopListener(this.mCameraEx);
        }
    }

    public void removeCamera() {
        if (this.mIsFocusMagnificationStarting) {
            stopFocusMagnification();
        }
        this.mMfAssistTimer = null;
        setControllersTerminatingParameters();
        terminateControllers();
        finalizeListeners();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_PARAMETERS, getBackupParameter().flatten());
        this.mBackupParameters = null;
        this.mCamera = null;
        this.mCameraEx = null;
        this.mMainLooperHandler.removeCallbacksAndMessages(null);
        AbstractController.clearCache();
        if (Environment.isNewBizDeviceLSC()) {
            resetDigitalZoomInfoValue();
            this.mIsZoomModeInitialized = false;
        }
    }

    public void removeMediaRecorder() {
        if (this.mMediaRecorder != null) {
            if (2 == this.mCurrentMode) {
                finalizeRecorderParameters();
            }
            notifyMediaRecorderRemoving();
            try {
                BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_RECORDER_PARAMETERS, getBackupRecorderParameter().flatten());
            } catch (Exception e) {
            }
            this.mRecorderBackupParameters = null;
            this.mMediaRecorder = null;
        }
    }

    private void finalizeListeners() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDispModeNotifyListener);
        this.mIsListenerInitialized = false;
    }

    public Pair<Camera.Parameters, CameraEx.ParametersModifier> getParameters() {
        ExecutorCreator.getInstance().waitChangingRecMode();
        return this.mCameraParams;
    }

    public void updateParameters() {
        this.mCameraParams = getCameraParameterPair();
    }

    public Pair<Camera.Parameters, CameraEx.ParametersModifier> getEmptyParameters() {
        return getEmptyCameraParameterPair();
    }

    public Pair<Camera.Parameters, CameraEx.ParametersModifier> getSupportedParameters(int mode) {
        return 2 == mode ? this.mMovieSupportedParams : this.mStillSupportedParams;
    }

    public Pair<Camera.Parameters, CameraEx.ParametersModifier> getSupportedParameters() {
        return 2 == this.mCurrentMode ? this.mMovieSupportedParams : this.mStillSupportedParams;
    }

    public Pair<Camera.Parameters, CameraEx.ParametersModifier> getInitialParameters() {
        return this.mInitialParams;
    }

    public void setParametersDirect(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (!ExecutorCreator.getInstance().isRecordingModeChanging()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> oldParams = this.mCameraParams;
            if (sAvoidGetParametersOnSettingChanged) {
                synchronized (this) {
                    this.mCamera.setParameters((Camera.Parameters) params.first);
                    this.mCameraParams = getCameraParameterPair();
                }
            } else {
                this.mCamera.setParameters((Camera.Parameters) params.first);
                this.mCameraParams = getCameraParameterPair();
            }
            notifyParameterChanged(oldParams, this.mCameraParams);
            return;
        }
        Log.w(TAG, "CANNOT set the following values, because \"isRecordingModeChanging\" is true.");
        dump(params);
        Log.w(TAG, "StackTrace is the following.");
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stack) {
            Log.w(TAG, stackTraceElement.toString());
        }
    }

    public void setParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (32768 != this.mCurrentMode) {
            setParameters(params, false);
            return;
        }
        Log.w(TAG, "setParameters called in RecordingMode Changing: " + ((Camera.Parameters) params.first).flatten());
        String flatten = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_PARAMETERS, null);
        Camera.Parameters bkup = getBackupParameter();
        if (flatten != null && flatten.length() != 0) {
            bkup.unflatten(flatten);
        }
        write((Camera.Parameters) params.first, bkup);
        try {
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_PARAMETERS, bkup.flatten());
        } catch (Exception e) {
        }
    }

    public void setParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, boolean confirmSupported) {
        Camera.Parameters p = (Camera.Parameters) params.first;
        try {
            p.getInt(IS_EMPTY_PARAMETERS);
        } catch (NumberFormatException e) {
            Log.w(TAG, LOG_MSG_PARAMTERSARENOTEMPTY);
        } catch (IllegalArgumentException e2) {
            Log.w(TAG, LOG_MSG_PARAMTERSARENOTEMPTY);
        }
        if (this.mCamera != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> oldParams = this.mCameraParams;
            write((Camera.Parameters) params.first, getBackupParameter());
            if (sAvoidGetParametersOnSettingChanged) {
                synchronized (this) {
                    Camera.Parameters setParam = form((Camera.Parameters) params.first, confirmSupported);
                    this.mCamera.setParameters(setParam);
                    this.mCameraParams = getCameraParameterPair();
                }
            } else {
                Camera.Parameters setParam2 = form((Camera.Parameters) params.first, confirmSupported);
                this.mCamera.setParameters(setParam2);
                this.mCameraParams = getCameraParameterPair();
            }
            notifyParameterChanged(oldParams, this.mCameraParams);
            return;
        }
        Log.e(TAG, LOG_MSG_CAMERA_IS_NULL);
    }

    public MediaRecorder.Parameters getRecorderParameters() {
        return this.mRecorderParameters;
    }

    public MediaRecorder.Parameters getSupportedRecorderParameters() {
        return this.mSupportedRecorderParameters;
    }

    public void setRecorderParameters(MediaRecorder.Parameters params) {
        if (this.mMediaRecorder == null) {
            Log.e(TAG, LOG_MSG_RECORDER_IS_NULL);
            return;
        }
        write(params, getBackupRecorderParameter());
        MediaRecorder.Parameters current = this.mRecorderParameters;
        try {
            Log.i(TAG, "setRecorderParameters " + params.flatten());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mMediaRecorder.setParameters(params);
        this.mRecorderParameters = this.mMediaRecorder.getParameters();
        try {
            Log.i(TAG, "after setParam " + this.mRecorderParameters.flatten());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        notifyRecorderParameterChanged(current, this.mRecorderParameters);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class SpecialOperate {
        SpecialOperate() {
        }

        void run(Camera.Parameters from, Camera.Parameters to) {
        }

        void run(Camera cam, CameraEx camex, Camera.Parameters to) {
        }
    }

    private void write(Camera.Parameters from, Camera.Parameters to) {
        ParametersTokenizer tokenizer = new ParametersTokenizer(from);
        while (tokenizer.hasMoreElements()) {
            Pair<String, String> kv = tokenizer.nextToken();
            if (kv != null && !mIgnoreBackupList.contains(kv.first)) {
                SpecialOperate op = mSpecialList.get(kv.first);
                if (op != null) {
                    op.run(from, to);
                } else {
                    to.set((String) kv.first, (String) kv.second);
                    Log.i(TAG, "kv.first:" + ((String) kv.first) + " kv.second:" + ((String) kv.second));
                }
            }
        }
    }

    private void write(MediaRecorder.Parameters from, MediaRecorder.Parameters to) {
    }

    private void copy(Camera.Parameters from, Camera.Parameters to) {
        ParametersTokenizer tokenizer = new ParametersTokenizer(from);
        while (tokenizer.hasMoreElements()) {
            Pair<String, String> kv = tokenizer.nextToken();
            if (kv != null) {
                to.set((String) kv.first, (String) kv.second);
            }
        }
    }

    private void copy(MediaRecorder.Parameters from, MediaRecorder.Parameters to) {
        String flattened = from.flatten();
        to.unflatten(flattened);
    }

    /* loaded from: classes.dex */
    public static class ParametersTokenizer {
        private static final int delim = 61;
        private StringTokenizer tokenizer;

        public ParametersTokenizer(Camera.Parameters p) {
            String flattened = p.flatten();
            this.tokenizer = new StringTokenizer(flattened, MovieFormatController.Settings.SEMI_COLON);
        }

        public ParametersTokenizer(MediaRecorder.Parameters p) {
            String flattened = p.flatten();
            this.tokenizer = new StringTokenizer(flattened, MovieFormatController.Settings.SEMI_COLON);
        }

        public ParametersTokenizer(AudioManager.Parameters p) {
            String flattened = p.flatten();
            this.tokenizer = new StringTokenizer(flattened, MovieFormatController.Settings.SEMI_COLON);
        }

        public boolean hasMoreElements() {
            return this.tokenizer.hasMoreElements();
        }

        public Pair<String, String> nextToken() {
            String kv = this.tokenizer.nextToken();
            int pos = kv.indexOf(delim);
            if (pos == -1) {
                Log.i(CameraSetting.TAG, kv);
                return null;
            }
            String k = kv.substring(0, pos);
            String v = kv.substring(pos + 1);
            return new Pair<>(k, v);
        }
    }

    public static void putSupportedParamChecker(String key, ISupportedParameterChecker checker) {
        sParamCheckerList.put(key, checker);
    }

    private Camera.Parameters form(Camera.Parameters from) {
        return form(from, false);
    }

    private Camera.Parameters form(Camera.Parameters from, boolean confirmSupported) {
        Camera.Parameters to = this.mCameraEx.createEmptyParameters();
        to.set(IS_EMPTY_PARAMETERS, 1);
        ParametersTokenizer fromTokenizer = new ParametersTokenizer(from);
        long start = System.currentTimeMillis();
        Camera.Parameters supported = (Camera.Parameters) getSupportedParameters().first;
        while (fromTokenizer.hasMoreElements()) {
            Pair<String, String> kv = fromTokenizer.nextToken();
            if (kv != null) {
                ISupportedParameterChecker checker = null;
                if (confirmSupported) {
                    ISupportedParameterChecker checker2 = sParamCheckerList.get(kv.first);
                    checker = checker2;
                }
                if (checker == null) {
                    to.set((String) kv.first, (String) kv.second);
                } else {
                    checker.check((String) kv.second, supported, to, from);
                }
            }
        }
        long end = System.currentTimeMillis();
        Log.i(TAG, "form " + (end - start));
        ParametersTokenizer toTokenizer = new ParametersTokenizer(to);
        while (toTokenizer.hasMoreElements()) {
            Pair<String, String> kv2 = toTokenizer.nextToken();
            if (mIgnoreFormList.contains(kv2.first)) {
                to.remove((String) kv2.first);
            } else {
                SpecialOperate op = mSpecialFormList.get(kv2.first);
                if (op != null) {
                    op.run(this.mCamera, this.mCameraEx, to);
                }
            }
        }
        return to;
    }

    private void dump(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        String flattened = ((Camera.Parameters) params.first).flatten();
        StringTokenizer tokenizer = new StringTokenizer(flattened, MovieFormatController.Settings.SEMI_COLON);
        Log.i(TAG, "---------------------------");
        Log.i(TAG, "Camera.Parameters.flattened");
        Log.i(TAG, "---------------------------");
        while (tokenizer.hasMoreElements()) {
            String kv = tokenizer.nextToken();
            int pos = kv.indexOf(61);
            if (pos == -1) {
                Log.i(TAG, kv);
            } else {
                String k = kv.substring(0, pos);
                String v = kv.substring(pos + 1);
                Log.i(TAG, k + "\t" + v);
            }
        }
        Log.i(TAG, "---------------------------");
    }

    public boolean isShutterSpeedBulb() {
        return isShutterSpeedBulb(((CameraEx.ParametersModifier) this.mCameraParams.second).getShutterSpeed());
    }

    public static boolean isShutterSpeedBulb(Pair<Integer, Integer> ss) {
        return SHUTTER_SPEED_BULB.equals(ss);
    }

    public Pair<Integer, Integer> getShutterSpeed() {
        return this.mShutterSpeed;
    }

    public void incrementShutterSpeed() {
        if (!isShutterSpeedBulb() || ExecutorCreator.getInstance().isBulbEnabled()) {
            Log.i(TAG, LOG_MSG_INCREMENTSHUTTERSPEED);
            this.mCameraEx.incrementShutterSpeed();
        }
    }

    public void decrementShutterSpeed() {
        if (!isShutterSpeedBulb() || ExecutorCreator.getInstance().isBulbEnabled()) {
            Log.i(TAG, LOG_MSG_DECREMENTSHUTTERSPEED);
            this.mCameraEx.decrementShutterSpeed();
        }
    }

    public void onShutterSpeedChange(CameraEx.ShutterSpeedInfo info, CameraEx camera) {
        int i = info.currentShutterSpeed_d;
        int i2 = info.currentShutterSpeed_n;
        this.mShutterSpeedInfo = info;
        this.mCameraParams = getCameraParameterPair();
        if (!isShutterSpeedBulb() || ExecutorCreator.getInstance().isBulbEnabled()) {
            this.mShutterSpeed = ((CameraEx.ParametersModifier) this.mCameraParams.second).getShutterSpeed();
            mNotify.requestNotify(CameraNotificationManager.SHUTTER_SPEED);
        } else {
            this.mMainLooperHandler.post(this.mIncrementShutterRunnable);
        }
    }

    /* loaded from: classes.dex */
    private class IncrementShutterRunnable implements Runnable {
        private IncrementShutterRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.i(CameraSetting.TAG, CameraSetting.LOG_MSG_INCREMENTSHUTTERSPEED_BY_BULB);
            CameraSetting.this.mCameraEx.incrementShutterSpeed();
        }
    }

    public CameraEx.ShutterSpeedInfo getShutterSpeedInfo() {
        return this.mShutterSpeedInfo;
    }

    public int getAperture() {
        int aperture = ((CameraEx.ParametersModifier) this.mCameraParams.second).getAperture();
        return aperture;
    }

    public void incrementAperture() {
        Log.i(TAG, LOG_MSG_INCREMENTAPERTURE);
        this.mCameraEx.incrementAperture();
    }

    public void decrementAperture() {
        Log.i(TAG, LOG_MSG_DECREMENTAPERTURE);
        this.mCameraEx.decrementAperture();
    }

    public void onApertureChange(CameraEx.ApertureInfo info, CameraEx camera) {
        int aperture = info.currentAperture;
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_ONAPERTURECHANGED).append(Integer.toString(aperture));
        Log.i(TAG, builder.toString());
        this.mApertureInfo = info;
        this.mCameraParams = getCameraParameterPair();
        mNotify.requestNotify("Aperture");
    }

    public CameraEx.ApertureInfo getApertureInfo() {
        return this.mApertureInfo;
    }

    public void saveCustomWhiteBalance(int customNo) {
        this.mCameraEx.saveCustomWhiteBalance(customNo);
    }

    public void saveCWBAndUpdateCameraParams(int customNo) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> oldParams = this.mCameraParams;
        this.mCameraEx.saveCustomWhiteBalance(customNo);
        this.mCameraParams = getCameraParameterPair();
        notifyParameterChanged(oldParams, this.mCameraParams);
    }

    public boolean getAERangeErr() {
        return this.mAeErr;
    }

    public boolean getAEShutErr() {
        return this.mAeShutErr;
    }

    public boolean getIrisErr() {
        return this.mIrisErr;
    }

    public float getMeteredManual() {
        float step = DEFAULT_EXPOSURE_COMPENSATION_STEP;
        if (4 != Environment.DEVICE_TYPE) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = getSupportedParameters();
            if (supported == null) {
                supported = this.mStillSupportedParams;
            }
            step = ((Camera.Parameters) supported.first).getExposureCompensationStep();
        }
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETEXPOSURECOMPENSATIONSTEP).append(step);
        Log.i(TAG, builder.toString());
        float mm = roundExposureCompensationValue(this.mErLevel * step);
        return mm;
    }

    public int getMeteredManualIndex() {
        return this.mErLevel;
    }

    public boolean getFlashEmit() {
        return this.mIsFlashEmit;
    }

    public int getFlashChargeState() {
        return mFlashChargeState;
    }

    public boolean getFlashInternalEnable() {
        return this.mFlashInternalEnable;
    }

    public boolean getFlashExternalEnable() {
        return this.mFlashExternalEnable;
    }

    public static float roundExposureCompensationValue(float value) {
        BigDecimal bi = new BigDecimal(String.valueOf(value));
        return bi.setScale(1, 4).floatValue();
    }

    public boolean isMeteringInRange() {
        return this.mIsMeteringInRange;
    }

    public int getISOSensitivityAuto() {
        return this.mISOSensitivityAuto;
    }

    public void onChanged(int value, CameraEx cameraEx) {
        this.mISOSensitivityAuto = value;
        this.mCameraParams = getCameraParameterPair();
        mNotify.requestNotify(CameraNotificationManager.ISO_SENSITIVITY_AUTO);
    }

    public void onAERange(boolean ae, boolean ae_shut, boolean iris, CameraEx cam) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_ONAERANGE).append(LOG_MSG_ONAERANGE_AE).append(Boolean.toString(ae)).append(", ").append(LOG_MSG_ONAERANGE_AESHUT).append(Boolean.toString(ae_shut)).append(", ").append(LOG_MSG_ONAERANGE_IRIS).append(Boolean.toString(iris));
        Log.i(TAG, builder.toString());
        this.mAeErr = ae;
        this.mCameraParams = getCameraParameterPair();
        if (iris != this.mIrisErr) {
            this.mIrisErr = iris;
            mNotify.requestNotify("Aperture");
        }
        if (ae_shut != this.mAeShutErr) {
            this.mAeShutErr = ae_shut;
            mNotify.requestNotify(CameraNotificationManager.SHUTTER_SPEED);
        }
        if (ae != this.mAeErr) {
            this.mAeErr = ae;
            mNotify.requestNotify(CameraNotificationManager.AE_RANGE_CHANGED);
        }
    }

    public void onEVRange(int er_level, CameraEx cam) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_ONEVRANGE_ER_LEVE).append(Integer.toString(er_level));
        Log.i(TAG, builder.toString());
        this.mErLevel = er_level;
        this.mCameraParams = getCameraParameterPair();
        mNotify.requestNotify(CameraNotificationManager.METERED_MANUAL);
    }

    public void onMeteringRange(boolean metering, CameraEx cam) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_ONMETERINGRANGE_METERING).append(Boolean.toString(metering));
        Log.i(TAG, builder.toString());
        this.mIsMeteringInRange = metering;
        this.mCameraParams = getCameraParameterPair();
        mNotify.requestNotify(CameraNotificationManager.METERING_RANGE);
    }

    public void setAppSpecificSettingChangedListener(SparseArray<String[]> appOwnTags) {
        Log.d(TAG, "setAppSpecificSettingChangedListener");
        synchronized (sLockForSettingChangedListener) {
            clearAppSpecificSettingChangedListener();
            sAppSpecificSettingChangeNotifyTags = appOwnTags;
            if (this.mIsListenerInitialized && sAppSpecificSettingChangeNotifyTags != null) {
                int[] types = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                try {
                    if (types.length > 0) {
                        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                        builder.replace(0, builder.length(), LOG_MSG_ENABLE_APP_SETTING).append(Arrays.toString(types));
                        Log.d(TAG, builder.toString());
                        StringBuilderThreadLocal.releaseScratchBuilder(builder);
                        this.mCameraEx.enableSettingChangedTypes(types);
                    }
                } catch (Exception e) {
                    Log.w(TAG, LOG_MSG_FAILED_TO_SET_SETTINGCHANGED_TYPES);
                    sAppSpecificSettingChangeNotifyTags.remove(19);
                    sAppSpecificSettingChangeNotifyTags.remove(22);
                    int[] types2 = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                    if (types2.length > 0) {
                        this.mCameraEx.enableSettingChangedTypes(types2);
                    }
                }
            }
        }
    }

    public void clearAppSpecificSettingChangedListener() {
        Log.d(TAG, "clearAppSpecificSettingChangedListener");
        synchronized (sLockForSettingChangedListener) {
            if (sAppSpecificSettingChangeNotifyTags != null) {
                if (this.mCameraEx != null && this.mIsListenerInitialized) {
                    int[] types = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                    try {
                        if (types.length > 0) {
                            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                            builder.replace(0, builder.length(), LOG_MSG_DISABLE_APP_SETTING).append(Arrays.toString(types));
                            Log.d(TAG, builder.toString());
                            StringBuilderThreadLocal.releaseScratchBuilder(builder);
                            this.mCameraEx.disableSettingChangedTypes(types);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, LOG_MSG_FAILED_TO_SET_SETTINGCHANGED_TYPES);
                        sAppSpecificSettingChangeNotifyTags.remove(19);
                        sAppSpecificSettingChangeNotifyTags.remove(22);
                        int[] types2 = getKeysOfSparseArray(sAppSpecificSettingChangeNotifyTags, SETTING_CHANGE_NOTIFY_TAGS);
                        if (types2.length > 0) {
                            this.mCameraEx.disableSettingChangedTypes(types2);
                        }
                    }
                }
                sAppSpecificSettingChangeNotifyTags = null;
            }
        }
    }

    public static void setAvoidGetParametersOnSettingChanged(boolean avoidIt) {
        sAvoidGetParametersOnSettingChanged = avoidIt;
    }

    public void onChanged(int[] types, Camera.Parameters param, CameraEx cam) {
        String[] tags;
        if (!sAvoidGetParametersOnSettingChanged) {
            this.mCameraParams = getCameraParameterPair();
        } else {
            ParametersTokenizer tokenizer = new ParametersTokenizer(param);
            synchronized (this) {
                Camera.Parameters cached = (Camera.Parameters) this.mCameraParams.first;
                while (tokenizer.hasMoreElements()) {
                    Pair<String, String> kv = tokenizer.nextToken();
                    cached.set((String) kv.first, (String) kv.second);
                }
            }
        }
        StringBuilder builder = sStringBuilder.get();
        for (int type : types) {
            builder.replace(0, builder.length(), LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED).append(", ").append("status = ").append(Integer.toString(type));
            Log.i(TAG, builder.toString());
            if (9 == type) {
                AbstractController.clearCache();
            }
            if (type == 4) {
                this.mMainLooperHandler.post(this.mAELUpdateRunnable);
                mNotify.requestNotify(CameraNotificationManager.AE_LOCK);
            }
            if (type == 30) {
                this.mMainLooperHandler.post(this.mUpdateMediaRemainingRunnable);
            }
            String[] tags2 = SETTING_CHANGE_NOTIFY_TAGS.get(type);
            if (tags2 != null) {
                for (String tag : tags2) {
                    builder.replace(0, builder.length(), LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED).append("type = ").append(type).append(", ").append("tag = ").append(tag);
                    Log.i(TAG, builder.toString());
                    if (type == 2) {
                        this.mMainLooperHandler.post(new RunnableOnSendNotifyToFocusMagnificaction(CameraNotificationManager.FOCUS_CHANGE));
                    }
                    mNotify.requestNotify(tag);
                }
            }
            synchronized (sLockForSettingChangedListener) {
                if (sAppSpecificSettingChangeNotifyTags != null && (tags = sAppSpecificSettingChangeNotifyTags.get(type)) != null) {
                    for (String tag2 : tags) {
                        builder.replace(0, builder.length(), LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED).append("type = ").append(type).append(", ").append("tag = ").append(tag2);
                        Log.i(TAG, builder.toString());
                        mNotify.requestNotify(tag2);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AELUpdateRunnable implements Runnable {
        private AELUpdateRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            String ael = ((CameraEx.ParametersModifier) CameraSetting.this.mCameraParams.second).getAutoExposureLock();
            if ("unlocked".equals(ael)) {
                CameraSetting.mAELButtonCtrlState = 1;
                if (!CameraSetting.mS1OnAELock) {
                    CameraSetting.mAELState = false;
                    return;
                }
                return;
            }
            if ("locked".equals(ael)) {
                CameraSetting.mAELState = true;
            } else {
                Log.d(CameraSetting.TAG, ael);
            }
        }
    }

    public void onEquipmentChange(int deviceType, int status, CameraEx camera) {
        Log.d(TAG, LOG_MSG_EQUIPMENTCALLBACK_ONEQUIPMENTCHANGE);
        this.mCameraParams = getCameraParameterPair();
        if (1 == deviceType) {
            invalidateLensInfoCache();
        }
        String tag = EQUIPMENT_CALLBACK_TAGS.get(deviceType);
        if (tag != null) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_EQUIPMENTCALLBACK_ONEQUIPMENTCHANGE).append(LOG_MSG_DEVICETYPE).append(Integer.toString(deviceType)).append(", ").append("status = ").append(Integer.toString(status));
            Log.i(TAG, builder.toString());
            if (deviceType == 3) {
                this.mFlashExternalEnable = status != 0;
            } else if (deviceType == 2) {
                this.mFlashInternalEnable = status != 0;
            }
            if (deviceType == 1) {
                this.mMainLooperHandler.post(new RunnableOnSendNotifyToFocusMagnificaction(CameraNotificationManager.DEVICE_LENS_CHANGED));
            }
            mNotify.requestNotify(tag);
        }
        String tag2 = EQUIPMENT_CALLBACK_FLASH_ONOFF_TAGS.get(deviceType);
        if (tag2 != null) {
            if (status == 0 || status == 1) {
                mNotify.requestNotify(tag2);
            }
        }
    }

    public CameraEx.ExternalFlashInfo getExternalFlashInfo() {
        CameraEx.ExternalFlashInfo info = this.mCameraEx.getExternalFlashInfo();
        return info;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FlashChargingListener implements CameraEx.FlashChargingStateListener {
        private FlashChargingListener() {
        }

        public void onChanged(int state, CameraEx cam) {
            int unused = CameraSetting.mFlashChargeState = state;
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_FLASH_CHARGING).append("status = ").append(Integer.toString(state));
            Log.i(CameraSetting.TAG, builder.toString());
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.FLASH_CHARGE_STATUS, Integer.valueOf(state));
        }
    }

    public int getPowerZoomStatus() {
        return this.mPowerZoomState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PowerZoomListener implements CameraEx.PowerZoomListener {
        private PowerZoomListener() {
        }

        public void onChanged(int status, CameraEx cameraEx) {
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_POWER_ZOOM).append("status = ").append(Integer.toString(status));
            Log.i(CameraSetting.TAG, builder.toString());
            CameraSetting.this.mPowerZoomState = status;
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.POWER_ZOOM_CHANGED, Integer.valueOf(status));
        }
    }

    public void onFlash(boolean emit, CameraEx cam) {
        this.mIsFlashEmit = emit;
        mNotify.requestNotify(CameraNotificationManager.FLASH_EMITTION);
    }

    public void onAnalizedData(CameraEx.AnalizedData data, CameraEx cam) {
        mNotify.requestNotify(CameraNotificationManager.HISTOGRAM_UPDATE, data);
    }

    public void onChanged(boolean ready, boolean on, CameraEx cam) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_FOCUSLIGHTSTATELISTENER_ONCHANGED).append(LOG_MSG_READY).append(Boolean.toString(ready)).append(", ").append(LOG_MSG_ON).append(Boolean.toString(on));
        Log.i(TAG, builder.toString());
        mNotify.requestNotify(CameraNotificationManager.FOCUS_LIGHT_STATE, new CameraNotificationManager.FocusLightStateInfo(ready, on));
    }

    public void onError(int error, CameraEx cameraex) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_ERRORCALLBACK_ONERROR).append(LOG_MSG_ERROR).append(Integer.toString(error));
        Log.i(TAG, builder.toString());
        this.mCameraError = error;
        mNotify.requestNotify(CameraNotificationManager.CAMERA_ERROR, Integer.valueOf(error));
    }

    public int getCameraError() {
        return this.mCameraError;
    }

    public void onChanged(String mode, CameraEx cameraex) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_AUTOSCENEMODELISTENER_ONCHANGED).append("mode = ").append(mode);
        Log.i(TAG, builder.toString());
        this.mAutoSceneMode = mode;
        mNotify.requestNotify(CameraNotificationManager.AUTOSCENEMODE_CHANGED, mode);
    }

    public String getAutoSceneMode() {
        return this.mAutoSceneMode;
    }

    public void onFaceDetected(FaceInfo[] faceinfos, CameraEx cameraex) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_FACEDETECTIONLISTENER_ONFACEDETECTED).append(LOG_MSG_FACEINFO);
        for (FaceInfo info : faceinfos) {
            builder.append(" ( ").append("id = ").append(Integer.toString(info.id)).append(", ").append(LOG_MSG_SCORE).append(Integer.toString(info.score)).append(" ) ");
        }
        Log.i(TAG, builder.toString());
        this.mfaceInfos = faceinfos;
        mNotify.requestNotify(CameraNotificationManager.FACE_DETECTED, faceinfos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NDFilterListener implements CameraEx.NDFilterStatusListener {
        private NDFilterListener() {
        }

        public void onChanged(boolean onoff, CameraEx cam) {
            CameraSetting.mNDFilterStatus = onoff;
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.NDFILTER_STATUS_CHANGED);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TrackingFocusListener implements CameraEx.TrackingFocusListener {
        private TrackingFocusListener() {
        }

        public void onChanged(CameraEx.TrackingFocusInfo Info, CameraEx cam) {
            CameraSetting.mTrackingFocusInfo = Info;
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.TRACKING_FOCUS_INFO_CHANGED);
        }
    }

    public FaceInfo[] getFaceInfos() {
        return this.mfaceInfos;
    }

    public void onFocalLengthChanged(int length, CameraEx cameraex) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_FOCALLENGTHCHANGELISTENER_ONFOCALLENGTHCHANGED).append("length = ").append(length);
        Log.i(TAG, builder.toString());
        this.mFocalLength = length;
        mNotify.requestNotify(CameraNotificationManager.FOCAL_LENGTH_CHANGED);
    }

    public int getFocalLength() {
        if (this.mFocalLength != -100) {
            return this.mFocalLength;
        }
        if (this.mFocalLengthByZoomInfo != -100) {
            return this.mFocalLengthByZoomInfo;
        }
        CameraEx.LensInfo lensInfo = getLensInfo();
        if (lensInfo != null) {
            return lensInfo.FocalLength.wide;
        }
        return -100;
    }

    public void enableLensInfoCache(boolean enableIt) {
        this.mIsLensInfoCacheEnabledByUser = enableIt;
        if (!enableIt) {
            invalidateLensInfoCache();
        }
    }

    public void invalidateLensInfoCache() {
        Log.i(TAG, LOG_MSG_INVALIDATE_LENSINFO);
        this.mLensInfo = null;
    }

    public CameraEx.LensInfo getLensInfo() {
        CameraEx.LensInfo info = this.mLensInfo;
        if (info == null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = getSupportedParameters();
            if (supported == null) {
                supported = this.mStillSupportedParams;
            }
            boolean isSupported = ((CameraEx.ParametersModifier) supported.second).isSupportedLensInfo();
            if (isSupported && this.mCameraEx != null) {
                info = this.mCameraEx.getLensInfo();
                if (this.mIsLensInfoCacheEnabledByUser && this.mIsLensInfoCacheEnabled) {
                    this.mLensInfo = info;
                    Log.i(TAG, LOG_MSG_STORE_LENSINFO);
                }
            }
        } else {
            Log.i(TAG, LOG_MSG_STORED_LENSINFO);
        }
        return info;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calcFocalLengthByZoomInfo() {
        CameraEx.LensInfo lensInfo = getLensInfo();
        if (lensInfo != null) {
            CameraEx.WideTele wideTele = lensInfo.FocalLength;
            int minFocalLength = wideTele.wide;
            int maxFocalLength = wideTele.tele;
            int focalLength = (((maxFocalLength - minFocalLength) * getOpticalPosition()) / 100) + minFocalLength;
            if (minFocalLength <= focalLength || focalLength <= maxFocalLength) {
                this.mFocalLengthByZoomInfo = focalLength;
            }
        }
    }

    private void resetFocalLength() {
        this.mFocalLength = -100;
        this.mFocalLengthByZoomInfo = -100;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized Pair<Camera.Parameters, CameraEx.ParametersModifier> getCameraParameterPair() {
        Camera.Parameters p;
        CameraEx.ParametersModifier m;
        p = this.mCamera.getParameters();
        m = this.mCameraEx.createParametersModifier(p);
        AvailableInfo.update();
        return new Pair<>(p, m);
    }

    private Pair<Camera.Parameters, CameraEx.ParametersModifier> getCameraInitialParameterPair() {
        if (1 > getPfApiVersion()) {
            return null;
        }
        Camera.Parameters p = this.mCameraEx.getInitialParameters();
        CameraEx.ParametersModifier m = this.mCameraEx.createParametersModifier(p);
        AvailableInfo.update();
        return new Pair<>(p, m);
    }

    private Pair<Camera.Parameters, CameraEx.ParametersModifier> getEmptyCameraParameterPair() {
        Camera.Parameters p = this.mCameraEx.createEmptyParameters();
        CameraEx.ParametersModifier m = this.mCameraEx.createParametersModifier(p);
        p.set(IS_EMPTY_PARAMETERS, 1);
        return new Pair<>(p, m);
    }

    private Camera.Parameters getBackupParameter() {
        if (this.mBackupParameters == null) {
            this.mBackupParameters = this.mCameraEx.createEmptyParameters();
            this.mBackupParameters.set(IS_EMPTY_PARAMETERS, 1);
        }
        return this.mBackupParameters;
    }

    public boolean isDigitalZoomModeSet() {
        if (this.mBackupParameters != null) {
            return (this.mBackupParameters.get(Keys.KEY_SMART_ZOOM) == null && this.mBackupParameters.get(Keys.KEY_SUPER_RESOLUTION_ZOOM) == null && this.mBackupParameters.get(Keys.KEY_PRECISION_ZOOM) == null) ? false : true;
        }
        return false;
    }

    private MediaRecorder.Parameters getBackupRecorderParameter() {
        if (this.mRecorderBackupParameters == null) {
            this.mRecorderBackupParameters = new MediaRecorder.Parameters();
        }
        return this.mRecorderBackupParameters;
    }

    private void notifyParameterChanged(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj1, Pair<Camera.Parameters, CameraEx.ParametersModifier> obj2) {
        for (ParameterComparator<Pair<Camera.Parameters, CameraEx.ParametersModifier>> comparator : this.CAMERA_PARAMETER_COMPARATORS) {
            comparator.compare(obj1, obj2);
        }
    }

    private void notifyRecorderParameterChanged(MediaRecorder.Parameters obj1, MediaRecorder.Parameters obj2) {
        for (ParameterComparator<MediaRecorder.Parameters> comparator : this.RECORDER_PARAMETER_COMPARATORS) {
            comparator.compare(obj1, obj2);
        }
    }

    private void createParameterComparatorList() {
        if (this.CAMERA_PARAMETER_COMPARATORS == null) {
            this.CAMERA_PARAMETER_COMPARATORS = new ArrayList();
        } else {
            this.CAMERA_PARAMETER_COMPARATORS.clear();
        }
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.DRIVE_MODE) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.12
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                String v = ((CameraEx.ParametersModifier) obj.second).getBracketMode();
                return v;
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<Integer>("ExposureCompensation") { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.13
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public Integer getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                int v = ((Camera.Parameters) obj.first).getExposureCompensation();
                return Integer.valueOf(v);
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Object>(CameraNotificationManager.DRO_AUTO_HDR, 4) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.14
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Object>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Object> list) {
                list.add(((CameraEx.ParametersModifier) obj.second).getDROMode());
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getDROLevel()));
                list.add(((CameraEx.ParametersModifier) obj.second).getHDRMode());
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getHDRExposureDifferenceLevel()));
            }
        });
        ComparatorCommand remainingUpdateCommand = new ComparatorCommand() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.15
            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.ComparatorCommand
            public void execute() {
                CameraSetting.this.mMainLooperHandler.post(CameraSetting.this.mUpdateMediaRemainingRunnable);
            }
        };
        ComparatorCommand resetDigitalZoomOnStillChangedCommand = new ComparatorCommand() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.16
            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.ComparatorCommand
            public void execute() {
                if (CameraSetting.this.mIsParameterInitialized) {
                    CameraSetting.this.resetDigitalZoom();
                }
            }
        };
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<Camera.Size>(CameraNotificationManager.PICTURE_SIZE, remainingUpdateCommand) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.17
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public Camera.Size getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                Camera.Size v = ((Camera.Parameters) obj.first).getPictureSize();
                return v;
            }
        });
        if (1 <= getPfApiVersion()) {
            List<ComparatorCommand> imgSizeChangeCommands = new ArrayList<>();
            imgSizeChangeCommands.add(remainingUpdateCommand);
            imgSizeChangeCommands.add(resetDigitalZoomOnStillChangedCommand);
            this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.PICTURE_SIZE, imgSizeChangeCommands) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.18
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                    String v = ((CameraEx.ParametersModifier) obj.second).getImageSize();
                    return v;
                }
            });
            this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.PICTURE_SIZE, remainingUpdateCommand) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.19
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                    String v = ((CameraEx.ParametersModifier) obj.second).getImageAspectRatio();
                    return v;
                }
            });
        }
        List<ComparatorCommand> imgQualityChangeCommands = new ArrayList<>();
        imgQualityChangeCommands.add(remainingUpdateCommand);
        imgQualityChangeCommands.add(resetDigitalZoomOnStillChangedCommand);
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Object>(CameraNotificationManager.PICTURE_QUALITY, imgQualityChangeCommands, 2) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.20
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Object>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Object> list) {
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureStorageFormat());
                list.add(Integer.valueOf(((Camera.Parameters) obj.first).getJpegQuality()));
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Object>(CameraNotificationManager.WB_MODE_CHANGE) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.21
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Object>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Object> list) {
                list.add(((Camera.Parameters) obj.first).getWhiteBalance());
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getCustomWhiteBalance()));
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Integer>(CameraNotificationManager.WB_DETAIL_CHANGE, 3) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.22
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Integer>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Integer> list) {
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getLightBalanceForWhiteBalance()));
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getColorCompensationForWhiteBalance()));
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getColorTemperatureForWhiteBalance()));
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.CREATIVE_STYLE_CHANGE) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.23
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                return ((CameraEx.ParametersModifier) obj.second).getColorMode();
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Object>(CameraNotificationManager.PICTURE_EFFECT_CHANGE, 7) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.24
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Object>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Object> list) {
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureEffect());
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureEffectToyCameraEffect());
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureEffectPosterizationEffect());
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureEffectPartColorEffect());
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getPictureEffectSoftFocusEffectLevel()));
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getPictureEffectHDRArtEffectLevel()));
                list.add(((CameraEx.ParametersModifier) obj.second).getPictureEffectMiniatureFocusArea());
                list.add(Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getPictureEffectIllustEffectLevel()));
            }
        });
        if (1 <= getPfApiVersion()) {
            this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.APSC_MODE_CHANGED, remainingUpdateCommand) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.25
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                    return ((CameraEx.ParametersModifier) obj.second).getApscMode();
                }
            });
        }
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<Pair<Integer, Integer>>(CameraNotificationManager.FOCUS_POINT) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.26
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public Pair<Integer, Integer> getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                return ((CameraEx.ParametersModifier) obj.second).getFocusPoint();
            }
        });
        ComparatorCommand sceneUpdateCommand = new ComparatorCommand() { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.27
            @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting.ComparatorCommand
            public void execute() {
                AbstractController.clearCache();
            }
        };
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<String>(CameraNotificationManager.SCENE_MODE, sceneUpdateCommand) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.28
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                String scene = ((Camera.Parameters) obj.first).getSceneMode();
                Log.e("ParameterComparator", "scene: " + scene);
                return scene;
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new ListComparator<Boolean>(CameraNotificationManager.DIGITAL_ZOOM_MODE_CHANGED) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.29
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> pair, List x1) {
                getValue2(pair, (List<Boolean>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj, List<Boolean> out) {
                out.add(Boolean.valueOf(((CameraEx.ParametersModifier) obj.second).getDigitalZoomMode(DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART)));
                out.add(Boolean.valueOf(((CameraEx.ParametersModifier) obj.second).getDigitalZoomMode(DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION)));
                out.add(Boolean.valueOf(((CameraEx.ParametersModifier) obj.second).getDigitalZoomMode(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION)));
            }
        });
        this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<Boolean>(CameraNotificationManager.IR_CONTROL_MODE_CHANGED) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.30
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public Boolean getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                return Boolean.valueOf(((CameraEx.ParametersModifier) obj.second).getRemoteControlMode());
            }
        });
        if (Environment.isIntervalRecAPISupported()) {
            this.CAMERA_PARAMETER_COMPARATORS.add(new SimpleComparator<Integer>(CameraNotificationManager.TAG_INTERVAL_SETTING_CHANGED) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.31
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public Integer getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
                    if (2 == CameraSetting.this.getCurrentMode()) {
                        return null;
                    }
                    List<Integer> values = ((CameraEx.ParametersModifier) CameraSetting.this.getSupportedParameters().second).getSupportedIntervalRecTime();
                    if (values == null) {
                        return null;
                    }
                    try {
                        return Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getIntervalRecTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
        }
        if (this.RECORDER_PARAMETER_COMPARATORS == null) {
            this.RECORDER_PARAMETER_COMPARATORS = new ArrayList();
        } else {
            this.RECORDER_PARAMETER_COMPARATORS.clear();
        }
        this.RECORDER_PARAMETER_COMPARATORS.add(new RecorderSimpleComparator<String>(CameraNotificationManager.MOVIE_FORMAT) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.32
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
            public String getValue(MediaRecorder.Parameters obj) {
                return obj.getOutputFormat();
            }
        });
        this.RECORDER_PARAMETER_COMPARATORS.add(new RecorderListComparator<String>(CameraNotificationManager.MOVIE_FORMAT) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.33
            @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._ListComparator
            protected /* bridge */ /* synthetic */ void getValue(MediaRecorder.Parameters parameters, List x1) {
                getValue2(parameters, (List<String>) x1);
            }

            /* renamed from: getValue, reason: avoid collision after fix types in other method */
            protected void getValue2(MediaRecorder.Parameters obj, List<String> list) {
                list.add(obj.getVideoEncodingBitRate());
                list.add(obj.getVideoAspectRatio());
                list.add(obj.getVideoFrameRate());
                list.add(obj.getVideoSize());
            }
        });
        if (Environment.isLoopRecAPISupported()) {
            this.RECORDER_PARAMETER_COMPARATORS.add(new RecorderSimpleComparator<Integer>(CameraNotificationManager.TAG_LOOPREC_MAX_DURATION_CHANGED) { // from class: com.sony.imaging.app.base.shooting.camera.CameraSetting.34
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public Integer getValue(MediaRecorder.Parameters obj) {
                    if (1 == CameraSetting.this.getCurrentMode()) {
                        return null;
                    }
                    List<Integer> values = obj.getSupportedLoopRecTimes();
                    if (values != null) {
                        return Integer.valueOf(obj.getLoopRecTime());
                    }
                    return null;
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public static class UpdateMediaRemainingRunnable implements Runnable {
        @Override // java.lang.Runnable
        public void run() {
            MediaNotificationManager.getInstance().updateRemainingAmount();
            MediaNotificationManager.getInstance().notifyRemainingAmountChange();
        }
    }

    public void addParameterComparator(ParameterComparator<Pair<Camera.Parameters, CameraEx.ParametersModifier>> c) throws IllegalStateException {
        if (this.CAMERA_PARAMETER_COMPARATORS == null || this.CAMERA_PARAMETER_COMPARATORS.size() == 0) {
            IllegalStateException e = new IllegalStateException(LOG_MSG_COMPARATOR_NOT_INITIALIZED);
            throw e;
        }
        this.CAMERA_PARAMETER_COMPARATORS.add(c);
    }

    /* loaded from: classes.dex */
    public abstract class SimpleComparator<T> extends ParameterComparator._SimpleComparator<T, Pair<Camera.Parameters, CameraEx.ParametersModifier>> {
        public SimpleComparator(String tag) {
            super(tag);
        }

        public SimpleComparator(String tag, ComparatorCommand command) {
            super(tag, command);
        }

        public SimpleComparator(String tag, List<ComparatorCommand> commands) {
            super(tag, commands);
        }
    }

    /* loaded from: classes.dex */
    public abstract class RecorderSimpleComparator<T> extends ParameterComparator._SimpleComparator<T, MediaRecorder.Parameters> {
        public RecorderSimpleComparator(String tag) {
            super(tag);
        }

        public RecorderSimpleComparator(String tag, ComparatorCommand command) {
            super(tag, command);
        }

        public RecorderSimpleComparator(String tag, List<ComparatorCommand> commands) {
            super(tag, commands);
        }
    }

    /* loaded from: classes.dex */
    public abstract class ListComparator<T> extends ParameterComparator._ListComparator<T, Pair<Camera.Parameters, CameraEx.ParametersModifier>> {
        public ListComparator(String tag) {
            super(tag);
        }

        public ListComparator(String tag, ComparatorCommand command) {
            super(tag, command);
        }

        public ListComparator(String tag, List<ComparatorCommand> commands) {
            super(tag, commands);
        }

        public ListComparator(String tag, int capacity) {
            super(tag, capacity);
        }

        public ListComparator(String tag, List<ComparatorCommand> commands, int capacity) {
            super(tag, commands, capacity);
        }
    }

    /* loaded from: classes.dex */
    public abstract class RecorderListComparator<T> extends ParameterComparator._ListComparator<T, MediaRecorder.Parameters> {
        public RecorderListComparator(String tag) {
            super(tag);
        }

        public RecorderListComparator(String tag, ComparatorCommand command) {
            super(tag, command);
        }

        public RecorderListComparator(String tag, List<ComparatorCommand> commands) {
            super(tag, commands);
        }

        public RecorderListComparator(String tag, int capacity) {
            super(tag, capacity);
        }

        public RecorderListComparator(String tag, List<ComparatorCommand> commands, int capacity) {
            super(tag, commands, capacity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FocusAreaChangedListener implements CameraEx.FocusAreaListener {
        private FocusAreaChangedListener() {
        }

        public void onChanged(CameraEx.FocusAreaInfos infos, CameraEx cam) {
            Log.i(CameraSetting.TAG, CameraSetting.LOG_MSG_FOCUS_AREA_CHANGED);
            CameraSetting.this.mIsFocusAreaInfosForViewPatternDirty = true;
            if (CameraSetting.PF_VER_FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43 >= CameraSetting.getPfApiVersion() && 43 == CameraSetting.getImagerAspect()) {
                CameraSetting.convertFocusAreaRectIndexForMulti(infos);
            }
            CameraSetting.this.mFocusAreaInfosForAPIVer1.put(infos.focusAreaMode, infos);
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.FOCUS_AREA_INFO);
        }
    }

    private static void dumpAreaInfos(CameraEx.FocusAreaInfos infos) {
        Log.d(TAG, MSG_DUMP_AREA_INFOS);
        Log.d(TAG, infos.focusAreaMode);
        int c = infos.rectInfos.length;
        for (int i = 0; i < c; i++) {
            Log.d(TAG, infos.rectInfos[i].index + ", " + infos.rectInfos[i].rect.flattenToString());
        }
        Log.d(TAG, MSG_DUMP_AREA_INFOS_END);
    }

    private void registerFocusAreaChangedListener() {
        if (1 <= getPfApiVersion()) {
            Log.d(TAG, "setFocusAreaListener");
            this.mCameraEx.setFocusAreaListener(new FocusAreaChangedListener());
        }
    }

    private void unregisterFocusAreaChangedListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setFocusAreaListener((CameraEx.FocusAreaListener) null);
        }
    }

    public void clearFocusAreaInfos() {
        this.mIsFocusAreaInfosForViewPatternDirty = false;
        this.mFocusAreaInfosForViewPattern.clear();
        this.mFocusAreaInfosForAPIVer1.clear();
    }

    public CameraEx.FocusAreaInfos getFocusAreaRectInfos(String focusAreaMode, int asp, int viewPattern) {
        CameraEx.FocusAreaInfos infos = this.mFocusAreaInfosForAPIVer1.get(focusAreaMode);
        if (infos != null) {
            if (1 == viewPattern) {
                infos = createReverseFocusAreaInfos(infos);
            }
        } else {
            Log.w(TAG, LOG_MSG_FOCUS_AREA_CHANGED_NOT_COME);
            CameraEx.FocusAreaInfos[] infosArray = getFocusAreaRectInfos(asp, viewPattern);
            for (CameraEx.FocusAreaInfos fais : infosArray) {
                if (fais.focusAreaMode.equals(focusAreaMode)) {
                    return fais;
                }
            }
        }
        return infos;
    }

    public CameraEx.FocusAreaInfos[] getFocusAreaRectInfos(int asp, int viewPattern) {
        if (this.mIsFocusAreaInfosForViewPatternDirty) {
            this.mIsFocusAreaInfosForViewPatternDirty = false;
            this.mFocusAreaInfosForViewPattern.clear();
        }
        SparseArray<CameraEx.FocusAreaInfos[]> areaInfos = this.mFocusAreaInfosForViewPattern.get(viewPattern);
        if (areaInfos == null) {
            areaInfos = new SparseArray<>();
            this.mFocusAreaInfosForViewPattern.append(viewPattern, areaInfos);
        }
        CameraEx.FocusAreaInfos[] infosArray = areaInfos.get(asp);
        if (infosArray == null) {
            if (this.mCameraEx != null) {
                infosArray = this.mCameraEx.getFocusAreaInfos(asp);
                if (infosArray != null) {
                    if (PF_VER_FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43 >= getPfApiVersion() && 43 == getImagerAspect()) {
                        convertFocusAreaRectIndex(infosArray);
                    }
                    if (viewPattern == 1) {
                        Log.d(TAG, "VIEW_PATTERN_REVERSE_OSD180");
                        infosArray = createReverseFocusAreaInfosArray(infosArray);
                    }
                    areaInfos.append(asp, infosArray);
                } else {
                    Log.w(TAG, LOG_MSG_GETFOCUSAREAINFOS_IS_NULL);
                }
            } else {
                Log.w(TAG, LOG_MSG_CAMERAEX_IS_NULL);
                return null;
            }
        }
        return infosArray;
    }

    private CameraEx.FocusAreaInfos[] createReverseFocusAreaInfosArray(CameraEx.FocusAreaInfos[] original) {
        CameraEx.FocusAreaInfos[] newAreaInfosArray = createFocusAreaInfosArray(original);
        for (CameraEx.FocusAreaInfos fais : newAreaInfosArray) {
            CameraEx.FocusAreaRectInfo[] arr$ = fais.rectInfos;
            for (CameraEx.FocusAreaRectInfo rInfo : arr$) {
                int tmp = rInfo.rect.left;
                rInfo.rect.left = rInfo.rect.right;
                rInfo.rect.right = tmp;
                rInfo.rect.left = -rInfo.rect.left;
                rInfo.rect.right = -rInfo.rect.right;
            }
        }
        return newAreaInfosArray;
    }

    private CameraEx.FocusAreaInfos createReverseFocusAreaInfos(CameraEx.FocusAreaInfos original) {
        CameraEx.FocusAreaInfos newAreaInfos = new CameraEx.FocusAreaInfos();
        createFocusAreaInfos(original, newAreaInfos);
        CameraEx.FocusAreaRectInfo[] arr$ = newAreaInfos.rectInfos;
        for (CameraEx.FocusAreaRectInfo rInfo : arr$) {
            int tmp = rInfo.rect.left;
            rInfo.rect.left = rInfo.rect.right;
            rInfo.rect.right = tmp;
            rInfo.rect.left = -rInfo.rect.left;
            rInfo.rect.right = -rInfo.rect.right;
        }
        return newAreaInfos;
    }

    private CameraEx.FocusAreaInfos[] createFocusAreaInfosArray(CameraEx.FocusAreaInfos[] original) {
        CameraEx.FocusAreaInfos[] copy = new CameraEx.FocusAreaInfos[original.length];
        for (int i = 0; i < original.length; i++) {
            CameraEx.FocusAreaInfos newAreaInfos = new CameraEx.FocusAreaInfos();
            createFocusAreaInfos(original[i], newAreaInfos);
            copy[i] = newAreaInfos;
        }
        return copy;
    }

    private void createFocusAreaInfos(CameraEx.FocusAreaInfos from, CameraEx.FocusAreaInfos to) {
        to.focusAreaMode = from.focusAreaMode.toString();
        to.rectInfos = new CameraEx.FocusAreaRectInfo[from.rectInfos.length];
        for (int i = 0; i < from.rectInfos.length; i++) {
            to.rectInfos[i] = new CameraEx.FocusAreaRectInfo();
            to.rectInfos[i].index = from.rectInfos[i].index;
            to.rectInfos[i].rect = new Rect(from.rectInfos[i].rect);
            if (1 <= getPfApiVersion()) {
                to.rectInfos[i].enable = from.rectInfos[i].enable;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DispModeNotificationListener implements NotificationListener {
        String[] tags;

        private DispModeNotificationListener() {
            this.tags = new String[]{DisplayModeObserver.TAG_DISPMODE_CHANGE, DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_YUVLAYOUT_CHANGE.equals(tag) || DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE.equals(tag) || DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag)) {
                CameraSetting.this.mCameraParams = CameraSetting.this.getCameraParameterPair();
            }
            if (DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE.equals(tag)) {
                CameraSetting.this.updateFocusMagnificationCoordinate();
            }
            if (DisplayModeObserver.TAG_DISPMODE_CHANGE.equals(tag) || DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag)) {
                int dispMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
                if (CameraSetting.this.mCameraEx != null) {
                    if (dispMode != 5) {
                        CameraSetting.this.mCameraEx.setPreviewAnalizeListener((CameraEx.PreviewAnalizeListener) null);
                        return;
                    }
                    boolean isSupported = false;
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = CameraSetting.this.getSupportedParameters();
                    if (supported != null) {
                        isSupported = ((CameraEx.ParametersModifier) supported.second).isSupportedPreviewAnalize();
                    }
                    if (isSupported) {
                        CameraSetting.this.mCameraEx.setPreviewAnalizeListener(CameraSetting.mSetting);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AFMFSWNotificationListener implements NotificationListener {
        private String INH_AFMF_SW_MF;
        private final String[] tags = {CameraNotificationManager.FOCUS_CHANGE};
        private boolean INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P = false;

        AFMFSWNotificationListener() {
            this.INH_AFMF_SW_MF = null;
            if (1 == Environment.getVersionOfHW()) {
                this.INH_AFMF_SW_MF = "INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_C";
            } else {
                this.INH_AFMF_SW_MF = "INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P";
            }
        }

        public void update() {
            AvailableInfo.update();
            this.INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P = AvailableInfo.isFactor(this.INH_AFMF_SW_MF);
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AvailableInfo.update();
            boolean SW_MF = AvailableInfo.isFactor(this.INH_AFMF_SW_MF);
            Log.i("TAMA", "" + this.INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P + ExposureModeController.SOFT_SNAP + SW_MF);
            if (this.INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P != SW_MF) {
                CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED);
            }
            this.INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P = SW_MF;
        }
    }

    public boolean isProgramLineAdjusted() {
        return this.mIsProgramLineAdjusted;
    }

    private void setupProgramLine(String mode) {
        boolean needReset = this.mNeedToResetProgramLine;
        this.mNeedToResetProgramLine = false;
        if (mode != null && !mode.equals("program-auto")) {
            needReset = true;
        }
        if (needReset) {
            resetProgramLine();
        }
    }

    public void onChanged(boolean adjustedProgramLine, CameraEx camera) {
        this.mIsProgramLineAdjusted = adjustedProgramLine;
        mNotify.requestNotify(CameraNotificationManager.PROGRAM_LINE_CHANGE);
    }

    public void incrementProgramLine() {
        Log.i(TAG, LOG_MSG_INCREMENTPROGRAMLINE);
        this.mCameraEx.adjustProgramLine(1);
    }

    public void decrementProgramLine() {
        Log.i(TAG, LOG_MSG_DECREMENTPROGRAMLINE);
        this.mCameraEx.adjustProgramLine(-1);
    }

    public void resetProgramLine() {
        Log.i(TAG, LOG_MSG_RESETPROGRAMLINE);
        if (this.mCamera == null || ExecutorCreator.getInstance().isRecordingModeChanging()) {
            this.mNeedToResetProgramLine = true;
        } else {
            this.mCameraEx.adjustProgramLine(0);
        }
    }

    public static int getPfApiVersion() {
        return Environment.getVersionPfAPI();
    }

    public void resetMfAssistInfo() {
        this.mIsFocusMagnificationStarting = false;
        this.mFocusMagnificationRatio = 0;
        this.mFocusMagnificationActualRatio = 0;
        this.mMfLastMagnifyingPosition = null;
        this.mMagnifyingPosition = null;
        this.mIsFocusRingRotating = false;
        this.mFocusMagnificationNextRatio = 0;
        this.mIsDigitalZoomAtMagnifyStarting = false;
        this.mBlockMagnifyingStopbyDigitalZoom = false;
        this.mUseMagnifyRatio1_0 = false;
        this.mChangeZoomRatio = false;
        this.mDigitalZoomRatio = 0;
        this.mIsAvailableMagnificationPointMove = true;
        if (Environment.isAvailablePreviewMagnificationPointMovable() && 1 == ScalarProperties.getInt("ui.preview.magnification")) {
            this.mIsAvailableMagnificationPointMove = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusMagnificationCoordinate() {
        if (1 <= getPfApiVersion() && this.mCameraEx != null && this.mFocusMagnificationRatio != 0) {
            boolean isReverse = DisplayModeObserver.getInstance().isPanelReverse();
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_UPDATE_FOCUS_MAGNIGICATION_POSITION).append(isReverse);
            Log.i(TAG, builder.toString());
            Pair<Integer, Integer> position = this.mMagnifyingPosition;
            if (isReverse) {
                position = Pair.create(Integer.valueOf(-((Integer) position.first).intValue()), position.second);
            }
            this.mCameraEx.setPreviewMagnification(this.mFocusMagnificationRatio, position);
        }
    }

    public void startFocusMagnification(int ratio, Pair<Integer, Integer> position) {
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_START_FOCUS_MAGNIGICATION).append(ratio).append(", ").append(" ( ").append(position.first).append(", ").append(position.second).append(" ) ");
        Log.d(TAG, builder.toString());
        if (1 <= getPfApiVersion() && this.mCameraEx != null) {
            this.mIsFocusMagnificationStarting = true;
            if (this.mFocusMagnificationRatio == 0 && this.mIsDigitalZoomAtMagnifyStarting) {
                this.mDigitalZoomRatio = getDigitalZoomMagnification();
            }
            if (!this.mIsAvailableMagnificationPointMove) {
                position = new Pair<>(0, 0);
            }
            Pair<Integer, Integer> pfPosition = position;
            if (DisplayModeObserver.getInstance().isPanelReverse()) {
                pfPosition = Pair.create(Integer.valueOf(-((Integer) position.first).intValue()), position.second);
            }
            if (ratio != 1) {
                this.mCameraEx.setPreviewMagnification(ratio, pfPosition);
            } else if (this.mFocusMagnificationRatio != 0 && this.mFocusMagnificationRatio != 1) {
                this.mCameraEx.stopPreviewMagnification();
            }
            this.mMagnifyingPosition = position;
            int sensorType = FocusAreaController.getInstance().getSensorType();
            if (sensorType == 2 && FocusModeController.MANUAL.equals(((Camera.Parameters) this.mCameraParams.first).getFocusMode())) {
                this.mMfLastMagnifyingPosition = position;
            }
            if (sensorType == 1) {
                this.mMfLastMagnifyingPosition = position;
            }
            if (ratio == 1 && (this.mFocusMagnificationRatio == 0 || this.mFocusMagnificationRatio == 1)) {
                this.mFocusMagnificationRatio = 1;
                this.mFocusMagnificationActualRatio = 100;
                CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED);
                return;
            }
            this.mFocusMagnificationNextRatio = ratio;
        }
    }

    public void stopFocusMagnification() {
        Log.d(TAG, LOG_MSG_STOP_FOCUS_MAGNIGICATION);
        cancelMfAssistTimeout();
        boolean ratio_1_0 = false;
        if (1 <= getPfApiVersion() && this.mCameraEx != null && (this.mFocusMagnificationRatio != 1 || this.mFocusMagnificationNextRatio != 1)) {
            this.mCameraEx.stopPreviewMagnification();
        }
        if (this.mFocusMagnificationRatio == 1) {
            ratio_1_0 = true;
        }
        this.mFocusMagnificationActualRatio = 0;
        this.mFocusMagnificationRatio = 0;
        this.mIsFocusMagnificationStarting = false;
        this.mFocusMagnificationNextRatio = 0;
        this.mIsDigitalZoomAtMagnifyStarting = false;
        this.mBlockMagnifyingStopbyDigitalZoom = false;
        this.mUseMagnifyRatio1_0 = false;
        if (ratio_1_0) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED);
        }
    }

    public int getFocusMagnificationRatio(int level) {
        if (1 > getPfApiVersion() || this.mCameraEx == null) {
            return 0;
        }
        int result = this.mCameraEx.getPreviewMagnificationRatio(level);
        Log.d(TAG, "getPreviewMagnificationRatio : " + result);
        return result;
    }

    protected void checkFocusMagnificactionStop(String notify) {
        boolean isStopMagnifier = false;
        if (this.mFocusMagnificationRatio == 1 || this.mFocusMagnificationNextRatio == 1) {
            if (notify.equals(CameraNotificationManager.ZOOM_INFO_CHANGED)) {
                if (this.mBlockMagnifyingStopbyDigitalZoom) {
                    this.mBlockMagnifyingStopbyDigitalZoom = false;
                } else {
                    isStopMagnifier = true;
                }
            }
            if (notify.equals(CameraNotificationManager.FOCUS_CHANGE)) {
                AvailableInfo.update();
                if (AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE")) {
                    isStopMagnifier = true;
                }
            }
            if (notify.equals(CameraNotificationManager.DEVICE_LENS_CHANGED)) {
                isStopMagnifier = true;
            }
        }
        if (isStopMagnifier) {
            stopFocusMagnification();
        }
    }

    /* loaded from: classes.dex */
    private class RunnableOnFocusMagnificactionChanged implements Runnable {
        private Pair<Integer, Integer> mCoordinate;
        private int mMag;
        private int mMagLevel;

        RunnableOnFocusMagnificactionChanged(int mag, int magLevel, Pair<Integer, Integer> coordinates) {
            this.mMag = mag;
            this.mMagLevel = magLevel;
            this.mCoordinate = coordinates;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d(CameraSetting.TAG, CameraSetting.LOG_MSG_RUNNABLE_OF_FOCUSMAGNIFICATION_CHANGED);
            boolean isStopMagnifier = false;
            if (this.mMagLevel == 0) {
                if (CameraSetting.this.mFocusMagnificationNextRatio != 1) {
                    if (CameraSetting.this.mFocusMagnificationNextRatio != 0) {
                        AvailableInfo.update();
                        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE")) {
                            CameraSetting.this.mFocusMagnificationRatio = 1;
                            CameraSetting.this.mFocusMagnificationActualRatio = 100;
                        } else {
                            isStopMagnifier = true;
                        }
                    } else if (CameraSetting.this.mFocusMagnificationRatio != 1) {
                        isStopMagnifier = true;
                    }
                } else {
                    AvailableInfo.update();
                    if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE")) {
                        CameraSetting.this.mFocusMagnificationRatio = 1;
                        CameraSetting.this.mFocusMagnificationActualRatio = 100;
                    } else {
                        isStopMagnifier = true;
                    }
                }
            } else {
                CameraSetting.this.mFocusMagnificationRatio = this.mMagLevel;
                CameraSetting.this.mFocusMagnificationActualRatio = this.mMag;
            }
            if (CameraSetting.this.mFocusMagnificationNextRatio == CameraSetting.this.mFocusMagnificationRatio) {
                CameraSetting.this.mFocusMagnificationNextRatio = 0;
            }
            if (isStopMagnifier) {
                Log.i(CameraSetting.TAG, CameraSetting.LOG_MSG_STOP_FOCUS_MAGNIGICATION_ON_CHANGED);
                if (CameraSetting.this.mFocusMagnificationRatio == 0) {
                    CameraSetting.this.mFocusMagnificationNextRatio = 0;
                } else {
                    CameraSetting.this.stopFocusMagnification();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RunnableOnSendNotifyToFocusMagnificaction implements Runnable {
        private String mNotify;

        RunnableOnSendNotifyToFocusMagnificaction(String notify) {
            this.mNotify = notify;
        }

        @Override // java.lang.Runnable
        public void run() {
            CameraSetting.this.checkFocusMagnificactionStop(this.mNotify);
            if (this.mNotify.equals(CameraNotificationManager.ZOOM_INFO_CHANGED) && CameraSetting.this.mFocusMagnificationRatio == 0) {
                int zoomRatio = CameraSetting.this.getDigitalZoomMagnification();
                if (CameraSetting.this.mDigitalZoomRatio != zoomRatio) {
                    CameraSetting.this.mChangeZoomRatio = true;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FocusMagnificationListener implements CameraEx.PreviewMagnificationListener {
        private FocusMagnificationListener() {
        }

        public void onChanged(boolean onoff, int mag, int magLevel, Pair<Integer, Integer> coordinates, CameraEx camex) {
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_FOCUS_MAGNIGICATION_CHANGED).append(onoff).append(", ").append(mag);
            if (coordinates != null) {
                builder.append(" ( ").append(coordinates.first).append(", ").append(coordinates.second).append(" ) ");
            }
            Log.d(CameraSetting.TAG, builder.toString());
            Handler handler = CameraSetting.this.mMainLooperHandler;
            CameraSetting cameraSetting = CameraSetting.this;
            if (!onoff) {
                mag = 0;
            }
            if (!onoff) {
                magLevel = 0;
            }
            handler.post(new RunnableOnFocusMagnificactionChanged(mag, magLevel, coordinates));
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED);
        }

        public void onInfoUpdated(boolean focusRing, Pair<Integer, Integer> focusPos, CameraEx camex) {
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_FOCUS_RING_CHANGED).append(focusRing);
            if (focusPos != null) {
                builder.append(" ( ").append(focusPos.first).append(", ").append(focusPos.second).append(" ) ");
            }
            Log.d(CameraSetting.TAG, builder.toString());
            CameraSetting.this.mIsFocusRingRotating = focusRing;
            if (!focusRing) {
                CameraSetting.mNotify.requestNotify(CameraNotificationManager.FOCUS_RING_ROTATION_STOPPED);
            } else {
                CameraSetting.this.mFocusedPositionOnRingRotated = focusPos;
                CameraSetting.mNotify.requestNotify(CameraNotificationManager.FOCUS_RING_ROTATION_STARTED);
            }
        }
    }

    protected void registerFocusMagnificationListener() {
        if (1 <= getPfApiVersion()) {
            FocusMagnificationListener listener = new FocusMagnificationListener();
            this.mCameraEx.setPreviewMagnificationListener(listener);
        }
    }

    protected void unregisterFocusMagnificationListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setPreviewMagnificationListener((CameraEx.PreviewMagnificationListener) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MfAssistTimeoutTask extends TimerTask {
        private MfAssistTimeoutTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            Log.i(CameraSetting.TAG, CameraSetting.LOG_MSG_MFASSIST_TIMEOUT);
            CameraSetting.this.stopFocusMagnification();
        }
    }

    public void scheduleMfAssistTimeout(int duration) {
        if (this.mFocusMagnificationRatio == 0) {
            Log.w(TAG, LOG_MSG_MFASSIST_TIMER_REQUESTED_UNMAGNIFYING);
            return;
        }
        if (this.mMfAssistTimeoutTask != null) {
            Log.w(TAG, LOG_MSG_TASK_ALREADY_EXIST);
            cancelMfAssistTimeout();
        }
        if (this.mMfAssistTimer == null) {
            this.mMfAssistTimer = new Timer();
        }
        if (this.mMfAssistTimeoutTask == null) {
            this.mMfAssistTimeoutTask = new MfAssistTimeoutTask();
        }
        this.mMfAssistTimer.schedule(this.mMfAssistTimeoutTask, duration);
    }

    public void cancelMfAssistTimeout() {
        if (this.mMfAssistTimeoutTask != null) {
            Log.d(TAG, LOG_MSG_CANCEL_MFASSIST_TIMER);
            this.mMfAssistTimeoutTask.cancel();
            this.mMfAssistTimeoutTask = null;
        }
        if (this.mMfAssistTimer != null) {
            this.mMfAssistTimer.purge();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAvailableMagnificationPointMove() {
        return this.mIsAvailableMagnificationPointMove;
    }

    protected void registerFocusDriveListener() {
        if (Environment.isAvailableFocusDrive()) {
            CameraEx.FocusDriveListener listener = new FocusDriveListener();
            this.mCameraEx.setFocusDriveListener(listener);
        }
    }

    protected void unregisterFocusDriveListener() {
        if (Environment.isAvailableFocusDrive()) {
            this.mCameraEx.setFocusDriveListener((CameraEx.FocusDriveListener) null);
        }
        this.mFocusCurrentPosition = 0;
        this.mFocusMaxPosition = 0;
    }

    public void moveFocusDrive(int direction) {
        moveFocusDrive(direction, FOCUS_DRIVE_SPEED);
    }

    public void moveFocusDrive(int direction, int speed) {
        if (Environment.isAvailableFocusDrive()) {
            this.mCameraEx.startOneShotFocusDrive(direction, speed);
        }
    }

    public int getFocusCurrentPosition() {
        return this.mFocusCurrentPosition;
    }

    public int getFocusMaxPosition() {
        return this.mFocusMaxPosition;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FocusDriveListener implements CameraEx.FocusDriveListener {
        private FocusDriveListener() {
        }

        public void onChanged(CameraEx.FocusPosition pos, CameraEx cameraEx) {
            CameraSetting.this.mFocusCurrentPosition = pos.currentPosition;
            CameraSetting.this.mFocusMaxPosition = pos.maxPosition;
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_FOCUS_DRIVE_ON_CHANGED).append(pos.currentPosition).append(" / ").append(pos.maxPosition);
            Log.d(CameraSetting.TAG, builder.toString());
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.FOCUS_POSITION_CHANGED);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ApscModeListener implements CameraEx.AutoApscModeListener {
        private ApscModeListener() {
        }

        public void onChanged(String mode, CameraEx cam) {
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_APSC_MODE_CHANGED).append(mode);
            Log.d(CameraSetting.TAG, builder.toString());
            CameraSetting.this.mAutoApscMode = mode;
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.APSC_MODE_CHANGED);
            CameraSetting.this.mMainLooperHandler.post(CameraSetting.this.mUpdateMediaRemainingRunnable);
        }
    }

    protected void registerApscModeListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setAutoApscModeListener(new ApscModeListener());
        }
        resetApscEffectiveValue();
    }

    protected void unregisterApscModeListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setAutoApscModeListener((CameraEx.AutoApscModeListener) null);
        }
        resetApscEffectiveValue();
    }

    public void resetApscEffectiveValue() {
        this.mAutoApscMode = null;
    }

    protected void registerNDFilterListener() {
        resetNDFilterStatus();
        if (6 <= getPfApiVersion()) {
            this.mCameraEx.setNDFilterListener(new NDFilterListener());
        }
    }

    protected void unregisterNDFIlterListener() {
        if (6 <= getPfApiVersion()) {
            this.mCameraEx.setNDFilterListener((CameraEx.NDFilterStatusListener) null);
        }
        resetNDFilterStatus();
    }

    public void resetNDFilterStatus() {
        mNDFilterStatus = false;
    }

    protected void registerTrackingFocusListener() {
        resetTrackingFocusInfo();
        if (8 <= getPfApiVersion()) {
            this.mCameraEx.setTrackingFocusListener(new TrackingFocusListener());
        }
    }

    protected void unregisterTrackingFocusListener() {
        if (8 <= getPfApiVersion()) {
            this.mCameraEx.setTrackingFocusListener((CameraEx.TrackingFocusListener) null);
        }
        resetTrackingFocusInfo();
    }

    void resetTrackingFocusInfo() {
        mTrackingFocusInfo = null;
    }

    public CameraEx.TrackingFocusInfo getTrackingFocusInfo() {
        return mTrackingFocusInfo;
    }

    public int getOpticalZoomMagnification() {
        return this.mOpticalZoomMagnification;
    }

    public int getDigitalZoomMagnification() {
        return this.mDigitalZoomMagnification;
    }

    public String getDigitalZoomType() {
        return this.mCurrentZoomType;
    }

    public boolean getZoomStopped() {
        return this.mIsZoomStopped;
    }

    public int getOpticalPosition() {
        return this.mOpticalPosition;
    }

    public int getDigitalPosition() {
        return this.mDigitalPosition;
    }

    public boolean isZoomModeInitialized() {
        if (Environment.isNewBizDeviceLSC()) {
            return this.mIsZoomModeInitialized;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ZoomInfoChangedListner implements CameraEx.ZoomChangeListener {
        private ZoomInfoChangedListner() {
        }

        public void onChanged(CameraEx.ZoomInfo zoomInfo, CameraEx cameraEx) {
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_ZOOMCHANGELISTENER_ONCHANGED);
            Log.i(CameraSetting.TAG, builder.toString());
            if (zoomInfo.digitalZoomType != null && zoomInfo.digitalMagnification < 110) {
                CameraSetting.this.mDigitalZoomMagnification = DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED;
            } else {
                CameraSetting.this.mDigitalZoomMagnification = zoomInfo.digitalMagnification;
            }
            CameraSetting.this.mOpticalZoomMagnification = zoomInfo.opticalMagnification;
            if (CameraSetting.this.mCurrentZoomType != null || zoomInfo.digitalZoomType == null) {
                if (CameraSetting.this.mCurrentZoomType != null && zoomInfo.digitalZoomType == null) {
                    CameraSetting.mNotify.requestNotify(CameraNotificationManager.DIGITAL_ZOOM_ONOFF_CHANGED, false);
                }
            } else {
                CameraSetting.mNotify.requestNotify(CameraNotificationManager.DIGITAL_ZOOM_ONOFF_CHANGED, true);
            }
            CameraSetting.this.mCurrentZoomType = zoomInfo.digitalZoomType;
            CameraSetting.this.mIsZoomStopped = zoomInfo.stopped;
            CameraSetting.this.mOpticalPosition = zoomInfo.opticalPosition;
            CameraSetting.this.mDigitalPosition = zoomInfo.digitalPosition;
            if (Environment.isNewBizDeviceLSC() && !CameraSetting.this.mIsZoomModeInitialized) {
                CameraSetting.this.mIsZoomModeInitialized = true;
            }
            CameraSetting.this.calcFocalLengthByZoomInfo();
            CameraSetting.this.mMainLooperHandler.post(new RunnableOnSendNotifyToFocusMagnificaction(CameraNotificationManager.ZOOM_INFO_CHANGED));
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.ZOOM_INFO_CHANGED, zoomInfo);
        }
    }

    public void registZoomChangeListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setZoomChangeListener(new ZoomInfoChangedListner());
        }
        resetDigitalZoomInfoValue();
    }

    protected void unregistZoomChangeListener() {
        if (1 <= getPfApiVersion()) {
            this.mCameraEx.setZoomChangeListener((CameraEx.ZoomChangeListener) null);
        }
    }

    public void resetDigitalZoomInfoValue() {
        this.mOpticalZoomMagnification = -1;
        this.mDigitalZoomMagnification = -1;
        this.mOpticalPosition = -1;
        this.mDigitalPosition = -1;
        this.mCurrentZoomType = null;
        this.mIsZoomStopped = false;
    }

    public void resetDigitalZoom() {
        if (1 <= getPfApiVersion()) {
            try {
                this.mCameraEx.resetDigitalZoom();
            } catch (RuntimeException e) {
                Log.e(TAG, "digital zoom reset error", e);
            }
        }
    }

    private void initMovieListeners() {
        if (Environment.isMovieAPISupported() && 2 == getCurrentMode()) {
            resetMovieRecListenerValue();
            MovieShootingExecutor.setOnRecTimeListener(new MovieRecTimeChangedListener());
            MovieShootingExecutor.setOnRecRemainTimeListener(new MovieRemainTimeChangedListener());
            MovieShootingExecutor.setOnErrorListener(new MovieRecErrorListener());
        }
    }

    private void suspendMovieListeners() {
        if (Environment.isMovieAPISupported() && 2 == getCurrentMode()) {
            resetMovieRecListenerValue();
            MovieShootingExecutor.setOnRecTimeListener(null);
            MovieShootingExecutor.setOnRecRemainTimeListener(null);
            MovieShootingExecutor.setOnErrorListener(null);
        }
    }

    private void resetMovieRecListenerValue() {
        this.mMovieRecTime = -1000;
        this.mMovieRecRemainTime = -1000;
        this.mMovieRecErrorFactor = -1001;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MovieRecTimeChangedListener implements MediaRecorder.OnRecTimeListener {
        private MovieRecTimeChangedListener() {
        }

        public void onChanged(int time, MediaRecorder mr) {
            CameraSetting.this.mMovieRecTime = time;
            CameraSetting.mNotify.requestNotify(CameraNotificationManager.MOVIE_REC_TIME_CHANGED, Integer.valueOf(CameraSetting.this.mMovieRecTime));
        }
    }

    public int getMovieRecTime() {
        return this.mMovieRecTime;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MovieRemainTimeChangedListener implements MediaRecorder.OnRecRemainListener {
        private MovieRemainTimeChangedListener() {
        }

        public void onChanged(int time, MediaRecorder mr) {
            CameraSetting.this.mMovieRecRemainTime = time;
            MediaNotificationManager.getInstance().updateRemainMovieRecTime(CameraSetting.this.mMovieRecRemainTime);
        }
    }

    public int getMovieRecRemainTime() {
        return this.mMovieRecRemainTime;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MovieRecErrorListener implements MediaRecorder.OnErrorListener {
        private MovieRecErrorListener() {
        }

        public void onError(int error, MediaRecorder mr) {
            CameraSetting.this.mMovieRecErrorFactor = error;
            StringBuilder builder = CameraSetting.sStringBuilder.get();
            builder.replace(0, builder.length(), CameraSetting.LOG_MSG_ON_ERROR_LISTENER).append(CameraSetting.LOG_MSG_ERROR).append(Integer.toString(CameraSetting.this.mMovieRecErrorFactor));
            Log.i(CameraSetting.TAG, builder.toString());
            MediaNotificationManager.getInstance().updateMovieRecErrorFactor(CameraSetting.this.mMovieRecErrorFactor);
        }
    }

    public int getMovieRecErrorFactor() {
        return this.mMovieRecErrorFactor;
    }

    private static void convertFocusAreaRectIndex(CameraEx.FocusAreaInfos[] infosArray) {
        for (CameraEx.FocusAreaInfos infos : infosArray) {
            convertFocusAreaRectIndexForMulti(infos);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void convertFocusAreaRectIndexForMulti(CameraEx.FocusAreaInfos infos) {
        if (FocusAreaController.MULTI.equals(infos.focusAreaMode)) {
            CameraEx.FocusAreaRectInfo[] arr$ = infos.rectInfos;
            for (CameraEx.FocusAreaRectInfo info : arr$) {
                if (FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43.length >= info.index) {
                    info.index = FOCUSAREA_RECT_INDEX_CONVERT_FOR_IMAGER43[info.index];
                }
            }
        }
    }

    public static int getImagerAspect() {
        if (PF_VER_SUPPPORTS_IMAGER_ASPECT > getPfApiVersion()) {
            return 32;
        }
        int ret = ScalarProperties.getInt("device.imager.aspect");
        return ret;
    }

    public boolean isIntelligentActiveOn() {
        return false;
    }

    public static boolean isFocusHoldSupported() {
        return 7 <= getPfApiVersion() && Environment.isNewBizDeviceLSC();
    }

    public boolean startFocusHold() {
        if (!isFocusHoldSupported()) {
            return false;
        }
        this.mCameraEx.startFocusHold();
        return true;
    }

    public boolean stopFocusHold() {
        if (!isFocusHoldSupported()) {
            return false;
        }
        this.mCameraEx.stopFocusHold();
        return true;
    }

    public static int convertExecutorDef2CamSetDef(int exec_recmode) {
        if (1 == exec_recmode || 4 == exec_recmode) {
            return 1;
        }
        if (2 != exec_recmode && 8 != exec_recmode) {
            return 32768;
        }
        return 2;
    }
}
