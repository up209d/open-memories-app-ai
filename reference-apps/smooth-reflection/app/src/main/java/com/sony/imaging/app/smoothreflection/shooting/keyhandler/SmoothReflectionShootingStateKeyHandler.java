package com.sony.imaging.app.smoothreflection.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.smoothreflection.playback.SmoothRefelctionPlayRootContainer;

/* loaded from: classes.dex */
public class SmoothReflectionShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        if (SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg) {
            SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg = false;
        }
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS1Key() {
        if (SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg) {
            SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg = false;
        }
        return super.pushedUmRemoteS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        if (SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg) {
            SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg = false;
        }
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (!SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg) {
            return super.pushedS2Key();
        }
        SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg = false;
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        if (SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg) {
            SmoothRefelctionPlayRootContainer.playback_pushed_S1_flg = false;
        }
        return super.releasedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 1;
    }
}
