package com.sony.imaging.app.startrails.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.base.menu.controller.STWhiteBalanceController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeChangeListener;
import com.sony.imaging.app.startrails.util.ThemeRecomandedMenuSetting;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STShootingMenuState extends ShootingMenuState implements NotificationListener {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String TAG = "LightShaftShootingMenuState";
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};
    STUtility mUtilityInstance = null;

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        if (this.mUtilityInstance.getCurrentTrail() != 2 || !ModeDialDetector.hasModeDial()) {
            return true;
        }
        boolean ret = STExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        return ID_LASTBASTIONLAYOUT;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 == msg.what) {
            AppLog.info(TAG, "ignore S2 On");
            return true;
        }
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    public void closeMenuLayouts() {
        this.mUtilityInstance.setMenuBoot(false);
        updateValueThemeValues();
        super.closeMenuLayouts();
    }

    private void updateValueThemeValues() {
        if (this.mUtilityInstance.getCurrentTrail() != this.mUtilityInstance.getLastTrail() || this.mUtilityInstance.isEEStateBoot()) {
            STWhiteBalanceController.getInstance().saveThemeWBValues();
            this.mUtilityInstance.setUpdateThemeProperty(true);
            STWhiteBalanceController.getInstance().resetBackupOnCamera();
            ThemeChangeListener.getInstance().updateThemeProperty();
        }
        if (this.mUtilityInstance.getCurrentTrail() == this.mUtilityInstance.getLastTrail()) {
            STUtility.getInstance().updateBackValue();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 3);
        this.mUtilityInstance = STUtility.getInstance();
        this.mUtilityInstance.updateTrails();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.mUtilityInstance.setPlayBackKeyPressed(false);
        updateValueThemeValues();
        if (this.mUtilityInstance.getCurrentTrail() != this.mUtilityInstance.getLastTrail()) {
            ThemeRecomandedMenuSetting.getInstance().setThemeSpecificShutterSpeed();
        }
        this.mUtilityInstance.updateTrails();
        super.onPause();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 3);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        this.mUtilityInstance.setEVDialRotated(true);
        return super.turnedEVDial();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            setNextState(STMFModeCheckState.TAG, null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfAelKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AppLog.info(TAG, AppLog.getMethodName());
        return -1;
    }
}
