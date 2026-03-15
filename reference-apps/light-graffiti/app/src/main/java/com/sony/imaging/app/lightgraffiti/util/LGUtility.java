package com.sony.imaging.app.lightgraffiti.util;

import android.R;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.AppContext;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.FileHelper;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class LGUtility {
    private static final String FORMAT_BIG_DIGIT = "%.0f\"";
    private static final String FORMAT_ONE_DIGIT = "%.1f\"";
    private static String FORMAT_SMALL_DIGIT = null;
    private static final int STREAK_LEVEL_FULL = 7;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private static final float THRESHODL_ONE_OR_SMALL = 0.4f;
    private static boolean isNeedUpdateSettings;
    private boolean isEVDialRotated;
    private boolean isSelfTimerFinished;
    private boolean misOkFocusedOnRemainingMemroryCaution;
    private String ssValueString;
    private static String TAG = LGUtility.class.getSimpleName();
    private static LGUtility sLightgraffitiUtilityInstance = null;
    private static Object instanceLock = new Object();
    public static Kikilog.Options kikilogOptions = new Kikilog.Options();
    private static boolean isPreTakePicture = false;
    private static CopyOnWriteArrayList<String> sShutterSpeedValueList = new CopyOnWriteArrayList<>();
    private static boolean isAfterProgress = false;
    public static int PLAY_DISP_MODE_UNINTIALIZED = -1;
    private static int mPlayDispMode = PLAY_DISP_MODE_UNINTIALIZED;
    public static int[] ISO_DARK_NIGHT = {IntervalRecExecutor.INTVL_REC_INITIALIZED, 250, 320, 400, 500, AppRoot.USER_KEYCODE.WATER_HOUSING, 800, 1000, 1250, 1600, 2000, 2500, 3200};
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
    private boolean mIsLauncherBoot = false;
    private boolean mIsEEStateBoot = false;
    private boolean mIsCustomThemeBoot = false;
    private boolean mIsMenuBoot = false;
    private boolean isPlaybackPressed = false;
    private boolean isCaptureStatus = false;
    private boolean isFromPlayback = false;
    private int mCurrentPosi = -1;
    private boolean mShutterSpeedUninitialized = true;
    public boolean isLensAttachEventReady = false;
    public boolean isFirstTimeLaunchCareStarted = false;

    public int getAvailableRemainingShot() {
        return this.totalAvailRemaining;
    }

    public int getCautionId() {
        int cautionID = 0;
        AvailableInfo.update();
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            if (!MediaNotificationManager.getInstance().isMounted()) {
                cautionID = LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING;
            } else if (getAvailableRemainingShot() <= 0) {
                cautionID = LGInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL;
            }
        }
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
            return LGInfo.CAUTION_ID_STRID_CAU_TIMELAPSE_LENS_STEADYSHOT;
        }
        return cautionID;
    }

    public void createShutterSpeedValuesList() {
        if (sShutterSpeedValueList != null && sShutterSpeedValueList.size() < 56) {
            sShutterSpeedValueList.clear();
            sShutterSpeedValueList.add(LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_30SEC);
            sShutterSpeedValueList.add("25\"");
            sShutterSpeedValueList.add(LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_20SEC);
            sShutterSpeedValueList.add("15\"");
            sShutterSpeedValueList.add("13\"");
            sShutterSpeedValueList.add(LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_10SEC);
            sShutterSpeedValueList.add("8\"");
            sShutterSpeedValueList.add("6\"");
            sShutterSpeedValueList.add(LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_5SEC);
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

    private LGUtility() {
        isPreTakePicture = false;
        isNeedUpdateSettings = true;
        createShutterSpeedValuesList();
    }

    public static LGUtility getInstance() {
        LGUtility lGUtility;
        synchronized (instanceLock) {
            AppLog.info(TAG, "getInstance()");
            if (sLightgraffitiUtilityInstance == null) {
                sLightgraffitiUtilityInstance = new LGUtility();
                FORMAT_SMALL_DIGIT = AppContext.getAppContext().getResources().getString(R.string.restr_pin_create_pin);
            }
            lGUtility = sLightgraffitiUtilityInstance;
        }
        return lGUtility;
    }

    public static void destroyInstance() {
        synchronized (instanceLock) {
            sLightgraffitiUtilityInstance = null;
        }
    }

    public synchronized void setRecommanedSS(String value) {
        AppLog.enter(TAG, "setRecommanedSS value = " + value);
        if (this.mCurrentPosi == -1) {
            updateCurrentPosition();
        }
        int targetPosi = getRecommendedPosition(value);
        AppLog.info(TAG, "targetPosi=" + targetPosi + ", currentPosi=" + this.mCurrentPosi);
        if (this.mCurrentPosi != targetPosi) {
            if (this.mCurrentPosi > targetPosi) {
                decrementSSValue(this.mCurrentPosi - targetPosi);
            } else {
                incrementSSValue(targetPosi - this.mCurrentPosi);
            }
        }
        this.mCurrentPosi = targetPosi;
        AppLog.exit(TAG, "setRecommanedSS value = " + value);
    }

    private void incrementSSValue(int position) {
        AppLog.info(TAG, "incrementSSValue position = " + position);
        CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        cameraEx.adjustShutterSpeed(position);
    }

    private void decrementSSValue(int position) {
        AppLog.info(TAG, "decrementSSValue position = " + position);
        CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        cameraEx.adjustShutterSpeed(-position);
    }

    public void updateCurrentPosition() {
        AppLog.enter(TAG, "updateCurrentPosition");
        updateSSValue();
        AppLog.exit(TAG, "updateCurrentPosition");
        this.mCurrentPosi = sShutterSpeedValueList.indexOf(getSSValue());
    }

    private int getRecommendedPosition(String value) {
        return sShutterSpeedValueList.indexOf(value);
    }

    private void setValue(String displayValue) {
        AppLog.enter(TAG, "setValue displayValue:" + displayValue);
        this.ssValueString = displayValue;
        AppLog.exit(TAG, "setValue");
    }

    private String getSSValue() {
        return this.ssValueString;
    }

    public static int getPlayDispMode() {
        return mPlayDispMode;
    }

    public static void setPlayDispMode(int playDispMode) {
        mPlayDispMode = playDispMode;
    }

    public static void clearPlayDispMode() {
        mPlayDispMode = PLAY_DISP_MODE_UNINTIALIZED;
    }

    public boolean isShutterSpeedUninitialized() {
        Log.d(TAG, "isShutterSpeedUninitialized : " + this.mShutterSpeedUninitialized);
        return this.mShutterSpeedUninitialized;
    }

    public void setShutterSpeedUninitialized(boolean shutterSpeedUninitialized) {
        Log.d(TAG, "setShutterSpeedUninitialized : " + this.mShutterSpeedUninitialized + "->" + shutterSpeedUninitialized);
        this.mShutterSpeedUninitialized = shutterSpeedUninitialized;
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
        int index = filePath.indexOf(".");
        return filePath.substring(index - 8, index) + ".AVI";
    }

    public String getTimeFormat(PlainCalendar calendar) {
        int hour = calendar.hour;
        int minute = calendar.minute;
        int second = calendar.second;
        return (hour < 10 ? ISOSensitivityController.ISO_AUTO : "") + hour + (minute < 10 ? ISOSensitivityController.ISO_AUTO : "") + minute + (second < 10 ? ISOSensitivityController.ISO_AUTO : "") + second;
    }

    public String getAviFoldername(String filePath) {
        int index = filePath.indexOf(".");
        return filePath.substring(index - 15, index - 9);
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
        int index = path.indexOf(".");
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

    public void setCaptureStatus(boolean captureStatus) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCaptureStatus = captureStatus;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @SuppressLint({"DefaultLocale"})
    private void updateSSValue() {
        String displayValue;
        AppLog.enter(TAG, "updateSSValue");
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss == null) {
            AppLog.exit(TAG, "updateSSValue : ss is null");
            return;
        }
        float value = 0.0f;
        if (((Integer) ss.second).intValue() != 0) {
            float value2 = ((Integer) ss.first).intValue() / ((Integer) ss.second).intValue();
            BigDecimal bi = new BigDecimal(String.valueOf(value2));
            value = bi.setScale(1, 4).floatValue();
        }
        AppLog.info(TAG, "ss.first: " + ss.first + "ss.second: " + ss.second);
        if (value < THRESHODL_ONE_OR_SMALL) {
            displayValue = String.format(FORMAT_SMALL_DIGIT, ss.first, ss.second);
        } else {
            displayValue = (value >= THRESHODL_BIG_OR_ONE || ((float) ((Integer) ss.second).intValue()) == 1.0f) ? "" + String.format(FORMAT_BIG_DIGIT, Float.valueOf(value)) : String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
        }
        setValue(displayValue);
        AppLog.exit(TAG, "updateSSValue");
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

    public boolean isFirstTimeOpen() {
        String stage = LGStateHolder.getInstance().getShootingStage();
        boolean ret = false;
        if (stage.equals(LGStateHolder.SHOOTING_1ST)) {
            ret = BackUpUtil.getInstance().getPreferenceBoolean(LGConstants.BACKUP_KEY_1ST_INTRODUCTION_LAYOUT_OPEN_KEY, false);
        } else if (stage.equals(LGStateHolder.SHOOTING_2ND)) {
            ret = BackUpUtil.getInstance().getPreferenceBoolean(LGConstants.BACKUP_KEY_2ND_INTRODUCTION_LAYOUT_OPEN_KEY, false);
        } else if (LGStateHolder.getInstance().isShootingStage3rd()) {
            ret = BackUpUtil.getInstance().getPreferenceBoolean(LGConstants.BACKUP_KEY_3RD_INTRODUCTION_LAYOUT_OPEN_KEY, false);
        }
        return !ret;
    }

    public int getScaledOptimazeImageSizeX() {
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                return AppRoot.USER_KEYCODE.WATER_HOUSING;
            }
            return 1024;
        }
        Log.e(TAG, "getScaledOptimazeImageSizeX failed . because getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO) can not acquired.");
        Log.e(TAG, "[CAUTION]getScaledOptimazeImageSizeX for the time being returned 1024 , but there it is not be the appropriate value .");
        return 1024;
    }

    public int getScaledOptimazeImageSizeY() {
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                return AppRoot.USER_KEYCODE.WATER_HOUSING;
            }
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                return 684;
            }
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                return 576;
            }
            return 768;
        }
        Log.e(TAG, "getScaledOptimazeImageSizeY failed . because getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO) can not acquired.");
        Log.e(TAG, "[CAUTION]getScaledOptimazeImageSizeY for the time being returned 768 , but there it is not be the appropriate value .");
        return 768;
    }

    public void calculateRemainingMemory() {
        MediaNotificationManager.getInstance().updateRemainingAmount();
        if (MediaNotificationManager.getInstance().getRemaining() != -1) {
            this.totalAvailRemaining = MediaNotificationManager.getInstance().getRemaining();
        }
    }

    public boolean isAfterProgress() {
        return isAfterProgress;
    }

    public void setAfterProgress(boolean flg) {
        isAfterProgress = flg;
    }

    public void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    public void outputKikilog(Integer kikilogId) {
        Log.d(TAG, AppLog.getMethodName() + " : kikilogId=0x" + Integer.toHexString(kikilogId.intValue()));
        Kikilog.Options options = kikilogOptions;
        kikilogOptions.getClass();
        options.recType = 32;
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), kikilogOptions);
        }
    }

    public boolean isModelDSC() {
        int modelCategory = ScalarProperties.getInt("model.category");
        return modelCategory == 2 || modelCategory == 2;
    }
}
