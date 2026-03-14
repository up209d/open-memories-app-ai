package com.sony.imaging.app.liveviewgrading.shooting;

import android.os.Message;
import android.view.View;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingExposureModeController;

/* loaded from: classes.dex */
public class ColorGradingShootingMenuState extends ShootingMenuState {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ret = true;
        if (ModeDialDetector.hasModeDial()) {
            ret = ColorGradingExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
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

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (ColorGradingController.getInstance().isPlayKeyInvalid()) {
            ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(false);
            ColorGradingController.getInstance().setPlayKeyInvalid(false);
            return -1;
        }
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(true);
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        View view = getLayout("ID_COLORGRADINGPRESETRESETCONFIRMATIONSCREEN").getView();
        if (view != null) {
            getLayout("ID_COLORGRADINGPRESETRESETCONFIRMATIONSCREEN").closeLayout();
        }
        super.onPause();
    }
}
