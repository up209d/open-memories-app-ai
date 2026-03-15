package com.sony.imaging.app.base.shooting.trigger;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class FocusAdjustmentKeyHandler extends MfAssistKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        returnToNormalEE();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            super.pushedLeftKey();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            super.pushedRightKey();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            super.pushedUpKey();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            super.pushedDownKey();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        controller.moveFocusDriveFar();
        controller.rescheduleTimeout();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        controller.moveFocusDriveNear();
        controller.rescheduleTimeout();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        return turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        return turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        returnToNormalEEFocusCancel();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedResetFuncKey() {
        return pushedDeleteKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (isMovieRecording()) {
            return -1;
        }
        int ret = super.pushedPlayBackKey();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        if (isMovieRecording()) {
            return -1;
        }
        int ret = super.pushedPlayIndexKey();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler
    protected void returnToNormalEE() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            controller.stop();
        } else {
            FocusAdjustmentState state = (FocusAdjustmentState) this.target;
            state.returnToNormalEE();
        }
    }

    protected void returnToNormalEEFocusCancel() {
        FocusAdjustmentState state = (FocusAdjustmentState) this.target;
        state.setPushedMenuKeyFlg();
        returnToNormalEE();
    }

    protected boolean isMovieRecording() {
        if (!Environment.isMovieAPISupported() || !((BaseApp) this.target.getActivity()).isMovieModeSupported()) {
            return false;
        }
        int recmode = ExecutorCreator.getInstance().getRecordingMode();
        return (2 == recmode || 8 == recmode) && MovieShootingExecutor.isMovieRecording();
    }
}
