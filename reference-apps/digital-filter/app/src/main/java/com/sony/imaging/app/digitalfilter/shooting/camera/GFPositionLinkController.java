package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFPositionLinkController extends AbstractController {
    public static final String OFF = "position-link-off";
    public static final String ON = "position-link-on";
    public static final String RELATIVE_MODE = "position-link-mode";
    private static final String TAG = AppLog.getClassName();
    private static GFPositionLinkController mInstance;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(RELATIVE_MODE);
        sSupportedList.add(OFF);
        sSupportedList.add(ON);
    }

    public static GFPositionLinkController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFPositionLinkController createInstance() {
        if (mInstance == null) {
            mInstance = new GFPositionLinkController();
        }
        return mInstance;
    }

    private GFPositionLinkController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(RELATIVE_MODE)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.savePositionLinkValue(value, theme);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String theme = GFThemeController.getInstance().getValue();
        return this.mGFBackUpKey.getPositionLinkValue(theme);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isLink() {
        String value = getValue(RELATIVE_MODE);
        return ON.equals(value);
    }
}
