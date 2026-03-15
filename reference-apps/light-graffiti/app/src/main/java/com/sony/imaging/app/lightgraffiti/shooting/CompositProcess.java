package com.sony.imaging.app.lightgraffiti.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.IntervalRecCaptureState;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.lightgraffiti.LightGraffitiApp;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGFlashController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGFocusModeController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGRemoConController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGLensProblemCautionRelayState;
import com.sony.imaging.app.lightgraffiti.shooting.timer.LGSelfTimerThread;
import com.sony.imaging.app.lightgraffiti.shooting.timer.LGTimerThread;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGImageUtil;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.lightgraffiti.util.LGSAMixFilter;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.hardware.indicator.Light;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;

/* loaded from: classes.dex */
public class CompositProcess implements IImagingProcess, ICaptureProcess, LGStateHolder.PrepareCallback, NotificationListener {
    private static final int AUTOREVIEW_TIME_OFF = 0;
    private static final int AUTOREVIEW_TIME_WORKAROUND = 2;
    private static IAdapter sAdapter;
    private CameraEx mCameraEx;
    private Double mPFVersion;
    public static final String TAG = CompositProcess.class.getSimpleName();
    public static boolean isThisProcessInProcessing = false;
    private static String[] TAGS = {LGConstants.IN_CAPTURE_S2_PUSHED, LGConstants.CONFIRM_REVIEW_CLOSE, LGConstants.CONFIRM_REVIEW_FINISH, LGConstants.SHOOTING_2ND_CLOSE};
    private static boolean isTakePictureStarted = false;
    private int mPictureReviewTime = 0;
    private int mOriginalEvComp = 0;
    private CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;
    private LGStateHolder mLGStateHolder = null;
    private String mProcessStage = "";
    private RunnableTask mRunnableTask = null;
    private CancelRunnable mCancelRunnable = null;
    public final int BURSTABLE_TAKE_PICTURE_DELAY_INTERVAL = 440;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        Log.d(TAG, "Prepare process entered.");
        this.mLGStateHolder = LGStateHolder.getInstance();
        this.mLGStateHolder.setPrepareCallback(this);
        this.mCameraEx = cameraEx;
        sAdapter = adapter;
        sAdapter.enableNextCapture(1);
        onPrepare();
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.PrepareCallback
    public void onPrepare() {
        if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            Log.d(TAG, "Shooting 1st prepare");
            this.mPFVersion = Double.valueOf(Double.parseDouble(ScalarProperties.getString("version.platform")));
            Log.i(TAG, "PFVersion: " + this.mPFVersion);
            MemoryMapConfig.setAllocationPolicy(1);
            CameraSequence.Options opt = new CameraSequence.Options();
            opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence("LightGraffiti", "00"));
            opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
            opt.setOption("DETECTION_OFF", false);
            opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
            if (is__EXPOSURE_COUNT_moreThan2_accepted()) {
                opt.setOption("EXPOSURE_COUNT", 1);
            }
            opt.setOption("RECORD_COUNT", 1);
            sAdapter.setOptions(opt);
            this.mPictureReviewTime = getCurrentAutoReviewTime();
            this.mOriginalEvComp = ExposureCompensationController.getInstance().getExposureCompensationIndex();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params.first).setExposureCompensation(0);
            CameraSetting.getInstance().setParameters(params);
        } else if (this.mLGStateHolder.isShootingStage3rd()) {
            Log.d(TAG, "Shooting 3rd prepare");
            CameraSequence.Options opt2 = new CameraSequence.Options();
            opt2.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence("LightGraffiti", "00"));
            opt2.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
            opt2.setOption("DETECTION_OFF", false);
            opt2.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
            if (is__EXPOSURE_COUNT_moreThan2_accepted()) {
                opt2.setOption("EXPOSURE_COUNT", 1);
            }
            opt2.setOption("RECORD_COUNT", 1);
            sAdapter.setOptions(opt2);
            LGPreviewEffect.getInstance().startPreviewEffect();
        }
        AELController.getInstance().cancelAELock();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        Log.d(TAG, "terminate() called.");
        this.mCameraEx = null;
        sAdapter = null;
        this.mAutoPictureReviewControl = null;
        LGTimerThread.getInstance().timerStop();
        this.mLGStateHolder.setPrepareCallback(null);
        this.mLGStateHolder.setIsProcessInProcessing(false);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        if (!this.mLGStateHolder.getShootingStageNext().equals(LGStateHolder.SELFTIMER_COUNTING)) {
            isTakePictureStarted = true;
        }
        if (LGLensProblemCautionRelayState.isLensProblemState) {
            Log.d(TAG, "takePicture isLensProblemState = true");
            this.mCameraEx.cancelTakePicture();
            sAdapter.enableNextCapture(1);
            return;
        }
        Log.d(TAG, "Is Start Focusing : " + LGStateHolder.getInstance().getIsStartFocusing().toString() + ". IS Remote Shutter : " + LGStateHolder.getInstance().getIsRemoteShutter().toString());
        if (LGStateHolder.getInstance().getIsStartFocusing().booleanValue() && !LGStateHolder.getInstance().getIsRemoteShutter().booleanValue()) {
            Log.d(TAG, "Already focused shooting process entered. (S2/Selftimer/2nd time shoot.)");
            try {
                if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                    Log.d(TAG, "not Selftimer");
                    myStartShutter();
                } else {
                    Log.d(TAG, "Selftimer");
                    if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING)) {
                        Log.d(TAG, "Selftimer counted and going on shutter.");
                        LGRemoConController.getInstance().setValue(false);
                        myStartShutter();
                    } else {
                        Log.d(TAG, "Selftimer hasn't counted and going on start selftimer.");
                        myStartSelfTimerShutter();
                    }
                }
                return;
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
                return;
            }
        }
        Log.d(TAG, "Not focused shooting process entered.");
        if (LGStateHolder.getInstance().getIsRemoteShutter().booleanValue()) {
            Log.d(TAG, "Remote Shutter.");
            LGFocusControl();
            return;
        }
        Log.d(TAG, "Not Remote Shutter.");
        this.mCameraEx.cancelTakePicture();
        unLockCameraSettings();
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
        this.mLGStateHolder.setIsStartFocusing(false);
        Log.d(TAG, "Shutter status is STATUS_CANCELED");
        if (!this.mLGStateHolder.isShootingStage3rd()) {
            this.mLGStateHolder.prepareShootingStage1st();
        }
        sAdapter.enableNextCapture(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void myStartShutter() {
        Log.d(TAG, "Start shutter w/o self-timer.");
        Log.d(TAG, "stopPreviewEffect");
        LGPreviewEffect.getInstance().stopPreviewEffect();
        LGSelfTimerThread.getInstance().stopSelftimer();
        Light.setState("LID_SELF_TIMER", false);
        if (LGStateHolder.getInstance().getShootingStageNext().equals(LGStateHolder.EXPOSING_1ST)) {
            Log.d(TAG, "Lock camera settings");
            lockCameraSettings();
        }
        if (this.mLGStateHolder.getShootingStageNext().equals(LGStateHolder.EXPOSING_1ST) || this.mLGStateHolder.getShootingStageNext().equals(LGStateHolder.EXPOSING_2ND)) {
            float exCompStep = ExposureCompensationController.getInstance().getExposureCompensationStep();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params.first).setExposureCompensation((int) ((-3.0f) / exCompStep));
            CameraSetting.getInstance().setParameters(params);
            Log.d(TAG, "set exposure compensation step = " + ((int) ((-3.0f) / exCompStep)));
        }
        String stage = this.mLGStateHolder.getShootingStageNext();
        if (!LGStateHolder.EXPOSING_3RD.equals(stage) && !is__EXPOSURE_COUNT_moreThan2_accepted()) {
            Log.i(TAG, "Set AutoReview time to 0 sec for PF 2.0 and later. this wil be restored at final takepicture");
            setAutoReviewControl(0);
        }
        ((LGNormalExecutor) ExecutorCreator.getInstance().getSequence()).myLockAutoFocus(true);
        Log.d(TAG, "takePicture <<<<");
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.mProcessStage = this.mLGStateHolder.getShootingStageNext();
        this.mLGStateHolder.setIsProcessInProcessing(true);
        LGUtility.getInstance().calculateRemainingMemory();
        if (this.mLGStateHolder.getShootingStageNext().equals(LGStateHolder.EXPOSING_1ST)) {
            sAdapter.enableHalt(true);
            if (this.mCameraEx != null) {
                this.mCameraEx.setRecordingMedia(AvindexStore.getVirtualMediaIds()[0], new CameraEx.RecordingMediaChangeCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.1
                    public synchronized void onRecordingMediaChange(CameraEx cameraEx) {
                        Log.d(CompositProcess.TAG, "Change media to virtual media done");
                        CompositProcess.sAdapter.enableHalt(false);
                        CompositProcess.this.burstableTakePictureWithDealy();
                    }
                });
            }
        } else if (this.mLGStateHolder.getShootingStageNext().equals(LGStateHolder.EXPOSING_2ND)) {
            Log.d(TAG, "2nd time shoot call.");
            sAdapter.enableHalt(false);
            burstableTakePictureWithDealy();
        } else {
            Log.d(TAG, "3rd time shoot call.");
            sAdapter.enableHalt(false);
            this.mCameraEx.burstableTakePicture();
        }
        if (LGStateHolder.EXPOSING_1ST.equals(this.mProcessStage)) {
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SHOOTING_STAGE_1ST);
        } else if (LGStateHolder.EXPOSING_2ND.equals(this.mProcessStage)) {
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SHOOTING_STAGE_2ND);
        } else if (LGStateHolder.EXPOSING_3RD.equals(this.mProcessStage)) {
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SHOOTING_STAGE_3RD);
            String flasVal = LGFlashController.getInstance().getValue(FlashController.FLASHMODE);
            if (!"off".equals(flasVal)) {
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_FLASH_LIGHT_USED_IN_3RD);
            }
        }
        Log.d(TAG, "takePicture >>>>");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void burstableTakePictureWithDealy() {
        Log.d(TAG, "called burstableTakePictureWithDealy.");
        Runnable execTakePicture = new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.2
            @Override // java.lang.Runnable
            public void run() {
                Log.d(CompositProcess.TAG, "called burstableTakePicture Runnable");
                LGRemoConController.getInstance().setValue(false);
                try {
                    CompositProcess.this.mCameraEx.burstableTakePicture();
                } catch (Exception e) {
                    LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                    Log.e(CompositProcess.TAG, "burstableTakePicture fail.");
                    e.printStackTrace();
                }
            }
        };
        new Handler().postDelayed(execTakePicture, 440L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void myStartSelfTimerShutter() {
        Log.d(TAG, "Selftimer ON and not counting.");
        this.mLGStateHolder.setShootingStageNext();
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        createRunnableTask();
        try {
            if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_2)) {
                this.mRunnableTask.execute(LGConstants.SELFTIMER_DELEY_2SEC);
                LGSelfTimerThread.getInstance().startSelftimer(LGSelfTimerController.SELF_TIMER_2);
            }
            if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_10)) {
                this.mRunnableTask.execute(LGConstants.SELFTIMER_DELEY_10SEC);
                LGSelfTimerThread.getInstance().startSelftimer(LGSelfTimerController.SELF_TIMER_10);
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void LGFocusControl() {
        try {
            if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                Handler handler = new Handler();
                handler.post(new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.3
                    @Override // java.lang.Runnable
                    public void run() {
                        String focus_mode = LGFocusModeController.getInstance().getValue();
                        if (!FocusModeController.MANUAL.equalsIgnoreCase(focus_mode) || !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT)) {
                            Light.setState("LID_SELF_TIMER", true);
                            CompositProcess.this.sleep(50L);
                            Light.setState("LID_SELF_TIMER", false);
                        }
                    }
                });
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
        String focusMode = ((Camera.Parameters) p.first).getFocusMode();
        CameraNotificationManager.OnFocusInfo obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
        if (obj == null) {
            Log.d(TAG, "obj is null. Remote shutter.");
            if (!FocusModeController.MANUAL.equals(focusMode)) {
                ExecutorCreator.getInstance().getSequence().autoFocus(new Camera.AutoFocusCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.4
                    @Override // android.hardware.Camera.AutoFocusCallback
                    public void onAutoFocus(boolean success, Camera camera) {
                        Log.d(CompositProcess.TAG, "AutoFocus : " + success);
                        int sensorType = FocusAreaController.getInstance().getSensorType();
                        if (sensorType != 1 || success) {
                            Log.d(CompositProcess.TAG, "onAutoFocus() setIsStartFocusing(true)");
                            CompositProcess.this.mLGStateHolder.setIsStartFocusing(true);
                            CompositProcess.this.mLGStateHolder.setIsRemoteShutter(false);
                            Log.d(CompositProcess.TAG, "Stop autoFocus");
                            ExecutorCreator.getInstance().getSequence().autoFocus(null);
                            try {
                                if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                                    CompositProcess.this.myStartShutter();
                                } else {
                                    CompositProcess.this.myStartSelfTimerShutter();
                                }
                            } catch (IController.NotSupportedException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                });
                startLGAutoFocusTimer();
                return;
            }
            Log.d(TAG, "onAutoFocus() setIsStartFocusing(true)");
            this.mLGStateHolder.setIsStartFocusing(true);
            this.mLGStateHolder.setIsRemoteShutter(false);
            try {
                if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                    myStartShutter();
                } else {
                    myStartSelfTimerShutter();
                }
                return;
            } catch (IController.NotSupportedException e2) {
                e2.printStackTrace();
                return;
            }
        }
        Log.d(TAG, "obj is NOT null. Imitate Remote shutter by GPMA shutter.");
        if (!FocusModeController.MANUAL.equals(focusMode) && obj.status != 1) {
            ExecutorCreator.getInstance().getSequence().autoFocus(new Camera.AutoFocusCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.5
                @Override // android.hardware.Camera.AutoFocusCallback
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.d(CompositProcess.TAG, "AutoFocus : " + success);
                    int sensorType = FocusAreaController.getInstance().getSensorType();
                    if (sensorType != 1 || success) {
                        Log.d(CompositProcess.TAG, "onAutoFocus() setIsStartFocusing(true)");
                        CompositProcess.this.mLGStateHolder.setIsStartFocusing(true);
                        CompositProcess.this.mLGStateHolder.setIsRemoteShutter(false);
                        Log.d(CompositProcess.TAG, "Stop autoFocus");
                        ExecutorCreator.getInstance().getSequence().autoFocus(null);
                        try {
                            if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                                CompositProcess.this.myStartShutter();
                            } else {
                                CompositProcess.this.myStartSelfTimerShutter();
                            }
                        } catch (IController.NotSupportedException e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            });
            startLGAutoFocusTimer();
            return;
        }
        Log.d(TAG, "onAutoFocus() setIsStartFocusing(true)");
        this.mLGStateHolder.setIsStartFocusing(true);
        this.mLGStateHolder.setIsRemoteShutter(false);
        try {
            if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                myStartShutter();
            } else {
                myStartSelfTimerShutter();
            }
        } catch (IController.NotSupportedException e3) {
            e3.printStackTrace();
        }
    }

    private void startLGAutoFocusTimer() {
        long interval = getLGTimeoutInterval();
        Log.d(TAG, "isStartFocusing = " + this.mLGStateHolder.getIsStartFocusing().toString());
        Log.d(TAG, "Cancel autoFocus.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.6
            @Override // java.lang.Runnable
            public void run() {
                if (!CompositProcess.this.mLGStateHolder.getIsStartFocusing().booleanValue()) {
                    if (ExecutorCreator.getInstance().getSequence() != null) {
                        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                    }
                    if (CompositProcess.this.mCameraEx != null) {
                        CompositProcess.this.mCameraEx.cancelTakePicture();
                    }
                    LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                    LGSelfTimerController.getInstance().unsetOneTimeTimer();
                    if (CompositProcess.sAdapter != null) {
                        CompositProcess.sAdapter.enableNextCapture(1);
                    }
                }
            }
        }, interval);
    }

    private long getLGTimeoutInterval() {
        return 8000L;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        Log.d(TAG, IntervalRecCaptureState.LOG_ON_SHUTTER);
        if (status != 0) {
            this.mCameraEx.cancelTakePicture();
            unLockCameraSettings();
            LGRemoConController.getInstance().resetRemoconModeToDiademValue();
            this.mLGStateHolder.setIsStartFocusing(false);
            Log.d(TAG, "Error occured in onShutter!");
            if (1 == status) {
                Log.d(TAG, "Shutter status is STATUS_CANCELED");
                if (!this.mLGStateHolder.isShootingStage3rd()) {
                    this.mLGStateHolder.prepareShootingStage1st();
                }
            } else if (2 == status) {
                Log.d(TAG, "Shutter status is STATUS_ERROR");
                this.mLGStateHolder.prepareShootingStage1st();
            }
            sAdapter.enableNextCapture(status);
        } else {
            Log.d(TAG, "Request onShutter return was STATUS_OK status:" + status);
            String stage = LGStateHolder.getInstance().getShootingStageNext();
            if (stage.equals(LGStateHolder.EXPOSING_1ST) || stage.equals(LGStateHolder.EXPOSING_2ND)) {
                String value = "";
                try {
                    value = LGAppTopController.getInstance().getValue(LGAppTopController.DURATION_SELECTION);
                } catch (IController.NotSupportedException e) {
                    Log.e(TAG, "NotSupportedException LGAppTopController.getValue()");
                    e.printStackTrace();
                }
                LGTimerThread.ILGTimerCallback cb = LGNormalCaptureLayout.getLocalTimerCallback();
                LGTimerThread.getInstance().timerStart(value, cb);
            } else {
                Log.d(TAG, "3rd shooting, NR or Processing screan transition timer starts.");
                Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
                if (ss == null) {
                    Log.d(TAG, "updateSSValue : ss is null");
                }
                long value2 = 0;
                if (((Integer) ss.second).intValue() != 0) {
                    value2 = (((Integer) ss.first).intValue() * 1050) / ((Integer) ss.second).intValue();
                }
                Log.d(TAG, "ss.first: " + ss.first + "  ss.second: " + ss.second + "  value:" + value2);
                Handler shooting3rdHandler = new Handler();
                shooting3rdHandler.postDelayed(new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.7
                    @Override // java.lang.Runnable
                    public void run() {
                        CompositProcess.this.mLGStateHolder.setShootingStageNext();
                    }
                }, value2);
            }
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
            if (this.mRunnableTask != null) {
                this.mRunnableTask.removeCallbacks();
                this.mRunnableTask = null;
            }
            if (this.mCancelRunnable != null) {
                this.mCancelRunnable.removeCallbacks();
                this.mCancelRunnable = null;
            }
            this.mLGStateHolder.setShootingStageNext();
        }
        isTakePictureStarted = false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d(TAG, "onShutterSequence");
        Log.d(TAG, "onShutter Shooting stage " + this.mLGStateHolder.getShootingStage());
        Log.d(TAG, "onShutter Process stage " + this.mProcessStage);
        if (this.mProcessStage.equals(LGStateHolder.EXPOSING_1ST)) {
            process_1st(raw, sequence);
            sAdapter.enableHalt(true);
        } else if (this.mProcessStage.equals(LGStateHolder.EXPOSING_2ND)) {
            process_2nd(raw, sequence);
            sAdapter.enableHalt(true);
        } else if (this.mProcessStage.equals(LGStateHolder.EXPOSING_3RD)) {
            unLockCameraSettings();
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
            process_3rd(raw, sequence);
            this.mLGStateHolder.setIsStartFocusing(false);
        } else {
            Log.d(TAG, "Error occured in onShutterSequence!");
            Log.d(TAG, "Shutter status is STATUS_CANCELED");
            this.mCameraEx.cancelTakePicture();
            unLockCameraSettings();
            LGRemoConController.getInstance().resetRemoconModeToDiademValue();
            this.mLGStateHolder.setIsStartFocusing(false);
            this.mLGStateHolder.prepareShootingStage1st();
            sAdapter.enableNextCapture(1);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        releaseRawData(raw);
        this.mLGStateHolder.setIsProcessInProcessing(false);
        this.mLGStateHolder.setIsRemoteShutter(false);
    }

    private void process_1st(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d(TAG, this.mLGStateHolder.getShootingStage());
        Log.d(TAG, "1枚目 PF差分統合");
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mLGStateHolder.setYc_1(filter.getOutput());
        filter.release();
        sequence.storeImage(this.mLGStateHolder.getYc_1(), false);
        this.mCameraEx.cancelTakePicture();
        Log.d(TAG, "露出補正を0に");
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((Camera.Parameters) params.first).setExposureCompensation(0);
        CameraSetting.getInstance().setParameters(params);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
        this.mLGStateHolder.setIsProcessInProcessing(false);
        if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.PROCESSING)) {
            this.mLGStateHolder.setShootingStageNext();
        }
    }

    private void process_2nd(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d(TAG, "2枚目 PF差分統合");
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mLGStateHolder.setYc_3(filter.getOutput());
        filter.release();
        this.mLGStateHolder.memStat();
        Log.d(TAG, "Store Image 直前");
        sequence.storeImage(this.mLGStateHolder.getYc_3(), false);
        Log.d(TAG, "疑似Rec実施");
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(this.mLGStateHolder.getYc_3(), false);
        scaleFilter.setDestSize(this.mLGStateHolder.getYc_3().getWidth(), this.mLGStateHolder.getYc_3().getHeight());
        if (scaleFilter.execute()) {
            this.mLGStateHolder.setYc_2(scaleFilter.getOutput());
            Log.d(TAG, "Yc_2 generated by copying Yc_3.");
        }
        scaleFilter.release();
        LGSAMixFilter mixFilter = new LGSAMixFilter();
        mixFilter.setParam(this.mLGStateHolder.getYc_3(), this.mLGStateHolder.getYc_1(), this.mLGStateHolder.getYc_3(), this.mLGStateHolder.getYc_1().getWidth(), this.mLGStateHolder.getYc_1().getHeight(), 0);
        mixFilter.execute();
        mixFilter.releaseResources();
        Log.d(TAG, "SA DIFF finished.");
        LGSAMixFilter mixFilter2 = new LGSAMixFilter();
        mixFilter2.setParam(this.mLGStateHolder.getYc_3(), this.mLGStateHolder.getYc_2(), null, this.mLGStateHolder.getYc_3().getWidth(), this.mLGStateHolder.getYc_3().getHeight(), 1);
        mixFilter2.execute();
        mixFilter2.releaseResources();
        Log.d(TAG, "SA LPF finished.");
        this.mLGStateHolder.getYc_3().release();
        this.mLGStateHolder.setYc_3(null);
        this.mLGStateHolder.memStat();
        OptimizedImage cropImg = LGImageUtil.getInstance().getSFRImage(this.mLGStateHolder.getYc_2(), false);
        this.mLGStateHolder.setDb_mini(LGImageUtil.getInstance().copyOptimizedImageToDeviceBuffer(cropImg));
        cropImg.release();
        Log.d(TAG, "db_2(mini) generated to db_mini.");
        this.mCameraEx.cancelTakePicture();
        Log.d(TAG, "露出補正を0に");
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((Camera.Parameters) params.first).setExposureCompensation(0);
        CameraSetting.getInstance().setParameters(params);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
        this.mLGStateHolder.setIsProcessInProcessing(false);
        if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.PROCESSING)) {
            this.mLGStateHolder.setShootingStageNext();
        }
    }

    private void process_3rd(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d(TAG, "Now shooting stage is " + this.mLGStateHolder.getShootingStage());
        Log.d(TAG, "Start process_3rd.");
        this.mLGStateHolder.getDb_mini().release();
        this.mLGStateHolder.setDb_mini(null);
        if (this.mLGStateHolder.getDb_review() != null) {
            this.mLGStateHolder.getDb_review().release();
            this.mLGStateHolder.setDb_review(null);
        }
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mLGStateHolder.setYc_3(filter.getOutput());
        filter.release();
        LGSAMixFilter mixFilter = new LGSAMixFilter();
        mixFilter.setParamWithDeviceBuffer(this.mLGStateHolder.getDb_2(), this.mLGStateHolder.getYc_3(), this.mLGStateHolder.getYc_3().getWidth(), this.mLGStateHolder.getYc_3().getHeight(), 2);
        mixFilter.execute();
        mixFilter.releaseResources();
        Log.d(TAG, "SA COMP finished.");
        this.mLGStateHolder.memStat();
        sequence.storeImage(this.mLGStateHolder.getYc_3(), false);
        Log.d(TAG, "storeImage in yuv done. ");
        OptimizedImage cropImg = LGImageUtil.getInstance().getSFRImage(this.mLGStateHolder.getYc_3(), false);
        this.mLGStateHolder.setDb_review(LGImageUtil.getInstance().copyOptimizedImageToDeviceBuffer(cropImg));
        cropImg.release();
        Log.d(TAG, "YC_3(mini) generated to db_review.");
        LGImageUtil.getInstance().copyDeviceBufferToOptimizedImage(this.mLGStateHolder.getDb_2(), this.mLGStateHolder.getYc_3());
        this.mLGStateHolder.memStat();
        OptimizedImage cropImg2 = LGImageUtil.getInstance().getSFRImage(this.mLGStateHolder.getYc_3(), true);
        this.mLGStateHolder.setDb_mini(LGImageUtil.getInstance().copyOptimizedImageToDeviceBuffer(cropImg2));
        cropImg2.release();
        Log.d(TAG, "db_2(mini) generated to db_mini.");
        this.mCameraEx.cancelTakePicture();
        this.mOriginalEvComp = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
        sAdapter.enableNextCapture(0);
        this.mLGStateHolder.setIsProcessInProcessing(false);
        if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.PROCESSING)) {
            this.mLGStateHolder.setShootingStageNext();
        }
    }

    private boolean releaseRawData(CameraSequence.RawData raw) {
        if (raw != null) {
            raw.release();
            if (raw.isValid()) {
                Log.e(TAG, "RawData release fault!");
                return false;
            }
            Log.d(TAG, "RawData successfully released.");
            return true;
        }
        Log.d(TAG, "RAW data already released.");
        return true;
    }

    private void releaseYCData() {
        if (this.mLGStateHolder.getYc_1() != null) {
            this.mLGStateHolder.getYc_1().release();
            this.mLGStateHolder.setYc_1(null);
        }
        if (this.mLGStateHolder.getYc_2() != null) {
            this.mLGStateHolder.getYc_2().release();
            this.mLGStateHolder.setYc_2(null);
        }
        if (this.mLGStateHolder.getYc_3() != null) {
            this.mLGStateHolder.getYc_3().release();
            this.mLGStateHolder.setYc_3(null);
        }
    }

    public String getFilePathForSpecialSequence(String app_name, String index) {
        String megaPixel = ScalarProperties.getString("mem.rawimage.size.in.mega.pixel");
        String deviceMemory = ScalarProperties.getString("device.memory");
        String filename = "lib" + deviceMemory + megaPixel + "m_" + app_name + "_" + index + "_";
        Log.d(TAG, "For load MemoryMap fo this camera:" + filename);
        MemoryMapFile map = new MemoryMapFile(LightGraffitiApp.thisApp, app_name, index);
        String filePath = map.getPath();
        Log.i(TAG, "Applied MemoryMap for this camera:" + filePath);
        if (!new File(filePath.replace("/android", "")).exists()) {
            Log.e(TAG, "Memory Map File not exist. Check the file in /libs/armeabli/ folder from your project in Eclipse.  ");
        }
        return filePath;
    }

    private boolean is__EXPOSURE_COUNT_moreThan2_accepted() {
        return this.mPFVersion.doubleValue() < 2.0d;
    }

    private int getCurrentAutoReviewTime() {
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        int reviewTime = this.mAutoPictureReviewControl.getPictureReviewTime();
        if (reviewTime == 0) {
            reviewTime = 2;
        }
        Log.i(TAG, "AutoReviewTime:" + reviewTime);
        return reviewTime;
    }

    private void setAutoReviewControl(int reviewTime) {
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        this.mAutoPictureReviewControl.setPictureReviewTime(reviewTime);
    }

    private void lockCameraSettings() {
        AELController.getInstance().holdAELock(true);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(true);
        CameraSetting.getInstance().setParameters(params);
    }

    private void unLockCameraSettings() {
        if (!this.mProcessStage.equals(LGStateHolder.EXPOSING_3RD)) {
            AELController.getInstance().holdAELock(false);
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(false);
        CameraSetting.getInstance().setParameters(params);
        ((LGNormalExecutor) ExecutorCreator.getInstance().getSequence()).myLockAutoFocus(true);
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNotificationListener() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
    }

    private void createRunnableTask() {
        Log.d(TAG, "createRunnableTask()");
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask();
        }
        if (this.mCancelRunnable == null) {
            this.mCancelRunnable = new CancelRunnable();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RunnableTask {
        private Handler handler = new Handler();
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(CompositProcess.TAG, "RunnableTask.run() starts.");
                if (CompositProcess.this.mLGStateHolder.getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING)) {
                    CompositProcess.this.takePicture();
                }
            }
        };

        protected RunnableTask() {
        }

        public void execute(long time) {
            this.handler.postDelayed(this.r, time);
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
            this.handler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class CancelRunnable {
        Handler handler = new Handler();
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.CancelRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(CompositProcess.TAG, "CancelRunnable");
                if (!CompositProcess.isTakePictureStarted) {
                    if (CompositProcess.this.mRunnableTask != null) {
                        CompositProcess.this.mRunnableTask.removeCallbacks();
                        CompositProcess.this.mRunnableTask = null;
                    }
                    if (CompositProcess.this.mCancelRunnable != null) {
                        CompositProcess.this.mCancelRunnable.removeCallbacks();
                        CompositProcess.this.mCancelRunnable = null;
                    }
                    LGSelfTimerThread.getInstance().stopSelftimer();
                    Light.setState("LID_SELF_TIMER", false);
                    LGStateHolder.getInstance().setShootingStagePrevious();
                    if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_2ND)) {
                        Log.d(CompositProcess.TAG, "Shooting 2nd selftimer cancelation. Do nothing.");
                        return;
                    }
                    LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                    CompositProcess.this.mLGStateHolder.setIsStartFocusing(false);
                    CompositProcess.this.removeNotificationListener();
                    Log.d(CompositProcess.TAG, "Shooting 1st/3rd selftimer cancelation. Enables next capture.");
                    CompositProcess.sAdapter.enableNextCapture(1);
                    return;
                }
                Log.d(CompositProcess.TAG, "CancelRunnable canceled, because takePicuture() started.");
            }
        };

        protected CancelRunnable() {
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

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(LGConstants.IN_CAPTURE_S2_PUSHED)) {
            Log.d(TAG, "IN_CAPTURE_S2_PUSHED received in stage " + this.mLGStateHolder.getShootingStage());
            if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.CONFIRM_REVIEW)) {
                CameraNotificationManager.getInstance().removeNotificationListener(this);
                unLockCameraSettings();
                this.mLGStateHolder.getYc_1().release();
                this.mLGStateHolder.setYc_1(null);
                this.mLGStateHolder.setDb_2(LGImageUtil.getInstance().copyOptimizedImageToDeviceBuffer(this.mLGStateHolder.getYc_2()));
                this.mLGStateHolder.getYc_2().release();
                this.mLGStateHolder.setYc_2(null);
                if (!is__EXPOSURE_COUNT_moreThan2_accepted()) {
                    Log.i(TAG, "Set AutoReview time set back to review time." + this.mPictureReviewTime + "sec.");
                    setAutoReviewControl(this.mPictureReviewTime);
                }
                this.mCameraEx.setRecordingMedia(AvindexStore.getExternalMediaIds()[0], new CameraEx.RecordingMediaChangeCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.8
                    public synchronized void onRecordingMediaChange(CameraEx cameraEx) {
                        Log.d(CompositProcess.TAG, "Change media to real media   done");
                        LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                        Log.d(CompositProcess.TAG, "2枚目撮影後処理と3枚目前準備 PF差分統合");
                        CompositProcess.sAdapter.enableNextCapture(0);
                        LGPreviewEffect.getInstance().startPreviewEffect();
                        LGUtility.getInstance().calculateRemainingMemory();
                    }
                });
                Log.d(TAG, "露出補正を元の値に");
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
                ((Camera.Parameters) params.first).setExposureCompensation(this.mOriginalEvComp);
                CameraSetting.getInstance().setParameters(params);
                this.mLGStateHolder.setIsStartFocusing(false);
                this.mLGStateHolder.setShootingStageNext();
                return;
            }
            if (this.mLGStateHolder.getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING)) {
                this.mCancelRunnable.execute();
                return;
            } else {
                CameraNotificationManager.getInstance().removeNotificationListener(this);
                ((LGNormalExecutor) ExecutorCreator.getInstance().getSequence()).takePicture();
                return;
            }
        }
        if (tag.equals(LGConstants.CONFIRM_REVIEW_CLOSE)) {
            Log.d(TAG, "露出補正を0に");
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params2.first).setExposureCompensation(0);
            CameraSetting.getInstance().setParameters(params2);
            LGRemoConController.getInstance().resetRemoconModeToDiademValue();
            this.mLGStateHolder.prepareShootingStage2nd();
            return;
        }
        if (tag.equals(LGConstants.CONFIRM_REVIEW_FINISH)) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            unLockCameraSettings();
            this.mLGStateHolder.getYc_1().release();
            this.mLGStateHolder.setYc_1(null);
            this.mLGStateHolder.setDb_2(LGImageUtil.getInstance().copyOptimizedImageToDeviceBuffer(this.mLGStateHolder.getYc_2()));
            this.mLGStateHolder.getYc_2().release();
            this.mLGStateHolder.setYc_2(null);
            if (!is__EXPOSURE_COUNT_moreThan2_accepted()) {
                Log.i(TAG, "Set AutoReview time set back to review time." + this.mPictureReviewTime + "sec.");
                setAutoReviewControl(this.mPictureReviewTime);
            }
            this.mCameraEx.setRecordingMedia(AvindexStore.getExternalMediaIds()[0], new CameraEx.RecordingMediaChangeCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.9
                public synchronized void onRecordingMediaChange(CameraEx cameraEx) {
                    Log.d(CompositProcess.TAG, "Change media to real media   done");
                    LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                    Log.d(CompositProcess.TAG, "2枚目撮影後処理と3枚目前準備 PF差分統合");
                    CompositProcess.sAdapter.enableNextCapture(0);
                    LGPreviewEffect.getInstance().startPreviewEffect();
                    LGUtility.getInstance().calculateRemainingMemory();
                }
            });
            Log.d(TAG, "露出補正を元の値に");
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params3 = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params3.first).setExposureCompensation(this.mOriginalEvComp);
            CameraSetting.getInstance().setParameters(params3);
            this.mLGStateHolder.setIsStartFocusing(false);
            this.mLGStateHolder.setShootingStageNext();
            return;
        }
        if (tag.equals(LGConstants.SHOOTING_2ND_CLOSE)) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            unLockCameraSettings();
            this.mLGStateHolder.setIsStartFocusing(false);
            this.mCameraEx.setRecordingMedia(AvindexStore.getExternalMediaIds()[0], new CameraEx.RecordingMediaChangeCallback() { // from class: com.sony.imaging.app.lightgraffiti.shooting.CompositProcess.10
                public synchronized void onRecordingMediaChange(CameraEx cameraEx) {
                    Log.d(CompositProcess.TAG, "Change media to real media   done");
                    LGRemoConController.getInstance().resetRemoconModeToDiademValue();
                    LGStateHolder.getInstance().prepareShootingStage1st();
                    CompositProcess.sAdapter.enableNextCapture(0);
                    String stage = LGStateHolder.getInstance().getShootingStage();
                    LGMenuDataInitializer.initMenuData(stage);
                    LGUtility.getInstance().calculateRemainingMemory();
                }
            });
        }
    }

    public static IAdapter getAdapter() {
        return sAdapter;
    }

    public synchronized void sleep(long msec) {
        try {
            wait(msec);
        } catch (InterruptedException e) {
        }
    }
}
