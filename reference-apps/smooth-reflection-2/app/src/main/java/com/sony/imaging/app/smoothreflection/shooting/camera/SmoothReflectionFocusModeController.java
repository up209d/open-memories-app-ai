package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.smoothreflection.common.AppLog;

/* loaded from: classes.dex */
public class SmoothReflectionFocusModeController extends FocusModeController {
    private static final String TAG = AppLog.getClassName();
    private static SmoothReflectionFocusModeController sInstance = null;

    private SmoothReflectionFocusModeController() {
    }

    public static SmoothReflectionFocusModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SmoothReflectionFocusModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }
}
