package com.sony.imaging.app.smoothreflection.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SaRawCompAdapterImpl extends ImagingAdapterImpl implements IImagingAdapter, ICaptureAdapater {
    private static final String TAG = AppLog.getClassName();
    private ICaptureProcess mCaptureProcess;

    public SaRawCompAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mCaptureProcess = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isNeedReleaseLock = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCaptureProcess.preTakePicture();
        this.mCaptureProcess.takePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater
    public boolean startSelfTimerShutter() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCaptureProcess.preTakePicture();
        this.mCaptureProcess.startSelfTimerShutter();
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.enableNextCapture(status);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
