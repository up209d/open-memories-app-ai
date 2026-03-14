package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFShootingOrderController extends AbstractController {
    public static final String ORDER = "gf-shooting-order";
    public static final String ORDER_123 = "gf-shooting-order123";
    public static final String ORDER_132 = "gf-shooting-order132";
    public static final String ORDER_213 = "gf-shooting-order213";
    public static final String ORDER_231 = "gf-shooting-order231";
    public static final String ORDER_312 = "gf-shooting-order312";
    public static final String ORDER_321 = "gf-shooting-order321";
    private static final String TAG = AppLog.getClassName();
    private static GFShootingOrderController mInstance = null;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(ORDER);
        sSupportedList.add(ORDER_123);
        sSupportedList.add(ORDER_132);
        sSupportedList.add(ORDER_213);
        sSupportedList.add(ORDER_231);
        sSupportedList.add(ORDER_312);
        sSupportedList.add(ORDER_321);
    }

    public static GFShootingOrderController getInstance() {
        if (mInstance == null) {
            mInstance = new GFShootingOrderController();
        }
        return mInstance;
    }

    private GFShootingOrderController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(ORDER)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.saveShootingOrder(value, theme);
        }
    }

    public String getValue() {
        try {
            return getValue(null);
        } catch (IController.NotSupportedException e) {
            return null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String theme = GFThemeController.getInstance().getValue();
        return this.mGFBackUpKey.getShootingOrder(theme);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    public boolean isLandFirst() {
        String value = getValue();
        boolean isLand3 = value.equals(ORDER_123) || value.equals(ORDER_132);
        boolean isLand2 = isLandSkyOrder() && !GFFilterSetController.getInstance().need3rdShooting();
        return isLand3 || isLand2;
    }

    public boolean isSkyFirst() {
        String value = getValue();
        boolean isSky3 = value.equals(ORDER_213) || value.equals(ORDER_231);
        boolean isSky2 = (isLandSkyOrder() || GFFilterSetController.getInstance().need3rdShooting()) ? false : true;
        return isSky3 || isSky2;
    }

    public boolean isLayer3First() {
        String value = getValue();
        return (value.equals(ORDER_312) || value.equals(ORDER_321)) && GFFilterSetController.getInstance().need3rdShooting();
    }

    public boolean isLayer3Second() {
        String value = getValue();
        return value.equals(ORDER_132) || value.equals(ORDER_231);
    }

    public boolean isLayer3Third() {
        String value = getValue();
        return value.equals(ORDER_123) || value.equals(ORDER_213);
    }

    public boolean isLandSkyOrder() {
        String value = getValue();
        return value.equals(ORDER_123) || value.equals(ORDER_132) || value.equals(ORDER_312);
    }
}
