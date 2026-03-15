package com.sony.imaging.app.lightshaft.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.shooting.LightShaftEffectProcess;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class LightShaftExecutorCreator extends ExecutorCreator {
    private static final String TAG = "LightShaftExecutorCreator";
    private boolean mIsSettingDone = false;
    private LightShaftEffectProcess mCaptureProcess = new LightShaftEffectProcess();
    private boolean mIsCautionDisplaying = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        setDefaultSettings();
        return this.mCaptureProcess;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        boolean isSpinal = !DriveModeController.getInstance().isRemoteControl();
        AppLog.info(TAG, "isSpinal called from LightShaftExecutorCreator returns " + isSpinal);
        return isSpinal;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        return true;
    }

    public boolean isInitialSettingDone() {
        return this.mIsSettingDone;
    }

    public void setInitialSettingRequired(boolean mIsSettingDone) {
        this.mIsSettingDone = mIsSettingDone;
    }

    private void setDriveModeToSingle() {
        DriveModeController dvCntl = DriveModeController.getInstance();
        if (dvCntl != null) {
            String dvStr = dvCntl.getValue();
            if (dvStr != DriveModeController.SINGLE) {
                dvCntl.setValue(DriveModeController.DRIVEMODE, DriveModeController.SINGLE);
            }
        }
    }

    private void setDefaultSettings() {
        if (isInitialSettingDone()) {
            setInitialSettingRequired(false);
            AppLog.info(TAG, "setDefaultSettings done");
            setDriveModeToSingle();
            setDROToOff();
        }
        setExposureMode();
    }

    private void setDROToOff() {
        DROAutoHDRController droCntl = DROAutoHDRController.getInstance();
        if (droCntl != null) {
            String dvStr = droCntl.getValue();
            if (dvStr != "off") {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
            }
        }
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial() && !LightShaftExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        return false;
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
        ExposureModeController emc = ExposureModeController.getInstance();
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
    public boolean isEnableDigitalZoom() {
        return false;
    }
}
