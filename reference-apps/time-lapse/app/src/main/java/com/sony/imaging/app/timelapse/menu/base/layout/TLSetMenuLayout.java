package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TLSetMenuLayout extends SetMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        TLCommonUtil.getInstance().prepareStillImageChecking(this.mService);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        TLCommonUtil.getInstance().checkStillImageSettings();
        super.onPause();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }
}
