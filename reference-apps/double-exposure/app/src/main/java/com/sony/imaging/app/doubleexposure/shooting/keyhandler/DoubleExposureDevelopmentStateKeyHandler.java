package com.sony.imaging.app.doubleexposure.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DoubleExposureDevelopmentStateKeyHandler extends DevelopmentStateKeyHandler {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }
}
