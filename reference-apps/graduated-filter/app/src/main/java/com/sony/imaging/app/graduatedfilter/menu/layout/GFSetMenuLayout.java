package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.graduatedfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageAdjustmentController;

/* loaded from: classes.dex */
public class GFSetMenuLayout extends SetMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        String itemID = this.mService.getMenuItemId();
        if (232 == code && itemID.equalsIgnoreCase(GFImageAdjustmentController.ADJUSTMENT)) {
            GFKikiLogUtil.getInstance().countSkipAdjustmentChanging();
        }
        return super.onKeyDown(keyCode, event);
    }
}
