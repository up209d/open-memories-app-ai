package com.sony.imaging.app.srctrl.network.base;

import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.srctrl.network.NetworkRootState;

/* loaded from: classes.dex */
public class NwStateBase extends ContainerState {
    protected NetworkRootState getContainer() {
        return (NetworkRootState) this.container;
    }

    protected NetworkRootState getRootContainer() {
        return (NetworkRootState) getData(NetworkRootState.PROP_ID_APP_ROOT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getLogTag() {
        return getClass().getSimpleName();
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }
}
