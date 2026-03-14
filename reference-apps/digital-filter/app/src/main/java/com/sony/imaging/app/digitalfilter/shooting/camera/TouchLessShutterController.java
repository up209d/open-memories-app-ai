package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class TouchLessShutterController extends AbstractController {
    private static final String KEY_BKUP_OPTIONS_PARAM__TOUCHLESS_SHUTTER_CONTROLLER_VALUE = "GF_TOUCHLESS_SHUTTER_CONTROLLER_VALUE";
    public static final String OFF = "Off";
    public static final String ON = "On";
    public static final String TAG_SECONDSHOOTING_PRIORITY = "SecondShootingPriority";
    private static final String TAG = AppLog.getClassName();
    public static String TAG_TOUCHLESS_SHUTTER = "TouchLessShutter";
    public static boolean ExposingByTouchLessShutter = false;
    private static List<String> mSupportedList = null;
    private static TouchLessShutterController sInstance = null;
    private static final HashMap<String, String> SECOND_SHOOTING_VALUES = new HashMap<>();

    static {
        SECOND_SHOOTING_VALUES.put("Off", "Off");
        SECOND_SHOOTING_VALUES.put("On", "On");
    }

    private TouchLessShutterController() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mSupportedList == null) {
            mSupportedList = new ArrayList();
            mSupportedList.add("Off");
            mSupportedList.add("On");
        }
        ExposingByTouchLessShutter = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static TouchLessShutterController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new TouchLessShutterController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        BackUpUtil.getInstance().setPreference(KEY_BKUP_OPTIONS_PARAM__TOUCHLESS_SHUTTER_CONTROLLER_VALUE, value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return BackUpUtil.getInstance().getPreferenceString(KEY_BKUP_OPTIONS_PARAM__TOUCHLESS_SHUTTER_CONTROLLER_VALUE, "Off");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!hasTouchLessAddOn() || 1 != ScalarProperties.getInt("device.evf.internal.supported")) {
            return null;
        }
        if (mSupportedList.isEmpty()) {
            mSupportedList = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> supporteds;
        AppLog.enter(TAG, AppLog.getMethodName());
        ArrayList<String> availables = new ArrayList<>();
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (2 != device && (supporteds = getSupportedValue(null)) != null) {
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable(TAG_SECONDSHOOTING_PRIORITY, mode)) {
                    availables.add(mode);
                }
            }
        }
        if (availables.isEmpty()) {
            availables = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        return 2 != device;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bRetVal = isUnavailableAPISceneFactor(TAG_SECONDSHOOTING_PRIORITY, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return bRetVal;
    }

    public boolean hasTouchLessAddOn() {
        return true;
    }
}
