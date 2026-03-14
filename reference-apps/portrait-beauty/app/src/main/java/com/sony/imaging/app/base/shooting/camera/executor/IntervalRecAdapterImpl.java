package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class IntervalRecAdapterImpl extends AdapterImpl implements ICaptureAdapater {
    private static final String LOG_ENABLE_NEXTCAPTURE = "enableNextCapture";
    private static final String LOG_ON_PROGRESS_WARNING = "this Adapter does't handle onProgress method.";
    private static final String LOG_SET_OPTIONS_WARNING = "this Adapter does't handle setOptions method.";
    private static final String LOG_START_SELF_TIMER_SHUTTER = "startSelfTimerShutter";
    private static final String LOG_TAKE_PICTURE = "takePicture";
    public static final String TAG = "IntervalRecAdapterImpl";
    protected ICaptureProcess mCaptureProcess;

    public IntervalRecAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mCaptureProcess = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        Log.i(TAG, LOG_TAKE_PICTURE);
        this.mExecutor.lockAutoFocus(true);
        this.mCaptureProcess.takePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        Log.i(TAG, LOG_ENABLE_NEXTCAPTURE);
        this.mExecutor.onStartShutter(status, this.mCameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onShutter(int status, CameraEx cameraEx) {
        this.mCaptureProcess.onShutter(status, cameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setOptions(CameraSequence.Options options) {
        Log.w(TAG, LOG_SET_OPTIONS_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onProgress(int current, int max) {
        Log.w(TAG, LOG_ON_PROGRESS_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater
    public boolean startSelfTimerShutter() {
        Log.i(TAG, LOG_START_SELF_TIMER_SHUTTER);
        return false;
    }
}
