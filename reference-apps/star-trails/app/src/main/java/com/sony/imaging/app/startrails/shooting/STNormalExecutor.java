package com.sony.imaging.app.startrails.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class STNormalExecutor extends NormalExecutor {
    private static final String TAG = "STNormalExecutor";
    private static boolean isRemConOn = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        AppLog.info(TAG, "myTakePicture");
        int type = getShutterType();
        if (2 == type) {
            isRemConOn = getOriginalRemoteControl();
        } else {
            isRemConOn = false;
        }
        if (isRemConOn) {
            setRemoteControlStatus(false);
        }
        enableTermination(false);
        this.mAdapter.takePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        AppLog.info(TAG, "myRelease");
        if (isRemConOn) {
            setRemoteControlStatus(true);
        }
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        mCameraEx.cancelTakePicture();
        super.myRelease();
    }

    private void setRemoteControlStatus(boolean status) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(status);
        sCamera.setParameters((Camera.Parameters) setParams.first);
    }
}
