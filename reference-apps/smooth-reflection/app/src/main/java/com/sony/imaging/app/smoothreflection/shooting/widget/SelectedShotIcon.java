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
public class SelectedShotIcon extends ActiveImage {
    private final String TAG;

    public SelectedShotIcon(Context context, AttributeSet attrs) {
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
            setVisibility(0);
            String selectedShot = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, ThemeController.SUPPORTED_SHOTS_ARRAY[7]);
            if (ThemeController.SUPPORTED_SHOTS_ARRAY[0].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_2;
                ShootNumberLimitText.sTotalNumberOfShot = 2;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[1].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_4;
                ShootNumberLimitText.sTotalNumberOfShot = 4;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[2].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_6;
                ShootNumberLimitText.sTotalNumberOfShot = 6;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[3].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_8;
                ShootNumberLimitText.sTotalNumberOfShot = 8;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[4].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_16;
                ShootNumberLimitText.sTotalNumberOfShot = 16;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[5].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_32;
                ShootNumberLimitText.sTotalNumberOfShot = 32;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[6].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_48;
                ShootNumberLimitText.sTotalNumberOfShot = 48;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[7].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_64;
                ShootNumberLimitText.sTotalNumberOfShot = 64;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[8].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_96;
                ShootNumberLimitText.sTotalNumberOfShot = 96;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[9].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_128;
                ShootNumberLimitText.sTotalNumberOfShot = 128;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[10].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_192;
                ShootNumberLimitText.sTotalNumberOfShot = 192;
            } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[11].equals(selectedShot)) {
                drawable = R.drawable.p_16_dd_parts_smooth_shots_info_256;
                ShootNumberLimitText.sTotalNumberOfShot = 256;
            }
            setImageResource(drawable);
        } else {
            setVisibility(4);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
