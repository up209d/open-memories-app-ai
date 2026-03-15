package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import java.util.List;

/* loaded from: classes.dex */
public class CreativeStyleOptionView extends RelativeLayout {
    private static final int AREAFIG_OPTION_CURSOL_HEIGHT = 56;
    private static final int AREAFIG_OPTION_CURSOL_LEFT = 0;
    private static final int AREAFIG_OPTION_CURSOL_TOP = 12;
    private static final int AREAFIG_OPTION_CURSOL_WIDTH = 152;
    private static final int AREAFIG_OPTION_DOWN_HEIGHT = 18;
    private static final int AREAFIG_OPTION_DOWN_LEFT = 59;
    private static final int AREAFIG_OPTION_DOWN_TOP = 60;
    private static final int AREAFIG_OPTION_DOWN_WIDTH = 34;
    private static final int AREAFIG_OPTION_ICON_HEIGHT = 36;
    private static final int AREAFIG_OPTION_ICON_LEFT = 29;
    private static final int AREAFIG_OPTION_ICON_TOP = 22;
    private static final int AREAFIG_OPTION_ICON_WIDTH = 36;
    private static final int AREAFIG_OPTION_UP_HEIGHT = 18;
    private static final int AREAFIG_OPTION_UP_LEFT = 59;
    private static final int AREAFIG_OPTION_UP_TOP = 0;
    private static final int AREAFIG_OPTION_UP_WIDTH = 34;
    private static final int AREAFIG_OPTION_VALUE_HEIGHT = 46;
    private static final int AREAFIG_OPTION_VALUE_LEFT = 65;
    private static final int AREAFIG_OPTION_VALUE_TOP = 16;
    private static final int AREAFIG_OPTION_VALUE_WIDTH = 80;
    private static final String TAG = "CreativeStyleOptionView";
    private final int INVALID_ALPHA;
    private final int INVISIBLE_ALPHA;
    private final int VALID_ALPHA;
    protected boolean mAvailable;
    protected int mIndex;
    protected ImageView mOpCursor;
    protected ImageView mOpDownArrow;
    protected ImageView mOpIcon;
    protected ImageView mOpUpArrow;
    protected TextView mOpValue;
    protected String mOptionType;
    protected List<String> mValueArray;

    public CreativeStyleOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.INVISIBLE_ALPHA = 0;
        this.INVALID_ALPHA = LiveviewCommon.PAYLOAD_HEADER_SIZE;
        this.VALID_ALPHA = BatteryIcon.BATTERY_STATUS_CHARGING;
        this.mOpCursor = null;
        this.mOpUpArrow = null;
        this.mOpDownArrow = null;
        this.mOpIcon = null;
        this.mOpValue = null;
        this.mValueArray = null;
        this.mIndex = -1;
        this.mAvailable = false;
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CreativeStyleOption);
        int resourceId = attr.getResourceId(1, 0);
        if (resourceId != 0) {
            this.mOpCursor = new ImageView(context);
            this.mOpCursor.setImageResource(resourceId);
            this.mOpCursor.setScaleType(ImageView.ScaleType.FIT_XY);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AREAFIG_OPTION_CURSOL_WIDTH, AREAFIG_OPTION_CURSOL_HEIGHT);
            params.leftMargin = 0;
            params.topMargin = 12;
            addView(this.mOpCursor, params);
        }
        int resourceId2 = attr.getResourceId(2, 0);
        if (resourceId2 != 0) {
            this.mOpUpArrow = new ImageView(context);
            this.mOpUpArrow.setImageResource(resourceId2);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(34, 18);
            params2.leftMargin = 59;
            params2.topMargin = 0;
            addView(this.mOpUpArrow, params2);
        }
        int resourceId3 = attr.getResourceId(3, 0);
        if (resourceId3 != 0) {
            this.mOpDownArrow = new ImageView(context);
            this.mOpDownArrow.setImageResource(resourceId3);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(34, 18);
            params3.leftMargin = 59;
            params3.topMargin = 60;
            addView(this.mOpDownArrow, params3);
        }
        int resourceId4 = attr.getResourceId(4, 0);
        if (resourceId4 != 0) {
            this.mOpIcon = new ImageView(context);
            this.mOpIcon.setImageResource(resourceId4);
            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(36, 36);
            int left = attr.getDimensionPixelOffset(5, 29);
            int top = attr.getDimensionPixelOffset(6, 22);
            params4.leftMargin = left;
            params4.topMargin = top;
            addView(this.mOpIcon, params4);
        }
        int styleId = attr.getResourceId(0, R.attr.RESID_FONTSIZE_BIG_L_EDGE_ON);
        int textColorId = attr.getResourceId(9, R.color.RESID_FONTSTYLE_STD_NORMAL);
        this.mOpValue = new TextView(context, attrs, styleId);
        this.mOpValue.setTextColor(getResources().getColor(textColorId));
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(AREAFIG_OPTION_VALUE_WIDTH, 46);
        int left2 = attr.getDimensionPixelOffset(7, AREAFIG_OPTION_VALUE_LEFT);
        int top2 = attr.getDimensionPixelOffset(8, 16);
        params5.leftMargin = left2;
        params5.topMargin = top2;
        addView(this.mOpValue, params5);
        attr.recycle();
    }

    public void init(String type, List<String> array) {
        this.mOptionType = type;
        this.mValueArray = array;
    }

    public void setCursol() {
        if (this.mOpCursor != null) {
            this.mOpCursor.setVisibility(0);
        }
        setUpDownArrow();
    }

    public void releaseCursol() {
        if (this.mOpCursor != null) {
            this.mOpCursor.setVisibility(4);
        }
        if (this.mOpUpArrow != null) {
            this.mOpUpArrow.setVisibility(4);
        }
        if (this.mOpDownArrow != null) {
            this.mOpDownArrow.setVisibility(4);
        }
    }

    public void setAvailable(boolean available) {
        this.mAvailable = available;
        if (this.mAvailable) {
            this.mOpIcon.setAlpha(BatteryIcon.BATTERY_STATUS_CHARGING);
            this.mOpValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        } else {
            this.mOpIcon.setAlpha(LiveviewCommon.PAYLOAD_HEADER_SIZE);
            this.mOpValue.setText(17041730);
            this.mOpValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CREATIVESTYLE_PARAM_DISABLE));
        }
    }

    public boolean getAvailable() {
        return this.mAvailable;
    }

    public void setCurrentValue(int value) {
        int index;
        if (this.mValueArray != null && (index = this.mValueArray.indexOf(String.valueOf(value))) >= 0) {
            this.mIndex = index;
            if (this.mAvailable) {
                this.mOpValue.setText(valueToText(Integer.valueOf(this.mValueArray.get(this.mIndex)).intValue()));
            }
        }
    }

    public String getCurrentValue() {
        return this.mValueArray.get(this.mIndex);
    }

    public void moveNext() {
        if (this.mAvailable) {
            changeValue(1);
        }
    }

    public void movePrevious() {
        if (this.mAvailable) {
            changeValue(-1);
        }
    }

    private void changeValue(int changeValue) {
        if (this.mValueArray != null && this.mIndex >= 0) {
            int value = Integer.valueOf(this.mValueArray.get(this.mIndex)).intValue();
            int index = this.mValueArray.indexOf(String.valueOf(value + changeValue));
            if (index >= 0) {
                this.mIndex = index;
                setUpDownArrow();
            }
        }
    }

    private void setUpDownArrow() {
        if (this.mAvailable) {
            if (this.mIndex >= 0) {
                int value = Integer.valueOf(this.mValueArray.get(this.mIndex)).intValue();
                Pair<Integer, Integer> supportedRange = CreativeStyleController.getInstance().getSupportedRange(this.mOptionType);
                int max = ((Integer) supportedRange.first).intValue();
                int min = ((Integer) supportedRange.second).intValue();
                if (value >= max) {
                    this.mOpUpArrow.setVisibility(4);
                    this.mOpDownArrow.setVisibility(0);
                    return;
                } else if (value <= min) {
                    this.mOpUpArrow.setVisibility(0);
                    this.mOpDownArrow.setVisibility(4);
                    return;
                } else {
                    this.mOpUpArrow.setVisibility(0);
                    this.mOpDownArrow.setVisibility(0);
                    return;
                }
            }
            return;
        }
        this.mOpUpArrow.setVisibility(4);
        this.mOpDownArrow.setVisibility(4);
    }

    private String valueToText(int value) {
        if (value > 0) {
            String ret = String.valueOf(Math.abs(value));
            return String.format((String) getResources().getText(17041715), ret);
        }
        if (value < 0) {
            String ret2 = String.valueOf(Math.abs(value));
            return String.format((String) getResources().getText(android.R.string.resolver_work_tab_accessibility), ret2);
        }
        String ret3 = String.valueOf(Math.abs(value));
        return String.format((String) getResources().getText(17041716), ret3);
    }
}
