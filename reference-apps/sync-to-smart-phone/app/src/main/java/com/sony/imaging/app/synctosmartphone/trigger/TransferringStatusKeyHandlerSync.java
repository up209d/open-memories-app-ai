package com.sony.imaging.app.synctosmartphone.trigger;

import android.util.Log;
import com.sony.imaging.app.fw.BaseInvalidKeyHandler;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;

/* loaded from: classes.dex */
public class TransferringStatusKeyHandlerSync extends BaseInvalidKeyHandler {
    private static final String TAG = "TransferringStatusKeyHandlerSync";

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        State state = (State) this.target;
        state.setNextState(ConstantsSync.MAIN_STATE, null);
        Log.d(TAG, "pushedMenuKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        State state = (State) this.target;
        state.setNextState(ConstantsSync.CONNECTING_STATE, null);
        Log.d(TAG, "pushedPlayBackKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        State state = (State) this.target;
        state.setNextState(ConstantsSync.DIALOG_STATE, null);
        Log.d(TAG, "pushedFnKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey = 1");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return "None";
    }
}
