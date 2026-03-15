package com.sony.imaging.app.lightshaft.shooting;

import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.lightshaft.AppContext;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class LightShaftEffectProcess implements ICaptureProcess, IImagingProcess {
    private static final String TAG = "LightShaftEffectProcess";
    private boolean isNoCard;
    private IAdapter mAdapter = null;
    CameraEx mCameraEx = null;

    public String getFilePathForSpecialSequence() {
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "LightShaft", "00");
        String filePath = map.getPath();
        return filePath;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        this.mAdapter = adapter;
        this.mCameraEx = camera;
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence());
        opt.setOption("EXPOSURE_COUNT", 1);
        if (!MediaNotificationManager.getInstance().isNoCard()) {
            opt.setOption("RECORD_COUNT", 2);
            this.isNoCard = false;
        } else if (MediaNotificationManager.getInstance().isMounted() && MediaNotificationManager.getInstance().getRemaining() == 1) {
            opt.setOption("RECORD_COUNT", 2);
            this.isNoCard = false;
        } else {
            opt.setOption("RECORD_COUNT", 1);
            this.isNoCard = true;
        }
        this.mAdapter.setOptions(opt);
    }

    private void displayKikilogs() {
        Integer.valueOf(4149);
        int effect = ShaftsEffect.getInstance().getParameters().getEffect();
        switch (effect) {
            case 1:
                pictureTakeKikilogs(4158);
                return;
            case 2:
                pictureTakeKikilogs(4159);
                return;
            case 3:
                pictureTakeKikilogs(4160);
                return;
            case 4:
                pictureTakeKikilogs(4161);
                return;
            default:
                return;
        }
    }

    public static void pictureTakeKikilogs(Integer kikilogId) {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.info(TAG, "LightShaftEffectProcess terminate()");
        this.mAdapter = null;
        this.mCameraEx = null;
        this.isNoCard = false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.info(TAG, " onShutter()");
        if (status != 0) {
            this.mAdapter.enableNextCapture(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        this.mAdapter.onProgress(0, 5);
        displayKikilogs();
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mAdapter.onProgress(1, 5);
        OptimizedImage original = filter.getOutput();
        filter.release();
        AppLog.info(TAG, "filter release()  onShutterSequence()");
        this.mAdapter.onProgress(2, 5);
        if (!this.isNoCard) {
            AppLog.info(TAG, "save original onShutterSequence()");
            sequence.storeImage(original, false);
        }
        this.mAdapter.onProgress(3, 5);
        OptimizedImage copy = ShaftsEffect.getInstance().run(original, sequence);
        this.mAdapter.onProgress(4, 5);
        AppLog.info(TAG, "save copy onShutterSequence()");
        sequence.storeImage(copy, true);
        AppLog.info(TAG, "enableNextCapture   onShutterSequence()");
        this.mAdapter.enableNextCapture(0);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.info(TAG, "preTakePicture called from LightShaftEffectProcess");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.info(TAG, "takePicture called from LightShaftEffectProcess");
        this.mCameraEx.cancelTakePicture();
        this.mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        AppLog.info(TAG, "startSelfTimerShutter called from LightShaftEffectProcess");
        this.mCameraEx.startSelfTimerShutter();
    }
}
