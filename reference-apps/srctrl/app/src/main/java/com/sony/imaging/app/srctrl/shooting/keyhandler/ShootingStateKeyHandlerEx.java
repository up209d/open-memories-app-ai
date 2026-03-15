package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class ShootingStateKeyHandlerEx extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        int ret = super.pushedS1Key();
        if (ret == 1 && StateController.getInstance().getAppCondition() == StateController.AppCondition.SHOOTING_FOCUSING_REMOTE) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING);
        }
        return ret;
    }
}
