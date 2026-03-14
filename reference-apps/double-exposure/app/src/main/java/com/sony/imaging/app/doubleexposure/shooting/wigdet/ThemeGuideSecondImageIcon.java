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
public class ThemeGuideSecondImageIcon extends ActiveImage {
    private final String TAG;

    public ThemeGuideSecondImageIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme != null && "Manual".equalsIgnoreCase(selectedTheme)) {
            setVisibility(4);
            setBackgroundDrawable(null);
        } else {
            setVisibility(0);
            if (ThemeSelectionController.SOFTFILTER.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_softfilter_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_softfilter_2nd_img_normal);
                }
            } else if (ThemeSelectionController.SIL.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_easysilhouette_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_easysilhouette_2nd_img_normal);
                }
            } else if (ThemeSelectionController.SKY.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_sky_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_sky_2nd_img_normal);
                }
            } else if (ThemeSelectionController.TEXTURE.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_texture_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_texture_2nd_img_normal);
                }
            } else if ("Rotation".equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_rotation_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_rotation_2nd_img_normal);
                }
            } else if (ThemeSelectionController.MIRROR.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_mirror_2nd_img_disable);
                } else {
                    setBackgroundResource(R.drawable.p_16_dd_parts_mle_mirror_2nd_img_normal);
                }
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
