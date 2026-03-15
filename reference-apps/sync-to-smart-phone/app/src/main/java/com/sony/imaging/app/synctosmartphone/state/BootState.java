package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class BootState extends ContainerState {
    private static final String TAG = BootState.class.getSimpleName();

    protected String getNextState() {
        Log.d(TAG, "getNextState = ");
        return ConstantsSync.MAIN_STATE;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.BOOT_MODE, 0);
        data.putInt(ConstantsSync.BOOT_FIRST_TIME, 1);
        addChildState(getNextState(), data);
        openLayout(ConstantsSync.BOOT_LAYOUT, data);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(ConstantsSync.BOOT_LAYOUT);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Bundle bundle = null;
        String nextState = null;
        if (msg.obj instanceof Bundle) {
            bundle = (Bundle) msg.obj;
            nextState = bundle.getString(MenuTable.NEXT_STATE);
        }
        if (ConstantsSync.REGISTRATION_SETTING_STATE.equals(nextState)) {
            addChildState(nextState, bundle);
            return true;
        }
        if (ConstantsSync.TRANSFERRING_STATUS_STATE.equals(nextState)) {
            addChildState(nextState, bundle);
            return true;
        }
        if (!ConstantsSync.TRANSFERRING_GUIDE_STATE.equals(nextState)) {
            return false;
        }
        addChildState(nextState, bundle);
        return true;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
