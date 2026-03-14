package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.util.AvailableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class ReverseController extends AbstractController {
    public static final String HORIZONTAL = "Horizontal";
    public static final String OFF = "Off";
    public static final String TAG_REVERSE = "Reverse";
    public static final String VERTICAL = "Vertical";
    private String mSelectedValue = "Off";
    private List<String> mSupportedList;
    private static final String TAG = AppLog.getClassName();
    private static ReverseController sInstance = null;
    private static final HashMap<String, String> REVERSE_VALUES = new HashMap<>();

    static {
        REVERSE_VALUES.put("Off", "Off");
        REVERSE_VALUES.put(HORIZONTAL, HORIZONTAL);
        REVERSE_VALUES.put(VERTICAL, VERTICAL);
    }

    private ReverseController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList();
            this.mSupportedList.add(TAG_REVERSE);
            this.mSupportedList.add("Off");
            this.mSupportedList.add(HORIZONTAL);
            this.mSupportedList.add(VERTICAL);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static ReverseController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new ReverseController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().requestNotify(TAG_REVERSE);
        this.mSelectedValue = value;
        DoubleExposureUtil doubleExposureUtil = DoubleExposureUtil.getInstance();
        if (DoubleExposureConstant.FIRST_SHOOTING.equals(doubleExposureUtil.getCurrentShootingScreen())) {
            doubleExposureUtil.setReverseFirstShooting(value);
        } else {
            doubleExposureUtil.setReverseSecondShooting(value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
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
                if (AvailableInfo.isAvailable(TAG_REVERSE, mode)) {
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
        boolean bRetVal = true;
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (tag.equalsIgnoreCase(TAG_REVERSE) && (selectedTheme.equalsIgnoreCase("Rotation") || selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR))) {
            bRetVal = false;
        } else if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.SECOND_SHOOTING)) {
            bRetVal = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = isUnavailableAPISceneFactor(TAG_REVERSE, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }
}
