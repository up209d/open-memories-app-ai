package com.sony.imaging.app.startrails.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class STSelfTimerMenuController extends AbstractController {
    public static final String DRIVE_MODE = "drivemode";
    public static final String SELF_TIMER_OFF = "selftimeroff";
    public static final String SELF_TIMER_ON = "selftimeron";
    private static final String TAG = "STSelfTimerMenuController";
    private static STSelfTimerMenuController mInstance;
    private static boolean sSelfTimer = true;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("drivemode");
        sSupportedList.add("selftimeron");
        sSupportedList.add("selftimeroff");
    }

    public static STSelfTimerMenuController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        sSelfTimer = BackUpUtil.getInstance().getPreferenceBoolean(STBackUpKey.SELF_TIMER_STATUS_KEY, true);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static STSelfTimerMenuController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new STSelfTimerMenuController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private STSelfTimerMenuController() {
        this.mBackupUtil = null;
        if (this.mBackupUtil == null) {
            this.mBackupUtil = BackUpUtil.getInstance();
        }
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals("drivemode")) {
            if (value.equals("selftimeron")) {
                sSelfTimer = true;
            } else {
                sSelfTimer = false;
            }
            this.mBackupUtil.setPreference(STBackUpKey.SELF_TIMER_STATUS_KEY, Boolean.valueOf(sSelfTimer));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = "selftimeroff";
        AppLog.enter(TAG, AppLog.getMethodName());
        if (tag.equals("drivemode")) {
            sSelfTimer = this.mBackupUtil.getPreferenceBoolean(STBackUpKey.SELF_TIMER_STATUS_KEY, true);
            if (sSelfTimer) {
                value = "selftimeron";
            } else {
                value = "selftimeroff";
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isSelfTimer() {
        return sSelfTimer;
    }
}
