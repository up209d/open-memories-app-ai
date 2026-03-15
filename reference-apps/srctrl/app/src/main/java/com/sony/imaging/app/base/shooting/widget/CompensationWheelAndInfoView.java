package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import java.lang.reflect.Array;
import java.util.Arrays;

/* loaded from: classes.dex */
public class CompensationWheelAndInfoView extends RelativeLayout {
    private static final int AREAFIG_COMP_VALUE_HEIGHT = 46;
    private static final int AREAFIG_COMP_VALUE_LEFT = 316;
    private static final int AREAFIG_COMP_VALUE_TOP = 130;
    private static final int AREAFIG_COMP_VALUE_WIDTH = 84;
    private static final int AREAFIG_GAUGE_GUIDE_HEIGHT = 76;
    private static final int AREAFIG_GAUGE_GUIDE_LEFT = 0;
    private static final int AREAFIG_GAUGE_GUIDE_TOP = 219;
    private static final int AREAFIG_GAUGE_GUIDE_WIDTH = 640;
    private static final int COMPBAR_2_SET_LEFT_MARGIN = 85;
    private static final int COMPBAR_2_SET_WIDTH_CENTER = 290;
    private static final int COMPBAR_2_SET_WIDTH_LEFT = 90;
    private static final int COMPBAR_2_SET_WIDTH_RIGHT = 90;
    private static final int COMPBAR_2_SINGLE_LEFT_MARGIN = 130;
    private static final int COMPBAR_2_SINGLE_LEFT_MARGIN_EVF = 157;
    private static final int COMPBAR_2_SINGLE_WIDTH = 380;
    private static final int COMPBAR_2_SINGLE_WIDTH_EVF = 326;
    private static final int COMPBAR_3_SET_LEFT_MARGIN = 85;
    private static final int COMPBAR_3_SET_WIDTH_CENTER = 290;
    private static final int COMPBAR_3_SET_WIDTH_LEFT = 90;
    private static final int COMPBAR_3_SET_WIDTH_RIGHT = 90;
    private static final int COMPBAR_3_SINGLE_LEFT_MARGIN = 130;
    private static final int COMPBAR_3_SINGLE_LEFT_MARGIN_EVF = 157;
    private static final int COMPBAR_3_SINGLE_WIDTH = 380;
    private static final int COMPBAR_3_SINGLE_WIDTH_EVF = 326;
    private static final int COMPBAR_5_SET_LEFT_MARGIN = 85;
    private static final int COMPBAR_5_SET_WIDTH_CENTER = 180;
    private static final int COMPBAR_5_SET_WIDTH_LEFT = 145;
    private static final int COMPBAR_5_SET_WIDTH_RIGHT = 145;
    private static final int COMPBAR_HEIGHT = 34;
    private static final int COMPBAR_SET_ARR_CENTER = 1;
    private static final int COMPBAR_SET_ARR_COUNT = 3;
    private static final int COMPBAR_SET_ARR_LEFT = 0;
    private static final int COMPBAR_SET_ARR_RIGHT = 2;
    private static final int COMPBAR_SET_HEIGHT = 34;
    private static final int COMPBAR_SET_TOP_MARGIN = 21;
    private static final int COMPBAR_SINGLE_HEIGHT = 36;
    private static final int COMPBAR_SINGLE_HEIGHT_EVF = 30;
    private static final int COMPBAR_SINGLE_TOP_MARGIN = 21;
    private static final int COMPBAR_TOP_MARGIN = 21;
    private static final int COMPIND_ARR_COUNT = 2;
    private static final int COMPIND_ARR_LOWER = 1;
    private static final int COMPIND_ARR_UPPER = 0;
    private static final int COMPIND_OFFSET_EV = 34;
    private static final int COMPIND_OFFSET_FLASH = 35;
    public static final int COMP_MENU_KIND_EV = 1;
    public static final int COMP_MENU_KIND_FLASH = 2;
    public static final int COMP_MENU_KIND_FLASH_EVF = 3;
    public static final int COMP_MENU_KIND_UNKNOWN = 0;
    private static final int CUSTOMVIEW_LEFT = 0;
    private static final int CUSTOMVIEW_TOP = 130;
    private static final int EV_COMP_2_MOVIE_AVAILABLE_MAX_STEP_3 = 12;
    private static final int EV_COMP_2_MOVIE_AVAILABLE_MIN_STEP_3 = 0;
    private static final int EV_COMP_3_MOVIE_AVAILABLE_MAX_STEP_3 = 15;
    private static final int EV_COMP_3_MOVIE_AVAILABLE_MIN_STEP_3 = 3;
    private static final int EV_COMP_5_MOVIE_AVAILABLE_MAX_STEP_3 = 21;
    private static final int EV_COMP_5_MOVIE_AVAILABLE_MIN_STEP_3 = 9;
    public static final int GAUGE_GUIDE_TYPE_2 = 3;
    public static final int GAUGE_GUIDE_TYPE_3 = 2;
    public static final int GAUGE_GUIDE_TYPE_5 = 1;
    private static final int GAUGE_GUIDE_TYPE_ARR_COUNT = 4;
    public static final int GAUGE_GUIDE_TYPE_NONE = 0;
    private static final int GAUGE_GUIDE_TYPE_VALUE_2 = 20;
    private static final int GAUGE_GUIDE_TYPE_VALUE_3 = 30;
    private static final int GAUGE_GUIDE_TYPE_VALUE_5 = 50;
    private static final String TAG = "CompensationWheelAndInfoView";
    private int[] mCompbarSetLeftMargin;
    private int[][] mCompbarSetMovieResIds;
    private int[][] mCompbarSetResIds;
    private int[][] mCompbarSetResWidth;
    private int[] mCompbarSingleLeftMargin;
    private int[] mCompbarSingleLeftMargin_evf;
    private int[][] mCompbarSingleResId;
    private int[][] mCompbarSingleResId_evf;
    private int[][] mCompbarSingleResWidth;
    private int[][] mCompbarSingleResWidth_evf;
    private CompensationWheelView mCompensationWheel;
    private int[] mCompindResIds;
    private TextView mInformationText;

    public CompensationWheelAndInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(AREAFIG_COMP_VALUE_WIDTH, 46);
        param.leftMargin = AREAFIG_COMP_VALUE_LEFT;
        param.topMargin = 0;
        this.mInformationText = new TextView(context, attrs, R.attr.RESID_FONTSIZE_BIG_L_EDGE_ON);
        this.mInformationText.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_FOCUSED));
        this.mInformationText.setGravity(17);
        addView(this.mInformationText, param);
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(640, AREAFIG_GAUGE_GUIDE_HEIGHT);
        param2.leftMargin = 0;
        param2.topMargin = 89;
        this.mCompensationWheel = new CompensationWheelView(context, attrs);
        addView(this.mCompensationWheel, param2);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CompensationWheelAndInfo);
        this.mCompbarSetResIds = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSetResIds[0], -1);
        this.mCompbarSetResIds[1][0] = array.getResourceId(0, -1);
        this.mCompbarSetResIds[1][1] = array.getResourceId(2, -1);
        this.mCompbarSetResIds[1][2] = array.getResourceId(3, -1);
        this.mCompbarSetResIds[2][0] = array.getResourceId(5, -1);
        this.mCompbarSetResIds[2][1] = array.getResourceId(7, -1);
        this.mCompbarSetResIds[2][2] = array.getResourceId(8, -1);
        this.mCompbarSetResIds[3][0] = array.getResourceId(10, -1);
        this.mCompbarSetResIds[3][1] = array.getResourceId(11, -1);
        this.mCompbarSetResIds[3][2] = array.getResourceId(12, -1);
        this.mCompbarSetMovieResIds = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSetMovieResIds[0], -1);
        this.mCompbarSetMovieResIds[1][0] = array.getResourceId(1, -1);
        this.mCompbarSetMovieResIds[1][1] = array.getResourceId(2, -1);
        this.mCompbarSetMovieResIds[1][2] = array.getResourceId(4, -1);
        this.mCompbarSetMovieResIds[2][0] = array.getResourceId(6, -1);
        this.mCompbarSetMovieResIds[2][1] = array.getResourceId(7, -1);
        this.mCompbarSetMovieResIds[2][2] = array.getResourceId(9, -1);
        this.mCompbarSetMovieResIds[3][0] = array.getResourceId(10, -1);
        this.mCompbarSetMovieResIds[3][1] = array.getResourceId(11, -1);
        this.mCompbarSetMovieResIds[3][2] = array.getResourceId(12, -1);
        this.mCompbarSetResWidth = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSetResWidth[0], -1);
        this.mCompbarSetResWidth[1][0] = 145;
        this.mCompbarSetResWidth[1][1] = COMPBAR_5_SET_WIDTH_CENTER;
        this.mCompbarSetResWidth[1][2] = 145;
        this.mCompbarSetResWidth[2][0] = 90;
        this.mCompbarSetResWidth[2][1] = 290;
        this.mCompbarSetResWidth[2][2] = 90;
        this.mCompbarSetResWidth[3][0] = 90;
        this.mCompbarSetResWidth[3][1] = 290;
        this.mCompbarSetResWidth[3][2] = 90;
        this.mCompbarSetLeftMargin = new int[4];
        Arrays.fill(this.mCompbarSetLeftMargin, -1);
        this.mCompbarSetLeftMargin[1] = 85;
        this.mCompbarSetLeftMargin[2] = 85;
        this.mCompbarSetLeftMargin[3] = 85;
        this.mCompbarSingleResId = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSingleResId[0], -1);
        Arrays.fill(this.mCompbarSingleResId[1], -1);
        this.mCompbarSingleResId[2][0] = array.getResourceId(13, -1);
        this.mCompbarSingleResId[2][1] = -1;
        this.mCompbarSingleResId[2][2] = -1;
        this.mCompbarSingleResId[3][0] = array.getResourceId(14, -1);
        this.mCompbarSingleResId[3][1] = -1;
        this.mCompbarSingleResId[3][2] = -1;
        this.mCompbarSingleResWidth = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSingleResWidth[0], -1);
        Arrays.fill(this.mCompbarSingleResWidth[1], -1);
        this.mCompbarSingleResWidth[2][0] = 380;
        this.mCompbarSingleResWidth[2][1] = 0;
        this.mCompbarSingleResWidth[2][2] = 0;
        this.mCompbarSingleResWidth[3][0] = 380;
        this.mCompbarSingleResWidth[3][1] = 0;
        this.mCompbarSingleResWidth[3][2] = 0;
        this.mCompbarSingleLeftMargin = new int[4];
        Arrays.fill(this.mCompbarSingleLeftMargin, -1);
        this.mCompbarSingleLeftMargin[2] = 130;
        this.mCompbarSingleLeftMargin[3] = 130;
        this.mCompbarSingleResId_evf = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSingleResId_evf[0], -1);
        Arrays.fill(this.mCompbarSingleResId_evf[1], -1);
        this.mCompbarSingleResId_evf[2][0] = array.getResourceId(15, -1);
        this.mCompbarSingleResId_evf[2][1] = -1;
        this.mCompbarSingleResId_evf[2][2] = -1;
        this.mCompbarSingleResId_evf[3][0] = array.getResourceId(16, -1);
        this.mCompbarSingleResId_evf[3][1] = -1;
        this.mCompbarSingleResId_evf[3][2] = -1;
        this.mCompbarSingleResWidth_evf = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 4, 3);
        Arrays.fill(this.mCompbarSingleResWidth_evf[0], -1);
        Arrays.fill(this.mCompbarSingleResWidth_evf[1], -1);
        this.mCompbarSingleResWidth_evf[2][0] = 326;
        this.mCompbarSingleResWidth_evf[2][1] = 0;
        this.mCompbarSingleResWidth_evf[2][2] = 0;
        this.mCompbarSingleResWidth_evf[3][0] = 326;
        this.mCompbarSingleResWidth_evf[3][1] = 0;
        this.mCompbarSingleResWidth_evf[3][2] = 0;
        this.mCompbarSingleLeftMargin_evf = new int[4];
        Arrays.fill(this.mCompbarSingleLeftMargin_evf, -1);
        this.mCompbarSingleLeftMargin_evf[2] = 157;
        this.mCompbarSingleLeftMargin_evf[3] = 157;
        this.mCompindResIds = new int[2];
        this.mCompindResIds[0] = array.getResourceId(17, -1);
        this.mCompindResIds[1] = array.getResourceId(18, -1);
    }

    public void initialize(int menuKind, boolean movieMode, int gaugeGuideType, int range) {
        if (menuKind == 1) {
            int[] compbarResId = this.mCompbarSetResIds[gaugeGuideType];
            int availableMax = range - 1;
            int availableMin = 0;
            if (movieMode) {
                compbarResId = this.mCompbarSetMovieResIds[gaugeGuideType];
                if (gaugeGuideType == 1) {
                    availableMax = 21;
                    availableMin = 9;
                } else if (gaugeGuideType == 2) {
                    availableMax = 15;
                    availableMin = 3;
                } else if (gaugeGuideType == 3) {
                    availableMax = 12;
                    availableMin = 0;
                }
            }
            Pair<Integer, Integer> availableRange = new Pair<>(Integer.valueOf(availableMax), Integer.valueOf(availableMin));
            this.mCompensationWheel.setResources(range, availableRange, this.mCompindResIds, 34, compbarResId, this.mCompbarSetResWidth[gaugeGuideType], 34, this.mCompbarSetLeftMargin[gaugeGuideType], 21, true);
        } else if (menuKind == 2) {
            this.mCompensationWheel.setResources(range, this.mCompindResIds, 35, this.mCompbarSingleResId[gaugeGuideType], this.mCompbarSingleResWidth[gaugeGuideType], 36, this.mCompbarSingleLeftMargin[gaugeGuideType], 21, true);
        } else if (menuKind == 3) {
            this.mCompensationWheel.setResources(range, this.mCompindResIds, 35, this.mCompbarSingleResId_evf[gaugeGuideType], this.mCompbarSingleResWidth_evf[gaugeGuideType], 30, this.mCompbarSingleLeftMargin_evf[gaugeGuideType], 21, false);
        }
        Log.i(TAG, "initialize menukind = " + menuKind);
    }

    public CompensationWheelView getCompensationWheelView() {
        return this.mCompensationWheel;
    }

    public void setInformationText(String text) {
        this.mInformationText.setText(text);
    }

    public static int getGaugeGuideType(float range) {
        int rangeValue = (int) (Math.abs(range) * 10.0f);
        switch (rangeValue) {
            case 20:
                return 3;
            case 30:
                return 2;
            case 50:
                return 1;
            default:
                return 0;
        }
    }
}
