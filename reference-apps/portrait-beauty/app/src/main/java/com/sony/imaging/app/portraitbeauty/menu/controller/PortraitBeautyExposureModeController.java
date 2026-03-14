package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;

/* loaded from: classes.dex */
public class PortraitBeautyExposureModeController extends ExposureModeController {
    private static final String TAG = AppLog.getClassName();
    private static PortraitBeautyExposureModeController sInstance = null;

    private PortraitBeautyExposureModeController() {
    }

    public static PortraitBeautyExposureModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new PortraitBeautyExposureModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        return true;
    }
}
