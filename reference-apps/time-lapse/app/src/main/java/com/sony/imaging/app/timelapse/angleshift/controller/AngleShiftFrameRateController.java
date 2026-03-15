package com.sony.imaging.app.timelapse.angleshift.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AngleShiftFrameRateController extends AbstractController {
    public static final String ASFRAMERATE = "ASFrameRate";
    public static final String MOVIE_24P = "framerate-24p";
    public static final String MOVIE_30P = "framerate-30p";
    private static AngleShiftFrameRateController mInstance;
    private static final ArrayList<String> mParamList = new ArrayList<>();
    private BackUpUtil mBackUpUtil;
    private String mSelectedValue = null;

    public static AngleShiftFrameRateController getInstance() {
        if (mInstance == null) {
            new AngleShiftFrameRateController();
        }
        return mInstance;
    }

    private static void setController(AngleShiftFrameRateController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AngleShiftFrameRateController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    static {
        mParamList.add(ASFRAMERATE);
        mParamList.add("framerate-24p");
        mParamList.add("framerate-30p");
    }

    public void setValue(String value) {
        this.mSelectedValue = value;
        setCurrentValue(ASFRAMERATE, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(ASFRAMERATE)) {
            this.mBackUpUtil.setPreference(ASFRAMERATE, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return tag.equals(ASFRAMERATE) ? getCurValue(tag) : this.mSelectedValue;
    }

    public String getValue() {
        return getCurValue(ASFRAMERATE);
    }

    private String getCurValue(String tag) {
        if (tag.equals(ASFRAMERATE)) {
            this.mSelectedValue = this.mBackUpUtil.getPreferenceString(ASFRAMERATE, "framerate-24p");
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
