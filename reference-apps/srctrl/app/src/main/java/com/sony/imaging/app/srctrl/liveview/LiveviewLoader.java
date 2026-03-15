package com.sony.imaging.app.srctrl.liveview;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.liveview.FrameInfoLoader;
import com.sony.imaging.app.srctrl.liveview.JpegLoader;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.servlet.LiveviewChunkTransfer;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class LiveviewLoader {
    public static final int LIVEVIEW_OBTAINING_INTERVAL = 30;
    private static LiveviewLoader sInstance;
    private static final String TAG = LiveviewLoader.class.getSimpleName();
    private static JpegLoader loader = null;
    private static FrameInfoLoader frameInfoloader = null;
    private static volatile boolean isGenerating = false;
    private static volatile boolean isLoading = false;
    private static LiveviewChunkTransfer chunkTransfer = null;
    private static GetFrameInfoThread getFrameInfoThread = null;
    private static final int[] mQParamList = {-2147417855, 131189, 7100710, 7962761, 66052, 16776944, AppRoot.USER_KEYCODE.SK2, -1667457892};
    private static CameraSequence camera_seq = null;

    public LiveviewLoader() {
        isGenerating = false;
        isLoading = false;
        loader = new JpegLoader();
        frameInfoloader = new FrameInfoLoader();
        LiveviewCommon.reset();
    }

    private boolean startGeneratingPreview() {
        if (isGeneratingPreview()) {
            Log.v(TAG, "Liveview has been already being generated...");
            return true;
        }
        if (4 != RunStatus.getStatus()) {
            Log.e(TAG, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") at " + Thread.currentThread().getStackTrace()[2].toString());
            return false;
        }
        boolean started = startSequence();
        if (started) {
            getFrameInfoThread = new GetFrameInfoThread();
            getFrameInfoThread.start(frameInfoloader, camera_seq);
            setGeneratingPreview(true);
            return started;
        }
        return started;
    }

    private boolean startSequence() {
        CameraSequence tmp_camera_seq = null;
        for (int i = 0; i < 20; i++) {
            ExecutorCreator creator = SRCtrlExecutorCreator.getInstance();
            if (creator == null) {
                Log.v(TAG, "ExecutorCreator is null.");
            } else {
                BaseShootingExecutor executor = creator.getSequence();
                if (executor == null) {
                    Log.v(TAG, "ShootingExecutor is null.");
                } else {
                    CameraEx cameraEx = executor.getCameraEx();
                    if (cameraEx == null) {
                        Log.v(TAG, "CameraSequence is null.");
                    } else {
                        tmp_camera_seq = CameraSequence.open(cameraEx);
                    }
                }
            }
            if (tmp_camera_seq != null) {
                break;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (tmp_camera_seq == null) {
            Log.e(TAG, "CameraSequence has not been changed.");
            return false;
        }
        camera_seq = tmp_camera_seq;
        LiveviewContainer liveview = LiveviewContainer.getInstance();
        CameraSequence.Options opts = new CameraSequence.Options();
        opts.setOption("PREVIEW_FRAME_RATE", liveview.getFrameRate());
        opts.setOption("PREVIEW_FRAME_WIDTH", liveview.getWidth());
        opts.setOption("PREVIEW_FRAME_HEIGHT", 0);
        opts.setOption("PREVIEW_FRAME_FORMAT", 256);
        opts.setOption("PREVIEW_FRAME_MAX_NUM", 1);
        opts.setOption("JPEG_COMPRESS_RATE_DENOM", liveview.getCompressRate());
        opts.setOption("JPEG_COMPRESS_MAX_SIZE", liveview.getMaxFileSize());
        if (SRCtrlEnvironment.getInstance().isEnableInterimPreview()) {
            opts.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
        }
        if (liveview.getLiveviewSize().equals(LiveviewContainer.s_LARGE_LIVEVIEW_SIZE)) {
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_1", mQParamList[0]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_2", mQParamList[1]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_3", mQParamList[2]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_4", mQParamList[3]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_5", mQParamList[4]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_6", mQParamList[5]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_7", mQParamList[6]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_8", mQParamList[7]);
        }
        try {
            camera_seq.startPreviewSequence(opts);
            return true;
        } catch (RuntimeException e2) {
            Log.e(TAG, "Failed to Start Preview Sequence");
            return false;
        }
    }

    private void stopGeneratingPreview() {
        if (isGeneratingPreview()) {
            setGeneratingPreview(false);
            if (getFrameInfoThread != null) {
                getFrameInfoThread.stop();
            }
            stopSequence();
        }
    }

    private void stopSequence() {
        try {
            camera_seq.stopPreviewSequence();
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to stop Preview Sequence");
        }
        try {
            camera_seq.release();
            camera_seq = null;
        } catch (RuntimeException e2) {
            Log.e(TAG, "Failed to release");
        }
    }

    private static void setGeneratingPreview(boolean lo) {
        isGenerating = lo;
        Log.v(TAG, "generating = " + String.valueOf(isGenerating));
    }

    public static boolean isGeneratingPreview() {
        return isGenerating;
    }

    private static void setLoadingPreview(boolean lo) {
        if (isLoading != lo) {
            isLoading = lo;
            ParamsGenerator.updateLiveviewStatus();
            ServerEventHandler.getInstance().onServerStatusChanged();
            Log.v(TAG, "loading = " + String.valueOf(isLoading));
        }
    }

    public static boolean isLoadingPreview() {
        return isLoading;
    }

    public static void setLiveviewChunkTransferInstance(LiveviewChunkTransfer transfer) {
        chunkTransfer = transfer;
    }

    public static JpegLoader.LiveviewData getLiveviewData() throws IllegalStateException {
        if (!isGeneratingPreview()) {
            Log.e(TAG, "LiveviewLoader is not generating liveview data.");
            throw new IllegalStateException();
        }
        if (!isLoadingPreview()) {
            Log.e(TAG, "JpegLoader is not loading liveview data.");
            throw new IllegalStateException();
        }
        return loader.getLiveviewData(camera_seq);
    }

    public static FrameInfoLoader.FrameInfoData getFrameInfoData() throws IllegalStateException {
        if (!isGeneratingPreview()) {
            Log.e(TAG, "LiveviewLoader is not generating frame data.");
            throw new IllegalStateException();
        }
        if (getFrameInfoThread == null) {
            return null;
        }
        FrameInfoLoader.FrameInfoData ret = getFrameInfoThread.getFrameInfoDataCopy();
        return ret;
    }

    public static synchronized boolean startObtainingImages() {
        boolean ret;
        synchronized (LiveviewLoader.class) {
            Log.v(TAG, "Make liveview ready!");
            ret = false;
            if (sInstance == null) {
                sInstance = new LiveviewLoader();
            }
            if (sInstance != null) {
                ret = sInstance.startGeneratingPreview();
            }
            if (ret) {
                setLoadingPreview(true);
            }
        }
        return ret;
    }

    public static synchronized boolean stopObtainingImages() {
        synchronized (LiveviewLoader.class) {
            if (isLoadingPreview()) {
                setLoadingPreview(false);
            }
            if (sInstance != null) {
                Log.v(TAG, "Make liveview down!");
                sInstance.stopGeneratingPreview();
            }
        }
        return true;
    }

    public static synchronized void clean() {
        synchronized (LiveviewLoader.class) {
            stopObtainingImages();
            if (sInstance != null) {
                sInstance.kill();
                sInstance = null;
            }
        }
    }

    public void kill() {
        if (chunkTransfer != null) {
            chunkTransfer.notifyGetScalarInfraIsKilled();
        }
        loader = null;
    }

    public static void pauseFrameInfoThread() {
        if (getFrameInfoThread != null) {
            getFrameInfoThread.pause();
        }
    }

    public static void resumeGetFrameInfoThread() {
        if (getFrameInfoThread != null) {
            getFrameInfoThread.resume();
        }
    }
}
