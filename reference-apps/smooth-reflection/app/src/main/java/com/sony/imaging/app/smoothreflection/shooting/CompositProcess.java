package com.sony.imaging.app.smoothreflection.shooting;

import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.smoothreflection.common.AppContext;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: classes.dex */
public class CompositProcess implements IImagingProcess, ICaptureProcess {
    private static int mCounter = 0;
    private IAdapter mAdapter;
    private CameraEx mCameraEx;
    private Queue<CameraSequence.RawData> raws = new LinkedList();
    private final String TAG = CompositProcess.class.getName();
    private final int CAPTURE_NUM = 3;
    private Object mediaLockObject = new Object();
    Handler mHandlerFromMain = null;
    MyRecordingMediaChangeCallback2 sMyRecordingMediaChangeCallback2 = new MyRecordingMediaChangeCallback2();
    MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();

    private String getFilePathForSpecialSequence() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "SmoothReflection", "00");
        String filePath = map.getPath();
        AppLog.info(this.TAG, "Memory map file path: " + filePath);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return filePath;
    }

    public void setMemoryMapConfiguration() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        if (pfMajorVersion == 2) {
            MemoryMapConfig.setAllocationPolicy(1);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        this.mAdapter = adapter;
        this.mAdapter = adapter;
        setMemoryMapConfiguration();
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence());
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
        opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
        this.mAdapter.setOptions(opt);
        setVirtualMediaIds();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        this.mCameraEx = null;
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        mCounter = 0;
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
        if (status != 0) {
            releaseRawData();
            this.mAdapter.enableNextCapture(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        this.mCameraEx.cancelTakePicture();
        StarTrailSequence(raw, sequence);
        this.mAdapter.enableNextCapture(0);
    }

    private void preTakePictureSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        OptimizedImage img = filter.getOutput();
        filter.release();
        sequence.storeImage(img, true);
        releaseRawData();
    }

    private void StarTrailSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        mCounter++;
        if (mCounter < 2) {
            takePicture();
        }
        Log.i("kamata", "counter " + mCounter);
        if (mCounter == 3) {
            CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
            filter.setSource(raw, true);
            filter.execute();
            OptimizedImage img = filter.getOutput();
            filter.release();
            releaseRawData();
            sequence.storeImage(img, true);
            return;
        }
        CameraSequence.DefaultDevelopFilter filter2 = new CameraSequence.DefaultDevelopFilter();
        filter2.setSource(raw, true);
        filter2.execute();
        OptimizedImage img2 = filter2.getOutput();
        filter2.release();
        sequence.storeImage(img2, true);
        if (mCounter == 2) {
            Log.i("kamata", "setActualMediaIds ");
            setActualMediaIds();
        }
    }

    private void releaseRawData() {
        for (CameraSequence.RawData raw : this.raws) {
            raw.release();
        }
        this.raws.clear();
    }

    private void setVirtualMediaIds() {
        String[] ids = AvindexStore.getVirtualMediaIds();
        SetRecordingMediaRunnable mSetRecordingMediaRunnable = new SetRecordingMediaRunnable(ids[0]);
        this.mHandlerFromMain = new Handler();
        synchronized (this.mediaLockObject) {
            this.mHandlerFromMain.post(mSetRecordingMediaRunnable);
            try {
                this.mediaLockObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setActualMediaIds() {
        String[] ids = AvindexStore.getExternalMediaIds();
        this.mCameraEx.setRecordingMedia(ids[0], this.sMyRecordingMediaChangeCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SetRecordingMediaRunnable implements Runnable {
        private String mMedia;

        public SetRecordingMediaRunnable(String media) {
            this.mMedia = media;
        }

        @Override // java.lang.Runnable
        public void run() {
            CompositProcess.this.mCameraEx.setRecordingMedia(this.mMedia, CompositProcess.this.sMyRecordingMediaChangeCallback2);
        }
    }

    /* loaded from: classes.dex */
    class MyRecordingMediaChangeCallback2 implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback2() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            Log.i("kamata", "sMyRecordingMediaChangeCallback2 1");
            synchronized (CompositProcess.this.mediaLockObject) {
                CompositProcess.this.mediaLockObject.notifyAll();
            }
            Log.i("kamata", "sMyRecordingMediaChangeCallback2 2");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            Log.i("kamata", "takePicture ");
            CompositProcess.this.takePicture();
        }
    }
}
