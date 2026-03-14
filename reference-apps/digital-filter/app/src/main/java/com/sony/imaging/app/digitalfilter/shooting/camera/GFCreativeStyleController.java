package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.menu.layout.GFCreativeStyleMenuLayout;

/* loaded from: classes.dex */
public class GFCreativeStyleController extends CreativeStyleController {
    private static GFCreativeStyleController mInstance = null;

    public static GFCreativeStyleController getInstance() {
        if (mInstance == null) {
            mInstance = new GFCreativeStyleController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    public CreativeStyleController.CreativeStyleOptions getDetailValueFromBackUp(String mode) {
        if (GFCreativeStyleMenuLayout.isOptionSetting) {
            return super.getDetailValueFromBackUp(mode);
        }
        String theme = GFThemeController.getInstance().getValue();
        String bkupValue = GFBackUpKey.getInstance().getCSOption(mode, theme);
        String[] optArray = bkupValue.split("/");
        CreativeStyleController.CreativeStyleOptions options = (CreativeStyleController.CreativeStyleOptions) super.getDetailValue();
        int contrast = Integer.parseInt(optArray[0]);
        int saturation = Integer.parseInt(optArray[1]);
        int sharpness = Integer.parseInt(optArray[2]);
        options.contrast = contrast;
        options.saturation = saturation;
        options.sharpness = sharpness;
        setDetailValue(options);
        return options;
    }
}
