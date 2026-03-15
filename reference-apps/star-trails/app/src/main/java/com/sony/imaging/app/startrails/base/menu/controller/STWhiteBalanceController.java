package com.sony.imaging.app.startrails.base.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class STWhiteBalanceController extends WhiteBalanceController {
    public static final String BRIGHT_NIGHT = "bright";
    private static final String CUSTOM_THEME = "custom";
    private static final String DARK_NIGHT = "dark";
    private static String TAG = AppLog.getClassName();
    private static STWhiteBalanceController mInstance;
    private final String STRING_JOINER = "_";
    private final String[] WB_MODE = {"auto", WhiteBalanceController.DAYLIGHT, WhiteBalanceController.SHADE, WhiteBalanceController.CLOUDY, WhiteBalanceController.INCANDESCENT, WhiteBalanceController.FLUORESCENT_DAYLIGHT, WhiteBalanceController.WARM_FLUORESCENT, WhiteBalanceController.FLUORESCENT_DAYWHITE, WhiteBalanceController.FLUORESCENT_COOLWHITE, WhiteBalanceController.FLASH, WhiteBalanceController.COLOR_TEMP, "custom"};

    public static STWhiteBalanceController getInstance() {
        AppLog.enter(TAG, "getInstance()");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance()");
        return mInstance;
    }

    private static STWhiteBalanceController createInstance() {
        AppLog.enter(TAG, "createInstance()");
        if (mInstance == null) {
            mInstance = new STWhiteBalanceController();
        }
        AppLog.exit(TAG, "createInstance()");
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue()");
        super.setValue(itemId, value);
        super.getDetailValue();
        STUtility.getInstance().updateWhiteBalanceValue(value);
        AppLog.exit(TAG, "setvalue()");
    }

    public void setStarTrailsValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue");
        super.setValue(itemId, value);
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) super.getDetailValue();
        param.setColorComp(0);
        param.setLightBalance(0);
        param.setColorTemp(0);
        super.setDetailValue(param);
        AppLog.exit(TAG, "setvalue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (STUtility.getInstance().getCurrentTrail() != 2 && itemId.endsWith(WhiteBalanceController.WHITEBALANCE)) {
            return 1;
        }
        int getCautionIndex = super.getCautionIndex(itemId);
        return getCautionIndex;
    }

    public void saveThemeWBValues() {
        String current_theme = getThemeName(STUtility.getInstance().getLastTrail());
        BackUpUtil backupInstance = BackUpUtil.getInstance();
        for (int i = 0; i < this.WB_MODE.length; i++) {
            updateValueforBackup(current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.LIGHT, backupInstance.getPreferenceInt(getBackUpKey(this.WB_MODE[i], WhiteBalanceController.LIGHT), 0), backupInstance);
            updateValueforBackup(current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.COMP, backupInstance.getPreferenceInt(getBackUpKey(this.WB_MODE[i], WhiteBalanceController.COMP), 0), backupInstance);
            if (WhiteBalanceController.COLOR_TEMP.equals(this.WB_MODE[i])) {
                updateValueforBackup(current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.TEMP, backupInstance.getPreferenceInt(getBackUpKey(this.WB_MODE[i], WhiteBalanceController.TEMP), WhiteBalanceController.DEF_TEMP), backupInstance);
            }
        }
    }

    private String getThemeName(int trail) {
        switch (trail) {
            case 1:
                return DARK_NIGHT;
            case 2:
                return "custom";
            default:
                return BRIGHT_NIGHT;
        }
    }

    private void updateValueforBackup(String backup_key, int value, BackUpUtil backup) {
        Log.i(TAG, "for updateValueforBackup backup_key =" + backup_key + "  value " + value);
        backup.setPreference(backup_key, Integer.valueOf(value));
    }

    public void resetBackupOnCamera() {
        String current_theme = getThemeName(STUtility.getInstance().getCurrentTrail());
        BackUpUtil backupInstance = BackUpUtil.getInstance();
        for (int i = 0; i < this.WB_MODE.length; i++) {
            String sBackupKey = current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.LIGHT;
            int backupValue = backupInstance.getPreferenceInt(sBackupKey, 0);
            updateValueforBackupOnBase(this.WB_MODE[i], WhiteBalanceController.LIGHT, backupValue, backupInstance);
            String sBackupKey2 = current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.COMP;
            int backupValue2 = backupInstance.getPreferenceInt(sBackupKey2, 0);
            updateValueforBackupOnBase(this.WB_MODE[i], WhiteBalanceController.COMP, backupValue2, backupInstance);
            if (WhiteBalanceController.COLOR_TEMP.equals(this.WB_MODE[i])) {
                String sBackupKey3 = current_theme + "_" + this.WB_MODE[i] + "_" + WhiteBalanceController.TEMP;
                int backupValue3 = backupInstance.getPreferenceInt(sBackupKey3, WhiteBalanceController.DEF_TEMP);
                updateValueforBackupOnBase(this.WB_MODE[i], WhiteBalanceController.TEMP, backupValue3, backupInstance);
                updateValueforBackupOnBase(this.WB_MODE[i], WhiteBalanceController.TEMP, backupValue3, backupInstance);
            }
        }
    }

    private void updateValueforBackupOnBase(String mode, String option, int valueLight, BackUpUtil backup) {
        String backup_key = getBackUpKey(mode, option);
        backup.setPreference(backup_key, Integer.valueOf(valueLight));
        Log.i(TAG, "for updateValueforBackupOnBase backup_key =" + backup_key + "  value " + valueLight);
    }
}
