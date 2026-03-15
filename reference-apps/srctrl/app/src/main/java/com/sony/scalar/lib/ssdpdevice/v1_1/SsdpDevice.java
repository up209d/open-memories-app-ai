package com.sony.scalar.lib.ssdpdevice.v1_1;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import com.sony.scalar.lib.devicediscover.ssdpdevice.SsdpDeviceJni;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/* loaded from: classes.dex */
public final class SsdpDevice {
    private static final int QUEUE_SIZE = 200;
    private static final int RETRY_INTERVAL = 1000;
    private static SsdpDevice sInstance = new SsdpDevice();
    private Context mContext;
    private WifiManager.MulticastLock mMclock;
    private final BlockingQueue<Runnable> mQueue = new ArrayBlockingQueue(200);
    private boolean mIsLaunched = false;
    private final Thread mLifeCycleThread = new Thread(new Runnable() { // from class: com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice.1
        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    ((Runnable) SsdpDevice.this.mQueue.take()).run();
                } catch (InterruptedException e) {
                    DevLog.e("Interrupted exception while getting from queue.");
                }
            }
        }
    });

    /* loaded from: classes.dex */
    public interface DdStatusListener {
        void notifyStatus(DdStatus ddStatus, int i);
    }

    static /* synthetic */ String access$200() {
        return getIpAddress();
    }

    private SsdpDevice() {
        this.mLifeCycleThread.start();
    }

    public static SsdpDevice getInstance() {
        return sInstance;
    }

    public void initialize(Context context) {
        this.mContext = context;
    }

    public boolean enqueueStartServer(final ServerConf conf, final DdStatusListener listener) {
        DevLog.d("Enqueue start server... ");
        return this.mQueue.add(new Runnable() { // from class: com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice.2
            @Override // java.lang.Runnable
            public void run() {
                DevLog.d("Start server task beginning");
                if (SsdpDevice.this.mQueue.size() != 0) {
                    DevLog.d("Start server skipped");
                    SsdpDevice.notifyStatus(listener, DdStatus.SKIPPED);
                    return;
                }
                String ipAddress = SsdpDevice.access$200();
                if (ipAddress == null) {
                    DevLog.e("No IP address... ");
                    SsdpDevice.notifyStatus(listener, DdStatus.ERROR_NETWORK);
                    return;
                }
                if (conf.descriptionFile != null && conf.deviceDescription != null && conf.descriptionPath != null && SsdpDevice.this.writeDescription(conf.deviceDescription, conf.descriptionPath + conf.descriptionFile)) {
                    if (SsdpDevice.this.mIsLaunched) {
                        DevLog.i("Already started. Stop once to restart.");
                        SsdpDevice.this.runStop();
                    }
                    int res = SsdpDevice.this.runStart(ipAddress, conf);
                    if (SsdpDevice.this.mQueue.size() != 0) {
                        SsdpDevice.notifyStatus(listener, DdStatus.ON_GOING);
                    } else {
                        switch (res) {
                            case 0:
                                SsdpDevice.notifyStatus(listener, DdStatus.OK);
                                break;
                            default:
                                SsdpDevice.notifyStatus(listener, DdStatus.ERROR_FROM_SERVICE, res);
                                break;
                        }
                    }
                    DevLog.d("Start server task end");
                    return;
                }
                DevLog.e("No device description...");
                SsdpDevice.notifyStatus(listener, DdStatus.ERROR_DESCRIPTION);
            }
        });
    }

    public boolean enqueueStopServer(final DdStatusListener listener) {
        DevLog.d("Enqueue stop server...");
        return this.mQueue.add(new Runnable() { // from class: com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice.3
            @Override // java.lang.Runnable
            public void run() {
                DevLog.d("Stop server task beginning");
                if (SsdpDevice.this.mQueue.size() == 0) {
                    SsdpDevice.this.runStop();
                    DevLog.d("Stop server task end");
                    if (SsdpDevice.this.mQueue.size() != 0) {
                        SsdpDevice.notifyStatus(listener, DdStatus.ON_GOING);
                        return;
                    } else {
                        SsdpDevice.notifyStatus(listener, DdStatus.FINISHED);
                        return;
                    }
                }
                DevLog.d("Stop server skipped");
                SsdpDevice.notifyStatus(listener, DdStatus.SKIPPED);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int runStart(String ipAddress, ServerConf conf) {
        DevLog.v("runStart");
        if (this.mMclock == null) {
            WifiManager wifi = (WifiManager) this.mContext.getSystemService("wifi");
            this.mMclock = wifi.createMulticastLock("fliing_lock");
        }
        int retry = conf.retry;
        int res = -1;
        while (true) {
            if (retry < 0) {
                break;
            }
            res = SsdpDeviceJni.startServer(ipAddress, String.valueOf(conf.ssdpPort), conf.descriptionPath, conf.descriptionFile);
            if (res == 0) {
                this.mIsLaunched = true;
                break;
            }
            runStop();
            DevLog.w("Service start error: retrying(" + retry + ")..." + res);
            retry--;
            SystemClock.sleep(1000L);
        }
        DevLog.i("Start code " + res);
        if (res == 0) {
            this.mMclock.acquire();
        }
        return res;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runStop() {
        DevLog.v("runStop");
        SsdpDeviceJni.finishServer();
        this.mIsLaunched = false;
        if (this.mMclock != null) {
            try {
                this.mMclock.release();
                this.mMclock = null;
            } catch (Exception e) {
                DevLog.w("E lock.release " + e.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifyStatus(DdStatusListener listener, DdStatus status) {
        notifyStatus(listener, status, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifyStatus(DdStatusListener statusListener, DdStatus status, int opt) {
        if (statusListener != null) {
            statusListener.notifyStatus(status, opt);
        }
    }

    private static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddr = addresses.nextElement();
                    if (inetAddr instanceof Inet4Address) {
                        String address = inetAddr.getHostAddress();
                        DevLog.d("IP found " + address);
                        if (!"127.0.0.1".equals(address) && !"0.0.0.0".equals(address)) {
                            return address;
                        }
                    }
                }
            }
            DevLog.d("No available IP found ");
            return null;
        } catch (Exception e) {
            DevLog.e(e.toString());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean writeDescription(String deviceDescription, String filePath) {
        BufferedWriter out;
        FileOutputStream fout = null;
        BufferedWriter out2 = null;
        try {
            try {
                File file = new File(filePath);
                if (!file.getParentFile().mkdir()) {
                    DevLog.v("Directory seems to be created already");
                }
                FileOutputStream fout2 = new FileOutputStream(filePath, false);
                try {
                    out = new BufferedWriter(new OutputStreamWriter(fout2, "UTF-8"));
                } catch (Exception e) {
                    fout = fout2;
                } catch (Throwable th) {
                    th = th;
                    fout = fout2;
                }
                try {
                    out.write(deviceDescription);
                    out.flush();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e2) {
                            DevLog.w("BufferedWriter close IOException");
                        }
                    }
                    if (fout2 != null) {
                        try {
                            fout2.close();
                        } catch (IOException e3) {
                            DevLog.w("FileOutputStream close IOException");
                        }
                    }
                    DevLog.d("DD created: " + filePath);
                    return true;
                } catch (Exception e4) {
                    out2 = out;
                    fout = fout2;
                    DevLog.e("Can't create file: " + filePath);
                    if (out2 != null) {
                        try {
                            out2.close();
                        } catch (IOException e5) {
                            DevLog.w("BufferedWriter close IOException");
                        }
                    }
                    if (fout == null) {
                        return false;
                    }
                    try {
                        fout.close();
                        return false;
                    } catch (IOException e6) {
                        DevLog.w("FileOutputStream close IOException");
                        return false;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    out2 = out;
                    fout = fout2;
                    if (out2 != null) {
                        try {
                            out2.close();
                        } catch (IOException e7) {
                            DevLog.w("BufferedWriter close IOException");
                        }
                    }
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (IOException e8) {
                            DevLog.w("FileOutputStream close IOException");
                        }
                    }
                    throw th;
                }
            } catch (Exception e9) {
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }
}
