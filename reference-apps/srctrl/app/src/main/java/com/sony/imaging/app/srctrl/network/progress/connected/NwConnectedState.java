package com.sony.imaging.app.srctrl.network.progress.connected;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import java.util.List;

/* loaded from: classes.dex */
public class NwConnectedState extends NetworkRootState {
    public static final String ID_CONNECTED_LAYOUT = "CONNECTED_LAYOUT";
    private static final String TAG = NwConnectedState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        List<WifiP2pDeviceInfo> stationList = this.wifiP2pManager.getStationList();
        this.bReceiver = null;
        if (stationList.size() == 0) {
            setNextState(NetworkRootState.ID_WAITING, null);
            return;
        }
        WifiP2pDeviceInfo dDevice = stationList.get(0);
        setDirectTargetDeviceName(dDevice.getName());
        Log.v(TAG, dDevice.toString());
        openLayout(ID_CONNECTED_LAYOUT);
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.progress.connected.NwConnectedState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwConnectedState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.bReceiver != null) {
            this.bReceiver.unregisterThis(getActivity());
            closeLayout(ID_CONNECTED_LAYOUT);
        }
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
