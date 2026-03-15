package com.sony.scalar.lib.devicediscover.ssdpdevice;

/* loaded from: classes.dex */
public class SsdpDeviceJni {
    public static native void finishServer();

    public static native int startServer(String str, String str2, String str3, String str4);

    static {
        System.loadLibrary("ssdpdevice");
    }
}
