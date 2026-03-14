package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFAreaSettingDummyController extends AbstractController {
    private static final String LAND = "LandSettings";
    private static final String TAG = AppLog.getClassName();
    private static GFAreaSettingDummyController mInstance = null;
    private static ArrayList<String> sSupportedList;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("LandSettings");
    }

    public static GFAreaSettingDummyController getInstance() {
        if (mInstance == null) {
            mInstance = new GFAreaSettingDummyController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return "LandSettings";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if ("LandSettings".equals(tag)) {
            return sSupportedList;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return "LandSettings".equals(tag);
    }
}
