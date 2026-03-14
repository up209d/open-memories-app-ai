package com.sony.imaging.app.portraitbeauty.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyPlayRootContainer;
import com.sony.imaging.app.portraitbeauty.playback.layout.IEachPlaybackTriggerFunction;
import com.sony.imaging.app.portraitbeauty.playback.layout.PortraitBeautyBrowserIndexLayout;

/* loaded from: classes.dex */
public class PortraitBeautyBrowserIndex extends BrowserIndex implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            PortraitBeautyBrowserIndexLayout layout = (PortraitBeautyBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(PortraitBeautyPlayRootContainer.ID_DELETE_MULTIPLE);
        return true;
    }
}
