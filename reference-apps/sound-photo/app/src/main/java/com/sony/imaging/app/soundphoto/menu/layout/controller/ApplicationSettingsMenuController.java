package com.sony.imaging.app.soundphoto.menu.layout.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ApplicationSettingsMenuController extends AbstractController {
    public static final String APPLICATION_SETTINGS = "ApplicationSettings";
    private static final String TAG = "ApplicationSettingsMenuController";
    private static ApplicationSettingsMenuController mInstance;
    private static ArrayList<String> sSupportedList;
    private int mPostDuration = -1;
    private int mTotalDuration = -1;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("ApplicationSettings");
    }

    public static ApplicationSettingsMenuController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static ApplicationSettingsMenuController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ApplicationSettingsMenuController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private ApplicationSettingsMenuController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return tag;
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

    public void updateThemeParameter(int post_duration, int duration) {
        this.mPostDuration = post_duration;
        this.mTotalDuration = duration;
    }

    public void updateBackupValue() {
        BackUpUtil.getInstance().setPreference(SPBackUpKey.POST_DURATION_KEY, Integer.valueOf(this.mPostDuration));
        BackUpUtil.getInstance().setPreference(SPBackUpKey.TOTAL_DURATION_KEY, Integer.valueOf(this.mTotalDuration));
    }

    public int getTotalDuration() {
        if (-1 == this.mTotalDuration) {
            this.mTotalDuration = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.TOTAL_DURATION_KEY, 10);
        }
        return this.mTotalDuration;
    }

    public int getPostDuration() {
        if (-1 == this.mPostDuration) {
            this.mPostDuration = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.POST_DURATION_KEY, 2);
        }
        return this.mPostDuration;
    }

    public int getPreAudioTime() {
        return getTotalDuration() - getPostDuration();
    }
}
