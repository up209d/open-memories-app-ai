package com.sony.imaging.app.portraitbeauty.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautySAWrapper;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautySoftSkinController;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class PortraitBeautyEffectProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    private static final int QUALITY_FINE = 50;
    public static final String UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG = "UpdateShootNumberAfterCancelShooting";
    public static String mBackup_picturesize;
    public static int captureTime = 0;
    public static byte CAPTURE_NOT_STARTED = 0;
    public static byte CAPTURE_STARTED = 1;
    public static byte CAPTURE_CANCELLED = 2;
    public static byte CAPTURE_DONE = 3;
    public static byte sIsCaptureStarted = CAPTURE_NOT_STARTED;
    private final String TAG = AppLog.getClassName();
    private IAdapter mAdapter = null;
    private CameraEx mCameraEx = null;
    private CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;
    private PortraitBeautySAWrapper mPortraitBeautySAWrapper = null;
    private boolean is_mBackup_picturesize_used = false;
    private String[] TAGS = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, PortraitBeautyConstants.CANCELSHOOTINGTIMERTAG};
    private Handler myHandler = null;
    private TickerRunnable tickerRunnableTask = null;
    private Handler iconHandler = null;
    private UpdateIconRunnable updateIconRunnable = null;
    private Handler beepHandler = null;
    private BeepRunnable beepRunnableTask = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx camera, IAdapter adapter) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCameraEx = camera;
        this.mAdapter = adapter;
        if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat())) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((Camera.Parameters) params.first).setJpegQuality(50);
            CameraSetting.getInstance().setParameters(params);
        }
        String filePath = getFilePathForSpecialSequence();
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", filePath);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        this.mAdapter.setOptions(opt);
        if (this.mPortraitBeautySAWrapper == null) {
            this.mPortraitBeautySAWrapper = PortraitBeautySAWrapper.getInstance();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private String getFilePathForSpecialSequence() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "MyBestPortrait", "00");
        String filePath = map.getPath();
        Log.i(this.TAG, "filePath:" + filePath);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return filePath;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        if (this.mPortraitBeautySAWrapper != null) {
            this.mPortraitBeautySAWrapper.terminate();
            this.mPortraitBeautySAWrapper = null;
        }
        deInitialize();
        if (PortraitBeautyUtil.bIsAdjustModeGuide && true == this.is_mBackup_picturesize_used && CameraSetting.getPfApiVersion() <= 1) {
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, mBackup_picturesize);
            this.is_mBackup_picturesize_used = false;
        }
        PortraitBeautyExecutorCreater.isHalt = false;
        this.mAdapter = null;
        this.mCameraEx = null;
        this.mAutoPictureReviewControl = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        if (status != 0) {
            this.mAdapter.enableHalt(true);
            enableNextCapture(status);
            unLockCameraSetting();
            if (this.mPortraitBeautySAWrapper != null) {
                this.mPortraitBeautySAWrapper.terminate();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        if (!this.mAdapter.enableHalt(true)) {
            CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.PROCESSING_PROGRESS_START);
            if (!PortraitBeautyUtil.bIsAdjustModeGuide) {
                AppLog.info(this.TAG, "onShutterSequence() in Select Mode Case");
                setEffectToOriginalImage_SelectEffectMode(raw, sequence);
            } else if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
                setEffectToPsedurecImage_AdjustEffectMode(raw, sequence);
            }
            unLockCameraSetting();
            enableNextCapture(0);
            CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.PROCESSING_PROGRESS_STOP);
        }
    }

    private void setEffectToPsedurecImage_AdjustEffectMode(CameraSequence.RawData raw, CameraSequence sequence) {
        this.mAdapter.onProgress(0, 3);
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, true);
        filter.execute();
        this.mAdapter.onProgress(1, 3);
        OptimizedImage img = filter.getOutput();
        filter.release();
        this.mAdapter.onProgress(2, 3);
        sequence.storeImage(img, true);
        enableNextCapture(0);
        this.mAdapter.onProgress(3, 3);
    }

    private void setEffectToOriginalImage_SelectEffectMode(CameraSequence.RawData raw, CameraSequence sequence) {
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        int isoValue = getISOValue();
        int softSkinLevel = PortraitBeautySoftSkinController.getInstance().convValue_String2Int(BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, PortraitBeautySoftSkinController.SOFTSKIN_MID));
        int whiteSkinLevel = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, 4);
        AppLog.info(this.TAG, "IMDLAPP8-902 Face brightening Skin level = " + whiteSkinLevel);
        if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat())) {
            AppLog.info(this.TAG, "Image quality is RAW+JPEG");
            filter.setRawFileStoreEnabled(true);
            filter.setSource(raw, true);
            filter.execute();
        } else {
            AppLog.info(this.TAG, "Image quality is Fine or Standard");
            filter.setRawFileStoreEnabled(false);
            filter.setSource(raw, true);
            filter.execute();
        }
        OptimizedImage originalOptimizedImage = filter.getOutput();
        filter.release();
        this.mAdapter.onProgress(0, 1);
        int faceNumbers = this.mPortraitBeautySAWrapper.getFaces(originalOptimizedImage);
        if (faceNumbers > 0 && 1 != softSkinLevel) {
            originalOptimizedImage = this.mPortraitBeautySAWrapper.setSoftSkinEffect(originalOptimizedImage, faceNumbers, softSkinLevel, isoValue, true);
        } else {
            AppLog.info(this.TAG, "SoftSkin level is OFF");
        }
        this.mAdapter.onProgress(0, 1);
        if (whiteSkinLevel != 0) {
            this.mPortraitBeautySAWrapper.initialize();
            this.mPortraitBeautySAWrapper.setSize(true);
            if (originalOptimizedImage != null) {
                this.mPortraitBeautySAWrapper.setOptimizeImage(originalOptimizedImage, originalOptimizedImage);
                OptimizedImage optimizedImageWithEffect = this.mPortraitBeautySAWrapper.executeSA();
                this.mAdapter.onProgress(1, 1);
                sequence.storeImage(optimizedImageWithEffect, true);
                originalOptimizedImage.release();
                if (optimizedImageWithEffect != null) {
                    optimizedImageWithEffect.release();
                }
            }
            this.mPortraitBeautySAWrapper.terminate();
            AppLog.info(this.TAG, "Face brightening Skin level = " + whiteSkinLevel);
            return;
        }
        this.mAdapter.onProgress(1, 1);
        sequence.storeImage(originalOptimizedImage, true);
        AppLog.info(this.TAG, "Face brightening Skin level is OFF");
        if (originalOptimizedImage != null) {
            originalOptimizedImage.release();
        }
    }

    private int getSoftSkinLevel() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int level = -1;
        String softSkinLevel = null;
        try {
            softSkinLevel = PortraitBeautySoftSkinController.getInstance().getValue(PortraitBeautySoftSkinController.SOFTSKIN);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        if (softSkinLevel == null) {
            return -1;
        }
        if (softSkinLevel.equalsIgnoreCase("Off")) {
            level = 1;
        } else if (softSkinLevel.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
            level = 2;
        } else if (softSkinLevel.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
            level = 3;
        } else if (softSkinLevel.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
            level = 4;
        }
        AppLog.info(this.TAG, "level: " + level);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return level;
    }

    private int getISOValue() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        int isoValue = ((CameraEx.ParametersModifier) params.second).getISOSensitivity();
        if (isoValue == 0) {
            isoValue = CameraSetting.getInstance().getISOSensitivityAuto();
        }
        AppLog.info(this.TAG, "ISO value: " + isoValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return isoValue;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.info(this.TAG, "preTakePicture called from PortraitBeautyEffectProcess");
        if (PortraitBeautyUtil.bIsAdjustModeGuide && CameraSetting.getPfApiVersion() <= 1) {
            mBackup_picturesize = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, PictureSizeController.SIZE_L);
            this.is_mBackup_picturesize_used = true;
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        sIsCaptureStarted = CAPTURE_NOT_STARTED;
        setAutoReviewControl();
        captureTime = PortraitBeautyUtil.getInstance().getTime();
        initialize();
        if (captureTime > 0) {
            CameraNotificationManager.getInstance().requestNotify("selftimer");
            int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (device == 0 || device == 2) {
                String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_TIMER, backupDispMode);
            }
        }
        lockCameraSettings();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (!this.mAdapter.enableHalt(true)) {
            sIsCaptureStarted = CAPTURE_STARTED;
            if (captureTime == 0) {
                this.tickerRunnableTask.execute(0);
            } else {
                if (true == (DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus())) {
                    sIsCaptureStarted = CAPTURE_DONE;
                    takeNextPicture();
                } else {
                    startSelfTimer();
                }
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    private void startSelfTimer() {
        executeRunnable();
    }

    private void executeRunnable() {
        this.beepRunnableTask.execute(0);
        this.tickerRunnableTask.execute(captureTime);
        this.updateIconRunnable.execute(1000);
    }

    private void enableNextCapture(int status) {
        int device;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyUtil.bIsAdjustModeGuide && true == this.is_mBackup_picturesize_used && CameraSetting.getPfApiVersion() <= 1) {
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, mBackup_picturesize);
            this.is_mBackup_picturesize_used = false;
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        unLockCameraSetting();
        deInitialize();
        if (!PortraitBeautyUtil.bIsAdjustModeGuide && ((device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice()) == 0 || device == 2)) {
            String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_TIMER, "1");
            PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode));
        }
        this.mAdapter.enableNextCapture(status);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void takeNextPicture() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mAdapter.enableHalt(false);
        sIsCaptureStarted = CAPTURE_DONE;
        this.mCameraEx.cancelTakePicture();
        if (!AELController.getInstance().isAELock()) {
            lockCameraSettings();
        }
        CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG);
        this.mCameraEx.burstableTakePicture();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void lockCameraSettings() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AELController.getInstance().holdAELock(true);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void unLockCameraSetting() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AELController.getInstance().holdAELock(false);
        AELController.getInstance().cancelAELock();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            sIsCaptureStarted = CAPTURE_CANCELLED;
            CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG);
            enableNextCapture(1);
            CameraNotificationManager.getInstance().requestNotify(UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG);
            return;
        }
        if (tag.equals(PortraitBeautyConstants.CANCELSHOOTINGTIMERTAG)) {
            CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG);
            if (sIsCaptureStarted != CAPTURE_DONE) {
                sIsCaptureStarted = CAPTURE_CANCELLED;
                ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                enableNextCapture(1);
            }
        }
    }

    private void initialize() {
        if (this.myHandler == null) {
            this.myHandler = new Handler();
        }
        if (this.tickerRunnableTask == null) {
            this.tickerRunnableTask = new TickerRunnable(this.myHandler);
        }
        initializeBeep();
        initializeIconUpdateRunnable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deInitialize() {
        if (this.tickerRunnableTask != null) {
            this.tickerRunnableTask.removeCallbacks();
        }
        this.tickerRunnableTask = null;
        deInitializeBeep();
        deInitializeUpdateIconHandler();
    }

    private void initializeIconUpdateRunnable() {
        if (this.iconHandler == null) {
            this.iconHandler = new Handler();
        }
        if (this.updateIconRunnable == null) {
            this.updateIconRunnable = new UpdateIconRunnable(this.iconHandler);
        }
    }

    private void deInitializeUpdateIconHandler() {
        if (this.updateIconRunnable != null) {
            this.updateIconRunnable.removeCallbacks();
            this.updateIconRunnable = null;
        }
    }

    /* loaded from: classes.dex */
    public class UpdateIconRunnable {
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess.UpdateIconRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                AppLog.enter(PortraitBeautyEffectProcess.this.TAG, AppLog.getMethodName() + "Laxmi11" + UpdateIconRunnable.this.counter);
                if (UpdateIconRunnable.this.counter < PortraitBeautyEffectProcess.captureTime / 1000) {
                    UpdateIconRunnable.this.execute(1000);
                    CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERUPDATEICONSETTINGTAG, Integer.valueOf(UpdateIconRunnable.this.counter));
                    UpdateIconRunnable.this.counter++;
                }
            }
        };
        int counter = 0;

        public UpdateIconRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute(int time) {
            if (this.handler != null) {
                this.handler.postDelayed(this.r, time);
            }
        }

        public void removeCallbacks() {
            if (this.handler != null) {
                this.handler.removeCallbacks(this.r);
                this.r = null;
                this.handler = null;
            }
        }
    }

    /* loaded from: classes.dex */
    public class TickerRunnable {
        int counter;
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess.TickerRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                if (PortraitBeautyEffectProcess.captureTime == 0) {
                    CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG);
                }
                if (PortraitBeautyEffectProcess.CAPTURE_STARTED == PortraitBeautyEffectProcess.sIsCaptureStarted) {
                    PortraitBeautyEffectProcess.sIsCaptureStarted = PortraitBeautyEffectProcess.CAPTURE_DONE;
                    PortraitBeautyEffectProcess.this.deInitialize();
                    PortraitBeautyEffectProcess.this.takeNextPicture();
                }
            }
        };

        public TickerRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void execute(int time) {
            if (this.handler != null) {
                this.handler.postDelayed(this.r, time);
            }
        }

        public void removeCallbacks() {
            if (this.handler != null) {
                this.handler.removeCallbacks(this.r);
                this.r = null;
                this.handler = null;
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
    }

    private void setAutoReviewControl() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
        }
        this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        this.mAutoPictureReviewControl.setPictureReviewTime(0);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void initializeBeep() {
        if (this.beepHandler == null) {
            this.beepHandler = new Handler();
        }
        if (this.beepRunnableTask == null) {
            this.beepRunnableTask = new BeepRunnable(this.beepHandler);
        }
    }

    private void deInitializeBeep() {
        if (this.beepRunnableTask != null) {
            this.beepRunnableTask.removeCallbacks();
            this.beepRunnableTask = null;
        }
    }

    /* loaded from: classes.dex */
    public class BeepRunnable {
        int counter;
        int[] drawables;
        Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess.BeepRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                if (PortraitBeautyEffectProcess.CAPTURE_STARTED == PortraitBeautyEffectProcess.sIsCaptureStarted) {
                    if (BeepRunnable.this.counter > 1) {
                        PortraitBeautyUtil.getInstance().playBEEPfunction();
                    }
                    if (BeepRunnable.this.counter > BeepRunnable.this.beepTime) {
                        BeepRunnable.this.execute(CatchLightPlayBackLayout.mLayoutParams_faceZoom__width);
                    } else {
                        BeepRunnable.this.execute(1000);
                    }
                    BeepRunnable.this.counter++;
                }
            }
        };
        int beepTime = 0;

        public int getBeepTime() {
            return this.beepTime;
        }

        public BeepRunnable(Handler hnd) {
            this.counter = 0;
            this.handler = hnd;
            this.counter = 0;
        }

        public void incrementCounter() {
            this.counter++;
        }

        public void setBeepTime(int time) {
            this.beepTime = time;
        }

        public void setIcons(int[] icons) {
            this.drawables = icons;
        }

        public void execute(int time) {
            if (this.handler != null) {
                setBeepTime();
                this.handler.postDelayed(this.r, time);
            }
        }

        private void setBeepTime() {
            if (PortraitBeautyEffectProcess.this.beepRunnableTask != null) {
                switch (PortraitBeautyEffectProcess.captureTime / 1000) {
                    case 3:
                        setBeepTime(0);
                        return;
                    case 6:
                        setBeepTime(3);
                        return;
                    case 11:
                        setBeepTime(8);
                        return;
                    default:
                        setBeepTime(0);
                        return;
                }
            }
        }

        public void removeCallbacks() {
            if (this.handler != null) {
                this.handler.removeCallbacks(this.r);
                this.handler = null;
                this.counter = 0;
                this.beepTime = 0;
            }
        }
    }
}
