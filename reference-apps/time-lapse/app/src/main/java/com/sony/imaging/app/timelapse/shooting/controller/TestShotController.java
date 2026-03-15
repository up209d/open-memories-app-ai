package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TestShotController extends AbstractController {
    public static final String TESTSHOT_ASSIGN_KEY = "testshot";
    public static final String TESTSHOT_OFF = "testshot-off";
    public static final String TESTSHOT_ON = "testshot-on";
    private static TestShotController mInstance;
    private BackUpUtil mBackUpUtil;
    private ArrayList<String> mParamList;
    private String mSelectedValue = null;

    public static TestShotController getInstance() {
        if (mInstance == null) {
            new TestShotController();
        }
        return mInstance;
    }

    private static void setController(TestShotController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected TestShotController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
        this.mParamList = new ArrayList<>();
        this.mParamList.add(TESTSHOT_ASSIGN_KEY);
        this.mParamList.add(TESTSHOT_ON);
        this.mParamList.add(TESTSHOT_OFF);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(TESTSHOT_ASSIGN_KEY)) {
            this.mBackUpUtil.setPreference(TESTSHOT_ASSIGN_KEY, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return tag.equals(TESTSHOT_ASSIGN_KEY) ? getCurValue(tag) : this.mSelectedValue;
    }

    private String getCurValue(String tag) {
        if (tag.equals(TESTSHOT_ASSIGN_KEY)) {
            this.mSelectedValue = this.mBackUpUtil.getPreferenceString(TESTSHOT_ASSIGN_KEY, TESTSHOT_ON);
        }
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (Environment.getVersionOfHW() == 1) {
            return null;
        }
        ArrayList<String> getSupportedValueList = this.mParamList;
        return getSupportedValueList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (Environment.getVersionOfHW() == 1) {
            return null;
        }
        ArrayList<String> getAvailableValueList = this.mParamList;
        return getAvailableValueList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
