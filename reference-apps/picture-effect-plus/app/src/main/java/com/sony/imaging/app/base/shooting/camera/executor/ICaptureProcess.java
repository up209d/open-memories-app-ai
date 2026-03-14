package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public interface ICaptureProcess extends IProcess {
    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void onShutter(int i, CameraEx cameraEx);

    void preTakePicture();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void prepare(CameraEx cameraEx, IAdapter iAdapter);

    void startSelfTimerShutter();

    void takePicture();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void terminate();
}
