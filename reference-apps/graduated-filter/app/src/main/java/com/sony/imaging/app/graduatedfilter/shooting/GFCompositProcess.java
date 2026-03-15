package com.sony.imaging.app.graduatedfilter.shooting;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.graduatedfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.graduatedfilter.common.GFRawAPIHandling;
import com.sony.imaging.app.graduatedfilter.common.GFRawDataInfo;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.sa.NDSA;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeIso;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeSs;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeWhiteBalance;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.ScalarCalendar;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFCompositProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    private static IAdapter mAdapter;
    private static CameraEx mCameraEx;
    private static final String TAG = AppLog.getClassName();
    private static Camera mCamera = null;
    private static boolean isRemoteControlMode = false;
    private static boolean isFirstShooting = true;
    private static boolean isSelfTimer = false;
    private static boolean cancelTakePicture = false;
    private static CameraSequence mSequence = null;
    private static WatchDogTimerForMaybePhaseDiffRunnableTask mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
    private static SelfTimerRunnableTask mSelfTimerRunnableTask = null;
    private static boolean isRaw = false;
    private static boolean isRawJpeg = false;
    private static DeviceBuffer m1stRaw = null;
    private static DeviceBuffer m2ndRaw = null;
    private static CameraSequence.RawData mCompositRaw = null;
    private static GFRawDataInfo m1stRawDataInfo = null;
    private static GFRawDataInfo m2ndRawDataInfo = null;
    private static CameraSequence.YcDataInfo mYcInfo = null;
    private static DSP mDSP = null;
    private static GFEffectParameters.Parameters mParams = null;
    private static boolean Ss_changing = false;
    private static boolean Iso_changing = false;
    private static boolean Aperture_changing = false;
    private static boolean Wb_changing = false;
    private static boolean Media_changing = false;
    private static CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;
    private static int mPictureReviewTime = 0;
    private static boolean needSsSetting = false;
    private static boolean needApertureSetting = false;
    private static boolean needMediaSetting = false;
    private static boolean needAELockSetting = false;
    private static boolean isPFIssue = false;
    private static boolean isFirstStoreImageExecuted = false;
    private static boolean isLastStoreImageExecuted = false;
    private static boolean isCameraReady = false;
    private static boolean needAutoWhiteBalanceLock = false;
    private static boolean mToggledFocusMode = false;
    private static String mFocusMode = null;
    public static boolean debugOutputFile = false;
    public static String debugFileName = "/mnt/sdcard/skyhdr.txt";
    private static boolean waitCameraSettingChange__isTimerShooting = false;
    private static long usableSpaceBeforeShooting = 0;
    private static long usableSpace2ndShooting = 0;
    private static FileWriter mFw = null;
    MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();
    private Object waitApertureSettingChange__lockObject = new Object();
    private Object waitCameraSettingChange__lockObject = new Object();
    private String[] TAGS = {GFConstants.CANCELTAKEPICTURE};

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mCameraEx = cameraEx;
        mCamera = cameraEx.getNormalCamera();
        mAdapter = adapter;
        GFKikiLogUtil.getInstance().setCaptureIds();
        GFImageAdjustmentUtil.getInstance();
        GFCommonUtil.getInstance().setIrisRingSettingByCamera(false);
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        try {
            String pictureQuality = PictureQualityController.getInstance().getValue(null);
            isRaw = PictureQualityController.PICTURE_QUALITY_RAW.equalsIgnoreCase(pictureQuality);
            isRawJpeg = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equalsIgnoreCase(pictureQuality);
            AppLog.info(TAG, "isRaw:" + isRaw);
            AppLog.info(TAG, "isRawJpeg:" + isRawJpeg);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        setAutoWhiteBalanceLock(false);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        isRemoteControlMode = ((CameraEx.ParametersModifier) params.second).getRemoteControlMode();
        mPictureReviewTime = getAutoReviewControl();
        isFirstShooting = true;
        isSelfTimer = GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"));
        GFCommonUtil.getInstance().setPushedIR2SecKey(false);
        needMediaSetting = GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        if (SaUtil.isAVIP()) {
            GFRawAPIHandling.setPhicalAddress();
        }
        CameraNotificationManager.getInstance().requestNotify(GFConstants.RESTART_COMPOSIT_PROCESS);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            boolean z = false;
            boolean unused = GFCompositProcess.Media_changing = false;
            GFCompositProcess gFCompositProcess = GFCompositProcess.this;
            if (GFCompositProcess.isFirstShooting && GFCompositProcess.isSelfTimer) {
                z = true;
            }
            gFCompositProcess.waitCameraSettingChange(z);
            GFCompositProcess.this.burstableTakePicture_cameraSettingChangeDone();
        }
    }

    private static void setAutoWhiteBalanceLock(boolean isLock) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
        if (params != null && ((CameraEx.ParametersModifier) params.second).isAutoWhiteBalanceLockSupported() && (emptyParams = CameraSetting.getInstance().getEmptyParameters()) != null) {
            ((CameraEx.ParametersModifier) emptyParams.second).setAutoWhiteBalanceLock(isLock);
            CameraSetting.getInstance().setParameters(emptyParams);
        }
    }

    private void set1stOptions() {
        setMemoryMapConfiguration();
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence("GraduatedFilter", "00"));
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", false);
        opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
        if (!SaUtil.isAVIP()) {
            opt.setOption("DETECTION_OFF", false);
        }
        mAdapter.setOptions(opt);
    }

    private void set2ndOptions() {
        if (!needMediaSetting) {
            CameraSequence.Options opt = new CameraSequence.Options();
            opt.setOption("RECORD_COUNT", 2);
            mAdapter.setOptions(opt);
        }
    }

    private void set1stCameraSetting(boolean isTimer) {
        Media_changing = false;
        if (GFShootingOrderController.FILTER_BASE.equalsIgnoreCase(GFShootingOrderController.getInstance().getValue())) {
            setCameraSetting(false);
        } else {
            setCameraSetting(true);
        }
        needAutoWhiteBalanceLock = !GFWhiteBalanceController.getInstance().isSupportedABGM() && "auto".equalsIgnoreCase(mParams.getWBMode(true)) && ("auto".equalsIgnoreCase(mParams.getWBMode(false)) || GFWhiteBalanceController.SAME.equalsIgnoreCase(mParams.getWBMode(false)));
        if (needAutoWhiteBalanceLock) {
            setAutoWhiteBalanceLock(true);
        }
        waitCameraSettingChange(isFirstShooting && isSelfTimer);
        burstableTakePicture_cameraSettingChangeDone();
    }

    private void set2ndCameraSetting() {
        Media_changing = needMediaSetting;
        if (needMediaSetting) {
            GFCommonUtil.getInstance().setActualMediaIds(mCameraEx, this.sMyRecordingMediaChangeCallback);
            GFCommonUtil.getInstance().setActualMediaStatus(true);
        }
        if (GFShootingOrderController.FILTER_BASE.equalsIgnoreCase(GFShootingOrderController.getInstance().getValue())) {
            setCameraSetting(true);
        } else {
            setCameraSetting(false);
        }
        if (!needMediaSetting) {
            waitCameraSettingChange(false);
            burstableTakePicture_cameraSettingChangeDone();
        }
    }

    private void setCameraSetting(boolean isBase) {
        Wb_changing = true;
        Ss_changing = needSsSetting;
        Aperture_changing = needApertureSetting;
        Iso_changing = true;
        GFCommonUtil.getInstance().setBaseCameraSettingsFlag(isBase);
        CameraNotificationManager.getInstance().requestNotify(GFEffectParameters.TAG_CHANGE);
        mParams = GFEffectParameters.getInstance().getParameters();
        float maxShutterSpeed = CameraSetting.getInstance().getShutterSpeedInfo().currentAvailableMax_n / CameraSetting.getInstance().getShutterSpeedInfo().currentAvailableMax_d;
        float targetShutterSpeed = mParams.getSSNumerator(isBase) / mParams.getSSDenominator(isBase);
        final boolean needWaitApertureSetting = needApertureSetting && needSsSetting && GFCommonUtil.getInstance().isDSC() && targetShutterSpeed < maxShutterSpeed;
        if (GFCommonUtil.getInstance().hasIrisRing()) {
            FocusModeController.getInstance().setValue(FocusModeController.MANUAL);
            mCamera.cancelAutoFocus();
        }
        new ChangeWhiteBalance().execute(mParams.getWBMode(isBase), mParams.getWBOption(isBase), new ChangeWhiteBalance.Callback() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.1
            @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeWhiteBalance.Callback
            public void cbFunction() {
                Log.d(GFCompositProcess.TAG, "WB change done. : " + GFWhiteBalanceController.getInstance().getValue());
                boolean unused = GFCompositProcess.Wb_changing = false;
                GFCompositProcess.this.burstableTakePicture_cameraSettingChangeDone();
            }
        });
        if (needApertureSetting) {
            int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), mParams.getAperture(isBase));
            new ChangeAperture().execute(step, mParams.getAperture(isBase), new ChangeAperture.Callback() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.2
                @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture.Callback
                public void cbFunction() {
                    boolean unused = GFCompositProcess.Aperture_changing = false;
                    GFCompositProcess.this.burstableTakePicture_cameraSettingChangeDone();
                    if (needWaitApertureSetting) {
                        GFCompositProcess.this.apertureSettingChangeDone();
                    }
                }
            });
        }
        if (needWaitApertureSetting) {
            waitApertureSettingChange();
        }
        if (needSsSetting) {
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            if (ss != null) {
                int SS_adjustment = ChangeSs.getSsAdjustmentStep(mParams.getSSNumerator(!isBase), mParams.getSSDenominator(isBase ? false : true), mParams.getSSNumerator(isBase), mParams.getSSDenominator(isBase));
                new ChangeSs().execute(SS_adjustment, mParams.getSSNumerator(isBase), mParams.getSSDenominator(isBase), new ChangeSs.Callback() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.3
                    @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeSs.Callback
                    public void cbFunction() {
                        Log.d(GFCompositProcess.TAG, "SS change done.  " + CameraSetting.getInstance().getShutterSpeed().first + "/" + CameraSetting.getInstance().getShutterSpeed().second);
                        boolean unused = GFCompositProcess.Ss_changing = false;
                        GFCompositProcess.this.burstableTakePicture_cameraSettingChangeDone();
                    }
                });
            } else {
                Ss_changing = false;
            }
        }
        new ChangeIso().execute(mParams.getISO(isBase), new ChangeIso.Callback() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.4
            @Override // com.sony.imaging.app.graduatedfilter.shooting.ChangeIso.Callback
            public void cbFunction() {
                Log.d(GFCompositProcess.TAG, "ISO change done. : " + ISOSensitivityController.getInstance().getValue());
                boolean unused = GFCompositProcess.Iso_changing = false;
                GFCompositProcess.this.burstableTakePicture_cameraSettingChangeDone();
            }
        });
        ExposureCompensationController.getInstance().setValue(null, mParams.getExposureComp(isBase));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void apertureSettingChangeDone() {
        synchronized (this.waitApertureSettingChange__lockObject) {
            this.waitApertureSettingChange__lockObject.notifyAll();
        }
    }

    private void waitApertureSettingChange() {
        new Thread(new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.5
            @Override // java.lang.Runnable
            public void run() {
                synchronized (GFCompositProcess.this.waitCameraSettingChange__lockObject) {
                    if (GFCompositProcess.Aperture_changing) {
                        Log.d(GFCompositProcess.TAG, "Start to wait Aperture changing.");
                        try {
                            GFCompositProcess.this.waitApertureSettingChange__lockObject.wait(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(GFCompositProcess.TAG, "Stop to wait Aperture changing.");
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void burstableTakePicture_cameraSettingChangeDone() {
        if (!Wb_changing && !Iso_changing && !Ss_changing && !Aperture_changing && !Media_changing) {
            Log.d(TAG, "All change done");
            synchronized (this.waitCameraSettingChange__lockObject) {
                Log.d(TAG, "Lock release request(notifyAll)");
                this.waitCameraSettingChange__lockObject.notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitCameraSettingChange(boolean isTimerShooting) {
        waitCameraSettingChange__isTimerShooting = isTimerShooting;
        new Thread(new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.6
            @Override // java.lang.Runnable
            public void run() {
                synchronized (GFCompositProcess.this.waitCameraSettingChange__lockObject) {
                    try {
                        if (GFCompositProcess.waitCameraSettingChange__isTimerShooting) {
                            GFCompositProcess.mSelfTimerRunnableTask.execute();
                        } else {
                            if (GFCompositProcess.Wb_changing || GFCompositProcess.Iso_changing || GFCompositProcess.Ss_changing || GFCompositProcess.Aperture_changing || GFCompositProcess.Media_changing) {
                                Log.d(GFCompositProcess.TAG, "Lock started");
                                GFCompositProcess.this.waitCameraSettingChange__lockObject.wait(3000L);
                                Log.d(GFCompositProcess.TAG, "Lock released");
                            }
                            GFCompositProcess.mAdapter.enableHalt(false);
                            if (!AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
                                if (GFCompositProcess.needAELockSetting && GFCompositProcess.isFirstShooting) {
                                    AELController.getInstance().holdAELock(true);
                                }
                                GFCompositProcess.mCameraEx.burstableTakePicture();
                            } else {
                                CautionUtilityClass.getInstance().requestTrigger(GFCommonUtil.getInstance().getCautionId());
                                GFCompositProcess.releaseAllRawData();
                                GFCompositProcess.enableNextCapture(0);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static String getFilePathForSpecialSequence(String appName, String index) {
        String megaPixel = ScalarProperties.getString("mem.rawimage.size.in.mega.pixel");
        String deviceMemory = ScalarProperties.getString("device.memory");
        String filename = "lib" + deviceMemory + megaPixel + "m_" + appName + "_" + index + "_";
        AppLog.info(TAG, "MemoryMapFile(w/o version) : " + filename);
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), appName, index);
        String filePath = map.getPath();
        return filePath;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        setAutoReviewControl(mPictureReviewTime);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (mWatchDogTimerForMaybePhaseDiffRunnableTask != null) {
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            mWatchDogTimerForMaybePhaseDiffRunnableTask.removeCallbacks();
            mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
        }
        mAutoPictureReviewControl = null;
        mCameraEx = null;
        mCamera = null;
        mAdapter = null;
        releaseAllRawData();
        if (mDSP != null) {
            mDSP.release();
            mDSP = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void releaseAllRawData() {
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        boolean isPullingBack = 2 == RunStatus.getStatus();
        if ((!isFirstStoreImageExecuted || isPullingBack) && m1stRaw != null) {
            m1stRaw.release();
            m1stRaw = null;
        }
        m1stRawDataInfo = null;
        if (!isLastStoreImageExecuted && m2ndRaw != null) {
            m2ndRaw.release();
            m2ndRaw = null;
        }
        m2ndRawDataInfo = null;
        if (!isLastStoreImageExecuted && mCompositRaw != null) {
            mCompositRaw.release();
            mCompositRaw = null;
        }
        if (mDSP != null) {
            mDSP.release();
            mDSP = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean equalsIgnoreCase = GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE);
        needApertureSetting = equalsIgnoreCase;
        needSsSetting = equalsIgnoreCase;
        needSsSetting |= GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Shutter");
        needApertureSetting |= GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Aperture");
        if (GFCommonUtil.getInstance().isFixedAperture()) {
            needApertureSetting = false;
        }
        if (GFCommonUtil.getInstance().hasIrisRing()) {
            mToggledFocusMode = false;
            mFocusMode = FocusModeController.getInstance().getValue();
            if (GFFocusModeController.getInstance().isFocusControl()) {
                if (GFFocusModeController.getInstance().isFocusHeld()) {
                    GFFocusModeController.getInstance().holdFocusControl(false);
                } else {
                    mToggledFocusMode = true;
                    GFFocusModeController.getInstance().toggleFocusControl();
                }
            }
        }
        if (((GFAdapterImpl) getAdapter()).getSequence() == null) {
            ((GFAdapterImpl) getAdapter()).setSequence();
        }
        GFCommonUtil.getInstance().clearCancelByLensChangingFlag();
        set1stOptions();
        isPFIssue = false;
        try {
            setAutoReviewControl(0);
            isCameraReady = CameraSetting.getInstance().getShutterSpeedInfo() != null;
            needAELockSetting = (AELController.getInstance().getAELockButtonState() || ((GFCommonUtil.getInstance().hasIrisRing() || GFCommonUtil.getInstance().isFixedAperture()) && GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE))) ? false : true;
            isFirstStoreImageExecuted = false;
            isLastStoreImageExecuted = false;
            isFirstShooting = true;
            cancelTakePicture = false;
            if (GFCommonUtil.getInstance().isPushedIR2SecKey()) {
                GFCommonUtil.getInstance().setPushedIR2SecKey(false);
                isSelfTimer = true;
            }
            if (isSelfTimer) {
                mSelfTimerRunnableTask = new SelfTimerRunnableTask();
            }
            if (isRemoteControlMode) {
                setRemoteControlMode(false);
            }
            releaseAllRawData();
            if (SaUtil.isAVIP()) {
                GFRawAPIHandling.getEmptyImageInstance();
            }
            mDSP = DSP.createProcessor("sony-di-dsp");
            CameraNotificationManager.getInstance().setNotificationListener(this);
            GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(true);
            AppLog.exit(TAG, AppLog.getMethodName());
        } catch (Exception e) {
            AppLog.error(TAG, "PF Issue(MK151H-8991) happened.");
            isPFIssue = true;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isPFIssue || !isCameraReady) {
            enableNextCapture(1);
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            return;
        }
        if (isFirstShooting) {
            if (GFCommonUtil.getInstance().maybePhaseDiffFlag()) {
                int waitTime = WhiteBalanceController.DEF_TEMP;
                if (isSelfTimer) {
                    waitTime = WhiteBalanceController.DEF_TEMP + 2000;
                }
                mWatchDogTimerForMaybePhaseDiffRunnableTask = new WatchDogTimerForMaybePhaseDiffRunnableTask();
                mWatchDogTimerForMaybePhaseDiffRunnableTask.execute(waitTime);
            }
            set1stCameraSetting(isSelfTimer);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WatchDogTimerForMaybePhaseDiffRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private WatchDogTimerForMaybePhaseDiffRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.WatchDogTimerForMaybePhaseDiffRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFCompositProcess.mAdapter.enableHalt(true) && GFCommonUtil.getInstance().maybePhaseDiffFlag()) {
                        GFCompositProcess.enableNextCapture(1);
                        AppLog.info(GFCompositProcess.TAG, "Cancel S2 trigger. Because onShutter() is not coming and maybePhaseDiff is true. ");
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelfTimerRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private SelfTimerRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess.SelfTimerRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFCompositProcess.mAdapter.enableHalt(true)) {
                        if (GFCompositProcess.cancelTakePicture || MediaNotificationManager.getInstance().getMediaState() == 0) {
                            GFCompositProcess.enableNextCapture(1);
                            return;
                        }
                        if (GFCompositProcess.needAELockSetting) {
                            AELController.getInstance().holdAELock(true);
                        }
                        GFCompositProcess.this.burstableTakePicture();
                    }
                }
            };
        }

        public void execute() {
            this.mHandler.postDelayed(this.mRunnable, GFConstants.SELFTIMER_DELAY_TIME);
        }

        public void removeCallbacks() {
            AppLog.enter(GFCompositProcess.TAG, AppLog.getMethodName());
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mRunnable = null;
            this.mHandler = null;
            AppLog.exit(GFCompositProcess.TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void burstableTakePicture() {
        mAdapter.enableHalt(false);
        mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
        AppLog.info(TAG, "onShutter:" + status);
        if (status != 0) {
            releaseAllRawData();
            isFirstShooting = true;
            int runStatus = RunStatus.getStatus();
            if (2 != runStatus) {
                mAdapter.enableNextCapture(status);
            }
            mAdapter.enableHalt(true);
        }
        if (!isFirstShooting) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.MUTE_SCREEN);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mSequence = sequence;
        if (!needMediaSetting || isFirstShooting) {
            storeImage(raw, false, true);
        }
        if (isFirstShooting) {
            mCameraEx.cancelTakePicture();
            int runStatus = RunStatus.getStatus();
            if (2 == runStatus) {
                releaseAllRawData();
                mAdapter.enableHalt(true);
                return;
            } else {
                if (GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
                    setAutoReviewControl(mPictureReviewTime);
                }
                isFirstShooting = false;
                set2ndOptions();
                set2ndCameraSetting();
            }
        } else {
            if (needMediaSetting && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
                filter.setSource(raw, false);
                filter.execute();
                OptimizedImage img = filter.getOutput();
                mYcInfo = getYcDataInfo(img);
                img.release();
                filter.clearSources();
                filter.release();
            }
            CameraNotificationManager.getInstance().requestNotify(GFConstants.START_PROCESSING);
            m2ndRawDataInfo = GFRawAPIHandling.getGFRawDataInfo(raw);
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                Log.d(TAG, "m2ndRawDataInfo(wbB: " + m2ndRawDataInfo.wbB + ", wbR: " + m2ndRawDataInfo.wbR + LogHelper.MSG_CLOSE_BRACKET);
                Log.d(TAG, "mYcInfo(wbB: " + mYcInfo.wbB + ", wbR: " + mYcInfo.wbR + LogHelper.MSG_CLOSE_BRACKET);
                m2ndRawDataInfo.wbB = mYcInfo.wbB;
                m2ndRawDataInfo.wbR = mYcInfo.wbR;
            }
            if (SaUtil.isAVIP()) {
                m2ndRaw = mDSP.createBuffer(16032768);
            } else {
                m2ndRaw = mDSP.createBuffer(m2ndRawDataInfo.canvasSizeX * m2ndRawDataInfo.canvasSizeY);
            }
            mCompositRaw = raw;
            NDSA.getInstance().open(mDSP, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw);
            int runStatus2 = RunStatus.getStatus();
            if (2 == runStatus2 || cancelTakePicture) {
                storeCompositImage();
                CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
            } else if (GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
                storeCompositImage();
                CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                CameraNotificationManager.getInstance().requestNotify(GFConstants.REQUEST_AUTOREVIEW);
            } else {
                NDSA.getInstance().execute();
                int runStatus3 = RunStatus.getStatus();
                if (2 == runStatus3) {
                    storeCompositImage();
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                } else {
                    GFImageAdjustmentUtil.getInstance().initialize();
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.REQUEST_ADJUSTMENT);
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static DeviceBuffer copyRawData(CameraSequence.RawData raw, GFRawDataInfo info) {
        DeviceBuffer rawBuffer;
        if (SaUtil.isAVIP()) {
            rawBuffer = mDSP.createBuffer(16032768);
        } else {
            rawBuffer = mDSP.createBuffer(info.canvasSizeX * info.canvasSizeY);
        }
        NDSA.getInstance().copy(mDSP, rawBuffer, info, raw);
        return rawBuffer;
    }

    private static void setUsableSpaceBeforeShooting() {
        File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
        usableSpaceBeforeShooting = memoryCard.getUsableSpace();
    }

    private static long getStableUsableSpace(int interval) {
        int i = 0;
        List<Long> sizeList = new ArrayList<>();
        File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
        while (true) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sizeList.add(Long.valueOf(memoryCard.getUsableSpace()));
            if (sizeList.size() > 6) {
                sizeList.remove(0);
            }
            if (sizeList.size() == 6 && sizeList.get(0).longValue() - sizeList.get(5).longValue() == 0) {
                Log.d(TAG, "Still writing is done.");
                break;
            }
            i++;
            if (i > 30000 / interval) {
                break;
            }
        }
        long size = sizeList.get(0).longValue();
        return size;
    }

    private static void wait1stStillWriting() {
        File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
        do {
        } while (usableSpaceBeforeShooting == memoryCard.getUsableSpace());
        usableSpace2ndShooting = getStableUsableSpace(30);
    }

    private static void wait2ndStillWriting() {
        File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
        do {
        } while (usableSpace2ndShooting == memoryCard.getUsableSpace());
        getStableUsableSpace(50);
    }

    public static void storeImage(CameraSequence.RawData raw, boolean isRawReleased, boolean isOptimizedImageReleased) {
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        boolean needWait = (isRaw || isRawJpeg) && !isFirstShooting && !isLastStoreImageExecuted && GFImageSavingController.ALL.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        if (isFirstShooting) {
            setUsableSpaceBeforeShooting();
        }
        if (needWait) {
            wait1stStillWriting();
        }
        if (isRaw) {
            filter.setSource(raw, false);
            filter.execute();
            OptimizedImage img = filter.getOutput();
            mYcInfo = getYcDataInfo(img);
            set1stRawDataInfo(raw);
            img.release();
            mSequence.storeImage(raw, isRawReleased || isFirstShooting);
        } else {
            filter.setRawFileStoreEnabled(isRawJpeg);
            filter.setSource(raw, isRawReleased);
            filter.execute();
            OptimizedImage img2 = filter.getOutput();
            mYcInfo = getYcDataInfo(img2);
            set1stRawDataInfo(raw);
            mSequence.storeImage(img2, isOptimizedImageReleased);
            if (isFirstShooting) {
                raw.release();
            }
        }
        if (needWait) {
            wait2ndStillWriting();
        }
        isFirstStoreImageExecuted = true;
        filter.clearSources();
        filter.release();
    }

    private static CameraSequence.YcDataInfo getYcDataInfo(OptimizedImage img) {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            return null;
        }
        CameraSequence.YcDataInfo ycInfo = mSequence.getYcDataInfo(img);
        return ycInfo;
    }

    private static void set1stRawDataInfo(CameraSequence.RawData raw) {
        if (isFirstShooting) {
            m1stRawDataInfo = GFRawAPIHandling.getGFRawDataInfo(raw);
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                Log.d(TAG, "m1stRawDataInfo(wbB: " + m1stRawDataInfo.wbB + ", wbR: " + m1stRawDataInfo.wbR + LogHelper.MSG_CLOSE_BRACKET);
                Log.d(TAG, "mYcInfo(wbB: " + mYcInfo.wbB + ", wbR: " + mYcInfo.wbR + LogHelper.MSG_CLOSE_BRACKET);
                m1stRawDataInfo.wbB = mYcInfo.wbB;
                m1stRawDataInfo.wbR = mYcInfo.wbR;
            }
            m1stRaw = copyRawData(raw, m1stRawDataInfo);
        }
    }

    public static void recordLog() {
        ScalarCalendar cal = new ScalarCalendar();
        int year = cal.get(1);
        int month = cal.get(2) + 1;
        int date = cal.get(5);
        int hour = cal.get(11);
        int minute = cal.get(12);
        int second = cal.get(13);
        String datestring = String.format("%4d/%02d/%02d %02d:%02d:%02d", Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
        cal.clear();
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Point point = mParams.getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio);
        int valueX = point.x;
        int valueY = point.y;
        double perX = (valueX / m2ndRawDataInfo.validSizeX) * 100.0d;
        double perY = (valueY / m2ndRawDataInfo.validSizeY) * 100.0d;
        int degree = 360 - mParams.getDegree();
        if (360 <= degree) {
            degree = 0;
        }
        int shadingLevel = mParams.getStrength();
        String level = String.valueOf(shadingLevel + 1);
        if (mFw == null) {
            try {
                mFw = new FileWriter(debugFileName, true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "file error");
            }
        }
        try {
            mFw.write(datestring + ExposureModeController.SOFT_SNAP);
            mFw.write("X=" + String.valueOf(valueX) + LogHelper.MSG_OPEN_BRACKET + perX + "%) ");
            mFw.write("Y=" + String.valueOf(valueY) + LogHelper.MSG_OPEN_BRACKET + perY + "%) ");
            mFw.write("deg=" + String.valueOf(degree) + ExposureModeController.SOFT_SNAP);
            mFw.write("defocus=" + level + " \n");
            mFw.close();
            Log.d(TAG, "LOG " + datestring + "," + valueX + LogHelper.MSG_OPEN_BRACKET + perX + "%)," + valueY + LogHelper.MSG_OPEN_BRACKET + perY + "%)," + degree + "," + level);
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.d(TAG, "file error");
        }
        if (mFw != null) {
            try {
                mFw.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            mFw = null;
        }
    }

    public static synchronized void storeCompositImage() {
        synchronized (GFCompositProcess.class) {
            if (debugOutputFile) {
                recordLog();
            }
            if (isLastStoreImageExecuted) {
                AppLog.warning(TAG, "storeCompositImage() is already called.");
            } else {
                NDSA.getInstance().update();
                m1stRaw.release();
                m1stRaw = null;
                m2ndRaw.release();
                m2ndRaw = null;
                NDSA.getInstance().close();
                if (mCompositRaw != null) {
                    isLastStoreImageExecuted = true;
                    storeImage(mCompositRaw, true, true);
                    GFKikiLogUtil.getInstance().countShots();
                }
                releaseAllRawData();
                int runStatus = RunStatus.getStatus();
                if (2 == runStatus) {
                    mAdapter.enableHalt(true);
                } else {
                    enableNextCapture(0);
                }
            }
        }
    }

    public static DeviceBuffer get1stRawData() {
        return m1stRaw;
    }

    public static DeviceBuffer get2ndRawData() {
        return m2ndRaw;
    }

    public static CameraSequence.RawData getCompositRaw() {
        return mCompositRaw;
    }

    private void setMemoryMapConfiguration() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion == 2) {
            AppLog.info(TAG, "ALLOCATION_POLICY_VERSION_1");
            MemoryMapConfig.setAllocationPolicy(1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void enableNextCapture(int status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        TouchLessShutterController.ExposingByTouchLessShutter = false;
        if (needAutoWhiteBalanceLock) {
            setAutoWhiteBalanceLock(false);
        }
        mCameraEx.cancelTakePicture();
        mCamera.cancelAutoFocus();
        mAdapter.enableNextCapture(status);
        if (mSelfTimerRunnableTask != null) {
            mSelfTimerRunnableTask.removeCallbacks();
            mSelfTimerRunnableTask = null;
        }
        if (mWatchDogTimerForMaybePhaseDiffRunnableTask != null) {
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(false);
            mWatchDogTimerForMaybePhaseDiffRunnableTask.removeCallbacks();
            mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
        }
        isSelfTimer = GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"));
        if (isRemoteControlMode) {
            setRemoteControlMode(true);
        }
        if (needAELockSetting || GFCommonUtil.getInstance().isCancelByLensChanging()) {
            AELController.getInstance().holdAELock(false);
        }
        GFCommonUtil.getInstance().setDuringSelfTimer(false);
        if (GFCommonUtil.getInstance().hasIrisRing()) {
            String focusMode = FocusModeController.getInstance().getFocusModeFromFocusModeDial();
            if (focusMode != null) {
                FocusModeController.getInstance().setValue(focusMode);
                if (mToggledFocusMode) {
                    GFFocusModeController.getInstance().toggleFocusControl();
                }
            } else {
                FocusModeController.getInstance().setValue(mFocusMode);
            }
        }
        if (GFCommonUtil.getInstance().needActualMediaSetting()) {
            GFCommonUtil.getInstance().setActualMediaIds(mCameraEx, null);
        }
        mAdapter.enableHalt(true);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private static void setRemoteControlMode(boolean mode) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(mode);
        mCamera.setParameters((Camera.Parameters) setParams.first);
    }

    private int getAutoReviewControl() {
        if (mAutoPictureReviewControl == null) {
            mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            mCameraEx.setAutoPictureReviewControl(mAutoPictureReviewControl);
        }
        int reviewTime = mAutoPictureReviewControl.getPictureReviewTime();
        return reviewTime;
    }

    private static void setAutoReviewControl(int reviewTime) {
        if (mAutoPictureReviewControl == null) {
            mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            mCameraEx.setAutoPictureReviewControl(mAutoPictureReviewControl);
        }
        mAutoPictureReviewControl.setPictureReviewTime(reviewTime);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static IAdapter getAdapter() {
        return mAdapter;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        cancelTakePicture = true;
    }
}
