package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public interface IImagingProcess extends IProcess {
    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void onShutter(int i, CameraEx cameraEx);

    void onShutterSequence(CameraSequence.RawData rawData, CameraSequence cameraSequence);

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void prepare(CameraEx cameraEx, IAdapter iAdapter);

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void terminate();
}
