package com.sony.imaging.app.timelapse.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class TLNormalExecutor extends NormalExecutor {
    private static final String TAG = "TLNormalExecutor";

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(false);
        sCamera.setParameters((Camera.Parameters) setParams.first);
        enableTermination(false);
        this.mAdapter.takePicture();
    }
}
