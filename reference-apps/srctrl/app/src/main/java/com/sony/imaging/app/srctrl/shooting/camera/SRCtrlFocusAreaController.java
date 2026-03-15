package com.sony.imaging.app.srctrl.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class SRCtrlFocusAreaController extends FocusAreaController {
    @Override // com.sony.imaging.app.base.shooting.camera.FocusAreaController
    protected boolean isNeedToCheckAdditionalFactor() {
        if (StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF == StateController.getInstance().getAppCondition() || StateController.AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST == StateController.getInstance().getAppCondition()) {
            return false;
        }
        return true;
    }
}
