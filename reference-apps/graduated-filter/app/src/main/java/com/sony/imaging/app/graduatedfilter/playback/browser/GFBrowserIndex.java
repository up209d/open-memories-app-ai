package com.sony.imaging.app.graduatedfilter.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.graduatedfilter.playback.GFPlayRoot;
import com.sony.imaging.app.graduatedfilter.playback.layout.GFBrowserIndexLayout;
import com.sony.imaging.app.graduatedfilter.playback.layout.IEachPlaybackTriggerFunction;

/* loaded from: classes.dex */
public class GFBrowserIndex extends BrowserIndex implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            GFBrowserIndexLayout layout = (GFBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.graduatedfilter.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(GFPlayRoot.ID_DELETE_MULTIPLE);
        return true;
    }
}
