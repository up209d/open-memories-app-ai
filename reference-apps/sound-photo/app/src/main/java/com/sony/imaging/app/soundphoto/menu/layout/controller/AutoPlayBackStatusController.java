package com.sony.imaging.app.soundphoto.menu.layout.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AutoPlayBackStatusController extends AbstractController {
    public static final String PLAY_STATUS = "autoplaybackstatus";
    public static final String PLAY_STATUS_OFF = "play_status_off";
    public static final String PLAY_STATUS_ON = "play_status_on";
    private static final String TAG = "AutoPlayBackStatusController";
    private static AutoPlayBackStatusController mInstance;
    private static boolean sPlayStatus = true;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(PLAY_STATUS);
        sSupportedList.add(PLAY_STATUS_ON);
        sSupportedList.add(PLAY_STATUS_OFF);
    }

    public static AutoPlayBackStatusController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        sPlayStatus = BackUpUtil.getInstance().getPreferenceBoolean(SPBackUpKey.AUTO_PLAY_BACK_STATUS_KEY, false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static AutoPlayBackStatusController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new AutoPlayBackStatusController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private AutoPlayBackStatusController() {
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
        if (PLAY_STATUS.equals(tag)) {
            if (PLAY_STATUS_ON.equals(value)) {
                sPlayStatus = true;
            } else {
                sPlayStatus = false;
            }
            this.mBackupUtil.setPreference(SPBackUpKey.AUTO_PLAY_BACK_STATUS_KEY, Boolean.valueOf(sPlayStatus));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = PLAY_STATUS_OFF;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (PLAY_STATUS.equals(tag)) {
            sPlayStatus = this.mBackupUtil.getPreferenceBoolean(SPBackUpKey.AUTO_PLAY_BACK_STATUS_KEY, false);
            if (sPlayStatus) {
                value = PLAY_STATUS_ON;
            } else {
                value = PLAY_STATUS_OFF;
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

    public boolean isAutoPlayBackOn() {
        return sPlayStatus;
    }
}
