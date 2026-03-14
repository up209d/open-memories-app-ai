package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFFilterSetController extends AbstractController {
    public static final String FILTER_SET = "filter-set";
    public static final String LAYER3_SETTINGS = "Layer3Settings";
    private static final String TAG = AppLog.getClassName();
    public static final String THREE_AREAS = "three-areas";
    public static final String TWO_AREAS = "two-areas";
    private static GFFilterSetController mInstance;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(FILTER_SET);
        sSupportedList.add(TWO_AREAS);
        sSupportedList.add(THREE_AREAS);
    }

    public static GFFilterSetController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFFilterSetController createInstance() {
        if (mInstance == null) {
            mInstance = new GFFilterSetController();
        }
        return mInstance;
    }

    private GFFilterSetController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (value.equals(TWO_AREAS) || value.equals(THREE_AREAS)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.saveFilterSetValue(value, theme);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.FILTER_SET_CHANGED);
        }
    }

    public void setValue(String value) {
        AppLog.info(TAG, "setValue  value =  " + value);
        if (value.equals(TWO_AREAS) || value.equals(THREE_AREAS)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.saveFilterSetValue(value, theme);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String theme = GFThemeController.getInstance().getValue();
        String value = this.mGFBackUpKey.getFilterSetValue(theme);
        return value;
    }

    public String getValue() {
        String theme = GFThemeController.getInstance().getValue();
        String value = this.mGFBackUpKey.getFilterSetValue(theme);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (tag.equals("Layer3Settings")) {
            return THREE_AREAS.equals(getValue());
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean need3rdShooting(String value) {
        return (value == null || value.equals(TWO_AREAS) || !value.equals(THREE_AREAS)) ? false : true;
    }

    public boolean need3rdShooting() {
        return need3rdShooting(getValue());
    }
}
