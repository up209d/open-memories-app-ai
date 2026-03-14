package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.widget.ActiveLayout;
import com.sony.imaging.app.base.shooting.widget.NumberImage;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFWhiteBalanceFnIcon extends ActiveLayout {
    private static final int ICON_2COLUMN_AB_X = 8;
    private static final int ICON_2COLUMN_AB_Y = 38;
    private static final int ICON_2COLUMN_GM_X = 37;
    private static final int ICON_2COLUMN_GM_Y = 38;
    private static final int ICON_2COLUMN_X = 0;
    private static final int ICON_2COLUMN_Y = 2;
    private static final int ICON_K_AB_LOWER_X = 5;
    private static final int ICON_K_AB_LOWER_Y = 22;
    private static final int ICON_K_AB_UPPER_X = 17;
    private static final int ICON_K_AB_UPPER_Y = 0;
    private static final int ICON_K_AB_X = 8;
    private static final int ICON_K_AB_Y = 41;
    private static final int ICON_K_GM_X = 37;
    private static final int ICON_K_GM_Y = 41;
    private static final int ICON_K_LOWER_X = 5;
    private static final int ICON_K_LOWER_Y = 28;
    private static final int ICON_K_UPPER_X = 0;
    private static final int ICON_K_UPPER_Y = 2;
    private static final String LOG_INVALID = "Invalid value of WBModeSettings";
    private static final String TAG = "GFWhiteBalanceFnIcon";
    RelativeLayout mAbLayout;
    RelativeLayout.LayoutParams mAbLayoutParam;
    private Context mContext;
    RelativeLayout mDefalutIconLayout;
    RelativeLayout.LayoutParams mDefalutIconLayoutParam;
    protected final DisplayModeObserver mDisplayObserver;
    private boolean mFnMode;
    RelativeLayout mGmLayout;
    RelativeLayout.LayoutParams mGmLayoutParam;
    RelativeLayout mKLayout;
    RelativeLayout.LayoutParams mKLayoutParam;
    private NumberImageForAB_GM mLayoutAB;
    private NumberImageForAB_GM mLayoutGM;
    private NumberImage mLayoutk;
    private ActiveLayout.ActiveLayoutListener mListener;
    private int mResourceId;
    private ImageView mTempImageView;
    RelativeLayout mUpperIconLayout;
    RelativeLayout.LayoutParams mUpperIconLayoutParam;
    private WhiteBalanceController mWbController;
    private ImageView mWhiteBalanceImageView;

    public GFWhiteBalanceFnIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.mFnMode = false;
        this.mContext = context;
        this.mResourceId = 17306584;
        this.mWbController = WhiteBalanceController.getInstance();
        this.mDefalutIconLayoutParam = makeLayoutParam();
        this.mDefalutIconLayoutParam.leftMargin = 0;
        this.mDefalutIconLayoutParam.topMargin = 2;
        this.mUpperIconLayoutParam = makeLayoutParam();
        this.mUpperIconLayoutParam.leftMargin = 0;
        this.mUpperIconLayoutParam.topMargin = 2;
        this.mKLayoutParam = makeLayoutParam();
        this.mKLayoutParam.leftMargin = 5;
        this.mKLayoutParam.topMargin = 28;
        this.mAbLayoutParam = makeLayoutParam();
        this.mGmLayoutParam = makeLayoutParam();
        this.mDefalutIconLayout = new RelativeLayout(this.mContext);
        this.mUpperIconLayout = new RelativeLayout(this.mContext);
        this.mKLayout = new RelativeLayout(this.mContext);
        this.mAbLayout = new RelativeLayout(this.mContext);
        this.mGmLayout = new RelativeLayout(this.mContext);
        this.mWhiteBalanceImageView = new ImageView(this.mContext);
        this.mTempImageView = new ImageView(this.mContext);
        this.mLayoutk = new NumberImage(this.mContext);
        this.mLayoutAB = new NumberImageForAB_GM(this.mContext);
        this.mLayoutGM = new NumberImageForAB_GM(this.mContext);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveLayout.ActiveLayoutListener() { // from class: com.sony.imaging.app.digitalfilter.shooting.widget.GFWhiteBalanceFnIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    if (GFWhiteBalanceFnIcon.this.mFnMode) {
                        GFWhiteBalanceFnIcon.this.refresh();
                    } else {
                        GFWhiteBalanceFnIcon.this.setVisibility(4);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener
                public String[] addTags() {
                    return new String[]{CameraNotificationManager.WB_MODE_CHANGE, CameraNotificationManager.WB_DETAIL_CHANGE};
                }
            };
        }
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public boolean isVisible() {
        boolean visible = !this.mWbController.isUnavailableSceneFactor(WhiteBalanceController.WHITEBALANCE);
        return visible && this.mFnMode;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public void setFnMode(boolean fnMode) {
        this.mFnMode = fnMode;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        removeAllViewsInLayout();
        this.mDefalutIconLayout.removeAllViews();
        this.mUpperIconLayout.removeAllViews();
        this.mKLayout.removeAllViews();
        this.mAbLayout.removeAllViews();
        this.mGmLayout.removeAllViews();
        String s = this.mWbController.getValue();
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValueFromPF();
        int layer = GFCommonUtil.getInstance().getSettingLayer();
        String option = GFEffectParameters.getInstance().getParameters().getWBOption(layer);
        String[] optArray = option.split("/");
        int light = Integer.parseInt(optArray[0]);
        int comp = Integer.parseInt(optArray[1]);
        int temp = Integer.parseInt(optArray[2]);
        param.setLightBalance(light);
        param.setColorComp(comp);
        if (s.equals(WhiteBalanceController.COLOR_TEMP) || s.equals(WhiteBalanceController.CUSTOM) || s.equals("custom1") || s.equals("custom2") || s.equals("custom3")) {
            refresh_wbvalue_k(s, param.getColorTemp());
            return;
        }
        param.setColorTemp(temp);
        refresh_wb_param();
        refresh_wb(s);
    }

    private void refresh_wb_param() {
        this.mAbLayoutParam.leftMargin = 8;
        this.mAbLayoutParam.topMargin = 38;
        this.mGmLayoutParam.leftMargin = 37;
        this.mGmLayoutParam.topMargin = 38;
    }

    private int getIconResourceId(String s) {
        if (s.equals("auto")) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_001;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_002;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_003;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_004;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_005;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_006;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_007;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_008;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_009;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_010;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_011;
        }
        if (s.equals(WhiteBalanceController.COLOR_TEMP)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_012;
        }
        if (s.equals(WhiteBalanceController.CUSTOM)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_013;
        }
        if (s.equals("custom1")) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_014;
        }
        if (s.equals("custom2")) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_015;
        }
        if (s.equals("custom3")) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_016;
        }
        return R.drawable.p_16_dd_parts_skyhdr_fn_wb_001;
    }

    private int getNoAbgmIconResourceId(String s) {
        if (s.equals("auto")) {
            return 17306536;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17306574;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17306855;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return 17306708;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17306382;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17306675;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17306787;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17306564;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17306606;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return 17306540;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307573;
        }
        if (s.equals(WhiteBalanceController.COLOR_TEMP)) {
            return R.drawable.p_16_dd_parts_skyhdr_fn_wb_colortemp_wo_abgm;
        }
        if (s.equals(WhiteBalanceController.CUSTOM)) {
            return 17306691;
        }
        if (s.equals("custom1")) {
            return 17306449;
        }
        if (s.equals("custom2")) {
            return 17306483;
        }
        if (s.equals("custom3")) {
            return 17306523;
        }
        return 17306536;
    }

    private void refresh_wb(String s) {
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValueFromPF();
        int ab = param.getLightBalance();
        int gm = param.getColorComp();
        this.mWhiteBalanceImageView.setVisibility(0);
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            this.mWhiteBalanceImageView.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            this.mWhiteBalanceImageView.setVisibility(4);
        } else if (ab != 0 || gm != 0) {
            this.mResourceId = getIconResourceId(s);
        } else {
            this.mResourceId = getNoAbgmIconResourceId(s);
        }
        this.mWhiteBalanceImageView.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mDefalutIconLayout.addView(this.mWhiteBalanceImageView, this.mDefalutIconLayoutParam);
        addView(this.mDefalutIconLayout, ilp);
        Resources res = getResources();
        if (ab != 0 || gm != 0) {
            makeLightBalance(ab, res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG));
            makeColorComp(gm, res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG));
            RelativeLayout.LayoutParams ablp = makeLayoutParam();
            this.mAbLayout.addView(this.mLayoutAB, this.mAbLayoutParam);
            addView(this.mAbLayout, ablp);
            RelativeLayout.LayoutParams gmlp = makeLayoutParam();
            this.mGmLayout.addView(this.mLayoutGM, this.mGmLayoutParam);
            addView(this.mGmLayout, gmlp);
        }
    }

    private void refresh_wbvalue_k(String s, int k) {
        TypedArray imageArrayK;
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValueFromPF();
        int ab = param.getLightBalance();
        int gm = param.getColorComp();
        this.mTempImageView.setVisibility(0);
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            this.mTempImageView.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            this.mTempImageView.setVisibility(4);
        } else if (ab != 0 || gm != 0) {
            this.mResourceId = getIconResourceId(s);
            this.mUpperIconLayoutParam.leftMargin = 17;
            this.mUpperIconLayoutParam.topMargin = 0;
            this.mKLayoutParam.leftMargin = 5;
            this.mKLayoutParam.topMargin = 22;
            this.mAbLayoutParam.leftMargin = 8;
            this.mAbLayoutParam.topMargin = 41;
            this.mGmLayoutParam.leftMargin = 37;
            this.mGmLayoutParam.topMargin = 41;
        } else {
            this.mResourceId = getNoAbgmIconResourceId(s);
            this.mUpperIconLayoutParam.leftMargin = 0;
            this.mUpperIconLayoutParam.topMargin = 2;
            this.mKLayoutParam.leftMargin = 5;
            this.mKLayoutParam.topMargin = 28;
            this.mAbLayoutParam.leftMargin = 8;
            this.mAbLayoutParam.topMargin = 38;
            this.mGmLayoutParam.leftMargin = 37;
            this.mGmLayoutParam.topMargin = 38;
        }
        this.mTempImageView.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mUpperIconLayout.addView(this.mTempImageView, this.mUpperIconLayoutParam);
        addView(this.mUpperIconLayout, ilp);
        Resources res = getResources();
        if (ab != 0 || gm != 0) {
            imageArrayK = res.obtainTypedArray(R.array.OSD_SKY_SETTING_WBNUMBER_K);
        } else {
            imageArrayK = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_K);
        }
        String dispColorTemp = "" + k + "k";
        this.mLayoutk.makeImageFromString(dispColorTemp, imageArrayK);
        RelativeLayout.LayoutParams klp = makeLayoutParam();
        this.mKLayout.addView(this.mLayoutk, this.mKLayoutParam);
        addView(this.mKLayout, klp);
        if (ab != 0 || gm != 0) {
            makeLightBalance(ab, res.obtainTypedArray(R.array.OSD_SKY_SETTING_WBNUMBER_AG));
            makeColorComp(gm, res.obtainTypedArray(R.array.OSD_SKY_SETTING_WBNUMBER_AG));
            RelativeLayout.LayoutParams ablp = makeLayoutParam();
            this.mAbLayout.addView(this.mLayoutAB, this.mAbLayoutParam);
            addView(this.mAbLayout, ablp);
            RelativeLayout.LayoutParams gmlp = makeLayoutParam();
            this.mGmLayout.addView(this.mLayoutGM, this.mGmLayoutParam);
            addView(this.mGmLayout, gmlp);
        }
    }

    private void makeLightBalance(int value, TypedArray imageArray) {
        String dispLightBalance = "";
        if (value > 0) {
            dispLightBalance = "a" + Math.abs(value);
        } else if (value < 0) {
            dispLightBalance = "b" + Math.abs(value);
        }
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (2 == aspect) {
            this.mLayoutAB.makeImageFromStringLapover(dispLightBalance, imageArray, -2, 0);
        } else {
            this.mLayoutAB.makeImageFromString(dispLightBalance, imageArray);
        }
    }

    private void makeColorComp(int value, TypedArray imageArray) {
        String dispColorComp = "";
        if (value > 0) {
            dispColorComp = "m" + Math.abs(value);
        } else if (value < 0) {
            dispColorComp = "g" + Math.abs(value);
        }
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (2 == aspect) {
            this.mLayoutGM.makeImageFromStringLapover(dispColorComp, imageArray, -2, 0);
        } else {
            this.mLayoutGM.makeImageFromString(dispColorComp, imageArray);
        }
    }

    private RelativeLayout.LayoutParams makeLayoutParam() {
        RelativeLayout.LayoutParams rlpm = new RelativeLayout.LayoutParams(-2, -2);
        return rlpm;
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (1073741824 == View.MeasureSpec.getMode(widthMeasureSpec)) {
            width = params.width;
        }
        if (1073741824 == View.MeasureSpec.getMode(heightMeasureSpec)) {
            height = params.height;
        }
        setMeasuredDimension(width, height);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public void setAlpha(int alpha) {
        if (this.mWhiteBalanceImageView != null) {
            this.mWhiteBalanceImageView.setAlpha(alpha);
        }
        if (this.mTempImageView != null) {
            this.mTempImageView.setAlpha(alpha);
        }
        if (this.mLayoutk != null) {
            this.mLayoutk.setAlpha(alpha);
        }
        if (this.mLayoutAB != null) {
            this.mLayoutAB.setAlpha(alpha);
        }
        if (this.mLayoutGM != null) {
            this.mLayoutGM.setAlpha(alpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NumberImageForAB_GM extends NumberImage {
        private static final int VALUE_A = 8;
        private static final int VALUE_B = 10;
        private static final int VALUE_G = 9;
        private static final int VALUE_M = 11;

        public NumberImageForAB_GM(Context context) {
            super(context);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.NumberImage
        public int setNumber(char ch) {
            if (ch == 'b') {
                return 10;
            }
            if (ch == 'g') {
                return 9;
            }
            if (ch == 'm') {
                return 11;
            }
            if (ch == 'a') {
                return 8;
            }
            Log.e(GFWhiteBalanceFnIcon.TAG, GFWhiteBalanceFnIcon.LOG_INVALID);
            return 0;
        }
    }
}
