package com.sony.imaging.app.srctrl.util;

import android.content.Context;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SRCtrlEnvironment {
    private static final int CONTINUOUS_SHOOTING_API_VERSION = 10;
    private static final int ENABLE_GET_BATTERY_THRESHOLD_LIST = 2;
    private static final int INTVAL_IRIS_RING_TYPE_1 = 1;
    private static final int INTVAL_IRIS_RING_TYPE_2 = 2;
    private static final int INTVAL_IRIS_RING_TYPE_NONE = 0;
    private static final int STREAMING_PLAYBACK_API_VERSION = 13;
    private Context context = null;
    private List<Integer> handringKeys = new ArrayList();
    private boolean mIsS1AfDisable = false;
    private String s_SERVER_NAME = null;
    private static final String TAG = SRCtrlEnvironment.class.getSimpleName();
    private static SRCtrlEnvironment sInstance = new SRCtrlEnvironment();
    private static int category = -1;
    private static Boolean _isEnableBulbShooting = null;

    public static SRCtrlEnvironment getInstance() {
        return sInstance;
    }

    private SRCtrlEnvironment() {
        Log.i(TAG, "Constructor called.");
    }

    public void initialize(Context context) {
        this.context = context;
        this.handringKeys.clear();
        Iterator<Integer> it = AppRoot.USER_KEYCODE.getIterator();
        while (it.hasNext()) {
            this.handringKeys.add(it.next());
        }
        category = ScalarProperties.getInt("model.category");
    }

    public List<Integer> getHandringKeys() {
        return this.handringKeys;
    }

    public boolean getS1AfDisable() {
        return this.mIsS1AfDisable;
    }

    public void setS1AfDisable(boolean isDisable) {
        this.mIsS1AfDisable = isDisable;
    }

    public String getContinuousShootBaseURL() {
        return SRCtrlConstants.CONTINUOUS_DIRECTORY;
    }

    public String getStreamingURL() {
        return SRCtrlConstants.STREAMIMG_URL;
    }

    public Context getContext() {
        return this.context;
    }

    public int getPort() {
        return SRCtrlConstants.HTTP_PORT_INT;
    }

    public String getLiveviewURL() {
        return SRCtrlConstants.LIVEVIEW_URL;
    }

    public String getPostviewBaseURL() {
        return SRCtrlConstants.POSTVIEW_DIRECTORY;
    }

    public String getPostviewOnMemoryBaseURL() {
        return SRCtrlConstants.POSTVIEW_DIRECTORY_ON_MEMORY;
    }

    public String getContentTransferBaseURL() {
        return SRCtrlConstants.CONTENTTRANSFER_DIRECTORY;
    }

    public String getServerKeyword() {
        return SRCtrlConstants.SERVER_KEYWORD_4_1;
    }

    public String getServerVersion() {
        return SRCtrlConstants.SERVER_VERSION_4_1;
    }

    public String getServerName() {
        if (this.s_SERVER_NAME != null) {
            return this.s_SERVER_NAME;
        }
        StringBuffer buff = new StringBuffer("Smart Remote Control");
        buff.append(" SR/");
        boolean bReady = SRCtrl.VERSION_NAME_STR != null;
        buff.append(bReady ? SRCtrl.VERSION_NAME_STR : "");
        buff.append(ExposureModeController.SOFT_SNAP);
        buff.append(getServerKeyword());
        String sRet = buff.toString();
        if (bReady) {
            this.s_SERVER_NAME = sRet;
            return sRet;
        }
        return sRet;
    }

    public String getMacAddr() {
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

    public boolean isStreamingPlaybackAPISupported() {
        return 13 <= Environment.getVersionPfAPI();
    }

    public boolean isEnableGetBatteryThresholdList() {
        return 2 <= Environment.getVersionPfAPI();
    }

    public boolean isEnableContinuousShooting() {
        return 10 <= Environment.getVersionPfAPI();
    }

    public boolean isEnableBulbShooting() {
        if (_isEnableBulbShooting != null) {
            return _isEnableBulbShooting.booleanValue();
        }
        boolean enable = false;
        if (!isSystemApp() && 2 <= Environment.getVersionPfAPI() && Environment.isBulbSupported) {
            enable = true;
        }
        _isEnableBulbShooting = Boolean.valueOf(enable);
        return _isEnableBulbShooting.booleanValue();
    }

    public boolean isEnableInterimPreview() {
        return 7 <= Environment.getVersionPfAPI();
    }

    public boolean isEnableLensID() {
        return 15 <= Environment.getVersionPfAPI();
    }

    public boolean isEnableAttachedLensInfo() {
        return isCategoryILDCE();
    }

    private boolean isIrisRingMounted() {
        if (Environment.getVersionPfAPI() < 13) {
            return false;
        }
        switch (ScalarProperties.getInt("device.iris.ring.type")) {
            case 0:
                return false;
            case 1:
            case 2:
                return true;
            default:
                Log.e(TAG, "ERROR: undefined camera");
                return false;
        }
    }

    public boolean isIrisRingInvalid() {
        if (isSystemApp()) {
            return false;
        }
        return isIrisRingMounted();
    }

    public boolean isSystemApp() {
        return SRCtrl.isSystemApp();
    }

    public boolean isCategoryDSC() {
        return category == 2;
    }

    public boolean isCategoryILDCE() {
        return category == 1;
    }

    public String escapeDecimalPoint(String val) {
        return val.replace(',', '.');
    }
}
