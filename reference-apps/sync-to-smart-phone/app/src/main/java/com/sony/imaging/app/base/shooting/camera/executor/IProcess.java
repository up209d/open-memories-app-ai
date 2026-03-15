package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public interface IProcess {
    void onShutter(int i, CameraEx cameraEx);

    void prepare(CameraEx cameraEx, IAdapter iAdapter);

    void terminate();
}
