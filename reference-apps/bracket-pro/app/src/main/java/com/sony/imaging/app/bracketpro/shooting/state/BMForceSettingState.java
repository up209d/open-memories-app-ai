package com.sony.imaging.app.bracketpro.shooting.state;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BMForceSettingState extends ForceSettingState {
    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        BMEEState.isBMCautionStateBooted = true;
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        CameraEx.AutoPictureReviewControl mAutoPicRevCtl = new CameraEx.AutoPictureReviewControl();
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        mCameraEx.setAutoPictureReviewControl(mAutoPicRevCtl);
        BMCaptureProcess.sAutoReviewTime = mAutoPicRevCtl.getPictureReviewTime();
        ((CameraEx.ParametersModifier) params.second).setMultiShootNRMode(false);
        return params;
    }
}
