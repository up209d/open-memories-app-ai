package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;

/* loaded from: classes.dex */
public class DoubleExposurePictureQualityController extends PictureQualityController {
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposurePictureQualityController sInstance = null;

    private DoubleExposurePictureQualityController() {
    }

    public static DoubleExposurePictureQualityController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposurePictureQualityController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean bRetVal;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.SECOND_SHOOTING)) {
            bRetVal = false;
        } else {
            bRetVal = super.isAvailable(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.SECOND_SHOOTING)) {
            ret = 1;
        } else {
            ret = super.getCautionIndex(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }
}
