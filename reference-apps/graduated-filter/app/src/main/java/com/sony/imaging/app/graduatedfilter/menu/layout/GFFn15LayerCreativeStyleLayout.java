package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.Fn15LayerCreativeStyleLayout;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;

/* loaded from: classes.dex */
public class GFFn15LayerCreativeStyleLayout extends Fn15LayerCreativeStyleLayout {
    public static final String MENU_ID = "ID_GFFN15LAYERCREATIVESTYLELAYOUT";
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCreativeStyleLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.isCanceled = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            params.setCreativeStyle(CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE));
            CreativeStyleController.CreativeStyleOptions option = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
            String value = "" + option.contrast + "/" + option.saturation + "/" + option.sharpness;
            params.setCreativeStyleOption(value);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCreativeStyleLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
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
