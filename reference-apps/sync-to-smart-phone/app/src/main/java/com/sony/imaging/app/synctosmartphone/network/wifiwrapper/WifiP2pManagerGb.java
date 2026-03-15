package com.sony.imaging.app.synctosmartphone.network.wifiwrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory;
import com.sony.wifi.direct.DirectConfiguration;
import com.sony.wifi.direct.DirectDevice;
import com.sony.wifi.direct.DirectManager;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/* loaded from: classes.dex */
public class WifiP2pManagerGb implements WifiP2pManagerFactory.IWifiP2pManager {
    private static final String TAG = WifiP2pManagerGb.class.getSimpleName();
    private BroadcastReceiver bReceiver;
    private DirectManager directManager;

    public WifiP2pManagerGb(Context context) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "WifiP2pManagerGb constructor: Context=" + context);
        this.directManager = (DirectManager) context.getSystemService("wifi-direct");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.sony.wifi.direct.DIRECT_STATE_CHANGED_ACTION");
        iFilter.addAction("com.sony.wifi.direct.GROUP_CREATE_SUCCESS_ACTION");
        iFilter.addAction("com.sony.wifi.direct.GROUP_CREATE_FAILURE_ACTION");
        iFilter.addAction("com.sony.wifi.direct.GROUP_TERMINATE_ACTION");
        iFilter.addAction("com.sony.wifi.direct.STA_CONNECTED_ACTION");
        iFilter.addAction("com.sony.wifi.direct.STA_DISCONNECTED_ACTION");
        iFilter.addAction("com.sony.wifi.direct.PROVISIONING_REQUEST_ACTION");
        iFilter.addAction("com.sony.wifi.direct.WPS_REG_SUCCESS_ACTION");
        iFilter.addAction("com.sony.wifi.direct.WPS_REG_FAILURE_ACTION");
        this.bReceiver = new BroadcastReceiver() { // from class: com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerGb.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiP2pManagerGb.this.handleEvent(context2, intent);
            }
        };
        context.getApplicationContext().registerReceiver(this.bReceiver, iFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if ("com.sony.wifi.direct.DIRECT_STATE_CHANGED_ACTION".equals(action)) {
            Intent broadCast = new Intent(WifiP2pManagerFactory.IWifiP2pManager.DIRECT_STATE_CHANGED_ACTION);
            boolean doSendBroadcast = true;
            int previousState = intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, -1);
            switch (previousState) {
                case -1:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: DIRECT_STATE_UNKNOWN");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 5);
                    break;
                case 0:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: DIRECT_STATE_DISABLING");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 5);
                    break;
                case 1:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: DIRECT_STATE_DISABLED");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 1);
                    break;
                case 2:
                default:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: default");
                    break;
                case 3:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: DIRECT_STATE_ENABLING");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 5);
                    break;
                case 4:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION previousState: DIRECT_STATE_ENABLED");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 3);
                    break;
            }
            int currentState = intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, -1);
            switch (currentState) {
                case -1:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: DIRECT_STATE_UNKNOWN");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 5);
                    break;
                case 0:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: DIRECT_STATE_DISABLING");
                    doSendBroadcast = false;
                    break;
                case 1:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: DIRECT_STATE_DISABLED");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 1);
                    break;
                case 2:
                default:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: default");
                    break;
                case 3:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: DIRECT_STATE_ENABLING");
                    doSendBroadcast = false;
                    break;
                case 4:
                    Log.v(TAG, "DIRECT_STATE_CHANGED_ACTION currentState: DIRECT_STATE_ENABLED");
                    broadCast.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 3);
                    break;
            }
            if (doSendBroadcast) {
                context.sendBroadcast(broadCast);
                return;
            }
            return;
        }
        if ("com.sony.wifi.direct.GROUP_CREATE_SUCCESS_ACTION".equals(action)) {
            Intent broadCast2 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
            DirectConfiguration config = intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG);
            WifiP2pConfiguration retConfig = new WifiP2pConfiguration();
            retConfig.setNetworkId(config.getNetworkId());
            retConfig.setSsid(config.getSsid());
            retConfig.setPreSharedKey(config.getPreSharedKey());
            Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getNetworkId: " + config.getNetworkId());
            Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getSsid: " + config.getSsid());
            Log.v(TAG, "GROUP_CREATE_SUCCESS_ACTION getPreSharedKey: " + config.getPreSharedKey());
            broadCast2.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG, retConfig);
            context.sendBroadcast(broadCast2);
            return;
        }
        if ("com.sony.wifi.direct.GROUP_CREATE_FAILURE_ACTION".equals(action)) {
            context.sendBroadcast(new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION));
            return;
        }
        if ("com.sony.wifi.direct.GROUP_TERMINATE_ACTION".equals(action)) {
            context.sendBroadcast(new Intent(WifiP2pManagerFactory.IWifiP2pManager.GROUP_TERMINATE_ACTION));
            return;
        }
        if ("com.sony.wifi.direct.STA_CONNECTED_ACTION".equals(action)) {
            Intent broadCast3 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
            broadCast3.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            context.sendBroadcast(broadCast3);
            return;
        }
        if ("com.sony.wifi.direct.STA_DISCONNECTED_ACTION".equals(action)) {
            Intent broadCast4 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
            broadCast4.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR, intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            context.sendBroadcast(broadCast4);
            return;
        }
        if ("com.sony.wifi.direct.WPS_REG_SUCCESS_ACTION".equals(action)) {
            context.sendBroadcast(new Intent(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_SUCCESS_ACTION));
            return;
        }
        if ("com.sony.wifi.direct.WPS_REG_FAILURE_ACTION".equals(action)) {
            Intent broadCast5 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.WPS_REG_FAILURE_ACTION);
            int errorCode = intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, -1);
            switch (errorCode) {
                case 1001:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION errorCode: USER_CANCELED");
                    broadCast5.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 2);
                    break;
                case 1002:
                default:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION errorCode: UNKNOWN_ERROR");
                    broadCast5.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 3);
                    break;
                case 1003:
                    Log.v(TAG, "WPS_REG_FAILURE_ACTION errorCode: TIMEOUT");
                    broadCast5.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, 1);
                    break;
            }
            context.sendBroadcast(broadCast5);
            return;
        }
        if ("com.sony.wifi.direct.PROVISIONING_REQUEST_ACTION".equals(action)) {
            DirectDevice device = intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE);
            Intent broadCast6 = new Intent(WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION);
            if (device != null) {
                WifiP2pDeviceInfo retDevice = new WifiP2pDeviceInfo();
                retDevice.setName(device.getName());
                retDevice.setP2PDevAddress(device.getP2PDevAddress());
                retDevice.setConfigMethod(convertConfMethod(device.getConfigMethod()));
                retDevice.setIsOperatingModeGO(device.getOperatingMode() == 3);
                Log.v(TAG, "PROVISIONING_REQUEST_ACTION getName: " + device.getName());
                Log.v(TAG, "PROVISIONING_REQUEST_ACTION getP2PDevAddress: " + device.getP2PDevAddress());
                Log.v(TAG, "PROVISIONING_REQUEST_ACTION getConfigMethod: " + device.getConfigMethod());
                Log.v(TAG, "PROVISIONING_REQUEST_ACTION getOperatingMode: " + device.getOperatingMode());
                broadCast6.putExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE, retDevice);
            }
            context.sendBroadcast(broadCast6);
        }
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setDirectEnabled(boolean enabled) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "setDirectEnabled: " + enabled);
        return this.directManager.setDirectEnabled(enabled);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean saveConfiguration() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "saveConfiguration");
        return this.directManager.saveConfiguration();
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setSsidPostfix(String postfix) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "setSsidPostfix: " + postfix);
        return this.directManager.setSsidPostfix(postfix);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setModelName(String name) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "setModelName: " + name);
        return this.directManager.setModelName(name);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startGo(int netId) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "startGo: " + netId);
        return netId == -2 ? this.directManager.startGo(-2) : this.directManager.startGo(netId);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public WifiP2pDeviceInfo getMyDevice() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "getMyDevice");
        DirectDevice device = this.directManager.getMyDevice();
        if (device != null) {
            WifiP2pDeviceInfo retDevice = new WifiP2pDeviceInfo();
            retDevice.setName(device.getName());
            retDevice.setP2PDevAddress(device.getP2PDevAddress());
            retDevice.setConfigMethod(convertConfMethod(device.getConfigMethod()));
            retDevice.setIsOperatingModeGO(device.getOperatingMode() == 3);
            Log.v(TAG, "getMyDevice getName: " + device.getName());
            Log.v(TAG, "getMyDevice getP2PDevAddress: " + device.getP2PDevAddress());
            Log.v(TAG, "getMyDevice getConfigMethod: " + device.getConfigMethod());
            Log.v(TAG, "getMyDevice getOperatingMode: " + device.getOperatingMode());
            return retDevice;
        }
        return null;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean isDirectEnabled() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "isDirectEnabled");
        return this.directManager.isDirectEnabled();
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public List<WifiP2pDeviceInfo> getStationList() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "getStationList");
        List<WifiP2pDeviceInfo> retStationList = new ArrayList<>();
        List<DirectDevice> stationList = this.directManager.getStationList();
        for (DirectDevice device : stationList) {
            WifiP2pDeviceInfo retDevice = new WifiP2pDeviceInfo();
            retDevice.setName(device.getName());
            retDevice.setP2PDevAddress(device.getP2PDevAddress());
            retDevice.setConfigMethod(convertConfMethod(device.getConfigMethod()));
            retDevice.setIsOperatingModeGO(device.getOperatingMode() == 3);
            Log.v(TAG, "getMyDevice getName: " + device.getName());
            Log.v(TAG, "getMyDevice getP2PDevAddress: " + device.getP2PDevAddress());
            Log.v(TAG, "getMyDevice getConfigMethod: " + device.getConfigMethod());
            Log.v(TAG, "getMyDevice getOperatingMode: " + device.getOperatingMode());
            retStationList.add(retDevice);
        }
        return retStationList;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startWpsPbc(String p2pDevAddr) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "startWpsPbc: " + p2pDevAddr);
        return this.directManager.startWpsPbc(p2pDevAddr);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean startWpsPin(String pincode) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "startWpsPin: " + pincode);
        return this.directManager.startWpsPin(pincode);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean cancel() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "cancel");
        return this.directManager.cancel();
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean removeGroup() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "removeGroup");
        return this.directManager.removeGroup();
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public String createWpsPin(boolean isLongPin) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "createWpsPin: " + isLongPin);
        return this.directManager.createWpsPin(isLongPin);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean setDeviceName(String name) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "setDeviceName: " + name);
        return this.directManager.setDeviceName(name);
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public List<WifiP2pConfiguration> getConfigurations() {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "getConfigurations");
        List<WifiP2pConfiguration> retConfigrations = new ArrayList<>();
        List<DirectConfiguration> configrations = this.directManager.getConfigurations();
        for (DirectConfiguration config : configrations) {
            WifiP2pConfiguration addConfig = new WifiP2pConfiguration();
            addConfig.setNetworkId(config.getNetworkId());
            Log.v(TAG, "getConfigurations getNetworkId: " + config.getNetworkId());
            retConfigrations.add(addConfig);
        }
        return retConfigrations;
    }

    @Override // com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory.IWifiP2pManager
    public boolean isValidWpsPin(String pincode) {
        Log.v(TAG, "呼び出し元 : " + getCalledMethod());
        Log.v(TAG, "isValidWpsPin: " + pincode);
        return this.directManager.isValidWpsPin(pincode);
    }

    private BitSet convertConfMethod(BitSet directDeviceConfMethod) {
        if (directDeviceConfMethod == null) {
            return null;
        }
        BitSet retConfMethod = new BitSet();
        if (directDeviceConfMethod.get(7)) {
            retConfMethod.set(1);
        }
        if (directDeviceConfMethod.get(8)) {
            retConfMethod.set(3);
        }
        if (directDeviceConfMethod.get(3)) {
            retConfMethod.set(2);
            return retConfMethod;
        }
        return retConfMethod;
    }

    private String getCalledMethod() {
        StackTraceElement e = new Exception().getStackTrace()[2];
        String ret = e.getClassName() + "  :  " + e.getMethodName();
        return ret;
    }
}
