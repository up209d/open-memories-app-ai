package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.IntervalRecCaptureState;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class CaptureAdapterImpl extends AdapterImpl implements ICaptureAdapater {
    private static final String LOG_FAILED_SELF_TIMER = "startSelfTimerShutter is failed";
    private static final String LOG_ON_PROGRESS_WARNING = "this Adapter does't handle onProgress method.";
    private static final String LOG_SET_OPTIONS_WARNING = "this Adapter does't handle setOptions method.";
    public static final String TAG = "CaptureAdapterImpl";
    private ICaptureProcess mCaptureProcess;

    public CaptureAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mCaptureProcess = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        this.mExecutor.lockAutoFocus(false);
        this.mExecutor.onStartShutter(status, this.mCameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onProgress(int current, int max) {
        Log.w(TAG, LOG_ON_PROGRESS_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onShutter(int status, CameraEx cameraEx) {
        Log.i(TAG, IntervalRecCaptureState.LOG_ON_SHUTTER);
        this.mCaptureProcess.onShutter(status, cameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setOptions(CameraSequence.Options options) {
        Log.w(TAG, LOG_SET_OPTIONS_WARNING);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater
    public boolean startSelfTimerShutter() {
        Log.i(TAG, "preTakePicture");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
        Log.i(TAG, "startSelfTimer");
        try {
            this.mCaptureProcess.startSelfTimerShutter();
            return true;
        } catch (RuntimeException e) {
            Log.i(TAG, LOG_FAILED_SELF_TIMER);
            enableNextCapture(1);
            return false;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        Log.i(TAG, "preTakePicture");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
        Log.i(TAG, "takePicture");
        this.mCaptureProcess.takePicture();
        this.mExecutor.lockAutoFocus(true);
    }
}
