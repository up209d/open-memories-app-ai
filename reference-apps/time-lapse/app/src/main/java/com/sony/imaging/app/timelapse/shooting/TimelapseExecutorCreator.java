package com.sony.imaging.app.timelapse.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.databaseutil.TimeLapseBO;
import com.sony.imaging.app.timelapse.menu.controller.TimelapseExposureModeController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.MovieController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.PlainCalendar;
import java.util.List;

/* loaded from: classes.dex */
public class TimelapseExecutorCreator extends ExecutorCreator {
    private static final String TAG = TimelapseExecutorCreator.class.getName();
    public static boolean isNoShotCaptured = false;
    private TLAviCaptureProcessOldPF mAviCaptureProcessOldPF = new TLAviCaptureProcessOldPF();
    private TLSingleCaptureProcessOldPF mSingleProcessOldPf = new TLSingleCaptureProcessOldPF();
    private TLAviCaptureProcess mAviCaptureProcessNewPF = new TLAviCaptureProcess();
    private TLSingleCaptureProcess mSingleProcessNewPF = new TLSingleCaptureProcess();
    private boolean isSettingDone = true;
    private boolean mSpecial = false;
    private TLNormalExecutor mTLNormalExecutor = new TLNormalExecutor();
    private int mCounter = 0;
    private TimeLapseBO mTimelapseBo = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        IProcess process;
        setDefaultSettingSForTimeLapse();
        if (isSpecial()) {
            isNoShotCaptured = true;
            TimelapsePictureEffectController.getInstance().setMiniatureForSpecialSequence(true);
            if (!isAELockTrackingSelected()) {
                process = this.mAviCaptureProcessNewPF;
            } else {
                process = this.mAviCaptureProcessOldPF;
            }
        } else {
            TimelapsePictureEffectController.getInstance().setMiniatureForSpecialSequence(false);
            if (!isAELockTrackingSelected()) {
                process = this.mSingleProcessNewPF;
            } else {
                process = this.mSingleProcessOldPf;
            }
        }
        AppLog.trace(TAG, "getProcess() selected process = " + process.toString());
        return process;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public Class<?> getNextExecutor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!this.mIsRecModeChanging && !MediaNotificationManager.getInstance().isError()) {
            return TLNormalExecutor.class;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.getNextExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        BaseShootingExecutor executor;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (TLNormalExecutor.class.equals(clazz)) {
            executor = this.mTLNormalExecutor;
        } else {
            executor = super.getExecutor(clazz);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return executor;
    }

    private boolean isAELockTrackingSelected() {
        return TLCommonUtil.getInstance().getAETrakingStatus() == 0 || TLCommonUtil.getInstance().getAETrakingStatus() == 4;
    }

    private void setDriveModeToSingle() {
        DriveModeController dvCntl = DriveModeController.getInstance();
        if (dvCntl != null) {
            dvCntl.setValue("drivemode", "single");
        }
    }

    private boolean isSettingDone() {
        return this.isSettingDone;
    }

    public void setSettingDone(boolean isSettingDone) {
        this.isSettingDone = isSettingDone;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        this.mSpecial = false;
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            this.mSpecial = true;
        }
        return this.mSpecial;
    }

    public void setMovieModeSettings(boolean isMovieMode) {
        this.mSpecial = isMovieMode;
        isSpecial();
    }

    private void setDefaultSettingSForTimeLapse() {
        if (isSettingDone()) {
            setSettingDone(false);
            setDriveModeToSingle();
            setDROToOff();
            FaceDetectionController.getInstance().setValue("off", "off");
            MeteringController.getInstance().setValue(MeteringController.MULTIMODE, FocusAreaController.MULTI);
        }
        FlashController.getInstance().setValue(FlashController.FLASHMODE, "off");
        FlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, "0");
        setExposureMode();
    }

    private void setDROToOff() {
        DROAutoHDRController droCntl = DROAutoHDRController.getInstance();
        if (droCntl != null) {
            String dvStr = droCntl.getValue();
            if (dvStr != "off") {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
            }
        }
    }

    public void setCounter(int counter) {
        this.mCounter = counter;
    }

    public TimeLapseBO getTimeLapseBOObject() {
        return this.mTimelapseBo;
    }

    public void createTimeLapseBOData() {
        int width;
        int height;
        TimeLapseBO timelapseBo = new TimeLapseBO();
        TLCommonUtil cUtil = TLCommonUtil.getInstance();
        PlainCalendar calendar = cUtil.getPinedCalender();
        timelapseBo.setStartDate(cUtil.getDateFormat(calendar));
        timelapseBo.setStartTime(cUtil.getTimeFormat(calendar));
        timelapseBo.setStartUtcDateTime(cUtil.getUTCTime());
        timelapseBo.setThemeName(cUtil.getCurrentState());
        if (TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL.equalsIgnoreCase(cUtil.getFileFormat())) {
            timelapseBo.setMovieMode("STILL");
        } else if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            timelapseBo.setMovieMode(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG24);
        } else if ("framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            timelapseBo.setMovieMode(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG30);
        }
        String menuAspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        int aspectRetio = 1;
        if (PictureSizeController.ASPECT_3_2.equalsIgnoreCase(menuAspect)) {
            aspectRetio = 10;
        } else if (PictureSizeController.ASPECT_4_3.equalsIgnoreCase(menuAspect)) {
            aspectRetio = 0;
        } else if (PictureSizeController.ASPECT_1_1.equalsIgnoreCase(menuAspect)) {
            aspectRetio = 2;
        }
        timelapseBo.setAspectRatio(aspectRetio);
        if (isSpecial()) {
            String tag = "MOVIE_1920_1080";
            try {
                tag = MovieController.getInstance().getValue(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ("MOVIE_1920_1080".equalsIgnoreCase(tag)) {
                width = 1920;
                height = 1080;
            } else {
                width = 1280;
                height = 720;
            }
            String filePath = cUtil.getAviFilePathName(calendar);
            cUtil.createFolder(calendar);
            timelapseBo.setStartFullPathFileName(filePath);
            timelapseBo.setWidth(width);
            timelapseBo.setHeight(height);
            timelapseBo.setFps(cUtil.getFrameRate());
            timelapseBo.setShootingMode("MOVIE");
        } else {
            timelapseBo.setShootingMode("STILL");
        }
        this.mCounter = 0;
        this.mTimelapseBo = timelapseBo;
        ListViewLayout.listPosition = -1;
    }

    public void saveTimeLapseBOData() {
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        TLCommonUtil cUtil = TLCommonUtil.getInstance();
        if (this.mTimelapseBo != null && this.mCounter != 0) {
            try {
            } catch (Exception e) {
                Log.d(TAG, "Database opeartion failed after shooting finished");
            }
            if (DatabaseUtil.DbResult.DB_ERROR == dataBaseOperations.importDatabase()) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                return;
            }
            this.mTimelapseBo.setEndUtcDateTime(cUtil.getUTCTime());
            this.mTimelapseBo.setShootingNumber(this.mCounter);
            dataBaseOperations.saveJpegGroup(this.mTimelapseBo);
            DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
            Log.d(TAG, "saveTimeLapseBOData" + result);
            if (DatabaseUtil.DbResult.SUCCEEDED == result) {
                dataBaseOperations.getTimeLapseBOList().add(this.mTimelapseBo);
            } else if (DatabaseUtil.DbResult.DB_ERROR == result) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
            } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
                dataBaseOperations.importDatabase();
            }
            if (TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE.equals(cUtil.getFileFormat())) {
                try {
                    if (DatabaseUtil.DbResult.DB_ERROR == dataBaseOperations.importDatabase()) {
                        DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                        return;
                    }
                    this.mTimelapseBo.setShootingMode("STILL");
                    this.mTimelapseBo.setMovieMode("STILL");
                    this.mTimelapseBo.setShootingNumber(this.mCounter);
                    dataBaseOperations.saveJpegGroup(this.mTimelapseBo);
                    DatabaseUtil.DbResult result2 = dataBaseOperations.exportDatabase();
                    Log.d(TAG, "saveTimeLapseBOData" + result2);
                    if (DatabaseUtil.DbResult.SUCCEEDED == result2) {
                        dataBaseOperations.getTimeLapseBOList().add(this.mTimelapseBo);
                    } else if (DatabaseUtil.DbResult.DB_ERROR == result2) {
                        DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                    } else if (DatabaseUtil.DbResult.SUCCEEDED != result2) {
                        dataBaseOperations.importDatabase();
                    }
                } catch (Exception e2) {
                    Log.d(TAG, "Database opeartion failed after shooting finished");
                }
            }
            this.mTimelapseBo = null;
            this.mCounter = 0;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void halt() {
        if (RunStatus.getStatus() != 4) {
            saveTimeLapseBOData();
        }
        if (TLCommonUtil.getInstance().isValidThemeForUpdatedOption()) {
            TLCommonUtil.getInstance().resetShutterSpeed();
        }
        TLCommonUtil.getInstance().setLowShutterSpeedOFF();
        TimelapsePictureEffectController.getInstance().setMiniatureForSpecialSequence(false);
        super.halt();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        return true;
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial() && !TimelapseExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void settingBeforeInitialized() {
        String expMode = ExposureModeController.getInstance().getValue(null);
        ExposureModeController emc = ExposureModeController.getInstance();
        if (!emc.isValidValue(expMode)) {
            if (1 == getRecordingMode()) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "ProgramAuto");
                return;
            }
            List<String> supported = emc.getSupportedValue(ExposureModeController.EXPOSURE_MODE);
            if (supported.contains("movie")) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "movie");
            } else if (supported.contains(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
            } else if (supported.contains(ExposureModeController.MOVIE_AUTO)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_AUTO);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isAElockedOnAutoFocus() {
        boolean isAElockedOnAutoFocus = false;
        if (TLCommonUtil.getInstance().getAETrakingStatus() == 0) {
            isAElockedOnAutoFocus = true;
        }
        AppLog.trace(TAG, "isAElockedOnAutoFocus = " + isAElockedOnAutoFocus);
        return isAElockedOnAutoFocus;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isInheritRawRecMode() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isFocusHoldEnabled() {
        return false;
    }

    public boolean isRequiredToUpdateActualMedia() {
        return this.mCounter < 1;
    }
}
