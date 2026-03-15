package com.sony.imaging.app.timelapse.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TimelapseDevelopmentState extends DevelopmentState {
    private static final String NEXT_STATE = "ExposureModeCheck";
    private static final String TESTSHOT = "TESTSHOT";

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (TLCommonUtil.getInstance().isTestShot()) {
            switchToPlayback();
        }
    }

    private void switchToPlayback() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TESTSHOT, true);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
        this.data = null;
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (!TLCommonUtil.getInstance().isTestShot() && name.equals("EE")) {
            name = NEXT_STATE;
        }
        super.setNextState(name, bundle);
    }
}
