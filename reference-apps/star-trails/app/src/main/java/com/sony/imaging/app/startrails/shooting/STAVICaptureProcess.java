package com.sony.imaging.app.startrails.shooting;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.AviExporter;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.startrails.StarTrails;
import com.sony.imaging.app.startrails.UtilPFWorkaround;
import com.sony.imaging.app.startrails.base.menu.controller.STPictureEffectController;
import com.sony.imaging.app.startrails.menu.controller.MovieController;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;
import com.sony.imaging.app.startrails.metadatamanager.StillSAExec;
import com.sony.imaging.app.startrails.metadatamanager.YuvToRgbConversion;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.MiniatureImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.graphics.imagefilter.SoftFocusImageFilter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class STAVICaptureProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    private static final int MINIMUM_NUMBER_SHOT_LIMIT = 30;
    private boolean storeThumbnail;
    public static boolean isIn_onShutterSequence = false;
    private static String[] TAGS = {STConstants.REMOVE_CAPTURE_CALLBACK, STConstants.CAPTURING_STARTED, CameraNotificationManager.AE_LOCK_BUTTON};
    private final String TAG = "STAVICaptureProcessTest";
    private IAdapter mAdapter = null;
    private CameraEx mCameraEx = null;
    private CameraSequence.DefaultDevelopFilter devFilter = null;
    private ScaleImageFilter mScaledImageFilter = null;
    private AviExporter aviExporter = null;
    private boolean isMovieSize_1920_1080 = false;
    private YuvToRgbConversion yuvConv = null;
    private boolean isMiniatureEffectSet = false;
    private MiniatureImageFilter minFilter = null;
    private boolean isSoftFocusSet = false;
    private SoftFocusImageFilter mSoftFocusImageFilter = null;
    private DSP mYuvToRGBDsp = null;
    OptimizedImage img = null;
    private CameraSequence mSequence = null;
    CreativeStyleController.CreativeStyleOptions mCreativeStyleBackupObject = null;
    private int mCounter = 0;
    private int mMiniatureId = 3;
    private int mSoftFocusEffectId = 3;
    private STUtility stUtility = null;
    private ThemeParameterSettingUtility mParamUtil = null;
    private RunnableTask mRunnableTask = null;
    private CameraEx.AutoPictureReviewControl mAutoPicRevCtl = null;
    private int bitmapDisplayInterval = 2;
    private int bitmapDisplayFirstPosition = 2;
    private boolean isDisplay = false;
    private Handler myHandler = null;
    private Handler mTaskHandler = null;
    private boolean isFirstTakePicture = false;
    private boolean isSelfTimerOffFirstTakePicture = false;
    private String mPictureEffect = null;
    private boolean isRequestCancelTakePicture = false;
    private CropImageFilter mCropImageFilter = null;
    int mAspect = ScalarProperties.getInt("device.panel.aspect");
    int mStatus = 2;

    public DSP getYuvToRGBDsp() {
        return this.mYuvToRGBDsp;
    }

    public void createYuvToRGBDsp() {
        try {
            if (this.mYuvToRGBDsp == null) {
                this.mYuvToRGBDsp = DSP.createProcessor("sony-di-dsp");
                this.mYuvToRGBDsp.setProgram(STUtility.getInstance().getFilePathForYuvToRGBConv());
            }
        } catch (Exception e) {
            this.mYuvToRGBDsp.release();
            this.mYuvToRGBDsp = null;
        }
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
            if (this.stUtility == null) {
                this.stUtility = STUtility.getInstance();
            }
            setAutoReviewControl();
        } catch (Exception e) {
            Log.d("STAVICaptureProcessTest", e.toString());
        }
    }

    private void setMovieQualityFactor() {
        try {
            String tag = MovieController.getInstance().getValue(null);
            if (STConstants.MOVIE_1920_1080.equals(tag)) {
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
        if (this.mAdapter != null) {
            CameraSequence.Options opt = new CameraSequence.Options();
            STUtility.getInstance().setMemoryMapConfiguration();
            opt.setOption("MEMORY_MAP_FILE", STUtility.getInstance().getFilePathForSpecialSequence());
            opt.setOption("EXPOSURE_COUNT", 1);
            opt.setOption("RECORD_COUNT", 1);
            opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
            opt.setOption("AUTO_RELEASE_LOCK_ENABLED", false);
            this.mAdapter.setOptions(opt);
        }
        initialize();
        StillSAExec.getInstance().open();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.enter("STAVICaptureProcessTest", "*************TERMINATE   ");
        ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(this.mCounter);
        handleCameraOnTerminate();
        StillSAExec.getInstance().terminate();
        this.mCameraEx = null;
        this.mAdapter = null;
        this.mAutoPicRevCtl = null;
        AppLog.exit("STAVICaptureProcessTest", "*************TERMINATE    ");
    }

    private void handleCameraOnTerminate() {
        AppLog.enter("STAVICaptureProcessTest", "*************TERMINATE    ");
        if (!this.isRequestCancelTakePicture) {
            this.isRequestCancelTakePicture = true;
            if (!isIn_onShutterSequence) {
                AppLog.trace("STAVICaptureProcessTest", "****************************************************CancelRunnable");
                if (this.mAdapter != null) {
                    this.mAdapter.enableNextCapture(1);
                }
            }
        }
        enableNextPictureSetting();
        AppLog.exit("STAVICaptureProcessTest", "*************TERMINATE    ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableNextPictureSetting() {
        setPostTakePictureSetting();
        releaseMemory();
        ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(this.mCounter);
    }

    private void createRunnableTask() {
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask(this.mTaskHandler);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        this.mStatus = status;
        AppLog.trace("STAVICaptureProcessTest", "onShutter status =  " + status);
        if (status != 0) {
            AppLog.enter("STAVICaptureProcessTest", "*************onShutter  ShutterListener.STATUS_OK not OK ");
            if (this.mCounter >= 1) {
                this.mCounter--;
            }
            ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(this.mCounter);
            Log.d("STAVICaptureProcessTest", "enableNextCapture in onShutter");
            this.mAdapter.enableNextCapture(status);
            enableNextPictureSetting();
            return;
        }
        AppLog.exit("STAVICaptureProcessTest", "*************onShutter  ShutterListener.STATUS_OK OK ");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        Log.d("STAVICaptureProcessTest", "onShutterSequence********************* start");
        isIn_onShutterSequence = true;
        this.mSequence = sequence;
        if (this.stUtility != null) {
            processTask(raw, sequence);
        }
        System.gc();
        isIn_onShutterSequence = false;
        Log.d("STAVICaptureProcessTest", "onShutterSequence********************* end");
    }

    private void preTakePictureSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter("STAVICaptureProcessTest", AppLog.getMethodName());
        this.devFilter.setSource(raw, true);
        this.devFilter.execute();
        this.img = this.devFilter.getOutput();
        this.devFilter.release();
        if (this.isMiniatureEffectSet) {
            this.img = getMiniatureOptimizedImage(this.img);
        } else if (this.isSoftFocusSet) {
            this.img = getSoftFocusOptimizedImage(this.img);
        }
        sequence.storeImage(this.img, true);
        printTestShotKikiLog();
        AppLog.exit("STAVICaptureProcessTest", AppLog.getMethodName());
    }

    private void printTestShotKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4242;
        Kikilog.setUserLog(kikilogId.intValue(), options);
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
        reinitializeObject();
        setMovieQualityFactor();
        createRunnableTask();
        if (!STUtility.getInstance().isPreTakePictureTestShot()) {
            isSaveBitmap();
        }
        createMovieShootingObjects();
        STConstants.sFrameBitmapList.clear();
        StillSAExec.getInstance().initialize();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        setCameraSetting(true);
    }

    private void reinitializeObject() {
        this.mCounter = 0;
        this.isFirstTakePicture = false;
        this.isSelfTimerOffFirstTakePicture = true;
        this.bitmapDisplayFirstPosition = 2;
        this.isMiniatureEffectSet = false;
        this.isSoftFocusSet = false;
        this.storeThumbnail = true;
        this.isRequestCancelTakePicture = false;
        STConstants.sCaptureImageCounter = this.mCounter;
        if (this.mParamUtil == null) {
            this.mParamUtil = ThemeParameterSettingUtility.getInstance();
        }
        if (this.stUtility == null) {
            this.stUtility = STUtility.getInstance();
        }
        if (STSelfTimerMenuController.getInstance().isSelfTimer()) {
            this.isFirstTakePicture = true;
        }
    }

    private void isSaveBitmap() {
        this.isDisplay = false;
        if (this.mParamUtil == null) {
            this.mParamUtil = ThemeParameterSettingUtility.getInstance();
        }
        if (this.mParamUtil.getNumberOfShot() >= 30) {
            this.isDisplay = true;
            this.bitmapDisplayInterval = this.mParamUtil.getNumberOfShot() / 5;
            createYuvToRGBDsp();
            if (this.yuvConv != null && this.mYuvToRGBDsp != null) {
                this.yuvConv = new YuvToRgbConversion(this.mYuvToRGBDsp);
            }
        }
    }

    private void setPostTakePictureSetting() {
        this.isRequestCancelTakePicture = true;
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        setCameraSetting(false);
        STConstants.sCaptureImageCounter = 0;
        ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(STConstants.sCaptureImageCounter);
        if (this.isMiniatureEffectSet || this.isSoftFocusSet) {
            CameraSetting.getInstance().getEmptyParameters();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setPictureEffect(this.mPictureEffect);
            if (this.mCreativeStyleBackupObject != null) {
                setCreativeStyleStdValue(this.mCreativeStyleBackupObject);
            }
            CameraSetting.getInstance().setParameters(params);
        }
        this.isMiniatureEffectSet = false;
        this.isSoftFocusSet = false;
        String arg0 = AvindexStore.getExternalMediaIds()[0];
        AvindexStore.calculateAvailableSize(arg0);
    }

    private void setCameraSetting(boolean isCamSetting) {
        AELController.getInstance().holdAELock(isCamSetting);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = null;
        if (this.mCameraEx != null) {
            params = CameraSetting.getInstance().getEmptyParameters();
        } else {
            Log.i("STAVICaptureProcessTest", "Camera settings instance not available ");
        }
        if (params != null && "auto".equals(WhiteBalanceController.getInstance().getValue())) {
            ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(isCamSetting);
            CameraSetting.getInstance().setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        if (this.isSelfTimerOffFirstTakePicture) {
            if (!this.mAdapter.enableHalt(true)) {
                if (this.isFirstTakePicture) {
                    this.mRunnableTask.execute(STConstants.CAPTURE_DELAY_TIME);
                    return;
                } else {
                    this.mRunnableTask.execute(getDelayTime());
                    return;
                }
            }
            return;
        }
        if (this.mCameraEx != null) {
            this.mAdapter.enableHalt(false);
            this.mCameraEx.cancelTakePicture();
            this.mCameraEx.burstableTakePicture();
            updateCaptureStatus();
        }
    }

    private int getDelayTime() {
        int shutterSpeed;
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss != null) {
            shutterSpeed = (((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue();
        } else {
            shutterSpeed = 0;
        }
        if (shutterSpeed < 1000) {
            int wait_time = shutterSpeed + IntervalRecExecutor.INTVL_REC_INITIALIZED + 160 + 80;
            return wait_time;
        }
        return 500;
    }

    private void updateCaptureStatus() {
        STUtility.getInstance().setCaptureStatus(true);
        STConstants.sCaptureImageCounter = this.mCounter + 1;
        ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(STConstants.sCaptureImageCounter);
        CameraNotificationManager.getInstance().requestNotify(STConstants.TIMER_UPDATE);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
    }

    private void createAviExporterOptionData() {
        AviExporter.Options options = new AviExporter.Options();
        if (this.isMovieSize_1920_1080) {
            options.width = 1920;
            options.height = 1080;
        } else {
            options.width = 1280;
            options.height = 720;
        }
        if (this.mParamUtil.getRecordingMode() == 0) {
            options.fps = 24;
        } else {
            options.fps = 30;
        }
        options.isAdobeRGB = AvailableInfo.isFactor("INH_FACTOR_CAM_SET_STL_COLOR_SPACE_EXPANDED_TYPE_C");
        options.fullPathFileName = ((STExecutorCreator) STExecutorCreator.getInstance()).getStarTrailsBO().getFullPathFileName();
        options.libpath = STConstants.LIB_PATH;
        if (this.aviExporter == null) {
            this.aviExporter = new AviExporter();
        }
        try {
            this.aviExporter.open(options);
        } catch (Exception e) {
            Log.d("STAVICaptureProcessTest", e.toString());
        }
    }

    private void releaseOptimizedImage(OptimizedImage optImage) {
        if (optImage != null) {
            optImage.release();
        }
    }

    private void releaseMemory() {
        Log.d("STAVICaptureProcessTest", "*******************Release memory start");
        if (this.mRunnableTask != null) {
            this.mRunnableTask.removeCallbacks();
            this.mRunnableTask = null;
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
        if (this.mSoftFocusImageFilter != null) {
            this.mSoftFocusImageFilter.release();
            this.mSoftFocusImageFilter = null;
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
                Log.d("STAVICaptureProcessTest", e.toString());
            }
            this.aviExporter = null;
        }
        this.isFirstTakePicture = false;
        this.isSelfTimerOffFirstTakePicture = false;
        System.gc();
        Log.d("STAVICaptureProcessTest", "*******************Release memory end");
    }

    private void createMovieShootingObjects() {
        if (this.devFilter == null) {
            this.devFilter = new CameraSequence.DefaultDevelopFilter();
        }
        if (!STUtility.getInstance().isPreTakePictureTestShot()) {
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
        if (this.stUtility.isSilenShutterOFF() && this.stUtility.getCurrentTrail() == 2) {
            String picEffect = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, "off");
            if (PictureEffectController.MODE_MINIATURE.equals(picEffect)) {
                setMiniatureID(STPictureEffectController.getInstance().getValue(PictureEffectController.MODE_MINIATURE));
            } else if (PictureEffectController.MODE_SOFT_FOCUS.equals(picEffect)) {
                setSoftFocusID(STPictureEffectController.getInstance().getValue(PictureEffectController.MODE_SOFT_FOCUS));
            }
            if (this.minFilter == null && this.isMiniatureEffectSet) {
                setPictureEffectOff();
                this.minFilter = new MiniatureImageFilter();
                return;
            } else {
                if (this.mSoftFocusImageFilter == null && this.isSoftFocusSet) {
                    setPictureEffectOff();
                    this.mSoftFocusImageFilter = new SoftFocusImageFilter();
                    return;
                }
                return;
            }
        }
        Log.d("STAVICaptureProcessTest", "*******************Picture Effect is off so no requirment of SA Effect");
    }

    private void setPictureEffectOff() {
        this.mPictureEffect = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, "off");
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setPictureEffect("off");
        CameraSetting.getInstance().setParameters(params);
        setCreativeStyle();
    }

    private void setCreativeStyle() {
        CreativeStyleController controller = CreativeStyleController.getInstance();
        if (!"standard".equals(controller.getValue(CreativeStyleController.CREATIVESTYLE))) {
            this.mCreativeStyleBackupObject = (CreativeStyleController.CreativeStyleOptions) controller.getDetailValue("standard");
        }
        CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) controller.getDetailValue();
        param.contrast = 0;
        param.saturation = 0;
        param.sharpness = 0;
        setCreativeStyleStdValue(param);
    }

    private void setCreativeStyleStdValue(CreativeStyleController.CreativeStyleOptions param) {
        CreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, "standard");
        CreativeStyleController.getInstance().setDetailValue(param);
    }

    private void setMiniatureID(String pictureEffect) {
        this.isMiniatureEffectSet = true;
        if (PictureEffectController.MINIATURE_LEFT.equals(pictureEffect)) {
            this.mMiniatureId = 3;
            return;
        }
        if (PictureEffectController.MINIATURE_HCENTER.equals(pictureEffect)) {
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
            return;
        }
        if (PictureEffectController.MINIATURE_RIGHT.equals(pictureEffect)) {
            this.mMiniatureId = 4;
        } else if ("auto".equals(pictureEffect)) {
            this.mMiniatureId = 1;
        } else {
            this.isMiniatureEffectSet = false;
        }
    }

    private void setSoftFocusID(String pictureEffect) {
        this.isSoftFocusSet = true;
        if (PictureEffectController.SOFT_FOCUS_HIGH.equals(pictureEffect)) {
            this.mSoftFocusEffectId = 3;
            return;
        }
        if (PictureEffectController.SOFT_FOCUS_MEDIUM.equals(pictureEffect)) {
            this.mSoftFocusEffectId = 2;
        } else if (PictureEffectController.SOFT_FOCUS_LOW.equals(pictureEffect)) {
            this.mSoftFocusEffectId = 1;
        } else {
            this.isSoftFocusSet = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RunnableTask {
        private Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.startrails.shooting.STAVICaptureProcess.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    STAVICaptureProcess.this.isFirstTakePicture = false;
                    String focusMode = STAVICaptureProcess.this.getFocusMode();
                    if (!FocusModeController.MANUAL.equalsIgnoreCase(focusMode)) {
                        STAVICaptureProcess.this.isRequestCancelTakePicture = true;
                    }
                    if (STUtility.getInstance().getCaptureStatus() && !STAVICaptureProcess.this.isRequestCancelTakePicture && !STAVICaptureProcess.this.stUtility.isDetachedLens()) {
                        STAVICaptureProcess.this.takePicture();
                        return;
                    }
                    Log.d("kamata", "cancel 1");
                    Log.d("STAVICaptureProcessTest", "enableNextCapture in ProcessTask");
                    STAVICaptureProcess.this.mAdapter.enableNextCapture(1);
                    STAVICaptureProcess.this.enableNextPictureSetting();
                } catch (Exception e) {
                    Log.d("STAVICaptureProcessTest", "Exception occured while taking burstable picture");
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
        Log.d("STAVICaptureProcessTest", "crop: original hegith : " + origHeight + " width : " + origWidth);
        double formal = (origWidth / 16.0f) * 9.0f;
        Log.d("STAVICaptureProcessTest", "crop: hegith " + formal);
        int height = ((int) ((formal / 8.0d) + 0.5d)) * 8;
        Log.d("STAVICaptureProcessTest", "crop: hegith " + height);
        int top = (origHeight - height) / 2;
        int top2 = top + (top % 4);
        int right = 0 + origWidth;
        int bottom = top2 + height;
        if (bottom > origHeight) {
            bottom = origHeight;
        }
        Log.d("STAVICaptureProcessTest", "crop: left 0: top " + top2 + ": right " + right + ": bottom " + bottom);
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

    private OptimizedImage storeFrame_Thumbnail(OptimizedImage img) {
        OptimizedImage optImage;
        OptimizedImage scaledImage = null;
        if (this.isMovieSize_1920_1080) {
            optImage = getScaledImage(img, 1920, 1080);
        } else {
            optImage = getScaledImage(img, 1280, 720);
        }
        if (optImage != null) {
            StillSAExec.getInstance().execute(optImage);
        } else {
            AppLog.info("STAVICaptureProcessTest", " optImage" + optImage);
        }
        releaseOptimizedImage(img);
        if (this.storeThumbnail) {
            createAviExporterOptionData();
            if (!this.aviExporter.storeThumbnail(optImage, false)) {
                this.mCounter = 0;
                ((STExecutorCreator) STExecutorCreator.getInstance()).setCounter(this.mCounter);
                this.isRequestCancelTakePicture = true;
            }
            this.storeThumbnail = false;
        }
        try {
            if (!this.aviExporter.storeFrame(optImage, false)) {
                this.isRequestCancelTakePicture = true;
            }
        } catch (Exception e) {
            this.isRequestCancelTakePicture = true;
        }
        if (checkBmpPosition(this.mCounter)) {
            scaledImage = getScaled_3_4_Image(optImage, 160, 90);
        }
        releaseOptimizedImage(optImage);
        return scaledImage;
    }

    private void processTask(CameraSequence.RawData raw, CameraSequence sequence) {
        OptimizedImage scaledImg = null;
        if (this.stUtility.isPreTakePictureTestShot()) {
            if (UtilPFWorkaround.isHighSpeedLensPowerOffWorkaroundNeeded && StarTrails.isIn_onShutdown) {
                throw new IllegalStateException("Reboot Camera to avoid freeze.(IMDLAPP12-465)");
            }
            preTakePictureSequence(raw, sequence);
        } else {
            isIn_onShutterSequence = false;
            try {
                this.mCounter = STConstants.sCaptureImageCounter;
                STUtility.getInstance().calculateRemainingMemory();
                if (STUtility.getInstance().getAvailableRemainingShot() <= 2) {
                    this.isRequestCancelTakePicture = true;
                }
                if (RunStatus.getStatus() == 2) {
                    this.isRequestCancelTakePicture = true;
                }
                this.devFilter.setSource(raw, true);
                this.devFilter.execute();
                OptimizedImage img = this.devFilter.getOutput();
                this.devFilter.clearSources();
                if (this.isMiniatureEffectSet) {
                    img = getMiniatureOptimizedImage(img);
                } else if (this.isSoftFocusSet) {
                    img = getSoftFocusOptimizedImage(img);
                }
                this.mSequence.storeImage(img, false);
                if (this.aviExporter == null) {
                    this.aviExporter = new AviExporter();
                }
                OptimizedImage optImage = get16to9CroppedImage(img);
                if (optImage != null) {
                    releaseOptimizedImage(img);
                    scaledImg = storeFrame_Thumbnail(optImage);
                    STUtility.getInstance().calculateRemainingMemory();
                    if (STUtility.getInstance().getAvailableRemainingShot() <= 0) {
                        Log.d("STAVICaptureProcessTest", "enableNextCapture memory full");
                        this.isRequestCancelTakePicture = true;
                    }
                }
            } catch (Exception e) {
                this.isRequestCancelTakePicture = true;
                Log.d("STAVICaptureProcessTest", "*************** processTask exception*******************");
            }
        }
        releaseRawData(raw);
        releaseOptimizedImage(this.img);
        String focusMode = getFocusMode();
        if (!FocusModeController.MANUAL.equalsIgnoreCase(focusMode)) {
            this.isRequestCancelTakePicture = true;
        }
        if (this.mParamUtil != null && this.mCounter < this.mParamUtil.getNumberOfShot() && !this.stUtility.isPreTakePictureTestShot()) {
            if (scaledImg != null) {
                addBitmapinList(scaledImg);
            }
            if (this.mStatus == 0 && this.mCounter < this.mParamUtil.getNumberOfShot() && !this.isRequestCancelTakePicture && !this.stUtility.isDetachedLens()) {
                takePicture();
                Log.d("kamata", "takePicture");
                Log.d("STAVICaptureProcessTest", "takePicture in ProcessTask");
            }
            if (this.isRequestCancelTakePicture || this.stUtility.isDetachedLens()) {
                Log.d("kamata", "cancel 2");
                Log.d("STAVICaptureProcessTest", "enableNextCapture in ProcessTask");
                this.mAdapter.enableNextCapture(1);
                enableNextPictureSetting();
                return;
            }
            return;
        }
        releaseOptimizedImage(scaledImg);
        Log.d("STAVICaptureProcessTest", "****************************************************Process task 1 image");
        this.isRequestCancelTakePicture = true;
        enableNextPictureSetting();
        if (this.mAdapter != null) {
            this.mAdapter.enableNextCapture(this.mStatus);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getFocusMode() {
        Log.d("STAVICaptureProcessTest", "Focus mode == " + FocusModeController.MANUAL);
        if (this.mCameraEx == null) {
            return FocusModeController.MANUAL;
        }
        try {
            String focusMode = this.mCameraEx.getNormalCamera().getParameters().getFocusMode();
            return focusMode;
        } catch (Exception e) {
            Log.d("STAVICaptureProcessTest", "mCameraEx.getNormalCamera().getParameters() not available");
            return FocusModeController.MANUAL;
        }
    }

    private void addBitmapinList(OptimizedImage optImage) {
        if (STConstants.sFrameBitmapList.size() < 5 && this.yuvConv != null) {
            Bitmap bmp = this.yuvConv.yuv2rgb_main(optImage);
            releaseOptimizedImage(optImage);
            if (bmp != null) {
                STConstants.sFrameBitmapList.add(bmp);
                CameraNotificationManager.getInstance().requestNotify(STConstants.THUMBNAIL_UPDATE);
            }
            this.yuvConv.releaseYuvToRgbResources();
        }
    }

    public boolean checkBmpPosition(int counter) {
        if (!this.isDisplay || counter != (this.bitmapDisplayInterval * STConstants.sFrameBitmapList.size()) + this.bitmapDisplayFirstPosition) {
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
        if (STConstants.REMOVE_CAPTURE_CALLBACK.equalsIgnoreCase(tag)) {
            cancelCapturing();
            return;
        }
        if (STConstants.CAPTURING_STARTED.equalsIgnoreCase(tag)) {
            this.isSelfTimerOffFirstTakePicture = false;
        } else if (CameraNotificationManager.AE_LOCK_BUTTON.equalsIgnoreCase(tag) && STConstants.sCaptureImageCounter < 1 && !AELController.getInstance().getAELockButtonState()) {
            Log.i("STAVICaptureProcessTest", "AELockButtonState is false and set on from notify()");
            AELController.getInstance().holdAELock(true);
        }
    }

    private void cancelCapturing() {
        this.mStatus = 1;
        if (!this.isRequestCancelTakePicture) {
            this.isRequestCancelTakePicture = true;
            if (this.mAdapter != null && STUtility.getInstance().isPreTakePictureTestShot()) {
                this.mAdapter.enableNextCapture(this.mStatus);
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

    private synchronized OptimizedImage getSoftFocusOptimizedImage(OptimizedImage optImg) {
        OptimizedImage opImg;
        if (this.mSoftFocusImageFilter.isSupported()) {
            this.mSoftFocusImageFilter.setEffectLevel(this.mSoftFocusEffectId);
            this.mSoftFocusImageFilter.setSource(optImg, true);
            this.mSoftFocusImageFilter.execute();
            opImg = this.mSoftFocusImageFilter.getOutput();
            this.mSoftFocusImageFilter.clearSources();
        } else {
            opImg = null;
        }
        return opImg;
    }
}
