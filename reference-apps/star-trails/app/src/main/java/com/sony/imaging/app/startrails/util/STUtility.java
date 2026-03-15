package com.sony.imaging.app.startrails.util;

import android.R;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.avi.util.MediaFileHelper;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.UtilPFWorkaround;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.menu.controller.MovieController;
import com.sony.imaging.app.startrails.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.FileHelper;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class STUtility {
    private static final int BRIGHT_NIGHT_SS = 5;
    private static final int DARK_NIGHT_SS = 10;
    private static final String FORMAT_BIG_DIGIT = "%.0f\"";
    private static final String FORMAT_ONE_DIGIT = "%.1f\"";
    private static String FORMAT_SMALL_DIGIT = null;
    private static final int STREAK_LEVEL_FULL = 7;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private static final float THRESHODL_ONE_OR_SMALL = 0.4f;
    private static boolean isNeedUpdateSettings;
    private boolean isEVDialRotated;
    private boolean isSelfTimerFinished;
    private int mCurrentShutterSpeed;
    private int mLastTrail;
    private boolean misOkFocusedOnRemainingMemroryCaution;
    private String ssValueString;
    private static String TAG = "STUtility";
    private static STUtility sSTUtilityInstance = null;
    private static boolean isPreTakePicture = false;
    private static CopyOnWriteArrayList<String> sShutterSpeedValueList = new CopyOnWriteArrayList<>();
    public static int[] ISO_DARK_NIGHT = {IntervalRecExecutor.INTVL_REC_INITIALIZED, 250, 320, 400, 500, 640, 800, 1000, 1250, 1600, 2000, STConstants.CAPTURE_DELAY_TIME, 3200};
    public static boolean isSettingsApplied = false;
    private final int BYTE_IMAGE_SIZE = 33554432;
    private final int MOVIE_SIZE_FHD = 829440;
    private final int MOVIE_SIZE_HD = 368640;
    private int totalAvailRemaining = 0;
    private final String[] MONTH_NAME = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private PlainCalendar calendar = null;
    private String hours = "";
    private String minutes = "";
    private String amPm = "";
    private String year = "";
    private String month = "";
    private String day = "";
    private int mCurrentTrail = -1;
    private boolean mIsLauncherBoot = false;
    private boolean mIsEEStateBoot = false;
    private boolean mIsCustomThemeBoot = false;
    private boolean mIsMenuBoot = false;
    private boolean isPlaybackPressed = false;
    private boolean isCaptureStatus = false;
    private int THEME_BRIGHT_NIGHT_DFAULT_SHUTTER = 5;
    private int THEME_DARK_NIGHT_DFAULT_SHUTTER = 10;
    private boolean isFromPlayback = false;
    private boolean mDetachedLens = false;

    public void createShutterSpeedValuesList() {
        if (sShutterSpeedValueList != null && sShutterSpeedValueList.size() < 61) {
            sShutterSpeedValueList.clear();
            sShutterSpeedValueList.add("30\"");
            sShutterSpeedValueList.add("25\"");
            sShutterSpeedValueList.add("20\"");
            sShutterSpeedValueList.add("15\"");
            sShutterSpeedValueList.add("13\"");
            sShutterSpeedValueList.add(STConstants.DEFAULT_VALUE_SHUTTER_SPEED_DARK_NIGHT);
            sShutterSpeedValueList.add("8\"");
            sShutterSpeedValueList.add("6\"");
            sShutterSpeedValueList.add(STConstants.DEFAULT_VALUE_SHUTTER_SPEED);
            sShutterSpeedValueList.add("4\"");
            sShutterSpeedValueList.add("3.2\"");
            sShutterSpeedValueList.add("2.5\"");
            sShutterSpeedValueList.add("2\"");
            sShutterSpeedValueList.add("1.6\"");
            sShutterSpeedValueList.add("1.3\"");
            sShutterSpeedValueList.add("1\"");
            sShutterSpeedValueList.add("0.8\"");
            sShutterSpeedValueList.add("0.6\"");
            sShutterSpeedValueList.add("0.5\"");
            sShutterSpeedValueList.add("0.4\"");
            sShutterSpeedValueList.add("1/3");
            sShutterSpeedValueList.add("1/4");
            sShutterSpeedValueList.add("1/5");
            sShutterSpeedValueList.add("1/6");
            sShutterSpeedValueList.add("1/8");
            sShutterSpeedValueList.add("1/10");
            sShutterSpeedValueList.add("1/13");
            sShutterSpeedValueList.add("1/15");
            sShutterSpeedValueList.add("1/20");
            sShutterSpeedValueList.add("1/25");
            sShutterSpeedValueList.add("1/30");
            sShutterSpeedValueList.add("1/40");
            sShutterSpeedValueList.add("1/50");
            sShutterSpeedValueList.add("1/60");
            sShutterSpeedValueList.add("1/80");
            sShutterSpeedValueList.add("1/100");
            sShutterSpeedValueList.add("1/125");
            sShutterSpeedValueList.add("1/160");
            sShutterSpeedValueList.add("1/200");
            sShutterSpeedValueList.add("1/250");
            sShutterSpeedValueList.add("1/320");
            sShutterSpeedValueList.add("1/400");
            sShutterSpeedValueList.add("1/500");
            sShutterSpeedValueList.add("1/640");
            sShutterSpeedValueList.add("1/800");
            sShutterSpeedValueList.add("1/1000");
            sShutterSpeedValueList.add("1/1250");
            sShutterSpeedValueList.add("1/1600");
            sShutterSpeedValueList.add("1/2000");
            sShutterSpeedValueList.add("1/2500");
            sShutterSpeedValueList.add("1/3200");
            sShutterSpeedValueList.add("1/4000");
            sShutterSpeedValueList.add("1/5000");
            sShutterSpeedValueList.add("1/6400");
            sShutterSpeedValueList.add("1/8000");
            sShutterSpeedValueList.add("1/10000");
            sShutterSpeedValueList.add("1/12800");
            sShutterSpeedValueList.add("1/16000");
            sShutterSpeedValueList.add("1/20000");
            sShutterSpeedValueList.add("1/25600");
            sShutterSpeedValueList.add("1/32000");
        }
    }

    private STUtility() {
        isPreTakePicture = false;
        isNeedUpdateSettings = true;
        createShutterSpeedValuesList();
    }

    public static STUtility getInstance() {
        AppLog.info(TAG, "getInstance()");
        if (sSTUtilityInstance == null) {
            sSTUtilityInstance = new STUtility();
            FORMAT_SMALL_DIGIT = AppContext.getAppContext().getResources().getString(R.string.restr_pin_create_pin);
        }
        return sSTUtilityInstance;
    }

    public boolean isPreTakePictureTestShot() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName() + " = " + isPreTakePicture);
        return isPreTakePicture;
    }

    public void setPreTakePictureTestShot(boolean preTakePicture) {
        isPreTakePicture = preTakePicture;
    }

    public void setIsLauncherBoot(boolean isLauncherBoot) {
        this.mIsLauncherBoot = isLauncherBoot;
    }

    public boolean isLauncherBoot() {
        return this.mIsLauncherBoot;
    }

    public boolean isAppTopBooted() {
        return this.mIsMenuBoot;
    }

    public void setMenuBoot(boolean isBooting) {
        AppLog.info(TAG, "setMenuBoot status " + isBooting);
        this.mIsMenuBoot = isBooting;
        setIsLauncherBoot(isBooting);
    }

    public boolean isCustomThemeBooted() {
        this.mIsCustomThemeBoot = BackUpUtil.getInstance().getPreferenceBoolean(STBackUpKey.CUSTOM_MINIMUM_APERTURE_VALUE_KEY, true);
        return this.mIsCustomThemeBoot;
    }

    public void setCustomThemeBooted(boolean isCustomThemeBoot) {
        BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_MINIMUM_APERTURE_VALUE_KEY, Boolean.valueOf(isCustomThemeBoot));
        this.mIsCustomThemeBoot = isCustomThemeBoot;
    }

    public boolean isEEStateBoot() {
        return this.mIsEEStateBoot;
    }

    public void setIsEEStateBoot(boolean EEStateBoot) {
        this.mIsEEStateBoot = EEStateBoot;
    }

    public void setEVDialRotated(boolean isEVDialRotated) {
        this.isEVDialRotated = isEVDialRotated;
    }

    public boolean isEVDialRotated() {
        return this.isEVDialRotated;
    }

    public int getCurrentTrail() {
        String value;
        if (-1 == this.mCurrentTrail && (value = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.SELECTED_THEME, ThemeSelectionController.BRIGHT_NIGHT)) != null) {
            switch (value.charAt(0)) {
                case 'b':
                    this.mCurrentTrail = 0;
                    break;
                case 'c':
                    this.mCurrentTrail = 2;
                    break;
                case 'd':
                    this.mCurrentTrail = 1;
                    break;
            }
        }
        return this.mCurrentTrail;
    }

    public void setCurrentTrail(int selectedTrail) {
        this.mCurrentTrail = selectedTrail;
        themeSwitchKikiLog();
    }

    private void themeSwitchKikiLog() {
        Integer kikilogId;
        AppLog.enter(TAG, AppLog.getMethodName());
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer.valueOf(4141);
        switch (this.mCurrentTrail) {
            case 1:
                kikilogId = 4240;
                break;
            case 2:
                kikilogId = 4241;
                break;
            default:
                kikilogId = 4239;
                break;
        }
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setOkFocusedOnRemainingMemoryCaution(boolean isFocusedOk) {
        this.misOkFocusedOnRemainingMemroryCaution = isFocusedOk;
    }

    public boolean isOkFocusedOnRemainingMemroryCaution() {
        return this.misOkFocusedOnRemainingMemroryCaution;
    }

    public boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }

    public void setFlashModeOff() {
        AppLog.enter(TAG, "setFlashModeOff()");
        String value = FlashController.getInstance().getValue();
        if (!value.equals("off")) {
            CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            Camera mCamera = mCameraEx.getNormalCamera();
            Camera.Parameters params = mCameraEx.createEmptyParameters();
            params.setFlashMode("on");
            mCamera.setParameters(params);
            Camera.Parameters params2 = mCameraEx.createEmptyParameters();
            params2.setFlashMode("off");
            mCamera.setParameters(params2);
        }
        AppLog.exit(TAG, "setFlashModeOff()");
    }

    public PlainCalendar pinCalendar() {
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
        String filePath = aviDirectory + "/" + fileName;
        return filePath;
    }

    public String getTimeFormat(PlainCalendar calendar) {
        int hour = calendar.hour;
        int minute = calendar.minute;
        int second = calendar.second;
        return (hour < 10 ? ISOSensitivityController.ISO_AUTO : "") + hour + (minute < 10 ? ISOSensitivityController.ISO_AUTO : "") + minute + (second < 10 ? ISOSensitivityController.ISO_AUTO : "") + second;
    }

    public String getAviFoldername(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        return filePath.substring(index - 15, index - 9);
    }

    private String getAviFolderName(PlainCalendar calendar) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String mediaPath = getFilePathOnMedia();
        String date = getDateFormat(calendar);
        String aviDirectory = mediaPath + "/" + date.substring(2, date.length());
        AppLog.exit(TAG, AppLog.getMethodName());
        return aviDirectory;
    }

    public String getFilePathForSpecialSequence() {
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "Startrails", "00");
        String filePath = map.getPath();
        return filePath;
    }

    public void setMemoryMapConfiguration() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 2) {
            AppLog.info("kamata", "ALLOCATION_POLICY_VERSION_1");
            MemoryMapConfig.setAllocationPolicy(1);
        }
    }

    public String getFilePathOnMedia() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            if (2 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + STConstants.MS_CARD_PATH;
            } else if (1 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + STConstants.SD_CARD_PATH;
            }
            AppLog.trace("YES", "========== Files path on Media regarding this application ============== " + mFilePathOnMedia);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mFilePathOnMedia;
    }

    public String getDateFormat(PlainCalendar calendar2) {
        int year = calendar2.year;
        int month = calendar2.month;
        int day = calendar2.day;
        return (year < 10 ? ISOSensitivityController.ISO_AUTO : "") + year + (month < 10 ? ISOSensitivityController.ISO_AUTO : "") + month + (day < 10 ? ISOSensitivityController.ISO_AUTO : "") + day;
    }

    public Bitmap getThumbnailFilename(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        String thmPath = filePath.substring(0, index) + ".THM";
        Bitmap mThumbnailBitmap = BitmapFactory.decodeFile(thmPath);
        return mThumbnailBitmap;
    }

    public boolean createFolder(PlainCalendar calendar) {
        AppLog.enter(TAG, AppLog.getMethodName());
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
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @SuppressLint({"SimpleDateFormat"})
    public String getRecordingTime(int sec) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Calendar cal = Calendar.getInstance();
        cal.clear(11);
        cal.clear(12);
        cal.clear(13);
        cal.set(11, sec / 3600);
        cal.set(12, (sec % 3600) / 60);
        cal.set(13, (sec % 3600) % 60);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String str = sdf.format(cal.getTime());
        AppLog.trace(TAG, "getRecordingTime: " + str);
        AppLog.exit(TAG, AppLog.getMethodName());
        return str;
    }

    public long getUTCTime() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Calendar cal = Calendar.getInstance();
        cal.clear();
        PlainCalendar pc = TimeUtil.getCurrentCalendar();
        cal.set(pc.year, pc.month - 1, pc.day, pc.hour, pc.minute, pc.second);
        AppLog.trace(TAG, "Local Time: " + cal.getTimeInMillis());
        AppLog.trace(TAG, "Hour: " + cal.get(10) + "Minute: " + cal.get(12));
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diffGMT = p.gmtDiff;
        int diffSummerTime = p.summerTimeDiff;
        AppLog.trace(TAG, "SummerTime: " + (-diffSummerTime));
        AppLog.trace(TAG, "Diff: " + (-diffGMT));
        cal.add(12, -diffGMT);
        cal.add(12, -diffSummerTime);
        AppLog.trace(TAG, "Universal Time: " + cal.getTimeInMillis());
        AppLog.trace(TAG, "Hour: " + cal.get(10) + "Minute: " + cal.get(12));
        AppLog.exit(TAG, AppLog.getMethodName());
        return cal.getTimeInMillis();
    }

    public String getFormattedTime(String time) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String newTime = "01:10 AM";
        if (time != null) {
            this.hours = time.substring(0, 2);
            this.minutes = time.substring(2, 4);
            int hourValue = Integer.parseInt(this.hours);
            if (hourValue < 12) {
                this.amPm = "AM";
            } else {
                this.amPm = "PM";
                int newHour = hourValue - 12;
                if (newHour < 10) {
                    this.hours = ISOSensitivityController.ISO_AUTO + newHour;
                } else {
                    this.hours = "" + newHour;
                }
            }
            newTime = this.hours + ":" + this.minutes + ExposureModeController.SOFT_SNAP + this.amPm;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return newTime;
    }

    public String getFormatteddate(String date) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String newDate = "";
        if (date != null) {
            this.year = date.substring(0, 4);
            this.month = getMonthName(date.substring(4, 6));
            this.day = date.substring(6);
            newDate = this.month + "-" + this.day + "-" + this.year;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return newDate;
    }

    public String getMonthName(String month) {
        return this.MONTH_NAME[Integer.parseInt(month) - 1];
    }

    public void deleteThumbnailFile(String path) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int index = path.indexOf(StringBuilderThreadLocal.PERIOD);
        String str1 = path.substring(0, index) + ".THM";
        boolean isDeleted = false;
        File dir = new File(str1);
        if (FileHelper.exists(dir)) {
            isDeleted = dir.delete();
        }
        if (!isDeleted) {
            AppLog.trace(TAG, "Thumnail file cannot be deleted");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isAviFileExist(String filePath) {
        File dir = new File(filePath);
        return FileHelper.exists(dir);
    }

    public int getCautionId() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int cautionID = 0;
        DatabaseUtil.MediaStatus mediaStatus = MediaFileHelper.checkMediaStatus();
        AvailableInfo.update();
        calculateRemainingMemory();
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
            cautionID = STInfo.CAUTION_ID_DLAPP_STEADY_SHOT;
        } else if (AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
            if (com.sony.imaging.app.util.Environment.getVersionOfHW() == 1) {
                cautionID = 1399;
            } else {
                cautionID = 3289;
            }
            setCaptureStatus(true);
        } else {
            if (isPreTakePictureTestShot()) {
                return 0;
            }
            if (mediaStatus == DatabaseUtil.MediaStatus.READ_ONLY) {
                cautionID = 2247;
            } else if (mediaStatus == DatabaseUtil.MediaStatus.NO_CARD) {
                cautionID = STInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING;
            } else if (getAvailableRemainingShot() <= 0) {
                cautionID = STInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL;
            } else if (DataBaseAdapter.getInstance().isDBSizeFull()) {
                cautionID = STInfo.CAUTION_ID_DLAPP_DB_FULL;
            } else if (isAviFileExist(getAviFilePathName(getPinedCalender()))) {
                cautionID = STInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY;
            } else if (getAvailableRemainingShot() < ThemeParameterSettingUtility.getInstance().getNumberOfShot()) {
                cautionID = STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return cautionID;
    }

    public boolean isMediaLocked() {
        DatabaseUtil.MediaStatus mediaStatus = MediaFileHelper.checkMediaStatus();
        if (mediaStatus != DatabaseUtil.MediaStatus.ERROR && mediaStatus != DatabaseUtil.MediaStatus.NO_CARD && mediaStatus != DatabaseUtil.MediaStatus.READ_ONLY) {
            return false;
        }
        return true;
    }

    public int getThemeNameResID(int theme) {
        int[] resIDs = {com.sony.imaging.app.startrails.R.string.STRID_FUNC_STRLS_THEME_BRIGHT_NIGHT, com.sony.imaging.app.startrails.R.string.STRID_FUNC_STRLS_THEME_DARK_NIGHT, com.sony.imaging.app.startrails.R.string.STRID_AMC_STR_01036};
        return resIDs[theme];
    }

    public void calculateRemainingMemory() {
        AppLog.enter(TAG, AppLog.getMethodName());
        MediaNotificationManager.getInstance().updateRemainingAmount();
        this.totalAvailRemaining = MediaNotificationManager.getInstance().getRemaining();
        try {
            String tag = MovieController.getInstance().getValue(null);
            File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
            if (memoryCard != null) {
                if (tag.equalsIgnoreCase(STConstants.MOVIE_1920_1080)) {
                    this.totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 829440);
                } else {
                    this.totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 368640);
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        this.totalAvailRemaining = this.totalAvailRemaining < 0 ? 0 : this.totalAvailRemaining;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getAvailableRemainingShot() {
        if (isMediaLocked()) {
            this.totalAvailRemaining = 0;
        }
        return this.totalAvailRemaining;
    }

    public boolean isCapturingStarted() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isCapturingStarted = STConstants.sCaptureImageCounter > 0;
        AppLog.enter(TAG, AppLog.getMethodName() + " = " + isCapturingStarted);
        return isCapturingStarted;
    }

    public void setPlayBackKeyPressed(boolean playbackKeyStatus) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isPlaybackPressed = playbackKeyStatus;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isPlayBackKeyPressed() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName() + "  = " + this.isPlaybackPressed);
        return this.isPlaybackPressed;
    }

    public void setDetachedLensStatus(boolean status) {
        this.mDetachedLens = status;
    }

    public boolean isDetachedLens() {
        return this.mDetachedLens;
    }

    public void updateWhiteBalanceValue(String value) {
        AppLog.enter(TAG, "updateCustomWhiteBalanceValue");
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String optValue = (("" + param.getLightBalance()) + "/" + param.getColorComp()) + "/" + param.getColorTemp();
        switch (this.mCurrentTrail) {
            case 0:
                if (value != null) {
                    BackUpUtil.getInstance().setPreference(STBackUpKey.BRIGHTNIGHT_WB_KEY, value);
                }
                BackUpUtil.getInstance().setPreference(STBackUpKey.BRIGHT_WB_KEY_OPTION_VALUE, optValue);
                AppLog.trace(TAG, "CUSTOM_WB_OPTION_VALUE  " + optValue);
                break;
            case 1:
                if (value != null) {
                    BackUpUtil.getInstance().setPreference(STBackUpKey.DARKNIGHT_WB_KEY, value);
                }
                BackUpUtil.getInstance().setPreference(STBackUpKey.DARKNIGHT_WB_KEY_OPTION_VALUE, optValue);
                AppLog.trace(TAG, "CUSTOM_WB_OPTION_VALUE  " + optValue);
                break;
            default:
                if (value != null) {
                    BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_WB_KEY, value);
                }
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_WB_KEY_OPTION_VALUE, optValue);
                AppLog.trace(TAG, "CUSTOM_WB_OPTION_VALUE  " + optValue);
                break;
        }
        AppLog.exit(TAG, "updateCustomWhiteBalanceValue");
    }

    public int getShutterSpeed() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentShutterSpeed = this.mCurrentShutterSpeed >= 1 ? this.mCurrentShutterSpeed : 1;
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentShutterSpeed;
    }

    public void setShutterSpeed(int shutterspeed) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (shutterspeed == 0) {
            this.mCurrentShutterSpeed = 1;
        } else {
            this.mCurrentShutterSpeed = shutterspeed;
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    public String getTotleRecordingTime() {
        int totleTime;
        AppLog.enter(TAG, AppLog.getMethodName());
        ThemeParameterSettingUtility.getInstance().getNumberOfShot();
        if (STConstants.sCaptureImageCounter >= 1) {
            totleTime = this.mCurrentShutterSpeed * (ThemeParameterSettingUtility.getInstance().getNumberOfShot() - STConstants.sCaptureImageCounter);
        } else {
            totleTime = this.mCurrentShutterSpeed * ThemeParameterSettingUtility.getInstance().getNumberOfShot();
        }
        String recTime = getRecordingTime(totleTime);
        AppLog.exit(TAG, AppLog.getMethodName() + " Totle recording time = " + recTime);
        return recTime;
    }

    public String getFilePathForYuvToRGBConv() {
        String filePath;
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_pack_yuv2rgb.so";
        } else {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_yuv2rgb_musashi_03RGBA.so";
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    public String getFilePathForSA() {
        String filePath;
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_startrail_still_beta_avip.so";
        } else {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_startrail_still_beta_musashi.so";
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    public String getFilePathForStarTrailsSALib() {
        String filePath;
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 1) {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_startrail_still_beta_avip.so";
        } else {
            filePath = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/libsa_startrail_still_beta_musashi.so";
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    public void setCaptureStatus(boolean captureStatus) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCaptureStatus = captureStatus;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean getCaptureStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.isCaptureStatus;
    }

    public CharSequence calculateShootingTime(int numberOfShot) {
        int currentShutterSpeed;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (getCurrentTrail() == 0) {
            currentShutterSpeed = this.THEME_BRIGHT_NIGHT_DFAULT_SHUTTER;
        } else if (this.mCurrentTrail == this.mLastTrail && 1 == getCurrentTrail()) {
            currentShutterSpeed = getShutterSpeed();
        } else if (1 == getCurrentTrail()) {
            currentShutterSpeed = this.THEME_DARK_NIGHT_DFAULT_SHUTTER;
        } else {
            currentShutterSpeed = getCurrentShutterSpeed();
        }
        String recTime = getInstance().getRecordingTime(currentShutterSpeed * numberOfShot);
        AppLog.exit(TAG, AppLog.getMethodName());
        return recTime;
    }

    private int getCurrentShutterSpeed() {
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        float value = 1.0f;
        if (ss != null && ((Integer) ss.second).intValue() != 0) {
            BigDecimal bi = new BigDecimal(String.valueOf(((Integer) ss.first).intValue() / ((Integer) ss.second).intValue()));
            value = bi.setScale(1, 4).floatValue();
        }
        AppLog.info(TAG, "" + value);
        if (value < 1.0f) {
            value = 1.0f;
        }
        int ssValue = (int) value;
        return ssValue;
    }

    public CharSequence getStreakValue(int streakLevelValue) {
        if (streakLevelValue >= 7) {
            CharSequence strStreakLevelValue = AppContext.getAppContext().getResources().getString(com.sony.imaging.app.startrails.R.string.STRID_FUNC_STRS_OPTION_STREAKS_FULL);
            return strStreakLevelValue;
        }
        CharSequence strStreakLevelValue2 = String.valueOf(streakLevelValue);
        return strStreakLevelValue2;
    }

    public void updateBackValue() {
        int value = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        switch (getInstance().getCurrentTrail()) {
            case 1:
                BackUpUtil.getInstance().setPreference(STBackUpKey.DARK_NIGHT_EXPO_COMP_VALUE_KEY, Integer.valueOf(value));
                return;
            case 2:
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_EXPOSURE_COMPENSATION_KEY, Integer.valueOf(value));
                return;
            default:
                BackUpUtil.getInstance().setPreference(STBackUpKey.BRIGHT_NIGHT_EXPO_COMP_VALUE_KEY, Integer.valueOf(value));
                return;
        }
    }

    public void updateExposureMode() {
        switch (getCurrentTrail()) {
            case 1:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MANUAL_MODE);
                return;
            case 2:
                ThemeRecomandedMenuSetting.getInstance().updateExposureModeForCustomTheme();
                return;
            default:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
                return;
        }
    }

    public void setUpdateThemeProperty(boolean isRequire) {
        AppLog.info(TAG, "setUpdateThemeProperty " + isRequire);
        isNeedUpdateSettings = isRequire;
    }

    public boolean isUpdateThemePropertyRequired() {
        AppLog.info(TAG, "setUpdateThemeProperty " + isNeedUpdateSettings);
        return isNeedUpdateSettings;
    }

    public void setVirtualMediaIds() {
        AppLog.info(TAG, "setVirtualMediaIds");
        UtilPFWorkaround.setVirtualMediaIds(isPreTakePictureTestShot());
    }

    public void setActualMediaIds() {
        ExecutorCreator creator = ExecutorCreator.getInstance();
        creator.stableSequence();
        ShootingExecutor executor = (ShootingExecutor) creator.getSequence();
        String[] ids = AvindexStore.getExternalMediaIds();
        if (ids != null && ids[0] != null) {
            executor.setRecordingMedia(ids[0], null);
        }
        creator.updateSequence();
    }

    public synchronized void setRecommanedSS(String value) {
        int target = getRecommendedPosition(value);
        int ssPosi = getCurrentPosition();
        if (ssPosi > target) {
            decrementSSValue(ssPosi - target);
        } else {
            incrementSSValue(target - ssPosi);
        }
    }

    private void incrementSSValue(int position) {
        CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        cameraEx.adjustShutterSpeed(position);
    }

    private void decrementSSValue(int position) {
        CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        cameraEx.adjustShutterSpeed(-position);
    }

    private int getCurrentPosition() {
        updateSSValue();
        return sShutterSpeedValueList.indexOf(getSSValue());
    }

    private int getRecommendedPosition(String value) {
        return sShutterSpeedValueList.indexOf(value);
    }

    @SuppressLint({"DefaultLocale"})
    private void updateSSValue() {
        String displayValue;
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss != null) {
            float value = STConstants.INVALID_APERTURE_VALUE;
            if (((Integer) ss.second).intValue() != 0) {
                float value2 = ((Integer) ss.first).intValue() / ((Integer) ss.second).intValue();
                BigDecimal bi = new BigDecimal(String.valueOf(value2));
                value = bi.setScale(1, 4).floatValue();
            }
            if (value < THRESHODL_ONE_OR_SMALL) {
                displayValue = String.format(FORMAT_SMALL_DIGIT, ss.first, ss.second);
            } else {
                displayValue = (value >= 10.0f || ((float) ((Integer) ss.second).intValue()) == 1.0f) ? "" + String.format(FORMAT_BIG_DIGIT, Float.valueOf(value)) : String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
            }
            setValue(displayValue);
        }
    }

    private void setValue(String displayValue) {
        this.ssValueString = displayValue;
    }

    private String getSSValue() {
        return this.ssValueString;
    }

    public int getLastTrail() {
        return this.mLastTrail;
    }

    public void setLastTrail(int mLastTrail) {
        this.mLastTrail = mLastTrail;
    }

    public void releaseBitmapList() {
        Log.d(TAG, "releaseBitmapList() sFrameBitmapList " + STConstants.sFrameBitmapList);
        if (STConstants.sFrameBitmapList != null) {
            int size = STConstants.sFrameBitmapList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bmp = STConstants.sFrameBitmapList.get(i);
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                }
            }
            STConstants.sFrameBitmapList.clear();
        }
    }

    public void setSelfTimerStatus(boolean selfTimerStatus) {
        this.isSelfTimerFinished = selfTimerStatus;
    }

    public boolean isSelfTimerProcessing() {
        return this.isSelfTimerFinished;
    }

    public boolean isFromPlayback() {
        return this.isFromPlayback;
    }

    public void setFromPlayback(boolean fromPlayback) {
        this.isFromPlayback = fromPlayback;
    }

    public void setAperture() {
        String setApertureValue = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_APERTURE_KEY, STConstants.CUSTOM_APERTURE);
        CameraSetting camSetting = CameraSetting.getInstance();
        String apertureValue = getApertureValues(camSetting.getApertureInfo());
        setExactAperture(camSetting, setApertureValue, apertureValue);
    }

    void setExactAperture(CameraSetting camSetting, String setApertureValue, String apertureValue) {
        if (!apertureValue.equalsIgnoreCase(STConstants.INVALID_APERTURE_STRING) && !apertureValue.equalsIgnoreCase(setApertureValue)) {
            int curruntApertureposition = 0;
            int expectedApertureposition = 0;
            if (STConstants.APERTURE_VALUE_LIST.contains(apertureValue)) {
                curruntApertureposition = STConstants.APERTURE_VALUE_LIST.indexOf(apertureValue);
            }
            if (STConstants.APERTURE_VALUE_LIST.contains(setApertureValue)) {
                expectedApertureposition = STConstants.APERTURE_VALUE_LIST.indexOf(setApertureValue);
            }
            camSetting.getCamera().adjustAperture(expectedApertureposition - curruntApertureposition);
        }
    }

    public String getApertureValues(CameraEx.ApertureInfo info) {
        if (info == null) {
            return STConstants.INVALID_APERTURE_STRING;
        }
        int currentAperture = info.currentAperture;
        String convertedAperture = convertApertureValueFormat(currentAperture / 100.0f);
        return convertedAperture;
    }

    private String convertApertureValueFormat(float value) {
        String displayValue;
        if (value == STConstants.INVALID_APERTURE_VALUE) {
            displayValue = STConstants.INVALID_APERTURE_STRING;
        } else if (value < 10.0f) {
            displayValue = String.format(STConstants.FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(STConstants.FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        return displayValue.replace(',', '.');
    }

    private static void disableIrisRing() {
        CameraEx cameraEx;
        int type = ScalarProperties.getInt("device.iris.ring.type");
        Log.d("Iris Ring", "Iris Type = " + type);
        if (type != 0 && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            cameraEx.disableIrisRing();
            Log.d("Iris Ring", "Iris Ring is disabled!");
        }
    }

    public static void enableIrisRing() {
        CameraEx cameraEx;
        int type = ScalarProperties.getInt("device.iris.ring.type");
        Log.d("Iris Ring", "Iris Type = " + type);
        if (type != 0 && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            cameraEx.enableIrisRing();
            Log.d("Iris Ring", "Iris Ring is enable");
        }
    }

    public static void checkIRISState() {
        if (getInstance().getCurrentTrail() == 1) {
            disableIrisRing();
        } else {
            enableIrisRing();
        }
    }

    public void updateTrails() {
        setLastTrail(this.mCurrentTrail);
    }

    public boolean isSilenShutterOFF() {
        String valueOfSilentShutter = null;
        try {
            valueOfSilentShutter = SilentShutterController.getInstance().getValue(SilentShutterController.TAG_SILENT_SHUTTER);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        if (valueOfSilentShutter != null && "on".equals(valueOfSilentShutter)) {
            return false;
        }
        return true;
    }

    public void setDarkNightIsoBackupNone() {
        BackUpUtil.getInstance().setPreference(STBackUpKey.DARK_NIGHT_ISO_VALUE_KEY, "none");
    }

    public boolean isApplySettingFinish() {
        if (getCurrentTrail() == 0 && !isSettingsApplied) {
            int ss = getShutterSpeed();
            AppLog.trace(TAG, "isShootingAllowed() ss= " + ss);
            if (ss == 5) {
                isSettingsApplied = true;
            } else {
                isSettingsApplied = false;
            }
        } else {
            isSettingsApplied = true;
        }
        return isSettingsApplied;
    }
}
