package com.sony.imaging.app.graduatedfilter;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFBackUpKey {
    public static final String KEY_HISTGRAM_VIEW = "KEY_HISTGRAM_VIEW";
    public static final String KEY_PARAM_CUSTOM1 = "KEY_PARAM_CUSTOM1";
    public static final String KEY_PARAM_CUSTOM2 = "KEY_PARAM_CUSTOM2";
    public static final String KEY_PARAM_SKY_BLUE = "KEY_PARAM_SKY_BLUE";
    public static final String KEY_PARAM_STANDARD = "KEY_PARAM_STANDARD";
    public static final String KEY_PARAM_SUNSET = "KEY_PARAM_SUNSET";
    public static final String KEY_SELECTED_EFFECT = "SELECTED_EFFECT";
    public static final String KEY_SHOW_ADJUSTGUIDE = "KEY_SHOW_ADJUSTGUIDE";
    public static final String KEY_SHOW_INTRODUCTION = "KEY_SHOW_INTRODUCTION";
    public static final String KEY_SHOW_STEP1GUIDE = "KEY_SHOW_STEP1GUIDE";
    public static final String KEY_SHOW_STEP2GUIDE = "KEY_SHOW_STEP2GUIDE";
    private static final String TAG = AppLog.getClassName();
    private static GFBackUpKey mInstance = null;
    private static final String[] mWBMode = {"auto", WhiteBalanceController.DAYLIGHT, WhiteBalanceController.SHADE, WhiteBalanceController.CLOUDY, WhiteBalanceController.INCANDESCENT, WhiteBalanceController.WARM_FLUORESCENT, WhiteBalanceController.FLUORESCENT_COOLWHITE, WhiteBalanceController.FLUORESCENT_DAYWHITE, WhiteBalanceController.FLUORESCENT_DAYLIGHT, WhiteBalanceController.FLASH, WhiteBalanceController.UNDERWATER_AUTO, WhiteBalanceController.COLOR_TEMP};
    private static final String[] mCSMode = {"standard", CreativeStyleController.VIVID, CreativeStyleController.NEUTRAL, CreativeStyleController.CLEAR, CreativeStyleController.DEEP, CreativeStyleController.LIGHT, "portrait", "landscape", "sunset", CreativeStyleController.NIGHT, CreativeStyleController.RED_LEAVES, CreativeStyleController.MONO, CreativeStyleController.SEPIA};

    public static GFBackUpKey getInstance() {
        if (mInstance == null) {
            mInstance = new GFBackUpKey();
        }
        return mInstance;
    }

    public String getLastParameters() {
        String parameter = null;
        String value = GFThemeController.getInstance().getValue(null);
        if ("skyblue".equalsIgnoreCase(value)) {
            parameter = BackUpUtil.getInstance().getPreferenceString(KEY_PARAM_SKY_BLUE, getDefaultParam());
        } else if ("sunset".equalsIgnoreCase(value)) {
            parameter = BackUpUtil.getInstance().getPreferenceString(KEY_PARAM_SUNSET, getDefaultParam());
        } else if ("standard".equalsIgnoreCase(value)) {
            parameter = BackUpUtil.getInstance().getPreferenceString(KEY_PARAM_STANDARD, getDefaultParam());
        } else if ("custom1".equalsIgnoreCase(value)) {
            parameter = BackUpUtil.getInstance().getPreferenceString(KEY_PARAM_CUSTOM1, getDefaultParam());
        } else if ("custom2".equalsIgnoreCase(value)) {
            parameter = BackUpUtil.getInstance().getPreferenceString(KEY_PARAM_CUSTOM2, getDefaultParam());
        }
        AppLog.info(TAG, "Setting Params: " + parameter);
        return parameter;
    }

    private String getDefaultParam() {
        return GFEffectParameters.getInstance().getParameters().getDefaultFlatten();
    }

    public void saveLastParameters(String parameter) {
        String key = KEY_PARAM_SKY_BLUE;
        String value = GFThemeController.getInstance().getValue(null);
        if ("skyblue".equalsIgnoreCase(value)) {
            key = KEY_PARAM_SKY_BLUE;
        } else if ("sunset".equalsIgnoreCase(value)) {
            key = KEY_PARAM_SUNSET;
        } else if ("standard".equalsIgnoreCase(value)) {
            key = KEY_PARAM_STANDARD;
        } else if ("custom1".equalsIgnoreCase(value)) {
            key = KEY_PARAM_CUSTOM1;
        } else if ("custom2".equalsIgnoreCase(value)) {
            key = KEY_PARAM_CUSTOM2;
        }
        BackUpUtil.getInstance().setPreference(key, parameter);
        AppLog.info(TAG, "saveLastParameters: " + parameter);
    }

    public int getLastEffect() {
        int effect = 0;
        String value = GFThemeController.getInstance().getValue(null);
        if ("skyblue".equalsIgnoreCase(value)) {
            effect = 0;
        } else if ("sunset".equalsIgnoreCase(value)) {
            effect = 1;
        } else if ("standard".equalsIgnoreCase(value)) {
            effect = 2;
        } else if ("custom1".equalsIgnoreCase(value)) {
            effect = 3;
        } else if ("custom2".equalsIgnoreCase(value)) {
            effect = 4;
        }
        AppLog.info(TAG, "getLastEffect = " + effect);
        return effect;
    }

    public void saveWBOption(String value, String option, boolean base, int theme) {
        String key = getBackUpKeyWBOption(value, base, theme);
        BackUpUtil.getInstance().setPreference(key, option);
        AppLog.info(TAG, "saveWBOption(key) = " + key);
        AppLog.info(TAG, "saveWBOption = " + option);
    }

    public String getWBOption(String value, boolean base, int theme) {
        String defaultValue = "0/0/5500";
        String key = getBackUpKeyWBOption(value, base, theme);
        if (!base && ((theme == 0 || theme == 1) && WhiteBalanceController.COLOR_TEMP.equalsIgnoreCase(value))) {
            defaultValue = GFEffectParameters.Parameters.DEFAULT_FILTER_WB_OPTION[theme];
        }
        String option = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        Log.d(TAG, "getWBOption(key) = " + key);
        Log.d(TAG, "getWBOption = " + option);
        return option;
    }

    public void saveCTempFilterWBOption(String option, boolean base, int theme) {
        String key = getBackUpKeyWBOption(WhiteBalanceController.COLOR_TEMP, base, theme);
        BackUpUtil.getInstance().setPreference(key, option);
    }

    public String getCTempFilterWBOption(boolean base, int theme) {
        String defaultValue;
        String key = getBackUpKeyWBOption(WhiteBalanceController.COLOR_TEMP, base, theme);
        if (base) {
            defaultValue = GFEffectParameters.Parameters.DEFAULT_BASE_WB_OPTION[theme];
        } else {
            defaultValue = GFEffectParameters.Parameters.DEFAULT_FILTER_WB_OPTION[theme];
        }
        String option = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        return option;
    }

    public void resetWBOptions(int theme) {
        String[] arr$ = mWBMode;
        for (String wbMode : arr$) {
            saveWBOption(wbMode, "0/0/5500", true, theme);
            saveWBOption(wbMode, "0/0/5500", false, theme);
        }
    }

    public String getBackUpKeyWB(String mode, boolean base, int theme) {
        String ret;
        if (base) {
            ret = "ID_BASE_WB_" + getEffect(theme) + "_";
        } else {
            ret = "ID_FILTER_WB_" + getEffect(theme) + "_";
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

    public String getBackUpKeyWBOption(String mode, boolean base, int theme) {
        String ret = getBackUpKeyWB(mode, base, theme);
        if (ret != null) {
            return ret + "_OPTION";
        }
        return ret;
    }

    public void saveCSOption(String value, String option, int theme) {
        String key = getBackUpKeyCSOption(value, theme);
        BackUpUtil.getInstance().setPreference(key, option);
        AppLog.info(TAG, "saveCSOption(key) = " + key);
        AppLog.info(TAG, "saveCSOption = " + option);
    }

    public String getCSOption(String value, int theme) {
        String defaultValue;
        String key = getBackUpKeyCSOption(value, theme);
        if (value == GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLE[theme]) {
            defaultValue = GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLEOPTION[theme];
        } else {
            defaultValue = "0/0/0";
        }
        String option = BackUpUtil.getInstance().getPreferenceString(key, defaultValue);
        AppLog.info(TAG, "getCSOption(key) = " + key);
        AppLog.info(TAG, "getCSOption = " + option);
        return option;
    }

    public void resetCSOptions(int theme) {
        String[] arr$ = mCSMode;
        for (String csMode : arr$) {
            saveCSOption(csMode, "0/0/0", theme);
        }
        saveCSOption(GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLE[theme], GFEffectParameters.Parameters.DEFAULT_CREATIVESTYLEOPTION[theme], theme);
    }

    public String getBackUpKeyCS(String mode, int theme) {
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

    public String getBackUpKeyCSOption(String mode, int theme) {
        String ret = getBackUpKeyCS(mode, theme);
        if (ret != null) {
            return ret + "_OPTION";
        }
        return ret;
    }

    private static String getEffect(int effect) {
        switch (effect) {
            case 0:
                return "SKY_BLUE";
            case 1:
                return "SUNSET";
            case 2:
                return "ND";
            case 3:
                return "CUSTOM1";
            case 4:
                return "CUSTOM2";
            default:
                return "SKY_BLUE";
        }
    }

    public void saveMeteringSpotValue(String value, int theme) {
        String key = getMeteringSpotBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, value);
    }

    public void resetMeteringSpotValue(int theme) {
        String key = getMeteringSpotBackUpKey(theme);
        BackUpUtil.getInstance().setPreference(key, MeteringController.STANDARD);
    }

    public String getMeteringSpotValue(int theme) {
        String key = getMeteringSpotBackUpKey(theme);
        String value = BackUpUtil.getInstance().getPreferenceString(key, MeteringController.STANDARD);
        return value;
    }

    public String getMeteringSpotBackUpKey(int theme) {
        String key = "ID_GF_" + getEffect(theme) + "_METERING_SPOT";
        return key;
    }
}
