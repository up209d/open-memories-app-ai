package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class GFThemeController extends AbstractController {
    public static final String CUSTOM1 = "custom1";
    public static final String CUSTOM2 = "custom2";
    public static final String DEFAULT_THEME = "standard";
    public static final String STANDARD = "standard";
    public static final String SUNSET = "sunset";
    public static final String THEME = "theme";
    private static GFThemeController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;
    private static final String TAG = AppLog.getClassName();
    public static final String REVERSE = "reverse";
    public static final String STRIPE = "stripe";
    public static final String BLUESKY = "skyblue";
    private static final List<Pair<String, Integer>> sThemeNameList = Arrays.asList(new Pair("standard", Integer.valueOf(R.string.STRID_FUNC_SKYND_TGRADUATEDND)), new Pair(REVERSE, Integer.valueOf(R.string.STRID_FUNC_SKYND_TREVERSEGND)), new Pair(STRIPE, Integer.valueOf(R.string.STRID_FUNC_SKYND_TSTRIPE2)), new Pair(BLUESKY, Integer.valueOf(R.string.STRID_FUNC_SKYND_TBLUESKY)), new Pair("sunset", Integer.valueOf(R.string.STRID_FUNC_SKYND_TSUNSET)), new Pair("custom1", Integer.valueOf(android.R.string.capability_title_canTakeScreenshot)), new Pair("custom2", Integer.valueOf(android.R.string.capital_off)));

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(THEME);
        sSupportedList.add("standard");
        sSupportedList.add(REVERSE);
        sSupportedList.add(STRIPE);
        sSupportedList.add(BLUESKY);
        sSupportedList.add("sunset");
        sSupportedList.add("custom1");
        sSupportedList.add("custom2");
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
            GFBackUpKey.getInstance().checkWBLimit();
            params.unflatten(GFBackUpKey.getInstance().getLastParameters());
            GFEffectParameters.getInstance().setParameters(params);
        }
        GFBackUpKey.getInstance().checkWBLimit();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mBackupUtil.getPreferenceString(GFBackUpKey.KEY_SELECTED_EFFECT, "standard");
    }

    public String getValue() {
        return getValue(THEME);
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

    public List<Pair<String, Integer>> getThemeNameList() {
        return sThemeNameList;
    }
}
