package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionExposureModeIconView extends ExposureModeIconView {
    private final String TAG;
    private String mSelectedTheme;

    public SmoothReflectionExposureModeIconView(Context context) {
        super(context);
        this.TAG = AppLog.getClassName();
        this.mSelectedTheme = null;
    }

    public SmoothReflectionExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mSelectedTheme = null;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mSelectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        setVisibility(4);
        if (ThemeController.CUSTOM.equals(this.mSelectedTheme)) {
            setVisibility(0);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mSelectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (!ThemeController.CUSTOM.equals(this.mSelectedTheme)) {
            visibility = 8;
        }
        super.setVisibility(visibility);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
