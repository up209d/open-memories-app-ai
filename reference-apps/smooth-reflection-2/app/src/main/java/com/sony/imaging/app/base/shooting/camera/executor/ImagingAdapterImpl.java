package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.IntervalRecCaptureState;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class ImagingAdapterImpl extends AdapterImpl implements IImagingAdapter, CameraSequence.ShutterSequenceCallback {
    private static final String LOG_CAMERA_SEQUENCE = "CameraSequence";
    private static final String LOG_OPEN = "open ";
    private static final String LOG_RELEASE = "release ";
    private static final String LOG_SET_RELEASE_LOCK = "setReleaseLock";
    private static final String LOG_SET_SHUTTER_SEQUENCE_CALLBACK = "setShutterSequenceCallback";
    private static final StringBuilder STRBUILD = new StringBuilder();
    public static final String TAG = "ImagingAdapterImpl";
    protected boolean isNeedReleaseLock;
    IImagingProcess mImagingProcess;
    protected CameraSequence mSequence;

    public ImagingAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.mSequence = null;
        this.mImagingProcess = null;
        this.isNeedReleaseLock = true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        this.mExecutor.onStartShutter(status, this.mCameraEx);
        if (this.isNeedReleaseLock && this.mSequence != null) {
            Log.i(TAG, LOG_SET_RELEASE_LOCK);
            this.mSequence.setReleaseLock(false);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onProgress(int current, int max) {
        STRBUILD.replace(0, STRBUILD.length(), "onProgress").append(current / max);
        Log.i(TAG, STRBUILD.toString());
        double progress = current;
        ShootingExecutor.onProgress(progress / max);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onShutter(int status, CameraEx cameraEx) {
        Log.i(TAG, IntervalRecCaptureState.LOG_ON_SHUTTER);
        this.mImagingProcess.onShutter(status, cameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingAdapter
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.i(TAG, "onShutterSequence");
        this.mImagingProcess.onShutterSequence(raw, sequence);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void prepare() {
        STRBUILD.replace(0, STRBUILD.length(), LOG_OPEN).append(LOG_CAMERA_SEQUENCE);
        Log.i(TAG, STRBUILD.toString());
        this.mSequence = CameraSequence.open(this.mCameraEx);
        setOptions(null);
        super.prepare();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setOptions(CameraSequence.Options options) {
        if (this.isNeedReleaseLock) {
            if (options == null) {
                options = new CameraSequence.Options();
            }
            options.setOption("AUTO_RELEASE_LOCK_ENABLED", true);
        }
        Log.d(TAG, LOG_SET_SHUTTER_SEQUENCE_CALLBACK);
        this.mSequence.setShutterSequenceCallback(this, options);
        this.mSequence.setReleaseLock(false);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mImagingProcess = (IImagingProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void terminate() {
        STRBUILD.replace(0, STRBUILD.length(), LOG_RELEASE).append(LOG_CAMERA_SEQUENCE);
        Log.i(TAG, STRBUILD.toString());
        if (this.mSequence != null) {
            this.mSequence.release();
            this.mSequence = null;
        }
        super.terminate();
    }
}
