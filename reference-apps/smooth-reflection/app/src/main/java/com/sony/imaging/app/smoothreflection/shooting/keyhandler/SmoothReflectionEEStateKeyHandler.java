package com.sony.imaging.app.smoothreflection.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.widget.ShootNumberLimitText;

/* loaded from: classes.dex */
public class SmoothReflectionEEStateKeyHandler extends EEStateKeyHandler {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ShootNumberLimitText.sShotNumber = ShootNumberLimitText.sTotalNumberOfShot;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedS2Key();
    }
}
