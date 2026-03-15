package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.widget.ActiveLayout;
import com.sony.imaging.app.util.NotificationListener;
import java.util.List;

/* loaded from: classes.dex */
public class WhiteBalanceIcon extends ActiveLayout {
    private static final int EVF_CWB_H = 22;
    private static final int EVF_CWB_W = 40;
    private static final int EVF_CWB_Y = 13;
    private static final int EVF_ICON_ONLY_LEFT_X = 40;
    private static final int EVF_ICON_ONLY_LEFT_Y = 0;
    private static final int EVF_LOWER_LEFT_X = 42;
    private static final int EVF_LOWER_LEFT_Y = 28;
    private static final int EVF_LOWER_RIGHT_X = 70;
    private static final int EVF_LOWER_RIGHT_Y = 28;
    private static final int EVF_UPPER_K_X = 41;
    private static final int EVF_UPPER_K_Y = 6;
    private static final String LOG_INVALID = "Invalid value of WBModeSettings";
    private static final int OSD169_ABGB_PADDING_LEFT = -2;
    private static final int OSD169_ABGB_PADDING_TOP = 0;
    private static final int OSD169_ICON_ONLY_LEFT_X = 28;
    private static final int OSD169_K_PADDING_LEFT = -3;
    private static final int OSD169_K_PADDING_TOP = 0;
    private static final int OSD169_LOWER_LEFT_X = 41;
    private static final int OSD169_LOWER_RIGHT_X = 63;
    private static final int OSD169_UPPER_K_X = 39;
    private static final int OSD43_ICON_ONLY_LEFT_X = 38;
    private static final int OSD43_LOWER_LEFT_X = 46;
    private static final int OSD43_LOWER_RIGHT_X = 75;
    private static final int OSD43_UPPER_K_X = 43;
    private static final int OSD_CWB_H = 22;
    private static final int OSD_CWB_W = 38;
    private static final int OSD_CWB_Y = 23;
    private static final int OSD_ICON_ONLY_LEFT_Y = 0;
    private static final int OSD_LOWER_LEFT_Y = 36;
    private static final int OSD_LOWER_RIGHT_Y = 36;
    private static final int OSD_UPPER_K_Y = 9;
    private static final String TAG = "WhiteBalanceIcon";
    private boolean isError;
    RelativeLayout.LayoutParams mAbLayoutParam;
    RelativeLayout mCWBIconLayout;
    RelativeLayout.LayoutParams mCWBIconLayoutParam;
    private Context mContext;
    RelativeLayout.LayoutParams mDefalutIconLayoutParam;
    RelativeLayout mDefalutLayout;
    protected final DisplayModeObserver mDisplayObserver;
    RelativeLayout.LayoutParams mGmLayoutParam;
    private ActiveLayout.ActiveLayoutListener mListener;
    RelativeLayout mLowerLayout;
    private int mResourceId;
    RelativeLayout.LayoutParams mUpperIconLayoutParam;
    RelativeLayout.LayoutParams mUpperKLayoutParam;
    RelativeLayout mUpperLayout;
    private WhiteBalanceController mWbController;
    private int numOfCWB;

    public WhiteBalanceIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.isError = false;
        this.numOfCWB = 0;
        this.mContext = context;
        this.mResourceId = 17306584;
        this.mWbController = WhiteBalanceController.getInstance();
        List<String> supportCWBlist = this.mWbController.getSupportedCustomWhiteBalance();
        this.numOfCWB = supportCWBlist.size();
        this.mDefalutIconLayoutParam = makeLayoutParam();
        int device = this.mDisplayObserver.getActiveDevice();
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (device != 1) {
            if (2 == aspect) {
                this.mDefalutIconLayoutParam.leftMargin = 28;
            } else {
                this.mDefalutIconLayoutParam.leftMargin = 38;
            }
        } else {
            this.mDefalutIconLayoutParam.leftMargin = 40;
        }
        this.mDefalutIconLayoutParam.topMargin = 0;
        this.mUpperIconLayoutParam = makeLayoutParam();
        this.mAbLayoutParam = makeLayoutParam();
        this.mGmLayoutParam = makeLayoutParam();
        this.mUpperKLayoutParam = makeLayoutParam();
        this.mCWBIconLayoutParam = makeLayoutParam();
        this.mDefalutLayout = new RelativeLayout(this.mContext);
        this.mUpperLayout = new RelativeLayout(this.mContext);
        this.mLowerLayout = new RelativeLayout(this.mContext);
        this.mCWBIconLayout = new RelativeLayout(this.mContext);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveLayout.ActiveLayoutListener() { // from class: com.sony.imaging.app.base.shooting.widget.WhiteBalanceIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    WhiteBalanceIcon.this.refresh();
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
        return !this.mWbController.isUnavailableSceneFactor(WhiteBalanceController.WHITEBALANCE);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        removeAllViewsInLayout();
        this.mDefalutLayout.removeAllViews();
        this.mLowerLayout.removeAllViews();
        this.mUpperLayout.removeAllViews();
        this.mCWBIconLayout.removeAllViews();
        refreshParam();
        String s = this.mWbController.getValue();
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) this.mWbController.getDetailValue();
        if (s.equals(WhiteBalanceController.COLOR_TEMP)) {
            refresh_wbvalue_k(param.getColorTemp(), param.getLightBalance(), param.getColorComp());
            return;
        }
        if (s.equals(WhiteBalanceController.CUSTOM) || s.equals("custom1") || s.equals("custom2") || s.equals("custom3")) {
            refresh_cwbvalue(param.getColorTemp(), param.getLightBalance(), param.getColorComp());
        } else if (hasDetailValue(param)) {
            refresh_wbvalue_ab_gm(s, param.getLightBalance(), param.getColorComp());
        } else {
            refresh_wb(s);
        }
    }

    private void refreshParam() {
        int device = this.mDisplayObserver.getActiveDevice();
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (device != 1) {
            if (2 == aspect) {
                this.mUpperIconLayoutParam.leftMargin = 28;
                this.mAbLayoutParam.leftMargin = 41;
                this.mGmLayoutParam.leftMargin = OSD169_LOWER_RIGHT_X;
                this.mUpperKLayoutParam.leftMargin = 39;
            } else {
                this.mUpperIconLayoutParam.leftMargin = 38;
                this.mAbLayoutParam.leftMargin = OSD43_LOWER_LEFT_X;
                this.mGmLayoutParam.leftMargin = OSD43_LOWER_RIGHT_X;
                this.mUpperKLayoutParam.leftMargin = 43;
            }
            this.mUpperIconLayoutParam.topMargin = 0;
            this.mAbLayoutParam.topMargin = 36;
            this.mGmLayoutParam.topMargin = 36;
            this.mUpperKLayoutParam.topMargin = 9;
            this.mCWBIconLayoutParam.leftMargin = 0;
            this.mCWBIconLayoutParam.topMargin = 23;
            this.mCWBIconLayoutParam.width = 38;
            this.mCWBIconLayoutParam.height = 22;
            return;
        }
        this.mUpperIconLayoutParam.leftMargin = 40;
        this.mUpperIconLayoutParam.topMargin = 0;
        this.mAbLayoutParam.leftMargin = 42;
        this.mAbLayoutParam.topMargin = 28;
        this.mGmLayoutParam.leftMargin = EVF_LOWER_RIGHT_X;
        this.mGmLayoutParam.topMargin = 28;
        this.mUpperKLayoutParam.leftMargin = 41;
        this.mUpperKLayoutParam.topMargin = 6;
        this.mCWBIconLayoutParam.leftMargin = 0;
        this.mCWBIconLayoutParam.topMargin = 13;
        this.mCWBIconLayoutParam.width = 40;
        this.mCWBIconLayoutParam.height = 22;
    }

    private boolean hasDetailValue(WhiteBalanceController.WhiteBalanceParam param) {
        return (param.getLightBalance() == 0 && param.getColorComp() == 0 && param.getColorTemp() == 5500) ? false : true;
    }

    private void refresh_wb(String s) {
        ImageView iv = new ImageView(this.mContext);
        iv.setVisibility(0);
        int device = this.mDisplayObserver.getActiveDevice();
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            iv.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            iv.setVisibility(4);
        } else if (device != 1) {
            this.mResourceId = getIconResourceId_osd_b(s);
        } else if (device == 1) {
            this.mResourceId = getIconResourceId_evf_b(s);
        }
        iv.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mDefalutLayout.addView(iv, this.mDefalutIconLayoutParam);
        addView(this.mDefalutLayout, ilp);
    }

    private void refresh_wbvalue_ab_gm(String s, int ab, int gm) {
        TypedArray imageArrayAG;
        ImageView iv = new ImageView(this.mContext);
        iv.setVisibility(0);
        int device = this.mDisplayObserver.getActiveDevice();
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            iv.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            iv.setVisibility(4);
        } else if (device != 1) {
            this.mResourceId = getIconResourceId_osd_s(s);
        } else if (device == 1) {
            this.mResourceId = getIconResourceId_evf_s(s);
        }
        iv.setImageResource(this.mResourceId);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mUpperLayout.addView(iv, this.mUpperIconLayoutParam);
        Resources res = getResources();
        if (device != 1) {
            if (this.isError && device != 1) {
                imageArrayAG = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG_ORANGE);
            } else {
                imageArrayAG = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG);
            }
        } else if (this.isError) {
            imageArrayAG = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_AG_ORANGE);
        } else {
            imageArrayAG = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_AG);
        }
        NumberImageForAB_GM layoutAB = makeLightBalance(ab, imageArrayAG);
        NumberImageForAB_GM layoutGM = makeColorComp(gm, imageArrayAG);
        RelativeLayout.LayoutParams lp = makeLayoutParam();
        this.mLowerLayout.addView(layoutAB, this.mAbLayoutParam);
        this.mLowerLayout.addView(layoutGM, this.mGmLayoutParam);
        addView(this.mUpperLayout, ilp);
        addView(this.mLowerLayout, lp);
    }

    private int getIconResourceId_osd_b(String s) {
        if (s.equals("auto")) {
            return 17304988;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17304989;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17304992;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return 17304993;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17304994;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17304996;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17304998;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17304999;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17305002;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return android.R.drawable.list_section_divider_material;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307573;
        }
        return 17304988;
    }

    private int getIconResourceId_osd_s(String s) {
        if (s.equals("auto")) {
            return 17306584;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17306608;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17306681;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return 17306746;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17306805;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17306989;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17307130;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17307420;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17307455;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return 17307080;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307570;
        }
        return 17306584;
    }

    private int getIconResourceId_evf_b(String s) {
        if (s.equals("auto")) {
            return 17306745;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17306370;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17307439;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return 17306714;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17307429;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17306782;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17306518;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17306356;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17306511;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return 17306976;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307571;
        }
        return 17306745;
    }

    private int getIconResourceId_evf_s(String s) {
        if (s.equals("auto")) {
            return 17306622;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17306563;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17306403;
        }
        if (s.equals(WhiteBalanceController.CLOUDY)) {
            return 17306541;
        }
        if (s.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17307408;
        }
        if (s.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17306431;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17306581;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17307082;
        }
        if (s.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17306452;
        }
        if (s.equals(WhiteBalanceController.FLASH)) {
            return 17306391;
        }
        if (s.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307572;
        }
        return 17306622;
    }

    private void refresh_wbvalue_k(int k, int ab, int gm) {
        TypedArray imageArrayK;
        TypedArray imageArrayAG;
        Resources res = getResources();
        int device = this.mDisplayObserver.getActiveDevice();
        if (device != 1) {
            imageArrayK = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_K);
            imageArrayAG = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG);
        } else {
            imageArrayK = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_K);
            imageArrayAG = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_AG);
        }
        NumberImage layoutk = makeColorTemp(k, imageArrayK);
        RelativeLayout.LayoutParams klp = makeLayoutParam();
        this.mUpperLayout.addView(layoutk, this.mUpperKLayoutParam);
        NumberImageForAB_GM layoutAB = makeLightBalance(ab, imageArrayAG);
        NumberImageForAB_GM layoutGM = makeColorComp(gm, imageArrayAG);
        RelativeLayout.LayoutParams lp = makeLayoutParam();
        this.mLowerLayout.addView(layoutAB, this.mAbLayoutParam);
        this.mLowerLayout.addView(layoutGM, this.mGmLayoutParam);
        addView(this.mUpperLayout, klp);
        addView(this.mLowerLayout, lp);
    }

    private void refresh_cwbvalue(int k, int ab, int gm) {
        TypedArray imageArrayK;
        TypedArray imageArrayAG;
        Resources res = getResources();
        int device = this.mDisplayObserver.getActiveDevice();
        if (device != 1) {
            if (this.isError) {
                imageArrayK = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_K_ORANGE);
                imageArrayAG = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG_ORANGE);
                if (1 < this.numOfCWB) {
                    String mode = this.mWbController.getValue();
                    if ("custom1".equals(mode)) {
                        this.mResourceId = 17304891;
                    } else if ("custom2".equals(mode)) {
                        this.mResourceId = android.R.drawable.expander_open_mtrl_alpha;
                    } else if ("custom3".equals(mode)) {
                        this.mResourceId = android.R.drawable.sim_dark_orange;
                    } else {
                        this.mResourceId = 17305792;
                    }
                } else {
                    this.mResourceId = 17305792;
                }
            } else {
                imageArrayK = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_K);
                imageArrayAG = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_AG);
                if (1 < this.numOfCWB) {
                    String mode2 = this.mWbController.getValue();
                    if ("custom1".equals(mode2)) {
                        this.mResourceId = 17306728;
                    } else if ("custom2".equals(mode2)) {
                        this.mResourceId = 17306800;
                    } else if ("custom3".equals(mode2)) {
                        this.mResourceId = 17306986;
                    } else {
                        this.mResourceId = 17306640;
                    }
                } else {
                    this.mResourceId = 17306640;
                }
            }
        } else if (this.isError) {
            imageArrayK = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_K_ORANGE);
            imageArrayAG = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_AG_ORANGE);
            if (1 < this.numOfCWB) {
                String mode3 = this.mWbController.getValue();
                if ("custom1".equals(mode3)) {
                    this.mResourceId = android.R.drawable.btn_switch_to_off_mtrl_00010;
                } else if ("custom2".equals(mode3)) {
                    this.mResourceId = 17305374;
                } else if ("custom3".equals(mode3)) {
                    this.mResourceId = android.R.drawable.scrubber_control_focused_holo;
                } else {
                    this.mResourceId = 17305009;
                }
            } else {
                this.mResourceId = 17305009;
            }
        } else {
            imageArrayK = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_K);
            imageArrayAG = res.obtainTypedArray(R.array.EVF_REC_SETTING_WBNUMBER_AG);
            if (1 < this.numOfCWB) {
                String mode4 = this.mWbController.getValue();
                if ("custom1".equals(mode4)) {
                    this.mResourceId = 17307503;
                } else if ("custom2".equals(mode4)) {
                    this.mResourceId = 17307440;
                } else if ("custom3".equals(mode4)) {
                    this.mResourceId = 17306724;
                } else {
                    this.mResourceId = 17306814;
                }
            } else {
                this.mResourceId = 17306814;
            }
        }
        NumberImage layoutk = makeColorTemp(k, imageArrayK);
        RelativeLayout.LayoutParams klp = makeLayoutParam();
        this.mUpperLayout.addView(layoutk, this.mUpperKLayoutParam);
        NumberImageForAB_GM layoutAB = makeLightBalance(ab, imageArrayAG);
        NumberImageForAB_GM layoutGM = makeColorComp(gm, imageArrayAG);
        RelativeLayout.LayoutParams lp = makeLayoutParam();
        this.mLowerLayout.addView(layoutAB, this.mAbLayoutParam);
        this.mLowerLayout.addView(layoutGM, this.mGmLayoutParam);
        addView(this.mUpperLayout, klp);
        addView(this.mLowerLayout, lp);
        ImageView iv = new ImageView(this.mContext);
        iv.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mCWBIconLayout.addView(iv, this.mCWBIconLayoutParam);
        addView(this.mCWBIconLayout, ilp);
    }

    private NumberImage makeColorTemp(int ColorTemp, TypedArray imageArray) {
        NumberImage layout = new NumberImage(this.mContext);
        String dispColorTemp = "" + ColorTemp + "k";
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (2 == aspect) {
            layout.makeImageFromStringLapover(dispColorTemp, imageArray, -3, 0);
        } else {
            layout.makeImageFromString(dispColorTemp, imageArray);
        }
        return layout;
    }

    private NumberImageForAB_GM makeLightBalance(int value, TypedArray imageArray) {
        String dispLightBalance = "";
        if (value > 0) {
            dispLightBalance = "a" + Math.abs(value);
        } else if (value < 0) {
            dispLightBalance = "b" + Math.abs(value);
        }
        NumberImageForAB_GM layout = new NumberImageForAB_GM(this.mContext);
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (2 == aspect) {
            layout.makeImageFromStringLapover(dispLightBalance, imageArray, -2, 0);
        } else {
            layout.makeImageFromString(dispLightBalance, imageArray);
        }
        return layout;
    }

    private NumberImageForAB_GM makeColorComp(int value, TypedArray imageArray) {
        String dispColorComp = "";
        if (value > 0) {
            dispColorComp = "m" + Math.abs(value);
        } else if (value < 0) {
            dispColorComp = "g" + Math.abs(value);
        }
        NumberImageForAB_GM layout = new NumberImageForAB_GM(this.mContext);
        int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
        if (2 == aspect) {
            layout.makeImageFromStringLapover(dispColorComp, imageArray, -2, 0);
        } else {
            layout.makeImageFromString(dispColorComp, imageArray);
        }
        return layout;
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
            Log.e("NumberImage", WhiteBalanceIcon.LOG_INVALID);
            return 0;
        }
    }
}
