package com.sony.imaging.app.graduatedfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFColorSettingController extends AbstractController {
    private static final String RESET = "";
    private static final String TAG = AppLog.getClassName();
    private static GFColorSettingController mInstance = null;

    public static GFColorSettingController getInstance() {
        if (mInstance == null) {
            mInstance = new GFColorSettingController();
        }
        return mInstance;
    }

    private GFColorSettingController() {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return "";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> availables = new ArrayList<>();
        availables.add("");
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        availables.add("");
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        boolean isInitialSetting = params.getWbGM() == 0 && params.getWbAB() == 0;
        return !isInitialSetting;
    }
}
