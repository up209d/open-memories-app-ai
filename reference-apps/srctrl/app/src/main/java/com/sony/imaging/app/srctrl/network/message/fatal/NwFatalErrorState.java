package com.sony.imaging.app.srctrl.network.message.fatal;

import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class NwFatalErrorState extends NetworkRootState {
    public static final String ID_FATAL_LAYOUT = "FATAL_LAYOUT";

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.srctrl.network.base.NwStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        StateController.getInstance().setFatalError(true);
        openLayout(ID_FATAL_LAYOUT);
        setDirectTargetDeviceName(null);
        setDirectTargetDeviceAddress(null);
    }

    @Override // com.sony.imaging.app.srctrl.network.NetworkRootState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        throw new IllegalStateException();
    }
}
