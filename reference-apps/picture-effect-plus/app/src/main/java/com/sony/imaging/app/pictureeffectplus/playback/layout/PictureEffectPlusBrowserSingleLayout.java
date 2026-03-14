package com.sony.imaging.app.pictureeffectplus.playback.layout;

import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;

/* loaded from: classes.dex */
public class PictureEffectPlusBrowserSingleLayout extends BrowserSingleLayout {
    IPictureEffectPlusPlaybackTriggerFunction mPictureEffectPlusPbTrigger;

    public void setPictureEffectPlusPbTrigger(IPictureEffectPlusPlaybackTriggerFunction trigger) {
        this.mPictureEffectPlusPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return this.mPictureEffectPlusPbTrigger.transitionDeleteMultiple() ? 1 : -1;
    }
}
