package com.sony.imaging.app.startrails.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.layout.ISTPlaybackTriggerFunction;
import com.sony.imaging.app.startrails.playback.layout.STBrowserSingleLayout;

/* loaded from: classes.dex */
public class STBrowserSingle extends BrowserSingle implements ISTPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            STBrowserSingleLayout layout = (STBrowserSingleLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.startrails.playback.layout.ISTPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(STPlayRootContainer.ID_DELETE_MULTIPLE);
        return true;
    }
}
