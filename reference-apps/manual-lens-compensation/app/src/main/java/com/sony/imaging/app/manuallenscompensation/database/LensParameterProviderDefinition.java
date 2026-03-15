package com.sony.imaging.app.manuallenscompensation.database;

import android.net.Uri;
import android.provider.BaseColumns;

/* loaded from: classes.dex */
public final class LensParameterProviderDefinition {
    public static final String AUTHORITY = "com.sony.imaging.app.manuallenscompensation.provider.LensParameterProvider";
    public static final String PARAMTERS_PACKAGE_NAME = "com.sony.imaging.app.lensparameter";
    public static final String targetActivity = "LensParameterActivity";
    public static final String targetMimeType = "application/vnd.sony.lensparam";

    /* loaded from: classes.dex */
    public static class LensColumns implements BaseColumns {
        public static final String APP_VER = "APP_VER";
        public static final String BLUE_CHROMATIC_ABERRATION = "BLUE_CHROMATIC_ABERRATION_CORRECTION";
        public static final String BLUE_COLOR_VIGNETTING = "BLUE_COLOR_VIGNETTING_CORRECTION";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.sony.imaging.app.lensparameter";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.sony.imaging.app.lensparameter";
        public static final Uri CONTENT_URI = Uri.parse("content://com.sony.imaging.app.manuallenscompensation.provider.LensParameterProvider/parames");
        public static final String CREATION_DATE = "CREATION_DATE";
        public static final String DB_VER = "DB_VER";
        public static final String DEFAULT_SORT_ORDER = "Number DESC";
        public static final String DISTORTION = "DISTORTION_CORRECTION";
        public static final String EV_STEP_VAL = "EV_STEP_VAL";
        public static final String EXPOSURE_STEP = "EXPOSURE_STEP";
        public static final String FILE_NAME = "FILE_NAME";
        public static final String FOCAL_LENGTH = "focalLength";
        public static final String F_LENGTH_SET_FLAG = "F_LENGTH_SET_FLAG";
        public static final String F_VALUE = "FValue";
        public static final String IS_VALID = "IS_VALID";
        public static final String LENS_NAME = "lens";
        public static final String LIGHT_VIGNETTING = "LIGHT_VIGNETTING_CORRECTION";
        public static final String MAX_F_NO = "MAX_F_NO";
        public static final String MAX_F_NO_SET_FLAG = "MAX_F_NO_SET_FLAG";
        public static final String MIN_F_NO_SET_FLAG = "MIN_F_NO_SET_FLAG";
        public static final String NEXT_VER_OFFSET = "NEXT_VER_OFFSET";
        public static final String NUMBER = "Number";
        public static final String OPTION_FLG = "OPTION_FLG";
        public static final String PARAMETER_COUNT = "PARAMETER_COUNT";
        public static final String PARAMETER_FIVE = "PARAMETER_FIVE";
        public static final String PARAMETER_FOUR = "PARAMETER_FOUR";
        public static final String PARAMETER_ONE = "PARAMETER_ONE";
        public static final String PARAMETER_SEVEN = "PARAMETER_SEVEN";
        public static final String PARAMETER_SIX = "PARAMETER_SIX";
        public static final String PARAMETER_THREE = "PARAMETER_THREE";
        public static final String PARAMETER_TWO = "PARAMETER_TWO";
        public static final String RED_CHROMATIC_ABERRATION = "RED_CHROMATIC_ABERRATION_CORRECTION";
        public static final String RED_COLOR_VIGNETTING = "RED_COLOR_VIGNETTING_CORRECTION";
    }

    private LensParameterProviderDefinition() {
    }
}
