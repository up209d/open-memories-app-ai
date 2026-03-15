package com.sony.imaging.app.synctosmartphone.network.wifiwrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory;
import com.sony.wifi.p2p.WifiP2pExtManager;
import com.sony.wifi.wps.WpsManager;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class WifiP2pManagerJb implements WifiP2pManagerFactory.IWifiP2pManager {
    private static final String TAG = WifiP2pManagerJb.class.getSimpleName();
    private BroadcastReceiver _bReceiver;
    private WifiP2pManager.Channel _channel;
    private String _connectedDeviceAddress = "";
    private WifiP2pExtManager _wifiP2pManager;
    private WpsManager _wpsManager;

    /* loaded from: classes.dex */
    private interface SyncListener {
        boolean await();
    }

    /* loaded from: classes.dex */
    private class SyncActionListener implements WifiP2pManager.ActionListener, SyncListener {
        private int _failureReason;
        private CountDownLatch _latch;
        private boolean _ret;

        private SyncActionListener() {
            this._latch = new CountDownLatch(1);
        }

        public boolean getResult() {
            return this._ret;
        }

        public int getFailureReason() {
            return this._failureReason;
        }

        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
        public void onFailure(int reason) {
            Log.v(WifiP2pManagerJb.TAG, "SyncActionListener\u3000onFailure: reason=" + reason);
            this._ret = false;
            this._failureReason = reason;
            this._latch.countDown();
        }

        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
        public void onSuccess() {
            Log.v(WifiP2pManagerJb.TAG, "SyncActionListener\u3000onSuccess");
            this._ret = true;
            this._latch.countDown();
        }

        @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.SyncListener
        public boolean await() {
            try {
                Log.v(WifiP2pManagerJb.TAG, "SyncActionListener await start");
                this._latch.await();
                Log.v(WifiP2pManagerJb.TAG, "SyncActionListener await end");
                return true;
            } catch (InterruptedException e) {
                Log.e(WifiP2pManagerJb.TAG, "SyncActionListener await error");
                e.printStackTrace();
                return false;
            }
        }
    }

    /* loaded from: classes.dex */
    private class SyncDeviceListener implements WifiP2pExtManager.WifiP2pDeviceListener, SyncListener {
        private WifiP2pDevice _device;
        private CountDownLatch _latch;

        private SyncDeviceListener() {
            this._latch = new CountDownLatch(1);
            this._device = new WifiP2pDevice();
        }

        public WifiP2pDevice getDevice() {
            return this._device;
        }

        public void onDeviceInfoAvailable(WifiP2pDevice device) {
            Log.v(WifiP2pManagerJb.TAG, "SyncDeviceListener onDeviceInfoAvailable: device=" + device);
            this._device = device;
            this._latch.countDown();
        }

        @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.SyncListener
        public boolean await() {
            try {
                Log.v(WifiP2pManagerJb.TAG, "SyncDeviceListener await start");
                this._latch.await();
                Log.v(WifiP2pManagerJb.TAG, "SyncDeviceListener await end");
                return true;
            } catch (InterruptedException e) {
                Log.e(WifiP2pManagerJb.TAG, "SyncDeviceListener await error");
                e.printStackTrace();
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SyncGroupInfoListener implements WifiP2pManager.GroupInfoListener, SyncListener {
        private WifiP2pGroup _groupInfo;
        private CountDownLatch _latch;

        private SyncGroupInfoListener() {
            this._latch = new CountDownLatch(1);
            this._groupInfo = new WifiP2pGroup();
        }

        public WifiP2pGroup getGroup() {
            return this._groupInfo;
        }

        @Override // android.net.wifi.p2p.WifiP2pManager.GroupInfoListener
        public void onGroupInfoAvailable(WifiP2pGroup groupInfo) {
            Log.v(WifiP2pManagerJb.TAG, "SyncGroupInfoListener onGroupInfoAvailable: groupInfo=" + groupInfo);
            this._groupInfo = groupInfo;
            this._latch.countDown();
        }

        @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.SyncListener
        public boolean await() {
            try {
                Log.v(WifiP2pManagerJb.TAG, "SyncGroupInfoListener await start");
                this._latch.await();
                Log.v(WifiP2pManagerJb.TAG, "SyncGroupInfoListener await end");
                return true;
            } catch (InterruptedException e) {
                Log.e(WifiP2pManagerJb.TAG, "SyncGroupInfoListener await error");
                e.printStackTrace();
                return false;
            }
        }
    }

    /* loaded from: classes.dex */
    private class SyncWifiP2pEnabledListener implements WifiP2pExtManager.WifiP2pEnabledListener, SyncListener {
        private boolean _enabled;
        private CountDownLatch _latch;

        private SyncWifiP2pEnabledListener() {
            this._latch = new CountDownLatch(1);
        }

        public boolean getEnableStatus() {
            return this._enabled;
        }

        public void onEnableStatusAvailable(boolean enabled) {
            Log.v(WifiP2pManagerJb.TAG, "SyncWifiP2pEnabledListener onEnableStatusAvailable");
            this._enabled = enabled;
            this._latch.countDown();
        }

        @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.SyncListener
        public boolean await() {
            try {
                Log.v(WifiP2pManagerJb.TAG, "SyncWifiP2pEnabledListener await start");
                this._latch.await();
                Log.v(WifiP2pManagerJb.TAG, "SyncWifiP2pEnabledListener await end");
                return true;
            } catch (InterruptedException e) {
                Log.e(WifiP2pManagerJb.TAG, "SyncWifiP2pEnabledListener await error");
                e.printStackTrace();
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SyncPeerListListener implements WifiP2pManager.PeerListListener, SyncListener {
        private CountDownLatch _latch;
        private WifiP2pDeviceList _peersList;

        private SyncPeerListListener() {
            this._latch = new CountDownLatch(1);
            this._peersList = new WifiP2pDeviceList();
        }

        public WifiP2pDeviceList getPeersList() {
            return this._peersList;
        }

        @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
        public void onPeersAvailable(WifiP2pDeviceList arg0) {
            Log.v(WifiP2pManagerJb.TAG, "SyncPeerListListener onPeersAvailable");
            this._peersList = arg0;
            this._latch.countDown();
        }

        @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.SyncListener
        public boolean await() {
            try {
                Log.v(WifiP2pManagerJb.TAG, "SyncPeerListListener await start");
                this._latch.await();
                Log.v(WifiP2pManagerJb.TAG, "SyncPeerListListener await end");
                return true;
            } catch (InterruptedException e) {
                Log.e(WifiP2pManagerJb.TAG, "SyncPeerListListener await error");
                e.printStackTrace();
                return false;
            }
        }
    }

    public WifiP2pManagerJb(Context context) {
        this._wifiP2pManager = (WifiP2pExtManager) context.getSystemService("wifip2p");
        this._wpsManager = (WpsManager) context.getSystemService("wifi-wps");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.sony.wifi.p2p.PROVISIONING_REQUEST_ACTION");
        iFilter.addAction("com.sony.wifi.p2p.WPS_REG_FAILURE_ACTION");
        iFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        iFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        iFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this._bReceiver = new BroadcastReceiver() { // from class: com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerJb.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiP2pManagerJb.this.handleEvent(context2, intent);
            }
        };
        context.getApplicationContext().registerReceiver(this._bReceiver, iFilter);
        P2pLooperThread looperThread = new P2pLooperThread(context);
        new Thread(looperThread).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Context context, Intent intent) {
        WifiP2pDeviceList deviceList;
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if ("com.sony.wifi.p2p.PROVISIONING_REQUEST_ACTION".equals(action)) {
            WifiP2pConfig p2pConfig = (WifiP2pConfig) intent.getParcelableExtra("wifiP2pConfig");
            WifiP2pDeviceInfo retDevice = new WifiP2pDeviceInfo();
            retDevice.setP2PDevAddress(p2pConfig.deviceAddress == null ? "-" : p2pConfig.deviceAddress);
            SyncPeerListListener peerListListener = new SyncPeerListListener();
            this._wifiP2pManager.requestPeers(this._channel, peerListListener);
            if (peerListListener.await() && (deviceList = peerListListener.getPeersList()) != null && deviceList.getDeviceList() != null) {
                Iterator i$ = deviceList.getDeviceList().iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    WifiP2pDevice device = i$.next();
                    Log.v(TAG, "Peers: deviceAddress:" + device.deviceAddress + " deviceName:" + device.deviceName);
                    if (retDevice.getP2PDevAddress().equals(device.deviceAddress)) {
                        retDevice.setName(device.deviceName);
                        break;
                    }
                }
            }
            BitSet configMethod = new BitSet();
            switch (p2pConfig.wps.setup) {
                case 0:
                    configMethod.set(1);
                    break;
                case 1:
                    configMethod.set(3);
                    break;
                case 2:
                    configMethod.set(2);
                    break;
                default:
                    Log.e(TAG, "PROVISIONING_REQUEST_ACTION : Unknown wps setup=" + p2pConfig.wps.setup);
                    return;
            }
            retDevice.setConfigMethod(configMethod);
            Log.v(TAG, "PROVISIONING_REQUEST_ACTION getName: " + retDevice.getName());
            Log.v(TAG, "PROVISIONING_REQUEST_ACTION getP2PDevAddress: " + retDevice.getP2PDevAddress());
            Log.v(TAG, "PROVISIONING_REQUEST_ACTION getConfigMethod: " + retDevice.getConfigMethod());
            Log.v(TAG, "PROVISIONING_REQUEST_ACTION isOperatingModeGO: " + retDevice.isOperatingModeGO());
            Intent broadCast = new Intent(WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION);
            broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE, retDevice);
            context.sendBroadcast(broadCast);
            return;
        }
        if ("com.sony.wifi.p2p.WPS_REG_FAILURE_ACTION".equals(action)) {
            Intent broadCast2 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION);
            Integer error = Integer.valueOf(intent.getIntExtra("wps_error_code", 3));
            switch (error.intValue()) {
                case 0:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION, ERROR_CODE = user canceled.");
                    broadCast2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 2);
                    break;
                case 1:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION, ERROR_CODE = timeout.");
                    broadCast2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 1);
                    break;
                case 2:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION, ERROR_CODE = unknown error.");
                    broadCast2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 3);
                    break;
                default:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION, ERROR_CODE = unexpected error.");
                    broadCast2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 3);
                    break;
            }
            context.sendBroadcast(broadCast2);
            return;
        }
        if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
            WifiP2pInfo p2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (p2pInfo.groupFormed && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                Intent broadCast3 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
                WifiP2pConfiguration retConfig = new WifiP2pConfiguration();
                List<WifiP2pConfiguration> getConfig = getConfigurations();
                if (getConfig != null && getConfig.size() > 0) {
                    retConfig.setNetworkId(getConfig.get(0).getNetworkId());
                    retConfig.setSsid(getConfig.get(0).getSsid());
                    retConfig.setPreSharedKey(getConfig.get(0).getPreSharedKey());
                    Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getNetworkId: " + retConfig.getNetworkId());
                    Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getSsid: " + retConfig.getSsid());
                    Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getPreSharedKey: " + retConfig.getPreSharedKey());
                }
                broadCast3.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG, retConfig);
                context.sendBroadcast(broadCast3);
                return;
            }
            if (!p2pInfo.groupFormed && networkInfo.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
                context.sendBroadcast(new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION));
                return;
            } else {
                if (!p2pInfo.groupFormed && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                    context.sendBroadcast(new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_TERMINATE_ACTION));
                    return;
                }
                return;
            }
        }
        if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
            checkStationConnected(context);
            return;
        }
        if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
            Intent broadCast4 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.DIRECT_STATE_CHANGED_ACTION);
            int state = intent.getIntExtra("wifi_p2p_state", 1);
            switch (state) {
                case 1:
                    Log.v(TAG, "WIFI_P2P_STATE_CHANGED_ACTION : WIFI_P2P_STATE_DISABLED");
                    checkStationConnected(context);
                    broadCast4.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 1);
                    break;
                case 2:
                    Log.v(TAG, "WIFI_P2P_STATE_CHANGED_ACTION : WIFI_P2P_STATE_ENABLED");
                    broadCast4.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 3);
                    break;
                default:
                    Log.v(TAG, "WIFI_P2P_STATE_CHANGED_ACTION : Unknown State=" + state);
                    broadCast4.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 5);
                    break;
            }
            context.sendBroadcast(broadCast4);
        }
    }

    private void checkStationConnected(Context context) {
        Log.d(TAG, "checkStationConnected called");
        SyncGroupInfoListener groupInfoListener = new SyncGroupInfoListener();
        this._wifiP2pManager.requestGroupInfo(this._channel, groupInfoListener);
        if (groupInfoListener.await()) {
            String nextConnectedDeviceAddress = "";
            if (groupInfoListener.getGroup() != null) {
                for (WifiP2pDevice device : groupInfoListener.getGroup().getClientList()) {
                    if (device != null && device.deviceAddress != null) {
                        nextConnectedDeviceAddress = device.deviceAddress;
                    }
                }
            } else {
                Log.e(TAG, "checkStationConnected: getGroup is null");
            }
            if (this._connectedDeviceAddress.isEmpty() && nextConnectedDeviceAddress.isEmpty()) {
                Log.d(TAG, "checkStationConnected: curConnectedDeviceAddress.isEmpty() && nextConnectedDeviceAddress.isEmpty()");
                Log.d(TAG, "curConnectedDeviceAddress : " + this._connectedDeviceAddress);
                Log.d(TAG, "nextConnectedDeviceAddress : " + nextConnectedDeviceAddress);
            } else if (this._connectedDeviceAddress.isEmpty() && !nextConnectedDeviceAddress.isEmpty()) {
                Log.d(TAG, "checkStationConnected: STA_CONNECTED_ACTION");
                Log.d(TAG, "curConnectedDeviceAddress : " + this._connectedDeviceAddress);
                Log.d(TAG, "nextConnectedDeviceAddress : " + nextConnectedDeviceAddress);
                Intent connectIntent = new Intent(STA_CONNECTED_ACTION);
                connectIntent.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, nextConnectedDeviceAddress);
                context.sendBroadcast(connectIntent);
            } else if (!this._connectedDeviceAddress.isEmpty() && nextConnectedDeviceAddress.isEmpty()) {
                Log.d(TAG, "checkStationConnected: STA_DISCONNECTED_ACTION");
                Log.d(TAG, "curConnectedDeviceAddress : " + this._connectedDeviceAddress);
                Log.d(TAG, "nextConnectedDeviceAddress : " + nextConnectedDeviceAddress);
                Intent disconnectIntent = new Intent(STA_DISCONNECTED_ACTION);
                disconnectIntent.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, this._connectedDeviceAddress);
                context.sendBroadcast(disconnectIntent);
            } else if (!this._connectedDeviceAddress.isEmpty() && !nextConnectedDeviceAddress.isEmpty()) {
                Log.d(TAG, "checkStationConnected: !curConnectedDeviceAddress.isEmpty() && !nextConnectedDeviceAddress.isEmpty()");
                if (!this._connectedDeviceAddress.equals(nextConnectedDeviceAddress)) {
                    Log.d(TAG, "checkStationConnected: STA_DISCONNECTED_ACTION and STA_CONNECTED_ACTION");
                    Log.d(TAG, "curConnectedDeviceAddress : " + this._connectedDeviceAddress);
                    Log.d(TAG, "nextConnectedDeviceAddress : " + nextConnectedDeviceAddress);
                    Intent disconnectIntent2 = new Intent(STA_DISCONNECTED_ACTION);
                    disconnectIntent2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, this._connectedDeviceAddress);
                    context.sendBroadcast(disconnectIntent2);
                    Intent connectIntent2 = new Intent(STA_CONNECTED_ACTION);
                    connectIntent2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, nextConnectedDeviceAddress);
                    context.sendBroadcast(connectIntent2);
                } else {
                    Log.d(TAG, "checkStationConnected: Nothing to Do");
                    Log.d(TAG, "curConnectedDeviceAddress : " + this._connectedDeviceAddress);
                    Log.d(TAG, "nextConnectedDeviceAddress : " + nextConnectedDeviceAddress);
                }
            }
            this._connectedDeviceAddress = nextConnectedDeviceAddress;
            return;
        }
        Log.e(TAG, "checkStationConnected: requestGroupInfo await Error");
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public synchronized boolean setDirectEnabled(boolean enabled) {
        boolean z = false;
        synchronized (this) {
            Log.v(TAG, "setDirectEnabled: enabled=" + enabled);
            SyncActionListener actionListener = new SyncActionListener();
            this._wifiP2pManager.setDirectEnabled(this._channel, enabled, actionListener);
            if (actionListener.await()) {
                if (actionListener.getResult()) {
                    z = true;
                } else {
                    switch (actionListener.getFailureReason()) {
                        case 0:
                            Log.e(TAG, "Indicates that the operation failed due to an internal error.");
                            break;
                        case 1:
                            Log.e(TAG, "Indicates that the operation failed because p2p is unsupported on the device.");
                            break;
                        case 2:
                            Log.e(TAG, "Indicates that the operation failed because the framework is busy and unable to service the request.");
                            break;
                        default:
                            Log.e(TAG, "invalid error.");
                            break;
                    }
                }
            }
        }
        return z;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public synchronized boolean saveConfiguration() {
        SyncActionListener actionListener;
        Log.v(TAG, "saveConfiguration");
        actionListener = new SyncActionListener();
        this._wifiP2pManager.saveConfiguration(this._channel, actionListener);
        return actionListener.await() ? actionListener.getResult() : false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setSsidPostfix(String postfix) {
        Log.v(TAG, "setSsidPostfix: postfix=" + postfix);
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.setSsidPostfix(this._channel, postfix, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setModelName(String name) {
        Log.v(TAG, "setModelName: name=" + name);
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.setModelName(this._channel, name, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startGo(int netId) {
        Log.v(TAG, "startGo");
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.createGroup(this._channel, actionListener);
        if (actionListener.await()) {
            Log.v(TAG, "startGo end");
            return actionListener.getResult();
        }
        Log.v(TAG, "startGo error");
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public WifiP2pDeviceInfo getMyDevice() {
        Log.v(TAG, "getMyDevice");
        SyncDeviceListener deviceListener = new SyncDeviceListener();
        this._wifiP2pManager.getMyDevice(this._channel, deviceListener);
        if (!deviceListener.await()) {
            return null;
        }
        if (deviceListener.getDevice() != null) {
            WifiP2pDeviceInfo retDeviceInfo = new WifiP2pDeviceInfo();
            retDeviceInfo.setName(deviceListener.getDevice().deviceName);
            retDeviceInfo.setP2PDevAddress(deviceListener.getDevice().deviceAddress);
            retDeviceInfo.setIsOperatingModeGO(deviceListener.getDevice().isGroupOwner());
            BitSet configMethod = new BitSet();
            configMethod.set(3, deviceListener.getDevice().wpsDisplaySupported());
            configMethod.set(2, deviceListener.getDevice().wpsKeypadSupported());
            configMethod.set(1, deviceListener.getDevice().wpsPbcSupported());
            Log.v(TAG, "getMyDevice : deviceName=" + deviceListener.getDevice().deviceName);
            Log.v(TAG, "getMyDevice : deviceAddress=" + deviceListener.getDevice().deviceAddress);
            Log.v(TAG, "getMyDevice : isGroupOwner=" + deviceListener.getDevice().isGroupOwner());
            Log.v(TAG, "getMyDevice : configMethod=" + configMethod);
            retDeviceInfo.setConfigMethod(configMethod);
            return retDeviceInfo;
        }
        Log.e(TAG, "getMyDevice: device is null.");
        return null;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean isDirectEnabled() {
        Log.v(TAG, "isDirectEnabled");
        SyncWifiP2pEnabledListener enabledListener = new SyncWifiP2pEnabledListener();
        this._wifiP2pManager.isDirectEnabled(this._channel, enabledListener);
        if (enabledListener.await()) {
            return enabledListener.getEnableStatus();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public List<WifiP2pDeviceInfo> getStationList() {
        Log.v(TAG, "saveConfiguration");
        List<WifiP2pDeviceInfo> retDeviceInfoList = new ArrayList<>();
        SyncGroupInfoListener groupInfoListener = new SyncGroupInfoListener();
        this._wifiP2pManager.requestGroupInfo(this._channel, groupInfoListener);
        if (!groupInfoListener.await()) {
            return null;
        }
        for (WifiP2pDevice device : groupInfoListener.getGroup().getClientList()) {
            WifiP2pDeviceInfo retDeviceInfo = new WifiP2pDeviceInfo();
            retDeviceInfo.setName(device.deviceName);
            retDeviceInfo.setP2PDevAddress(device.deviceAddress);
            retDeviceInfo.setIsOperatingModeGO(device.isGroupOwner());
            BitSet configMethod = new BitSet();
            configMethod.set(3, device.wpsDisplaySupported());
            configMethod.set(2, device.wpsKeypadSupported());
            configMethod.set(1, device.wpsPbcSupported());
            retDeviceInfo.setConfigMethod(configMethod);
            retDeviceInfoList.add(retDeviceInfo);
        }
        return retDeviceInfoList;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startWpsPbc(String p2pDevAddr) {
        Log.v(TAG, "startWpsPbc: p2pDevAddr=" + p2pDevAddr);
        WifiP2pConfig config = new WifiP2pConfig();
        config.wps.setup = 0;
        config.groupOwnerIntent = 1;
        config.deviceAddress = p2pDevAddr;
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.connect(this._channel, config, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startWpsPin(String pincode) {
        Log.v(TAG, "startWpsPin: pincode=" + pincode);
        WifiP2pConfig config = new WifiP2pConfig();
        config.wps.setup = 2;
        config.groupOwnerIntent = 1;
        config.wps.pin = pincode;
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.connect(this._channel, config, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean cancel() {
        Log.v(TAG, "cancel");
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.cancelConnect(this._channel, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean removeGroup() {
        Log.v(TAG, "removeGroup");
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.removeGroup(this._channel, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public String createWpsPin(boolean isLongPin) {
        Log.v(TAG, "createWpsPin: isLongPin=" + isLongPin);
        return this._wpsManager.createWpsPin(isLongPin);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setDeviceName(String name) {
        Log.v(TAG, "setDeviceName: name=" + name);
        SyncActionListener actionListener = new SyncActionListener();
        this._wifiP2pManager.setDeviceName(this._channel, name, actionListener);
        if (actionListener.await()) {
            return actionListener.getResult();
        }
        return false;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public List<WifiP2pConfiguration> getConfigurations() {
        Log.v(TAG, "getConfigurations");
        List<WifiP2pConfiguration> retDeviceInfo = new ArrayList<>();
        SyncGroupInfoListener groupInfoListener = new SyncGroupInfoListener();
        this._wifiP2pManager.requestGroupInfo(this._channel, groupInfoListener);
        if (!groupInfoListener.await()) {
            return null;
        }
        WifiP2pConfiguration config = new WifiP2pConfiguration();
        if (groupInfoListener.getGroup() != null) {
            config.setNetworkId(1);
            config.setPreSharedKey(groupInfoListener.getGroup().getPassphrase());
            config.setSsid(groupInfoListener.getGroup().getNetworkName());
            retDeviceInfo.add(config);
            return retDeviceInfo;
        }
        Log.e(TAG, "getConfigurations: groupInfo is null.");
        return retDeviceInfo;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean isValidWpsPin(String pincode) {
        Log.v(TAG, "isValidWpsPin: pincode=" + pincode);
        return this._wpsManager.isValidWpsPin(pincode);
    }

    /* loaded from: classes.dex */
    private class P2pLooperThread extends Thread {
        private Context _context;

        public P2pLooperThread(Context context) {
            this._context = context;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.v(WifiP2pManagerJb.TAG, "P2pLooperThread run");
            if (this._context != null) {
                Looper.prepare();
                WifiP2pManagerJb.this._channel = WifiP2pManagerJb.this._wifiP2pManager.initialize(this._context, Looper.myLooper(), (WifiP2pManager.ChannelListener) null);
                Looper.loop();
            }
        }
    }
}
