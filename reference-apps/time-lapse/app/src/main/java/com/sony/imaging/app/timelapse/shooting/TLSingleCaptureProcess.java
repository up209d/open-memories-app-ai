package com.sony.imaging.app.timelapse.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLPictureQualityController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class TLSingleCaptureProcess implements ICaptureProcess, NotificationListener {
    public static final int DELAY_OFFSET = 90;
    public static final int EXTRA_MARGIN_TIME = 500;
    private IAdapter mAdapter;
    private CameraEx.AutoPictureReviewControl mAutoPicRevCtl;
    private CameraEx mCameraEx;
    private static final String TAG = TLSingleCaptureProcess.class.getName();
    private static boolean isRemoteControlMode = false;
    private static boolean isFirstShooting = true;
    private static String[] TAGS = {TimeLapseConstants.REMOVE_CAPTURE_CALLBACK};
    private static WatchDogTimerForMaybePhaseDiffRunnableTask mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
    private int mCounter = 0;
    private Camera mCamera = null;
    private RunnableTask mRunnableTask = null;
    private CancelRunnable mCancelRunnable = null;
    private TLCommonUtil cUtil = null;
    private boolean isFirstTakePicture = false;
    private Handler myHandler = null;
    private boolean isCancelledTakePicture = false;
    private boolean isCapturing = false;
    private long startTime = 0;
    private boolean isProcessTaskFinished = false;
    private boolean isTimeOut = false;
    TimelapseExposureController mTLEX = null;
    private long mFirstTakePictureTime = 0;
    int mStatus = 2;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mAdapter = adapter;
        this.mCameraEx = cameraEx;
        this.mCamera = this.mCameraEx.getNormalCamera();
        if (this.myHandler == null) {
            this.myHandler = new Handler();
        }
        setAutoReviewControl();
        initializeExposureControllerForAE();
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        isRemoteControlMode = this.cUtil.isRemoteControlMode();
        isFirstShooting = true;
        this.cUtil.setShutterSpeed();
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
            AppLog.info(TAG, "AE tracking implementation = startMetering in  setAETrackingCameraSetting()");
            this.mTLEX.startMetering(this.mCameraEx);
        }
    }

    private void releaseMemory() {
        ((TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance()).setCounter(this.mCounter);
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
        this.cUtil = null;
        this.isFirstTakePicture = false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (!TimeLapseConstants.isMemoryReleased) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            TimeLapseConstants.isMemoryReleased = true;
        }
        if (!this.isCancelledTakePicture) {
            this.isCancelledTakePicture = true;
            if (!this.isCapturing) {
                AppLog.trace(TAG, "****************************************************CancelRunnable");
                if (this.mAdapter != null) {
                    this.mAdapter.enableNextCapture(1);
                }
            }
        }
        if (this.mTLEX != null && this.mCameraEx != null) {
            this.mTLEX.stopMetering(this.mCameraEx);
            this.mTLEX.close();
        }
        TLPictureQualityController.getInstance().resetTestShotDummyQuality();
        releaseMemory();
        this.mCameraEx = null;
        this.mAdapter = null;
        this.mAutoPicRevCtl = null;
        this.isFirstTakePicture = false;
        isRemoteControlMode = false;
        this.mFirstTakePictureTime = 0L;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        this.cUtil = TLCommonUtil.getInstance();
        if (isRemoteControlMode) {
            this.cUtil.setRemoteControlMode(false, this.mCamera);
        }
        isFirstShooting = true;
        TLPictureQualityController.getInstance().setTestShotDummyQuality();
        setAutoReviewControl();
        this.mCounter = 0;
        this.isCapturing = false;
        this.isProcessTaskFinished = false;
        this.isTimeOut = false;
        TimeLapseConstants.sCaptureImageCounter = this.mCounter;
        this.isFirstTakePicture = false;
        this.mFirstTakePictureTime = 0L;
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            this.isFirstTakePicture = true;
        }
        createRunnableTask();
        this.isCancelledTakePicture = false;
        setCameraSetting(true);
        TimeLapseConstants.isMemoryReleased = false;
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    private void setAutoReviewControl() {
        if (this.mAutoPicRevCtl == null) {
            this.mAutoPicRevCtl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPicRevCtl);
            this.mAutoPicRevCtl.setPictureReviewTime(0);
        }
    }

    private void setPostTakePictureSetting() {
        if (!TimeLapseConstants.isMemoryReleased) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            TimeLapseConstants.isMemoryReleased = true;
        }
        setCameraSetting(false);
        this.mAutoPicRevCtl = null;
    }

    private void setCameraSetting(boolean shouldLock) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = null;
        if (this.mCameraEx != null) {
            params = CameraSetting.getInstance().getEmptyParameters();
        } else {
            Log.i(TAG, "Camera settings instance not available ");
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
            startRunnableTask();
        }
    }

    private void startRunnableTask() {
        startRunnableTask(false);
    }

    private void startRunnableTask(boolean retry) {
        if (!retry) {
            this.startTime = System.currentTimeMillis();
            this.mCounter++;
            TimeLapseConstants.sCaptureImageCounter = this.mCounter;
            TLCommonUtil.getInstance().setCaptureStatus(true);
            TimeLapseStableLayout.isCapturing = true;
            CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.TIMER_UPDATE);
        }
        if (this.cUtil != null && this.mCounter < this.cUtil.getShootingNum() && this.mRunnableTask != null) {
            int adjustIntervalTime = (this.cUtil.getInterval() * 1000) - 90;
            if (this.mCounter > 1) {
                adjustIntervalTime = this.cUtil.getAdjustInterval(this.cUtil.getInterval(), this.mFirstTakePictureTime, 90, this.mCounter);
            } else {
                this.mFirstTakePictureTime = System.currentTimeMillis();
            }
            Log.e(TAG, "adjustIntervalTime = " + adjustIntervalTime);
            this.mRunnableTask.execute(adjustIntervalTime);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    private void createRunnableTask() {
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask();
        }
        if (this.mCancelRunnable == null) {
            this.mCancelRunnable = new CancelRunnable(this.myHandler);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.info(TAG, "onShutter(int status " + status);
        if (TLCommonUtil.getInstance().isSilentShutterOn()) {
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        }
        TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        this.mTLEX.resumeMetering(this.mCameraEx);
        this.mCameraEx.cancelTakePicture();
        this.mStatus = 2;
        if (!this.mAdapter.enableHalt(true)) {
            if (TLCommonUtil.getInstance().isTestShot()) {
                TestShotSequence(status, cameraEx);
                return;
            }
            this.isCapturing = false;
            if (status == 0 && this.mCounter < this.cUtil.getShootingNum() && !this.isCancelledTakePicture) {
                this.mStatus = 0;
                this.isProcessTaskFinished = true;
                if (this.isTimeOut) {
                    TwoSecondTimerLayout.isShootingCautionIconDisp = true;
                    AppLog.info(TAG, "AE tracking implementation = pauseMetering in after storeImage()   method ");
                    startRunnableTask(true);
                    AppLog.trace(TAG, "takePicture(Task Retry) in ProcessTask");
                    this.isTimeOut = false;
                    return;
                }
                return;
            }
            AppLog.trace(TAG, "onShutter : status is not OK -->" + this.mStatus);
            isFirstShooting = false;
            if (1 == this.cUtil.getShootingNum() && status == 0) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                }
            }
            if (status != 0 && this.mCounter >= 1) {
                this.mCounter--;
            }
            this.isCancelledTakePicture = true;
            this.mAdapter.enableNextCapture(status);
            enableNextPictureSetting();
        }
    }

    private void TestShotSequence(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter.enableNextCapture(status);
        enableNextPictureSetting();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableNextPictureSetting() {
        if (this.mTLEX != null && this.mCameraEx != null) {
            this.mTLEX.stopMetering(this.mCameraEx);
            this.mTLEX.close();
            initializeExposureControllerForAE();
        }
        setPostTakePictureSetting();
        if (isRemoteControlMode) {
            this.cUtil.setRemoteControlMode(true, this.mCamera);
        }
        releaseMemory();
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
        private Handler handler = new Handler();
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcess.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                if (TLSingleCaptureProcess.this.isFirstTakePicture) {
                    try {
                        TLSingleCaptureProcess.this.isFirstTakePicture = false;
                        TLSingleCaptureProcess.this.mTLEX.pauseMetering(TLSingleCaptureProcess.this.mCameraEx);
                        return;
                    } catch (Exception e) {
                        AppLog.trace(TLSingleCaptureProcess.TAG, "Exception occured while taking burstable picture");
                        return;
                    }
                }
                TLSingleCaptureProcess.this.isTimeOut = true;
                if (TLSingleCaptureProcess.this.mStatus == 0 && TLSingleCaptureProcess.this.mCounter < TLSingleCaptureProcess.this.cUtil.getShootingNum() && !TLSingleCaptureProcess.this.isCancelledTakePicture) {
                    TLSingleCaptureProcess.this.setIntervalCautionIcon();
                    try {
                        if (TLSingleCaptureProcess.this.isProcessTaskFinished) {
                            AppLog.info(TLSingleCaptureProcess.TAG, "AE tracking implementation = pauseMetering in after storeImage()   method ");
                            TLSingleCaptureProcess.this.mTLEX.pauseMetering(TLSingleCaptureProcess.this.mCameraEx);
                            AppLog.trace(TLSingleCaptureProcess.TAG, "takePicture in RunnableTask");
                            TLSingleCaptureProcess.this.isTimeOut = false;
                            TLSingleCaptureProcess.this.isProcessTaskFinished = false;
                        }
                    } catch (Exception e2) {
                        AppLog.trace(TLSingleCaptureProcess.TAG, "Exception occured while taking burstable picture" + e2.toString());
                    }
                }
            }
        };

        protected RunnableTask() {
        }

        public void execute(int time) {
            this.handler.postDelayed(this.r, time);
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
            this.r = null;
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
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcess.CancelRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                if (!TLSingleCaptureProcess.this.isCancelledTakePicture) {
                    TLSingleCaptureProcess.this.isCancelledTakePicture = true;
                    if (!TLSingleCaptureProcess.this.isCapturing) {
                        AppLog.trace(TLSingleCaptureProcess.TAG, "****************************************************CancelRunnable");
                        if (TLSingleCaptureProcess.this.mAdapter != null) {
                            TLSingleCaptureProcess.this.mAdapter.enableNextCapture(1);
                        }
                        TLSingleCaptureProcess.this.enableNextPictureSetting();
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TimelapseExposureControllerCallbackImpl implements TimelapseExposureController.TimelapseExposureControllerCallback {
        private TimelapseExposureControllerCallbackImpl() {
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onStartMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onStartMeteringDone");
            AppLog.info(TLSingleCaptureProcess.TAG, "AE tracking implementation = onStartMeteringDone in  TimelapseExposureControllerCallbackImpl");
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onPauseMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onPauseMeteringDone");
            AppLog.info(TLSingleCaptureProcess.TAG, "AE tracking implementation = onPauseMeteringDone in  TimelapseExposureControllerCallbackImpl");
            TLSingleCaptureProcess.this.takePicture();
        }

        @Override // com.sony.imaging.app.avi.timelapse.exposure.TimelapseExposureController.TimelapseExposureControllerCallback
        public void onStopMeteringDone(CameraEx cameraEx) {
            Log.e("Timelapse", "onStopMeteringDone");
            AppLog.info(TLSingleCaptureProcess.TAG, "AE tracking implementation = onStopMeteringDone in  TimelapseExposureControllerCallbackImpl");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WatchDogTimerForMaybePhaseDiffRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private WatchDogTimerForMaybePhaseDiffRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcess.WatchDogTimerForMaybePhaseDiffRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!TLSingleCaptureProcess.this.mAdapter.enableHalt(true) && TLCommonUtil.getInstance().maybePhaseDiffFlag()) {
                        TLSingleCaptureProcess.this.isCapturing = false;
                        TLSingleCaptureProcess.this.cancelShootingSequence();
                        AppLog.info(TLSingleCaptureProcess.TAG, "Cancel S2 trigger. Because onShutter() is not coming and maybePhaseDiff is true. ");
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

    protected void cancelShootingSequence() {
        if (!this.isCancelledTakePicture) {
            this.isCancelledTakePicture = true;
            if (!this.isCapturing) {
                if (this.mCameraEx != null) {
                    this.mCameraEx.cancelTakePicture();
                }
                Log.d(TAG, "****************************************************CancelRunnable");
                if (this.mAdapter != null) {
                    this.mAdapter.enableNextCapture(1);
                }
                enableNextPictureSetting();
            }
        }
    }
}
