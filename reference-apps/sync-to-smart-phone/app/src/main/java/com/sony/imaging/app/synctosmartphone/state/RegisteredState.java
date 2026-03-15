package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class RegisteredState extends State {
    private static final String TAG = RegisteredState.class.getSimpleName();
    private int bootMode;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        NetworkStateUtil.getInstance().setState(this, data);
        NetworkStateUtil.getInstance().end();
        boolean bret = AutoSyncDataBaseUtil.getInstance().setAutoSyncMode(true);
        Log.d(TAG, "call AutoSyncDataBaseUtil.setAutoSyncMode(true) : " + bret);
        openLayout(ConstantsSync.REGISTERED_LAYOUT, data);
        Log.d(TAG, "onResume = ");
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(ConstantsSync.REGISTERED_LAYOUT);
        super.onPause();
        Log.d(TAG, "onPause = ");
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
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
        data.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
        setNextState(ConstantsSync.MAIN_STATE, data);
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
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.DIALOG_ID, 0);
        data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
        data.putString(ConstantsSync.BEFORE_STATE, ConstantsSync.REGISTERED_STATE);
        setNextState(ConstantsSync.DIALOG_STATE, data);
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
}
