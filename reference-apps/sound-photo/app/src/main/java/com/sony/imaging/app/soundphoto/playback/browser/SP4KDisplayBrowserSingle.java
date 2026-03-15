package com.sony.imaging.app.soundphoto.playback.browser;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu;
import com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout;

/* loaded from: classes.dex */
public class SP4KDisplayBrowserSingle extends BrowserSingle implements ITransitToPlaybackMenu {
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        open4kStartingDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            SPBrowserPlayingLayout layout = (SPBrowserPlayingLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu
    public boolean transitionToMenuState() {
        PlayRootContainer root = getRootContainer();
        root.setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.SINGLE);
        root.changeApp(PlayRootContainer.ID_BROWSER);
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionIndexPb() {
        getRootContainer().transitionBrowser();
        return false;
    }
}
