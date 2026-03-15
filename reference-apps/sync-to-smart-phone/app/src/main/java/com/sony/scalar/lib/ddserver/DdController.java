package com.sony.scalar.lib.ddserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import com.sony.scalar.lib.ddserver.util.DevLog;
import com.sony.scalar.lib.webapiddservice.DeviceInfo;
import com.sony.scalar.lib.webapiddservice.aidl.IDdService;
import com.sony.scalar.lib.webapiddservice.aidl.IDdServiceCallback;
import com.sony.wifi.direct.DirectDevice;
import com.sony.wifi.direct.DirectManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class DdController {
    private static int bindCount = 0;
    private ServerConf conf;
    private Context context;
    private DdStatusListener statusListener;
    private IDdService service = null;
    private ServiceConnection svcConn = new ServiceConnection() { // from class: com.sony.scalar.lib.ddserver.DdController.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder binder) {
            DevLog.i("onConnected");
            DdController.this.service = IDdService.Stub.asInterface(binder);
            int i = DdController.bindCount;
            DdController.bindCount = i + 1;
            if (i != 0) {
                return;
            }
            DdController.this.startService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            DevLog.i("Disconnected");
            DdController.this.service = null;
        }
    };
    private IDdServiceCallback callback = new IDdServiceCallback.Stub() { // from class: com.sony.scalar.lib.ddserver.DdController.2
        @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdServiceCallback
        public void notifyStatus(int id, String str) throws RemoteException {
            DevLog.i("Service callback: ID=" + id + "  Param=" + str);
            if (id == 0) {
                DdController.this.notifyStatus(DdStatus.OK);
            } else {
                DdController.this.notifyStatus(DdStatus.ERROR_FROM_SERVICE, id);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface DdStatusListener {
        void notifyStatus(DdStatus ddStatus, int i);
    }

    public DdController(Context context, ServerConf conf, DdStatusListener listener) {
        this.context = context;
        this.statusListener = listener;
        this.conf = conf;
    }

    public boolean bindService() {
        boolean b = checkPackage(this.conf.ddServicePackage);
        if (b) {
            this.context.bindService(new Intent(this.conf.ddServiceIntent), this.svcConn, 1);
        } else {
            notifyStatus(DdStatus.ERROR_SERVICE_NOT_FOUND);
        }
        DevLog.w("BIND");
        return b;
    }

    public void unbindService() {
        DevLog.w("UNBIND");
        bindCount--;
        if (this.service != null) {
            try {
                this.service.unregisterCallback(this.callback);
            } catch (Exception e) {
            }
        }
        this.context.unbindService(this.svcConn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyStatus(DdStatus status) {
        notifyStatus(status, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyStatus(DdStatus status, int opt) {
        if (this.statusListener != null) {
            this.statusListener.notifyStatus(status, opt);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startService() {
        DeviceInfo devInfo;
        String path;
        DevLog.i("Starting service... " + this.service);
        if (this.service != null && (devInfo = makeDeviceInfo()) != null) {
            try {
                if (this.conf.debugOnPhone) {
                    path = this.conf.descriptionPathPhone;
                } else {
                    path = this.conf.descriptionPath;
                }
                this.service.initialize(devInfo, path, this.conf.descriptionFile, this.conf.ssdpPort);
                this.service.registerCallback(this.callback);
                this.service.start();
            } catch (RemoteException e) {
                DevLog.e(e.toString());
                notifyStatus(DdStatus.ERROR_SERVICE_START);
            }
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
                            macAddr = String.valueOf(String.valueOf(macAddr) + hexchar[(octet & 240) >> 4]) + hexchar[octet & 15];
                            if (i < 5) {
                                macAddr = String.valueOf(macAddr) + ':';
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            DevLog.e("NetworkInterface error " + e);
        }
        return macAddr;
    }

    private String getMacAddress_wifi() {
        Object wifiObj = this.context.getSystemService("wifi");
        if (wifiObj != null && (wifiObj instanceof WifiManager)) {
            WifiInfo wifiInfo = ((WifiManager) wifiObj).getConnectionInfo();
            DevLog.i("Mac wifiInfo " + wifiInfo);
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        }
        return null;
    }

    private String getIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddr = addresses.nextElement();
                    if (inetAddr instanceof Inet4Address) {
                        String address = inetAddr.getHostAddress();
                        DevLog.i("IP found " + address);
                        if (!"127.0.0.1".equals(address) && !"0.0.0.0".equals(address)) {
                            return address;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            DevLog.e(e.toString());
            return null;
        }
    }

    private String getFriendlyName() {
        if (this.conf.debugOnPhone) {
            return "Debug phone";
        }
        Object obj = this.context.getSystemService("wifi-direct");
        if (obj instanceof DirectManager) {
            DirectManager directManager = (DirectManager) obj;
            DirectDevice device = directManager.getMyDevice();
            if (device == null) {
                return null;
            }
            String name = directManager.getMyDevice().getName();
            return name;
        }
        DevLog.e("Can't get DirectManager service");
        return null;
    }

    private String getUuid(String macAddress) {
        String uuid = String.valueOf(this.conf.uniqueServiceId) + "-1010-8000-" + macAddress.substring(0, 2) + macAddress.substring(3, 5) + macAddress.substring(6, 8) + macAddress.substring(9, 11) + macAddress.substring(12, 14) + macAddress.substring(15, 17);
        return uuid;
    }

    private DeviceInfo makeDeviceInfo() {
        String ipAddress = getIpAddress();
        DevLog.i("#IP: " + ipAddress);
        String macAddress = getMacAddress();
        DevLog.i("#Mac: " + macAddress);
        String friendlyName = getFriendlyName();
        DevLog.i("#FriendlyName: " + friendlyName);
        if (ipAddress == null || macAddress == null) {
            notifyStatus(DdStatus.ERROR_NETWORK);
            return null;
        }
        if (friendlyName == null) {
            notifyStatus(DdStatus.ERROR_DEVICE_NAME);
            return null;
        }
        DeviceInfo info = new DeviceInfo();
        info.setIpAddress(ipAddress);
        info.setFriendlyName(friendlyName);
        info.setModelDescription(this.conf.modelDescription);
        info.setModelName(this.conf.modelName);
        info.setUdn("uuid:" + getUuid(macAddress));
        info.setWebServiceInfo(new ArrayList<>(Arrays.asList(this.conf.serviceInfo)));
        info.setIconInfoList(new ArrayList<>(Arrays.asList(this.conf.iconInfo)));
        return info;
    }

    private boolean checkPackage(String packageName) {
        PackageManager pm = this.context.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, 0);
            DevLog.i(String.valueOf(packageName) + " is found");
            return true;
        } catch (Exception e) {
            DevLog.i(String.valueOf(packageName) + " is not found ");
            return false;
        }
    }
}
