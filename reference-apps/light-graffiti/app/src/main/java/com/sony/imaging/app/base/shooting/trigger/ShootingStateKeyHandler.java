package com.sony.imaging.app.base.shooting.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class ShootingStateKeyHandler extends ShootingKeyHandlerBase {
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String MOVIE_CAPTURE_STATE = "MovieCapture";
    private static final String PTAG_RELEASE_LAG_END = "start shutter lag";
    private static final String PTAG_START_AUTOFOCUS = "StartAutoFocus";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        PTag.start("StartAutoFocus");
        ExecutorCreator executorCreator = ExecutorCreator.getInstance();
        if (!executorCreator.isAElockedOnAutoFocus()) {
            String behavior = null;
            String focusMode = AutoFocusModeController.getInstance().getValue();
            if ("af-s".equals(focusMode)) {
                behavior = "af_woaf";
            } else if ("af-c".equals(focusMode)) {
                behavior = "afc_woaf";
            }
            executorCreator.getSequence().autoFocus(null, behavior);
            return 1;
        }
        executorCreator.getSequence().autoFocus(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        ExecutorCreator executor = ExecutorCreator.getInstance();
        PTag.start(PTAG_RELEASE_LAG_END);
        executor.getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (!ShootingExecutor.class.isInstance(executor)) {
            return 1;
        }
        executor.cancelTakePicture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AELController.getInstance().holdAELock(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        FocusModeController.getInstance().holdFocusControl(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AELController.getInstance().cancelAELock();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AELController.getInstance().cancelAELock();
        return 1;
    }

    protected boolean isBulbOpenedBy(int shutterType) {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        return executor.isBulbOpened() && executor.getShutterType() == shutterType;
    }

    protected void stopBulbShooting() {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (executor.isBulbOpened()) {
            executor.cancelTakePicture();
            DriveModeController cntl = DriveModeController.getInstance();
            if (cntl.isRemoteControl()) {
                cntl.setTempSelfTimer(0);
            }
        }
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedFocusHoldCustomKey() {
        return CameraSetting.getInstance().stopFocusHold() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        if (Environment.isMovieAPISupported() && ((BaseApp) this.target.getActivity()).isMovieModeSupported()) {
            if (ModeDialDetector.hasModeDial() && !ExposureModeController.getInstance().isValidDialPosition()) {
                return -1;
            }
            if (Settings.getMovieButtonMode() == 0) {
                boolean writing = false;
                if (AvindexStore.getExternalMediaIds()[0].equals(ExecutorCreator.getInstance().getSequence().getRecordingMedia())) {
                    AvailableInfo.update();
                    writing = AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING");
                }
                if (!writing) {
                    String expMode = ExposureModeController.getInstance().getValue(null);
                    if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
                        CameraSetting.getInstance().resetProgramLine();
                    }
                    Bundle data = new Bundle();
                    if (ExecutorCreator.getInstance().getRecordingMode() == 1) {
                        data.putString(MovieCaptureState.MOVIE_REC_FROM, MovieCaptureState.FROM_STILL);
                    } else if (ExecutorCreator.getInstance().getRecordingMode() == 4) {
                        data.putString(MovieCaptureState.MOVIE_REC_FROM, "Interval");
                    } else if (!MovieShootingExecutor.isMovieRecording()) {
                        data.putString(MovieCaptureState.MOVIE_REC_FROM, MovieCaptureState.FROM_MOVIE);
                    }
                    ShootingState state = (ShootingState) this.target;
                    state.replaceChildState(MOVIE_CAPTURE_STATE, data);
                    return 1;
                }
                CautionUtilityClass.getInstance().requestTrigger(1410);
                return -1;
            }
            CautionUtilityClass.getInstance().requestTrigger(2057);
            return -1;
        }
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
        return -1;
    }
}
