package com.sony.imaging.app.timelapse.angleshift.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AngleShiftFaderController extends AbstractController {
    public static final String ASFADER = "ASFader";
    public static final String FADE_OFF = "FadeOff";
    public static final String FADE_ON = "FadeOn";
    private static AngleShiftFaderController mInstance;
    private static final ArrayList<String> mParamList = new ArrayList<>();
    private BackUpUtil mBackUpUtil;
    private String mSelectedValue = null;

    public static AngleShiftFaderController getInstance() {
        if (mInstance == null) {
            new AngleShiftFaderController();
        }
        return mInstance;
    }

    private static void setController(AngleShiftFaderController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AngleShiftFaderController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    static {
        mParamList.add(ASFADER);
        mParamList.add(FADE_ON);
        mParamList.add(FADE_OFF);
    }

    public void setValue(String value) {
        this.mSelectedValue = value;
        setCurrentValue(ASFADER, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(ASFADER)) {
            this.mBackUpUtil.setPreference(ASFADER, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return tag.equals(ASFADER) ? getCurValue(tag) : this.mSelectedValue;
    }

    public String getValue() {
        return getCurValue(ASFADER);
    }

    private String getCurValue(String tag) {
        if (tag.equals(ASFADER)) {
            this.mSelectedValue = this.mBackUpUtil.getPreferenceString(ASFADER, FADE_ON);
        }
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
