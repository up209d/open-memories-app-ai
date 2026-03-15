package com.sony.imaging.app.startrails.util;

import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.base.menu.controller.STCreativeStyleController;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.base.menu.controller.STISOSensitivityController;
import com.sony.imaging.app.startrails.base.menu.controller.STPictureEffectController;
import com.sony.imaging.app.startrails.base.menu.controller.STWhiteBalanceController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeRecomandedMenuSetting {
    public static final String DEFAULT_DETAIL_VALUE = "2;2;2";
    public static final String ID_CREATIVESTYLE_STD_OPTION = "ID_CREATIVESTYLE_STD_OPTION";
    private static final String SEPARATOR_SLASH = "/";
    private static String TAG = "ThemeRecomandedMenuSetting";
    private static ThemeRecomandedMenuSetting mTheInstance = new ThemeRecomandedMenuSetting();

    public static ThemeRecomandedMenuSetting getInstance() {
        return mTheInstance;
    }

    private ThemeRecomandedMenuSetting() {
    }

    public void updateBrightNightThemeProperty() {
        AppLog.enter(TAG, AppLog.getMethodName());
        STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
        STPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
        updateWhiteBalance();
        STISOSensitivityController.getInstance().setStartTrailsValue(ISOSensitivityController.MENU_ITEM_ID_ISO, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.BRIGHT_NIGHT_ISO_VALUE_KEY, ISOSensitivityController.ISO_AUTO));
        STCreativeStyleController.getInstance().setStarTrailsValue(CreativeStyleController.CREATIVESTYLE, "standard");
        setThemeExposureCompensation();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateDarkNightThemeProperty() {
        AppLog.enter(TAG, AppLog.getMethodName());
        STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MANUAL_MODE);
        STPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
        updateWhiteBalance();
        STCreativeStyleController.getInstance().setStarTrailsValue(CreativeStyleController.CREATIVESTYLE, "standard");
        setThemeExposureCompensation();
        setDarkNightDefaltValue();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateCustomtThemeProperty() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String pictureEffect = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, "off");
        String optValue = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_CREACTIVE_STYLE_STANDARD_KEY_OPTION_VALUE, DEFAULT_DETAIL_VALUE);
        BackUpUtil.getInstance().setPreference("ID_CREATIVESTYLE_STD_OPTION", optValue);
        if ("off".equals(pictureEffect)) {
            STCreativeStyleController.getInstance().setStarTrailsValue(CreativeStyleController.CREATIVESTYLE, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_CREATIVE_STYLE_KEY, "standard"));
        }
        updateExposureModeForCustomTheme();
        STPictureEffectController.getInstance().setStarTrailsValue(PictureEffectController.PICTUREEFFECT, pictureEffect);
        updateWhiteBalance();
        STISOSensitivityController.getInstance().setStartTrailsValue(ISOSensitivityController.MENU_ITEM_ID_ISO, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_ISO_VALUE_KEY, ISOSensitivityController.ISO_AUTO));
        setThemeExposureCompensation();
        if (STUtility.getInstance().isCustomThemeBooted()) {
            STUtility.getInstance().setCustomThemeBooted(false);
            updateDarkNightAperture();
        } else {
            STUtility.getInstance().setAperture();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateWhiteBalance() {
        AppLog.info(TAG, "updateWhiteBalance");
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 0:
                STWhiteBalanceController.getInstance().setStarTrailsValue(WhiteBalanceController.WHITEBALANCE, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.BRIGHTNIGHT_WB_KEY, "auto"));
                updateWhiteBalanceOption(STBackUpKey.BRIGHT_WB_KEY_OPTION_VALUE, STConstants.BRIGHT_NIGHT_WB_OPTOIN_VALUE);
                break;
            case 1:
                STWhiteBalanceController.getInstance().setStarTrailsValue(WhiteBalanceController.WHITEBALANCE, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.DARKNIGHT_WB_KEY, "auto"));
                updateWhiteBalanceOption(STBackUpKey.DARKNIGHT_WB_KEY_OPTION_VALUE, STConstants.DARK_NIGHT_WB_OPTOIN_VALUE);
                break;
            default:
                STWhiteBalanceController.getInstance().setStarTrailsValue(WhiteBalanceController.WHITEBALANCE, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_WB_KEY, "auto"));
                updateWhiteBalanceOption(STBackUpKey.CUSTOM_WB_KEY_OPTION_VALUE, STConstants.CUSTOM_WB_OPTOIN_VALUE);
                break;
        }
        AppLog.info(TAG, "updateWhiteBalance");
    }

    public void setDarkNightDefaltValue() {
        CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
        CameraEx.LensInfo lensInfo = CameraSetting.getInstance().getLensInfo();
        if (apertureInfo != null) {
            String iso = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.DARK_NIGHT_ISO_VALUE_KEY, "none");
            if ("none".equals(iso)) {
                setDarkNightDefaultIsoValue(apertureInfo.currentAvailableMin, lensInfo);
            }
            updateDarkNightAperture();
        }
    }

    private void setDarkNightDefaultIsoValue(int apetuerValue, CameraEx.LensInfo lensInfo) {
        String isoValue = getDefaultIsoValue(apetuerValue, lensInfo);
        STISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, isoValue);
    }

    private String getDefaultIsoValue(int apetuerValue, CameraEx.LensInfo lensInfo) {
        int index;
        if (lensInfo == null) {
            index = 9;
        } else if (apetuerValue < 105) {
            index = 0;
        } else if (apetuerValue < 115) {
            index = 1;
        } else if (apetuerValue < 130) {
            index = 2;
        } else if (apetuerValue < 150) {
            index = 3;
        } else if (apetuerValue < 170) {
            index = 4;
        } else if (apetuerValue < 190) {
            index = 5;
        } else if (apetuerValue < 210) {
            index = 6;
        } else if (apetuerValue < 235) {
            index = 7;
        } else if (apetuerValue < 265) {
            index = 8;
        } else if (apetuerValue < 300) {
            index = 9;
        } else if (apetuerValue < 335) {
            index = 10;
        } else if (apetuerValue < 400) {
            index = 11;
        } else {
            index = 12;
        }
        List<String> supportedListString = STISOSensitivityController.getInstance().getSupportedValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
        List<Integer> supportedList = createIntegerList(supportedListString);
        int defaultIndex = supportedList.indexOf(Integer.valueOf(STUtility.ISO_DARK_NIGHT[index]));
        if (-1 == defaultIndex) {
            defaultIndex = 0;
            while (defaultIndex < supportedList.size() && STUtility.ISO_DARK_NIGHT[index] >= supportedList.get(defaultIndex).intValue()) {
                defaultIndex++;
            }
            if (defaultIndex > 0 && defaultIndex < supportedList.size()) {
                int subL = STUtility.ISO_DARK_NIGHT[index] - supportedList.get(defaultIndex - 1).intValue();
                int subH = supportedList.get(defaultIndex).intValue() - STUtility.ISO_DARK_NIGHT[index];
                if (subL <= subH) {
                    defaultIndex--;
                }
            }
        }
        return Integer.toString(supportedList.get(defaultIndex).intValue());
    }

    private List<Integer> createIntegerList(List<String> supportedListString) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < supportedListString.size(); i++) {
            String isoValue = supportedListString.get(i);
            try {
                Integer.parseInt(isoValue);
                list.add(Integer.valueOf(Integer.parseInt(isoValue)));
            } catch (NumberFormatException e) {
                list.add(0);
            }
        }
        return list;
    }

    public void updateDarkNightAperture() {
        CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
        if (apertureInfo != null && apertureInfo.currentAvailableMin != CameraSetting.getInstance().getAperture()) {
            CameraSetting.getInstance().getCamera().adjustAperture(-40);
        }
    }

    public void updateExposureModeForCustomTheme() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ModeDialDetector.hasModeDial()) {
            setExposureModeForCustom();
        } else {
            STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_EXPOSURE_MODE_KEY, "Aperture"));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateWhiteBalanceOption(String key, String optoinValue) {
        AppLog.exit(TAG, "updateWhiteBalanceOption");
        String optValue = BackUpUtil.getInstance().getPreferenceString(key, optoinValue);
        AppLog.trace(TAG, "updateWhiteBalanceOption optoinValue" + optoinValue);
        String[] value = optValue.split(SEPARATOR_SLASH);
        int lightBalance = Integer.parseInt(value[0]);
        int colorCompensation = Integer.parseInt(value[1]);
        int colorTemperature = Integer.parseInt(value[2]);
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) STWhiteBalanceController.getInstance().getDetailValue();
        param.setColorComp(colorCompensation);
        param.setLightBalance(lightBalance);
        param.setColorTemp(colorTemperature);
        STWhiteBalanceController.getInstance().setDetailValue(param);
        AppLog.exit(TAG, "setDefaultWhiteBalanceOption");
    }

    private void setExposureModeForCustom() {
        switch (ModeDialDetector.getModeDialPosition()) {
            case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
                return;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
                return;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
                return;
            case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MANUAL_MODE);
                return;
            default:
                return;
        }
    }

    private void setThemeExposureCompensation() {
        AppLog.info(TAG, "setThemeExposureCompensation");
        if (true == STUtility.getInstance().isEVDialRotated()) {
            STUtility.getInstance().setEVDialRotated(false);
        } else {
            STExposureCompensationController.getInstance().setStartTrailsValue("ExposureCompensation", "" + getExposureValueFromBackUp());
            AppLog.info(TAG, "setThemeExposureCompensation");
        }
    }

    private int getExposureValueFromBackUp() {
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                int expoValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DARK_NIGHT_EXPO_COMP_VALUE_KEY, 0);
                return expoValue;
            case 2:
                int expoValue2 = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_EXPOSURE_COMPENSATION_KEY, 0);
                return expoValue2;
            default:
                int expoValue3 = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BRIGHT_NIGHT_EXPO_COMP_VALUE_KEY, 2);
                return expoValue3;
        }
    }

    public void setThemeSpecificShutterSpeed() {
        AppLog.info(TAG, "setThemeSpecificShutterSpeed");
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                STUtility.getInstance().setRecommanedSS(STConstants.DEFAULT_VALUE_SHUTTER_SPEED_DARK_NIGHT);
                break;
            case 2:
                break;
            default:
                STUtility.getInstance().setRecommanedSS(STConstants.DEFAULT_VALUE_SHUTTER_SPEED);
                break;
        }
        AppLog.info(TAG, "setThemeSpecificShutterSpeed");
    }
}
