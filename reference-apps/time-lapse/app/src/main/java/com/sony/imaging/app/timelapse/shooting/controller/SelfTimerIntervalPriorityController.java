package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SelfTimerIntervalPriorityController extends AbstractController {
    public static final String BACKUP_INTERVAL_PRIORITY_CUSTOM = "BACKUP_INTERVAL_PRIORITY_CUSTOM";
    public static final String BACKUP_INTERVAL_PRIORITY_SUNRISE = "BACKUP_INTERVAL_PRIORITY_SUNRISE";
    public static final String BACKUP_INTERVAL_PRIORITY_SUNSET = "BACKUP_INTERVAL_PRIORITY_SUNSET";
    public static final String DRIVE_MODE = "drivemode";
    public static final String INTERVAL_PRIORITY = "interval-priority";
    public static final String INTERVAL_PRIORITY_OFF = "interval-priority-off";
    public static final String INTERVAL_PRIORITY_ON = "interval-priority-on";
    public static final String SELF_TIMER_OFF = "selftimeroff";
    public static final String SELF_TIMER_ON = "selftimeron";
    private static final String TAG = "SelfTimerIntervalPriorityController";
    private static SelfTimerIntervalPriorityController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;
    private boolean mInetrvalPriority = false;
    private boolean mSelfTimer;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("drivemode");
        sSupportedList.add(SELF_TIMER_ON);
        sSupportedList.add(SELF_TIMER_OFF);
        if (!TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            sSupportedList.add(INTERVAL_PRIORITY);
            sSupportedList.add(INTERVAL_PRIORITY_ON);
            sSupportedList.add(INTERVAL_PRIORITY_OFF);
        }
    }

    public static SelfTimerIntervalPriorityController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static SelfTimerIntervalPriorityController createInstance() {
        if (mInstance == null) {
            mInstance = new SelfTimerIntervalPriorityController();
        }
        return mInstance;
    }

    private boolean setUpdatedIntervalPriority() {
        switch (TLCommonUtil.getInstance().getCurrentState()) {
            case 3:
                this.mInetrvalPriority = this.mBackupUtil.getPreferenceBoolean(BACKUP_INTERVAL_PRIORITY_SUNSET, true);
                break;
            case 4:
                this.mInetrvalPriority = this.mBackupUtil.getPreferenceBoolean(BACKUP_INTERVAL_PRIORITY_SUNRISE, true);
                break;
            case 5:
            case 6:
            default:
                this.mInetrvalPriority = false;
                AppLog.trace(TAG, "Theme not supporting this theme");
                break;
            case 7:
                this.mInetrvalPriority = this.mBackupUtil.getPreferenceBoolean(BACKUP_INTERVAL_PRIORITY_CUSTOM, false);
                break;
        }
        return this.mInetrvalPriority;
    }

    private SelfTimerIntervalPriorityController() {
        this.mSelfTimer = true;
        this.mBackupUtil = null;
        if (this.mBackupUtil == null) {
            this.mBackupUtil = BackUpUtil.getInstance();
        }
        getSupportedValue(TAG);
        setUpdatedIntervalPriority();
        this.mSelfTimer = this.mBackupUtil.getPreferenceBoolean("drivemode", true);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals("drivemode")) {
            if (value.equals(SELF_TIMER_ON)) {
                this.mSelfTimer = true;
            } else {
                this.mSelfTimer = false;
            }
            this.mBackupUtil.setPreference("drivemode", Boolean.valueOf(this.mSelfTimer));
            return;
        }
        if (value.equalsIgnoreCase(INTERVAL_PRIORITY_ON)) {
            this.mInetrvalPriority = true;
        } else {
            this.mInetrvalPriority = false;
        }
        updateSetting();
    }

    private void updateSetting() {
        switch (TLCommonUtil.getInstance().getCurrentState()) {
            case 3:
                this.mBackupUtil.setPreference(BACKUP_INTERVAL_PRIORITY_SUNSET, Boolean.valueOf(this.mInetrvalPriority));
                return;
            case 4:
                this.mBackupUtil.setPreference(BACKUP_INTERVAL_PRIORITY_SUNRISE, Boolean.valueOf(this.mInetrvalPriority));
                return;
            case 5:
            case 6:
            default:
                AppLog.trace(TAG, "Theme not supporting this theme");
                return;
            case 7:
                this.mBackupUtil.setPreference(BACKUP_INTERVAL_PRIORITY_CUSTOM, Boolean.valueOf(this.mInetrvalPriority));
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        if (tag.equals("drivemode")) {
            this.mSelfTimer = this.mBackupUtil.getPreferenceBoolean("drivemode", true);
            if (this.mSelfTimer) {
                return SELF_TIMER_ON;
            }
            return SELF_TIMER_OFF;
        }
        setUpdatedIntervalPriority();
        if (this.mInetrvalPriority) {
            return INTERVAL_PRIORITY_ON;
        }
        return INTERVAL_PRIORITY_OFF;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if ((!tag.equals(INTERVAL_PRIORITY) && !tag.equals(INTERVAL_PRIORITY_OFF) && !tag.equals(INTERVAL_PRIORITY_ON)) || TLCommonUtil.getInstance().isValidThemeForUpdatedOption()) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isSelfTimer() {
        setUpdatedIntervalPriority();
        return this.mSelfTimer;
    }

    public boolean isIntervalPriorityEnabled() {
        setUpdatedIntervalPriority();
        return this.mInetrvalPriority;
    }
}
