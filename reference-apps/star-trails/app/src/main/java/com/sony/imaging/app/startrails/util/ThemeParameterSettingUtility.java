package com.sony.imaging.app.startrails.util;

import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class ThemeParameterSettingUtility {
    private static String TAG = "ThemeParameterSettingUtility";
    private static ThemeParameterSettingUtility sParameterSettingInstance = null;
    int mNumberOfShot = -1;
    int mRecordingMode = -1;
    int mStreakLevel = -1;

    public static ThemeParameterSettingUtility getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sParameterSettingInstance == null) {
            sParameterSettingInstance = new ThemeParameterSettingUtility();
            sParameterSettingInstance.initializeThemeParameters();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sParameterSettingInstance;
    }

    public void initializeThemeParameters() {
        int recMode;
        int shot;
        int streakLevel;
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                recMode = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_RECORDING_MODE_KEY, 0);
                shot = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_SHOOTING_NUM_KEY, 480);
                streakLevel = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_STREAK_LEVEL_KEY, 4);
                break;
            case 2:
                recMode = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_RECORDING_MODE_KEY, 0);
                shot = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_SHOOTING_NUM_KEY, 480);
                streakLevel = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_STREAK_LEVEL_KEY, 4);
                break;
            default:
                recMode = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_RECORDING_MODE_KEY, 0);
                shot = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_SHOOTING_NUM_KEY, 480);
                streakLevel = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_STREAK_LEVEL_KEY, 4);
                break;
        }
        updateThemeParameter(recMode, shot, streakLevel);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateThemeParameter(int frameRate, int numberofShot, int streakLevel) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mNumberOfShot = numberofShot;
        this.mRecordingMode = frameRate;
        this.mStreakLevel = streakLevel;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private ThemeParameterSettingUtility() {
    }

    public int getNumberOfShot() {
        return this.mNumberOfShot;
    }

    public void setNumberOfShot(int mNumberOfShot) {
        this.mNumberOfShot = mNumberOfShot;
    }

    public int getRecordingMode() {
        return this.mRecordingMode;
    }

    public void setRecordingMode(int mRecordingMode) {
        this.mRecordingMode = mRecordingMode;
    }

    public int getStreakLevel() {
        return this.mStreakLevel;
    }

    public void setStreakLevel(int mStreakLevel) {
        this.mStreakLevel = mStreakLevel;
    }

    public void updateBackupValue() {
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                BackUpUtil.getInstance().setPreference(STBackUpKey.DNIGHT_RECORDING_MODE_KEY, Integer.valueOf(this.mRecordingMode));
                BackUpUtil.getInstance().setPreference(STBackUpKey.DNIGHT_SHOOTING_NUM_KEY, Integer.valueOf(this.mNumberOfShot));
                BackUpUtil.getInstance().setPreference(STBackUpKey.DNIGHT_STREAK_LEVEL_KEY, Integer.valueOf(this.mStreakLevel));
                break;
            case 2:
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_RECORDING_MODE_KEY, Integer.valueOf(this.mRecordingMode));
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_SHOOTING_NUM_KEY, Integer.valueOf(this.mNumberOfShot));
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_STREAK_LEVEL_KEY, Integer.valueOf(this.mStreakLevel));
                break;
            default:
                BackUpUtil.getInstance().setPreference(STBackUpKey.BNIGHT_RECORDING_MODE_KEY, Integer.valueOf(this.mRecordingMode));
                BackUpUtil.getInstance().setPreference(STBackUpKey.BNIGHT_SHOOTING_NUM_KEY, Integer.valueOf(this.mNumberOfShot));
                BackUpUtil.getInstance().setPreference(STBackUpKey.BNIGHT_STREAK_LEVEL_KEY, Integer.valueOf(this.mStreakLevel));
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
