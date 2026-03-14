package com.sony.imaging.app.digitalfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class GFFn15LayerMenuLayout extends Fn15LayerMenuLayout {
    public static final String MENU_ID = "ID_GFFN15LAYERMENULAYOUT";
    private static boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        System.gc();
        isCanceled = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            String itemID = this.mService.getMenuItemId();
            if (itemID.equalsIgnoreCase(DROAutoHDRController.MENU_ITEM_ID_DROHDR)) {
                params.setDRO(DROAutoHDRController.getInstance().getValue());
            } else if (itemID.equalsIgnoreCase(FlashController.FLASHMODE)) {
                if (!isCanceled) {
                    String mode = FlashController.getInstance().getSettingValue(FlashController.FLASHMODE);
                    params.setFlashMode(mode);
                }
            } else if (itemID.equalsIgnoreCase("MeteringMode")) {
                String meteringID = MeteringController.getInstance().getValue("MeteringMode");
                params.setMeteringMode(meteringID);
                if (Environment.isMeteringSpotSizeAPISupported() && meteringID.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT)) {
                    String theme = GFThemeController.getInstance().getValue();
                    String value = MeteringController.getInstance().getValue(meteringID);
                    GFBackUpKey.getInstance().saveMeteringSpotValue(value, theme);
                }
            }
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }
}
