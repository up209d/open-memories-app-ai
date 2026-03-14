package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFViewModeController extends AbstractController {
    public static final String INTERVAL_PB = "interval-playback";
    public static final String NORMAL_PB = "normal-playback";
    private static final String TAG = AppLog.getClassName();
    public static final String VIEW_MODE = "view-mode";
    private static GFViewModeController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(VIEW_MODE);
        sSupportedList.add(NORMAL_PB);
        sSupportedList.add(INTERVAL_PB);
    }

    public static GFViewModeController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFViewModeController createInstance() {
        if (mInstance == null) {
            mInstance = new GFViewModeController();
        }
        return mInstance;
    }

    private GFViewModeController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (value.equals(NORMAL_PB) || value.equals(INTERVAL_PB)) {
            this.mBackupUtil.setPreference(VIEW_MODE, value);
        }
    }

    public void setValue(String value) {
        AppLog.info(TAG, "setValue  value =  " + value);
        if (value.equals(NORMAL_PB) || value.equals(INTERVAL_PB)) {
            this.mBackupUtil.setPreference(VIEW_MODE, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mBackupUtil.getPreferenceString(VIEW_MODE, NORMAL_PB);
    }

    public String getValue() {
        return this.mBackupUtil.getPreferenceString(VIEW_MODE, NORMAL_PB);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (GFIntervalController.getInstance().isSupportedInterval()) {
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
}
