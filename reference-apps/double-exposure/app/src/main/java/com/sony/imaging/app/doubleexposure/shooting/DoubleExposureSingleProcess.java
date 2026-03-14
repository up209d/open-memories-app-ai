package com.sony.imaging.app.doubleexposure.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class DoubleExposureSingleProcess implements ICaptureProcess {
    private static final String TAG = AppLog.getClassName();
    private CameraEx mCameraEx = null;
    private IAdapter mAdapter = null;
    private CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx = null;
        this.mAdapter = null;
        this.mAutoPictureReviewControl = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setAutoReviewControl();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.burstableTakePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.startSelfTimerShutter();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter.enableNextCapture(status);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setAutoReviewControl() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
        }
        this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        this.mAutoPictureReviewControl.setPictureReviewTime(this.mAutoPictureReviewControl.getPictureReviewSupportedTimes()[1]);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
