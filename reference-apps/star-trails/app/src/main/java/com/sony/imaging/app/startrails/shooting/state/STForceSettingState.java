package com.sony.imaging.app.startrails.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class STForceSettingState extends ForceSettingState {
    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (STUtility.getInstance().isPFverOver2()) {
            ((CameraEx.ParametersModifier) params.second).setSoftSkinEffect("off");
        }
        if (!AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_TYPE_NONE_TYPE_P")) {
            ((CameraEx.ParametersModifier) params.second).setAntiHandBlurMode("off");
        }
        return params;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState, com.sony.imaging.app.base.shooting.camera.ForceSetting.FinishCallback
    public void onCallback() {
        if (this.data == null) {
            this.data = new Bundle();
        }
        this.data.putBoolean(STExposureModeCheckState.TAG, true);
        setNextState(LongExposureNRCheckState.TAG, this.data);
    }
}
