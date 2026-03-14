package com.sony.imaging.app.doubleexposure.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;

/* loaded from: classes.dex */
public class DoubleExposureMfAssistKeyHandler extends MfAssistKeyHandler {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retVal = super.turnedEVDial();
        DoubleExposureUtil.getInstance().updateExposureCompensation();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }
}
