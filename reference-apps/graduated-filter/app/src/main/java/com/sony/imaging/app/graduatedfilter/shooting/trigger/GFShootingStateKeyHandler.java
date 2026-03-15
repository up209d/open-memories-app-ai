package com.sony.imaging.app.graduatedfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;

/* loaded from: classes.dex */
public class GFShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        GFCommonUtil.getInstance().setS1_ONfromPlayback(false);
        return super.releasedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (GFCommonUtil.getInstance().enableS1_ONfromPlayback()) {
            return super.pushedS2Key();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 1;
    }
}
