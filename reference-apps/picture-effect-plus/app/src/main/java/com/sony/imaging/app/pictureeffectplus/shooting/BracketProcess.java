package com.sony.imaging.app.pictureeffectplus.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BracketProcess implements ICaptureProcess {
    private IAdapter mAdapter;
    private CameraEx mCameraEx;
    private int mShutterCount;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        this.mCameraEx = null;
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        this.mShutterCount = 0;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        this.mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        this.mShutterCount++;
        if (status == 0) {
            switch (this.mShutterCount) {
                case 1:
                case 2:
                    this.mCameraEx.burstableTakePicture();
                    return;
                default:
                    this.mAdapter.enableNextCapture(status);
                    return;
            }
        }
        this.mAdapter.enableNextCapture(status);
    }
}
