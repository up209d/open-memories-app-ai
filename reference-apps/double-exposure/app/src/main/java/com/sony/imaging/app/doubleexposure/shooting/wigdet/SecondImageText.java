package com.sony.imaging.app.doubleexposure.shooting.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveText;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SecondImageText extends ActiveText {
    private final String TAG;

    public SecondImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int stringID = -1;
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            stringID = R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            stringID = R.string.STRID_FUNC_MLE_SOFTFILTER_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            stringID = R.string.STRID_FUNC_MLE_SIL_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            stringID = R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase("Rotation")) {
            stringID = R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            stringID = R.string.STRID_FUNC_MLE_MIRROR_2ND_SHOOTING_GUIDE;
        } else if (selectedTheme.equalsIgnoreCase("Manual")) {
            stringID = R.string.STRID_FUNC_MLE_MIRROR_2ND_SHOOTING_GUIDE;
        }
        setText(stringID);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        return null;
    }
}
