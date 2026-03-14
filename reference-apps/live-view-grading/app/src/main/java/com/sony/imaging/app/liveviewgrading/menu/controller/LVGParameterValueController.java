package com.sony.imaging.app.liveviewgrading.menu.controller;

import android.util.Log;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LVGParameterValueController {
    public static final int BIG_AIR = 11;
    public static final int BOLD = 4;
    private static final String CINEMA_COAST_DEFAULT_VALUES = "Contrast=0;Saturation=1;Sharpness=3;ColorTemp=0;ColorTint=0;ColorDepthR=2;ColorDepthG=1;ColorDepthB=5;";
    private static final String CINEMA_MISTY_DEFAULT_VALUES = "Contrast=0;Saturation=2;Sharpness=-6;ColorTemp=6;ColorTint=2;ColorDepthR=-4;ColorDepthG=-5;ColorDepthB=5;";
    private static final String CINEMA_SILKY_DEFAULT_VALUES = "Contrast=0;Saturation=-1;Sharpness=-3;ColorTemp=0;ColorTint=1;ColorDepthR=-2;ColorDepthG=-4;ColorDepthB=-2;";
    private static final String CINEMA_VELVETY_DEFAULT_VALUES = "Contrast=0;Saturation=0;Sharpness=-2;ColorTemp=-6;ColorTint=2;ColorDepthR=2;ColorDepthG=4;ColorDepthB=-5;";
    public static final int CLEAR = 1;
    public static final int COAST_SIDE_LIGHT = 5;
    private static final String EXT_180_DEFAULT_VALUES = "Contrast=0;Saturation=-1;Sharpness=0;ColorTemp=2;ColorTint=0;ColorDepthR=-3;ColorDepthG=-2;ColorDepthB=-6;";
    private static final String EXT_BIG_AIR_DEFAULT_VALUES = "Contrast=0;Saturation=-3;Sharpness=-6;ColorTemp=-4;ColorTint=2;ColorDepthR=2;ColorDepthG=-5;ColorDepthB=-3;";
    private static final String EXT_SNOW_DEFAULT_VALUES = "Contrast=0;Saturation=1;Sharpness=5;ColorTemp=0;ColorTint=0;ColorDepthR=7;ColorDepthG=-6;ColorDepthB=7;";
    private static final String EXT_SURF_DEFAULT_VALUES = "Contrast=0;Saturation=2;Sharpness=2;ColorTemp=0;ColorTint=0;ColorDepthR=5;ColorDepthG=-4;ColorDepthB=-3;";
    public static final int MISTY_BLUE = 7;
    public static final int MONOCHROME = 3;
    public static final int SILKY = 6;
    public static final int SNOW_TRICKS = 12;
    private static final String STD_BOLD_DEFAULT_VALUES = "Contrast=0;Saturation=-4;Sharpness=-2;ColorTemp=-5;ColorTint=1;ColorDepthR=7;ColorDepthG=5;ColorDepthB=-7;";
    private static final String STD_CLEAR_DEFAULT_VALUES = "Contrast=0;Saturation=1;Sharpness=2;ColorTemp=1;ColorTint=0;ColorDepthR=-5;ColorDepthG=-5;ColorDepthB=-4;";
    private static final String STD_MONO_DEFAULT_VALUES = "Contrast=0;Saturation=0;Sharpness=0;ColorTemp=0;ColorTint=0;ColorDepthR=0;ColorDepthG=0;ColorDepthB=0;";
    private static final String STD_VIVID_DEFAULT_VALUES = "Contrast=0;Saturation=2;Sharpness=4;ColorTemp=0;ColorTint=0;ColorDepthR=4;ColorDepthG=4;ColorDepthB=5;";
    public static final int SURF_TRIP = 10;
    public static final int VELVETY_DEW = 8;
    public static final int VIVID = 2;
    public static final int _180 = 9;
    private static LVGParameterValueController mValueController;

    private LVGParameterValueController() {
    }

    public static LVGParameterValueController getInstance() {
        if (mValueController == null) {
            mValueController = new LVGParameterValueController();
        }
        return mValueController;
    }

    public String getPresetValues() {
        switch (getLastSelectedPreset()) {
            case 1:
                String preset = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_CLEAR, STD_CLEAR_DEFAULT_VALUES);
                return preset;
            case 2:
                String preset2 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_VIVID, STD_VIVID_DEFAULT_VALUES);
                return preset2;
            case 3:
                String preset3 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_MONOCHROME, STD_MONO_DEFAULT_VALUES);
                return preset3;
            case 4:
                String preset4 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_BOLD, STD_BOLD_DEFAULT_VALUES);
                return preset4;
            case 5:
                String preset5 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_COAST_SIDE_LIGHT, CINEMA_COAST_DEFAULT_VALUES);
                return preset5;
            case 6:
                String preset6 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_SILKY, CINEMA_SILKY_DEFAULT_VALUES);
                return preset6;
            case 7:
                String preset7 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_MISTY_BLUE, CINEMA_MISTY_DEFAULT_VALUES);
                return preset7;
            case 8:
                String preset8 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_VELVETY_DEW, CINEMA_VELVETY_DEFAULT_VALUES);
                return preset8;
            case 9:
                String preset9 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_180, EXT_180_DEFAULT_VALUES);
                return preset9;
            case 10:
                String preset10 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_SURF_TRIP, EXT_SURF_DEFAULT_VALUES);
                return preset10;
            case 11:
                String preset11 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_BIG_AIR, EXT_BIG_AIR_DEFAULT_VALUES);
                return preset11;
            case 12:
                String preset12 = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.KEY_BKUP_SNOW_TRICKS, EXT_SNOW_DEFAULT_VALUES);
                return preset12;
            default:
                return "";
        }
    }

    public void setPresetValues(String presetValue) {
        switch (getLastSelectedPreset()) {
            case 1:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_CLEAR, presetValue);
                return;
            case 2:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VIVID, presetValue);
                return;
            case 3:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MONOCHROME, presetValue);
                return;
            case 4:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BOLD, presetValue);
                return;
            case 5:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_COAST_SIDE_LIGHT, presetValue);
                return;
            case 6:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SILKY, presetValue);
                return;
            case 7:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MISTY_BLUE, presetValue);
                return;
            case 8:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VELVETY_DEW, presetValue);
                return;
            case 9:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_180, presetValue);
                return;
            case 10:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SURF_TRIP, presetValue);
                return;
            case 11:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BIG_AIR, presetValue);
                return;
            case 12:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SNOW_TRICKS, presetValue);
                return;
            default:
                return;
        }
    }

    public int getLastSelectedPreset() {
        String lastPreset = null;
        String lastSelectedGroup = BackUpUtil.getInstance().getPreferenceString(ColorGradingConstants.LAST_SELECTED_GROUP, ColorGradingController.STANDARD);
        Log.e("", "last group = " + lastSelectedGroup);
        if (lastSelectedGroup.equalsIgnoreCase(ColorGradingController.STANDARD)) {
            Log.e("", "group 1");
            lastPreset = ColorGradingController.getInstance().getGroup1SelectedPreset();
        } else if (lastSelectedGroup.equalsIgnoreCase(ColorGradingController.CINEMA)) {
            Log.e("", "group 2");
            lastPreset = ColorGradingController.getInstance().getGroup2SelectedPreset();
        } else if (lastSelectedGroup.equalsIgnoreCase(ColorGradingController.EXTREME)) {
            Log.e("", "group 3");
            lastPreset = ColorGradingController.getInstance().getGroup3SelectedPreset();
        }
        return getPresetNumber(lastPreset);
    }

    private int getPresetNumber(String preset) {
        if (preset.equalsIgnoreCase(ColorGradingController.CLEAR)) {
            Log.e("", "getPresetNumber CASE 1:1");
            return 1;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.VIVID)) {
            Log.e("", "getPresetNumber CASE 1:2");
            return 2;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.MONOCHROME)) {
            Log.e("", "getPresetNumber CASE 1:3");
            return 3;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.BOLD)) {
            Log.e("", "getPresetNumber CASE 1:4");
            return 4;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.COAST_SIDE_LIGHT)) {
            Log.e("", "getPresetNumber CASE 2:1");
            return 5;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.SILKY)) {
            Log.e("", "getPresetNumber CASE 2:2");
            return 6;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.MISTY_BLUE)) {
            Log.e("", "getPresetNumber CASE 2:3");
            return 7;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.VELVETY_DEW)) {
            Log.e("", "getPresetNumber CASE 2:4");
            return 8;
        }
        if (preset.equalsIgnoreCase(ColorGradingController._180)) {
            Log.e("", "getPresetNumber CASE 3:1");
            return 9;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.SURF_TRIP)) {
            Log.e("", "getPresetNumber CASE 3:2");
            return 10;
        }
        if (preset.equalsIgnoreCase(ColorGradingController.BIG_AIR)) {
            Log.e("", "getPresetNumber CASE 3:3");
            return 11;
        }
        if (!preset.equalsIgnoreCase(ColorGradingController.SNOW_TRICKS)) {
            return 0;
        }
        Log.e("", "getPresetNumber CASE 3:4");
        return 12;
    }

    public void resetCurrentPreset() {
        Log.e("", "INSIDE resetCurrentPreset");
        switch (getLastSelectedPreset()) {
            case 1:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_CLEAR, STD_CLEAR_DEFAULT_VALUES);
                return;
            case 2:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VIVID, STD_VIVID_DEFAULT_VALUES);
                return;
            case 3:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MONOCHROME, STD_MONO_DEFAULT_VALUES);
                return;
            case 4:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BOLD, STD_BOLD_DEFAULT_VALUES);
                return;
            case 5:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_COAST_SIDE_LIGHT, CINEMA_COAST_DEFAULT_VALUES);
                return;
            case 6:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SILKY, CINEMA_SILKY_DEFAULT_VALUES);
                return;
            case 7:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MISTY_BLUE, CINEMA_MISTY_DEFAULT_VALUES);
                return;
            case 8:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VELVETY_DEW, CINEMA_VELVETY_DEFAULT_VALUES);
                return;
            case 9:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_180, EXT_180_DEFAULT_VALUES);
                return;
            case 10:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SURF_TRIP, EXT_SURF_DEFAULT_VALUES);
                return;
            case 11:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BIG_AIR, EXT_BIG_AIR_DEFAULT_VALUES);
                return;
            case 12:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SNOW_TRICKS, EXT_SNOW_DEFAULT_VALUES);
                return;
            default:
                return;
        }
    }

    public void resetAllPresets() {
        Log.e("", "INSIDE resetAllPresets");
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_CLEAR, STD_CLEAR_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VIVID, STD_VIVID_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MONOCHROME, STD_MONO_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BOLD, STD_BOLD_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_COAST_SIDE_LIGHT, CINEMA_COAST_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SILKY, CINEMA_SILKY_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_MISTY_BLUE, CINEMA_MISTY_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_VELVETY_DEW, CINEMA_VELVETY_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_180, EXT_180_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SURF_TRIP, EXT_SURF_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_BIG_AIR, EXT_BIG_AIR_DEFAULT_VALUES);
        BackUpUtil.getInstance().setPreference(ColorGradingConstants.KEY_BKUP_SNOW_TRICKS, EXT_SNOW_DEFAULT_VALUES);
    }
}
