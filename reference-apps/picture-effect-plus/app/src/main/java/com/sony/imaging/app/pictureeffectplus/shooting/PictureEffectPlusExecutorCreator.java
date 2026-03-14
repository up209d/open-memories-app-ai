package com.sony.imaging.app.pictureeffectplus.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusExposureModeController;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectPlusExecutorCreator extends ExecutorCreator {
    private static final int SA_EFFECT_OFF = 0;
    private static final int SA_EFFECT_ON = 1;
    private static final String TAG = AppLog.getClassName();
    private EffectProcess mEffectProcess = new EffectProcess();
    private CompositProcess mCompositProcess = new CompositProcess();
    private SingleProcess mSingleProcess = new SingleProcess();
    private BracketProcess mBracketProcess = new BracketProcess();
    private boolean mSpecial = false;
    private boolean mIsCautionDisplaying = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        IProcess process;
        AppLog.enter(TAG, AppLog.getMethodName());
        setExposureMode();
        if (isSpecial()) {
            PictureEffectPlusController.getInstance().setMiniatureForSpecialSequence(true);
            process = this.mEffectProcess;
            Log.i(TAG, "getProcess EffectProcess");
        } else {
            process = this.mSingleProcess;
            PictureEffectPlusController.getInstance().setMiniatureForSpecialSequence(false);
            Log.i(TAG, "getProcess SingleProcess");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return process;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DriveModeController cntl = DriveModeController.getInstance();
        boolean spinal = (cntl.isSelfTimer() || cntl.isRemoteControl()) ? false : true;
        if (getIsCautionDisplaying()) {
            spinal = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return spinal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PictureEffectPlusController cntl = PictureEffectPlusController.getInstance();
        if (PictureEffectPlusController.getDriveModeBeforeSAEffect() == null) {
            String driveMode = DriveModeController.getInstance().getValue();
            PictureEffectPlusController.setDriveModeBeforeSAEffect(driveMode);
        } else if (PictureEffectPlusController.getInstance().getSAEffectStatus() == 0 && PictureEffectPlusController.getInstance().getMiniatureStatus() == 0) {
            String driveMode2 = DriveModeController.getInstance().getValue();
            PictureEffectPlusController.setDriveModeBeforeSAEffect(driveMode2);
        }
        int miniStatus = cntl.getMiniatureStatus();
        Log.i(TAG, "miniStatus:" + miniStatus + "effect:" + cntl.getMiniCombEffect());
        PictureEffectPlusController.getInstance().setSADriveMode();
        switch (miniStatus) {
            case 0:
                this.mSpecial = false;
                break;
            case 1:
                cntl.setMiniatureOnlySetting();
                this.mSpecial = false;
                break;
            case 2:
                this.mSpecial = true;
                break;
            default:
                this.mSpecial = false;
                break;
        }
        AppLog.enter(TAG, AppLog.getMethodName() + ":::::: Special Value ::::::" + this.mSpecial);
        return this.mSpecial;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    public void setIsCautionDisplaying(boolean isCautionDisplaying) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mIsCautionDisplaying = isCautionDisplaying;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean getIsCautionDisplaying() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "mIsCautionDisplaying:" + this.mIsCautionDisplaying);
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mIsCautionDisplaying;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void settingBeforeInitialized() {
        String expMode = ExposureModeController.getInstance().getValue(null);
        PictureEffectPlusExposureModeController emc = PictureEffectPlusExposureModeController.getInstance();
        if (!emc.isValidValue(expMode)) {
            if (1 == getRecordingMode()) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
                return;
            }
            List<String> supported = emc.getSupportedValue(ExposureModeController.EXPOSURE_MODE);
            if (supported.contains("movie")) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "movie");
            } else if (supported.contains(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
            } else if (supported.contains(ExposureModeController.MOVIE_AUTO)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_AUTO);
            }
        }
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial() && !PictureEffectPlusExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }
}
