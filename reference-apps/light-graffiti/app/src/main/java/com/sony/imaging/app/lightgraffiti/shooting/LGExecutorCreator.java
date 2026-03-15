package com.sony.imaging.app.lightgraffiti.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.CaptureAdapterImpl;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.camera.executor.ImagingAdapterImpl;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecAdapterImpl;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGExposureModeController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGRemoConController;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGExecutorCreator extends ExecutorCreator {
    private LGNormalExecutor mLGNormalExecutor = new LGNormalExecutor();
    private LGStableExecutor mLGStableExecutor = new LGStableExecutor();
    private CompositProcess mCompositProcess = new CompositProcess();
    private boolean mSpecial = true;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        IProcess process = this.mCompositProcess;
        return process;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IAdapter getAdapter(String name) {
        Camera camera = this.mCameraEx.getNormalCamera();
        if (getRecordingMode() == 2) {
            return null;
        }
        if (4 == getRecordingMode()) {
            if (!isIntervalRecEnable() || isSpinal() || isSpecial()) {
                return null;
            }
            IAdapter adapter = new IntervalRecAdapterImpl(camera, this.mCameraEx);
            return adapter;
        }
        if (!isSpinal()) {
            if (isSpecial()) {
                IAdapter adapter2 = new LGImagingAdapterImpl(camera, this.mCameraEx);
                return adapter2;
            }
            IAdapter adapter3 = new CaptureAdapterImpl(camera, this.mCameraEx);
            return adapter3;
        }
        if (!isSpecial()) {
            return null;
        }
        IAdapter adapter4 = new ImagingAdapterImpl(camera, this.mCameraEx);
        return adapter4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public Class<?> getNextExecutor() {
        return (getRecordingMode() != 1 || this.mIsRecModeChanging || MediaNotificationManager.getInstance().isError()) ? super.getNextExecutor() : LGNormalExecutor.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        if (LGNormalExecutor.class.equals(clazz)) {
            BaseShootingExecutor executor = this.mLGNormalExecutor;
            return executor;
        }
        BaseShootingExecutor executor2 = this.mLGStableExecutor;
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
        String expMode = LGExposureModeController.getInstance().getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Aperture".equals(expMode) || "Shutter".equals(expMode) || ExposureModeController.MANUAL_MODE.equals(expMode)) {
            Log.d("LGExecutorCreator", "ExpMode is PASM. OK.");
        } else {
            Log.w("LGExecutorCreator", "ExpMode is not PASM. change to SHUTTER_MODE.");
            LGExposureModeController.getInstance().setExposureMode("Shutter");
        }
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
    }
}
