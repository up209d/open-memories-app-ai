package com.sony.imaging.app.srctrl.network.wpspin.print;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class NwWpsPinPrintState extends NetworkRootState {
    public static final String ID_PIN_PRINT_LAYOUT = "PIN_PRINT_LAYOUT";
    private static final String TAG = NwWpsPinPrintState.class.getSimpleName();
    private SafeBroadcastReceiver bReceiver;
    private Handler handler;
    private IntentFilter iFilter;
    private Runnable timeoutRunnable;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        setWpsPin(this.wifiP2pManager.createWpsPin(true));
        openLayout(ID_PIN_PRINT_LAYOUT);
        this.wifiP2pManager.startWpsPin(getWpsPin());
        this.iFilter = new IntentFilter();
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.wpspin.print.NwWpsPinPrintState.1
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NwWpsPinPrintState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
        this.handler = new Handler();
        this.timeoutRunnable = new Runnable() { // from class: com.sony.imaging.app.srctrl.network.wpspin.print.NwWpsPinPrintState.2
            @Override // java.lang.Runnable
            public void run() {
                Log.e(NwWpsPinPrintState.TAG, "Timeout: WPS PIN DISPLAY TIMEOUT");
                StateController.getInstance().setIsErrorByTimeout(true);
                NwWpsPinPrintState.this.setNextState(NetworkRootState.ID_WPS_ERROR, null);
            }
        };
        this.handler.postDelayed(this.timeoutRunnable, 120000L);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.handler.removeCallbacks(this.timeoutRunnable);
        this.bReceiver.unregisterThis(getActivity());
        closeLayout(ID_PIN_PRINT_LAYOUT);
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
        setNextState(NetworkRootState.ID_WAITING, null);
    }
}
