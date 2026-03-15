package com.sony.imaging.app.soundphoto.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.soundphoto.database.SPDataBaseUpdater;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class SPExecutorCreator extends ExecutorCreator {
    private static final String TAG = "SPExecutorCreator";
    private NormalBurstExecutor mNormalBurstExecutor = new NormalBurstExecutor();
    private SPSingleProcess mSingleProcess = new SPSingleProcess();
    private boolean mSpecial = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        IProcess process = this.mSingleProcess;
        return process;
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
    public void halt() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPDataBaseUpdater.getInstance().saveAllSoundPhotoBOData();
        super.halt();
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
