package com.sony.imaging.app.base.shooting.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.List;

/* loaded from: classes.dex */
public class CustomWhiteBalanceExposureKeyHandler extends ShootingKeyHandlerBase {
    private static final String BUNDLE_KEY_CODE = "keyCode";
    private static final String ITEM_ID = "ItemId";
    private static final String MENU_STATE = "EE";
    private static final String MOVIE_CAPTURE_STATE = "MovieCapture";
    private static final String NEXT_STATE = "EE";
    private static final String TAG = "CustomWhiteBalanceExposureKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        WhiteBalanceController controller = WhiteBalanceController.getInstance();
        List<String> supportCWBlist = controller.getSupportedCustomWhiteBalance();
        int customNum = supportCWBlist.size();
        if (1 >= customNum) {
            return -1;
        }
        int current = controller.getCustomWhiteBalanceNum(controller.getValue()) + 1;
        if (customNum < current) {
            current = 1;
        }
        controller.setCustomWhiteBalance(current);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        WhiteBalanceController controller = WhiteBalanceController.getInstance();
        List<String> supportCWBlist = controller.getSupportedCustomWhiteBalance();
        int customNum = supportCWBlist.size();
        if (1 >= customNum) {
            return -1;
        }
        int current = controller.getCustomWhiteBalanceNum(controller.getValue()) - 1;
        if (1 > current) {
            current = customNum;
        }
        controller.setCustomWhiteBalance(current);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        setCustomWhiteBalanceAndStateChange(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Bundle bundle = new Bundle();
        bundle.putInt("keyCode", AppRoot.USER_KEYCODE.S1_ON);
        setCustomWhiteBalanceAndStateChange(bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        setCustomWhiteBalanceAndStateChange(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        setCustomWhiteBalanceAndStateChange(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        saveCWB();
        int code = ModeDialDetector.getModeDialPosition();
        if (code != -1) {
            Bundle bundle = new Bundle();
            bundle.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
            StateBase state = (StateBase) this.target;
            state.setNextState("EE", bundle);
            ExecutorCreator.getInstance().updateSequence();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        if (Environment.isMovieAPISupported() && ((BaseApp) this.target.getActivity()).isMovieModeSupported()) {
            if (Settings.getMovieButtonMode() == 0) {
                saveCWB();
                String expMode = ExposureModeController.getInstance().getValue(null);
                if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
                    CameraSetting.getInstance().resetProgramLine();
                }
                Bundle data = new Bundle();
                data.putString(MovieCaptureState.MOVIE_REC_FROM, MovieCaptureState.FROM_STILL);
                State state = (State) this.target;
                state.setNextState(MOVIE_CAPTURE_STATE, data);
                return 1;
            }
            CautionUtilityClass.getInstance().requestTrigger(2057);
            return -1;
        }
        CautionUtilityClass.getInstance().requestTrigger(131077);
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        if (Environment.isMovieAPISupported() && ((BaseApp) this.target.getActivity()).isMovieModeSupported()) {
            saveCWB();
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
            String expMode = ExposureModeController.getInstance().getValue(null);
            if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
                CameraSetting.getInstance().resetProgramLine();
            }
            ExecutorCreator ec = ExecutorCreator.getInstance();
            ec.setRecordingMode(2, null);
            State state = (State) this.target;
            state.setNextState("EE", null);
            return 1;
        }
        CautionUtilityClass.getInstance().requestTrigger(131077);
        return -1;
    }

    private void setCustomWhiteBalanceAndStateChange(Bundle bundle) {
        saveCWB();
        StateBase state = (StateBase) this.target;
        state.setNextState("EE", bundle);
    }

    private void saveCWB() {
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        wbc.saveCustomWhiteBalance(wbc.getValue());
    }
}
