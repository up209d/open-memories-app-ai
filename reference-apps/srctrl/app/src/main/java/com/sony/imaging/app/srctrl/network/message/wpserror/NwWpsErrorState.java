package com.sony.imaging.app.srctrl.network.message.wpserror;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;
import java.util.BitSet;

/* loaded from: classes.dex */
public class NwWpsErrorState extends NetworkRootState {
    public static final String ID_INVALID_LAYOUT = "ERROR_LAYOUT";
    public static final String ID_TIMEOUT_LAYOUT = "TIMEOUT_LAYOUT";
    private static final String TAG = NwWpsErrorState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (StateController.getInstance().isErrorByTimeout()) {
            openLayout(ID_TIMEOUT_LAYOUT);
        } else {
            openLayout(ID_INVALID_LAYOUT);
        }
        setDirectTargetDeviceName(null);
        setDirectTargetDeviceAddress(null);
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.message.wpserror.NwWpsErrorState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwWpsErrorState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        if (StateController.getInstance().isErrorByTimeout()) {
            closeLayout(ID_TIMEOUT_LAYOUT);
        } else {
            closeLayout(ID_INVALID_LAYOUT);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION.equals(action)) {
            handleProvisioningRequest((WifiP2pDeviceInfo) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE));
        }
    }

    private void handleStationConnected(String addr) {
        setNextState(NetworkRootState.ID_CONNECTED, null);
    }

    private void handleProvisioningRequest(WifiP2pDeviceInfo device) {
        setDirectTargetDeviceName(device.getName());
        setDirectTargetDeviceAddress(device.getP2PDevAddress());
        Log.v(TAG, "TargetDeviceName: " + getDirectTargetDeviceName());
        Log.v(TAG, "TargetDeviceAddress: " + getDirectTargetDeviceAddress());
        BitSet confMethod = device.getConfigMethod();
        Log.v(TAG, "Bitset of ConfigMethod: " + confMethod.toString());
        if (confMethod.get(1)) {
            Log.v(TAG, "Provisioning Request: WPS PUSH BUTTON");
            return;
        }
        if (confMethod.get(3)) {
            Log.v(TAG, "Provisioning Request: WPS PIN DISPLAY");
            setNextState(NetworkRootState.ID_WPS_PIN_PRINT, null);
        } else if (confMethod.get(2)) {
            Log.v(TAG, "Provisioning Request: WPS PIN INPUT");
            setNextState(NetworkRootState.ID_WPS_PIN_INPUT, null);
        }
    }
}
