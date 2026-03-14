package com.sony.imaging.app.digitalfilter.common;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.shooting.ChangeAperture;
import com.sony.imaging.app.digitalfilter.shooting.ChangeSs;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.GFExecutorCreator;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFMeteringController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.FileHelper;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import com.sony.scalar.sysutil.didep.Settings;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Calendar;

/* loaded from: classes.dex */
public class GFCommonUtil {
    private static final int BATTERY_PREEND = 0;
    public static final int DUMMY_BORDER = 0;
    public static final int FRAME_NUMBER_PER_24P_AVI = 1440;
    public static final int FRAME_NUMBER_PER_30P_AVI = 1800;
    private static final String INH_FACTOR_ID_BAD_BLOCK_OVER = "INH_FACTOR_BAD_BLOCK_WRITE_PROTECT";
    private static final String INH_FACTOR_ID_READ_ONLY = "INH_FACTOR_READ_ONLY_MEDIA";
    private static final String INH_FACTOR_ID_WRITE_PROTECTED = "INH_FACTOR_MEDIA_WRITE_PROTECT";
    private static final int LIMITED_MODEL_ID = 10;
    private static final String LIMITED_MODEL_PREFIX = "DSC-RX";
    public static final int MAIN_BORDER = 0;
    public static final boolean NO_POLLING_APERTURE_CHANGE = false;
    public static final boolean POLLING_APERTURE_CHANGE = true;
    private static final String PROP_MODEL_NAME = "model.name";
    public static final int SUB1_BORDER = 1;
    private static boolean isMenuOpened;
    private static final String TAG = AppLog.getClassName();
    private static GFCommonUtil mInstance = null;
    private static boolean mPushedIR2SecKey = false;
    private static int mPFMajorVersion = 0;
    private static int mPFAPIVersion = 0;
    private static boolean hasIrisRing = false;
    private static boolean isDSC = false;
    private static boolean isAVIP = false;
    private static boolean mTransitionByS1 = false;
    private static boolean mAdjustmentStateSetting = false;
    private static boolean isThemeSelected = false;
    private static String mPrevMenuItemId = null;
    private static String mPrevArea = GFSettingMenuLayout.LAND_ID;
    private static boolean mTransitionFromWBAdjustment = false;
    private static boolean mDuringSelfTimer = false;
    private static int mPreviewButtonPosition = 0;
    private static boolean show3rdAreaSettingGuide = true;
    public static boolean showIrisLensMessage = false;
    private static boolean mNeedLimitedByLensSpec = false;
    private static boolean mRX10 = false;
    private static boolean mRX100 = false;
    private static boolean isInvalidShutter = false;
    private static boolean isCanceledByLensStatus = false;
    private static boolean isValidFlashCheck = true;
    private static boolean needActualMediaSetting = false;
    private static PlainCalendar mCalendar = null;
    private static int mFileNumber = 0;
    private static int mLaunchBootFactor = 2;
    private static int mBorderId = 0;
    private static boolean mIsLand = false;
    private static boolean mIsSky = false;
    private static boolean mIsLayer3 = false;
    private static boolean mLayerSettingMenu = false;
    private static boolean isVerticalLinkExecuted = false;
    private boolean mMaybePhaseDiff = false;
    private int mCameraSettingsLayer = 0;
    private String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private boolean misOkFocusedOnRemainingMemroryCaution = false;

    protected GFCommonUtil() {
        String version = ScalarProperties.getString("version.platform");
        mPFMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        mPFAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        int type = ScalarProperties.getInt("device.iris.ring.type");
        hasIrisRing = type != 0;
        int modelCategory = ScalarProperties.getInt("model.category");
        isDSC = modelCategory == 2 || modelCategory == 2;
        isAVIP = isSupportedVersion(2, 0) ? false : true;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "mPFMajorVersion : ").append(mPFMajorVersion).append(", mPFAPIVersion : ").append(mPFAPIVersion);
        AppLog.info(TAG, builder.toString());
        builder.replace(0, builder.length(), "hasIrisRing : ").append(hasIrisRing);
        AppLog.info(TAG, builder.toString());
        builder.replace(0, builder.length(), "isDSC : ").append(isDSC);
        AppLog.info(TAG, builder.toString());
        builder.replace(0, builder.length(), "isAVIP : ").append(isAVIP);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    public static GFCommonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new GFCommonUtil();
        }
        return mInstance;
    }

    public int getCautionId() {
        int cautionID = 0;
        AvailableInfo.update();
        boolean isInterval = GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE));
        if (isBatteryPreEnd()) {
            cautionID = GFInfo.CAUTION_ID_DLAPP_NOBATTERY;
        } else if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
            cautionID = GFInfo.CAUTION_ID_DLAPP_STEADY_SHOT;
        } else if (AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
            cautionID = 1 == Environment.getVersionOfHW() ? 1399 : 3289;
        } else if (isMediaInhOn()) {
            cautionID = 1;
        } else if (!MediaNotificationManager.getInstance().isMounted()) {
            cautionID = GFInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING;
        } else if (MediaNotificationManager.getInstance().getRemaining() < getNumberOfShooting()) {
            cautionID = !AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING") ? GFInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE : 1410;
        } else if (MediaNotificationManager.getInstance().getRemaining() <= (getNumberOfShooting() * 2) - 1 && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
            cautionID = 1410;
        } else if (AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_F") || AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_P")) {
            int sensorType = FocusAreaController.getInstance().getSensorType();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
            String focusMode = ((Camera.Parameters) p.first).getFocusMode();
            if (sensorType == 1 && "auto".equals(focusMode)) {
                cautionID = 3110;
            }
        } else if (isInterval) {
            if (isAviFileExist(getAviFilePathName(getPinedCalender()))) {
                cautionID = GFInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY;
            } else if (getAvailableRemainingShot() < getNumberOfIntervalShooting()) {
                cautionID = getAvailableRemainingShot() <= 0 ? GFInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE : GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS;
            }
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "getCautionId : ").append(cautionID);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return cautionID;
    }

    public int getNumberOfShooting() {
        if (GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE))) {
            return 1;
        }
        if (!GFFilterSetController.THREE_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
            return 3;
        }
        return 4;
    }

    public int getNumberOfIntervalShooting() {
        int shots = GFIntervalController.getInstance().getIntValue(GFIntervalController.INTERVAL_SHOTS);
        return shots * getNumberOfShooting();
    }

    public boolean isMediaInhOn() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isFactor(INH_FACTOR_ID_BAD_BLOCK_OVER, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_WRITE_PROTECTED, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_READ_ONLY, media[0]);
    }

    public boolean isSupportedVersion(int majorVersion, int apiVersion) {
        return mPFMajorVersion >= majorVersion + 1 || (mPFMajorVersion == majorVersion && mPFAPIVersion >= apiVersion);
    }

    public boolean isOneDial() {
        KeyStatus dial1Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL1_STATUS);
        KeyStatus dial2Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL2_STATUS);
        KeyStatus dial3Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL3_STATUS);
        return dial1Status.valid == 1 && dial2Status.valid == 0 && dial3Status.valid == 0;
    }

    public boolean isTwoDial() {
        KeyStatus dial1Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL1_STATUS);
        KeyStatus dial2Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL2_STATUS);
        KeyStatus dial3Status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL3_STATUS);
        return dial1Status.valid == 1 && dial2Status.valid == 1 && dial3Status.valid == 0;
    }

    public void setMaybePhaseDiffFlag(boolean isMaybePhaseDiff) {
        this.mMaybePhaseDiff = isMaybePhaseDiff;
    }

    public boolean maybePhaseDiffFlag() {
        return this.mMaybePhaseDiff;
    }

    public void setPushedIR2SecKey(boolean isPushed) {
        mPushedIR2SecKey = isPushed;
    }

    public boolean isPushedIR2SecKey() {
        return mPushedIR2SecKey;
    }

    public boolean disableShootingOnPlayback() {
        return !GFExecutorCreator.isEnableShootingFromPlayback();
    }

    public void setHoldKey() {
        if (getInstance().disableShootingOnPlayback()) {
            AppLog.info(TAG, "setHoldKey : HOLDMODE_THINOUT");
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 1);
            HoldKeyServer.holdKey(517, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S2_ON, 1);
        }
    }

    public void clearHoldKey() {
        if (getInstance().disableShootingOnPlayback()) {
            AppLog.info(TAG, "clearHoldKey : CLEAR_HOLD");
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 3);
            HoldKeyServer.holdKey(517, 3);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S2_ON, 3);
        }
    }

    public void setS1_ONfromPlayback(boolean isS1) {
        mTransitionByS1 = isS1;
    }

    public boolean enableS1_ONfromPlayback() {
        return (disableShootingOnPlayback() && mTransitionByS1) ? false : true;
    }

    public boolean hasIrisRing() {
        return hasIrisRing;
    }

    public boolean isFixedAperture() {
        boolean isSupportedFixedAperture = isSupportedVersion(2, 7);
        CameraSetting.getInstance().invalidateLensInfoCache();
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        if (info == null) {
            return false;
        }
        if (isSupportedFixedAperture) {
            boolean fixedAperture = info.FixedAperture;
            return fixedAperture;
        }
        CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
        return apertureInfo != null && apertureInfo.currentAvailableMax == apertureInfo.currentAvailableMin;
    }

    public boolean isMaxAperture() {
        CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
        return info != null && info.currentAperture == info.currentAvailableMax;
    }

    public boolean isMinAperture() {
        CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
        return info != null && info.currentAperture == info.currentAvailableMin;
    }

    public boolean isMaxMinAperture() {
        CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
        return info != null && (info.currentAperture == info.currentAvailableMax || info.currentAperture == info.currentAvailableMin);
    }

    public boolean isDSC() {
        return isDSC;
    }

    public boolean isAVIP() {
        return isAVIP;
    }

    public boolean isManual() {
        return ExposureModeController.MANUAL_MODE.equals(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE));
    }

    public boolean isAperture() {
        return "Aperture".equals(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE));
    }

    public void setIrisRingSettingByCamera(boolean isEnable) {
        CameraEx cameraEx;
        AppLog.info("Iris Ring", "isEnable = " + isEnable);
        if (hasIrisRing() && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            if (isEnable) {
                cameraEx.enableIrisRing();
                AppLog.info("Iris Ring", "Iris Ring is enable!");
            } else {
                cameraEx.disableIrisRing();
                AppLog.info("Iris Ring", "Iris Ring is disabled!");
            }
        }
    }

    public void setAutoWhiteBalanceLock(boolean isLock) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
        if (params != null && ((CameraEx.ParametersModifier) params.second).isAutoWhiteBalanceLockSupported() && (emptyParams = CameraSetting.getInstance().getEmptyParameters()) != null) {
            ((CameraEx.ParametersModifier) emptyParams.second).setAutoWhiteBalanceLock(isLock);
            CameraSetting.getInstance().setParameters(emptyParams);
        }
    }

    public boolean getAutoWhiteBalanceLock() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        boolean isLock = ((CameraEx.ParametersModifier) params.second).getAutoWhiteBalanceLock();
        return isLock;
    }

    public void setActualMediaIds(CameraEx cameraEx, CameraEx.RecordingMediaChangeCallback callback) {
        Log.d(TAG, "setActualMediaIds");
        String[] ids = AvindexStore.getExternalMediaIds();
        cameraEx.setRecordingMedia(ids[0], callback);
    }

    public void setVirtualMediaIds() {
        Log.d(TAG, "setVirtualMediaIds");
        ShootingExecutor executor = (ShootingExecutor) ExecutorCreator.getInstance().getSequence();
        String[] ids = AvindexStore.getVirtualMediaIds();
        executor.setRecordingMedia(ids[0], null);
    }

    public void setVirtualMediaIds(CameraEx cameraEx, CameraEx.RecordingMediaChangeCallback callback) {
        Log.d(TAG, "setVirtualMediaIds");
        String[] ids = AvindexStore.getVirtualMediaIds();
        cameraEx.setRecordingMedia(ids[0], callback);
    }

    public void startAdjustmentSetting() {
        mAdjustmentStateSetting = true;
    }

    public void stopAdjustmentSetting() {
        mAdjustmentStateSetting = false;
    }

    public boolean isAdjustmentSetting() {
        return mAdjustmentStateSetting;
    }

    public boolean isThemeSelected() {
        return isThemeSelected;
    }

    public void setThemeSelectedFlag(boolean flag) {
        isThemeSelected = flag;
    }

    public void setPrevMenuItemId(String itemId) {
        mPrevMenuItemId = itemId;
    }

    public String getPrevMenuItemId() {
        return mPrevMenuItemId;
    }

    public void setPrevArea(String area) {
        mPrevArea = area;
    }

    public String getPrevArea() {
        return mPrevArea;
    }

    public void setCommonCameraSettings() {
        if (!ModeDialDetector.hasModeDial()) {
            setExpMode();
        }
        setFlashMode();
        setFlashComp();
        setMeteringMode();
        setDRO();
        setCreativeStyle();
    }

    public boolean isEnableFlash() {
        boolean isFlashInternalEnable = CameraSetting.getInstance().getFlashInternalEnable();
        boolean isFlashExternalEnable = CameraSetting.getInstance().getFlashExternalEnable();
        return isFlashInternalEnable || isFlashExternalEnable;
    }

    public void setCameraSettings(int layer, boolean isPollingAperture) {
        boolean isAEL = AELController.getInstance().getAELockButtonState();
        if (!isAEL) {
            GFAELController.getInstance().holdAELockWithoutCaution(true);
        }
        if (hasIrisRing() && isManual()) {
            setApertureAndShutterSpeed(layer);
        } else {
            if (!isFixedAperture()) {
                setAperture(layer, isPollingAperture);
            }
            setShutterSpeed(layer);
        }
        setIso(layer);
        setEvComp(layer);
        setWhiteBalance(layer);
        if (!isAEL) {
            GFAELController.getInstance().holdAELockWithoutCaution(false);
        }
    }

    public void setCameraSettingsLayerDuringShots(int layer) {
        this.mCameraSettingsLayer = layer;
    }

    public int getCameraSettingsLayerDuringShots() {
        return this.mCameraSettingsLayer;
    }

    public void setEECameraSettings() {
        if (GFEEAreaController.getInstance().isLand()) {
            getInstance().setBaseCameraSettings(true);
        } else if (GFEEAreaController.getInstance().isSky()) {
            getInstance().setFilterCameraSettings(true);
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            getInstance().setLayer3CameraSettings(true);
        }
    }

    public void setBaseCameraSettings(boolean isPollingAperture) {
        setCameraSettings(0, isPollingAperture);
        setBorderId(0);
    }

    public void setFilterCameraSettings(boolean isPollingAperture) {
        setCameraSettings(1, isPollingAperture);
        setBorderId(0);
    }

    public void setLayer3CameraSettings(boolean isPollingAperture) {
        setCameraSettings(2, isPollingAperture);
        setBorderId(1);
    }

    public void setExpMode() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        GFExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, params.getExpMode());
    }

    public void setFlashMode() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        FlashController.getInstance().setValue(FlashController.FLASHMODE, params.getFlashMode());
    }

    public void setFlashComp() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        FlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, params.getFlashComp());
    }

    public void setMeteringMode() {
        String value;
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String itemID = params.getMeteringMode();
        MeteringController.getInstance().setValue("MeteringMode", itemID);
        if (itemID.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT) && (value = GFMeteringController.getInstance().getValue(itemID)) != null) {
            GFMeteringController.getInstance().setValue(itemID, value);
        }
    }

    public void setDRO() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String value = params.getDRO();
        if ("off".equalsIgnoreCase(value)) {
            DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
        } else {
            DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, DROAutoHDRController.MENU_ITEM_ID_DRO);
            DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DRO, value);
        }
    }

    public void setCreativeStyle() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        CreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, params.getCreativeStyle());
        CreativeStyleController.CreativeStyleOptions options = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        String[] optArray = params.getCreativeStyleOption().split("/");
        int contrast = Integer.parseInt(optArray[0]);
        int saturation = Integer.parseInt(optArray[1]);
        int shapness = Integer.parseInt(optArray[2]);
        options.set(contrast, saturation, shapness);
        CreativeStyleController.getInstance().setDetailValue(options);
    }

    public void setAperture(int layer, boolean needPolling) {
        if (isManual() || isAperture()) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(layer));
            if (step != 0) {
                new ChangeAperture().execute(step, params.getAperture(layer), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.digitalfilter.common.GFCommonUtil.1
                    @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeAperture.Callback
                    public void cbFunction() {
                    }
                }, needPolling);
            }
        }
    }

    public void setApertureRetry(final int layer) {
        if (isManual() || isAperture()) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(layer));
            if (step != 0) {
                new ChangeAperture().execute(step, params.getAperture(layer), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.digitalfilter.common.GFCommonUtil.2
                    @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeAperture.Callback
                    public void cbFunction() {
                        GFCommonUtil.this.setAperture(layer, false);
                    }
                }, true);
            }
        }
    }

    public void setShutterSpeed(int layer) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ChangeSs.executeWithoutCallBack(params.getSSNumerator(layer), params.getSSDenominator(layer));
    }

    public void setApertureAndShutterSpeed(final int layer) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(layer));
        new ChangeAperture().execute(step, params.getAperture(layer), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.digitalfilter.common.GFCommonUtil.3
            @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeAperture.Callback
            public void cbFunction() {
                GFCommonUtil.this.setShutterSpeed(layer);
            }
        }, true);
    }

    public void setIso(int layer) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, params.getISO(layer));
    }

    public void setEvComp(int layer) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ExposureCompensationController.getInstance().setValue("ExposureCompensation", params.getExposureComp(layer));
    }

    public void setWhiteBalance(int layer) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String wbMode = params.getWBMode(layer);
        WhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, wbMode);
        WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String[] optArray = params.getWBOption(layer).split("/");
        int light = Integer.parseInt(optArray[0]);
        int comp = Integer.parseInt(optArray[1]);
        int temp = Integer.parseInt(optArray[2]);
        options.setLightBalance(light);
        options.setColorComp(comp);
        if (!WhiteBalanceController.CUSTOM.equalsIgnoreCase(wbMode) && !"custom1".equalsIgnoreCase(wbMode) && !"custom2".equalsIgnoreCase(wbMode) && !"custom3".equalsIgnoreCase(wbMode)) {
            options.setColorTemp(temp);
        }
        WhiteBalanceController.getInstance().setDetailValue(options);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.WB_DETAIL_CHANGE);
    }

    public void resetParameters() {
        String theme = GFThemeController.getInstance().getValue();
        GFBackUpKey.getInstance().resetCSOptions(theme);
        GFBackUpKey.getInstance().resetWBOptions(theme);
        GFBackUpKey.getInstance().resetMeteringSpotValue(theme);
        GFBackUpKey.getInstance().resetDeviceDirection(theme);
        GFBackUpKey.getInstance().resetLinkValue(theme);
        GFBackUpKey.getInstance().resetIntervalValue(theme);
        GFBackUpKey.getInstance().resetEEArea(theme);
        GFBackUpKey.getInstance().resetShootingOrder(theme);
        GFBackUpKey.getInstance().resetFilterSetValue(theme);
        GFBackUpKey.getInstance().resetPositionLinkValue(theme);
        GFBackUpKey.getInstance().resetShadingLinkValue(theme);
        String defaultParams = GFEffectParameters.getInstance().getParameters().getDefaultFlatten();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.unflatten(defaultParams);
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTempAWB()) {
            GFBackUpKey.getInstance().limitToAWB();
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            GFBackUpKey.getInstance().limitToCTemp();
        }
        if (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS)) {
            GFSettingMenuLayout.disable3rdArea();
        }
        GFEffectParameters.getInstance().setParameters(params);
        GFBackUpKey.getInstance().saveLastParameters(params.flatten());
    }

    public void setGraduationSeed(DeviceBuffer devbuffer, short[] graduationSeed) {
        ByteBuffer saTable = SaUtil.getSaParam(graduationSeed.length * 2);
        for (short table : graduationSeed) {
            saTable.putShort(table);
        }
        devbuffer.write(saTable);
    }

    public void setTransitionFromWBAdjustment(boolean isWBAdjustment) {
        mTransitionFromWBAdjustment = isWBAdjustment;
    }

    public boolean isTransitionFromWBAdjustment() {
        return mTransitionFromWBAdjustment;
    }

    public String getSignedInteger(int value) {
        if (value > 0) {
            String signedSymbol = AppContext.getAppContext().getResources().getString(17041715);
            String signedValue = String.format(signedSymbol, Integer.valueOf(value));
            return signedValue;
        }
        if (value == 0) {
            String signedValue2 = AppContext.getAppContext().getResources().getString(17041844);
            return signedValue2;
        }
        String signedSymbol2 = AppContext.getAppContext().getResources().getString(R.string.resolver_work_tab_accessibility);
        String signedValue3 = String.format(signedSymbol2, Integer.valueOf(-value));
        return signedValue3;
    }

    public void setDuringSelfTimer(boolean isSelfTimer) {
        mDuringSelfTimer = isSelfTimer;
    }

    public boolean duringSelfTimer() {
        return mDuringSelfTimer;
    }

    public void updateAppName() {
        AppNameView.setText(getThemeName() + ExposureModeController.SOFT_SNAP);
    }

    public String getThemeName() {
        int nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TBLUESKY;
        String value = GFThemeController.getInstance().getValue(null);
        if (GFThemeController.BLUESKY.equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TBLUESKY;
        } else if ("sunset".equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TSUNSET;
        } else if ("standard".equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TGRADUATEDND;
        } else if ("custom1".equalsIgnoreCase(value)) {
            nameId = R.string.capability_title_canTakeScreenshot;
        } else if ("custom2".equalsIgnoreCase(value)) {
            nameId = R.string.capital_off;
        } else if (GFThemeController.REVERSE.equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TREVERSEGND;
        } else if (GFThemeController.STRIPE.equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TSTRIPE2;
        }
        return AppContext.getAppContext().getResources().getString(nameId);
    }

    public void setMenuTitleText(TextView titleText, BaseMenuService service) {
        titleText.setText(service.getMenuItemText(service.getMenuItemId()));
    }

    public void setPreivewButtonPostion(int position) {
        mPreviewButtonPosition = position;
    }

    public int getPreivewButtonPostion() {
        return mPreviewButtonPosition;
    }

    public void setShow3rdAreaSettingFlag(boolean showGuide) {
        show3rdAreaSettingGuide = showGuide;
    }

    public boolean needShow3rdAreaSettingGuide() {
        return show3rdAreaSettingGuide;
    }

    public void setShowIrisLensMessage(boolean showGuide) {
        showIrisLensMessage = showGuide;
    }

    public boolean needShowIrisLensMessage() {
        return showIrisLensMessage;
    }

    public boolean isReversedDisplay() {
        DisplayManager.DeviceStatus deviceStatus;
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        return disp != null && (deviceStatus = disp.getActiveDeviceStatus()) != null && disp.getActiveDevice() == 0 && 1 == deviceStatus.viewPattern;
    }

    public void checkModelName() {
        mNeedLimitedByLensSpec = false;
        mRX10 = false;
        mRX100 = false;
        String modelName = ScalarProperties.getString(PROP_MODEL_NAME);
        Log.d(TAG, "avoidLensDistortion(modelName)=" + modelName);
        try {
            if (modelName.startsWith(LIMITED_MODEL_PREFIX)) {
                String[] digits = modelName.split("\\D+");
                if (digits.length > 0) {
                    int id = Integer.parseInt(digits[1]);
                    if (id >= 10) {
                        mNeedLimitedByLensSpec = true;
                    }
                    if (id == 10) {
                        mRX10 = true;
                    }
                    if (id == 100) {
                        mRX100 = true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "avoidLensDistortion() cannot parse model name.");
        }
    }

    public boolean avoidLensDistortion() {
        return mNeedLimitedByLensSpec;
    }

    public boolean isImager1inch() {
        return mNeedLimitedByLensSpec;
    }

    public boolean isRX10() {
        return mRX10;
    }

    public boolean isRX100() {
        return mRX100;
    }

    public boolean isTriggerMfAssist(int code) {
        int[] keys = {code};
        int[] functions = Settings.getKeyFunction(keys);
        return functions != null && 61 == functions[0] && FocusModeController.MANUAL.equalsIgnoreCase(FocusModeController.getInstance().getValue());
    }

    public int setFocusModeByDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            return 1;
        }
        return 1;
    }

    public boolean isBatteryPreEnd() {
        int level = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
        return level == 0 && plugged != 1;
    }

    public void setInvalidShutter(boolean isPushedS1) {
        isInvalidShutter = isPushedS1;
    }

    public boolean isInvalidShutter() {
        return isInvalidShutter;
    }

    public void clearCancelByLensChangingFlag() {
        isCanceledByLensStatus = false;
    }

    public void cancelByLensChanging() {
        isCanceledByLensStatus = true;
    }

    public boolean isCancelByLensChanging() {
        return isCanceledByLensStatus;
    }

    public void validFlashCheck() {
        isValidFlashCheck = true;
    }

    public void inValidFlashCheck() {
        isValidFlashCheck = false;
    }

    public boolean isValidFlashCheck() {
        return isValidFlashCheck;
    }

    public void openMenu() {
        isMenuOpened = true;
    }

    public void closeMenu() {
        isMenuOpened = false;
    }

    public boolean isMenuOpened() {
        return isMenuOpened;
    }

    public void setActualMediaStatus(boolean isActualMedia) {
        needActualMediaSetting = !isActualMedia;
    }

    public boolean needActualMediaSetting() {
        return needActualMediaSetting;
    }

    public PlainCalendar pinCalendar() {
        mFileNumber = 0;
        PlainCalendar currentCalendar = TimeUtil.getCurrentCalendar();
        mCalendar = currentCalendar;
        return currentCalendar;
    }

    public PlainCalendar getPinedCalender() {
        return mCalendar;
    }

    public String getDateFormat(PlainCalendar calendar) {
        int year = calendar.year;
        int month = calendar.month;
        int day = calendar.day;
        return (year < 10 ? ISOSensitivityController.ISO_AUTO : "") + year + (month < 10 ? ISOSensitivityController.ISO_AUTO : "") + month + (day < 10 ? ISOSensitivityController.ISO_AUTO : "") + day;
    }

    public String getTimeFormat(PlainCalendar calendar) {
        int hour = calendar.hour;
        int minute = calendar.minute;
        int second = calendar.second;
        return (hour < 10 ? ISOSensitivityController.ISO_AUTO : "") + hour + (minute < 10 ? ISOSensitivityController.ISO_AUTO : "") + minute + (second < 10 ? ISOSensitivityController.ISO_AUTO : "") + second;
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

    public String getAviFilePathNameSequel() {
        mFileNumber++;
        String sFileNumber = String.format("%02d", Integer.valueOf(mFileNumber));
        String fileName = getTimeFormat(getPinedCalender()) + sFileNumber + ".AVI";
        String aviDirectory = getAviFolderName(getPinedCalender());
        String filePath = aviDirectory + "/" + fileName;
        return filePath;
    }

    public String getAviFoldername(String filePath) {
        int index = filePath.indexOf(StringBuilderThreadLocal.PERIOD);
        return filePath.substring(index - 15, index - 9);
    }

    public String getFilePathOnMedia() {
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            if (2 == mMediaId) {
                mFilePathOnMedia = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + GFConstants.MS_CARD_PATH;
            } else if (1 == mMediaId) {
                mFilePathOnMedia = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + GFConstants.SD_CARD_PATH;
            }
            AppLog.trace("YES", "========== Files path on Media regarding this application ============== " + mFilePathOnMedia);
        }
        return mFilePathOnMedia;
    }

    private String getAviFolderName(PlainCalendar calendar) {
        String mediaPath = getFilePathOnMedia();
        String date = getDateFormat(calendar);
        String aviDirectory = mediaPath + "/" + date.substring(2, date.length());
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

    public String getFormattedTime(String time) {
        String amPm;
        if (time == null) {
            return "01:10 AM";
        }
        String hours = time.substring(0, 2);
        String minutes = time.substring(2, 4);
        int hourValue = Integer.parseInt(hours);
        if (hourValue < 12) {
            amPm = "AM";
        } else {
            amPm = "PM";
            int newHour = hourValue - 12;
            if (newHour < 10) {
                hours = ISOSensitivityController.ISO_AUTO + newHour;
            } else {
                hours = "" + newHour;
            }
        }
        String newTime = hours + ":" + minutes + ExposureModeController.SOFT_SNAP + amPm;
        return newTime;
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
            AppLog.trace(TAG, "Thumnail file cannot be deleted");
        }
    }

    public boolean isAviFileExist(String filePath) {
        File dir = new File(filePath);
        return FileHelper.exists(dir);
    }

    public String getFormatteddate(String date) {
        if (date == null) {
            return "";
        }
        String year = date.substring(0, 4);
        String month = getMonthName(date.substring(4, 6));
        String day = date.substring(6);
        String newDate = month + "-" + day + "-" + year;
        return newDate;
    }

    public long getUTCTime() {
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
        return cal.getTimeInMillis();
    }

    public int getThemeNameResID(int theme) {
        int[] resIDs = {com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TBLUESKY, com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TSUNSET, com.sony.imaging.app.digitalfilter.R.string.STRID_FUNC_SKYND_TGRADUATEDND, R.string.capability_title_canTakeScreenshot, R.string.capital_off, R.string.capital_on};
        if (theme > resIDs.length - 1) {
            return -1;
        }
        return resIDs[theme];
    }

    public int getBootFactor() {
        return mLaunchBootFactor;
    }

    public void setBootFactor(int factorState) {
        mLaunchBootFactor = factorState;
    }

    public boolean hasAngleShiftAddon() {
        return true;
    }

    public boolean canQueryforRecoveryDB() {
        return isSupportedVersion(2, 5);
    }

    public int getAvailableRemainingShot() {
        MediaNotificationManager.getInstance().updateRemainingAmount();
        int stillRemainingNumber = MediaNotificationManager.getInstance().getRemaining();
        return stillRemainingNumber;
    }

    public void setOkFocusedOnRemainingMemoryCaution(boolean isFocusedOk) {
        this.misOkFocusedOnRemainingMemroryCaution = isFocusedOk;
    }

    public boolean isOkFocusedOnRemainingMemroryCaution() {
        return this.misOkFocusedOnRemainingMemroryCaution;
    }

    public PointF getOSDSAPoint(int previewWidth, int previewHeight, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            PointF point = param.getOSDSAPoint(previewWidth, previewHeight);
            return point;
        }
        if (id != 1) {
            return null;
        }
        PointF point2 = param.getOSDSAPoint2(previewWidth, previewHeight);
        return point2;
    }

    public Point getSAPoint(int rawWidth, int rawHeight, String aspectRatioOfPictureImage, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            Point point = param.getSAPoint(rawWidth, rawHeight, aspectRatioOfPictureImage);
            return point;
        }
        if (id != 1) {
            return null;
        }
        Point point2 = param.getSAPoint2(rawWidth, rawHeight, aspectRatioOfPictureImage);
        return point2;
    }

    public int getSADegree(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            int degree = param.getSADegree();
            return degree;
        }
        if (id != 1) {
            return 0;
        }
        int degree2 = param.getSADegree2();
        return degree2;
    }

    public int getSAStrength(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            int strength = param.getSAStrength();
            return strength;
        }
        if (id != 1) {
            return 0;
        }
        int strength2 = param.getSAStrength2();
        return strength2;
    }

    public Point getPoint(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            Point point = param.getPoint();
            return point;
        }
        if (id != 1) {
            return null;
        }
        Point point2 = param.getPoint2();
        return point2;
    }

    public Point getPoint() {
        return getPoint(mBorderId);
    }

    public void setPoint(Point p, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            param.setPoint(p);
        } else if (id == 1) {
            param.setPoint2(p);
        }
    }

    public void setPoint(Point p) {
        setPoint(p, mBorderId);
    }

    public int getDegree(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            int degree = param.getDegree();
            return degree;
        }
        if (id != 1) {
            return 0;
        }
        int degree2 = param.getDegree2();
        return degree2;
    }

    public int getDegree() {
        return getDegree(mBorderId);
    }

    public PointF getOSDPoint(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            PointF point = param.getOSDPoint();
            return point;
        }
        if (id != 1) {
            return null;
        }
        PointF point2 = param.getOSDPoint2();
        return point2;
    }

    public PointF getOSDPoint() {
        return getOSDPoint(mBorderId);
    }

    public int getStrength(int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            int strength = param.getStrength();
            return strength;
        }
        if (id != 1) {
            return 0;
        }
        int strength2 = param.getStrength2();
        return strength2;
    }

    public int getStrength() {
        return getStrength(mBorderId);
    }

    public void setDegree(int degree, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            param.setDegree(degree);
        } else if (id == 1) {
            param.setDegree2(degree);
        }
    }

    public void setDegree(int degree) {
        setDegree(degree, mBorderId);
    }

    public void setOSDPoint(PointF point, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            param.setOSDPoint(point);
        } else if (id == 1) {
            param.setOSDPoint2(point);
        }
    }

    public void setOSDPoint(PointF point) {
        setOSDPoint(point, mBorderId);
    }

    public void setStrength(int level, int id) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        if (id == 0) {
            param.setStrength(level);
        } else if (id == 1) {
            param.setStrength2(level);
        }
    }

    public void setStrength(int level) {
        setStrength(level, mBorderId);
    }

    public void setBorderId(int id) {
        mBorderId = id;
    }

    public int getBorderId() {
        return mBorderId;
    }

    public void toggleBorder() {
        if (mBorderId == 0) {
            setBorderId(1);
        } else {
            setBorderId(0);
        }
    }

    public void setLayerFlag(boolean isLand, boolean isSky, boolean isLayer3) {
        mIsLand = isLand;
        mIsSky = isSky;
        mIsLayer3 = isLayer3;
    }

    public boolean isLand() {
        return mIsLand;
    }

    public boolean isSky() {
        return mIsSky;
    }

    public boolean isLayer3() {
        return mIsLayer3;
    }

    public int getSettingLayer() {
        if (isSky()) {
            return 1;
        }
        if (!isLayer3()) {
            return 0;
        }
        return 2;
    }

    public void startLayerSetting() {
        mLayerSettingMenu = true;
    }

    public void endLayerSetting() {
        mLayerSettingMenu = false;
    }

    public boolean isLayerSetting() {
        return mLayerSettingMenu;
    }

    public boolean isAvailableAperture() {
        boolean availableAperture = isAvailableModeforAperture();
        if (getInstance().isFixedAperture()) {
            return false;
        }
        return availableAperture;
    }

    public boolean isAvailableModeforAperture() {
        String exp = GFExposureModeController.getInstance().getValue(null);
        return ExposureModeController.MANUAL_MODE.equals(exp) || "Aperture".equals(exp);
    }

    public boolean isAvailableShutterSpeed() {
        String exp = GFExposureModeController.getInstance().getValue(null);
        return ExposureModeController.MANUAL_MODE.equals(exp) || "Shutter".equals(exp);
    }

    public void setVerticalLinkState(boolean status) {
        isVerticalLinkExecuted = status;
    }

    public boolean isVerticalLinkExecuted() {
        return isVerticalLinkExecuted;
    }

    public void updateLogIcon(ImageView logIcon) {
        int debugStage = BackUpUtil.getInstance().getPreferenceInt(GFBackUpKey.KEY_DEBUG_LOG, 0);
        switch (debugStage) {
            case 0:
                logIcon.setVisibility(4);
                return;
            case 1:
                logIcon.setImageResource(com.sony.imaging.app.digitalfilter.R.drawable.p_16_dd_parts_skyhdr_logout);
                logIcon.setVisibility(0);
                return;
            case 2:
                logIcon.setVisibility(4);
                return;
            case 3:
                logIcon.setVisibility(0);
                boolean isAlgDebug = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_DEBUG_ALG_LOG, false);
                if (isAlgDebug) {
                    logIcon.setImageResource(com.sony.imaging.app.digitalfilter.R.drawable.p_16_dd_parts_skyhdr_algout);
                    return;
                } else {
                    logIcon.setImageResource(com.sony.imaging.app.digitalfilter.R.drawable.p_16_dd_parts_skyhdr_logout);
                    return;
                }
            default:
                logIcon.setVisibility(4);
                return;
        }
    }
}
