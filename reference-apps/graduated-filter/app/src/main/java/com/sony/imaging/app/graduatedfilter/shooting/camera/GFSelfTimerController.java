package com.sony.imaging.app.graduatedfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFSelfTimerController extends AbstractController {
    public static final String DRIVE_MODE = "drivemode";
    public static final String SELF_TIMER_OFF = "selftimeroff";
    public static final String SELF_TIMER_ON = "selftimeron";
    private static final String TAG = AppLog.getClassName();
    private static GFSelfTimerController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("drivemode");
        sSupportedList.add(SELF_TIMER_ON);
        sSupportedList.add(SELF_TIMER_OFF);
    }

    public static GFSelfTimerController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFSelfTimerController createInstance() {
        if (mInstance == null) {
            mInstance = new GFSelfTimerController();
        }
        return mInstance;
    }

    private GFSelfTimerController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals("drivemode")) {
            this.mBackupUtil.setPreference("drivemode", value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mBackupUtil.getPreferenceString("drivemode", SELF_TIMER_OFF);
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
