package com.sony.imaging.app.srctrl.playback.browser;

import android.util.Log;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class ContentsTransferState extends PlayStateBase {
    private static final String TAG = ContentsTransferState.class.getSimpleName();

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        if (stateController.isWaitingBackTransition()) {
            Log.v(TAG, "Change to Shooting state because of the back transition flag.");
            stateController.changeToShootingState();
            stateController.setGoBackFlag(false);
        } else {
            ContentsManager mgr = ContentsManager.getInstance();
            mgr.setPbMode(2);
        }
    }
}
