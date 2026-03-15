package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FrameRateController extends AbstractController {
    public static final String FRAMERATE = "FrameRate";
    public static final String MOVIE_24P = "framerate-24p";
    public static final String MOVIE_30P = "framerate-30p";
    private static FrameRateController mInstance;
    private BackUpUtil mBackUpUtil;
    private ArrayList<String> mParamList;
    private String mSelectedValue = null;

    public static FrameRateController getInstance() {
        if (mInstance == null) {
            new FrameRateController();
        }
        return mInstance;
    }

    private static void setController(FrameRateController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected FrameRateController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
        this.mParamList = new ArrayList<>();
        this.mParamList.add(FRAMERATE);
        this.mParamList.add("framerate-24p");
        this.mParamList.add("framerate-30p");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(FRAMERATE)) {
            this.mBackUpUtil.setPreference(FRAMERATE, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return tag.equals(FRAMERATE) ? getCurValue(tag) : this.mSelectedValue;
    }

    public String getValue() {
        return getCurValue(FRAMERATE);
    }

    private String getCurValue(String tag) {
        if (tag.equals(FRAMERATE)) {
            this.mSelectedValue = this.mBackUpUtil.getPreferenceString(FRAMERATE, "framerate-24p");
        }
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1;
    }
}
