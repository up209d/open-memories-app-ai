package com.sony.imaging.app.smoothreflection.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class NormalBurstExecutor extends NormalExecutor {
    private static final String TAG = AppLog.getClassName();
    private NormalBurstShutterCb mShutterCb = null;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mShutterCb = new NormalBurstShutterCb(this.mAdapter);
        super.prepare(camera, cameraEx);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    private class NormalBurstShutterCb extends ShootingExecutor.MyShutterCb {
        IAdapter mAdapter;

        public NormalBurstShutterCb(IAdapter adapter) {
            this.mAdapter = null;
            this.mAdapter = adapter;
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor.MyShutterCb
        public void onShutter(int status, CameraEx cameraEx) {
            super.onShutter(status, cameraEx);
            if (1 == NormalBurstExecutor.this.getShutterType()) {
                cameraEx.cancelTakePicture();
            }
            if (this.mAdapter != null) {
                this.mAdapter.onShutter(status, cameraEx);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected CameraEx.ShutterListener getShutterListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mShutterCb;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        lockAutoFocus(true);
        super.takePicture();
        lockAutoFocus(true);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
