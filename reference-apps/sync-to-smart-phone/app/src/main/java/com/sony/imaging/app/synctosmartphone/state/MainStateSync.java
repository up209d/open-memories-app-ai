package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.MainCursolPosUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class MainStateSync extends State {
    private static final String TAG = MainStateSync.class.getSimpleName();
    private int bootMode;
    boolean m_iRight = false;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        NetworkStateUtil.getInstance().clearCanceledTransferring();
        int bootFirstTime = data.getInt(ConstantsSync.BOOT_FIRST_TIME);
        int syncErrorCode = SyncBackUpUtil.getInstance().getSyncError(0);
        boolean KeyDisplayErrorAppStart = SyncBackUpUtil.getInstance().getKeyDisplayErrorAppStart(false);
        if (Settings.System.getInt(getActivity().getContentResolver(), "airplane_mode_on", 0) == 1) {
            Bundle nextData = new Bundle();
            nextData.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            nextData.putInt(ConstantsSync.DIALOG_ID, 9);
            setNextState(ConstantsSync.DIALOG_STATE, nextData);
            return;
        }
        if (syncErrorCode != 0 && 1 == bootFirstTime && true == KeyDisplayErrorAppStart) {
            SyncBackUpUtil.getInstance().setKeyDisplayErrorAppStart(false);
            int totalCnt = SyncBackUpUtil.getInstance().getSyncErrorTotal(0);
            int sentCnt = SyncBackUpUtil.getInstance().getSyncErrorSent(0);
            Bundle nextData2 = new Bundle();
            nextData2.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            nextData2.putInt(ConstantsSync.DIALOG_ID, 6);
            nextData2.putInt(ConstantsSync.SYNC_TOTAL_FILE, totalCnt);
            nextData2.putInt(ConstantsSync.SYNC_SENT_FILE, sentCnt);
            setNextState(ConstantsSync.DIALOG_STATE, nextData2);
            return;
        }
        openLayout(ConstantsSync.MAIN_LAYOUT, data);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(ConstantsSync.MAIN_LAYOUT);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        boolean m_iUp = MainCursolPosUtil.getInstance().getCursolUp();
        boolean m_iRight = MainCursolPosUtil.getInstance().getCursolRight();
        Log.d(TAG, "pushedCenterKey CursolRigth = " + m_iRight);
        Log.d(TAG, "pushedCenterKey CursolRigth = " + m_iUp);
        if (m_iUp) {
            if (m_iRight) {
                Bundle data = new Bundle();
                data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                setNextState(ConstantsSync.REGISTRATION_SETTING_STATE, data);
            } else {
                AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
                boolean flg = SyncBackUpUtil.getInstance().getRegister(false);
                long startUTC = SyncBackUpUtil.getInstance().getStartUTC(0L);
                if (0 == startUTC) {
                    dbUtil.setAutoSyncMode(true);
                    if (flg) {
                        Bundle data2 = new Bundle();
                        data2.putInt(ConstantsSync.DIALOG_ID, 0);
                        data2.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                        data2.putString(ConstantsSync.BEFORE_STATE, ConstantsSync.MAIN_STATE);
                        setNextState(ConstantsSync.DIALOG_STATE, data2);
                    } else {
                        Bundle data3 = new Bundle();
                        data3.putInt(ConstantsSync.DIALOG_ID, 3);
                        data3.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                        setNextState(ConstantsSync.DIALOG_STATE, data3);
                    }
                } else {
                    dbUtil.setAutoSyncMode(false);
                    Bundle data4 = new Bundle();
                    data4.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
                    data4.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
                    closeLayout(ConstantsSync.MAIN_LAYOUT);
                    openLayout(ConstantsSync.MAIN_LAYOUT, data4);
                }
            }
        } else {
            Bundle data5 = new Bundle();
            data5.putInt(ConstantsSync.BOOT_MODE, 1);
            setNextState(ConstantsSync.REGISTRATING_STATE, data5);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        Log.d(TAG, "turnedDial1ToRight");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        setSlectedButton();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
        setNextState(ConstantsSync.MENU_STATE, data);
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

    private void setSlectedButton() {
        Log.d(TAG, "setSelctedButton");
    }
}
