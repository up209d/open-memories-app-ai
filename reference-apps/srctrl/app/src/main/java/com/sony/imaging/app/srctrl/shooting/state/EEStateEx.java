package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class EEStateEx extends EEState {
    private static final String CHILD_S1_ON_STATE = "S1OnEE";
    public static final String STATE_NAME = "EE";
    private static final String tag = EEStateEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        KeyStatus s1OnStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 != s1OnStatus.status && !CameraOperationTouchAFPosition.isSet()) {
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        StateController stateController = StateController.getInstance();
        StateController.AppCondition appLastCondition = stateController.getLastAppConditionBeforeCapturing();
        if (StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF == appLastCondition || StateController.AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST == appLastCondition) {
            Log.v(tag, "Next state screen is " + StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF.name());
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF);
            stateController.setLastAppConditionBeforeCapturing(StateController.AppCondition.SHOOTING_REMOTE);
            return S1OnEEStateForTouchAF.STATE_NAME;
        }
        if (StateController.AppCondition.SHOOTING_FOCUSING_REMOTE == stateController.getAppCondition()) {
            return "S1OnEE";
        }
        return super.getNextChildState();
    }
}
