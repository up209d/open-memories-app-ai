package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.srctrl.shooting.keyhandler.S1OffEEStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class S1OffEEStateEx extends S1OffEEState {
    public static final String STATE_NAME = "S1OffEE";
    public static final String tag = S1OffEEState.class.getName();

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        S1OffEEStateKeyHandlerEx.s_pushed_S2 = false;
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        if (!StateController.AppCondition.DIAL_INHIBIT.equals(stateController.getAppCondition())) {
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_EE);
        }
        super.onResume();
        if (stateController.isWaitingBackTransition()) {
            Log.v(tag, "Change to network state because of the back transition flag.");
            stateController.changeToNetworkState();
            stateController.setGoBackFlag(false);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
        if (!StateController.AppCondition.DIAL_INHIBIT.equals(appCondition) && !StateController.AppCondition.SHOOTING_REMOTE.equals(appCondition) && !StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(appCondition) && !StateController.AppCondition.SHOOTING_START_FOCUSING.equals(appCondition) && !StateController.AppCondition.SHOOTING_START_FOCUSING_REMOTE.equals(appCondition) && !StateController.AppCondition.SHOOTING_FOCUSING.equals(appCondition) && !StateController.AppCondition.SHOOTING_FOCUSING_REMOTE.equals(appCondition)) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_INHIBIT);
        }
        super.onPause();
    }
}
