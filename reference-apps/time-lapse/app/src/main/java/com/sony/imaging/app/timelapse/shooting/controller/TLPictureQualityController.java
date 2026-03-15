package com.sony.imaging.app.timelapse.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class TLPictureQualityController extends PictureQualityController {
    private static final int CAUTION_INDEX_RECORDING_MODE = 1;
    private static final int CAUTION_INDEX_THEME = 2;
    public static final String ITEM_PICTURE_QUALITY_RAW = "setPictureStorageFormat_raw";
    public static final String ITEM_PICTURE_QUALITY_RAWJPEG = "setPictureStorageFormat_rawjpeg";
    private static final String TAG = TLPictureQualityController.class.getName();
    private static TLPictureQualityController mInstance = new TLPictureQualityController();
    private String mQuality = null;
    private String mExclusionQuality = null;
    private boolean isTestShotDummyQuality = false;

    public static TLPictureQualityController getInstance() {
        return mInstance;
    }

    public void setMiniatureValue() throws IllegalArgumentException {
        boolean isRAWJPEG = PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
        boolean isRAW = PictureQualityController.PICTURE_QUALITY_RAW.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat()) || isRAWJPEG;
        if (isRAW) {
            this.mQuality = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat();
            super.setValue(null, PictureQualityController.PICTURE_QUALITY_FINE);
        }
    }

    public void validateRecordingModeExclusion(boolean isExclusionEffective) throws IllegalArgumentException {
        if (isExclusionEffective) {
            boolean isRAW = PictureQualityController.PICTURE_QUALITY_RAW.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat());
            if (isRAW) {
                this.mExclusionQuality = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat();
                super.setValue(null, PictureQualityController.PICTURE_QUALITY_RAWJPEG);
                return;
            }
            return;
        }
        if (this.mExclusionQuality != null) {
            super.setValue(null, this.mExclusionQuality);
            this.mExclusionQuality = null;
        }
    }

    public void resetValue() {
        if (this.mQuality != null) {
            super.setValue(null, this.mQuality);
        }
        this.mQuality = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> list = super.getAvailableValue(tag);
        if (TLShootModeSettingController.getInstance().isStillMovieShootMode() && list.contains(PictureQualityController.PICTURE_QUALITY_RAW)) {
            list.remove(PictureQualityController.PICTURE_QUALITY_RAW);
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        this.mExclusionQuality = null;
        this.mQuality = null;
        super.setValue(tag, value);
        if (7 == TLCommonUtil.getInstance().getCurrentState()) {
            TimelapseCreativeStyleController.getInstance().setTimelapseValue(CreativeStyleController.CREATIVESTYLE, null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret;
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
            ret = 1;
        } else if (5 == TLCommonUtil.getInstance().getCurrentState() && (ITEM_PICTURE_QUALITY_RAW.equals(itemId) || ITEM_PICTURE_QUALITY_RAWJPEG.equals(itemId))) {
            ret = 2;
        } else if (TLShootModeSettingController.getInstance().isStillMovieShootMode() && ITEM_PICTURE_QUALITY_RAW.equals(itemId)) {
            ret = 1;
        } else {
            ret = super.getCautionIndex(itemId);
        }
        Log.d(TAG, "caution index " + itemId + " position " + ret);
        return ret;
    }

    public void setTestShotDummyQuality() {
        this.isTestShotDummyQuality = false;
        if (TLCommonUtil.getInstance().isTestShot() && PictureQualityController.PICTURE_QUALITY_RAW.equals(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getPictureStorageFormat())) {
            this.isTestShotDummyQuality = true;
            super.setValue(null, PictureQualityController.PICTURE_QUALITY_RAWJPEG);
        }
    }

    public void resetTestShotDummyQuality() {
        if (this.isTestShotDummyQuality) {
            super.setValue(null, PictureQualityController.PICTURE_QUALITY_RAW);
            this.isTestShotDummyQuality = false;
        }
    }

    public boolean isTestShotWithRAW() {
        return this.isTestShotDummyQuality;
    }
}
