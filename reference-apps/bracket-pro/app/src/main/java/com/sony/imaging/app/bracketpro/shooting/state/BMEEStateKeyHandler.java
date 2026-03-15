package com.sony.imaging.app.bracketpro.shooting.state;

import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMEEStateKeyHandler extends EEStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (!FocusModeDialDetector.hasFocusModeDial() || !currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return super.turnedFocusModeDial();
        }
        FocusModeController.getInstance().setValue("af-s");
        return 1;
    }
}
