package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;

/* loaded from: classes.dex */
public class LGShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        LGStateHolder.getInstance().setShootingEnable(true);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (LGStateHolder.getInstance().isShootingEnable()) {
            return super.pushedS2Key();
        }
        LGStateHolder.getInstance().setShootingEnable(true);
        return -1;
    }
}
