package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class FlashCompensation extends LabelFixedFontFileInfo {
    private static final int DECIMAL_FACTOR = 10;
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final String SEPARATOR_SLASH = "/";
    private static final int VAL_FLASH_FIRED = 1;
    private static final int VAL_FLASH_FIRED_AUTO = 25;
    private static final int VAL_FLASH_FIRED_AUTO_RED_EYE_REDUCTION = 89;
    private static final int VAL_FLASH_FIRED_AUTO_RED_EYE_REDUCTION_RETURN_LIGHT_DETECTED = 95;
    private static final int VAL_FLASH_FIRED_AUTO_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED = 93;
    private static final int VAL_FLASH_FIRED_AUTO_RETURN_LIGHT_DETECTED = 31;
    private static final int VAL_FLASH_FIRED_AUTO_RETURN_LIGHT_NOT_DETECTED = 29;
    private static final int VAL_FLASH_FIRED_COMPULSORY = 9;
    private static final int VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION = 73;
    private static final int VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION_RETURN_LIGHT_DETECTED = 79;
    private static final int VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED = 77;
    private static final int VAL_FLASH_FIRED_COMPULSORY_RETURN_LIGHT_DETECTED = 15;
    private static final int VAL_FLASH_FIRED_COMPULSORY_RETURN_LIGHT_NOT_DETECTED = 13;
    private static final int VAL_FLASH_FIRED_RED_EYE_REDUCTION = 65;
    private static final int VAL_FLASH_FIRED_RED_EYE_REDUCTION_RETURN_LIGHT_DETECTED = 71;
    private static final int VAL_FLASH_FIRED_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED = 69;
    private static final int VAL_FLASH_NOT_FIRED = 0;
    private static final int VAL_FLASH_NOT_FIRED_AUTO = 24;
    private static final int VAL_FLASH_NOT_FIRED_COMPULSORY = 16;
    private static final int VAL_FLASH_NOT_SPECIFIED = 255;
    private static final int VAL_FLASH_NO_FLASH_FUNCTION = 32;
    private static final int VAL_FLASH_STROBE_RETURN_LIGHT_DETECTED = 7;
    private static final int VAL_FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED = 5;
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
        int contentType = AudioVolumeController.INVALID_VALUE;
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
        } else if (1 == flash || 5 == flash || 7 == flash || 9 == flash || 13 == flash || 15 == flash || 25 == flash || 29 == flash || 31 == flash || VAL_FLASH_FIRED_RED_EYE_REDUCTION == flash || VAL_FLASH_FIRED_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED == flash || VAL_FLASH_FIRED_RED_EYE_REDUCTION_RETURN_LIGHT_DETECTED == flash || VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION == flash || VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED == flash || VAL_FLASH_FIRED_COMPULSORY_RED_EYE_REDUCTION_RETURN_LIGHT_DETECTED == flash || VAL_FLASH_FIRED_AUTO_RED_EYE_REDUCTION == flash || VAL_FLASH_FIRED_AUTO_RED_EYE_REDUCTION_RETURN_LIGHT_NOT_DETECTED == flash || 95 == flash) {
            if (flashBias != null) {
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
                    } else {
                        flashBias = null;
                    }
                    setVisibility(isDisplay ? 0 : 4);
                }
            } else {
                setVisibility(4);
            }
        } else {
            setVisibility(4);
            flashBias = null;
        }
        this.mFlashBias = flashBias;
    }

    public void setValue(long flash, long flashBiasNumer, long flashBiasDenom) {
        StringBuffer buf = new StringBuffer();
        buf.append(flashBiasNumer).append('/').append(flashBiasDenom);
        setValue((int) flash, buf.toString(), -1);
    }
}
