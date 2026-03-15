package com.sony.imaging.app.smoothreflection.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class SAEProtoExecutorCreator extends ExecutorCreator {
    private static final String TAG = AppLog.getClassName();
    public static boolean isHalt = false;
    private NormalBurstExecutor mNormalBurstExecutor = new NormalBurstExecutor();
    private SmoothExecutor mSmoothExecutor = new SmoothExecutor();
    private SmoothReflectionCompositProcess mCompositProcessSR = new SmoothReflectionCompositProcess();
    private IProcess mCompositProcess = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        isHalt = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        setDefaultCameraSettings();
        this.mCompositProcess = this.mCompositProcessSR;
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCompositProcess;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected Class<?> getNextExecutor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DriveModeController cntl = DriveModeController.getInstance();
        if (DriveModeController.SPEED_PRIORITY_BURST == cntl.getValue()) {
            return NormalBurstExecutor.class;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return SmoothExecutor.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        BaseShootingExecutor executor;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (NormalBurstExecutor.class.equals(clazz)) {
            executor = this.mNormalBurstExecutor;
        } else {
            executor = this.mSmoothExecutor;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return executor;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void halt() {
        AppLog.enter(TAG, AppLog.getMethodName());
        isHalt = true;
        super.halt();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void settingBeforeInitialized() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String expMode = ExposureModeController.getInstance().getValue(null);
        ExposureModeController emc = ExposureModeController.getInstance();
        if (!emc.isValidValue(expMode)) {
            if (1 == getRecordingMode()) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
            } else {
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
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isEnableDigitalZoom() {
        return false;
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial() && !SmoothReflectionExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }

    private void setDefaultCameraSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        FaceDetectionController.getInstance().setValue("off", "off");
        FlashController.getInstance().setValue(FlashController.FLASHMODE, "off");
        FlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, ISOSensitivityController.ISO_AUTO);
        setExposureMode();
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
