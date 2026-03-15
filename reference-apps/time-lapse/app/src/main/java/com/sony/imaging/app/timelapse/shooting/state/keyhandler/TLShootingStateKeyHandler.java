package com.sony.imaging.app.timelapse.shooting.state.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;

/* loaded from: classes.dex */
public class TLShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 1;
    }
}
