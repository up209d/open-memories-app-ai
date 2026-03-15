package com.sony.imaging.app.manuallenscompensation.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DeleteFocusMagnifierController extends AbstractController {
    public static final String FOCUS_MAGNIFIER_OFF = "-";
    public static final String FOCUS_MAGNIFIER_ON = "Focus Magnifier";
    public static final String FOCUS_MAGNIFIER_SWITCH_IC = "cE800FocusMagnifier";
    private static final String TAG = "CUR_FOCUS_MAGNIFIER";
    private static DeleteFocusMagnifierController mInstance;
    private BackUpUtil mBackUpUtil;
    private ArrayList<String> mParamList;
    private String mSelectedValue = null;

    public static DeleteFocusMagnifierController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            new DeleteFocusMagnifierController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static void setController(DeleteFocusMagnifierController controller) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = controller;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected DeleteFocusMagnifierController() {
        this.mBackUpUtil = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
        this.mParamList = new ArrayList<>();
        this.mParamList.add(TAG);
        this.mParamList.add(FOCUS_MAGNIFIER_ON);
        this.mParamList.add(FOCUS_MAGNIFIER_OFF);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setCurrentValue(String tag, String value) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (tag.equals(FOCUS_MAGNIFIER_SWITCH_IC)) {
            this.mBackUpUtil.setPreference(TAG, value);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (tag.equals(FOCUS_MAGNIFIER_SWITCH_IC)) {
            return getCurValue(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedValue;
    }

    private String getCurValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (tag.equals(FOCUS_MAGNIFIER_SWITCH_IC)) {
            return this.mBackUpUtil.getPreferenceString(TAG, FOCUS_MAGNIFIER_ON);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        ArrayList<String> getSupportedValueList;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (Environment.getVersionOfHW() == 1) {
            getSupportedValueList = null;
        } else {
            getSupportedValueList = this.mParamList;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return getSupportedValueList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> getAvailableValueList;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (Environment.getVersionOfHW() == 1) {
            getAvailableValueList = null;
        } else {
            getAvailableValueList = this.mParamList;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return getAvailableValueList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
