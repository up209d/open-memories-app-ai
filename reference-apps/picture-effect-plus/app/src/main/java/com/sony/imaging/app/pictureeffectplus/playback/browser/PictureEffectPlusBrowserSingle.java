package com.sony.imaging.app.pictureeffectplus.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.pictureeffectplus.playback.layout.IPictureEffectPlusPlaybackTriggerFunction;
import com.sony.imaging.app.pictureeffectplus.playback.layout.PictureEffectPlusBrowserSingleLayout;

/* loaded from: classes.dex */
public class PictureEffectPlusBrowserSingle extends BrowserSingle implements IPictureEffectPlusPlaybackTriggerFunction {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        if (layoutTag.equals("NORMAL_PB") || layoutTag.equals(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT)) {
            PictureEffectPlusBrowserSingleLayout layout = (PictureEffectPlusBrowserSingleLayout) getLayout(layoutTag);
            layout.setPictureEffectPlusPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.pictureeffectplus.playback.layout.IPictureEffectPlusPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp("ID_DELETE_MULTIPLE");
        return true;
    }
}
