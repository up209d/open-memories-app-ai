package com.sony.imaging.app.srctrl.shooting.state;

import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class MfAssistStateEx extends MfAssistState {
    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.SHOOTING_START_FOCUSING_REMOTE) || StateController.getInstance().getAppCondition().equals(StateController.AppCondition.SHOOTING_FOCUSING_REMOTE)) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING_REMOTE);
        } else {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING);
        }
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        super.onResume();
    }
}
