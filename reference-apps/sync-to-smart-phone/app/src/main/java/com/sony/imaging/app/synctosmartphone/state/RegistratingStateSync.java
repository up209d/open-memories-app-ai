package com.sony.imaging.app.synctosmartphone.state;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncKikiLogUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class RegistratingStateSync extends State {
    private static final String TAG = RegistratingStateSync.class.getSimpleName();
    private int bootMode;
    private NetworkStateUtil networkStateUtil;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        boolean Display_LensInfo;
        super.onResume();
        int numOfReservedImages = 0;
        boolean m_iTransferNow = false;
        boolean m_iAutoTranfer = false;
        Bundle data = this.data;
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        if (1 == this.bootMode) {
            BaseApp baseApp = (BaseApp) getActivity();
            Intent baseIntent = baseApp.getIntent();
            String prmSuspendFactor = baseIntent.getStringExtra("param_off_factor");
            Log.d(TAG, "onCreate param_off_factor=[" + prmSuspendFactor + "]");
            AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
            long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
            int autoTransferStartNum = SyncBackUpUtil.getInstance().getImageStartNum(1);
            boolean flg = SyncBackUpUtil.getInstance().getRegister(false);
            if (!dbUtil.isMediaExist()) {
                SyncKikiLogUtil.logNotInsertMemoryCard();
                SyncBackUpUtil.getInstance().setSyncError(0);
                SyncBackUpUtil.getInstance().setSyncDate(0L);
                SyncBackUpUtil.getInstance().setSyncErrorTotal(0);
                SyncBackUpUtil.getInstance().setSyncErrorSent(0);
                baseApp.finish(false);
                return;
            }
            boolean bret = dbUtil.isNeedUpdateDatabase();
            if (bret) {
                dbUtil.updateDatabase(false);
            } else {
                dbUtil.importDatabase();
            }
            numOfReservedImages = dbUtil.getNumberOfTransferReservationFiles(lastStartTime);
            Log.d(TAG, "Auto Transfer TransferNum:" + numOfReservedImages);
            if (prmSuspendFactor != null) {
                if (!prmSuspendFactor.equals("OFF_BY_KEY") && !prmSuspendFactor.equals("OFF_BY_UM")) {
                    baseApp.finish(false);
                    return;
                }
                Log.d(TAG, "Auto Transfer TransferNum:" + numOfReservedImages);
                if (autoTransferStartNum > numOfReservedImages) {
                    SyncBackUpUtil.getInstance().setSyncError(0);
                    SyncBackUpUtil.getInstance().setSyncDate(0L);
                    SyncBackUpUtil.getInstance().setSyncErrorTotal(0);
                    SyncBackUpUtil.getInstance().setSyncErrorSent(0);
                    baseApp.finish(false);
                    return;
                }
                m_iAutoTranfer = true;
            } else {
                m_iTransferNow = true;
                Log.d(TAG, "Start Transfer by TransferNow");
            }
            if (!flg || ((0 == lastStartTime && !m_iTransferNow) || numOfReservedImages == 0)) {
                SyncBackUpUtil.getInstance().setSyncError(0);
                SyncBackUpUtil.getInstance().setSyncDate(0L);
                SyncBackUpUtil.getInstance().setSyncErrorTotal(0);
                SyncBackUpUtil.getInstance().setSyncErrorSent(0);
                baseApp.finish(false);
                return;
            }
            SyncBackUpUtil.getInstance().setSyncErrorTotal(numOfReservedImages);
        }
        int batteryLevel = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
        Log.d(TAG, " BatteryObserver.TAG_PLUGGED = " + plugged);
        if (Settings.System.getInt(getActivity().getContentResolver(), "airplane_mode_on", 0) == 1) {
            SyncBackUpUtil.getInstance().setSyncError(9);
        } else if (batteryLevel < 10 && 1 != plugged && 1 == this.bootMode) {
            SyncKikiLogUtil.logCameraBatExhaused();
            SyncBackUpUtil.getInstance().setSyncError(3);
        } else {
            SyncBackUpUtil.getInstance().setSyncError(0);
        }
        int syncErrorCode = SyncBackUpUtil.getInstance().getSyncError(0);
        if (syncErrorCode != 0) {
            SyncBackUpUtil.getInstance().setSyncDate(AutoSyncDataBaseUtil.getInstance().getUTCTime());
            SyncBackUpUtil.getInstance().setSyncErrorTotal(numOfReservedImages);
            SyncBackUpUtil.getInstance().setSyncErrorSent(0);
            Bundle nextData = new Bundle();
            nextData.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            nextData.putInt(ConstantsSync.DIALOG_ID, 6);
            nextData.putInt(ConstantsSync.SYNC_TOTAL_FILE, numOfReservedImages);
            nextData.putInt(ConstantsSync.SYNC_SENT_FILE, 0);
            setNextState(ConstantsSync.DIALOG_STATE, nextData);
            return;
        }
        if (m_iAutoTranfer) {
            String version = ScalarProperties.getString("version.platform");
            int pfVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
            int apiVersion = Integer.parseInt(version.substring(version.indexOf(".") + 1));
            int uiFastSuspendSupport = ScalarProperties.getInt("ui.fast.suspend.supported");
            if (pfVersion <= 2 && apiVersion < 6) {
                Display_LensInfo = true;
            } else if (uiFastSuspendSupport == 0) {
                Display_LensInfo = true;
            } else {
                Display_LensInfo = false;
            }
            if (Display_LensInfo) {
                boolean m_iFirstTime = SyncBackUpUtil.getInstance().getAppFirstTime(false);
                if (!m_iFirstTime) {
                    SyncBackUpUtil.getInstance().setAppFirstTime(true);
                    data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    setNextState(ConstantsSync.TRANSFERRING_GUIDE_STATE, data);
                    return;
                }
            }
        }
        openLayout(ConstantsSync.REGISTRATING_LAYOUT, data);
        this.networkStateUtil = NetworkStateUtil.getInstance();
        this.networkStateUtil.setState(this, data);
        this.networkStateUtil.start(getActivity().getApplicationContext());
        this.networkStateUtil.clearCanceledTransferring();
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(ConstantsSync.REGISTRATING_LAYOUT);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        if (this.bootMode == 0) {
            invokeFinishProcess();
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish(false);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        if (this.bootMode == 0) {
            invokeFinishProcess();
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish(false);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        if (this.bootMode == 0) {
            invokeFinishProcess();
            Bundle data = new Bundle();
            data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            setNextState(ConstantsSync.REGISTRATION_SETTING_STATE, data);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        if (1 == this.bootMode) {
            invokeFinishProcess();
            long crntUtcTime = AutoSyncDataBaseUtil.getInstance().getUTCTime();
            int totalCnt = AutoSyncDataBaseUtil.getInstance().getNumberOfTransferReservationFiles(crntUtcTime);
            SyncBackUpUtil.getInstance().setSyncError(ConstantsSync.CAUTION_HALT_DONE);
            SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
            SyncBackUpUtil.getInstance().setSyncErrorTotal(totalCnt);
            SyncBackUpUtil.getInstance().setSyncErrorSent(0);
            SyncKikiLogUtil.logTransferHalt();
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish(false);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        Log.d(TAG, "turnedDial1ToRight");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.d(TAG, "pushedPlayBackKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Log.d(TAG, "pushedFnKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    private void invokeFinishProcess() {
        NetworkStateUtil.getInstance().end();
    }
}
