package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class WhiteBalance extends CompoundFileInfo {
    private static final String B_BLANK = "     ";
    private static final long COLOR_TEMP_THRES = 9999;
    private static final long NONE = -1;
    private static final String PREFIX_A = "A";
    private static final String PREFIX_B = "B";
    private static final String PREFIX_G = "G";
    private static final String PREFIX_M = "M";
    private static final long VAL_WHITEBALANCE_AUTO_UNDER_WATER = 128;
    private static final long VAL_WHITEBALANCE_AWB = 0;
    private static final long VAL_WHITEBALANCE_CLOUDY = 32;
    private static final long VAL_WHITEBALANCE_DAYLIGHT = 16;
    private static final long VAL_WHITEBALANCE_FLASH = 80;
    private static final long VAL_WHITEBALANCE_FLUORESCENT_0 = 0;
    private static final long VAL_WHITEBALANCE_FLUORESCENT_MINUS_1 = 4294967295L;
    private static final long VAL_WHITEBALANCE_FLUORESCENT_PLUS_1 = 1;
    private static final long VAL_WHITEBALANCE_FLUORESCENT_PLUS_2 = 2;
    private static final long VAL_WHITEBALANCE_INCANDESCENT = 64;
    private static final long VAL_WHITEBALANCE_MANUAL_1 = 112;
    private static final long VAL_WHITEBALANCE_MANUAL_2 = 1;
    private static final long VAL_WHITEBALANCE_SHADE = 48;
    private static final long VAL_WHITEBALANCE_TUNING = 96;
    private static final long WB2_AXIS_TUNING_THRES = 10;
    private long mAbValue;
    private long mColorTemp;
    private ImageView mImageView;
    private long mMgValue;
    private TextView mTextBView;
    private TextView mTextCView;
    private long mWhiteBalance;
    private long mWhiteBalanceTuning;

    public WhiteBalance(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWhiteBalance = Long.MIN_VALUE;
        this.mWhiteBalanceTuning = Long.MIN_VALUE;
        this.mAbValue = Long.MIN_VALUE;
        this.mMgValue = Long.MIN_VALUE;
        this.mColorTemp = Long.MIN_VALUE;
        this.mTextBView = new TextView(context, attrs, R.attr.RESID_FONTSIZE_SP_PLAY_INFO);
        this.mTextCView = new TextView(context, attrs, R.attr.RESID_FONTSIZE_SP_PLAY_INFO);
        this.mImageView = new ImageView(context);
        this.mTextBView.setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        this.mTextCView.setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.WhiteBalance);
        int wbIconWidth = attr.getDimensionPixelSize(0, 0);
        int wbIconHeight = attr.getDimensionPixelSize(1, 0);
        int wbIconMarginLeft = attr.getDimensionPixelSize(2, 0);
        int wbIconMarginTop = attr.getDimensionPixelSize(3, 0);
        int wbTxtBWidth = attr.getDimensionPixelSize(4, 0);
        int wbTxtBHeight = attr.getDimensionPixelSize(5, 0);
        int wbTxtBMarginLeft = attr.getDimensionPixelSize(6, 0);
        int wbTxtBMarginTop = attr.getDimensionPixelSize(7, 0);
        int wbTxtCWidth = attr.getDimensionPixelSize(8, 0);
        int wbTxtCHeight = attr.getDimensionPixelSize(9, 0);
        int wbTxtCMarginLeft = attr.getDimensionPixelSize(10, 0);
        int wbTxtCMarginTop = attr.getDimensionPixelSize(11, 0);
        this.mTextBView.setTypeface(Typeface.UNIVERS);
        this.mTextCView.setTypeface(Typeface.UNIVERS);
        this.mTextBView.setGravity(19);
        this.mTextCView.setGravity(19);
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(wbIconWidth, wbIconHeight);
        paramsImageView.leftMargin = wbIconMarginLeft;
        paramsImageView.topMargin = wbIconMarginTop;
        addView(this.mImageView, paramsImageView);
        RelativeLayout.LayoutParams paramsTextBView = new RelativeLayout.LayoutParams(wbTxtBWidth, wbTxtBHeight);
        paramsTextBView.leftMargin = wbTxtBMarginLeft;
        paramsTextBView.topMargin = wbTxtBMarginTop;
        addView(this.mTextBView, paramsTextBView);
        RelativeLayout.LayoutParams paramsTextCView = new RelativeLayout.LayoutParams(wbTxtCWidth, wbTxtCHeight);
        paramsTextCView.leftMargin = wbTxtCMarginLeft;
        paramsTextCView.topMargin = wbTxtCMarginTop;
        addView(this.mTextCView, paramsTextCView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        this.mWhiteBalance = Long.MIN_VALUE;
        this.mWhiteBalanceTuning = Long.MIN_VALUE;
        this.mAbValue = Long.MIN_VALUE;
        this.mMgValue = Long.MIN_VALUE;
        this.mColorTemp = Long.MIN_VALUE;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo
    public void setContentInfo(ContentInfo info) {
        long whiteBalance = NONE;
        long whiteBalanceTuning = NONE;
        long abValue = NONE;
        long mgValue = NONE;
        long colorTemp = NONE;
        if (info != null) {
            whiteBalance = info.getLong("MkNoteWhiteBalance");
            whiteBalanceTuning = info.getLong("MkNoteWBTuning");
            abValue = info.getLong("MkNoteWB2AxisTuningVal1");
            mgValue = info.getLong("MkNoteWB2AxisTuningVal2");
            colorTemp = info.getLong("MkNoteClolorTemp");
        }
        setValue(whiteBalance, whiteBalanceTuning, abValue, mgValue, colorTemp);
    }

    public void setValue(long whiteBalance, long whiteBalanceTuning, long abValue, long mgValue, long colorTemp) {
        if (whiteBalance != this.mWhiteBalance || whiteBalanceTuning != this.mWhiteBalanceTuning || abValue != this.mAbValue || mgValue != this.mMgValue || colorTemp != this.mColorTemp) {
            boolean isDisplay = false;
            if (whiteBalance == 0) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304682);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_DAYLIGHT) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304699);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_CLOUDY) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304725);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_SHADE) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304701);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_INCANDESCENT) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304728);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_FLASH) {
                if (setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(android.R.drawable.ic_input_extract_action_return);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_AUTO_UNDER_WATER) {
                if (Environment.getVersionPfAPI() >= 2) {
                    if (setBalance(abValue, mgValue)) {
                        this.mImageView.setImageResource(17307587);
                        this.mImageView.setVisibility(0);
                        isDisplay = true;
                    }
                } else {
                    isDisplay = false;
                }
            } else if (whiteBalance == VAL_WHITEBALANCE_TUNING) {
                if (whiteBalanceTuning == VAL_WHITEBALANCE_FLUORESCENT_MINUS_1) {
                    if (setBalance(abValue, mgValue)) {
                        this.mImageView.setImageResource(17304730);
                        this.mImageView.setVisibility(0);
                        isDisplay = true;
                    }
                } else if (whiteBalanceTuning == 0) {
                    if (setBalance(abValue, mgValue)) {
                        this.mImageView.setImageResource(17304731);
                        this.mImageView.setVisibility(0);
                        isDisplay = true;
                    }
                } else if (whiteBalanceTuning == 1) {
                    if (setBalance(abValue, mgValue)) {
                        this.mImageView.setImageResource(17304733);
                        this.mImageView.setVisibility(0);
                        isDisplay = true;
                    }
                } else if (whiteBalanceTuning == 2 && setBalance(abValue, mgValue)) {
                    this.mImageView.setImageResource(17304734);
                    this.mImageView.setVisibility(0);
                    isDisplay = true;
                }
            } else if ((whiteBalance == VAL_WHITEBALANCE_MANUAL_1 || whiteBalance == 1) && setTempAndBalance(colorTemp, abValue, mgValue)) {
                this.mImageView.setVisibility(4);
                isDisplay = true;
            }
            setVisibility(isDisplay ? 0 : 4);
        }
        this.mWhiteBalance = whiteBalance;
        this.mWhiteBalanceTuning = whiteBalanceTuning;
        this.mAbValue = abValue;
        this.mMgValue = mgValue;
        this.mColorTemp = colorTemp;
    }

    private boolean setBalance(long abValue, long mgValue) {
        String textAB;
        String textMG;
        if (abValue != 0 || mgValue != 0) {
            if (abValue > 0 && abValue < WB2_AXIS_TUNING_THRES) {
                textAB = LogHelper.getScratchBuilder(PREFIX_A).append(abValue).toString();
            } else if (abValue == 0) {
                textAB = B_BLANK;
            } else if (abValue < 0 && abValue > -10) {
                textAB = LogHelper.getScratchBuilder(PREFIX_B).append(-abValue).toString();
            } else {
                return false;
            }
            if (mgValue > 0 && mgValue < WB2_AXIS_TUNING_THRES) {
                textMG = LogHelper.getScratchBuilder("M").append(mgValue).toString();
            } else if (mgValue == 0) {
                textMG = B_BLANK;
            } else if (mgValue < 0 && mgValue > -10) {
                textMG = LogHelper.getScratchBuilder(PREFIX_G).append(-mgValue).toString();
            } else {
                return false;
            }
            this.mTextBView.setText(String.format(getResources().getString(android.R.string.zen_mode_forever), textAB, textMG));
            this.mTextBView.setVisibility(0);
            this.mTextCView.setVisibility(4);
        } else {
            this.mTextBView.setVisibility(4);
            this.mTextCView.setVisibility(4);
        }
        return true;
    }

    private boolean setTempAndBalance(long colorTemp, long abValue, long mgValue) {
        String textAB;
        String textMG;
        if (0 >= colorTemp || COLOR_TEMP_THRES < colorTemp) {
            return false;
        }
        if (abValue > 0 && abValue < WB2_AXIS_TUNING_THRES) {
            textAB = LogHelper.getScratchBuilder(PREFIX_A).append(abValue).toString();
        } else if (abValue == 0) {
            textAB = B_BLANK;
        } else if (abValue < 0 && abValue > -10) {
            textAB = LogHelper.getScratchBuilder(PREFIX_B).append(-abValue).toString();
        } else {
            return false;
        }
        if (mgValue > 0 && mgValue < WB2_AXIS_TUNING_THRES) {
            textMG = LogHelper.getScratchBuilder("M").append(mgValue).toString();
        } else if (mgValue == 0) {
            textMG = B_BLANK;
        } else if (mgValue < 0 && mgValue > -10) {
            textMG = LogHelper.getScratchBuilder(PREFIX_G).append(-mgValue).toString();
        } else {
            return false;
        }
        String textColorTemp = String.format(getResources().getString(android.R.string.zen_mode_forever_dnd), Long.valueOf(colorTemp), textAB, textMG);
        this.mTextCView.setText(textColorTemp);
        this.mTextBView.setVisibility(4);
        this.mTextCView.setVisibility(0);
        return true;
    }
}
