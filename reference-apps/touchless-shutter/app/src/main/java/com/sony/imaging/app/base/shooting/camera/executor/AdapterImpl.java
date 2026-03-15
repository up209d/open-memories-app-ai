package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public abstract class AdapterImpl implements IAdapter {
    static final String LOG_ENABLE_NEXT_CAPTURE = "enableNextCapture";
    static final String LOG_ON_PROGRESS = "onProgress";
    static final String LOG_ON_SHUTTER = "onShutter";
    static final String LOG_ON_SHUTTER_SEQUENCE = "onShutterSequence";
    static final String LOG_PREPARE = "prepare";
    static final String LOG_PRE_TAKE_PICTURE = "preTakePicture";
    static final String LOG_START_SELF_TIMER = "startSelfTimer";
    static final String LOG_TAKE_PICTURE = "takePicture";
    static final String LOG_TERMINATE = "terminate";
    protected static final String TAG = "AdapterImpl";
    protected Camera mCamera;
    protected CameraEx mCameraEx;
    protected ShootingExecutor mExecutor = null;
    protected IProcess mProcess = null;

    public AdapterImpl(Camera camera, CameraEx cameraEx) {
        this.mCamera = null;
        this.mCameraEx = null;
        this.mCamera = camera;
        this.mCameraEx = cameraEx;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void prepare() {
        Log.i(TAG, LOG_PREPARE);
        this.mProcess.prepare(this.mCameraEx, this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void terminate() {
        Log.i(TAG, LOG_TERMINATE);
        this.mProcess.terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setProcess(IProcess process) {
        this.mProcess = process;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void setExecutor(ShootingExecutor shootingExecutor) {
        this.mExecutor = shootingExecutor;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public void lockCancelTakePicture(boolean lock) {
        this.mExecutor.lockCancelTakePicture(lock);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IAdapter
    public boolean enableHalt(boolean isEnable) {
        this.mExecutor.enableTermination(isEnable);
        if (!isEnable) {
            return false;
        }
        boolean ret = this.mExecutor.tryRelease();
        return ret;
    }
}
