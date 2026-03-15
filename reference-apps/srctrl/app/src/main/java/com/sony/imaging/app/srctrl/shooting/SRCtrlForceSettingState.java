package com.sony.imaging.app.srctrl.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SRCtrlForceSettingState extends ForceSettingState {
    private static final String tag = SRCtrlForceSettingState.class.getName();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        DriveModeController dController = DriveModeController.getInstance();
        String dMode = dController.getValue(DriveModeController.DRIVEMODE);
        if (dMode != null && !dMode.equals(DriveModeController.SINGLE) && !dMode.equals(DriveModeController.SELF_TIMER) && dController.isAvailable(DriveModeController.BURST) && !SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            dController.setValue(DriveModeController.DRIVEMODE, DriveModeController.SINGLE);
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> curParams = CameraSetting.getInstance().getParameters();
        if (((CameraEx.ParametersModifier) curParams.second).getPictureStorageFormat().equals(PictureQualityController.PICTURE_QUALITY_RAW)) {
            ((Camera.Parameters) params.first).setJpegQuality(50);
            ((CameraEx.ParametersModifier) params.second).setPictureStorageFormat(PictureQualityController.PICTURE_QUALITY_RAWJPEG);
        }
        if (SRCtrlEnvironment.getInstance().isIrisRingInvalid()) {
            String fNumSetViaWebApi = CameraOperationFNumber.getFNumberSetViaWebApi();
            if (fNumSetViaWebApi == null) {
                CameraOperationFNumber.setFNumberSetViaWebApi(CameraOperationFNumber.get());
            } else {
                CameraOperationFNumber.set(fNumSetViaWebApi);
            }
        }
        return params;
    }
}
