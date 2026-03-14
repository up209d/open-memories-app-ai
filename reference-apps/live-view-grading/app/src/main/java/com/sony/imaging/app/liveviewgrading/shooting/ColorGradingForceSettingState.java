package com.sony.imaging.app.liveviewgrading.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ColorGradingForceSettingState extends ForceSettingState {
    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        return params;
    }
}
