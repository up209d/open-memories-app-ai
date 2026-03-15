package com.sony.imaging.app.srctrl.shooting.state;

import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class ShootingMenuStateEx extends ShootingMenuState {
    private final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    StateController stateController;

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.stateController = StateController.getInstance();
        this.stateController.setState(this);
        int recMode = ExecutorCreator.getInstance().getRecordingMode();
        if (recMode == 2) {
            this.stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_MENU);
        } else {
            this.stateController.setAppCondition(StateController.AppCondition.SHOOTING_MENU);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (!StateController.AppCondition.DIAL_INHIBIT.equals(StateController.getInstance().getAppCondition())) {
            int recMode = ExecutorCreator.getInstance().getRecordingMode();
            if (recMode == 2) {
                StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_INHIBIT);
            } else {
                StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_INHIBIT);
            }
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        if (!ModeDialDetector.hasModeDial() || ExposureModeControllerEx.getInstance().getCautionId() == 65535) {
            return true;
        }
        boolean ret = ExposureModeControllerEx.getInstance().isValidDialPosition();
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        return "LastBastionLayout";
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return -1;
    }
}
