package com.sony.imaging.app.soundphoto.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SPSingleProcess implements ICaptureProcess {
    private static final String TAG = "SPSingleProcess";
    private static CameraEx mCameraEx;
    private IAdapter mAdapter;
    private CameraEx.AutoPictureReviewControl mAutoPicRevCtl = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mCameraEx = cameraEx;
        this.mAdapter = adapter;
        AppLog.exit(TAG, AppLog.getMethodName());
        setAutoReviewControl();
    }

    private void setAutoReviewControl() {
        if (this.mAutoPicRevCtl == null) {
            this.mAutoPicRevCtl = new CameraEx.AutoPictureReviewControl();
        }
        mCameraEx.setAutoPictureReviewControl(this.mAutoPicRevCtl);
        this.mAutoPicRevCtl.setPictureReviewTime(0);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        mCameraEx = null;
        this.mAdapter = null;
        this.mAutoPicRevCtl = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPShootingState.getInstance().startPreCameraSequenceCount();
        mCameraEx.burstableTakePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, "onShutter()");
        if (status == 0) {
            SPUtil.getInstance().printKikiLog(SPConstants.SOUND_PHOTO_CAPTURE_KIKILOG_ID);
            SPShootingState.getInstance().setDecideLastSequenceID(true);
        } else {
            SPShootingState.getInstance().setDecideLastSequenceID(false);
        }
        SPShootingState.getInstance().endPreCameraSequenceCount();
        this.mAdapter.enableNextCapture(status);
        AppLog.exit(TAG, "onShutter()");
    }
}
