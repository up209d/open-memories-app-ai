package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class ExposureCompensation extends LabelFixedFontFileInfo {
    private static final int DECIMAL_FACTOR = 10;
    private static final int EXPOSURE_BIAS_THRES = 79;
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final String SEPARATOR_SLASH = "/";
    private String mExposureBias;

    public ExposureCompensation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mExposureBias = null;
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ExposureCompensation);
        int ecTxtMarginLeft = attr.getDimensionPixelSize(0, 0);
        int ecTxtMarginTop = attr.getDimensionPixelSize(1, 0);
        int ecTxtWidth = attr.getDimensionPixelSize(2, 0);
        int ecTxtHeight = attr.getDimensionPixelSize(3, 0);
        int ecImgMarginLeft = attr.getDimensionPixelSize(4, 0);
        int ecImgMarginTop = attr.getDimensionPixelSize(5, 0);
        int ecImgWidth = attr.getDimensionPixelSize(6, 0);
        int ecImgHeight = attr.getDimensionPixelSize(7, 0);
        Drawable icon = getResources().getDrawable(17305271);
        icon.setBounds(ecImgMarginLeft, ecImgMarginTop, ecImgWidth, ecImgHeight);
        setCompoundDrawables(icon, null, null, null);
        RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(ecTxtWidth, ecTxtHeight);
        paramsTextView.leftMargin = ecTxtMarginLeft;
        paramsTextView.topMargin = ecTxtMarginTop;
        setLayoutParams(paramsTextView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mExposureBias = null;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        String exposureBias = null;
        if (info != null) {
            exposureBias = info.getString("ExposureBiasValue");
        }
        setValue(exposureBias);
    }

    public void setValue(String exposureBias) {
        if (exposureBias != null) {
            if (!exposureBias.equals(this.mExposureBias)) {
                boolean isDisplay = false;
                String txtExposureBias = "";
                String[] value = exposureBias.split(SEPARATOR_SLASH);
                if (value != null && value.length == 2) {
                    int exposureBiasNumer = Integer.parseInt(value[0]);
                    int exposureBiasDenom = Integer.parseInt(value[1]);
                    if (exposureBiasDenom != 0) {
                        int exposureBias4Disp = (exposureBiasNumer * 10) / exposureBiasDenom;
                        if (exposureBias4Disp == 0) {
                            txtExposureBias = getResources().getString(android.R.string.restr_pin_enter_new_pin);
                            isDisplay = true;
                        } else if (exposureBias4Disp > 0 && exposureBias4Disp <= EXPOSURE_BIAS_THRES) {
                            int exposureBias_Int = exposureBias4Disp / 10;
                            int exposureBias_Point = exposureBias4Disp % 10;
                            txtExposureBias = String.format(getResources().getString(17041720), Integer.valueOf(exposureBias_Int), Integer.valueOf(exposureBias_Point));
                            isDisplay = true;
                        } else if (exposureBias4Disp < 0 && exposureBias4Disp >= -79) {
                            int exposureBias4Disp2 = -exposureBias4Disp;
                            int exposureBias_Int2 = exposureBias4Disp2 / 10;
                            int exposureBias_Point2 = exposureBias4Disp2 % 10;
                            txtExposureBias = String.format(getResources().getString(17041719), Integer.valueOf(exposureBias_Int2), Integer.valueOf(exposureBias_Point2));
                            isDisplay = true;
                        }
                    }
                }
                if (isDisplay) {
                    setText(txtExposureBias);
                }
                setVisibility(isDisplay ? 0 : 4);
            }
        } else {
            setVisibility(4);
        }
        this.mExposureBias = exposureBias;
    }

    public void setValue(long exposureBiasNumer, long exposureBiasDenom) {
        StringBuffer buf = new StringBuffer();
        buf.append(exposureBiasNumer).append('/').append(exposureBiasDenom);
        setValue(buf.toString());
    }
}
