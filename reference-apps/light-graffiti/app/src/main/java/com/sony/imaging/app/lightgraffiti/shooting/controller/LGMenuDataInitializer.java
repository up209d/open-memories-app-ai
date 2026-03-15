package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LGMenuDataInitializer {
    private static final String TAG = LGMenuDataInitializer.class.getSimpleName();

    public static void initMenuData(String stage) {
        Log.d(TAG, AppLog.getMethodName() + " : stage=" + stage);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            BackUpUtil backup = BackUpUtil.getInstance();
            String value = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH, "on");
            LGFlashController.getInstance().setValue(FlashController.FLASHMODE, value);
            String value2 = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH_COMPENSATION, ISOSensitivityController.ISO_AUTO);
            LGFlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, value2);
            if (EVDialDetector.hasEVDial() && EVDialDetector.getEVDialPosition() != 0) {
                LGExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            } else {
                String value3 = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_EXPOSURE_COMPENSATION, ISOSensitivityController.ISO_AUTO);
                LGExposureCompensationController.getInstance().setValue("ExposureCompensation", value3);
            }
            String value4 = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_ISO_SENSITIVITY, ISOSensitivityController.ISO_AUTO);
            LGISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, value4);
            String value5 = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_DRO_AUTO_HDR, DROAutoHDRController.MENU_ITEM_ID_DRO);
            LGDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, value5);
            String valuePic = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_PICTURE_EFFECT, "off");
            LGPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
            String value6 = backup.getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE, "standard");
            LGCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, value6);
            CreativeStyleController.CreativeStyleOptions optionValue = (CreativeStyleController.CreativeStyleOptions) LGCreativeStyleController.getInstance().getDetailValue(value6);
            optionValue.contrast = backup.getPreferenceInt(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_CONTRAST + value6, 0);
            optionValue.saturation = backup.getPreferenceInt(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_SATURATION + value6, 0);
            optionValue.sharpness = backup.getPreferenceInt(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_SHARPNESS + value6, 0);
            LGCreativeStyleController.getInstance().setDetailValue(optionValue);
            LGPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, valuePic);
            LGExposureModeController.getInstance().setExposureMode(ExposureModeController.PROGRAM_AUTO_MODE);
            String nr = LGLongExposureNrController.getInstance().getSaveDiademValue();
            LGLongExposureNrController.getInstance().setValue(nr);
            return;
        }
        LGFlashController.getInstance().setValue(FlashController.FLASHMODE, "off");
        LGFlashController.getInstance().setValue(FlashController.FLASH_COMPENSATION, ISOSensitivityController.ISO_AUTO);
        LGExposureCompensationController.getInstance().setValue("ExposureCompensation", ISOSensitivityController.ISO_AUTO);
        LGISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, ISOSensitivityController.ISO_AUTO);
        LGDROAutoHDRController.getInstance().setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
        LGPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
        LGCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, "standard");
        CreativeStyleController.CreativeStyleOptions optionValue2 = (CreativeStyleController.CreativeStyleOptions) LGCreativeStyleController.getInstance().getDetailValue("standard");
        optionValue2.contrast = 0;
        optionValue2.saturation = 0;
        optionValue2.sharpness = 0;
        LGCreativeStyleController.getInstance().setDetailValue(optionValue2);
        LGExposureModeController.getInstance().setExposureMode("Shutter");
        LGLongExposureNrController.getInstance().setValue(LGLongExposureNrController.LONG_EXPOSURE_NR_OFF);
    }
}
