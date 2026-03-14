package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class FlashCompensation extends LabelFixedFontFileInfo {
    private static final int DECIMAL_FACTOR = 10;
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final String SEPARATOR_SLASH = "/";
    private static final int VAL_FLASH_NOT_FIRED = 0;
    private static final int VAL_FLASH_NOT_FIRED_AUTO = 24;
    private static final int VAL_FLASH_NOT_FIRED_COMPULSORY = 16;
    private static final int VAL_FLASH_NOT_SPECIFIED = 255;
    private static final int VAL_FLASH_NO_FLASH_FUNCTION = 32;
    private String mFlashBias;

    public FlashCompensation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlashBias = null;
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FlashCompensation);
        int fcTxtMarginLeft = attr.getDimensionPixelSize(0, 0);
        int fcTxtMarginTop = attr.getDimensionPixelSize(1, 0);
        int fcTxtWidth = attr.getDimensionPixelSize(2, 0);
        int fcTxtHeight = attr.getDimensionPixelSize(3, 0);
        int fcIconMarginLeft = attr.getDimensionPixelSize(4, 0);
        int fcIconMarginTop = attr.getDimensionPixelSize(5, 0);
        int fcIconWidth = attr.getDimensionPixelSize(6, 0);
        int fcIconHeight = attr.getDimensionPixelSize(7, 0);
        Drawable icon = getResources().getDrawable(17305148);
        icon.setBounds(fcIconMarginLeft, fcIconMarginTop, fcIconWidth, fcIconHeight);
        setCompoundDrawables(icon, null, null, null);
        RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(fcTxtWidth, fcTxtHeight);
        paramsTextView.leftMargin = fcTxtMarginLeft;
        paramsTextView.topMargin = fcTxtMarginTop;
        setLayoutParams(paramsTextView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mFlashBias = null;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        int flash = 255;
        String flashBias = null;
        int contentType = Integer.MIN_VALUE;
        if (info != null) {
            flash = info.getInt("Flash");
            flashBias = info.getString("MkNoteFlashBias");
            contentType = info.getInt("ContentType");
        }
        setValue(flash, flashBias, contentType);
    }

    public void setValue(int flash, String flashBias, int contentType) {
        if (1 == contentType || 2 == contentType || 3 == contentType || 4 == contentType || 16 == contentType || 9 == contentType || 12 == contentType) {
            setVisibility(4);
            flashBias = null;
        } else if (flash == 0 || 16 == flash || 24 == flash || 32 == flash || 255 == flash || Integer.MIN_VALUE == flash) {
            setVisibility(4);
            flashBias = null;
        } else if (flashBias != null) {
            if (!flashBias.equals(this.mFlashBias)) {
                boolean isDisplay = false;
                String txtflashBias = "";
                String[] value = flashBias.split(SEPARATOR_SLASH);
                if (value != null && value.length == 2) {
                    int flashBiasNumer = Integer.parseInt(value[0]);
                    int flashBiasDenom = Integer.parseInt(value[1]);
                    if (flashBiasDenom != 0) {
                        int flashBias4Disp = (flashBiasNumer * 10) / flashBiasDenom;
                        if (flashBias4Disp == 0) {
                            txtflashBias = getResources().getString(android.R.string.restr_pin_confirm_pin);
                        } else if (flashBias4Disp > 0) {
                            int flashBias_Int = flashBias4Disp / 10;
                            int flashBias_Point = flashBias4Disp % 10;
                            txtflashBias = String.format(getResources().getString(17041743), Integer.valueOf(flashBias_Int), Integer.valueOf(flashBias_Point));
                        } else if (flashBias4Disp < 0) {
                            int flashBias4Disp2 = -flashBias4Disp;
                            int flashBias_Int2 = flashBias4Disp2 / 10;
                            int flashBias_Point2 = flashBias4Disp2 % 10;
                            txtflashBias = String.format(getResources().getString(17041717), Integer.valueOf(flashBias_Int2), Integer.valueOf(flashBias_Point2));
                        }
                        isDisplay = true;
                    }
                }
                if (isDisplay) {
                    setText(txtflashBias);
                }
                setVisibility(isDisplay ? 0 : 4);
            }
        } else {
            setVisibility(4);
        }
        this.mFlashBias = flashBias;
    }

    public void setValue(long flash, long flashBiasNumer, long flashBiasDenom) {
        StringBuffer buf = new StringBuffer();
        buf.append(flashBiasNumer).append('/').append(flashBiasDenom);
        setValue((int) flash, buf.toString(), -1);
    }
}
