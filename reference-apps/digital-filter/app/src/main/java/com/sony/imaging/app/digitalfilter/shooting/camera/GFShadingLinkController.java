package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFShadingLinkController extends AbstractController {
    public static final String OFF = "shading-link-off";
    public static final String ON = "shading-link-on";
    public static final String RELATIVE_MODE = "shading-link-mode";
    private static final String TAG = AppLog.getClassName();
    private static GFShadingLinkController mInstance;
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

    public static GFShadingLinkController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFShadingLinkController createInstance() {
        if (mInstance == null) {
            mInstance = new GFShadingLinkController();
        }
        return mInstance;
    }

    private GFShadingLinkController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(RELATIVE_MODE)) {
            String theme = GFThemeController.getInstance().getValue();
            this.mGFBackUpKey.saveShadingLinkValue(value, theme);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String theme = GFThemeController.getInstance().getValue();
        return this.mGFBackUpKey.getShadingLinkValue(theme);
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
