package com.sony.imaging.app.synctosmartphone.network.wifiwrapper;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class WifiP2pManagerFactory {
    private static final String TAG = WifiP2pManagerFactory.class.getSimpleName();
    private static IWifiP2pManager instance = null;

    /* loaded from: classes.dex */
    public interface IWifiP2pManager {
        public static final int DIRECT_STATE_DISABLED = 1;
        public static final int DIRECT_STATE_ENABLED = 3;
        public static final int DIRECT_STATE_UNKNOWN = 5;
        public static final String EXTRA_DIRECT_CONFIG = "direct_config";
        public static final String EXTRA_DIRECT_DETAILED_STATE = "direct_detailed_state";
        public static final String EXTRA_DIRECT_OPERATION_MODE = "direct_operate_mode";
        public static final String EXTRA_DIRECT_STATE = "direct_state";
        public static final String EXTRA_ERROR_CODE = "error_code";
        public static final String EXTRA_P2P_DEVICE = "p2p_device";
        public static final String EXTRA_P2P_DEV_ADDR = "p2p_dev_addr";
        public static final String EXTRA_PREVIOUS_DIRECT_DETAILED_STATE = "previous_direct_detailed_state";
        public static final String EXTRA_PREVIOUS_DIRECT_STATE = "previous_direct_state";
        public static final String EXTRA_SERVICE_DISCOVERY_INFO = "p2p_serv_disc_info";
        public static final String EXTRA_STA_ADDR = "sta_addr";
        public static final int PERSISTENT_GO = -2;
        public static final String DIRECT_STATE_CHANGED_ACTION = WifiP2pManagerFactory.class.getName() + ".DIRECT_STATE_CHANGED_ACTION";
        public static final String DIRECT_DETAILED_STATE_CHANGED_ACTION = WifiP2pManagerFactory.class.getName() + ".DIRECT_DETAILED_STATE_CHANGED_ACTION";
        public static final String DEVICE_FOUND_ACTION = WifiP2pManagerFactory.class.getName() + ".DEVICE_FOUND_ACTION";
        public static final String SERVICE_DISCOVERY_RESPONSE_ACTION = WifiP2pManagerFactory.class.getName() + ".SERVICE_DISCOVERY_RESPONSE_ACTION";
        public static final String GO_NEG_REQUEST_ACTION = WifiP2pManagerFactory.class.getName() + ".GO_NEG_REQUEST_ACTION";
        public static final String PROVISIONING_REQUEST_ACTION = WifiP2pManagerFactory.class.getName() + ".PROVISIONING_REQUEST_ACTION";
        public static final String INVITATION_REQUEST_ACTION = WifiP2pManagerFactory.class.getName() + ".INVITATION_REQUEST_ACTION";
        public static final String STA_CONNECTED_ACTION = WifiP2pManagerFactory.class.getName() + ".STA_CONNECTED_ACTION";
        public static final String STA_DISCONNECTED_ACTION = WifiP2pManagerFactory.class.getName() + ".STA_DISCONNECTED_ACTION";
        public static final String WPS_REG_SUCCESS_ACTION = WifiP2pManagerFactory.class.getName() + ".WPS_REG_SUCCESS_ACTION";
        public static final String WPS_REG_FAILURE_ACTION = WifiP2pManagerFactory.class.getName() + ".WPS_REG_FAILURE_ACTION";
        public static final String GROUP_CREATE_SUCCESS_ACTION = WifiP2pManagerFactory.class.getName() + ".GROUP_CREATE_SUCCESS_ACTION";
        public static final String GROUP_CREATE_FAILURE_ACTION = WifiP2pManagerFactory.class.getName() + ".GROUP_CREATE_FAILURE_ACTION";
        public static final String GROUP_TERMINATE_ACTION = WifiP2pManagerFactory.class.getName() + ".GROUP_TERMINATE_ACTION";
        public static final String INVITE_SUCCESS_ACTION = WifiP2pManagerFactory.class.getName() + ".INVITE_SUCCESS_ACTION";
        public static final String INVITE_FAILURE_ACTION = WifiP2pManagerFactory.class.getName() + ".INVITE_FAILURE_ACTION";
        public static final String STA_REJECTED_ACTION = WifiP2pManagerFactory.class.getName() + ".STA_REJECTED_ACTION";
        public static final String AUTO_CONNECT_START_ACTION = WifiP2pManagerFactory.class.getName() + ".AUTO_CONNECT_START_ACTION";

        boolean cancel();

        String createWpsPin(boolean z);

        List<WifiP2pConfiguration> getConfigurations();

        WifiP2pDeviceInfo getMyDevice();

        List<WifiP2pDeviceInfo> getStationList();

        boolean isDirectEnabled();

        boolean isValidWpsPin(String str);

        boolean removeGroup();

        boolean saveConfiguration();

        boolean setDeviceName(String str);

        boolean setDirectEnabled(boolean z);

        boolean setModelName(String str);

        boolean setSsidPostfix(String str);

        boolean startGo(int i);

        boolean startWpsPbc(String str);

        boolean startWpsPin(String str);
    }

    public static IWifiP2pManager getInstance(Context context) {
        Log.v(TAG, "getInstance build version [" + getBuildSdkVersion() + "]");
        if (instance == null) {
            Context context2 = context.getApplicationContext();
            switch (getBuildSdkVersion()) {
                case 10:
                    Log.v(TAG, "new GINGERBREAD context:" + context2);
                    instance = new WifiP2pManagerGb(context2);
                    break;
                case 16:
                    instance = new WifiP2pManagerJb(context2);
                    break;
                default:
                    Log.v(TAG, "default");
                    break;
            }
        }
        return instance;
    }

    private static int getBuildSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}
