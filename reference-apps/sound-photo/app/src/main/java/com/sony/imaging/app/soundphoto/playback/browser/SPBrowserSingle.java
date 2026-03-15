package com.sony.imaging.app.soundphoto.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.playback.layout.BrowserSingle4kLayout;
import com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu;
import com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPBrowserSingle extends BrowserSingle implements ITransitToPlaybackMenu {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (((BaseApp) getActivity()).is4kPlaybackSupported() && mgr.isInitialQueryDone() && 2 == DisplayModeObserver.getInstance().get4kOutputStatus()) {
            return SingleStateBase.ID_4K_PLAYBACK_LAYOUT;
        }
        if (mgr.isInitialQueryDone() || DataBaseOperations.getInstance().getTotalFiles() <= 0) {
            if (mgr.isEmpty() || DataBaseOperations.getInstance().getTotalFiles() <= 0) {
                return "NO_FILE";
            }
            return "NORMAL_PB";
        }
        if (mgr.getResumeId() != null) {
            return SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT;
        }
        return SingleStateBase.ID_NO_BUFFER_LAYOUT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            SPBrowserPlayingLayout layout = (SPBrowserPlayingLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
        if (SingleStateBase.ID_4K_PLAYBACK_LAYOUT.equals(layoutTag)) {
            BrowserSingle4kLayout layout2 = (BrowserSingle4kLayout) getLayout(layoutTag);
            layout2.setContentIDChanged(true);
        }
    }

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (PlaySubApp.ID_INDEX_PB.equals(name)) {
            SPUtil.getInstance().setSoundPlayingState(false);
            SPUtil.getInstance().releaseAudioTrackController();
        }
        super.setNextState(name, bundle);
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu
    public boolean transitionToMenuState() {
        openMenu();
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionZoom(EventParcel event) {
        SPUtil.getInstance().setSoundPlayingState(false);
        SPUtil.getInstance().releaseAudioTrackController();
        return super.transitionZoom(event);
    }

    private void openMenu() {
        setNextState("Menu", null);
    }
}
