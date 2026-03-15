package com.sony.imaging.app.base.common;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.fw.State;

/* loaded from: classes.dex */
public class ForceExitScreenState extends State implements ForceExitScreenLayout.Callback {
    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout("ForceExitScreen", this.data);
        ForceExitScreenLayout layout = (ForceExitScreenLayout) getLayout("ForceExitScreen");
        layout.setCallback(this);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout("ForceExitScreen");
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.common.layout.ForceExitScreenLayout.Callback
    public void onClose() {
        removeState();
    }

    @Override // com.sony.imaging.app.base.common.layout.ForceExitScreenLayout.Callback
    public void onFinish() {
        removeState();
        ((BaseApp) getActivity()).finish(false);
    }
}
