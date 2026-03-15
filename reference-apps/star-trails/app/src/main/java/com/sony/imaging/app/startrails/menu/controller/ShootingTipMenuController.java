package com.sony.imaging.app.startrails.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ShootingTipMenuController extends AbstractController {
    public static final String TAG = "shootingtip";
    public static ShootingTipMenuController mInstance;
    private static final ArrayList<String> sMenuItemList = new ArrayList<>();
    private String LAST_SELECTED_TAG = "selftimeron";

    static {
        sMenuItemList.add(TAG);
    }

    public static ShootingTipMenuController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static ShootingTipMenuController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ShootingTipMenuController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private ShootingTipMenuController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.LAST_SELECTED_TAG = value;
        if (value.equalsIgnoreCase("Normal")) {
            BackUpUtil.getInstance().setPreference("Normal", value);
        } else {
            BackUpUtil.getInstance().setPreference(STConstants.MONITOR_BRIGHTNESS_OFF, value);
        }
        CameraNotificationManager.getInstance().requestNotify(TAG);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        Log.e(TAG, "ShootingTipMenuController.getValue()");
        return this.LAST_SELECTED_TAG.equalsIgnoreCase(STConstants.MOVIE_1920_1080) ? STConstants.MOVIE_1920_1080 : STConstants.MOVIE_1280_720;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Log.e(TAG, "ShootingTipMenuController.getSupportedValue()");
        return sMenuItemList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sMenuItemList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Log.e(TAG, "ShootingTipMenuController.isAvailable()");
        return true;
    }
}
