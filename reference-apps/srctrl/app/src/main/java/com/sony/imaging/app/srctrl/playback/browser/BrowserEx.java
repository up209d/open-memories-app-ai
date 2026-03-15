package com.sony.imaging.app.srctrl.playback.browser;

import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class BrowserEx extends Browser {
    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        return PlaySubApp.ID_INDEX_PB;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
    }
}
