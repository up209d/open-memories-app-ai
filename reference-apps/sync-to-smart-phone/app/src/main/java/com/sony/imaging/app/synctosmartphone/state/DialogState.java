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
import com.sony.imaging.app.util.DatabaseUtil;

/* loaded from: classes.dex */
public class DialogState extends State {
    private static final String TAG = DialogState.class.getSimpleName();
    private int bootMode;
    private int mDialogId = 0;
    private boolean mUpperButton = false;
    private String mBeforeState = "";
    private Runnable AutoPoweroffTask = new Runnable() { // from class: com.sony.imaging.app.synctosmartphone.state.DialogState.1
        @Override // java.lang.Runnable
        public void run() {
            Log.i(DialogState.TAG, "AutoPowerOff");
            DialogState.this.finishApp();
        }
    };

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        this.mDialogId = data.getInt(ConstantsSync.DIALOG_ID);
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        this.mUpperButton = false;
        if (this.mDialogId == 0) {
            this.mBeforeState = data.getString(ConstantsSync.BEFORE_STATE);
        }
        openLayout(ConstantsSync.DIALOG_LAYOUT, data);
        Log.d(TAG, "onResume =  mDialogId = " + this.mDialogId);
        if (this.mDialogId == 6 && this.bootMode == 1) {
            AutoSyncDataBaseUtil autoSyncDB = AutoSyncDataBaseUtil.getInstance();
            DatabaseUtil.DbResult dbResult = autoSyncDB.exportDatabase();
            Log.v(TAG, "exportDatabase.dbResult = " + dbResult);
            getHandler().postDelayed(this.AutoPoweroffTask, 3000L);
        }
        if (this.mDialogId == 6 && this.mDialogId == 8) {
            NetworkStateUtil.getInstance().end();
        }
        if (this.mDialogId == 1) {
            NetworkStateUtil.getInstance().clearCanceledTransferring();
        }
        if (this.mDialogId == 3) {
            this.mUpperButton = true;
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getHandler().removeCallbacks(this.AutoPoweroffTask);
        closeLayout(ConstantsSync.DIALOG_LAYOUT);
        super.onPause();
        Log.d(TAG, "onPause = ");
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        if (this.mDialogId != 7 && (this.mDialogId != 6 || this.bootMode != 1)) {
            finishApp();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        if (this.mDialogId != 7 && (this.mDialogId != 6 || this.bootMode != 1)) {
            finishApp();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        if (this.mDialogId != 0) {
            if (this.mDialogId == 1 || this.mDialogId == 2 || this.mDialogId == 3) {
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                data.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                setNextState(ConstantsSync.MAIN_STATE, data);
            } else if (this.mDialogId == 4 || this.mDialogId == 5) {
                Bundle data2 = new Bundle();
                data2.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.TRANSFERRING_STATUS_STATE, data2);
            } else if (this.mDialogId != 6 && this.mDialogId != 7) {
                if (this.mDialogId == 8) {
                    Bundle data3 = new Bundle();
                    data3.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    data3.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                    setNextState(ConstantsSync.MAIN_STATE, data3);
                } else if (this.mDialogId == 9) {
                }
            }
        }
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        if (this.mDialogId == 0) {
            if (ConstantsSync.MAIN_STATE.equals(this.mBeforeState)) {
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                data.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                setNextState(ConstantsSync.MAIN_STATE, data);
            } else if (ConstantsSync.REGISTERED_STATE.equals(this.mBeforeState)) {
                finishApp();
            }
        } else if (this.mDialogId == 1) {
            if (true == this.mUpperButton) {
                AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
                dbUtil.setAutoSyncMode(false);
                SyncBackUpUtil.getInstance().setRegister(false);
                SyncBackUpUtil.getInstance().setSmartphone(null);
                SyncBackUpUtil.getInstance().setSmartphoneInfo(null);
                this.mDialogId = 2;
                Bundle data2 = new Bundle();
                data2.putInt(ConstantsSync.DIALOG_ID, this.mDialogId);
                data2.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                Log.v(TAG, " Pushed CenterKey = setNextState");
                closeLayout(ConstantsSync.DIALOG_LAYOUT);
                openLayout(ConstantsSync.DIALOG_LAYOUT, data2);
            } else {
                Bundle data3 = new Bundle();
                data3.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.REGISTRATION_SETTING_STATE, data3);
            }
        } else if (this.mDialogId == 2) {
            Bundle data4 = new Bundle();
            data4.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            data4.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
            setNextState(ConstantsSync.MAIN_STATE, data4);
        } else if (this.mDialogId == 3) {
            if (true == this.mUpperButton) {
                Bundle data5 = new Bundle();
                data5.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.REGISTRATION_SETTING_STATE, data5);
            } else {
                Bundle data6 = new Bundle();
                data6.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                data6.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                setNextState(ConstantsSync.MAIN_STATE, data6);
            }
        } else if (this.mDialogId == 4) {
            if (true == this.mUpperButton) {
                this.mDialogId = 5;
                Bundle data7 = new Bundle();
                data7.putInt(ConstantsSync.DIALOG_ID, this.mDialogId);
                data7.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                boolean bret = AutoSyncDataBaseUtil.getInstance().deleteDatabase();
                Log.v(TAG, " Delete Database = " + bret);
                SyncBackUpUtil.getInstance().setSyncError(0);
                SyncBackUpUtil.getInstance().setSyncDate(0L);
                closeLayout(ConstantsSync.DIALOG_LAYOUT);
                openLayout(ConstantsSync.DIALOG_LAYOUT, data7);
            } else {
                Bundle data8 = new Bundle();
                data8.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.TRANSFERRING_STATUS_STATE, data8);
            }
        } else if (this.mDialogId == 5) {
            Bundle data9 = new Bundle();
            data9.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            setNextState(ConstantsSync.TRANSFERRING_STATUS_STATE, data9);
        } else if (this.mDialogId == 6) {
            if (this.bootMode == 1) {
                finishApp();
            } else {
                Bundle data10 = new Bundle();
                data10.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                data10.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                setNextState(ConstantsSync.MAIN_STATE, data10);
            }
        } else if (this.mDialogId == 7) {
            if (true == this.mUpperButton) {
                if (!NetworkStateUtil.getInstance().isCanceledTransferring()) {
                    NetworkStateUtil.getInstance().setCanceledTransferring();
                    int totalNotifySyncImage = NetworkStateUtil.getInstance().getNumOfTotalSyncImage();
                    int notifySyncImage = NetworkStateUtil.getInstance().getNumberOfNotifySyncImage();
                    if (notifySyncImage > 0) {
                        notifySyncImage--;
                    }
                    SyncBackUpUtil.getInstance().setSyncDate(AutoSyncDataBaseUtil.getInstance().getUTCTime());
                    SyncBackUpUtil.getInstance().setSyncError(ConstantsSync.CAUTION_HALT_DONE);
                    SyncBackUpUtil.getInstance().setSyncErrorTotal(totalNotifySyncImage);
                    SyncBackUpUtil.getInstance().setSyncErrorSent(notifySyncImage);
                    SyncKikiLogUtil.logTransferHalt();
                    this.mDialogId = 6;
                    Bundle data11 = new Bundle();
                    data11.putInt(ConstantsSync.DIALOG_ID, this.mDialogId);
                    data11.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    data11.putInt(ConstantsSync.SYNC_TOTAL_FILE, totalNotifySyncImage);
                    data11.putInt(ConstantsSync.SYNC_SENT_FILE, notifySyncImage);
                    closeLayout(ConstantsSync.DIALOG_LAYOUT);
                    openLayout(ConstantsSync.DIALOG_LAYOUT, data11);
                    AutoSyncDataBaseUtil autoSyncDB = AutoSyncDataBaseUtil.getInstance();
                    autoSyncDB.endGetReservationFiles();
                    getHandler().postDelayed(this.AutoPoweroffTask, 3000L);
                }
            } else {
                Bundle data12 = new Bundle();
                data12.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.TRANSFERRING_STATE, data12);
            }
        } else if (this.mDialogId == 8) {
            Bundle data13 = new Bundle();
            data13.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            data13.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
            setNextState(ConstantsSync.MAIN_STATE, data13);
        } else if (this.mDialogId == 9) {
            finishApp();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        setSelectedButton();
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
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        setSelectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.d(TAG, "pushedPlayBackKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Log.d(TAG, "pushedFnKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    private void setSelectedButton() {
        if (!NetworkStateUtil.getInstance().isCanceledTransferring()) {
            if (this.mDialogId == 1 || this.mDialogId == 3 || this.mDialogId == 4 || this.mDialogId == 7) {
                this.mUpperButton = this.mUpperButton ? false : true;
            }
            Log.d(TAG, "setSelctedButton mUpperButton = " + this.mUpperButton);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishApp() {
        Log.i(TAG, "finishApp");
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish(false);
    }
}
