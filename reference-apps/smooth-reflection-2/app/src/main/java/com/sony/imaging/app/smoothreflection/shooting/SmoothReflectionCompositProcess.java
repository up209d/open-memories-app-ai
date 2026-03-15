package com.sony.imaging.app.smoothreflection.shooting;

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
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess;
import com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.smoothreflection.common.AppContext;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.MemoryUtil;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionWhiteBalanceController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.smoothreflection.shooting.keyhandler.SmoothReflectionCaptureStateKeyHandler;
import com.sony.imaging.app.smoothreflection.shooting.widget.ShootNumberLimitText;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.MemoryMapFile;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class SmoothReflectionCompositProcess implements IImagingProcess, ICaptureProcess, NotificationListener {
    private static final int AUTOREVIEW_TIME_OFF = 0;
    private static final int AUTOREVIEW_TIME_WORKAROUND = 2;
    private static final int BatteryPreEnd = 0;
    public static final String CAPTUREDIMAGETAG = "CapturedImageTag";
    private static final int CAPTURE_DELAY_TIME = 0;
    public static final String MEDIA_NOTIFIACTION_TAG = "MediaNotificationTag";
    private static final String PROP_MODEL_NAME = "model.name";
    public static final String SELFTIMERSETTINGTAG = "SelfTimerSettingTag";
    private static final int SELFTIMER_DELAY_TIME = 2000;
    private static final int SET_MEDIA_STATE = 0;
    private static final int TAKEPICTURE_STATE = 2;
    public static final String UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG = "UpdateShootNumberAfterCancelShooting";
    private static final int WAIT_MEDIA_SATE = 1;
    private static int mStatus;
    private static final String TAG = AppLog.getClassName();
    public static int mShootNumber = 0;
    public static int mRemaining = 0;
    public static boolean sCancelPicturebyPressingMenu = false;
    public static boolean sbISShootingFinish = false;
    public static boolean isRemoteControlMode = false;
    public static String keep_picture_size = null;
    public static boolean fakeicon_flg = false;
    private CameraEx mCameraEx = null;
    private IAdapter mAdapter = null;
    private SaRawComp mSaRawComp = null;
    private Camera mCamera = null;
    private CameraEx.AutoPictureReviewControl mAutoPictureReviewControl = null;
    private TakePictureRunnableTask mTakePictureRunnableTask = null;
    private SelfTimerRunnableTask mSelfTimerRunnableTask = null;
    private boolean mbFirstTimePicture = true;
    private boolean mbCancelTakePicture = false;
    private boolean mbIsInternalMedia = false;
    private int mShutterListenerStatus = 2;
    private int mTotalShot = 0;
    private int mPictureReviewTime = 0;
    private int mMemAddr = -1011909968;
    private int AddressSize = 4;
    private int EmptyListSize = 22;
    private int ActiveListSize = 22;
    private int[] OriginalList = new int[this.EmptyListSize];
    private ImageDataSet imData = null;
    public boolean debug_flg = false;
    long lstart = 0;
    long lstop = 0;
    private int ImageParamCaptureShareOffset = 14568;
    private int dmm_info_raw_unit_type_Offset = 15560;
    private int dmm_info_raw_count_Offset = 15564;
    private int dmm_info_raw0_Offset = 15568;
    private int unitHandle_Offset = 14584;
    private int unitDataOffset_Offset = 15556;
    private int info_canvasSizeX_Offset = 504;
    private int info_canvasSizeY_Offset = 508;
    private int info_marginOffsetX_Offset = 512;
    private int info_marginOffsetY_Offset = AppRoot.USER_KEYCODE.S1_ON;
    private int info_marginSizeX_Offset = AppRoot.USER_KEYCODE.FN;
    private int info_marginSizeY_Offset = AppRoot.USER_KEYCODE.DIAL1_STATUS;
    private int info_validOffsetX_Offset = AppRoot.USER_KEYCODE.DIAL2_RIGHT;
    private int info_validOffsetY_Offset = AppRoot.USER_KEYCODE.AEL;
    private int info_validSizeX_Offset = AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO;
    private int info_validSizeY_Offset = AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL;
    private int info_clpGr_Offset = 8802;
    private int info_clpGb_Offset = 8804;
    private int info_clpR_Offset = 8800;
    private int info_clpB_Offset = 8806;
    private int info_clpOfst_Offset = 8646;
    private int info_wbR_Offset = 7556;
    private int info_wbB_Offset = 7558;
    private String[] TAGS = {SmoothReflectionCaptureStateKeyHandler.CANCELTAKEPICTURETAG, MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
    MyRecordingMediaChangeCallback2 sMyRecordingMediaChangeCallback2 = new MyRecordingMediaChangeCallback2();
    MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();

    /* loaded from: classes.dex */
    public static class ImageDataSet {
        public int dmm_info_raw0;
        public int dmm_info_raw_count;
        public int dmm_info_raw_unit_type;
        public int info_canvasSizeX;
        public int info_canvasSizeY;
        public int info_clpB;
        public int info_clpGb;
        public int info_clpGr;
        public int info_clpOfst;
        public int info_clpR;
        public int info_marginOffsetX;
        public int info_marginOffsetY;
        public int info_marginSizeX;
        public int info_marginSizeY;
        public int info_validOffsetX;
        public int info_validOffsetY;
        public int info_validSizeX;
        public int info_validSizeY;
        public int info_wbB;
        public int info_wbR;
        public int raw_address;
        public int unitDataOffset;
        public int unitHandle;
        public int ddithRstmod = 0;
        public int ddithOn = 0;
        public int expBit = 3;
        public int decompMode = 4;
        public int dth0 = 8000;
        public int dth1 = 10400;
        public int dth2 = 12900;
        public int dth3 = 14100;
        public int dp0 = 4000;
        public int dp1 = 7200;
        public int dp2 = 10050;
        public int dp3 = 12075;
        public int dithRstmod = 0;
        public int dithOn = 0;
        public int rndBit = 3;
        public int compMode = 4;
        public int th0 = SmoothReflectionCompositProcess.SELFTIMER_DELAY_TIME;
        public int th1 = 3200;
        public int th2 = 5700;
        public int th3 = 8100;
        public int p0 = 4000;
        public int p1 = 7200;
        public int p2 = 10050;
        public int p3 = 12075;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void prepare(CameraEx cameraEx, IAdapter adapter) {
        AppLog.enter(TAG, AppLog.getMethodName());
        updateRemainingAmount();
        this.mCameraEx = cameraEx;
        this.mCamera = this.mCameraEx.getNormalCamera();
        this.mAdapter = adapter;
        isRemoteControlMode = isRemoteControlMode();
        setMemoryMapConfiguration();
        setVirtualMediaIds();
        CameraSequence.Options opt = new CameraSequence.Options();
        opt.setOption("MEMORY_MAP_FILE", getFilePathForSpecialSequence());
        opt.setOption("EXPOSURE_COUNT", 1);
        opt.setOption("RECORD_COUNT", 1);
        opt.setOption("INTERIM_PRE_REVIEW_ENABLED", true);
        if (!SaUtil.isAVIP()) {
            opt.setOption("DETECTION_OFF", false);
        }
        this.mAdapter.setOptions(opt);
        this.mPictureReviewTime = getAutoReviewControl();
        this.mSaRawComp = new SaRawComp();
        if (SaUtil.isAVIP()) {
            String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
            Log.d("MODEL NAME", "MODEL NAME = " + ModelName);
            if (ModelName.equals("NEX-6")) {
                this.mMemAddr = -1011909968;
            } else if (ModelName.equals("NEX-5R")) {
                this.mMemAddr = -1011893712;
            } else if (ModelName.equals("NEX-5T")) {
                this.mMemAddr = -1011886800;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isRemoteControlMode() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        return ((CameraEx.ParametersModifier) params.second).getRemoteControlMode();
    }

    private int getAutoReviewControl() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        int reviewTime = this.mAutoPictureReviewControl.getPictureReviewTime();
        if (reviewTime == 0) {
            reviewTime = 2;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return reviewTime;
    }

    private void setAutoReviewControl(int reviewTime) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mAutoPictureReviewControl == null) {
            this.mAutoPictureReviewControl = new CameraEx.AutoPictureReviewControl();
            this.mCameraEx.setAutoPictureReviewControl(this.mAutoPictureReviewControl);
        }
        this.mAutoPictureReviewControl.setPictureReviewTime(reviewTime);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getFilePathForSpecialSequence() {
        AppLog.enter(TAG, AppLog.getMethodName());
        MemoryMapFile map = new MemoryMapFile(AppContext.getAppContext(), "SmoothReflection", "00");
        String filePath = map.getPath();
        AppLog.info(TAG, "Memory map file path: " + filePath);
        AppLog.exit(TAG, AppLog.getMethodName());
        return filePath;
    }

    private void setMemoryMapConfiguration() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        if (pfMajorVersion == 2) {
            AppLog.info("kamata", "ALLOCATION_POLICY_VERSION_1");
            MemoryMapConfig.setAllocationPolicy(1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void preTakePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        sbISShootingFinish = true;
        setAutoReviewControl(0);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (true == ThemeController.CUSTOM.equals(selectedTheme)) {
            BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, ThemeController.SUPPORTED_SHOTS_ARRAY[7]);
            ShootNumberLimitText.sShotNumber = setShotNumberForCustomTheme();
        } else {
            ShootNumberLimitText.sShotNumber = ShootNumberLimitText.sTotalNumberOfShot;
        }
        this.mTotalShot = ShootNumberLimitText.sShotNumber;
        mShootNumber = 0;
        this.mbFirstTimePicture = true;
        this.mbCancelTakePicture = false;
        this.mShutterListenerStatus = 2;
        this.mTakePictureRunnableTask = new TakePictureRunnableTask();
        this.mSelfTimerRunnableTask = new SelfTimerRunnableTask();
        lockCameraSettings();
        this.mSaRawComp.open();
        if (SaUtil.isAVIP()) {
            Log.d(TAG, "preTakePicture Memory Get********************* start");
            keep_picture_size = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            fakeicon_flg = true;
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, PictureSizeController.SIZE_S);
            getEmptyImageInstance();
            Log.d(TAG, "preTakePicture Memory Get********************* end");
        } else {
            fakeicon_flg = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void takePicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mStatus != 0 && 1 != mStatus) {
            mStatus = 2;
            takePictureExec();
        } else {
            mStatus = 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void takePictureExec() {
        if (this.mbFirstTimePicture) {
            this.mbFirstTimePicture = false;
            String SelfTimerSetting = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMEROFF);
            if (SelfTimerIntervalPriorityController.SELFTIMERON.equals(SelfTimerSetting)) {
                CameraNotificationManager.getInstance().requestNotify(SELFTIMERSETTINGTAG);
                if (this.mSelfTimerRunnableTask != null) {
                    this.mSelfTimerRunnableTask.execute();
                }
            } else {
                takeNextPicture();
            }
        } else if (this.mTakePictureRunnableTask != null) {
            this.mTakePictureRunnableTask.execute();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ICaptureProcess
    public void startSelfTimerShutter() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.startSelfTimerShutter();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void onShutter(int status, CameraEx cameraEx) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "Status: " + status);
        this.mShutterListenerStatus = status;
        if (status != 0) {
            this.mSaRawComp.close();
            unLockCameraSetting();
            mShootNumber = 0;
            this.mCameraEx.cancelTakePicture();
            enableNextCapture(status);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess
    public synchronized void onShutterSequence(CameraSequence.RawData raw, CameraSequence sequence) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (SaUtil.isAVIP()) {
            Log.d(TAG, "onShutterSequence Memory Get********************* start");
            getRawInfo();
            Log.d(TAG, "onShutterSequence Memory Get********************* end");
        }
        if (SAEProtoExecutorCreator.isHalt || BatteryObserver.getInt(BatteryObserver.TAG_LEVEL) == 0) {
            this.mbCancelTakePicture = true;
        }
        if (this.mShutterListenerStatus == 0) {
            AppLog.info(TAG, "mShutterListenerStatus == ShutterListener.STATUS_OK");
            onShutterSequenceForSmoothReflectionImage(raw, sequence);
        } else {
            AppLog.info(TAG, "mShutterListenerStatus != ShutterListener.STATUS_OK");
            if (raw != null) {
                raw.release();
            }
            this.mAdapter.enableHalt(true);
            this.mSaRawComp.close();
            mShootNumber = 0;
            unLockCameraSetting();
            enableNextCapture(this.mShutterListenerStatus);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.IImagingProcess, com.sony.imaging.app.base.shooting.camera.executor.IProcess
    public void terminate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (this.mTakePictureRunnableTask != null) {
            this.mTakePictureRunnableTask.removeCallbacks();
            this.mTakePictureRunnableTask = null;
        }
        if (this.mSelfTimerRunnableTask != null) {
            this.mSelfTimerRunnableTask.removeCallbacks();
            this.mSelfTimerRunnableTask = null;
        }
        this.mbCancelTakePicture = false;
        this.mbFirstTimePicture = true;
        this.mCameraEx = null;
        this.mAdapter = null;
        mShootNumber = 0;
        this.mAutoPictureReviewControl = null;
        this.mbIsInternalMedia = false;
        if (this.mSaRawComp != null) {
            this.mSaRawComp.close();
            this.mSaRawComp = null;
        }
        clearPictureIconSize();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private OptimizedImage develop(CameraSequence.RawData raw) {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        String imageQuality = null;
        try {
            imageQuality = PictureQualityController.getInstance().getValue("API_NAME");
        } catch (IController.NotSupportedException e) {
            AppLog.info(TAG, e.toString());
        }
        if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equalsIgnoreCase(imageQuality)) {
            filter.setRawFileStoreEnabled(true);
            filter.setSource(raw, false);
            filter.execute();
        } else {
            filter.setRawFileStoreEnabled(false);
            filter.setSource(raw, true);
            filter.execute();
        }
        OptimizedImage optimizedImage = filter.getOutput();
        if (filter != null) {
            filter.clearSources();
            filter.release();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return optimizedImage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void takeNextPicture() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAdapter.enableHalt(false);
        this.mCameraEx.cancelTakePicture();
        this.mCameraEx.burstableTakePicture();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public synchronized void onShutterSequenceForSmoothReflectionImage(CameraSequence.RawData raw, CameraSequence sequence) {
        OptimizedImage img2;
        mShootNumber++;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus()) {
            if (!SaUtil.isAVIP()) {
                SaUtil.dispMemoryAddressArm(raw, PictureQualityController.PICTURE_QUALITY_RAW);
            }
            SmoothReflectionUtil.getInstance().setCurrentShootNumber(mShootNumber);
            CameraNotificationManager.getInstance().requestNotify(CAPTUREDIMAGETAG, Integer.valueOf(mShootNumber));
            if (mShootNumber < this.mTotalShot) {
                this.mSaRawComp.execute(raw, sequence);
                CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
                filter.setSource(raw, true);
                boolean dev_result = filter.execute();
                if (dev_result) {
                    Log.d("DEV RESULT", "DEV RESULT IS OK!");
                }
                OptimizedImage img = filter.getOutput();
                filter.clearSources();
                filter.release();
                if (raw != null) {
                    raw.release();
                }
                if (SaUtil.isAVIP()) {
                    img2 = img;
                } else {
                    img2 = getDummyImage(img);
                }
                sequence.storeImage(img2, true);
                if (mShootNumber == this.mTotalShot - 1 || this.mbCancelTakePicture) {
                    this.mTotalShot = mShootNumber + 1;
                    setActualMediaIds();
                    setAutoReviewControl(this.mPictureReviewTime);
                    clearPictureIconSize();
                } else {
                    try {
                        takePicture();
                    } catch (Exception e) {
                        this.mAdapter.enableHalt(true);
                        this.mSaRawComp.close();
                        mShootNumber = 0;
                        unLockCameraSetting();
                        if (this.mTakePictureRunnableTask != null) {
                            this.mTakePictureRunnableTask.removeCallbacks();
                            this.mTakePictureRunnableTask = null;
                        }
                        if (this.mSelfTimerRunnableTask != null) {
                            this.mSelfTimerRunnableTask.removeCallbacks();
                            this.mSelfTimerRunnableTask = null;
                        }
                        enableNextCapture(0);
                    }
                }
            } else {
                AppLog.info(TAG, "Last Picture Taken and store in Memory");
                sbISShootingFinish = false;
                this.mSaRawComp.execute(raw, sequence);
                this.mSaRawComp.close();
                String imageQuality = null;
                try {
                    imageQuality = PictureQualityController.getInstance().getValue("API_NAME");
                } catch (IController.NotSupportedException e2) {
                    AppLog.info(TAG, e2.toString());
                }
                if (PictureQualityController.PICTURE_QUALITY_RAW.equalsIgnoreCase(imageQuality)) {
                    sequence.storeImage(raw, true);
                } else {
                    OptimizedImage optimizedImage = develop(raw);
                    sequence.storeImage(optimizedImage, true);
                }
                updateRemainingAmount();
                setVirtualMediaIds();
                if (raw != null) {
                    raw.release();
                }
                unLockCameraSetting();
                if (this.mTakePictureRunnableTask != null) {
                    this.mTakePictureRunnableTask.removeCallbacks();
                    this.mTakePictureRunnableTask = null;
                }
                if (this.mSelfTimerRunnableTask != null) {
                    this.mSelfTimerRunnableTask.removeCallbacks();
                    this.mSelfTimerRunnableTask = null;
                }
                if (!this.mAdapter.enableHalt(true)) {
                    enableNextCapture(0);
                    if (true == this.mbCancelTakePicture) {
                        CameraNotificationManager.getInstance().requestNotify(UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG);
                    } else {
                        enableNextCapture(0);
                    }
                    this.mbCancelTakePicture = false;
                }
            }
        } else {
            clearPictureIconSize();
            unLockCameraSetting();
            sbISShootingFinish = false;
            if (this.mTakePictureRunnableTask != null) {
                this.mTakePictureRunnableTask.removeCallbacks();
                this.mTakePictureRunnableTask = null;
            }
            if (this.mSelfTimerRunnableTask != null) {
                this.mSelfTimerRunnableTask.removeCallbacks();
                this.mSelfTimerRunnableTask = null;
            }
            if (!this.mAdapter.enableHalt(true)) {
                enableNextCapture(1);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private OptimizedImage getDummyImage(OptimizedImage img) {
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        DeviceBuffer bootParam = dsp.createBuffer(60);
        dsp.setProgram("/android/data/data/com.sony.imaging.app.smoothreflection/lib/libsa_imreturn_musashi.so");
        int dummyWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
        int dummyHeghit = 432;
        String aspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (aspect.equals(PictureSizeController.ASPECT_3_2)) {
            dummyWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            dummyHeghit = 432;
        } else if (aspect.equals(PictureSizeController.ASPECT_4_3)) {
            dummyWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            dummyHeghit = 480;
        } else if (aspect.equals(PictureSizeController.ASPECT_16_9)) {
            dummyWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            dummyHeghit = 360;
        } else if (aspect.equals(PictureSizeController.ASPECT_1_1)) {
            dummyWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            dummyHeghit = AppRoot.USER_KEYCODE.WATER_HOUSING;
        }
        OptimizedImage dummyImage = dsp.createImage(dummyWidth, dummyHeghit);
        if (dummyImage == null) {
            Log.d("CREATE IMAGE", "CREATE IMAGE FAILED!!!!!!!!!!!!!!!!!!!!!!");
        }
        setBootParam(dsp, bootParam);
        dsp.setArg(1, img);
        dsp.setArg(2, dummyImage);
        dsp.execute();
        bootParam.release();
        img.release();
        dsp.release();
        return dummyImage;
    }

    private void setBootParam(DSP dsp, DeviceBuffer bootParam) {
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(dsp.getPropertyAsInt("program-descriptor"));
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 1);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.position(0);
        bootParam.write(buf);
        dsp.setArg(0, bootParam);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TakePictureRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private TakePictureRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess.TakePictureRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName() + "continuous shooting");
                    SmoothReflectionCompositProcess.this.takeNextPicture();
                    AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName() + "continuous shooting");
                }
            };
        }

        public void execute() {
            AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
            this.mHandler.postDelayed(this.mRunnable, 0L);
            AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
        }

        public void removeCallbacks() {
            AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mRunnable = null;
            this.mHandler = null;
            AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelfTimerRunnableTask {
        private Handler mHandler;
        private Runnable mRunnable;

        private SelfTimerRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess.SelfTimerRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!SmoothReflectionCompositProcess.this.mAdapter.enableHalt(true)) {
                        AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName() + "self timer shooting");
                        if (!SmoothReflectionCompositProcess.this.mbCancelTakePicture) {
                            AppLog.info(SmoothReflectionCompositProcess.TAG, "Self timer Take picture");
                            if (!SmoothReflectionCompositProcess.sCancelPicturebyPressingMenu) {
                                SmoothReflectionCompositProcess.this.takeNextPicture();
                            } else {
                                SmoothReflectionCompositProcess.sCancelPicturebyPressingMenu = false;
                                SmoothReflectionCompositProcess.this.enableNextCapture(1);
                                CameraNotificationManager.getInstance().requestNotify(SmoothReflectionCompositProcess.UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG);
                            }
                        } else if (!SmoothReflectionCompositProcess.this.mAdapter.enableHalt(true)) {
                            AppLog.info(SmoothReflectionCompositProcess.TAG, "Self timer Cancel Take picture");
                            SmoothReflectionCompositProcess.this.mbCancelTakePicture = false;
                            SmoothReflectionCompositProcess.this.enableNextCapture(1);
                            CameraNotificationManager.getInstance().requestNotify(SmoothReflectionCompositProcess.UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG);
                        } else {
                            return;
                        }
                        AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName() + "self timer shooting");
                    }
                }
            };
        }

        public void execute() {
            AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
            this.mHandler.postDelayed(this.mRunnable, 2000L);
            AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
        }

        public void removeCallbacks() {
            AppLog.enter(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mRunnable = null;
            this.mHandler = null;
            AppLog.exit(SmoothReflectionCompositProcess.TAG, AppLog.getMethodName());
        }
    }

    private void lockCameraSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AELController.getInstance().holdAELock(true);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        if (params != null && "auto".equals(SmoothReflectionWhiteBalanceController.getInstance().getValue())) {
            ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(true);
            CameraSetting.getInstance().setParameters(params);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void unLockCameraSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AELController.getInstance().holdAELock(false);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
        if (params != null) {
            ((CameraEx.ParametersModifier) params.second).setAutoWhiteBalanceLock(false);
            CameraSetting.getInstance().setParameters(params);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableNextCapture(int status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraEx.cancelTakePicture();
        this.mAdapter.enableNextCapture(status);
        if (isRemoteControlMode) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(true);
            this.mCamera.setParameters((Camera.Parameters) setParams.first);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (tag.equals(SmoothReflectionCaptureStateKeyHandler.CANCELTAKEPICTURETAG)) {
            this.mbCancelTakePicture = true;
            this.mCameraEx.cancelTakePicture();
        } else if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            enableNextCapture(1);
            CameraNotificationManager.getInstance().requestNotify(UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG);
            this.mbCancelTakePicture = false;
            unLockCameraSetting();
            if (this.mTakePictureRunnableTask != null) {
                this.mTakePictureRunnableTask.removeCallbacks();
                this.mTakePictureRunnableTask = null;
            }
            if (this.mSelfTimerRunnableTask != null) {
                this.mSelfTimerRunnableTask.removeCallbacks();
                this.mSelfTimerRunnableTask = null;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setVirtualMediaIds() {
        String[] ids = AvindexStore.getVirtualMediaIds();
        this.mCameraEx.setRecordingMedia(ids[0], this.sMyRecordingMediaChangeCallback2);
        this.mbIsInternalMedia = true;
        mStatus = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyRecordingMediaChangeCallback2 implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback2() {
        }

        public synchronized void onRecordingMediaChange(CameraEx cameraEx) {
            if (1 == SmoothReflectionCompositProcess.mStatus) {
                SmoothReflectionCompositProcess.this.takePictureExec();
            }
            int unused = SmoothReflectionCompositProcess.mStatus = 2;
        }
    }

    private void setActualMediaIds() {
        String[] ids = AvindexStore.getExternalMediaIds();
        this.mCameraEx.setRecordingMedia(ids[0], this.sMyRecordingMediaChangeCallback);
        this.mbIsInternalMedia = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            if (!SmoothReflectionCompositProcess.this.mbIsInternalMedia) {
                SmoothReflectionCompositProcess.this.takePicture();
            }
        }
    }

    public static int updateRemainingAmount() {
        MediaNotificationManager.getInstance().updateRemainingAmount();
        if (MediaNotificationManager.getInstance().getRemaining() >= 0) {
            mRemaining = MediaNotificationManager.getInstance().getRemaining();
        }
        return mRemaining;
    }

    private int setShotNumberForCustomTheme() {
        String selectedShot = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, ThemeController.SUPPORTED_SHOTS_ARRAY[7]);
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[0].equals(selectedShot)) {
            return 2;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[1].equals(selectedShot)) {
            return 4;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[2].equals(selectedShot)) {
            return 6;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[3].equals(selectedShot)) {
            return 8;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[4].equals(selectedShot)) {
            return 16;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[5].equals(selectedShot)) {
            return 32;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[6].equals(selectedShot)) {
            return 48;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[7].equals(selectedShot)) {
            return 64;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[8].equals(selectedShot)) {
            return 96;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[9].equals(selectedShot)) {
            return 128;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[10].equals(selectedShot)) {
            return 192;
        }
        if (!ThemeController.SUPPORTED_SHOTS_ARRAY[11].equals(selectedShot)) {
            return 0;
        }
        return 256;
    }

    private void getRawInfo() {
        int next_address = this.mMemAddr;
        byte[] AddressBuf = new byte[4];
        int[] CurrentList = new int[this.EmptyListSize];
        int[] CurrentActive = new int[this.ActiveListSize];
        byte[] Byte4Buf = new byte[4];
        byte[] Byte2Buf = new byte[2];
        int n = 0;
        int ActiveCount = 0;
        int MainAddress = 0;
        boolean MatchingFlg = false;
        MemoryUtil mUtil = new MemoryUtil();
        while (next_address != 0) {
            mUtil.memoryCopyDiademToApplication(next_address, AddressBuf, this.AddressSize);
            next_address = LittleEndian4Int(AddressBuf);
            CurrentList[n] = next_address;
            n++;
        }
        for (int i = 0; this.OriginalList[i] != 0; i++) {
            for (int j = 0; CurrentList[j] != 0; j++) {
                if (this.OriginalList[i] == CurrentList[j]) {
                    MatchingFlg = true;
                }
            }
            if (!MatchingFlg) {
                CurrentActive[ActiveCount] = this.OriginalList[i];
                ActiveCount++;
            } else {
                MatchingFlg = false;
            }
        }
        for (int m = 0; m < ActiveCount; m++) {
            int temp_address = CurrentActive[m] + this.ImageParamCaptureShareOffset;
            mUtil.memoryCopyDiademToApplication(temp_address, AddressBuf, this.AddressSize);
            int temp_address2 = LittleEndian4Int(AddressBuf);
            if (temp_address2 != 0) {
                switch (ActiveCount) {
                    case 1:
                    case 2:
                        MainAddress = CurrentActive[m];
                        break;
                    case 3:
                    case 4:
                        if (CurrentActive[m] != 0) {
                            MainAddress = CurrentActive[m];
                            break;
                        } else {
                            break;
                        }
                    case 5:
                    case 6:
                        if (CurrentActive[m] != 0 && CurrentActive[m] != 0) {
                            MainAddress = CurrentActive[m];
                            break;
                        }
                        break;
                    default:
                        Log.e("ActiveImageInstanceError", "The number of ImageInstance is unexpected.(more than 7. the expected number is 1 to 6.) ActiveCount = " + ActiveCount);
                        break;
                }
            }
        }
        this.imData = new ImageDataSet();
        mUtil.memoryCopyDiademToApplication(this.dmm_info_raw_unit_type_Offset + MainAddress, Byte4Buf, 4);
        this.imData.dmm_info_raw_unit_type = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.dmm_info_raw_count_Offset + MainAddress, Byte4Buf, 4);
        this.imData.dmm_info_raw_count = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.unitHandle_Offset + MainAddress, Byte4Buf, 4);
        this.imData.unitHandle = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.unitDataOffset_Offset + MainAddress, Byte4Buf, 4);
        this.imData.unitDataOffset = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.dmm_info_raw0_Offset + MainAddress, Byte4Buf, 4);
        this.imData.dmm_info_raw0 = LittleEndian4Int(Byte4Buf) + this.imData.unitDataOffset;
        this.imData.raw_address = this.dmm_info_raw0_Offset + MainAddress;
        mUtil.memoryCopyDiademToApplication(this.info_canvasSizeX_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_canvasSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_canvasSizeY_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_canvasSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_marginOffsetX_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_marginOffsetX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_marginOffsetY_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_marginOffsetY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_marginSizeX_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_marginSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_marginSizeY_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_marginSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_validOffsetX_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_validOffsetX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_validOffsetY_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_validOffsetY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_validSizeX_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_validSizeX = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_validSizeY_Offset + MainAddress, Byte4Buf, 4);
        this.imData.info_validSizeY = LittleEndian4Int(Byte4Buf);
        mUtil.memoryCopyDiademToApplication(this.info_clpGr_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_clpGr = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_clpGb_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_clpGb = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_clpR_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_clpR = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_clpB_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_clpB = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_clpOfst_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_clpOfst = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_wbR_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_wbR = LittleEndian2Int(Byte2Buf);
        mUtil.memoryCopyDiademToApplication(this.info_wbB_Offset + MainAddress, Byte2Buf, 2);
        this.imData.info_wbB = LittleEndian2Int(Byte2Buf);
        this.mSaRawComp.setImageDataSet(this.imData);
    }

    private void getEmptyImageInstance() {
        int next_address = this.mMemAddr;
        byte[] AddressBuf = new byte[4];
        MemoryUtil mUtil = new MemoryUtil();
        int i = 0;
        while (next_address != 0) {
            mUtil.memoryCopyDiademToApplication(next_address, AddressBuf, this.AddressSize);
            next_address = LittleEndian4Int(AddressBuf);
            this.OriginalList[i] = next_address;
            i++;
        }
    }

    public static int LittleEndian4Int(byte[] buf) {
        return (buf[0] & 255) | (65280 & (buf[1] << 8)) | (16711680 & (buf[2] << 16)) | ((-16777216) & (buf[3] << 24));
    }

    public static int LittleEndian2Int(byte[] buf) {
        return (buf[0] & 255) | (65280 & (buf[1] << 8));
    }

    public int getOriginalPictureIconSize() {
        if (keep_picture_size.equals(PictureSizeController.SIZE_L)) {
            return 0;
        }
        if (keep_picture_size.equals(PictureSizeController.SIZE_M)) {
            return 1;
        }
        if (!keep_picture_size.equals(PictureSizeController.SIZE_S)) {
            return 0;
        }
        return 2;
    }

    public void clearPictureIconSize() {
        if (SaUtil.isAVIP() && fakeicon_flg) {
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, keep_picture_size);
            fakeicon_flg = false;
        }
    }
}
