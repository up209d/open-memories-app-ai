package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class Aperture extends LabelFixedFontFileInfo {
    private static final int APERTURE_THRES = 100;
    private static final int DECIMAL_FACTOR = 10;
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final String MSG_ERR_APERTURE_IS_NULL = "###ERROR: aperture is null.";
    private static final String SEPARATOR_SLASH = "/";
    private static final String TAG = Aperture.class.getSimpleName();
    private String mAperture;

    public Aperture(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAperture = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mAperture = null;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        String aperture = null;
        if (info != null) {
            aperture = info.getString("FNumber");
        }
        setValue(aperture);
    }

    public void setValue(String aperture) {
        if (aperture != null) {
            if (!aperture.equals(this.mAperture)) {
                int apertureNumer = 0;
                int apertureDenom = 0;
                String[] apertureValue = aperture.split("/");
                if (apertureValue != null && apertureValue.length == 2) {
                    apertureNumer = Integer.parseInt(apertureValue[0]);
                    apertureDenom = Integer.parseInt(apertureValue[1]);
                }
                setValue(apertureNumer, apertureDenom);
            }
        } else {
            setVisibility(4);
            Log.w(TAG, MSG_ERR_APERTURE_IS_NULL);
        }
        this.mAperture = aperture;
    }

    public void setValue(int apertureNumer, int apertureDenom) {
        int aValue;
        boolean isDisplay = false;
        String txtAperture = "";
        if (apertureDenom != 0 && (aValue = (apertureNumer * 10) / apertureDenom) != 0) {
            int aValue_Int = aValue / 10;
            int aValue_Point = aValue % 10;
            if (aValue < 100) {
                txtAperture = String.format(getResources().getString(17041946), Integer.valueOf(aValue_Int), Integer.valueOf(aValue_Point));
                isDisplay = true;
            } else {
                txtAperture = String.format(getResources().getString(R.string.restr_pin_enter_admin_pin), Integer.valueOf(aValue_Int));
                isDisplay = true;
            }
        }
        if (isDisplay) {
            setText(txtAperture);
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
