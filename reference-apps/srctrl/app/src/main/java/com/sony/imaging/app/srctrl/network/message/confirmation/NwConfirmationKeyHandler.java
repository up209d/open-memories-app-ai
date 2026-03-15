package com.sony.imaging.app.srctrl.network.message.confirmation;

import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwConfirmationKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwConfirmationKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        moveToConnectedState();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        moveToChangingPwState();
        return 1;
    }

    private void moveToConnectedState() {
        NetworkRootState state = (NetworkRootState) this.target;
        state.setNextState(NetworkRootState.ID_CONNECTED, null);
    }

    private void moveToChangingPwState() {
        NetworkRootState state = (NetworkRootState) this.target;
        StateController.getInstance().setIsRestartForNewConfig(true);
        state.setNextState(NetworkRootState.ID_RESTARTING, null);
    }
}
