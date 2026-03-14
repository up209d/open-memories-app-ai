package com.sony.imaging.app.base.shooting.camera.executor;

/* loaded from: classes.dex */
public class StableExecutor extends NormalExecutor {
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        onStartShutter(1, sCameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myStartSelfTimerShutter() {
        onStartShutter(1, sCameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelSelfTimerShutter() {
    }
}
