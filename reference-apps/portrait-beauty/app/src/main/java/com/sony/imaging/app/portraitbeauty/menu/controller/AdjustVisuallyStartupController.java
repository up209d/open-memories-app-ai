package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AdjustVisuallyStartupController extends AbstractController {
    public static final String ADJUST_VISULLY_STARTUP_ITEM_ID = "AdjustVisullyStatupItemId";
    public static final String ADJUST_VISULLY_STARTUP_OFF = "Off";
    public static final String ADJUST_VISULLY_STARTUP_ON = "On";
    public static final String ADJUST_VISULLY_STARTUP_SETTING = "ADJUST_VISULLY_STARTUP_SETTING";
    private static AdjustVisuallyStartupController mInstance;
    private BackUpUtil mBackUpUtil;
    private ArrayList<String> mParamList;
    private String mSelectedValue = null;
    private boolean isAdjustVisullySelectedInMenu = false;

    public static AdjustVisuallyStartupController getInstance() {
        if (mInstance == null) {
            new AdjustVisuallyStartupController();
        }
        return mInstance;
    }

    private static void setController(AdjustVisuallyStartupController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AdjustVisuallyStartupController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
        this.mParamList = new ArrayList<>();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(ADJUST_VISULLY_STARTUP_ITEM_ID)) {
            this.mBackUpUtil.setPreference(ADJUST_VISULLY_STARTUP_SETTING, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return tag.equals(ADJUST_VISULLY_STARTUP_ITEM_ID) ? getCurValue(tag) : this.mSelectedValue;
    }

    private String getCurValue(String tag) {
        return tag.equals(ADJUST_VISULLY_STARTUP_ITEM_ID) ? this.mBackUpUtil.getPreferenceString(ADJUST_VISULLY_STARTUP_SETTING, "On") : this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        this.mParamList.add("On");
        this.mParamList.add("Off");
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        this.mParamList.add("On");
        this.mParamList.add("Off");
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    public boolean isAdjustVisullySelectedInMenu() {
        return this.isAdjustVisullySelectedInMenu;
    }

    public void setAdjustVisullySelectedInMenu(boolean isAdjustVisullySelectedInMenu) {
        this.isAdjustVisullySelectedInMenu = isAdjustVisullySelectedInMenu;
    }
}
