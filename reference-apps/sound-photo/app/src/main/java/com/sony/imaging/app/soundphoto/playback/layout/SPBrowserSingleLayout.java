package com.sony.imaging.app.soundphoto.playback.layout;

import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;

/* loaded from: classes.dex */
public class SPBrowserSingleLayout extends BrowserSingleLayout {
    com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu mEachPbTrigger;

    public void setEachPbTrigger(com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return this.mEachPbTrigger.transitionToMenuState() ? 1 : -1;
    }
}
