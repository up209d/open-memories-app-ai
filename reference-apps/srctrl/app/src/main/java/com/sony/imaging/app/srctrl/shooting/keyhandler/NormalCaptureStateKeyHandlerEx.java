package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.BaseKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;

/* loaded from: classes.dex */
public class NormalCaptureStateKeyHandlerEx extends BaseKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_P;
        }
        if ("Aperture".equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_A;
        }
        if ("Shutter".equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_S;
        }
        if ("Manual".equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_M;
        }
        return ICustomKey.CATEGORY_SHOOTING_OTHER;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS2Key() {
        return -1;
    }
}
