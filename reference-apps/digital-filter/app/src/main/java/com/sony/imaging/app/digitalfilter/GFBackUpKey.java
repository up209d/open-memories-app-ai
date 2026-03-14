package com.sony.imaging.app.digitalfilter;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFClippingCorrectionController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShadingLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFVerticalLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class GFBackUpKey {
    private static final String DEFAULT_WB_OPTION = "0/0/5500";
    public static final String KEY_DEBUG_ALG_LOG = "KEY_DEBUG_ALG_LOG";
    public static final String KEY_DEBUG_LOG = "KEY_DEBUG_LOG";
    public static final String KEY_HISTGRAM_VIEW = "KEY_HISTGRAM_VIEW";
    public static final String KEY_ISO_AUTO_MAXMIN_INIT = "KEY_ISO_AUTO_MAXMIN_INIT";
    public static final String KEY_PARAM_CUSTOM1 = "KEY_PARAM_CUSTOM1";
    public static final String KEY_PARAM_CUSTOM2 = "KEY_PARAM_CUSTOM2";
    public static final String KEY_PARAM_REVERSE = "KEY_PARAM_REVERSE";
    public static final String KEY_PARAM_SKY_BLUE = "KEY_PARAM_SKY_BLUE";
    public static final String KEY_PARAM_STANDARD = "KEY_PARAM_STANDARD";
    public static final String KEY_PARAM_STRIPE = "KEY_PARAM_STRIPE";
    public static final String KEY_PARAM_SUNSET = "KEY_PARAM_SUNSET";
    public static final String KEY_RESET_APSC_SETTING = "KEY_RESET_APSC_SETTING";
    public static final String KEY_SELECTED_EFFECT = "SELECTED_EFFECT";
    public static final String KEY_SHOW_3RD_AREA_GUIDE = "KEY_SHOW_3RD_AREA_GUIDE";
    public static final String KEY_SHOW_ADJUSTGUIDE = "KEY_SHOW_ADJUSTGUIDE";
    public static final String KEY_SHOW_EXPCOMP_LINK_MSG = "KEY_SHOW_EXPCOMP_LINK_MSG";
    public static final String KEY_SHOW_FNO_LINK_MSG = "KEY_SHOW_FNO_LINK_MSG";
    public static final String KEY_SHOW_INTRODUCTION = "KEY_SHOW_INTRODUCTION";
    public static final String KEY_SHOW_ISO_LINK_MSG = "KEY_SHOW_ISO_LINK_MSG";
    public static final String KEY_SHOW_SS_LINK_MSG = "KEY_SHOW_SS_LINK_MSG";
    public static final String KEY_SHOW_STEP1GUIDE = "KEY_SHOW_STEP1GUIDE";
    public static final String KEY_SHOW_STEP2GUIDE = "KEY_SHOW_STEP2GUIDE";
    public static final String KEY_SHOW_WB_LINK_MSG = "KEY_SHOW_WB_LINK_MSG";
    private static final String STR_SEMICOLON = ";";
    private static final String TAG = AppLog.getClassName();
    private static GFBackUpKey mInstance = null;
    private static final String[] mWBMode = {"auto", WhiteBalanceController.DAYLIGHT, WhiteBalanceController.SHADE, WhiteBalanceController.CLOUDY, WhiteBalanceController.INCANDESCENT, WhiteBalanceController.WARM_FLUORESCENT, WhiteBalanceController.FLUORESCENT_COOLWHITE, WhiteBalanceController.FLUORESCENT_DAYWHITE, WhiteBalanceController.FLUORESCENT_DAYLIGHT, WhiteBalanceController.FLASH, WhiteBalanceController.UNDERWATER_AUTO, WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.CUSTOM, "custom1", "custom2"};
    private static final String[] mCSMode = {"standard", CreativeStyleController.VIVID, CreativeStyleController.NEUTRAL, CreativeStyleController.CLEAR, CreativeStyleController.DEEP, CreativeStyleController.LIGHT, "portrait", "landscape", "sunset", CreativeStyleController.NIGHT, CreativeStyleController.RED_LEAVES, CreativeStyleController.MONO, CreativeStyleController.SEPIA};
    private static final String STR_EFFECT = "Effect=";
    private static final int LENGTH_STR_EFFECT = STR_EFFECT.length();

    public static GFBackUpKey getInstance() {
        if (mInstance == null) {
            mInstance = new GFBackUpKey();
        }
        return mInstance;
    }

    public String getLastParameters() {
        String key = KEY_PARAM_SKY_BLUE;
        String value = GFThemeController.getInstance().getValue(null);
        if (GFThemeController.BLUESKY.equals(value)) {
            key = KEY_PARAM_SKY_BLUE;
        } else if ("sunset".equals(value)) {
            key = KEY_PARAM_SUNSET;
        } else if ("standard".equals(value)) {
            key = KEY_PARAM_STANDARD;
        } else if ("custom1".equals(value)) {
            key = KEY_PARAM_CUSTOM1;
        } else if ("custom2".equals(value)) {
            key = KEY_PARAM_CUSTOM2;
        } else if (GFThemeController.REVERSE.equals(value)) {
            key = KEY_PARAM_REVERSE;
        } else if (GFThemeController.STRIPE.equals(value)) {
            key = KEY_PARAM_STRIPE;
        }
        String parameter = BackUpUtil.getInstance().getPreferenceString(key, getDefaultParam());
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "getLastParameters(").append(key).append("): ").append(parameter);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return parameter;
    }

    private String getDefaultParam() {
        return GFEffectParameters.getInstance().getParameters().getDefaultFlatten();
    }

    public void saveLastParameters(String parameter) {
        String key = KEY_PARAM_SKY_BLUE;
        String value = GFThemeController.getInstance().getValue(null);
        if (GFThemeController.BLUESKY.equals(value)) {
            key = KEY_PARAM_SKY_BLUE;
        } else if ("sunset".equals(value)) {
            key = KEY_PARAM_SUNSET;
        } else if ("standard".equals(value)) {
            key = KEY_PARAM_STANDARD;
        } else if ("custom1".equals(value)) {
            key = KEY_PARAM_CUSTOM1;
        } else if ("custom2".equals(value)) {
            key = KEY_PARAM_CUSTOM2;
        } else if (GFThemeController.REVERSE.equals(value)) {
            key = KEY_PARAM_REVERSE;
        } else if (GFThemeController.STRIPE.equals(value)) {
            key = KEY_PARAM_STRIPE;
        }
        BackUpUtil.getInstance().setPreference(key, parameter);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "saveLastParameters(").append(key).append("): ").append(parameter);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    public int getLastEffect() {
        String value = GFThemeController.getInstance().getValue(null);
        if (GFThemeController.BLUESKY.equals(value)) {
            return 0;
        }
        if ("sunset".equals(value)) {
            return 1;
        }
        if ("standard".equals(value)) {
            return 2;
        }
        if ("custom1".equals(value)) {
            return 3;
        }
        if ("custom2".equals(value)) {
            return 4;
        }
        if (GFThemeController.REVERSE.equals(value)) {
            return 5;
        }
        if (!GFThemeController.STRIPE.equals(value)) {
            return 0;
        }
        return 6;
    }

    public void saveWBOption(String value, String option, int layer, String theme) {
        String key = getBackUpKeyWBOption(value, layer, theme);
        BackUpUtil.getInstance().setPreference(key, option);
    }

    public String getWBOption(String value, int layer, String theme) {
        String defaultValue = DEFAULT_WB_OPTION;
        String key = getBackUpKeyWBOption(value, layer, theme);
        int themeId = getEffectId(theme);
        if (layer != 0) {
            if (layer == 1) {
                if (theme.equals(GFThemeController.BLUESKY) || theme.equals("sunset")) {
                    if (WhiteBalanceController.COLOR_TEMP.equals(value)) {
                        defaultValue = GFEffectParameters.Parameters.DEFAULT_FILTER_WB_OPTION[themeId];
                    }
                } else if (theme.equals(GFThemeController.STRIPE)) {
                    String defaultMode = GFEffectParameters.Parameters.DEFAULT_FILTER_WB[themeId];
                    if (!GFWhiteBalanceController.getInstance().isSupportedABGM() || GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
                        defaultMode = WhiteBalanceController.COLOR_TEMP;
                    }
                    if (defaultMode.equals(value)) {
                        defaultValue = (GFWhiteBalanceController.getInstance().isSupportedABGM() && defaultMode.equals("auto")) ? "7/0/5500" : "0/0/9900";
                    }
                }
            } else if (layer == 2 && theme.equals(GFThemeController.BLUESKY) && WhiteBalanceController.COLOR_TEMP.equals(value)) {
                defaultValue = GFEffectParameters.Parameters.DEFAULT_LAYER3_WB_OPTION[themeId];
            }
        }
        String option = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        return option;
    }

    public void resetWBOptions(String theme) {
        String[] arr$ = mWBMode;
        for (String wbMode : arr$) {
            saveWBOption(wbMode, DEFAULT_WB_OPTION, 0, theme);
            saveWBOption(wbMode, DEFAULT_WB_OPTION, 1, theme);
            saveWBOption(wbMode, DEFAULT_WB_OPTION, 2, theme);
        }
        saveWBOption(WhiteBalanceController.COLOR_TEMP, GFEffectParameters.Parameters.DEFAULT_LAYER3_WB_OPTION[getEffectId(theme)], 2, theme);
        saveWBOption(WhiteBalanceController.COLOR_TEMP, GFEffectParameters.Parameters.DEFAULT_FILTER_WB_OPTION[getEffectId(theme)], 1, theme);
        saveWBOption(WhiteBalanceController.COLOR_TEMP, GFEffectParameters.Parameters.DEFAULT_BASE_WB_OPTION[getEffectId(theme)], 0, theme);
        if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            saveWBOption("auto", "7/0/5500", 1, GFThemeController.STRIPE);
        }
        saveWBOption(WhiteBalanceController.COLOR_TEMP, "0/0/9900", 1, GFThemeController.STRIPE);
    }

    private void copyWBOptions(String dst, String src) {
        String[] arr$ = mWBMode;
        for (String wbMode : arr$) {
            copyWBOptions(wbMode, 0, dst, src);
            copyWBOptions(wbMode, 1, dst, src);
            copyWBOptions(wbMode, 2, dst, src);
        }
    }

    private void copyWBOptions(String mode, int layer, String dst, String src) {
        String option = getWBOption(mode, layer, src);
        String key = getBackUpKeyWBOption(mode, layer, dst);
        BackUpUtil.getInstance().setPreference(key, option);
    }

    public void checkWBLimit() {
        String value = GFWhiteBalanceLimitController.getInstance().getValue(null);
        if (GFWhiteBalanceLimitController.CTEMP_AWB.equals(value)) {
            limitToAWB();
        } else if (GFWhiteBalanceLimitController.CTEMP.equals(value)) {
            limitToCTemp();
        }
    }

    public void limitToAWB() {
        String theme = GFThemeController.getInstance().getValue();
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        boolean isUpdateCommonSetting = false;
        String wbMode = param.getRawWBMode(0);
        if (!isCTempOrAWB(wbMode)) {
            wbMode = "auto";
            param.setWBMode(0, "auto");
            if (GFLinkAreaController.getInstance().isWBLink(0)) {
                String wbOption = getWBOption("auto", 0, theme);
                setCommonWB("auto", theme);
                setCommonWBOption(wbOption, theme);
                if (GFLinkAreaController.getInstance().isWBLink(1)) {
                    param.setWBMode(1, "auto");
                    getInstance().saveWBOption("auto", wbOption, 1, theme);
                }
                if (GFLinkAreaController.getInstance().isWBLink(2)) {
                    param.setWBMode(2, "auto");
                    getInstance().saveWBOption("auto", wbOption, 2, theme);
                }
                isUpdateCommonSetting = true;
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.LAND, theme)) {
            setCommonWB(wbMode, theme);
            setCommonWBOption(getWBOption(wbMode, 0, theme), theme);
        }
        String wbMode2 = param.getRawWBMode(1);
        if (!isCTempOrAWB(wbMode2)) {
            wbMode2 = "auto";
            param.setWBMode(1, "auto");
            if (GFLinkAreaController.getInstance().isWBLink(1) && !isUpdateCommonSetting) {
                String wbOption2 = getWBOption("auto", 1, theme);
                setCommonWB("auto", theme);
                setCommonWBOption(wbOption2, theme);
                if (GFLinkAreaController.getInstance().isWBLink(2)) {
                    param.setWBMode(2, "auto");
                    getInstance().saveWBOption("auto", wbOption2, 2, theme);
                }
                isUpdateCommonSetting = true;
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.SKY, theme)) {
            setCommonWB(wbMode2, theme);
            setCommonWBOption(getWBOption(wbMode2, 1, theme), theme);
        }
        String wbMode3 = param.getRawWBMode(2);
        if (!isCTempOrAWB(wbMode3)) {
            wbMode3 = "auto";
            param.setWBMode(2, "auto");
            if (GFLinkAreaController.getInstance().isWBLink(2) && !isUpdateCommonSetting) {
                String wbOption3 = getWBOption("auto", 2, theme);
                setCommonWB("auto", theme);
                setCommonWBOption(wbOption3, theme);
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.LAYER3, theme)) {
            setCommonWB(wbMode3, theme);
            setCommonWBOption(getWBOption(wbMode3, 2, theme), theme);
        }
    }

    public void limitToCTemp() {
        String theme = GFThemeController.getInstance().getValue();
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        boolean isUpdateCommonSetting = false;
        if (!isCTemp(param.getRawWBMode(0))) {
            param.setWBMode(0, WhiteBalanceController.COLOR_TEMP);
            if (GFLinkAreaController.getInstance().isWBLink(0)) {
                String wbOption = getWBOption(WhiteBalanceController.COLOR_TEMP, 0, theme);
                setCommonWB(WhiteBalanceController.COLOR_TEMP, theme);
                setCommonWBOption(wbOption, theme);
                if (GFLinkAreaController.getInstance().isWBLink(1)) {
                    param.setWBMode(1, WhiteBalanceController.COLOR_TEMP);
                    getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, wbOption, 1, theme);
                }
                if (GFLinkAreaController.getInstance().isWBLink(2)) {
                    param.setWBMode(2, WhiteBalanceController.COLOR_TEMP);
                    getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, wbOption, 2, theme);
                }
                isUpdateCommonSetting = true;
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.LAND, theme)) {
            setCommonWB(param.getRawWBMode(0), theme);
            setCommonWBOption(getWBOption(WhiteBalanceController.COLOR_TEMP, 0, theme), theme);
        }
        if (!isCTemp(param.getRawWBMode(1))) {
            param.setWBMode(1, WhiteBalanceController.COLOR_TEMP);
            if (GFLinkAreaController.getInstance().isWBLink(1) && !isUpdateCommonSetting) {
                String wbOption2 = getWBOption(WhiteBalanceController.COLOR_TEMP, 1, theme);
                setCommonWB(WhiteBalanceController.COLOR_TEMP, theme);
                setCommonWBOption(wbOption2, theme);
                if (GFLinkAreaController.getInstance().isWBLink(2)) {
                    param.setWBMode(2, WhiteBalanceController.COLOR_TEMP);
                    getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, wbOption2, 2, theme);
                }
                isUpdateCommonSetting = true;
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.SKY, theme)) {
            setCommonWB(param.getRawWBMode(1), theme);
            setCommonWBOption(getWBOption(WhiteBalanceController.COLOR_TEMP, 1, theme), theme);
        }
        if (!isCTemp(param.getRawWBMode(2))) {
            param.setWBMode(2, WhiteBalanceController.COLOR_TEMP);
            if (GFLinkAreaController.getInstance().isWBLink(2) && !isUpdateCommonSetting) {
                String wbOption3 = getWBOption(WhiteBalanceController.COLOR_TEMP, 2, theme);
                setCommonWB(WhiteBalanceController.COLOR_TEMP, theme);
                setCommonWBOption(wbOption3, theme);
            }
        }
        if (GFLinkAreaController.getInstance().isWBParent(GFLinkAreaController.LAYER3, theme)) {
            setCommonWB(param.getRawWBMode(2), theme);
            setCommonWBOption(getWBOption(WhiteBalanceController.COLOR_TEMP, 2, theme), theme);
        }
    }

    private boolean isCTempOrAWB(String w) {
        return "auto".equals(w) || WhiteBalanceController.COLOR_TEMP.equals(w);
    }

    private boolean isCTemp(String w) {
        return WhiteBalanceController.COLOR_TEMP.equals(w);
    }

    public String getBackUpKeyWB(String mode, int layer, String theme) {
        String ret = null;
        if (layer == 0) {
            ret = "ID_BASE_WB_" + getEffect(theme) + "_";
        } else if (layer == 1) {
            ret = "ID_FILTER_WB_" + getEffect(theme) + "_";
        } else if (layer == 2) {
            ret = "ID_LAYER3_WB_" + getEffect(theme) + "_";
        }
        if ("auto".equals(mode)) {
            return ret + "AUTO";
        }
        if (WhiteBalanceController.DAYLIGHT.equals(mode)) {
            return ret + "DAYLIGHT";
        }
        if (WhiteBalanceController.SHADE.equals(mode)) {
            return ret + "SHADE";
        }
        if (WhiteBalanceController.CLOUDY.equals(mode)) {
            return ret + "CLOUDY";
        }
        if (WhiteBalanceController.INCANDESCENT.equals(mode)) {
            return ret + "INCANDESCENT";
        }
        if (WhiteBalanceController.WARM_FLUORESCENT.equals(mode)) {
            return ret + "FLOUR_WARM_WHITE";
        }
        if (WhiteBalanceController.FLUORESCENT_COOLWHITE.equals(mode)) {
            return ret + "FLOUR_COOL_WHITE";
        }
        if (WhiteBalanceController.FLUORESCENT_DAYWHITE.equals(mode)) {
            return ret + "FLOUR_DAY_WHITE";
        }
        if (WhiteBalanceController.FLUORESCENT_DAYLIGHT.equals(mode)) {
            return ret + "FLOUR_DAYLIGHT";
        }
        if (WhiteBalanceController.FLASH.equals(mode)) {
            return ret + "FLASH";
        }
        if (WhiteBalanceController.UNDERWATER_AUTO.equals(mode)) {
            return ret + "UNDERWATER_AUTO";
        }
        if (WhiteBalanceController.COLOR_TEMP.equals(mode)) {
            return ret + "CTEMP_FILTER";
        }
        if (WhiteBalanceController.CUSTOM.equals(mode)) {
            return ret + "CUSTOM";
        }
        if ("custom1".equals(mode)) {
            return ret + "CUSTOM";
        }
        if ("custom2".equals(mode)) {
            return ret + "CUSTOM2";
        }
        if ("custom3".equals(mode)) {
            return ret + "CUSTOM3";
        }
        Log.e(TAG, "getBackUpKeyWB() cannot find a key.");
        return null;
    }

    public String getBackUpKeyWBOption(String mode, int layer, String theme) {
        String ret = getBackUpKeyWB(mode, layer, theme);
        if (ret != null) {
            return ret + "_OPTION";
        }
        return ret;
    }

    public void saveCSOption(String value, String option, String theme) {
        String key = getBackUpKeyCSOption(value, theme);
        BackUpUtil.getInstance().setPreference(key, option);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "saveCSOption(key) = ").append(key);
        AppLog.info(TAG, builder.toString());
        builder.replace(0, builder.length(), "saveCSOption = ").append(option);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    public String getCSOption(String value, String theme) {
        String defaultValue;
        String key = getBackUpKeyCSOption(value, theme);
        if (value == GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLE[getEffectId(theme)]) {
            defaultValue = GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLEOPTION[getEffectId(theme)];
        } else {
            defaultValue = "0/0/0";
        }
        String option = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "getCSOption(key) = ").append(key);
        AppLog.info(TAG, builder.toString());
        builder.replace(0, builder.length(), "getCSOption = ").append(option);
        AppLog.info(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return option;
    }

    public void resetCSOptions(String theme) {
        String[] arr$ = mCSMode;
        for (String csMode : arr$) {
            saveCSOption(csMode, "0/0/0", theme);
        }
        saveCSOption(GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLE[getEffectId(theme)], GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLEOPTION[getEffectId(theme)], theme);
    }

    private void copyCSOptions(String dst, String src) {
        String[] arr$ = mCSMode;
        for (String csMode : arr$) {
            String option = getCSOption(csMode, src);
            saveCSOption(csMode, option, dst);
        }
    }

    public void saveMeteringSpotValue(String value, String theme) {
        String key = getMeteringSpotBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetMeteringSpotValue(String theme) {
        String key = getMeteringSpotBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, MeteringController.STANDARD);
    }

    public void copyMeteringSpotValue(String dst, String src) {
        String value = getMeteringSpotValue(src);
        String key = getMeteringSpotBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getMeteringSpotValue(String theme) {
        String key = getMeteringSpotBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, MeteringController.STANDARD);
        return value;
    }

    public String getMeteringSpotBackUpKey(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_METERING_SPOT";
        return key;
    }

    public void saveDeviceDirection(float rad, String theme) {
        String key = getBackUpKeyDeviceDirection(theme);
        BackUpUtil.getInstance().setPreference(key, Float.valueOf(rad));
    }

    public float getDeviceDirection(String theme) {
        String key = getBackUpKeyDeviceDirection(theme);
        float rad = BackUpUtil.getInstance().getPreferenceFloat(key, 0.0f);
        return rad;
    }

    public void resetDeviceDirection(String theme) {
        saveDeviceDirection(0.0f, theme);
    }

    private void copyDeviceDirection(String dst, String src) {
        float rad = getDeviceDirection(src);
        saveDeviceDirection(rad, dst);
    }

    public void copyParameters(String srcTheme) {
        String currentTheme = GFThemeController.getInstance().getValue();
        copyWBOptions(currentTheme, srcTheme);
        copyCSOptions(currentTheme, srcTheme);
        copyMeteringSpotValue(currentTheme, srcTheme);
        copyDeviceDirection(currentTheme, srcTheme);
        copyLinkValue(currentTheme, srcTheme);
        copyEEArea(currentTheme, srcTheme);
        copyShootingOrdera(currentTheme, srcTheme);
        copyFilterSetValue(currentTheme, srcTheme);
        copyIntervalValue(currentTheme, srcTheme);
        copyPositionLinkValue(currentTheme, srcTheme);
        copyShadingLinkValue(currentTheme, srcTheme);
        String parameter = getParameters(srcTheme);
        String parameter2 = replaceEffect(parameter, currentTheme);
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.unflatten(parameter2);
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTempAWB()) {
            getInstance().limitToAWB();
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            getInstance().limitToCTemp();
        }
        GFEffectParameters.getInstance().setParameters(params);
        saveLastParameters(params.flatten());
        if (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS)) {
            GFSettingMenuLayout.disable3rdArea();
        }
        GFCommonUtil.getInstance().setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
    }

    public String replaceEffect(String parameter, String currentEffect) {
        int result = parameter.indexOf(STR_EFFECT);
        String str0 = parameter.substring(0, LENGTH_STR_EFFECT + result);
        String str1 = parameter.substring(LENGTH_STR_EFFECT + result);
        return str0 + getEffectId(currentEffect) + str1.substring(str1.indexOf(";"));
    }

    public String getParameters(String value) {
        String key;
        int effect = getEffectId(value);
        if (GFThemeController.BLUESKY.equals(value)) {
            key = KEY_PARAM_SKY_BLUE;
        } else if ("sunset".equals(value)) {
            key = KEY_PARAM_SUNSET;
        } else if ("standard".equals(value)) {
            key = KEY_PARAM_STANDARD;
        } else if ("custom1".equals(value)) {
            key = KEY_PARAM_CUSTOM1;
        } else if ("custom2".equals(value)) {
            key = KEY_PARAM_CUSTOM2;
        } else if (GFThemeController.REVERSE.equals(value)) {
            key = KEY_PARAM_REVERSE;
        } else {
            if (!GFThemeController.STRIPE.equals(value)) {
                return null;
            }
            key = KEY_PARAM_STRIPE;
        }
        String parameter = BackUpUtil.getInstance().getPreferenceString(key, null);
        if (parameter == null) {
            setDefaultParameters(key, effect);
            parameter = BackUpUtil.getInstance().getPreferenceString(key, null);
        }
        return parameter;
    }

    private void setDefaultParameters(String key, int targetEffect) {
        int currentEffect = getLastEffect();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setEffect(targetEffect);
        String defaultValue = getDefaultParam();
        BackUpUtil.getInstance().setPreference(key, defaultValue);
        params.setEffect(currentEffect);
    }

    public String getBackUpKeyCS(String mode, String theme) {
        String ret = "ID_CS_" + getEffect(theme) + "_";
        if ("standard".equals(mode)) {
            return ret + "STD_";
        }
        if (CreativeStyleController.VIVID.equals(mode)) {
            return ret + "VIVID_";
        }
        if (CreativeStyleController.NEUTRAL.equals(mode)) {
            return ret + "NTRL_";
        }
        if (CreativeStyleController.CLEAR.equals(mode)) {
            return ret + "CLEAR_";
        }
        if (CreativeStyleController.DEEP.equals(mode)) {
            return ret + "DEEP_";
        }
        if (CreativeStyleController.LIGHT.equals(mode)) {
            return ret + "LIGHT_";
        }
        if ("portrait".equals(mode)) {
            return ret + "PORT_";
        }
        if ("landscape".equals(mode)) {
            return ret + "LAND_";
        }
        if ("sunset".equals(mode)) {
            return ret + "SUNSET_";
        }
        if (CreativeStyleController.NIGHT.equals(mode)) {
            return ret + "NIGHT_";
        }
        if (CreativeStyleController.RED_LEAVES.equals(mode)) {
            return ret + "AUTM_";
        }
        if (CreativeStyleController.MONO.equals(mode)) {
            return ret + "MONO_";
        }
        if (CreativeStyleController.SEPIA.equals(mode)) {
            return ret + "SEPIA_";
        }
        Log.e(TAG, "getBackUpKeyCS() cannot find a key.");
        return null;
    }

    public String getBackUpKeyCSOption(String mode, String theme) {
        String ret = getBackUpKeyCS(mode, theme);
        if (ret != null) {
            return ret + "_OPTION";
        }
        return ret;
    }

    private String getBackUpKeyDeviceDirection(String theme) {
        String key = "ID_RAD_" + getEffect(theme);
        return key;
    }

    private static int getEffectId(String effect) {
        if (GFThemeController.BLUESKY.equals(effect)) {
            return 0;
        }
        if ("sunset".equals(effect)) {
            return 1;
        }
        if ("standard".equals(effect)) {
            return 2;
        }
        if ("custom1".equals(effect)) {
            return 3;
        }
        if ("custom2".equals(effect)) {
            return 4;
        }
        if (GFThemeController.REVERSE.equals(effect)) {
            return 5;
        }
        if (!GFThemeController.STRIPE.equals(effect)) {
            return 0;
        }
        return 6;
    }

    public String getShootingOrder(String theme) {
        String key = getShootingOrderBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFShootingOrderController.ORDER_123);
        return value;
    }

    public void saveShootingOrder(String value, String theme) {
        String key = getShootingOrderBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetShootingOrder(String theme) {
        String key = getShootingOrderBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, GFShootingOrderController.ORDER_123);
    }

    public void copyShootingOrdera(String dst, String src) {
        String key = getShootingOrderBackUpKey(src);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFShootingOrderController.ORDER_123);
        String key2 = getShootingOrderBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key2, value);
    }

    private String getShootingOrderBackUpKey(String theme) {
        String ret = "ID_GF_" + getEffect(theme) + "_SHOOTING_ORDER";
        return ret;
    }

    public String getEEArea(String theme) {
        String key = getEEAreaBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFEEAreaController.FIRST);
        return value;
    }

    public void saveEEArea(String value, String theme) {
        String key = getEEAreaBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetEEArea(String theme) {
        String key = getEEAreaBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, GFEEAreaController.FIRST);
    }

    public void copyEEArea(String dst, String src) {
        String key = getEEAreaBackUpKey(src);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFEEAreaController.FIRST);
        String key2 = getEEAreaBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key2, value);
    }

    private String getEEAreaBackUpKey(String theme) {
        String ret = "ID_GF_" + getEffect(theme) + "_EE_AREA";
        return ret;
    }

    public String getFilterSetValue(String theme) {
        String key = getFilterSetBackUpKey(theme);
        String defaultValue = GFFilterSetController.TWO_AREAS;
        if (theme.equals(GFThemeController.REVERSE) || theme.equals(GFThemeController.STRIPE)) {
            defaultValue = GFFilterSetController.THREE_AREAS;
        }
        String value = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        return value;
    }

    public void saveFilterSetValue(String value, String theme) {
        String key = getFilterSetBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetFilterSetValue(String theme) {
        String key = getFilterSetBackUpKey(theme);
        String defaultValue = GFFilterSetController.TWO_AREAS;
        if (theme.equals(GFThemeController.REVERSE) || theme.equals(GFThemeController.STRIPE)) {
            defaultValue = GFFilterSetController.THREE_AREAS;
        }
        BackUpUtil.getInstance().setPreference(key, defaultValue);
    }

    public void copyFilterSetValue(String dst, String src) {
        String key = getFilterSetBackUpKey(src);
        String defaultValue = GFFilterSetController.TWO_AREAS;
        if (src.equals(GFThemeController.REVERSE) || src.equals(GFThemeController.STRIPE)) {
            defaultValue = GFFilterSetController.THREE_AREAS;
        }
        String value = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        String key2 = getFilterSetBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key2, value);
    }

    private String getFilterSetBackUpKey(String theme) {
        String ret = "ID_GF_" + getEffect(theme) + "_FILTER_SET";
        return ret;
    }

    private String getIntervalBackUpKey(String tag, String theme) {
        String ret = "ID_GF_" + getEffect(theme) + "_";
        if (tag.equals(GFIntervalController.INTERVAL_MODE)) {
            return ret + "INTERVAL_MODE";
        }
        if (tag.equals(GFIntervalController.INTERVAL_TIME)) {
            return ret + "INTERVAL_TIME";
        }
        if (tag.equals(GFIntervalController.INTERVAL_SHOTS)) {
            return ret + "INTERVAL_SHOTS";
        }
        if (tag.equals(GFIntervalController.INTERVAL_AE)) {
            return ret + "INTERVAL_AE";
        }
        return ret;
    }

    public void saveIntervalValue(String tag, String value, String theme) {
        String key = getIntervalBackUpKey(tag, theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetIntervalValue(String theme) {
        String key = getIntervalBackUpKey(GFIntervalController.INTERVAL_MODE, theme);
        BackUpUtil.getInstance().setPreference(key, GFIntervalController.INTERVAL_OFF);
        String key2 = getIntervalBackUpKey(GFIntervalController.INTERVAL_TIME, theme);
        BackUpUtil.getInstance().setPreference(key2, Integer.toString(10));
        String key3 = getIntervalBackUpKey(GFIntervalController.INTERVAL_SHOTS, theme);
        BackUpUtil.getInstance().setPreference(key3, Integer.toString(GFIntervalController.DEFAULT_SHOTS));
        String key4 = getIntervalBackUpKey(GFIntervalController.INTERVAL_AE, theme);
        BackUpUtil.getInstance().setPreference(key4, "ae-lock");
    }

    public void copyIntervalValue(String dst, String src) {
        String key = getIntervalBackUpKey(GFIntervalController.INTERVAL_MODE, src);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFIntervalController.INTERVAL_OFF);
        String key2 = getIntervalBackUpKey(GFIntervalController.INTERVAL_MODE, dst);
        BackUpUtil.getInstance().setPreference(key2, value);
        String key3 = getIntervalBackUpKey(GFIntervalController.INTERVAL_TIME, src);
        String value2 = BackUpUtil.getInstance().getPreferenceString(key3, Integer.toString(10));
        String key4 = getIntervalBackUpKey(GFIntervalController.INTERVAL_TIME, dst);
        BackUpUtil.getInstance().setPreference(key4, value2);
        String key5 = getIntervalBackUpKey(GFIntervalController.INTERVAL_SHOTS, src);
        String value3 = BackUpUtil.getInstance().getPreferenceString(key5, Integer.toString(GFIntervalController.DEFAULT_SHOTS));
        String key6 = getIntervalBackUpKey(GFIntervalController.INTERVAL_SHOTS, dst);
        BackUpUtil.getInstance().setPreference(key6, value3);
        String key7 = getIntervalBackUpKey(GFIntervalController.INTERVAL_AE, src);
        String value4 = BackUpUtil.getInstance().getPreferenceString(key7, "ae-lock");
        String key8 = getIntervalBackUpKey(GFIntervalController.INTERVAL_AE, dst);
        BackUpUtil.getInstance().setPreference(key8, value4);
    }

    public String getIntervalValue(String tag, String theme) {
        String defaultValue;
        String key = getIntervalBackUpKey(tag, theme);
        if (tag.equals(GFIntervalController.INTERVAL_MODE)) {
            defaultValue = GFIntervalController.INTERVAL_OFF;
        } else if (tag.equals(GFIntervalController.INTERVAL_TIME)) {
            defaultValue = "10";
        } else if (tag.equals(GFIntervalController.INTERVAL_SHOTS)) {
            defaultValue = "240";
        } else if (tag.equals(GFIntervalController.INTERVAL_AE)) {
            defaultValue = "ae-lock";
        } else {
            return null;
        }
        return BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
    }

    public void saveLinkValue(String tag, String value, String theme) {
        String key = getLinkBackUpKey(tag, theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetLinkValue(String theme) {
        String[] arr$ = GFLinkAreaController.getInstance().getLinkTags();
        for (String tag : arr$) {
            String key = getLinkBackUpKey(tag, theme);
            String defaultValue = GFLinkAreaController.getInstance().getDefaultValue(tag, theme);
            BackUpUtil.getInstance().setPreference(key, defaultValue);
        }
        setParentExpComp("none", theme);
        setParentAperture("none", theme);
        setParentSS("none", theme);
        setParentISO("none", theme);
        setParentWB("none", theme);
        GFLinkAreaController.getInstance().checkParent(theme);
        setCommonExpComp(getDefaultExpComp(theme), theme);
        setCommonAperture(getDefaultAperture(theme), theme);
        setCommonSsNumerator(getDefaultSsNumerator(theme), theme);
        setCommonSsDenominator(getDefaultSsDenominator(theme), theme);
        setCommonISO(getDefaultISO(theme), theme);
        setCommonWB(getDefaultWB(theme), theme);
        setCommonWBOption(getDefaultWBOption(theme), theme);
    }

    public void copyLinkValue(String dst, String src) {
        String[] arr$ = GFLinkAreaController.getInstance().getLinkTags();
        for (String tag : arr$) {
            String value = getLinkValue(tag, src);
            String key = getLinkBackUpKey(tag, dst);
            BackUpUtil.getInstance().setPreference(key, value);
        }
        setCommonExpComp(getCommonExpComp(src), dst);
        setCommonAperture(getCommonAperture(src), dst);
        setCommonSsNumerator(getCommonSsNumerator(src), dst);
        setCommonSsDenominator(getCommonSsDenominator(src), dst);
        setCommonISO(getCommonISO(src), dst);
        setCommonWB(getCommonWB(src), dst);
        setCommonWBOption(getCommonWBOption(src), dst);
        setParentExpComp(getParentExpComp(src), dst);
        setParentAperture(getParentAperture(src), dst);
        setParentSS(getParentSS(src), dst);
        setParentISO(getParentISO(src), dst);
        setParentWB(getParentWB(src), dst);
        GFLinkAreaController.getInstance().checkParent(dst);
    }

    public String getLinkValue(String tag, String theme) {
        String key = getLinkBackUpKey(tag, theme);
        String defaultValue = GFLinkAreaController.getInstance().getDefaultValue(tag, theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        return value;
    }

    public String getLinkBackUpKey(String tag, String theme) {
        String ret = "ID_GF_" + getEffect(theme) + "_";
        if (tag.equals(GFLinkAreaController.LAND_APERTURE)) {
            return ret + "LAND_APERTURE_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAND_SS)) {
            return ret + "LAND_SS_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAND_EXPCOMP)) {
            return ret + "LAND_EXPCOMP_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAND_ISO)) {
            return ret + "LAND_ISO_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAND_WB)) {
            return ret + "LAND_WB_LINK";
        }
        if (tag.equals(GFLinkAreaController.SKY_APERTURE)) {
            return ret + "SKY_APERTURE_LINK";
        }
        if (tag.equals(GFLinkAreaController.SKY_SS)) {
            return ret + "SKY_SS_LINK";
        }
        if (tag.equals(GFLinkAreaController.SKY_EXPCOMP)) {
            return ret + "SKY_EXPCOMP_LINK";
        }
        if (tag.equals(GFLinkAreaController.SKY_ISO)) {
            return ret + "SKY_ISO_LINK";
        }
        if (tag.equals(GFLinkAreaController.SKY_WB)) {
            return ret + "SKY_WB_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAYER3_APERTURE)) {
            return ret + "LAYER3_APERTURE_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAYER3_SS)) {
            return ret + "LAYER3_SS_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAYER3_EXPCOMP)) {
            return ret + "LAYER3_EXPCOMP_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAYER3_ISO)) {
            return ret + "LAYER3_ISO_LINK";
        }
        if (tag.equals(GFLinkAreaController.LAYER3_WB)) {
            return ret + "LAYER3_WB_LINK";
        }
        return ret;
    }

    public void setCommonExpComp(String value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_EXPCOMP";
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getDefaultExpComp(String theme) {
        String defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_EXPCOMP[getEffectId(theme)];
        return defaultValue;
    }

    public String getCommonExpComp(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_EXPCOMP";
        String defaultValue = getDefaultExpComp(theme);
        return BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
    }

    public void setCommonAperture(int value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_APERTURE";
        BackUpUtil.getInstance().setPreference(key, Integer.valueOf(value));
    }

    private int getDefaultAperture(String theme) {
        int defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_APERTURE[getEffectId(theme)];
        if (theme.equals(GFThemeController.BLUESKY)) {
            if (GFCommonUtil.getInstance().isRX100()) {
                return 1100;
            }
            return defaultValue;
        }
        if (theme.equals("sunset")) {
            if (GFCommonUtil.getInstance().isRX10() || GFCommonUtil.getInstance().isRX100()) {
                return 560;
            }
            return defaultValue;
        }
        if (theme.equals("standard")) {
            if (GFCommonUtil.getInstance().isRX100()) {
                return 560;
            }
            return defaultValue;
        }
        if (theme.equals("custom1")) {
            if (GFCommonUtil.getInstance().isRX100()) {
                return 560;
            }
            return defaultValue;
        }
        if (theme.equals("custom2")) {
            if (GFCommonUtil.getInstance().isRX100()) {
                return 560;
            }
            return defaultValue;
        }
        if (theme.equals(GFThemeController.REVERSE)) {
            if (GFCommonUtil.getInstance().isRX100()) {
                return 560;
            }
            return defaultValue;
        }
        if (theme.equals(GFThemeController.STRIPE) && GFCommonUtil.getInstance().isRX100()) {
            return 560;
        }
        return defaultValue;
    }

    public int getCommonAperture(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_APERTURE";
        int defaultValue = getDefaultAperture(theme);
        return BackUpUtil.getInstance().getPreferenceInt(key, defaultValue);
    }

    public void setCommonSsNumerator(int value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_SS_NUM";
        BackUpUtil.getInstance().setPreference(key, Integer.valueOf(value));
    }

    private int getDefaultSsNumerator(String theme) {
        int defaultValue = ((Integer) GFEffectParameters.Parameters.DEFAULT_BASE_SS.get(getEffectId(theme)).first).intValue();
        if (theme.equals(GFThemeController.BLUESKY)) {
            if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                return 1;
            }
            return defaultValue;
        }
        if (theme.equals("sunset")) {
            if (GFCommonUtil.getInstance().isRX10()) {
                return 1;
            }
            if (GFCommonUtil.getInstance().isRX100()) {
                return 10;
            }
            return defaultValue;
        }
        if (theme.equals("standard")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals("custom1")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals("custom2")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals(GFThemeController.REVERSE)) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (!theme.equals(GFThemeController.STRIPE) || GFCommonUtil.getInstance().isRX100()) {
        }
        return defaultValue;
    }

    public int getCommonSsNumerator(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_SS_NUM";
        int defaultValue = getDefaultSsNumerator(theme);
        return BackUpUtil.getInstance().getPreferenceInt(key, defaultValue);
    }

    public void setCommonSsDenominator(int value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_SS_DEN";
        BackUpUtil.getInstance().setPreference(key, Integer.valueOf(value));
    }

    public int getDefaultSsDenominator(String theme) {
        int defaultValue = ((Integer) GFEffectParameters.Parameters.DEFAULT_BASE_SS.get(getEffectId(theme)).second).intValue();
        if (theme.equals(GFThemeController.BLUESKY)) {
            if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                return 160;
            }
            return defaultValue;
        }
        if (theme.equals("sunset")) {
            if (GFCommonUtil.getInstance().isRX10()) {
                return 2;
            }
            if (GFCommonUtil.getInstance().isRX100()) {
                return 25;
            }
            return defaultValue;
        }
        if (theme.equals("standard")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals("custom1")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals("custom2")) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (theme.equals(GFThemeController.REVERSE)) {
            if (GFCommonUtil.getInstance().isRX100()) {
            }
            return defaultValue;
        }
        if (!theme.equals(GFThemeController.STRIPE) || GFCommonUtil.getInstance().isRX100()) {
        }
        return defaultValue;
    }

    public int getCommonSsDenominator(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_SS_DEN";
        int defaultValue = getDefaultSsDenominator(theme);
        return BackUpUtil.getInstance().getPreferenceInt(key, defaultValue);
    }

    public void setCommonISO(String value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_ISO";
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getDefaultISO(String theme) {
        String defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_ISO[getEffectId(theme)];
        if (GFCommonUtil.getInstance().isRX100()) {
            return ISOSensitivityController.ISO_125;
        }
        return defaultValue;
    }

    public String getCommonISO(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_ISO";
        String defaultValue = getDefaultISO(theme);
        return BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
    }

    public void setCommonWB(String value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_WB";
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getDefaultWB(String theme) {
        String defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_WB[getEffectId(theme)];
        if (theme.equals(GFThemeController.STRIPE) && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            return "auto";
        }
        return defaultValue;
    }

    public String getCommonWB(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_WB";
        String defaultValue = getDefaultWB(theme);
        return BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
    }

    public void setCommonWBOption(String value, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_WBOPTION";
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getDefaultWBOption(String theme) {
        String defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_WB_OPTION[getEffectId(theme)];
        return defaultValue;
    }

    public String getCommonWBOption(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_COMMON_WBOPTION";
        String defaultValue = getDefaultWBOption(theme);
        return BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
    }

    public void setParentExpComp(String tag, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_EXPCOMP";
        BackUpUtil.getInstance().setPreference(key, tag);
    }

    public String getParentExpComp(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_EXPCOMP";
        return BackUpUtil.getInstance().getPreferenceString(key, "none");
    }

    public void setParentAperture(String tag, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_APERTURE";
        BackUpUtil.getInstance().setPreference(key, tag);
    }

    public String getParentAperture(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_APERTURE";
        return BackUpUtil.getInstance().getPreferenceString(key, "none");
    }

    public void setParentSS(String tag, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_SS";
        BackUpUtil.getInstance().setPreference(key, tag);
    }

    public String getParentSS(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_SS";
        return BackUpUtil.getInstance().getPreferenceString(key, "none");
    }

    public void setParentISO(String tag, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_ISO";
        BackUpUtil.getInstance().setPreference(key, tag);
    }

    public String getParentISO(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_ISO";
        return BackUpUtil.getInstance().getPreferenceString(key, "none");
    }

    public void setParentWB(String tag, String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_WB";
        BackUpUtil.getInstance().setPreference(key, tag);
    }

    public String getParentWB(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_PARENT_WB";
        return BackUpUtil.getInstance().getPreferenceString(key, "none");
    }

    public void savePositionLinkValue(String value, String theme) {
        String key = getPositionLinkBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetPositionLinkValue(String theme) {
        String key = getPositionLinkBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, GFPositionLinkController.OFF);
    }

    public void copyPositionLinkValue(String dst, String src) {
        String value = getPositionLinkValue(src);
        String key = getPositionLinkBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getPositionLinkValue(String theme) {
        String key = getPositionLinkBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFPositionLinkController.OFF);
        return value;
    }

    public String getPositionLinkBackUpKey(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_POS_LINK";
        return key;
    }

    public void saveShadingLinkValue(String value, String theme) {
        String key = getShadingLinkBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetShadingLinkValue(String theme) {
        String key = getShadingLinkBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, GFShadingLinkController.OFF);
    }

    public void copyShadingLinkValue(String dst, String src) {
        String value = getShadingLinkValue(src);
        String key = getShadingLinkBackUpKey(dst);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getShadingLinkValue(String theme) {
        String key = getShadingLinkBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFShadingLinkController.OFF);
        return value;
    }

    public String getShadingLinkBackUpKey(String theme) {
        String key = "ID_GF_" + getEffect(theme) + "_SHADING_LINK";
        return key;
    }

    public void saveVerticalLinkValue(String value) {
        String key = getVerticalLinkBackUpKey();
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getVerticalLinkValue() {
        String key = getVerticalLinkBackUpKey();
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFVerticalLinkController.VERTICAL);
        return value;
    }

    public String getVerticalLinkBackUpKey() {
        return "ID_GF_VERTICAL_LINK";
    }

    public void saveClippingCorrectionValue(String value) {
        String key = getClippingCorrectionBackUpKey();
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public String getClippingCorrectionValue() {
        String key = getClippingCorrectionBackUpKey();
        String value = BackUpUtil.getInstance().getPreferenceString(key, GFClippingCorrectionController.NORMAL);
        return value;
    }

    public String getClippingCorrectionBackUpKey() {
        return "ID_GF_CLIPPING_CORRECTION";
    }

    private static String getEffect(String theme) {
        if (theme.equals(GFThemeController.BLUESKY)) {
            return "SKY_BLUE";
        }
        if (theme.equals("sunset")) {
            return "SUNSET";
        }
        if (theme.equals("standard")) {
            return "ND";
        }
        if (theme.equals("custom1")) {
            return "CUSTOM1";
        }
        if (theme.equals("custom2")) {
            return "CUSTOM2";
        }
        if (theme.equals(GFThemeController.REVERSE)) {
            return "REVERSE";
        }
        if (!theme.equals(GFThemeController.STRIPE)) {
            return "SKY_BLUE";
        }
        return "STRIPE";
    }
}
