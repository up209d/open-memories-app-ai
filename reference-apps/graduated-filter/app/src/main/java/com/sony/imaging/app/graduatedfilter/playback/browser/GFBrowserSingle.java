package com.sony.imaging.app.graduatedfilter.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.graduatedfilter.playback.GFPlayRoot;
import com.sony.imaging.app.graduatedfilter.playback.layout.GFBrowserSingleLayout;
import com.sony.imaging.app.graduatedfilter.playback.layout.IEachPlaybackTriggerFunction;

/* loaded from: classes.dex */
public class GFBrowserSingle extends BrowserSingle implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            GFBrowserSingleLayout layout = (GFBrowserSingleLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.graduatedfilter.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(GFPlayRoot.ID_DELETE_MULTIPLE);
        return true;
    }
}
