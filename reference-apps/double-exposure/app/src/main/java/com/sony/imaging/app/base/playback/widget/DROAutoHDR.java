package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class DROAutoHDR extends CompoundFileInfo {
    private static final long DRO_AUTO = 3;
    private static final long DRO_LV_0 = 0;
    private static final long DRO_LV_1 = 16;
    private static final long DRO_LV_2 = 17;
    private static final long DRO_LV_3 = 18;
    private static final long DRO_LV_4 = 19;
    private static final long DRO_LV_5 = 20;
    private static final long HDR_1_0_EV = 65552;
    private static final long HDR_2_0_EV = 65554;
    private static final long HDR_3_0_EV = 65556;
    private static final long HDR_4_0_EV = 65558;
    private static final long HDR_5_0_EV = 65560;
    private static final long HDR_6_0_EV = 65562;
    private static final long HDR_AUTO = 65537;
    private static final long HDR_AUTO_GP = 65536;
    private static final long HDR_WARNING_1 = 131072;
    private static final long HDR_WARNING_2 = 196608;
    private static final long HIGHER_BIT_MASK = -65536;
    private static final long NONE = -1;
    private long mAutoHDR;
    private long mEnhanceLevel;
    private ImageView mImageView;
    boolean mIsOnlyErrDisplay;
    private int mRscDr;
    private int mRscDro;
    private int mRscHdr;
    private int mRscHdrErr;
    private TextView mTextView;

    public DROAutoHDR(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsOnlyErrDisplay = false;
        this.mAutoHDR = Long.MIN_VALUE;
        this.mEnhanceLevel = Long.MIN_VALUE;
        this.mTextView = new TextView(context, attrs, R.attr.RESID_FONTSIZE_PLAY_HISTOGRAM_EDGE);
        this.mTextView.setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        this.mImageView = new ImageView(context);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.DROAutoHDR);
        int iconWidth = attr.getDimensionPixelSize(0, 0);
        int iconHeight = attr.getDimensionPixelSize(1, 0);
        int iconMarginLeft = attr.getDimensionPixelSize(2, 0);
        int iconMarginTop = attr.getDimensionPixelSize(3, 0);
        int txtWidth = attr.getDimensionPixelSize(4, 0);
        int txtHeight = attr.getDimensionPixelSize(5, 0);
        int txtMarginLeft = attr.getDimensionPixelSize(6, 0);
        int txtMarginTop = attr.getDimensionPixelSize(7, 0);
        this.mIsOnlyErrDisplay = attr.getBoolean(8, false);
        this.mRscHdrErr = attr.getResourceId(12, 0);
        if (!this.mIsOnlyErrDisplay) {
            this.mRscDr = attr.getResourceId(9, 0);
            this.mRscDro = attr.getResourceId(10, 0);
            this.mRscHdr = attr.getResourceId(11, 0);
        } else {
            this.mRscDr = 0;
            this.mRscDro = 0;
            this.mRscHdr = 0;
        }
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(iconWidth, iconHeight);
        paramsImageView.leftMargin = iconMarginLeft;
        paramsImageView.topMargin = iconMarginTop;
        addView(this.mImageView, paramsImageView);
        if (!this.mIsOnlyErrDisplay) {
            this.mTextView.setTypeface(Typeface.UNIVERS);
            this.mTextView.setGravity(19);
            RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(txtWidth, txtHeight);
            paramsTextView.leftMargin = txtMarginLeft;
            paramsTextView.topMargin = txtMarginTop;
            addView(this.mTextView, paramsTextView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        this.mAutoHDR = Long.MIN_VALUE;
        this.mEnhanceLevel = Long.MIN_VALUE;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo
    public void setContentInfo(ContentInfo info) {
        long autoHDR = NONE;
        long enhanceLevel = NONE;
        if (info != null) {
            autoHDR = info.getLong("MkNoteAutoHDR");
            enhanceLevel = info.getLong("MkNoteEnhanceLevel");
        }
        setValue(autoHDR, enhanceLevel);
    }

    public void setValue(long autoHDR, long enhanceLevel) {
        if (autoHDR != this.mAutoHDR || enhanceLevel != this.mEnhanceLevel) {
            boolean isDisplay = false;
            boolean bHdrVal = false;
            boolean bDroVal = false;
            int hdrVal = 0;
            int droVal = 0;
            String text = "";
            if (!this.mIsOnlyErrDisplay) {
                if (enhanceLevel == 0) {
                    if ((HIGHER_BIT_MASK & autoHDR) == HDR_WARNING_1 || (HIGHER_BIT_MASK & autoHDR) == HDR_WARNING_2) {
                        this.mImageView.setImageResource(this.mRscHdrErr);
                    } else if ((HIGHER_BIT_MASK & autoHDR) == HDR_AUTO_GP) {
                        if (autoHDR == HDR_AUTO) {
                            text = getResources().getString(17042019);
                            bHdrVal = false;
                        } else if (autoHDR == HDR_1_0_EV) {
                            bHdrVal = true;
                            hdrVal = 1;
                        } else if (autoHDR == HDR_2_0_EV) {
                            bHdrVal = true;
                            hdrVal = 2;
                        } else if (autoHDR == HDR_3_0_EV) {
                            bHdrVal = true;
                            hdrVal = 3;
                        } else if (autoHDR == HDR_4_0_EV) {
                            bHdrVal = true;
                            hdrVal = 4;
                        } else if (autoHDR == HDR_5_0_EV) {
                            bHdrVal = true;
                            hdrVal = 5;
                        } else if (autoHDR == HDR_6_0_EV) {
                            bHdrVal = true;
                            hdrVal = 6;
                        }
                        this.mImageView.setImageResource(this.mRscHdr);
                    } else {
                        text = getResources().getString(17042020);
                        this.mImageView.setImageResource(this.mRscDr);
                    }
                    isDisplay = true;
                } else if (enhanceLevel == 3) {
                    text = getResources().getString(17042019);
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                } else if (enhanceLevel == DRO_LV_1) {
                    bDroVal = true;
                    droVal = 1;
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                } else if (enhanceLevel == DRO_LV_2) {
                    bDroVal = true;
                    droVal = 2;
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                } else if (enhanceLevel == DRO_LV_3) {
                    bDroVal = true;
                    droVal = 3;
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                } else if (enhanceLevel == DRO_LV_4) {
                    bDroVal = true;
                    droVal = 4;
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                } else if (enhanceLevel == DRO_LV_5) {
                    bDroVal = true;
                    droVal = 5;
                    this.mImageView.setImageResource(this.mRscDro);
                    isDisplay = true;
                }
                if (isDisplay) {
                    if (bHdrVal) {
                        this.mTextView.setText(String.format(getResources().getString(android.R.string.sipAddressTypeOther), Integer.valueOf(hdrVal), 0));
                    } else if (bDroVal) {
                        this.mTextView.setText(String.format(getResources().getString(17042021), Integer.valueOf(droVal)));
                    } else {
                        this.mTextView.setText(text);
                    }
                }
            } else if (enhanceLevel == 0 && ((HIGHER_BIT_MASK & autoHDR) == HDR_WARNING_1 || (HIGHER_BIT_MASK & autoHDR) == HDR_WARNING_2)) {
                this.mImageView.setImageResource(this.mRscHdrErr);
                isDisplay = true;
                this.mAutoHDR = autoHDR;
                this.mEnhanceLevel = enhanceLevel;
            }
            setVisibility(isDisplay ? 0 : 4);
        }
        this.mAutoHDR = autoHDR;
        this.mEnhanceLevel = enhanceLevel;
    }
}
