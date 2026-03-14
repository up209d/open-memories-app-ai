package com.sony.imaging.app.doubleexposure.shooting.state;

import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class DoubleExposureS1OnEEState extends S1OnEEState {
    private static final String TAG = AppLog.getClassName();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.S1OnEEState
    public NotificationListener getListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (ThemeSelectionController.SOFTFILTER.equals(selectedTheme) && DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen)) {
            return null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.getListener();
    }
}
