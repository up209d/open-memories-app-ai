package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureCameraSettings;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.util.AvailableInfo;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FirstShootingController extends AbstractController {
    private static final String TAG_FIRSTSHOOTING = "FirstShooting";
    private List<String> mSupportedList;
    private static final String TAG = AppLog.getClassName();
    private static FirstShootingController sInstance = null;

    private FirstShootingController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList();
            this.mSupportedList.add(TAG_FIRSTSHOOTING);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static FirstShootingController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new FirstShootingController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        setDefaultFocusModeSoftFilter();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return null;
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
                if (AvailableInfo.isAvailable(TAG_FIRSTSHOOTING, mode)) {
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
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            bRetVal = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = isUnavailableAPISceneFactor(TAG_FIRSTSHOOTING, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    private void setDefaultFocusModeSoftFilter() {
        if (ThemeSelectionController.SOFTFILTER.equalsIgnoreCase(ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION))) {
            DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
            DoubleExposureUtil.getInstance().setInitFocusMode(true);
            cameraSettings.setDefaultFocusMode("af-s");
        }
    }
}
