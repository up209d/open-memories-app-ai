package com.sony.imaging.app.graduatedfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFThemeController extends AbstractController {
    public static final String CUSTOM1 = "custom1";
    public static final String CUSTOM2 = "custom2";
    public static final String CUSTOM3 = "custom3";
    public static final String DEFAULT_THEME = "skyblue";
    public static final String SKYBLUE = "skyblue";
    public static final String STANDARD = "standard";
    public static final String SUNSET = "sunset";
    private static final String TAG = AppLog.getClassName();
    public static final String THEME = "theme";
    private static final String THEME1 = "skyblue";
    private static final String THEME2 = "sunset";
    private static final String THEME3 = "standard";
    private static final String THEME4 = "custom1";
    private static final String THEME5 = "custom2";
    private static final String THEME6 = "custom3";
    private static GFThemeController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(THEME);
        sSupportedList.add("skyblue");
        sSupportedList.add("sunset");
        sSupportedList.add("standard");
        sSupportedList.add("custom1");
        sSupportedList.add("custom2");
        sSupportedList.add("custom3");
    }

    public static GFThemeController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFThemeController createInstance() {
        if (mInstance == null) {
            mInstance = new GFThemeController();
        }
        return mInstance;
    }

    private GFThemeController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String lastParams = getValue(tag);
        if (tag.equals(THEME) && !lastParams.equalsIgnoreCase(value)) {
            this.mBackupUtil.setPreference(GFBackUpKey.KEY_SELECTED_EFFECT, value);
            params.setEffect(GFBackUpKey.getInstance().getLastEffect());
            params.unflatten(GFBackUpKey.getInstance().getLastParameters());
            GFEffectParameters.getInstance().setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mBackupUtil.getPreferenceString(GFBackUpKey.KEY_SELECTED_EFFECT, "skyblue");
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
}
