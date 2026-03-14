package com.sony.imaging.app.portraitbeauty.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyPlayRootContainer;
import com.sony.imaging.app.portraitbeauty.playback.layout.IEachPlaybackTriggerFunction;
import com.sony.imaging.app.portraitbeauty.playback.layout.PortraitBeautyBrowserSingleLayout;

/* loaded from: classes.dex */
public class PortraitBeautyBrowserSingle extends BrowserSingle implements IEachPlaybackTriggerFunction {
    public static final int TRANSIT_TO_CATCHLIGHTSTATE = 100;
    public static final int TRANSIT_TO_SINGLEPLAYBACK = 101;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            PortraitBeautyBrowserSingleLayout layout = (PortraitBeautyBrowserSingleLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.layout.IEachPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp(PortraitBeautyPlayRootContainer.ID_DELETE_MULTIPLE);
        return true;
    }
}
