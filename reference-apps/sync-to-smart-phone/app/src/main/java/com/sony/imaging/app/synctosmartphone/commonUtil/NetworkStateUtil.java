package com.sony.imaging.app.synctosmartphone.commonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pConfiguration;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.synctosmartphone.state.ConnectingStateSync;
import com.sony.imaging.app.synctosmartphone.state.TransferringStateSync;
import com.sony.imaging.app.synctosmartphone.webapi.definition.StatusCode;
import com.sony.imaging.app.synctosmartphone.webapi.util.AutoSyncDeviceDescription;
import com.sony.imaging.app.synctosmartphone.webapi.util.WsController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.lib.ssdpdevice.DdStatus;
import com.sony.scalar.lib.ssdpdevice.DevLog;
import com.sony.scalar.lib.ssdpdevice.ServerConf;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.sysnetutil.ScalarWifiInfo;
import com.sony.scalar.sysutil.ScalarProperties;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class NetworkStateUtil extends State implements SsdpDevice.DdStatusListener {
    public static final int DELAY_WIFI_GO_TO_FATAL_ERR = 5000;
    private static final String DEVICE_DESCRIPTION_PAIRING_FILE = "scalarwebapi_dd_pairing.xml";
    private static final String DEVICE_DESCRIPTION_SYNC_FILE = "scalarwebapi_dd_sync.xml";
    private static final String TAG = NetworkStateUtil.class.getSimpleName();
    private static NetworkStateUtil mInstance = null;
    private int bootMode;
    private Runnable delayedGoToFatalError;
    private Runnable delayedWifiEnabler;
    private IntentFilter iFilter;
    private boolean isDdServerReady;
    private boolean isDisableActionFiltered;
    private boolean isDisablingForFinish;
    private boolean isEnableActionFiltered;
    private boolean isGroupCreateActionFiltered;
    private boolean isInitialDisabling;
    private boolean isRetrying;
    private WeakReference<Context> mApplicationContextRef;
    private State state = null;
    private int numOfNotifySyncImage = 0;
    private int numOfTotalSyncImage = 0;
    private Handler handler = null;
    private WifiManager wifiManager = null;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = null;
    private SafeBroadcastReceiver bReceiver = null;
    private boolean isStart = false;
    private WsController wsController = null;
    private SsdpDevice ssdpDevice = null;
    private ServerConf serverConf = null;
    private volatile boolean bCanceledTransferring = false;
    public int curWifiMgrState = 4;
    public int curWifiDirectMgrState = 5;

    public static NetworkStateUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkStateUtil();
        }
        return mInstance;
    }

    public synchronized void setState(State state, Bundle data) {
        this.state = state;
        Activity act = state.getActivity();
        if (act != null) {
            this.mApplicationContextRef = new WeakReference<>(act.getApplicationContext());
            this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
            if (this.bReceiver == null && this.isStart) {
                this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil.1
                    @Override // com.sony.imaging.app.synctosmartphone.commonUtil.SafeBroadcastReceiver
                    public void checkedOnReceive(Context context, Intent intent) {
                        NetworkStateUtil.this.handleEvent(intent);
                    }
                };
                this.bReceiver.registerThis(act, this.iFilter);
            }
        }
    }

    public synchronized void clearState(State state) {
        if (this.state.equals(state)) {
            this.mApplicationContextRef.clear();
            if (this.bReceiver != null) {
                this.bReceiver.unregisterThis(state.getActivity());
                this.bReceiver = null;
            }
            this.state = null;
        }
    }

    public synchronized State getState() {
        return this.state;
    }

    public synchronized Context getApplicationContext() {
        return this.mApplicationContextRef == null ? null : this.mApplicationContextRef.get();
    }

    public void start(Context appContext) {
        Log.d(TAG, "Start Wifi ");
        this.isStart = true;
        this.handler = new Handler();
        this.wifiManager = (WifiManager) this.state.getActivity().getSystemService("wifi");
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(appContext);
        this.delayedWifiEnabler = new Runnable() { // from class: com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil.2
            @Override // java.lang.Runnable
            public void run() {
                Log.v(NetworkStateUtil.TAG, "JB_Check : Wifi Connect by start.");
                Log.v(NetworkStateUtil.TAG, "Delayed Wi-Fi Enabler works.");
                NetworkStateUtil.this.isEnableActionFiltered = false;
                NetworkStateUtil.this.wifiManager.setWifiEnabled(true);
            }
        };
        this.delayedGoToFatalError = new Runnable() { // from class: com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil.3
            @Override // java.lang.Runnable
            public void run() {
                if (NetworkStateUtil.this.isRetrying) {
                    Log.d(NetworkStateUtil.TAG, "Retrying now, ignore goToFatalErrorState()");
                }
                if (NetworkStateUtil.this.curWifiMgrState == 1 || NetworkStateUtil.this.curWifiDirectMgrState == 1) {
                    Log.e(NetworkStateUtil.TAG, "State of WifiMgr or WiFiDirectMgr is still DISABLED");
                    NetworkStateUtil.this.setNextStateForID15(1, 0);
                } else {
                    Log.d(NetworkStateUtil.TAG, "State of WifiMgr or WiFiDirectMgr is not DISABLED, so ignore gotToFatalErrorState()");
                }
            }
        };
        initFlags();
        this.wsController = WsController.getInstance(this.state.getActivity());
        this.iFilter = new IntentFilter();
        this.iFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.DIRECT_STATE_CHANGED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil.4
            @Override // com.sony.imaging.app.synctosmartphone.commonUtil.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                NetworkStateUtil.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(this.state.getActivity(), this.iFilter);
        if (this.wifiManager.getWifiState() != 1) {
            this.isDisableActionFiltered = false;
            this.isInitialDisabling = true;
            Log.v("fakefragment", "JB_Check : Wifi disconnect by Starts faile. wifiManager.getWifiState: " + this.wifiManager.getWifiState());
            Log.v(TAG, "Disable Wi-Fi for initialization");
            this.wifiManager.setWifiEnabled(false);
            return;
        }
        this.isEnableActionFiltered = false;
        Log.v(TAG, "JB_Check : Wifi Connect by start (Clean start Wi-Fi). wifiManager.getWifiState: " + this.wifiManager.getWifiState());
        Log.v(TAG, "Clean start Wi-Fi");
        this.wifiManager.setWifiEnabled(true);
    }

    private void initFlags() {
        this.isInitialDisabling = false;
        this.isDisableActionFiltered = true;
        this.isEnableActionFiltered = true;
        this.isGroupCreateActionFiltered = true;
        this.isDisablingForFinish = false;
        this.isDdServerReady = false;
        this.isRetrying = false;
    }

    public void end() {
        Log.d(TAG, "End Wifi ");
        if (!isCanceledTransferring()) {
            long crntUtcTime = AutoSyncDataBaseUtil.getInstance().getUTCTime();
            setCanceledTransferring();
            if (this.numOfTotalSyncImage > 0) {
                if (this.numOfNotifySyncImage <= this.numOfTotalSyncImage) {
                    SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
                    SyncBackUpUtil.getInstance().setSyncError(4);
                    SyncBackUpUtil.getInstance().setSyncErrorSent(this.numOfNotifySyncImage - 1);
                    SyncKikiLogUtil.logNotConnect();
                } else {
                    SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
                    SyncBackUpUtil.getInstance().setSyncError(0);
                    SyncBackUpUtil.getInstance().setSyncErrorTotal(0);
                    SyncBackUpUtil.getInstance().setSyncErrorSent(0);
                }
            }
        }
        if (this.handler != null) {
            this.handler.removeCallbacks(this.delayedWifiEnabler);
            this.isDisablingForFinish = true;
        }
        if (this.bReceiver != null) {
            this.bReceiver.unregisterThis(this.state.getActivity());
            this.bReceiver = null;
        }
        AutoSyncDataBaseUtil.getInstance().endGetReservationFiles();
        invokeFinishProcess();
        this.isStart = false;
    }

    public int getNumberOfNotifySyncImage() {
        return this.numOfNotifySyncImage;
    }

    public int getNumOfTotalSyncImage() {
        return this.numOfTotalSyncImage;
    }

    public void setActPairingResult(int errCode) {
        if (StatusCode.OK.toInt() == errCode) {
            if (this.state != null) {
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                this.state.setNextState(ConstantsSync.REGISTERED_STATE, data);
                return;
            }
            return;
        }
        if (StatusCode.NOT_UNDER_PAIRING_MODE.toInt() == errCode) {
            setNextStateForID15(1, 0);
        } else if (this.state != null) {
            Bundle data2 = new Bundle();
            data2.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            data2.putInt(ConstantsSync.DIALOG_ID, 8);
            this.state.setNextState(ConstantsSync.DIALOG_STATE, data2);
        }
    }

    public void setGetInterfaceInformationResult(int errCode) {
        if (StatusCode.OK.toInt() != errCode) {
            if (this.bootMode == 0) {
                if (this.state != null) {
                    Bundle data = new Bundle();
                    data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    data.putInt(ConstantsSync.DIALOG_ID, 8);
                    this.state.setNextState(ConstantsSync.DIALOG_STATE, data);
                    return;
                }
                return;
            }
            setNextStateForID15(1, 0);
        }
    }

    public void setNotifySyncStatusResult(int errCode, String syncStatus, int totalCnt, int downloadedCnt, boolean isDonwloadedNull) {
        if (!isCanceledTransferring()) {
            if (StatusCode.OK.toInt() == errCode) {
                if (syncStatus.equals("start")) {
                    this.numOfNotifySyncImage = 1;
                    this.numOfTotalSyncImage = totalCnt;
                    if (totalCnt == 0) {
                        setNextStateForID15(0, downloadedCnt);
                        return;
                    }
                    if (this.state != null) {
                        Bundle data = new Bundle();
                        data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                        this.state.setNextState(ConstantsSync.TRANSFERRING_STATE, data);
                    }
                    ProgressManager.getInstance().requestNotify(ConstantsSync.TRANSFER_PROGRESS);
                    return;
                }
                if (syncStatus.equals("downloaded")) {
                    if (isDonwloadedNull) {
                        setNextStateForID15(0, downloadedCnt);
                        return;
                    } else {
                        this.numOfNotifySyncImage++;
                        ProgressManager.getInstance().requestNotify(ConstantsSync.TRANSFER_PROGRESS);
                        return;
                    }
                }
                if (syncStatus.equals("skip")) {
                    this.numOfNotifySyncImage += 2;
                    ProgressManager.getInstance().requestNotify(ConstantsSync.TRANSFER_PROGRESS);
                    return;
                } else {
                    if (syncStatus.equals("memoryFull")) {
                        setNextStateForID15(2, downloadedCnt);
                        return;
                    }
                    if (syncStatus.equals("batteryLow")) {
                        setNextStateForID15(3, downloadedCnt);
                        return;
                    } else if (syncStatus.equals("end")) {
                        setNextStateForID15(4, downloadedCnt);
                        return;
                    } else {
                        setNextStateForID15(8, downloadedCnt);
                        return;
                    }
                }
            }
            if (StatusCode.ANY.toInt() == errCode) {
                if (syncStatus.equals("start")) {
                    setNextStateForID15(1, downloadedCnt);
                    return;
                } else {
                    setNextStateForID15(4, downloadedCnt);
                    return;
                }
            }
            if (StatusCode.TIMEOUT.toInt() == errCode) {
                if (syncStatus.equals("start")) {
                    setNextStateForID15(1, downloadedCnt);
                    return;
                } else {
                    setNextStateForID15(4, downloadedCnt);
                    return;
                }
            }
            if (StatusCode.UNSUPPORTED_OPERATION.toInt() == errCode) {
                setNextStateForID15(8, downloadedCnt);
            } else if (StatusCode.CLIENT_NOT_PAIRED.toInt() == errCode) {
                setNextStateForID15(6, downloadedCnt);
            } else {
                setNextStateForID15(8, downloadedCnt);
            }
        }
    }

    public boolean isCanceledTransferring() {
        return this.bCanceledTransferring;
    }

    public void clearCanceledTransferring() {
        this.bCanceledTransferring = false;
        this.numOfNotifySyncImage = 0;
        this.numOfTotalSyncImage = 0;
    }

    public void setCanceledTransferring() {
        this.bCanceledTransferring = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        Log.v(TAG, "Wi-Fi Action in handleEvent(): " + action);
        Log.v(TAG, "JB_Check : Wi-Fi Action in handleEvent(): " + action);
        if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
            handleWifiStateChanged(intent.getIntExtra("wifi_state", 4));
            return;
        }
        if (WifiP2pManagerFactory.IWifiP2pManager.DIRECT_STATE_CHANGED_ACTION.equals(action)) {
            handleDirectStateChanged(intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_PREVIOUS_DIRECT_STATE, 5), intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_STATE, 5));
            return;
        }
        if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION.equals(action)) {
            handleGroupCreateSuccess((WifiP2pConfiguration) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG));
            return;
        }
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION.equals(action)) {
            Log.v("fakefragment", "JB_Check : STA_DISCONNECTED_ACTION is recieved. " + action + ", " + intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            handleStationDisconnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            return;
        }
        if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION.equals(action)) {
            handleGroupCreateFailure(intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, -1));
            return;
        }
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.PROVISIONING_REQUEST_ACTION.equals(action)) {
            handleProvisioningRequest((WifiP2pDeviceInfo) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_P2P_DEVICE));
        } else if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION.equals(action)) {
            handleGroupCreateSuccess((WifiP2pConfiguration) intent.getParcelableExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_DIRECT_CONFIG));
        }
    }

    private void handleWifiStateChanged(int state) {
        Log.v(TAG, "handleWifiStateChanged: " + state);
        this.curWifiMgrState = state;
        switch (state) {
            case 1:
                if (this.isDisableActionFiltered) {
                    Log.v(TAG, "Ignore WIFI_STATE_DISABLED");
                    return;
                }
                if (this.isInitialDisabling) {
                    this.isInitialDisabling = false;
                    this.isEnableActionFiltered = false;
                    Log.v(TAG, "Restarting Wi-Fi");
                    this.handler.postDelayed(this.delayedWifiEnabler, 5000L);
                    return;
                }
                if (this.isDisablingForFinish) {
                    Log.v(TAG, "Wi-Fi is Disabled for Finish");
                    return;
                }
                if (this.isRetrying) {
                    Log.e(TAG, "WifiStateChanged: WIFI_STATE_DISABLED");
                    delayedGoToFatalErrorState();
                    return;
                } else {
                    Log.v(TAG, "Retrying Wi-Fi Enable");
                    this.isRetrying = true;
                    this.handler.postDelayed(this.delayedWifiEnabler, 5000L);
                    return;
                }
            case 2:
                this.isDisableActionFiltered = false;
                return;
            case 3:
                if (this.isEnableActionFiltered) {
                    Log.v(TAG, "Ignore WIFI_STATE_ENABLED");
                    return;
                } else {
                    this.isRetrying = false;
                    this.wifiP2pManager.setDirectEnabled(true);
                    return;
                }
            case 4:
                if (this.isDisableActionFiltered) {
                    Log.v(TAG, "Ignore WIFI_STATE_UNKNOWN");
                    return;
                }
                if (this.isRetrying) {
                    Log.e(TAG, "WifiStateChanged: WIFI_STATE_UNKNOWN");
                    delayedGoToFatalErrorState();
                    return;
                } else {
                    Log.v(TAG, "Retrying Wi-Fi Enable");
                    this.isRetrying = true;
                    this.handler.postDelayed(this.delayedWifiEnabler, 5000L);
                    return;
                }
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextStateForID15(int errorCode, int numOfSentImages) {
        if (!isCanceledTransferring()) {
            setCanceledTransferring();
            switch (errorCode) {
                case 0:
                    SyncKikiLogUtil.logTransferSuccess();
                    break;
                case 1:
                    SyncKikiLogUtil.logNotConnect();
                    break;
                case 2:
                    SyncKikiLogUtil.logSmartphoneMediaFull();
                    break;
                case 3:
                    SyncKikiLogUtil.logNotConnect();
                    break;
                case 4:
                    SyncKikiLogUtil.logNotConnect();
                    break;
                case 6:
                    SyncKikiLogUtil.logNotConnect();
                    break;
                case 8:
                    SyncKikiLogUtil.logNotConnect();
                    break;
            }
            if (this.state != null) {
                long crntUtcTime = AutoSyncDataBaseUtil.getInstance().getUTCTime();
                SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
                SyncBackUpUtil.getInstance().setSyncError(errorCode);
                SyncBackUpUtil.getInstance().setSyncErrorSent(numOfSentImages);
                int totalCnt = SyncBackUpUtil.getInstance().getSyncErrorTotal(0);
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                data.putInt(ConstantsSync.DIALOG_ID, 6);
                data.putInt(ConstantsSync.SYNC_TOTAL_FILE, totalCnt);
                data.putInt(ConstantsSync.SYNC_SENT_FILE, numOfSentImages);
                this.state.setNextState(ConstantsSync.DIALOG_STATE, data);
            }
        }
    }

    private void handleDirectStateChanged(int previousState, int currentState) {
        Log.v(TAG, "handleDirectStateChanged: " + currentState);
        this.curWifiDirectMgrState = currentState;
        switch (currentState) {
            case 1:
                if (previousState != 3) {
                    if (this.isDisableActionFiltered) {
                        Log.v(TAG, "Ignore DIRECT_STATE_DISABLED");
                        return;
                    }
                    if (this.isDisablingForFinish) {
                        Log.v(TAG, "Wi-Fi Direct is Disabled for Finish");
                        return;
                    }
                    if (!this.isInitialDisabling) {
                        Log.e(TAG, "DirectStateChanged: DIRECT_STATE_DISABLED");
                        if (!this.isRetrying) {
                            delayedGoToFatalErrorState();
                            return;
                        } else {
                            Log.w(TAG, "now Retrying Enable Wi-Fi. maybe Eanable Wi-Fi Direct soon after Enabled Wi-Fi.");
                            return;
                        }
                    }
                    return;
                }
                return;
            case 2:
            case 4:
            default:
                return;
            case 3:
                this.isRetrying = false;
                if (this.isEnableActionFiltered) {
                    Log.v(TAG, "Ignore DIRECT_STATE_ENABLED");
                    return;
                } else {
                    startGroupOwner();
                    return;
                }
            case 5:
                if (this.isDisableActionFiltered) {
                    Log.v(TAG, "Ignore DIRECT_STATE_UNKNOWN");
                    return;
                } else {
                    Log.e(TAG, "DirectStateChanged: DIRECT_STATE_UNKNOWN");
                    delayedGoToFatalErrorState();
                    return;
                }
        }
    }

    private void handleGroupCreateSuccess(WifiP2pConfiguration config) {
        this.isRetrying = false;
        if (this.isGroupCreateActionFiltered) {
            Log.v(TAG, "Ignore GROUP_CREATE_SUCCESS");
            return;
        }
        if (!this.isDdServerReady) {
            this.wifiP2pManager.saveConfiguration();
            WifiP2pDeviceInfo dDevice = this.wifiP2pManager.getMyDevice();
            WifiParameters wifiParameters = WifiParameters.getInstance();
            wifiParameters.setSSID(config.getSsid());
            wifiParameters.setPASSWD(config.getPreSharedKey());
            wifiParameters.setDeviceName(dDevice.getName());
            Log.v(TAG, "SSID       = " + wifiParameters.getSSID());
            Log.v(TAG, "PASSWD     = " + wifiParameters.getPASSWD());
            Log.v(TAG, "DeviceName = " + wifiParameters.getDeviceName());
            Log.v(TAG, "Start ssdp");
            DevLog.enable(true);
            try {
                try {
                    this.serverConf = new ServerConf();
                    if (this.bootMode == 0) {
                        this.serverConf.deviceDescription = new AutoSyncDeviceDescription(this.state.getActivity(), DEVICE_DESCRIPTION_PAIRING_FILE).getDescription();
                    } else {
                        this.serverConf.deviceDescription = new AutoSyncDeviceDescription(this.state.getActivity(), DEVICE_DESCRIPTION_SYNC_FILE).getDescription();
                    }
                    this.serverConf.descriptionPath = "/data/data/" + this.state.getActivity().getPackageName();
                    this.serverConf.descriptionFile = "/dd.xml";
                    this.serverConf.retry = 3;
                    this.ssdpDevice = new SsdpDevice(this.state.getActivity(), this.serverConf, this);
                    if (this.ssdpDevice == null || !this.ssdpDevice.startServer()) {
                        Log.e(TAG, "Failed start DD Server Service");
                        setNextStateForID15(1, 0);
                    } else {
                        Log.v(TAG, "SUCCESS Start DD Server Service");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                    this.ssdpDevice = null;
                    if (this.ssdpDevice == null || !this.ssdpDevice.startServer()) {
                        Log.e(TAG, "Failed start DD Server Service");
                        setNextStateForID15(1, 0);
                    } else {
                        Log.v(TAG, "SUCCESS Start DD Server Service");
                    }
                }
            } catch (Throwable th) {
                if (this.ssdpDevice == null || !this.ssdpDevice.startServer()) {
                    Log.e(TAG, "Failed start DD Server Service");
                    setNextStateForID15(1, 0);
                } else {
                    Log.v(TAG, "SUCCESS Start DD Server Service");
                }
                throw th;
            }
        }
        Log.v(TAG, "GROUP_CREATE_SUCCESS");
        if (!this.wsController.start()) {
            Log.e(TAG, "Failed Binding Web Server Service");
            setNextStateForID15(1, 0);
        } else {
            Log.v(TAG, "SUCCESS Binding Web Server Service");
        }
    }

    private void handleGroupCreateFailure(int err) {
        if (this.isGroupCreateActionFiltered) {
            Log.v(TAG, "Ignore GROUP_CREATE_FAILURE");
        } else {
            if (this.isRetrying) {
                Log.v(TAG, "Already retried but GROUP_CREATE_FAILURE");
                return;
            }
            Log.v(TAG, "Retrying group creation");
            this.isRetrying = true;
            startGroupOwner();
        }
    }

    private void handleStationDisconnected(String addr) {
        Log.v(TAG, "handleStationDisconnected : addr = " + addr);
        boolean layoutStatus = TransferringStateSync.getInstance().getTransferringLayoutStatus();
        if (this.bootMode == 0) {
            this.wsController.unbind();
            if (!this.wsController.start()) {
                Log.e(TAG, "Failed Binding Web Server Service");
                return;
            } else {
                if (this.state != null) {
                    Bundle data = new Bundle();
                    data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    this.state.setNextState(ConstantsSync.REGISTRATION_WAITING_STATE, data);
                    return;
                }
                return;
            }
        }
        if (layoutStatus) {
            Log.e(TAG, "Disconnected during transffering");
            int errorCode = StatusCode.OK.toInt();
            int transerredCnt = getNumberOfNotifySyncImage() - 1;
            AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
            if (!isCanceledTransferring()) {
                dbUtil.endGetReservationFiles();
            }
            setNotifySyncStatusResult(errorCode, "end", 0, transerredCnt, false);
        }
    }

    private void handleStationConnected(String addr) {
        Log.v(TAG, "handleStationConnected : addr = " + addr);
        boolean layoutStatus = ConnectingStateSync.getInstance().getConnectingLayoutStatus();
        if (!layoutStatus && this.state != null) {
            Bundle data = new Bundle();
            data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            this.state.setNextState(ConstantsSync.CONNECTING_STATE, data);
        }
        Log.v(TAG, "handleStationConnected : setNextState = ");
    }

    private void handleProvisioningRequest(WifiP2pDeviceInfo device) {
        Log.v(TAG, "TargetDeviceName: " + device.getName());
        Log.v(TAG, "TargetDeviceAddress: " + device.getP2PDevAddress());
    }

    private void invokeFinishProcess() {
        if (this.wsController != null) {
            this.wsController.unbind();
        }
        if (this.isDdServerReady) {
            if (this.ssdpDevice != null) {
                this.ssdpDevice.stopServer();
            }
            this.isDdServerReady = false;
        }
        if (this.wifiP2pManager != null) {
            this.wifiP2pManager.setDirectEnabled(false);
        }
        if (this.wifiManager != null) {
            Log.v(TAG, "JB_Check : Wifi disconnect by invokeFinishProcess.");
            this.wifiManager.setWifiEnabled(false);
        }
    }

    @Override // com.sony.scalar.lib.ssdpdevice.SsdpDevice.DdStatusListener
    public void notifyStatus(DdStatus status, int opt) {
        if (status == DdStatus.OK) {
            if (!this.isDisablingForFinish) {
                Log.v(TAG, "DD Server is ready");
                this.isDdServerReady = true;
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                this.state.setNextState(ConstantsSync.REGISTRATION_WAITING_STATE, data);
                clearState(this.state);
                return;
            }
            return;
        }
        if (DdStatus.ERROR_SERVICE_START.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_SERVICE_START");
            return;
        }
        if (DdStatus.ERROR_FROM_SERVICE.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_FROM_SERVICE");
            return;
        }
        if (DdStatus.ERROR_NETWORK.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_NETWORK");
        } else if (DdStatus.ERROR_SERVICE_NOT_FOUND.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_SERVICE_NOT_FOUND");
        } else if (DdStatus.ERROR_DEVICE_NAME.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_DEVICE_NAME");
        }
    }

    private void startGroupOwner() {
        this.isGroupCreateActionFiltered = false;
        WifiP2pDeviceInfo dDevice = this.wifiP2pManager.getMyDevice();
        if (dDevice == null) {
            try {
                Log.v(TAG, "Waiting for DirectDevice");
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException");
            }
            dDevice = this.wifiP2pManager.getMyDevice();
        }
        List<WifiP2pConfiguration> confList = this.wifiP2pManager.getConfigurations();
        if (confList.size() > 0) {
            Log.w(TAG, "#### Use device info ####");
            this.wifiP2pManager.setSsidPostfix(ScalarWifiInfo.getProductCode() + ":" + dDevice.getName());
            this.wifiP2pManager.startGo(confList.get(confList.size() - 1).getNetworkId());
            return;
        }
        String deviceName = getDeviceName();
        String modelName = ScalarProperties.getString("model.name");
        Log.v(TAG, "deviceName:" + deviceName + "  modelName:" + modelName);
        this.wifiP2pManager.setSsidPostfix(ScalarWifiInfo.getProductCode() + ":" + deviceName);
        this.wifiP2pManager.setModelName(modelName);
        this.wifiP2pManager.setDeviceName(deviceName);
        this.wifiP2pManager.startGo(-2);
    }

    private String getDeviceName() {
        String name;
        if (8 <= Environment.getVersionPfAPI()) {
            int deviceNameValid = ScalarProperties.getInt("net.service.device.name.valid");
            if (deviceNameValid == 0) {
                name = ScalarProperties.getString("model.name");
            } else {
                name = ScalarProperties.getString("net.service.device.name");
            }
        } else {
            name = ScalarProperties.getString("model.name");
        }
        Log.v(TAG, "getDeviceName:" + name);
        return name;
    }

    private void delayedGoToFatalErrorState() {
        Log.w(TAG, "Go to Fatal Error State after few sec.");
        this.handler.postDelayed(this.delayedGoToFatalError, 5000L);
    }
}
