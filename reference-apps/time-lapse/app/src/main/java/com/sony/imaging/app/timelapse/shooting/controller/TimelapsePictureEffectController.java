package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class TimelapsePictureEffectController extends PictureEffectController {
    private static final String TAG = "TimelapsePictureEffectController";
    private static TimelapsePictureEffectController mInstance = new TimelapsePictureEffectController();

    public static TimelapsePictureEffectController getInstance() {
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> list = super.getAvailableValue(tag);
        if (list != null && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            list.remove("auto");
            list.remove(PictureEffectController.MODE_ILLUST);
            list.remove(PictureEffectController.WATERCOLOR);
        }
        if (list != null && TLShootModeSettingController.getInstance().getCurrentCaptureState() == 2) {
            list.remove(PictureEffectController.MODE_RICH_TONE_MONOCHROME);
            list.remove(PictureEffectController.MODE_SOFT_FOCUS);
            list.remove(PictureEffectController.MODE_HDR_ART);
        }
        if (list != null && TLCommonUtil.getInstance().getCurrentState() != 7) {
            list.remove(PictureEffectController.MODE_ILLUST);
            list.remove(PictureEffectController.WATERCOLOR);
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable = super.isAvailable(tag);
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
            return isAvailable;
        }
        if (PictureEffectController.MODE_ILLUST.equals(tag) || PictureEffectController.MODE_ILLUST.equals(tag)) {
            return false;
        }
        return isAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        updateBackupValues(itemId, value);
        super.setValue(itemId, value);
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            updateCreativeStyle();
        }
    }

    public void setValueNoBackup(String itemId, String value) {
        super.setValue(itemId, value);
    }

    public void setThemeValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            updateCreativeStyle();
        }
    }

    private void updateBackupValues(String itemId, String value) {
        boolean isRAW = true;
        BackUpUtil backup = BackUpUtil.getInstance();
        int captureState = TLShootModeSettingController.getInstance().getCurrentCaptureState();
        int theme = TLCommonUtil.getInstance().getCurrentState();
        if ("PictureEffect".equals(itemId)) {
            if (theme == 7) {
                boolean isRAWJPEG = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
                if (!PictureQualityController.PICTURE_QUALITY_RAW.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat()) && !isRAWJPEG) {
                    isRAW = false;
                }
                if (!isRAW) {
                    backup.setPreference("PictureEffect", value);
                    return;
                }
                return;
            }
            return;
        }
        if (PictureEffectController.MODE_MINIATURE.equals(itemId) && !PictureEffectController.MODE_MINIATURE.equals(value) && !"off".equals(value) && !"auto".equals(value) && captureState != 1) {
            if (theme == 7) {
                backup.setPreference(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME, value);
                AppLog.info(TAG, "set LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME ** itemId: " + itemId + " value: " + value + LogHelper.MSG_CLOSE_BRACKET);
                return;
            } else {
                if (theme == 5) {
                    backup.setPreference(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME, value);
                    AppLog.info(TAG, "set LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME ** itemId: " + itemId + " value: " + value + LogHelper.MSG_CLOSE_BRACKET);
                    return;
                }
                return;
            }
        }
        if (PictureEffectController.MODE_MINIATURE.equals(itemId) && captureState == 1) {
            if (theme == 7) {
                backup.setPreference(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME_STILL, value);
                AppLog.info(TAG, "set LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME_STILL ** itemId: " + itemId + " value: " + value + LogHelper.MSG_CLOSE_BRACKET);
            } else if (theme == 5) {
                backup.setPreference(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME_STILL, value);
                AppLog.info(TAG, "set LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME_STILL ** itemId: " + itemId + " value: " + value + LogHelper.MSG_CLOSE_BRACKET);
            }
        }
    }

    private void updateCreativeStyle() {
        String creativeStyle = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY, "standard");
        TimelapseCreativeStyleController.getInstance().setTimelapseValue(CreativeStyleController.CREATIVESTYLE, creativeStyle);
    }
}
