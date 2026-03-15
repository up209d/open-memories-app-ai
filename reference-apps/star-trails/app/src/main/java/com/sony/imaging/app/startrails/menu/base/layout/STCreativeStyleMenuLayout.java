package com.sony.imaging.app.startrails.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.startrails.base.menu.controller.STCreativeStyleController;

/* loaded from: classes.dex */
public class STCreativeStyleMenuLayout extends CreativeStyleMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_CREATIVESTYLEMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        STCreativeStyleController.getInstance().updateDetailValueForCustom();
        super.closeLayout();
    }
}
