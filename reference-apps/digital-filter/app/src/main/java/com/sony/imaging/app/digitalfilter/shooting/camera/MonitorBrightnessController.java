package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MonitorBrightnessController extends AbstractController {
    public static final String BACKUP_MONITOR_BRIGHTNESS = "BACKUP_MONITOR_BRIGHTNESS";
    public static final String DIM = "dim";
    public static final String MONITOR_BRIGHTNESS = "monitor-brightness-control";
    public static final String NORMAL = "normal";
    public static final String PANEL_OFF = "panel-off";
    private static final String TAG = "TimelapseMonitorBrightnessController";
    private static MonitorBrightnessController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;
    private int mCurrMonitorBrightness;
    private int mMonitorBrightness;
    private static int BRIGHTNESS_PANEL_OFF = 2;
    private static int BRIGHTNESS_DIM = 1;
    private static int BRIGHTNESS_NORMAL = 0;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(MONITOR_BRIGHTNESS);
        sSupportedList.add(PANEL_OFF);
        sSupportedList.add(DIM);
        sSupportedList.add("normal");
    }

    public static MonitorBrightnessController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static MonitorBrightnessController createInstance() {
        if (mInstance == null) {
            mInstance = new MonitorBrightnessController();
        }
        return mInstance;
    }

    private MonitorBrightnessController() {
        this.mMonitorBrightness = 0;
        this.mCurrMonitorBrightness = 0;
        this.mBackupUtil = null;
        if (this.mBackupUtil == null) {
            this.mBackupUtil = BackUpUtil.getInstance();
        }
        getSupportedValue(TAG);
        this.mMonitorBrightness = this.mBackupUtil.getPreferenceInt(BACKUP_MONITOR_BRIGHTNESS, BRIGHTNESS_NORMAL);
        this.mCurrMonitorBrightness = this.mMonitorBrightness;
        getSupportedValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (value.equalsIgnoreCase(PANEL_OFF)) {
            this.mMonitorBrightness = BRIGHTNESS_PANEL_OFF;
        } else if (value.equalsIgnoreCase(DIM)) {
            this.mMonitorBrightness = BRIGHTNESS_DIM;
        } else {
            this.mMonitorBrightness = BRIGHTNESS_NORMAL;
        }
        this.mCurrMonitorBrightness = this.mMonitorBrightness;
        this.mBackupUtil.setPreference(BACKUP_MONITOR_BRIGHTNESS, Integer.valueOf(this.mMonitorBrightness));
    }

    public void toggleSetting() {
        AppLog.enter(TAG, "toggleSetting");
        this.mCurrMonitorBrightness++;
        DisplayManager dispManager = new DisplayManager();
        if (isSupportedDispOffMode()) {
            if (this.mCurrMonitorBrightness == BRIGHTNESS_PANEL_OFF) {
                dispManager.switchDisplayOutputTo("DEVICE_ID_NONE");
                dispManager.setSavingBatteryMode("SAVING_BATTERY_OFF");
            } else if (this.mCurrMonitorBrightness == BRIGHTNESS_DIM) {
                dispManager.setSavingBatteryMode("SAVING_BATTERY_ON");
            } else {
                this.mCurrMonitorBrightness = BRIGHTNESS_NORMAL;
                dispManager.switchDisplayOutputTo("DEVICE_ID_SETTING_RELEASE");
            }
        } else if (isSupportedSavingBatteryMode()) {
            if (this.mCurrMonitorBrightness == BRIGHTNESS_DIM) {
                dispManager.setSavingBatteryMode("SAVING_BATTERY_ON");
            } else {
                this.mCurrMonitorBrightness = BRIGHTNESS_NORMAL;
                dispManager.setSavingBatteryMode("SAVING_BATTERY_OFF");
            }
        }
        AppLog.exit(TAG, "toggleSetting mCurrMonitorBrightness" + this.mCurrMonitorBrightness);
        dispManager.finish();
    }

    public void applySetting() {
        if (isSupportedDispOffMode() && BRIGHTNESS_PANEL_OFF == this.mMonitorBrightness) {
            DisplayManager dispManager = new DisplayManager();
            dispManager.switchDisplayOutputTo("DEVICE_ID_NONE");
            dispManager.finish();
        } else if (isSupportedSavingBatteryMode() && BRIGHTNESS_DIM == this.mMonitorBrightness) {
            DisplayManager dispManager2 = new DisplayManager();
            dispManager2.setSavingBatteryMode("SAVING_BATTERY_ON");
            dispManager2.finish();
        }
        this.mCurrMonitorBrightness = this.mMonitorBrightness;
    }

    public void clearSetting() {
        AppLog.enter(TAG, "clearSetting");
        AppLog.enter(TAG, "clearSetting mCurrMonitorBrightness" + this.mCurrMonitorBrightness);
        if (isSupportedDispOffMode() && BRIGHTNESS_PANEL_OFF == this.mCurrMonitorBrightness) {
            AppLog.info(TAG, "clearSetting BRIGHTNESS_PANEL_OFF mCurrMonitorBrightness" + BRIGHTNESS_PANEL_OFF);
            DisplayManager dispManager = new DisplayManager();
            dispManager.switchDisplayOutputTo("DEVICE_ID_SETTING_RELEASE");
            dispManager.finish();
        } else if (isSupportedSavingBatteryMode() && BRIGHTNESS_DIM == this.mCurrMonitorBrightness) {
            DisplayManager dispManager2 = new DisplayManager();
            AppLog.info(TAG, "clearSetting BRIGHTNESS_DIM mCurrMonitorBrightness" + this.mCurrMonitorBrightness);
            dispManager2.setSavingBatteryMode("SAVING_BATTERY_OFF");
            dispManager2.finish();
        }
        AppLog.exit(TAG, "clearSetting");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        this.mMonitorBrightness = this.mBackupUtil.getPreferenceInt(BACKUP_MONITOR_BRIGHTNESS, BRIGHTNESS_NORMAL);
        if (BRIGHTNESS_PANEL_OFF == this.mMonitorBrightness) {
            return PANEL_OFF;
        }
        if (BRIGHTNESS_DIM == this.mMonitorBrightness) {
            return DIM;
        }
        return "normal";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        getSupportedValue();
        return sSupportedList;
    }

    private void getSupportedValue() {
        if (isSupportedSavingBatteryMode()) {
            if (!isSupportedDispOffMode()) {
                sSupportedList.remove(PANEL_OFF);
                return;
            }
            return;
        }
        sSupportedList = null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupported()) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isSupported() {
        return isSupportedSavingBatteryMode() || isSupportedDispOffMode();
    }

    public boolean isSupportedSavingBatteryMode() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (2 <= pfMajorVersion && 2 <= CameraSetting.getPfApiVersion()) {
            AppLog.info(TAG, "setSavingBatteryMode is supported");
            return true;
        }
        AppLog.info(TAG, "setSavingBatteryMode is NOT supported");
        return false;
    }

    public boolean isSupportedDispOffMode() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        boolean isInternalEVF = 1 == ScalarProperties.getInt("device.evf.internal.supported");
        if (2 <= pfMajorVersion && 7 <= CameraSetting.getPfApiVersion() && !isInternalEVF) {
            AppLog.info(TAG, "switchDisplayOutputTo(DisplayManager.DEVICE_ID_SETTING_RELEASE) is supported");
            return true;
        }
        AppLog.info(TAG, "switchDisplayOutputTo(DisplayManager.DEVICE_ID_SETTING_RELEASE) is NOT supported");
        return false;
    }

    public void initializeFromBackup() {
        if (isSupported()) {
            this.mCurrMonitorBrightness = BackUpUtil.getInstance().getPreferenceInt(BACKUP_MONITOR_BRIGHTNESS, BRIGHTNESS_NORMAL);
            try {
                setValue(TAG, String.valueOf(this.mCurrMonitorBrightness));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
