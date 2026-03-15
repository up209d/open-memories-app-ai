package com.sony.imaging.app.startrails.shooting.state;

import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STCustomWhiteBalanceControllerState extends CustomWhiteBalanceControllerState {
    private static String TAG = "STCustomWhiteBalanceControllerState";

    @Override // com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        AppLog.enter(TAG, "onPause");
        STUtility.getInstance().updateWhiteBalanceValue(null);
        AppLog.exit(TAG, "onPause");
    }
}
