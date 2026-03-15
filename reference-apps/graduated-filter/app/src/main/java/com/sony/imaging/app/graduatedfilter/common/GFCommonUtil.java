package com.sony.imaging.app.graduatedfilter.common;

import android.R;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
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
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeSs;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.GFExecutorCreator;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFMeteringController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class GFCommonUtil {
    private static final int BATTERY_PREEND = 0;
    private static final String INH_FACTOR_ID_BAD_BLOCK_OVER = "INH_FACTOR_BAD_BLOCK_WRITE_PROTECT";
    private static final String INH_FACTOR_ID_READ_ONLY = "INH_FACTOR_READ_ONLY_MEDIA";
    private static final String INH_FACTOR_ID_WRITE_PROTECTED = "INH_FACTOR_MEDIA_WRITE_PROTECT";
    private static final int LIMITED_MODEL_ID = 10;
    private static final String LIMITED_MODEL_PREFIX = "DSC-RX";
    private static final String PROP_MODEL_NAME = "model.name";
    private static boolean isMenuOpened;
    private static final String TAG = AppLog.getClassName();
    private static GFCommonUtil mInstance = null;
    private static boolean mPushedIR2SecKey = false;
    private static boolean mTransitionByS1 = false;
    private static boolean mFilterSetting = false;
    private static boolean mAdjustmentStateSetting = false;
    private static boolean needCTempSetting = true;
    private static boolean mTransitionFromWBAdjustment = false;
    private static boolean mDuringSelfTimer = false;
    private static int mPreviewButtonPosition = 0;
    private static boolean showFilterEVSettingGuide = true;
    public static boolean showIrisLensMessage = false;
    private static boolean mNeedLimitedByLensSpec = false;
    private static boolean mRX10 = false;
    private static boolean mRX100 = false;
    private static boolean isInvalidShutter = false;
    private static boolean isCanceledByLensStatus = false;
    private static boolean isValidFlashCheck = true;
    private static boolean needActualMediaSetting = false;
    private boolean mMaybePhaseDiff = false;
    private boolean mBaseCameraSettings = false;

    public static GFCommonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new GFCommonUtil();
        }
        return mInstance;
    }

    public int getCautionId() {
        int cautionID = 0;
        AvailableInfo.update();
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
        } else if (MediaNotificationManager.getInstance().isMounted() && MediaNotificationManager.getInstance().getRemaining() < getNumberOfShooting()) {
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
        }
        AppLog.info("getCautionId", "getCautionId : " + cautionID);
        return cautionID;
    }

    private int getNumberOfShooting() {
        if (!GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE))) {
            return 3;
        }
        return 1;
    }

    public boolean isMediaInhOn() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isFactor(INH_FACTOR_ID_BAD_BLOCK_OVER, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_WRITE_PROTECTED, media[0]) || AvailableInfo.isFactor(INH_FACTOR_ID_READ_ONLY, media[0]);
    }

    public boolean isSupportedVersion(int majorValue, int minorValue) {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        return pfMajorVersion >= majorValue + 1 || (pfMajorVersion == majorValue && pfAPIVersion >= minorValue);
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
        int type = ScalarProperties.getInt("device.iris.ring.type");
        AppLog.info("Iris Ring", "Iris Type = " + type);
        return type != 0;
    }

    public boolean isFixedAperture() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        boolean isSupportedFixedAperture = pfMajorVersion >= 3 || (pfMajorVersion == 2 && pfAPIVersion >= 7);
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        if (isSupportedFixedAperture && info != null) {
            boolean fixedAperture = info.FixedAperture;
            return fixedAperture;
        }
        CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
        if (apertureInfo != null && apertureInfo.currentAvailableMax == apertureInfo.currentAvailableMin) {
            return true;
        }
        return false;
    }

    public boolean isDSC() {
        int modelCategory = ScalarProperties.getInt("model.category");
        return modelCategory == 2 || modelCategory == 2;
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

    public void startFilterSetting() {
        mFilterSetting = true;
    }

    public void stopFilterSetting() {
        mFilterSetting = false;
    }

    public boolean isFilterSetting() {
        return mFilterSetting;
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

    private void setCameraSettings(boolean isBase) {
        if (hasIrisRing() && isManual()) {
            setApertureAndShutterSpeed(isBase);
        } else {
            if (!isFixedAperture()) {
                setAperture(isBase);
            }
            setShutterSpeed(isBase);
        }
        setIso(isBase);
        setEvComp(isBase);
        setWhiteBalance(isBase);
    }

    public void setWhiteBalanceOnCameraSettings() {
        setWhiteBalance(false);
    }

    public void setWhiteBalanceOffCameraSettings() {
        setWhiteBalance(true);
    }

    public void setBaseCameraSettingsFlag(boolean isBase) {
        this.mBaseCameraSettings = isBase;
    }

    public boolean isBaseCameraSettings() {
        return this.mBaseCameraSettings;
    }

    public void setBaseCameraSettings() {
        setCameraSettings(true);
        this.mBaseCameraSettings = true;
    }

    public void setFilterCameraSettings() {
        setCameraSettings(false);
        this.mBaseCameraSettings = false;
    }

    public void needCTempSetting() {
        needCTempSetting = true;
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
        if (itemID != null && itemID.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT) && (value = GFMeteringController.getInstance().getValue(itemID)) != null) {
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

    public void setAperture(boolean isBaseSetting) {
        if (isManual() || isAperture()) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(isBaseSetting));
            new ChangeAperture().execute(step, params.getAperture(isBaseSetting), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.graduatedfilter.common.GFCommonUtil.1
                @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture.Callback
                public void cbFunction() {
                }
            });
        }
    }

    public void setShutterSpeed(boolean isBaseSetting) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ChangeSs.executeWithoutCallBack(params.getSSNumerator(isBaseSetting), params.getSSDenominator(isBaseSetting));
    }

    public void setApertureAndShutterSpeed(final boolean isBaseSetting) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(isBaseSetting));
        new ChangeAperture().execute(step, params.getAperture(isBaseSetting), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.graduatedfilter.common.GFCommonUtil.2
            @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture.Callback
            public void cbFunction() {
                GFCommonUtil.this.setShutterSpeed(isBaseSetting);
            }
        });
    }

    public void setIso(boolean isBaseSetting) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, params.getISO(isBaseSetting));
    }

    public void setEvComp(boolean isBaseSetting) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        ExposureCompensationController.getInstance().setValue("ExposureCompensation", params.getExposureComp(isBaseSetting));
    }

    public void setWhiteBalance(boolean isBaseSetting) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String wbMode = params.getWBMode(isBaseSetting);
        WhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, wbMode);
        WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String[] optArray = params.getWBOption(!isFilterSetting()).split("/");
        int light = Integer.parseInt(optArray[0]);
        int comp = Integer.parseInt(optArray[1]);
        int temp = Integer.parseInt(optArray[2]);
        options.setLightBalance(light);
        options.setColorComp(comp);
        if (!WhiteBalanceController.CUSTOM.equalsIgnoreCase(wbMode) && !"custom1".equalsIgnoreCase(wbMode) && !"custom2".equalsIgnoreCase(wbMode) && !"custom3".equalsIgnoreCase(wbMode)) {
            options.setColorTemp(temp);
        }
        WhiteBalanceController.getInstance().setDetailValue(options);
    }

    public void resetParameters() {
        int theme = GFEffectParameters.Parameters.getEffect();
        GFBackUpKey.getInstance().resetCSOptions(theme);
        GFBackUpKey.getInstance().resetWBOptions(theme);
        GFBackUpKey.getInstance().resetMeteringSpotValue(theme);
        GFBackUpKey.getInstance().saveCTempFilterWBOption(GFEffectParameters.Parameters.DEFAULT_FILTER_WB_OPTION[theme], false, theme);
        GFBackUpKey.getInstance().saveCTempFilterWBOption(GFEffectParameters.Parameters.DEFAULT_BASE_WB_OPTION[theme], true, theme);
        String defaultParams = GFEffectParameters.getInstance().getParameters().getDefaultFlatten();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.unflatten(defaultParams);
        GFEffectParameters.getInstance().setParameters(params);
        GFBackUpKey.getInstance().saveLastParameters(defaultParams);
    }

    public void resetColorParameters() {
        int theme = GFBackUpKey.getInstance().getLastEffect();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setWbGM(GFEffectParameters.Parameters.DEFAULT_FILTER_WB_R[theme]);
        params.setWbAB(GFEffectParameters.Parameters.DEFAULT_FILTER_WB_B[theme]);
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
        int nameId = com.sony.imaging.app.graduatedfilter.R.string.STRID_FUNC_SKYND_TBLUESKY;
        String value = GFThemeController.getInstance().getValue(null);
        if ("skyblue".equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.graduatedfilter.R.string.STRID_FUNC_SKYND_TBLUESKY;
        } else if ("sunset".equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.graduatedfilter.R.string.STRID_FUNC_SKYND_TSUNSET;
        } else if ("standard".equalsIgnoreCase(value)) {
            nameId = com.sony.imaging.app.graduatedfilter.R.string.STRID_FUNC_SKYND_TGRADUATEDND;
        } else if ("custom1".equalsIgnoreCase(value)) {
            nameId = R.string.capability_title_canTakeScreenshot;
        } else if ("custom2".equalsIgnoreCase(value)) {
            nameId = R.string.capital_off;
        } else if ("custom3".equalsIgnoreCase(value)) {
            nameId = R.string.capital_on;
        }
        AppNameView.setText(AppContext.getAppContext().getResources().getString(nameId));
    }

    public void setPreivewButtonPostion(int position) {
        mPreviewButtonPosition = position;
    }

    public int getPreivewButtonPostion() {
        return mPreviewButtonPosition;
    }

    public void setShowFilterEVSettingFlag(boolean showGuide) {
        showFilterEVSettingGuide = showGuide;
    }

    public boolean needShowFilterExpSettingGuide() {
        return showFilterEVSettingGuide;
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
}
