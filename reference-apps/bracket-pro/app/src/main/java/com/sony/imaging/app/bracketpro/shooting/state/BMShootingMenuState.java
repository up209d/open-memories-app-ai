package com.sony.imaging.app.bracketpro.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.BracketMasterMain;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;

/* loaded from: classes.dex */
public class BMShootingMenuState extends ShootingMenuState {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String TAG = AppLog.getClassName();
    private static String mCurrentBracket = null;

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_BRKMASTER));
        AppNameView.show(true);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        boolean ret = true;
        if (ModeDialDetector.hasModeDial()) {
            ret = BMExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
        }
        AppLog.info(TAG, AppLog.getMethodName() + "canRemoveState return " + ret);
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
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
        refreshBracketVaues();
        super.closeMenuLayouts();
    }

    private void refreshBracketVaues() {
        if (BMMenuController.ShutterSpeedBracket.equals(BracketMasterUtil.getCurrentBracketType()) || BMMenuController.ApertureBracket.equals(BracketMasterUtil.getCurrentBracketType())) {
            BracketMasterController.getInstance().refreshShutterSpeedValues();
            BracketMasterController.getInstance().refreshApertureValues();
            BracketMasterController.getInstance().setMaxMinShutterSpeedValue();
            BracketMasterController.getInstance().setMaxMinApertureSpeedValue();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        refreshBracketVaues();
        BMEEState.isBMCautionStateBooted = true;
        AppNameView.setText(getResources().getString(BracketMasterUtil.getBracketTypeString()));
        BracketMasterMain.setIsLauncherBoot(false);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        if (!BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return super.pushedAELToggleCustomKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int status = super.pushedAfMfToggleCustomKey();
        BracketMasterUtil.setHoldStatus(FocusModeController.getInstance().isFocusHeld());
        return status;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        String currentBracketType = BracketMasterUtil.getCurrentBracketType();
        if (BMMenuController.FocusBracket.equals(currentBracketType) || (BMMenuController.ApertureBracket.equals(currentBracketType) && BracketMasterUtil.isIRISRingEnabledDevice())) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int retState = super.pushedAfMfHoldCustomKey();
        return retState;
    }
}
