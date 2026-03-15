package com.sony.imaging.app.each.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.each.EachApp;

/* loaded from: classes.dex */
public class TLS_DevelopmentStateKeyHandler extends DevelopmentStateKeyHandler {
    String TAG = "TLS_DevelopmentStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(this.TAG, "pushedS2Key");
        if (EachApp.ExposingByTouchLessShutter) {
            return -1;
        }
        return super.pushedS2Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        Log.d(this.TAG, "releasedS2Key");
        if (EachApp.ExposingByTouchLessShutter || isBulbOpenedBy(1)) {
            return -1;
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(0.0d));
        return super.releasedS2Key();
    }

    protected boolean isBulbOpenedBy(int shutterType) {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        return executor.isBulbOpened() && executor.getShutterType() == shutterType;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        Log.d(this.TAG, "pushedIRShutterKey");
        if (EachApp.ExposingByTouchLessShutter) {
            return -1;
        }
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        Log.d(this.TAG, "pushedIR2SecKey");
        if (EachApp.ExposingByTouchLessShutter) {
            return -1;
        }
        return super.pushedIR2SecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        Log.d(this.TAG, "pushedIRShutterNotCheckDrivemodeKey");
        if (EachApp.ExposingByTouchLessShutter) {
            return -1;
        }
        return super.pushedIRShutterNotCheckDrivemodeKey();
    }
}
