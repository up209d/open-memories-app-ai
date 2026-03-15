package com.sony.imaging.app.srctrl.network.wpspin.print;

import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwWpsPinPrintKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwWpsPinPrintKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        cancelAndMoveToWaitingState();
        return 1;
    }

    private void cancelAndMoveToWaitingState() {
        NetworkRootState state = (NetworkRootState) this.target;
        WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = WifiP2pManagerFactory.getInstance(state.getActivity().getApplicationContext());
        wifiP2pManager.cancel();
        state.setNextState(NetworkRootState.ID_WAITING, null);
    }
}
