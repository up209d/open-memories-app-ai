package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.ISOFn15LayerMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFISOFn15LayerMenuLayout extends ISOFn15LayerMenuLayout {
    public static final String MENU_ID = "ID_GFISOFN15LAYERMENULAYOUT";
    private static boolean isBaseSetting = false;
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.isCanceled = false;
        isBaseSetting = GFCommonUtil.getInstance().isFilterSetting() ? false : true;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            params.setISO(isBaseSetting, ISOSensitivityController.getInstance().getValue());
        }
        super.closeLayout();
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
