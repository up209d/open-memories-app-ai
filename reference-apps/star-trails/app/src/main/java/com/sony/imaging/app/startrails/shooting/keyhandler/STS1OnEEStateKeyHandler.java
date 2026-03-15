package com.sony.imaging.app.startrails.shooting.keyhandler;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.util.AppLog;

/* loaded from: classes.dex */
public class STS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    private static final String TAG = "STS1OnEEStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        boolean ret = STExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        if (ret) {
            ((IUserChanging) this.target).changeExposure();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfAelKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }
}
