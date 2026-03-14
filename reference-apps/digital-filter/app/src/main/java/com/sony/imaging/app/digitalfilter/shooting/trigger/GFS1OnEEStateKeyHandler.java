package com.sony.imaging.app.digitalfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;

/* loaded from: classes.dex */
public class GFS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (!GFCommonUtil.getInstance().enableS1_ONfromPlayback()) {
            super.releasedS1Key();
            GFCommonUtil.getInstance().setS1_ONfromPlayback(false);
        }
        return super.pushedPlayBackKey();
    }
}
