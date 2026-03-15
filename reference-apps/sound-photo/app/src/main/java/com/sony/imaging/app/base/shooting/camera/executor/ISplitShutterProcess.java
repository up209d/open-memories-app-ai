package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public interface ISplitShutterProcess extends IProcess {
    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void onShutter(int i, CameraEx cameraEx);

    void onSplitShutterSequence(CameraSequence.RawData rawData, CameraSequence.SplitExposureProgressInfo splitExposureProgressInfo, CameraSequence cameraSequence);

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void prepare(CameraEx cameraEx, IAdapter iAdapter);

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IProcess
    void terminate();
}
