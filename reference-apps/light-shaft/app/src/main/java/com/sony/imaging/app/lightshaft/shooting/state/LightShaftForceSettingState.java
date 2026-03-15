package com.sony.imaging.app.lightshaft.shooting.state;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LightShaftForceSettingState extends ForceSettingState {
    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        return params;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState, com.sony.imaging.app.base.shooting.camera.ForceSetting.FinishCallback
    public void onCallback() {
        LightShaft.setIsEEStateBoot(true);
        super.onCallback();
    }
}
