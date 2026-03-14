package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusAppSettingController;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;

/* loaded from: classes.dex */
public class PictureEffectPlusPageMenuLayout extends PageMenuLayout {
    private static final String TAG = AppLog.getClassName();
    private PictureEffectPlusController mController = PictureEffectPlusController.getInstance();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return PageMenuLayout.MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemid.equalsIgnoreCase(PictureEffectPlusAppSettingController.ApplicationSettings)) {
            this.mController.setComingFromApplicationSettings(true);
            String curEfffect = this.mController.getBackupEffectValue();
            if (curEfffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                if (this.mController.getPartColorCurrentPlate() == 0) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                    itemid = PictureEffectPlusController.MODE_PART_COLOR_CH0;
                } else if (this.mController.getPartColorCurrentPlate() == 1) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                    itemid = PictureEffectPlusController.MODE_PART_COLOR_CH0;
                } else if (this.mController.getPartColorCurrentPlate() == 2) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                    itemid = PictureEffectPlusController.MODE_PART_COLOR_CH1;
                } else if (this.mController.getPartColorCurrentPlate() == 3) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                    itemid = PictureEffectPlusController.MODE_PART_COLOR_CH1;
                }
                this.mController.setPrevSelectedPlate(this.mController.getPartColorCurrentPlate());
            } else if (curEfffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                if (this.mController.getSelectedPlate() == 0) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_AREA);
                    itemid = PictureEffectPlusController.MODE_MINIATURE_AREA;
                } else {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
                    itemid = PictureEffectPlusController.MODE_MINIATURE_EFFECT;
                }
                this.mController.setPrevSelectedPlateForMiniAndToy(this.mController.getSelectedPlate());
            } else if (curEfffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                if (this.mController.getSelectedPlate() == 0) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
                    itemid = PictureEffectPlusController.MODE_TOY_CAMERA_COLOR;
                } else {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
                    itemid = PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS;
                }
                this.mController.setPrevSelectedPlateForMiniAndToy(this.mController.getSelectedPlate());
            } else if (!curEfffect.equals("watercolor") && !curEfffect.equals(PictureEffectController.MODE_RICH_TONE_MONOCHROME) && !curEfffect.equals(PictureEffectController.MODE_RETRO_PHOTO)) {
                this.mService.setMenuItemId(curEfffect);
                itemid = curEfffect;
            }
        } else if (itemid.equalsIgnoreCase(PictureEffectPlusController.PICTUREEFFECTPLUS)) {
            this.mController.setAppTopOpenFromMenu(true);
        }
        this.mController.keepCurrentEffectOptionSetting(PictureEffectPlusController.EFFECT_SET_STATUS);
        AppLog.exit(TAG, AppLog.getMethodName());
        super.doItemClickProcessing(itemid);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void openNextMenu(String itemId, String layoutId) {
        String nextFragmentID = layoutId;
        if (itemId.equalsIgnoreCase(PictureEffectPlusAppSettingController.ApplicationSettings) && PictureEffectPlusController.getInstance().isPlusParameterSelected()) {
            nextFragmentID = "ID_PICTUREEFFECTPLUSOPTIONMENULAYOUTSUB";
        }
        super.openNextMenu(itemId, nextFragmentID);
    }
}
