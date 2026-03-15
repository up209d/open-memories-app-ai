package com.sony.imaging.app.startrails.shooting.keyhandler;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.startrails.shooting.state.MFModeCheckState;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STMFAssistStateKeyHandler extends MfAssistKeyHandler {
    private static final String MENU_STATE = "Menu";
    private static final String TAG = "STMFAssistStateKeyHandler";

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            if (STUtility.getInstance().isApplySettingFinish()) {
                STUtility.getInstance().setPreTakePictureTestShot(true);
                ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
            }
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            State state = (State) this.target;
            state.setNextState(MFModeCheckState.TAG, null);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        if (isS1On()) {
            return -1;
        }
        openMenu();
        return 1;
    }

    protected void openMenu() {
        State state = (State) this.target;
        Bundle bundle = new Bundle();
        bundle.putString(ExposureModeController.EXPOSURE_MODE, ExposureModeController.EXPOSURE_MODE);
        bundle.putString(MenuState.ITEM_ID, ExposureModeController.EXPOSURE_MODE);
        state.setNextState("Menu", bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }
}
