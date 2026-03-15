package com.sony.imaging.app.timelapse.shooting.state.keyhandler;

import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseMonitorBrightnessController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;

/* loaded from: classes.dex */
public class TimeLapseCaptureStateKeyHandler extends CaptureStateKeyHandler {
    private static final String TAG = "TimeLapseCaptureStateKeyHandler";

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (TimeLapseConstants.sCaptureImageCounter > 0 && DisplayModeObserver.getInstance().getActiveDevice() != 2) {
            TimelapseMonitorBrightnessController.getInstance().toggleSetting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (TLCommonUtil.getInstance().isTestShot()) {
            return -1;
        }
        if (MediaNotificationManager.getInstance().isError()) {
            return 1;
        }
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer() && TimeLapseStableLayout.isCapturing) {
            cancelTakePicture();
        }
        if (TimeLapseConstants.sCaptureImageCounter > 0) {
            cancelTakePicture();
        }
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            cancelTakePicture();
            return 1;
        }
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer() || TimeLapseConstants.sCaptureImageCounter <= 0) {
            return 1;
        }
        cancelTakePicture();
        return 1;
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
        if (TLCommonUtil.getInstance().isTestShot()) {
            return -1;
        }
        cancelTakePicture();
        return 1;
    }

    private void cancelTakePicture() {
        Log.i(TAG, "TimeLapseCaptureStateKeyHandler:  cancelTakePicture()  ");
        TimeLapseConstants.sCaptureImageCounter = 0;
        TimeLapseStableLayout.isCapturing = false;
        notifyCancelCapture();
        if (TLCommonUtil.getInstance().isTestShot() && SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            TLCommonUtil.getInstance().setActualMediaIds();
        }
    }

    private void notifyCancelCapture() {
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK);
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_TWO_SECOND);
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_STABLE);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        cancelTakePicture();
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        TLCommonUtil.getInstance().setDetachedLensStatus(true);
        cancelTakePicture();
        return super.detachedLens();
    }
}
