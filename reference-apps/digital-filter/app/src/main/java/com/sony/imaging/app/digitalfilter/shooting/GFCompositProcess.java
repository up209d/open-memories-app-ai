package com.sony.imaging.app.digitalfilter.shooting;

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
import com.sony.imaging.app.base.shooting.camera.ApscModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.common.GFRawAPIHandling;
import com.sony.imaging.app.digitalfilter.common.GFRawDataInfo;
import com.sony.imaging.app.digitalfilter.sa.NDSA;
import com.sony.imaging.app.digitalfilter.sa.NDSA2;
import com.sony.imaging.app.digitalfilter.sa.NDSA2Multi;
import com.sony.imaging.app.digitalfilter.shooting.ChangeAperture;
import com.sony.imaging.app.digitalfilter.shooting.ChangeIso;
import com.sony.imaging.app.digitalfilter.shooting.ChangeSs;
import com.sony.imaging.app.digitalfilter.shooting.ChangeWhiteBalance;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFClippingCorrectionController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
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
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class GFCompositProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    private static final String CPY_CMD = "cp";
    private static final String CPY_DST = "$IMG_PATH\n";
    private static final String CPY_EXPORT = "export IMG_PATH=/local_image_path";
    private static final String SIM_CMD = "sim";
    private static final String SIM_OUT_PREFIX = "RESULT\n";
    private static final String SIM_RAW_SUFFIX = ".ARW";
    private static final String SIM_SPACE = " ";
    private static final int STORE_IMAGE_COMPOSIT = -1;
    private static final String STR_RERUTN = "\n";
    private static IAdapter mAdapter;
    private static CameraEx mCameraEx;
    private static final String TAG = AppLog.getClassName();
    private static CameraSetting mCamSet = null;
    private static GFCommonUtil mComUtil = null;
    private static Camera mCamera = null;
    private static boolean isRemoteControlMode = false;
    private static boolean isSelfTimer = false;
    private static boolean cancelTakePicture = false;
    private static CameraSequence mSequence = null;
    private static WatchDogTimerForMaybePhaseDiffRunnableTask mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
    private static SelfTimerRunnableTask mSelfTimerRunnableTask = null;
    private static boolean isRaw = false;
    private static boolean isRawJpeg = false;
    private static DeviceBuffer m1stRaw = null;
    private static DeviceBuffer m2ndRaw = null;
    private static DeviceBuffer m3rdRaw = null;
    private static CameraSequence.RawData mCompositRaw = null;
    private static GFRawDataInfo m1stRawDataInfo = null;
    private static GFRawDataInfo m2ndRawDataInfo = null;
    private static GFRawDataInfo m3rdRawDataInfo = null;
    private static CameraSequence.YcDataInfo mYcInfo = null;
    private static DSP mDSP0 = null;
    private static DSP mDSP1 = null;
    private static DSP mDSP2 = null;
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
    private static boolean isStoredCompositImage1 = false;
    private static boolean isStoredCompositImage2 = false;
    public static boolean canStoreCompositImageByCaputureState = false;
    private static boolean isCameraReady = false;
    private static boolean mToggledFocusMode = false;
    private static String mFocusMode = null;
    public static boolean debugAlgo = false;
    public static boolean debugOutputFile = false;
    public static String debugFileName = Environment.getExternalStorageDirectory().getPath() + "/DFILTER.TXT";
    public static boolean debugOutputSimFile = false;
    public static final String mPicturePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
    public static String debugSimFileName = Environment.getExternalStorageDirectory().getPath() + "/SIM_CMD.TXT";
    public static String debugCopyFileName = Environment.getExternalStorageDirectory().getPath() + "/CPY_CMD.TXT";
    public static String mJpegFile = Environment.getExternalStorageDirectory().getPath() + "/SKYHDR.JPG";
    private static int mAllCounterInterval = 0;
    private static int mCurrentShootingLayer = 1;
    private static int m1stShootingLayer = 1;
    private static int m2ndShootingLayer = 0;
    private static int m3rdShootingLayer = 2;
    private static boolean need3rdShooting = false;
    private static String mApscSetting = null;
    private static boolean waitCameraSettingChange__isTimerShooting = false;
    private static Object waitCameraSettingChange__lockObject = new Object();
    private static boolean waitCameraSettingChange__isActualFirstShooting = false;
    private static long usableSpaceBeforeShooting = 0;
    private static long usableSpace2ndShooting = 0;
    private static FileWriter mFw = null;
    private static String mSimFiles0 = null;
    private static String mSimFiles1 = null;
    private static String mSimFiles2 = null;
    private static int STORE_IMAGE_1ST = 1;
    private static int STORE_IMAGE_2ND = 2;
    private static int STORE_IMAGE_3RD = 3;
    private static int mCurrentStoreImage = STORE_IMAGE_1ST;
    private static LinkedList<Integer> mSimWbR0 = null;
    private static LinkedList<Integer> mSimWbB0 = null;
    private static LinkedList<Integer> mSimWbR1 = null;
    private static LinkedList<Integer> mSimWbB1 = null;
    private static LinkedList<Integer> mSimWbR2 = null;
    private static LinkedList<Integer> mSimWbB2 = null;
    private static LinkedList<Integer> mSimWbRout = null;
    private static LinkedList<Integer> mSimWbBout = null;
    private static LinkedList<Integer> mSimCanvasX = null;
    private static LinkedList<Integer> mSimCanvasY = null;
    private static LinkedList<Integer> mSimMarginOffsetX = null;
    private static LinkedList<Integer> mSimMarginOffsetY = null;
    private static LinkedList<Integer> mSimMarginX = null;
    private static LinkedList<Integer> mSimMarginY = null;
    private static LinkedList<Integer> mSimValidOffsetX = null;
    private static LinkedList<Integer> mSimValidOffsetY = null;
    private static LinkedList<Integer> mSimValidX = null;
    private static LinkedList<Integer> mSimValidY = null;
    private static LinkedList<Integer> mSimX1 = null;
    private static LinkedList<Integer> mSimY1 = null;
    private static LinkedList<Integer> mSimX2 = null;
    private static LinkedList<Integer> mSimY2 = null;
    private static LinkedList<Integer> mSimDegree1 = null;
    private static LinkedList<Integer> mSimDegree2 = null;
    private static LinkedList<Integer> mSimCoeff1 = null;
    private static LinkedList<Integer> mSimCoeff2 = null;
    private static LinkedList<Integer> m1stShot = null;
    private static LinkedList<Integer> m2ndShot = null;
    private static LinkedList<Integer> m3rdShot = null;
    private static Object LinkedListObj = new Object();
    MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();
    private Object waitApertureSettingChange__lockObject = new Object();
    private String[] TAGS = {GFConstants.CANCELTAKEPICTURE};

    /* JADX INFO: Access modifiers changed from: protected */
    public GFCompositProcess() {
        mCamSet = CameraSetting.getInstance();
        mComUtil = GFCommonUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mCameraEx = cameraEx;
        mCamera = cameraEx.getNormalCamera();
        mAdapter = adapter;
        mAllCounterInterval = 0;
        GFKikiLogUtil.getInstance().setCaptureIds();
        GFImageAdjustmentUtil.getInstance();
        mComUtil.setIrisRingSettingByCamera(false);
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        try {
            String pictureQuality = PictureQualityController.getInstance().getValue(null);
            isRaw = PictureQualityController.PICTURE_QUALITY_RAW.equalsIgnoreCase(pictureQuality);
            isRawJpeg = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equalsIgnoreCase(pictureQuality);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        debugAlgo = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_DEBUG_ALG_LOG, false);
        debugOutputFile = 3 == BackUpUtil.getInstance().getPreferenceInt(GFBackUpKey.KEY_DEBUG_LOG, 0);
        debugOutputSimFile = 3 == BackUpUtil.getInstance().getPreferenceInt(GFBackUpKey.KEY_DEBUG_LOG, 0);
        if (debugOutputFile || debugOutputSimFile) {
            if (!isRaw && !isRawJpeg) {
                isRawJpeg = true;
                PictureQualityController.getInstance().setValue(null, PictureQualityController.PICTURE_QUALITY_RAWJPEG);
            }
            if (!GFImageSavingController.getInstance().getValue().equals(GFImageSavingController.ALL)) {
                GFImageSavingController.getInstance().setValue(GFImageSavingController.SAVE, GFImageSavingController.ALL);
            }
        }
        need3rdShooting = GFFilterSetController.getInstance().need3rdShooting();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = mCamSet.getParameters();
        isRemoteControlMode = ((CameraEx.ParametersModifier) params.second).getRemoteControlMode();
        mPictureReviewTime = getAutoReviewControl();
        isSelfTimer = GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"));
        mComUtil.setPushedIR2SecKey(false);
        needMediaSetting = GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        if (mComUtil.isAVIP()) {
            GFRawAPIHandling.setPhicalAddress();
        }
        checkShootingOrder();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.RESTART_COMPOSIT_PROCESS);
        if (debugOutputSimFile && ((isRaw || isRawJpeg) && !needMediaSetting && mComUtil.isSupportedVersion(2, 7))) {
            setStoreImageCompleteListener();
        }
        mCameraEx.setJpegListener((CameraEx.JpegListener) null);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String changeApscSetting() {
        String value = null;
        try {
            value = ApscModeController.getInstance().getValue(ApscModeController.TAG_APSC_MODE_SETTING);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        AppLog.info(TAG, "APSC SETTING: " + value);
        if (value != null && value.equals("auto")) {
            try {
                String condition = ApscModeController.getInstance().getValue(ApscModeController.TAG_APSC_MODE_CONDITION);
                if (condition.equals("on") || condition.equals("off")) {
                    ApscModeController.getInstance().setValue(ApscModeController.TAG_APSC_MODE_SETTING, condition);
                }
                AppLog.info(TAG, "APSC CONDITION: " + condition);
            } catch (IController.NotSupportedException e2) {
                e2.printStackTrace();
            }
        }
        return value;
    }

    private static void restoreApscSetting(String value) {
        if (value != null && value.equals("auto")) {
            ApscModeController.getInstance().setValue(ApscModeController.TAG_APSC_MODE_SETTING, "auto");
        }
    }

    private void checkShootingOrder() {
        String value = GFShootingOrderController.getInstance().getValue();
        boolean isOrder12 = false;
        boolean isOrder21 = false;
        if (!need3rdShooting) {
            isOrder12 = GFShootingOrderController.getInstance().isLandSkyOrder();
            isOrder21 = !isOrder12;
        }
        if (GFShootingOrderController.ORDER_123.equalsIgnoreCase(value) || isOrder12) {
            m1stShootingLayer = 0;
            m2ndShootingLayer = 1;
            m3rdShootingLayer = 2;
            return;
        }
        if (GFShootingOrderController.ORDER_213.equalsIgnoreCase(value) || isOrder21) {
            m1stShootingLayer = 1;
            m2ndShootingLayer = 0;
            m3rdShootingLayer = 2;
            return;
        }
        if (GFShootingOrderController.ORDER_312.equalsIgnoreCase(value)) {
            m1stShootingLayer = 2;
            m2ndShootingLayer = 0;
            m3rdShootingLayer = 1;
            return;
        }
        if (GFShootingOrderController.ORDER_132.equalsIgnoreCase(value)) {
            m1stShootingLayer = 0;
            m2ndShootingLayer = 2;
            m3rdShootingLayer = 1;
        } else if (GFShootingOrderController.ORDER_231.equalsIgnoreCase(value)) {
            m1stShootingLayer = 1;
            m2ndShootingLayer = 2;
            m3rdShootingLayer = 0;
        } else if (GFShootingOrderController.ORDER_321.equalsIgnoreCase(value)) {
            m1stShootingLayer = 2;
            m2ndShootingLayer = 1;
            m3rdShootingLayer = 0;
        }
    }

    /* loaded from: classes.dex */
    class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            boolean unused = GFCompositProcess.Media_changing = false;
            GFCompositProcess.this.waitAndShooting();
        }
    }

    private void set1stOptions() {
        setMemoryMapConfiguration();
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence("DigitalFilter", "01"));
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", false);
        opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
        if (!mComUtil.isAVIP()) {
            opt.setOption("DETECTION_OFF", false);
        }
        mAdapter.setOptions(opt);
    }

    private void set1stCameraSetting() {
        setCameraSetting(m1stShootingLayer);
    }

    private void setNextOptions() {
        if (!needMediaSetting && isCompositShooting()) {
            CameraSequence.Options opt = new CameraSequence.Options();
            if (debugAlgo) {
                opt.setOption("RECORD_COUNT", 3);
            } else {
                opt.setOption("RECORD_COUNT", 2);
            }
            mAdapter.setOptions(opt);
        }
        if (!isCompositShooting()) {
            mCameraEx.setJpegListener((CameraEx.JpegListener) null);
        }
    }

    private void setNextCameraSetting() {
        setCameraSetting(mCurrentShootingLayer);
    }

    private void setNextState() {
        mAllCounterInterval++;
        if (mCurrentShootingLayer == m1stShootingLayer) {
            mCurrentShootingLayer = m2ndShootingLayer;
            return;
        }
        if (mCurrentShootingLayer == m2ndShootingLayer) {
            if (need3rdShooting) {
                mCurrentShootingLayer = m3rdShootingLayer;
                return;
            } else {
                mCurrentShootingLayer = m1stShootingLayer;
                return;
            }
        }
        mCurrentShootingLayer = m1stShootingLayer;
    }

    private static boolean is1stShootingLayer() {
        return mCurrentShootingLayer == m1stShootingLayer;
    }

    private static boolean is2ndShootingLayer() {
        return mCurrentShootingLayer == m2ndShootingLayer;
    }

    private static boolean is3rdShootingLayer() {
        return mCurrentShootingLayer == m3rdShootingLayer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void waitAndShooting() {
        waitAndShooting(false);
    }

    private synchronized void waitAndShooting(boolean isSelfTimer2) {
        waitCameraSettingChange(isSelfTimer2, isFirstShootingOfAll());
        burstableTakePicture_cameraSettingChangeDone();
    }

    private void setCameraSetting(int layer) {
        int min;
        Wb_changing = true;
        Ss_changing = needSsSetting;
        Aperture_changing = needApertureSetting;
        Iso_changing = true;
        mComUtil.setCameraSettingsLayerDuringShots(layer);
        CameraNotificationManager.getInstance().requestNotify(GFEffectParameters.TAG_CHANGE);
        float maxShutterSpeed = mCamSet.getShutterSpeedInfo().currentAvailableMax_n / mCamSet.getShutterSpeedInfo().currentAvailableMax_d;
        float targetShutterSpeed = mParams.getSSNumerator(layer) / mParams.getSSDenominator(layer);
        final boolean needWaitApertureSetting = needApertureSetting && needSsSetting && mComUtil.isDSC() && targetShutterSpeed < maxShutterSpeed;
        if (mComUtil.hasIrisRing()) {
            FocusModeController.getInstance().setValue(FocusModeController.MANUAL);
            mCamera.cancelAutoFocus();
        }
        new ChangeWhiteBalance().execute(mParams.getWBMode(layer), mParams.getWBOption(layer), new ChangeWhiteBalance.Callback() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.1
            @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeWhiteBalance.Callback
            public void cbFunction() {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "").append("WB change done. : ").append(GFWhiteBalanceController.getInstance().getValue());
                Log.d(GFCompositProcess.TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                boolean unused = GFCompositProcess.Wb_changing = false;
                GFCompositProcess.burstableTakePicture_cameraSettingChangeDone();
            }
        });
        if (needApertureSetting) {
            CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
            int aperture = mParams.getAperture(layer);
            if (apertureInfo != null && aperture < (min = apertureInfo.currentAvailableMin)) {
                AppLog.info(TAG, "Target Aperture is less than currentAvailableMin:" + min);
                aperture = min;
            }
            int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), aperture);
            new ChangeAperture().execute(step, aperture, new ChangeAperture.Callback() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.2
                @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeAperture.Callback
                public void cbFunction() {
                    boolean unused = GFCompositProcess.Aperture_changing = false;
                    GFCompositProcess.burstableTakePicture_cameraSettingChangeDone();
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
                int SS_adjustment = ChangeSs.getSsAdjustmentStep(((Integer) ss.first).intValue(), ((Integer) ss.second).intValue(), mParams.getSSNumerator(layer), mParams.getSSDenominator(layer));
                new ChangeSs().execute(SS_adjustment, mParams.getSSNumerator(layer), mParams.getSSDenominator(layer), new ChangeSs.Callback() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.3
                    @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeSs.Callback
                    public void cbFunction() {
                        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                        builder.replace(0, builder.length(), "").append("SS change done. : ").append(GFCompositProcess.mCamSet.getShutterSpeed().first).append("/").append(GFCompositProcess.mCamSet.getShutterSpeed().second);
                        Log.d(GFCompositProcess.TAG, builder.toString());
                        StringBuilderThreadLocal.releaseScratchBuilder(builder);
                        boolean unused = GFCompositProcess.Ss_changing = false;
                        GFCompositProcess.burstableTakePicture_cameraSettingChangeDone();
                    }
                });
            } else {
                Ss_changing = false;
            }
        }
        new ChangeIso().execute(mParams.getISO(layer), new ChangeIso.Callback() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.4
            @Override // com.sony.imaging.app.digitalfilter.shooting.ChangeIso.Callback
            public void cbFunction() {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "").append("ISO change done. : ").append(ISOSensitivityController.getInstance().getValue());
                Log.d(GFCompositProcess.TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                boolean unused = GFCompositProcess.Iso_changing = false;
                GFCompositProcess.burstableTakePicture_cameraSettingChangeDone();
            }
        });
        ExposureCompensationController.getInstance().setValue(null, mParams.getExposureComp(layer));
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM() && (mParams.getWBMode(layer).equals("auto") || mParams.getWBMode(layer).equals(WhiteBalanceController.UNDERWATER_AUTO))) {
            if (!GFCommonUtil.getInstance().getAutoWhiteBalanceLock()) {
                GFCommonUtil.getInstance().setAutoWhiteBalanceLock(true);
            }
        } else {
            GFCommonUtil.getInstance().setAutoWhiteBalanceLock(false);
        }
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CAMERA_SETTING_CHANGED, Integer.valueOf(layer));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void apertureSettingChangeDone() {
        synchronized (this.waitApertureSettingChange__lockObject) {
            this.waitApertureSettingChange__lockObject.notifyAll();
        }
    }

    private void waitApertureSettingChange() {
        new Thread(new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.5
            @Override // java.lang.Runnable
            public void run() {
                synchronized (GFCompositProcess.waitCameraSettingChange__lockObject) {
                    if (GFCompositProcess.Aperture_changing) {
                        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                        builder.replace(0, builder.length(), "Start to wait Aperture changing.");
                        Log.d(GFCompositProcess.TAG, builder.toString());
                        StringBuilderThreadLocal.releaseScratchBuilder(builder);
                        try {
                            GFCompositProcess.this.waitApertureSettingChange__lockObject.wait(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                        builder2.replace(0, builder2.length(), "Stop to wait Aperture changing.");
                        Log.d(GFCompositProcess.TAG, builder2.toString());
                        StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void burstableTakePicture_cameraSettingChangeDone() {
        synchronized (GFCompositProcess.class) {
            if (Wb_changing || Iso_changing || Ss_changing || Aperture_changing || Media_changing) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "").append("Wb_changing:").append(Wb_changing).append(STR_RERUTN).append("Iso_changing:").append(Iso_changing).append(STR_RERUTN).append("Ss_changing:").append(Ss_changing).append(STR_RERUTN).append("Aperture_changing:").append(Aperture_changing).append(STR_RERUTN).append("Media_changing:").append(Media_changing);
                Log.d(TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
            } else {
                StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                builder2.replace(0, builder2.length(), "All change done.");
                Log.d(TAG, builder2.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                synchronized (waitCameraSettingChange__lockObject) {
                    StringBuilder builder3 = StringBuilderThreadLocal.getScratchBuilder();
                    builder3.replace(0, builder3.length(), "Lock release request(notifyAll).");
                    Log.d(TAG, builder3.toString());
                    StringBuilderThreadLocal.releaseScratchBuilder(builder3);
                    waitCameraSettingChange__lockObject.notifyAll();
                }
            }
        }
    }

    private void waitCameraSettingChange(boolean isTimerShooting, boolean isActualFirstShooting) {
        waitCameraSettingChange__isTimerShooting = isTimerShooting;
        waitCameraSettingChange__isActualFirstShooting = isActualFirstShooting;
        new Thread(new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.6
            @Override // java.lang.Runnable
            public void run() {
                synchronized (GFCompositProcess.waitCameraSettingChange__lockObject) {
                    try {
                        if (GFCompositProcess.waitCameraSettingChange__isTimerShooting) {
                            GFCompositProcess.mSelfTimerRunnableTask.execute();
                        } else {
                            if (GFCompositProcess.Wb_changing || GFCompositProcess.Iso_changing || GFCompositProcess.Ss_changing || GFCompositProcess.Aperture_changing || GFCompositProcess.Media_changing) {
                                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                                builder.replace(0, builder.length(), "Lock started").append(GFCompositProcess.STR_RERUTN).append("  Wb_changing:").append(GFCompositProcess.Wb_changing).append(GFCompositProcess.STR_RERUTN).append("  Iso_changing:").append(GFCompositProcess.Iso_changing).append(GFCompositProcess.STR_RERUTN).append("  Ss_changing:").append(GFCompositProcess.Ss_changing).append(GFCompositProcess.STR_RERUTN).append("  Aperture_changing:").append(GFCompositProcess.Aperture_changing).append(GFCompositProcess.STR_RERUTN).append("  Media_changing:").append(GFCompositProcess.Media_changing);
                                Log.d(GFCompositProcess.TAG, builder.toString());
                                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                                GFCompositProcess.waitCameraSettingChange__lockObject.wait(3000L);
                                StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                                builder2.replace(0, builder2.length(), "Lock released 3sec?");
                                Log.d(GFCompositProcess.TAG, builder2.toString());
                                StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                            }
                            GFCompositProcess.mAdapter.enableHalt(false);
                            if (AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
                                CautionUtilityClass.getInstance().requestTrigger(GFCompositProcess.mComUtil.getCautionId());
                                GFCompositProcess.releaseAllRawData();
                                if (GFCompositProcess.mDSP0 != null) {
                                    GFCompositProcess.mDSP0.release();
                                    DSP unused = GFCompositProcess.mDSP0 = null;
                                }
                                if (GFCompositProcess.mDSP1 != null) {
                                    GFCompositProcess.mDSP1.release();
                                    DSP unused2 = GFCompositProcess.mDSP1 = null;
                                }
                                if (GFCompositProcess.mDSP2 != null) {
                                    GFCompositProcess.mDSP2.release();
                                    DSP unused3 = GFCompositProcess.mDSP2 = null;
                                }
                                GFCompositProcess.enableNextCapture(0);
                                return;
                            }
                            if (GFCompositProcess.waitCameraSettingChange__isActualFirstShooting && GFCompositProcess.needAELockSetting) {
                                AELController.getInstance().holdAELock(true);
                            }
                            GFCompositProcess.burstableTakePicture();
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
        System.gc();
        setAutoReviewControl(mPictureReviewTime);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (mCameraEx != null) {
            mCameraEx.setJpegListener((CameraEx.JpegListener) null);
            if (debugOutputSimFile && ((isRaw || isRawJpeg) && !needMediaSetting && mComUtil.isSupportedVersion(2, 7))) {
                removeStoreImageCompleteListener();
            }
        }
        if (mWatchDogTimerForMaybePhaseDiffRunnableTask != null) {
            mComUtil.setMaybePhaseDiffFlag(false);
            mWatchDogTimerForMaybePhaseDiffRunnableTask.removeCallbacks();
            mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
        }
        mApscSetting = null;
        mAutoPictureReviewControl = null;
        mCameraEx = null;
        mCamera = null;
        mAdapter = null;
        releaseAllRawData();
        if (mDSP0 != null) {
            mDSP0.release();
            mDSP0 = null;
        }
        if (mDSP1 != null) {
            mDSP1.release();
            mDSP1 = null;
        }
        if (mDSP2 != null) {
            mDSP2.release();
            mDSP2 = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void releaseAllRawData() {
        if (m1stRaw != null) {
            m1stRaw.release();
            m1stRaw = null;
        }
        if (m2ndRaw != null) {
            m2ndRaw.release();
            m2ndRaw = null;
        }
        if (m3rdRaw != null) {
            m3rdRaw.release();
            m3rdRaw = null;
        }
        m1stRawDataInfo = null;
        m2ndRawDataInfo = null;
        m3rdRawDataInfo = null;
        if (!isStoredCompositImage1 && mCompositRaw != null) {
            mCompositRaw.release();
            mCompositRaw = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        mApscSetting = changeApscSetting();
        mParams = GFEffectParameters.getInstance().getParameters();
        mAllCounterInterval = 0;
        mCurrentShootingLayer = m1stShootingLayer;
        if (debugOutputSimFile && mComUtil.isSupportedVersion(2, 7)) {
            initStoreImageCompleteListenerParameters();
        }
        String exposureMode = GFExposureModeController.getInstance().getValue(null);
        boolean equalsIgnoreCase = exposureMode.equalsIgnoreCase(ExposureModeController.MANUAL_MODE);
        needApertureSetting = equalsIgnoreCase;
        needSsSetting = equalsIgnoreCase;
        needSsSetting |= exposureMode.equalsIgnoreCase("Shutter");
        needApertureSetting |= exposureMode.equalsIgnoreCase("Aperture");
        if (mComUtil.isFixedAperture()) {
            needApertureSetting = false;
        }
        if (mComUtil.hasIrisRing()) {
            mToggledFocusMode = false;
            mFocusMode = FocusModeController.getInstance().getValue();
            if (GFFocusModeController.getInstance().isFocusControl()) {
                if (GFFocusModeController.getInstance().isAfMfControlHold()) {
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
        mComUtil.clearCancelByLensChangingFlag();
        isPFIssue = false;
        try {
            setAutoReviewControl(0);
            isCameraReady = mCamSet.getShutterSpeedInfo() != null;
            needAELockSetting = (AELController.getInstance().getAELockButtonState() || ((mComUtil.hasIrisRing() || mComUtil.isFixedAperture()) && exposureMode.equalsIgnoreCase(ExposureModeController.MANUAL_MODE))) ? false : true;
            isStoredCompositImage1 = false;
            isStoredCompositImage2 = false;
            cancelTakePicture = false;
            isSelfTimer = GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"));
            if (mComUtil.isPushedIR2SecKey()) {
                mComUtil.setPushedIR2SecKey(false);
                isSelfTimer = true;
            }
            if (isSelfTimer) {
                mSelfTimerRunnableTask = new SelfTimerRunnableTask();
            }
            if (isRemoteControlMode) {
                setRemoteControlMode(false);
            }
            releaseAllRawData();
            if (mDSP0 != null) {
                mDSP0.release();
                mDSP0 = null;
            }
            if (mDSP1 != null) {
                mDSP1.release();
                mDSP1 = null;
            }
            if (mDSP2 != null) {
                mDSP2.release();
                mDSP2 = null;
            }
            if (mComUtil.isAVIP()) {
                GFRawAPIHandling.getEmptyImageInstance();
            }
            mDSP0 = DSP.createProcessor("sony-di-dsp");
            mDSP1 = DSP.createProcessor("sony-di-dsp");
            mDSP2 = DSP.createProcessor("sony-di-dsp");
            set1stOptions();
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
            mComUtil.setMaybePhaseDiffFlag(false);
            return;
        }
        if (mComUtil.maybePhaseDiffFlag()) {
            int waitTime = WhiteBalanceController.DEF_TEMP;
            if (isSelfTimer) {
                waitTime = WhiteBalanceController.DEF_TEMP + 2000;
            }
            mWatchDogTimerForMaybePhaseDiffRunnableTask = new WatchDogTimerForMaybePhaseDiffRunnableTask();
            mWatchDogTimerForMaybePhaseDiffRunnableTask.execute(waitTime);
        }
        Media_changing = false;
        set1stCameraSetting();
        waitAndShooting(isSelfTimer);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private static boolean isCompositShooting() {
        return need3rdShooting ? mCurrentShootingLayer == m3rdShootingLayer : mCurrentShootingLayer == m2ndShootingLayer;
    }

    private boolean isFirstShootingOfAll() {
        return mAllCounterInterval == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WatchDogTimerForMaybePhaseDiffRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private WatchDogTimerForMaybePhaseDiffRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.WatchDogTimerForMaybePhaseDiffRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFCompositProcess.mAdapter.enableHalt(true) && GFCompositProcess.mComUtil.maybePhaseDiffFlag()) {
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
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.SelfTimerRunnableTask.1
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
                        GFCompositProcess.burstableTakePicture();
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
    public static void burstableTakePicture() {
        mAdapter.enableHalt(false);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "burstableTakePicture");
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        mCameraEx.burstableTakePicture();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        mCameraEx.startSelfTimerShutter();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName() + "(STATUS : " + status + LogHelper.MSG_CLOSE_BRACKET);
        mComUtil.setMaybePhaseDiffFlag(false);
        if (status != 0) {
            releaseAllRawData();
            if (mDSP0 != null) {
                mDSP0.release();
                mDSP0 = null;
            }
            if (mDSP1 != null) {
                mDSP1.release();
                mDSP1 = null;
            }
            if (mDSP2 != null) {
                mDSP2.release();
                mDSP2 = null;
            }
            int runStatus = RunStatus.getStatus();
            if (2 != runStatus) {
                mAdapter.enableNextCapture(status);
            }
            mAdapter.enableHalt(true);
        } else if (isCompositShooting()) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.MUTE_SCREEN);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mSequence = sequence;
        OptimizedImage mOptImage = copyAndDevelopmentImage(raw);
        if (!needMediaSetting || !isCompositShooting()) {
            storeImage(raw, mOptImage);
        } else if (mOptImage != null) {
            mOptImage.release();
        }
        if (!isCompositShooting()) {
            mCameraEx.setJpegListener((CameraEx.JpegListener) null);
            mCameraEx.cancelTakePicture();
            int runStatus = RunStatus.getStatus();
            if (2 == runStatus) {
                releaseAllRawData();
                if (mDSP0 != null) {
                    mDSP0.release();
                    mDSP0 = null;
                }
                if (mDSP1 != null) {
                    mDSP1.release();
                    mDSP1 = null;
                }
                if (mDSP2 != null) {
                    mDSP2.release();
                    mDSP2 = null;
                }
                mAdapter.enableHalt(true);
                return;
            }
            setNextState();
            if (isCompositShooting() && GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
                setAutoReviewControl(mPictureReviewTime);
            }
            Media_changing = needMediaSetting && isCompositShooting();
            if (Media_changing) {
                mComUtil.setActualMediaIds(mCameraEx, this.sMyRecordingMediaChangeCallback);
                mComUtil.setActualMediaStatus(true);
            }
            setNextOptions();
            setNextCameraSetting();
            if (!needMediaSetting || !isCompositShooting()) {
                waitAndShooting();
            }
        } else {
            canStoreCompositImageByCaputureState = false;
            CameraNotificationManager.getInstance().requestNotify(GFConstants.START_PROCESSING);
            mCompositRaw = raw;
            if (need3rdShooting) {
                if (GFShootingOrderController.getInstance().isLayer3Third()) {
                    if (GFConstants.NDSA_MULTI_UNIT) {
                        NDSA2Multi.getInstance().open(mDSP0, mDSP1, mDSP2, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, m3rdRaw, m3rdRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    } else {
                        NDSA2.getInstance().open(mDSP0, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, m3rdRaw, m3rdRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    }
                } else if (GFShootingOrderController.getInstance().isLayer3Second()) {
                    if (GFConstants.NDSA_MULTI_UNIT) {
                        NDSA2Multi.getInstance().open(mDSP0, mDSP1, mDSP2, m1stRaw, m1stRawDataInfo, m3rdRaw, m3rdRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    } else {
                        NDSA2.getInstance().open(mDSP0, m1stRaw, m1stRawDataInfo, m3rdRaw, m3rdRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    }
                } else if (GFShootingOrderController.getInstance().isLayer3First()) {
                    if (GFConstants.NDSA_MULTI_UNIT) {
                        NDSA2Multi.getInstance().open(mDSP0, mDSP1, mDSP2, m2ndRaw, m2ndRawDataInfo, m3rdRaw, m3rdRawDataInfo, m1stRaw, m1stRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    } else {
                        NDSA2.getInstance().open(mDSP0, m2ndRaw, m2ndRawDataInfo, m3rdRaw, m3rdRawDataInfo, m1stRaw, m1stRawDataInfo, mCompositRaw, m3rdRawDataInfo, m3rdRawDataInfo.axiRawAddr, 3);
                    }
                }
            } else if (GFConstants.NDSA_MULTI_UNIT) {
                NDSA2Multi.getInstance().open(mDSP0, mDSP1, mDSP2, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m2ndRawDataInfo, m2ndRawDataInfo.axiRawAddr, 2);
            } else {
                NDSA2.getInstance().open(mDSP0, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m2ndRawDataInfo, m2ndRawDataInfo.axiRawAddr, 2);
            }
            AppLog.info(TAG, "NDSA2Multi open");
            int runStatus2 = RunStatus.getStatus();
            if (2 == runStatus2 || cancelTakePicture) {
                AppLog.info(TAG, "A storeCompositImage() by PULLINGBACK");
                storeCompositImage();
                CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
            } else if (GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
                storeCompositImage();
                CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                CameraNotificationManager.getInstance().requestNotify(GFConstants.REQUEST_AUTOREVIEW);
            } else {
                if (GFConstants.NDSA_MULTI_UNIT) {
                    NDSA2Multi.getInstance().execute();
                } else {
                    NDSA2.getInstance().execute();
                }
                int runStatus3 = RunStatus.getStatus();
                if (2 == runStatus3) {
                    AppLog.info(TAG, "B storeCompositImage() by PULLINGBACK");
                    storeCompositImage();
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                } else {
                    GFImageAdjustmentUtil.getInstance().initialize();
                    canStoreCompositImageByCaputureState = true;
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.END_PROCESSING);
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.REQUEST_ADJUSTMENT);
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static DeviceBuffer copyRawData(CameraSequence.RawData raw, GFRawDataInfo info) {
        DeviceBuffer rawBuffer;
        if (mComUtil.isAVIP()) {
            rawBuffer = mDSP0.createBuffer(16032768);
        } else {
            rawBuffer = mDSP0.createBuffer(info.canvasSizeX * info.canvasSizeY);
        }
        NDSA.getInstance().copy(mDSP0, rawBuffer, info, raw);
        return rawBuffer;
    }

    public static DeviceBuffer copyRawData(DeviceBuffer rawBuffer, CameraSequence.RawData raw, GFRawDataInfo info) {
        if (rawBuffer == null) {
            return copyRawData(raw, info);
        }
        NDSA.getInstance().copy(mDSP0, rawBuffer, info, raw);
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
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "Still writing is done.");
                Log.d(TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
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

    private static OptimizedImage copyAndDevelopmentImage(CameraSequence.RawData raw) {
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setRawFileStoreEnabled(isRawJpeg);
        filter.setSource(raw, false);
        filter.execute();
        OptimizedImage optImage = filter.getOutput();
        mYcInfo = getYcDataInfo(optImage);
        setRawDataInfo(raw);
        filter.clearSources();
        filter.release();
        if (isRaw) {
            optImage.release();
            return null;
        }
        return optImage;
    }

    private static void storeImage(CameraSequence.RawData raw, OptimizedImage optImage) {
        boolean needWait = (isRaw || isRawJpeg) && isCompositShooting() && GFImageSavingController.ALL.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        if (!isCompositShooting()) {
            setUsableSpaceBeforeShooting();
        }
        if (needWait) {
            wait1stStillWriting();
        }
        if (isRaw) {
            mSequence.storeImage(raw, isCompositShooting() ? false : true);
        } else {
            mSequence.storeImage(optImage, true);
            if (!isCompositShooting()) {
                raw.release();
            }
        }
        if (needWait) {
            wait2ndStillWriting();
        }
    }

    public static void storeCompositImage(CameraSequence.RawData raw) {
        boolean needWait = (isRaw || isRawJpeg) && isCompositShooting() && !isStoredCompositImage1 && GFImageSavingController.ALL.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        if (!isCompositShooting()) {
            setUsableSpaceBeforeShooting();
        }
        if (needWait) {
            wait1stStillWriting();
        }
        if (isRaw) {
            mSequence.storeImage(raw, isStoredCompositImage1);
        } else {
            CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
            filter.setRawFileStoreEnabled(isRawJpeg);
            filter.setSource(raw, isStoredCompositImage1);
            filter.execute();
            OptimizedImage img = filter.getOutput();
            if (debugAlgo) {
                mSequence.storeImage(img, true);
            } else {
                mSequence.storeImage(img, isStoredCompositImage1);
            }
            filter.clearSources();
            filter.release();
        }
        if (needWait) {
            wait2ndStillWriting();
        }
    }

    private static CameraSequence.YcDataInfo getYcDataInfo(OptimizedImage img) {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            return null;
        }
        CameraSequence.YcDataInfo ycInfo = mSequence.getYcDataInfo(img);
        return ycInfo;
    }

    private static void setRawDataInfo(CameraSequence.RawData raw) {
        if (is1stShootingLayer()) {
            m1stRawDataInfo = GFRawAPIHandling.getGFRawDataInfo(raw);
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "").append("m1stRawDataInfo(wbB: ").append(m1stRawDataInfo.wbB).append(", wbR: ").append(m1stRawDataInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN).append("mYcInfo(wbB: ").append(mYcInfo.wbB).append(", wbR: ").append(mYcInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN);
                Log.d(TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                m1stRawDataInfo.wbB = mYcInfo.wbB;
                m1stRawDataInfo.wbR = mYcInfo.wbR;
            }
            m1stRaw = copyRawData(raw, m1stRawDataInfo);
            return;
        }
        if (is2ndShootingLayer()) {
            m2ndRawDataInfo = GFRawAPIHandling.getGFRawDataInfo(raw);
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                builder2.replace(0, builder2.length(), "").append("m2ndRawDataInfo(wbB: ").append(m2ndRawDataInfo.wbB).append(", wbR: ").append(m2ndRawDataInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN).append("mYcInfo(wbB: ").append(mYcInfo.wbB).append(", wbR: ").append(mYcInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN);
                Log.d(TAG, builder2.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                m2ndRawDataInfo.wbB = mYcInfo.wbB;
                m2ndRawDataInfo.wbR = mYcInfo.wbR;
            }
            m2ndRaw = copyRawData(raw, m2ndRawDataInfo);
            return;
        }
        if (is3rdShootingLayer()) {
            m3rdRawDataInfo = GFRawAPIHandling.getGFRawDataInfo(raw);
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                StringBuilder builder3 = StringBuilderThreadLocal.getScratchBuilder();
                builder3.replace(0, builder3.length(), "").append("m3rdRawDataInfo(wbB: ").append(m3rdRawDataInfo.wbB).append(", wbR: ").append(m3rdRawDataInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN).append("mYcInfo(wbB: ").append(mYcInfo.wbB).append(", wbR: ").append(mYcInfo.wbR).append(LogHelper.MSG_CLOSE_BRACKET).append(STR_RERUTN);
                Log.d(TAG, builder3.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder3);
                m3rdRawDataInfo.wbB = mYcInfo.wbB;
                m3rdRawDataInfo.wbR = mYcInfo.wbR;
            }
            m3rdRaw = copyRawData(raw, m3rdRawDataInfo);
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
            mFw.write(datestring + " ");
            mFw.write("X=" + String.valueOf(valueX) + LogHelper.MSG_OPEN_BRACKET + perX + "%) ");
            mFw.write("Y=" + String.valueOf(valueY) + LogHelper.MSG_OPEN_BRACKET + perY + "%) ");
            mFw.write("deg=" + String.valueOf(degree) + " ");
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

    private synchronized void initStoreImageCompleteListenerParameters() {
        synchronized (LinkedListObj) {
            mCurrentStoreImage = STORE_IMAGE_1ST;
            mSimWbR0 = new LinkedList<>();
            mSimWbB0 = new LinkedList<>();
            mSimWbR1 = new LinkedList<>();
            mSimWbB1 = new LinkedList<>();
            mSimWbR2 = new LinkedList<>();
            mSimWbB2 = new LinkedList<>();
            mSimWbRout = new LinkedList<>();
            mSimWbBout = new LinkedList<>();
            mSimCanvasX = new LinkedList<>();
            mSimCanvasY = new LinkedList<>();
            mSimMarginOffsetX = new LinkedList<>();
            mSimMarginOffsetY = new LinkedList<>();
            mSimMarginX = new LinkedList<>();
            mSimMarginY = new LinkedList<>();
            mSimValidOffsetX = new LinkedList<>();
            mSimValidOffsetY = new LinkedList<>();
            mSimValidX = new LinkedList<>();
            mSimValidY = new LinkedList<>();
            mSimX1 = new LinkedList<>();
            mSimY1 = new LinkedList<>();
            mSimX2 = new LinkedList<>();
            mSimY2 = new LinkedList<>();
            mSimDegree1 = new LinkedList<>();
            mSimDegree2 = new LinkedList<>();
            mSimCoeff1 = new LinkedList<>();
            mSimCoeff2 = new LinkedList<>();
            m1stShot = new LinkedList<>();
            m2ndShot = new LinkedList<>();
            m3rdShot = new LinkedList<>();
        }
    }

    private static synchronized void offerDumpParameters() {
        synchronized (GFCompositProcess.class) {
            synchronized (LinkedListObj) {
                boolean isBaseFirst = GFShootingOrderController.getInstance().isLandSkyOrder();
                GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
                String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
                Point point = param.getSAPoint(m2ndRawDataInfo.validSizeX, m2ndRawDataInfo.validSizeY, aspectRatio);
                mSimX1.offer(Integer.valueOf(point.x));
                mSimY1.offer(Integer.valueOf(point.y));
                int degree1 = param.getSADegree();
                if (isBaseFirst && (degree1 = degree1 + 180) >= 360) {
                    degree1 -= 360;
                }
                mSimDegree1.offer(Integer.valueOf(degree1));
                mSimCoeff1.offer(Integer.valueOf(param.getStrength() + 1));
                mSimCanvasX.offer(Integer.valueOf(m1stRawDataInfo.canvasSizeX));
                mSimCanvasY.offer(Integer.valueOf(m1stRawDataInfo.canvasSizeY));
                mSimMarginOffsetX.offer(Integer.valueOf(m1stRawDataInfo.marginOffsetX));
                mSimMarginOffsetY.offer(Integer.valueOf(m1stRawDataInfo.marginOffsetY));
                mSimMarginX.offer(Integer.valueOf(m1stRawDataInfo.marginSizeX));
                mSimMarginY.offer(Integer.valueOf(m1stRawDataInfo.marginSizeY));
                mSimValidOffsetX.offer(Integer.valueOf(m1stRawDataInfo.validOffsetX));
                mSimValidOffsetY.offer(Integer.valueOf(m1stRawDataInfo.validOffsetY));
                mSimValidX.offer(Integer.valueOf(m1stRawDataInfo.validSizeX));
                mSimValidY.offer(Integer.valueOf(m1stRawDataInfo.validSizeY));
                if (need3rdShooting) {
                    if (GFShootingOrderController.getInstance().isLayer3Third()) {
                        if (isBaseFirst) {
                            mSimWbR0.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                            m1stShot.offer(1);
                            m2ndShot.offer(2);
                        } else {
                            mSimWbR0.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                            m1stShot.offer(2);
                            m2ndShot.offer(1);
                        }
                        mSimWbR2.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                        mSimWbB2.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                        m3rdShot.offer(3);
                    } else if (GFShootingOrderController.getInstance().isLayer3Second()) {
                        if (isBaseFirst) {
                            mSimWbR0.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                            m1stShot.offer(1);
                            m3rdShot.offer(2);
                        } else {
                            mSimWbR0.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                            m1stShot.offer(2);
                            m3rdShot.offer(1);
                        }
                        mSimWbR2.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                        mSimWbB2.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                        m2ndShot.offer(3);
                    } else if (GFShootingOrderController.getInstance().isLayer3First()) {
                        if (isBaseFirst) {
                            mSimWbR0.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                            m2ndShot.offer(1);
                            m3rdShot.offer(2);
                        } else {
                            mSimWbR0.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                            mSimWbB0.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                            mSimWbR1.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                            mSimWbB1.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                            m2ndShot.offer(2);
                            m3rdShot.offer(1);
                        }
                        mSimWbR2.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                        mSimWbB2.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                        m1stShot.offer(3);
                    }
                    mSimWbRout.offer(Integer.valueOf(m3rdRawDataInfo.wbR));
                    mSimWbBout.offer(Integer.valueOf(m3rdRawDataInfo.wbB));
                } else {
                    if (isBaseFirst) {
                        mSimWbR0.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                        mSimWbB0.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                        mSimWbR1.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                        mSimWbB1.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                        m1stShot.offer(1);
                        m2ndShot.offer(2);
                    } else {
                        mSimWbR0.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                        mSimWbB0.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                        mSimWbR1.offer(Integer.valueOf(m1stRawDataInfo.wbR));
                        mSimWbB1.offer(Integer.valueOf(m1stRawDataInfo.wbB));
                        m1stShot.offer(2);
                        m2ndShot.offer(1);
                    }
                    m3rdShot.offer(0);
                    mSimWbR2.offer(0);
                    mSimWbB2.offer(0);
                    mSimWbRout.offer(Integer.valueOf(m2ndRawDataInfo.wbR));
                    mSimWbBout.offer(Integer.valueOf(m2ndRawDataInfo.wbB));
                }
                if (need3rdShooting) {
                    mSimDegree2.offer(Integer.valueOf(param.getSADegree2()));
                    mSimCoeff2.offer(Integer.valueOf(param.getStrength2() + 1));
                    Point point2 = param.getSAPoint2(m3rdRawDataInfo.validSizeX, m3rdRawDataInfo.validSizeY, aspectRatio);
                    mSimX2.offer(Integer.valueOf(point2.x));
                    mSimY2.offer(Integer.valueOf(point2.y));
                } else {
                    mSimDegree2.offer(0);
                    mSimCoeff2.offer(0);
                    mSimX2.offer(0);
                    mSimY2.offer(0);
                }
            }
        }
    }

    private void setStoreImageCompleteListener() {
        CameraEx.StoreImageCompleteListener storeImageListner = new CameraEx.StoreImageCompleteListener() { // from class: com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess.7
            private boolean is1stStoreImage() {
                return GFCompositProcess.mCurrentStoreImage == GFCompositProcess.STORE_IMAGE_1ST;
            }

            private boolean is2ndStoreImage() {
                return GFCompositProcess.mCurrentStoreImage == GFCompositProcess.STORE_IMAGE_2ND;
            }

            private boolean is3rdStoreImage() {
                return GFCompositProcess.mCurrentStoreImage == GFCompositProcess.STORE_IMAGE_3RD;
            }

            private boolean isCompositStoreImage() {
                return GFCompositProcess.mCurrentStoreImage == -1;
            }

            public void onDone(int id, CameraEx.StoreImageInfo info, CameraEx cameraEx) {
                String gammaMode;
                boolean writeCmd = false;
                synchronized (GFCompositProcess.LinkedListObj) {
                    if (isCompositStoreImage()) {
                        int unused = GFCompositProcess.mCurrentStoreImage = GFCompositProcess.STORE_IMAGE_1ST;
                        writeCmd = true;
                    } else if (is1stStoreImage()) {
                        String unused2 = GFCompositProcess.mSimFiles0 = info.FileName + GFCompositProcess.SIM_RAW_SUFFIX;
                        int unused3 = GFCompositProcess.mCurrentStoreImage = GFCompositProcess.STORE_IMAGE_2ND;
                    } else if (is2ndStoreImage()) {
                        String unused4 = GFCompositProcess.mSimFiles1 = info.FileName + GFCompositProcess.SIM_RAW_SUFFIX;
                        if (GFCompositProcess.need3rdShooting) {
                            int unused5 = GFCompositProcess.mCurrentStoreImage = GFCompositProcess.STORE_IMAGE_3RD;
                        } else {
                            int unused6 = GFCompositProcess.mCurrentStoreImage = -1;
                            String unused7 = GFCompositProcess.mSimFiles2 = null;
                        }
                    } else if (is3rdStoreImage()) {
                        String unused8 = GFCompositProcess.mSimFiles2 = info.FileName + GFCompositProcess.SIM_RAW_SUFFIX;
                        int unused9 = GFCompositProcess.mCurrentStoreImage = -1;
                    }
                    if (writeCmd) {
                        if (GFClippingCorrectionController.getInstance().getValue(null).equals(GFClippingCorrectionController.NORMAL)) {
                            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/GAMMA0.txt");
                            if (file.exists()) {
                                gammaMode = "61/SDCARD/GAMMA0.txt    61/SDCARD/IGAMMA0.txt    GRAD3.00 ";
                            } else {
                                gammaMode = "61/KNEE/GAMMA0.txt      61/KNEE/IGAMMA0.txt      GRAD3.00 ";
                            }
                        } else if (GFClippingCorrectionController.getInstance().getValue(null).equals(GFClippingCorrectionController.LOW)) {
                            File file2 = new File(Environment.getExternalStorageDirectory().getPath() + "/GAMMA1.txt");
                            if (file2.exists()) {
                                gammaMode = "61/SDCARD/GAMMA1.txt    61/SDCARD/IGAMMA1.txt    GRAD3.00 ";
                            } else {
                                gammaMode = "61/KNEE_WC/GAMMA1.txt   61/KNEE_WC/IGAMMA1.txt   GRAD3.00 ";
                            }
                        } else {
                            File file3 = new File(Environment.getExternalStorageDirectory().getPath() + "/GAMMA2.txt");
                            if (file3.exists()) {
                                gammaMode = "61/SDCARD/GAMMA2.txt    61/SDCARD/IGAMMA2.txt    GRAD3.00 ";
                            } else {
                                gammaMode = "61/LINEAR/GAMMA2.txt    61/LINEAR/IGAMMA2.txt    GRAD3.00 ";
                            }
                        }
                        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                        builder.replace(0, builder.length(), GFCompositProcess.SIM_CMD);
                        builder.append(" ").append(GFCompositProcess.mSimFiles0).append(" ").append(GFCompositProcess.mSimWbR0.poll()).append(" ").append(GFCompositProcess.mSimWbB0.poll()).append(" ").append(GFCompositProcess.mSimFiles1).append(" ").append(GFCompositProcess.mSimWbR1.poll()).append(" ").append(GFCompositProcess.mSimWbB1.poll()).append(" ").append(GFCompositProcess.mSimFiles2).append(" ").append(GFCompositProcess.mSimWbR2.poll()).append(" ").append(GFCompositProcess.mSimWbB2.poll()).append(" ").append(GFCompositProcess.mSimWbRout.poll()).append(" ").append(GFCompositProcess.mSimWbBout.poll()).append(" ").append(GFCompositProcess.mSimCanvasX.poll()).append(" ").append(GFCompositProcess.mSimCanvasY.poll()).append(" ").append(GFCompositProcess.mSimMarginOffsetX.poll()).append(" ").append(GFCompositProcess.mSimMarginOffsetY.poll()).append(" ").append(GFCompositProcess.mSimMarginX.poll()).append(" ").append(GFCompositProcess.mSimMarginY.poll()).append(" ").append(GFCompositProcess.mSimValidOffsetX.poll()).append(" ").append(GFCompositProcess.mSimValidOffsetY.poll()).append(" ").append(GFCompositProcess.mSimValidX.poll()).append(" ").append(GFCompositProcess.mSimValidY.poll()).append(" ").append(GFCompositProcess.mSimX1.poll()).append(" ").append(GFCompositProcess.mSimY1.poll()).append(" ").append(GFCompositProcess.mSimDegree1.poll()).append(" ").append(GFCompositProcess.mSimCoeff1.poll()).append(" ").append(GFCompositProcess.mSimX2.poll()).append(" ").append(GFCompositProcess.mSimY2.poll()).append(" ").append(GFCompositProcess.mSimDegree2.poll()).append(" ").append(GFCompositProcess.mSimCoeff2.poll()).append(" ").append(GFCompositProcess.m1stShot.poll()).append(" ").append(GFCompositProcess.m2ndShot.poll()).append(" ").append(GFCompositProcess.m3rdShot.poll()).append(" ").append(gammaMode).append(" ").append(GFCompositProcess.SIM_OUT_PREFIX);
                        String simCmd = builder.toString();
                        AppLog.info(GFCompositProcess.TAG, simCmd);
                        StringBuilderThreadLocal.releaseScratchBuilder(builder);
                        FileWriter simFw = null;
                        try {
                            FileWriter simFw2 = new FileWriter(GFCompositProcess.debugSimFileName, true);
                            simFw = simFw2;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(GFCompositProcess.TAG, "file open error");
                        }
                        if (simFw != null) {
                            try {
                                simFw.write(simCmd);
                            } catch (IOException e2) {
                                Log.d(GFCompositProcess.TAG, "file access error");
                                e2.printStackTrace();
                            }
                            try {
                                simFw.close();
                            } catch (IOException e3) {
                                Log.d(GFCompositProcess.TAG, "file close error");
                                e3.printStackTrace();
                            }
                        }
                        StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                        builder2.replace(0, builder2.length(), GFCompositProcess.CPY_CMD).append(" ").append(info.FileName).append(GFCompositProcess.SIM_RAW_SUFFIX).append(" ").append(GFCompositProcess.CPY_DST);
                        String cpyCmd = builder2.toString();
                        StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                        FileWriter cpyFw = null;
                        try {
                            FileWriter cpyFw2 = new FileWriter(GFCompositProcess.debugCopyFileName, true);
                            cpyFw = cpyFw2;
                        } catch (Exception e4) {
                            e4.printStackTrace();
                            Log.d(GFCompositProcess.TAG, "file open error");
                        }
                        if (cpyFw != null) {
                            try {
                                cpyFw.write(cpyCmd);
                            } catch (IOException e5) {
                                Log.d(GFCompositProcess.TAG, "file access error");
                                e5.printStackTrace();
                            }
                            try {
                                cpyFw.close();
                            } catch (IOException e6) {
                                Log.d(GFCompositProcess.TAG, "file close error");
                                e6.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        mCameraEx.setStoreImageCompleteListener(storeImageListner);
    }

    private void removeStoreImageCompleteListener() {
        mCameraEx.setStoreImageCompleteListener((CameraEx.StoreImageCompleteListener) null);
        mSimWbR0 = null;
        mSimWbB0 = null;
        mSimWbR1 = null;
        mSimWbB1 = null;
        mSimWbR2 = null;
        mSimWbB2 = null;
        mSimWbRout = null;
        mSimWbBout = null;
        mSimCanvasX = null;
        mSimCanvasY = null;
        mSimMarginOffsetX = null;
        mSimMarginOffsetY = null;
        mSimMarginX = null;
        mSimMarginY = null;
        mSimValidOffsetX = null;
        mSimValidOffsetY = null;
        mSimValidX = null;
        mSimValidY = null;
        mSimX1 = null;
        mSimY1 = null;
        mSimX2 = null;
        mSimY2 = null;
        mSimDegree1 = null;
        mSimDegree2 = null;
        mSimCoeff1 = null;
        mSimCoeff2 = null;
        m1stShot = null;
        m2ndShot = null;
        m3rdShot = null;
    }

    public static synchronized void storeCompositImageMain() {
        synchronized (GFCompositProcess.class) {
            if (GFConstants.NDSA_MULTI_UNIT) {
                NDSA2Multi.getInstance().update();
                NDSA2Multi.getInstance().close();
            } else {
                NDSA2.getInstance().update();
                NDSA2.getInstance().close();
            }
            if (debugOutputSimFile && ((isRaw || isRawJpeg) && !needMediaSetting && isCompositShooting() && mComUtil.isSupportedVersion(2, 7))) {
                offerDumpParameters();
            }
            if (!debugAlgo) {
                if (m1stRaw != null) {
                    m1stRaw.release();
                    m1stRaw = null;
                }
                if (m2ndRaw != null) {
                    m2ndRaw.release();
                    m2ndRaw = null;
                }
                if (m3rdRaw != null) {
                    m3rdRaw.release();
                    m3rdRaw = null;
                }
                m1stRawDataInfo = null;
                m2ndRawDataInfo = null;
                m3rdRawDataInfo = null;
            }
            if (mCompositRaw != null) {
                isStoredCompositImage1 = debugAlgo ? false : true;
                storeCompositImage(mCompositRaw);
                if (!debugAlgo) {
                    CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                }
                GFKikiLogUtil.getInstance().countShots();
                ((GFExecutorCreator) GFExecutorCreator.getInstance()).incrementShootingNumber();
            }
            if (debugAlgo) {
                if (need3rdShooting) {
                    NDSA.getInstance().open(mDSP0, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m3rdRawDataInfo.axiRawAddr, 0);
                    NDSA.getInstance().execute();
                    NDSA.getInstance().close();
                    DeviceBuffer m1st2ndRaw = copyRawData(null, mCompositRaw, m3rdRawDataInfo);
                    NDSA.getInstance().open(mDSP0, m1st2ndRaw, m2ndRawDataInfo, m3rdRaw, m3rdRawDataInfo, mCompositRaw, m3rdRawDataInfo.axiRawAddr, 1);
                    NDSA.getInstance().execute();
                    NDSA.getInstance().close();
                    m1st2ndRaw.release();
                } else {
                    NDSA.getInstance().open(mDSP0, m1stRaw, m1stRawDataInfo, m2ndRaw, m2ndRawDataInfo, mCompositRaw, m2ndRawDataInfo.axiRawAddr, 0);
                    NDSA.getInstance().execute();
                    NDSA.getInstance().close();
                }
                if (m1stRaw != null) {
                    m1stRaw.release();
                    m1stRaw = null;
                }
                if (m2ndRaw != null) {
                    m2ndRaw.release();
                    m2ndRaw = null;
                }
                if (m3rdRaw != null) {
                    m3rdRaw.release();
                    m3rdRaw = null;
                }
                m1stRawDataInfo = null;
                m2ndRawDataInfo = null;
                m3rdRawDataInfo = null;
                if (mCompositRaw != null) {
                    isStoredCompositImage1 = true;
                    storeCompositImage(mCompositRaw);
                    CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                    ((GFExecutorCreator) GFExecutorCreator.getInstance()).incrementShootingNumber();
                }
            }
        }
    }

    public static synchronized void storeCompositImage() {
        synchronized (GFCompositProcess.class) {
            if (debugOutputFile) {
                recordLog();
            }
            if (isStoredCompositImage1 || isStoredCompositImage2) {
                AppLog.warning(TAG, "storeCompositImage() is already called.");
            } else {
                isStoredCompositImage2 = true;
                storeCompositImageMain();
                if (mDSP0 != null) {
                    mDSP0.release();
                    mDSP0 = null;
                }
                if (mDSP1 != null) {
                    mDSP1.release();
                    mDSP1 = null;
                }
                if (mDSP2 != null) {
                    mDSP2.release();
                    mDSP2 = null;
                }
                int runStatus = RunStatus.getStatus();
                if (2 == runStatus) {
                    mAdapter.enableHalt(true);
                } else {
                    enableNextCapture(0);
                }
            }
        }
    }

    public static CameraSequence.RawData getCompositRaw() {
        return mCompositRaw;
    }

    private void setMemoryMapConfiguration() {
        if (mComUtil.isSupportedVersion(2, 0)) {
            MemoryMapConfig.setAllocationPolicy(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void enableNextCapture(int status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        GFCommonUtil.getInstance().setAutoWhiteBalanceLock(false);
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        ExecutorCreator.getInstance().getSequence();
        if (1 != ExecutorCreator.getInstance().getSequence().getShutterType()) {
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
        }
        TouchLessShutterController.ExposingByTouchLessShutter = false;
        mCameraEx.cancelTakePicture();
        mCamera.cancelAutoFocus();
        mAdapter.enableNextCapture(status);
        if (mSelfTimerRunnableTask != null) {
            mSelfTimerRunnableTask.removeCallbacks();
            mSelfTimerRunnableTask = null;
        }
        if (mWatchDogTimerForMaybePhaseDiffRunnableTask != null) {
            mComUtil.setMaybePhaseDiffFlag(false);
            mWatchDogTimerForMaybePhaseDiffRunnableTask.removeCallbacks();
            mWatchDogTimerForMaybePhaseDiffRunnableTask = null;
        }
        if (isRemoteControlMode) {
            setRemoteControlMode(true);
        }
        if (needAELockSetting || mComUtil.isCancelByLensChanging()) {
            AELController.getInstance().holdAELock(false);
        }
        mComUtil.setDuringSelfTimer(false);
        if (mComUtil.hasIrisRing()) {
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
        if (mComUtil.needActualMediaSetting()) {
            mComUtil.setActualMediaIds(mCameraEx, null);
        }
        mAdapter.enableHalt(true);
        restoreApscSetting(mApscSetting);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.ENABLE_NEXT_CAPTURE);
        int borderId = 0;
        if (GFEEAreaController.getInstance().isLayer3()) {
            borderId = 1;
        }
        GFCommonUtil.getInstance().setBorderId(borderId);
        GFCommonUtil.getInstance().setCameraSettingsLayerDuringShots(-1);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private static void setRemoteControlMode(boolean mode) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = mCamSet.getEmptyParameters();
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
        if (tag.equals(GFConstants.CANCELTAKEPICTURE)) {
            cancelTakePicture = true;
        }
    }
}
