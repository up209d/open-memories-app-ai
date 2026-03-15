package com.sony.imaging.app.timelapse.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TLBrowserIndex extends BrowserIndex {
    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        TLCommonUtil.getInstance().setAngleShiftBoot(false);
        TLCommonUtil.getInstance().setAngleShiftBootFrom(AngleShiftConstants.PB_INDEX);
        Bundle bundle = new Bundle();
        setNextState("Edit", bundle);
        return 1;
    }
}
