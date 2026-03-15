package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class PowerOffState extends ContainerState {
    private static final String TAG = BootState.class.getSimpleName();
    DisplayManager db = null;

    protected String getNextState() {
        Log.d(TAG, "getNextState = ConstantsSync.REGISTRATING_STATE");
        return ConstantsSync.REGISTRATING_STATE;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.BOOT_MODE, 1);
        if (this.db == null) {
            this.db = new DisplayManager();
            this.db.setSavingBatteryMode("SAVING_BATTERY_ON");
        }
        addChildState(getNextState(), data);
        openLayout(ConstantsSync.POWEROFF_LAYOUT, data);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.db != null) {
            this.db.finish();
            this.db = null;
        }
        closeLayout(ConstantsSync.POWEROFF_LAYOUT);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
