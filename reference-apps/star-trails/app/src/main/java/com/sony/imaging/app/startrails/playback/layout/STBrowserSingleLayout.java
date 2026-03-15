package com.sony.imaging.app.startrails.playback.layout;

import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;

/* loaded from: classes.dex */
public class STBrowserSingleLayout extends BrowserSingleLayout {
    ISTPlaybackTriggerFunction mEachPbTrigger;

    public void setEachPbTrigger(ISTPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return this.mEachPbTrigger.transitionDeleteMultiple() ? 1 : -1;
    }
}
