package com.sony.imaging.app.bracketpro.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class NormalBurstExecutor extends NormalExecutor {
    private NormalBurstShutterCb mShutterCb = null;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        this.mShutterCb = new NormalBurstShutterCb(this.mAdapter);
        super.prepare(camera, cameraEx);
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
        return this.mShutterCb;
    }
}
