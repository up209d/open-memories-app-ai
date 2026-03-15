package com.sony.imaging.app.srctrl.network.progress.connected;

import android.view.KeyEvent;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class NwConnectedKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwConnectedKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        moveToRestartingState();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        if (7 < Environment.getVersionPfAPI()) {
            return -1;
        }
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        moveToConfirmState();
        return 1;
    }

    private void moveToConfirmState() {
        NetworkRootState state = (NetworkRootState) this.target;
        state.setNextState("ID_CONFIRM", null);
    }

    private void moveToRestartingState() {
        NetworkRootState state = (NetworkRootState) this.target;
        StateController.getInstance().setIsRestartForNewConfig(false);
        state.setNextState(NetworkRootState.ID_RESTARTING, null);
    }

    public void moveToShootingState() {
        NetworkRootState state = (NetworkRootState) this.target;
        ((SRCtrl) state.getActivity()).changeApp("APP_SHOOTING");
    }
}
