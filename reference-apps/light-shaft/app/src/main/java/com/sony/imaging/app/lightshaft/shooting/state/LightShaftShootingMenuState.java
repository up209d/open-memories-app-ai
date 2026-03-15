package com.sony.imaging.app.lightshaft.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.imaging.app.lightshaft.LightShaftConstants;
import com.sony.imaging.app.lightshaft.shooting.camera.LightShaftExposureModeController;

/* loaded from: classes.dex */
public class LightShaftShootingMenuState extends ShootingMenuState {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String TAG = "LightShaftShootingMenuState";

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        if (!ModeDialDetector.hasModeDial()) {
            return true;
        }
        boolean ret = LightShaftExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
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
        LightShaft.setMenuBoot(false);
        super.closeMenuLayouts();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        LightShaftConstants.getInstance().setEVDialRotated(true);
        return super.turnedEVDial();
    }
}
