package com.sony.imaging.app.pictureeffectplus.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.pictureeffectplus.AppContext;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.MiniatureImageFilter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class EffectProcess implements IImagingProcess, ICaptureProcess {
    private static final String TAG = "EffectProcess";
    private IAdapter mAdapter;
    private CameraEx mCameraEx;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        this.mAdapter = adapter;
        this.mCameraEx = camera;
        ScalarProperties.getString("device.memory");
        ScalarProperties.getString("mem.rawimage.size.in.mega.pixel");
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence());
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        this.mAdapter.setOptions(opt);
    }

    public String getFilePathForSpecialSequence() {
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "PictureEffectPlus", "00");
        String filePath = map.getPath();
        return filePath;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        this.mAdapter = null;
        this.mCameraEx = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        if (status != 0) {
            this.mAdapter.enableNextCapture(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        makeImages(raw, sequence);
        this.mAdapter.enableNextCapture(0);
    }

    private void makeImages(CameraSequence.RawData raw, CameraSequence sequence) {
        CameraSequence.DefaultDevelopFilter f1 = new CameraSequence.DefaultDevelopFilter();
        PictureEffectPlusController cntl = PictureEffectPlusController.getInstance();
        String miniArea = cntl.getMiniatureFocusArea();
        int miniFilterArea = 1;
        Log.i(TAG, "miniature area:" + miniArea);
        if (PictureEffectController.MINIATURE_HCENTER.equals(miniArea)) {
            miniFilterArea = 1;
        } else if (PictureEffectController.MINIATURE_VCENTER.equals(miniArea)) {
            miniFilterArea = 2;
        } else if (PictureEffectController.MINIATURE_UPPER.equals(miniArea)) {
            miniFilterArea = 5;
        } else if (PictureEffectController.MINIATURE_LOWER.equals(miniArea)) {
            miniFilterArea = 6;
        } else if (PictureEffectController.MINIATURE_RIGHT.equals(miniArea)) {
            miniFilterArea = 4;
        } else if (PictureEffectController.MINIATURE_LEFT.equals(miniArea)) {
            miniFilterArea = 3;
        }
        if (f1.isSupported()) {
            if (raw.isValid()) {
                f1.setSource(raw, true);
                if (f1.getNumberOfSources() == 1 && f1.getNumberOfOutputs() == 0) {
                    if (f1.execute()) {
                        OptimizedImage img1 = f1.getOutput();
                        f1.release();
                        MiniatureImageFilter f2 = new MiniatureImageFilter();
                        f2.setMiniatureArea(miniFilterArea);
                        f2.setSource(img1, true);
                        if (f2.execute()) {
                            OptimizedImage img2 = f2.getOutput();
                            f2.release();
                            sequence.storeImage(img2, true);
                            Log.i(TAG, "finish store miniature comb pict");
                            return;
                        }
                        f2.release();
                        return;
                    }
                    f1.release();
                    raw.release();
                    return;
                }
                f1.release();
                raw.release();
                return;
            }
            f1.release();
            raw.release();
            return;
        }
        f1.release();
        raw.release();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        this.mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }
}
