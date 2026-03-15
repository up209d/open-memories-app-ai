package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFFn15LayerWhiteBalanceLayout extends Fn15LayerWhiteBalanceLayout {
    public static final String MENU_ID = "ID_GFFN15LAYERWHITEBALANCELAYOUT";
    private static boolean isBaseSetting = false;
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.isCanceled = false;
        isBaseSetting = GFCommonUtil.getInstance().isFilterSetting() ? false : true;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            params.setWBMode(isBaseSetting, WhiteBalanceController.getInstance().getValue());
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.isCanceled = true;
        return super.pushedMenuKey();
    }
}
