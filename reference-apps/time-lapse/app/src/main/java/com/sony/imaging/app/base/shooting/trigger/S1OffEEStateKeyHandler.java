package com.sony.imaging.app.base.shooting.trigger;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.List;

/* loaded from: classes.dex */
public class S1OffEEStateKeyHandler extends ShootingKeyHandlerBase {
    private static final String CAPTURE_STATE = "Capture";
    private static final String EXIT_TO_WIFI = "ExitToWifi";
    protected static final String FOCUS_ADJUSTMENT_STATE = "FocusAdjustment";
    private static final String HALF_SHUTTER_STATE = "S1OnEE";
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String ITEM_ID = "ItemId";
    private static final String LOG_STR_CUSTOMKEY_NO_SUPPORT = "Controller Class is not implemented yet or Controller Class is not connected with CustomKey class.";
    private static final String LOG_STR_IUSER_CHANGING_NO_SUPPORT = "Current State does not support IUserChanging IF";
    private static final String MENU_STATE = "Menu";
    private static final String PTAG_AE_LOCK = "Start AE Lock";
    private static final String PTAG_TRANSITION_PLAYBACK = "transition from EE to playback";
    private static final String TAG = "S1OffEEStateKeyHandler";
    protected final int NEXT = 1;
    protected final int PREV = -1;

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        try {
            ((IUserChanging) this.target).changeOther();
        } catch (ClassCastException e) {
            Log.w(TAG, LOG_STR_IUSER_CHANGING_NO_SUPPORT);
        }
        boolean handled = false;
        int scanCode = event.getScanCode();
        if (scanCode == 232) {
            handled = openFocusAdjsutment();
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean openMenuBy(int distance) {
        BaseMenuService service = new BaseMenuService(this.target.getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(MenuController.SEGMENT_1ST_LAYER);
        Bundle bundle = new Bundle();
        bundle.putString("ItemId", MenuController.SEGMENT_1ST_LAYER);
        bundle.putString(MenuState.LAYOUT_ID, layoutID);
        String currentMode = MenuController.getInstance().convertFromRecMode(ExecutorCreator.getInstance().getRecordingMode());
        List<String> menuItems = service.getMenuItemList();
        int selection = menuItems.indexOf(currentMode);
        if (-1 == selection) {
            selection = 0;
        }
        int size = menuItems.size();
        String item = menuItems.get(((selection + size) + distance) % size);
        bundle.putString(BaseMenuLayout.BUNDLE_KEY_SELECTED_ITEM, item);
        openMenu(bundle);
        return true;
    }

    public boolean isPushedKey(int scanCode) {
        KeyStatus key = ScalarInput.getKeyStatus(scanCode);
        if (key.status != 1) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            if (!isPushedKey(AppRoot.USER_KEYCODE.RIGHT)) {
                return !openMenuBy(-1) ? -1 : 1;
            }
            Bundle bundle = new Bundle();
            State state = (State) this.target;
            state.setNextState(EXIT_TO_WIFI, bundle);
            return 1;
        }
        return super.pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            if (!isPushedKey(AppRoot.USER_KEYCODE.LEFT)) {
                return !openMenuBy(1) ? -1 : 1;
            }
            Bundle bundle = new Bundle();
            State state = (State) this.target;
            state.setNextState(EXIT_TO_WIFI, bundle);
            return 1;
        }
        return super.pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedInvalidCustomKey(IKeyFunction keyFunction) {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedNoAssignedCustomKey(IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        DisplayModeObserver.getInstance().toggleDisplayMode(0);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        PTag.start(PTAG_AE_LOCK);
        if (AELController.getInstance().isAvailable(null)) {
            AELController.getInstance().holdAELock(true);
        } else {
            CautionUtilityClass.getInstance().requestTrigger(1574);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        if (AELController.getInstance().isAvailable(null)) {
            AELController.getInstance().repressAELock();
            return 1;
        }
        CautionUtilityClass.getInstance().requestTrigger(1574);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        FocusModeController.getInstance().holdFocusControl(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        FocusModeController.getInstance().toggleFocusControl();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFocusHoldCustomKey() {
        if (!CameraSetting.getInstance().isFocusHoldSupported()) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        if (FocusAreaController.getInstance().getSensorType() != 1) {
            return !CameraSetting.getInstance().startFocusHold() ? -1 : 1;
        }
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedFocusHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        if (!FocusMagnificationController.isSupportedByPf()) {
            return pushedInvalidCustomKey(CustomizableFunction.MfAssist);
        }
        int ret = -1;
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        List<Integer> supported = controller.getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO);
        if (supported != null && supported.size() > 0 && controller.isAvailable(FocusMagnificationController.ITEM_ID_FOCUS_MAGNIFICATION)) {
            controller.startFocusMagnifier();
            ret = 1;
        }
        if (ret == -1) {
            CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
            return ret;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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
        }
        openMenuWithCustomKey(keyFunction);
        if (keyFunction.isValid()) {
            ret = 1;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openMenuWithCustomKey(IKeyFunction keyFunction) {
        String itemId = keyFunction.getItemIdForMenu();
        if (itemId != null) {
            BaseMenuService service = new BaseMenuService(this.target.getActivity());
            String layoutID = service.getMenuItemCustomStartLayoutID(itemId);
            Bundle bundle = new Bundle();
            bundle.putString("ItemId", itemId);
            bundle.putString(MenuState.LAYOUT_ID, layoutID);
            openMenu(bundle);
            return;
        }
        pushedInvalidCustomKey(keyFunction);
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedExposureCompensationCustomKey() {
        ExposureCompensationController.getInstance().incrementExposureCompensation();
        ((IUserChanging) this.target).changeExposure();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedExposureCompensationCustomKey() {
        ExposureCompensationController.getInstance().decrementExposureCompensation();
        ((IUserChanging) this.target).changeExposure();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        CameraSetting.getInstance().incrementAperture();
        ((IUserChanging) this.target).changeAperture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        CameraSetting.getInstance().decrementAperture();
        ((IUserChanging) this.target).changeAperture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        CameraSetting.getInstance().incrementShutterSpeed();
        ((IUserChanging) this.target).changeShutterSpeed();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        CameraSetting.getInstance().decrementShutterSpeed();
        ((IUserChanging) this.target).changeShutterSpeed();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        CameraSetting.getInstance().incrementProgramLine();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        CameraSetting.getInstance().decrementProgramLine();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedLensApertureRingToLeft() {
        CameraSetting.getInstance().decrementAperture();
        ((IUserChanging) this.target).changeAperture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedLensApertureRingToRight() {
        CameraSetting.getInstance().incrementAperture();
        ((IUserChanging) this.target).changeAperture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedTvAvChangeCustomKey() {
        toggleDialTvAv();
        if (isDialTvAvToggled()) {
            ((IUserChanging) this.target).changeAperture();
            return 1;
        }
        ((IUserChanging) this.target).changeShutterSpeed();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedTvOrAvCustomKey() {
        if (isDialTvAvToggled()) {
            int ret = incrementedApertureCustomKey();
            return ret;
        }
        int ret2 = incrementedShutterSpeedCustomKey();
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedTvOrAvCustomKey() {
        if (isDialTvAvToggled()) {
            int ret = decrementedApertureCustomKey();
            return ret;
        }
        int ret2 = decrementedShutterSpeedCustomKey();
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedIsoSensitivityCustomKey() {
        ISOSensitivityController.getInstance().increment();
        ((IUserChanging) this.target).changeISOSensitivity();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedIsoSensitivityCustomKey() {
        ISOSensitivityController.getInstance().decrement();
        ((IUserChanging) this.target).changeISOSensitivity();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        if (!cntl.isScnMode()) {
            return -1;
        }
        if (Environment.getVersionPfAPI() >= 2) {
            int dialExposureSetting = Settings.getDialExposureCompensation();
            if (1 != dialExposureSetting) {
                return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
            }
            return -1;
        }
        return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        if (!cntl.isScnMode()) {
            return -1;
        }
        if (Environment.getVersionPfAPI() >= 2) {
            int dialExposureSetting = Settings.getDialExposureCompensation();
            if (1 != dialExposureSetting) {
                return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
            }
            return -1;
        }
        return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        if (cntl.isScnMode() && Environment.getVersionPfAPI() >= 2) {
            int dialExposureSetting = Settings.getDialExposureCompensation();
            if (1 == dialExposureSetting) {
                return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
            }
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        if (cntl.isScnMode() && Environment.getVersionPfAPI() >= 2) {
            int dialExposureSetting = Settings.getDialExposureCompensation();
            if (1 == dialExposureSetting) {
                return pushedSettingFuncCustomKey(CustomizableFunction.ScnSelection);
            }
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        State state = (State) this.target;
        state.setNextState(HALF_SHUTTER_STATE, null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openMenu(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PTag.start(PTAG_TRANSITION_PLAYBACK);
        State state = (State) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        PTag.start(PTAG_TRANSITION_PLAYBACK);
        State state = (State) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return 0;
        }
        if (!DriveModeController.getInstance().isRemoteControl()) {
            return 1;
        }
        State state = (State) this.target;
        Bundle data = new Bundle();
        data.putBoolean("TakePicByRemote", true);
        state.setNextState(CAPTURE_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return -1;
        }
        if (!DriveModeController.getInstance().isRemoteControl()) {
            return -1;
        }
        State state = (State) this.target;
        Bundle data = new Bundle();
        data.putBoolean("TakePicByRemote", true);
        state.setNextState(CAPTURE_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return 0;
        }
        DriveModeController cntl = DriveModeController.getInstance();
        if (!cntl.isRemoteControl()) {
            return 1;
        }
        cntl.setTempSelfTimer(1);
        State state = (State) this.target;
        Bundle data = new Bundle();
        data.putBoolean("TakePicByRemote", true);
        state.setNextState(CAPTURE_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        if (ModeDialDetector.hasModeDial()) {
            int code = ModeDialDetector.getModeDialPosition();
            if (code != -1) {
                Bundle bundle = new Bundle();
                bundle.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
                openMenu(bundle);
            }
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.START_FACTOR, MenuState.FACTOR_FN_KEY);
        openMenu(bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedExpandFocusKey() {
        return pushedMfAssistCustomKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        ((IUserChanging) this.target).changeExposure();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCenterKey() {
        return super.releasedCenterKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedZebraKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPeakingKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openMenu(Bundle bundle) {
        State state = (State) this.target;
        state.setNextState("Menu", bundle);
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pressedZoomLeverToTele() {
        return showZoomLeverInvalidCaution();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pressedZoomLeverToWide() {
        return showZoomLeverInvalidCaution();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
            AvailableInfo.update();
            if (!AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
                String expMode = ExposureModeController.getInstance().getValue(null);
                if ("ProgramAuto".equals(expMode)) {
                    CameraSetting.getInstance().resetProgramLine();
                }
                ExecutorCreator ec = ExecutorCreator.getInstance();
                ec.setRecordingMode(2, null);
                State state = (State) this.target;
                state.setNextState("EE", null);
                return -1;
            }
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return -1;
        }
        CautionUtilityClass.getInstance().requestTrigger(2057);
        return -1;
    }

    protected boolean openFocusAdjsutment() {
        if (!FocusModeController.isFocusShiftByControlWheel()) {
            return false;
        }
        String focusMode = FocusModeController.getInstance().getValue();
        if (!FocusModeController.MANUAL.equals(focusMode) && !FocusModeController.SMF.equals(focusMode)) {
            return false;
        }
        Bundle bundle = new Bundle();
        State state = (State) this.target;
        state.setNextState(FOCUS_ADJUSTMENT_STATE, bundle);
        return true;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        return 0;
    }
}
