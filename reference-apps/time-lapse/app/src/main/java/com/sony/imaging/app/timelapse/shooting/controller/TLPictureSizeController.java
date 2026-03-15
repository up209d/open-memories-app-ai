package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.List;

/* loaded from: classes.dex */
public class TLPictureSizeController extends PictureSizeController {
    private static TLPictureSizeController mInstance = new TLPictureSizeController();

    public static TLPictureSizeController getInstance() {
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
            return false;
        }
        boolean ret = super.isAvailable(tag);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_HAITA_KEY, false);
        }
        super.setValue(itemId, value);
    }

    public void setTLValue(String itemId, String value) {
        super.setValue(itemId, value);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String itemId) {
        if (PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
            List<String> availables = super.getAvailableValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 2) {
                availables.remove(PictureSizeController.ASPECT_1_1);
                return availables;
            }
            return availables;
        }
        return super.getAvailableValue(itemId);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0 || itemId.contains(PictureSizeController.ASPECT_1_1)) {
            return 1;
        }
        int ret = super.getCautionIndex(itemId);
        return ret;
    }
}
