package com.sony.imaging.app.lightshaft.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.imaging.app.lightshaft.shooting.camera.LightShaftExposureModeController;

/* loaded from: classes.dex */
public class LightShaftExposureModeCheckState extends ExposureModeCheckState {
    private static final String TAG = "LightShaftExposureModeCheckState";

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        LightShaftExposureModeController emc = LightShaftExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        AppLog.trace(TAG, "Exposure mode = " + expMode);
        if (ModeDialDetector.hasModeDial()) {
            int modeDialPosition = ModeDialDetector.getModeDialPosition();
            AppLog.trace(TAG, "Exposure mode = " + expMode + "  ModeDialPosition = " + modeDialPosition);
            if (!LightShaftExposureModeController.getInstance().isValidDialPosition(modeDialPosition)) {
                if (emc.getCautionId() != 65535) {
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
                    CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
                    return;
                }
                return;
            }
            transitNextState();
            return;
        }
        if (!emc.isValidExpoMode(expMode)) {
            LightShaftExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        }
        transitNextState();
    }

    private void transitNextState() {
        LightShaft.setIsEEStateBoot(true);
        setNextState(getNextState(), this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ExposureModeController emc = LightShaftExposureModeController.getInstance();
        CautionUtilityClass.getInstance().executeTerminate();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }
}
