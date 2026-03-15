package com.sony.imaging.app.timelapse.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.menu.controller.TimelapseExposureModeController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;

/* loaded from: classes.dex */
public class TLExposureModeCheckState extends ExposureModeCheckState {
    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        TimelapseExposureModeController emc = TimelapseExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            if (ModeDialDetector.hasModeDial() && !TimelapseExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
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
        if (!emc.isAvailable(expMode)) {
            TimelapseExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
        }
        transitNextState();
    }

    private void transitNextState() {
        setNextState(getNextState(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        if (this.data != null) {
            String previousState = this.data.getString(TimeLapseConstants.PREVIOUS_STATE);
            if (previousState != null && previousState.equals(TimeLapseConstants.EACH_FORCE_SETTING_STATE)) {
                ExecutorCreator.getInstance().updateSequence();
                return;
            }
            return;
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
            ExecutorCreator.getInstance().updateSequence();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ExposureModeController emc = TimelapseExposureModeController.getInstance();
        CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }
}
