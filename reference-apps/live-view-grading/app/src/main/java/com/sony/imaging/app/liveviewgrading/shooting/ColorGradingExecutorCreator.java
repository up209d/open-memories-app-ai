package com.sony.imaging.app.liveviewgrading.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingExposureModeController;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class ColorGradingExecutorCreator extends ExecutorCreator {
    private NormalBurstExecutor mNormalBurstExecutor = new NormalBurstExecutor();
    private EffectProcess mEffectProcess = new EffectProcess();
    private CompositProcess mCompositProcess = new CompositProcess();
    private SingleProcess mSingleProcess = new SingleProcess();
    private BracketProcess mBracketProcess = new BracketProcess();
    private boolean mSpecial = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        if (isSpecial()) {
            IProcess process = this.mEffectProcess;
            return process;
        }
        IProcess process2 = this.mSingleProcess;
        return process2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public Class<?> getNextExecutor() {
        if (getRecordingMode() == 1 && !this.mIsRecModeChanging) {
            DriveModeController cntl = DriveModeController.getInstance();
            if (DriveModeController.SPEED_PRIORITY_BURST == cntl.getValue()) {
                return NormalBurstExecutor.class;
            }
        }
        return super.getNextExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        if (NormalBurstExecutor.class.equals(clazz)) {
            BaseShootingExecutor executor = this.mNormalBurstExecutor;
            return executor;
        }
        BaseShootingExecutor executor2 = super.getExecutor(clazz);
        return executor2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        return false;
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
        ColorGradingExposureModeController emc = ColorGradingExposureModeController.getInstance();
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
    public int getRecordingMode() {
        return 2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public int getSupportingRecMode() {
        return 2;
    }
}
