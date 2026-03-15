package com.sony.imaging.app.smoothreflection.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothExecutor extends NormalExecutor {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        lockAutoFocus(true);
        super.takePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        AppLog.enter(TAG, AppLog.getMethodName());
        lockAutoFocus(false);
        super.myRelease();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean tryRelease() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!SmoothReflectionCompositProcess.sbISShootingFinish) {
            lockAutoFocus(false);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.tryRelease();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        lockAutoFocus(false);
        super.terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void captureCustomWhiteBalance(CameraEx.CustomWhiteBalanceCallback listener) {
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        mCameraEx.cancelTakePicture();
        super.captureCustomWhiteBalance(listener);
    }
}
