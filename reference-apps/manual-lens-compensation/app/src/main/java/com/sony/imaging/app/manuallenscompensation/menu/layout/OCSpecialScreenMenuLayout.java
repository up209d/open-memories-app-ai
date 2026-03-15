package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.view.View;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;

/* loaded from: classes.dex */
public class OCSpecialScreenMenuLayout extends SpecialScreenMenuLayout {
    private static final String PICTUREEFFECT_HDR_ART = "PictureEffect_hdr-art";
    private static final String PICTUREEFFECT_HDR_ART_HDR_ART_1 = "PictureEffect_hdr-art_hdr-art-1";
    private static final String PICTUREEFFECT_HDR_ART_HDR_ART_2 = "PictureEffect_hdr-art_hdr-art-2";
    private static final String PICTUREEFFECT_HDR_ART_HDR_ART_3 = "PictureEffect_hdr-art_hdr-art-3";
    private static final String PICTUREEFFECT_RICHTONE_MONO = "PictureEffect_richtone-mono";
    private static final String PICTUREEFFECT_TOY_CAMERA = "PictureEffect_toy-camera";
    private static final String PICTUREEFFECT_TOY_CAMERA_COOL = "PictureEffect_toy-camera_cool";
    private static final String PICTUREEFFECT_TOY_CAMERA_GREEN = "PictureEffect_toy-camera_green";
    private static final String PICTUREEFFECT_TOY_CAMERA_MAGENTA = "PictureEffect_toy-camera_magenta";
    private static final String PICTUREEFFECT_TOY_CAMERA_NORMAL = "PictureEffect_toy-camera_normal";
    private static final String PICTUREEFFECT_TOY_CAMERA_WARM = "PictureEffect_toy-camera_warm";
    private static final String TAG = "ID_SPECIALSCREENMENULAYOUT";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
        if (itemId.equalsIgnoreCase(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        }
        super.requestCautionTrigger(itemId);
        AppLog.exit("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
        super.onResume();
        AppLog.exit("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        AppLog.enter("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
        return "ID_SPECIALSCREENMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        AppLog.enter("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
        String itemId = (String) adapterView.getItemAtPosition(position);
        if (itemId.equals(PICTUREEFFECT_HDR_ART) || itemId.equals(PICTUREEFFECT_TOY_CAMERA) || itemId.equals(PICTUREEFFECT_RICHTONE_MONO) || itemId.equals(PICTUREEFFECT_HDR_ART_HDR_ART_1) || itemId.equals(PICTUREEFFECT_HDR_ART_HDR_ART_2) || itemId.equals(PICTUREEFFECT_HDR_ART_HDR_ART_3) || itemId.equals(PICTUREEFFECT_TOY_CAMERA_MAGENTA) || itemId.equals(PICTUREEFFECT_TOY_CAMERA_GREEN) || itemId.equals(PICTUREEFFECT_TOY_CAMERA_WARM) || itemId.equals(PICTUREEFFECT_TOY_CAMERA_COOL) || itemId.equals(PICTUREEFFECT_TOY_CAMERA_NORMAL)) {
            OCUtil.getInstance().setShadingEffectOff();
        } else if (!OCController.getInstance().isSupportPictureEffect()) {
            OCUtil.getInstance().setSelectedProfileEffect();
        }
        super.onItemSelected(adapterView, view, position, id);
        AppLog.exit("ID_SPECIALSCREENMENULAYOUT", AppLog.getMethodName());
    }
}
