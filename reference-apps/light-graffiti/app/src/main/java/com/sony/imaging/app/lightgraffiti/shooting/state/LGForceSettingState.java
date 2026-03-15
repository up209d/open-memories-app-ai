package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGForceSettingState extends ForceSettingState {
    private static final int S_OFF = 0;
    static String TAG = LGForceSettingState.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setSelfTimer(0);
        CameraSetting.getInstance().setParameters(setParams);
        if (!AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_TYPE_NONE_TYPE_P")) {
            ((CameraEx.ParametersModifier) params.second).setAntiHandBlurMode("off");
        }
        return params;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState, com.sony.imaging.app.base.shooting.camera.ForceSetting.FinishCallback
    public void onCallback() {
        setInitialFactor();
        super.onCallback();
    }

    private void setInitialFactor() {
        DROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
    }
}
