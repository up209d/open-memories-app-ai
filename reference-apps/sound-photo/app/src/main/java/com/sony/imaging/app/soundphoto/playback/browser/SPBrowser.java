package com.sony.imaging.app.soundphoto.playback.browser;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPBrowser extends Browser {
    public static final String ID_MENU_STATE = "Menu";

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        if (SPUtil.getInstance().isMenuBootrequired()) {
            return "Menu";
        }
        PlayRootContainer root = getRootContainer();
        PlayRootContainer.PB_MODE mode = (PlayRootContainer.PB_MODE) root.getData(PlayRootContainer.PROP_ID_PB_MODE);
        if (mode == PlayRootContainer.PB_MODE.INDEX) {
            return PlaySubApp.ID_INDEX_PB;
        }
        return PlaySubApp.ID_SINGLE_PB;
    }
}
