package com.sony.imaging.app.smoothreflection.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.menu.layout.ThemeMenuLayout;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SmoothReflectionShootingMenuState extends ShootingMenuState {
    public static final String ID_MONOTONECOLORSELECTIONMENULAYOUT = "ID_MONOTONECOLORSELECTIONMENULAYOUT";
    public static final String ID_SMOOTHLEVELMENULAYOUT = "ID_SMOOTHLEVELMENULAYOUT";
    public static final String ID_THEMESELECTIONMENULAYOUT = "ID_THEMESELECTIONMENULAYOUT";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SMOOTH_REFLECTION));
        AppNameView.show(false);
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (ThemeMenuLayout.sCurrentSelectedTheme == null || ThemeMenuLayout.sPreviousSelectedTheme == null) {
            ThemeMenuLayout.sCurrentSelectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
            ThemeMenuLayout.sPreviousSelectedTheme = ThemeMenuLayout.sCurrentSelectedTheme;
        }
        if (!ThemeMenuLayout.sCurrentSelectedTheme.equalsIgnoreCase(ThemeMenuLayout.sPreviousSelectedTheme) || true == ThemeController.MONOTONE.equals(ThemeMenuLayout.sCurrentSelectedTheme) || SmoothReflectionApp.sBootFactor == 0 || true == ThemeMenuLayout.sbISDeleteKeyPushed) {
            ThemeMenuLayout.sbISDeleteKeyPushed = false;
            if (!ThemeController.MONOTONE.equals(ThemeMenuLayout.sCurrentSelectedTheme)) {
                SmoothReflectionUtil.getInstance().clearGammmTable();
            }
            BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeMenuLayout.sCurrentSelectedTheme);
            SmoothReflectionUtil.getInstance().setRecommandedCameraSettings(ThemeMenuLayout.sCurrentSelectedTheme);
            ThemeMenuLayout.sPreviousSelectedTheme = ThemeMenuLayout.sCurrentSelectedTheme;
        }
        ThemeMenuLayout.sbISCenterKeyPushed = false;
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ThemeMenuLayout.sbISDeleteKeyPushed = false;
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
        if (ModeDialDetector.hasModeDial() && ThemeController.CUSTOM.equalsIgnoreCase(ThemeMenuLayout.sCurrentSelectedTheme) && SmoothReflectionExposureModeController.getInstance().getCautionId() != 65535) {
            bRetVal = SmoothReflectionExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
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
        SmoothReflectionConstants.sISEVDIALTURN = true;
        SmoothReflectionConstants.slastStateExposureCompansasion = 0;
        onClosed(null);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }
}
