package com.sony.imaging.app.startrails.shooting.keyhandler;

import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STCaptureStateKeyHandler extends CaptureStateKeyHandler {
    private static final String TAG = "STCaptureStateKeyHandler";

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (STConstants.sCaptureImageCounter >= 1) {
            int device = DisplayModeObserver.getInstance().getActiveDevice();
            if (device != 1) {
                STCaptureDisplayModeObserver.getInstance().toggleDisplayMode(0);
            }
            AppLog.exit(TAG, AppLog.getMethodName());
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFocusKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAfMfSlideKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (STUtility.getInstance().isPreTakePictureTestShot() || MediaNotificationManager.getInstance().isError()) {
            return -1;
        }
        if (STSelfTimerMenuController.getInstance().isSelfTimer() && STUtility.getInstance().isCapturingStarted()) {
            cancelTakePicture();
            return -1;
        }
        if (STConstants.sCaptureImageCounter > 1) {
            cancelTakePicture();
            return -1;
        }
        if (STSelfTimerMenuController.getInstance().isSelfTimer() && STUtility.getInstance().isSelfTimerProcessing() && !STUtility.getInstance().isPreTakePictureTestShot()) {
            cancelTakePicture();
            return -1;
        }
        if (STSelfTimerMenuController.getInstance().isSelfTimer() || STConstants.sCaptureImageCounter <= 1) {
            return 0;
        }
        cancelTakePicture();
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

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (STUtility.getInstance().isPreTakePictureTestShot()) {
            return -1;
        }
        if ((STUtility.getInstance().isPreTakePictureTestShot() && STConstants.sCaptureImageCounter == 0) || !STUtility.getInstance().isPreTakePictureTestShot()) {
            cancelTakePicture();
        }
        return 1;
    }

    private void cancelTakePicture() {
        Log.i(TAG, "STCaptureStateKeyHandler:  cancelTakePicture()  ");
        if (STUtility.getInstance().isSelfTimerProcessing()) {
            STUtility.getInstance().setCaptureStatus(false);
        }
        notifyCancelCapture();
        if (STUtility.getInstance().isPreTakePictureTestShot() && STUtility.getInstance().isSelfTimerProcessing()) {
            STUtility.getInstance().setActualMediaIds();
        }
    }

    private void notifyCancelCapture() {
        CameraNotificationManager.getInstance().requestNotify(STConstants.REMOVE_CAPTURE_CALLBACK);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        cancelTakePicture();
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        STUtility.getInstance().setDetachedLensStatus(true);
        cancelTakePicture();
        return super.detachedLens();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfAelKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }
}
