package com.sony.imaging.app.digitalfilter.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.digitalfilter.playback.layout.GFBrowserIndexLayout;
import com.sony.imaging.app.digitalfilter.playback.layout.IEachPlaybackTriggerFunction;

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

    @Override // com.sony.imaging.app.digitalfilter.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        return false;
    }
}
