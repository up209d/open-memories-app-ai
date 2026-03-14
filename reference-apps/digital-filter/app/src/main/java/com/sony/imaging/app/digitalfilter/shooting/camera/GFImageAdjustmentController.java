package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFImageAdjustmentController extends AbstractController {
    public static final String ADJUSTMENT = "gf-adjustment";
    public static final String OFF = "gf-adjustment-off";
    public static final String ON = "gf-adjustment-on";
    private static final String TAG = AppLog.getClassName();
    private static GFImageAdjustmentController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(ADJUSTMENT);
        sSupportedList.add(ON);
        sSupportedList.add(OFF);
    }

    public static GFImageAdjustmentController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFImageAdjustmentController createInstance() {
        if (mInstance == null) {
            mInstance = new GFImageAdjustmentController();
        }
        return mInstance;
    }

    private GFImageAdjustmentController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(ADJUSTMENT)) {
            this.mBackupUtil.setPreference(ADJUSTMENT, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE)) ? OFF : this.mBackupUtil.getPreferenceString(ADJUSTMENT, OFF);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return !GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE));
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }
}
