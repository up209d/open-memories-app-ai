package com.sony.imaging.app.liveviewgrading.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.liveviewgrading.AppLog;

/* loaded from: classes.dex */
public class ColorGradingEEStateKeyHandler extends EEStateKeyHandler {
    private static final String TAG = "ColorGradingEEStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        AppLog.trace(TAG, "S2 Key Event ignore S2 On");
        return super.pushedS2Key();
    }
}
