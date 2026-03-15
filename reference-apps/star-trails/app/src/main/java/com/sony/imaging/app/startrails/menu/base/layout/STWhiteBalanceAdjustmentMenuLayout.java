package com.sony.imaging.app.startrails.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STWhiteBalanceAdjustmentMenuLayout extends WhiteBalanceAdjustmentMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return WhiteBalanceAdjustmentMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        STUtility.getInstance().updateWhiteBalanceValue(null);
        super.closeLayout();
    }
}
