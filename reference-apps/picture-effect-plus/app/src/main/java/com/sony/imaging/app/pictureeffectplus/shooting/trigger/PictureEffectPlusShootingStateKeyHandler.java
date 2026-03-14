package com.sony.imaging.app.pictureeffectplus.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.pictureeffectplus.PictureEffectKikilogUtil;

/* loaded from: classes.dex */
public class PictureEffectPlusShootingStateKeyHandler extends ShootingStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        PictureEffectKikilogUtil.incrementShootingPictureEffectPlusCount();
        return super.pushedS2Key();
    }
}
