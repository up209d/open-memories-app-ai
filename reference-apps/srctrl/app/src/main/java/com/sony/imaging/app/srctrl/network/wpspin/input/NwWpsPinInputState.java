package com.sony.imaging.app.srctrl.network.wpspin.input;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;

/* loaded from: classes.dex */
public class NwWpsPinInputState extends NetworkRootState {
    public static final String ID_PIN_INPUT_LAYOUT = "PIN_INPUT_LAYOUT";
    private static final String TAG = NwWpsPinInputState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(ID_PIN_INPUT_LAYOUT);
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        setWpsPin(this.wifiP2pManager.createWpsPin(true));
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.wpspin.input.NwWpsPinInputState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwWpsPinInputState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_PIN_INPUT_LAYOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION.equals(action)) {
            handleWpsRegFailure(intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 0));
        }
    }

    private void handleStationConnected(String addr) {
        setNextState(NetworkRootState.ID_CONNECTED, null);
    }

    private void handleWpsRegFailure(int error) {
        this.wifiP2pManager.cancel();
        setNextState(NetworkRootState.ID_WAITING, null);
    }
}
