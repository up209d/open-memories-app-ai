package com.sony.imaging.app.soundphoto.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserIndexLayout;
import com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu;

/* loaded from: classes.dex */
public class SP4KDisplayBrowserIndex extends BrowserIndex implements ITransitToPlaybackMenu {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            SPBrowserIndexLayout layout = (SPBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu
    public boolean transitionToMenuState() {
        addChildState(SPPlayRootContainer.ID_PLAYBACK_MENU, (Bundle) null);
        return false;
    }
}
