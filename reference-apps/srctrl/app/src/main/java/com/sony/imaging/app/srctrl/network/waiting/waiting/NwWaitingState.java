package com.sony.imaging.app.srctrl.network.waiting.waiting;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pConfiguration;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.NfcCtrlManager;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class NwWaitingState extends NetworkRootState {
    public static final String ID_WAITING_LAYOUT = "WAITING_LAYOUT";
    public static final String ID_WAITING_LAYOUT_QR = "WAITING_LAYOUT_QR";
    private SafeBroadcastReceiver bReceiver;
    private IntentFilter iFilter;
    OnDisplayEventListener mDispEventListener;
    DisplayManager mDisplayManager = null;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;
    private static final String TAG = NwWaitingState.class.getSimpleName();
    protected static final LauoutType NONE = null;
    private static LauoutType currentlayout = LauoutType.NONE;
    private static Map<LauoutType, String> layoutMap = new HashMap<LauoutType, String>() { // from class: com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingState.2
        private static final long serialVersionUID = 1;

        {
            put(LauoutType.NONE, "");
            put(LauoutType.PASS, NwWaitingState.ID_WAITING_LAYOUT);
            put(LauoutType.QR, NwWaitingState.ID_WAITING_LAYOUT_QR);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum LauoutType {
        NONE,
        PASS,
        QR
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        StateController.getInstance().setInitialWifiSetup(false);
        setDirectTargetDeviceName(null);
        setDirectTargetDeviceAddress(null);
        openLayout(LauoutType.QR);
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        this.bReceiver = null;
        if (this.wifiP2pManager.getStationList().size() != 0) {
            setNextState(NetworkRootState.ID_CONNECTED, null);
        } else {
            this.iFilter = new IntentFilter();
            this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
            this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION);
            this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
            this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingState.1
                @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
                public void checkedOnReceive(Context context, Intent intent) {
                    NwWaitingState.this.handleEvent(intent);
                }
            };
            this.bReceiver.registerThis(getActivity(), this.iFilter);
        }
        NfcCtrlManager.getInstance().startNfcTouchStandby();
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.finish();
        this.mDispEventListener = null;
        this.mDisplayManager = null;
        NfcCtrlManager.getInstance().stopNfcTouchStandby();
        if (this.bReceiver != null) {
            this.bReceiver.unregisterThis(getActivity());
        }
        this.wifiP2pManager = null;
        closeLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION.equals(action)) {
            handleProvisioningRequest((WifiP2pDeviceInfo) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION.equals(action)) {
            handleGroupCreateSuccess((WifiP2pConfiguration) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG));
        }
    }

    private void handleGroupCreateSuccess(WifiP2pConfiguration config) {
        if (this.wifiP2pManager.getStationList().size() != 0) {
            setNextState(NetworkRootState.ID_CONNECTED, null);
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
            setNextState(NetworkRootState.ID_WPS_PBC, null);
        } else if (confMethod.get(3)) {
            Log.v(TAG, "Provisioning Request: WPS PIN DISPLAY");
            setNextState(NetworkRootState.ID_WPS_PIN_PRINT, null);
        } else if (confMethod.get(2)) {
            Log.v(TAG, "Provisioning Request: WPS PIN INPUT");
            setNextState(NetworkRootState.ID_WPS_PIN_INPUT, null);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        switchLayout();
        return 1;
    }

    private void switchLayout() {
        LauoutType newlayout = NONE;
        switch (currentlayout) {
            case NONE:
                newlayout = LauoutType.QR;
                break;
            case PASS:
                newlayout = LauoutType.QR;
                break;
            case QR:
                newlayout = LauoutType.PASS;
                break;
        }
        openLayout(newlayout);
    }

    private void openLayout(LauoutType newLayout) {
        if (!LauoutType.NONE.equals(newLayout) && !currentlayout.equals(newLayout)) {
            closeLayout();
            openLayout(layoutMap.get(newLayout));
            currentlayout = newLayout;
        }
    }

    private void closeLayout() {
        if (!LauoutType.NONE.equals(currentlayout)) {
            closeLayout(layoutMap.get(currentlayout));
            currentlayout = LauoutType.NONE;
        }
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            Log.i(NwWaitingState.TAG, "onDeviceStatusChanged");
            NfcCtrlManager.getInstance().stopNfcTouchStandby();
            NfcCtrlManager.getInstance().startNfcTouchStandby();
        }
    }
}
