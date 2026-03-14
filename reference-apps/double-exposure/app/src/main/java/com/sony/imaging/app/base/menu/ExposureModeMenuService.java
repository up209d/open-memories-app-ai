package com.sony.imaging.app.base.menu;

import android.content.Context;
import com.sony.imaging.app.base.menu.MenuAccessor;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;

/* loaded from: classes.dex */
public class ExposureModeMenuService extends BaseMenuService {
    public ExposureModeMenuService(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.menu.BaseMenuService
    protected MenuAccessor.DisplayMenuItem getDisplayItem(String itemId) {
        MenuAccessor.DisplayMenuItem item = this.mAccessor.getDisplayItem(itemId);
        return item;
    }

    @Override // com.sony.imaging.app.base.menu.BaseMenuService
    public String getMenuItemOptionStr(String itemid) {
        String optionStr = super.getMenuItemOptionStr(itemid);
        if (optionStr == null) {
            return null;
        }
        String[] optionStrAry = optionStr.split(AbstractSupportedChecker.SEPARATOR, -1);
        if (ModeDialDetector.getDialPosition() == 2) {
            String str = optionStrAry[1];
            if (str == null) {
                return optionStrAry[0];
            }
            return str;
        }
        return optionStrAry[0];
    }
}
