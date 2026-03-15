package com.sony.imaging.app.bracketpro.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.BracketMasterMain;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.fw.Definition;

/* loaded from: classes.dex */
public class BMExposureModeCheckState extends ExposureModeCheckState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BMExposureModeController emc = BMExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        if (ModeDialDetector.hasModeDial()) {
            AppLog.info(TAG, "Has Mode Dial.");
            if (!emc.isValidDialPosition(ModeDialDetector.getModeDialPosition()) && !BracketMasterMain.getIsLauncherBoot()) {
                if (emc.getCautionId() != 65535) {
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
                    CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
                    ExecutorCreator.getInstance().stableSequence();
                }
            } else {
                setNextTransition();
            }
        } else {
            if (!emc.isAvailable(expMode)) {
                BMExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
            }
            setNextTransition();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setNextTransition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(getNextState(), this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BMExposureModeController emc = BMExposureModeController.getInstance();
        CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
