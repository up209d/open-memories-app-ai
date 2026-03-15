package com.sony.imaging.app.synctosmartphone.commonUtil;

/* loaded from: classes.dex */
public class WifiParameters {
    private static String DEV_NAME = "";
    private static String SSID = "";
    private static String PASSWD = "";
    private static WifiParameters mInstance = null;
    private static String CAMERA_DEV_NAME = "";
    private static String CAMERA_IP_ADDR = "";

    public static WifiParameters getInstance() {
        if (mInstance == null) {
            mInstance = new WifiParameters();
        }
        return mInstance;
    }

    private static void setWifiParameters(WifiParameters param) {
        if (mInstance == null) {
            mInstance = param;
        }
    }

    protected WifiParameters() {
        setWifiParameters(this);
    }

    public void setDeviceName(String value) {
        DEV_NAME = value;
    }

    public void setSSID(String value) {
        SSID = value;
    }

    public void setPASSWD(String value) {
        PASSWD = value;
    }

    public void setDeviceNameOfCamera(String value) {
        CAMERA_DEV_NAME = value;
    }

    public void setIPAddrOfCamera(String value) {
        CAMERA_IP_ADDR = value;
    }

    public String getDeviceName() {
        return DEV_NAME;
    }

    public String getSSID() {
        return SSID;
    }

    public String getPASSWD() {
        return PASSWD;
    }

    public String getDeviceNameOfCamera() {
        return CAMERA_DEV_NAME;
    }

    public String getIPAddrOfCamera() {
        return CAMERA_IP_ADDR;
    }
}
