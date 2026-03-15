package com.sony.scalar.lib.ssdpdevice;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.scalar.lib.devicediscover.ssdpdevice.SsdpDeviceJni;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class SsdpDevice {
    public static final int RETRY_INTERVAL = 1000;
    private ServerConf conf;
    private String ipAddress;
    private WifiManager.MulticastLock lock;
    private DdStatusListener statusListener;

    /* loaded from: classes.dex */
    public interface DdStatusListener {
        void notifyStatus(DdStatus ddStatus, int i);
    }

    public SsdpDevice(Context context, ServerConf conf, DdStatusListener listener) {
        this.conf = conf;
        this.statusListener = listener;
        WifiManager wifi = (WifiManager) context.getSystemService("wifi");
        if (wifi != null) {
            this.lock = wifi.createMulticastLock("fliing_lock");
            this.lock.setReferenceCounted(true);
        }
    }

    public boolean startServer() {
        DevLog.d("Starting service... ");
        this.ipAddress = getIpAddress();
        if (this.conf.descriptionFile != null && this.conf.deviceDescription != null && this.conf.descriptionPath != null) {
            writeDescription(this.conf.deviceDescription, String.valueOf(this.conf.descriptionPath) + this.conf.descriptionFile);
            if (this.lock != null) {
                this.lock.acquire();
            }
            new Thread(new Runnable() { // from class: com.sony.scalar.lib.ssdpdevice.SsdpDevice.1
                @Override // java.lang.Runnable
                public void run() {
                    SsdpDevice.this.startSsdpServer();
                }
            }).start();
            return true;
        }
        return false;
    }

    public void stopServer() {
        DevLog.d("Stop server");
        SsdpDeviceJni.finishServer();
        if (this.lock != null) {
            this.lock.release();
        }
        notifyServerStatus(0, "Stop server");
    }

    private void notifyStatus(DdStatus status) {
        notifyStatus(status, 0);
    }

    private void notifyStatus(DdStatus status, int opt) {
        if (this.statusListener != null) {
            this.statusListener.notifyStatus(status, opt);
        }
    }

    private void notifyServerStatus(int id, String str) {
        DevLog.d("Service callback: ID=" + id + "  Param=" + str);
        if (id == 0) {
            notifyStatus(DdStatus.OK);
        } else {
            notifyStatus(DdStatus.ERROR_FROM_SERVICE, id);
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public void startSsdpServer() {
        int retry = this.conf.retry;
        int res = -1;
        while (retry >= 0) {
            res = SsdpDeviceJni.startServer(this.ipAddress, String.valueOf(this.conf.ssdpPort), this.conf.descriptionPath, this.conf.descriptionFile);
            if (res == 0) {
                break;
            }
            DevLog.w("Service start error: retrying(" + retry + ")..." + res);
            retry--;
            if (res == -2) {
                SsdpDeviceJni.finishServer();
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
        }
        DevLog.i("Start code " + res);
        switch (res) {
            case -2:
            case -1:
                notifyServerStatus(1, "Service already started");
                return;
            case 0:
                DevLog.i("Start service");
                notifyServerStatus(0, "Start service");
                return;
            default:
                notifyServerStatus(3, "Service start error");
                return;
        }
    }

    private void writeDescription(String deviceDescription, String filePath) {
        FileOutputStream fout;
        BufferedWriter out;
        try {
            File file = new File(filePath);
            file.getParentFile().mkdir();
            fout = new FileOutputStream(filePath, false);
            try {
                out = new BufferedWriter(new OutputStreamWriter(fout, AutoSyncConstants.TEXT_ENCODING_UTF8));
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
        }
        try {
            out.write(deviceDescription);
            out.flush();
            out.close();
            fout.close();
            DevLog.d("DD created: " + filePath);
        } catch (Exception e3) {
            e = e3;
            DevLog.e("Can't create file: " + filePath);
            notifyServerStatus(2, "File write error: " + e);
        }
    }
}
