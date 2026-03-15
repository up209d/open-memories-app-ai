package com.sony.imaging.app.srctrl.shooting.state;

import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class FocusAdjustmentStateEx extends FocusAdjustmentState {
    @Override // com.sony.imaging.app.base.shooting.FocusAdjustmentState, com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING);
    }
}
