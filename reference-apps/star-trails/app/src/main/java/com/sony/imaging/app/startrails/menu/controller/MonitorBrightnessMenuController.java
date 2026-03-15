package com.sony.imaging.app.startrails.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MonitorBrightnessMenuController extends AbstractController {
    public static final String TAG = "MonitorBrightness";
    public static MonitorBrightnessMenuController mInstance;
    private static String LAST_SELECTED_TAG = null;
    private static final ArrayList<String> sMenuItemList = new ArrayList<>();

    static {
        sMenuItemList.add("Normal");
        sMenuItemList.add(STConstants.MONITOR_BRIGHTNESS_OFF);
        sMenuItemList.add(TAG);
    }

    public static MonitorBrightnessMenuController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        LAST_SELECTED_TAG = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.MONITOR_BRIGHTNESS_KEY, STConstants.MONITOR_BRIGHTNESS_OFF);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static MonitorBrightnessMenuController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new MonitorBrightnessMenuController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private MonitorBrightnessMenuController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        LAST_SELECTED_TAG = value;
        if (value.equalsIgnoreCase("Normal")) {
            BackUpUtil.getInstance().setPreference(STBackUpKey.MONITOR_BRIGHTNESS_KEY, value);
        } else {
            BackUpUtil.getInstance().setPreference(STBackUpKey.MONITOR_BRIGHTNESS_KEY, value);
        }
        CameraNotificationManager.getInstance().requestNotify(TAG);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        Log.e(TAG, "MonitorBrightnessMenuController.getValue()");
        return LAST_SELECTED_TAG.equalsIgnoreCase("Normal") ? "Normal" : STConstants.MONITOR_BRIGHTNESS_OFF;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Log.e(TAG, "MonitorBrightnessMenuController.getSupportedValue()");
        return sMenuItemList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        Log.e(TAG, "MonitorBrightnessMenuController.getAvailableValue()");
        return sMenuItemList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Log.e(TAG, "MonitorBrightnessMenuController.isAvailable()");
        return true;
    }
}
