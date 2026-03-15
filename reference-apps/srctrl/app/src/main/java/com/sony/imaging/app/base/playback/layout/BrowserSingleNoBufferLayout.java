package com.sony.imaging.app.base.playback.layout;

import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;

/* loaded from: classes.dex */
public class BrowserSingleNoBufferLayout extends PlayLayoutBase {
    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_browser_singlepb_nobuffer;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 14;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }
}
