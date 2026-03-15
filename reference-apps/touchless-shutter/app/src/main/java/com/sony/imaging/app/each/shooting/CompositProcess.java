package com.sony.imaging.app.each.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: classes.dex */
public class CompositProcess implements IImagingProcess, ICaptureProcess {
    private int captureCounter;
    private IAdapter mAdapter;
    private CameraEx mCameraEx;
    private final int MAX_CAPTURE_NUM = 3;
    private Queue<CameraSequence.RawData> raws = new LinkedList();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
        this.mAdapter = adapter;
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", "/android/data/data/com.sony.imaging.app.each/lib/libSingleCap.so");
        opt.setOption("EXPOSURE_COUNT", 3);
        opt.setOption("RECORD_COUNT", 1);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
        opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
        this.mAdapter.setOptions(opt);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        this.mCameraEx = null;
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        this.captureCounter = 0;
        this.raws.clear();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        this.mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        this.captureCounter++;
        if (status != 0) {
            releaseRawData();
            this.mAdapter.enableNextCapture(status);
        } else if (this.captureCounter < 3) {
            this.mCameraEx.burstableTakePicture();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        if (this.captureCounter == 3) {
            this.mAdapter.onProgress(0, 2);
            CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
            filter.setSource(raw, true);
            filter.execute();
            this.mAdapter.onProgress(1, 2);
            OptimizedImage img = filter.getOutput();
            filter.release();
            this.mAdapter.onProgress(2, 2);
            sequence.storeImage(img, true);
            this.mAdapter.enableNextCapture(0);
            releaseRawData();
            return;
        }
        this.raws.add(raw);
    }

    private void releaseRawData() {
        for (CameraSequence.RawData raw : this.raws) {
            raw.release();
        }
        this.raws.clear();
    }
}
