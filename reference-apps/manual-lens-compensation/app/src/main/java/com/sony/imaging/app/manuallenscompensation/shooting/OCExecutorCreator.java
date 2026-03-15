package com.sony.imaging.app.manuallenscompensation.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class OCExecutorCreator extends ExecutorCreator {
    private static final String TAG = "OCExecutorCreator";
    private SingleProcess mSingleProcess = new SingleProcess();
    private boolean mSpecial = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        OCController.getInstance().enable(true);
        return this.mSingleProcess;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        AppLog.info(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        return this.mSpecial;
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

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isInheritRawRecMode() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isFocusHoldEnabled() {
        return false;
    }
}
