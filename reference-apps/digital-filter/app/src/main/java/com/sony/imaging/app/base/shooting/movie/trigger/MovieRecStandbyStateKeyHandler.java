package com.sony.imaging.app.base.shooting.movie.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.util.List;

/* loaded from: classes.dex */
public class MovieRecStandbyStateKeyHandler extends MovieKeyHandlerBase {
    private static final String EXIT_TO_WIFI = "ExitToWifi";
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String MENU_STATE = "Menu";
    private static final String MOVIE_CAPTURE_STATE = "MovieCapture";
    private static final String TAG = "MovieRecStandbyStateKeyHandler";
    protected final int NEXT = 1;
    protected final int PREV = -1;

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieLockKey() {
        if (FocusModeController.getInstance().isFocusControl()) {
            return 1;
        }
        int ret = pushedS1Key();
        return ret;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieLockKey() {
        return releasedS1Key();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieEELockKey() {
        if (FocusModeController.getInstance().isFocusControl()) {
            return 1;
        }
        int ret = pushedS1Key();
        return ret;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieEELockKey() {
        return releasedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        CautionUtilityClass.getInstance().requestTrigger(2058);
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        transitionMovieCapture();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        transitionMovieCapture();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        boolean bMounted = mediaNotifier.isMounted();
        AvailableInfo.update();
        if (bMounted && AvailableInfo.isFactor(INH_ID_FILE_WRITING)) {
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return -1;
        }
        if (!DriveModeController.getInstance().isRemoteControl()) {
            return -1;
        }
        transitionMovieCapture();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedExpandFocusKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.START_FACTOR, MenuState.FACTOR_FN_KEY);
        openMenu(bundle);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    public void openMenu(Bundle bundle) {
        State state = (State) this.target;
        state.setNextState("Menu", bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        ((MovieRecStandbyState) this.target).transitionStillMode();
        return -1;
    }

    protected void transitionMovieCapture() {
        Bundle data = new Bundle();
        data.putString(MovieCaptureState.MOVIE_REC_FROM, MovieCaptureState.FROM_MOVIE);
        State state = (State) this.target;
        state.setNextState(MOVIE_CAPTURE_STATE, data);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        Integer intId;
        int ret = -1;
        if (keyFunction.isValid()) {
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
            ret = 1;
        } else {
            String itemid = keyFunction.getItemIdForMenu();
            BaseMenuService service = new BaseMenuService(this.target.getActivity());
            int[] cautionId = service.getMenuItemCautionId(itemid);
            if (cautionId != null) {
                IController controller = service.getExecClassInstance(itemid);
                int index = controller.getCautionIndex(itemid);
                if (index < 0) {
                    if (BaseMenuService.CAUTION_ID_DICTIONARY.containsKey(Integer.valueOf(index)) && (intId = BaseMenuService.CAUTION_ID_DICTIONARY.get(Integer.valueOf(index))) != null) {
                        CautionUtilityClass.getInstance().requestTrigger(intId.intValue());
                    }
                } else if (index >= 0 && index < cautionId.length) {
                    CautionUtilityClass.getInstance().requestTrigger(cautionId[index]);
                }
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    public boolean openMenuBy(int distance) {
        BaseMenuService service = new BaseMenuService(this.target.getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(MenuController.SEGMENT_1ST_LAYER);
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, MenuController.SEGMENT_1ST_LAYER);
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    public boolean isPushedKey(int scanCode) {
        KeyStatus key = ScalarInput.getKeyStatus(scanCode);
        if (key.status != 1) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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
}
