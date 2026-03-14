package com.sony.imaging.app.pictureeffectplus.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.parameters.Keys;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.PictureEffectPlusBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectPlusController extends PictureEffectController {
    private static final int B_PHASE = 330;
    private static final int B_RANGE = 30;
    private static final int B_SAT = 14;
    public static final String EFFECT_SET_STATUS = "effect_setting_status";
    public static final String GLOBAL_MENU_STATUS = "global_menu_status";
    private static int G_PHASE = 0;
    private static final int G_RANGE = 63;
    private static final int G_SAT = 2;
    public static final String ILLUST_HIGH = "illust-3";
    public static final String ILLUST_LOW = "illust-1";
    public static final String ILLUST_MID = "illust-2";
    private static final String ILLUST_PREFIX = "illust-";
    public static final ArrayList<String> ITEM_ID_SA_USE_EFFECT;
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETMAXPICTUREEFFECTILLUSTEFFECTLEVEL = "getMaxPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_GETMAXTOYCAMERATUNING = "getMaxToyCameraTuning = ";
    private static final String LOG_MSG_GETMINICOMBEFFECT = "getMiniCombEffect = ";
    private static final String LOG_MSG_GETMINPICTUREEFFECTILLUSTEFFECTLEVEL = "getMinPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_GETMINTOYCAMERATUNING = "getMinToyCameraTuning = ";
    private static final String LOG_MSG_GETPICTUREEFFECT = "getPictureEffect = ";
    private static final String LOG_MSG_GETPICTUREEFFECTILLUSTEFFECTLEVEL = "getPictureEffectIllustEffectLevel =";
    private static final String LOG_MSG_GETPICTUREEFFECTMINIATUREFOCUSAREA = "getPictureEffectMiniatureFocusArea = ";
    private static final String LOG_MSG_GETPICTUREEFFECTPARTCOLORPLUSEFFECT_CH0 = "getPictureEffectPartColorPlusEffect_ch0 = ";
    private static final String LOG_MSG_GETPICTUREEFFECTPARTCOLORPLUSEFFECT_CH1 = "getPictureEffectPartColorPlusEffect_ch1 = ";
    private static final String LOG_MSG_GETPICTUREEFFECTSOFTHIGHKEYEFFECT = "getPictureEffectSoftHightKeyEffect = ";
    private static final String LOG_MSG_GETPICTUREEFFECTTOYCAMERAEFFECT = "getPictureEffectToyCameraEffect = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTMINIATUREFOCUSAREAS = "getSupportedPictureEffectMiniatureFocusAreas = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS_CH0 = "getSupportedPictureEffectPartColorPlusEffects_ch0 = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS_CH1 = "getSupportedPictureEffectPartColorPlusEffects_ch1 = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTSOFTHIGHKEYEFFECTS = "getSupportedPictureEffectSoftHightKeyEffects = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTTOYCAMERAEFFECTS = "getSupportedPictureEffectToyCameraEffects = ";
    private static final String LOG_MSG_GETTOYCAMERATUNING = "getToyCameraTuning = ";
    private static final String LOG_MSG_SETPICTUREEFFECTILLUSTEFFECTLEVEL = "setPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_SETPICTUREEFFECTMINIATUREFOCUSAREA = "setPictureEffectMiniatureFocusArea = ";
    private static final String LOG_MSG_SETPICTUREEFFECTPARTCOLORPLUSEFFECT_CH0 = "setPictureEffectPartColorPlus_CH_0_Effect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTPARTCOLORPLUSEFFECT_CH1 = "setPictureEffectPartColorPlus_CH_1_Effect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTPLUS = "setPictureEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTSOFTHIGHKEYEFFECT = "setPictureEffectSoftHightKeyEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTTOYCAMERAEFFECT = "setPictureEffectToyCameraEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTWATERCOLOREFFECTLEVEL = "setPictureEffectWaterColorEffectLevel = ";
    private static final String LOG_MSG_SETTOYCAMERATUNING = "setToyCameraTuning = ";
    private static final String LOG_MSG_SUPPORTED_MINI_COMB_EFFEECT = "SUPPORTED_MINI_COMB_EFFEECT = ";
    public static final String MINIATURE_NORMAL = "comb-off";
    public static final String MINIATURE_RETRO = "min_retro";
    public static final int MINIATURE_STATUS_COMB = 2;
    public static final int MINIATURE_STATUS_OFF = 0;
    public static final int MINIATURE_STATUS_ONLY = 1;
    public static final String MINIATURE_TOY_CAMERA_COOL = "cool";
    public static final String MINIATURE_TOY_CAMERA_GREEN = "green";
    public static final String MINIATURE_TOY_CAMERA_MAGENTA = "magenta";
    public static final String MINIATURE_TOY_CAMERA_NORMAL = "normal";
    public static final String MINIATURE_TOY_CAMERA_WARM = "warm";
    public static final String MODE_ILLUST = "illust";
    public static final String MODE_MINIATURE_AREA = "miniature-area";
    public static final String MODE_MINIATURE_EFFECT = "miniature-effect";
    public static final String MODE_MINIATURE_PLUS = "miniature-plus";
    public static final String MODE_PART_COLOR_CH0 = "part-color-ch0";
    public static final String MODE_PART_COLOR_CH1 = "part-color-ch1";
    public static final String MODE_PART_COLOR_PLUS = "part-color-plus";
    public static final String MODE_TOY_CAMERA_COLOR = "toy-camera-color";
    public static final String MODE_TOY_CAMERA_DARKNESS = "toy-camera-darkness";
    public static final String MODE_TOY_CAMERA_PLUS = "toy-camera-plus";
    public static final String MODE_WATER_COLOR = "watercolor";
    public static final String NO_OPTION_VALUE = "hoge";
    public static final String PART_COLOR_BLUE = "partblue";
    public static final String PART_COLOR_BLUE_CH0 = "partbluech0";
    public static final String PART_COLOR_CUSTOM = "part-color-custom";
    public static final String PART_COLOR_CUSTOM_CH0 = "part-color-custom-ch0";
    public static final String PART_COLOR_CUSTOM_SET = "part-color-custom-set";
    public static final String PART_COLOR_CUSTOM_SET_CH0 = "part-color-custom-set-ch0";
    public static final String PART_COLOR_GREEN = "partgreen";
    public static final String PART_COLOR_GREEN_CH0 = "partgreench0";
    public static final String PART_COLOR_NON_SET = "part-color-non-set";
    public static final String PART_COLOR_NON_SET_CH0 = "part-color-non-set-ch0";
    public static final String PART_COLOR_RED_CH0 = "partredch0";
    public static final int PART_COLOR_STATUS_OFF = 0;
    public static final int PART_COLOR_STATUS_ON = 1;
    public static final String PART_COLOR_YELLOW_CH0 = "partyellowch0";
    public static final String PICTUREEFFECTMENU = "PictureEffectMenu";
    public static final String PICTUREEFFECTPLUS = "ApplicationTop";
    private static int R_PHASE = 0;
    private static int R_RANGE = 0;
    private static int R_SAT = 0;
    private static final int SA_EFFECT_OFF = 0;
    private static final int SA_EFFECT_ON = 1;
    private static final String SET_PICTURE_EFFECT_PART_COLOR_CH01 = "SET_PICTURE_EFFECT_PART_COLOR_CH01 = ";
    public static final String SOFT_HIGH_KEY_BLUE = "softhighkeyblue";
    public static final String SOFT_HIGH_KEY_GREEN = "softhighkeygreen";
    public static final String SOFT_HIGH_KEY_PINK = "softhighkeypink";
    private static final ArrayList<String> SUPPORTED_MINI_COMB_EFFEECT;
    public static final String TOY_CAMERA_COOL = "toy-cool";
    public static final String TOY_CAMERA_GREEN = "toy-green";
    public static final String TOY_CAMERA_HIGH = "toy-camera-16";
    public static final String TOY_CAMERA_LOW = "toy-camera--16";
    public static final String TOY_CAMERA_MAGENTA = "toy-magenta";
    public static final String TOY_CAMERA_MID = "toy-camera-0";
    public static final String TOY_CAMERA_NORMAL = "toy-normal";
    private static final String TOY_CAMERA_PREFIX = "toy-camera-";
    public static final String TOY_CAMERA_WARM = "toy-warm";
    public static final int TOY_STATUS_OFF = 0;
    public static final int TOY_STATUS_ON = 1;
    public static final String WATER_COLOR_HIGH = "watercolor-3";
    public static final String WATER_COLOR_LOW = "watercolor-1";
    public static final String WATER_COLOR_MID = "watercolor-2";
    private static final String WATER_COLOR_PREFIX = "watercolor-";
    private static final int Y_PHASE = 155;
    private static final int Y_RANGE = 18;
    private static final int Y_SAT = 18;
    private static final int ZERO = 0;
    private static boolean ch0;
    private static boolean ch1;
    private static BackUpUtil mBackupUtil;
    private static int mCurPartColorPlate;
    private static String mDriveModeBeforeSA;
    private static PictureEffectPlusController mInstance;
    private static int mIsMiniature;
    private static int mIsPartColor;
    private static int mIsSAEffect;
    private static int mIsToy;
    private static String mMiniArea;
    private static String mMiniCombEffect;
    private static String mMode;
    private static int mTargetCh;
    private String callLayout;
    private int category;
    private boolean ovf_setting;
    private String previewMode_settting;
    private static final String TAG = AppLog.getClassName();
    private static int[] mEnabledCh = null;
    public static String option_param = PictureEffectController.PART_COLOR_RED;
    private static String[] ch_color_str = new String[2];
    private static CameraEx.SelectedColor RedColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor YellowColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor GreenColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor BlueColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor[] ch0_1_CustomColor = new CameraEx.SelectedColor[2];
    private static CameraEx.SelectedColor capturedColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor adjustedColor = new CameraEx.SelectedColor();
    private static CameraEx.SelectedColor[] ch0_1_Color = new CameraEx.SelectedColor[2];
    private static String ID_PICTUREEFFECTPLUS_CURRENT_EFFECT = null;
    private static String ID_PICTUREEFFECTPLUS_ILLUST_LEVEL = null;
    private static String ID_PICTUREEFFECTPLUS_SOFT_HIGH_KEY_COLOR = null;
    private static String ID_PICTUREEFFECTPLUS_POSTER_COLOR = null;
    private static String ID_PICTUREEFFECTPLUS_HDR_LEVEL = null;
    private static String ID_PICTUREEFFECTPLUS_SOFT_FOCUS_LEVEL = null;
    private static boolean PART_COLOR_ADJUST_STATUS_NO = false;
    private static boolean PART_COLOR_ADJUST_STATUS_YES = true;
    private static boolean ch0_colorAdjusteStatus = false;
    private static boolean ch1_colorAdjusteStatus = false;
    public static boolean COLOR_CAPTURE_STATUS_NO = false;
    public static boolean COLOR_CAPTURE_STATUS_YES = true;
    private static boolean colorCaptureStatus = false;
    private static final ArrayList<String> ITEM_ID_BACK_TO_THIRD_MENU = new ArrayList<>();
    private boolean plusParameterSelected = false;
    private boolean isAppTopOpenFromMenu = false;
    private boolean isShootingScreenOpened = false;
    private boolean isComingFromApplicationSettings = false;
    private boolean isComeFromColorIndication = false;
    private int selectedPlate = 0;
    private int preSelectedPlateForMiniAndToy = 0;
    private boolean isComeFromColorAdjustment = false;
    private int prevSelectedPlate = 0;
    protected String FUNC_NAME = "";
    private CameraEx.SelectedColor ch0_1_Color_temp = new CameraEx.SelectedColor();
    private String mFirstPlateColor_temp = null;
    private final int MIN_SAT = 0;
    private final int MAX_SAT = 64;
    private String mMiniatureEffect = MINIATURE_NORMAL;
    private String previousColorCh0 = "";
    private String previousColorCh1 = "";
    private String previousMiniArea = "";
    private String previousMiniColor = "";
    private String previousToyColor = "";
    private String previousToyDarkness = "";
    private String previousOptionValue = "";
    private String currentEffect = "";
    private CameraEx.SelectedColor[] previous_ch0_1_Color = new CameraEx.SelectedColor[2];
    private CameraEx.SelectedColor[] previous_custom_ch0_1_Color = new CameraEx.SelectedColor[2];

    public boolean isComeFromColorAdjustment() {
        return this.isComeFromColorAdjustment;
    }

    public void setComeFromColorAdjustment(boolean isComeFromColorAdjustment) {
        this.isComeFromColorAdjustment = isComeFromColorAdjustment;
    }

    static {
        ITEM_ID_BACK_TO_THIRD_MENU.add(PictureEffectController.MODE_POSTERIZATION);
        ITEM_ID_BACK_TO_THIRD_MENU.add(PictureEffectController.MODE_SOFT_FOCUS);
        ITEM_ID_BACK_TO_THIRD_MENU.add(PictureEffectController.MODE_HDR_ART);
        ITEM_ID_BACK_TO_THIRD_MENU.add("illust");
        ITEM_ID_BACK_TO_THIRD_MENU.add(PictureEffectController.MODE_SOFT_HIGH_KEY);
        ITEM_ID_BACK_TO_THIRD_MENU.add(MODE_PART_COLOR_PLUS);
        ITEM_ID_BACK_TO_THIRD_MENU.add(MODE_TOY_CAMERA_PLUS);
        ITEM_ID_BACK_TO_THIRD_MENU.add(MODE_MINIATURE_PLUS);
        ITEM_ID_SA_USE_EFFECT = new ArrayList<>();
        ITEM_ID_SA_USE_EFFECT.add("illust");
        ITEM_ID_SA_USE_EFFECT.add("watercolor");
        ITEM_ID_SA_USE_EFFECT.add(PictureEffectController.MODE_RICH_TONE_MONOCHROME);
        ITEM_ID_SA_USE_EFFECT.add(PictureEffectController.MODE_HDR_ART);
        ITEM_ID_SA_USE_EFFECT.add(PictureEffectController.MODE_SOFT_FOCUS);
        R_PHASE = AppRoot.USER_KEYCODE.LEFT;
        R_RANGE = 20;
        R_SAT = 26;
        G_PHASE = 225;
        mIsMiniature = 0;
        mIsSAEffect = 0;
        mDriveModeBeforeSA = null;
        mIsToy = 0;
        mIsPartColor = 0;
        mMiniArea = "auto";
        mMiniCombEffect = MINIATURE_NORMAL;
        SUPPORTED_MINI_COMB_EFFEECT = new ArrayList<>();
        SUPPORTED_MINI_COMB_EFFEECT.add(MINIATURE_NORMAL);
        SUPPORTED_MINI_COMB_EFFEECT.add(MINIATURE_RETRO);
        SUPPORTED_MINI_COMB_EFFEECT.add("normal");
        SUPPORTED_MINI_COMB_EFFEECT.add("cool");
        SUPPORTED_MINI_COMB_EFFEECT.add("warm");
        SUPPORTED_MINI_COMB_EFFEECT.add("green");
        SUPPORTED_MINI_COMB_EFFEECT.add("magenta");
    }

    public void setColorAdjustStatus(int ch, boolean status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setColorAdjustStatus:" + status);
        if (ch == 0) {
            ch0_colorAdjusteStatus = status;
        } else if (1 == ch) {
            ch1_colorAdjusteStatus = status;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean getColorAdjustStatus(int ch) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ch == 0) {
            AppLog.exit(TAG, AppLog.getMethodName());
            return ch0_colorAdjusteStatus;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ch1_colorAdjusteStatus;
    }

    public void setColorCaptureStatus(boolean status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setColorCaptureStatus:" + status);
        colorCaptureStatus = status;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean getColorCaptureStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return colorCaptureStatus;
    }

    public static PictureEffectPlusController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            AppLog.enter(TAG, AppLog.getMethodName());
            createInstance();
            Log.i(TAG, "PictureEffectPlusController:create instance");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static PictureEffectPlusController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new PictureEffectPlusController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private void initSelectedColors() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.category == 2) {
            R_PHASE = 90;
            R_RANGE = 33;
            R_SAT = 25;
            G_PHASE = 230;
        }
        RedColor.Phase = R_PHASE;
        RedColor.Range = R_RANGE;
        RedColor.Saturation = R_SAT;
        RedColor.Y = 0;
        RedColor.Cb = 0;
        RedColor.Cr = 0;
        YellowColor.Phase = Y_PHASE;
        YellowColor.Range = 18;
        YellowColor.Saturation = 18;
        YellowColor.Y = 0;
        YellowColor.Cb = 0;
        YellowColor.Cr = 0;
        GreenColor.Phase = G_PHASE;
        GreenColor.Range = G_RANGE;
        GreenColor.Saturation = 2;
        GreenColor.Y = 0;
        GreenColor.Cb = 0;
        GreenColor.Cr = 0;
        BlueColor.Phase = B_PHASE;
        BlueColor.Range = 30;
        BlueColor.Saturation = 14;
        BlueColor.Y = 0;
        BlueColor.Cb = 0;
        BlueColor.Cr = 0;
        adjustedColor.Phase = 0;
        adjustedColor.Range = 0;
        adjustedColor.Saturation = 0;
        adjustedColor.Y = 0;
        adjustedColor.Cb = 0;
        adjustedColor.Cr = 0;
        for (int i = 0; i < ch0_1_Color.length; i++) {
            ch0_1_Color[i] = new CameraEx.SelectedColor();
            ch0_1_CustomColor[i] = new CameraEx.SelectedColor();
            this.previous_ch0_1_Color[i] = new CameraEx.SelectedColor();
            this.previous_custom_ch0_1_Color[i] = new CameraEx.SelectedColor();
        }
        ch0_1_CustomColor[0].Phase = R_PHASE;
        ch0_1_CustomColor[0].Range = R_RANGE;
        ch0_1_CustomColor[0].Saturation = R_SAT;
        ch0_1_CustomColor[0].Y = 0;
        ch0_1_CustomColor[0].Cb = 0;
        ch0_1_CustomColor[0].Cr = 0;
        ch0_1_CustomColor[1].Phase = R_PHASE;
        ch0_1_CustomColor[1].Range = R_RANGE;
        ch0_1_CustomColor[1].Saturation = R_SAT;
        ch0_1_CustomColor[1].Y = 0;
        ch0_1_CustomColor[1].Cb = 0;
        ch0_1_CustomColor[1].Cr = 0;
        ch0_1_Color[0].Phase = R_PHASE;
        ch0_1_Color[0].Range = R_RANGE;
        ch0_1_Color[0].Saturation = R_SAT;
        ch0_1_Color[0].Y = 0;
        ch0_1_Color[0].Cb = 0;
        ch0_1_Color[0].Cr = 0;
        ch0_1_Color[1].Phase = R_PHASE;
        ch0_1_Color[1].Range = R_RANGE;
        ch0_1_Color[1].Saturation = R_SAT;
        ch0_1_Color[1].Y = 0;
        ch0_1_Color[1].Cb = 0;
        ch0_1_Color[1].Cr = 0;
        capturedColor.Phase = 0;
        capturedColor.Range = 0;
        capturedColor.Saturation = 0;
        capturedColor.Y = 0;
        capturedColor.Cb = 0;
        capturedColor.Cr = 0;
        ch_color_str[0] = PART_COLOR_RED_CH0;
        ch_color_str[1] = PART_COLOR_NON_SET;
        mMode = "off";
        Log.i(TAG, "initSelectedColors");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setPartColorCh(int targetCh) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mTargetCh = targetCh;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getPartColorCh() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mTargetCh;
    }

    public void setPartColorCurrentPlate(int targetCh) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mCurPartColorPlate = targetCh;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getPartColorCurrentPlate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mCurPartColorPlate;
    }

    protected PictureEffectPlusController() {
        this.category = -1;
        this.ovf_setting = false;
        this.previewMode_settting = "";
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSetting camSetting = CameraSetting.getInstance();
        camSetting.addParameterComparator(new waterComparator(camSetting, NotificationTag.PICTURE_EFFECT_PLUS_CHANGE));
        camSetting.addParameterComparator(new illustComparator(camSetting, NotificationTag.PICTURE_EFFECT_PLUS_CHANGE));
        camSetting.addParameterComparator(new softHighKeyComparator(camSetting, NotificationTag.PICTURE_EFFECT_PLUS_CHANGE));
        camSetting.addParameterComparator(new toyTuningComparator(camSetting, NotificationTag.PICTURE_EFFECT_PLUS_CHANGE));
        camSetting.addParameterComparator(new toyEffectComparator(camSetting, NotificationTag.PICTURE_EFFECT_PLUS_CHANGE));
        mBackupUtil = BackUpUtil.getInstance();
        initSelectedColors();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        this.previewMode_settting = ((CameraEx.ParametersModifier) p.second).getShootingPreviewMode();
        this.ovf_setting = ((CameraEx.ParametersModifier) p.second).getOVFPreviewMode();
        this.category = ScalarProperties.getInt("model.category");
        Log.i(TAG, "PictureEffectPlusController");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class waterComparator extends CameraSetting.SimpleComparator<Integer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public waterComparator(CameraSetting cameraSetting, String tag) {
            super(tag);
            cameraSetting.getClass();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting._SimpleComparator
        public Integer getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
            return Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getPictureEffectWaterColorEffectLevel());
        }
    }

    /* loaded from: classes.dex */
    class illustComparator extends CameraSetting.SimpleComparator<Integer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public illustComparator(CameraSetting cameraSetting, String tag) {
            super(tag);
            cameraSetting.getClass();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting._SimpleComparator
        public Integer getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
            return Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getPictureEffectIllustEffectLevel());
        }
    }

    /* loaded from: classes.dex */
    class softHighKeyComparator extends CameraSetting.SimpleComparator<String> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public softHighKeyComparator(CameraSetting cameraSetting, String tag) {
            super(tag);
            cameraSetting.getClass();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting._SimpleComparator
        public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
            return ((CameraEx.ParametersModifier) obj.second).getPictureEffectSoftHightKeyEffect();
        }
    }

    /* loaded from: classes.dex */
    class toyTuningComparator extends CameraSetting.SimpleComparator<Integer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public toyTuningComparator(CameraSetting cameraSetting, String tag) {
            super(tag);
            cameraSetting.getClass();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting._SimpleComparator
        public Integer getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
            return Integer.valueOf(((CameraEx.ParametersModifier) obj.second).getToyCameraTuning());
        }
    }

    /* loaded from: classes.dex */
    class toyEffectComparator extends CameraSetting.SimpleComparator<String> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public toyEffectComparator(CameraSetting cameraSetting, String tag) {
            super(tag);
            cameraSetting.getClass();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.shooting.camera.CameraSetting._SimpleComparator
        public String getValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> obj) {
            return ((CameraEx.ParametersModifier) obj.second).getPictureEffectToyCameraEffect();
        }
    }

    protected void setMiniatureStatus(int isMiniature) {
        Log.i(TAG, "setMiniatureStatus isMiniature:" + isMiniature);
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsMiniature = isMiniature;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getMiniatureStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mIsMiniature;
    }

    public void setSADriveMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int isMiniature = getMiniatureStatus();
        int isSAEffect = getSAEffectStatus();
        DriveModeController mDriveModeController = DriveModeController.getInstance();
        if (isMiniature == 0 && isSAEffect == 0) {
            String driveMode = getDriveModeBeforeSAEffect();
            if (driveMode != null) {
                Log.i(TAG, "DriveMode:" + driveMode);
                if (driveMode.equals(DriveModeController.SELF_TIMER_10S) || driveMode.equals(DriveModeController.SELF_TIMER_2S)) {
                    mDriveModeController.setValue(DriveModeController.SELF_TIMER, driveMode);
                } else if (driveMode.equals(DriveModeController.SELF_TIMER_BURST_10S_3SHOT) || driveMode.equals(DriveModeController.SELF_TIMER_BURST_10S_5SHOT)) {
                    mDriveModeController.setValue(DriveModeController.SELF_TIMER_BURST, driveMode);
                } else {
                    mDriveModeController.setValue(DriveModeController.DRIVEMODE, driveMode);
                }
            }
        } else if (!mDriveModeController.getValue().equals(DriveModeController.SINGLE)) {
            mDriveModeController.setValue(DriveModeController.DRIVEMODE, DriveModeController.SINGLE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setSAEffectStatus(int isSAEffect) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsSAEffect = isSAEffect;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getSAEffectStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mIsSAEffect;
    }

    public static void setDriveModeBeforeSAEffect(String driveMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mDriveModeBeforeSA = driveMode;
        if (mDriveModeBeforeSA != null) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_DRIVE_MODE_BEFORE_SAEFFECT, driveMode);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static String getDriveModeBeforeSAEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mBackupUtil == null) {
            mBackupUtil = BackUpUtil.getInstance();
        }
        mDriveModeBeforeSA = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_DRIVE_MODE_BEFORE_SAEFFECT, DriveModeController.SINGLE);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mDriveModeBeforeSA;
    }

    private void setMiniatureEffectBeforeAuto(String effect) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setMiniatureEffectBeforeAuto:" + effect);
        mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT_BEFORE_AUTO, effect);
        this.mMiniatureEffect = effect;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getMiniatureEffectBeforeAuto() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mMiniatureEffect;
    }

    private void setToyStatus(int isToy) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setToyStatus isToy:" + isToy);
        mIsToy = isToy;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getToyStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mIsToy;
    }

    public void setPartColorStatus(int isPartColor) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsPartColor = isPartColor;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getPartColorStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mIsPartColor;
    }

    private static void setMiniatureFocusArea(String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setMiniatureFocusArea area:" + value);
        if (!mMiniArea.equals(value)) {
            CameraNotificationManager.getInstance().requestNotify(NotificationTag.PICTURE_EFFECT_PLUS_CHANGE);
        }
        mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_AREA, value);
        mMiniArea = value;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getMiniatureFocusArea() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mMiniArea;
    }

    private void setMiniCombEffect(String effect) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setMiniCombEffect effect:" + effect);
        if (!mMiniCombEffect.equals(effect)) {
            CameraNotificationManager.getInstance().requestNotify(NotificationTag.PICTURE_EFFECT_PLUS_CHANGE);
        }
        mMiniCombEffect = effect;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getMiniCombEffect() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mMiniCombEffect;
    }

    protected int getMiniatureFilterArea(String miniArea) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int miniFilterArea = 1;
        if ("auto".equals(miniArea)) {
            miniFilterArea = 1;
        } else if (PictureEffectController.MINIATURE_VCENTER.equals(miniArea)) {
            miniFilterArea = 2;
        } else if (PictureEffectController.MINIATURE_UPPER.equals(miniArea)) {
            miniFilterArea = 5;
        } else if (PictureEffectController.MINIATURE_LOWER.equals(miniArea)) {
            miniFilterArea = 6;
        } else if (PictureEffectController.MINIATURE_RIGHT.equals(miniArea)) {
            miniFilterArea = 4;
        } else if (PictureEffectController.MINIATURE_LEFT.equals(miniArea)) {
            miniFilterArea = 3;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return miniFilterArea;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        if (itemId.equals(PICTUREEFFECTPLUS)) {
            setBackupEffectValue(itemId, value);
            if (ITEM_ID_SA_USE_EFFECT.contains(value)) {
                setSAEffectStatus(1);
            } else {
                setSAEffectStatus(0);
            }
            if (!setPlusPictureEffect(p, value)) {
                super.setValue(PictureEffectController.PICTUREEFFECT, value);
            }
            setPlusParameterSelected(false);
        } else {
            setPlusParameterSelected(true);
            setBackupEffectOptionValue(itemId, value);
            if (!setPlusPictureOptionValue(p, itemId, value)) {
                super.setValue(itemId, value);
            }
        }
        Log.i(TAG, "setValue");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void forceEffectSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String currentEffect = getBackupEffectValue();
        setValue(PICTUREEFFECTPLUS, currentEffect);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void forceEffectOptionSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String currentEffect = getBackupEffectValue();
        if (currentEffect.equals(MODE_PART_COLOR_PLUS)) {
            setValue(MODE_PART_COLOR_CH1, getBackupEffectOptionValue(MODE_PART_COLOR_CH1));
            setValue(MODE_PART_COLOR_CH0, getBackupEffectOptionValue(MODE_PART_COLOR_CH0));
        } else if (currentEffect.equals(MODE_MINIATURE_PLUS)) {
            setValue(MODE_MINIATURE_AREA, getBackupEffectOptionValue(MODE_MINIATURE_AREA));
            setValue(MODE_MINIATURE_EFFECT, getBackupEffectOptionValue(MODE_MINIATURE_EFFECT));
        } else if (currentEffect.equals(MODE_TOY_CAMERA_PLUS)) {
            setValue(MODE_TOY_CAMERA_DARKNESS, getBackupEffectOptionValue(MODE_TOY_CAMERA_DARKNESS));
            setValue(MODE_TOY_CAMERA_COLOR, getBackupEffectOptionValue(MODE_TOY_CAMERA_COLOR));
        } else if (currentEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY)) {
            setValue(PictureEffectController.MODE_SOFT_HIGH_KEY, getBackupEffectOptionValue(PictureEffectController.MODE_SOFT_HIGH_KEY));
        } else if (currentEffect.equals("illust")) {
            setValue("illust", getBackupEffectOptionValue("illust"));
        } else if (currentEffect.equals(PictureEffectController.MODE_POSTERIZATION)) {
            setValue(PictureEffectController.MODE_POSTERIZATION, getBackupEffectOptionValue(PictureEffectController.MODE_POSTERIZATION));
        } else if (currentEffect.equals(PictureEffectController.MODE_HDR_ART)) {
            setValue(PictureEffectController.MODE_HDR_ART, getBackupEffectOptionValue(PictureEffectController.MODE_HDR_ART));
        } else if (currentEffect.equals(PictureEffectController.MODE_SOFT_FOCUS)) {
            setValue(PictureEffectController.MODE_SOFT_FOCUS, getBackupEffectOptionValue(PictureEffectController.MODE_SOFT_FOCUS));
        }
        Log.i(TAG, "forceEffectOptionSetting");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBackupEffectValue(String itemId, String value) {
        Log.i(TAG, "setBackupEffectValue itemId:" + itemId + " value:" + value);
        if (itemId.equals(PICTUREEFFECTPLUS)) {
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_CURRENT_EFFECT, value);
        }
    }

    private void setBackupEffectOptionValue(String itemId, String value) {
        if (itemId.equals(MODE_PART_COLOR_CH0)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_COLOR, value);
        } else if (itemId.equals(MODE_PART_COLOR_CH1)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_COLOR, value);
        } else if (itemId.equals(MODE_MINIATURE_AREA)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_AREA, value);
        } else if (itemId.equals(MODE_MINIATURE_EFFECT)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT, value);
        } else if (itemId.equals(MODE_TOY_CAMERA_COLOR)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_TOYCAMERA_COLOR, value);
        } else if (itemId.equals(MODE_TOY_CAMERA_DARKNESS)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_TOYCAMERA_DARKNESS_LEVEL, value);
        } else if (itemId.equals(PictureEffectController.MODE_SOFT_HIGH_KEY)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_SOFT_HIGH_KEY_COLOR, value);
        } else if (itemId.equals("illust")) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_ILLUST_LEVEL, value);
        } else if (itemId.equals(PictureEffectController.MODE_POSTERIZATION)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_POSTER_COLOR, value);
        } else if (itemId.equals(PictureEffectController.MODE_HDR_ART)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_HDR_LEVEL, value);
        } else if (itemId.equals(PictureEffectController.MODE_SOFT_FOCUS)) {
            mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_SOFT_FOCUS_LEVEL, value);
        }
        Log.i(TAG, "setBackupEffectOptionValue itemId:" + itemId + " value:" + value);
    }

    public void checkPartColorSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String current_effect = getBackupEffectValue();
        if (current_effect.equals(MODE_PART_COLOR_PLUS)) {
            String current_effect_option_ch0 = getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
            String current_effect_option_ch1 = getBackupEffectOptionValue(MODE_PART_COLOR_CH1);
            if (current_effect_option_ch0.equals(PART_COLOR_CUSTOM_SET_CH0)) {
                setValue(MODE_PART_COLOR_CH0, PART_COLOR_CUSTOM_CH0);
            }
            if (current_effect_option_ch1.equals(PART_COLOR_CUSTOM_SET)) {
                setValue(MODE_PART_COLOR_CH1, PART_COLOR_CUSTOM);
            }
        }
        Log.i(TAG, "checkPartColorSettings");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getBackupEffectValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "getBackupEffectValue");
        String effect = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_CURRENT_EFFECT, MODE_PART_COLOR_PLUS);
        Log.i(TAG, "Backup effect:" + effect);
        AppLog.exit(TAG, AppLog.getMethodName());
        return effect;
    }

    public String getBackupEffectOptionValue(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.category == 2) {
            R_PHASE = 90;
            R_RANGE = 33;
            R_SAT = 25;
            G_PHASE = 230;
        }
        Log.i(TAG, "getBackupEffectOptionValue itemId:" + itemId);
        if (itemId.equals(MODE_PART_COLOR_CH0)) {
            ch_color_str[0] = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_COLOR, PART_COLOR_RED_CH0);
            ch0_1_Color[0].Saturation = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_SATU, R_SAT);
            ch0_1_Color[0].Range = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_RANGE, R_RANGE);
            ch0_1_Color[0].Phase = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_PHASE, R_PHASE);
            ch0_1_Color[0].Y = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_Y, 0);
            ch0_1_Color[0].Cb = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CB, 0);
            ch0_1_Color[0].Cr = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CR, 0);
            ch0_1_CustomColor[0].Saturation = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_SATU, R_SAT);
            ch0_1_CustomColor[0].Range = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_RANGE, R_RANGE);
            ch0_1_CustomColor[0].Phase = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_PHASE, R_PHASE);
            ch0_1_CustomColor[0].Y = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_Y, 0);
            ch0_1_CustomColor[0].Cb = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CB, 0);
            ch0_1_CustomColor[0].Cr = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CR, 0);
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_COLOR, PART_COLOR_RED_CH0);
        }
        if (itemId.equals(MODE_PART_COLOR_CH1)) {
            ch_color_str[1] = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_COLOR, PART_COLOR_NON_SET);
            ch0_1_Color[1].Saturation = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_SATU, 0);
            ch0_1_Color[1].Range = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_RANGE, 0);
            ch0_1_Color[1].Phase = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_PHASE, 0);
            ch0_1_Color[1].Y = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_Y, 0);
            ch0_1_Color[1].Cb = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CB, 0);
            ch0_1_Color[1].Cr = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CR, 0);
            ch0_1_CustomColor[1].Saturation = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_SATU, R_SAT);
            ch0_1_CustomColor[1].Range = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_RANGE, R_RANGE);
            ch0_1_CustomColor[1].Phase = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_PHASE, R_PHASE);
            ch0_1_CustomColor[1].Y = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_Y, 0);
            ch0_1_CustomColor[1].Cb = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CB, 0);
            ch0_1_CustomColor[1].Cr = mBackupUtil.getPreferenceInt(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CR, 0);
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_COLOR, PART_COLOR_NON_SET);
        }
        if (itemId.equals(MODE_MINIATURE_AREA)) {
            setMiniatureFocusArea(mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_AREA, PictureEffectController.MINIATURE_HCENTER));
            return getMiniatureFocusArea();
        }
        if (itemId.equals(MODE_MINIATURE_EFFECT)) {
            setMiniCombEffect(mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT, MINIATURE_NORMAL));
            setMiniatureEffectBeforeAuto(mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT_BEFORE_AUTO, MINIATURE_NORMAL));
            return getMiniCombEffect();
        }
        if (itemId.equals(MODE_TOY_CAMERA_COLOR)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_TOYCAMERA_COLOR, TOY_CAMERA_NORMAL);
        }
        if (itemId.equals(MODE_TOY_CAMERA_DARKNESS)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_TOYCAMERA_DARKNESS_LEVEL, TOY_CAMERA_MID);
        }
        if (itemId.equals(PictureEffectController.MODE_SOFT_HIGH_KEY)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_SOFT_HIGH_KEY_COLOR, SOFT_HIGH_KEY_BLUE);
        }
        if (itemId.equals("illust")) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_ILLUST_LEVEL, ILLUST_MID);
        }
        if (itemId.equals(PictureEffectController.MODE_POSTERIZATION)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_POSTER_COLOR, PictureEffectController.POSTERIZATION_COLOR);
        }
        if (itemId.equals(PictureEffectController.MODE_HDR_ART)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_HDR_LEVEL, PictureEffectController.HDR_ART_MEDIUM);
        }
        if (itemId.equals(PictureEffectController.MODE_SOFT_FOCUS)) {
            return mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_SOFT_FOCUS_LEVEL, PictureEffectController.SOFT_FOCUS_MEDIUM);
        }
        return NO_OPTION_VALUE;
    }

    private void resetPictureEffectSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!mMode.equals("off")) {
            int[] reset = new int[0];
            mMode = "off";
            ((CameraEx.ParametersModifier) p.second).setColorSelectMode(mMode, reset);
            this.mCamSet.setParameters(p);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        Log.i(TAG, "resetPictureEffectSetting");
    }

    private boolean setPlusPictureEffect(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ret = true;
        resetPictureEffectSetting(p);
        Log.i(TAG, "setPlusPictureEffect value:" + value);
        if (MODE_PART_COLOR_PLUS.equals(value) || MODE_PART_COLOR_CH0.equals(value) || MODE_PART_COLOR_CH1.equals(value)) {
            setMiniatureStatus(0);
            setInitialcolors(p);
            STRBUILD.replace(0, STRBUILD.length(), SET_PICTURE_EFFECT_PART_COLOR_CH01).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_TOY_CAMERA_PLUS.equals(value) || MODE_TOY_CAMERA_COLOR.equals(value) || MODE_TOY_CAMERA_DARKNESS.equals(value)) {
            setMiniatureStatus(0);
            ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_TOY_CAMERA);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTPLUS).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_MINIATURE_PLUS.equals(value) || MODE_MINIATURE_AREA.equals(value) || MODE_MINIATURE_EFFECT.equals(value)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
            Log.i(TAG, "setValue MINIATURE");
        } else if (MINIATURE_NORMAL.equals(value)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_MINIATURE);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTPLUS).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if (MINIATURE_RETRO.equals(value)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_RETRO_PHOTO);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTPLUS).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if ("cool".equals(value) || "warm".equals(value) || "normal".equals(value) || "green".equals(value) || "magenta".equals(value)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_TOY_CAMERA);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTPLUS).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else {
            setMiniatureStatus(0);
            ret = false;
        }
        if (MODE_MINIATURE_PLUS.equals(value) || MODE_MINIATURE_AREA.equals(value) || MODE_MINIATURE_EFFECT.equals(value)) {
            Log.i(TAG, "not setParameters");
        } else {
            this.mCamSet.setParameters(p);
        }
        if ("watercolor".equals(value)) {
            setMiniatureStatus(0);
            int level = Integer.valueOf(WATER_COLOR_MID.substring(WATER_COLOR_PREFIX.length())).intValue();
            ((CameraEx.ParametersModifier) p.second).setPictureEffectWaterColorEffectLevel(level);
            this.mCamSet.setParameters(p);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTWATERCOLOREFFECTLEVEL).append(level);
            Log.i(TAG, STRBUILD.toString());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    public void setInitialcolors(Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
        ch_color_str[0] = getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
        ch_color_str[1] = getBackupEffectOptionValue(MODE_PART_COLOR_CH1);
        if (PART_COLOR_RED_CH0.equals(ch_color_str[0]) || PART_COLOR_GREEN_CH0.equals(ch_color_str[0]) || PART_COLOR_BLUE_CH0.equals(ch_color_str[0]) || PART_COLOR_YELLOW_CH0.equals(ch_color_str[0])) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_PART_COLOR);
            String colorVal = getColorEffectNameOnPalleteOne(ch_color_str[0]);
            ((CameraEx.ParametersModifier) p.second).setPictureEffectPartColorEffect(colorVal);
        } else {
            PartColorOffSettings();
        }
        if (PART_COLOR_NON_SET_CH0.equals(ch_color_str[0])) {
            ch0 = false;
        } else if (!PART_COLOR_NON_SET_CH0.equals(ch_color_str[0])) {
            ch0 = true;
        }
        if (PART_COLOR_NON_SET.equals(ch_color_str[1])) {
            ch1 = false;
        } else if (!PART_COLOR_NON_SET.equals(ch_color_str[1])) {
            ch1 = true;
        }
        if (ch0 && ch1) {
            mEnabledCh = new int[2];
            mEnabledCh[0] = 0;
            mEnabledCh[1] = 1;
            mMode = "extract";
        } else if (ch0 && !ch1) {
            mEnabledCh = new int[1];
            mEnabledCh[0] = 0;
            mMode = "extract";
        }
        if (!ch0 && ch1) {
            mEnabledCh = new int[1];
            mEnabledCh[0] = 1;
            mMode = "extract";
        } else if (!ch0 && !ch1) {
            mEnabledCh = new int[0];
            ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
            mMode = "off";
        }
        ((CameraEx.ParametersModifier) p.second).setColorSelectMode(mMode, mEnabledCh);
        this.mCamSet.getCamera().setColorSelectToChannel(0, ch0_1_Color[0]);
        this.mCamSet.getCamera().setColorSelectToChannel(1, ch0_1_Color[1]);
        AppLog.exit(TAG, AppLog.getMethodName());
        Log.i(TAG, "setInitialcolors");
    }

    private String getColorEffectNameOnPalleteOne(String effect) {
        if (effect.equals(PART_COLOR_RED_CH0)) {
            return PictureEffectController.PART_COLOR_RED;
        }
        if (effect.equals(PART_COLOR_GREEN_CH0)) {
            return "green";
        }
        if (effect.equals(PART_COLOR_BLUE_CH0)) {
            return PictureEffectController.PART_COLOR_BLUE;
        }
        if (!effect.equals(PART_COLOR_YELLOW_CH0)) {
            return null;
        }
        return PictureEffectController.PART_COLOR_YELLOW;
    }

    public void setMiniatureOnlySetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        setPlusPictureEffect(p, MINIATURE_NORMAL);
        String miniArea = getMiniatureFocusArea();
        ((CameraEx.ParametersModifier) p.second).setPictureEffectMiniatureFocusArea(miniArea);
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTMINIATUREFOCUSAREA).append(miniArea);
        this.mCamSet.setParameters(p);
        Log.i(TAG, "setMiniatureOnlySetting");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setMiniatureCombSetting(String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        setPlusPictureEffect(p, value);
        this.mCamSet.setParameters(p);
        Log.i(TAG, "setMiniatureCombSetting :" + value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean setPlusPictureOptionValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String itemId, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ret = true;
        setToyStatus(0);
        Log.i(TAG, "setPlusPictureOptionValue itemId:" + itemId + " value:" + value);
        if ("illust".equals(itemId)) {
            setMiniatureStatus(0);
            String level = value.substring(ILLUST_PREFIX.length());
            ((CameraEx.ParametersModifier) p.second).setPictureEffectIllustEffectLevel(Integer.valueOf(level).intValue());
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTILLUSTEFFECTLEVEL).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if (PictureEffectController.MODE_SOFT_HIGH_KEY.equals(itemId)) {
            setMiniatureStatus(0);
            String softHighColorValue = getSoftHighKeyColor(value);
            ((CameraEx.ParametersModifier) p.second).setPictureEffectSoftHightKeyEffect(softHighColorValue);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTSOFTHIGHKEYEFFECT).append(value);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_TOY_CAMERA_COLOR.equals(itemId) || MODE_TOY_CAMERA_DARKNESS.equals(itemId)) {
            setMiniatureStatus(0);
            setToyStatus(1);
            if (MODE_TOY_CAMERA_DARKNESS.equals(itemId)) {
                String level2 = value.substring(TOY_CAMERA_PREFIX.length());
                int darkness = Integer.valueOf(level2).intValue();
                ((CameraEx.ParametersModifier) p.second).setToyCameraTuning(darkness);
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETTOYCAMERATUNING).append(value);
                Log.i(TAG, STRBUILD.toString());
            } else if (MODE_TOY_CAMERA_COLOR.equals(itemId)) {
                String toyValue = getToyCameraColorValue(value);
                ((CameraEx.ParametersModifier) p.second).setPictureEffectToyCameraEffect(toyValue);
                mBackupUtil.setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_TOYCAMERA_COLOR, value);
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTTOYCAMERAEFFECT).append(value);
                Log.i(TAG, STRBUILD.toString());
            }
        } else if (MODE_MINIATURE_AREA.equals(itemId) || MODE_MINIATURE_EFFECT.equals(itemId)) {
            if (getMiniCombEffect().equals(MINIATURE_NORMAL)) {
                setMiniatureStatus(1);
            }
            if (MODE_MINIATURE_AREA.equals(itemId)) {
                if (value.equals("auto")) {
                    if (!getMiniCombEffect().equals(MINIATURE_NORMAL)) {
                        setMiniCombEffect(MINIATURE_NORMAL);
                        setMiniatureStatus(1);
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_MINIATURE);
                        setBackupEffectOptionValue(MODE_MINIATURE_EFFECT, MINIATURE_NORMAL);
                    }
                } else {
                    String effect_option_before_auto = getMiniatureEffectBeforeAuto();
                    setMiniCombEffect(effect_option_before_auto);
                    setBackupEffectOptionValue(MODE_MINIATURE_EFFECT, effect_option_before_auto);
                    if (effect_option_before_auto.equals(MINIATURE_RETRO)) {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_RETRO_PHOTO);
                        setMiniatureStatus(2);
                    } else if (effect_option_before_auto.equals("normal") || effect_option_before_auto.equals("cool") || effect_option_before_auto.equals("warm") || effect_option_before_auto.equals("green") || effect_option_before_auto.equals("magenta")) {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_TOY_CAMERA);
                        ((CameraEx.ParametersModifier) p.second).setPictureEffectToyCameraEffect(effect_option_before_auto);
                        ((CameraEx.ParametersModifier) p.second).setToyCameraTuning(-16);
                        setMiniatureStatus(2);
                    } else {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_MINIATURE);
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_MINIATURE);
                        setMiniatureStatus(1);
                    }
                }
                setMiniatureFocusArea(value);
            } else if (MODE_MINIATURE_EFFECT.equals(itemId)) {
                Log.i(TAG, "setPlusPictureOptionValue itemId:" + itemId + "value:" + value);
                setMiniCombEffect(value);
                if (!getMiniatureFocusArea().equals("auto")) {
                    setMiniatureEffectBeforeAuto(value);
                    setBackupEffectOptionValue(itemId, value);
                    setMiniatureStatus(2);
                } else {
                    value = MINIATURE_NORMAL;
                    setMiniatureStatus(1);
                }
                setMiniatureCombSetting(value);
                if (value.equals(MINIATURE_NORMAL)) {
                    setMiniatureStatus(1);
                    Log.i(TAG, "MINIATURE NORMAL");
                } else if (value.equals(MINIATURE_RETRO)) {
                    setMiniatureStatus(2);
                    Log.i(TAG, "MINIATURE RETRO");
                } else if (value.equals("normal") || value.equals("cool") || value.equals("warm") || value.equals("green") || value.equals("magenta")) {
                    setMiniatureStatus(2);
                    ((CameraEx.ParametersModifier) p.second).setPictureEffectToyCameraEffect(value);
                    ((CameraEx.ParametersModifier) p.second).setToyCameraTuning(-16);
                    STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTUREEFFECTTOYCAMERAEFFECT).append(value);
                    Log.i(TAG, STRBUILD.toString());
                }
            }
        } else if (MODE_PART_COLOR_CH0.equals(itemId) || MODE_PART_COLOR_CH1.equals(itemId)) {
            Log.i(TAG, "setPlusPictureOptionValue itemId:" + itemId + "value:" + value);
            setMiniatureStatus(0);
            if (MODE_PART_COLOR_CH0.equals(itemId)) {
                mTargetCh = 0;
                Log.i(TAG, "$$$$$$$$$$$$$$$$$$$$$$ ch_color_str[mTargetCh] $$$$$$" + ch_color_str[mTargetCh]);
                if (ch_color_str[mTargetCh].equals(value)) {
                    if (PART_COLOR_NON_SET_CH0.equals(value)) {
                        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_NO);
                    } else {
                        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_YES);
                    }
                } else if (!ch_color_str[mTargetCh].equals(value)) {
                    setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_NO);
                }
                if (PART_COLOR_ADJUST_STATUS_YES != getColorAdjustStatus(mTargetCh) && PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                }
                if (mTargetCh == 0) {
                    if (PART_COLOR_RED_CH0.equals(value) || PART_COLOR_GREEN_CH0.equals(value) || PART_COLOR_BLUE_CH0.equals(value) || PART_COLOR_YELLOW_CH0.equals(value)) {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_PART_COLOR);
                        String colorVal = getColorEffectNameOnPalleteOne(value);
                        ((CameraEx.ParametersModifier) p.second).setPictureEffectPartColorEffect(colorVal);
                    } else {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
                    }
                    this.mCamSet.setParameters(p);
                }
                if (PART_COLOR_NON_SET_CH0.equals(value) && mTargetCh == 0) {
                    ch0 = false;
                } else if (!PART_COLOR_NON_SET_CH0.equals(value) && mTargetCh == 0) {
                    ch0 = true;
                } else if (PART_COLOR_NON_SET.equals(value) && mTargetCh == 1) {
                    ch1 = false;
                } else if (!PART_COLOR_NON_SET.equals(value) && mTargetCh == 1) {
                    ch1 = true;
                }
                if (ch0 && ch1) {
                    mEnabledCh = new int[2];
                    mEnabledCh[0] = 0;
                    mEnabledCh[1] = 1;
                    mMode = "extract";
                } else if (ch0 && !ch1) {
                    mEnabledCh = new int[1];
                    mEnabledCh[0] = 0;
                    mMode = "extract";
                } else if (!ch0 && ch1) {
                    mEnabledCh = new int[1];
                    mEnabledCh[0] = 1;
                    mMode = "extract";
                } else if (!ch0 && !ch1) {
                    mEnabledCh = new int[0];
                    mMode = "off";
                }
                ((CameraEx.ParametersModifier) p.second).setColorSelectMode(mMode, mEnabledCh);
                if (PART_COLOR_RED_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(RedColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_RED_CH0;
                } else if (PART_COLOR_GREEN_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(GreenColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_GREEN_CH0;
                } else if (PART_COLOR_BLUE_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(BlueColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_BLUE_CH0;
                } else if (PART_COLOR_YELLOW_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(YellowColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_YELLOW_CH0;
                } else if (PART_COLOR_CUSTOM_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(ch0_1_CustomColor[mTargetCh], ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_CUSTOM_CH0;
                } else if (PART_COLOR_CUSTOM_SET_CH0.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(ch0_1_CustomColor[mTargetCh], ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_CUSTOM_SET_CH0;
                } else if (PART_COLOR_NON_SET_CH0.equals(value)) {
                    option_param = PART_COLOR_NON_SET_CH0;
                    if (mTargetCh == 0) {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
                    }
                }
            } else if (MODE_PART_COLOR_CH1.equals(itemId)) {
                mTargetCh = 1;
                if (ch_color_str[mTargetCh].equals(value)) {
                    if (PART_COLOR_NON_SET.equals(value)) {
                        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_NO);
                    } else {
                        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_YES);
                    }
                } else if (!ch_color_str[mTargetCh].equals(value)) {
                    setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_NO);
                }
                if (PART_COLOR_ADJUST_STATUS_YES != getColorAdjustStatus(mTargetCh) && PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                }
                if (PART_COLOR_NON_SET_CH0.equals(value) && mTargetCh == 0) {
                    ch0 = false;
                } else if (!PART_COLOR_NON_SET_CH0.equals(value) && mTargetCh == 0) {
                    ch0 = true;
                } else if (PART_COLOR_NON_SET.equals(value) && mTargetCh == 1) {
                    ch1 = false;
                } else if (!PART_COLOR_NON_SET.equals(value) && mTargetCh == 1) {
                    ch1 = true;
                }
                if (ch0 && ch1) {
                    mEnabledCh = new int[2];
                    mEnabledCh[0] = 0;
                    mEnabledCh[1] = 1;
                    mMode = "extract";
                } else if (ch0 && !ch1) {
                    mEnabledCh = new int[1];
                    mEnabledCh[0] = 0;
                    mMode = "extract";
                } else if (!ch0 && ch1) {
                    mEnabledCh = new int[1];
                    mEnabledCh[0] = 1;
                    mMode = "extract";
                } else if (!ch0 && !ch1) {
                    mEnabledCh = new int[0];
                    mMode = "off";
                }
                ((CameraEx.ParametersModifier) p.second).setColorSelectMode(mMode, mEnabledCh);
                if (PictureEffectController.PART_COLOR_RED.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(RedColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PictureEffectController.PART_COLOR_RED;
                } else if (PART_COLOR_GREEN.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(GreenColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_GREEN;
                } else if (PART_COLOR_BLUE.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(BlueColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_BLUE;
                } else if (PictureEffectController.PART_COLOR_YELLOW.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(YellowColor, ch0_1_Color[mTargetCh]);
                    }
                    option_param = PictureEffectController.PART_COLOR_YELLOW;
                } else if (PART_COLOR_CUSTOM.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(ch0_1_CustomColor[mTargetCh], ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_CUSTOM;
                } else if (PART_COLOR_CUSTOM_SET.equals(value)) {
                    if (PART_COLOR_ADJUST_STATUS_NO == getColorAdjustStatus(mTargetCh)) {
                        copySelectedColor(ch0_1_CustomColor[mTargetCh], ch0_1_Color[mTargetCh]);
                    }
                    option_param = PART_COLOR_CUSTOM_SET;
                } else if (PART_COLOR_NON_SET.equals(value)) {
                    option_param = PART_COLOR_NON_SET;
                    if (mTargetCh == 1) {
                        ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
                    }
                }
            }
            if (MODE_PART_COLOR_CH0.equals(itemId)) {
                ch_color_str[0] = option_param;
                this.mCamSet.getCamera().setColorSelectToChannel(0, ch0_1_Color[0]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_COLOR, ch_color_str[0]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_SATU, Integer.valueOf(ch0_1_Color[0].Saturation));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_RANGE, Integer.valueOf(ch0_1_Color[0].Range));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_PHASE, Integer.valueOf(ch0_1_Color[0].Phase));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_Y, Integer.valueOf(ch0_1_Color[0].Y));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CB, Integer.valueOf(ch0_1_Color[0].Cb));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CR, Integer.valueOf(ch0_1_Color[0].Cr));
                CameraNotificationManager.getInstance().requestNotify(NotificationTag.PICTURE_EFFECT_PLUS_CHANGE);
            } else if (MODE_PART_COLOR_CH1.equals(itemId)) {
                ch_color_str[1] = option_param;
                this.mCamSet.getCamera().setColorSelectToChannel(1, ch0_1_Color[1]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_COLOR, ch_color_str[1]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_SATU, Integer.valueOf(ch0_1_Color[1].Saturation));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_RANGE, Integer.valueOf(ch0_1_Color[1].Range));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_PHASE, Integer.valueOf(ch0_1_Color[1].Phase));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_Y, Integer.valueOf(ch0_1_Color[1].Y));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CB, Integer.valueOf(ch0_1_Color[1].Cb));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CR, Integer.valueOf(ch0_1_Color[1].Cr));
                CameraNotificationManager.getInstance().requestNotify(NotificationTag.PICTURE_EFFECT_PLUS_CHANGE);
            }
            PartColorOffSettings();
        } else {
            Log.i(TAG, "setPlusPictureOptionValue non set");
            setMiniatureStatus(0);
            ret = false;
        }
        if (ret) {
            this.mCamSet.setParameters(p);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    private String getSoftHighKeyColor(String value) {
        if (value.equals(SOFT_HIGH_KEY_BLUE)) {
            return PictureEffectController.PART_COLOR_BLUE;
        }
        if (value.equals(SOFT_HIGH_KEY_GREEN)) {
            return "green";
        }
        if (!value.equals(SOFT_HIGH_KEY_PINK)) {
            return "";
        }
        return "pink";
    }

    private String getToyCameraColorValue(String value) {
        if (value.equals(TOY_CAMERA_NORMAL)) {
            return "normal";
        }
        if (value.equals(TOY_CAMERA_COOL)) {
            return "cool";
        }
        if (value.equals(TOY_CAMERA_WARM)) {
            return "warm";
        }
        if (value.equals(TOY_CAMERA_GREEN)) {
            return "green";
        }
        if (!value.equals(TOY_CAMERA_MAGENTA)) {
            return "";
        }
        return "magenta";
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PICTUREEFFECTPLUS.equals(itemId) || PictureEffectController.PICTUREEFFECT.equals(itemId)) {
            ret = getBackupEffectValue();
        } else {
            ret = getBackupEffectOptionValue(itemId);
        }
        Log.i(TAG, "getValue");
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController
    public List<String> createSupportedOptionValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> list = super.createSupportedOptionValueArray(params, tag);
        if ("illust".equals(tag)) {
            int min = ((CameraEx.ParametersModifier) params.second).getMinPictureEffectIllustEffectLevel();
            int max = ((CameraEx.ParametersModifier) params.second).getMaxPictureEffectIllustEffectLevel();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETMINPICTUREEFFECTILLUSTEFFECTLEVEL).append(min).append(", ").append(LOG_MSG_GETMAXPICTUREEFFECTILLUSTEFFECTLEVEL).append(max);
            Log.i(TAG, STRBUILD.toString());
            list = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                list.add(ILLUST_PREFIX + i);
            }
        } else if (PictureEffectController.MODE_SOFT_HIGH_KEY.equals(tag)) {
            list = new ArrayList<>();
            list.add(SOFT_HIGH_KEY_BLUE);
            list.add(SOFT_HIGH_KEY_GREEN);
            list.add(SOFT_HIGH_KEY_PINK);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTSOFTHIGHKEYEFFECTS).append(list);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_TOY_CAMERA_DARKNESS.equals(tag)) {
            int min2 = ((CameraEx.ParametersModifier) params.second).getMinToyCameraTuning();
            int max2 = ((CameraEx.ParametersModifier) params.second).getMaxToyCameraTuning();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETMINTOYCAMERATUNING).append(min2).append(", ").append(LOG_MSG_GETMAXTOYCAMERATUNING).append(max2);
            Log.i(TAG, STRBUILD.toString());
            list = new ArrayList<>();
            for (int i2 = min2; i2 <= max2; i2++) {
                list.add(TOY_CAMERA_PREFIX + i2);
            }
        } else if (MODE_TOY_CAMERA_COLOR.equals(tag)) {
            list = new ArrayList<>();
            list.add(TOY_CAMERA_NORMAL);
            list.add(TOY_CAMERA_COOL);
            list.add(TOY_CAMERA_WARM);
            list.add(TOY_CAMERA_GREEN);
            list.add(TOY_CAMERA_MAGENTA);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTTOYCAMERAEFFECTS).append(list);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_MINIATURE_AREA.equals(tag)) {
            list = ((CameraEx.ParametersModifier) params.second).getSupportedPictureEffectMiniatureFocusAreas();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTMINIATUREFOCUSAREAS).append(list);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_MINIATURE_EFFECT.equals(tag)) {
            list = SUPPORTED_MINI_COMB_EFFEECT;
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SUPPORTED_MINI_COMB_EFFEECT).append(list);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_PART_COLOR_CH0.equals(tag)) {
            list = new ArrayList<>();
            list.add(PART_COLOR_RED_CH0);
            list.add(PART_COLOR_GREEN_CH0);
            list.add(PART_COLOR_BLUE_CH0);
            list.add(PART_COLOR_YELLOW_CH0);
            list.add(PART_COLOR_CUSTOM_CH0);
            list.add(PART_COLOR_CUSTOM_SET_CH0);
            list.add(PART_COLOR_NON_SET_CH0);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS_CH0).append(list);
            Log.i(TAG, STRBUILD.toString());
        } else if (MODE_PART_COLOR_CH1.equals(tag)) {
            list = new ArrayList<>();
            list.add(PictureEffectController.PART_COLOR_RED);
            list.add(PART_COLOR_GREEN);
            list.add(PART_COLOR_BLUE);
            list.add(PictureEffectController.PART_COLOR_YELLOW);
            list.add(PART_COLOR_CUSTOM);
            list.add(PART_COLOR_CUSTOM_SET);
            list.add(PART_COLOR_NON_SET);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS_CH1).append(list);
            Log.i(TAG, STRBUILD.toString());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController
    public List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ArrayList<String> list = (ArrayList) super.createSupportedValueArray(params);
        list.add(MODE_PART_COLOR_PLUS);
        list.add(MODE_PART_COLOR_CH0);
        list.add(MODE_PART_COLOR_CH1);
        if (list.contains(PictureEffectController.MODE_TOY_CAMERA)) {
            list.remove(PictureEffectController.MODE_TOY_CAMERA);
        }
        list.add(MODE_TOY_CAMERA_PLUS);
        list.add(MODE_TOY_CAMERA_DARKNESS);
        list.add(MODE_TOY_CAMERA_COLOR);
        if (list.contains(PictureEffectController.MODE_MINIATURE)) {
            list.remove(PictureEffectController.MODE_MINIATURE);
        }
        list.add(MODE_MINIATURE_PLUS);
        list.add(MODE_MINIATURE_AREA);
        list.add(MODE_MINIATURE_EFFECT);
        Log.i(TAG, "createSupportedValueArray list:" + list.toString());
        AppLog.exit(TAG, AppLog.getMethodName());
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController
    public void setItemNeedsOptionArray() {
        super.setItemNeedsOptionArray();
        AppLog.enter(TAG, AppLog.getMethodName());
        this.ITEM_ID_NEEDS_OPTION.add("illust");
        this.ITEM_ID_NEEDS_OPTION.add(PictureEffectController.MODE_SOFT_HIGH_KEY);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_PART_COLOR_CH0);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_PART_COLOR_CH1);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_TOY_CAMERA_DARKNESS);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_TOY_CAMERA_COLOR);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_MINIATURE_AREA);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_MINIATURE_EFFECT);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController
    public void setOptionApiNameDirectory() {
        super.setOptionApiNameDirectory();
        AppLog.enter(TAG, AppLog.getMethodName());
        this.OPTION_API_NAME_DICTIONARY.put("illust", "setPictureEffectIllustEffectLevel");
        this.OPTION_API_NAME_DICTIONARY.put(PictureEffectController.MODE_SOFT_HIGH_KEY, "setPictureEffectSoftHightKeyEffect");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_PART_COLOR_CH0, "setColorSelectToChannel");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_PART_COLOR_CH1, "setColorSelectToChannel");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_TOY_CAMERA_DARKNESS, "setToyCameraTuning");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_TOY_CAMERA_COLOR, "setPictureEffectToyCameraEffect");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_MINIATURE_AREA, "setPictureEffectMiniatureFocusArea");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_MINIATURE_EFFECT, "setPictureEffectToyCameraEffect");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> list;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PICTUREEFFECTPLUS.equals(tag)) {
            list = super.getAvailableValue(PictureEffectController.PICTUREEFFECT);
        } else {
            list = super.getAvailableValue(tag);
        }
        if (getMiniatureFocusArea().equals("auto") && tag.equals(MODE_MINIATURE_EFFECT)) {
            list = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        boolean isAvailable = super.isAvailable(tag);
        if (getMiniatureFocusArea().equals("auto") && tag.equals(MODE_MINIATURE_EFFECT)) {
            return false;
        }
        return isAvailable;
    }

    public void setChangedSaturationValue(int level, int STEP) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (adjustedColor.Saturation > 64) {
        }
        int saturation = 64 - ((64 / STEP) * level);
        if (saturation < 0) {
            saturation = 0;
        } else if (saturation > G_RANGE) {
            saturation = 64 - 1;
        }
        adjustedColor.Saturation = saturation;
        Log.d(TAG, "Testit:  @@@@@@@@@@adjustedColor.Saturation: " + adjustedColor.Saturation);
        this.mCamSet.getCamera().setColorSelectToChannel(mTargetCh, adjustedColor);
        Log.i(TAG, "setChangedSaturationValue saturation:" + saturation);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getCurrentSaturation(int STEP) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int saturation = adjustedColor.Saturation;
        if (saturation < 1) {
            saturation = 0;
        } else if (saturation > G_RANGE) {
            saturation = 64 - 1;
        }
        Log.i(TAG, "getCurrentSaturation saturation:" + saturation);
        AppLog.exit(TAG, AppLog.getMethodName());
        return (64 - saturation) / (64 / STEP);
    }

    public void setOriginalColor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_NO);
        changeEEDoublePartColor();
        this.mCamSet.getCamera().setColorSelectToChannel(mTargetCh, ch0_1_Color[mTargetCh]);
        Log.i(TAG, "setOriginalColor");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setChangedColor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setColorAdjustStatus(mTargetCh, PART_COLOR_ADJUST_STATUS_YES);
        changeEEDoublePartColor();
        copySelectedColor(adjustedColor, ch0_1_Color[mTargetCh]);
        if (mTargetCh == 0) {
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_SATU, Integer.valueOf(ch0_1_Color[0].Saturation));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_RANGE, Integer.valueOf(ch0_1_Color[0].Range));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_PHASE, Integer.valueOf(ch0_1_Color[0].Phase));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_Y, Integer.valueOf(ch0_1_Color[0].Y));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CB, Integer.valueOf(ch0_1_Color[0].Cb));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CR, Integer.valueOf(ch0_1_Color[0].Cr));
            if (PART_COLOR_CUSTOM_SET_CH0.equals(option_param) || PART_COLOR_CUSTOM_CH0.equals(option_param)) {
                copySelectedColor(adjustedColor, ch0_1_CustomColor[mTargetCh]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_SATU, Integer.valueOf(ch0_1_CustomColor[0].Saturation));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_RANGE, Integer.valueOf(ch0_1_CustomColor[0].Range));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_PHASE, Integer.valueOf(ch0_1_CustomColor[0].Phase));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_Y, Integer.valueOf(ch0_1_CustomColor[0].Y));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CB, Integer.valueOf(ch0_1_CustomColor[0].Cb));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CR, Integer.valueOf(ch0_1_CustomColor[0].Cr));
            }
        } else if (mTargetCh == 1) {
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_SATU, Integer.valueOf(ch0_1_Color[1].Saturation));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_RANGE, Integer.valueOf(ch0_1_Color[1].Range));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_PHASE, Integer.valueOf(ch0_1_Color[1].Phase));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_Y, Integer.valueOf(ch0_1_Color[1].Y));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CB, Integer.valueOf(ch0_1_Color[1].Cb));
            BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CR, Integer.valueOf(ch0_1_Color[1].Cr));
            if (PART_COLOR_CUSTOM_SET.equals(option_param) || PART_COLOR_CUSTOM.equals(option_param)) {
                copySelectedColor(adjustedColor, ch0_1_CustomColor[mTargetCh]);
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_SATU, Integer.valueOf(ch0_1_CustomColor[1].Saturation));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_RANGE, Integer.valueOf(ch0_1_CustomColor[1].Range));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_PHASE, Integer.valueOf(ch0_1_CustomColor[1].Phase));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_Y, Integer.valueOf(ch0_1_CustomColor[1].Y));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CB, Integer.valueOf(ch0_1_CustomColor[1].Cb));
                BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CR, Integer.valueOf(ch0_1_CustomColor[1].Cr));
            }
        }
        Log.i(TAG, "setChangedColor");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void copySelectedColor(CameraEx.SelectedColor src, CameraEx.SelectedColor dst) {
        AppLog.enter(TAG, AppLog.getMethodName());
        dst.Y = src.Y;
        dst.Cb = src.Cb;
        dst.Cr = src.Cr;
        dst.Phase = src.Phase;
        dst.Range = src.Range;
        dst.Saturation = src.Saturation;
        Log.i(TAG, "copySelectedColor");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void changeTargetChannelOnlyPartColor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int[] hoge = new int[1];
        if (mTargetCh == 0) {
            hoge[0] = 0;
        } else if (mTargetCh == 1) {
            hoge[0] = 1;
        }
        Log.i(TAG, "changeTargetChannelOnlyPartColor hoge:" + hoge.toString());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setColorSelectMode("extract", hoge);
        this.mCamSet.setParameters(p);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void changeEEDoublePartColor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setColorSelectMode("extract", mEnabledCh);
        this.mCamSet.setParameters(p);
        Log.i(TAG, "changeEEDoublePartColor");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public CameraEx.SelectedColor captureColorFromEE(int x, int y) {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraEx.SelectedColor tempCapturedColor = this.mCamSet.getCamera().getPreviewDisplayColor(x, y);
        if (tempCapturedColor != null) {
            copySelectedColor(tempCapturedColor, capturedColor);
            if (capturedColor.Saturation < 1) {
                capturedColor.Saturation = 1;
            } else if (capturedColor.Saturation > G_RANGE) {
                capturedColor.Saturation = G_RANGE;
            }
            Log.i(TAG, "captureColorFromEE x:" + x + "y:" + y + "capturedColor Cb:" + capturedColor.Cb + "Cr:" + capturedColor.Cr + "phase:" + capturedColor.Phase + "Range:" + capturedColor.Range + Keys.KEY_SATURATION + capturedColor.Saturation + "Y:" + capturedColor.Y);
        } else {
            Log.i(TAG, "captureColorFromEE x:" + x + "y:" + y + "captureColorFromEE:null");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return tempCapturedColor;
    }

    public void startOVFpartColorCondition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> param = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) param.second).setPictureEffect("off");
        this.mCamSet.setParameters(param);
        int[] hoge = new int[0];
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setColorSelectMode("off", hoge);
        ((CameraEx.ParametersModifier) p.second).setOVFPreviewMode(true);
        ((CameraEx.ParametersModifier) p.second).setShootingPreviewMode("iris_ss_iso_aeunlock");
        this.mCamSet.setParameters(p);
        DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
        CreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, "standard");
        Log.i(TAG, "startOVFpartColorCondition previewMode_settting:" + this.previewMode_settting + "ovf_setting:" + this.ovf_setting);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void endOVFpartColorCondition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setOVFPreviewMode(this.ovf_setting);
        ((CameraEx.ParametersModifier) p.second).setShootingPreviewMode(this.previewMode_settting);
        this.mCamSet.setParameters(p);
        Log.i(TAG, "endOVFpartColorCondition");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setCh0ExifInfo() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        String value = getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
        if (PART_COLOR_RED_CH0.equals(value) || PART_COLOR_GREEN_CH0.equals(value) || PART_COLOR_BLUE_CH0.equals(value) || PART_COLOR_YELLOW_CH0.equals(value)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect(PictureEffectController.MODE_PART_COLOR);
            String colVal = getColorEffectNameOnPalleteOne(value);
            ((CameraEx.ParametersModifier) p.second).setPictureEffectPartColorEffect(colVal);
        } else {
            ((CameraEx.ParametersModifier) p.second).setPictureEffect("off");
        }
        this.mCamSet.setParameters(p);
        Log.i(TAG, "setCh0ExifInfo");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setCustomSet2CustomOnBackupOptionValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PART_COLOR_CUSTOM_SET_CH0.equals(getBackupEffectOptionValue(MODE_PART_COLOR_CH0))) {
            setBackupEffectOptionValue(MODE_PART_COLOR_CH0, PART_COLOR_CUSTOM_CH0);
        }
        if (PART_COLOR_CUSTOM_SET.equals(getBackupEffectOptionValue(MODE_PART_COLOR_CH1))) {
            setBackupEffectOptionValue(MODE_PART_COLOR_CH1, PART_COLOR_CUSTOM);
        }
        Log.i(TAG, "setCustomSet2CustomOnBackupOptionValue");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setCapturedColorToEE() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCamSet.getCamera().setColorSelectToChannel(mTargetCh, capturedColor);
        Log.i(TAG, "setCapturedColorToEE");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void copyTargetColor2AdjustColor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (COLOR_CAPTURE_STATUS_NO == getColorCaptureStatus()) {
            Log.d(TAG, "Testit:  ********** Target plate no: " + mTargetCh);
            Log.d(TAG, "Testit:   ********** source ch0_1_Color[mTargetCh].Saturation: " + ch0_1_Color[mTargetCh].Saturation);
            Log.d(TAG, "Testit:   ********** before copy adjustedColor.Saturation: " + adjustedColor.Saturation);
            copySelectedColor(ch0_1_Color[mTargetCh], adjustedColor);
            Log.d(TAG, "Testit:  ********** source ch0_1_Color[mTargetCh].Saturation: " + ch0_1_Color[mTargetCh].Saturation);
            Log.d(TAG, "Testit:  **********  after copy adjustedColor.Saturation: " + adjustedColor.Saturation);
        } else if (COLOR_CAPTURE_STATUS_YES == getColorCaptureStatus()) {
            copySelectedColor(capturedColor, adjustedColor);
        }
        setColorCaptureStatus(COLOR_CAPTURE_STATUS_NO);
        Log.i(TAG, "copyTargetColor2AdjustColor");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isBackToThirdMenu() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isBack = false;
        String effect = getValue(PICTUREEFFECTPLUS);
        if (ITEM_ID_BACK_TO_THIRD_MENU.contains(effect)) {
            isBack = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isBack;
    }

    public void onTerminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (getBackupEffectValue().equals(MODE_PART_COLOR_PLUS)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
            mEnabledCh = new int[0];
            mMode = "off";
            ((CameraEx.ParametersModifier) p.second).setColorSelectMode(mMode, mEnabledCh);
            this.mCamSet.setParameters(p);
        }
        Log.i(TAG, "onTerminate");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PICTUREEFFECTPLUS.equals(tag)) {
            AppLog.exit(TAG, AppLog.getMethodName());
            return super.isUnavailableSceneFactor(PictureEffectController.PICTUREEFFECT);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.isUnavailableSceneFactor(tag);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PICTUREEFFECTPLUS.equals(tag)) {
            AppLog.exit(TAG, AppLog.getMethodName());
            return super.getSupportedValue(PictureEffectController.PICTUREEFFECT);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.getSupportedValue(tag);
    }

    private void PartColorOffSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setDroHdrMode2OFF();
        setCreativeStyle2std();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setDroHdrMode2OFF() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String droModeLevel = DROAutoHDRController.getInstance().getValue();
        if (!droModeLevel.equals("off")) {
            String backup_effect = getInstance().getBackupEffectValue();
            if (backup_effect.equals(MODE_PART_COLOR_PLUS)) {
                String backup_effect_option_ch0 = getInstance().getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
                String backup_effect_option_ch1 = getInstance().getBackupEffectOptionValue(MODE_PART_COLOR_CH1);
                if (backup_effect_option_ch0.equals(PART_COLOR_NON_SET_CH0) || backup_effect_option_ch0.equals(PART_COLOR_CUSTOM_CH0) || backup_effect_option_ch0.equals(PART_COLOR_CUSTOM_SET_CH0) || backup_effect_option_ch1.equals(PART_COLOR_NON_SET) || backup_effect_option_ch1.equals(PART_COLOR_CUSTOM) || backup_effect_option_ch1.equals(PART_COLOR_CUSTOM_SET)) {
                    DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
                }
            }
        }
        Log.i(TAG, "setDroHdrMode2OFF");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setCreativeStyle2std() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String creativeStyle = CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE);
        if (!creativeStyle.equals("standard")) {
            String backup_effect = getInstance().getBackupEffectValue();
            if (backup_effect.equals(MODE_PART_COLOR_PLUS)) {
                String backup_effect_option_ch0 = getInstance().getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
                String backup_effect_option_ch1 = getInstance().getBackupEffectOptionValue(MODE_PART_COLOR_CH1);
                if (backup_effect_option_ch0.equals(PART_COLOR_NON_SET_CH0) || backup_effect_option_ch0.equals(PART_COLOR_CUSTOM_CH0) || backup_effect_option_ch0.equals(PART_COLOR_CUSTOM_SET_CH0) || backup_effect_option_ch1.equals(PART_COLOR_NON_SET) || backup_effect_option_ch1.equals(PART_COLOR_CUSTOM) || backup_effect_option_ch1.equals(PART_COLOR_CUSTOM_SET)) {
                    CreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, "standard");
                }
            }
        }
        Log.i(TAG, "setCreativeStyle2std");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void keepCurrentEffectOptionSetting(String layout) {
        AppLog.enter(TAG, AppLog.getMethodName());
        setCallLayout(layout);
        this.currentEffect = getBackupEffectValue();
        if (this.currentEffect.equals(MODE_PART_COLOR_PLUS)) {
            copySelectedColor(ch0_1_Color[0], this.previous_ch0_1_Color[0]);
            copySelectedColor(ch0_1_Color[1], this.previous_ch0_1_Color[1]);
            copySelectedColor(ch0_1_CustomColor[0], this.previous_custom_ch0_1_Color[0]);
            copySelectedColor(ch0_1_CustomColor[1], this.previous_custom_ch0_1_Color[1]);
            this.previousColorCh0 = getBackupEffectOptionValue(MODE_PART_COLOR_CH0);
            this.previousColorCh1 = getBackupEffectOptionValue(MODE_PART_COLOR_CH1);
        } else if (this.currentEffect.equals(MODE_MINIATURE_PLUS)) {
            this.previousMiniArea = getBackupEffectOptionValue(MODE_MINIATURE_AREA);
            this.previousMiniColor = getBackupEffectOptionValue(MODE_MINIATURE_EFFECT);
        } else if (this.currentEffect.equals(MODE_TOY_CAMERA_PLUS)) {
            this.previousToyColor = getBackupEffectOptionValue(MODE_TOY_CAMERA_COLOR);
            this.previousToyDarkness = getBackupEffectOptionValue(MODE_TOY_CAMERA_DARKNESS);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY)) {
            this.previousOptionValue = getBackupEffectOptionValue(PictureEffectController.MODE_SOFT_HIGH_KEY);
        } else if (this.currentEffect.equals("illust")) {
            this.previousOptionValue = getBackupEffectOptionValue("illust");
        } else if (this.currentEffect.equals(PictureEffectController.MODE_POSTERIZATION)) {
            this.previousOptionValue = getBackupEffectOptionValue(PictureEffectController.MODE_POSTERIZATION);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_HDR_ART)) {
            this.previousOptionValue = getBackupEffectOptionValue(PictureEffectController.MODE_HDR_ART);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_SOFT_FOCUS)) {
            this.previousOptionValue = getBackupEffectOptionValue(PictureEffectController.MODE_SOFT_FOCUS);
        }
        Log.i(TAG, "keepCurrentEffectOptionSetting");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setPreviousEffectOptionSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setValue(PICTUREEFFECTPLUS, this.currentEffect);
        if (this.currentEffect.equals(MODE_PART_COLOR_PLUS)) {
            setPreviousPartColorSettings();
        } else if (this.currentEffect.equals(MODE_MINIATURE_PLUS)) {
            setValue(MODE_MINIATURE_AREA, this.previousMiniArea);
            setValue(MODE_MINIATURE_EFFECT, this.previousMiniColor);
        } else if (this.currentEffect.equals(MODE_TOY_CAMERA_PLUS)) {
            setValue(MODE_TOY_CAMERA_COLOR, this.previousToyColor);
            setValue(MODE_TOY_CAMERA_DARKNESS, this.previousToyDarkness);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY)) {
            setValue(PictureEffectController.MODE_SOFT_HIGH_KEY, this.previousOptionValue);
        } else if (this.currentEffect.equals("illust")) {
            setValue("illust", this.previousOptionValue);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_POSTERIZATION)) {
            setValue(PictureEffectController.MODE_POSTERIZATION, this.previousOptionValue);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_HDR_ART)) {
            setValue(PictureEffectController.MODE_HDR_ART, this.previousOptionValue);
        } else if (this.currentEffect.equals(PictureEffectController.MODE_SOFT_FOCUS)) {
            setValue(PictureEffectController.MODE_SOFT_FOCUS, this.previousOptionValue);
        }
        Log.i(TAG, "setPreviousEffectOptionSetting");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setPreviousPartColorSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setBackupEffectOptionValue(MODE_PART_COLOR_CH0, this.previousColorCh0);
        setBackupEffectOptionValue(MODE_PART_COLOR_CH1, this.previousColorCh1);
        Log.i(TAG, "previousColorCh0 :" + this.previousColorCh0 + " previousColorCh1:" + this.previousColorCh1);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> prev_param = this.mCamSet.getEmptyParameters();
        if (PictureEffectController.PART_COLOR_RED.equals(this.previousColorCh0) || PART_COLOR_GREEN.equals(this.previousColorCh0) || PART_COLOR_BLUE.equals(this.previousColorCh0) || PictureEffectController.PART_COLOR_YELLOW.equals(this.previousColorCh0)) {
            ((CameraEx.ParametersModifier) prev_param.second).setPictureEffect(PictureEffectController.MODE_PART_COLOR);
            String colVal = getColorEffectNameOnPalleteOne(this.previousColorCh0);
            ((CameraEx.ParametersModifier) prev_param.second).setPictureEffectPartColorEffect(colVal);
        } else {
            ((CameraEx.ParametersModifier) prev_param.second).setPictureEffect("off");
            PartColorOffSettings();
        }
        this.mCamSet.setParameters(prev_param);
        if (PART_COLOR_NON_SET_CH0.equals(this.previousColorCh0)) {
            ch0 = false;
        } else {
            ch0 = true;
        }
        if (PART_COLOR_NON_SET.equals(this.previousColorCh1)) {
            ch1 = false;
        } else {
            ch1 = true;
        }
        if (ch0 && ch1) {
            mEnabledCh = new int[2];
            mEnabledCh[0] = 0;
            mEnabledCh[1] = 1;
            mMode = "extract";
        } else if (ch0 && !ch1) {
            mEnabledCh = new int[1];
            mEnabledCh[0] = 0;
            mMode = "extract";
        } else if (!ch0 && ch1) {
            mEnabledCh = new int[1];
            mEnabledCh[0] = 1;
            mMode = "extract";
        } else if (!ch0 && !ch1) {
            mEnabledCh = new int[0];
            mMode = "off";
        }
        ch_color_str[0] = this.previousColorCh0;
        ch_color_str[1] = this.previousColorCh1;
        Log.e(TAG, "Inside the PartColorSetting method :::: ch_color_str[0] " + ch_color_str[0] + "  ch_color_str[1] ::::" + ch_color_str[1]);
        copySelectedColor(this.previous_ch0_1_Color[0], ch0_1_Color[0]);
        copySelectedColor(this.previous_ch0_1_Color[1], ch0_1_Color[1]);
        copySelectedColor(this.previous_custom_ch0_1_Color[0], ch0_1_CustomColor[0]);
        copySelectedColor(this.previous_custom_ch0_1_Color[1], ch0_1_CustomColor[1]);
        ((CameraEx.ParametersModifier) prev_param.second).setColorSelectMode(mMode, mEnabledCh);
        this.mCamSet.getCamera().setColorSelectToChannel(0, this.previous_ch0_1_Color[0]);
        this.mCamSet.getCamera().setColorSelectToChannel(1, this.previous_ch0_1_Color[1]);
        this.mCamSet.setParameters(prev_param);
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_COLOR, ch_color_str[0]);
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_SATU, Integer.valueOf(this.previous_ch0_1_Color[0].Saturation));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_RANGE, Integer.valueOf(this.previous_ch0_1_Color[0].Range));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_PHASE, Integer.valueOf(this.previous_ch0_1_Color[0].Phase));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_Y, Integer.valueOf(this.previous_ch0_1_Color[0].Y));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CB, Integer.valueOf(this.previous_ch0_1_Color[0].Cb));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH0_CR, Integer.valueOf(this.previous_ch0_1_Color[0].Cr));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_COLOR, ch_color_str[1]);
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_SATU, Integer.valueOf(this.previous_ch0_1_Color[1].Saturation));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_RANGE, Integer.valueOf(this.previous_ch0_1_Color[1].Range));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_PHASE, Integer.valueOf(this.previous_ch0_1_Color[1].Phase));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_Y, Integer.valueOf(this.previous_ch0_1_Color[1].Y));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CB, Integer.valueOf(this.previous_ch0_1_Color[1].Cb));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CH1_CR, Integer.valueOf(this.previous_ch0_1_Color[1].Cr));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_SATU, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Saturation));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_RANGE, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Range));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_PHASE, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Phase));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_Y, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Y));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CB, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Cb));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH0_CR, Integer.valueOf(this.previous_custom_ch0_1_Color[0].Cr));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_SATU, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Saturation));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_RANGE, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Range));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_PHASE, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Phase));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_Y, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Y));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CB, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Cb));
        BackUpUtil.getInstance().setPreference(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_PART_COLOR_CUSTOM_CH1_CR, Integer.valueOf(this.previous_custom_ch0_1_Color[1].Cr));
        CameraNotificationManager.getInstance().requestNotify(NotificationTag.PICTURE_EFFECT_PLUS_CHANGE);
        Log.i(TAG, "setPreviousPartColorSettings");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setCallLayout(String layout) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "setCallLayout callLayout:" + layout);
        this.callLayout = layout;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getCallLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "getCallLayout callLayout:" + this.callLayout);
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.callLayout;
    }

    public boolean isComingFromApplicationSettings() {
        return this.isComingFromApplicationSettings;
    }

    public void setComingFromApplicationSettings(boolean isComingFromApplicationSettings) {
        this.isComingFromApplicationSettings = isComingFromApplicationSettings;
    }

    public String getPreviousOptionValue() {
        return this.previousOptionValue;
    }

    public void setPreviousOptionValue(String previousOptionValue) {
        this.previousOptionValue = previousOptionValue;
    }

    public void setSelectedPlate(int plate) {
        this.selectedPlate = plate;
    }

    public int getSelectedPlate() {
        return this.selectedPlate;
    }

    public void setPrevSelectedPlateForMiniAndToy(int plate) {
        this.preSelectedPlateForMiniAndToy = plate;
    }

    public int getPreSelectedPlateForMiniAndToy() {
        return this.preSelectedPlateForMiniAndToy;
    }

    public boolean isShootingScreenOpened() {
        return this.isShootingScreenOpened;
    }

    public void setShootingScreenOpened(boolean isShootingScreenOpened) {
        this.isShootingScreenOpened = isShootingScreenOpened;
    }

    public String getPreviousToyColor() {
        return this.previousToyColor;
    }

    public void setPreviousToyColor(String previousToyColor) {
        this.previousToyColor = previousToyColor;
    }

    public boolean isAppTopOpenFromMenu() {
        return this.isAppTopOpenFromMenu;
    }

    public void setAppTopOpenFromMenu(boolean isAppTopOpenFromMenu) {
        this.isAppTopOpenFromMenu = isAppTopOpenFromMenu;
    }

    public String getPreviousToyDarkness() {
        return this.previousToyDarkness;
    }

    public void setPreviousToyDarkness(String previousToyDarkness) {
        this.previousToyDarkness = previousToyDarkness;
    }

    public boolean isPlusParameterSelected() {
        return this.plusParameterSelected;
    }

    public void setPlusParameterSelected(boolean plusParameterSelected) {
        this.plusParameterSelected = plusParameterSelected;
    }

    public void setComeFromColorIndication(boolean flag) {
        this.isComeFromColorIndication = flag;
    }

    public boolean isComeFromColorIndication() {
        return this.isComeFromColorIndication;
    }

    public int getPrevSelectedPlate() {
        return this.prevSelectedPlate;
    }

    public void setPrevSelectedPlate(int prevSelectedPlate) {
        this.prevSelectedPlate = prevSelectedPlate;
    }
}
