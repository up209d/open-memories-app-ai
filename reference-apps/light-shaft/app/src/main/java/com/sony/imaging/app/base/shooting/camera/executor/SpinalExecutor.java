package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SpinalExecutor extends ShootingExecutor {
    private static final String LOG_CANCEL_SELF_TIMER_WARNING = "Self timer cancel function is not permitted in spinal mode.";
    private static final String LOG_ON_SHUTTER_STOPPED = "onShutterStopped";
    private static final String LOG_SELF_TIMER_WARNING = "Self timer function is not permitted in spinal mode.";
    private static final String LOG_START_DIRECT_SHUTTER = "startDirectShutter";
    private static final String LOG_STOP_DIRECT_SHUTTER = "stopDirectShutter";
    private static final String PTAG_SPINAL_SETTING = "set spinal shooting mode";
    private static final String TAG = "SpinalExecutor";
    static int mStatus;
    private SpinalShutterCb mShutterCb = null;
    private MyStopDirectShutterCb mMyStopDirectShutterCb = new MyStopDirectShutterCb(this);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        this.mShutterCb = new SpinalShutterCb(this, this.mAdapter);
        super.prepare(camera, cameraEx);
        Log.d(TAG, LOG_START_DIRECT_SHUTTER);
        sCameraEx.startDirectShutter();
        PTag.end(PTAG_SPINAL_SETTING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        Log.d(TAG, LOG_STOP_DIRECT_SHUTTER);
        sCameraEx.stopDirectShutter(this.mMyStopDirectShutterCb);
    }

    /* loaded from: classes.dex */
    private static class MyStopDirectShutterCb implements CameraEx.DirectShutterStoppedCallback {
        ShootingExecutor mExecutor;

        public MyStopDirectShutterCb(ShootingExecutor executor) {
            this.mExecutor = executor;
        }

        public void onShutterStopped(CameraEx cam) {
            Log.i(SpinalExecutor.TAG, SpinalExecutor.LOG_ON_SHUTTER_STOPPED);
            this.mExecutor.tryRelease();
        }
    }

    /* loaded from: classes.dex */
    class SpinalShutterCb extends ShootingExecutor.MyShutterCb {
        IAdapter mAdapter;
        ShootingExecutor mExecutor;

        public SpinalShutterCb(ShootingExecutor executor, IAdapter adapter) {
            this.mExecutor = executor;
            this.mAdapter = adapter;
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor.MyShutterCb
        public void onShutter(int status, CameraEx cameraEx) {
            if (status == 0 && this.mAdapter != null && this.mAdapter.isSpecial()) {
                SpinalExecutor.this.enableTermination(false);
            }
            super.onShutter(status, cameraEx);
            this.mExecutor.sendKey(AppRoot.USER_KEYCODE.S2_ON, false);
            SpinalExecutor.mStatus = status;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        super.terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        int status = mStatus;
        if (this.mAdapter != null) {
            this.mAdapter.onShutter(status, sCameraEx);
        } else {
            onStartShutter(status, sCameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myStartSelfTimerShutter() {
        Log.e(TAG, LOG_SELF_TIMER_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelSelfTimerShutter() {
        Log.e(TAG, LOG_CANCEL_SELF_TIMER_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void inquireKey(int key) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopPreview() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected CameraEx.ShutterListener getShutterListener() {
        return this.mShutterCb;
    }
}
