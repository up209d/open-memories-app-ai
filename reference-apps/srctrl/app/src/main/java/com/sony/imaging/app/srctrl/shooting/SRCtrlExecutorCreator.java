package com.sony.imaging.app.srctrl.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.srctrl.shooting.camera.executor.NormalExecutorEx;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SRCtrlExecutorCreator extends ExecutorCreator {
    private static final String TAG = SRCtrlExecutorCreator.class.getName();
    private NormalExecutorEx mNormalExecutorEx = new NormalExecutorEx();
    private SingleProcess singleProcess = new SingleProcess();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        return this.singleProcess;
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        if (NormalExecutor.class.equals(clazz)) {
            BaseShootingExecutor executor = this.mNormalExecutorEx;
            return executor;
        }
        BaseShootingExecutor executor2 = super.getExecutor(clazz);
        return executor2;
    }

    public static String getRecordingMedia() {
        String[] ids = getMediaId();
        if (ids == null || ids.length == 0) {
            return null;
        }
        return ids[0];
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public synchronized void init() {
        StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        Log.v(TAG, "SRCtrlExecutorCreator#init() was called by...");
        for (StackTraceElement s : stackTraceArray) {
            Log.v(TAG, "  + " + s.toString());
        }
        super.init();
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
    public boolean isBulbEnabled() {
        return SRCtrlEnvironment.getInstance().isEnableBulbShooting();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isInheritRawRecMode() {
        return true;
    }
}
