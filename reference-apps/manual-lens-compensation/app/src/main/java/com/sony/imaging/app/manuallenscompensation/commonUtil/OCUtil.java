package com.sony.imaging.app.manuallenscompensation.commonUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.StatFs;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.menu.controller.OCExposureModeController;
import com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCAntiHandBlurController;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.FileHelper;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class OCUtil {
    private static final String EXCEPTION_REASON = "Exception in getting externalProfile from Media";
    private static final String EXTERNAL_PROFILE_DATABASE_SERVICE = "com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread";
    private static final String FORMAT_F = "F";
    private static final String INH_ID_PB_MODIFY = "INH_FEATURE_COMMON_PB_MODIFY";
    protected static final int PF_VERSION_SUPPORTING_EXIF = 10;
    private static final String TAG = "MLCUtil";
    private static OCUtil mInstance;
    private boolean isLauncherBooted = false;
    private boolean diademOVfMode = false;
    private boolean isExitExecute = false;
    private int lastCaptureMode = 1;
    private boolean isCloseMenuLayoutExecuted = false;
    private String mLastExportPath = "//PRIVATE/SONY/APP_LC/PROFILE/LENS0001.BIN";
    private boolean isSetExifDone = false;
    private ArrayList<FileInfoDetails> fileDetailsArray = null;
    private boolean isMovieRecStarted = false;

    public static boolean isSupportedByPF() {
        return 10 <= Environment.getVersionPfAPI();
    }

    public static OCUtil getInstance() {
        if (mInstance == null) {
            mInstance = new OCUtil();
        }
        return mInstance;
    }

    public boolean isLauncherBooted() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.isLauncherBooted;
    }

    public void setLauncherBooted(boolean setLauncherBooted) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isLauncherBooted = setLauncherBooted;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isDiademOVfMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.diademOVfMode;
    }

    public void setDiademOVfMode(boolean diademOVfMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.diademOVfMode = diademOVfMode;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean checkIfMenuStateClosed(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isClosed = false;
        if (bundle == null) {
            setLauncherBooted(false);
            isClosed = true;
            OpticalCompensation.sIsFirstTimeLaunched = false;
        } else if (bundle != null) {
            String itemID = bundle.getString("ItemId");
            boolean isSupportedMode = ExposureModeController.EXPOSURE_MODE.equals(itemID);
            if (itemID != null && isSupportedMode) {
                setLauncherBooted(false);
                isClosed = true;
            } else {
                setLauncherBooted(false);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isClosed;
    }

    public void printLensParametersLogs(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int param1 = params.getLevel(OCController.LIGHT_VIGNETTING);
        int param2 = params.getLevel(OCController.RED_COLOR_VIGNETTING);
        int param3 = params.getLevel(OCController.BLUE_COLOR_VIGNETTING);
        AppLog.info(TAG, "printLensParametersLogs() , <<<Shading>>>param1:" + param1 + "  ,param2: " + param2 + ",  param3: " + param3);
        int param4 = params.getLevel(OCController.RED_CHROMATIC_ABERRATION);
        int param5 = params.getLevel(OCController.BLUE_CHROMATIC_ABERRATION);
        AppLog.info(TAG, "printLensParametersLogs() , <<<CHROMATIC >>>param1:" + param4 + "  ,param2: " + param5);
        int param6 = params.getLevel(OCController.DISTORTION);
        AppLog.info(TAG, "printLensParametersLogs() , <<<DISTORION >>>param1:" + param6);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isCloseMenuLayoutExecuted() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.isCloseMenuLayoutExecuted;
    }

    public void setCloseMenuLayoutExecuted(boolean isCloseMenuLayoutExecuted) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCloseMenuLayoutExecuted = isCloseMenuLayoutExecuted;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setLensCorrectionParameter(LensCompensationParameter mParam) {
        AppLog.enter(TAG, AppLog.getMethodName());
        updateParamObjectWithChangedParameters(mParam);
        Context context = AppContext.getAppContext();
        mParam.applyCompensationParameter();
        printLensParametersLogs(mParam);
        LensCompensationParameter.saveCompensationParameter(context, mParam);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, Integer.valueOf(mParam.getId()));
        setAppTitle(mParam);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateParamObjectWithChangedParameters(LensCompensationParameter mParam) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCController controller = OCController.getInstance();
        for (int i = 0; i < OCConstants.SETTINGSSUPPORTED.size(); i++) {
            String tag = OCConstants.SETTINGSSUPPORTED.get(i);
            if (!OCController.getInstance().isSupportPictureEffect() && isShadingEffect(tag)) {
                AppLog.info(TAG, "picture effect not supported ");
            } else {
                int value = controller.getLensCorrectionLevel(tag);
                if (mParam != null) {
                    mParam.setLevel(tag, value);
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isShadingEffect(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isShading = false;
        if (tag.equals(OCController.LIGHT_VIGNETTING) || tag.equals(OCController.RED_COLOR_VIGNETTING) || tag.equals(OCController.BLUE_COLOR_VIGNETTING)) {
            isShading = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isShading;
    }

    public void setCameraPreviewMode(String priviewModeOFF, boolean setOVFPreviewMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getEmptyParameters();
        if (!isDiademOVfMode()) {
            isDiademOVfMode();
        }
        if (priviewModeOFF != null && priviewModeOFF.equalsIgnoreCase("off")) {
            ((CameraEx.ParametersModifier) params.second).setShootingPreviewMode("off");
        } else {
            String version = ScalarProperties.getString("version.platform");
            int pfVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
            int apiVersion = Integer.parseInt(version.substring(version.indexOf(".") + 1));
            if (pfVersion == 1 && apiVersion == 0) {
                ((CameraEx.ParametersModifier) params.second).setShootingPreviewMode("iris_ss_iso_aeunlock");
            }
        }
        cameraSetting.setParameters(params);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void saveAndApplyLensCorrectionParameter(LensCompensationParameter param) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (param != null) {
            Context context = AppContext.getAppContext();
            AppLog.info(TAG, "Before save in DB:: " + AppLog.getMethodName());
            getInstance().printLensParametersLogs(param);
            param.applyCompensationParameter();
            LensCompensationParameter.saveCompensationParameter(context, param);
            setAppTitle(param);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setExifData() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> camParams;
        AppLog.enter(TAG, AppLog.getMethodName());
        LensCompensationParameter params = getLensParameterObject();
        if (ModeDialDetector.hasModeDial()) {
            int position = ModeDialDetector.getModeDialPosition();
            boolean isValidposition = OCExposureModeController.getInstance().isValidDialPosition(position);
            if (position == 544 || !isValidposition || isMovieRecStarted()) {
                OCAntiHandBlurController.getInstance().updateSteadyShot(params);
                return;
            }
        }
        Log.i("EXIF Info", "param : " + params);
        Log.i("EXIF Info", "getLensParameterObject param : " + params);
        if (params != null && isSupportedByPF() && (camParams = CameraSetting.getInstance().getSupportedParameters()) != null && camParams.second != null && ((CameraEx.ParametersModifier) camParams.second).isSupportedExifInfo()) {
            CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            CameraEx.ExifInfo mExifInfo = new CameraEx.ExifInfo();
            mExifInfo.writeMode = true;
            Log.i("EXIF Info", "writeMode : " + mExifInfo.writeMode);
            if (params.mLensName != null && !params.mLensName.isEmpty() && params.mLensName.matches("\\p{ASCII}*")) {
                mExifInfo.lensName = params.mLensName + (char) 0;
            } else {
                mExifInfo.lensName = "----\u0000";
            }
            Log.i("EXIF Info", "lensName : " + mExifInfo.lensName);
            if (params.mFocalLength != null && params.mFocalLength.length() > 0) {
                mExifInfo.focalLengthNumer = Integer.parseInt(params.mFocalLength) * 10;
            } else {
                mExifInfo.focalLengthNumer = 0;
            }
            mExifInfo.focalLengthDenom = 10;
            Log.i("EXIF Info", "focalLengthNumer : " + mExifInfo.focalLengthNumer);
            Log.i("EXIF Info", "focalLengthDenom : " + mExifInfo.focalLengthDenom);
            try {
                mExifInfo.fNumberNumer = (int) ((Double.parseDouble(params.mFValue) * 100.0d) + 0.5d);
            } catch (Exception e) {
                mExifInfo.fNumberNumer = 0;
            }
            mExifInfo.fNumberDenom = 100;
            Log.i("EXIF Info", "fNumberNumer : " + mExifInfo.fNumberNumer);
            Log.i("EXIF Info", "fNumberDenom : " + mExifInfo.fNumberDenom);
            Double apexCode = Double.valueOf(OCConstants.DOUBLE_ZERO);
            mExifInfo.fNumberMinNumer = (int) (apexCode.doubleValue() * 1000.0d);
            mExifInfo.fNumberMinDenom = 1000;
            Log.i("EXIF Info", "fNumberMinNumer : " + mExifInfo.fNumberMinNumer);
            Log.i("EXIF Info", "fNumberMinDenom : " + mExifInfo.fNumberMinDenom);
            mCameraEx.setExifInfo(mExifInfo);
            this.isSetExifDone = true;
            setMovieRecStarted(false);
            Log.i("EXIF Info", "setExifData called enabled");
        }
        OCAntiHandBlurController.getInstance().updateSteadyShot(params);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isMovieRecStarted() {
        return this.isMovieRecStarted;
    }

    public void setMovieRecStarted(boolean movieRecording) {
        this.isMovieRecStarted = movieRecording;
    }

    public void resetExifDataOff() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> camParams;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isSupportedByPF() && this.isSetExifDone && (camParams = CameraSetting.getInstance().getSupportedParameters()) != null && camParams.second != null && ((CameraEx.ParametersModifier) camParams.second).isSupportedExifInfo()) {
            CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            AppLog.enter(TAG, AppLog.getMethodName() + "ExifInfo");
            CameraEx.ExifInfo mExifInfo = new CameraEx.ExifInfo();
            mExifInfo.writeMode = false;
            Log.i("EXIF Info", "writeMode : " + mExifInfo.writeMode);
            mExifInfo.lensName = "----\u0000";
            Log.i("EXIF Info", "lensName : " + mExifInfo.lensName);
            mExifInfo.focalLengthNumer = 0;
            mExifInfo.focalLengthDenom = 10;
            Log.i("EXIF Info", "focalLengthNumer : " + mExifInfo.focalLengthNumer);
            Log.i("EXIF Info", "focalLengthDenom : " + mExifInfo.focalLengthDenom);
            mExifInfo.fNumberNumer = 0;
            mExifInfo.fNumberDenom = 100;
            Log.i("EXIF Info", "fNumberNumer : " + mExifInfo.fNumberNumer);
            Log.i("EXIF Info", "fNumberDenom : " + mExifInfo.fNumberDenom);
            mExifInfo.fNumberMinNumer = 0;
            mExifInfo.fNumberMinDenom = 1000;
            Log.i("EXIF Info", "fNumberMinNumer : " + mExifInfo.fNumberMinNumer);
            Log.i("EXIF Info", "fNumberMinDenom : " + mExifInfo.fNumberMinDenom);
            mCameraEx.setExifInfo(mExifInfo);
            this.isSetExifDone = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int openMFAssitState(String callingTag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int validKeyEvent = 0;
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        String mfAssistOff = controller.getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS);
        if (isMFAssitSupport(callingTag, mfAssistOff) == 0 && !"off".equals(mfAssistOff)) {
            setCameraPreviewMode("off", isDiademOVfMode());
            List<Integer> supported = controller.getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO);
            if (supported != null && supported.size() > 0 && !"off".equals(mfAssistOff) && controller.isAvailable(null)) {
                controller.start(supported.get(0).intValue());
                validKeyEvent = 1;
                AppLog.info(callingTag, "Notification for Magnification on notify tag FocusRingRotated");
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return validKeyEvent;
    }

    private void displayCaution(int mfAssitSupport) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "mfAssitSupport Value..." + mfAssitSupport);
        switch (mfAssitSupport) {
            case 1:
                CautionUtilityClass.getInstance().requestTrigger(1472);
                break;
            case 2:
                CautionUtilityClass.getInstance().requestTrigger(33078);
                break;
            default:
                AppLog.info(TAG, TAG + AppLog.getMethodName());
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int isMFAssitSupport(String callingTag, String mfAssistOff) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int state = 0;
        String focusMode = FocusModeController.getInstance().getValue();
        if (callingTag.equals(OCConstants.OC_S1OFF_EE_STATE_KEYHANDLER) && "off".equals(mfAssistOff)) {
            state = 1;
            displayCaution(1);
        } else if (callingTag.equals(OCConstants.OC_S1OFF_EE_STATE_KEYHANDLER) && !FocusModeController.MANUAL.equals(FocusModeController.getInstance().getValue())) {
            state = 2;
            displayCaution(2);
        } else if (!"off".equals(mfAssistOff) && callingTag.equals(OCConstants.OC_S1ON_EE_STATE) && (FocusModeController.MANUAL.equals(focusMode) || FocusModeController.DMF.equals(focusMode))) {
            state = 0;
        } else if (callingTag.equals(OCConstants.OC_S1OFF_EE_STATE) && "off".equals(mfAssistOff)) {
            state = -1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return state;
    }

    public void setAppTitle(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String mName = params.mLensName;
        AppNameView.setText(mName);
        AppNameView.show(true);
        OCAppNameFocalValue.setText(getFocalValue(params));
        OCAppNameFocalValue.show(true);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isExitExecute() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.isExitExecute;
    }

    public void setExitExecute(boolean isExitExecute) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isExitExecute = isExitExecute;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getFocalValue(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        StringBuffer focalValue = new StringBuffer(" ");
        boolean b = false;
        try {
            if (!params.mFocalLength.equalsIgnoreCase("")) {
                focalValue.append(params.mFocalLength);
                focalValue.append(OCConstants.FOCALLENGTH_UNIT_STRING);
                b = true;
            }
            int dotIndex = params.mFValue.indexOf(".");
            String fValueIntPart = params.mFValue.substring(0, dotIndex);
            String fValueDecPart = params.mFValue.substring(dotIndex + 1);
            boolean isIntPartBlank = " ".equals(fValueIntPart);
            boolean isDecPartBlank = " ".equals(fValueDecPart);
            if (!isIntPartBlank || !isDecPartBlank) {
                if (b) {
                    focalValue.append("/");
                }
                focalValue.append(" F");
                if (!isIntPartBlank && !isDecPartBlank) {
                    focalValue.append(fValueIntPart).append(".").append(fValueDecPart);
                } else if (!isIntPartBlank && isDecPartBlank) {
                    focalValue.append(fValueIntPart);
                } else if (isIntPartBlank && !isDecPartBlank) {
                    focalValue.append("0").append(".").append(fValueDecPart);
                }
            }
        } catch (Exception exp) {
            AppLog.error(TAG, AppLog.getMethodName() + ", Exception message: " + exp);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return focalValue.toString();
    }

    public void setShadingEffectOff() {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCController controller = OCController.getInstance();
        controller.setLensCorrectionLevel(OCController.LIGHT_VIGNETTING, 0);
        controller.setLensCorrectionLevel(OCController.RED_COLOR_VIGNETTING, 0);
        controller.setLensCorrectionLevel(OCController.BLUE_COLOR_VIGNETTING, 0);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setSelectedProfileEffect() {
        LensCompensationParameter mParam;
        AppLog.enter(TAG, AppLog.getMethodName());
        int lastAppliedParamDbID = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, -1);
        if (lastAppliedParamDbID != -1 && (mParam = LensCompensationParameter.queryPreviousProfileParam(AppContext.getAppContext(), lastAppliedParamDbID)) != null) {
            mParam.applyCompensationParameter();
            OCController controller = OCController.getInstance();
            controller.setLensCorrectionLevel(OCController.LIGHT_VIGNETTING, mParam.getLevel(OCController.LIGHT_VIGNETTING));
            controller.setLensCorrectionLevel(OCController.RED_COLOR_VIGNETTING, mParam.getLevel(OCController.RED_COLOR_VIGNETTING));
            controller.setLensCorrectionLevel(OCController.BLUE_COLOR_VIGNETTING, mParam.getLevel(OCController.BLUE_COLOR_VIGNETTING));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getLastCaptureRecMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.lastCaptureMode;
    }

    public void setLastCaptureMode(int recMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.lastCaptureMode = recMode;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void openMoviePlaybackDisabledCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setLastCaptureMode(1);
        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_PLAYBACK);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void creatDefaultProfile(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        LensCompensationParameter params2 = LensCompensationParameter.createLensCompensationParameter();
        getInstance().saveAndApplyLensCorrectionParameter(params2);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, Integer.valueOf(params2.getId()));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getFilePathOnMedia() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            if (2 == mMediaId) {
                mFilePathOnMedia = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + OCConstants.MS_CARD_PATH;
            } else if (1 == mMediaId) {
                mFilePathOnMedia = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + OCConstants.SD_CARD_PATH;
            }
            AppLog.trace("YES", "========== Files path on Media regarding this application ============== " + mFilePathOnMedia);
            AppLog.exit(TAG, AppLog.getMethodName());
        }
        return mFilePathOnMedia;
    }

    public String writeToExternalMedia(byte[] data) throws Exception {
        AppLog.enter(TAG, AppLog.getMethodName());
        try {
            String extPath = getFilePathOnMedia();
            File extDir = new File(extPath);
            if (!FileHelper.exists(extDir)) {
                if (FileHelper.mkdirs(extDir)) {
                    Log.d(TAG, "================= Directory Created Successfully ==================== ");
                } else {
                    Log.e(TAG, "================= Directory not created ================== ");
                }
            } else {
                Log.d(TAG, "================= Directory all ready exist to store files==================== ");
            }
            try {
                String fileName = getFileNameToAdd(extDir);
                if (fileName != null && !fileName.equalsIgnoreCase("")) {
                    FileOutputStream fos = null;
                    try {
                        try {
                            File extFile = new File(extPath + File.separator + fileName);
                            FileOutputStream fos2 = new FileOutputStream(extFile);
                            try {
                                fos2.write(data);
                                fos2.flush();
                                fos2.close();
                                FileOutputStream fos3 = null;
                                try {
                                    ByteDataAnalyser byteDataAnalyser = new ByteDataAnalyser(data);
                                    FileInfoDetails fileInfoDetails = new FileInfoDetails(extFile, byteDataAnalyser);
                                    addProfile(fileInfoDetails);
                                    if (0 != 0) {
                                        try {
                                            fos3.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            throw new Exception(e);
                                        }
                                    }
                                    AppLog.exit(TAG, AppLog.getMethodName());
                                    if (extPath != null) {
                                        if (extPath.contains(OCConstants.MS_CARD_PATH)) {
                                            return OCConstants.MS_CARD_PATH + File.separator + fileName;
                                        }
                                        return OCConstants.SD_CARD_PATH + File.separator + fileName;
                                    }
                                    return fileName;
                                } catch (Exception e2) {
                                    AppLog.trace(TAG, EXCEPTION_REASON);
                                    throw new Exception(e2);
                                }
                            } catch (Exception e3) {
                                throw new Exception("null file name exception");
                            } catch (Throwable th) {
                                th = th;
                                fos = fos2;
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e4) {
                                        e4.printStackTrace();
                                        throw new Exception(e4);
                                    }
                                }
                                throw th;
                            }
                        } catch (Exception e5) {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } else {
                    throw new Exception("null file name exception");
                }
            } catch (Exception e6) {
                AppLog.trace(TAG, EXCEPTION_REASON);
                throw new Exception(e6);
            }
        } catch (Exception e7) {
            throw new Exception(e7);
        }
    }

    public boolean checkforAppliedProfile() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isNoProfile = false;
        Context context = AppContext.getAppContext();
        Cursor lenProfileCursor = LensCompensationParameter.queryAllProfilesFromDatabase(context);
        if (lenProfileCursor != null) {
            if (lenProfileCursor.getCount() == 0) {
                isNoProfile = true;
            }
            if (lenProfileCursor != null) {
                lenProfileCursor.close();
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isNoProfile;
    }

    public boolean isDBUpdateServiceRunning() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ActivityManager manager = (ActivityManager) AppContext.getAppContext().getSystemService("activity");
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EXTERNAL_PROFILE_DATABASE_SERVICE.equals(service.service.getClassName())) {
                AppLog.exit(TAG, AppLog.getMethodName());
                return true;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    public boolean checkMediaStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isCard = true;
        int state = MediaNotificationManager.getInstance().getMediaState();
        AppLog.enter(TAG, AppLog.getMethodName() + " " + state);
        if (state == 0) {
            isCard = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isCard;
    }

    public boolean isMediaErr() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        AppLog.exit(TAG, AppLog.getMethodName());
        return AvailableInfo.isInhibition(INH_ID_PB_MODIFY, media[0]);
    }

    public boolean checkMemorySpace() {
        if (!MediaNotificationManager.getInstance().isMounted() || isSpaceAvailableInMemoryCard()) {
            return false;
        }
        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_NO_SPACE_ON_MEMORY_CARD);
        return true;
    }

    public LensCompensationParameter getLensParameterObject() {
        LensCompensationParameter lensParameter = null;
        int lastAppliedParamDbID = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, -1);
        if (-1 != lastAppliedParamDbID) {
            lensParameter = LensCompensationParameter.queryPreviousProfileParam(AppContext.getAppContext(), lastAppliedParamDbID);
        }
        if (lensParameter == null) {
            LensCompensationParameter lensParameter2 = LensCompensationParameter.createLensCompensationParameter();
            return lensParameter2;
        }
        return lensParameter;
    }

    public boolean isSpaceAvailableInMemoryCard() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bIsAvailable = false;
        AvailableInfo.update();
        try {
            StatFs statFs = new StatFs(android.os.Environment.getExternalStorageDirectory().getPath());
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            AppLog.checkIf(TAG, "Available Size: " + availableSize);
            if (availableSize >= 512) {
                bIsAvailable = true;
            }
            AppLog.exit(TAG, AppLog.getMethodName());
            return bIsAvailable;
        } catch (Exception e) {
            AppLog.checkIf(TAG, "Exception: " + e.toString());
            return false;
        }
    }

    public void synchDBonMediaChange() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ExternalProfilesDatabaseUpdateThread ext = new ExternalProfilesDatabaseUpdateThread();
        new Thread(ext).start();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean externalProfilesFileExist() {
        File externalProfileDir;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isProfilExist = false;
        String filePath = getInstance().getFilePathOnMedia();
        if (filePath != null && (externalProfileDir = new File(filePath)) != null && externalProfileDir.exists()) {
            String[] array = externalProfileDir.list();
            if (array.length != 0) {
                isProfilExist = true;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isProfilExist;
    }

    public boolean isMaxProfileLimitExceed() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int counter = 0;
        String filePath = getFilePathOnMedia();
        File extDir = new File(filePath);
        Log.d("MediaCheckingTime", "start checking isMaxProfileLimitExceed");
        if (filePath != null) {
            String[] file_name_list = extDir.list();
            if (file_name_list != null) {
                List<String> files = Arrays.asList(file_name_list);
                for (int i = 1; i <= 200; i++) {
                    String match_file_name = OCConstants.FILE_PREFIX + String.format(OCConstants.FORMAT, Integer.valueOf(i)) + OCConstants.FILE_EXTENTION;
                    if (files.contains(match_file_name)) {
                        counter++;
                    }
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        boolean isMaxLimitReached = counter == 200;
        Log.d("MediaCheckingTime", "start checking9 count " + counter);
        Log.d("MediaCheckingTime", "start checking10 isProfilExist " + isMaxLimitReached);
        Log.d("MediaCheckingTime", "start checking isMaxProfileLimitExceed END");
        return isMaxLimitReached;
    }

    public boolean isValidName(String match_file_name) {
        int fileLength = match_file_name.length();
        if (fileLength != 12 || !match_file_name.startsWith(OCConstants.FILE_PREFIX) || !match_file_name.endsWith(OCConstants.FILE_EXTENTION)) {
            return false;
        }
        String fileIndex = match_file_name.substring(5, 8);
        int fileNumber = Integer.parseInt(fileIndex);
        if (fileNumber <= 0 || fileNumber > 200) {
            return false;
        }
        return true;
    }

    public String getLastExportedProfileText() {
        return this.mLastExportPath;
    }

    public void setLastExportedProfileText(String exportPath) {
        this.mLastExportPath = exportPath;
    }

    public ArrayList<FileInfoDetails> getFileArray() {
        if (this.fileDetailsArray == null) {
            this.fileDetailsArray = new ArrayList<>();
        }
        return this.fileDetailsArray;
    }

    public void setFileArray(ArrayList<FileInfoDetails> fileDetailsArray) {
        if (this.fileDetailsArray != null) {
            deleteAllProfile();
        }
        this.fileDetailsArray = fileDetailsArray;
    }

    private String getFileNameToAdd(File externalDirectory) {
        String match_file_name = null;
        for (int i = 0; i < 200; i++) {
            match_file_name = OCConstants.FILE_PREFIX + String.format(OCConstants.FORMAT, Integer.valueOf(i + 1)) + OCConstants.FILE_EXTENTION;
            File ff = new File(externalDirectory, match_file_name);
            if (!ff.exists()) {
                break;
            }
        }
        return match_file_name;
    }

    public void addProfile(FileInfoDetails fileInfoDetails) {
        getFileArray();
        int add_position = -1;
        int i = 0;
        while (true) {
            if (i >= this.fileDetailsArray.size()) {
                break;
            }
            if (this.fileDetailsArray.get(i).getFile().lastModified() < fileInfoDetails.getFile().lastModified()) {
                i++;
            } else {
                add_position = i;
                break;
            }
        }
        if (add_position == -1) {
            this.fileDetailsArray.add(fileInfoDetails);
        } else {
            this.fileDetailsArray.add(add_position, fileInfoDetails);
        }
    }

    public void deleteProfile(String file_name) {
        getFileArray();
        for (int i = 0; i < this.fileDetailsArray.size(); i++) {
            if (this.fileDetailsArray.get(i).getFile().getName().equalsIgnoreCase(file_name)) {
                this.fileDetailsArray.remove(i);
                return;
            }
        }
    }

    public void deleteAllProfile() {
        if (this.fileDetailsArray != null) {
            for (int i = 0; i < this.fileDetailsArray.size(); i++) {
                File file = this.fileDetailsArray.get(i).getFile();
                if (file != null) {
                }
                ByteDataAnalyser analyser = this.fileDetailsArray.get(i).getByteDataAnalyser();
                if (analyser != null) {
                }
                this.fileDetailsArray.remove(i);
            }
            this.fileDetailsArray.clear();
            this.fileDetailsArray = null;
        }
    }

    public boolean isOldLensAttached() {
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        if (mCameraEx == null) {
            return false;
        }
        CameraEx.LensInfo lensInfo = mCameraEx.getLensInfo();
        if (lensInfo != null) {
            return false;
        }
        return true;
    }

    public String getAdequateApertureValue() {
        String fValue = null;
        LensCompensationParameter mParam = getLensParameterObject();
        if (mParam != null) {
            fValue = mParam.mFValue;
        }
        String intPart = String.valueOf(0);
        String decimalPart = "";
        if (fValue != null) {
            String fValue2 = fValue.trim();
            if (fValue2.contains(".") && fValue2.length() != 1) {
                int dotIndex = fValue2.indexOf(".");
                String fValueIntPart = fValue2.substring(0, dotIndex);
                String fValueDecPart = fValue2.substring(dotIndex + 1);
                if (!fValueIntPart.isEmpty() || !fValueDecPart.isEmpty()) {
                    String fValueIntPart2 = fValueIntPart.trim();
                    if (!fValueIntPart2.isEmpty()) {
                        intPart = fValueIntPart2;
                    }
                    String fValueDecPart2 = fValueDecPart.trim();
                    if (!fValueDecPart2.isEmpty()) {
                        decimalPart = "." + fValueDecPart2;
                    }
                }
            }
        }
        String aperture = "F" + intPart + decimalPart;
        return aperture;
    }
}
