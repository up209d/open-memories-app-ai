package com.sony.imaging.app.graduatedfilter.shooting.state;

import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFShootingState extends ShootingState {
    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        super.onPause();
    }
}
