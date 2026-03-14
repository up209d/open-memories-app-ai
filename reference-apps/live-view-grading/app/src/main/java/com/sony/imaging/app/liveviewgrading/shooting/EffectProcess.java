package com.sony.imaging.app.liveviewgrading.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class EffectProcess implements IImagingProcess {
    private IAdapter mAdapter;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        this.mAdapter = adapter;
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", "/android/data/lib/com.sony.imaging.app.each/lib/libSingleCap.so");
        this.mAdapter.setOptions(opt);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        if (status != 0) {
            this.mAdapter.enableNextCapture(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
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
    }
}
