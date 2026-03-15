package com.sony.imaging.app.smoothreflection.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SingleProcess implements ICaptureProcess {
    private static final String TAG = AppLog.getClassName();
    private IAdapter mAdapter;
    private CameraEx mCameraEx;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx = null;
        this.mAdapter = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.burstableTakePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.startSelfTimerShutter();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter.enableNextCapture(status);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
