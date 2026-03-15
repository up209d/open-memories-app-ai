package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class S1OnEEStateForTouchAF extends S1OnEEStateEx {
    public static final String STATE_NAME = "S1OnEEStateForTouchAF";
    private static final String tag = S1OnEEStateForTouchAF.class.getName();

    @Override // com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx
    protected void setAppCondition() {
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState
    protected String getMfAssistStateName() {
        return S1OnEEStateForTouchAFAssist.STATE_NAME;
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx, com.sony.imaging.app.base.shooting.S1OnEEState
    protected NotificationListener getFocusInfoListener() {
        return null;
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx, com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx, com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (2 == RunStatus.getStatus()) {
            Log.v(tag, "Cancel AF and reset AF mode/area.");
            BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
            shootingExecutor.cancelAutoFocus();
            CameraOperationTouchAFPosition.leaveTouchAFMode(false);
        }
    }
}
