package com.sony.imaging.app.srctrl.network.progress.restarting;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pConfiguration;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;
import java.util.List;

/* loaded from: classes.dex */
public class RestartingState extends NetworkRootState {
    public static final String ID_CHANGING_CONFIG_LAYOUT = "CHANGING_CONFIG_LAYOUT";
    private static final String TAG = RestartingState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(ID_CHANGING_CONFIG_LAYOUT);
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        this.wifiP2pManager.removeGroup();
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_TERMINATE_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.progress.restarting.RestartingState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                RestartingState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_CHANGING_CONFIG_LAYOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION.equals(action)) {
            handleGroupCreateSuccess((WifiP2pConfiguration) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_TERMINATE_ACTION.equals(action)) {
            handleGroupTerminate();
        }
    }

    private void handleGroupCreateSuccess(WifiP2pConfiguration config) {
        this.wifiP2pManager.saveConfiguration();
        setMySsid(config.getSsid());
        setMyPsk(config.getPreSharedKey());
        setNextState(NetworkRootState.ID_WAITING, null);
    }

    private void handleGroupTerminate() {
        List<WifiP2pConfiguration> confList = this.wifiP2pManager.getConfigurations();
        if (StateController.getInstance().isRestartForNewConfig() || confList.size() == 0) {
            this.wifiP2pManager.startGo(-2);
        } else {
            this.wifiP2pManager.startGo(confList.get(confList.size() - 1).getNetworkId());
            setNextState(NetworkRootState.ID_WAITING, null);
        }
    }
}
