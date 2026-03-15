package com.sony.imaging.app.bracketpro.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMShootingStateKeyHandler extends ShootingStateKeyHandler {
    public static boolean isS2PressedOnPB = false;

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        if (checkMemoryStatus()) {
            CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_DLAPP_STORAGE_NO_SPACE);
            return 0;
        }
        if (isS2PressedOnPB) {
            isS2PressedOnPB = false;
            return 0;
        }
        return super.pushedS2Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        isS2PressedOnPB = false;
        return super.pushedS1Key();
    }

    private boolean checkMemoryStatus() {
        if (MediaNotificationManager.getInstance().isMounted()) {
            int remainingSpace = MediaNotificationManager.getInstance().getRemaining();
            if (getBracketType().equalsIgnoreCase(BMMenuController.FlashBracket) && remainingSpace < 2 && remainingSpace > 0) {
                return true;
            }
            if (!getBracketType().equalsIgnoreCase(BMMenuController.FlashBracket) && remainingSpace < 3 && remainingSpace > 0) {
                return true;
            }
        }
        return false;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int status = super.pushedAfMfToggleCustomKey();
        BracketMasterUtil.setHoldStatus(FocusModeController.getInstance().isFocusHeld());
        return status;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        String currentBracketType = BracketMasterUtil.getCurrentBracketType();
        if (BMMenuController.FocusBracket.equals(currentBracketType) || (BMMenuController.ApertureBracket.equals(currentBracketType) && BracketMasterUtil.isIRISRingEnabledDevice())) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int retState = super.pushedAfMfHoldCustomKey();
        return retState;
    }

    private String getBracketType() {
        return BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
    }
}
