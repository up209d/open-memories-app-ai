package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class WhiteBalanceAdjustLayerAndArrowView extends RelativeLayout {
    private static final int CUSTOMVIEW_ADJUSTLAYER_H = 169;
    private static final int CUSTOMVIEW_ADJUSTLAYER_W = 169;
    private static final int CUSTOMVIEW_ADJUSTLAYER_X = 21;
    private static final int CUSTOMVIEW_ADJUSTLAYER_Y = 18;
    private static final int CUSTOMVIEW_ARROW_BOTTOM_H = 18;
    private static final int CUSTOMVIEW_ARROW_BOTTOM_W = 34;
    private static final int CUSTOMVIEW_ARROW_BOTTOM_X = 89;
    private static final int CUSTOMVIEW_ARROW_BOTTOM_Y = 187;
    private static final int CUSTOMVIEW_ARROW_LEFT_H = 38;
    private static final int CUSTOMVIEW_ARROW_LEFT_W = 21;
    private static final int CUSTOMVIEW_ARROW_LEFT_X_169 = 23;
    private static final int CUSTOMVIEW_ARROW_LEFT_X_43 = 0;
    private static final int CUSTOMVIEW_ARROW_LEFT_Y = 84;
    private static final int CUSTOMVIEW_ARROW_RIGHT_H = 38;
    private static final int CUSTOMVIEW_ARROW_RIGHT_W = 21;
    private static final int CUSTOMVIEW_ARROW_RIGHT_X_169 = 166;
    private static final int CUSTOMVIEW_ARROW_RIGHT_X_43 = 190;
    private static final int CUSTOMVIEW_ARROW_RIGHT_Y = 84;
    private static final int CUSTOMVIEW_ARROW_TOP_H = 18;
    private static final int CUSTOMVIEW_ARROW_TOP_W = 34;
    private static final int CUSTOMVIEW_ARROW_TOP_X = 89;
    private static final int CUSTOMVIEW_ARROW_TOP_Y = 0;
    private static final boolean DEBUG = false;
    private static final int KNOB_CENTER = 10;
    private static final String RESOUCE_NAME_EVF_CHART = "android:drawable/p_wbtuningchart_with_lv_evf";
    private static final String TAG = "WhiteBalanceAdjustLayerAndArrowView";
    private ImageView mBottomArrowIcon;
    private List<String> mComp;
    private Context mContext;
    protected final DisplayModeObserver mDisplayObserver;
    private boolean mIsEvfpartsExist;
    private ImageView mLeftArrowIcon;
    private List<String> mLight;
    private int mLocalComp;
    private int mLocalLight;
    private ImageView mRightArrowIcon;
    private ImageView mTopArrowIcon;
    private WhiteBalanceController mWBController;
    private WhiteBalanceAdjustLayer mWhiteBalanceAdjustLayer;
    private int osdAspect;
    private Integer[] posX;
    private Integer[] posY;
    private static final Rect RECT_43 = new Rect(17, 17, 151, 151);
    private static final Rect RECT_169 = new Rect(34, 17, 135, 151);

    public WhiteBalanceAdjustLayerAndArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.WhiteBalance);
        this.mContext = context;
        int resId = getResources().getIdentifier(RESOUCE_NAME_EVF_CHART, null, context.getPackageName());
        if (resId == 0) {
            this.mIsEvfpartsExist = DEBUG;
        } else {
            this.mIsEvfpartsExist = true;
        }
        this.osdAspect = getAspect();
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(34, 18);
        param.leftMargin = 89;
        param.topMargin = 0;
        this.mTopArrowIcon = new ImageView(context);
        addView(this.mTopArrowIcon, param);
        this.mTopArrowIcon.setImageDrawable(attr.getDrawable(14));
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(34, 18);
        param2.leftMargin = 89;
        param2.topMargin = CUSTOMVIEW_ARROW_BOTTOM_Y;
        this.mBottomArrowIcon = new ImageView(context);
        addView(this.mBottomArrowIcon, param2);
        this.mBottomArrowIcon.setImageDrawable(attr.getDrawable(15));
        RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(21, 38);
        if (2 == this.osdAspect) {
            param3.leftMargin = 23;
        } else {
            param3.leftMargin = 0;
        }
        param3.topMargin = 84;
        this.mLeftArrowIcon = new ImageView(context);
        addView(this.mLeftArrowIcon, param3);
        this.mLeftArrowIcon.setImageDrawable(attr.getDrawable(16));
        RelativeLayout.LayoutParams param4 = new RelativeLayout.LayoutParams(21, 38);
        if (2 == this.osdAspect) {
            param4.leftMargin = CUSTOMVIEW_ARROW_RIGHT_X_169;
        } else {
            param4.leftMargin = CUSTOMVIEW_ARROW_RIGHT_X_43;
        }
        param4.topMargin = 84;
        this.mRightArrowIcon = new ImageView(context);
        addView(this.mRightArrowIcon, param4);
        this.mRightArrowIcon.setImageDrawable(attr.getDrawable(17));
        RelativeLayout.LayoutParams param5 = new RelativeLayout.LayoutParams(169, 169);
        param5.leftMargin = 21;
        param5.topMargin = 18;
        this.mWhiteBalanceAdjustLayer = new WhiteBalanceAdjustLayer(context, attrs);
        addView(this.mWhiteBalanceAdjustLayer, param5);
        attr.recycle();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.osdAspect = getAspect();
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) this.mLeftArrowIcon.getLayoutParams();
        if (2 == this.osdAspect) {
            param.leftMargin = 23;
        } else {
            param.leftMargin = 0;
        }
        this.mLeftArrowIcon.setLayoutParams(param);
        RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) this.mRightArrowIcon.getLayoutParams();
        if (2 == this.osdAspect) {
            param2.leftMargin = CUSTOMVIEW_ARROW_RIGHT_X_169;
        } else {
            param2.leftMargin = CUSTOMVIEW_ARROW_RIGHT_X_43;
        }
        this.mRightArrowIcon.setLayoutParams(param2);
    }

    private int getAspect() {
        if (this.mIsEvfpartsExist) {
            int aspect = this.mDisplayObserver.getActiveDeviceOsdAspect();
            return aspect;
        }
        int deviceOsdAspect = ScalarProperties.getInt("device.panel.aspect");
        if (169 == deviceOsdAspect) {
            return 2;
        }
        return 3;
    }

    public WhiteBalanceAdjustLayer getWhiteBalanceAdjustLayer() {
        return this.mWhiteBalanceAdjustLayer;
    }

    public void setWBController(WhiteBalanceController controller) {
        this.mWBController = controller;
        createXYPosition();
        WhiteBalanceController.WhiteBalanceParam mWBParam = (WhiteBalanceController.WhiteBalanceParam) this.mWBController.getDetailValue();
        this.mLocalLight = mWBParam.getLightBalance();
        this.mLocalComp = mWBParam.getColorComp();
        this.mWhiteBalanceAdjustLayer.setWBController(this.mWBController);
        this.mWhiteBalanceAdjustLayer.setXYPosition(this.posX, this.posY);
        updateArrow(this.mLocalLight, this.mLocalComp);
    }

    private void createXYPosition() {
        float divX;
        float divY;
        int rectLeft;
        int rectBottom;
        this.mLight = this.mWBController.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE);
        this.mComp = this.mWBController.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION);
        this.posX = new Integer[this.mLight.size()];
        this.posY = new Integer[this.mComp.size()];
        if (2 == this.osdAspect) {
            divX = RECT_169.width() / (this.mLight.size() - 1);
            divY = RECT_169.height() / (this.mComp.size() - 1);
            rectLeft = RECT_169.left;
            rectBottom = RECT_169.bottom;
        } else {
            divX = RECT_43.width() / (this.mLight.size() - 1);
            divY = RECT_43.height() / (this.mComp.size() - 1);
            rectLeft = RECT_43.left;
            rectBottom = RECT_43.bottom;
        }
        int size = this.mLight.size();
        for (int i = 0; size > i; i++) {
            this.posX[i] = Integer.valueOf((int) ((i * divX) + (rectLeft - 10)));
        }
        int size2 = this.mComp.size();
        for (int i2 = 0; size2 > i2; i2++) {
            this.posY[i2] = Integer.valueOf((int) ((rectBottom - 10) - (((size2 - 1) - i2) * divY)));
        }
    }

    public boolean moveX(int value) {
        if (value < (-this.posX.length) / 2 || value > this.posX.length / 2) {
            return DEBUG;
        }
        this.mWhiteBalanceAdjustLayer.moveX(value);
        this.mLocalLight = value;
        updateArrow(this.mLocalLight, this.mLocalComp);
        invalidate();
        return true;
    }

    public boolean moveY(int value) {
        if (value < (-this.posY.length) / 2 || value > this.posY.length / 2) {
            return DEBUG;
        }
        this.mWhiteBalanceAdjustLayer.moveY(value);
        this.mLocalComp = value;
        updateArrow(this.mLocalLight, this.mLocalComp);
        invalidate();
        return true;
    }

    public void reset(WhiteBalanceController.WhiteBalanceParam param) {
        this.mWhiteBalanceAdjustLayer.reset(param);
        this.mLocalLight = 0;
        this.mLocalComp = 0;
        updateArrow(this.mLocalLight, this.mLocalComp);
        invalidate();
    }

    protected void updateArrow(int light, int comp) {
        if ((-this.posX.length) / 2 == light) {
            this.mLeftArrowIcon.setVisibility(4);
        } else {
            this.mLeftArrowIcon.setVisibility(0);
        }
        if (this.posX.length / 2 == light) {
            this.mRightArrowIcon.setVisibility(4);
        } else {
            this.mRightArrowIcon.setVisibility(0);
        }
        if ((-this.posY.length) / 2 == comp) {
            this.mTopArrowIcon.setVisibility(4);
        } else {
            this.mTopArrowIcon.setVisibility(0);
        }
        if (this.posY.length / 2 == comp) {
            this.mBottomArrowIcon.setVisibility(4);
        } else {
            this.mBottomArrowIcon.setVisibility(0);
        }
    }
}
