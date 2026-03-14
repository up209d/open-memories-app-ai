package com.sony.imaging.app.digitalfilter.shooting.state;

import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFShootingState extends ShootingState {
    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (GFCommonUtil.getInstance().getBootFactor() == 1) {
            GFCommonUtil.getInstance().setBootFactor(2);
        }
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        super.onPause();
    }
}
