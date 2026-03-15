package com.sony.imaging.app.soundphoto.playback.layout;

import com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout;

/* loaded from: classes.dex */
public class BrowserSingle4kLayout extends SPBrowserPlayingLayout {
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int getImageType() {
        return 1;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout, com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        stop4kPlayback();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        stop4kPlayback();
        return 1;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout, com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return -1;
    }

    protected void stop4kPlayback() {
        if (this.m4kPbTrigger != null) {
            this.m4kPbTrigger.stop4kPlayback();
        }
        this.isPlaying = false;
    }

    public void setContentIDChanged(boolean isChanged) {
        this.contentIDChanged = isChanged;
        this.isPlaying = false;
    }
}
