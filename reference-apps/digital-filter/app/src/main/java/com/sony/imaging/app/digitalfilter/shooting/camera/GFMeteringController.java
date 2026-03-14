package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class GFMeteringController extends MeteringController {
    private static GFMeteringController mInstance = null;

    public static GFMeteringController getInstance() {
        if (mInstance == null) {
            mInstance = new GFMeteringController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.MeteringController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (Environment.isMeteringSpotSizeAPISupported() && itemId != null && itemId.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT)) {
            String theme = GFThemeController.getInstance().getValue();
            GFBackUpKey.getInstance().saveMeteringSpotValue(value, theme);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.MeteringController, com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        if (Environment.isMeteringSpotSizeAPISupported()) {
            if (itemId != null && itemId.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT)) {
                String theme = GFThemeController.getInstance().getValue();
                String value = GFBackUpKey.getInstance().getMeteringSpotValue(theme);
                return value;
            }
            String value2 = super.getValue(itemId);
            return value2;
        }
        String value3 = super.getValue(itemId);
        return value3;
    }
}
