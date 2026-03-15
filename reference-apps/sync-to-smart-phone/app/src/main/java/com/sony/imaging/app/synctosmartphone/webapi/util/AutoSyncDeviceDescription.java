package com.sony.imaging.app.synctosmartphone.webapi.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory;
import java.net.NetworkInterface;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class AutoSyncDeviceDescription extends DeviceDescription {
    private static final String TAG = AutoSyncDeviceDescription.class.getSimpleName();
    private final String UNIQUE_SERVICE_ID;
    private Context context;

    public AutoSyncDeviceDescription(Context context, String baseFile) {
        super(context, baseFile);
        this.UNIQUE_SERVICE_ID = "000000001001";
        this.context = context;
        this.description = this.description.replaceAll("\\$UUID\\$", getUuid("000000001001", getMacAddress()));
        this.description = this.description.replaceAll("\\$FRIENDLY_NAME\\$", getFriendlyName());
        Log.i(TAG, "Description " + this.description);
    }

    private String getMacAddress() {
        char[] hexchar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        String macAddr = "";
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            if (ifs != null) {
                while (ifs.hasMoreElements()) {
                    NetworkInterface netIf = ifs.nextElement();
                    byte[] macOct = netIf.getHardwareAddress();
                    if (macOct != null) {
                        macAddr = "";
                        for (int i = 0; i < 6; i++) {
                            byte octet = macOct[i];
                            macAddr = (macAddr + hexchar[(octet & 240) >> 4]) + hexchar[octet & 15];
                            if (i < 5) {
                                macAddr = macAddr + ':';
                            }
                        }
                        Log.d(TAG, "mac address = " + macAddr);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "NetworkInterface error " + e);
        }
        return macAddr;
    }

    private String getMacAddress_wifi() {
        Object wifiObj = this.context.getSystemService("wifi");
        if (wifiObj != null && (wifiObj instanceof WifiManager)) {
            WifiInfo wifiInfo = ((WifiManager) wifiObj).getConnectionInfo();
            Log.i(TAG, "Mac wifiInfo " + wifiInfo);
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        }
        return null;
    }

    private String getFriendlyName() {
        WifiP2pDeviceInfo deviceInfo;
        WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = WifiP2pManagerFactory.getInstance(this.context.getApplicationContext());
        if (wifiP2pManager == null || (deviceInfo = wifiP2pManager.getMyDevice()) == null) {
            return null;
        }
        return deviceInfo.getName();
    }

    private String getUuid(String uniqueId, String macAddress) {
        String uuid = "uuid:" + uniqueId + "-1010-8000-" + macAddress.substring(0, 2) + macAddress.substring(3, 5) + macAddress.substring(6, 8) + macAddress.substring(9, 11) + macAddress.substring(12, 14) + macAddress.substring(15, 17);
        return uuid;
    }
}
