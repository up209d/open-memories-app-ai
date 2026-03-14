package com.sony.imaging.app.smoothreflection.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;

/* loaded from: classes.dex */
public class SmoothReflectionCaptureStateKeyHandler extends CaptureStateKeyHandler {
    public static final String CANCELTAKEPICTURETAG = "CancelTakePictureTag";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
            return -1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedModeDial();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfAelKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionCompositProcess.mShootNumber >= 1) {
            CameraNotificationManager.getInstance().requestNotify(CANCELTAKEPICTURETAG);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }
}
