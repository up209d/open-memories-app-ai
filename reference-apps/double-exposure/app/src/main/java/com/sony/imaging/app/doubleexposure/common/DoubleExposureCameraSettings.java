package com.sony.imaging.app.doubleexposure.common;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureDROAutoHDRController;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureFocusModeController;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureWhiteBalanceController;
import com.sony.imaging.app.doubleexposure.menu.controller.ReverseController;
import com.sony.imaging.app.doubleexposure.menu.controller.RotationController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class DoubleExposureCameraSettings {
    private String TAG = AppLog.getClassName();
    private String mShootMode = ExposureModeController.PROGRAM_AUTO_MODE;
    private String mISOSensitivity = "0";
    private String mMeteringMode = MeteringController.MULTIMODE.toLowerCase();
    private String mWhiteBalance = "auto";
    private String mFaceDetection = "auto";
    private String mDroHdr = DROAutoHDRController.MODE_DRO_AUTO;
    private String mExposureCompensation = "0";
    private String mCreativeStyle = "standard";
    private String mPictureEffect = "off";
    private String mRotation = "Off";
    private String mReverse = "Off";

    public String getShootMode() {
        return this.mShootMode;
    }

    public void setShootMode(String shootMode) {
        this.mShootMode = shootMode;
    }

    public String getISOSensivity() {
        return this.mISOSensitivity;
    }

    public void setISOSensivity(String isoSensitivity) {
        this.mISOSensitivity = isoSensitivity;
    }

    public String getMeteringMode() {
        return this.mMeteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        this.mMeteringMode = meteringMode;
    }

    public String getWhiteBalance() {
        return this.mWhiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.mWhiteBalance = whiteBalance;
    }

    public String getFocusMode() {
        return DoubleExposureFocusModeController.getInstance().mFocusMode;
    }

    public void setFocusMode(String focusMode) {
        if (!DoubleExposureFocusModeController.getInstance().isFocusHeld()) {
            DoubleExposureFocusModeController.getInstance().mFocusMode = focusMode;
        }
    }

    public void setDefaultFocusMode(String focusMode) {
        if (DoubleExposureUtil.getInstance().isInitFocusMode()) {
            DoubleExposureFocusModeController.getInstance().mFocusMode = focusMode;
        }
    }

    public String getFaceDetection() {
        return this.mFaceDetection;
    }

    public void setFaceDetection(String faceDetection) {
        this.mFaceDetection = faceDetection;
    }

    public String getDroHDr() {
        return this.mDroHdr;
    }

    public void setDroHDr(String droHDr) {
        this.mDroHdr = droHDr;
    }

    public String getExposureCompensation() {
        return this.mExposureCompensation;
    }

    public void setExposureCompensation(String exposureCompensation) {
        this.mExposureCompensation = exposureCompensation;
    }

    public String getCreativeStyle() {
        return this.mCreativeStyle;
    }

    public void setCreativeStyle(String creativeStyle) {
        this.mCreativeStyle = creativeStyle;
    }

    public String getPictureEffect() {
        return this.mPictureEffect;
    }

    public void setPictureEffect(String pictureEffect) {
        this.mPictureEffect = pictureEffect;
    }

    public String getRotation() {
        return this.mRotation;
    }

    public void setRotation(String rotation) {
        this.mRotation = rotation;
    }

    public String getReverse() {
        return this.mReverse;
    }

    public void setReverse(String reverse) {
        this.mReverse = reverse;
    }

    public void setCameraSetting() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setExposureMode();
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_VALUEID_ISO_AUTO")) {
            AppLog.info(this.TAG, "Haita of ISO is off");
            ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, this.mISOSensitivity);
        } else {
            AppLog.info(this.TAG, "Haita of ISO is on");
        }
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_AE_METER_MODE")) {
            AppLog.info(this.TAG, "Haita of Metering Mode is off");
            MeteringController.getInstance().setValue("MeteringMode", this.mMeteringMode);
        } else {
            AppLog.info(this.TAG, "Haita of Metering Mode is on");
        }
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_WB_MODE")) {
            AppLog.info(this.TAG, "Haita of WhiteBalance is off");
            WhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, this.mWhiteBalance);
            setCustomWhiteBalanceOptionValue();
        } else {
            AppLog.info(this.TAG, "Haita of WhiteBalance is on");
        }
        String focusMode = DoubleExposureFocusModeController.getInstance().mFocusMode;
        if (DoubleExposureFocusModeController.getInstance().isFocusControl()) {
            if (!DoubleExposureFocusModeController.getInstance().isFocusHeld()) {
                DoubleExposureFocusModeController.getInstance().toggleFocusControl();
                if (!DoubleExposureUtil.getInstance().isInitFocusMode()) {
                    focusMode = DoubleExposureFocusModeController.getInstance().getValue(FocusModeController.TAG_FOCUS_MODE);
                }
                DoubleExposureFocusModeController.getInstance().setValue(FocusModeController.TAG_FOCUS_MODE, focusMode);
                DoubleExposureFocusModeController.getInstance().mFocusMode = focusMode;
            }
        } else if ("Manual".equalsIgnoreCase(ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION))) {
            if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_C")) {
                AppLog.info("kamata", "Haita of focus mode is off");
                DoubleExposureFocusModeController.getInstance().setValue(FocusModeController.TAG_FOCUS_MODE, focusMode);
            } else {
                AppLog.info("kamata", "Haita of Focus Mode is on");
            }
        } else if (!AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_C")) {
            AppLog.info("kamata", "Haita of Focus Mode is ON/OFF?");
            DoubleExposureFocusModeController.getInstance().setValue(FocusModeController.TAG_FOCUS_MODE, focusMode);
        } else {
            AppLog.info("kamata", "Haita of Focus Mode is on");
        }
        DoubleExposureUtil.getInstance().setInitFocusMode(false);
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_FACE_DETECTION_MODE")) {
            AppLog.info(this.TAG, "Haita of Face Detection is off");
            FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, this.mFaceDetection);
        } else {
            AppLog.info(this.TAG, "Haita of Face Detection is On");
        }
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_PICTURE_EFFECT_CMD")) {
            AppLog.info(this.TAG, "Haita of Picture is Off");
            String pictureEffectOptionValue = DoubleExposureUtil.getInstance().getPictureEffectOptionValue();
            if (pictureEffectOptionValue != null) {
                PictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, this.mPictureEffect);
                PictureEffectController.getInstance().setValue(this.mPictureEffect, pictureEffectOptionValue);
            } else {
                PictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, this.mPictureEffect);
            }
        } else {
            AppLog.info(this.TAG, "Haita of Picture is On");
        }
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_DRO_LEVEL") || !AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_HDR_LEVEL")) {
            AppLog.info(this.TAG, "Haita of DroHdr is off");
            String itemId = getDROHDRItemId(this.mDroHdr);
            AppLog.info(this.TAG, "DRO HDR item ID is : " + itemId + " DRO HDR  is : " + this.mDroHdr);
            if (itemId != null) {
                DoubleExposureDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, itemId);
                DoubleExposureDROAutoHDRController.getInstance().setValue(itemId, this.mDroHdr);
            } else {
                DoubleExposureDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, itemId);
            }
        } else {
            AppLog.info(this.TAG, "Haita of DroHdr is On");
        }
        ExposureCompensationController.getInstance().setValue("ExposureCompensation", this.mExposureCompensation);
        if (!AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_COLOR_MODE")) {
            AppLog.info(this.TAG, "Haita of Creative Style is off");
            CreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, this.mCreativeStyle);
        } else {
            AppLog.info(this.TAG, "Haita of Creative Style is on");
        }
        RotationController.getInstance().setValue("Rotation", this.mRotation);
        ReverseController.getInstance().setValue(ReverseController.TAG_REVERSE, this.mReverse);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public String getCameraSettingsValues() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String mFocusMode = DoubleExposureFocusModeController.getInstance().mFocusMode;
        String values = this.mShootMode + "/" + this.mISOSensitivity + "/" + this.mMeteringMode + "/" + this.mWhiteBalance + "/" + mFocusMode + "/" + this.mFaceDetection + "/" + this.mDroHdr + "/" + this.mExposureCompensation + "/" + this.mCreativeStyle + "/" + this.mPictureEffect + "/" + this.mRotation + "/" + this.mReverse;
        AppLog.info(this.TAG, "Values: " + values);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return values;
    }

    public void setCameraSettingsValues(String values) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "Values: " + values);
        if (values != null) {
            StringTokenizer token = new StringTokenizer(values, "/");
            ArrayList<String> camValues = new ArrayList<>();
            while (token.hasMoreTokens()) {
                String value = token.nextToken();
                camValues.add(value);
            }
            this.mShootMode = camValues.get(0);
            this.mISOSensitivity = camValues.get(1);
            this.mMeteringMode = camValues.get(2);
            this.mWhiteBalance = camValues.get(3);
            this.mFaceDetection = camValues.get(5);
            this.mDroHdr = camValues.get(6);
            this.mExposureCompensation = camValues.get(7);
            this.mCreativeStyle = camValues.get(8);
            this.mPictureEffect = camValues.get(9);
            this.mRotation = camValues.get(10);
            this.mReverse = camValues.get(11);
        } else {
            AppLog.info(this.TAG, "Camera Settings value is null");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public String getDROHDRItemId(String drohdr) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String itemIdDROHDR = null;
        if (DROAutoHDRController.MODE_DRO_AUTO.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_1.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_2.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_3.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_4.equals(drohdr) || DROAutoHDRController.DRO_LEVEL_5.equals(drohdr)) {
            itemIdDROHDR = DROAutoHDRController.MENU_ITEM_ID_DRO;
        } else if (DROAutoHDRController.MODE_HDR_AUTO.equals(drohdr) || DROAutoHDRController.HDR_EV_1.equals(drohdr) || DROAutoHDRController.HDR_EV_2.equals(drohdr) || DROAutoHDRController.HDR_EV_3.equals(drohdr) || DROAutoHDRController.HDR_EV_4.equals(drohdr) || DROAutoHDRController.HDR_EV_5.equals(drohdr) || DROAutoHDRController.HDR_EV_6.equals(drohdr)) {
            itemIdDROHDR = DROAutoHDRController.MENU_ITEM_ID_HDR;
        }
        AppLog.info(this.TAG, "DRO HDR item ID is : " + itemIdDROHDR);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return itemIdDROHDR;
    }

    private void setCustomWhiteBalanceOptionValue() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if ("Manual".equalsIgnoreCase(ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION))) {
            String optValue = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_MANUAL_THEME_CUSTOM_WHITE_BALANCE_VALUE, "0/0/0");
            String[] value = optValue.split("/");
            int light = Integer.parseInt(value[0]);
            int comp = Integer.parseInt(value[1]);
            int temp = Integer.parseInt(value[2]);
            WhiteBalanceController.WhiteBalanceParam wbInfo = new WhiteBalanceController.WhiteBalanceParam(light, comp, temp);
            DoubleExposureWhiteBalanceController.getInstance().setDetailValue(wbInfo);
        } else {
            WhiteBalanceController.WhiteBalanceParam wbInfo2 = new WhiteBalanceController.WhiteBalanceParam(0, 0, 0);
            DoubleExposureWhiteBalanceController.getInstance().setDetailValue(wbInfo2);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setExposureMode() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if ("Manual".equalsIgnoreCase(ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION)) && ModeDialDetector.hasModeDial()) {
            int mModeDialPosition = ModeDialDetector.getModeDialPosition();
            String mode = ExposureModeController.PROGRAM_AUTO_MODE;
            switch (mModeDialPosition) {
                case AppRoot.USER_KEYCODE.MODE_DIAL_AUTO /* 535 */:
                    mode = ExposureModeController.INTELLIGENT_AUTO_MODE;
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                    mode = ExposureModeController.PROGRAM_AUTO_MODE;
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                    mode = ExposureModeController.SHUTTER_MODE;
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                    mode = "Aperture";
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                    mode = "Manual";
                    break;
                case AppRoot.USER_KEYCODE.MODE_DIAL_SCN /* 545 */:
                    mode = "SceneSelectionMode";
                    break;
            }
            this.mShootMode = mode;
        }
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, this.mShootMode);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
