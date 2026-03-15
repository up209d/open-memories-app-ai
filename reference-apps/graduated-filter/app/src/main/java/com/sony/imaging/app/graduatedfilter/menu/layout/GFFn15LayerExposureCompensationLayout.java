package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.Fn15LayerExposureCompensationLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFFn15LayerExposureCompensationLayout extends Fn15LayerExposureCompensationLayout {
    public static final String MENU_ID = "ID_GFFN15LAYEREXPOSURECOMPENSATIONLAYOUT";
    private static boolean isBaseSetting = false;
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isBaseSetting = !GFCommonUtil.getInstance().isFilterSetting();
        this.isCanceled = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            try {
                params.setExposureComp(isBaseSetting, ExposureCompensationController.getInstance().getValue("ExposureCompensation"));
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.isCanceled = true;
        return super.pushedMenuKey();
    }
}
