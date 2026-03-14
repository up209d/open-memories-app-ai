package com.sony.imaging.app.smoothreflection.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.smoothreflection.playback.SmoothRefelctionPlayRootContainer;
import com.sony.imaging.app.smoothreflection.playback.layout.EachBrowserIndexLayout;
import com.sony.imaging.app.smoothreflection.playback.layout.IEachPlaybackTriggerFunction;

/* loaded from: classes.dex */
public class EachBrowserIndex extends BrowserIndex implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            EachBrowserIndexLayout layout = (EachBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.smoothreflection.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(SmoothRefelctionPlayRootContainer.ID_DELETE_MULTIPLE);
        return true;
    }
}
