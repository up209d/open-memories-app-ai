package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class SplitShutterCaptureImagingAdapterImpl extends AdapterImpl implements ICaptureAdapater, CameraSequence.SplitShutterSequenceCallback {
    private static final String LOG_FAILED_SELF_TIMER = "startSelfTimerShutter is failed";
    public static final String TAG = "SplitShutterCaptureImagingAdapterImpl";
    protected boolean isNeedReleaseLock;
    private ICaptureProcess mCaptureProcess;
    protected CameraSequence mSequence;
    protected ISplitShutterProcess mSplitShutterProcess;

    public SplitShutterCaptureImagingAdapterImpl(Camera camera, CameraEx cameraEx) {
        super(camera, cameraEx);
        this.isNeedReleaseLock = true;
        this.mSequence = null;
        this.mSplitShutterProcess = null;
        this.mCaptureProcess = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public String getName() {
        return TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean isSpecial() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mCaptureProcess = (ICaptureProcess) process;
        this.mSplitShutterProcess = (ISplitShutterProcess) process;
        super.setProcess(process);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void prepare() {
        Log.d(TAG, "prepare()");
        this.mSequence = CameraSequence.open(this.mCameraEx);
        setOptions(null);
        super.prepare();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setOptions(CameraSequence.Options opt) {
        Log.d(TAG, "setOptions(Options options)");
        if (this.isNeedReleaseLock) {
            if (opt == null) {
                opt = new CameraSequence.Options();
            }
            opt.setOption("AUTO_RELEASE_LOCK_ENABLED", true);
        }
        this.mSequence.setSplitShutterSequenceCallback(this, opt);
        Log.d(TAG, "setSplitShutterSequenceCallback(this, opt) setted");
        this.mSequence.setReleaseLock(false);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void takePicture() {
        Log.i(TAG, "takePicture()");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
        this.mCaptureProcess.takePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureAdapater
    public boolean startSelfTimerShutter() {
        Log.i(TAG, "startSelfTimerShutter()");
        this.mCaptureProcess.preTakePicture();
        this.mExecutor.lockAutoFocus(true);
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
    public void onShutter(int status, CameraEx cameraEx) {
        Log.i(TAG, "onShutter(final int status, final CameraEx cameraEx)");
        this.mSplitShutterProcess.onShutter(status, cameraEx);
    }

    public void onSplitShutterSequence(CameraSequence.RawData raw, CameraSequence.SplitExposureProgressInfo info, CameraSequence sequence) {
        Log.i(TAG, "onSplitShutterSequence( RawData raw, SplitExposureProgressInfo info, CameraSequence sequence )");
        this.mSplitShutterProcess.onSplitShutterSequence(raw, info, sequence);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void onProgress(int current, int max) {
        Log.i(TAG, "onProgress(final int current, final int max)");
        double progress = current;
        ShootingExecutor.onProgress(progress / max);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.AdapterImpl, com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void terminate() {
        Log.i(TAG, "terminate()");
        this.mSequence.setReleaseLock(false);
        if (this.mSequence != null) {
            this.mSequence.release();
            this.mSequence = null;
        }
        super.terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void enableNextCapture(int status) {
        this.mExecutor.lockAutoFocus(false);
        this.mExecutor.onStartShutter(status, this.mCameraEx);
        if (this.isNeedReleaseLock && this.mSequence != null) {
            Log.i(TAG, "setReleaseLock");
            this.mSequence.setReleaseLock(false);
        }
    }
}
