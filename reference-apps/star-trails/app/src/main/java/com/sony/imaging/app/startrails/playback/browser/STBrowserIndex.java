package com.sony.imaging.app.startrails.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.layout.ISTPlaybackTriggerFunction;
import com.sony.imaging.app.startrails.playback.layout.STBrowserIndexLayout;

/* loaded from: classes.dex */
public class STBrowserIndex extends BrowserIndex implements ISTPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            STBrowserIndexLayout layout = (STBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.startrails.playback.layout.ISTPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(STPlayRootContainer.ID_DELETE_MULTIPLE);
        return true;
    }
}
