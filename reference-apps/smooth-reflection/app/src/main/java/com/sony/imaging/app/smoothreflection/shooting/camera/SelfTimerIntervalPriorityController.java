package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SelfTimerIntervalPriorityController extends AbstractController {
    public static final String SELFTIMER = "selftimer";
    public static final String SELFTIMEROFF = "selftimeroff";
    public static final String SELFTIMERON = "selftimeron";
    public static final String SELFTIMER_CHANGE = "SelfTimerChange";
    private static final String TAG = AppLog.getClassName();
    private static SelfTimerIntervalPriorityController sInstance = null;
    private String mSelectedPriority = SELFTIMEROFF;
    private ArrayList<String> mSupportedList;

    private SelfTimerIntervalPriorityController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList<>();
            this.mSupportedList.add("selftimer");
            this.mSupportedList.add(SELFTIMERON);
            this.mSupportedList.add(SELFTIMEROFF);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static SelfTimerIntervalPriorityController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new SelfTimerIntervalPriorityController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedPriority = value;
        BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_SELF_TIMER, this.mSelectedPriority);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedPriority;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
