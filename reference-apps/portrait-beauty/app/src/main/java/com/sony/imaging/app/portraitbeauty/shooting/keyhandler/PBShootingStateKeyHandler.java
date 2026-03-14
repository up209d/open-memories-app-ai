package com.sony.imaging.app.portraitbeauty.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;

/* loaded from: classes.dex */
public class PBShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return isBulbOpenedBy(1) ? -1 : 1;
    }
}
