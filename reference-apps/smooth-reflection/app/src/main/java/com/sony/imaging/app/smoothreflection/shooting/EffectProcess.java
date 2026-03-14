package com.sony.imaging.app.smoothreflection.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class EffectProcess implements IImagingProcess {
    private static final String TAG = AppLog.getClassName();
    private IAdapter mAdapter = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter = adapter;
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", "/android/data/lib/com.sony.imaging.app.smoothreflection/lib/libSingleCap.so");
        this.mAdapter.setOptions(opt);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (status != 0) {
            this.mAdapter.enableNextCapture(status);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter.onProgress(0, 3);
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mAdapter.onProgress(1, 3);
        OptimizedImage img = filter.getOutput();
        filter.release();
        this.mAdapter.onProgress(2, 3);
        sequence.storeImage(img, true);
        this.mAdapter.enableNextCapture(0);
        this.mAdapter.onProgress(3, 3);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
