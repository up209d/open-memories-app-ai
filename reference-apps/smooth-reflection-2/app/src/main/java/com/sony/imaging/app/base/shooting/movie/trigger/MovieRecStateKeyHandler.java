package com.sony.imaging.app.base.shooting.movie.trigger;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.AvailableInfo;

/* loaded from: classes.dex */
public class MovieRecStateKeyHandler extends MovieKeyHandlerBase {
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String MENU_STATE = "Menu";
    private static final String TAG = "MovieRecStateKeyHandler";

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

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.i(TAG, "pushedMovieRecKey target:" + this.target.getClass());
        Log.i(TAG, "Executor :" + ExecutorCreator.getInstance().getSequence());
        ((MovieRecState) this.target).stopMovieRec();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return pushedMovieRecKey();
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
    public int pushedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
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
        ((MovieRecState) this.target).stopMovieRec();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        MovieRecState state = (MovieRecState) this.target;
        state.turnModeDail();
        state.stopMovieRec();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedExpandFocusKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.START_FACTOR, MenuState.FACTOR_FN_KEY);
        openMenu(bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler
    protected void openMenu(Bundle bundle) {
        ContainerState state = (ContainerState) this.target;
        state.addChildState("Menu", bundle);
        state.closeLayout(StateBase.FOCUS_LAYOUT);
        state.closeLayout(StateBase.DEFAULT_LAYOUT);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
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
            } else if (CustomizableFunction.ExposureMode.equals(keyFunction)) {
                return -1;
            }
            openMenuWithCustomKey(keyFunction);
            ret = 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        ((IUserChanging) this.target).changeExposure();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            return 1;
        }
        return 1;
    }
}
