package com.sony.imaging.app.srctrl.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class NormalExecutorEx extends NormalExecutor {
    private static final String TAG = NormalExecutorEx.class.getSimpleName();
    private int pictureReviewTime = 0;
    private Boolean longExposureNRSettingValue = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        Log.v(TAG, "myRelease");
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S2_ON);
        if (status.status == 1) {
            Log.v(TAG, "myCancelTakePicture");
            myCancelTakePicture();
        }
        super.myRelease();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        super.prepare(camera, cameraEx);
        this.pictureReviewTime = sAutoReviewControl.getPictureReviewTime();
        this.longExposureNRSettingValue = null;
    }

    public void disablePictureReview() {
        sAutoReviewControl.setPictureReviewTime(0);
    }

    public void enablePictureReview() {
        sAutoReviewControl.setPictureReviewTime(this.pictureReviewTime);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void cancelTakePicture() {
        Log.v(TAG, "cancelTakePicture!!!");
        StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
        if ((StateController.AppCondition.SHOOTING_REMOTE.equals(appCondition) || StateController.AppCondition.SHOOTING_LOCAL.equals(appCondition)) && CameraSetting.getInstance().isShutterSpeedBulb()) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_STILL_POST_PROCESSING);
            CaptureStateUtil.getUtil().onBulbShuterEnd();
        }
        super.cancelTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture(int shutterType) {
        Log.v(TAG, "takePicture(int shutterType)");
        setAutoReviewByRemoteOrLocalShooting();
        setLongExposureNR();
        super.takePicture(shutterType);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture() {
        Log.v(TAG, "takePicture");
        setAutoReviewByRemoteOrLocalShooting();
        setLongExposureNR();
        super.takePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startSelfTimerShutter() {
        Log.v(TAG, "startSelfTimerShutter");
        setAutoReviewByRemoteOrLocalShooting();
        setLongExposureNR();
        super.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startSelfTimerShutter(int shutterType) {
        Log.v(TAG, "startSelfTimerShutter(int shutterType)");
        setAutoReviewByRemoteOrLocalShooting();
        setLongExposureNR();
        super.startSelfTimerShutter(shutterType);
    }

    private void setAutoReviewByRemoteOrLocalShooting() {
        if (StateController.AppCondition.SHOOTING_REMOTE.equals(StateController.getInstance().getAppCondition())) {
            disablePictureReview();
        } else {
            enablePictureReview();
        }
    }

    private boolean isLongExposureNR() {
        return ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getLongExposureNR();
    }

    private void setLongExposureNR() {
        Log.v(TAG, "setLongExposureNR Start.");
        if (!Environment.isCancelExposureSupported) {
            if (CameraSetting.getInstance().isShutterSpeedBulb()) {
                setLongExposureNRCache();
                setLongExposureNR(false);
            } else {
                resetLongExposureNR();
            }
        }
        Log.v(TAG, "setLongExposureNR End.");
    }

    private void resetLongExposureNR() {
        if (this.longExposureNRSettingValue != null) {
            setLongExposureNR(this.longExposureNRSettingValue.booleanValue());
            this.longExposureNRSettingValue = null;
        }
    }

    private void setLongExposureNRCache() {
        if (this.longExposureNRSettingValue == null) {
            this.longExposureNRSettingValue = Boolean.valueOf(isLongExposureNR());
        }
    }

    private void setLongExposureNR(boolean value) {
        Camera.Parameters sparam = CameraSetting.getInstance().getCamera().createEmptyParameters();
        CameraEx.ParametersModifier sparamex = CameraSetting.getInstance().getCamera().createParametersModifier(sparam);
        sparamex.setLongExposureNR(value);
        Log.d(TAG, "Long Exposure NR: " + value);
        CameraSetting.getInstance().getCamera().getNormalCamera().setParameters(sparam);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        Log.v(TAG, "terminate");
        enablePictureReview();
        resetLongExposureNR();
        super.terminate();
    }
}
