package com.sony.imaging.app.timelapse.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.AELController;
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
public class TLSingleCaptureProcessOldPF implements ICaptureProcess, NotificationListener {
    public static final int DELAY_OFFSET = 18;
    public static final int EXTRA_MARGIN_TIME = 500;
    public static final int TRACKING_WAIT_VSYNC = 36;
    private IAdapter mAdapter;
    private CameraEx.AutoPictureReviewControl mAutoPicRevCtl;
    private CameraEx mCameraEx;
    private static final String TAG = TLSingleCaptureProcessOldPF.class.getName();
    private static boolean isRemoteControlMode = false;
    private static boolean isFirstShooting = true;
    private static String[] TAGS = {TimeLapseConstants.REMOVE_CAPTURE_CALLBACK, CameraNotificationManager.AE_LOCK, CameraNotificationManager.AE_LOCK_BUTTON};
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
    private boolean isEnableTakePicture = false;
    private boolean isTimeOut = false;
    private boolean isAEL = false;
    private long mFirstTakePictureTime = 0;
    private CameraEx.PreviewStartListener previewListener = new CameraEx.PreviewStartListener() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcessOldPF.1
        public void onStart(CameraEx cam) {
            AppLog.enter(TLSingleCaptureProcessOldPF.TAG, "PreviewStartListener");
            try {
                Thread.sleep(600L);
                TLSingleCaptureProcessOldPF.this.isEnableTakePicture = true;
                if (TLSingleCaptureProcessOldPF.this.mStatus == 0 && TLSingleCaptureProcessOldPF.this.mCounter < TLSingleCaptureProcessOldPF.this.cUtil.getShootingNum() && !TLSingleCaptureProcessOldPF.this.isCancelledTakePicture && TLSingleCaptureProcessOldPF.this.isTimeOut) {
                    TLSingleCaptureProcessOldPF.this.isEnableTakePicture = false;
                    TLSingleCaptureProcessOldPF.this.isProcessTaskFinished = false;
                    TLSingleCaptureProcessOldPF.this.isTimeOut = false;
                    TwoSecondTimerLayout.isShootingCautionIconDisp = true;
                    TLSingleCaptureProcessOldPF.this.takePicture();
                    AppLog.trace(TLSingleCaptureProcessOldPF.TAG, "takePicture in PreviewStartListener");
                }
            } catch (InterruptedException e) {
                AppLog.trace(TLSingleCaptureProcessOldPF.TAG, e.toString());
            }
        }
    };
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
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        this.cUtil.setShutterSpeed();
        isRemoteControlMode = this.cUtil.isRemoteControlMode();
        this.isAEL = this.cUtil.getAETrakingStatus() == 0;
        if (!this.isAEL) {
            this.mCameraEx.setPreviewStartListener(this.previewListener);
        }
        isFirstShooting = true;
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
        this.isFirstTakePicture = false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (!TimeLapseConstants.isMemoryReleased) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            TimeLapseConstants.isMemoryReleased = true;
        }
        if (!this.isAEL) {
            this.mCameraEx.setPreviewStartListener((CameraEx.PreviewStartListener) null);
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
        releaseMemory();
        TLPictureQualityController.getInstance().resetTestShotDummyQuality();
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
        TLPictureQualityController.getInstance().setTestShotDummyQuality();
        setAutoReviewControl();
        this.mCounter = 0;
        this.isCapturing = false;
        this.isProcessTaskFinished = false;
        this.isEnableTakePicture = false;
        this.isTimeOut = false;
        TimeLapseConstants.sCaptureImageCounter = this.mCounter;
        this.isFirstTakePicture = false;
        this.mFirstTakePictureTime = 0L;
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            this.isFirstTakePicture = true;
        }
        createRunnableTask();
        this.isCancelledTakePicture = false;
        setAETrackingCameraSetting();
        setCameraSetting(true);
        isFirstShooting = true;
        CameraNotificationManager.getInstance().setNotificationListener(this);
        TimeLapseConstants.isMemoryReleased = false;
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

    private void setAETrackingCameraSetting() {
        AppLog.enter(TAG, " AELController AETrakingStatus " + this.cUtil.getAETrakingStatus());
        if (this.cUtil.getAETrakingStatus() == 0) {
            AELController.getInstance().holdAELock(true);
            AppLog.enter(TAG, " AELController AE locked");
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
            if (this.mCounter <= 1) {
                setAETrackingCameraSetting();
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
            int adjustIntervalTime = (this.cUtil.getInterval() * 1000) - 18;
            if (this.mCounter > 1) {
                adjustIntervalTime = this.cUtil.getAdjustInterval(this.cUtil.getInterval(), this.mFirstTakePictureTime, 18, this.mCounter);
            } else {
                this.mFirstTakePictureTime = System.currentTimeMillis();
            }
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
        TLCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
        if (TLCommonUtil.getInstance().isSilentShutterOn()) {
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        }
        if (this.cUtil == null) {
            this.cUtil = TLCommonUtil.getInstance();
        }
        AppLog.trace(TAG, "onShutter status =  " + status);
        this.mCameraEx.cancelTakePicture();
        this.mStatus = 2;
        if (!this.mAdapter.enableHalt(true)) {
            if (TLCommonUtil.getInstance().isTestShot()) {
                TestShotSequence(status, cameraEx);
                return;
            }
            this.isCapturing = false;
            isFirstShooting = false;
            if (status == 0 && this.mCounter < this.cUtil.getShootingNum() && !this.isCancelledTakePicture) {
                this.mStatus = 0;
                this.isProcessTaskFinished = true;
                if (this.isTimeOut) {
                    if (this.isEnableTakePicture || this.isAEL) {
                        this.isTimeOut = false;
                        TwoSecondTimerLayout.isShootingCautionIconDisp = true;
                        startRunnableTask(true);
                        AppLog.trace(TAG, "takePicture(Task Retry) in ProcessTask");
                        return;
                    }
                    return;
                }
                return;
            }
            AppLog.trace(TAG, "onShutter : status is not OK -->" + this.mStatus);
            if (1 == this.cUtil.getShootingNum() && status == 0) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                }
            }
            if (status != 0 && this.mCounter >= 1) {
                this.mCounter--;
            }
            isFirstShooting = true;
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
        if (isRemoteControlMode) {
            this.cUtil.setRemoteControlMode(true, this.mCamera);
        }
        releaseMemory();
        setPostTakePictureSetting();
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
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcessOldPF.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                if (TLSingleCaptureProcessOldPF.this.isFirstTakePicture) {
                    try {
                        TLSingleCaptureProcessOldPF.this.isFirstTakePicture = false;
                        TLSingleCaptureProcessOldPF.this.isEnableTakePicture = false;
                        TLSingleCaptureProcessOldPF.this.takePicture();
                        return;
                    } catch (Exception e) {
                        AppLog.trace(TLSingleCaptureProcessOldPF.TAG, "Exception occured while taking burstable picture");
                        return;
                    }
                }
                TLSingleCaptureProcessOldPF.this.isTimeOut = true;
                if (TLSingleCaptureProcessOldPF.this.mStatus == 0 && TLSingleCaptureProcessOldPF.this.mCounter < TLSingleCaptureProcessOldPF.this.cUtil.getShootingNum() && !TLSingleCaptureProcessOldPF.this.isCancelledTakePicture) {
                    TLSingleCaptureProcessOldPF.this.setIntervalCautionIcon();
                    try {
                        if (TLSingleCaptureProcessOldPF.this.isProcessTaskFinished) {
                            if (TLSingleCaptureProcessOldPF.this.isEnableTakePicture || TLSingleCaptureProcessOldPF.this.isAEL) {
                                TLSingleCaptureProcessOldPF.this.isTimeOut = false;
                                TLSingleCaptureProcessOldPF.this.isProcessTaskFinished = false;
                                TLSingleCaptureProcessOldPF.this.isEnableTakePicture = false;
                                TLSingleCaptureProcessOldPF.this.takePicture();
                                AppLog.trace(TLSingleCaptureProcessOldPF.TAG, "takePicture in RunnableTask");
                            }
                        }
                    } catch (Exception e2) {
                        AppLog.trace(TLSingleCaptureProcessOldPF.TAG, "Exception occured while taking burstable picture" + e2.toString());
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
            return;
        }
        if ((CameraNotificationManager.AE_LOCK_BUTTON.equals(tag) || CameraNotificationManager.AE_LOCK.equals(tag)) && TimeLapseConstants.sCaptureImageCounter <= 1 && !AELController.getInstance().getAELockButtonState()) {
            Log.i(TAG, "AELockButtonState is false and set on from notify()");
            AELController.getInstance().holdAELock(true);
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
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcessOldPF.CancelRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                if (!TLSingleCaptureProcessOldPF.this.isCancelledTakePicture) {
                    TLSingleCaptureProcessOldPF.this.isCancelledTakePicture = true;
                    if (!TLSingleCaptureProcessOldPF.this.isCapturing) {
                        AppLog.trace(TLSingleCaptureProcessOldPF.TAG, "****************************************************CancelRunnable");
                        if (TLSingleCaptureProcessOldPF.this.mAdapter != null) {
                            TLSingleCaptureProcessOldPF.this.mAdapter.enableNextCapture(1);
                        }
                        TLSingleCaptureProcessOldPF.this.enableNextPictureSetting();
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
    public class WatchDogTimerForMaybePhaseDiffRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private WatchDogTimerForMaybePhaseDiffRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.TLSingleCaptureProcessOldPF.WatchDogTimerForMaybePhaseDiffRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!TLSingleCaptureProcessOldPF.this.mAdapter.enableHalt(true) && TLCommonUtil.getInstance().maybePhaseDiffFlag()) {
                        TLSingleCaptureProcessOldPF.this.isCapturing = false;
                        TLSingleCaptureProcessOldPF.this.cancelShootingSequence();
                        AppLog.info(TLSingleCaptureProcessOldPF.TAG, "Cancel S2 trigger. Because onShutter() is not coming and maybePhaseDiff is true. ");
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
