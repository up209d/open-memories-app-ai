package com.sony.imaging.app.base.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ForceSetting;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ForceSettingState extends StateBase implements ForceSetting.FinishCallback {
    private static final String NEXT_STATE = "ExposureModeCheck";

    protected boolean isForceSetting() {
        return false;
    }

    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        return params;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (isForceSetting()) {
            ForceSetting.setFinishCallback(this);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ForceSetting.exec(onSetForceSetting(params));
            return;
        }
        onCallback();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        ForceSetting.cancel();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ForceSetting.FinishCallback
    public void onCallback() {
        setNextState("ExposureModeCheck", this.data);
    }
}
