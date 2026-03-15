package com.sony.imaging.app.timelapse.shooting;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.AviExporter;
import com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapse;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.metadatamanager.YuvToRgbConversion;
import com.sony.imaging.app.timelapse.shooting.controller.MovieController;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLPictureQualityController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.MiniatureImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class TLAviCaptureProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    public static final int DELAY_OFFSET = 90;
    private static final int EXTRA_MARGIN_TIME = 500;
    private final String TAG;
    private AviExporter aviExporter;
    private int bitmapDisplayFirstPosition;
    private int bitmapDisplayInterval;
    private TLCommonUtil cUtil;
    private CameraSequence.DefaultDevelopFilter devFilter;
    private boolean isCancelledTakePicture;
    private boolean isCapturing;
    private boolean isDisplay;
    private boolean isFirstTakePicture;
    private boolean isMiniatureEffectSet;
    private boolean isMovieSize_1920_1080;
    private boolean isProcessTaskFinished;
    private boolean isTimeOut;
    private IAdapter mAdapter;
    int mAspect;
    private CameraEx.AutoPictureReviewControl mAutoPicRevCtl;
    private Camera mCamera;
    private CameraEx mCameraEx;
    private CancelRunnable mCancelRunnable;
    private int mCounter;
    private CropImageFilter mCropImageFilter;
    private long mFirstTakePictureTime;
    private int mMiniatureId;
    private int mNumberOfShots;
    private RunnableTask mRunnableTask;
    private ScaleImageFilter mScaledImageFilter;
    private CameraSequence mSequence;
    int mStatus;
    TimelapseExposureController mTLEX;
    private Handler mTaskHandler;
    private DSP mYuvToRGBDsp;
    private MiniatureImageFilter minFilter;
    private Handler myHandler;
    private long startTime;
    private boolean storeThumbnail;
    private YuvToRgbConversion yuvConv;
    private static boolean isRemoteControlMode = false;
    private static boolean isFirstShooting = true;
    private static String[] TAGS = {TimeLapseConstants.REMOVE_CAPTURE_CALLBACK, MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
    private static WatchDogTimerForMaybePhaseDiffRunnableTask mWatchDogTimerForMaybePhaseDiffRunnableTask = null;

    public DSP getYuvToRGBDsp() {
        return this.mYuvToRGBDsp;
    }

    public void createYuvToRGBDsp() {
        try {
            if (this.mYuvToRGBDsp == null) {
                this.mYuvToRGBDsp = DSP.createProcessor("sony-di-dsp");
                this.mYuvToRGBDsp.setProgram(TLCommonUtil.getInstance().getFilePathForYuvToRGBConv());
            }
        } catch (Exception e) {
            this.mYuvToRGBDsp.release();
            this.mYuvToRGBDsp = null;
        }
    }

    public TLAviCaptureProcess() {
        this.TAG = TLAviCaptureProcess.class.getName();
        this.mAdapter = null;
        this.mCameraEx = null;
        this.mCamera = null;
        this.devFilter = null;
        this.mScaledImageFilter = null;
        this.aviExporter = null;
        this.isMovieSize_1920_1080 = false;
        this.yuvConv = null;
        this.isMiniatureEffectSet = false;
        this.minFilter = null;
        this.mYuvToRGBDsp = null;
        this.mSequence = null;
        this.mCounter = 0;
        this.mMiniatureId = 3;
        this.cUtil = null;
        this.mRunnableTask = null;
        this.mAutoPicRevCtl = null;
        this.bitmapDisplayInterval = 2;
        this.bitmapDisplayFirstPosition = 2;
        this.mCancelRunnable = null;
        this.isDisplay = false;
        this.myHandler = null;
        this.mTaskHandler = null;
        this.isFirstTakePicture = false;
        this.isProcessTaskFinished = false;
        this.isTimeOut = false;
        this.startTime = 0L;
        this.isCapturing = false;
        this.isCancelledTakePicture = false;
        this.mCropImageFilter = null;
        this.mAspect = ScalarProperties.getInt("device.panel.aspect");
        this.mTLEX = null;
        this.mFirstTakePictureTime = 0L;
        this.mNumberOfShots = 0;
        this.mStatus = 2;
    }

    public TLAviCaptureProcess(boolean isMiniature) {
        this.TAG = TLAviCaptureProcess.class.getName();
        this.mAdapter = null;
        this.mCameraEx = null;
        this.mCamera = null;
        this.devFilter = null;
        this.mScaledImageFilter = null;
        this.aviExporter = null;
        this.isMovieSize_1920_1080 = false;
        this.yuvConv = null;
        this.isMiniatureEffectSet = false;
        this.minFilter = null;
        this.mYuvToRGBDsp = null;
        this.mSequence = null;
        this.mCounter = 0;
        this.mMiniatureId = 3;
        this.cUtil = null;
        this.mRunnableTask = null;
        this.mAutoPicRevCtl = null;
        this.bitmapDisplayInterval = 2;
        this.bitmapDisplayFirstPosition = 2;
        this.mCancelRunnable = null;
        this.isDisplay = false;
        this.myHandler = null;
        this.mTaskHandler = null;
        this.isFirstTakePicture = false;
        this.isProcessTaskFinished = false;
        this.isTimeOut = false;
        this.startTime = 0L;
        this.isCapturing = false;
        this.isCancelledTakePicture = false;
        this.mCropImageFilter = null;
        this.mAspect = ScalarProperties.getInt("device.panel.aspect");
        this.mTLEX = null;
        this.mFirstTakePictureTime = 0L;
        this.mNumberOfShots = 0;
        this.mStatus = 2;
        this.isMiniatureEffectSet = isMiniature;
    }

    private void setAutoReviewControl() {
        if (this.mAutoPicRevCtl == null) {
            this.mAutoPicRevCtl = new CameraEx.AutoPictureReviewControl();
        }
        this.mCameraEx.setAutoPictureReviewControl(this.mAutoPicRevCtl);
        this.mAutoPicRevCtl.setPictureReviewTime(0);
    }

    private void initialize() {
        try {
            if (this.myHandler == null) {
                this.myHandler = new Handler();
            }
            if (this.mTaskHandler == null) {
                this.mTaskHandler = new Handler();
            }
            setAutoReviewControl();
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
        }
    }

    private void setMovieQualityFactor() {
        try {
            String tag = MovieController.getInstance().getValue(null);
            if (tag.equalsIgnoreCase("MOVIE_1920_1080")) {
                this.isMovieSize_1920_1080 = true;
            } else {
                this.isMovieSize_1920_1080 = false;
            }
        } catch (IController.NotSupportedException e) {
            this.isMovieSize_1920_1080 = true;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        this.mAdapter = adapter;
        this.mCameraEx = camera;
        this.mCamera = this.mCameraEx.getNormalCamera();
        initialize();
        if (this.mAdapter != null) {
            CameraSequence.Options opt = new CameraSequence.Options();
            TLCommonUtil.getInstance().setMemoryMapConfiguration();
            opt.setOption("MEMORY_MAP_FILE", TLCommonUtil.getInstance().getFilePathForSpecialSequence());
            opt.setOption("EXPOSURE_COUNT", 1);
            opt.setOption("RECORD_COUNT", 1);
            opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
            opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
            this.mAdapter.setOptions(opt);
        }
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        isRemoteControlMode = this.cUtil.isRemoteControlMode();
        this.cUtil.setShutterSpeed();
        initializeExposureControllerForAE();
        isFirstShooting = true;
    }

    private void initializeExposureControllerForAE() {
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        if (this.mTLEX == null) {
            this.mTLEX = new TimelapseExposureController();
        }
        TimelapseExposureController.Options tlexOptions = this.cUtil.setExposureOptionForAESettings(new TimelapseExposureController.Options());
        if (this.mTLEX != null) {
            this.mTLEX.open(this.mCameraEx, new TimelapseExposureControllerCallbackImpl(), tlexOptions);
            AppLog.info(this.TAG, "AE tracking implementation = startMetering in  setAETrackingCameraSetting()");
            this.mTLEX.startMetering(this.mCameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        Log.d(this.TAG, "*************TERMINATE Status NOT OK");
        if (!TimeLapseConstants.isMemoryReleased) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            TimeLapseConstants.isMemoryReleased = true;
        }
        TLCommonUtil.getInstance().setPictureEffectIconUpdate(true);
        ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
        if (this.mTLEX != null && this.mCameraEx != null) {
            this.mTLEX.stopMetering(this.mCameraEx);
            this.mTLEX.close();
        }
        TLPictureQualityController.getInstance().resetTestShotDummyQuality();
        releaseMemory();
        this.mCameraEx = null;
        this.mAdapter = null;
        this.mAutoPicRevCtl = null;
        this.mFirstTakePictureTime = 0L;
        isRemoteControlMode = false;
        Log.d(this.TAG, "*************TERMINATE Status OK");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableNextPictureSetting() {
        if (isRemoteControlMode) {
            this.cUtil.setRemoteControlMode(true, this.mCamera);
        }
        setPostTakePictureSetting();
        if (this.mTLEX != null && this.mCameraEx != null) {
            this.mTLEX.stopMetering(this.mCameraEx);
            this.mTLEX.close();
            initializeExposureControllerForAE();
        }
        releaseMemory();
        ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
    }

    private void createRunnableTask() {
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask(this.mTaskHandler);
        }
        if (this.mCancelRunnable == null) {
            this.mCancelRunnable = new CancelRunnable(this.myHandler);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        if (TLCommonUtil.getInstance().isSilentShutterOn()) {
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        }
        TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
        this.mStatus = status;
        AppLog.trace(this.TAG, "onShutter status =  " + status);
        if (status != 0) {
            AppLog.enter(this.TAG, "*************onShutter  ShutterListener.STATUS_OK not OK ");
            if (this.mCounter >= 1) {
                this.mCounter--;
            }
            isFirstShooting = true;
            ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
            Log.d(this.TAG, "enableNextCapture in onShutter");
            this.mAdapter.enableNextCapture(status);
            enableNextPictureSetting();
            return;
        }
        isFirstShooting = false;
        AppLog.exit(this.TAG, "*************onShutter  ShutterListener.STATUS_OK OK ");
    }

    private void TestShotSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean isRAWJPEG = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
        try {
            this.devFilter.setRawFileStoreEnabled(isRAWJPEG);
            this.devFilter.setSource(raw, true);
            this.devFilter.execute();
            OptimizedImage img = this.devFilter.getOutput();
            this.devFilter.clearSources();
            if (isRAWJPEG) {
                sequence.storeImage(img, true);
                if (raw != null && raw.isValid()) {
                    raw.release();
                }
            } else {
                if (this.isMiniatureEffectSet) {
                    img = getMiniatureOptimizedImage(img);
                }
                sequence.storeImage(img, true);
            }
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
        }
        enableNextPictureSetting();
        if (this.mAdapter != null) {
            this.mAdapter.enableNextCapture(this.mStatus);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.info(this.TAG, "AE tracking implementation = resumeMetering in onShutterSequence method");
        TimelapseExecutorCreator.isNoShotCaptured = false;
        this.mTLEX.resumeMetering(sequence.getCameraEx());
        this.mCameraEx.cancelTakePicture();
        Log.d(this.TAG, "onShutterSequence********************* start:" + this.mCounter);
        this.mSequence = sequence;
        isFirstShooting = false;
        if (TLCommonUtil.getInstance().isTestShot()) {
            TestShotSequence(raw, sequence);
            return;
        }
        if (this.cUtil != null) {
            processTask(raw);
        }
        System.gc();
        Log.d(this.TAG, "onShutterSequence********************* end");
    }

    private synchronized OptimizedImage getScaledImage(OptimizedImage optImg, int width, int height) {
        OptimizedImage scaledImage;
        if (this.mScaledImageFilter == null) {
            this.mScaledImageFilter = new ScaleImageFilter();
        }
        this.mScaledImageFilter.setSource(optImg, false);
        this.mScaledImageFilter.setDestSize(width, height);
        boolean result = this.mScaledImageFilter.execute();
        scaledImage = result ? this.mScaledImageFilter.getOutput() : null;
        this.mScaledImageFilter.clearSources();
        return scaledImage;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        this.mCounter = 0;
        TimeLapse.isIn_onShutdown = false;
        this.isFirstTakePicture = false;
        this.bitmapDisplayFirstPosition = 2;
        this.isMiniatureEffectSet = false;
        this.storeThumbnail = true;
        this.mNumberOfShots = -1;
        this.isProcessTaskFinished = false;
        this.isTimeOut = false;
        this.isCancelledTakePicture = false;
        this.mFirstTakePictureTime = 0L;
        TwoSecondTimerLayout.isShootingCautionIconDisp = false;
        TimeLapseConstants.sCaptureImageCounter = this.mCounter;
        TLPictureQualityController.getInstance().setTestShotDummyQuality();
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        if (this.cUtil.isTakePictureByRemote()) {
            this.cUtil.setRemoteControlMode(false, this.mCamera);
        }
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            this.isFirstTakePicture = true;
        }
        setMovieQualityFactor();
        createRunnableTask();
        isSaveBitmap();
        TLCommonUtil.getInstance().setPictureEffectIconUpdate(false);
        createMovieShootingObjects();
        TimeLapseConstants.sFrameBitmapList.clear();
        isFirstShooting = true;
        setCameraSetting(true);
        TLCommonUtil.getInstance().setInternalTotalRemaining(TLCommonUtil.getInstance().getAvailableRemainingShot());
        TimeLapseConstants.isMemoryReleased = false;
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    private void isSaveBitmap() {
        this.isDisplay = false;
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        if (this.cUtil != null && this.cUtil.getShootingNum() >= 30 && this.cUtil.getInterval() >= 2) {
            this.isDisplay = true;
            this.bitmapDisplayInterval = this.cUtil.getShootingNum() / 5;
            createYuvToRGBDsp();
            if (this.yuvConv != null && this.mYuvToRGBDsp != null) {
                this.yuvConv = new YuvToRgbConversion(this.mYuvToRGBDsp);
            }
        }
    }

    private void setPostTakePictureSetting() {
        this.isCancelledTakePicture = true;
        if (!TimeLapseConstants.isMemoryReleased) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            TimeLapseConstants.isMemoryReleased = true;
        }
        setCameraSetting(false);
        if (this.isMiniatureEffectSet) {
            TimelapsePictureEffectController.getInstance().setThemeValue("PictureEffect", PictureEffectController.MODE_MINIATURE);
        }
        String arg0 = AvindexStore.getExternalMediaIds()[0];
        AvindexStore.calculateAvailableSize(arg0);
    }

    private void setCameraSetting(boolean shouldLock) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = null;
        if (this.mCameraEx != null) {
            params = CameraSetting.getInstance().getEmptyParameters();
        } else {
            Log.i(this.TAG, "Camera settings instance not available ");
        }
        if (params != null && "auto".equals(WhiteBalanceController.getInstance().getValue())) {
            ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(shouldLock);
            CameraSetting.getInstance().setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        if (isFirstShooting) {
            if (TLCommonUtil.getInstance().maybePhaseDiffFlag()) {
                int waitTime = WhiteBalanceController.DEF_TEMP;
                if (this.isFirstTakePicture) {
                    waitTime = WhiteBalanceController.DEF_TEMP + 2000;
                }
                mWatchDogTimerForMaybePhaseDiffRunnableTask = new WatchDogTimerForMaybePhaseDiffRunnableTask();
                mWatchDogTimerForMaybePhaseDiffRunnableTask.execute(waitTime);
            }
            isFirstShooting = false;
        }
        if (this.isFirstTakePicture) {
            if (!this.mAdapter.enableHalt(true)) {
                this.mRunnableTask.execute(TimeLapseConstants.CAPTURE_DELAY_TIME);
            }
        } else if (this.mCameraEx != null) {
            if (this.cUtil.isTakePictureByRemote() && this.mCounter < 1) {
                this.mCamera.autoFocus(null);
            }
            this.mAdapter.enableHalt(false);
            this.mCameraEx.burstableTakePicture();
            this.isCapturing = true;
            this.startTime = System.currentTimeMillis();
            startRunnableTask();
        }
    }

    private void startRunnableTask() {
        this.mCounter++;
        TimeLapseConstants.sCaptureImageCounter = this.mCounter;
        ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
        TLCommonUtil.getInstance().setCaptureStatus(true);
        TimeLapseStableLayout.isCapturing = true;
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.TIMER_UPDATE);
        if (this.cUtil != null && this.mCounter < this.cUtil.getShootingNum() && this.mRunnableTask != null) {
            int adjustIntervalTime = (this.cUtil.getInterval() * 1000) - 90;
            if (this.mCounter > 1) {
                adjustIntervalTime = this.cUtil.getAdjustInterval(this.cUtil.getInterval(), this.mFirstTakePictureTime, 90, this.mCounter);
            } else {
                this.mFirstTakePictureTime = System.currentTimeMillis();
            }
            this.mRunnableTask.execute(adjustIntervalTime);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
    }

    private void createAviExporterOptionData(boolean isFirstPicture) {
        AviExporter.Options options = new AviExporter.Options();
        if (this.isMovieSize_1920_1080) {
            options.width = 1920;
            options.height = 1080;
        } else {
            options.width = 1280;
            options.height = 720;
        }
        options.fps = this.cUtil.getFrameRate();
        options.isAdobeRGB = AvailableInfo.isFactor("INH_FACTOR_CAM_SET_STL_COLOR_SPACE_EXPANDED_TYPE_C");
        if (isFirstPicture) {
            options.fullPathFileName = ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).getTimeLapseBOObject().getStartFullPathFileName();
        } else {
            String filePath = this.cUtil.getAviFilePathNameSequel();
            ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).getTimeLapseBOObject().setSequelFullPathFileName(filePath);
            options.fullPathFileName = ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).getTimeLapseBOObject().getSequelFullPathFileName();
        }
        options.libpath = TimeLapseConstants.LIB_PATH;
        if (this.aviExporter == null) {
            this.aviExporter = new AviExporter();
        }
        try {
            this.aviExporter.open(options);
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
        }
    }

    private void releaseOptimizedImage(OptimizedImage optImage) {
        if (optImage != null) {
            optImage.release();
        }
    }

    private void releaseMemory() {
        Log.d(this.TAG, "*******************Release memory start");
        TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
        if (this.mRunnableTask != null) {
            this.mRunnableTask.removeCallbacks();
            this.mRunnableTask = null;
        }
        if (this.mCancelRunnable != null) {
            this.mCancelRunnable.removeCallbacks();
            this.mCancelRunnable = null;
        }
        if (mWatchDogTimerForMaybePhaseDiffRunnableTask != null) {
            TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            mWatchDogTimerForMaybePhaseDiffRunnableTask.removeCallbacks();
            mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
        }
        if (this.devFilter != null) {
            this.devFilter.release();
            this.devFilter = null;
        }
        if (this.mCropImageFilter != null) {
            this.mCropImageFilter.release();
            this.mCropImageFilter = null;
        }
        if (this.minFilter != null) {
            this.minFilter.release();
            this.minFilter = null;
        }
        if (this.yuvConv != null) {
            this.yuvConv.releaseYuvToRgbResources();
            this.yuvConv = null;
        }
        if (this.mYuvToRGBDsp != null) {
            this.mYuvToRGBDsp.release();
            this.mYuvToRGBDsp = null;
        }
        if (this.mScaledImageFilter != null) {
            this.mScaledImageFilter.release();
            this.mScaledImageFilter = null;
        }
        if (this.aviExporter != null) {
            try {
                this.aviExporter.close();
            } catch (Exception e) {
                Log.d(this.TAG, e.toString());
            }
            this.aviExporter = null;
        }
        this.isFirstTakePicture = false;
        System.gc();
        Log.d(this.TAG, "*******************Release memory end");
    }

    private void createMovieShootingObjects() {
        if (this.devFilter == null) {
            this.devFilter = new CameraSequence.DefaultDevelopFilter();
        }
        this.isMiniatureEffectSet = PictureEffectController.getInstance().getValue("PictureEffect").equalsIgnoreCase(PictureEffectController.MODE_MINIATURE);
        if (this.isMiniatureEffectSet) {
            setMiniatureID();
        }
        if (this.minFilter == null && this.isMiniatureEffectSet) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setPictureEffect("off");
            CameraSetting.getInstance().setParameters(params);
            this.minFilter = new MiniatureImageFilter();
        }
        if (this.aviExporter == null) {
            this.aviExporter = new AviExporter();
        }
        if (this.mScaledImageFilter == null) {
            this.mScaledImageFilter = new ScaleImageFilter();
        }
        if (this.yuvConv == null && this.mYuvToRGBDsp != null) {
            this.yuvConv = new YuvToRgbConversion(this.mYuvToRGBDsp);
        }
    }

    private void setMiniatureID() {
        String pictureEffect = PictureEffectController.getInstance().getValue(PictureEffectController.MODE_MINIATURE);
        if (PictureEffectController.MINIATURE_LEFT.equals(pictureEffect)) {
            this.mMiniatureId = 3;
            return;
        }
        if ("hcenter".equals(pictureEffect)) {
            this.mMiniatureId = 1;
            return;
        }
        if (PictureEffectController.MINIATURE_UPPER.equals(pictureEffect)) {
            this.mMiniatureId = 5;
            return;
        }
        if (PictureEffectController.MINIATURE_VCENTER.equals(pictureEffect)) {
            this.mMiniatureId = 2;
            return;
        }
        if (PictureEffectController.MINIATURE_LOWER.equals(pictureEffect)) {
            this.mMiniatureId = 6;
        } else if (PictureEffectController.MINIATURE_RIGHT.equals(pictureEffect)) {
            this.mMiniatureId = 4;
        } else if ("auto".equals(pictureEffect)) {
            this.mMiniatureId = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIntervalCautionIcon() {
        long timeoutTime = System.currentTimeMillis();
        if (timeoutTime - this.startTime > (this.cUtil.getInterval() * 1000) + 500) {
            TwoSecondTimerLayout.isShootingCautionIconDisp = true;
        } else {
            TwoSecondTimerLayout.isShootingCautionIconDisp = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RunnableTask {
        private Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLAviCaptureProcess.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                if (TLAviCaptureProcess.this.isFirstTakePicture) {
                    try {
                        TLAviCaptureProcess.this.isFirstTakePicture = false;
                        TLAviCaptureProcess.this.mTLEX.pauseMetering(TLAviCaptureProcess.this.mCameraEx);
                        return;
                    } catch (Exception e) {
                        Log.d(TLAviCaptureProcess.this.TAG, "Exception occured while taking burstable picture");
                        return;
                    }
                }
                TLAviCaptureProcess.this.isTimeOut = true;
                if (TLAviCaptureProcess.this.mStatus == 0 && TLAviCaptureProcess.this.mCounter < TLAviCaptureProcess.this.cUtil.getShootingNum() && !TLAviCaptureProcess.this.isCancelledTakePicture && !TLAviCaptureProcess.this.cUtil.isDetachedLens()) {
                    TLAviCaptureProcess.this.setIntervalCautionIcon();
                    try {
                        if (TLAviCaptureProcess.this.isProcessTaskFinished) {
                            AppLog.info(TLAviCaptureProcess.this.TAG, "AE tracking implementation = pauseMetering in run method");
                            TLAviCaptureProcess.this.mTLEX.pauseMetering(TLAviCaptureProcess.this.mCameraEx);
                            Log.d(TLAviCaptureProcess.this.TAG, "takePicture in RunnableTask");
                            TLAviCaptureProcess.this.isTimeOut = false;
                            TLAviCaptureProcess.this.isProcessTaskFinished = false;
                        }
                    } catch (Exception e2) {
                        Log.d(TLAviCaptureProcess.this.TAG, "Exception occured while taking burstable picture" + e2.toString());
                    }
                }
            }
        };

        public RunnableTask(Handler hnd) {
            this.handler = null;
            this.handler = hnd;
        }

        public void execute(int time) {
            this.handler.postDelayed(this.r, time);
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
            this.r = null;
        }
    }

    private OptimizedImage get16to9CroppedImage(OptimizedImage optImg) {
        OptimizedImage croppedImg = null;
        int origWidth = optImg.getWidth();
        int origHeight = optImg.getHeight();
        Log.d(this.TAG, "crop: original hegith : " + origHeight + " width : " + origWidth);
        double formal = (origWidth / 16.0f) * 9.0f;
        Log.d(this.TAG, "crop: hegith " + formal);
        int height = ((int) ((formal / 8.0d) + 0.5d)) * 8;
        Log.d(this.TAG, "crop: hegith " + height);
        int top = (origHeight - height) / 2;
        int top2 = top + (top % 4);
        int right = 0 + origWidth;
        int bottom = top2 + height;
        if (bottom > origHeight) {
            bottom = origHeight;
        }
        Log.d(this.TAG, "crop: left 0: top " + top2 + ": right " + right + ": bottom " + bottom);
        if (this.mCropImageFilter == null) {
            this.mCropImageFilter = new CropImageFilter();
        }
        this.mCropImageFilter.setSrcRect(0, top2, right, bottom);
        this.mCropImageFilter.setSource(optImg, false);
        boolean resultCrop = this.mCropImageFilter.execute();
        if (resultCrop) {
            croppedImg = this.mCropImageFilter.getOutput();
        }
        this.mCropImageFilter.clearSources();
        return croppedImg;
    }

    private OptimizedImage getScaled_3_4_Image(OptimizedImage optImage, int width, int height) {
        OptimizedImage optImage_3_4_scaled = null;
        float scale = 0.75f;
        if (this.mAspect != 169) {
            scale = 1.0f;
        }
        int scaledWidth = (int) (width * scale);
        int scaledHeight = height & (-2);
        this.mScaledImageFilter.setSource(optImage, true);
        this.mScaledImageFilter.setDestSize(scaledWidth & (-4), scaledHeight);
        boolean isExecuted = this.mScaledImageFilter.execute();
        if (isExecuted) {
            optImage_3_4_scaled = this.mScaledImageFilter.getOutput();
        }
        this.mScaledImageFilter.clearSources();
        return optImage_3_4_scaled;
    }

    private void checkNumberOfAviFile() {
        this.mNumberOfShots++;
        if (TLCommonUtil.getInstance().getFrameNumberPerAviFile() < this.mNumberOfShots) {
            this.mNumberOfShots = 0;
            this.storeThumbnail = true;
            try {
                this.aviExporter.close();
            } catch (Exception e) {
                Log.d(this.TAG, e.toString());
            }
        }
    }

    private OptimizedImage storeFrame_Thumbnail(OptimizedImage img) {
        OptimizedImage optImage;
        OptimizedImage scaledImage = null;
        if (this.isMovieSize_1920_1080) {
            optImage = getScaledImage(img, 1920, 1080);
        } else {
            optImage = getScaledImage(img, 1280, 720);
        }
        releaseOptimizedImage(img);
        checkNumberOfAviFile();
        if (this.storeThumbnail) {
            createAviExporterOptionData(1 == this.mCounter);
            if (!this.aviExporter.storeThumbnail(optImage, false)) {
                this.mCounter = 0;
                ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
                this.isCancelledTakePicture = true;
            }
            this.storeThumbnail = false;
        }
        try {
            if (!this.aviExporter.storeFrame(optImage, false)) {
                this.isCancelledTakePicture = true;
            }
        } catch (Exception e) {
            this.isCancelledTakePicture = true;
        }
        if (checkBmpPosition(this.mCounter)) {
            scaledImage = getScaled_3_4_Image(optImage, 160, 90);
        }
        releaseOptimizedImage(optImage);
        return scaledImage;
    }

    private void processTask(CameraSequence.RawData raw) {
        OptimizedImage img;
        OptimizedImage scaledImg = null;
        boolean isRAWJPEG = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
        boolean isRAW = PictureQualityController.PICTURE_QUALITY_RAW.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
        try {
            if (isRAW) {
                this.devFilter.setRawFileStoreEnabled(false);
                this.devFilter.setSource(raw, false);
                this.devFilter.execute();
                img = this.devFilter.getOutput();
                this.devFilter.clearSources();
            } else {
                this.devFilter.setRawFileStoreEnabled(isRAWJPEG);
                this.devFilter.setSource(raw, !isRAWJPEG);
                this.devFilter.execute();
                img = this.devFilter.getOutput();
                this.devFilter.clearSources();
                if (isRAWJPEG) {
                    this.mSequence.storeImage(img, false);
                    if (raw != null && raw.isValid()) {
                        raw.release();
                    }
                } else {
                    if (this.isMiniatureEffectSet) {
                        img = getMiniatureOptimizedImage(img);
                    }
                    this.mSequence.storeImage(img, false);
                }
            }
            if (this.aviExporter == null) {
                this.aviExporter = new AviExporter();
            }
            OptimizedImage optImage = get16to9CroppedImage(img);
            if (optImage != null) {
                releaseOptimizedImage(img);
                scaledImg = storeFrame_Thumbnail(optImage);
                if (TLCommonUtil.getInstance().decrementTotalRemaining() <= 0) {
                    Log.d(this.TAG, "enableNextCapture memory full");
                    this.isCancelledTakePicture = true;
                }
            }
        } catch (Exception e) {
            this.isCancelledTakePicture = true;
            Log.d(this.TAG, "*************** processTask exception*******************");
        }
        if (isRAW) {
            this.mSequence.storeImage(raw, true);
        } else {
            releaseRawData(raw);
        }
        this.isCapturing = false;
        if (this.mAdapter.enableHalt(true)) {
            releaseOptimizedImage(scaledImg);
            return;
        }
        if (this.cUtil != null && this.mCounter < this.cUtil.getShootingNum()) {
            if (scaledImg != null) {
                addBitmapinList(scaledImg);
            }
            if (this.isCancelledTakePicture || this.cUtil.isDetachedLens()) {
                Log.d(this.TAG, "enableNextCapture in ProcessTask");
                this.mAdapter.enableNextCapture(1);
                enableNextPictureSetting();
                return;
            }
            this.isProcessTaskFinished = true;
            if (this.isTimeOut) {
                TwoSecondTimerLayout.isShootingCautionIconDisp = true;
                AppLog.info(this.TAG, "AE tracking implementation = pauseMetering in after storeImage()   method ");
                this.mTLEX.pauseMetering(this.mCameraEx);
                this.isProcessTaskFinished = false;
                this.isTimeOut = false;
                return;
            }
            return;
        }
        releaseOptimizedImage(scaledImg);
        Log.d(this.TAG, "****************************************************Process task 1 image");
        this.isCancelledTakePicture = true;
        enableNextPictureSetting();
        if (this.mAdapter != null) {
            this.mAdapter.enableNextCapture(this.mStatus);
        }
    }

    private void addBitmapinList(OptimizedImage optImage) {
        if (TimeLapseConstants.sFrameBitmapList.size() < 5 && this.yuvConv != null) {
            Bitmap bmp = this.yuvConv.yuv2rgb_main(optImage);
            releaseOptimizedImage(optImage);
            if (bmp != null) {
                TimeLapseConstants.sFrameBitmapList.add(bmp);
                CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.THUMBNAIL_UPDATE);
            }
            this.yuvConv.releaseYuvToRgbResources();
        }
    }

    public boolean checkBmpPosition(int counter) {
        if (!this.isDisplay || counter != (this.bitmapDisplayInterval * TimeLapseConstants.sFrameBitmapList.size()) + this.bitmapDisplayFirstPosition) {
            return false;
        }
        this.bitmapDisplayFirstPosition = 1;
        return true;
    }

    private void releaseRawData(CameraSequence.RawData raw) {
        if (raw != null) {
            raw.release();
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK)) {
            cancelCapture();
        }
    }

    private void cancelCapture() {
        if (this.mCancelRunnable != null) {
            this.mCancelRunnable.execute();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class CancelRunnable {
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLAviCaptureProcess.CancelRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(TLAviCaptureProcess.this.TAG, "CancelRunnable");
                TLAviCaptureProcess.this.mStatus = 1;
                if (!TLAviCaptureProcess.this.isCancelledTakePicture) {
                    TLAviCaptureProcess.this.isCancelledTakePicture = true;
                    if (!TLAviCaptureProcess.this.isCapturing) {
                        Log.d(TLAviCaptureProcess.this.TAG, "****************************************************CancelRunnable");
                        if (TLAviCaptureProcess.this.mAdapter != null) {
                            TLAviCaptureProcess.this.mAdapter.enableNextCapture(1);
                        }
                        TLAviCaptureProcess.this.enableNextPictureSetting();
                    }
                }
            }
        };

        public CancelRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute() {
            if (this.handler != null) {
                this.handler.post(this.r);
            }
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
            this.r = null;
        }
    }

    protected void cancelShootingSequence() {
        if (!this.isCancelledTakePicture) {
            this.isCancelledTakePicture = true;
            if (!this.isCapturing) {
                if (this.mCameraEx != null) {
                    this.mCameraEx.cancelTakePicture();
                }
                Log.d(this.TAG, "****************************************************CancelRunnable");
                if (this.mAdapter != null) {
                    this.mAdapter.enableNextCapture(1);
                }
                enableNextPictureSetting();
            }
        }
    }

    private synchronized OptimizedImage getMiniatureOptimizedImage(OptimizedImage optImg) {
        OptimizedImage opImg;
        if (this.minFilter.isSupported()) {
            this.minFilter.setMiniatureArea(this.mMiniatureId);
            this.minFilter.setSource(optImg, true);
            this.minFilter.execute();
            opImg = this.minFilter.getOutput();
            this.minFilter.clearSources();
        } else {
            opImg = null;
        }
        return opImg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TimelapseExposureControllerCallbackImpl implements TimelapseExposureController.TimelapseExposureControllerCallback {
        private TimelapseExposureControllerCallbackImpl() {
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onStartMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onStartMeteringDone");
            AppLog.info(TLAviCaptureProcess.this.TAG, "AE tracking implementation = onStartMeteringDone in  TimelapseExposureControllerCallbackImpl");
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onPauseMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onPauseMeteringDone");
            AppLog.info(TLAviCaptureProcess.this.TAG, "AE tracking implementation = onPauseMeteringDone in  TimelapseExposureControllerCallbackImpl");
            if (!TLAviCaptureProcess.this.cUtil.isDetachedLens()) {
                TLAviCaptureProcess.this.takePicture();
            }
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onStopMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onStopMeteringDone");
            AppLog.info(TLAviCaptureProcess.this.TAG, "AE tracking implementation = onStopMeteringDone in  TimelapseExposureControllerCallbackImpl");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WatchDogTimerForMaybePhaseDiffRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private WatchDogTimerForMaybePhaseDiffRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLAviCaptureProcess.WatchDogTimerForMaybePhaseDiffRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!TLAviCaptureProcess.this.mAdapter.enableHalt(true) && TLCommonUtil.getInstance().maybePhaseDiffFlag()) {
                        TLAviCaptureProcess.this.isCapturing = false;
                        TLAviCaptureProcess.this.cancelShootingSequence();
                        AppLog.info(TLAviCaptureProcess.this.TAG, "Cancel S2 trigger. Because onShutter() is not coming and maybePhaseDiff is true. ");
                    }
                }
            };
        }

        public void execute(int waitTime) {
            this.mHandler.postDelayed(this.mRunnable, waitTime);
        }

        public void removeCallbacks() {
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mRunnable = null;
            this.mHandler = null;
        }
    }
}
