package com.sony.imaging.app.doubleexposure.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureModeController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.menu.layout.DoubleExposurePageMenuLayout;

/* loaded from: classes.dex */
public class DoubleExposureShootingMenuState extends ShootingMenuState {
    public static final String ID_FIRSTSHOOTINGMENULAYOUT = "ID_FIRSTSHOOTINGMENULAYOUT";
    public static final String ID_MODESELECTIONMENULAYOUT = "ID_MODESELECTIONMENULAYOUT";
    public static final String ID_THEMEGUIDELAYOUT = "ID_THEMEGUIDELAYOUT";
    public static final String ID_THEMESELECTIONMENULAYOUT = "ID_THEMESELECTIONMENULAYOUT";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_NAME));
        AppNameView.show(false);
        DoubleExposureUtil.getInstance().setCurrentMenuLayout(DoubleExposurePageMenuLayout.MENU_ID);
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
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
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        boolean bRetVal;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (518 == msg.what) {
            AppLog.info(this.TAG, "ignore S2 On");
            bRetVal = true;
        } else {
            bRetVal = super.handleMessage(msg);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bRetVal = true;
        if (ModeDialDetector.hasModeDial()) {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if ("Manual".equalsIgnoreCase(selectedTheme) && DoubleExposureModeController.getInstance().getCautionId() != 65535) {
                bRetVal = DoubleExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bRetVal;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return "LastBastionLayout";
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().setTurnedEVDial(true);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }
}
