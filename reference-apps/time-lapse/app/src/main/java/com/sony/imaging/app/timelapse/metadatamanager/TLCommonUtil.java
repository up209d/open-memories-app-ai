package com.sony.imaging.app.timelapse.metadatamanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ApscModeController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapse;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.UtilPFWorkaround;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.AppContext;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.menu.controller.TimelapseExposureModeController;
import com.sony.imaging.app.timelapse.shooting.TimelapseExecutorCreator;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.MovieController;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLPictureQualityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLPictureSizeController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TLSilentShutterController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseCreativeStyleController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseWhiteBalanceController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.FileHelper;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/* loaded from: classes.dex */
public class TLCommonUtil {
    public static final int FRAME_NUMBER_PER_24P_AVI = 1440;
    public static final int FRAME_NUMBER_PER_30P_AVI = 1800;
    private static final String INH_FACTOR_ID_BAD_BLOCK_OVER = "INH_FACTOR_BAD_BLOCK_WRITE_PROTECT";
    private static final String INH_FACTOR_ID_READ_ONLY = "INH_FACTOR_READ_ONLY_MEDIA";
    private static final String INH_FACTOR_ID_WRITE_PROTECTED = "INH_FACTOR_MEDIA_WRITE_PROTECT";
    private static final int LIMIT_TIME_FOR_LONG_INTERVAL = 1000;
    private static final int LIMIT_TIME_FOR_SHORT_INTERVAL = 500;
    private static final String SEPARATOR_SLASH = "/";
    private static final String UPDATE_SEQUNCE_NOT_REQUIRED = "Update sequenc is not required";
    BackUpUtil backupUtil;
    private int mCurrentState;
    private int mLastSelectedState;
    private int mLastState;
    private int totalAvailRemaining;
    private static String AETRACKINGTAG = "AE tracking implementation  option value option.";
    private static boolean sTimelapseLiteState = false;
    private static boolean mTestShot = false;
    private static int mTargetMedia = 0;
    private static TLCommonUtil themeUtil = null;
    private static String[] itemArray = {PictureEffectController.MODE_HDR_ART, PictureEffectController.MODE_SOFT_FOCUS, PictureEffectController.MODE_ILLUST, PictureEffectController.MODE_TOY_CAMERA, PictureEffectController.MODE_POSTERIZATION, PictureEffectController.MODE_PART_COLOR};
    private static List<String> PE_HAVING_OPTION_VALUES_LIST = Arrays.asList(itemArray);
    private static String[] mItemsArrayNotSuportingMovie = {PictureEffectController.MODE_HDR_ART, PictureEffectController.MODE_SOFT_FOCUS, PictureEffectController.MODE_RICH_TONE_MONOCHROME, PictureEffectController.WATERCOLOR, PictureEffectController.MODE_ILLUST};
    private static List<String> MOVIE_NOT_SUPPORT_ARRAY_LIST = Arrays.asList(mItemsArrayNotSuportingMovie);
    private static long discardTimes = 0;
    private static boolean isUpdate = true;
    private static String mPictureEffectKey = null;
    private static String[] THEME_INH_FACTORS_KEY = {"INH_FACTOR_THEME_CLOUDY", "INH_FACTOR_THEME_NIGHTSKY", "INH_FACTOR_THEME_NIGHTSCEAN", "INH_FACTOR_THEME_SUNSET", "INH_FACTOR_THEME_SUNRISE", "INH_FACTOR_THEME_MINIATURE", "INH_FACTOR_THEME_AUTO", "INH_FACTOR_THEME_CUSTOM"};
    private static final List<String> UnSupportedRawDeviceList = Arrays.asList("ILDCSC_MUSASHI4Gb", "DSCSC_MUSASHI4Gb", "DSCSC_KOJIRO4Gb");
    private static Layout mSavingDoneLayout = null;
    private final String TAG = TLCommonUtil.class.getName();
    private String mPictureEffect = "off";
    private final int BYTE_IMAGE_SIZE = 33554432;
    private final int MOVIE_SIZE_FHD = 829440;
    private final int MOVIE_SIZE_HD = 368640;
    private String mFileFormat = null;
    private int mShootingNum = 5;
    private int mAEState = 0;
    private int mLaunchBootFactor = 2;
    private boolean mCaptured = false;
    String[] mExposureCompesationValue = {"+3.0", "+2.7", "+2.3", "+2.0", "+1.7", "+1.3", "+1.0", "+0.7", "+0.3", "0.0", "-0.3", "-0.7", "-1.0", "-1.3", "-1.7", "-2.0", "-2.3", "-2.7", "-3.0"};
    private int interval = 2;
    private String selectedThemeName = null;
    List<String> mApertureValueList = null;
    private String hours = "";
    private String minutes = "";
    private String amPm = "";
    private String year = "";
    private String month = "";
    private String day = "";
    private boolean mHasAngleShiftAddon = false;
    private boolean misOkFocusedOnRemainingMemroryCaution = false;
    private int mInternalTotalRemaining = 0;
    String mOldBackupFileFormat = null;
    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private PlainCalendar calendar = null;
    private int fileNumber = 0;
    private boolean isThemeChanged = false;
    private boolean mDetachedLens = false;
    private boolean isCaneledByPowerOff = false;
    private boolean isS2_ONFromPlayBack = false;
    private boolean isAngleShiftBootDone = false;
    private String mBootFromWhere = null;
    private boolean isImageSizeSetting = false;
    private boolean isImageAspectSetting = false;
    private boolean isImageQualitySetting = false;
    private String mImageSize = null;
    private String mImageAspect = null;
    private String mImageQuality = null;
    private boolean isRemoteCaptured = false;
    private boolean mMaybePhaseDiff = false;

    public static TLCommonUtil getInstance() {
        if (themeUtil == null) {
            themeUtil = new TLCommonUtil();
            mTestShot = false;
            mTargetMedia = 0;
        }
        return themeUtil;
    }

    public int getFrameRate() {
        if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue()) || !"framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            return 24;
        }
        return 30;
    }

    public String getSelectedThemeName() {
        return this.selectedThemeName;
    }

    public void updateLastAppliedTheme() {
        this.mLastSelectedState = this.mCurrentState;
    }

    public void setSelectedThemeName(String selectedThemeName) {
        this.selectedThemeName = selectedThemeName;
    }

    private TLCommonUtil() {
        if (this.backupUtil == null) {
            this.backupUtil = BackUpUtil.getInstance();
        }
        this.mCurrentState = this.backupUtil.getPreferenceInt(TimeLapseConstants.TIMELAPSE_CURRENT_STATE, 6);
        calculateRemainingMemory();
    }

    public String getDateFormat(PlainCalendar calendar) {
        int year = calendar.year;
        int month = calendar.month;
        int day = calendar.day;
        return (year < 10 ? "0" : "") + year + (month < 10 ? "0" : "") + month + (day < 10 ? "0" : "") + day;
    }

    public String getTimeFormat(PlainCalendar calendar) {
        int hour = calendar.hour;
        int minute = calendar.minute;
        int second = calendar.second;
        return (hour < 10 ? "0" : "") + hour + (minute < 10 ? "0" : "") + minute + (second < 10 ? "0" : "") + second;
    }

    public void setCaptureStatus(boolean bCaptured) {
        this.mCaptured = bCaptured;
    }

    public boolean isCaptured() {
        return this.mCaptured;
    }

    public String getRecordingTime(int sec) {
        int hours = sec / 3600;
        int remainder = sec % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        String totalRecTime = (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        return totalRecTime;
    }

    public int getInterval() {
        return this.interval;
    }

    public int getAdjustInterval(int setVal, long firstTakePictureTime, int delayOffset, int count) {
        int adjustDelayTime;
        int settingTime = setVal * LIMIT_TIME_FOR_LONG_INTERVAL;
        int adjustDelayTime2 = settingTime - delayOffset;
        long currentTimeMillis = System.currentTimeMillis();
        if (count == 2) {
            discardTimes = 0L;
        }
        int totalDelayTime = (int) ((currentTimeMillis - firstTakePictureTime) - discardTimes);
        if (currentTimeMillis <= firstTakePictureTime) {
            return adjustDelayTime2;
        }
        int totalDelayTime2 = totalDelayTime - ((count - 1) * settingTime);
        if (setVal >= 3) {
            if (totalDelayTime2 > LIMIT_TIME_FOR_LONG_INTERVAL) {
                discardTimes += totalDelayTime2 - 1000;
                totalDelayTime2 = LIMIT_TIME_FOR_LONG_INTERVAL;
            }
            if (totalDelayTime2 < -1000) {
                discardTimes += totalDelayTime2 + LIMIT_TIME_FOR_LONG_INTERVAL;
                totalDelayTime2 = -1000;
            }
        } else {
            if (totalDelayTime2 > 500) {
                discardTimes += totalDelayTime2 - 500;
                totalDelayTime2 = 500;
            }
            if (totalDelayTime2 < -500) {
                discardTimes += totalDelayTime2 + 500;
                totalDelayTime2 = -500;
            }
        }
        if (totalDelayTime2 >= 0) {
            if (adjustDelayTime2 > totalDelayTime2) {
                adjustDelayTime = adjustDelayTime2 - totalDelayTime2;
            } else {
                int totalDelayTime3 = totalDelayTime2 / (count - 1);
                if (adjustDelayTime2 > totalDelayTime3) {
                    adjustDelayTime = adjustDelayTime2 - totalDelayTime3;
                } else {
                    adjustDelayTime = 500;
                }
            }
        } else if (adjustDelayTime2 - totalDelayTime2 < settingTime * 2) {
            adjustDelayTime = adjustDelayTime2 - totalDelayTime2;
        } else {
            int totalDelayTime4 = totalDelayTime2 / (count - 1);
            if (adjustDelayTime2 - totalDelayTime4 < settingTime * 2) {
                adjustDelayTime = adjustDelayTime2 - totalDelayTime4;
            } else {
                adjustDelayTime = settingTime + 500;
            }
        }
        if (setVal >= 3) {
            if (adjustDelayTime > (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) + LIMIT_TIME_FOR_LONG_INTERVAL) {
                adjustDelayTime = (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) + LIMIT_TIME_FOR_LONG_INTERVAL;
            }
            if (adjustDelayTime < (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) - 1000) {
                adjustDelayTime = (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) - 1000;
            }
        } else {
            if (adjustDelayTime > (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) + 500) {
                adjustDelayTime = (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) + 500;
            }
            if (adjustDelayTime < (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) - 500) {
                adjustDelayTime = (setVal * LIMIT_TIME_FOR_LONG_INTERVAL) - 500;
            }
        }
        AppLog.info("getAdjustInterval", "adjustDelayTime: " + adjustDelayTime);
        return adjustDelayTime;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getShootingNum() {
        return this.mShootingNum;
    }

    public void setShootingNum(int shootingNum) {
        this.mShootingNum = shootingNum;
    }

    public String getFileFormat() {
        return this.mFileFormat;
    }

    public int getAETrakingStatus() {
        return this.mAEState;
    }

    public void setAELStatus(int aeStatus) {
        this.mAEState = aeStatus;
    }

    public String getPictureEffect() {
        if ("off".equalsIgnoreCase(this.mPictureEffect)) {
            return null;
        }
        if (this.mCurrentState != 5 && this.mCurrentState != 7) {
            return null;
        }
        if (PictureEffectController.MODE_MINIATURE.equalsIgnoreCase(this.mPictureEffect)) {
            return PictureEffectController.getInstance().getValue(this.mPictureEffect);
        }
        return this.mPictureEffect;
    }

    public void setPictureEffect(String pictureEffect) {
        if (this.mFileFormat.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL) || !TimelapsePictureEffectController.getInstance().getValue("PictureEffect").equalsIgnoreCase(PictureEffectController.MODE_MINIATURE) || !pictureEffect.equalsIgnoreCase("auto")) {
            this.mPictureEffect = pictureEffect;
        }
    }

    public int getCurrentState() {
        this.mCurrentState = this.backupUtil.getPreferenceInt(TimeLapseConstants.TIMELAPSE_CURRENT_STATE, 6);
        return this.mCurrentState;
    }

    public void setCurrentState(int currentState) {
        this.mCurrentState = currentState;
        this.backupUtil.setPreference(TimeLapseConstants.TIMELAPSE_CURRENT_STATE, Integer.valueOf(currentState));
        setThemeUtil(currentState);
        TLSilentShutterController.getInstance().setSilentShutterHaitaValue();
        setImageAspect(this.mFileFormat);
    }

    public short getState() {
        themeUtil = getInstance();
        return (short) themeUtil.mCurrentState;
    }

    public void setState() {
        themeUtil = getInstance();
    }

    public static TLCommonUtil getThemeUtil() {
        themeUtil = getInstance();
        return themeUtil;
    }

    public void setPictureEffectIconUpdate(boolean bUpdate) {
        if (bUpdate) {
            AppLog.info(this.TAG, "setPictureEffectIconUpdate: " + bUpdate);
        }
        isUpdate = bUpdate;
        if (!isUpdate) {
            setPictureEffectKey();
        }
    }

    public boolean isPictureEffectIconUpdate() {
        AppLog.info(this.TAG, "isPictureEffectIconUpdate: " + isUpdate);
        return isUpdate;
    }

    public void setPictureEffectKey() {
        PictureEffectController controller = PictureEffectController.getInstance();
        String value = controller.getValue();
        String mode = controller.getValue("PictureEffect");
        if (value != null && mode != null && !value.equals(mode)) {
            value = mode + value;
        }
        mPictureEffectKey = value;
    }

    public String getPictureEffectKey() {
        return mPictureEffectKey;
    }

    public void setThemeUtil(int currentState) {
        themeUtil.mCurrentState = currentState;
        setValue();
        setDefaultValue();
        setFactor(currentState);
        themeSwitchKikiLog();
    }

    private void setFactor(int currentState) {
        for (int themeIndex = 0; themeIndex < THEME_INH_FACTORS_KEY.length; themeIndex++) {
            boolean isApplicableFactor = false;
            if (currentState == themeIndex) {
                isApplicableFactor = true;
            }
            AvailableInfo.setFactor(THEME_INH_FACTORS_KEY[themeIndex], isApplicableFactor);
        }
    }

    public void setDefaultValue() {
        switch (this.mCurrentState) {
            case 0:
                setThemeAutomaticSetting("Aperture", "100", "landscape");
                return;
            case 1:
                int modelCategory = ScalarProperties.getInt("model.category");
                String isoValue = "1600";
                if (modelCategory == 2 || modelCategory == 2) {
                    isoValue = ISOSensitivityController.ISO_800;
                }
                setThemeAutomaticSetting("Aperture", isoValue, "standard");
                return;
            case 2:
                setThemeAutomaticSetting("Aperture", "100", "standard");
                return;
            case 3:
                setThemeAutomaticSetting("Aperture", "100", "sunset");
                return;
            case 4:
                setThemeAutomaticSetting("Aperture", "100", "landscape");
                return;
            case 5:
                setThemeAutomaticSetting("Aperture", "100", "standard");
                return;
            case 6:
                setThemeAutomaticSetting("ProgramAuto", "0", "standard");
                return;
            case 7:
                String expoMode = null;
                if (ModeDialDetector.hasModeDial()) {
                    setExposureMode();
                } else {
                    expoMode = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_EXPOSURE_MODE_KEY, "ProgramAuto");
                }
                String iso = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_ISO_VALUE_KEY, "0");
                String creativeStyle = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY, "standard");
                setThemeAutomaticSetting(expoMode, iso, creativeStyle);
                return;
            default:
                setThemeAutomaticSetting("Aperture", "100", "standard");
                return;
        }
    }

    private void setImageAspect(String fileFormat) {
        if (TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE.equalsIgnoreCase(fileFormat)) {
            String aspect = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (PictureSizeController.ASPECT_1_1.equalsIgnoreCase(aspect)) {
                String haitaAspect = TLPictureSizeController.getInstance().getAvailableValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO).get(0);
                TLPictureSizeController.getInstance().setTLValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, haitaAspect);
                TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE = haitaAspect;
                this.backupUtil.setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE);
                this.backupUtil.setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_HAITA_KEY, true);
                return;
            }
            return;
        }
        if (TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL.equalsIgnoreCase(fileFormat)) {
            boolean haitaDisable = this.backupUtil.getPreferenceBoolean(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_HAITA_KEY, false);
            if (haitaDisable) {
                TLPictureSizeController.getInstance().setTLValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, PictureSizeController.ASPECT_1_1);
                TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE = PictureSizeController.ASPECT_1_1;
                this.backupUtil.setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE);
                this.backupUtil.setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_HAITA_KEY, false);
            }
        }
    }

    public void saveChangedValues(String recMode, int aeTrackingStatus, int interval, int shootingNum) {
        AppLog.enter("INSIDE", "storeChangedValues");
        setImageAspect(recMode);
        switch (this.mCurrentState) {
            case 0:
                this.backupUtil.setPreference(TimeLapseConstants.CLOUDY_SKY_FILE_FORMAT_KEY, recMode);
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.CLOUDY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.CLOUDY_SKY_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.CLOUDY_SKY_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.CLOUDY_SKY_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 1:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SKY_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SKY_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SKY_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SKY_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 2:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SCENE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SCENE_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SCENE_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SCENE_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.NIGHTY_SCENE_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 3:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.SUN_SET_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.SUN_SET_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.SUN_SET_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.SUN_SET_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 4:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.SUN_RISE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.SUN_RISE_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.SUN_RISE_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.SUN_RISE_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 5:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.MINIATURE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.MINIATURE_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.MINIATURE_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.MINIATURE_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.MINIATURE_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 6:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.STANDARD_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.STANDARD_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.STANDARD_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
            case 7:
                this.mOldBackupFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_FILE_FORMAT_KEY, recMode);
                this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_AEL_STATUS_KEY, Integer.valueOf(aeTrackingStatus));
                this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_INTERVAL_KEY, Integer.valueOf(interval));
                this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_SHOOTING_NUM_KEY, Integer.valueOf(shootingNum));
                break;
        }
        setThemeUtil(this.mCurrentState);
    }

    private void updateExecutorSequence() {
        if (this.mFileFormat.equals(this.mOldBackupFileFormat) || this.mOldBackupFileFormat == null || this.mLastSelectedState != this.mCurrentState) {
            Log.d(this.TAG, UPDATE_SEQUNCE_NOT_REQUIRED);
        } else if (TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL.equals(this.mFileFormat) || TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL.equals(this.mOldBackupFileFormat)) {
            TimelapsePictureEffectController.getInstance().setValueNoBackup("PictureEffect", "off");
            ExecutorCreator.getInstance().updateSequence();
            ExecutorCreator.getInstance().stableSequence();
        }
        this.mOldBackupFileFormat = null;
    }

    public int getAvailableRemainingShot() {
        return this.totalAvailRemaining;
    }

    public void setMovieRemaining(int mRemaining) {
        this.totalAvailRemaining = mRemaining;
    }

    public void setInternalTotalRemaining(int internalTotalRemaining) {
        this.mInternalTotalRemaining = internalTotalRemaining;
    }

    public int getInternalTotalRemaining() {
        return this.mInternalTotalRemaining;
    }

    public int decrementTotalRemaining() {
        this.mInternalTotalRemaining--;
        if (this.mInternalTotalRemaining < 0) {
            this.mInternalTotalRemaining = 0;
        }
        return this.mInternalTotalRemaining;
    }

    public void setValue() {
        switch (this.mCurrentState) {
            case 0:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_INTERVAL_KEY, 5);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.CLOUDY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 1:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_INTERVAL_KEY, 30);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 2:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_INTERVAL_KEY, 3);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SCENE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 3:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_INTERVAL_KEY, 10);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.SUN_SET_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                if (isOlderPFVersion()) {
                    this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 4);
                } else {
                    this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 3);
                    if (4 == this.mAEState) {
                        this.mAEState = 3;
                        this.backupUtil.setPreference(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 3);
                    }
                }
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 4:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_INTERVAL_KEY, 10);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.SUN_RISE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                if (isOlderPFVersion()) {
                    this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 4);
                } else {
                    this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 3);
                    if (4 == this.mAEState) {
                        this.mAEState = 3;
                        this.backupUtil.setPreference(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 3);
                    }
                }
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 5:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_INTERVAL_KEY, 2);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.MINIATURE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.MINIATURE_PICTURE_EFFECT_KEY, "hcenter");
                break;
            case 6:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_INTERVAL_KEY, 1);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
            case 7:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_INTERVAL_KEY, 1);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_AEL_STATUS_KEY, 0);
                if (!getInstance().isOlderPFVersion() && 4 == this.mAEState) {
                    this.mAEState = 3;
                    this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_AEL_STATUS_KEY, 3);
                    break;
                }
                break;
            default:
                this.interval = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_INTERVAL_KEY, 1);
                this.mShootingNum = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_SHOOTING_NUM_KEY, 240);
                this.mFileFormat = this.backupUtil.getPreferenceString(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAEState = this.backupUtil.getPreferenceInt(TimeLapseConstants.STANDARD_AEL_STATUS_KEY, 0);
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.COMMON_THEME_PICTURE_EFFECT_KEY, "off");
                break;
        }
        setShootModeFactor();
        setPictureEffect(this.mPictureEffect);
        updateExecutorSequence();
    }

    private void setShootModeFactor() {
        if (this.mFileFormat.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL)) {
            TLShootModeSettingController.getInstance().setCaptureState(1);
        } else if (this.mFileFormat.equalsIgnoreCase("MOVIE_FORMAT")) {
            TLShootModeSettingController.getInstance().setCaptureState(0);
        } else {
            TLShootModeSettingController.getInstance().setCaptureState(2);
        }
        if (this.mCurrentState == 7) {
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_THEME_STILL_PICTURE_EFFECT_KEY, "off");
            } else {
                this.mPictureEffect = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_THEME_MOVIE_PICTURE_EFFECT_KEY, "off");
            }
        }
        calculateRemainingMemory();
    }

    private void setThemeAutomaticSetting(String exposureMode, String isoValue, String creativeStyle) {
        if (exposureMode != null && !ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE).equalsIgnoreCase(exposureMode)) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, exposureMode);
        }
        if (!ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO).equalsIgnoreCase(isoValue)) {
            ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, isoValue);
        }
        if (!isSilentShutterOn() && this.mCurrentState == 7 && "off".equalsIgnoreCase(this.mPictureEffect)) {
            updatePictureEffectOnCamera("off", false);
        }
        TimelapseCreativeStyleController.getInstance().setTimelapseValue(CreativeStyleController.CREATIVESTYLE, creativeStyle);
        if (!isSilentShutterOn() && this.mCurrentState != 5 && this.mCurrentState != 7) {
            updatePictureEffectOnCamera("off", false);
        }
        if (this.mCurrentState == 5) {
            TLPictureQualityController.getInstance().setMiniatureValue();
            changeRemainingPictureEffectValue(PictureEffectController.MODE_MINIATURE);
        } else {
            TLPictureQualityController.getInstance().resetValue();
        }
        if (this.mCurrentState != 7) {
            if (this.mCurrentState == 4) {
                TimelapseWhiteBalanceController.getInstance().setTimelapseValue(WhiteBalanceController.WHITEBALANCE, "color-temp");
            } else {
                TimelapseWhiteBalanceController.getInstance().setTimelapseValue(WhiteBalanceController.WHITEBALANCE, "auto");
            }
        } else {
            TimelapseWhiteBalanceController.getInstance().setTimelapseValue(WhiteBalanceController.WHITEBALANCE, "auto");
            String optValue = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_AWB_KEY_OPTION_VALUE, "0/0/0");
            String[] value = optValue.split(SEPARATOR_SLASH);
            int lightBalance = Integer.parseInt(value[0]);
            int colorCompensation = Integer.parseInt(value[1]);
            int colorTemperature = Integer.parseInt(value[2]);
            WhiteBalanceController.WhiteBalanceParam wbInfo = new WhiteBalanceController.WhiteBalanceParam(lightBalance, colorCompensation, colorTemperature);
            WhiteBalanceController.getInstance().setDetailValue(wbInfo);
            String wbValue = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_WB_KEY, "auto");
            TimelapseWhiteBalanceController.getInstance().setTimelapseValue(WhiteBalanceController.WHITEBALANCE, wbValue);
            String optValue2 = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_WB_KEY_OPTION_VALUE, "0/0/0");
            String[] value2 = optValue2.split(SEPARATOR_SLASH);
            int lightBalance2 = Integer.parseInt(value2[0]);
            int colorCompensation2 = Integer.parseInt(value2[1]);
            int colorTemperature2 = Integer.parseInt(value2[2]);
            WhiteBalanceController.WhiteBalanceParam wbInfo2 = new WhiteBalanceController.WhiteBalanceParam(lightBalance2, colorCompensation2, colorTemperature2);
            WhiteBalanceController.getInstance().setDetailValue(wbInfo2);
            handlePictureEffectValuesForApplication();
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 0) {
            TLPictureSizeController.getInstance().setTLValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, this.backupUtil.getPreferenceString(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE));
            TLPictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, this.backupUtil.getPreferenceString(TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE));
        } else {
            TLPictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, PictureSizeController.ASPECT_16_9);
            TLPictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, PictureSizeController.SIZE_L);
        }
        setThemeExposureAperture();
    }

    private void handlePictureEffectValuesForApplication() {
        if (!isSilentShutterOn()) {
            String lastSelectedItemID = this.backupUtil.getPreferenceString("PictureEffect", "off");
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
                changePictureEffectValue(lastSelectedItemID);
                changeRemainingPictureEffectValue(lastSelectedItemID);
            } else if (MOVIE_NOT_SUPPORT_ARRAY_LIST.contains(lastSelectedItemID)) {
                updatePictureEffectOnCamera("off", false);
            } else {
                changeRemainingPictureEffectValue(lastSelectedItemID);
            }
        }
    }

    private void setExposureMode() {
        String exposureMode = null;
        switch (ModeDialDetector.getModeDialPosition()) {
            case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                exposureMode = "ProgramAuto";
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                exposureMode = "Shutter";
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                exposureMode = "Aperture";
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                exposureMode = ExposureModeController.MANUAL_MODE;
                break;
        }
        if (exposureMode != null) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, exposureMode);
        }
    }

    public String getFilePathOnMedia() {
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            if (2 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + TimeLapseConstants.MS_CARD_PATH;
            } else if (1 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + TimeLapseConstants.SD_CARD_PATH;
            }
            AppLog.trace("YES", "========== Files path on Media regarding this application ============== " + mFilePathOnMedia);
        }
        return mFilePathOnMedia;
    }

    public String getFormattedTime(String time) {
        if (time == null) {
            return "01:10 AM";
        }
        this.hours = time.substring(0, 2);
        this.minutes = time.substring(2, 4);
        int hourValue = Integer.parseInt(this.hours);
        if (hourValue < 12) {
            this.amPm = "AM";
        } else {
            this.amPm = "PM";
            int newHour = hourValue - 12;
            if (newHour < 10) {
                this.hours = "0" + newHour;
            } else {
                this.hours = "" + newHour;
            }
        }
        String newTime = this.hours + ":" + this.minutes + ExposureModeController.SOFT_SNAP + this.amPm;
        return newTime;
    }

    public String getFormatteddate(String date) {
        if (date == null) {
            return "";
        }
        this.year = date.substring(0, 4);
        this.month = getMonthName(date.substring(4, 6));
        this.day = date.substring(6);
        String newDate = this.month + "-" + this.day + "-" + this.year;
        return newDate;
    }

    public String getMonthName(String month) {
        return this.monthName[Integer.parseInt(month) - 1];
    }

    public void deleteThumbnailFile(String path) {
        int index = path.indexOf(StringBuilderThreadLocal.PERIOD);
        String str1 = path.substring(0, index) + ".THM";
        boolean isDeleted = false;
        File dir = new File(str1);
        if (FileHelper.exists(dir)) {
            isDeleted = dir.delete();
        }
        if (!isDeleted) {
            AppLog.trace(this.TAG, "Thumnail file cannot be deleted");
        }
    }

    public boolean isAviFileExist(String filePath) {
        File dir = new File(filePath);
        return FileHelper.exists(dir);
    }

    private void themeSwitchKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4141;
        switch (this.mCurrentState) {
            case 0:
                kikilogId = 4180;
                break;
            case 1:
                kikilogId = 4181;
                break;
            case 2:
                kikilogId = 4182;
                break;
            case 3:
                kikilogId = 4184;
                break;
            case 4:
                kikilogId = 4183;
                break;
            case 5:
                kikilogId = 4185;
                break;
            case 6:
                kikilogId = 4186;
                if (isTimelapseLiteApplication()) {
                    kikilogId = 4189;
                    break;
                }
                break;
            case 7:
                kikilogId = 4187;
                break;
        }
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }

    public PlainCalendar pinCalendar() {
        this.fileNumber = 0;
        PlainCalendar currentCalendar = TimeUtil.getCurrentCalendar();
        this.calendar = currentCalendar;
        return currentCalendar;
    }

    public PlainCalendar getPinedCalender() {
        return this.calendar;
    }

    public String getAviFilename(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        return filePath.substring(index - 8, index) + ".AVI";
    }

    public String getAviFilePathName(PlainCalendar calendar) {
        String fileName = getTimeFormat(calendar) + "00.AVI";
        String aviDirectory = getAviFolderName(calendar);
        String filePath = aviDirectory + SEPARATOR_SLASH + fileName;
        return filePath;
    }

    public String getAviFilePathNameSequel() {
        this.fileNumber++;
        String sFileNumber = String.format("%02d", Integer.valueOf(this.fileNumber));
        String fileName = getTimeFormat(getPinedCalender()) + sFileNumber + ".AVI";
        String aviDirectory = getAviFolderName(getPinedCalender());
        String filePath = aviDirectory + SEPARATOR_SLASH + fileName;
        return filePath;
    }

    public String getAviFoldername(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        return filePath.substring(index - 15, index - 9);
    }

    private String getAviFolderName(PlainCalendar calendar) {
        String mediaPath = getFilePathOnMedia();
        String date = getDateFormat(calendar);
        String aviDirectory = mediaPath + SEPARATOR_SLASH + date.substring(2, date.length());
        return aviDirectory;
    }

    public Bitmap getThumbnailFilename(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        String thmPath = filePath.substring(0, index) + ".THM";
        Bitmap mThumbnailBitmap = BitmapFactory.decodeFile(thmPath);
        return mThumbnailBitmap;
    }

    public boolean createFolder(PlainCalendar calendar) {
        String aviFolderName = getAviFolderName(calendar);
        File dir = new File(aviFolderName);
        if (!FileHelper.exists(dir)) {
            if (FileHelper.mkdirs(dir)) {
                AppLog.trace("YES", "================= Directory Created Successfully ==================== ");
            } else {
                AppLog.trace("YES", "================= Directory not created ================== ");
            }
        } else {
            AppLog.trace("YES", "================= Directory all ready exist to store AVI files==================== ");
        }
        return false;
    }

    public boolean checkExposureMode() {
        if (getCurrentState() == 7) {
            if (ModeDialDetector.hasModeDial()) {
                boolean isCautionEnable = TimelapseExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getDialPosition());
                return isCautionEnable;
            }
            String expMode = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
            if (TimelapseExposureModeController.getInstance().isValidExpoMode(expMode)) {
                return false;
            }
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "ProgramAuto");
            return false;
        }
        this.backupUtil.setPreference(TimeLapseConstants.CUSTOM_APERTURE_KEY, "F5.6");
        return false;
    }

    public long getUTCTime() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        PlainCalendar pc = TimeUtil.getCurrentCalendar();
        cal.set(pc.year, pc.month - 1, pc.day, pc.hour, pc.minute, pc.second);
        AppLog.trace(this.TAG, "Local Time: " + cal.getTimeInMillis());
        AppLog.trace(this.TAG, "Hour: " + cal.get(10) + "Minute: " + cal.get(12));
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diffGMT = p.gmtDiff;
        int diffSummerTime = p.summerTimeDiff;
        AppLog.trace(this.TAG, "SummerTime: " + (-diffSummerTime));
        AppLog.trace(this.TAG, "Diff: " + (-diffGMT));
        cal.add(12, -diffGMT);
        cal.add(12, -diffSummerTime);
        AppLog.trace(this.TAG, "Universal Time: " + cal.getTimeInMillis());
        AppLog.trace(this.TAG, "Hour: " + cal.get(10) + "Minute: " + cal.get(12));
        return cal.getTimeInMillis();
    }

    public int getThemeNameResID(int theme) {
        int[] resIDs = {R.string.STRID_FUNC_TIMELAPSE_TCLOUD, R.string.STRID_FUNC_TIMELAPSE_TSTAR, R.string.STRID_AMC_STR_00962, R.string.STRID_FUNC_TIMELAPSE_TSUNSET, R.string.STRID_FUNC_TIMELAPSE_TSUNRISE, R.string.STRID_AMC_STR_06826, R.string.STRID_AMC_STR_01016, R.string.STRID_AMC_STR_01036};
        return resIDs[theme];
    }

    public void setThemeExposureAperture() {
        setThemeExposureCompensation();
        setThemeApertureValue();
    }

    public void setNightSkyExposureCompensation() {
        if (this.isThemeChanged) {
            ExposureCompensationController.getInstance().setValue("ExposureCompensation", "2");
        }
    }

    public void setThemeChanged(boolean themeChanged) {
        this.isThemeChanged = themeChanged;
    }

    private void setThemeExposureCompensation() {
        int expoComp = 0;
        if (this.mLastState != this.mCurrentState) {
            if (this.mCurrentState == 1) {
                expoComp = 2;
            }
        } else if (TimeLapseConstants.isEVDial) {
            TimeLapseConstants.isEVDial = false;
            expoComp = ExposureCompensationController.getInstance().getExposureCompensationIndex();
            if (CameraSetting.getPfApiVersion() >= 2 && EVDialDetector.hasEVDial()) {
                int ev = EVDialDetector.getEVDialPosition();
                if (ev != 0) {
                    expoComp = ev;
                    TimeLapseConstants.slastStateExposureCompansasion = expoComp;
                } else {
                    expoComp = TimeLapseConstants.slastStateExposureCompansasion;
                }
            }
        } else {
            expoComp = TimeLapseConstants.slastStateExposureCompansasion;
        }
        this.mLastState = this.mCurrentState;
        AppLog.info(this.TAG, "TimeLapseConstants.Current index =" + expoComp);
        ExposureCompensationController.getInstance().setValue("ExposureCompensation", "" + expoComp);
    }

    public void setThemeApertureValue() {
        String apertureValue;
        setIrisRingSettingByCamera(getCurrentState() == 7);
        switch (getCurrentState()) {
            case 0:
            case 3:
            case 4:
                apertureValue = "F8.0";
                break;
            case 1:
                apertureValue = TimeLapseConstants.NIGHTY_SKY_APERTURE;
                break;
            case 2:
            case 5:
                apertureValue = "F5.6";
                break;
            case 6:
            default:
                apertureValue = "F8.0";
                break;
            case 7:
                apertureValue = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_APERTURE_KEY, "F5.6");
                break;
        }
        setAperture(apertureValue);
    }

    public String getTargetApertureValue() {
        switch (getCurrentState()) {
            case 0:
            case 3:
            case 4:
                return "F8.0";
            case 1:
                return TimeLapseConstants.NIGHTY_SKY_APERTURE;
            case 2:
            case 5:
                return "F5.6";
            case 6:
            default:
                return "F8.0";
            case 7:
                String apertureValue = this.backupUtil.getPreferenceString(TimeLapseConstants.CUSTOM_APERTURE_KEY, "F5.6");
                return apertureValue;
        }
    }

    private void setAperture(String setApertureValue) {
        CameraSetting camSetting = CameraSetting.getInstance();
        String apertureValue = getApertureValues(camSetting.getApertureInfo());
        setExactAperture(camSetting, setApertureValue, apertureValue);
    }

    void setExactAperture(CameraSetting camSetting, String setApertureValue, String apertureValue) {
        if (!apertureValue.equalsIgnoreCase(TimeLapseConstants.INVALID_APERTURE_STRING) && !apertureValue.equalsIgnoreCase(setApertureValue)) {
            int curruntApertureposition = 0;
            int expectedApertureposition = 0;
            if (apertureValue.equalsIgnoreCase("F2.4")) {
                curruntApertureposition = TimeLapseConstants.APERTURE_VALUE_LIST.indexOf("F2.5");
            } else if (apertureValue.equalsIgnoreCase("F1.7")) {
                curruntApertureposition = TimeLapseConstants.APERTURE_VALUE_LIST.indexOf("F1.8");
            } else if (TimeLapseConstants.APERTURE_VALUE_LIST.contains(apertureValue)) {
                curruntApertureposition = TimeLapseConstants.APERTURE_VALUE_LIST.indexOf(apertureValue);
            }
            if (TimeLapseConstants.APERTURE_VALUE_LIST.contains(setApertureValue)) {
                expectedApertureposition = TimeLapseConstants.APERTURE_VALUE_LIST.indexOf(setApertureValue);
            }
            AppLog.info(this.TAG, "adjustAperture expectedApertureposition: " + expectedApertureposition);
            AppLog.info(this.TAG, "adjustAperture curruntApertureposition: " + curruntApertureposition);
            AppLog.info(this.TAG, "adjustAperture arg: " + (expectedApertureposition - curruntApertureposition));
            camSetting.getCamera().adjustAperture(expectedApertureposition - curruntApertureposition);
        }
    }

    public String getApertureValues(CameraEx.ApertureInfo info) {
        if (info == null) {
            return TimeLapseConstants.INVALID_APERTURE_STRING;
        }
        int currentAperture = info.currentAperture;
        String convertedAperture = convertApertureValueFormat(currentAperture / 100.0f);
        return convertedAperture;
    }

    private String convertApertureValueFormat(float value) {
        String displayValue;
        if (value == TimeLapseConstants.INVALID_APERTURE_VALUE) {
            displayValue = TimeLapseConstants.INVALID_APERTURE_STRING;
        } else if (value < 10.0f) {
            displayValue = String.format(TimeLapseConstants.FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(TimeLapseConstants.FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        return displayValue.replace(',', '.');
    }

    public void changePictureEffectValue(String lastSelectedItemID) {
        if (MOVIE_NOT_SUPPORT_ARRAY_LIST.contains(lastSelectedItemID)) {
            if (PE_HAVING_OPTION_VALUES_LIST.contains(lastSelectedItemID)) {
                updatePictureEffectOnCamera(lastSelectedItemID, true);
            } else {
                updatePictureEffectOnCamera(lastSelectedItemID, false);
            }
        }
    }

    public void changeRemainingPictureEffectValue(String lastSelectedItemID) {
        if ("PictureEffect".equals(lastSelectedItemID)) {
            lastSelectedItemID = "off";
        }
        if (PE_HAVING_OPTION_VALUES_LIST.contains(lastSelectedItemID)) {
            updatePictureEffectOnCamera(lastSelectedItemID, true);
        } else if (lastSelectedItemID.equals(PictureEffectController.MODE_MINIATURE)) {
            updateMiniaturePictureEffect();
        } else {
            updatePictureEffectOnCamera(lastSelectedItemID, false);
        }
    }

    private void updateMiniaturePictureEffect() {
        TimelapsePictureEffectController controller = TimelapsePictureEffectController.getInstance();
        controller.setThemeValue("PictureEffect", PictureEffectController.MODE_MINIATURE);
        String miniatureOption = controller.getValue(PictureEffectController.MODE_MINIATURE);
        if (CameraSetting.getInstance().getCamera() != null) {
            int captureState = TLShootModeSettingController.getInstance().getCurrentCaptureState();
            int currentState = getCurrentState();
            BackUpUtil backup = BackUpUtil.getInstance();
            if (captureState != 1) {
                if ("auto".equals(miniatureOption)) {
                    if (currentState == 7) {
                        backup.setPreference(TimeLapseConstants.IS_INHERITED_MINIATURE_AUTO_CUSTOM, true);
                    } else if (currentState == 5) {
                        miniatureOption = backup.getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME, "hcenter");
                        backup.setPreference(TimeLapseConstants.MINIATURE_PICTURE_EFFECT_KEY, miniatureOption);
                        backup.setPreference(TimeLapseConstants.IS_INHERITED_MINIATURE_AUTO_MINIATURE, true);
                        AppLog.info(this.TAG, "setMiniatureOption(A2): " + miniatureOption);
                    }
                } else if (currentState == 7) {
                    miniatureOption = backup.getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME, "hcenter");
                } else if (currentState == 5) {
                    miniatureOption = backup.getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME, "hcenter");
                    AppLog.info(this.TAG, "setMiniatureOption(A2): " + miniatureOption);
                }
            } else if (currentState == 7) {
                miniatureOption = backup.getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME_STILL, "auto");
            } else if (currentState == 5) {
                miniatureOption = backup.getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME_STILL, "auto");
                AppLog.info(this.TAG, "setMiniatureOption(A2): " + miniatureOption);
            }
        }
        TimelapsePictureEffectController.getInstance().setThemeValue(PictureEffectController.MODE_MINIATURE, miniatureOption);
    }

    private void updatePictureEffectOnCamera(String pictureEffect, boolean isOptionValueAbailable) {
        TimelapsePictureEffectController.getInstance().setThemeValue("PictureEffect", pictureEffect);
        if (isOptionValueAbailable) {
            this.mPictureEffect = TimelapsePictureEffectController.getInstance().getValue(pictureEffect);
            TimelapsePictureEffectController.getInstance().setThemeValue(pictureEffect, this.mPictureEffect);
        }
    }

    public boolean calculateRemainingMemory() {
        MediaNotificationManager.getInstance().updateRemainingAmount();
        int stillRemainingNumber = MediaNotificationManager.getInstance().getRemaining();
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
            this.totalAvailRemaining = -1;
            try {
                String tag = MovieController.getInstance().getValue(null);
                File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
                if (memoryCard != null) {
                    if (tag.equalsIgnoreCase("MOVIE_1920_1080")) {
                        this.totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 829440);
                    } else {
                        this.totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 368640);
                    }
                }
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        } else if (stillRemainingNumber > 0 && TLShootModeSettingController.getInstance().getCurrentCaptureState() == 2) {
            this.totalAvailRemaining = -1;
            try {
                File memoryCard2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
                long usableSpace = memoryCard2.getUsableSpace();
                long sizeOfStillImage = usableSpace / stillRemainingNumber;
                String tag2 = MovieController.getInstance().getValue(null);
                if (memoryCard2 != null) {
                    if (tag2.equalsIgnoreCase("MOVIE_1920_1080")) {
                        this.totalAvailRemaining = (int) (usableSpace / (829440 + sizeOfStillImage));
                    } else {
                        this.totalAvailRemaining = (int) (usableSpace / (368640 + sizeOfStillImage));
                    }
                }
            } catch (IController.NotSupportedException e2) {
                e2.printStackTrace();
            }
        } else {
            this.totalAvailRemaining = stillRemainingNumber;
        }
        this.totalAvailRemaining = this.totalAvailRemaining < 0 ? 0 : this.totalAvailRemaining;
        if (this.totalAvailRemaining >= this.mShootingNum) {
            return false;
        }
        return true;
    }

    public String getThemeName(Context context) {
        this.selectedThemeName = context.getResources().getString(getThemeNameResID(this.mCurrentState));
        return this.selectedThemeName;
    }

    public boolean isTimelapseLiteApplication() {
        return sTimelapseLiteState;
    }

    public static void setTimelapseLite(boolean isTimelapseLite) {
        sTimelapseLiteState = isTimelapseLite;
    }

    public int getBootFactor() {
        return this.mLaunchBootFactor;
    }

    public void setBootFactor(int factorState) {
        this.mLaunchBootFactor = factorState;
    }

    public boolean isOlderPFVersion() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (2 <= pfMajorVersion && 3 <= CameraSetting.getPfApiVersion()) {
            AppLog.info("PF version", "PF version  is Latest");
            return false;
        }
        AppLog.info("PF version", "PF version  is lower");
        return true;
    }

    public String getFilePathForYuvToRGBConv() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            String filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_pack_yuv2rgb.so";
            return filePath;
        }
        String filePath2 = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_yuv2rgb_musashi_03RGBA.so";
        return filePath2;
    }

    public String getFilePathForSpecialSequence() {
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "Timelapse", "00");
        String filePath = map.getPath();
        return filePath;
    }

    public void setMemoryMapConfiguration() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 2) {
            AppLog.info(this.TAG, "ALLOCATION_POLICY_VERSION_1");
            MemoryMapConfig.setAllocationPolicy(1);
        }
    }

    public boolean isValidThemeForUpdatedOption() {
        if (this.mCurrentState != 3 && this.mCurrentState != 4 && this.mCurrentState != 7) {
            return false;
        }
        return true;
    }

    public int getCautionId() {
        AvailableInfo.update();
        calculateRemainingMemory();
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
            return TimelapseInfo.CAUTION_ID_DLAPP_STEADY_SHOT;
        }
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
            if (1 == com.sony.imaging.app.util.Environment.getVersionOfHW()) {
                return 1399;
            }
            return 3289;
        }
        if (isTestShot()) {
            return 0;
        }
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_F") || AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_P")) {
            int sensorType = FocusAreaController.getInstance().getSensorType();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
            String focusMode = ((Camera.Parameters) p.first).getFocusMode();
            if (sensorType != 1 || !"auto".equals(focusMode) || getInstance().isTestShot()) {
                return 0;
            }
            return 3110;
        }
        if (getInstance().isMediaInhOn()) {
            return 1;
        }
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return TimelapseInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING;
        }
        if (getAvailableRemainingShot() <= 0) {
            return TimelapseInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL;
        }
        if (DataBaseAdapter.getInstance().isDBSizeFull()) {
            return TimelapseInfo.CAUTION_ID_DLAPP_DB_FULL;
        }
        if (isAviFileExist(getAviFilePathName(getPinedCalender()))) {
            return TimelapseInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY;
        }
        if (getAvailableRemainingShot() >= getShootingNum()) {
            return 0;
        }
        return TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS;
    }

    public int getAutoShutterSpeedLowLimit(boolean isPFSuportedLongExposureNR) {
        int autoShutterSpeedLowLimit;
        if (this.interval >= 32) {
            autoShutterSpeedLowLimit = 32000000;
        } else {
            autoShutterSpeedLowLimit = this.interval * 1000000;
        }
        if (isPFSuportedLongExposureNR) {
            if (this.interval == 1) {
                return 999900;
            }
            return autoShutterSpeedLowLimit / 2;
        }
        return autoShutterSpeedLowLimit;
    }

    public void setLowShutterSpeedOFF() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        if (params != null) {
            ((CameraEx.ParametersModifier) params.second).setAutoShutterSpeedLowLimit(0);
            AppLog.info(this.TAG, "ShutterSpeed value setAutoShutterSpeedLowLimit to 0");
            CameraSetting.getInstance().setParameters(params);
        }
    }

    public void setOkFocusedOnRemainingMemoryCaution(boolean isFocusedOk) {
        this.misOkFocusedOnRemainingMemroryCaution = isFocusedOk;
    }

    public boolean isOkFocusedOnRemainingMemroryCaution() {
        return this.misOkFocusedOnRemainingMemroryCaution;
    }

    public TimelapseExposureController.Options setExposureOptionForAESettings(TimelapseExposureController.Options option) {
        int i = 1;
        option.fps = 0;
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            option.fps = getFrameRate();
        }
        AppLog.info(this.TAG, AETRACKINGTAG + "fps = " + option.fps);
        option.intervalTimeMillis = getInterval() * LIMIT_TIME_FOR_LONG_INTERVAL;
        AppLog.info(this.TAG, AETRACKINGTAG + "intervalTimeMillis =" + option.intervalTimeMillis);
        option.intervalPriority = SelfTimerIntervalPriorityController.getInstance().isIntervalPriorityEnabled() ? 1 : 0;
        AppLog.info(this.TAG, AETRACKINGTAG + "intervalPriority  =" + option.intervalPriority);
        option.shootingNum = getShootingNum();
        AppLog.info(this.TAG, AETRACKINGTAG + "shootingNum = " + option.shootingNum);
        switch (this.mAEState) {
            case 1:
                option.autoExposureTracking = 500;
                break;
            case 2:
                option.autoExposureTracking = 300;
                break;
            case 3:
                option.autoExposureTracking = 100;
                break;
            case 4:
                option.autoExposureTracking = TimelapseExposureController.Options.AUTO_EXPOSURE_TRACKING_NO_SMOOTHING;
                break;
            default:
                option.autoExposureTracking = 0;
                break;
        }
        AppLog.info(this.TAG, AETRACKINGTAG + "autoExposureTracking = " + option.autoExposureTracking);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
        if (!((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR()) {
            i = 2;
        } else if (!((CameraEx.ParametersModifier) params.second).getLongExposureNR()) {
            i = 0;
        }
        option.longExposureNR = i;
        AppLog.info(this.TAG, AETRACKINGTAG + "longExposureNR = " + option.longExposureNR);
        return option;
    }

    public boolean isSupportedGetLongExposureNR() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        return 2 <= pfMajorVersion && 2 <= CameraSetting.getPfApiVersion();
    }

    public void setShutterSpeed() {
        int ssLowLimit;
        boolean isIntervalPriorityEnabled = SelfTimerIntervalPriorityController.getInstance().isIntervalPriorityEnabled();
        if (isIntervalPriorityEnabled) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
            if (isSupportedGetLongExposureNR() && true == ((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR()) {
                ssLowLimit = getAutoShutterSpeedLowLimit(((CameraEx.ParametersModifier) params.second).getLongExposureNR());
            } else {
                ssLowLimit = getAutoShutterSpeedLowLimit(false);
            }
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) params2.second).setAutoShutterSpeedLowLimit(ssLowLimit);
            CameraSetting.getInstance().setParameters(params2);
            AppLog.info(this.TAG, "ShutterSpeed value from incrementShutterSpeed();");
        }
    }

    public void resetShutterSpeed() {
        AppLog.info(this.TAG, "ShutterSpeed value from incrementShutterSpeed();");
        CameraSetting.getInstance().getParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setAutoShutterSpeedLowLimit(0);
        CameraSetting.getInstance().setParameters(params);
    }

    public void setFlashModeOff() {
        String flashMode = FlashController.getInstance().getValue(FlashController.FLASHMODE);
        AppLog.info(this.TAG, "setFlashModeOff" + flashMode);
        if (!flashMode.equalsIgnoreCase("off")) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params.first).setFlashMode("off");
            ((CameraEx.ParametersModifier) params.second).setFlashCompensation(0);
            CameraSetting.getInstance().setParameters(params);
        }
    }

    public void setJpegDummyQuality() {
        if (CameraSetting.getInstance().getCamera() != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
                String jpegQuality = null;
                try {
                    jpegQuality = PictureQualityController.getInstance().getValue(null);
                } catch (IController.NotSupportedException e) {
                    e.printStackTrace();
                }
                if (jpegQuality.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_EXTRAFINE)) {
                    ((Camera.Parameters) params.first).setJpegQuality(50);
                    CameraSetting.getInstance().setParameters(params);
                    TimeLapseConstants.isDummySetting = true;
                }
            } else if (TimeLapseConstants.isDummySetting) {
                ((Camera.Parameters) params.first).setJpegQuality(95);
                CameraSetting.getInstance().setParameters(params);
                TimeLapseConstants.isDummySetting = false;
            }
        }
    }

    public boolean isTestShot() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName() + " = " + mTestShot);
        return mTestShot;
    }

    public void setIrisRingSettingByCamera(boolean isEnable) {
        CameraEx cameraEx;
        int type = ScalarProperties.getInt("device.iris.ring.type");
        AppLog.info("Iris Ring", "Iris Type = " + type);
        AppLog.info("Iris Ring", "isEnable = " + isEnable);
        if (type != 0 && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            if (isEnable) {
                cameraEx.enableIrisRing();
                AppLog.info("Iris Ring", "Iris Ring is enable!");
            } else {
                cameraEx.disableIrisRing();
                AppLog.info("Iris Ring", "Iris Ring is disabled!");
            }
        }
    }

    public void setTestShot(boolean testShot) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        mTestShot = testShot;
        AppLog.exit(this.TAG, AppLog.getMethodName() + " = " + mTestShot);
    }

    public void setTargetMedia(int target) {
        mTargetMedia = target;
    }

    public int getTargetMedia() {
        return mTargetMedia;
    }

    public String getTimeString(int val, Layout layout) {
        String secondString = layout.getResources().getString(R.string.STRID_FUNC_TIMELAPSE_INTERVAL_VALUE);
        String minSecString = layout.getResources().getString(R.string.STRID_FUNC_TIMELAPSE_MIN_SEC);
        return val >= 60 ? String.format(minSecString, Integer.valueOf(val / 60), Integer.valueOf(val % 60)) : String.format(secondString, Integer.valueOf(val));
    }

    public boolean isS1On() {
        KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        return key.valid == 1 && key.status == 1;
    }

    public void setDetachedLensStatus(boolean status) {
        this.mDetachedLens = status;
    }

    public boolean isDetachedLens() {
        return this.mDetachedLens;
    }

    public void setActualMediaIds() {
        if (!TimeLapse.isIn_onShutdown || !TimelapseExecutorCreator.isNoShotCaptured) {
            AppLog.info(this.TAG, "setActualMediaIds");
            ExecutorCreator creator = ExecutorCreator.getInstance();
            creator.stableSequence();
            ShootingExecutor executor = (ShootingExecutor) creator.getSequence();
            String[] ids = AvindexStore.getExternalMediaIds();
            if (ids != null && ids[0] != null) {
                executor.setRecordingMedia(ids[0], null);
            }
            creator.updateSequence();
        }
    }

    public void setVirtualMediaIds() {
        AppLog.info(this.TAG, "setVirtualMediaIds");
        UtilPFWorkaround.setVirtualMediaIds(getInstance().isTestShot());
    }

    public void setIsPowerOffDuringCapturing(boolean poweroff) {
        this.isCaneledByPowerOff = poweroff;
    }

    public boolean isPowerOffDuringCaptureing() {
        return this.isCaneledByPowerOff;
    }

    public void setS2_ONFromPlayBack(boolean s2on) {
        this.isS2_ONFromPlayBack = s2on;
    }

    public boolean isS2_ONFromPlayBack() {
        return this.isS2_ONFromPlayBack;
    }

    public int getFrameNumberPerAviFile() {
        if (!"framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            return FRAME_NUMBER_PER_24P_AVI;
        }
        return FRAME_NUMBER_PER_30P_AVI;
    }

    public boolean hasAngleShiftAddon() {
        return this.mHasAngleShiftAddon;
    }

    public void setAngleShiftAddon(boolean hasAngleShiftAddon) {
        AppLog.info(this.TAG, "Has AngleShift AddOn? : " + hasAngleShiftAddon);
        this.mHasAngleShiftAddon = hasAngleShiftAddon;
    }

    public void setAngleShiftBoot(boolean isBootDone) {
        this.isAngleShiftBootDone = isBootDone;
    }

    public boolean isAngleShiftBootDone() {
        return this.isAngleShiftBootDone;
    }

    public void setAngleShiftBootFrom(String bootFromWhere) {
        this.mBootFromWhere = bootFromWhere;
    }

    public String getAngleShiftBootFrom() {
        return this.mBootFromWhere;
    }

    public void prepareStillImageChecking(BaseMenuService service) {
        this.isImageSizeSetting = false;
        this.isImageAspectSetting = false;
        this.isImageQualitySetting = false;
        if (getInstance().hasAngleShiftAddon()) {
            this.isImageSizeSetting = service.getMenuItemId().equalsIgnoreCase(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            if (this.isImageSizeSetting) {
                this.mImageSize = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            }
            this.isImageAspectSetting = service.getMenuItemId().equalsIgnoreCase(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (this.isImageAspectSetting) {
                this.mImageAspect = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            }
            this.isImageQualitySetting = service.getMenuItemId().equalsIgnoreCase(TimeLapseConstants.API_NAME);
            if (this.isImageQualitySetting) {
                try {
                    this.mImageQuality = TLPictureQualityController.getInstance().getValue(null);
                } catch (IController.NotSupportedException e) {
                    this.mImageQuality = null;
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkStillImageSettings() {
        if (this.isImageSizeSetting) {
            String imageSize = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            if (!this.mImageSize.equalsIgnoreCase(imageSize) && getInstance().getCurrentPictureWidth() < 1920) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_SIZE);
                return;
            }
            return;
        }
        if (this.isImageAspectSetting) {
            String imageAspect = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (!this.mImageAspect.equalsIgnoreCase(imageAspect) && !getInstance().isSupportedAspectRatioByAngleShiftAddOn()) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_ASPECT);
                return;
            }
            return;
        }
        if (this.isImageQualitySetting) {
            try {
                String imageQaulity = TLPictureQualityController.getInstance().getValue(null);
                if (!this.mImageQuality.equalsIgnoreCase(imageQaulity) && imageQaulity.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_RAW) && !getInstance().isSupportedRAW()) {
                    CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_SETTING);
                }
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSupportedAspectRatioByAngleShiftAddOn() {
        String aspect = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        return PictureSizeController.ASPECT_16_9.equalsIgnoreCase(aspect) || PictureSizeController.ASPECT_3_2.equalsIgnoreCase(aspect) || PictureSizeController.ASPECT_4_3.equalsIgnoreCase(aspect);
    }

    public int getCurrentPictureWidth() {
        List<ScalarProperties.PictureSize> list = ScalarProperties.getSupportedPictureSizes();
        int index = 0;
        String imageSize = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        if (PictureSizeController.SIZE_VGA.equalsIgnoreCase(imageSize)) {
            index = 12;
        } else {
            String aspect = TLPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (PictureSizeController.ASPECT_3_2.equalsIgnoreCase(aspect)) {
                index = 0;
            } else if (PictureSizeController.ASPECT_16_9.equalsIgnoreCase(aspect)) {
                index = 3;
            } else if (PictureSizeController.ASPECT_4_3.equalsIgnoreCase(aspect)) {
                index = 6;
            } else if (PictureSizeController.ASPECT_1_1.equalsIgnoreCase(aspect)) {
                index = 9;
            } else {
                AppLog.warning(this.TAG, "unknown aspect: " + aspect);
            }
            if (PictureSizeController.SIZE_S.equalsIgnoreCase(imageSize)) {
                index += 2;
            } else if (PictureSizeController.SIZE_M.equalsIgnoreCase(imageSize)) {
                index++;
            }
        }
        if (true == ApscModeController.isSupportedByPf()) {
            boolean isApscModeOn = false;
            if (14 < list.size() && list.get(14).width > 0) {
                try {
                    isApscModeOn = "on".equalsIgnoreCase(ApscModeController.getInstance().getValue(ApscModeController.TAG_APSC_MODE_CONDITION));
                } catch (IController.NotSupportedException e) {
                    e.printStackTrace();
                }
            }
            if (isApscModeOn) {
                int identifierApscOffset = 12 + 1;
                while (identifierApscOffset < list.size() && -1 != list.get(identifierApscOffset).width) {
                    identifierApscOffset++;
                }
                if (index + identifierApscOffset + 1 < list.size()) {
                    index += identifierApscOffset + 1;
                }
            }
        }
        int width = list.get(index).width;
        AppLog.info(this.TAG, "getPictureWidth() index = " + index + ", width = " + width + StringBuilderThreadLocal.PERIOD);
        return width;
    }

    public boolean isMediaInhOn() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isFactor(INH_FACTOR_ID_BAD_BLOCK_OVER, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_WRITE_PROTECTED, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_READ_ONLY, media[0]);
    }

    public boolean isSupportedRAW() {
        String deviceMemory = ScalarProperties.getString("device.memory");
        return UnSupportedRawDeviceList.indexOf(deviceMemory) == -1;
    }

    public void setSavingDoneLayout(Layout savingDoneLayout) {
        mSavingDoneLayout = savingDoneLayout;
    }

    public Layout getSavingDoneLayout() {
        return mSavingDoneLayout;
    }

    public boolean isSilentShutterOn() {
        String valueOfSilentShutter = null;
        try {
            valueOfSilentShutter = SilentShutterController.getInstance().getValue(SilentShutterController.TAG_SILENT_SHUTTER);
        } catch (IController.NotSupportedException e) {
            Log.d(this.TAG, "Silent shutter not supported");
        }
        if (valueOfSilentShutter != null && "on".equals(valueOfSilentShutter)) {
            return true;
        }
        return false;
    }

    public void setTakePictureByRemote(boolean isRemoteCapturedStatus) {
        this.isRemoteCaptured = isRemoteCapturedStatus;
    }

    public boolean isTakePictureByRemote() {
        return this.isRemoteCaptured;
    }

    public boolean isRemoteControlMode() {
        return ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getRemoteControlMode();
    }

    public void setRemoteControlMode(boolean remoteControlMode, Camera camera) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(remoteControlMode);
        camera.setParameters((Camera.Parameters) setParams.first);
        if (remoteControlMode && this.isRemoteCaptured) {
            camera.cancelAutoFocus();
        }
    }

    public boolean canQueryforRecoveryDB() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        return 2 <= pfMajorVersion && 5 <= CameraSetting.getPfApiVersion();
    }

    public void setMaybePhaseDiffFlag(boolean isMaybePhaseDiff) {
        this.mMaybePhaseDiff = isMaybePhaseDiff;
    }

    public boolean maybePhaseDiffFlag() {
        return this.mMaybePhaseDiff;
    }

    public boolean isSupportedVersion(int majorValue, int minorValue) {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        return pfMajorVersion >= majorValue + 1 || (pfMajorVersion == majorValue && pfAPIVersion >= minorValue);
    }
}
