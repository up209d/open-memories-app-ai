package com.sony.imaging.app.doubleexposure.shooting.keyhandler;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureCameraSettings;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureCompensationController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.State;

/* loaded from: classes.dex */
public class DoubleExposureS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        State state = (State) this.target;
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            DoubleExposureUtil.getInstance().setImageSelection(true);
            Bundle bundle = new Bundle();
            Bundle data = new Bundle();
            data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
            bundle.putParcelable(S1OffEEState.STATE_NAME, data);
            Activity appRoot = state.getActivity();
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
        } else {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if (selectedTheme != null && selectedTheme.equalsIgnoreCase("Manual")) {
                DoubleExposureUtil.getInstance().setTransitionFlag(true);
                DoubleExposureUtil.getInstance().setCurrentMenuSelectionScreen(DoubleExposureConstant.MODE_SELECTION);
                state.setNextState("EE", null);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedExposureCompensationCustomKey() {
        int retVal = super.incrementedExposureCompensationCustomKey();
        AppLog.enter(TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().updateExposureCompensationCustomKey();
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedExposureCompensationCustomKey() {
        int retVal = super.decrementedExposureCompensationCustomKey();
        AppLog.enter(TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().updateExposureCompensationCustomKey();
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (ThemeSelectionController.SOFTFILTER.equals(selectedTheme) && DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen)) {
            CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
            return -1;
        }
        int ret = super.pushedMfAssistCustomKey();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1 && !DoubleExposureUtil.getInstance().isInitFocusMode()) {
            DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
            cameraSettings.setFocusMode(FocusModeController.getInstance().getFocusModeFromFocusModeDial());
            AppLog.exit(TAG, "false == isInitFocusMode");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.turnedFocusModeDial();
    }
}
