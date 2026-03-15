package com.sony.imaging.app.graduatedfilter.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.executor.CaptureImagingAdapterImpl;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class GFAdapterImpl extends CaptureImagingAdapterImpl {
    public GFAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
    }

    public CameraSequence getSequence() {
        return this.mSequence;
    }

    public void setSequence() {
        this.mSequence = CameraSequence.open(this.mCameraEx);
        SFRSA.getInstance().setCameraSequence(this.mSequence);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void prepare() {
        super.prepare();
        SFRSA.getInstance().setCameraSequence(this.mSequence);
    }
}
