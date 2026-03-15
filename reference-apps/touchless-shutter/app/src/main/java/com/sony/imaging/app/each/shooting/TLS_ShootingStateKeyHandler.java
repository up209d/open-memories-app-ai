package com.sony.imaging.app.each.shooting;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;

/* loaded from: classes.dex */
public class TLS_ShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(0.0d));
        return super.releasedS2Key();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler
    public void stopBulbShooting() {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (executor.isBulbOpened()) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(0.0d));
        }
        super.stopBulbShooting();
    }
}
