package com.sony.imaging.app.srctrl.network;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.srctrl.SRCtrlRootState;
import com.sony.imaging.app.srctrl.network.base.NwStateBase;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.HDMIInfoWrapper;

/* loaded from: classes.dex */
public class NetworkRootState extends NwStateBase {
    public static final String ID_CONFIRM = "ID_CONFIRM";
    public static final String ID_CONNECTED = "ID_CONNECTED";
    public static final String ID_REGISTERING = "ID_REGISTERING";
    public static final String ID_RESTARTING = "ID_RESTARTING";
    public static final String ID_STANDBY = "ID_STANDBY";
    public static final String ID_WAITING = "ID_WAITING";
    public static final String ID_WPS_ERROR = "ID_WPS_ERROR";
    public static final String ID_WPS_PBC = "ID_WPS_PBC";
    public static final String ID_WPS_PIN_INPUT = "ID_WPS_PIN_INPUT";
    public static final String ID_WPS_PIN_PRINT = "ID_WPS_PIN_PRINT";
    public static final String PROP_ID_APP_ROOT = "NetworkRoot";
    private static String directTargetDeviceAddress;
    private static String directTargetDeviceName;
    private static String myDeviceName;
    private static String myPsk;
    private static String mySsid;
    private static String myWpsPin;

    @Override // com.sony.imaging.app.fw.State
    public Object getData(String name) {
        return super.getData(name);
    }

    @Override // com.sony.imaging.app.fw.State
    public boolean setData(String name, Object o) {
        return super.setData(name, o);
    }

    @Override // com.sony.imaging.app.srctrl.network.base.NwStateBase
    protected NetworkRootState getContainer() {
        Log.v(getLogTag(), "getContainer should not be called for NetworkRootContainer");
        return null;
    }

    @Override // com.sony.imaging.app.fw.ContainerState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateController.getInstance().setState(this);
        Log.v(getLogTag(), "setRoot " + setData(PROP_ID_APP_ROOT, this));
    }

    @Override // com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppInfo.notifyAppInfo(getActivity().getApplicationContext(), getActivity().getPackageName(), getActivity().getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, BaseApp.PULLING_BACK_KEYS_FOR_PLAYBACK, BaseApp.RESUME_KEYS_FOR_SHOOTING);
        StateController stateController = StateController.getInstance();
        stateController.setAppCondition(StateController.AppCondition.PREPARATION);
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService("wifi");
        WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        if (stateController.isInitialWifiSetup()) {
            addChildState(ID_STANDBY, (Bundle) null);
        } else if (stateController.isFatalError()) {
            setNextState(SRCtrlRootState.FATAL_ERROR, null);
        } else if (wifiManager.isWifiEnabled()) {
            if (wifiP2pManager.isDirectEnabled()) {
                if (wifiP2pManager.getMyDevice().isOperatingModeGO()) {
                    if (wifiP2pManager.getStationList().size() != 0) {
                        addChildState(ID_CONNECTED, (Bundle) null);
                    } else {
                        addChildState(ID_WAITING, (Bundle) null);
                    }
                } else {
                    setNextState(SRCtrlRootState.FATAL_ERROR, null);
                }
            } else {
                setNextState(SRCtrlRootState.FATAL_ERROR, null);
            }
        } else {
            setNextState(SRCtrlRootState.FATAL_ERROR, null);
        }
        stateController.setIsClosingShootingState(false);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        removeData(PROP_ID_APP_ROOT);
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 9;
    }

    protected Bundle getBundleToLayout(String layoutTag) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDirectTargetDeviceName(String name) {
        directTargetDeviceName = name;
    }

    public static String getDirectTargetDeviceName() {
        return directTargetDeviceName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDirectTargetDeviceAddress(String addr) {
        directTargetDeviceAddress = addr;
    }

    public static String getDirectTargetDeviceAddress() {
        return directTargetDeviceAddress;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setWpsPin(String pin) {
        myWpsPin = pin;
    }

    public static String getWpsPin() {
        return myWpsPin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMySsid(String ssid) {
        mySsid = ssid;
    }

    public static String getMySsid() {
        return mySsid;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMyPsk(String psk) {
        myPsk = psk;
    }

    public static String getMyPsk() {
        return myPsk;
    }

    public static void setMyDeviceName(String name) {
        myDeviceName = name;
    }

    public static String getMyDeviceName() {
        return myDeviceName;
    }

    @Override // com.sony.imaging.app.fw.State
    public HDMIInfoWrapper.INFO_TYPE getHDMIInfoType() {
        return HDMIInfoWrapper.INFO_TYPE.INFO_ON;
    }
}
