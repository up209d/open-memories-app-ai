package com.sony.imaging.app.base.menu.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;

/* loaded from: classes.dex */
public class UnknownItemMenuLayout extends DisplayMenuItemsMenuLayout {
    public static final String MENU_ID = "ID_UNKOWNITEMMENULAYOUT";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        return false;
    }
}
