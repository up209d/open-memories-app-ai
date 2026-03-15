package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class TLSpecialScreenMenuLayout extends SpecialScreenMenuLayout {
    private static final String TAG = "TLSpecialScreenMenuLayout";
    private final String MINIATURE_AUTO = "PictureEffect_miniature_auto";

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        TLCommonUtil.getInstance().prepareStillImageChecking(this.mService);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        TLCommonUtil.getInstance().checkStillImageSettings();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        int[] cautionId = this.mService.getMenuItemCautionId(itemId);
        if (cautionId == null || cautionId[0] == 0) {
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && TLCommonUtil.getInstance().getCurrentState() == 7 && (itemId.equalsIgnoreCase("PictureEffect_miniature_auto") || itemId.endsWith(PictureEffectController.MODE_SOFT_FOCUS) || itemId.endsWith(PictureEffectController.MODE_RICH_TONE_MONOCHROME) || itemId.endsWith(PictureEffectController.MODE_HDR_ART) || itemId.endsWith(PictureEffectController.WATERCOLOR) || itemId.endsWith(PictureEffectController.MODE_ILLUST))) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
            } else if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && TLCommonUtil.getInstance().getCurrentState() == 5 && itemId.equalsIgnoreCase("PictureEffect_miniature_auto")) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
            } else if (itemId.equalsIgnoreCase("PictureEffect")) {
                int cautionID = 1 == Environment.getVersionOfHW() ? Info.CAUTION_ID_DLAPP_INVALID_FUNCTION : 33570;
                CautionUtilityClass.getInstance().requestTrigger(cautionID);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME);
            }
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.info(TAG, "requestCautionTrigger  itemId  " + itemId);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return SpecialScreenMenuLayout.MENU_ID;
    }
}
