package com.sony.imaging.app.manuallenscompensation.shooting.keyhandler;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.DeleteFocusMagnifierController;

/* loaded from: classes.dex */
public class OCMfAssistKeyHandler extends MfAssistKeyHandler {
    private static final String TAG = "OCMfAssistKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        if (!isMFAssistUserSelectionOn()) {
            return -1;
        }
        int ret = pushedCenterKey();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayBackKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayIndexKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    private boolean isMFAssistUserSelectionOn() {
        DeleteFocusMagnifierController controller = DeleteFocusMagnifierController.getInstance();
        String currentSetting = DeleteFocusMagnifierController.FOCUS_MAGNIFIER_OFF;
        try {
            currentSetting = controller.getValue(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_SWITCH_IC);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, "Exception occuered :" + e.getMessage());
        }
        if (currentSetting == null || !currentSetting.equals(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_ON)) {
            return false;
        }
        return true;
    }
}
