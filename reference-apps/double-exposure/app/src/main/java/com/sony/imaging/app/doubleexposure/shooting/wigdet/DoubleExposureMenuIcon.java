package com.sony.imaging.app.doubleexposure.shooting.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class DoubleExposureMenuIcon extends ActiveImage {
    private final String TAG;

    public DoubleExposureMenuIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            setImageResource(R.drawable.p_16_dd_parts_mle_still_img_selection);
        } else {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if (selectedTheme.equalsIgnoreCase("Manual")) {
                setImageResource(R.drawable.p_16_dd_parts_mle_mode_selection);
            } else if (1 == Environment.getVersionOfHW()) {
                setImageResource(R.drawable.p_16_dd_parts_guide_menu);
            } else {
                setImageDrawable(null);
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
