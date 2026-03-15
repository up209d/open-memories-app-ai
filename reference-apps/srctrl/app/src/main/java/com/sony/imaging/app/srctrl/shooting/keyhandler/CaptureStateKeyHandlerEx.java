package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;

/* loaded from: classes.dex */
public class CaptureStateKeyHandlerEx extends CaptureStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS2Key() {
        return -1;
    }
}
