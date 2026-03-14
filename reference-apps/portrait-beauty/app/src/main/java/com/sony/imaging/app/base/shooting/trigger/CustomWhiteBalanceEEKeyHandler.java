package com.sony.imaging.app.base.shooting.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class CustomWhiteBalanceEEKeyHandler extends ShootingKeyHandlerBase {
    public static final String CUSTOM_WB_INFO = "CUSTOM_WB_INFO";
    private static final String ITEM_ID = "ItemId";
    private static final String MENU_STATE = "EE";
    private static final String NEXT_STATE = "CWBCapture";
    private static final String RETURN_STATE = "EE";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return (Environment.isNewBizDeviceActionCam() && Environment.isCustomWBOnePush()) ? pushedCenterKey() : super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CustomWhiteBalanceEEState CWBEEState = (CustomWhiteBalanceEEState) this.target;
        if (CWBEEState != null) {
            CWBEEState.setNextState("EE", null);
            return 1;
        }
        StateBase BaseState = (StateBase) this.target;
        BaseState.setNextState("EE", null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        if (!ModeDialDetector.hasModeDial()) {
            return -1;
        }
        int code = ModeDialDetector.getModeDialPosition();
        if (code != -1) {
            Bundle bundle = new Bundle();
            bundle.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
            StateBase state = (StateBase) this.target;
            state.setNextState("EE", bundle);
            ExecutorCreator.getInstance().updateSequence();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        if (Environment.isNewBizDeviceActionCam() && Environment.isCustomWBOnePush()) {
            return -1;
        }
        return super.pushedIRRecKey();
    }
}
