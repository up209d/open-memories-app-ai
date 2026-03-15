package com.sony.imaging.app.lightgraffiti.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.lightgraffiti.playback.LGPlayRoot;
import com.sony.imaging.app.lightgraffiti.playback.layout.ILGPlaybackTriggerFunction;
import com.sony.imaging.app.lightgraffiti.playback.layout.LGBrowserSingleLayout;

/* loaded from: classes.dex */
public class LGBrowserSingle extends BrowserSingle implements ILGPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            LGBrowserSingleLayout layout = (LGBrowserSingleLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.playback.layout.ILGPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(LGPlayRoot.ID_DELETE_MULTIPLE);
        return true;
    }
}
