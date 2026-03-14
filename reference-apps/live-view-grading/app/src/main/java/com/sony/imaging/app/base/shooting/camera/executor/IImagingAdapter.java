package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public interface IImagingAdapter extends IAdapter {
    void onShutterSequence(CameraSequence.RawData rawData, CameraSequence cameraSequence);
}
