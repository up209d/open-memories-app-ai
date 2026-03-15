package com.sony.imaging.app.srctrl.network.message.wpserror;

import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwWpsErrorKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwWpsErrorKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        moveToWaitingState();
        return 1;
    }

    private void moveToWaitingState() {
        NetworkRootState state = (NetworkRootState) this.target;
        state.setNextState(NetworkRootState.ID_WAITING, null);
    }
}
