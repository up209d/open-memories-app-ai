package com.sony.imaging.app.doubleexposure.shooting.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;

/* loaded from: classes.dex */
public class MultipleExposureImageIcon extends ActiveImage {
    private final String TAG;

    public MultipleExposureImageIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int drawable = -1;
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SKY)) {
                drawable = R.drawable.p_16_dd_parts_mle_sky_3rd_img;
            } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SIL)) {
                drawable = R.drawable.p_16_dd_parts_mle_easysilhouette_3rd_img;
            } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
                drawable = R.drawable.p_16_dd_parts_mle_texture_3rd_img;
            } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
                drawable = R.drawable.p_16_dd_parts_mle_softfilter_3rd_img;
            } else if (selectedTheme.equalsIgnoreCase("Rotation")) {
                drawable = R.drawable.p_16_dd_parts_mle_rotation_3rd_img;
            } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
                drawable = R.drawable.p_16_dd_parts_mle_mirror_3rd_img;
            }
            setImageResource(drawable);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
