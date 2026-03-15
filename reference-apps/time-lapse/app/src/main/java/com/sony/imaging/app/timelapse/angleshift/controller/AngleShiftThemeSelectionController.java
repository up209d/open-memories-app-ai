package com.sony.imaging.app.timelapse.angleshift.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AngleShiftThemeSelectionController extends AbstractController {
    public static final String CUSTOM = "AngleShift-Custom";
    public static final String OFF = "AngleShift-Off";
    public static final String PAN = "AngleShift-Pan";
    public static final String THEMESELECTION = "AngleShiftThemeSelection";
    public static final String TILT = "AngleShift-Tilt";
    public static final String ZOOM = "AngleShift-Zoom";
    private String mTheme;
    public List<String> mThemeList;
    private static final String TAG = AppLog.getClassName();
    private static AngleShiftThemeSelectionController sInstance = null;
    private static final ArrayList<String> mSupportedValue = new ArrayList<>();

    static {
        mSupportedValue.add(THEMESELECTION);
        mSupportedValue.add(PAN);
        mSupportedValue.add(TILT);
        mSupportedValue.add(ZOOM);
        mSupportedValue.add(OFF);
        mSupportedValue.add(CUSTOM);
    }

    private AngleShiftThemeSelectionController() {
        this.mTheme = PAN;
        this.mThemeList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mThemeList = mSupportedValue;
        this.mThemeList.remove(THEMESELECTION);
        this.mTheme = this.mThemeList.get(BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static AngleShiftThemeSelectionController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new AngleShiftThemeSelectionController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mTheme = value;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mTheme;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
