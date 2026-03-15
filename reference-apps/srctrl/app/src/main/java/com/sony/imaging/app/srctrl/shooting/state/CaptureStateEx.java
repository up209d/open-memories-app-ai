package com.sony.imaging.app.srctrl.shooting.state;

import com.sony.imaging.app.base.shooting.CaptureState;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.srctrl.shooting.keyhandler.S1OffEEStateKeyHandlerEx;

/* loaded from: classes.dex */
public class CaptureStateEx extends CaptureState {
    public static final String STATE_NAME = "Capture";

    @Override // com.sony.imaging.app.base.shooting.CaptureState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        S1OffEEStateKeyHandlerEx.s_pushed_S2 = false;
        DigitalZoomController.getInstance().stopZoom();
        super.onResume();
    }
}
