package com.sony.imaging.app.manuallenscompensation.shooting.keyhandler;

import android.view.KeyEvent;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.DeleteFocusMagnifierController;

/* loaded from: classes.dex */
public class OCS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String TAG = "OCS1OffEEStateKeyHandler.java";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                int returnValue = pushedMfAssistCustomKey();
                return returnValue;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isMFAssistUserSelectionOn()) {
                    int returnValue2 = pushedMfAssistCustomKey();
                    return returnValue2;
                }
                int returnValue3 = super.onKeyDown(keyCode, event);
                return returnValue3;
            default:
                int returnValue4 = super.onKeyDown(keyCode, event);
                return returnValue4;
        }
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        super.pushedLeftKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        super.pushedRightKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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
}
