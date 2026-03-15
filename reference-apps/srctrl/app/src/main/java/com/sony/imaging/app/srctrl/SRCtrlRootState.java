package com.sony.imaging.app.srctrl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo;
import com.sony.imaging.app.srctrl.network.dd.SRCtrlDeviceDescription;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pConfiguration;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pDeviceInfo;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.shooting.state.MfAssistStateEx;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationLiveviewFrameInfo;
import com.sony.imaging.app.srctrl.webapi.util.WsController;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.scalar.lib.ssdpdevice.v1_1.DdStatus;
import com.sony.scalar.lib.ssdpdevice.v1_1.DevLog;
import com.sony.scalar.lib.ssdpdevice.v1_1.ServerConf;
import com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice;
import com.sony.scalar.sysnetutil.ScalarWifiInfo;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class SRCtrlRootState extends ContainerState implements SsdpDevice.DdStatusListener {
    public static final String APP_NETWORK = "APP_NETWORK";
    public static final String APP_PLAYBACK = "APP_PLAYBACK";
    public static final String APP_SHOOTING = "APP_SHOOTING";
    public static final int DELAY_WIFI_GO_TO_FATAL_ERR = 5000;
    private static final String DEVICE_DESCRIPTION_FILE = "/scalarwebapi_dd.xml";
    private static final String DEVICE_DESCRIPTION_FILEPATH = "/data/data";
    private static final int DEVICE_DESCRIPTION_PORT = 64321;
    private static final String DEVICE_DESCRIPTION_TEMPLATE_FILE;
    public static final String FATAL_ERROR = "FATAL_ERROR";
    private static final String TAG = SRCtrlRootState.class.getSimpleName();
    private static Thread mStopDdServerThread;
    public static WsController wsController;
    private SafeBroadcastReceiver bReceiver;
    private Runnable delayedGoToFatalError;
    private Runnable delayedWifiEnabler;
    private Handler handler;
    private IntentFilter iFilter;
    private boolean isDisableActionFiltered;
    private boolean isDisablingForFinish;
    private boolean isEnableActionFiltered;
    private boolean isGroupCreateActionFiltered;
    private boolean isInitialDisabling;
    private boolean isRetrying;
    private ServerConf serverConf;
    private SsdpDevice ssdpDevice;
    private StateHandle stateHandle;
    private WifiManager wifiManager;
    private WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager;
    public int curWifiMgrState = 4;
    public int curWifiDirectMgrState = 5;

    static {
        DEVICE_DESCRIPTION_TEMPLATE_FILE = PfBugAvailability.encodeAtPlay ? "scalarwebapi_noAvContent_template_dd.xml" : "scalarwebapi_template_dd.xml";
        mStopDdServerThread = null;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (Settings.System.getInt(getActivity().getContentResolver(), "airplane_mode_on", 0) == 1) {
            Log.v(TAG, "AIRPLANE MODE IS ON. FORCE EXIT APPLICATION WITHOUT CONFIRMATION !!!!");
            ((BaseApp) getActivity()).finish(false);
            return;
        }
        this.handler = new Handler();
        this.wifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.wifiP2pManager = WifiP2pManagerFactory.getInstance(getActivity().getApplicationContext());
        this.delayedWifiEnabler = new Runnable() { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.1
            @Override // java.lang.Runnable
            public void run() {
                Log.v(SRCtrlRootState.TAG, "Delayed Wi-Fi Enabler works.");
                SRCtrlRootState.this.wifiManager.setWifiEnabled(true);
            }
        };
        this.delayedGoToFatalError = new Runnable() { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.2
            @Override // java.lang.Runnable
            public void run() {
                if (SRCtrlRootState.this.isRetrying) {
                    Log.d(SRCtrlRootState.TAG, "Retrying now, ignore goToFatalErrorState()");
                }
                if (SRCtrlRootState.this.curWifiMgrState == 1 || SRCtrlRootState.this.curWifiDirectMgrState == 1) {
                    Log.e(SRCtrlRootState.TAG, "State of WifiMgr or WiFiDirectMgr is still DISABLED");
                    SRCtrlRootState.this.goToFatalErrorState();
                } else {
                    Log.d(SRCtrlRootState.TAG, "State of WifiMgr or WiFiDirectMgr is not DISABLED, so ignore gotToFatalErrorState()");
                }
            }
        };
        initFlags();
        StateController.getInstance().init();
        this.serverConf = new ServerConf();
        wsController = new WsController(getActivity());
        this.iFilter = new IntentFilter();
        this.iFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.DIRECT_STATE_CHANGED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_SUCCESS_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
        this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
        this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.3
            @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
            public void checkedOnReceive(Context context, Intent intent) {
                SRCtrlRootState.this.handleEvent(intent);
            }
        };
        this.bReceiver.registerThis(getActivity(), this.iFilter);
        if (this.wifiManager.getWifiState() != 1) {
            this.isDisableActionFiltered = false;
            this.isInitialDisabling = true;
            Log.v(TAG, "Disable Wi-Fi for initialization");
            this.wifiManager.setWifiEnabled(false);
        } else {
            this.isEnableActionFiltered = false;
            Log.v(TAG, "Clean start Wi-Fi");
            this.wifiManager.setWifiEnabled(true);
        }
        StateController.getInstance().setInitialWifiSetup(true);
        StateController.getInstance().setFatalError(false);
        this.stateHandle = addChildState(APP_NETWORK, (Bundle) null);
        setKeysForLensCaution();
        DigitalImagingDeviceInfo digitalImagingDeviceInfo = new DigitalImagingDeviceInfo(getActivity().getApplicationContext());
        digitalImagingDeviceInfo.createDigitalImagingDeviceInfoFile("/data/data/" + getActivity().getPackageName());
    }

    private void initFlags() {
        this.isInitialDisabling = false;
        this.isDisableActionFiltered = true;
        this.isEnableActionFiltered = true;
        this.isGroupCreateActionFiltered = true;
        this.isDisablingForFinish = false;
        this.isRetrying = false;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.delayedWifiEnabler != null) {
            this.handler.removeCallbacks(this.delayedWifiEnabler);
        }
        this.isDisablingForFinish = true;
        if (this.bReceiver != null) {
            this.bReceiver.unregisterThis(getActivity());
        }
        invokeFinishProcess();
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "intent: action=" + action);
        if (!StateController.getInstance().isFatalError()) {
            Log.v(TAG, "Wi-Fi Action in handleEvent(): " + action);
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
                handleStationDisconnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            } else if (WifiP2pManagerFactory.IWifiP2pManager.GROUP_CREATE_FAILURE_ACTION.equals(action)) {
                handleGroupCreateFailure(intent.getIntExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_ERROR_CODE, -1));
            } else if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
                handleStationConnected(intent.getStringExtra(WifiP2pManagerFactory.IWifiP2pManager.EXTRA_STA_ADDR));
            }
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
        Log.d(TAG, "in handleGroupCreateSucccess");
        if (mStopDdServerThread != null) {
            try {
                mStopDdServerThread.join();
            } catch (Exception e) {
                Log.e(TAG, "Exception at mStopDdServerThread.join()= " + e.toString());
            }
        }
        if (!StateController.getInstance().isDdServerReady()) {
            DevLog.enable(true);
            try {
                String ssid = config.getSsid();
                this.serverConf.deviceDescription = new SRCtrlDeviceDescription(getActivity(), DEVICE_DESCRIPTION_TEMPLATE_FILE, ssid).getDescription();
                if (this.serverConf.deviceDescription != null) {
                    this.serverConf.descriptionPath = "/data/data/" + getActivity().getPackageName();
                    this.serverConf.descriptionFile = DEVICE_DESCRIPTION_FILE;
                    this.serverConf.ssdpPort = DEVICE_DESCRIPTION_PORT;
                    this.serverConf.retry = 3;
                    this.ssdpDevice = SsdpDevice.getInstance();
                    this.ssdpDevice.initialize(getActivity().getApplicationContext());
                } else {
                    this.ssdpDevice = null;
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.toString());
                e2.printStackTrace();
                this.ssdpDevice = null;
            }
            if (this.ssdpDevice == null || !this.ssdpDevice.enqueueStartServer(this.serverConf, this)) {
                Log.e(TAG, "Failed Binding DD Server Service");
                goToFatalErrorState();
            } else {
                Log.v(TAG, "Start DD Server Service");
            }
        }
        if (!wsController.start()) {
            Log.e(TAG, "Failed Binding Web Server Service");
            goToFatalErrorState();
        }
    }

    private void handleGroupCreateFailure(int err) {
        if (this.isGroupCreateActionFiltered) {
            Log.v(TAG, "Ignore GROUP_CREATE_FAILURE");
        } else {
            if (this.isRetrying) {
                replaceChildState(this.stateHandle, FATAL_ERROR, (Bundle) null);
                return;
            }
            Log.v(TAG, "Retrying group creation");
            this.isRetrying = true;
            startGroupOwner();
        }
    }

    private void handleStationDisconnected(String addr) {
        Log.v(TAG, "SRCtrlRootState#handleStationDisconnected() was called.");
        if (CameraOperationLiveviewFrameInfo.get().booleanValue()) {
            CameraOperationLiveviewFrameInfo.set(false);
        }
        wsController.unbind();
        StateController stateController = StateController.getInstance();
        if (!wsController.start()) {
            Log.e(TAG, "Failed Binding Web Server Service");
            goToFatalErrorState();
            return;
        }
        StateController.AppCondition appCondition = stateController.getAppCondition();
        Log.v(TAG, "Current state= " + stateController.getAppCondition().name());
        if ((StateController.AppCondition.SHOOTING_INHIBIT.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_INHIBIT.equals(appCondition)) && (stateController.getState() instanceof MfAssistStateEx)) {
            Log.v(TAG, "Change the state to network state now.");
            stateController.changeToNetworkState();
            return;
        }
        if (StateController.AppCondition.SHOOTING_INHIBIT.equals(appCondition) || StateController.AppCondition.SHOOTING_LOCAL.equals(appCondition) || StateController.AppCondition.SHOOTING_STILL_SAVING.equals(appCondition) || StateController.AppCondition.SHOOTING_STILL_POST_PROCESSING.equals(appCondition) || StateController.AppCondition.SHOOTING_REMOTE.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_INHIBIT.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_REC.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_REC_MENU.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_WAIT_REC_START.equals(appCondition) || StateController.AppCondition.SHOOTING_MOVIE_WAIT_REC_STOP.equals(appCondition)) {
            Log.v(TAG, "Change the state to network state later.");
            stateController.setGoBackFlag(true);
        } else if (!StateController.AppCondition.PREPARATION.equals(stateController.getAppCondition())) {
            Log.v(TAG, "Change the state to network state now.");
            stateController.changeToNetworkState();
        }
    }

    private void handleStationConnected(String addr) {
        Log.v(TAG, "SRCtrlRootState#handleStationConnected() was called.");
        StateController stateController = StateController.getInstance();
        stateController.setGoBackFlag(false);
    }

    private void invokeFinishProcess() {
        StateController stateController = StateController.getInstance();
        stateController.onPauseCalled();
        if (wsController != null) {
            wsController.unbind();
        }
        if (stateController.isDdServerReady()) {
            Log.v(TAG, "Unbinding DD Server");
            mStopDdServerThread = new Thread() { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.4
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    final CountDownLatch latch = new CountDownLatch(1);
                    try {
                        Log.d(SRCtrlRootState.TAG, "start ssdpDevice stopServer.");
                        if (SRCtrlRootState.this.ssdpDevice != null) {
                            SRCtrlRootState.this.ssdpDevice.enqueueStopServer(new SsdpDevice.DdStatusListener() { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.4.1
                                @Override // com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice.DdStatusListener
                                public void notifyStatus(DdStatus arg0, int arg1) {
                                    Log.i(SRCtrlRootState.TAG, "SsdpDevice StopServer notify. DbStatus:" + arg0 + " opt:" + arg1);
                                    latch.countDown();
                                }
                            });
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(SRCtrlRootState.TAG, "IllegalArgumentException at unbinding DDServer");
                    } catch (Exception e2) {
                        Log.e(SRCtrlRootState.TAG, "Exception at unbinding DDServer. error = " + e2.toString());
                    }
                    Log.d(SRCtrlRootState.TAG, "finish ssdpDevice await.");
                    try {
                        latch.await();
                    } catch (InterruptedException e3) {
                        Log.e(SRCtrlRootState.TAG, "latch.await() error = " + e3.toString());
                    }
                    SRCtrlRootState.this.ssdpDevice = null;
                    Log.d(SRCtrlRootState.TAG, "finish ssdpDevice stopServer.");
                }
            };
            Log.d(TAG, "finish ssdpDevice start.");
            mStopDdServerThread.start();
            Log.d(TAG, "finish ssdpDevice join.");
            long startTime = System.currentTimeMillis();
            try {
                mStopDdServerThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "StopDdServerThread.join() error = " + e.toString());
            }
            Log.d(TAG, "finish ssdpDevice join end. blocking time = [" + (System.currentTimeMillis() - startTime) + "]ms");
        }
        if (this.wifiP2pManager != null) {
            this.wifiP2pManager.setDirectEnabled(false);
        }
        if (this.wifiManager != null) {
            this.wifiManager.setWifiEnabled(false);
        }
    }

    @Override // com.sony.scalar.lib.ssdpdevice.v1_1.SsdpDevice.DdStatusListener
    public void notifyStatus(DdStatus status, int opt) {
        if (status == DdStatus.OK) {
            if (!this.isDisablingForFinish) {
                Log.v(TAG, "DD Server is ready");
                StateController.getInstance().setDdServerReady(true);
                return;
            }
            return;
        }
        if (status == DdStatus.FINISHED) {
            Log.v(TAG, "DD Server Status notify: FINISHED");
            return;
        }
        if (status == DdStatus.ON_GOING) {
            Log.v(TAG, "DD Server Status notify: ON_GOING");
            return;
        }
        if (status == DdStatus.SKIPPED) {
            Log.v(TAG, "DD Server Status notify: SKIPPED");
            return;
        }
        if (DdStatus.ERROR_SERVICE_START.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_SERVICE_START");
        } else if (DdStatus.ERROR_FROM_SERVICE.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_FROM_SERVICE");
        } else if (DdStatus.ERROR_NETWORK.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_NETWORK");
        } else if (DdStatus.ERROR_SERVICE_NOT_FOUND.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_SERVICE_NOT_FOUND");
        } else if (DdStatus.ERROR_DEVICE_NAME.equals(status)) {
            Log.e(TAG, "DD Server Error: ERROR_DEVICE_NAME");
        }
        replaceChildState(this.stateHandle, FATAL_ERROR, (Bundle) null);
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
            String deviceName = dDevice.getName();
            NetworkRootState.setMyDeviceName(deviceName);
            this.wifiP2pManager.setSsidPostfix(ScalarWifiInfo.getProductCode() + ":" + deviceName);
            this.wifiP2pManager.startGo(confList.get(confList.size() - 1).getNetworkId());
            return;
        }
        String deviceName2 = getDeviceName();
        String modelName = ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME);
        Log.v(TAG, "deviceName:" + deviceName2 + "  modelName:" + modelName);
        NetworkRootState.setMyDeviceName(deviceName2);
        this.wifiP2pManager.setSsidPostfix(ScalarWifiInfo.getProductCode() + ":" + deviceName2);
        this.wifiP2pManager.setModelName(modelName);
        this.wifiP2pManager.setDeviceName(deviceName2);
        this.wifiP2pManager.startGo(-2);
    }

    private String getDeviceName() {
        String name;
        if (8 <= Environment.getVersionPfAPI()) {
            int deviceNameValid = ScalarProperties.getInt("net.service.device.name.valid");
            if (deviceNameValid == 0) {
                name = ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME);
            } else {
                name = ScalarProperties.getString("net.service.device.name");
            }
        } else {
            name = ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME);
        }
        Log.v(TAG, "getDeviceName:" + name);
        return name;
    }

    private void setKeysForLensCaution() {
        CautionUtilityClass.getInstance().setDispatchKeyEvent(1399, new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.srctrl.SRCtrlRootState.5
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
            public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
                CautionUtilityClass.getInstance().executeTerminate();
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
            public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
                CautionUtilityClass.getInstance().executeTerminate();
                return 1;
            }
        });
    }

    private void delayedGoToFatalErrorState() {
        Log.w(TAG, "Go to Fatal Error State after few sec.");
        this.handler.postDelayed(this.delayedGoToFatalError, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToFatalErrorState() {
        StateController.getInstance().setFatalError(true);
        replaceChildState(this.stateHandle, FATAL_ERROR, (Bundle) null);
    }
}
