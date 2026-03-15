package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class TimelapseCreativeStyleMenuLayout extends CreativeStyleMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        backUpDetailValue();
        return super.pushedFnKey();
    }

    private void backUpDetailValue() {
        CreativeStyleController.CreativeStyleOptions cStyleparam = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        String cStyleoptValue = "" + cStyleparam.contrast;
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, (cStyleoptValue + "/" + cStyleparam.saturation) + "/" + cStyleparam.sharpness);
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }
}
