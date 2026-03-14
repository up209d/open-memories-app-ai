package com.sony.imaging.app.smoothreflection.common;

import android.content.res.AssetManager;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.menu.layout.ThemeMenuLayout;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionCreativeStyleController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionDROAutoHDRController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionISOSensitivityController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionMeteringController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionPictureEffectController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionWhiteBalanceController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class SmoothReflectionUtil {
    public static final String STANDARD_APERTURE_11 = "F11";
    public static final String STANDARD_APERTURE_8 = "F8.0";
    public static HashMap<String, String> WATERFLOW_HIGH_SHOT;
    private BackUpUtil mBackUpUtil;
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionUtil sInstance = null;
    private String mCurrentMenuSelectionScreen = null;
    private String mCurrentMenuLayout = null;
    private int mCurrentShootNumber = 0;
    private CameraEx.GammaTable mGammaTable = null;
    private String mShootMode = ExposureModeController.PROGRAM_AUTO_MODE;
    private String mISOSensitivity = ISOSensitivityController.ISO_100;
    private String mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
    private String mWhiteBalance = "auto";
    private WhiteBalanceController.WhiteBalanceParam mWhiteBalanceParam = null;
    private String mDroHdr = "off";
    private String mCreativeStyle = "standard";
    private CreativeStyleController.CreativeStyleOptions mCreativeStyleOptions = null;
    private String mPictureEffect = "off";
    private String mPictureEffectOption = null;
    private String mApertureValue = getCameraApertureValues();
    private boolean mbComeFromPageMenu = false;
    private final byte BW_STATE = 0;
    private final byte SEPIA_STATE = 1;
    private final byte WARM_STATE = 2;
    private final byte COOL_STATE = 3;
    private final byte GREEN_STATE = 4;
    float[] bw = {0.334f, 0.333f, 0.333f, 0.333f, 0.334f, 0.333f, 0.333f, 0.333f, 0.334f};
    float[] sepia = {0.3543072f, 0.3532464f, 0.3532464f, 0.3377074f, 0.3366963f, 0.3366963f, 0.3099854f, 0.3090573f, 0.3090573f};
    float[] warm = {0.415496f, 0.414252f, 0.414252f, 0.339344f, 0.338328f, 0.338328f, 0.24716f, 0.24642f, 0.24642f};
    float[] cool = {0.30394f, 0.30303f, 0.30303f, 0.32064f, 0.31968f, 0.31968f, 0.37742f, 0.37629f, 0.37629f};
    float[] green = {0.3377074f, 0.3366963f, 0.3366963f, 0.3543072f, 0.3532464f, 0.3532464f, 0.3099854f, 0.3090573f, 0.3090573f};

    private SmoothReflectionUtil() {
        this.mBackUpUtil = null;
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    public static SmoothReflectionUtil getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionUtil();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public String getCurrentMenuSelectionScreen() {
        return this.mCurrentMenuSelectionScreen;
    }

    public void setCurrentMenuLayout(String currentMenuLayout) {
        this.mCurrentMenuLayout = currentMenuLayout;
    }

    public String getCurrentMenuLayout() {
        return this.mCurrentMenuLayout;
    }

    public void setCurrentMenuSelectionScreen(String mcurrentMenuSelectionScreen) {
        this.mCurrentMenuSelectionScreen = mcurrentMenuSelectionScreen;
    }

    public int getCurrentShootNumber() {
        return this.mCurrentShootNumber;
    }

    public void setCurrentShootNumber(int currentShootNumber) {
        this.mCurrentShootNumber = currentShootNumber;
    }

    public void saveCameraSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        this.mBackUpUtil.setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, selectedTheme);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void startKikiLog(int kikilogId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        AppLog.info(TAG, "kikilogStart");
        Kikilog.setUserLog(kikilogId, options);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getThemeResourceId(String theme) {
        if (ThemeController.CUSTOM.equals(theme)) {
            return R.string.STRID_AMC_STR_01036;
        }
        if (ThemeController.SILENT.equals(theme)) {
            return R.string.STRID_FUNC_SMOOTH_PHOTO_SILENT;
        }
        if (ThemeController.WATERFLOW.equals(theme)) {
            return R.string.STRID_FUNC_SMOOTH_PHOTO_WATER_FLOW;
        }
        if (ThemeController.SMOKEHAZE.equals(theme)) {
            return R.string.STRID_FUNC_SMOOTH_PHOTO_SMOKE_HAZE;
        }
        if (ThemeController.MONOTONE.equals(theme)) {
            return R.string.STRID_FUNC_SMOOTH_PHOTO_MONOTONE;
        }
        if (!ThemeController.TWILIGHTREFLECTION.equals(theme)) {
            return -1;
        }
        return R.string.STRID_FUNC_SMOOTH_PHOTO_TWILIGHT_REFLECTION;
    }

    public void updateApplicationTiltle(String value) {
        int kikilogSubId = 0;
        if (value.equalsIgnoreCase(ThemeController.CUSTOM)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_AMC_STR_01036));
            kikilogSubId = 4250;
        } else if (value.equalsIgnoreCase(ThemeController.SILENT)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_SMOOTH_PHOTO_SILENT));
            kikilogSubId = 4245;
        } else if (value.equalsIgnoreCase(ThemeController.WATERFLOW)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_SMOOTH_PHOTO_WATER_FLOW));
            kikilogSubId = 4246;
        } else if (value.equalsIgnoreCase(ThemeController.SMOKEHAZE)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_SMOOTH_PHOTO_SMOKE_HAZE));
            kikilogSubId = 4247;
        } else if (value.equalsIgnoreCase(ThemeController.MONOTONE)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_SMOOTH_PHOTO_MONOTONE));
            kikilogSubId = 4249;
        } else if (value.equalsIgnoreCase(ThemeController.TWILIGHTREFLECTION)) {
            AppNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_SMOOTH_PHOTO_TWILIGHT_REFLECTION));
            kikilogSubId = 4248;
        }
        startKikiLog(kikilogSubId);
        AppNameView.show(true);
    }

    public void setRecommandedCameraSettings(String selectedTheme) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCreativeStyleOptions = (CreativeStyleController.CreativeStyleOptions) SmoothReflectionCreativeStyleController.getInstance().getDetailValue();
        if (ThemeController.MONOTONE.equals(ThemeMenuLayout.sPreviousSelectedTheme) && !ThemeController.MONOTONE.equals(selectedTheme)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supParams = CameraSetting.getInstance().getSupportedParameters(1);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams = CameraSetting.getInstance().getEmptyParameters();
            if (!SaUtil.isAVIP() && ((CameraEx.ParametersModifier) supParams.second).isRGBMatrixSupported()) {
                ((CameraEx.ParametersModifier) emptyParams.second).setRGBMatrix((int[]) null);
            }
            CameraSetting.getInstance().setParameters(emptyParams);
        }
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            setExposureMode();
            this.mISOSensitivity = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_ISO, ISOSensitivityController.ISO_100);
            this.mMeteringMode = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_METERING_MODE, MeteringController.MULTIMODE.toLowerCase());
            this.mWhiteBalance = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_WHITE_BALANCE, "auto");
            if (WhiteBalanceController.CUSTOM.equals(this.mWhiteBalance) || "custom1".equals(this.mWhiteBalance) || "custom2".equals(this.mWhiteBalance) || "custom3".equals(this.mWhiteBalance)) {
                this.mWhiteBalanceParam = null;
            } else {
                this.mWhiteBalanceParam = getWhiteBalanceParamFromBackup();
            }
            this.mDroHdr = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_DRO, "off");
            this.mCreativeStyle = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE, "standard");
            getCreativeStyleOptionFromBackup();
            this.mPictureEffect = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_PICTURE_EFFECT, "off");
            this.mPictureEffectOption = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_PICTURE_EFFECT_OPTION, this.mPictureEffectOption);
            this.mApertureValue = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_APERTURE, this.mApertureValue);
        } else if (ThemeController.SILENT.equals(selectedTheme)) {
            this.mShootMode = "Aperture";
            this.mISOSensitivity = ISOSensitivityController.ISO_100;
            this.mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
            this.mWhiteBalance = "auto";
            this.mWhiteBalanceParam = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            this.mDroHdr = DROAutoHDRController.MODE_DRO_AUTO;
            ArrayList<String> supportedArray = (ArrayList) CreativeStyleController.getInstance().getSupportedValue(CreativeStyleController.CREATIVESTYLE);
            if (supportedArray.contains(CreativeStyleController.CLEAR)) {
                this.mCreativeStyle = CreativeStyleController.CLEAR;
                this.mCreativeStyleOptions.contrast = 0;
                this.mCreativeStyleOptions.saturation = 0;
                this.mCreativeStyleOptions.sharpness = 0;
            } else {
                this.mCreativeStyle = "landscape";
                this.mCreativeStyleOptions.contrast = 1;
                this.mCreativeStyleOptions.saturation = -2;
                this.mCreativeStyleOptions.sharpness = 0;
            }
            this.mPictureEffect = "off";
            this.mPictureEffectOption = null;
            this.mApertureValue = STANDARD_APERTURE_8;
        } else if (ThemeController.SMOKEHAZE.equals(selectedTheme)) {
            this.mShootMode = "Aperture";
            this.mISOSensitivity = ISOSensitivityController.ISO_100;
            this.mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
            this.mWhiteBalance = "auto";
            this.mWhiteBalanceParam = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            this.mDroHdr = DROAutoHDRController.MODE_DRO_AUTO;
            this.mCreativeStyle = "landscape";
            this.mCreativeStyleOptions.contrast = 0;
            this.mCreativeStyleOptions.saturation = 0;
            this.mCreativeStyleOptions.sharpness = 0;
            this.mPictureEffect = "off";
            this.mPictureEffectOption = null;
            this.mApertureValue = STANDARD_APERTURE_8;
        } else if (ThemeController.MONOTONE.equals(selectedTheme)) {
            this.mShootMode = "Aperture";
            this.mISOSensitivity = ISOSensitivityController.ISO_100;
            this.mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
            this.mWhiteBalance = "auto";
            this.mWhiteBalanceParam = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            this.mDroHdr = "off";
            this.mCreativeStyle = "standard";
            this.mCreativeStyleOptions.contrast = 3;
            String currentMonotoneValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, ThemeController.BW);
            if (ThemeController.WARM.equals(currentMonotoneValue)) {
                this.mCreativeStyleOptions.saturation = 3;
            } else {
                this.mCreativeStyleOptions.saturation = 0;
            }
            this.mCreativeStyleOptions.sharpness = 0;
            if (SaUtil.isAVIP()) {
                if (ThemeController.BW.equals(currentMonotoneValue)) {
                    this.mCreativeStyle = CreativeStyleController.MONO;
                    this.mCreativeStyleOptions.contrast = 0;
                    this.mCreativeStyleOptions.saturation = 0;
                    this.mCreativeStyleOptions.sharpness = 0;
                }
                if (ThemeController.SEPIA.equals(currentMonotoneValue)) {
                    this.mCreativeStyle = CreativeStyleController.SEPIA;
                    this.mCreativeStyleOptions.contrast = 0;
                    this.mCreativeStyleOptions.saturation = 0;
                    this.mCreativeStyleOptions.sharpness = 0;
                }
            }
            this.mPictureEffect = "off";
            this.mPictureEffectOption = null;
            this.mApertureValue = STANDARD_APERTURE_8;
        } else if (ThemeController.WATERFLOW.equals(selectedTheme)) {
            this.mShootMode = "Aperture";
            this.mISOSensitivity = ISOSensitivityController.ISO_100;
            this.mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
            this.mWhiteBalance = "auto";
            this.mWhiteBalanceParam = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            this.mDroHdr = DROAutoHDRController.MODE_DRO_AUTO;
            this.mCreativeStyle = CreativeStyleController.VIVID;
            this.mCreativeStyleOptions.contrast = 0;
            this.mCreativeStyleOptions.saturation = 0;
            this.mCreativeStyleOptions.sharpness = 0;
            this.mPictureEffect = "off";
            this.mPictureEffectOption = null;
            this.mApertureValue = STANDARD_APERTURE_11;
        } else if (ThemeController.TWILIGHTREFLECTION.equals(selectedTheme)) {
            this.mShootMode = "Aperture";
            this.mISOSensitivity = ISOSensitivityController.ISO_100;
            this.mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
            this.mWhiteBalance = "auto";
            this.mWhiteBalanceParam = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            this.mDroHdr = "off";
            ArrayList<String> supportedArray2 = (ArrayList) CreativeStyleController.getInstance().getSupportedValue(CreativeStyleController.CREATIVESTYLE);
            if (supportedArray2.contains(CreativeStyleController.NIGHT)) {
                this.mCreativeStyle = CreativeStyleController.NIGHT;
                this.mCreativeStyleOptions.contrast = 0;
                this.mCreativeStyleOptions.saturation = 0;
                this.mCreativeStyleOptions.sharpness = 0;
            } else {
                this.mCreativeStyle = "standard";
                this.mCreativeStyleOptions.contrast = 1;
                this.mCreativeStyleOptions.saturation = 2;
                this.mCreativeStyleOptions.sharpness = 0;
            }
            this.mPictureEffect = "off";
            this.mPictureEffectOption = null;
            this.mApertureValue = STANDARD_APERTURE_8;
        }
        setCameraSetting();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setRecommandedApertureSettings(String selectedTheme) {
        if (ThemeController.WATERFLOW.equals(selectedTheme)) {
            this.mApertureValue = STANDARD_APERTURE_11;
        } else {
            this.mApertureValue = STANDARD_APERTURE_8;
        }
        getInstance().setAperture(this.mApertureValue);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setCameraSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SmoothReflectionExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, this.mShootMode);
        SmoothReflectionISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, this.mISOSensitivity);
        SmoothReflectionMeteringController.getInstance().setValue("MeteringMode", this.mMeteringMode);
        SmoothReflectionWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, this.mWhiteBalance);
        if (this.mWhiteBalanceParam != null) {
            SmoothReflectionWhiteBalanceController.getInstance().setDetailValue(this.mWhiteBalanceParam);
        }
        if (this.mPictureEffectOption != null) {
            SmoothReflectionPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, this.mPictureEffect);
            SmoothReflectionPictureEffectController.getInstance().setValue(this.mPictureEffect, this.mPictureEffectOption);
        } else {
            SmoothReflectionPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, this.mPictureEffect);
        }
        String itemId = getDROHDRItemId(this.mDroHdr);
        AppLog.info(TAG, "DRO HDR item ID is : " + itemId + " DRO HDR  is : " + this.mDroHdr);
        if (itemId != null) {
            SmoothReflectionDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, itemId);
            SmoothReflectionDROAutoHDRController.getInstance().setValue(itemId, this.mDroHdr);
        } else {
            SmoothReflectionDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, itemId);
        }
        if (this.mPictureEffect.equals("off")) {
            SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, this.mCreativeStyle);
            SmoothReflectionCreativeStyleController.getInstance().setDetailValue(this.mCreativeStyleOptions);
        }
        getInstance().setAperture(this.mApertureValue);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getCameraApertureValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSetting camSetting = CameraSetting.getInstance();
        String convertedAperture = SmoothReflectionConstants.INVALID_APERTURE_STRING;
        if (camSetting.getApertureInfo() != null) {
            int currentAperture = camSetting.getApertureInfo().currentAperture;
            convertedAperture = getInstance().convertApertureValueFormat(currentAperture / 100.0f);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return convertedAperture;
    }

    private void setAperture(String setApertureValue) {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSetting camSetting = CameraSetting.getInstance();
        String apertureValue = getApertureValues(camSetting.getApertureInfo());
        setExactAperture(camSetting, setApertureValue, apertureValue);
        if (ThemeMenuLayout.sPreviousSelectedTheme != null && ThemeController.CUSTOM.equals(ThemeMenuLayout.sPreviousSelectedTheme)) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_APERTURE, apertureValue);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getApertureValues(CameraEx.ApertureInfo info) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String convertedAperture = SmoothReflectionConstants.INVALID_APERTURE_STRING;
        if (info != null) {
            int currentAperture = info.currentAperture;
            convertedAperture = convertApertureValueFormat(currentAperture / 100.0f);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return convertedAperture;
    }

    private void setExactAperture(CameraSetting camSetting, String setApertureValue, String apertureValue) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!apertureValue.equalsIgnoreCase(SmoothReflectionConstants.INVALID_APERTURE_STRING) && !apertureValue.equalsIgnoreCase(setApertureValue)) {
            int curruntApertureposition = 0;
            int expectedApertureposition = 0;
            if (SmoothReflectionConstants.APERTURE_VALUE_LIST.contains(apertureValue)) {
                curruntApertureposition = SmoothReflectionConstants.APERTURE_VALUE_LIST.indexOf(apertureValue);
            }
            if (SmoothReflectionConstants.APERTURE_VALUE_LIST.contains(setApertureValue)) {
                expectedApertureposition = SmoothReflectionConstants.APERTURE_VALUE_LIST.indexOf(setApertureValue);
            }
            camSetting.getCamera().adjustAperture(expectedApertureposition - curruntApertureposition);
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    private String convertApertureValueFormat(float value) {
        String displayValue;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (value == SmoothReflectionConstants.INVALID_APERTURE_VALUE) {
            displayValue = SmoothReflectionConstants.INVALID_APERTURE_STRING;
        } else if (value < 10.0f) {
            displayValue = String.format(SmoothReflectionConstants.FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(SmoothReflectionConstants.FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        String displayValue2 = displayValue.replace(',', '.');
        AppLog.exit(TAG, AppLog.getMethodName());
        return displayValue2;
    }

    private WhiteBalanceController.WhiteBalanceParam getWhiteBalanceParamFromBackup() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String optValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_WHITE_BALANCE_DETAIL, "0/0/0");
        String[] value = optValue.split("/");
        int light = Integer.parseInt(value[0]);
        int comp = Integer.parseInt(value[1]);
        int temp = Integer.parseInt(value[2]);
        WhiteBalanceController.WhiteBalanceParam wbParam = new WhiteBalanceController.WhiteBalanceParam(light, comp, temp);
        AppLog.exit(TAG, AppLog.getMethodName());
        return wbParam;
    }

    private void getCreativeStyleOptionFromBackup() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String optValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE_DETAIL, "0/0/0");
        String[] optValueArray = optValue.split("/");
        this.mCreativeStyleOptions.contrast = Integer.parseInt(optValueArray[0]);
        this.mCreativeStyleOptions.saturation = Integer.parseInt(optValueArray[1]);
        this.mCreativeStyleOptions.sharpness = Integer.parseInt(optValueArray[2]);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getDROHDRItemId(String drohdr) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String itemIdDROHDR = null;
        if (DROAutoHDRController.MODE_DRO_AUTO.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_1.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_2.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_3.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_4.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_5.equals(drohdr)) {
            itemIdDROHDR = DROAutoHDRController.MENU_ITEM_ID_DRO;
        }
        AppLog.info(TAG, "DRO HDR item ID is : " + itemIdDROHDR);
        AppLog.exit(TAG, AppLog.getMethodName());
        return itemIdDROHDR;
    }

    private String setExposureMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ModeDialDetector.hasModeDial()) {
            int mModeDialPosition = ModeDialDetector.getModeDialPosition();
            String mode = ExposureModeController.PROGRAM_AUTO_MODE;
            switch (mModeDialPosition) {
                case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                    mode = ExposureModeController.PROGRAM_AUTO_MODE;
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                    mode = "Shutter";
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                    mode = "Aperture";
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                    mode = ExposureModeController.MANUAL_MODE;
                    break;
            }
            this.mShootMode = mode;
        } else {
            this.mShootMode = this.mBackUpUtil.getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_SHOOT_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mShootMode;
    }

    public void setMonotoneSetting(String color) {
        AppLog.enter(TAG, AppLog.getMethodName());
        byte state = 0;
        int kikilogSubId = 0;
        if (ThemeController.BW.equals(color)) {
            state = 0;
        } else if (ThemeController.SEPIA.equals(color)) {
            state = 1;
        } else if (ThemeController.WARM.equals(color)) {
            state = 2;
        } else if (ThemeController.COOL.equals(color)) {
            state = 3;
        } else if (ThemeController.GREEN.equals(color)) {
            state = 4;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supParams = CameraSetting.getInstance().getSupportedParameters(1);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams = CameraSetting.getInstance().getEmptyParameters();
        int[] im = new int[this.cool.length];
        switch (state) {
            case 0:
                AppLog.info(TAG, "Selected Monotone : BW_STATE");
                for (int i = 0; i < this.bw.length; i++) {
                    im[i] = (int) ((this.bw[i] * 1024.0f) + 0.5d);
                }
                if (SaUtil.isAVIP()) {
                    SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, CreativeStyleController.MONO);
                }
                kikilogSubId = 4251;
                break;
            case 1:
                AppLog.info(TAG, "Selected Monotone : SEPIA_STATE");
                for (int i2 = 0; i2 < this.sepia.length; i2++) {
                    im[i2] = (int) ((this.sepia[i2] * 1024.0f) + 0.5d);
                }
                if (SaUtil.isAVIP()) {
                    SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, CreativeStyleController.SEPIA);
                }
                kikilogSubId = 4252;
                break;
            case 2:
                AppLog.info(TAG, "Selected Monotone : WARM_STATE");
                for (int i3 = 0; i3 < this.warm.length; i3++) {
                    im[i3] = (int) ((this.warm[i3] * 1024.0f) + 0.5d);
                }
                kikilogSubId = 4253;
                break;
            case 3:
                AppLog.info(TAG, "Selected Monotone : COOL_STATE");
                for (int i4 = 0; i4 < this.cool.length; i4++) {
                    im[i4] = (int) ((this.cool[i4] * 1024.0f) + 0.5d);
                }
                kikilogSubId = 4254;
                break;
            case 4:
                AppLog.info(TAG, "Selected Monotone : GREEN_STATE");
                for (int i5 = 0; i5 < this.green.length; i5++) {
                    im[i5] = (int) ((this.green[i5] * 1024.0f) + 0.5d);
                }
                kikilogSubId = 4255;
                break;
            default:
                AppLog.info(TAG, "Selected Monotone : default");
                for (int i6 = 0; i6 < this.bw.length; i6++) {
                    im[i6] = (int) ((this.bw[i6] * 1024.0f) + 0.5d);
                }
                if (SaUtil.isAVIP()) {
                    SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, CreativeStyleController.MONO);
                    break;
                }
                break;
        }
        if (!SaUtil.isAVIP() && ((CameraEx.ParametersModifier) supParams.second).isRGBMatrixSupported()) {
            ((CameraEx.ParametersModifier) emptyParams.second).setRGBMatrix(im);
        }
        CameraSetting.getInstance().setParameters(emptyParams);
        startKikiLog(kikilogSubId);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isComeFromPageMenu() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mbComeFromPageMenu;
    }

    public void setComeFromPageMenu(boolean isComeFromPageMenu) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mbComeFromPageMenu = isComeFromPageMenu;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void applyGammaTable() {
    }

    public void clearGammmTable() {
    }

    public void readFile() {
        AssetManager assetManager = AppContext.getAppContext().getAssets();
        try {
            InputStream input = assetManager.open("sr_gammatable.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            int j = 0;
            short[] gamma_table = new short[1024];
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    StringTokenizer token = new StringTokenizer(line, AbstractSupportedChecker.SEPARATOR);
                    Log.i(TAG, "setValue: " + line);
                    while (token.hasMoreTokens()) {
                        gamma_table[j] = Short.valueOf(token.nextToken()).shortValue();
                        j++;
                    }
                } else {
                    setGammaTable(gamma_table);
                    br.close();
                    input.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.toString();
        }
    }

    private void setGammaTable(short[] array) {
        if (array != null) {
            InputStream byteArrayIn = new ByteArrayInputStream(convShortArrayToByteArray(array));
            this.mGammaTable = CameraSetting.getInstance().getCamera().createGammaTable();
            AppLog.info(TAG, "mGammaTable is created " + this.mGammaTable);
            if (this.mGammaTable != null) {
                Log.i(TAG, "setGammaTable createGammaTable rerurns null");
                this.mGammaTable.setPictureEffectGammaForceOff(true);
                this.mGammaTable.write(byteArrayIn);
            }
        }
        CameraSetting.getInstance().getCamera().setExtendedGammaTable(this.mGammaTable);
    }

    protected byte[] convShortArrayToByteArray(short[] table) {
        byte[] w = new byte[table.length * 2];
        for (int i = 0; table.length > i; i++) {
            convShortToByte(w, i * 2, table[i]);
        }
        return w;
    }

    protected void convShortToByte(byte[] data, int offset, short value) {
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                data[offset + i] = Integer.valueOf(value >> 0).byteValue();
            } else {
                data[offset + i] = Integer.valueOf(value >> 8).byteValue();
            }
        }
    }
}
