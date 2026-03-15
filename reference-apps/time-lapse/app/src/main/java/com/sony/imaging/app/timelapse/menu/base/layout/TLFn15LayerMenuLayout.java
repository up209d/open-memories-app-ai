package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLPictureQualityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class TLFn15LayerMenuLayout extends Fn15LayerMenuLayout {
    private static final List<String> INVALID_ITEM_MINIATURE_THEME = Arrays.asList("PictureEffect_off", "PictureEffect_toy-camera", "PictureEffect_pop-color", "PictureEffect_posterization", "PictureEffect_retro-photo", "PictureEffect_soft-high-key", "PictureEffect_part-color", "PictureEffect_rough-mono", "PictureEffect_soft-focus", "PictureEffect_hdr-art", "PictureEffect_richtone-mono", "PictureEffect_watercolor", "PictureEffect_illust", TLPictureQualityController.ITEM_PICTURE_QUALITY_RAW, TLPictureQualityController.ITEM_PICTURE_QUALITY_RAWJPEG);

    private boolean isInvalidByMiniatureTheme(String itemId) {
        return INVALID_ITEM_MINIATURE_THEME.contains(itemId) && TLCommonUtil.getInstance().getCurrentState() == 5;
    }

    private boolean isInvalidByMovieFormat(String itemId) {
        return TimelapsePictureEffectController.getInstance().isAvailable(itemId) && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        TLCommonUtil.getInstance().prepareStillImageChecking(this.mService);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        TLCommonUtil.getInstance().checkStillImageSettings();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        if (isInvalidByMiniatureTheme(itemId)) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME);
        } else if (isInvalidByMovieFormat(itemId)) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
        } else {
            super.requestCautionTrigger(itemId);
        }
    }
}
