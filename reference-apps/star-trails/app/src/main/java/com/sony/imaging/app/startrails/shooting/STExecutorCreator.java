package com.sony.imaging.app.startrails.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.base.menu.controller.STPictureEffectController;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.menu.controller.MovieController;
import com.sony.imaging.app.startrails.playback.layout.ListViewLayout;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.PlainCalendar;
import java.util.List;

/* loaded from: classes.dex */
public class STExecutorCreator extends ExecutorCreator {
    private String TAG = "STExecutorCreator";
    private int mCounter = 0;
    private StarTrailsBO mStBo = null;
    private boolean mIsInitialSettingDone = false;
    private STAVICaptureProcess mCompositProcessCaptureProcess = new STAVICaptureProcess();
    private STNormalExecutor mSTNormalExecutor = new STNormalExecutor();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setFixedSettings();
        IProcess process = this.mCompositProcessCaptureProcess;
        return process;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public Class<?> getNextExecutor() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (!this.mIsRecModeChanging && !MediaNotificationManager.getInstance().isError()) {
            return STNormalExecutor.class;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.getNextExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        BaseShootingExecutor executor;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (STNormalExecutor.class.equals(clazz)) {
            executor = this.mSTNormalExecutor;
        } else {
            executor = super.getExecutor(clazz);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return executor;
    }

    public boolean isInitialSettingDone() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        return this.mIsInitialSettingDone;
    }

    private void setFixedSettings() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (isInitialSettingDone()) {
            setInitialSettingRequired(false);
            DriveModeController.getInstance().setValue("drivemode", DriveModeController.SINGLE);
            setDROToOff();
            FaceDetectionController.getInstance().setValue("off", "off");
            MeteringController.getInstance().setValue(MeteringController.MULTIMODE, FocusAreaController.MULTI);
            FocusModeController.getInstance().setValue(FocusModeController.MANUAL);
        }
        String picEffect = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, "off");
        if (picEffect.equalsIgnoreCase(PictureEffectController.MODE_MINIATURE)) {
            STPictureEffectController.getInstance().setMiniatureForSpecialSequence(true);
        } else {
            STPictureEffectController.getInstance().setMiniatureForSpecialSequence(false);
        }
        FlashController.getInstance().setValue(FlashController.FLASHMODE, "off");
        FlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, ISOSensitivityController.ISO_AUTO);
        setExposureMode();
        setNDFilterOff();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setNDFilterOff() {
        List<String> supParams = null;
        if (6 <= CameraSetting.getPfApiVersion()) {
            supParams = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedNDFilters();
        }
        if (supParams != null) {
            try {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> paramsEmpty = CameraSetting.getInstance().getEmptyParameters();
                ((CameraEx.ParametersModifier) paramsEmpty.second).setNDFilter("off");
                CameraSetting.getInstance().setParameters(paramsEmpty);
            } catch (Exception e) {
                AppLog.info(this.TAG, "NDFilter not supported");
            }
        }
    }

    private void setExposureMode() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (ModeDialDetector.hasModeDial() && !STExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setDROToOff() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DROAutoHDRController droCntl = DROAutoHDRController.getInstance();
        if (droCntl != null) {
            String dvStr = droCntl.getValue();
            if (dvStr != "off") {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void setInitialSettingRequired(boolean mIsInitialSettingDone) {
        this.mIsInitialSettingDone = mIsInitialSettingDone;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        AppLog.info(this.TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        AppLog.info(this.TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void settingBeforeInitialized() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String expMode = ExposureModeController.getInstance().getValue(null);
        ExposureModeController emc = ExposureModeController.getInstance();
        if (!emc.isValidValue(expMode)) {
            if (1 == getRecordingMode()) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
            } else {
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
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void setCounter(int counter) {
        AppLog.info(this.TAG, AppLog.getMethodName() + " Counter= " + this.mCounter);
        STConstants.sCaptureImageCounter = counter;
        this.mCounter = counter;
    }

    public StarTrailsBO getStarTrailsBO() {
        AppLog.info(this.TAG, AppLog.getMethodName() + "  " + this.mStBo);
        return this.mStBo;
    }

    public void createSTBOData() {
        int width;
        int height;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        StarTrailsBO startrailsBo = new StarTrailsBO();
        STUtility stUtil = STUtility.getInstance();
        ThemeParameterSettingUtility themeUtil = ThemeParameterSettingUtility.getInstance();
        PlainCalendar calendar = stUtil.getPinedCalender();
        startrailsBo.setStartDate(stUtil.getDateFormat(calendar));
        startrailsBo.setStartTime(stUtil.getTimeFormat(calendar));
        startrailsBo.setStartUtcDateTime(stUtil.getUTCTime());
        startrailsBo.setThemeName(STUtility.getInstance().getCurrentTrail());
        startrailsBo.setMovieMode("avi");
        startrailsBo.setAspectRatio(themeUtil.getStreakLevel());
        startrailsBo.setStreakLevel(themeUtil.getStreakLevel());
        String tag = STConstants.MOVIE_1920_1080;
        try {
            tag = MovieController.getInstance().getValue(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (STConstants.MOVIE_1920_1080.equalsIgnoreCase(tag)) {
            width = 1920;
            height = 1080;
        } else {
            width = 1280;
            height = 720;
        }
        String filePath = stUtil.getAviFilePathName(calendar);
        stUtil.createFolder(calendar);
        startrailsBo.setFullPathFileName(filePath);
        startrailsBo.setWidth(width);
        startrailsBo.setHeight(height);
        startrailsBo.setFps(themeUtil.getRecordingMode());
        startrailsBo.setShootingMode("MOVIE");
        this.mCounter = 0;
        this.mStBo = startrailsBo;
        ListViewLayout.listPosition = -1;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void saveStarTrailsBOData() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        STUtility cUtil = STUtility.getInstance();
        if (this.mStBo != null) {
            try {
            } catch (Exception e) {
                Log.d(this.TAG, "Database opeartion failed after shooting finished");
            } finally {
                STConstants.sCaptureImageCounter = 0;
                this.mStBo = null;
                this.mCounter = 0;
            }
            if (this.mCounter == 0) {
                return;
            }
            if (DatabaseUtil.DbResult.DB_ERROR == dataBaseOperations.importDatabase()) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                return;
            }
            this.mStBo.setEndUtcDateTime(cUtil.getUTCTime());
            this.mStBo.setShootingNumber(this.mCounter);
            dataBaseOperations.saveJpegGroup(this.mStBo);
            DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
            Log.d(this.TAG, "saveTimeLapseBOData" + result);
            if (DatabaseUtil.DbResult.SUCCEEDED == result) {
                dataBaseOperations.getStartrailsBOList().add(this.mStBo);
            } else if (DatabaseUtil.DbResult.DB_ERROR == result) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
            } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
                dataBaseOperations.importDatabase();
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void halt() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        PictureEffectController.getInstance().setMiniatureForSpecialSequence(false);
        saveStarTrailsBOData();
        super.halt();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
