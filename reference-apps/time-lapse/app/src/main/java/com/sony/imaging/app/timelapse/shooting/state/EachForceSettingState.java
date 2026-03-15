package com.sony.imaging.app.timelapse.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class EachForceSettingState extends ForceSettingState {
    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
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

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(TimeLapseConstants.PREVIOUS_STATE, TimeLapseConstants.EACH_FORCE_SETTING_STATE);
        super.setNextState(name, bundle);
    }

    private void setInitialFactor() {
        TLShootModeSettingController.getInstance().setRecordingFactorIds(BackUpUtil.getInstance().getPreferenceInt(TimeLapseConstants.TIMELAPSE_CURRENT_CAPTURE_STATE_KEY, 0));
        TLCommonUtil.getThemeUtil().setThemeUtil(BackUpUtil.getInstance().getPreferenceInt(TimeLapseConstants.TIMELAPSE_CURRENT_STATE, 6));
        TLCommonUtil.getThemeUtil().getThemeName(getActivity().getApplicationContext());
    }
}
