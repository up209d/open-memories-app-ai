package com.sony.imaging.app.bracketpro.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;

/* loaded from: classes.dex */
public class BMS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int status = super.pushedAfMfToggleCustomKey();
        BracketMasterUtil.setHoldStatus(FocusModeController.getInstance().isFocusHeld());
        return status;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
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
