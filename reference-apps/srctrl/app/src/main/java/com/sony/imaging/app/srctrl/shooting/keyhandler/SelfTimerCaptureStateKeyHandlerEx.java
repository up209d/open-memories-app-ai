package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler;

/* loaded from: classes.dex */
public class SelfTimerCaptureStateKeyHandlerEx extends SelfTimerCaptureStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS2Key() {
        return -1;
    }
}
