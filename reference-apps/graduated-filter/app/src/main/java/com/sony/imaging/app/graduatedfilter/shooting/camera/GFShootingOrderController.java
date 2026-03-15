package com.sony.imaging.app.graduatedfilter.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFShootingOrderController extends AbstractController {
    public static final String BASE_FILTER = "gf-base-filter";
    public static final String FILTER_BASE = "gf-filter-base";
    public static final String ORDER = "gf-shooting-order";
    private static final String TAG = AppLog.getClassName();
    private static GFShootingOrderController mInstance = null;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(ORDER);
        sSupportedList.add(FILTER_BASE);
        sSupportedList.add(BASE_FILTER);
    }

    public static GFShootingOrderController getInstance() {
        if (mInstance == null) {
            mInstance = new GFShootingOrderController();
        }
        return mInstance;
    }

    private GFShootingOrderController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(ORDER)) {
            this.mBackupUtil.setPreference(ORDER, value);
        }
    }

    public String getValue() {
        try {
            return getValue(null);
        } catch (IController.NotSupportedException e) {
            return null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return this.mBackupUtil.getPreferenceString(ORDER, FILTER_BASE);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
