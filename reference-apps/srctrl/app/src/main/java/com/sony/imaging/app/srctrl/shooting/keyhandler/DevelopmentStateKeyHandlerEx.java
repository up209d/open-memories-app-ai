package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil;

/* loaded from: classes.dex */
public class DevelopmentStateKeyHandlerEx extends DevelopmentStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return (!CaptureStateUtil.isExternalMediaMounted() || CaptureStateUtil.isSelfTimer() || CaptureStateUtil.isSingleShooting()) ? -1 : 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        if (CaptureStateUtil.remote_shooting_mode) {
            return -1;
        }
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        if (CaptureStateUtil.remote_shooting_mode) {
            return -1;
        }
        return super.releasedS1Key();
    }
}
