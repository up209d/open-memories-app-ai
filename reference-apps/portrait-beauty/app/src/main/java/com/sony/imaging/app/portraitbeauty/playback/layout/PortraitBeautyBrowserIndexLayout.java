package com.sony.imaging.app.portraitbeauty.playback.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;

/* loaded from: classes.dex */
public class PortraitBeautyBrowserIndexLayout extends BrowserIndexLayout {
    IEachPlaybackTriggerFunction mEachPbTrigger;

    public void setEachPbTrigger(IEachPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }
}
