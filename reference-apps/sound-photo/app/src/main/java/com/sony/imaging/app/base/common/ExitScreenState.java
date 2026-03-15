package com.sony.imaging.app.base.common;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.fw.State;

/* loaded from: classes.dex */
public class ExitScreenState extends State implements ExitScreenLayout.Callback {
    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout("ExitScreen");
        ExitScreenLayout layout = (ExitScreenLayout) getLayout("ExitScreen");
        layout.setCallback(this);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout("ExitScreen");
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout.Callback
    public void onClose() {
        removeState();
    }

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout.Callback
    public void onFinish() {
        removeState();
        ((BaseApp) getActivity()).finish(false);
    }
}
