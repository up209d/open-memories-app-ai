package com.sony.imaging.app.doubleexposure.shooting.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;

/* loaded from: classes.dex */
public class DoubleExposureModeIconView extends ExposureModeIconView {
    private final String TAG;

    public DoubleExposureModeIconView(Context context) {
        super(context);
        this.TAG = AppLog.getClassName();
    }

    public DoubleExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        setVisibility(4);
        if ("Manual".equals(selectedTheme)) {
            setVisibility(0);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equals(selectedTheme)) {
            visibility = 8;
        }
        super.setVisibility(visibility);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
