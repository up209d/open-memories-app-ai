package com.sony.imaging.app.each.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.each.EachApp;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SingleProcess implements ICaptureProcess {
    private IAdapter mAdapter;
    private CameraEx mCameraEx;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (EachApp.is_cancelExposure_available()) {
            Log.d("SingleProcess", "hit cancelExposure");
            ExecutorCreator.getInstance().getSequence().getCameraEx().cancelExposure();
        }
        this.mCameraEx = null;
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        try {
            this.mCameraEx.burstableTakePicture();
        } catch (Exception e) {
            Log.w("SingleProcess", "mCameraEx.burstableTakePicture() error");
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            this.mAdapter.enableNextCapture(2);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        if (status != 0) {
            Log.w("SingleProcess", "onShutter status:" + status);
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        }
        this.mAdapter.enableNextCapture(status);
    }
}
