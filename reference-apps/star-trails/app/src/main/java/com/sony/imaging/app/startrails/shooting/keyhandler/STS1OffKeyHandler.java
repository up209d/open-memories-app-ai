package com.sony.imaging.app.startrails.shooting.keyhandler;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class STS1OffKeyHandler extends S1OffEEStateKeyHandler {
    private static final int ISV_KEY_S1_2 = 517;
    public static final String STMFASSIST_STATE = "MfAssist";
    private static String TAG = "STS1OffKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!STUtility.getInstance().isCapturingStarted()) {
            STDisplayModeObserver.getInstance().toggleDisplayMode(0);
            AppLog.exit(TAG, AppLog.getMethodName());
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        STUtility.getInstance().setPreTakePictureTestShot(false);
        if (STUtility.getInstance().isFromPlayback()) {
            if (event.getScanCode() == 518) {
                return 1;
            }
            if (event.getScanCode() != ISV_KEY_S1_2) {
                STUtility.getInstance().setFromPlayback(false);
            }
        }
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            if (STUtility.getInstance().isApplySettingFinish()) {
                AppLog.enter(TAG, "event.getScanCode() " + event.getScanCode());
                STUtility.getInstance().setPreTakePictureTestShot(true);
                ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
            }
        } else if (event.getScanCode() == 232) {
            pushedMfAssistCustomKey();
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        boolean ret = STExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        if (ret) {
            ((IUserChanging) this.target).changeExposure();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        int returnState = -1;
        if (MediaNotificationManager.getInstance().isError()) {
            return 1;
        }
        STUtility.getInstance().pinCalendar();
        int cautionId = STUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            if (cautionId == 131205) {
                CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, new RemainingMemoryLessDispatcher());
                CautionUtilityClass.getInstance().requestTrigger(cautionId);
            }
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
        } else {
            returnState = super.pushedIRShutterNotCheckDrivemodeKey();
        }
        return returnState;
    }

    /* loaded from: classes.dex */
    private class RemainingMemoryLessDispatcher extends IkeyDispatchEach {
        private RemainingMemoryLessDispatcher() {
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
            if (CustomizableFunction.Unchanged.equals(func)) {
                int code = event.getScanCode();
                if (code == 0) {
                    code = event.getKeyCode();
                }
                if (event.getAction() == 0) {
                    switch (code) {
                        case 23:
                        case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                            STS1OffKeyHandler.this.startCapturing();
                            CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                            return 1;
                        case 82:
                        case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                        case AppRoot.USER_KEYCODE.MENU /* 514 */:
                            CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                            return 1;
                        case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                            break;
                        case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                        case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                        case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                        default:
                            return 0;
                        case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                            if (STUtility.getInstance().getCurrentTrail() == 2) {
                                CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                                return 0;
                            }
                            return -1;
                        case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                            STUtility.getInstance().setEVDialRotated(true);
                            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                            break;
                    }
                    CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                    return 0;
                }
                if (event.getAction() != 0) {
                    return 0;
                }
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                        return 0;
                    default:
                        return 0;
                }
            }
            return 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCapturing() {
        super.pushedIRShutterNotCheckDrivemodeKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        int ret = -1;
        if (CustomizableFunction.DigitalZoom.equals(keyFunction)) {
            if (!DigitalZoomController.isPFverOver1()) {
                return pushedInvalidCustomKey(CustomizableFunction.MfAssist);
            }
            if (!ExecutorCreator.getInstance().isEnableDigitalZoom()) {
                CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_ID_INVALID_THIS_FUNCTION);
                return -1;
            }
            if (!DigitalZoomController.getInstance().isAvailable(null)) {
                CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_ZOOM_INVALID_GUIDE);
                return -1;
            }
        } else if (CustomizableFunction.ExposureCompensation.equals(keyFunction) && STUtility.getInstance().getCurrentTrail() == 0) {
            openMenuWithCustomKey(keyFunction);
        } else if (STUtility.getInstance().getCurrentTrail() != 2 && STConstants.SELECTED_MENU_ITEM_THEME.contains(String.valueOf(keyFunction))) {
            CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_INVALID_THEME);
        } else {
            openMenuWithCustomKey(keyFunction);
        }
        if (keyFunction.isValid()) {
            ret = 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        displayCaution();
        return -1;
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        if (STUtility.getInstance().getCurrentTrail() != 0) {
            CameraSetting.getInstance().incrementShutterSpeed();
            ((IUserChanging) this.target).changeShutterSpeed();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        if (STUtility.getInstance().getCurrentTrail() != 0) {
            CameraSetting.getInstance().decrementShutterSpeed();
            ((IUserChanging) this.target).changeShutterSpeed();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedTvAvChangeCustomKey() {
        toggleDialTvAv();
        if (isDialTvAvToggled() && STUtility.getInstance().getCurrentTrail() != 1) {
            ((IUserChanging) this.target).changeAperture();
        } else {
            ((IUserChanging) this.target).changeShutterSpeed();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        if (STUtility.getInstance().getCurrentTrail() != 1) {
            CameraSetting.getInstance().incrementAperture();
            ((IUserChanging) this.target).changeAperture();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        if (STUtility.getInstance().getCurrentTrail() != 1) {
            CameraSetting.getInstance().decrementAperture();
            ((IUserChanging) this.target).changeAperture();
        }
        return 1;
    }
}
