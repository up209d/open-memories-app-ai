package com.sony.imaging.app.srctrl.network.progress.registering;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class NwWpsRegisteringState extends NetworkRootState {
    public static final String ID_REGISTERING_LAYOUT = "REGISTERING_LAYOUT";
    private static final String TAG = NwWpsRegisteringState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(ID_REGISTERING_LAYOUT);
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_SUCCESS_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.progress.registering.NwWpsRegisteringState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwWpsRegisteringState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_REGISTERING_LAYOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_SUCCESS_ACTION.equals(action)) {
            handleWpsRegisteringSuccess();
        } else if (WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION.equals(action)) {
            handleWpsRegisteringFailure(intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, -1));
        }
    }

    private void handleStationConnected(String addr) {
        setNextState(NetworkRootState.ID_CONNECTED, null);
    }

    private void handleWpsRegisteringSuccess() {
        Log.v(TAG, "WPS Registering Success");
    }

    private void handleWpsRegisteringFailure(int error) {
        if (error == 2) {
            Log.v(TAG, "WPS Registering canceled");
        } else if (error == 1) {
            StateController.getInstance().setIsErrorByTimeout(true);
            setNextState(NetworkRootState.ID_WPS_ERROR, null);
        } else {
            StateController.getInstance().setIsErrorByTimeout(false);
            setNextState(NetworkRootState.ID_WPS_ERROR, null);
        }
    }
}
