package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.util.AvailableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class SecondShootingPriorityController extends AbstractController {
    public static final String OFF = "Off";
    public static final String ON = "On";
    public static final String TAG_SECONDSHOOTING_PRIORITY = "SecondShootingPriority";
    private String mSelectedValue = "Off";
    private List<String> mSupportedList;
    private static final String TAG = AppLog.getClassName();
    private static SecondShootingPriorityController sInstance = null;
    private static final HashMap<String, String> SECOND_SHOOTING_VALUES = new HashMap<>();

    static {
        SECOND_SHOOTING_VALUES.put("Off", "Off");
        SECOND_SHOOTING_VALUES.put("On", "On");
    }

    private SecondShootingPriorityController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList();
            this.mSupportedList.add(TAG_SECONDSHOOTING_PRIORITY);
            this.mSupportedList.add("Off");
            this.mSupportedList.add("On");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static SecondShootingPriorityController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SecondShootingPriorityController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedValue = value;
        CameraNotificationManager.getInstance().requestNotify(TAG_SECONDSHOOTING_PRIORITY);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList.isEmpty()) {
            this.mSupportedList = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(null);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable(TAG_SECONDSHOOTING_PRIORITY, mode)) {
                    availables.add(mode);
                }
            }
        }
        if (availables.isEmpty()) {
            availables = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = isUnavailableAPISceneFactor(TAG_SECONDSHOOTING_PRIORITY, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }
}
