package com.sony.imaging.app.startrails.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STExposureCompensationMenuLayout extends ExposureCompensationMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_EXPOSURECOMPENSATIONMENULAYOUT";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout
    public void setCompensationLevel(int level) {
        super.setCompensationLevel(level);
        STUtility.getInstance().updateBackValue();
    }
}
