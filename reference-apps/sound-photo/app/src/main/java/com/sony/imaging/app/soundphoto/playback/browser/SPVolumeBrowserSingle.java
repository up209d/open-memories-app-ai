package com.sony.imaging.app.soundphoto.playback.browser;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.soundphoto.playback.menu.layout.IVolumeSettingTransitHandler;
import com.sony.imaging.app.soundphoto.playback.setting.volume.layout.SPBrowserVolumeSettingLayout;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPVolumeBrowserSingle extends BrowserSingle implements IVolumeSettingTransitHandler {
    int mLoggedDisplayMode;

    public SPVolumeBrowserSingle() {
        this.mLoggedDisplayMode = 3;
        SPUtil.getInstance().setSoundPlayingState(true);
        this.mLoggedDisplayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        DisplayModeObserver.getInstance().setDisplayMode(1, 3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            SPBrowserVolumeSettingLayout layout = (SPBrowserVolumeSettingLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu
    public boolean transitionToMenuState() {
        transitToPreviousBrouser(true);
        return false;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.menu.layout.IVolumeSettingTransitHandler
    public boolean transitionToNextState() {
        transitToPreviousBrouser(false);
        return false;
    }

    private void transitToPreviousBrouser(boolean isMenuStateRequired) {
        DisplayModeObserver.getInstance().setDisplayMode(1, this.mLoggedDisplayMode);
        SPUtil.getInstance().setMenuBootrequired(isMenuStateRequired);
        getRootContainer().transitionBrowser();
    }

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().setDisplayMode(1, this.mLoggedDisplayMode);
        super.onPause();
    }
}
