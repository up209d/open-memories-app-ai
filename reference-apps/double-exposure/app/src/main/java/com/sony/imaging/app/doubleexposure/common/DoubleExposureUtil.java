package com.sony.imaging.app.doubleexposure.common;

import android.widget.TextView;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureDROAutoHDRController;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureFocusModeController;
import com.sony.imaging.app.doubleexposure.menu.controller.ModeSelectionController;
import com.sony.imaging.app.doubleexposure.menu.controller.ReverseController;
import com.sony.imaging.app.doubleexposure.menu.controller.RotationController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class DoubleExposureUtil {
    public static final int BLENDING_EFFECT_LIGHTEN = 4;
    public static final int BLENDING_EFFECT_MULTIPLY = 3;
    public static final int BLENDING_EFFECT_SCREEN = 2;
    public static final int BLENDING_EFFECT_WEIGHT = 1;
    private BackUpUtil mBackUpUtil;
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposureUtil sInstance = null;
    private static boolean mInitFocusMode = false;
    private final int WEIGHT_MEAN_VALUE = 165;
    private String mCurrentShootingScreen = null;
    private String mCurrentMenuSelectionScreen = null;
    private String mCurrentMenuLayout = null;
    private boolean mbSaveSetting = true;
    private boolean mbTransitionFlag = false;
    private String mFirstImageTransition = null;
    private boolean mImageSelection = false;
    private boolean mIsTurnedEVDialInPlayback = false;
    private boolean mIsTurnedEVDial = false;
    private TextView mTxtVwButtonOk = null;
    private TextView mTxtVwButtonCancel = null;
    private String mExpsoureCompensationFirstShooting = "0";
    private String mExpsoureCompensationSecondShooting = "0";
    private String mRotationFirstShooting = "Off";
    private String mReverseFirstShooting = "Off";
    private String mRotationSecondShooting = "Off";
    private String mReverseSecondShooting = "Off";
    private String mGuideValuesFirstShooting = null;
    private String mGuideValuesSecondShooting = null;
    private String mFirstImageAspectRatio = null;
    private String mCameraSettingsAspectRatio = null;
    private String mCameraSettingsImageSize = null;
    private String mPictureEffectOptionValue = null;
    private List<String> mSupportedAspectRatio = null;
    private List<String> mSupportedImageSize = null;
    private String mCameraSettingsImageQuality = null;
    private String mCameraSettingsValues = null;
    private int mFirstImageFileNumber = -1;
    private int mFirstImageFolderNumber = -1;
    private String mLastSelectedTheme = null;
    private String GUIDE_VALUE_FLASE = DriveModeController.REMOTE_OFF;
    private String GUIDE_VALUE_TRUE = "true";
    private List<String> mGuideValuesListFirstShooting = null;
    private List<String> mGuideValuesListSecondShooting = null;

    private DoubleExposureUtil() {
        this.mBackUpUtil = null;
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    public static DoubleExposureUtil getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposureUtil();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public void setTurnedEVDial(boolean isTurned) {
        this.mIsTurnedEVDial = isTurned;
    }

    public boolean isTurnedEVDial() {
        return this.mIsTurnedEVDial;
    }

    public void setCurrentShootingScreen(String currentShootingScreen) {
        this.mCurrentShootingScreen = currentShootingScreen;
    }

    public String getCurrentShootingScreen() {
        return this.mCurrentShootingScreen;
    }

    public void setFirstImageTransition(String firstImageTransition) {
        this.mFirstImageTransition = firstImageTransition;
    }

    public String getFirstImageTransition() {
        return this.mFirstImageTransition;
    }

    public String getCurrentMenuSelectionScreen() {
        return this.mCurrentMenuSelectionScreen;
    }

    public void setCurrentMenuSelectionScreen(String mcurrentMenuSelectionScreen) {
        this.mCurrentMenuSelectionScreen = mcurrentMenuSelectionScreen;
    }

    public void setCurrentMenuLayout(String currentMenuLayout) {
        this.mCurrentMenuLayout = currentMenuLayout;
    }

    public String getCurrentMenuLayout() {
        return this.mCurrentMenuLayout;
    }

    public void setSaveSettings(boolean saveSettings) {
        this.mbSaveSetting = saveSettings;
    }

    public boolean IsSaveSettings() {
        return this.mbSaveSetting;
    }

    public void setTransitionFlag(boolean flag) {
        this.mbTransitionFlag = flag;
    }

    public boolean IsTransitionFlag() {
        return this.mbTransitionFlag;
    }

    public void setExposureCompensationFirstShooting(String exposureCompensation) {
        this.mExpsoureCompensationFirstShooting = exposureCompensation;
    }

    public String getExposureCompensationFirstShooting() {
        return this.mExpsoureCompensationFirstShooting;
    }

    public void setExposureCompensationSecondShooting(String exposureCompensation) {
        this.mExpsoureCompensationSecondShooting = exposureCompensation;
    }

    public String getExposureCompensationSecondShooting() {
        return this.mExpsoureCompensationSecondShooting;
    }

    public void setFirstImageAspectRatio(String aspectRatio) {
        this.mFirstImageAspectRatio = aspectRatio;
    }

    public String getRotationFirstShooting() {
        return this.mRotationFirstShooting;
    }

    public void setRotationFirstShooting(String rotation) {
        this.mRotationFirstShooting = rotation;
    }

    public String getRotationSecondShooting() {
        return this.mRotationSecondShooting;
    }

    public void setRotationSecondShooting(String rotation) {
        this.mRotationSecondShooting = rotation;
    }

    public String getReverseFirstShooting() {
        return this.mReverseFirstShooting;
    }

    public void setReverseFirstShooting(String reverse) {
        this.mReverseFirstShooting = reverse;
    }

    public String getReverseSecondShooting() {
        return this.mReverseSecondShooting;
    }

    public void setReverseSecondShooting(String reverse) {
        this.mReverseSecondShooting = reverse;
    }

    public String getFirstImageAspectRatio() {
        return this.mFirstImageAspectRatio;
    }

    public void setCameraSettingsAspectRatio(String aspectRatio) {
        this.mCameraSettingsAspectRatio = aspectRatio;
    }

    public String getCameraSettingsImageSize() {
        return this.mCameraSettingsImageSize;
    }

    public void setCameraSettingsImageSize(String imageSize) {
        this.mCameraSettingsImageSize = imageSize;
    }

    public String getCameraSettingsAspectRatio() {
        return this.mCameraSettingsAspectRatio;
    }

    public String getImageQuality() {
        return this.mCameraSettingsImageQuality;
    }

    public void setImageQuality(String imageQuality) {
        this.mCameraSettingsImageQuality = imageQuality;
    }

    public List<String> getSupportedAspectRatio() {
        return this.mSupportedAspectRatio;
    }

    public void setSupportedAspectRatio(List<String> supportedAspectRatio) {
        this.mSupportedAspectRatio = supportedAspectRatio;
    }

    public List<String> getSupportedImageSize() {
        return this.mSupportedImageSize;
    }

    public void setSupportedImageSize(List<String> supportedImageSize) {
        this.mSupportedImageSize = supportedImageSize;
    }

    public TextView getButtonOk() {
        return this.mTxtVwButtonOk;
    }

    public TextView getButtonCancel() {
        return this.mTxtVwButtonCancel;
    }

    public void setButtonOk(TextView buttonOk) {
        this.mTxtVwButtonOk = buttonOk;
    }

    public void setButtonCancel(TextView buttonCancel) {
        this.mTxtVwButtonCancel = buttonCancel;
    }

    public int getFirstImageFileNumber() {
        return this.mFirstImageFileNumber;
    }

    public void setFirstImageFileNumber(int firstImageFileNumber) {
        this.mFirstImageFileNumber = firstImageFileNumber;
    }

    public int getFirstImageFolderNumber() {
        return this.mFirstImageFolderNumber;
    }

    public void setFirstImageFolderNumber(int firstImageFolderNumber) {
        this.mFirstImageFolderNumber = firstImageFolderNumber;
    }

    public boolean IsTurnedEVDialInPlayback() {
        return this.mIsTurnedEVDialInPlayback;
    }

    public void setTurnedEVDialInPlayback(boolean turnedEVDialInPlayback) {
        this.mIsTurnedEVDialInPlayback = turnedEVDialInPlayback;
    }

    public String getLastSelectedTheme() {
        return this.mLastSelectedTheme;
    }

    public String getPictureEffectOptionValue() {
        return this.mPictureEffectOptionValue;
    }

    public void setPictureEffectOptionValue(String pictureEffectOptionValue) {
        this.mPictureEffectOptionValue = pictureEffectOptionValue;
    }

    public boolean getRecommendedValues() {
        String isoValue;
        String meteringMode;
        String whiteBalance;
        String focusMode;
        String creativeStyle;
        String faceDetection;
        String pictureEffect;
        String droHdr;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = true;
        DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
        cameraSettings.setShootMode(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE));
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_ISO_SPEED_RATE")) {
            isoValue = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
        } else {
            DoubleExposureCameraSettings tempCameraSettings = new DoubleExposureCameraSettings();
            tempCameraSettings.setCameraSettingsValues(this.mCameraSettingsValues);
            isoValue = tempCameraSettings.getMeteringMode();
        }
        cameraSettings.setISOSensivity(isoValue);
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_AE_METER_MODE")) {
            meteringMode = MeteringController.getInstance().getValue("MeteringMode");
        } else {
            DoubleExposureCameraSettings tempCameraSettings2 = new DoubleExposureCameraSettings();
            tempCameraSettings2.setCameraSettingsValues(this.mCameraSettingsValues);
            meteringMode = tempCameraSettings2.getMeteringMode();
        }
        cameraSettings.setMeteringMode(meteringMode);
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_WB_MODE")) {
            whiteBalance = WhiteBalanceController.getInstance().getValue(WhiteBalanceController.WHITEBALANCE);
            getCustomWhiteBalanceOptionValue();
        } else {
            DoubleExposureCameraSettings tempCameraSettings3 = new DoubleExposureCameraSettings();
            tempCameraSettings3.setCameraSettingsValues(this.mCameraSettingsValues);
            whiteBalance = tempCameraSettings3.getMeteringMode();
        }
        cameraSettings.setWhiteBalance(whiteBalance);
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE")) {
            focusMode = FocusModeController.getInstance().getValue();
        } else {
            DoubleExposureCameraSettings tempCameraSettings4 = new DoubleExposureCameraSettings();
            tempCameraSettings4.setCameraSettingsValues(this.mCameraSettingsValues);
            focusMode = tempCameraSettings4.getFocusMode();
        }
        cameraSettings.setFocusMode(focusMode);
        try {
            if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_FACE_DETECTION_MODE")) {
                faceDetection = FaceDetectionController.getInstance().getValue();
            } else {
                DoubleExposureCameraSettings tempCameraSettings5 = new DoubleExposureCameraSettings();
                tempCameraSettings5.setCameraSettingsValues(this.mCameraSettingsValues);
                faceDetection = tempCameraSettings5.getFaceDetection();
            }
            cameraSettings.setFaceDetection(faceDetection);
            if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_PICTURE_EFFECT_CMD")) {
                AppLog.info(TAG, "Haita of PictureEffect is off");
                pictureEffect = PictureEffectController.getInstance().getValue(PictureEffectController.PICTUREEFFECT);
                this.mPictureEffectOptionValue = PictureEffectController.getInstance().getValue(pictureEffect);
            } else {
                DoubleExposureCameraSettings tempCameraSettings6 = new DoubleExposureCameraSettings();
                tempCameraSettings6.setCameraSettingsValues(this.mCameraSettingsValues);
                pictureEffect = tempCameraSettings6.getPictureEffect();
            }
            cameraSettings.setPictureEffect(pictureEffect);
            if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_DRO_LEVEL") || !AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_HDR_LEVEL")) {
                droHdr = DoubleExposureDROAutoHDRController.getInstance().getValue();
            } else {
                DoubleExposureCameraSettings tempCameraSettings7 = new DoubleExposureCameraSettings();
                tempCameraSettings7.setCameraSettingsValues(this.mCameraSettingsValues);
                droHdr = tempCameraSettings7.getDroHDr();
            }
            cameraSettings.setDroHDr(droHdr);
            cameraSettings.setExposureCompensation(ExposureCompensationController.getInstance().getValue("ExposureCompensation"));
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
            bRetVal = false;
        }
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_COLOR_MODE")) {
            AppLog.info(TAG, "Haita of Creative Style is off");
            creativeStyle = CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE);
        } else {
            DoubleExposureCameraSettings tempCameraSettings8 = new DoubleExposureCameraSettings();
            tempCameraSettings8.setCameraSettingsValues(this.mCameraSettingsValues);
            creativeStyle = tempCameraSettings8.getCreativeStyle();
        }
        cameraSettings.setCreativeStyle(creativeStyle);
        try {
            cameraSettings.setRotation(RotationController.getInstance().getValue("Rotation"));
            cameraSettings.setReverse(ReverseController.getInstance().getValue(ReverseController.TAG_REVERSE));
        } catch (Exception e2) {
            AppLog.error(TAG, e2.toString());
            bRetVal = false;
        }
        this.mCameraSettingsValues = cameraSettings.getCameraSettingsValues();
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public void getFocusMode() {
        DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
        cameraSettings.setFocusMode(DoubleExposureFocusModeController.getInstance().getValue(FocusModeController.TAG_FOCUS_MODE));
    }

    public void setRecommendedValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
        if (!selectedTheme.equalsIgnoreCase("Manual")) {
            cameraSettings.setDefaultFocusMode("af-s");
        }
        if (selectedTheme.equalsIgnoreCase("Manual")) {
            AppLog.info(TAG, " Values = " + this.mCameraSettingsValues);
            cameraSettings.setCameraSettingsValues(this.mCameraSettingsValues);
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setFaceDetection("off");
                cameraSettings.setDroHDr("off");
                cameraSettings.setPictureEffect(PictureEffectController.MODE_ROUGH_MONO);
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
                cameraSettings.setRotation(this.mRotationFirstShooting);
                cameraSettings.setReverse(this.mReverseFirstShooting);
            } else {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
                cameraSettings.setRotation(this.mRotationSecondShooting);
                cameraSettings.setReverse(this.mReverseSecondShooting);
            }
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setCreativeStyle("landscape");
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
                cameraSettings.setRotation(this.mRotationFirstShooting);
                cameraSettings.setReverse(this.mReverseFirstShooting);
            } else {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
                cameraSettings.setRotation(this.mRotationSecondShooting);
                cameraSettings.setReverse(this.mReverseSecondShooting);
            }
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
                cameraSettings.setRotation(this.mRotationFirstShooting);
                cameraSettings.setReverse(this.mReverseFirstShooting);
            } else {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
                cameraSettings.setRotation(this.mRotationSecondShooting);
                cameraSettings.setReverse(this.mReverseSecondShooting);
            }
        } else if (selectedTheme.equalsIgnoreCase("Rotation")) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
            } else {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
            }
            cameraSettings.setRotation("On");
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
            } else {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
            }
            cameraSettings.setReverse(ReverseController.HORIZONTAL);
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            if (this.mCurrentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationFirstShooting);
                cameraSettings.setRotation(this.mRotationFirstShooting);
                cameraSettings.setReverse(this.mReverseFirstShooting);
            } else {
                if (this.mExpsoureCompensationSecondShooting == null) {
                    this.mExpsoureCompensationSecondShooting = "0";
                }
                cameraSettings.setExposureCompensation(this.mExpsoureCompensationSecondShooting);
                getInstance().setInitFocusMode(true);
                cameraSettings.setDefaultFocusMode(FocusModeController.MANUAL);
                cameraSettings.setRotation(this.mRotationSecondShooting);
                cameraSettings.setReverse(this.mReverseSecondShooting);
            }
        }
        cameraSettings.setCameraSetting();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isInitFocusMode() {
        return mInitFocusMode;
    }

    public void setInitFocusMode(boolean isInit) {
        mInitFocusMode = isInit;
    }

    public boolean isImageSelection() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, " mImageSelection = " + this.mImageSelection);
        AppLog.enter(TAG, AppLog.getMethodName());
        return this.mImageSelection;
    }

    public void setImageSelection(boolean imageSelection) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mImageSelection = imageSelection;
        AppLog.info(TAG, " mImageSelection = " + this.mImageSelection);
        AppLog.enter(TAG, AppLog.getMethodName());
    }

    public void setLastSelectedTheme(String lastSelectedTheme) {
        this.mLastSelectedTheme = lastSelectedTheme;
    }

    public int getBlendingMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int blendMode = 0;
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            blendMode = 4;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            blendMode = 1;
            DESA.getInstance().setWeightMeanValue(165);
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            blendMode = 3;
        } else if (selectedTheme.equalsIgnoreCase("Rotation") || selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR) || selectedTheme.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            blendMode = 2;
        } else if (selectedTheme.equalsIgnoreCase("Manual")) {
            String selectedBlendMode = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_SELECTED_MODE, ModeSelectionController.ADD);
            blendMode = ModeSelectionController.getInstance().getSupportedValue(ModeSelectionController.MODESELECTION).indexOf(selectedBlendMode) - 1;
            if (blendMode == 1) {
                int weightAverageValue = BackUpUtil.getInstance().getPreferenceInt(DoubleExposureBackUpKey.KEY_WEIGHTED_AVERAGE_VALUE, 5);
                DESA.getInstance().setWeightMeanValue((int) (weightAverageValue * 25.5f));
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return blendMode;
    }

    public void startKikiLog(int kikilogId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        AppLog.info(TAG, "kikilogStart");
        Kikilog.setUserLog(kikilogId, options);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateDefaultValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equalsIgnoreCase(selectedTheme)) {
            if (ThemeSelectionController.TEXTURE.equalsIgnoreCase(selectedTheme)) {
                int modelCategory = ScalarProperties.getInt("model.category");
                if (modelCategory == 0 || 1 == modelCategory || 1 == modelCategory) {
                    this.mExpsoureCompensationFirstShooting = DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_ILDC;
                    this.mExpsoureCompensationSecondShooting = DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_ILDC;
                } else {
                    this.mExpsoureCompensationFirstShooting = DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_DSC;
                    this.mExpsoureCompensationSecondShooting = DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_DSC;
                }
            } else if (ThemeSelectionController.SKY.equalsIgnoreCase(selectedTheme)) {
                this.mExpsoureCompensationFirstShooting = "0";
                this.mExpsoureCompensationSecondShooting = DoubleExposureConstant.EXPOSURE_COMPENSATION_SKY_DEFAULT_VALUE;
            } else {
                this.mExpsoureCompensationFirstShooting = "0";
                this.mExpsoureCompensationSecondShooting = "0";
            }
            this.mRotationFirstShooting = "Off";
            this.mReverseFirstShooting = "Off";
            this.mRotationSecondShooting = "Off";
            this.mReverseSecondShooting = "Off";
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public OptimizedImage getScaledOptimizedImage(OptimizedImage optimizedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (optimizedImage != null) {
            int imageWidth = optimizedImage.getWidth();
            int imageHeight = optimizedImage.getHeight();
            String firstImageAspectRatio = getAspectRatio(imageWidth, imageHeight);
            String firstPictureSize = null;
            if (firstImageAspectRatio != null) {
                firstPictureSize = getPictureSize(firstImageAspectRatio, imageHeight, imageWidth);
            } else {
                AppLog.info(TAG, "Aspect Ratio not supported");
            }
            if (this.mCameraSettingsImageSize != null && firstPictureSize != null && !this.mCameraSettingsImageSize.equalsIgnoreCase(firstPictureSize)) {
                ScalarProperties.PictureSize pictureSize = getNewPictureSize();
                optimizedImage = DoubleExposureImageUtil.getInstance().getScaledOptimizedImage(optimizedImage, true, pictureSize.width, pictureSize.height);
            } else {
                AppLog.info(TAG, "Both Picuture Sizes are same");
            }
            AppLog.exit(TAG, AppLog.getMethodName());
            return optimizedImage;
        }
        AppLog.info(TAG, "Optimized image is null");
        return optimizedImage;
    }

    public String getAspectRatio(float width, float height) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String aspectRatio = null;
        float aspect = width / height;
        if (1.4d <= aspect && aspect < 1.6d) {
            aspectRatio = PictureSizeController.ASPECT_3_2;
        } else if (1.6d <= aspect && aspect < 1.8d) {
            aspectRatio = PictureSizeController.ASPECT_16_9;
        } else if (1.2d <= aspect && aspect < 1.4d) {
            aspectRatio = PictureSizeController.ASPECT_4_3;
        } else if (0.9d <= aspect && aspect < 1.1d) {
            aspectRatio = PictureSizeController.ASPECT_1_1;
        }
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        AppLog.exit(TAG, AppLog.getMethodName());
        return aspectRatio;
    }

    public String getPictureSize(String aspectRatio, int width, int height) {
        String imageSize;
        AppLog.enter(TAG, AppLog.getMethodName());
        int indexApectRatio = getAspectRatioIndex(aspectRatio);
        if (-1 == indexApectRatio) {
            return null;
        }
        List<ScalarProperties.PictureSize> pictureSizeList = ScalarProperties.getSupportedPictureSizes();
        ScalarProperties.PictureSize pictureSizeS = null;
        ScalarProperties.PictureSize pictureSizeM = null;
        ScalarProperties.PictureSize pictureSizeL = null;
        if (pictureSizeList != null) {
            ScalarProperties.PictureSize pictureSizeL2 = pictureSizeList.get((indexApectRatio * 3) + 0);
            pictureSizeL = pictureSizeL2;
            ScalarProperties.PictureSize pictureSizeM2 = pictureSizeList.get((indexApectRatio * 3) + 1);
            pictureSizeM = pictureSizeM2;
            ScalarProperties.PictureSize pictureSizeS2 = pictureSizeList.get((indexApectRatio * 3) + 2);
            pictureSizeS = pictureSizeS2;
        }
        if (width <= pictureSizeS.width && height <= pictureSizeS.height) {
            imageSize = PictureSizeController.SIZE_S;
        } else if (width <= pictureSizeM.width && height <= pictureSizeM.height) {
            imageSize = PictureSizeController.SIZE_M;
        } else if (width <= pictureSizeL.width && height <= pictureSizeL.height) {
            imageSize = PictureSizeController.SIZE_L;
        } else {
            imageSize = null;
        }
        AppLog.info(TAG, "Picutre Size: " + imageSize);
        AppLog.exit(TAG, AppLog.getMethodName());
        return imageSize;
    }

    public void setCameraSettingValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraSettingsAspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        this.mCameraSettingsImageSize = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        this.mSupportedAspectRatio = PictureSizeController.getInstance().getSupportedValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        this.mSupportedImageSize = PictureSizeController.getInstance().getSupportedValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        try {
            this.mCameraSettingsImageQuality = PictureQualityController.getInstance().getValue("API_NAME");
        } catch (IController.NotSupportedException e) {
            AppLog.info(TAG, e.toString());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void loadCameraSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCameraSettingsValues = this.mBackUpUtil.getPreferenceString(DoubleExposureBackUpKey.KEY_MANUAL_THEME_CAMERA_SETTINGS, null);
        this.mPictureEffectOptionValue = this.mBackUpUtil.getPreferenceString(DoubleExposureBackUpKey.KEY_MANUAL_THEME_PICTURE_EFFECT_OPTION_VALUE, null);
        this.mCurrentShootingScreen = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_CURRENT_SHOOTING_SCREEN, DoubleExposureConstant.FIRST_SHOOTING);
        this.mGuideValuesFirstShooting = this.mBackUpUtil.getPreferenceString(DoubleExposureBackUpKey.KEY_FIRST_SHOOTING_GUIDE_VALUES, null);
        this.mGuideValuesSecondShooting = this.mBackUpUtil.getPreferenceString(DoubleExposureBackUpKey.KEY_SECOND_SHOOTING_GUIDE_VALUES, null);
        this.mGuideValuesListFirstShooting = setShootingGuideValues(this.mGuideValuesFirstShooting, this.mGuideValuesListFirstShooting);
        this.mGuideValuesListSecondShooting = setShootingGuideValues(this.mGuideValuesSecondShooting, this.mGuideValuesListSecondShooting);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void saveCameraSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_MANUAL_THEME_CAMERA_SETTINGS, this.mCameraSettingsValues);
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_MANUAL_THEME_PICTURE_EFFECT_OPTION_VALUE, this.mPictureEffectOptionValue);
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_CURRENT_SHOOTING_SCREEN, this.mCurrentShootingScreen);
        this.mGuideValuesFirstShooting = getShootingGuideValues(this.mGuideValuesListFirstShooting);
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_FIRST_SHOOTING_GUIDE_VALUES, this.mGuideValuesFirstShooting);
        this.mGuideValuesSecondShooting = getShootingGuideValues(this.mGuideValuesListSecondShooting);
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_SECOND_SHOOTING_GUIDE_VALUES, this.mGuideValuesSecondShooting);
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        this.mBackUpUtil.setPreference(DoubleExposureBackUpKey.KEY_SELECTED_THEME, selectedTheme);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void saveFocusMode() {
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme.equalsIgnoreCase("Manual")) {
            DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
            cameraSettings.setFocusMode(FocusModeController.getInstance().getValue());
        }
    }

    public void updateExposureCompensation() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String exposureCompensation = null;
        if (EVDialDetector.hasEVDial()) {
            exposureCompensation = Integer.toString(EVDialDetector.getEVDialPosition());
        } else {
            try {
                exposureCompensation = ExposureCompensationController.getInstance().getValue("ExposureCompensation");
            } catch (IController.NotSupportedException e) {
                AppLog.error(TAG, e.toString());
            }
        }
        AppLog.info(TAG, "Exposure Compensation Value: " + exposureCompensation);
        if ("Manual".equalsIgnoreCase(selectedTheme)) {
            DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
            cameraSettings.setCameraSettingsValues(this.mCameraSettingsValues);
            cameraSettings.setExposureCompensation(exposureCompensation);
            this.mCameraSettingsValues = cameraSettings.getCameraSettingsValues();
        } else if (DoubleExposureConstant.FIRST_SHOOTING.equals(this.mCurrentShootingScreen)) {
            this.mExpsoureCompensationFirstShooting = exposureCompensation;
        } else {
            this.mExpsoureCompensationSecondShooting = exposureCompensation;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateExposureCompensationCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String exposureCompensation = null;
        try {
            exposureCompensation = ExposureCompensationController.getInstance().getValue("ExposureCompensation");
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
        }
        AppLog.info(TAG, "Exposure Compensation Value: " + exposureCompensation);
        if ("Manual".equalsIgnoreCase(selectedTheme)) {
            DoubleExposureCameraSettings cameraSettings = new DoubleExposureCameraSettings();
            cameraSettings.setCameraSettingsValues(this.mCameraSettingsValues);
            cameraSettings.setExposureCompensation(exposureCompensation);
            this.mCameraSettingsValues = cameraSettings.getCameraSettingsValues();
        } else if (DoubleExposureConstant.FIRST_SHOOTING.equals(this.mCurrentShootingScreen)) {
            this.mExpsoureCompensationFirstShooting = exposureCompensation;
        } else {
            this.mExpsoureCompensationSecondShooting = exposureCompensation;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isGuideClosed() {
        String guideValue;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = false;
        int themeIndex = getThemeIndex();
        if (themeIndex >= 0) {
            if (this.mCurrentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equals(this.mCurrentShootingScreen)) {
                String guideValue2 = this.mGuideValuesListFirstShooting.get(themeIndex);
                guideValue = guideValue2;
            } else {
                String guideValue3 = this.mGuideValuesListSecondShooting.get(themeIndex);
                guideValue = guideValue3;
            }
            bRetVal = Boolean.parseBoolean(guideValue);
        } else {
            AppLog.checkIf(TAG, "Theme Index is less than 0.");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public void setGuideClosed() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int themeIndex = getThemeIndex();
        if (themeIndex >= 0) {
            if (this.mCurrentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equals(this.mCurrentShootingScreen)) {
                if (this.mGuideValuesListFirstShooting != null) {
                    this.mGuideValuesListFirstShooting.set(themeIndex, this.GUIDE_VALUE_TRUE);
                }
            } else if (this.mGuideValuesListSecondShooting != null) {
                this.mGuideValuesListSecondShooting.set(themeIndex, this.GUIDE_VALUE_TRUE);
            }
        } else {
            AppLog.checkIf(TAG, "Theme Index is less than 0.");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private ScalarProperties.PictureSize getNewPictureSize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int indexApectRatio = getAspectRatioIndex(this.mCameraSettingsAspectRatio);
        int indexImageSize = this.mSupportedImageSize.indexOf(this.mCameraSettingsImageSize);
        AppLog.info(TAG, "Index of Image Size: " + indexImageSize);
        List<ScalarProperties.PictureSize> pictureSizeList = ScalarProperties.getSupportedPictureSizes();
        ScalarProperties.PictureSize picSize = null;
        if (pictureSizeList != null) {
            ScalarProperties.PictureSize picSize2 = pictureSizeList.get((indexApectRatio * 3) + indexImageSize);
            picSize = picSize2;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return picSize;
    }

    private String getShootingGuideValues(List<String> guideValues) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String values = null;
        if (guideValues != null) {
            values = guideValues.get(0) + "/" + guideValues.get(1) + "/" + guideValues.get(2) + "/" + guideValues.get(3) + "/" + guideValues.get(4) + "/" + guideValues.get(5) + "/" + guideValues.get(6);
        }
        AppLog.info(TAG, "Values: " + values);
        AppLog.exit(TAG, AppLog.getMethodName());
        return values;
    }

    private List<String> setShootingGuideValues(String values, List<String> guideValues) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "Values: " + values);
        if (guideValues == null) {
            guideValues = new ArrayList<>();
        }
        if (values != null) {
            StringTokenizer token = new StringTokenizer(values, "/");
            while (token.hasMoreTokens()) {
                String value = token.nextToken();
                guideValues.add(value);
            }
        } else {
            AppLog.info(TAG, "Shooting Guide value is null");
            guideValues.add(0, this.GUIDE_VALUE_FLASE);
            guideValues.add(1, this.GUIDE_VALUE_FLASE);
            guideValues.add(2, this.GUIDE_VALUE_FLASE);
            guideValues.add(3, this.GUIDE_VALUE_FLASE);
            guideValues.add(4, this.GUIDE_VALUE_FLASE);
            guideValues.add(5, this.GUIDE_VALUE_FLASE);
            guideValues.add(6, this.GUIDE_VALUE_TRUE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return guideValues;
    }

    private int getThemeIndex() {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> list = ThemeSelectionController.getInstance().getSupportedValue(ThemeSelectionController.THEMESELECTION);
        int themeIndex = list.indexOf(ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION)) - 1;
        AppLog.info(TAG, "Theme Index is : " + themeIndex);
        AppLog.exit(TAG, AppLog.getMethodName());
        return themeIndex;
    }

    private int getAspectRatioIndex(String aspectRatio) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int aspectRatioIndex = -1;
        if (PictureSizeController.ASPECT_3_2.equals(aspectRatio)) {
            aspectRatioIndex = 0;
        } else if (PictureSizeController.ASPECT_16_9.equals(aspectRatio)) {
            aspectRatioIndex = 1;
        } else if (PictureSizeController.ASPECT_4_3.equals(aspectRatio)) {
            aspectRatioIndex = 2;
        } else if (PictureSizeController.ASPECT_1_1.equals(aspectRatio)) {
            aspectRatioIndex = 3;
        }
        AppLog.info(TAG, "Aspect Ratio Index: " + aspectRatioIndex);
        AppLog.exit(TAG, AppLog.getMethodName());
        return aspectRatioIndex;
    }

    private void getCustomWhiteBalanceOptionValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String optValue = (("" + param.getLightBalance()) + "/" + param.getColorComp()) + "/" + param.getColorTemp();
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_MANUAL_THEME_CUSTOM_WHITE_BALANCE_VALUE, optValue);
        AppLog.trace(TAG, "CUSTOM_WB_OPTION_VALUE  " + optValue);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
