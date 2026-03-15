package com.sony.imaging.app.graduatedfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;

/* loaded from: classes.dex */
public class GFCaptureStateKeyHandler extends CaptureStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (GFCommonUtil.getInstance().duringSelfTimer()) {
            GFCommonUtil.getInstance().setDuringSelfTimer(false);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
            return 1;
        }
        return 1;
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
