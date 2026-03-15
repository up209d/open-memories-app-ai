package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.ShootingModeController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeController extends ShootingModeController {
    public static final String SHOTS_DEFAULT_VALUE = "64";
    public static final String SMOOTHING = "Smoothing";
    public static final String THEMESELECTION = "Theme";
    protected static ThemeController mInstance;
    protected static List<String> mSupportedList;
    protected static List<String> mSupportedMonotoneList;
    protected static List<String> mSupportedSmoothingList;
    private static final String TAG = AppLog.getClassName();
    public static final String TWILIGHTREFLECTION = "TwilightReflection";
    public static final String WATERFLOW = "WaterFlow";
    public static final String SILENT = "Silent";
    public static final String SMOKEHAZE = "SmokeHaze";
    public static final String MONOTONE = "Monotone";
    public static final String CUSTOM = "Custom";
    protected static String[] SUPPORTED_ARRAY = {TWILIGHTREFLECTION, WATERFLOW, SILENT, SMOKEHAZE, MONOTONE, CUSTOM};
    public static final String LOW = "Low";
    public static final String MID = "Mid";
    public static final String HIGH = "High";
    protected static String[] SUPPORTED_SMOOTHING_ARRAY = {LOW, MID, HIGH};
    public static final String BW = "Bw";
    public static final String SEPIA = "Sepia";
    public static final String WARM = "Warm";
    public static final String COOL = "Cool";
    public static final String GREEN = "Green";
    protected static String[] SUPPORTED_MONOTONE_ARRAY = {BW, SEPIA, WARM, COOL, GREEN};
    protected static String[] SUPPORTED_MONOTONE_ARRAY_AVIP = {BW, SEPIA};
    public static String[] SUPPORTED_SHOTS_ARRAY = {"2", "4", "6", "8", "16", "32", "48", "64", "96", "128", "192", "256"};
    protected String mCurrentValue = TWILIGHTREFLECTION;
    protected String mPrevValue = TWILIGHTREFLECTION;
    protected String mCurrentSmoothingValue = MID;
    protected String mCurrentMonotoneValue = BW;

    public static IController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ThemeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private String getPictureQuality() {
        try {
            String quality = PictureQualityController.getInstance().getValue(null);
            return quality;
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
            return null;
        }
    }

    private void changePictureQuality(String tag, String value) {
        if (tag != null) {
            if (MONOTONE.equals(this.mPrevValue)) {
                if (!this.mPrevValue.equals(this.mCurrentValue) && BackUpUtil.getInstance().getPreferenceBoolean(SmoothReflectionBackUpKey.KEY_PICTUREQUALITY, false)) {
                    PictureQualityController.getInstance().setValue(SmoothReflectionPictureQualityController.API_NAME, PictureQualityController.PICTURE_QUALITY_RAWJPEG);
                    return;
                }
                return;
            }
            if (MONOTONE.equals(this.mCurrentValue)) {
                if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(getPictureQuality())) {
                    PictureQualityController.getInstance().setValue(SmoothReflectionPictureQualityController.API_NAME, PictureQualityController.PICTURE_QUALITY_FINE);
                    BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_PICTUREQUALITY, true);
                } else {
                    BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_PICTUREQUALITY, false);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        changePictureQuality(tag, value);
        if (MONOTONE.equals(tag)) {
            this.mCurrentMonotoneValue = value;
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, value);
            if (CameraSetting.getInstance().getCamera() != null) {
                SmoothReflectionUtil.getInstance().setMonotoneSetting(this.mCurrentMonotoneValue);
                SmoothReflectionUtil.getInstance().applyGammaTable();
            }
        } else if (SMOOTHING.equals(tag)) {
            String currentTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, TWILIGHTREFLECTION);
            if (currentTheme.equals(CUSTOM)) {
                this.mCurrentSmoothingValue = value;
                BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, value);
            } else {
                this.mCurrentSmoothingValue = value;
                BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_SMOOTH_LEVEL, value);
            }
        } else if (THEMESELECTION.equals(tag)) {
            this.mPrevValue = this.mCurrentValue;
            this.mCurrentValue = value;
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, value);
            if (MONOTONE.equals(value)) {
                String monotoneValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, this.mCurrentMonotoneValue);
                if (CameraSetting.getInstance().getCamera() != null) {
                    SmoothReflectionUtil.getInstance().setMonotoneSetting(monotoneValue);
                    SmoothReflectionUtil.getInstance().applyGammaTable();
                }
            } else {
                SmoothReflectionUtil.getInstance().clearGammmTable();
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String retValue;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (MONOTONE.equals(tag)) {
            this.mCurrentMonotoneValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, this.mCurrentMonotoneValue);
            retValue = this.mCurrentMonotoneValue;
        } else if (SMOOTHING.equals(tag)) {
            String currentTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, TWILIGHTREFLECTION);
            if (currentTheme.equals(CUSTOM)) {
                this.mCurrentSmoothingValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, "64");
            } else {
                this.mCurrentSmoothingValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SMOOTH_LEVEL, this.mCurrentSmoothingValue);
            }
            retValue = this.mCurrentSmoothingValue;
        } else {
            this.mCurrentValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, this.mCurrentValue);
            retValue = this.mCurrentValue;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return getSupportedValue(tag);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<String> retValue;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (MONOTONE.equals(tag)) {
            if (mSupportedMonotoneList == null) {
                mSupportedMonotoneList = new ArrayList();
                if (SaUtil.isAVIP()) {
                    String[] arr$ = SUPPORTED_MONOTONE_ARRAY_AVIP;
                    for (String value : arr$) {
                        mSupportedMonotoneList.add(value);
                    }
                } else {
                    String[] arr$2 = SUPPORTED_MONOTONE_ARRAY;
                    for (String value2 : arr$2) {
                        mSupportedMonotoneList.add(value2);
                    }
                }
            }
            retValue = mSupportedMonotoneList;
        } else if (SMOOTHING.equals(tag)) {
            String currentTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, TWILIGHTREFLECTION);
            if (currentTheme.equals(CUSTOM)) {
                mSupportedSmoothingList = null;
                if (mSupportedSmoothingList == null) {
                    mSupportedSmoothingList = new ArrayList();
                    String[] arr$3 = SUPPORTED_SHOTS_ARRAY;
                    for (String value3 : arr$3) {
                        mSupportedSmoothingList.add(value3);
                    }
                }
                retValue = mSupportedSmoothingList;
            } else {
                mSupportedSmoothingList = null;
                if (mSupportedSmoothingList == null) {
                    mSupportedSmoothingList = new ArrayList();
                    String[] arr$4 = SUPPORTED_SMOOTHING_ARRAY;
                    for (String value4 : arr$4) {
                        mSupportedSmoothingList.add(value4);
                    }
                }
                retValue = mSupportedSmoothingList;
            }
        } else {
            if (mSupportedList == null) {
                mSupportedList = new ArrayList();
                String[] arr$5 = SUPPORTED_ARRAY;
                for (String value5 : arr$5) {
                    mSupportedList.add(value5);
                }
            }
            retValue = mSupportedList;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retValue;
    }
}
