package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SelectedThemeIcon extends ActiveImage {
    private final String TAG;

    public SelectedThemeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int drawable = -1;
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_custom;
        } else if (ThemeController.WATERFLOW.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_water_flow;
        } else if (ThemeController.SILENT.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_silent;
        } else if (ThemeController.SMOKEHAZE.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_smoke_haze;
        } else if (ThemeController.TWILIGHTREFLECTION.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_twilight;
        } else if (ThemeController.MONOTONE.equals(selectedTheme)) {
            drawable = R.drawable.p_16_dd_parts_smooth_theme_info_monotone;
        }
        setImageResource(drawable);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
