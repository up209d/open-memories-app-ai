package com.sony.imaging.app.portraitbeauty.shooting.keyhandler;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class PortraitBeautyS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String CAPTURE_STATE = "Capture";
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String PTAG_START_AUTOFOCUS = "StartAutoFocus";
    public static boolean bIsAdjustModeGuide = false;
    String currentExposureMode = ExposureModeController.getInstance().getValue(null);

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            return -1;
        }
        PortraitBeautyDisplayModeObserver.getInstance().toggleDisplayMode(0);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        if (this.currentExposureMode.equalsIgnoreCase(ExposureModeController.PROGRAM_AUTO_MODE)) {
            return -1;
        }
        return super.incrementedProgramShiftCustomKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        if (this.currentExposureMode.equalsIgnoreCase(ExposureModeController.PROGRAM_AUTO_MODE)) {
            return -1;
        }
        return super.decrementedProgramShiftCustomKey();
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, "ApplicationSettings");
        openMenu(bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            switch (scanCode) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    return -1;
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    return 0;
                default:
                    int ret = super.onKeyDown(keyCode, event);
                    return ret;
            }
        }
        int ret2 = super.onKeyDown(keyCode, event);
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        int ret = -1;
        if (CustomizableFunction.DigitalZoom.equals(keyFunction)) {
            CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_ID_INVALID_THIS_FUNCTION);
            return -1;
        }
        openMenuWithCustomKey(keyFunction);
        if (keyFunction.isValid()) {
            ret = 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor(INH_ID_FILE_WRITING)) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return 0;
        }
        if (!DriveModeController.getInstance().isRemoteControl()) {
            return 1;
        }
        PTag.start("StartAutoFocus");
        ExecutorCreator executorCreator = ExecutorCreator.getInstance();
        if (!executorCreator.isAElockedOnAutoFocus()) {
            executorCreator.getSequence().autoFocus(null, "af_wiae");
        } else {
            executorCreator.getSequence().autoFocus(null);
        }
        State state = (State) this.target;
        Bundle data = new Bundle();
        data.putBoolean("TakePicByRemote", true);
        state.setNextState(CAPTURE_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor(INH_ID_FILE_WRITING)) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return 0;
        }
        if (!DriveModeController.getInstance().isRemoteControl()) {
            return 1;
        }
        PTag.start("StartAutoFocus");
        ExecutorCreator executorCreator = ExecutorCreator.getInstance();
        if (!executorCreator.isAElockedOnAutoFocus()) {
            executorCreator.getSequence().autoFocus(null, "af_wiae");
        } else {
            executorCreator.getSequence().autoFocus(null);
        }
        State state = (State) this.target;
        Bundle data = new Bundle();
        data.putBoolean("TakePicByRemote", true);
        state.setNextState(CAPTURE_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    public void openMenuWithCustomKey(IKeyFunction keyFunction) {
        String itemId = keyFunction.getItemIdForMenu();
        if (itemId != null) {
            BaseMenuService service = new BaseMenuService(this.target.getActivity());
            String layoutID = service.getMenuItemCustomStartLayoutID(itemId);
            Bundle bundle = new Bundle();
            bundle.putString(MenuState.ITEM_ID, itemId);
            bundle.putString(MenuState.LAYOUT_ID, layoutID);
            openMenu(bundle);
            return;
        }
        pushedInvalidCustomKey(keyFunction);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        displayCaution(PortraitBeautyInfo.CAUTION_ID_DLAPP_MODE_DIAL_INVALID);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        PortraitBeautyUtil.getInstance().setLensAttachedOnShootingScreen(true);
        return super.onLensAttached(event);
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.portraitbeauty.shooting.keyhandler.PortraitBeautyS1OffEEStateKeyHandler.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case 103:
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case 232:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 1;
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    case AppRoot.USER_KEYCODE.FN /* 520 */:
                    case AppRoot.USER_KEYCODE.AEL /* 532 */:
                    case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                    case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                    case AppRoot.USER_KEYCODE.DISP /* 608 */:
                    case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                    case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                    case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                    case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                        return -1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
                            FocusModeController focusModeController = FocusModeController.getInstance();
                            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
                        }
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
    }
}
