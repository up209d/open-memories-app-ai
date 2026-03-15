package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.menu.controller.OCExposureModeController;

/* loaded from: classes.dex */
public class OCExposureModeCheckState extends ExposureModeCheckState {
    private static final String EE_STATE = "EE";
    private static final String TAG = AppLog.getClassName();
    private final String MODE_DIALED_ENABLED_DEVICE = "this set has modedial";
    private final String WITHOUT_MODE_DIALED_DEVICE = "this set does not have mode dial";

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected String getNextState() {
        return "EE";
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        OCExposureModeController emc = OCExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        if (ModeDialDetector.hasModeDial()) {
            AppLog.trace(TAG, "this set has modedial");
            if (!emc.isValidDialPosition()) {
                if (emc.getCautionId() != 65535) {
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
                    CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
                    return;
                }
                return;
            }
            setNextTransition();
            return;
        }
        AppLog.trace(TAG, "this set does not have mode dial");
        if (!emc.isAvailable(expMode)) {
            OCExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
        }
        setNextTransition();
    }

    private void setNextTransition() {
        setNextState(getNextState(), this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        OCExposureModeController emc = OCExposureModeController.getInstance();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }
}
