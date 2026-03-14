package com.sony.imaging.app.util;

import android.util.Log;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class HDMIInfoWrapper {
    private static final String TAG = "HDMIInfoWrapper";
    protected static INFO_TYPE mCurrentType;
    protected static INFO_TYPE mForceType;
    protected static DisplayManager mDisplayManager = null;
    protected static boolean mInfoOff = false;

    /* loaded from: classes.dex */
    public enum INFO_TYPE {
        NEUTRAL,
        INFO_ON,
        INFO_OFF
    }

    public static void initialize() {
        mCurrentType = INFO_TYPE.INFO_ON;
        mForceType = INFO_TYPE.NEUTRAL;
        if (Environment.getVersionPfAPI() >= 2) {
            if (Settings.getHDMIOSDOutput() == 0) {
                mInfoOff = true;
            } else {
                mInfoOff = false;
            }
        }
        if (mInfoOff) {
            mDisplayManager = new DisplayManager();
            OnDisplayEventListener mDisplayEventListener = new OnDisplayEventListener();
            mDisplayManager.setDisplayStatusListener(mDisplayEventListener);
        }
    }

    public static void terminate() {
        mCurrentType = INFO_TYPE.INFO_ON;
        mForceType = INFO_TYPE.NEUTRAL;
        if (mDisplayManager != null) {
            mDisplayManager.finish();
            mDisplayManager = null;
        }
    }

    public static void setType(INFO_TYPE type) {
        Log.d(TAG, "setType: " + type + " mCurrentType: " + mCurrentType);
        if (mCurrentType != type) {
            mCurrentType = type;
            refresh();
        }
    }

    public static void setTypeForce(INFO_TYPE type) {
        if (mForceType != type) {
            mForceType = type;
            refresh();
        }
    }

    protected static void refresh() {
        INFO_TYPE requiredType;
        if (mInfoOff) {
            String outputDevice = "DEVICE_ID_PANEL";
            String[] devices = mDisplayManager.getActiveDevices();
            int outputDeviceNum = devices == null ? 0 : devices.length;
            Log.i(TAG, "Output Devices");
            for (int i = 0; i < outputDeviceNum; i++) {
                Log.i(TAG, "  " + devices[i]);
            }
            INFO_TYPE currentType = null;
            if (1 == outputDeviceNum) {
                if ("DEVICE_ID_HDMI".equals(devices[0])) {
                    currentType = INFO_TYPE.INFO_ON;
                    outputDevice = "DEVICE_ID_PANEL";
                } else {
                    outputDevice = devices[0];
                }
            } else if (2 == outputDeviceNum && ("DEVICE_ID_HDMI".equals(devices[0]) || "DEVICE_ID_HDMI".equals(devices[1]))) {
                if ("DEVICE_ID_PANEL".equals(devices[1]) || "DEVICE_ID_PANEL".equals(devices[0])) {
                    currentType = INFO_TYPE.INFO_OFF;
                    outputDevice = "DEVICE_ID_PANEL";
                } else if ("DEVICE_ID_FINDER".equals(devices[1]) || "DEVICE_ID_FINDER".equals(devices[0])) {
                    currentType = INFO_TYPE.INFO_OFF;
                    outputDevice = "DEVICE_ID_FINDER";
                }
            }
            INFO_TYPE info_type = INFO_TYPE.NEUTRAL;
            if (INFO_TYPE.NEUTRAL == mForceType) {
                requiredType = mCurrentType;
            } else {
                requiredType = mForceType;
            }
            if (currentType == null) {
                Log.d(TAG, "HDMI ON");
                mDisplayManager.setOSDOutput("DEVICE_ID_HDMI", "OSD_OUTPUT_ON");
            } else if (INFO_TYPE.INFO_OFF == requiredType) {
                Log.d(TAG, "HDMI OFF and Display to Panel or EVF");
                mDisplayManager.setOSDOutput("DEVICE_ID_HDMI", "OSD_OUTPUT_OFF");
                mDisplayManager.switchDisplayOutputTo(outputDevice);
            } else if (INFO_TYPE.INFO_ON == requiredType) {
                Log.d(TAG, "HDMI ON and Don't want to display to Panel or EVF");
                mDisplayManager.setOSDOutput("DEVICE_ID_HDMI", "OSD_OUTPUT_ON");
            }
        }
    }

    /* loaded from: classes.dex */
    static class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            if (4096 == eventId) {
                HDMIInfoWrapper.refresh();
            }
        }
    }
}
