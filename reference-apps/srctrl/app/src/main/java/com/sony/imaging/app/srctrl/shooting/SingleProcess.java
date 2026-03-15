package com.sony.imaging.app.srctrl.shooting;

import android.util.Log;
import com.sony.imaging.app.base.common.PfWorkaround;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SingleProcess implements ICaptureProcess {
    private static final String TAG = SingleProcess.class.getSimpleName();
    private IAdapter mAdapter;
    private CameraEx mCameraEx;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (Environment.isCancelExposureSupported) {
            Log.d(TAG, "hit cancelExposure");
            ExecutorCreator.getInstance().getSequence().getCameraEx().cancelExposure();
        }
        this.mCameraEx.cancelTakePicture();
        try {
            this.mCameraEx.cancelSelfTimerShutter();
        } catch (Exception e) {
            Log.i(TAG, "not selftimer mode");
        }
        this.mCameraEx.getNormalCamera().cancelAutoFocus();
        this.mCameraEx = null;
        this.mAdapter = null;
        if (!SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            PfWorkaround.sleepUntilCaptureEnd();
            boolean timeout_happended = PfWorkaround.sleepUntilSdWriteEnd(15000L);
            if (timeout_happended) {
                throw new IllegalStateException("SD write wait timeouted");
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        Log.v(TAG, "takePicture without SelfTimer");
        try {
            this.mCameraEx.burstableTakePicture();
        } catch (Exception e) {
            Log.e(TAG, "burstableTakePicture failed");
            onShutter(2, this.mCameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        Log.v(TAG, "takePicture with SelfTimer");
        this.mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        this.mAdapter.enableNextCapture(status);
    }
}
