package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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

/* loaded from: classes.dex */
public class WhiteBalanceFnIcon extends ActiveLayout {
    private static final int ICON_K_LOWER_LEFT_X = 5;
    private static final int ICON_K_LOWER_LEFT_Y = 29;
    private static final int ICON_ONLY_LEFT_X = 0;
    private static final int ICON_ONLY_LEFT_Y = 0;
    private static final int ICON_UPPER_LEFT_X = 0;
    private static final int ICON_UPPER_LEFT_Y = 0;
    private static final String LOG_INVALID = "Invalid value of WBModeSettings";
    private static final String TAG = "WhiteBalanceFnIcon";
    private Context mContext;
    RelativeLayout mDefalutIconLayout;
    RelativeLayout.LayoutParams mDefalutIconLayoutParam;
    protected final DisplayModeObserver mDisplayObserver;
    RelativeLayout mKLayout;
    RelativeLayout.LayoutParams mKLayoutParam;
    private NumberImage mLayoutk;
    private ActiveLayout.ActiveLayoutListener mListener;
    private int mResourceId;
    private ImageView mTempImageView;
    RelativeLayout mUpperIconLayout;
    RelativeLayout.LayoutParams mUpperIconLayoutParam;
    private WhiteBalanceController mWbController;
    private ImageView mWhiteBalanceImageView;

    public WhiteBalanceFnIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.mContext = context;
        this.mResourceId = 17306584;
        this.mWbController = WhiteBalanceController.getInstance();
        this.mDefalutIconLayoutParam = makeLayoutParam();
        this.mDefalutIconLayoutParam.leftMargin = 0;
        this.mDefalutIconLayoutParam.topMargin = 0;
        this.mUpperIconLayoutParam = makeLayoutParam();
        this.mUpperIconLayoutParam.leftMargin = 0;
        this.mUpperIconLayoutParam.topMargin = 0;
        this.mKLayoutParam = makeLayoutParam();
        this.mKLayoutParam.leftMargin = 5;
        this.mKLayoutParam.topMargin = 29;
        this.mDefalutIconLayout = new RelativeLayout(this.mContext);
        this.mUpperIconLayout = new RelativeLayout(this.mContext);
        this.mKLayout = new RelativeLayout(this.mContext);
        this.mWhiteBalanceImageView = new ImageView(this.mContext);
        this.mTempImageView = new ImageView(this.mContext);
        this.mLayoutk = new NumberImage(this.mContext);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveLayout.ActiveLayoutListener() { // from class: com.sony.imaging.app.base.shooting.widget.WhiteBalanceFnIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    WhiteBalanceFnIcon.this.refresh();
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
        this.mDefalutIconLayout.removeAllViews();
        this.mUpperIconLayout.removeAllViews();
        this.mKLayout.removeAllViews();
        String s = this.mWbController.getValue();
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) this.mWbController.getDetailValue();
        if (s.equals("color-temp") || s.equals(WhiteBalanceController.CUSTOM) || s.equals("custom1") || s.equals("custom2") || s.equals("custom3")) {
            refresh_wbvalue_k(s, param.getColorTemp());
        } else {
            refresh_wb(s);
        }
    }

    private int getIconResourceId(String s) {
        if (s.equals("auto")) {
            return 17306536;
        }
        if (s.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17306574;
        }
        if (s.equals(WhiteBalanceController.SHADE)) {
            return 17306855;
        }
        if (s.equals("cloudy-daylight")) {
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
        if (s.equals("color-temp")) {
            return 17306405;
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
        this.mWhiteBalanceImageView.setVisibility(0);
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            this.mWhiteBalanceImageView.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            this.mWhiteBalanceImageView.setVisibility(4);
        } else {
            this.mResourceId = getIconResourceId(s);
        }
        this.mWhiteBalanceImageView.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mDefalutIconLayout.addView(this.mWhiteBalanceImageView, this.mDefalutIconLayoutParam);
        addView(this.mDefalutIconLayout, ilp);
    }

    private void refresh_wbvalue_k(String s, int k) {
        this.mTempImageView.setVisibility(0);
        if (s.equals(WhiteBalanceController.CUSTOM_SET)) {
            this.mTempImageView.setVisibility(4);
        } else if (s.equals(FlashController.INVISIBLE)) {
            this.mTempImageView.setVisibility(4);
        } else {
            this.mResourceId = getIconResourceId(s);
        }
        this.mTempImageView.setImageResource(this.mResourceId);
        RelativeLayout.LayoutParams ilp = makeLayoutParam();
        this.mUpperIconLayout.addView(this.mTempImageView, this.mUpperIconLayoutParam);
        addView(this.mUpperIconLayout, ilp);
        Resources res = getResources();
        TypedArray imageArrayK = res.obtainTypedArray(R.array.OSD_REC_SETTING_WBNUMBER_K);
        String dispColorTemp = "" + k + "k";
        this.mLayoutk.makeImageFromString(dispColorTemp, imageArrayK);
        RelativeLayout.LayoutParams klp = makeLayoutParam();
        this.mKLayout.addView(this.mLayoutk, this.mKLayoutParam);
        addView(this.mKLayout, klp);
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
    }
}
