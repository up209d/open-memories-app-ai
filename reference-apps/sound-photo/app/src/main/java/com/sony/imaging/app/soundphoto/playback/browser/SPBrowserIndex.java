package com.sony.imaging.app.soundphoto.playback.browser;

import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserIndexLayout;
import com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu;

/* loaded from: classes.dex */
public class SPBrowserIndex extends BrowserIndex implements ITransitToPlaybackMenu {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.IndexStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.isInitialQueryDone() || DataBaseOperations.getInstance().getTotalFiles() == 0) {
            if (mgr.isEmpty() || DataBaseOperations.getInstance().getTotalFiles() == 0) {
                return "NO_FILE";
            }
            return "NORMAL_PB";
        }
        return IndexStateBase.ID_DATA_UNREADY_LAYOUT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            SPBrowserIndexLayout layout = (SPBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.IndexStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu
    public boolean transitionToMenuState() {
        openMenu();
        return false;
    }

    private void openMenu() {
        setNextState("Menu", null);
    }
}
