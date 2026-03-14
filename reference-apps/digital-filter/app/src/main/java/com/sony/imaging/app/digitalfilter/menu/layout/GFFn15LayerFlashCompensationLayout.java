package com.sony.imaging.app.digitalfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.Fn15LayerFlashCompensationLayout;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFFn15LayerFlashCompensationLayout extends Fn15LayerFlashCompensationLayout {
    public static final String MENU_ID = "ID_GFFN15LAYERFLASHCOMPENSATIONLAYOUT";
    private static boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            params.setFlashComp(FlashController.getInstance().getValue(FlashController.FLASH_COMPENSATION));
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }
}
