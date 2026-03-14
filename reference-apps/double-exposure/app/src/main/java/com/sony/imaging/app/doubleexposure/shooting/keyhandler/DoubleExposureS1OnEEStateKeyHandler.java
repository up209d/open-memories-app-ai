package com.sony.imaging.app.doubleexposure.shooting.keyhandler;

import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureCompensationController;

/* loaded from: classes.dex */
public class DoubleExposureS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = DoubleExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        if (bRetVal) {
            ((IUserChanging) this.target).changeExposure();
        }
        DoubleExposureUtil.getInstance().updateExposureCompensation();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }
}
