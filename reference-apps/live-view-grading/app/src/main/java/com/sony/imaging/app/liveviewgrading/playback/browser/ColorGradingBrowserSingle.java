package com.sony.imaging.app.liveviewgrading.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.liveviewgrading.playback.ColorGradingPlayRoot;
import com.sony.imaging.app.liveviewgrading.playback.layout.EachBrowserSingleLayout;
import com.sony.imaging.app.liveviewgrading.playback.layout.IEachPlaybackTriggerFunction;

/* loaded from: classes.dex */
public class ColorGradingBrowserSingle extends BrowserSingle implements IEachPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            EachBrowserSingleLayout layout = (EachBrowserSingleLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.liveviewgrading.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(ColorGradingPlayRoot.ID_DELETE_MULTIPLE);
        return true;
    }
}
