package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;

/* loaded from: classes.dex */
public class ColorGradingPresetParameterResetScreen extends SetMenuLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        super.doItemClickProcessing(itemid);
        Bundle bundle = this.data;
        bundle.putString("ID", itemid);
        openLayout("ID_COLORGRADINGPRESETRESETCONFIRMATIONSCREEN", bundle);
    }
}
