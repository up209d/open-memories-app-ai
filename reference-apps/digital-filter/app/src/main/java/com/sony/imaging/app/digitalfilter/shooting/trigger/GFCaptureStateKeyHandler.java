package com.sony.imaging.app.digitalfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;

/* loaded from: classes.dex */
public class GFCaptureStateKeyHandler extends CaptureStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        cancelTakePicture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelTakePicture();
        return 1;
    }

    private void cancelTakePicture() {
        if (GFCommonUtil.getInstance().duringSelfTimer()) {
            GFCommonUtil.getInstance().setDuringSelfTimer(false);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
        } else if (GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE))) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.info(TAG, "Canceled by attachedLens.");
        GFCommonUtil.getInstance().cancelByLensChanging();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.info(TAG, "Canceled by detachedLens.");
        GFCommonUtil.getInstance().cancelByLensChanging();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
        return 1;
    }
}
