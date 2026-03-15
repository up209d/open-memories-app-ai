package com.sony.imaging.app.startrails.shooting.keyhandler;

import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.startrails.shooting.state.MFModeCheckState;

/* loaded from: classes.dex */
public class STEEStateKeyHandler extends EEStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            State state = (State) this.target;
            state.setNextState(MFModeCheckState.TAG, null);
            return 1;
        }
        return 1;
    }
}
