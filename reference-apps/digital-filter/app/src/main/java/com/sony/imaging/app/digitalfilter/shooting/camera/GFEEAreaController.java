package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class GFEEAreaController extends AbstractController {
    public static final String EE = "gf-ee-area";
    public static final String FIRST = "gf-ee-1st";
    public static final String LAND = "gf-ee-land";
    public static final String LAYER3 = "gf-ee-layer3";
    public static final String SELECTED = "gf-ee-selected";
    public static final String SKY = "gf-ee-sky";
    private static final String TAG = AppLog.getClassName();
    private static GFEEAreaController mInstance = null;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(EE);
        sSupportedList.add(FIRST);
        sSupportedList.add(SELECTED);
        sSupportedList.add(LAND);
        sSupportedList.add(SKY);
        sSupportedList.add(LAYER3);
    }

    public static GFEEAreaController getInstance() {
        if (mInstance == null) {
            mInstance = new GFEEAreaController();
        }
        return mInstance;
    }

    private GFEEAreaController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(EE)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.saveEEArea(value, theme);
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
        inhiEEArea();
        String theme = GFThemeController.getInstance().getValue();
        return this.mGFBackUpKey.getEEArea(theme);
    }

    private void inhiEEArea() {
        if (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS)) {
            String theme = GFThemeController.getInstance().getValue();
            String value = this.mGFBackUpKey.getEEArea(theme);
            if (value.equals(LAYER3)) {
                setValue(EE, FIRST);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        Iterator i$ = sSupportedList.iterator();
        while (i$.hasNext()) {
            String str = i$.next();
            availables.add(str);
        }
        if (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS)) {
            availables.remove(LAYER3);
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return (GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS) && LAYER3.equals(tag)) ? false : true;
    }

    public boolean isLand(String theme) {
        String value = getValue();
        return value.equals(LAND) || (value.equals(FIRST) && GFShootingOrderController.getInstance().isLandFirst()) || (value.equals(SELECTED) && GFSettingMenuLayout.getCurrentArea().equals(GFSettingMenuLayout.LAND_ID));
    }

    public boolean isSky(String theme) {
        String value = getValue();
        return value.equals(SKY) || (value.equals(FIRST) && GFShootingOrderController.getInstance().isSkyFirst()) || (value.equals(SELECTED) && GFSettingMenuLayout.getCurrentArea().equals("SkySettings"));
    }

    public boolean isLayer3(String theme) {
        String value = getValue();
        return value.equals(LAYER3) || (value.equals(FIRST) && GFShootingOrderController.getInstance().isLayer3First()) || (value.equals(SELECTED) && GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings"));
    }

    public boolean isLand() {
        String theme = GFThemeController.getInstance().getValue();
        return isLand(theme);
    }

    public boolean isSky() {
        String theme = GFThemeController.getInstance().getValue();
        return isSky(theme);
    }

    public boolean isLayer3() {
        String theme = GFThemeController.getInstance().getValue();
        return isLayer3(theme);
    }
}
