package com.sony.imaging.app.liveviewgrading.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingMfAssistKeyHandler extends MfAssistKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(true);
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(true);
        return -1;
    }
}
