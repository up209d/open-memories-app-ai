package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFWhiteBalanceLimitController extends AbstractController {
    public static final String CTEMP = "limit-only-ctemp";
    public static final String CTEMP_AWB = "limit-ctemp-awb";
    public static final String NONE = "no-limit";
    private static final String TAG = AppLog.getClassName();
    public static final String WB_LIMIT = "gf-wb-limit-mode";
    private static GFWhiteBalanceLimitController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(WB_LIMIT);
        sSupportedList.add(NONE);
        sSupportedList.add(CTEMP_AWB);
        sSupportedList.add(CTEMP);
    }

    public static GFWhiteBalanceLimitController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFWhiteBalanceLimitController createInstance() {
        if (mInstance == null) {
            mInstance = new GFWhiteBalanceLimitController();
        }
        return mInstance;
    }

    private GFWhiteBalanceLimitController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(WB_LIMIT)) {
            this.mBackupUtil.setPreference(WB_LIMIT, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mBackupUtil.getPreferenceString(WB_LIMIT, NONE);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            sSupportedList.remove(CTEMP_AWB);
        }
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            sSupportedList.remove(CTEMP_AWB);
        }
        return sSupportedList;
    }

    public boolean isLimitToCTemp() {
        String value = getValue(WB_LIMIT);
        return CTEMP.equals(value);
    }

    public boolean isLimitToCTempAWB() {
        String value = getValue(WB_LIMIT);
        return CTEMP_AWB.equals(value);
    }
}
