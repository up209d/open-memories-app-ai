package com.sony.imaging.app.srctrl.network.message.confirmation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;

/* loaded from: classes.dex */
public class NwConfirmationState extends NetworkRootState {
    public static final String ID_CONFIRM_CHANGE_CONFIG_LAYOUT = "CONFIRM_CHANGE_CONFIG_LAYOUT";
    private static final String TAG = NwConfirmationState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(ID_CONFIRM_CHANGE_CONFIG_LAYOUT);
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.message.confirmation.NwConfirmationState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwConfirmationState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_CONFIRM_CHANGE_CONFIG_LAYOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION.equals(action)) {
            handleStationDisconnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        }
    }

    private void handleStationDisconnected(String addr) {
        setNextState(NetworkRootState.ID_WAITING, null);
    }
}
