package com.sony.imaging.app.lightgraffiti.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.CaptureImagingAdapterImpl;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class LGImagingAdapterImpl extends CaptureImagingAdapterImpl {
    private ICaptureProcess mCaptureProcess;

    public LGImagingAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mCaptureProcess = null;
    }

    public CameraSequence getSequence() {
        return this.mSequence;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.CaptureImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.CaptureImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        Log.i(CaptureImagingAdapterImpl.TAG, "run takePicture");
        this.mCaptureProcess.takePicture();
    }
}
