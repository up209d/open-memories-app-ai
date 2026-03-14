package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.List;

/* loaded from: classes.dex */
public class GFISOSensitivityController extends ISOSensitivityController {
    private static GFISOSensitivityController mInstance = null;

    public static GFISOSensitivityController getInstance() {
        if (mInstance == null) {
            mInstance = new GFISOSensitivityController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        List<String> list;
        if (ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN.equals(itemId) || ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX.equals(itemId)) {
            boolean isInit = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_ISO_AUTO_MAXMIN_INIT, false);
            if (!isInit && (list = getSupportedValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN)) != null) {
                String defaultMinIso = list.get(0);
                int maxIsoIndex = list.indexOf(ISOSensitivityController.ISO_800);
                setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN, defaultMinIso);
                if (maxIsoIndex != -1) {
                    setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX, ISOSensitivityController.ISO_800);
                } else {
                    setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX, defaultMinIso);
                }
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_ISO_AUTO_MAXMIN_INIT, true);
            }
        }
        return super.getValue(itemId);
    }
}
