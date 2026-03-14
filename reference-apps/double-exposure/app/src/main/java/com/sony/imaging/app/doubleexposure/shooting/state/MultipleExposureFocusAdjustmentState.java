package com.sony.imaging.app.doubleexposure.shooting.state;

import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.menu.layout.DoubleExposurePageMenuLayout;

/* loaded from: classes.dex */
public class MultipleExposureFocusAdjustmentState extends FocusAdjustmentState {
    private final String TAG = AppLog.getClassName();
    private DESA mDESA = null;

    @Override // com.sony.imaging.app.base.shooting.FocusAdjustmentState, com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen)) {
            this.mDESA = DESA.getInstance();
            this.mDESA.terminate();
        }
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String currentMenuLayout = DoubleExposureUtil.getInstance().getCurrentMenuLayout();
        AppLog.info(this.TAG, "currentmenulayout" + currentMenuLayout);
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme != null && selectedTheme.equalsIgnoreCase("Manual") && currentMenuLayout != null && currentMenuLayout.equalsIgnoreCase(DoubleExposurePageMenuLayout.MENU_ID)) {
            AppLog.info(this.TAG, "Current Menu Layout: " + currentMenuLayout);
            DoubleExposureUtil.getInstance().getRecommendedValues();
        }
        super.onDestroy();
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen) && this.mDESA != null) {
            this.mDESA.setPackageName(AppContext.getAppContext().getPackageName());
            this.mDESA.intialize();
            this.mDESA.setSFRMode(1);
            this.mDESA.startLiveViewEffect();
            this.mDESA = null;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
