package com.sony.imaging.app.srctrl.network.dd;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;

/* loaded from: classes.dex */
public class SRCtrlDeviceDescription extends DeviceDescription {
    private static final String TAG = SRCtrlDeviceDescription.class.getSimpleName();
    private final String UNIQUE_SERVICE_ID;
    private Context context;

    public SRCtrlDeviceDescription(Context context, String baseFile, String ssid) {
        super(context, baseFile);
        String friendlyName;
        this.UNIQUE_SERVICE_ID = SRCtrlConstants.DEVICE_DISCOVERY_UNIQUE_SERVICE_ID;
        this.context = context;
        if (this.description == null || this.description.isEmpty()) {
            Log.i(TAG, "description isEmpty");
            this.description = null;
            return;
        }
        String macAddress = getMacAddress();
        if (UtilPFWorkaround.isDeviceNameModifyNeeded()) {
            int ssidPrefixIndex = ssid.indexOf(SRCtrlConstants.SSID_STRING_DIRECT) + SRCtrlConstants.SSID_STRING_DIRECT.length();
            friendlyName = ssid.substring(ssidPrefixIndex + 5, ssid.length());
        } else {
            friendlyName = getFriendlyName();
        }
        if (macAddress == null || macAddress.isEmpty()) {
            Log.i(TAG, "Failed getMacAddress");
            this.description = null;
        } else if (friendlyName == null || friendlyName.isEmpty()) {
            Log.i(TAG, "Failed getFriendlyName");
            this.description = null;
        } else {
            this.description = this.description.replaceAll("\\$UUID\\$", getUuid(SRCtrlConstants.DEVICE_DISCOVERY_UNIQUE_SERVICE_ID, macAddress));
            this.description = this.description.replaceAll("\\$FRIENDLY_NAME\\$", SecurityUtils.escapeXMLText(Matcher.quoteReplacement(friendlyName)));
            Log.i(TAG, "Description " + this.description);
        }
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
