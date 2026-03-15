package com.sony.imaging.app.lightshaft;

import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LightShaftBackUpKey {
    private static final String DEFAULT_OPTIONS_PARAMS_ANGEL = "Effect=1;Point=500:100;Strength=5;Length=10;Range=7;Direction=8;NumberOfShafts=-1";
    private static final String DEFAULT_OPTIONS_PARAMS_BEAM = "Effect=4;Point=500:500;Strength=5;Length=10;Range=0;Direction=17;NumberOfShafts=-1";
    private static final String DEFAULT_OPTIONS_PARAMS_FLARE = "Effect=3;Point=500:500;Strength=5;Length=5;Range=-1;Direction=-1;NumberOfShafts=-1";
    private static final String DEFAULT_OPTIONS_PARAMS_STAR = "Effect=2;Point=500:500;Strength=5;Length=10;Range=-1;Direction=4;NumberOfShafts=4";
    private static final String TAG = "LightShaftBackUpKey";
    public static String SELECTED_EFFECT = "SELECTED_EFFECT";
    public static String KEY_BKUP_OPTIONS_PARAM_ANGEL = "KEY_BKUP_OPTIONS_PARAM_ANGEL";
    public static String KEY_BKUP_OPTIONS_PARAM_STAR = "KEY_BKUP_OPTIONS_PARAM_STAR";
    public static String KEY_BKUP_OPTIONS_PARAM_FLARE = "KEY_BKUP_OPTIONS_PARAM_FLARE";
    public static String KEY_BKUP_OPTIONS_PARAM_BEAM = "KEY_BKUP_OPTIONS_PARAM_BEAM";
    private static LightShaftBackUpKey sLightShaftBkp = null;

    public static LightShaftBackUpKey getInstance() {
        if (sLightShaftBkp == null) {
            sLightShaftBkp = new LightShaftBackUpKey();
        }
        return sLightShaftBkp;
    }

    public void saveOptionSettings(String optionParams) {
        switch (getLastSelectedEffect()) {
            case 2:
                BackUpUtil.getInstance().setPreference(KEY_BKUP_OPTIONS_PARAM_STAR, optionParams);
                return;
            case 3:
                BackUpUtil.getInstance().setPreference(KEY_BKUP_OPTIONS_PARAM_FLARE, optionParams);
                return;
            case 4:
                BackUpUtil.getInstance().setPreference(KEY_BKUP_OPTIONS_PARAM_BEAM, optionParams);
                return;
            default:
                BackUpUtil.getInstance().setPreference(KEY_BKUP_OPTIONS_PARAM_ANGEL, optionParams);
                return;
        }
    }

    public String getLastSavedOptionSettings() {
        String lastSavedSetting;
        switch (getLastSelectedEffect()) {
            case 2:
                lastSavedSetting = BackUpUtil.getInstance().getPreferenceString(KEY_BKUP_OPTIONS_PARAM_STAR, DEFAULT_OPTIONS_PARAMS_STAR);
                break;
            case 3:
                lastSavedSetting = BackUpUtil.getInstance().getPreferenceString(KEY_BKUP_OPTIONS_PARAM_FLARE, DEFAULT_OPTIONS_PARAMS_FLARE);
                break;
            case 4:
                lastSavedSetting = BackUpUtil.getInstance().getPreferenceString(KEY_BKUP_OPTIONS_PARAM_BEAM, DEFAULT_OPTIONS_PARAMS_BEAM);
                break;
            default:
                lastSavedSetting = BackUpUtil.getInstance().getPreferenceString(KEY_BKUP_OPTIONS_PARAM_ANGEL, DEFAULT_OPTIONS_PARAMS_ANGEL);
                break;
        }
        AppLog.info(TAG, "doItemClickProcessing lastSavedSetting= " + lastSavedSetting);
        return lastSavedSetting;
    }

    public int getLastSelectedEffect() {
        int lastSelected = 1;
        String value = BackUpUtil.getInstance().getPreferenceString(SELECTED_EFFECT, EffectSelectController.ANGEL);
        if (EffectSelectController.ANGEL.equals(value)) {
            lastSelected = 1;
        } else if (EffectSelectController.STAR.equals(value)) {
            lastSelected = 2;
        } else if (EffectSelectController.FLARE.equals(value)) {
            lastSelected = 3;
        } else if (EffectSelectController.BEAM.equals(value)) {
            lastSelected = 4;
        }
        AppLog.info(TAG, "doItemClickProcessing getLastSelectedEffect= " + value);
        return lastSelected;
    }
}
