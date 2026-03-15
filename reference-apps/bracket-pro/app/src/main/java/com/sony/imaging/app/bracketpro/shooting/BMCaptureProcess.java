package com.sony.imaging.app.bracketpro.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.BMAperture;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.HashMap;

/* loaded from: classes.dex */
public class BMCaptureProcess implements ICaptureProcess {
    private static final int AUTOREVIEW_TIME_OFF = 0;
    private static final int DSC_DELAYED_TIME = 50;
    private static final int NUM_FIVE_HUNDERED = 500;
    private static final String TAG = "BMCaptureProcess";
    int counts;
    protected IAdapter mAdapter;
    protected CameraEx mCameraEx;
    private NotificationListener mListener;
    protected NotificationManager mNotifier;
    private HashMap<String, Integer> mSS_IN_MILISEC;
    private int mShutterCount;
    long mTimeAtFlashBracket1stShotExposureDone;
    long mTimeAtFlashBracket1stShotOnShutter;
    int shutterSpeedSecondShootingSteps;
    int shutterSpeedThirdShootingSteps;
    static int count = 0;
    public static int sAutoReviewTime = 0;
    public static boolean sHaltState = false;
    public static boolean isCaptureStarted = false;
    public static Camera sCamera = null;
    private String mCurrentBracketCode = null;
    int stepsForSecondPhoto = 0;
    int stepsForThirdPhoto = 0;
    private Handler myHandler = null;
    private Handler mFocusBracketHandler = null;
    private BracketRunnable mBracketRunnable = null;
    private int DELAY_TIME = 0;
    private FlashRunnable mFlashRunnable = null;
    private FocusRunnable mFocusRunnable = null;
    private CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;
    private int mPictureReviewTime = 0;
    private boolean isFlashChangedByFlashTurnOffAtPretakepicture = false;
    private int SS_at_flash_on = 0;
    private boolean mDidFlashRunnableRun = false;
    int mPictureRangeSteps = 2;
    private String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.SHUTTER_SPEED};

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        this.mCameraEx = cameraEx;
        Log.i(TAG, "sCamera object initialized in memory");
        sCamera = this.mCameraEx.getNormalCamera();
        this.mAdapter = adapter;
        this.mShutterCount = 0;
        if (this.mSS_IN_MILISEC == null) {
            this.mSS_IN_MILISEC = new HashMap<>();
        } else {
            this.mSS_IN_MILISEC.clear();
        }
        fillSSList();
        sHaltState = false;
        this.mNotifier = CameraNotificationManager.getInstance();
        this.mListener = getFlashChangedNotificationListener();
    }

    private void fillSSList() {
        this.mSS_IN_MILISEC.put("30000", 32000);
        this.mSS_IN_MILISEC.put("25000", 25398);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_20000, 20159);
        this.mSS_IN_MILISEC.put("15000", 16000);
        this.mSS_IN_MILISEC.put("13000", 12699);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_10000, 10079);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_8000, 8000);
        this.mSS_IN_MILISEC.put("6000", 6350);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_5000, 5040);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_4000, 4000);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_3200, 3175);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_2500, 2520);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_2000, 2000);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_1600, 1587);
        this.mSS_IN_MILISEC.put("1300", 1260);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_1000, 1000);
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_800, 794);
        this.mSS_IN_MILISEC.put("625", Integer.valueOf(AppRoot.USER_KEYCODE.MODE_S));
        this.mSS_IN_MILISEC.put(ISOSensitivityController.ISO_500, Integer.valueOf(NUM_FIVE_HUNDERED));
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (sCamera != null) {
            Log.i(TAG, "sCamera object released from memory");
            sCamera = null;
        }
        this.mAutoPictureReviewControl = null;
        this.mCameraEx = null;
        this.mAdapter = null;
        if (this.mListener != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
        deInitializeRunnable();
    }

    private void initialize() {
        if (this.myHandler == null) {
            this.myHandler = new Handler();
        }
        if (this.mBracketRunnable == null) {
            this.mBracketRunnable = new BracketRunnable(this.myHandler);
        }
        if (this.mFlashRunnable == null) {
            this.mFlashRunnable = new FlashRunnable(this.myHandler);
        }
    }

    private void setBracketCameraSetting() {
        this.mCurrentBracketCode = BMMenuController.getInstance().getSelectedBracket();
        if (!this.mCurrentBracketCode.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            initialize();
            lockWhiteBalanceSetting(true);
        } else {
            this.mPictureReviewTime = getAutoReviewTime();
            setAutoReviewControl(0);
            lockWhiteBalanceSetting(false);
            if (CameraSetting.getInstance().getFlashExternalEnable() || CameraSetting.getInstance().getFlashInternalEnable()) {
                initialize();
            }
            if (this.mListener != null) {
                this.mNotifier.removeNotificationListener(this.mListener);
                this.mNotifier.setNotificationListener(this.mListener);
            }
            this.isFlashChangedByFlashTurnOffAtPretakepicture = false;
            setFlashMode(false);
        }
        if (BMMenuController.FocusBracket.equals(this.mCurrentBracketCode)) {
            lockExposureSetting(true);
            initializeFocusRunnable();
        } else {
            if (BMMenuController.ApertureBracket.equals(this.mCurrentBracketCode)) {
                initializeShootingSteps();
                if (BracketMasterUtil.isIRISRingEnabledDevice()) {
                    lockExposureSetting(true);
                    BracketMasterUtil.disableIrisRing();
                    return;
                }
                return;
            }
            if (BMMenuController.ShutterSpeedBracket.equals(this.mCurrentBracketCode)) {
                initializeShootingStepsForShutter();
            }
        }
    }

    private void initializeFocusRunnable() {
        if (this.mFocusBracketHandler == null) {
            this.mFocusBracketHandler = new Handler();
        }
        if (this.mFocusRunnable == null) {
            this.mFocusRunnable = new FocusRunnable(this.mFocusBracketHandler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkFlashState() {
        boolean result = false;
        Log.d(TAG, "checkFlashState <<<<");
        Log.d(TAG, "checkFlashState isFlashChangedByFlashTurnOffAtPretakepicture = " + this.isFlashChangedByFlashTurnOffAtPretakepicture);
        String flashValue = FlashController.getInstance().getValue();
        Log.d(TAG, "checkFlashState flashValue from controller= " + flashValue);
        int flashChargeState = ((Integer) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.FLASH_CHARGE_STATUS)).intValue();
        Log.d(TAG, "checkFlashState flashChargeState (0= CHARGE_DONE) = " + flashChargeState);
        int current_ss = getShutterSpeed();
        Log.d(TAG, "checkFlashState current_ss(flashSS=" + this.SS_at_flash_on + ") : " + current_ss);
        if (this.isFlashChangedByFlashTurnOffAtPretakepicture && "on".equals(flashValue) && flashChargeState == 0 && current_ss == this.SS_at_flash_on) {
            result = true;
            this.mNotifier.removeNotificationListener(this.mListener);
        }
        Log.d(TAG, "checkFlashState >>>>>  return result  = " + result);
        return result;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        isCaptureStarted = false;
        this.mShutterCount = 0;
        if (this.mAdapter != null) {
            this.mAdapter.enableHalt(false);
        }
        setBracketCameraSetting();
    }

    private void initializeShootingSteps() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BMAperture aperture = new BMAperture();
        this.stepsForSecondPhoto = aperture.getSecondShift() + 1;
        this.stepsForThirdPhoto = aperture.getThirdShootingPosition() + 1 + this.stepsForSecondPhoto;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void initializeShootingStepsForShutter() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int mshiftssT = (BracketMasterController.getInstance().getFirstPos() - BracketMasterController.getInstance().getCenterPos()) - 1;
        int mshiftssS = (BracketMasterController.getInstance().getCenterPos() - BracketMasterController.getInstance().getSecondPos()) - 1;
        Log.i(TAG, "handleShutterCallbackShutter()  mshiftssT:" + mshiftssT + "  mshiftssS:" + mshiftssS);
        this.shutterSpeedThirdShootingSteps = mshiftssT + 1;
        this.shutterSpeedSecondShootingSteps = mshiftssS + 1 + this.shutterSpeedThirdShootingSteps;
        Log.d(TAG, "shutterSpeedThirdShootingSteps : " + this.shutterSpeedThirdShootingSteps + " shutterSpeedSecondShootingSteps:" + this.shutterSpeedSecondShootingSteps);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        Log.d(TAG, "onShutter <<<   status:" + status);
        if (status == 0) {
            this.mAdapter.lockCancelTakePicture(true);
            this.DELAY_TIME = getDelayTime();
            if (BMMenuController.FocusBracket.equals(this.mCurrentBracketCode)) {
                if (this.mFocusRunnable != null) {
                    initializeFocusRunnable();
                }
                this.mFocusRunnable.execute(status);
            } else if (BMMenuController.ApertureBracket.equals(this.mCurrentBracketCode)) {
                handleShutterCallbackAperture(status);
            } else if (BMMenuController.ShutterSpeedBracket.equals(this.mCurrentBracketCode)) {
                handleShutterCallbackShutter(status);
            } else if (BMMenuController.FlashBracket.equals(this.mCurrentBracketCode)) {
                Log.d(TAG, "onShutter hit cancelTakePicture");
                this.mCameraEx.cancelTakePicture();
                handleShutterCallbackFlash(status);
            } else {
                enableNextCapture(status);
            }
        } else {
            if (BMMenuController.FocusBracket.equals(this.mCurrentBracketCode)) {
                lockWhiteBalanceSetting(false);
                lockExposureSetting(false);
            } else if (BMMenuController.ApertureBracket.equals(this.mCurrentBracketCode)) {
                adjustAperturePic();
                lockWhiteBalanceSetting(false);
                BracketMasterUtil.enableIrisRing();
            } else if (BMMenuController.ShutterSpeedBracket.equals(this.mCurrentBracketCode)) {
                adjustShutterPic();
                lockWhiteBalanceSetting(false);
            }
            if (BMMenuController.FlashBracket.equals(this.mCurrentBracketCode)) {
                setAutoReviewControl(this.mPictureReviewTime);
                setFlashMode(true);
            }
            if (this.mAdapter != null) {
                this.mAdapter.enableHalt(true);
            }
            enableNextCapture(status);
        }
        Log.d(TAG, "onShutter >>>");
    }

    public void enableNextCapture(int status) {
        Log.d(TAG, "enableNextCapture <<<<");
        Log.d(TAG, "before hit cancelTakePicture mCameraEx" + this.mCameraEx);
        if (this.mCameraEx != null) {
            Log.d(TAG, "hit cancelTakePicture");
            this.mCameraEx.cancelTakePicture();
        }
        if (this.mAdapter != null) {
            this.mAdapter.lockCancelTakePicture(false);
            this.mAdapter.enableNextCapture(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        this.mShutterCount = 0;
        if (BMMenuController.FlashBracket.equals(this.mCurrentBracketCode)) {
            isCaptureStarted = true;
            if (FlashController.getInstance().getValue().equals("on")) {
                this.SS_at_flash_on = getShutterSpeed();
                setFlashMode(false);
            }
        }
        if (this.mCameraEx != null) {
            this.mAdapter.enableHalt(false);
        }
        this.mCameraEx.cancelTakePicture();
        Log.d(TAG, "takePicture()->burstableTakePicture");
        this.mCameraEx.burstableTakePicture();
    }

    private void lockWhiteBalanceSetting(boolean shouldLock) {
        if (((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).isAutoWhiteBalanceLockSupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = CameraSetting.getInstance().getEmptyParameters();
            if (shouldLock && supported != null && "auto".equals(WhiteBalanceController.getInstance().getValue())) {
                ((CameraEx.ParametersModifier) supported.second).setAutoWhiteBalanceLock(true);
                CameraSetting.getInstance().setParameters(supported);
            } else if (!shouldLock && supported != null && "auto".equals(WhiteBalanceController.getInstance().getValue())) {
                ((CameraEx.ParametersModifier) supported.second).setAutoWhiteBalanceLock(false);
                CameraSetting.getInstance().setParameters(supported);
            }
        }
    }

    private void lockExposureSetting(boolean shouldLock) {
        AELController.getInstance().holdAELock(shouldLock);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlashMode(boolean shouldOn) {
        Log.d(TAG, "setFlashMode: " + shouldOn);
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        Camera mCamera = mCameraEx.getNormalCamera();
        if (shouldOn) {
            Camera.Parameters params = mCameraEx.createEmptyParameters();
            params.setFlashMode("on");
            mCamera.setParameters(params);
        } else {
            Camera.Parameters params2 = mCameraEx.createEmptyParameters();
            params2.setFlashMode("on");
            mCamera.setParameters(params2);
            Camera.Parameters params3 = mCameraEx.createEmptyParameters();
            params3.setFlashMode("off");
            mCamera.setParameters(params3);
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> param = CameraSetting.getInstance().getParameters();
        String flashMode = ((Camera.Parameters) param.first).getFlashMode();
        Log.d(TAG, "setFlashMode after setting value from camera setting " + flashMode);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        this.mCameraEx.startSelfTimerShutter();
    }

    private void handleShutterCallbackFlash(int status) {
        Log.d(TAG, "handleShutterCallbackFlash <<< ");
        this.mShutterCount++;
        switch (this.mShutterCount) {
            case 1:
                Log.d(TAG, "handleShutterCallbackFlash object mFlashRunnable = " + this.mFlashRunnable);
                if (this.mFlashRunnable != null) {
                    this.mTimeAtFlashBracket1stShotOnShutter = System.currentTimeMillis();
                    this.mDidFlashRunnableRun = false;
                    this.mFlashRunnable.execute();
                    break;
                } else {
                    this.mCameraEx.cancelTakePicture();
                    this.mCameraEx.burstableTakePicture();
                    break;
                }
            default:
                Log.d(TAG, "handleShutterCallbackFlash default ");
                isCaptureStarted = false;
                setAutoReviewControl(this.mPictureReviewTime);
                enableNextCapture(status);
                deInitializeRunnable();
                if (this.mAdapter != null) {
                    this.mAdapter.enableHalt(true);
                }
                this.mShutterCount = 0;
                break;
        }
        Log.d(TAG, "handleShutterCallbackFlash >>> ");
    }

    private void handleShutterCallbackShutter(int status) {
        Log.d(TAG, "handleShutterCallbackShutter <<<<");
        this.mShutterCount++;
        this.counts = 0;
        initializeAutoReview();
        switch (this.mShutterCount) {
            case 1:
                this.counts = 1;
                this.mCameraEx.adjustShutterSpeed(this.shutterSpeedThirdShootingSteps);
                captureOnAutoreview();
                break;
            case 2:
                this.counts = 2;
                this.mCameraEx.adjustShutterSpeed(-this.shutterSpeedSecondShootingSteps);
                captureOnAutoreview();
                break;
            case 3:
                int revertSteps = this.shutterSpeedSecondShootingSteps - this.shutterSpeedThirdShootingSteps;
                this.mCameraEx.adjustShutterSpeed(revertSteps);
                lockWhiteBalanceSetting(false);
                enableNextCapture(status);
                deInitializeRunnable();
                if (this.mAdapter != null) {
                    this.mAdapter.enableHalt(true);
                }
                this.mShutterCount = 0;
                break;
        }
        Log.d(TAG, "handleShutterCallbackShutter >>>>");
    }

    private void initializeAutoReview() {
        if (sAutoReviewTime == 0 && this.mBracketRunnable != null) {
            this.mBracketRunnable.removeCallbacks();
        }
    }

    private void captureOnAutoreview() {
        if (this.mBracketRunnable != null) {
            this.mBracketRunnable.execute();
        } else {
            this.mCameraEx.cancelTakePicture();
            this.mCameraEx.burstableTakePicture();
        }
    }

    private void deInitializeRunnable() {
        Log.d(TAG, "deInitializeRunnable <<<");
        if (this.mBracketRunnable != null) {
            this.mBracketRunnable.removeCallbacks();
            this.mBracketRunnable = null;
        }
        if (this.mFlashRunnable != null) {
            this.mFlashRunnable.removeCallbacks();
            this.mFlashRunnable = null;
        }
        this.myHandler = null;
        if (this.mFocusRunnable != null) {
            this.mFocusRunnable.removeCallbacks();
            this.mFocusRunnable = null;
        }
        if (this.mFocusBracketHandler != null) {
            this.mFocusBracketHandler = null;
        }
    }

    private void handleShutterCallbackAperture(int status) {
        this.mShutterCount++;
        count = 0;
        initializeAutoReview();
        switch (this.mShutterCount) {
            case 1:
                count = 1;
                this.mCameraEx.adjustAperture(-this.stepsForSecondPhoto);
                AppLog.info(TAG, "stepsForSecondPhoto:" + this.stepsForSecondPhoto);
                captureOnAutoreview();
                return;
            case 2:
                count = 2;
                this.mCameraEx.adjustAperture(this.stepsForThirdPhoto);
                AppLog.info(TAG, "stepsForThirdPhoto:" + this.stepsForThirdPhoto);
                captureOnAutoreview();
                return;
            case 3:
                AppLog.info(TAG, "stepsForRevert :" + (-(this.stepsForThirdPhoto - this.stepsForSecondPhoto)));
                this.mCameraEx.adjustAperture(-(this.stepsForThirdPhoto - this.stepsForSecondPhoto));
                lockWhiteBalanceSetting(false);
                if (BracketMasterUtil.isIRISRingEnabledDevice()) {
                    lockExposureSetting(false);
                    BracketMasterUtil.enableIrisRing();
                }
                enableNextCapture(status);
                deInitializeRunnable();
                if (this.mAdapter != null) {
                    this.mAdapter.enableHalt(true);
                }
                this.mShutterCount = 0;
                return;
            default:
                return;
        }
    }

    public void adjustShutterPic() {
        switch (this.counts) {
            case 1:
                this.mCameraEx.adjustShutterSpeed(-this.shutterSpeedThirdShootingSteps);
                break;
            case 2:
                this.mCameraEx.adjustShutterSpeed(this.shutterSpeedSecondShootingSteps - this.shutterSpeedThirdShootingSteps);
                break;
        }
        this.counts = 0;
    }

    public void adjustAperturePic() {
        switch (count) {
            case 1:
                this.mCameraEx.adjustAperture(this.stepsForSecondPhoto);
                break;
            case 2:
                this.mCameraEx.adjustAperture(-(this.stepsForThirdPhoto - this.stepsForSecondPhoto));
                break;
        }
        count = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShutterCallbackFocus(int status) {
        this.mShutterCount++;
        int firstPictureRangeSteps = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_FOCUS_RANGE, 2);
        BMEEState.isBMCautionStateBooted = true;
        if (firstPictureRangeSteps == 2) {
            this.mPictureRangeSteps = 4;
        } else if (firstPictureRangeSteps == 1) {
            this.mPictureRangeSteps = 2;
        } else if (firstPictureRangeSteps == 0) {
            this.mPictureRangeSteps = 1;
        }
        int secondPictureRangeSteps = this.mPictureRangeSteps + this.mPictureRangeSteps;
        switch (this.mShutterCount) {
            case 1:
                this.mCameraEx.shiftFocusPosition(this.mPictureRangeSteps);
                this.mCameraEx.cancelTakePicture();
                this.mCameraEx.burstableTakePicture();
                return;
            case 2:
                this.mCameraEx.shiftFocusPosition(-secondPictureRangeSteps);
                this.mCameraEx.cancelTakePicture();
                this.mCameraEx.burstableTakePicture();
                return;
            case 3:
                lockWhiteBalanceSetting(false);
                lockExposureSetting(false);
                enableNextCapture(status);
                this.mShutterCount = 0;
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BracketRunnable {
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess.BracketRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(BMCaptureProcess.TAG, "BrcketRunnable run <<<");
                BMCaptureProcess.this.mCameraEx.cancelTakePicture();
                if (BMCaptureProcess.this.mCurrentBracketCode != null && BMCaptureProcess.this.mCurrentBracketCode.equalsIgnoreCase(BMMenuController.ApertureBracket) && BracketMasterUtil.isIRISRingEnabledDevice() && !BMCaptureProcess.sHaltState) {
                    try {
                        BMCaptureProcess.sCamera.cancelAutoFocus();
                    } catch (Exception e) {
                    }
                }
                BMCaptureProcess.this.mCameraEx.burstableTakePicture();
            }
        };

        public BracketRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute() {
            if (this.handler != null) {
                this.handler.postDelayed(this.r, BMCaptureProcess.this.DELAY_TIME);
            }
        }

        public void removeCallbacks() {
            if (this.r != null && this.handler != null) {
                this.handler.removeCallbacks(this.r);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDelayTime() {
        int wait_time;
        int shutterSpeed = getShutterSpeed();
        if (shutterSpeed < 1000 || !isLongExposureNrOn(true)) {
            wait_time = shutterSpeed + IntervalRecExecutor.INTVL_REC_INITIALIZED + 160 + 80;
        } else {
            wait_time = ((shutterSpeed + IntervalRecExecutor.INTVL_REC_INITIALIZED) * 2) + 160 + 80;
        }
        String deviceModel = ScalarProperties.getString("device.memory");
        if (deviceModel.equalsIgnoreCase(BracketMasterSubMenu.DEVICE_MODEL_ZNS)) {
            wait_time = 1200;
        }
        Log.i(TAG, "SS:" + shutterSpeed + ",   wait time for SS:" + wait_time);
        return wait_time;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getShutterSpeed() {
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss != null) {
            int shutterSpeed = (((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue();
            return shutterSpeed;
        }
        return 0;
    }

    private boolean isLongExposureNrOn(boolean returnValueAtLongExposureNrNotSupported) {
        boolean ret_value;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        if (isPFverOver2() && true == ((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR()) {
            ret_value = ((CameraEx.ParametersModifier) params.second).getLongExposureNR();
        } else {
            ret_value = returnValueAtLongExposureNrNotSupported;
        }
        Log.i(TAG, "isLongExposureNrOn: " + ret_value);
        return ret_value;
    }

    public boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getShutterDelay() {
        int shutterSpeed;
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss != null) {
            shutterSpeed = (((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue();
        } else {
            shutterSpeed = 0;
        }
        if (shutterSpeed < NUM_FIVE_HUNDERED) {
            int postDelayedTime = shutterSpeed + 340;
            return postDelayedTime;
        }
        int postDelayedTime2 = this.mSS_IN_MILISEC.get(String.valueOf(shutterSpeed)).intValue();
        return postDelayedTime2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FlashRunnable {
        protected static final int FLASH_STATUS_WAIT_LOOP_DURATION = 100;
        protected static final int TIMEOUT_LENGTH = 14000;
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess.FlashRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                Log.i(BMCaptureProcess.TAG, "FlashRunnable.Runnable.run() <<<<");
                if (!BMCaptureProcess.this.mDidFlashRunnableRun) {
                    BMCaptureProcess.this.mDidFlashRunnableRun = true;
                    BMCaptureProcess.this.DELAY_TIME = BMCaptureProcess.this.getDelayTime();
                    Log.i(BMCaptureProcess.TAG, "Set actual DELAY_TIME for SS: " + BMCaptureProcess.this.DELAY_TIME);
                    BMCaptureProcess.this.setFlashMode(true);
                    BMCaptureProcess.this.mTimeAtFlashBracket1stShotExposureDone = BMCaptureProcess.this.mTimeAtFlashBracket1stShotOnShutter + BMCaptureProcess.this.DELAY_TIME;
                }
                if (BMCaptureProcess.this.checkFlashState()) {
                    BMCaptureProcess.this.mCameraEx.cancelTakePicture();
                    BMCaptureProcess.this.mCameraEx.burstableTakePicture();
                } else {
                    if (FlashRunnable.TIMEOUT_LENGTH > ((int) (System.currentTimeMillis() - BMCaptureProcess.this.mTimeAtFlashBracket1stShotExposureDone))) {
                        Log.i(BMCaptureProcess.TAG, "Waiting until14000.  Current: " + ((int) (System.currentTimeMillis() - BMCaptureProcess.this.mTimeAtFlashBracket1stShotExposureDone)));
                        FlashRunnable.this.execute(100);
                        return;
                    }
                    Log.i(BMCaptureProcess.TAG, "Timeout . Go to capture");
                    BMCaptureProcess.this.mCameraEx.cancelTakePicture();
                    BMCaptureProcess.this.mCameraEx.burstableTakePicture();
                    if (BMCaptureProcess.this.mListener != null) {
                        BMCaptureProcess.this.mNotifier.removeNotificationListener(BMCaptureProcess.this.mListener);
                    }
                }
            }
        };

        public FlashRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute() {
            execute(BMCaptureProcess.this.DELAY_TIME);
        }

        public void execute(int wait) {
            Log.i(BMCaptureProcess.TAG, "calling of execute()   from FlashRunnable.  wait:" + wait);
            if (this.handler != null) {
                this.handler.postDelayed(this.r, wait);
            }
        }

        public void removeCallbacks() {
            if (this.r != null && this.handler != null) {
                this.handler.removeCallbacks(this.r);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FocusRunnable {
        Handler handler;
        int status = 0;
        Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess.FocusRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                BMCaptureProcess.this.handleShutterCallbackFocus(FocusRunnable.this.status);
            }
        };

        public FocusRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute(int shutterStatus) {
            this.status = shutterStatus;
            if (this.handler != null) {
                long delayedTime = 0;
                String deviceCategory = ScalarProperties.getString("model.category");
                try {
                    if (2 == Integer.parseInt(deviceCategory)) {
                        delayedTime = BMCaptureProcess.this.getShutterDelay() + 50;
                    }
                } catch (NumberFormatException e) {
                    Log.i(BMCaptureProcess.TAG, "number format exception for fetchin info of device category");
                }
                this.handler.postDelayed(this.runnable, delayedTime);
            }
        }

        public void removeCallbacks() {
            if (this.runnable != null && this.handler != null) {
                this.handler.removeCallbacks(this.runnable);
            }
        }
    }

    private int getAutoReviewTime() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        int reviewTime = this.mAutoPictureReviewControl.getPictureReviewTime();
        Log.d(TAG, "get reviewTime:" + reviewTime);
        AppLog.exit(TAG, AppLog.getMethodName());
        return reviewTime;
    }

    private void setAutoReviewControl(int reviewTime) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.d(TAG, "set reviewTime:" + reviewTime);
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        this.mAutoPictureReviewControl.setPictureReviewTime(reviewTime);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected NotificationListener getFlashChangedNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                Log.d(BMCaptureProcess.TAG, "Notify: tag =  " + tag);
                if (CameraNotificationManager.FLASH_CHANGE.equals(tag)) {
                    String flashValue = FlashController.getInstance().getValue();
                    Log.i(BMCaptureProcess.TAG, "Notify: CameraNotificationManager.FLASH_CHANGE  " + flashValue);
                    if (!"off".equals(flashValue)) {
                        Log.w(BMCaptureProcess.TAG, "Flash should is expected to be OFF, but not OFF");
                    }
                    BMCaptureProcess.this.isFlashChangedByFlashTurnOffAtPretakepicture = true;
                }
                if (CameraNotificationManager.SHUTTER_SPEED.equals(tag)) {
                    Log.i(BMCaptureProcess.TAG, "Notify: CameraNotificationManager.SHUTTER_SPEED  " + BMCaptureProcess.this.getShutterSpeed());
                }
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return BMCaptureProcess.this.TAGS;
            }
        };
    }
}
