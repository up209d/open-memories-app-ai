package com.sony.imaging.app.timelapse.angleshift.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AngleShiftMovieController extends AbstractController {
    public static final String ASMOVIE_SIZE = "ASMovieSize";
    public static final String MOVIE_1280x720 = "Movie1280x720";
    public static final String MOVIE_1920x1080 = "Movie1920x1080";
    private static AngleShiftMovieController mInstance;
    private static final ArrayList<String> mParamList = new ArrayList<>();
    private BackUpUtil mBackUpUtil;
    private String mSelectedValue = null;

    public static AngleShiftMovieController getInstance() {
        if (mInstance == null) {
            new AngleShiftMovieController();
        }
        return mInstance;
    }

    private static void setController(AngleShiftMovieController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AngleShiftMovieController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    static {
        mParamList.add(ASMOVIE_SIZE);
        mParamList.add(MOVIE_1920x1080);
        mParamList.add(MOVIE_1280x720);
    }

    public void setValue(String value) {
        this.mSelectedValue = value;
        setCurrentValue(ASMOVIE_SIZE, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        this.mSelectedValue = value;
        setCurrentValue(tag, value);
    }

    private void setCurrentValue(String tag, String value) {
        if (tag.equals(ASMOVIE_SIZE)) {
            int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
            if (4 == theme && !getCurValue(ASMOVIE_SIZE).equalsIgnoreCase(value)) {
                AngleShiftSetting.getInstance().resetAngleShiftCustomRectByMovieSizeChanged();
            }
            this.mBackUpUtil.setPreference(ASMOVIE_SIZE, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return tag.equals(ASMOVIE_SIZE) ? getCurValue(tag) : this.mSelectedValue;
    }

    public String getValue() {
        return getCurValue(ASMOVIE_SIZE);
    }

    private String getCurValue(String tag) {
        if (tag.equals(ASMOVIE_SIZE)) {
            this.mSelectedValue = this.mBackUpUtil.getPreferenceString(ASMOVIE_SIZE, MOVIE_1920x1080);
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
