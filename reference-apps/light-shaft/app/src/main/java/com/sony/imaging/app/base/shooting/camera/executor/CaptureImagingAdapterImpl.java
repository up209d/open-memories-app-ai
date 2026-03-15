package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class CaptureImagingAdapterImpl extends ImagingAdapterImpl implements IImagingAdapter, ICaptureAdapater {
    public static final String TAG = "CaptureImagingAdapterImpl";
    private ICaptureProcess mCaptureProcess;

    public CaptureImagingAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mCaptureProcess = null;
        this.isNeedReleaseLock = false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        Log.i(TAG, "preTakePicture");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
        Log.i(TAG, "takePicture");
        this.mCaptureProcess.takePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater
    public void startSelfTimerShutter() {
        Log.i(TAG, "preTakePicture");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
        Log.i(TAG, "startSelfTimer");
        this.mCaptureProcess.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        this.mExecutor.lockAutoFocus(false);
        super.enableNextCapture(status);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        return true;
    }
}
