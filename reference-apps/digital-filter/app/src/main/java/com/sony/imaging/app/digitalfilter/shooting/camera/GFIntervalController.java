package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFIntervalController extends AbstractController {
    public static final String AEL = "ae-lock";
    public static final String AET_HI = "aet-hi";
    public static final String AET_LO = "aet-lo";
    public static final String AET_MID = "aet-mid";
    public static final String AE_OFF = "ae-off";
    public static final String DEFAULT_AE = "ae-lock";
    public static final int DEFAULT_SHOTS = 240;
    public static final int DEFAULT_TIME = 10;
    public static final String INTERVAL_AE = "ae-setting";
    public static final String INTERVAL_MODE = "interval-setting";
    public static final String INTERVAL_OFF = "interval-off";
    public static final String INTERVAL_ON = "interval-on";
    public static final String INTERVAL_SHOTS = "interval-shots";
    public static final String INTERVAL_TIME = "interval-time";
    private static final String TAG = AppLog.getClassName();
    private static GFIntervalController mInstance;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(INTERVAL_MODE);
        sSupportedList.add(INTERVAL_ON);
        sSupportedList.add(INTERVAL_OFF);
        sSupportedList.add(INTERVAL_TIME);
        sSupportedList.add(INTERVAL_SHOTS);
        sSupportedList.add(INTERVAL_AE);
        sSupportedList.add(AE_OFF);
        sSupportedList.add("ae-lock");
        sSupportedList.add(AET_HI);
        sSupportedList.add(AET_MID);
        sSupportedList.add(AET_LO);
    }

    public static GFIntervalController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFIntervalController createInstance() {
        if (mInstance == null) {
            mInstance = new GFIntervalController();
        }
        return mInstance;
    }

    private GFIntervalController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        String theme = GFThemeController.getInstance().getValue();
        this.mGFBackUpKey.saveIntervalValue(tag, value, theme);
    }

    public void setIntValue(String tag, int value) {
        if (tag.equals(INTERVAL_TIME) || tag.equals(INTERVAL_SHOTS)) {
            setValue(tag, "" + value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (!isSupportedInterval()) {
            return INTERVAL_OFF;
        }
        String theme = GFThemeController.getInstance().getValue();
        return this.mGFBackUpKey.getIntervalValue(tag, theme);
    }

    public int getIntValue(String tag) {
        if (!tag.equals(INTERVAL_TIME) && !tag.equals(INTERVAL_SHOTS)) {
            return 0;
        }
        String sValue = getValue(tag);
        return Integer.parseInt(sValue);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (isSupportedInterval()) {
            return sSupportedList;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isSupportedInterval() {
        return false;
    }

    private static String getEffect() {
        String theme = GFThemeController.getInstance().getValue();
        if (theme.equalsIgnoreCase(GFThemeController.BLUESKY)) {
            return "SKY_BLUE";
        }
        if (theme.equalsIgnoreCase("sunset")) {
            return "SUNSET";
        }
        if (theme.equalsIgnoreCase("standard")) {
            return "ND";
        }
        if (theme.equalsIgnoreCase("custom1")) {
            return "CUSTOM1";
        }
        if (theme.equalsIgnoreCase("custom2")) {
            return "CUSTOM2";
        }
        if (theme.equalsIgnoreCase(GFThemeController.REVERSE)) {
            return "REVERSE";
        }
        if (!theme.equalsIgnoreCase(GFThemeController.STRIPE)) {
            return "SKY_BLUE";
        }
        return "STRIPE";
    }
}
