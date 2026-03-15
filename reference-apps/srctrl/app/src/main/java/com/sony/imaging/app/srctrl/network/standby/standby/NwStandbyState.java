package com.sony.imaging.app.srctrl.network.standby.standby;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pConfiguration;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;

/* loaded from: classes.dex */
public class NwStandbyState extends NetworkRootState {
    public static final String ID_STANDBY_LAYOUT = "STANDBY_LAYOUT";
    private static final String TAG = NwStandbyState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(ID_STANDBY_LAYOUT);
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.standby.standby.NwStandbyState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwStandbyState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_STANDBY_LAYOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION.equals(action)) {
            handleGroupCreateSuccess((WifiP2pConfiguration) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG));
        }
    }

    private void handleGroupCreateSuccess(WifiP2pConfiguration config) {
        this.wifiP2pManager.saveConfiguration();
        setMySsid(config.getSsid());
        setMyPsk(config.getPreSharedKey());
        setNextState(NetworkRootState.ID_WAITING, null);
    }
}
