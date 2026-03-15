package com.sony.imaging.app.lightgraffiti.playback.layout;

import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;

/* loaded from: classes.dex */
public class LGBrowserSingleLayout extends BrowserSingleLayout {
    ILGPlaybackTriggerFunction mEachPbTrigger;

    public void setEachPbTrigger(ILGPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return this.mEachPbTrigger.transitionDeleteMultiple() ? 1 : -1;
    }
}
