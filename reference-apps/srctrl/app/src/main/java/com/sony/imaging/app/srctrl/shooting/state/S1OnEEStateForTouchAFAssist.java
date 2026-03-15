package com.sony.imaging.app.srctrl.shooting.state;

import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.util.OperationReceiver;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;

/* loaded from: classes.dex */
public class S1OnEEStateForTouchAFAssist extends MfAssistState {
    public static final String STATE_NAME = "S1OnEEStateForTouchAFAssist";
    private static final String SWITCH_MODE_TO_TOUCHAF_EE = "com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAFAssist_TO_S1OnEEStateForTouchAF";
    private static final String tag = S1OnEEStateForTouchAF.class.getName();

    @Override // com.sony.imaging.app.base.shooting.MfAssistState
    protected String getTranslationMode() {
        return SWITCH_MODE_TO_TOUCHAF_EE;
    }

    protected void setAppCondition() {
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST);
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setAppCondition();
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (2 == RunStatus.getStatus()) {
            Log.v(tag, "Cancel AF and reset AF mode/area.");
            BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
            shootingExecutor.cancelAutoFocus();
            CameraOperationTouchAFPosition.resetFocusSettings();
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (SWITCH_MODE_TO_TOUCHAF_EE.equals(msg.obj)) {
            return false;
        }
        return super.handleMessage(msg);
    }

    @Override // com.sony.imaging.app.fw.State
    public void removeState() {
        OperationReceiver.changeToS1OnStateForTouchAF();
    }
}
