package com.sony.imaging.app.lightgraffiti.playback.layout;

import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;

/* loaded from: classes.dex */
public class LGBrowserIndexLayout extends BrowserIndexLayout {
    ILGPlaybackTriggerFunction mEachPbTrigger;

    public void setEachPbTrigger(ILGPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return this.mEachPbTrigger.transitionDeleteMultiple() ? 1 : -1;
    }
}
