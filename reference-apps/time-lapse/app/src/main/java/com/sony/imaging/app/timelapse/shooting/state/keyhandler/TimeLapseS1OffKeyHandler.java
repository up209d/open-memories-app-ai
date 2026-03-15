package com.sony.imaging.app.timelapse.shooting.state.keyhandler;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.CustomKeySupportController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.base.TimeLapseDisplayModeObserver;
import com.sony.imaging.app.timelapse.shooting.controller.TLExposureCompensasionController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TestShotController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class TimeLapseS1OffKeyHandler extends S1OffEEStateKeyHandler {
    private static final String TAG = "TimeLapseS1OffKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        TimeLapseDisplayModeObserver.getInstance().toggleDisplayMode(0);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        CautionUtilityClass.getInstance().executeTerminate();
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        boolean ret = TLExposureCompensasionController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        if (ret) {
            ((IUserChanging) this.target).changeExposure();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            return super.incrementedApertureCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            return super.incrementedProgramShiftCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            return super.decrementedProgramShiftCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            return super.decrementedApertureCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomTeleLeverPushed(KeyEvent event) {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && DigitalZoomController.getInstance().isDigitalZoomStatus()) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
            return 1;
        }
        int retState = super.onZoomTeleLeverPushed(event);
        return retState;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomWideLeverPushed(KeyEvent event) {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && DigitalZoomController.getInstance().isDigitalZoomStatus()) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
            return 1;
        }
        int retState = super.onZoomTeleLeverPushed(event);
        return retState;
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
        TLCommonUtil.getInstance().pinCalendar();
        int cautionId = TLCommonUtil.getInstance().getCautionId();
        if (cautionId != 0) {
            if (cautionId == 131205) {
                CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, new RemainingMemoryLessDispatcher());
                CautionUtilityClass.getInstance().requestTrigger(cautionId);
            }
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
        } else {
            handleFocusLockScenarioforGPMA();
            returnState = super.pushedIRShutterNotCheckDrivemodeKey();
        }
        return returnState;
    }

    private void handleFocusLockScenarioforGPMA() {
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        boolean maybePhaseDiff = info != null && "A-mount".equalsIgnoreCase(info.LensType) && FocusAreaController.PHASE_SHIFT_SENSOR_UNKNOWN.equalsIgnoreCase(info.PhaseShiftSensor) && TLCommonUtil.getInstance().isSupportedVersion(2, 14);
        if (maybePhaseDiff) {
            CameraNotificationManager.OnFocusInfo obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
            if (obj == null || obj.status != 1) {
                AppLog.info(TAG, "maybePhaseDiff is true, so I cannot cancel S2 trigger.(Focus is not locked)");
                TLCommonUtil.getInstance().setMaybePhaseDiffFlag(true);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
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
                if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
                    CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
                    return -1;
                }
                CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_ZOOM_INVALID_GUIDE);
                return -1;
            }
        }
        openMenuWithCustomKey(keyFunction);
        if (keyFunction.isValid()) {
            ret = 1;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    public void openMenu(Bundle bundle) {
        AppLog.trace(TAG, "openMenu()");
        if (bundle != null) {
            String menuId = bundle.getString(MenuState.ITEM_ID);
            if (menuId == null) {
                menuId = bundle.getString(MenuState.START_FACTOR);
            }
            AppLog.trace(TAG, "openMenu()  menu Id " + menuId);
            if (!CustomKeySupportController.getInstance().isSupportedMenu(menuId) && !menuId.equals(MenuState.FACTOR_FN_KEY)) {
                AppLog.trace(TAG, "openMenu()  menu Id not supported  for custom key= " + menuId);
                CautionUtilityClass.getInstance().requestTrigger(CustomKeySupportController.getInstance().getCautionID(menuId));
                return;
            } else {
                super.openMenu(bundle);
                return;
            }
        }
        super.openMenu(bundle);
    }

    /* loaded from: classes.dex */
    private class RemainingMemoryLessDispatcher extends IkeyDispatchEach {
        private RemainingMemoryLessDispatcher() {
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                return 0;
            }
            int code = event.getScanCode();
            if (code == 0) {
                code = event.getKeyCode();
            }
            if (event.getAction() == 0) {
                switch (code) {
                    case 23:
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        TimeLapseS1OffKeyHandler.this.cancelCapturing();
                        if (TLCommonUtil.getInstance().isOkFocusedOnRemainingMemroryCaution()) {
                            TimeLapseStableLayout.isCapturing = true;
                            TimeLapseS1OffKeyHandler.this.startCapturing();
                        } else {
                            TimeLapseStableLayout.isCapturing = false;
                        }
                        return 1;
                    case 82:
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        TimeLapseS1OffKeyHandler.this.cancelCapturing();
                        return 1;
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        break;
                    case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                    case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                    default:
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        TimeLapseConstants.slastStateExposureCompansasion = ExposureCompensationController.getInstance().getExposureCompensationIndex();
                        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                        break;
                }
                TimeLapseS1OffKeyHandler.this.cancelCapturing();
                return 0;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelCapturing() {
        CautionUtilityClass.getInstance().disapperTrigger(TimelapseInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCapturing() {
        super.pushedIRShutterNotCheckDrivemodeKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        TLCommonUtil.getInstance().setTestShot(false);
        if (TLCommonUtil.getInstance().isS2_ONFromPlayBack()) {
            if (event.getScanCode() == 518) {
                return 1;
            }
            if (event.getScanCode() != 517) {
                TLCommonUtil.getInstance().setS2_ONFromPlayBack(false);
            }
        }
        if ((event.getScanCode() == 595 || event.getScanCode() == 513) && TestShotController.TESTSHOT_ON.equalsIgnoreCase(TestShotController.getInstance().getValue(TestShotController.TESTSHOT_ASSIGN_KEY))) {
            TLCommonUtil.getInstance().setTestShot(true);
            ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
        } else if (event.getScanCode() == 530) {
            if (TLCommonUtil.getInstance().hasAngleShiftAddon() && TLCommonUtil.getInstance().getCurrentPictureWidth() < 1920) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_SIZE);
            }
            retVal = super.onKeyDown(keyCode, event);
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }
}
