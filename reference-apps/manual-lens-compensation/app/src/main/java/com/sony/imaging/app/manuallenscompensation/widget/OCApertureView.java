package com.sony.imaging.app.manuallenscompensation.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.widget.ApertureView;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class OCApertureView extends ApertureView {
    private static final String TAG = "OCApertureView";
    private boolean bHighLightChanging;

    public OCApertureView(Context context) {
        super(context);
        this.bHighLightChanging = false;
    }

    public OCApertureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bHighLightChanging = false;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView
    public void setUserChanging(boolean changing) {
        this.bHighLightChanging = changing;
        super.setUserChanging(changing);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ApertureView, com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    public void refresh() {
        if (OCUtil.getInstance().isOldLensAttached()) {
            if (DisplayModeObserver.getInstance().getActiveDevice() == 1) {
                setTextAppearance(getContext(), R.style.RESID_FONTSIZE_SP_REC_LV_ISO_EVF);
            } else {
                setTextAppearance(getContext(), R.style.RESID_FONTSIZE_SP_REC_LV_AV_TV);
            }
            oldLensApertureRefresh();
            blink(false);
            return;
        }
        if (DisplayModeObserver.getInstance().getActiveDevice() == 1) {
            setTextAppearance(getContext(), R.style.RESID_FONTSIZE_SP_REC_LV_AV_TV_EVF);
        } else {
            setTextAppearance(getContext(), R.style.RESID_FONTSIZE_SP_REC_LV_AV_TV);
        }
        super.refresh();
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colors) {
        if (!this.bHighLightChanging) {
            setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.bHighLightChanging = false;
        OCUtil.getInstance().setExifData();
    }

    private void oldLensApertureRefresh() {
        String aperture = OCUtil.getInstance().getAdequateApertureValue();
        if (aperture.length() == 6) {
            aperture = aperture.substring(0, 5);
        }
        setValue(aperture);
    }
}
