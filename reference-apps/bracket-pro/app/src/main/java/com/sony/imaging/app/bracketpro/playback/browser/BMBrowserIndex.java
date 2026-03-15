package com.sony.imaging.app.bracketpro.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.bracketpro.playback.BMPlayRoot;
import com.sony.imaging.app.bracketpro.playback.layout.EachBrowserIndexLayout;
import com.sony.imaging.app.bracketpro.playback.layout.IEachPlaybackTriggerFunction;

/* loaded from: classes.dex */
public class BMBrowserIndex extends BrowserIndex implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            EachBrowserIndexLayout layout = (EachBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.bracketpro.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(BMPlayRoot.ID_DELETE_MULTIPLE);
        return true;
    }
}
