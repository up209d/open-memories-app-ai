package com.sony.imaging.app.portraitbeauty.menu.controller;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SelfTimerIntervalPriorityController extends AbstractController {
    public static final String SELFTIMER = "selftimer";
    public static final String SELFTIMEROFF = "selftimeroff";
    public static final String SELFTIMERON10 = "selftimeron10";
    public static final String SELFTIMERON2 = "selftimeron2";
    public static final String SELFTIMERON5 = "selftimeron5";
    public static final String SELFTIMER_CHANGE = "SelfTimerChange";
    private static final String TAG = AppLog.getClassName();
    private static SelfTimerIntervalPriorityController sInstance = null;
    private String mSelectedPriority = SELFTIMERON5;
    private ArrayList<String> mSupportedList;

    private SelfTimerIntervalPriorityController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList<>();
            this.mSupportedList.add("selftimer");
            this.mSupportedList.add(SELFTIMERON2);
            this.mSupportedList.add(SELFTIMERON5);
            this.mSupportedList.add(SELFTIMERON10);
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
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, this.mSelectedPriority);
        if (this.mSelectedPriority != SELFTIMEROFF && PortraitBeautyFocusModeController.getInstance().getValue() == "af-c") {
            PortraitBeautyFocusModeController.getInstance().setValue("af-s");
        }
        if (!BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SELFTIMERON5).equalsIgnoreCase(SELFTIMEROFF) && PortraitBeautyFocusModeController.getInstance().getValue().equalsIgnoreCase(FocusModeController.MANUAL) && ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getAutoFocusMode().equalsIgnoreCase("af-c")) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) setParams.second).setAutoFocusMode("af-s");
            CameraSetting.getInstance().setParameters(setParams);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String s_mSelectedPriority = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SELFTIMERON5);
        if (!s_mSelectedPriority.equalsIgnoreCase(this.mSelectedPriority)) {
            this.mSelectedPriority = s_mSelectedPriority;
        }
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
