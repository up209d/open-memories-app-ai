package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SmoothReflectionPictureQualityController extends PictureQualityController {
    public static final String API_NAME = "setPictureStorageFormat";
    private static SmoothReflectionPictureQualityController mInstance;

    private SmoothReflectionPictureQualityController() {
    }

    public static SmoothReflectionPictureQualityController getInstance() {
        if (mInstance == null) {
            mInstance = new SmoothReflectionPictureQualityController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        new ArrayList();
        List<String> availables = super.getAvailableValue(tag);
        if (ThemeController.MONOTONE.equals(getSelectedTheme())) {
            availables.remove(PictureQualityController.PICTURE_QUALITY_RAWJPEG);
        }
        AppLog.info("SmoothReflectionPictureQualityController", "availables : " + availables);
        return availables;
    }

    private String getSelectedTheme() {
        try {
            String selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
            return selectedTheme;
        } catch (IController.NotSupportedException e) {
            AppLog.error("SmoothReflectionPictureQualityController", e.toString());
            return null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
        super.setValue(tag, value);
        if (ThemeController.MONOTONE.equals(getSelectedTheme()) && !PictureQualityController.PICTURE_QUALITY_FINE.equals(value)) {
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_PICTUREQUALITY, false);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (ThemeController.MONOTONE.equals(getSelectedTheme())) {
            return 0;
        }
        int ret = super.getCautionIndex(itemId);
        return ret;
    }
}
