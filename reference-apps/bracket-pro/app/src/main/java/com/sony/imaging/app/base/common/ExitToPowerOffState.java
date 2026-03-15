package com.sony.imaging.app.base.common;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.didep.ScalarSystemManager;

/* loaded from: classes.dex */
public class ExitToPowerOffState extends State {
    private static final int POWEROFF_SUPPORT_PFAPI_VERSION = 7;
    private static final String TAG = "ExitToPowerOffState";
    private Bundle mBundle = null;

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 2;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();
        BaseProperties.requestingPowerOff(true);
        if (isSupportedByPF()) {
            ScalarSystemManager.requestPowerOff(11, 0, 1);
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mBundle = null;
        super.onPause();
    }

    protected boolean isSupportedByPF() {
        return 7 <= Environment.getVersionPfAPI();
    }
}
