package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncKikiLogUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.util.ApoWrapper;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class RegistrationWaitingStateSync extends State {
    private static final String REGISTRATION_WAITING_LAYOUT = "RegistrationWaitingLayoutSync";
    private static final String TAG = RegistrationWaitingStateSync.class.getSimpleName();
    private int bootMode;
    private NetworkStateUtil networkStateUtil;
    private Timer timer = null;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        openLayout("RegistrationWaitingLayoutSync", data);
        this.networkStateUtil = NetworkStateUtil.getInstance();
        this.networkStateUtil.setState(this, data);
        if (this.bootMode == 1) {
            this.timer = new Timer();
            TimerTask timerTask = new cancelTimerTask();
            this.timer.schedule(timerTask, 40000L);
        }
        Log.d(TAG, "onResume = onResume");
    }

    /* loaded from: classes.dex */
    private class cancelTimerTask extends TimerTask {
        private cancelTimerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            Log.i(RegistrationWaitingStateSync.TAG, "cancelTimerTask");
            RegistrationWaitingStateSync.this.invokeFinishProcess();
            Bundle data = new Bundle();
            data.putInt(ConstantsSync.BOOT_MODE, RegistrationWaitingStateSync.this.bootMode);
            if (RegistrationWaitingStateSync.this.bootMode == 0) {
                data.putInt(ConstantsSync.DIALOG_ID, 8);
            } else {
                long crntUtcTime = AutoSyncDataBaseUtil.getInstance().getUTCTime();
                int totalCnt = AutoSyncDataBaseUtil.getInstance().getNumberOfTransferReservationFiles(crntUtcTime);
                SyncKikiLogUtil.logNotConnect();
                SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
                SyncBackUpUtil.getInstance().setSyncError(1);
                SyncBackUpUtil.getInstance().setSyncErrorTotal(totalCnt);
                SyncBackUpUtil.getInstance().setSyncErrorSent(0);
                data.putInt(ConstantsSync.DIALOG_ID, 6);
                data.putInt(ConstantsSync.SYNC_TOTAL_FILE, totalCnt);
                data.putInt(ConstantsSync.SYNC_SENT_FILE, 0);
            }
            RegistrationWaitingStateSync.this.setNextState(ConstantsSync.DIALOG_STATE, data);
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.bootMode == 1 && this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        closeLayout("RegistrationWaitingLayoutSync");
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
        invokeFinishProcess();
        if (this.bootMode == 0) {
            Bundle data = new Bundle();
            data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            setNextState(ConstantsSync.REGISTRATION_SETTING_STATE, data);
            return 1;
        }
        long crntUtcTime = AutoSyncDataBaseUtil.getInstance().getUTCTime();
        int totalCnt = AutoSyncDataBaseUtil.getInstance().getNumberOfTransferReservationFiles(crntUtcTime);
        SyncBackUpUtil.getInstance().setSyncError(ConstantsSync.CAUTION_HALT_DONE);
        SyncBackUpUtil.getInstance().setSyncDate(crntUtcTime);
        SyncBackUpUtil.getInstance().setSyncErrorTotal(totalCnt);
        SyncBackUpUtil.getInstance().setSyncErrorSent(0);
        SyncKikiLogUtil.logTransferHalt();
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish(false);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeFinishProcess() {
        NetworkStateUtil.getInstance().end();
    }
}
