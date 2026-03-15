package com.sony.imaging.app.base.common;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class ExitToConfigState extends State {
    private static final int BACKUP_SUPPORT_PFAPI_VERSION = 12;
    private static final String TAG = "ExitToConfigState";
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
        if (isSupportedByPF()) {
            ScalarProperties.setInt("scalar.2.diadem.transition.mode", 1);
        }
        ((BaseApp) getActivity()).finish(false);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mBundle = null;
        super.onPause();
    }

    protected boolean isSupportedByPF() {
        return 12 <= Environment.getVersionPfAPI();
    }
}
